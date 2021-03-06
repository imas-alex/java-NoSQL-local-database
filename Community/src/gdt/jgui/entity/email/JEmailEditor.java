package gdt.jgui.entity.email;
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
import java.util.logging.Logger;
import gdt.data.entity.BaseHandler;
import gdt.data.entity.EmailHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.facet.ExtensionHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Identity;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
import gdt.jgui.console.*;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.entity.JReferenceEntry;
import gdt.jgui.tool.JTextEditor;
public class JEmailEditor extends JTextEditor implements JFacetRenderer,JRequester{
String entityKey$;
String entityLabel$;
String entihome$;
	private static final long serialVersionUID = 1L;
	public static final String ACTION_CREATE_EMAIL="action create email";
	public static final String ACTION_CREATE_ADDRESS="action create address";
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
		if(text$!=null)
			locator$=Locator.append(locator$, JTextEditor.TEXT, text$);
		locator$=Locator.append(locator$, BaseHandler.HANDLER_CLASS, getClass().getName());
		locator$=Locator.append(locator$, BaseHandler.HANDLER_METHOD, "instantiate");
		
		//System.out.println("JEmailEditor.getLocator:locator="+locator$);
				return locator$;
	}

	@Override
	public JContext instantiate(JMainConsole console, String locator$) {
		//System.out.println("JEmailEditor.instantiatelocator:0::"+locator$);	   
		
		Properties locator=Locator.toProperties(locator$);
	    entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
	    entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
	    entihome$=locator.getProperty(Entigrator.ENTIHOME);
	    if(entityLabel$==null){
	    	Entigrator entigrator=console.getEntigrator(entihome$);
	    	entityLabel$=entigrator.indx_getLabel(entityKey$);
	    }
	    text$=locator.getProperty(JTextEditor.TEXT);
		return super.instantiate(console, locator$);
	}

	@Override
	public String getTitle() {
		return "Email";
	}

	@Override
	public String getSubtitle() {
		return entityLabel$;	
	}

	@Override
	public String getType() {
			return "email";
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
		return EmailHandler.class.getName();
	}

	@Override
	public String getEntityType() {
		return "email";
	}

	@Override
	public String getCategoryIcon(Entigrator entigrator) {
		
		    return ExtensionHandler.loadIcon(entigrator,EmailHandler.EXTENSION_KEY, "email.png");
	}

	@Override
	public String getCategoryTitle() {
		return "Emails";
	}

	@Override
	public void adaptClone(JMainConsole console, String locator$) {
	}

	@Override
	public void adaptRename(JMainConsole console, String locator$) {
	}

	@Override
	public void reindex(JMainConsole console, Entigrator entigrator, Sack entity) {
		 try{
			// System.out.println("JEmailEditor:reindex:0:entity="+entity.getProperty("label"));
		    	String fhandler$=EmailHandler.class.getName();
		    	if(entity.getElementItem("fhandler", fhandler$)!=null){
					//System.out.println("JEmailEditor:reindex:1:entity="+entity.getProperty("label"));
		    		entity.putElementItem("jfacet", new Core(JEmailFacetAddItem.class.getName(),fhandler$,JEmailFacetOpenItem.class.getName()));
					entity.putElementItem("fhandler", new Core(null,fhandler$,JEmailFacetAddItem.EXTENSION_KEY));
					entigrator.ent_alter(entity);
				}
		    }catch(Exception e){
		    	Logger.getLogger(getClass().getName()).severe(e.toString());
		    }
	}
	@Override
	public String newEntity(JMainConsole console, String locator$) {
		try{
			Properties locator=Locator.toProperties(locator$);
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			JTextEditor textEditor=new JTextEditor();
		    String teLocator$=textEditor.getLocator();
		    teLocator$=Locator.append(teLocator$, Entigrator.ENTIHOME,entihome$);
		    teLocator$=Locator.append(teLocator$, JTextEditor.TEXT_TITLE,"Title of email");
		    String text$="NewEmail"+Identity.key().substring(0, 4);
		    teLocator$=Locator.append(teLocator$, JTextEditor.TEXT,text$);
		    JEmailEditor fp=new JEmailEditor();
		    String fpLocator$=fp.getLocator();
		    fpLocator$=Locator.append(fpLocator$, Entigrator.ENTIHOME,entihome$);
		    fpLocator$=Locator.append(fpLocator$, EntityHandler.ENTITY_KEY,entityKey$);
		    fpLocator$=Locator.append(fpLocator$, BaseHandler.HANDLER_METHOD,"response");
		    fpLocator$=Locator.append(fpLocator$, JRequester.REQUESTER_ACTION,ACTION_CREATE_EMAIL);
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
		
		if(ACTION_CREATE_EMAIL.equals(action$)){
			   entihome$=locator.getProperty(Entigrator.ENTIHOME);
			   String text$=locator.getProperty(JTextEditor.TEXT);
			   Entigrator entigrator=console.getEntigrator(entihome$);
			   Sack email=entigrator.ent_new("email", text$);
			   email.putAttribute(new Core(null,"icon","email.png"));
			   email.createElement("jfacet");
			   email.putElementItem("jfacet", new Core("gdt.jgui.entity.email.JEmailFacetAddItem","gdt.data.entity.EmailHandler","gdt.jgui.entity.email.JEmailFacetOpenItem"));
			   email.createElement("fhandler");
			   email.putElementItem("fhandler", new Core(null,"gdt.data.entity.EmailHandler",EmailHandler.EXTENSION_KEY));
			   entigrator.ent_alter(email);
			   //email=entigrator.ent_assignProperty(email, "email", "a@b.com");
			  
			   entigrator.saveHandlerIcon(JEmailEditor.class, "email.png");
			   entityKey$=email.getKey();
			   JTextEditor textEditor=new JTextEditor();
			    String teLocator$=textEditor.getLocator();
			    teLocator$=Locator.append(teLocator$, Entigrator.ENTIHOME,entihome$);
			    teLocator$=Locator.append(teLocator$, JTextEditor.TEXT_TITLE,"Address");
			    text$="a@b.com";
			    teLocator$=Locator.append(teLocator$, JTextEditor.TEXT,text$);
			    JEmailEditor fp=new JEmailEditor();
			    String fpLocator$=fp.getLocator();
			    fpLocator$=Locator.append(fpLocator$, Entigrator.ENTIHOME,entihome$);
			    fpLocator$=Locator.append(fpLocator$, EntityHandler.ENTITY_KEY,entityKey$);
			    fpLocator$=Locator.append(fpLocator$, BaseHandler.HANDLER_METHOD,"response");
			    fpLocator$=Locator.append(fpLocator$, JRequester.REQUESTER_ACTION,ACTION_CREATE_ADDRESS);
			    String requesterResponseLocator$=Locator.compressText(fpLocator$);
			    teLocator$=Locator.append(teLocator$,JRequester.REQUESTER_RESPONSE_LOCATOR,requesterResponseLocator$);
			    JConsoleHandler.execute(console, teLocator$);
			}
		if(ACTION_CREATE_ADDRESS.equals(action$)){
			   entihome$=locator.getProperty(Entigrator.ENTIHOME);
			   entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			   String text$=locator.getProperty(JTextEditor.TEXT);
			   Entigrator entigrator=console.getEntigrator(entihome$);
			   Sack email=entigrator.getEntityAtKey(entityKey$);
			   email=entigrator.ent_assignProperty(email, "email", text$);
			   entigrator.ent_alter(email);
			   JEntityFacetPanel efp=new JEntityFacetPanel();
			   String efpLocator$=efp.getLocator();
			   efpLocator$=Locator.append(efpLocator$, Entigrator.ENTIHOME, entihome$);
			   efpLocator$=Locator.append(efpLocator$, EntityHandler.ENTITY_KEY, entityKey$);
			   JConsoleHandler.execute(console, efpLocator$);
			  	}
	}
	@Override
	public void collectReferences(Entigrator entigrator, String entityKey$, ArrayList<JReferenceEntry> rel) {
	
	}
	@Override
	public String getFacetOpenItem() {
		// TODO Auto-generated method stub
		return JEmailFacetOpenItem.class.getName();
	}

	@Override
	public String getFacetIcon() {
		
		return "email.png";
	}
}
