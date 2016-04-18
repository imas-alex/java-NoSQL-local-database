package gdt.jgui.entity.contact;
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
import java.util.Properties;
import java.util.logging.Logger;
import gdt.data.entity.BaseHandler;
import gdt.data.entity.ContactHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.FacetHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Identity;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JFacetAddItem;
import gdt.jgui.console.JFacetOpenItem;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.entity.JEntitiesPanel;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.tool.JTextEditor;
public class JContactFacetAddItem extends JFacetAddItem{

	private static final long serialVersionUID = 1L;
	public static final String EXTENSION_KEY="_v6z8CVgemqMI6Bledpc7F1j0pVY";
	private Logger LOGGER=Logger.getLogger(JContactFacetAddItem.class.getName());
    public JContactFacetAddItem(){
		super();
	}
 
@Override
public String getLocator(){
	
	Properties locator=new Properties();
	locator.setProperty(Locator.LOCATOR_TITLE,"Contact");
	locator.setProperty(BaseHandler.HANDLER_CLASS,JContactFacetAddItem.class.getName());
	locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
	locator.setProperty(BaseHandler.HANDLER_METHOD,METHOD_ADD_COMPONENT);
	locator.setProperty(BaseHandler.HANDLER_LOCATION,EXTENSION_KEY);
	locator.setProperty( JContext.CONTEXT_TYPE,"Contact add ");
	locator.setProperty(Locator.LOCATOR_TITLE,"Contact");
	locator.setProperty(JFacetOpenItem.FACET_HANDLER_CLASS,ContactHandler.class.getName());
	if(entityKey$!=null)
		locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
	if(entihome$!=null)
		locator.setProperty(Entigrator.ENTIHOME,entihome$);
	 icon$=Support.readHandlerIcon(JContactFacetAddItem.class, "contact.png");
	if(icon$!=null)
	    	locator.setProperty(Locator.LOCATOR_ICON,icon$);
	 locator$=Locator.toString(locator);
	locator.setProperty(Locator.LOCATOR_CHECKABLE,Locator.LOCATOR_TRUE);
	 return Locator.toString(locator);
}
@Override
public void response(JMainConsole console, String locator$) {
	//System.out.println("JContactFacetAddItem:response:locator:"+locator$);
	try{
		Properties locator=Locator.toProperties(locator$);
		String entihome$=locator.getProperty(Entigrator.ENTIHOME);
		String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		String mode$=locator.getProperty(JFacetAddItem.ADD_MODE);
		if(JFacetAddItem.ADD_MODE_COMPONENT.equals(mode$)){
			String componentLabel$=locator.getProperty(JTextEditor.TEXT);
			Entigrator entigrator=console.getEntigrator(entihome$);
			Sack component=entigrator.ent_new("contact", componentLabel$);
			component=entigrator.ent_assignProperty(component, "email", "a@b.com");
			component=entigrator.ent_assignProperty(component, "phone", "123456");
			component.createElement("fhandler");
			component.putElementItem("fhandler", new Core(null,ContactHandler.class.getName(),EXTENSION_KEY));
			component.createElement("jfacet");
			component.putElementItem("jfacet", new Core(JContactFacetAddItem.class.getName(),ContactHandler.class.getName(),JContactFacetOpenItem.class.getName()));
			entigrator.save(component);
			entigrator.saveHandlerIcon(JContactFacetAddItem.class, "class.png");
			Sack container=entigrator.getEntityAtKey(entityKey$);
			entigrator.col_addComponent(container, component);
			JEntityFacetPanel efp=new JEntityFacetPanel();
			String efpLocator$=efp.getLocator();
			efpLocator$=Locator.append(efpLocator$,Entigrator.ENTIHOME,entihome$);
			efpLocator$=Locator.append(efpLocator$,EntityHandler.ENTITY_KEY,component.getKey());
		    JConsoleHandler.execute(console, efpLocator$);	
		}
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
}


@Override
public void addFacet(JMainConsole console, String locator$) {
	try{
	 //  System.out.println("JPhoneFacetAddItem:addFacet:locator:"+locator$);
	   Properties locator=Locator.toProperties(locator$);
	   String entihome$=locator.getProperty(Entigrator.ENTIHOME);
	   String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
	   Entigrator entigrator=console.getEntigrator(entihome$);
	   Sack entity=entigrator.getEntityAtKey(entityKey$);
	   if(!entity.existsElement("fhandler"))
		   entity.createElement("fhandler");
	   entity.putElementItem("fhandler", new Core(null,ContactHandler.class.getName(),EXTENSION_KEY)); 
	   if(!entity.existsElement("jfacet"))
		   entity.createElement("jfacet");
	   entity.putElementItem("jfacet", new Core(JContactFacetAddItem.class.getName(),ContactHandler.class.getName(),JContactFacetOpenItem.class.getName()));
	   entigrator.save(entity);
	   entigrator.ent_assignProperty(entity, "email", "a@b.com");
	   entigrator.ent_assignProperty(entity, "phone", "123456");
	}catch(Exception e){
		  LOGGER.severe(e.toString());
	  }
	
}

@Override
public void addComponent(JMainConsole console, String locator$) {
	try{
//		System.out.println("JEmailFacetAddItem:addComponent:locator:"+locator$);
	    Properties locator=Locator.toProperties(locator$);
	    String entihome$=locator.getProperty(Entigrator.ENTIHOME);
	    String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
	    Entigrator entigrator=console.getEntigrator(entihome$);
	    String label$=entigrator.indx_getLabel(entityKey$);
	    
		JTextEditor textEditor=new JTextEditor();
	    String editorLocator$=textEditor.getLocator();
	    editorLocator$=Locator.append(editorLocator$, JTextEditor.TEXT, label$+".contact."+Identity.key().substring(0,4));
//	    editorLocator$=Locator.append(editorLocator$,Locator.LOCATOR_DATA,Locator.compressText(locator$));
	    editorLocator$=Locator.append(editorLocator$,Locator.LOCATOR_TITLE,"Component label");
	    editorLocator$=Locator.append(editorLocator$,JTextEditor.TEXT_TITLE,"Add contact component");
	    String icon$=Support.readHandlerIcon(JEntitiesPanel.class, "edit.png");
	    editorLocator$=Locator.append(editorLocator$,Locator.LOCATOR_ICON,icon$);
	    String responseLocator$=getLocator();
	    responseLocator$=Locator.append(responseLocator$, BaseHandler.HANDLER_METHOD, "response");
	    responseLocator$=Locator.append(responseLocator$, Entigrator.ENTIHOME, entihome$);
	    responseLocator$=Locator.append(responseLocator$, EntityHandler.ENTITY_KEY, entityKey$);
	    responseLocator$=Locator.append(responseLocator$, ADD_MODE, ADD_MODE_COMPONENT);
	    String requesterResponseLocator$=Locator.compressText(responseLocator$);
	    editorLocator$=Locator.append(editorLocator$, JRequester.REQUESTER_RESPONSE_LOCATOR, requesterResponseLocator$);
	    JConsoleHandler.execute(console, editorLocator$);
	}catch(Exception e){
		  LOGGER.severe(e.toString());
	  }
	
}
@Override
public FacetHandler getFacetHandler() {
	return new ContactHandler();
} 

@Override
public String markAppliedUncheckable(JMainConsole console, String locator$) {
	try{
	Properties locator=Locator.toProperties(locator$);	
	String entihome$=locator.getProperty(Entigrator.ENTIHOME);
	String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
	Entigrator entigrator=console.getEntigrator(entihome$);
	Sack entity=entigrator.getEntityAtKey(entityKey$);
	if(entity==null)
		return null;
	boolean isApplied=false;
	if(entity.getProperty("contact")!=null){
		Core fhandler=entity.getElementItem("fhandler",ContactHandler.class.getName());
		if(fhandler!=null){
			if(ContactHandler.class.getName().equals(fhandler.name)
					&& EXTENSION_KEY.equals(fhandler.value)){
				Core jfacet=entity.getElementItem("jfacet", ContactHandler.class.getName());
				if(jfacet!=null){
					if( JContactFacetOpenItem.class.getName().equals(jfacet.value)
							&& JContactFacetAddItem.class.getName().equals(jfacet.type)){
						isApplied=true;	
					}
				}
			}
		}
	}
	if(isApplied)
		locator$=Locator.append(locator$, Locator.LOCATOR_CHECKABLE, Locator.LOCATOR_FALSE);
	else
		locator$=Locator.append(locator$, Locator.LOCATOR_CHECKABLE, Locator.LOCATOR_TRUE);
	this.locator$=locator$;
	return locator$;
	}catch(Exception e){
	LOGGER.info(e.toString());
	}	
	this.locator$=locator$;
	return locator$;
}
}

