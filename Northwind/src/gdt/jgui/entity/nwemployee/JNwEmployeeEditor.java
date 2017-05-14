package gdt.jgui.entity.nwemployee;
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
import java.util.ArrayList;
import java.util.Properties;
import java.util.Stack;
import java.util.logging.Logger;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.apache.commons.codec.binary.Base64;

import gdt.data.entity.NwEmployeeHandler;
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

public class JNwEmployeeEditor extends JFieldsEditor {

	private static final long serialVersionUID = 1L;
	boolean debug=false;
	public JNwEmployeeEditor() {
		super();
	}
	@Override
	public String getLocator() {
		try{
			Properties locator=new Properties();
			locator.setProperty(BaseHandler.HANDLER_CLASS,JNwEmployeeEditor.class.getName());
			locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
			 locator.setProperty( JContext.CONTEXT_TYPE,getType());
			locator.setProperty(Locator.LOCATOR_TITLE,getTitle());
			locator.setProperty(BaseHandler.HANDLER_LOCATION,NwEmployeeHandler.EXTENSION_KEY);
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
	    	locator.setProperty( Locator.LOCATOR_ICON_FILE, "nwEmployee.png");
			return Locator.toString(locator);
			}catch(Exception e){
	        Logger.getLogger(getClass().getName()).severe(e.toString());
	        return null;
			}
	}
	@Override
	public String getTitle() {
			return "nwEmployee";
	}
	@Override
	public String getSubtitle() {
		return entityLabel$;	
	}
	@Override
	public String getType() {
			return "nwEmployee";
	}
	@Override
	public String getFacetHandler() {
		return NwEmployeeHandler.class.getName();
	}

	@Override
	public String getEntityType() {
		return "nwEmployee";
	}

	@Override
	public String getCategoryIcon(Entigrator entigrator) {
		return ExtensionHandler.loadIcon(entigrator, NwEmployeeHandler.EXTENSION_KEY, "nwEmployee.png");
	}

	@Override
	public String getCategoryTitle() {
		return "nwEmployees";
	}
	
