package gdt.jgui.entity.procedure;
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Stack;
import java.util.logging.Logger;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import gdt.data.entity.ArchiveHandler;
import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.facet.ProcedureHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Identity;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JFacetRenderer;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.entity.JEntityPrimaryMenu;
import gdt.jgui.entity.JReferenceEntry;
import gdt.jgui.entity.query.JQueryPanel;
import gdt.jgui.tool.JTextEditor;

import javax.swing.JEditorPane;
import javax.swing.BorderFactory;
import javax.swing.JSplitPane;
import javax.swing.border.TitledBorder;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Desktop;
/**
 * This class represents the procedure context
 * @author imasa.
 *
 */
public class JProcedurePanel extends JPanel implements JFacetRenderer,JRequester {
private static final long serialVersionUID = 1L;
private static final String ACTION_CREATE_PROCEDURE="action create procedure";
public static final String DIVIDER_LOCATION="divider location";
public  static final String PROCEDURE_LIST_KEY ="_hXMY_5nonW4JAzrWcDo1_sBdd1g";
private	JEditorPane sourcePanel;
private	JEditorPane reportPanel;
protected String entihome$;
protected String entityKey$;
protected String entityLabel$;
protected JMainConsole console;
protected JMenu menu;
private JSplitPane splitPane;
private int dividerLocation=-1;
private boolean setDivider=true;
boolean debug=false;
/**
 * The default constructor.
 */
	public JProcedurePanel() {
		sourcePanel = new JEditorPane();
		JScrollPane scrollPaneTop = new JScrollPane(sourcePanel);
		scrollPaneTop.setBorder(BorderFactory.createTitledBorder(BorderFactory
		        .createEtchedBorder(), "Java Source", TitledBorder.CENTER,
		        TitledBorder.TOP));
		reportPanel = new JEditorPane();
		JScrollPane scrollPaneBottom = new JScrollPane(reportPanel);
		scrollPaneBottom.setBorder(BorderFactory.createTitledBorder(BorderFactory
		        .createEtchedBorder(), "Report", TitledBorder.CENTER,
		        TitledBorder.TOP));
		setLayout(new BorderLayout(0, 0));
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,scrollPaneTop,scrollPaneBottom);
		splitPane.setDividerLocation(0.5);
		add(splitPane);
		splitPane.addComponentListener(new ShowListener());
	}
	/**
	 * Get the panel to insert into the main console.
	 * @return the panel.
	 */
	@Override
	public JPanel getPanel() {
		return this;
	}
	/**
	 * Get the context menu.
	 * @return the context menu.
	 */
	@Override
		public JMenu getContextMenu() {
			menu=new JMenu("Context");
			menu.addMenuListener(new MenuListener(){
					@Override
					public void menuSelected(MenuEvent e) {
					menu.removeAll();	
						 JMenuItem runItem = new JMenuItem("Run");
							runItem.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
								  run();
								}
							} );
							menu.add(runItem);
							Entigrator entigrator=console.getEntigrator(entihome$);
							Sack procedure=entigrator.getEntityAtKey(entityKey$);
							if(procedure.getElementItem("parameter", "noreset")==null){
							JMenuItem resetItem = new JMenuItem("Reset");
							resetItem.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									 int response = JOptionPane.showConfirmDialog(console.getContentPanel(), "Reset source to default ?", "Confirm",
							  			        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
							    		  if (response == JOptionPane.YES_OPTION)
							    			  reset();
								}
							} );
							menu.add(resetItem);
							}
							JMenuItem folderItem = new JMenuItem("Open folder");
							folderItem.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
								try{
									File file=new File(entihome$+"/"+entityKey$);
									Desktop.getDesktop().open(file);
								}catch(Exception ee){
									Logger.getLogger(getClass().getName()).info(ee.toString());
								}
								}
							} );
							menu.add(folderItem);
							JMenuItem doneItem = new JMenuItem("Done");
							doneItem.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
								   console.back();
								}
							} );
							menu.add(doneItem);
							try{
				
								Sack entity=entigrator.getEntityAtKey(entityKey$);
								String template$=entity.getAttributeAt("template");
								if(template$!=null){
									JMenuItem adaptClone = new JMenuItem("Adapt clone");
									adaptClone.addActionListener(new ActionListener() {
										@Override
										public void actionPerformed(ActionEvent e) {
										try{
											adaptClone(console,getLocator());
										}catch(Exception ee){
											Logger.getLogger(getClass().getName()).info(ee.toString());
										}
										}
									} );
									menu.add(adaptClone);
								}
							}catch(Exception ee){
								Logger.getLogger(getClass().getName()).info(ee.toString());
							}
				}
					@Override
					public void menuDeselected(MenuEvent e) {
					}
					@Override
					public void menuCanceled(MenuEvent e) {
					}	
				});
				 return menu;
			 }
	/**
	 * Get the context locator.
	 * @return the context locator.
	 */
	@Override
	public String getLocator() {
		try{
			Properties locator=new Properties();
			locator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
			locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
			locator.setProperty( JContext.CONTEXT_TYPE,getType());
			locator.setProperty(Locator.LOCATOR_TITLE,getTitle());
			if(entityLabel$!=null){
				locator.setProperty(EntityHandler.ENTITY_LABEL,entityLabel$);
			}
			if(entityKey$!=null)
				locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
			if(entihome$!=null)
				locator.setProperty(Entigrator.ENTIHOME,entihome$);
			if(entityLabel$!=null)
				locator.setProperty(EntityHandler.ENTITY_LABEL,entityLabel$);
			locator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
	    	locator.setProperty(Locator.LOCATOR_ICON_CLASS,getClass().getName());
	    	locator.setProperty(Locator.LOCATOR_ICON_FILE,"procedure.png"); 
			return Locator.toString(locator);
			}catch(Exception e){
	        Logger.getLogger(getClass().getName()).severe(e.toString());
	        return null;
			}
	}
	/**
	 * Create the context.
	 * @param console the main console.
	 * @param locator$ the locator string.
	 * @return the procedure context.
	 */
	@Override
	public JContext instantiate(JMainConsole console, String locator$) {
		try{
//			System.out.println("JProcedurePanel.instantiate:locator="+locator$);
			this.console=console;
			Properties locator=Locator.toProperties(locator$);
			entihome$=locator.getProperty(Entigrator.ENTIHOME);
			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			Entigrator entigrator=console.getEntigrator(entihome$);
			entityLabel$=entigrator.indx_getLabel(entityKey$);
			if(Locator.LOCATOR_TRUE.equals(locator.getProperty(JFacetRenderer.ONLY_ITEM)))
				 return this;
			
			Sack entity=entigrator.getEntityAtKey(entityKey$);
            entityLabel$=entity.getProperty("label");
            String dividerLocation$=locator.getProperty(DIVIDER_LOCATION);
            if(dividerLocation$!=null)
            	dividerLocation=Integer.parseInt(dividerLocation$);
            if(debug)
            System.out.println("JProcedurePanel:instantiate:divider location="+dividerLocation$);
            File source=new File(entihome$+"/"+entityKey$+"/"+entityKey$+".java");
            if(source.exists()){
            FileInputStream fis = new FileInputStream(source);
            InputStreamReader ins = new InputStreamReader(fis, "UTF-8");
            BufferedReader rds = new BufferedReader(ins);
            String ss ;
            StringBuffer sbs=new StringBuffer();
	    	while((ss = rds.readLine()) != null){
	    	    sbs.append(ss+"\n");
	    	}
	    	rds.close();
	    	sourcePanel.setText(sbs.toString());
            }
	    	 File report=new File(entihome$+"/"+entityKey$+"/report.txt");
	    	 if(report.exists()){
	             FileInputStream fir = new FileInputStream(report);
	             InputStreamReader inr = new InputStreamReader(fir, "UTF-8");
	             BufferedReader rdr = new BufferedReader(inr);
	             String sr ;
	             StringBuffer sbr=new StringBuffer();
	 	    	while((sr = rdr.readLine()) != null){
	 	    	    sbr.append(sr+"\n");
	 	    	}
	 	    	rdr.close();
	 	    	reportPanel.setText(sbr.toString());
            }
    		
		}catch(Exception e){
	        Logger.getLogger(getClass().getName()).severe(e.toString());
		}
		return this;
	}
	/**
	 * Get context title.
	 * @return the context title.
	 */
	@Override
	public String getTitle() {
		if(entityLabel$==null){
			try{
				Entigrator entigrator=console.getEntigrator(entihome$);
				entityLabel$=entigrator.indx_getLabel(entityKey$);
			}catch(Exception e){}
		}
		if(entityLabel$!=null)
			return entityLabel$;
		return "Procedure";
	}
	/**
	 * Get context subtitle.
	 * @return the context subtitle.
	 */
	@Override
	public String getSubtitle() {
		return entihome$;
	}
	@Override
	public String getType() {
		return "procedure panel";
	}
	/**
	 * Complete the context. No action.
	 */
	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
	/**
	 * Add icon string to the locator.
	 * @param locator$ the origin locator.
	 * @return the locator.
	 */
	@Override
	public String addIconToLocator(String locator$) {
	    	return locator$;
	}
	/**
	 * Get facet handler class name.
	 * @return the facet handler class name.
	 */
	@Override
	public String getFacetHandler() {
		return ProcedureHandler.class.getName();
	}
	/**
	 * Get the type of the entity for the facet.
	 * @return the entity type.
	 */
	@Override
	public String getEntityType() {
		return "procedure";
	}
	/**
	 * Get facet icon as a Base64 string. 
	 * @return the icon string.
	 */
	@Override
	public String getCategoryIcon(Entigrator entigrator) {
		return Support.readHandlerIcon(null,getClass(), "procedure.png");
	}
	/**
	 * Get category title for entities having the facet type.
	 * @return the category title.
	 */

	@Override
	public String getCategoryTitle() {
		return "Procedures";
	}
	/**
	 * Adapt cloned entity.
	 * @param console the main console.
	 * @param locator$ the locator string.
	 */
	@Override
	public void adaptClone(JMainConsole console, String locator$) {
      try{
    	  String entihome$=Locator.getProperty(locator$,Entigrator.ENTIHOME);
    	  Entigrator entigrator=console.getEntigrator(entihome$);
    	  ProcedureHandler ph=new ProcedureHandler();
          ph.instantiate(locator$);
          ph.adaptClone(entigrator);
          createProjectFile(entityKey$);
      }catch(Exception e){
    	  Logger.getLogger(getClass().getName()).severe(e.toString());  
      }
	}
	/**
	 * Adapt renamed entity.
	 * @param console the main console.
	 * @param locator$ the locator string.
	 */
	@Override
	public void adaptRename(JMainConsole console, String locator$) {
		 try{
			 Entigrator entigrator=console.getEntigrator(entihome$);
	    	  ProcedureHandler ph=new ProcedureHandler();
	    	  String ph$=ph.getLocator();
	    	  ph$=Locator.append(ph$, Entigrator.ENTIHOME, entihome$);
	    	  ph$=Locator.append(ph$, EntityHandler.ENTITY_KEY, entityKey$);
	    	  ph$=Locator.append(ph$, EntityHandler.ENTITY_LABEL, entityLabel$);
	          ph.instantiate(ph$);
	          ph.adaptRename(entigrator);
	      }catch(Exception e){
	    	  Logger.getLogger(getClass().getName()).severe(e.toString());  
	      }
	}
	/**
	 * No action.
	 */
	@Override
	public void collectReferences(Entigrator entigrator, String entiyKey$, ArrayList<JReferenceEntry> sl) {
	}
	/**
	 * Rebuild entity's facet related parameters.
	 * @param console the main console
	 * @param entigrator the entigrator.
	 * @param entity the entity.
	 */
	@Override
	public void reindex(JMainConsole console, Entigrator entigrator, Sack entity) {
		 try{	
		    	String procedureHandler$=ProcedureHandler.class.getName();
		    	if(entity.getElementItem("fhandler", procedureHandler$)!=null){
					entity.putElementItem("jfacet", new Core(null,procedureHandler$,JProcedureFacetOpenItem.class.getName()));
					entigrator.save(entity);
				}
		    }catch(Exception e){
		    	Logger.getLogger(getClass().getName()).severe(e.toString());
		    }
		
	}
	/**
	 * Create a new entity of the facet type.
	 * @param console the main console.
	 * @param locator$ the locator string.
	 * @return the new entity key.
	 */
	@Override
	public String newEntity(JMainConsole console, String locator$) {
		try{
			Properties locator=Locator.toProperties(locator$);
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			JTextEditor textEditor=new JTextEditor();
		    String teLocator$=textEditor.getLocator();
		    teLocator$=Locator.append(teLocator$, Entigrator.ENTIHOME,entihome$);
		    teLocator$=Locator.append(teLocator$, JTextEditor.TEXT_TITLE,"New procedure");
		    String text$="NewProcedure"+Identity.key().substring(0, 4);
		    teLocator$=Locator.append(teLocator$, JTextEditor.TEXT,text$);
		    JProcedurePanel pp=new JProcedurePanel();
		    String ppLocator$=pp.getLocator();
		    ppLocator$=Locator.append(ppLocator$, Entigrator.ENTIHOME,entihome$);
		    ppLocator$=Locator.append(ppLocator$, EntityHandler.ENTITY_KEY,entityKey$);
		    ppLocator$=Locator.append(ppLocator$, BaseHandler.HANDLER_METHOD,"response");
		    ppLocator$=Locator.append(ppLocator$, JRequester.REQUESTER_ACTION,ACTION_CREATE_PROCEDURE);
		    String requesterResponseLocator$=Locator.compressText(ppLocator$);
		    teLocator$=Locator.append(teLocator$,JRequester.REQUESTER_RESPONSE_LOCATOR,requesterResponseLocator$);
		    JConsoleHandler.execute(console, teLocator$);
		}catch(Exception ee){   
			Logger.getLogger(getClass().getName()).severe(ee.toString());
		}
		return null;
	}
	/**
	 * Execute the response locator.
	 * @param console the main console.
	 * @param locator$ the response locator.
	 * 
	 */
	@Override
	public void response(JMainConsole console, String locator$) {
		try{
			Properties locator=Locator.toProperties(locator$);
			String action$=locator.getProperty(JRequester.REQUESTER_ACTION);
			if(ACTION_CREATE_PROCEDURE.equals(action$)){
				String entihome$=locator.getProperty(Entigrator.ENTIHOME);
				String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
				String text$=locator.getProperty(JTextEditor.TEXT);
				Entigrator entigrator=console.getEntigrator(entihome$);  
				Sack procedure=entigrator.ent_new("procedure", text$);
				procedure=entigrator.ent_assignProperty(procedure, "procedure", procedure.getProperty("label"));
				procedure=entigrator.ent_assignProperty(procedure, "folder", procedure.getProperty("label"));
				procedure.putAttribute(new Core(null,"icon","procedure.png"));
				entigrator.save(procedure);
				File folderHome=new File(entihome$+"/"+procedure.getKey());
				if(!folderHome.exists())
				    folderHome.mkdir();
				createSource(procedure.getKey());
				createProjectFile(procedure.getKey());
				createClasspathFile(procedure.getKey());
				entigrator.saveHandlerIcon(getClass(), "procedure.png");
				entityKey$=procedure.getKey();
				 JProcedurePanel pp=new JProcedurePanel();
				   String ppLocator$=pp.getLocator();
				   ppLocator$=Locator.append(ppLocator$, Entigrator.ENTIHOME, entihome$);
				   ppLocator$=Locator.append(ppLocator$, EntityHandler.ENTITY_KEY, entityKey$);
				   JEntityPrimaryMenu.reindexEntity(console, ppLocator$);
				   Stack<String> s=console.getTrack();
				   s.pop();
				   console.setTrack(s);
				   JConsoleHandler.execute(console, ppLocator$);
				   return;
				}
	}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		}
		
	}
