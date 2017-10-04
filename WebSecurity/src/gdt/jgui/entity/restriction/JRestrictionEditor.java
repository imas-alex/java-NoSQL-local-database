package gdt.jgui.entity.restriction;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
/*
 * Copyright 2016 Alexander Imas
 * This file is part of JEntigrator.

    JEntigrator is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    JEntigrator is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with JEntigrator.  If not, see <http://www.gnu.org/licenses/>.
 */
import java.util.ArrayList;
import java.util.Properties;
import java.util.Stack;
import java.util.logging.Logger;

import javax.swing.JMenu;
import javax.swing.JPanel;

import gdt.data.entity.BaseHandler;
import gdt.data.entity.RestrictionHandler;
import gdt.data.entity.UsersHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.facet.ExtensionHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Identity;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
import gdt.jgui.console.*;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.entity.JEntityPrimaryMenu;
import gdt.jgui.entity.JReferenceEntry;
import gdt.jgui.entity.fields.JFieldsEditor;
import gdt.jgui.entity.folder.JFolderPanel;
import gdt.jgui.entity.query.JQueryPanel;
import gdt.jgui.tool.JTextEditor;
public class JRestrictionEditor extends JFolderPanel implements JFacetRenderer,JRequester{
String entityKey$;
String entityLabel$;
String entihome$;
	private static final long serialVersionUID = 1L;
	public static final String ACTION_CREATE_RESTRICTION="action create restriction";
	
	@Override
	public String getLocator() {
		String locator$=super.getLocator();
		if(entityKey$!=null)
			locator$=Locator.append(locator$, EntityHandler.ENTITY_KEY, entityKey$);
		if(entityLabel$!=null){
			locator$=Locator.append(locator$, EntityHandler.ENTITY_LABEL, entityLabel$);
			locator$=Locator.append(locator$, "subtitle", entityLabel$);
		}
		if(entihome$!=null)
			locator$=Locator.append(locator$, Entigrator.ENTIHOME, entihome$);
		
		locator$=Locator.append(locator$, BaseHandler.HANDLER_CLASS, getClass().getName());
		locator$=Locator.append(locator$, BaseHandler.HANDLER_METHOD, "instantiate");
				return locator$;
	}

	@Override
	public String getTitle() {
		return "Restriction";
	}

	@Override
	public String getSubtitle() {
		return entityLabel$;	
	}