	@Override
	public void reindex(JMainConsole console, Entigrator entigrator, Sack entity) {
		 try{
			// System.out.println("JPhoneEditor:reindex:0:entity="+entity.getProperty("label"));
		    	String fhandler$=NwEmployeeHandler.class.getName();
		    	if(entity.getElementItem("fhandler", fhandler$)!=null){
		    		entity.putElementItem("jfacet", new Core(JNwEmployeeFacetAddItem.class.getName(),fhandler$,JNwEmployeeFacetOpenItem.class.getName()));
					entity.putElementItem("fhandler", new Core(null,fhandler$,NwEmployeeHandler.EXTENSION_KEY));
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
	    editorLocator$=Locator.append(editorLocator$, JTextEditor.TEXT, "nwEmployee"+Identity.key().substring(0,4));
	    editorLocator$=Locator.append(editorLocator$,Locator.LOCATOR_TITLE,"nwEmployee entity");
	    JNwEmployeeEditor fe=new JNwEmployeeEditor();
	    String feLocator$=fe.getLocator();
	    Properties responseLocator=Locator.toProperties(feLocator$);
	    entihome$=Locator.getProperty(locator$,Entigrator.ENTIHOME );
	    if(entihome$!=null)
	      responseLocator.setProperty(Entigrator.ENTIHOME,entihome$);
	   responseLocator.setProperty(BaseHandler.HANDLER_CLASS,JNwEmployeeEditor.class.getName());
		responseLocator.setProperty(BaseHandler.HANDLER_METHOD,"response");
		responseLocator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
		responseLocator.setProperty(BaseHandler.HANDLER_METHOD,"response");
		responseLocator.setProperty(JRequester.REQUESTER_ACTION,ACTION_NEW_ENTITY);
		responseLocator.setProperty(Locator.LOCATOR_TITLE,"nwEmployee");
		 String responseLocator$=Locator.toString(responseLocator);
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
				newEntity.putElementItem("field", new Core(null,"ID",null));
				newEntity.createElement("fhandler");
				newEntity.putElementItem("fhandler", new Core(null,NwEmployeeHandler.class.getName(),NwEmployeeHandler.EXTENSION_KEY));
				newEntity.putElementItem("fhandler", new Core(null,FieldsHandler.class.getName(),null));
				newEntity.createElement("jfacet");
				newEntity.putElementItem("jfacet", new Core("gdt.jgui.entity.nwemployee.JNwEmployeeFacetAddItem",NwEmployeeHandler.class.getName(),"gdt.jgui.entity.nwemployee.JNwEmployeeFacetOpenItem"));
				newEntity.putAttribute(new Core (null,"icon","nwEmployee.png"));
				entigrator.replace(newEntity);
				entigrator.ent_assignProperty(newEntity, "fields", text$);
				entigrator.ent_assignProperty(newEntity, "nwEmployee", text$);
				String icons$=entihome$+"/"+Entigrator.ICONS;
				Support.addHandlerIcon(JNwEmployeeEditor.class, "nwEmployee.png", icons$);
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
		return JNwEmployeeFacetOpenItem.class.getName();
	}
public String getFacetIcon() {
		
		return "nwEmployee.png";
	}
public JMenu getContextMenu() {
	menu=new JMenu("Context");
	menu.addMenuListener(new MenuListener(){
		@Override
		public void menuSelected(MenuEvent e) {
//		System.out.println("FieldsEditor:getConextMenu:menu selected");
		
		if(editCellItem!=null) 
		menu.remove(editCellItem);
		if(deleteItemsItem!=null)
		   menu.remove(deleteItemsItem);
		if(copyItem!=null)
			   menu.remove(copyItem);
		if(pasteItem!=null)
			   menu.remove(pasteItem);
		if(cutItem!=null)
			   menu.remove(cutItem);
		
		if(hasEditingCell()){
			editCellItem = new JMenuItem("Edit item");
			editCellItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try{
					JTextEditor textEditor=new JTextEditor();
					String locator$=textEditor.getLocator();
					locator$=Locator.append(locator$, Entigrator.ENTIHOME, entihome$);
					locator$=Locator.append(locator$, EntityHandler.ENTITY_KEY, entityKey$);
					String responseLocator$=getEditCellLocator();
					text$=Locator.getProperty(responseLocator$, JTextEditor.TEXT);
					locator$=Locator.append(locator$, JTextEditor.TEXT, text$);
//					System.out.println("FieldsEditor:edit cell:text="+text$);
	                locator$=Locator.append(locator$,JRequester.REQUESTER_RESPONSE_LOCATOR, Locator.compressText(responseLocator$));				
					   JConsoleHandler.execute(console, locator$);
					}catch(Exception ee){
						LOGGER.severe(ee.toString());
					}
				}
			} );
			menu.add(editCellItem);
			}
		if(hasSelectedRows()){
		    deleteItemsItem = new JMenuItem("Delete items");
		    deleteItemsItem.addActionListener(new ActionListener() {
			   @Override
			   public void actionPerformed(ActionEvent e) {
				   int response = JOptionPane.showConfirmDialog(JNwEmployeeEditor.this, "Delete selected fields ?", "Confirm",
					        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				   if (response == JOptionPane.YES_OPTION) {
						   deleteRows();
					    }
				  
			   }
		      } );
		menu.add(deleteItemsItem);
		copyItem = new JMenuItem("Copy");
		copyItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				copy(false);
			}
		} );
		menu.add(copyItem);
		cutItem = new JMenuItem("Cut");
		cutItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			   copy(true);
			}
		} );
		menu.add(cutItem);
		}
		if(hasFieldsToPaste()){
			pasteItem = new JMenuItem("Paste");
			pasteItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try{
					String[] sa=console.clipboard.getContent();
					Properties locator;
					String type$;
					String sourceEntityKey$;
					Sack sourceEntity;
					String sourceEntihome$;
					Entigrator sourceEntigrator;
					for(String aSa:sa){
						//System.out.println("FieldsEditor:paste:"+aSa);
						locator=Locator.toProperties(aSa);
						type$=locator.getProperty(Locator.LOCATOR_TYPE);
						if(LOCATOR_TYPE_FIELD.equals(type$)){
							String action$=locator.getProperty(JRequester.REQUESTER_ACTION);
							String name$=locator.getProperty(CELL_FIELD_NAME);
							String value$=locator.getProperty(CELL_FIELD_VALUE);
							if(name$!=null){
							if(ACTION_COPY_FIELDS.equals(action$))
								entity.putElementItem("field", new Core(null,name$,value$));
							if(ACTION_CUT_FIELDS.equals(action$)){
								entity.putElementItem("field", new Core(null,name$,value$));	
								sourceEntihome$=locator.getProperty(Entigrator.ENTIHOME);
								sourceEntigrator=console.getEntigrator(sourceEntihome$);
								sourceEntityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
								sourceEntity=sourceEntigrator.getEntityAtKey(sourceEntityKey$);
								sourceEntity.removeElementItem("field", name$);
								sourceEntigrator.save(sourceEntity);
							}
						}
					}
					}
					entigrator.save(entity);
					Core[] ca=entity.elementGet("field");
					replaceTable(ca);
					}catch(Exception ee){
						LOGGER.info(ee.toString());
					}
				}
			} );
			menu.add(pasteItem);
			}
		}
		
		@Override
		public void menuDeselected(MenuEvent e) {
		}
		@Override
		public void menuCanceled(MenuEvent e) {
		}	
	});
	doneItem = new JMenuItem("Done");
	doneItem.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			save();
			if(requesterResponseLocator$!=null){
				try{
				   
					byte[] ba=Base64.decodeBase64(requesterResponseLocator$);
				   String responseLocator$=new String(ba,"UTF-8");
				   responseLocator$=Locator.append(responseLocator$, Entigrator.ENTIHOME, entihome$);
				   responseLocator$=Locator.append(responseLocator$, EntityHandler.ENTITY_KEY, entityKey$);
				//  System.out.println("FieldsEditor:done.response locator="+responseLocator$);
				   JConsoleHandler.execute(console, responseLocator$);
					}catch(Exception ee){
						LOGGER.severe(ee.toString());
					}
			}else
			  console.back();
		}
	} );
	menu.add(doneItem);
	JMenuItem cancelItem = new JMenuItem("Cancel");
	cancelItem.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			close();
			console.back();
		}
	} );
	menu.add(cancelItem);
    menu.addSeparator();		
	JMenuItem addItemItem = new JMenuItem("Add item");
	addItemItem.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			addRow();
		}
	} );
	menu.add(addItemItem);
	if(postMenu!=null){
		for(JMenuItem jmi:postMenu)
			menu.add(jmi);
	}
	return menu;
	}

}
