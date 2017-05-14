package gdt.jgui.entity.nwcategory;
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
import gdt.data.entity.NwCategoryHandler;
import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.facet.ExtensionHandler;
import gdt.data.entity.facet.FieldsHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Identity;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.jgui.console.*;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.entity.JEntityPrimaryMenu;
import gdt.jgui.entity.JReferenceEntry;
import gdt.jgui.entity.fields.JFieldsEditor;
import gdt.jgui.tool.JTextEditor;

public class JNwCategoryEditor extends JFieldsEditor {

	private static final long serialVersionUID = 1L;
	
	boolean debug=false;
	public JNwCategoryEditor() {
		super();
	}
	@Override
	public String getLocator() {
		try{
			Properties locator=new Properties();
			locator.setProperty(BaseHandler.HANDLER_CLASS,JNwCategoryEditor.class.getName());
			locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
			 locator.setProperty( JContext.CONTEXT_TYPE,getType());
			locator.setProperty(Locator.LOCATOR_TITLE,getTitle());
			locator.setProperty(BaseHandler.HANDLER_LOCATION,NwCategoryHandler.EXTENSION_KEY);
			if(entityLabel$!=null){
				locator.setProperty(EntityHandler.ENTITY_LABEL,entityLabel$);
			}
			if(entityKey$!=null)
				locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
			
			if(entihome$!=null){
				locator.setProperty(Entigrator.ENTIHOME,entihome$);
			}
			locator.setProperty( Locator.LOCATOR_ICON_CONTAINER, Locator.LOCATOR_ICON_CONTAINER_CLASS);
	    	locator.setProperty( Locator.LOCATOR_ICON_CLASS, getClass().getName());
	    	locator.setProperty( Locator.LOCATOR_ICON_FILE, "nwCategory.png");
			return Locator.toString(locator);
			}catch(Exception e){
	        Logger.getLogger(getClass().getName()).severe(e.toString());
	        return null;
			}
	}
	@Override
	public String getTitle() {
			return "nwCategory";
	}
	@Override
	public String getSubtitle() {
		return entityLabel$;	
	}
	@Override
	public String getType() {
			return "nwCategory";
	}
	@Override
	public String getFacetHandler() {
		return NwCategoryHandler.class.getName();
	}

	@Override
	public String getEntityType() {
		return "nwCategory";
	}

	@Override
	public String getCategoryIcon(Entigrator entigrator) {
		return ExtensionHandler.loadIcon(entigrator, NwCategoryHandler.EXTENSION_KEY, "nwCategory.png");
				//Support.readHandlerIcon(null,JAddressEditor.class, "address.png");
	}

	@Override
	public String getCategoryTitle() {
		return "nwCategories";
	}
	
	@Override
	public void reindex(JMainConsole console, Entigrator entigrator, Sack entity) {
		 try{
			// System.out.println("JPhoneEditor:reindex:0:entity="+entity.getProperty("label"));
		    	String fhandler$=NwCategoryHandler.class.getName();
		    	if(entity.getElementItem("fhandler", fhandler$)!=null){
		    		entity.putElementItem("jfacet", new Core(JNwCategoryFacetAddItem.class.getName(),fhandler$,JNwCategoryFacetOpenItem.class.getName()));
					entity.putElementItem("fhandler", new Core(null,fhandler$,NwCategoryHandler.EXTENSION_KEY));
					entigrator.replace(entity);
				}
		    }catch(Exception e){
		    	Logger.getLogger(getClass().getName()).severe(e.toString());
		    }
	}
	@Override
	public String newEntity(JMainConsole console, String locator$) {
		JTextEditor textEditor=new JTextEditor();
	    String editorLocator$=textEditor.getLocator();
	    editorLocator$=Locator.append(editorLocator$, JTextEditor.TEXT, "nwCategory"+Identity.key().substring(0,4));
	    editorLocator$=Locator.append(editorLocator$,Locator.LOCATOR_TITLE,"nwCategory entity");
	    JNwCategoryEditor fe=new JNwCategoryEditor();
	    String feLocator$=fe.getLocator();
	    Properties responseLocator=Locator.toProperties(feLocator$);
	    entihome$=Locator.getProperty(locator$,Entigrator.ENTIHOME );
	    if(entihome$!=null)
	      responseLocator.setProperty(Entigrator.ENTIHOME,entihome$);
	   responseLocator.setProperty(BaseHandler.HANDLER_CLASS,JNwCategoryEditor.class.getName());
		responseLocator.setProperty(BaseHandler.HANDLER_METHOD,"response");
		responseLocator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
		responseLocator.setProperty(BaseHandler.HANDLER_METHOD,"response");
		responseLocator.setProperty(JRequester.REQUESTER_ACTION,ACTION_NEW_ENTITY);
		responseLocator.setProperty(Locator.LOCATOR_TITLE,"nwCategory");
		 String responseLocator$=Locator.toString(responseLocator);
    	//System.out.println("FieldsEditor:newEntity:responseLocator:=:"+responseLocator$);
		String requesterResponseLocator$=Locator.compressText(responseLocator$);
		editorLocator$=Locator.append(editorLocator$,JRequester.REQUESTER_RESPONSE_LOCATOR,requesterResponseLocator$);
		editorLocator$=Locator.append(editorLocator$,Entigrator.ENTIHOME,entihome$);JConsoleHandler.execute(console,editorLocator$); 
		return editorLocator$;
	}