private void createSource(String procedureKey$){
	try{
//		System.out.println("JProcedurePanel:createSource.procedure key="+procedureKey$);
		File procedureJava=new File(entihome$+"/"+procedureKey$+"/"+procedureKey$+".java");
		if(!procedureJava.exists())
			procedureJava.createNewFile();
		
		 FileOutputStream fos = new FileOutputStream(procedureJava, false);
		 Writer writer = new OutputStreamWriter(fos, "UTF-8");
		 writer.write("import java.io.File;\n");
		 writer.write("import java.io.FileOutputStream;\n");
		 writer.write("import java.io.OutputStreamWriter;\n");
		 writer.write("import java.io.Writer;\n");
		 writer.write("import java.text.SimpleDateFormat;\n");
		 writer.write("import java.util.Date;\n");
		 writer.write("import java.util.logging.Logger;\n");
		 writer.write("import gdt.data.entity.EntityHandler;\n");
		 writer.write("import gdt.data.grain.Locator;\n");
		 writer.write("import gdt.data.store.Entigrator;\n");
		 writer.write("import gdt.jgui.console.JConsoleHandler;\n");
	     writer.write("import gdt.jgui.console.JMainConsole;\n");
	     writer.write("import gdt.jgui.entity.procedure.JProcedurePanel;\n");
	     writer.write("import gdt.jgui.entity.procedure.Procedure;\n");
	     writer.write("import java.util.ArrayList;\n");
	    		 writer.write("import java.util.Collections;\n");
		 writer.write("public class "+procedureKey$+"  implements Procedure {\n");
		 //writer.write("private final static String ENTIHOME=\""+entihome$+"\";\n");
		 writer.write("private final static String ENTITY_KEY=\""+procedureKey$+"\";\n");
		 writer.write("@Override\n");
		 writer.write("public void run(JMainConsole console,String entihome$,Integer dividerLocation){\n");
		 writer.write("try{\n");
		 writer.write("//Do NOT change this section of the code\n"); 
		 writer.write("Entigrator entigrator=console.getEntigrator(entihome$);\n");
		 writer.write("String label$=entigrator.indx_getLabel(ENTITY_KEY);\n");
		 writer.write("// Put procedure code here\n");
	     //writer.write("//.....................\n");
		 writer.write("String [] sa=entigrator.indx_listEntitiesAtPropertyName(\"entity\");\n");
		 writer.write("ArrayList<String>sl=new ArrayList<String>();\n");
		 writer.write(" for(String s:sa)\n");
		 writer.write("sl.add(entigrator.indx_getLabel(s));\n");
		 writer.write(" Collections.sort(sl);\n");
	     writer.write("//\n");
	     writer.write("//Do NOT change this section of the code\n"); 
	     writer.write("File report=new File(entihome$+\"/\"+ENTITY_KEY+\"/report.txt\");\n");
	     writer.write("if(!report.exists())\n");
	     writer.write("	report.createNewFile();\n");
	     writer.write("Date curDate = new Date();\n");
	     writer.write("SimpleDateFormat format = new SimpleDateFormat();\n");
	     writer.write("format = new SimpleDateFormat(\"dd-M-yyyy hh:mm:ss\");\n");
	     writer.write("String date$= format.format(curDate);\n");
	     writer.write("FileOutputStream fos = new FileOutputStream(report, false);\n");
	     writer.write("Writer writer = new OutputStreamWriter(fos, \"UTF-8\");\n");
	     writer.write("writer.write(\"Report:   \"+label$+\"\\n\");\n");
	     writer.write("writer.write(date$+\"\\n\");\n");
	   	 writer.write("writer.write(\"__________ All entities _____________\\n\");\n");
	   	 writer.write("//Put report code here\n");
	   	// writer.write("//.....................\n");
	   	 writer.write("for(String s:sl)\n");
	   	 writer.write("	writer.write(s+\"\\n\");\n");
	   	 writer.write("//Do NOT change this section of the code\n"); 
	   	 writer.write("writer.close();\n");
	   	 writer.write("JProcedurePanel jpp=new JProcedurePanel();\n");
	   	 writer.write("String jppLocator$=jpp.getLocator();\n");
	     writer.write("jppLocator$=Locator.append(jppLocator$, Entigrator.ENTIHOME, entihome$);\n");
	     writer.write("jppLocator$=Locator.append(jppLocator$, EntityHandler.ENTITY_KEY,ENTITY_KEY);\n");
	     writer.write("jppLocator$=Locator.append(jppLocator$, JProcedurePanel.DIVIDER_LOCATION,String.valueOf(dividerLocation));\n");
	     writer.write("JConsoleHandler.execute(console, jppLocator$);\n");
		 writer.write("}catch(Exception e){\n");
		 writer.write("Logger.getLogger(getClass().getName());\n");
		 writer.write("}\n");
		 writer.write("}\n");
		 writer.write("}\n");
		 writer.close();   
		
	}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
	}
}