	@Override
	public String getType() {
			return "restirction";
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String addIconToLocator(String locator$) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFacetHandler() {
		return RestrictionHandler.class.getName();
	}

	@Override
	public String getEntityType() {
		return "restriction";
	}

	@Override
	public String getCategoryIcon(Entigrator entigrator) {
		
		    return ExtensionHandler.loadIcon(entigrator,UsersHandler.EXTENSION_KEY, "restriction.png");
	}

	@Override
	public String getCategoryTitle() {
		return "Restrictions";
	}

	@Override
	public void adaptClone(JMainConsole console, String locator$) {

	}

	@Override
	public void adaptRename(JMainConsole console, String locator$) {
	
	
	}

	@Override
	public String newEntity(JMainConsole console, String locator$) {
		try{
			Properties locator=Locator.toProperties(locator$);
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			JTextEditor textEditor=new JTextEditor();
		    String teLocator$=textEditor.getLocator();
		    teLocator$=Locator.append(teLocator$, Entigrator.ENTIHOME,entihome$);
		    teLocator$=Locator.append(teLocator$, JTextEditor.TEXT_TITLE,"Restirction");
		    String text$="NewRestrction"+Identity.key().substring(0, 4);
		    teLocator$=Locator.append(teLocator$, JTextEditor.TEXT,text$);
		    JRestrictionEditor fp=new JRestrictionEditor();
		    String fpLocator$=fp.getLocator();
		    fpLocator$=Locator.append(fpLocator$, Entigrator.ENTIHOME,entihome$);
		    fpLocator$=Locator.append(fpLocator$, EntityHandler.ENTITY_KEY,entityKey$);
		    fpLocator$=Locator.append(fpLocator$, BaseHandler.HANDLER_METHOD,"response");
		    fpLocator$=Locator.append(fpLocator$, JRequester.REQUESTER_ACTION,ACTION_CREATE_RESTRICTION);
		    String requesterResponseLocator$=Locator.compressText(fpLocator$);
		    teLocator$=Locator.append(teLocator$,JRequester.REQUESTER_RESPONSE_LOCATOR,requesterResponseLocator$);
		    JConsoleHandler.execute(console, teLocator$);
		}catch(Exception ee){   
			Logger.getLogger(getClass().getName()).severe(ee.toString());
			
		}
		return null;
	}

	@Override
	public void response(JMainConsole console, String locator$) {
		//System.out.println("JEmailEditor:response:locator="+locator$);
		Properties locator=Locator.toProperties(locator$);
		String action$=locator.getProperty(JRequester.REQUESTER_ACTION);
		
		if(ACTION_CREATE_RESTRICTION.equals(action$)){
			   entihome$=locator.getProperty(Entigrator.ENTIHOME);
			   String text$=locator.getProperty(JTextEditor.TEXT);
			   Entigrator entigrator=console.getEntigrator(entihome$);
			   Sack folder=entigrator.ent_new("restriction", text$);
			   folder=entigrator.ent_assignProperty(folder, "folder", folder.getProperty("label"));
			   folder=entigrator.ent_assignProperty(folder, "restriction", folder.getProperty("label"));
			   folder.putAttribute(new Core(null,"icon","restriction.png"));
			   folder.createElement("jfacet");
			   folder.putElementItem("jfacet", new Core(null,"gdt.data.entity.RestrictionHandler","gdt.jgui.entity.restriction.JRestrictionFacetOpenItem"));
			   folder.putElementItem("jfacet", new Core(null,"gdt.data.entity.facet.FolderHandler","gdt.jgui.entity.folder.JFolderFacetOpenItem"));
			   folder.createElement("fhandler");
			   folder.putElementItem("fhandler", new Core(null,"gdt.data.entity.RestrictionHandler",UsersHandler.EXTENSION_KEY));
			   folder.putElementItem("fhandler", new Core(null,"gdt.data.entity.facet.FolderHandler",null));
			   entigrator.ent_alter(folder);
			   entigrator.saveHandlerIcon(JRestrictionEditor.class, "restriction.png");
			   entityKey$=folder.getKey();
			   File folderHome=new File(entihome$+"/"+entityKey$);
			   if(!folderHome.exists())
			     folderHome.mkdir();
			   createSource(entihome$,entityKey$);
				createProjectFile(entihome$,entityKey$);
				createClasspathFile(entihome$,entityKey$);
			   JFolderPanel fp=new JFolderPanel();
			   String fLocator$=fp.getLocator();
			   fLocator$=Locator.append(fLocator$, Entigrator.ENTIHOME, entihome$);
			   fLocator$=Locator.append(fLocator$, EntityHandler.ENTITY_KEY, entityKey$);
			   
			   JEntityPrimaryMenu.reindexEntity(console, fLocator$);
			   Stack<String> s=console.getTrack();
			   s.pop();
			   console.setTrack(s);
			   entigrator.store_replace();
			   JConsoleHandler.execute(console, fLocator$);
		
			}
		
	}
	@Override
	public void collectReferences(Entigrator entigrator, String entityKey$, ArrayList<JReferenceEntry> rel) {
	
	}
	@Override
	public String getFacetOpenItem() {
		// TODO Auto-generated method stub
		return JRestrictionFacetOpenItem.class.getName();
	}

	@Override
	public String getFacetIcon() {
		return "restriction.png";
	}
	private static void createClasspathFile(String entihome$,String entityKey$){
		try{
			File classpath=new File(entihome$+"/"+entityKey$+"/.classpath");
			if(!classpath.exists())
				classpath.createNewFile();
			 FileOutputStream fos = new FileOutputStream(classpath, false);
			 Writer writer = new OutputStreamWriter(fos, "UTF-8");
		     writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
		     writer.write("<classpath>\n");
		     writer.write("<classpathentry kind=\"src\" path=\"\"/>\n");
		     writer.write("<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER\"/>\n");
		     writer.write("<classpathentry combineaccessrules=\"false\" kind=\"src\" path=\"/JEntigrator\"/>\n");
		     writer.write("<classpathentry kind=\"output\" path=\"\"/>\n");
		      writer.write("</classpath>\n");
			 writer.close();   
		}catch(Exception e){
			Logger.getLogger(JRestrictionEditor.class.getName()).severe(e.toString());
		}
	}
	private static void createProjectFile(String entihome$,String entityKey$){
		try{
			File project=new File(entihome$+"/"+entityKey$+"/.project");
			if(!project.exists())
				project.createNewFile();
			 FileOutputStream fos = new FileOutputStream(project, false);
			 Writer writer = new OutputStreamWriter(fos, "UTF-8");
		     writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
		     writer.write("<projectDescription>\n");
		     writer.write("<name>"+entityKey$+"</name>\n");
		     writer.write("<comment></comment>\n");
		     writer.write("<projects></projects>\n");
		     writer.write("<buildSpec>\n");
		     writer.write("<buildCommand>\n");
		     writer.write("<name>org.eclipse.jdt.core.javabuilder</name>\n");
		     writer.write("<arguments></arguments>\n");
		     writer.write("</buildCommand>\n");
		     writer.write("</buildSpec>\n");
		     writer.write("<natures>\n");
		     writer.write("<nature>org.eclipse.jdt.core.javanature</nature>\n");
	   		 writer.write("</natures>\n");
			 writer.write("</projectDescription>\n");
			 writer.close();   
		}catch(Exception e){
			Logger.getLogger(JRestrictionEditor.class.getName()).severe(e.toString());
		}
	}
	private static void createSource(String entihome$,String entityKey$){

		try{
			System.out.println("JProcedurePanel:createQuery:query key="+entityKey$);
			File queryJava=new File(entihome$+"/"+entityKey$+"/"+entityKey$+".java");
			if(!queryJava.exists())
				queryJava.createNewFile();
			 FileOutputStream fos = new FileOutputStream(queryJava, false);
			 Writer writer = new OutputStreamWriter(fos, "UTF-8");
			 writer.write("import java.util.logging.Logger;\n");
			 writer.write("import gdt.data.store.Entigrator;\n");
			 writer.write("import gdt.jgui.entity.restriction.Restriction;\n");
			 
			 writer.write("public class "+entityKey$+"  implements Restriction {\n");
			 writer.write("private final static String ENTITY_KEY=\""+entityKey$+"\";\n");
			 writer.write("@Override\n");
			 writer.write("public boolean deny(Entigrator entigrator,String locator$){\n");
			 writer.write("try{\n");
			 writer.write("}catch(Exception e){\n");
			 writer.write("Logger.getLogger(getClass().getName());\n");
			 writer.write("}\n");
			 writer.write("return false;\n");
			 writer.write("}\n");
			 writer.write("public boolean isForced(){\n");
			 writer.write("return false;}\n");
			 writer.write("}\n");
			 writer.close();   
			
		}catch(Exception e){
			Logger.getLogger(JQueryPanel.class.getName()).severe(e.toString());
		}
	}
	
}