	@Override
	public void response(JMainConsole console, String locator$) {
	//	System.out.println("JAddressEditor:response:"+Locator.remove(locator$,Locator.LOCATOR_ICON ));
		try{
			Properties locator=Locator.toProperties(locator$);
			String action$=locator.getProperty(JRequester.REQUESTER_ACTION);
			entihome$=locator.getProperty(Entigrator.ENTIHOME);
			Entigrator entigrator=console.getEntigrator(entihome$);
			String text$=locator.getProperty(JTextEditor.TEXT);
			if(ACTION_NEW_ENTITY.equals(action$)){
				Sack newEntity=entigrator.ent_new("", text$);
				newEntity.createElement("field");
				newEntity.putElementItem("field", new Core(null,"name",null));
				newEntity.putElementItem("field", new Core(null,"description",null));
				newEntity.putElementItem("field", new Core(null,"ID",null));
				newEntity.createElement("fhandler");
				newEntity.putElementItem("fhandler", new Core(null,NwCategoryHandler.class.getName(),NwCategoryHandler.EXTENSION_KEY));
				newEntity.putElementItem("fhandler", new Core(null,FieldsHandler.class.getName(),null));
				newEntity.createElement("jfacet");
				newEntity.putElementItem("jfacet", new Core("gdt.jgui.entity.nwcategory.JNwCategoryFacetAddItem",NwCategoryHandler.class.getName(),"gdt.jgui.entity.nwcategory.JNwCategoryFacetOpenItem"));
				
				newEntity.putAttribute(new Core (null,"icon","nwCategory.png"));
				entigrator.replace(newEntity);
				entigrator.ent_assignProperty(newEntity, "fields", text$);
				entigrator.ent_assignProperty(newEntity, "nwCategory", text$);
				String icons$=entihome$+"/"+Entigrator.ICONS;
				Support.addHandlerIcon(JNwCategoryEditor.class, "nwCategory.png", icons$);
				newEntity=entigrator.ent_reindex(newEntity);
				reindex(console, entigrator, newEntity);
				JEntityFacetPanel efp=new JEntityFacetPanel(); 
				String efpLocator$=efp.getLocator();
				efpLocator$=Locator.append(efpLocator$,Locator.LOCATOR_TITLE,newEntity.getProperty("label"));
				efpLocator$=Locator.append(efpLocator$, Entigrator.ENTIHOME, entihome$);
				efpLocator$=Locator.append(efpLocator$, EntityHandler.ENTITY_KEY, newEntity.getKey());
				efpLocator$=Locator.append(efpLocator$, EntityHandler.ENTITY_LABEL, newEntity.getProperty("label"));
				JEntityPrimaryMenu.reindexEntity(console, efpLocator$);
				Stack<String> s=console.getTrack();
				s.pop();
				console.setTrack(s);
				JConsoleHandler.execute(console, efpLocator$);
				return;
			}
				entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
				Sack entity=entigrator.getEntityAtKey(entityKey$);
				String cellField$=locator.getProperty(CELL_FIELD);
				String name$=locator.getProperty(CELL_FIELD_NAME);
				Core core=entity.getElementItem("field", name$);
				if(CELL_FIELD_NAME.equals(cellField$))
					core.name=text$;
				else if (CELL_FIELD_VALUE.equals(cellField$))
					core.value=text$;
//				System.out.println("FieldsEditor:response:name="+core.name+" value="+core.value);
				entity.putElementItem("field", core);
				entigrator.replace(entity);
				String feLocator$=getLocator();
				feLocator$=Locator.append(locator$, Entigrator.ENTIHOME, entihome$);
				feLocator$=Locator.append(locator$, EntityHandler.ENTITY_KEY, entityKey$);
				feLocator$=Locator.remove(feLocator$, BaseHandler.HANDLER_METHOD);
				JConsoleHandler.execute(console, feLocator$);
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		}
	}
	@Override
	public void collectReferences(Entigrator entigrator, String entityKey$, ArrayList<JReferenceEntry> rel) {
	}
	
	@Override
	public JFacetRenderer instantiate(JMainConsole console, String locator$) {
		try{
			//System.out.println("JMovieEditor.instantiate:begin");
				this.console=console;
				Properties locator=Locator.toProperties(locator$);
				entihome$=locator.getProperty(Entigrator.ENTIHOME);
				
				entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
				if(entityKey$!=null)
					return super.instantiate(console, locator$);
				else
					return this;
			}catch(Exception e){
				Logger.getLogger(getClass().getName()).severe(e.toString());
			}
			return this;
	}
	@Override
	public String getFacetOpenItem() {
		// TODO Auto-generated method stub
		return JNwCategoryFacetOpenItem.class.getName();
	}
public String getFacetIcon() {
		
		return "nwCategory.png";
	}
}