private void createProjectFile(String procedureKey$){
	try{
	//	System.out.println("JProcedurePanel:createProjectFile.procedure key="+procedureKey$);
		File project=new File(entihome$+"/"+procedureKey$+"/.project");
		if(!project.exists())
			project.createNewFile();
		 FileOutputStream fos = new FileOutputStream(project, false);
		 Writer writer = new OutputStreamWriter(fos, "UTF-8");
	     writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
	     writer.write("<projectDescription>\n");
	     writer.write("<name>"+procedureKey$+"</name>\n");
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
		Logger.getLogger(getClass().getName()).severe(e.toString());
	}
}
private void createClasspathFile(String procedureKey$){
	try{
	//	System.out.println("JProcedurePanel:createClasspathFile.procedure key="+procedureKey$);
		File classpath=new File(entihome$+"/"+procedureKey$+"/.classpath");
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
		Logger.getLogger(getClass().getName()).severe(e.toString());
	}
}
private String[] run(){
	try{
		File procedureJava=new File(entihome$+"/"+entityKey$+"/"+entityKey$+".java");
		if(!procedureJava.exists())
		   createSource(entityKey$);
		else{
			FileInputStream fis = new FileInputStream(procedureJava);
		    InputStreamReader ins = new InputStreamReader(fis, "UTF-8");
		    BufferedReader rds = new BufferedReader(ins);
		    String ss ;
		    StringBuffer sbs=new StringBuffer();
			while((ss = rds.readLine()) != null){
			    sbs.append(ss+"\n");
			}
			rds.close();
			sourcePanel.setText(sbs.toString());
		}
		File procedureHome=new File(entihome$+"/"+entityKey$);
		URL url = procedureHome.toURI().toURL();
	    URL[] urls = new URL[]{url};
	    ClassLoader parentLoader = JMainConsole.class.getClassLoader();
	    URLClassLoader cl = new URLClassLoader(urls,parentLoader);
	  Class<?> cls = cl.loadClass(entityKey$);
	  Object obj=cls.newInstance();
	  Method method = obj.getClass().getDeclaredMethod("run",JMainConsole.class,String.class,Integer.class);
	  Integer dividerLocation=new Integer(splitPane.getDividerLocation());
	  method.invoke(obj,console,entihome$,dividerLocation);
	}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
	}
	return null;
}
private void reset(){
	try{
	File folder=new File(entihome$+"/"+entityKey$);
	if(!folder.exists()){
		folder.mkdir();
		
	}
	createClasspathFile(entityKey$);
	createProjectFile(entityKey$);
	createSource(entityKey$);
	File procedureJava=new File(entihome$+"/"+entityKey$+"/"+entityKey$+".java");
	FileInputStream fis = new FileInputStream(procedureJava);
    InputStreamReader ins = new InputStreamReader(fis, "UTF-8");
    BufferedReader rds = new BufferedReader(ins);
    String ss ;
    StringBuffer sbs=new StringBuffer();
	while((ss = rds.readLine()) != null){
	    sbs.append(ss+"\n");
	}
	rds.close();
	sourcePanel.setText(sbs.toString());
	reportPanel.setText("");
	}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
}
}
/**
 * Restore the default procedure code.
 * @param console the main console.
 * @param entihome$ the database directory.
 */
