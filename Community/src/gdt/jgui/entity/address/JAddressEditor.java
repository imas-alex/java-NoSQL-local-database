package gdt.jgui.entity.address;
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
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Stack;
import java.util.logging.Logger;
import javax.swing.JMenuItem;
import gdt.data.entity.AddressHandler;
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
import gdt.jgui.entity.webset.JWeblinkEditor;
import gdt.jgui.entity.webset.JWebsetFacetOpenItem;
import gdt.jgui.tool.JTextEditor;

public class JAddressEditor extends JFieldsEditor {

	private static final long serialVersionUID = 1L;
	public static final String ACTION_CREATE_ADDRESS="action create address";
	public static final String ACTION_SET_DISPLAY_ADDRESS="action set display address";
	JMenuItem itemCompose;
	JMenuItem itemMap;
	boolean debug=false;
	public JAddressEditor() {
		super();
		postMenu=new JMenuItem[2];
		itemCompose=new JMenuItem("Compose");
		itemCompose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//System.out.println("JAddressEditor:compose:");
				save();
				String displayAddress$=compose();
				JTextEditor te=new JTextEditor();
				String teLocator$=te.getLocator();
				teLocator$=Locator.append(teLocator$, Entigrator.ENTIHOME, entihome$);
				teLocator$=Locator.append(teLocator$, EntityHandler.ENTITY_KEY, entityKey$);
				teLocator$=Locator.append(teLocator$,JTextEditor.TEXT,displayAddress$);
				teLocator$=Locator.append(teLocator$,JTextEditor.TEXT_TITLE,"Display address");
				teLocator$=Locator.append(teLocator$,JTextEditor.SUBTITLE,entityLabel$);
				
				String responseLocator$=getLocator();
				responseLocator$=Locator.append(responseLocator$, BaseHandler.HANDLER_METHOD, "response");
				responseLocator$=Locator.append(responseLocator$, BaseHandler.HANDLER_CLASS, JAddressEditor.class.getName());
				responseLocator$=Locator.append(responseLocator$, BaseHandler.HANDLER_SCOPE, JConsoleHandler.CONSOLE_SCOPE);
				responseLocator$=Locator.append(responseLocator$, BaseHandler.HANDLER_LOCATION,JAddressFacetAddItem.EXTENSION_KEY );
				responseLocator$=Locator.append(responseLocator$,JRequester.REQUESTER_ACTION,ACTION_SET_DISPLAY_ADDRESS);
				//responseLocator$=Locator.append(responseLocator$,JFieldsEditor.RELOAD_ENTITY,Locator.LOCATOR_TRUE);
				teLocator$=Locator.append(teLocator$,JRequester.REQUESTER_RESPONSE_LOCATOR,Locator.compressText(responseLocator$));
				console.outdatedTreatment$=JContext.OUTDATED_RELOAD;
				JConsoleHandler.execute(console, teLocator$);
				
			}
		} );
		postMenu[0]=itemCompose;
		itemMap=new JMenuItem("Map");
		itemMap.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//System.out.println("JAddressEditor:map:");
				try{
					String displayAddress$=getField("Display address").replaceAll("\\s+", "+");
					String maps$="https://www.google.de/maps/place/";
					if(debug)
						System.out.println("JAddressEditor:map="+maps$+displayAddress$);
					Desktop.getDesktop().browse(new URI(maps$+displayAddress$));
				}catch(Exception ee){
					Logger.getLogger(JWeblinkEditor.class.getName()).info(ee.toString());
				}
			}
		} );
		postMenu[1]=itemMap;
	}
	@Override
	public String getLocator() {
		try{
			Properties locator=new Properties();
			locator.setProperty(BaseHandler.HANDLER_CLASS,JAddressEditor.class.getName());
			locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
			 locator.setProperty( JContext.CONTEXT_TYPE,getType());
			locator.setProperty(Locator.LOCATOR_TITLE,getTitle());
			locator.setProperty(BaseHandler.HANDLER_LOCATION,AddressHandler.EXTENSION_KEY);
			if(entityLabel$!=null){
				locator.setProperty(EntityHandler.ENTITY_LABEL,entityLabel$);
			}
			if(entityKey$!=null)
				locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
			
			if(entihome$!=null){
				locator.setProperty(Entigrator.ENTIHOME,entihome$);
				Entigrator entigrator=console.getEntigrator(entihome$);
				// String icon$=Support.readHandlerIcon(null,JAddressEditor.class, "address.png");
			//String icon$=ExtensionHandler.loadIcon(entigrator,AddressHandler.EXTENSION_KEY, "address.png");
			//if(icon$!=null)
			 //   	locator.setProperty(Locator.LOCATOR_ICON,icon$);
			}
			locator.setProperty( Locator.LOCATOR_ICON_CONTAINER, Locator.LOCATOR_ICON_CONTAINER_CLASS);
	    	locator.setProperty( Locator.LOCATOR_ICON_CLASS, getClass().getName());
	    	locator.setProperty( Locator.LOCATOR_ICON_FILE, "address.png");
			return Locator.toString(locator);
			}catch(Exception e){
	        Logger.getLogger(getClass().getName()).severe(e.toString());
	        return null;
			}
	}
	@Override
	public String getTitle() {
		if(message$==null)
			return "Address";
		else
			return "Adress"+message$;
		//return "Address";
	}
	@Override
	public String getSubtitle() {
		return entityLabel$;	
	}
	@Override
	public String getType() {
			return "address";
	}
	@Override
	public String getFacetHandler() {
		return AddressHandler.class.getName();
	}

	@Override
	public String getEntityType() {
		return "address";
	}

	@Override
	public String getCategoryIcon(Entigrator entigrator) {
		return ExtensionHandler.loadIcon(entigrator, AddressHandler.EXTENSION_KEY, "address.png");
				//Support.readHandlerIcon(null,JAddressEditor.class, "address.png");
	}

	@Override
	public String getCategoryTitle() {
		return "Addresses";
	}
	
	@Override
	public void reindex(JMainConsole console, Entigrator entigrator, Sack entity) {
		 try{
			// System.out.println("JPhoneEditor:reindex:0:entity="+entity.getProperty("label"));
		    	String fhandler$=AddressHandler.class.getName();
		    	if(entity.getElementItem("fhandler", fhandler$)!=null){
					//System.out.println("JPhoneEditor:reindex:1:entity="+entity.getProperty("label"));
		    		entity.putElementItem("jfacet", new Core(JAddressFacetAddItem.class.getName(),fhandler$,JAddressFacetOpenItem.class.getName()));
					entity.putElementItem("fhandler", new Core(null,fhandler$,JAddressFacetAddItem.EXTENSION_KEY));
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
	    editorLocator$=Locator.append(editorLocator$, JTextEditor.TEXT, "Address"+Identity.key().substring(0,4));
	    editorLocator$=Locator.append(editorLocator$,Locator.LOCATOR_TITLE,"Address entity");
	    //String icon$=Support.readHandlerIcon(null,getClass(), "address.png");
	    //editorLocator$=Locator.append(editorLocator$,Locator.LOCATOR_ICON,icon$);
	    JAddressEditor fe=new JAddressEditor();
	    String feLocator$=fe.getLocator();
	    Properties responseLocator=Locator.toProperties(feLocator$);
	    entihome$=Locator.getProperty(locator$,Entigrator.ENTIHOME );
	    if(entihome$!=null)
	      responseLocator.setProperty(Entigrator.ENTIHOME,entihome$);
	   responseLocator.setProperty(BaseHandler.HANDLER_CLASS,JAddressEditor.class.getName());
		responseLocator.setProperty(BaseHandler.HANDLER_METHOD,"response");
		responseLocator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
		responseLocator.setProperty(BaseHandler.HANDLER_METHOD,"response");
		responseLocator.setProperty(JRequester.REQUESTER_ACTION,ACTION_NEW_ENTITY);
		responseLocator.setProperty(Locator.LOCATOR_TITLE,"Address");
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
				Sack newEntity=entigrator.ent_new("address", text$);
				newEntity.createElement("field");
				newEntity.putElementItem("field", new Core(null,"Country",null));
				newEntity.putElementItem("field", new Core(null,"City",null));
				newEntity.putElementItem("field", new Core(null,"Postal code",null));
				newEntity.putElementItem("field", new Core(null,"Street",null));
				newEntity.putElementItem("field", new Core(null,"Display address",null));
				newEntity.createElement("fhandler");
				newEntity.putElementItem("fhandler", new Core(null,AddressHandler.class.getName(),JAddressFacetAddItem.EXTENSION_KEY));
				newEntity.putElementItem("fhandler", new Core(null,FieldsHandler.class.getName(),null));
				newEntity.createElement("jfacet");
				newEntity.putElementItem("jfacet", new Core("gdt.jgui.entity.address.JAddressFacetAddItem",AddressHandler.class.getName(),"gdt.jgui.entity.address.JAddressFacetOpenItem"));
				
				newEntity.putAttribute(new Core (null,"icon","address.png"));
				entigrator.replace(newEntity);
				entigrator.ent_assignProperty(newEntity, "fields", text$);
				entigrator.ent_assignProperty(newEntity, "address", text$);
				String icons$=entihome$+"/"+Entigrator.ICONS;
				Support.addHandlerIcon(JAddressEditor.class, "address.png", icons$);
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
			if(ACTION_SET_DISPLAY_ADDRESS.equals(action$)){
				if(debug)
				   System.out.println("JAddressEditor:response:set display address:locator="+locator$);
				entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
				Sack entity=entigrator.getEntityAtKey(entityKey$);
				entity.putElementItem("field", new Core(null,"Display address",text$));
				entigrator.replace(entity);
				entigrator.ent_assignProperty(entity, "address", text$);
				String feLocator$=getLocator();
				feLocator$=Locator.remove(feLocator$, BaseHandler.HANDLER_METHOD);
				feLocator$=Locator.append(feLocator$,JContext.OUTDATED_TREATMENT,JContext.OUTDATED_RELOAD);
				//console.outdatedTreatment$=JContext.OUTDATED_RELOAD;
				if(debug)
					   System.out.println("JAddressEditor:response:set display address:feLocator="+feLocator$);
					
				JConsoleHandler.execute(console, feLocator$);
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
	private String compose() {
		try{
		Entigrator entigrator=console.getEntigrator(entihome$);
		Sack address=entigrator.getEntityAtKey(entityKey$);
        StringBuffer sb = new StringBuffer();
        String field$ = address.getElementItemAt("field", "Street");
        if (field$ != null)
            if (field$.length() > 0) {
                sb.append(field$ + ',');
            }
        field$ = address.getElementItemAt("field", "Postal code");
        if (field$ != null)
            if (field$.length() > 0) {
                sb.append(field$ + ',');
            }
        field$ = address.getElementItemAt("field", "City");
        if (field$ != null)
            if (field$.length() > 0) {
                sb.append(field$ + ',');
            }
        field$ = address.getElementItemAt("field", "Country");
        if (field$ != null)
            if (field$.length() > 0) {
                sb.append(field$ );
            }
        String displayAddress$=sb.toString();
        return displayAddress$;
		}catch(Exception e){
			Logger.getLogger(JAddressEditor.class.getName()).severe(e.toString());
			return null;
		}
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
		return JAddressFacetOpenItem.class.getName();
	}
public String getFacetIcon() {
		
		return "address.png";
	}
}