public static void refreshListProcedure(JMainConsole console,String entihome$){
	try{
		InputStream is=JProcedurePanel.class.getResourceAsStream("list.tar");
        TarArchiveInputStream tis = new TarArchiveInputStream(is);
		ArchiveHandler.extractEntitiesFromTar(entihome$,tis);
	    Entigrator entigrator=console.getEntigrator(entihome$);
	    Sack procedure=entigrator.getEntityAtKey(PROCEDURE_LIST_KEY);
	    String procedureLocator$=EntityHandler.getEntityLocator(entigrator, procedure);
	    JEntityPrimaryMenu.reindexEntity(console, procedureLocator$);
	}catch(Exception e){
		Logger.getLogger(JQueryPanel.class.getName()).severe(e.toString());	
	}
}
private class ShowListener implements ComponentListener{

	@Override
	public void componentResized(ComponentEvent e) {
		// System.out.println("JProcedurePanel:component resized:"+e.getComponent().getClass().getName() + " --- Resized");
		if(dividerLocation>0){
			splitPane.setDividerLocation(dividerLocation);
			return;
		}
		if(setDivider){
		 splitPane.setDividerLocation(0.5);
		 setDivider=false;
		 }
	}
	@Override
	public void componentMoved(ComponentEvent e) {
	}
	@Override
	public void componentShown(ComponentEvent e) {
	}
	@Override
	public void componentHidden(ComponentEvent e) {
		
	}
	
}
	public JEditorPane getSourcePanel() {
		return sourcePanel;
	}
	@Override
	public void activate() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getFacetOpenItem() {
		// TODO Auto-generated method stub
		return JProcedureFacetOpenItem.class.getName();
	}
	@Override
	public String getFacetIcon() {
		// TODO Auto-generated method stub
		return "procedure.png";
	}
}
