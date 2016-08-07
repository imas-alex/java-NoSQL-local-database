package gdt.jgui.entity.node;
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
import gdt.data.entity.NodeHandler;
import gdt.data.entity.BaseHandler;
import gdt.data.entity.EdgeHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.FacetHandler;
import gdt.data.entity.GraphHandler;
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
import gdt.jgui.entity.edge.JBondsPanel;
import gdt.jgui.entity.fields.JFieldsEditor;
import gdt.jgui.tool.JTextEditor;
public class JNodeEditor extends JFieldsEditor {

	private static final long serialVersionUID = 1L;
	public static final String ACTION_CREATE_NODE="action create node";
	public static final String ACTION_SET_DISPLAY_NODE="action set display node";
	JMenuItem itemEdge;
	//JMenuItem itemMap;
	public JNodeEditor() {
		super();
		postMenu=new JMenuItem[2];
		itemEdge=new JMenuItem("Add edge");
		itemEdge.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("JNodeEditor:add edge:");
			/*
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
				teLocator$=Locator.append(teLocator$,JRequester.REQUESTER_RESPONSE_LOCATOR,Locator.compressText(responseLocator$));
                JConsoleHandler.execute(console, teLocator$);
				*/
			}
		} );
		postMenu[0]=itemEdge;
	}
	@Override
	public String getLocator() {
		try{
			Properties locator=new Properties();
			locator.setProperty(BaseHandler.HANDLER_CLASS,JNodeEditor.class.getName());
			locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
			 locator.setProperty( JContext.CONTEXT_TYPE,getType());
			locator.setProperty(Locator.LOCATOR_TITLE,getTitle());
			locator.setProperty(BaseHandler.HANDLER_LOCATION,NodeHandler.EXTENSION_KEY);
			if(entityLabel$!=null){
				locator.setProperty(EntityHandler.ENTITY_LABEL,entityLabel$);
			}
			if(entityKey$!=null)
				locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
			if(entihome$!=null){
				locator.setProperty(Entigrator.ENTIHOME,entihome$);
			Entigrator entigrator=console.getEntigrator(entihome$);
			String icon$=ExtensionHandler.loadIcon(entigrator, GraphHandler.EXTENSION_KEY,"graph.png");
				// String icon$=Support.readHandlerIcon(JNodeEditor.class, "node.png");
			    if(icon$!=null)
			    	locator.setProperty(Locator.LOCATOR_ICON,icon$);
			}
			return Locator.toString(locator);
			}catch(Exception e){
	        Logger.getLogger(getClass().getName()).severe(e.toString());
	        return null;
			}
	}
	@Override
	public String getTitle() {
		return "Node";
	}
	@Override
	public String getSubtitle() {
		return entityLabel$;	
	}
	@Override
	public String getType() {
			return "node";
	}
	@Override
	public String getFacetHandler() {
		return NodeHandler.class.getName();
	}

	@Override
	public String getEntityType() {
		return "node";
	}

	@Override
	public String getCategoryIcon() {
		Entigrator entigrator=console.getEntigrator(entihome$);
		return ExtensionHandler.loadIcon(entigrator,EdgeHandler.EXTENSION_KEY,"node.png"); 

	}

	@Override
	public String getCategoryTitle() {
		return "Nodes";
	}
	
	@Override
	public void reindex(JMainConsole console, Entigrator entigrator, Sack entity) {
		 try{
			// System.out.println("JPhoneEditor:reindex:0:entity="+entity.getProperty("label"));
			Object fh= ExtensionHandler.loadHandlerInstance(entigrator,"_Tm142C8Sgti2iAKlDEcEXT2Kj1E","gdt.data.entity.NodeHandler");	
			System.out.println("JNodeEditor:reindex="+fh.getClass().getName()); 
			String fhandler$="gdt.data.entity.NodeHandler";
			 
					 //NodeHandler.class.getName();
		    	if(entity.getElementItem("fhandler", fhandler$)!=null){
					//System.out.println("JPhoneEditor:reindex:1:entity="+entity.getProperty("label"));
		    		entity.putElementItem("jfacet",new Core("gdt.jgui.entity.node.JNodeFacetAddItem",fhandler$,"gdt.jgui.entity.node.JNodeFacetAddItem")); 
		    				//new Core(JNodeFacetAddItem.class.getName(),fhandler$,JNodeFacetOpenItem.class.getName()));
					entity.putElementItem("fhandler", new Core(null,fhandler$,"_Tm142C8Sgti2iAKlDEcEXT2Kj1E"));
							//fhandler$,NodeHandler.EXTENSION_KEY));
					entigrator.save(entity);
				}
		    }catch(Exception e){
		    	Logger.getLogger(getClass().getName()).severe(e.toString());
		    }
	}
	@Override
	public String newEntity(JMainConsole console, String locator$) {
		System.out.println("JNodeEditor:newEntity:BEGIN");
		JTextEditor textEditor=new JTextEditor();
	    String editorLocator$=textEditor.getLocator();
	    editorLocator$=Locator.append(editorLocator$, JTextEditor.TEXT, "Node"+Identity.key().substring(0,4));
	    editorLocator$=Locator.append(editorLocator$,Locator.LOCATOR_TITLE,"Node entity");
	    JNodeEditor fe=new JNodeEditor();
	    String feLocator$=fe.getLocator();
	    Properties responseLocator=Locator.toProperties(feLocator$);
	    entihome$=Locator.getProperty(locator$,Entigrator.ENTIHOME );
	    if(entihome$!=null){
	      responseLocator.setProperty(Entigrator.ENTIHOME,entihome$);
	      Entigrator entigrator=console.getEntigrator(entihome$);
	      String icon$=ExtensionHandler.loadIcon(entigrator,NodeHandler.EXTENSION_KEY, "node.png");
	    		  //Support.readHandlerIcon(getClass(), "node.png");
		    editorLocator$=Locator.append(editorLocator$,Locator.LOCATOR_ICON,icon$);
		    
	    }
	    else
	    	System.out.println("JNodeEditor:newEntity:entihome is null");	
	   responseLocator.setProperty(BaseHandler.HANDLER_CLASS,JNodeEditor.class.getName());
		responseLocator.setProperty(BaseHandler.HANDLER_METHOD,"response");
		responseLocator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
		responseLocator.setProperty(JRequester.REQUESTER_ACTION,ACTION_NEW_ENTITY);
		responseLocator.setProperty(Locator.LOCATOR_TITLE,"Node");
		 String responseLocator$=Locator.toString(responseLocator);
    	//System.out.println("FieldsEditor:newEntity:responseLocator:=:"+responseLocator$);
		String requesterResponseLocator$=Locator.compressText(responseLocator$);
		editorLocator$=Locator.append(editorLocator$,JRequester.REQUESTER_RESPONSE_LOCATOR,requesterResponseLocator$);
		JConsoleHandler.execute(console,editorLocator$); 
		return editorLocator$;
	}

	@Override
	public void response(JMainConsole console, String locator$) {
		System.out.println("JNodeEditor:response:"+Locator.remove(locator$,Locator.LOCATOR_ICON ));
		try{
			Properties locator=Locator.toProperties(locator$);
			String action$=locator.getProperty(JRequester.REQUESTER_ACTION);
			entihome$=locator.getProperty(Entigrator.ENTIHOME);
			Entigrator entigrator=console.getEntigrator(entihome$);
			String text$=locator.getProperty(JTextEditor.TEXT);
			if(ACTION_NEW_ENTITY.equals(action$)){
				Sack newEntity=entigrator.ent_new("node", text$);
				newEntity.createElement("field");
				newEntity.putElementItem("field", new Core(null,"Node",text$));
				newEntity.createElement("fhandler");
				newEntity.putElementItem("fhandler", new Core(null,NodeHandler.class.getName(),NodeHandler.EXTENSION_KEY));
				newEntity.putElementItem("fhandler", new Core(null,FieldsHandler.class.getName(),null));
				newEntity.createElement("jfacet");
				newEntity.putElementItem("jfacet", new Core("gdt.jgui.entity.node.JNodeFacetAddItem",NodeHandler.class.getName(),"gdt.jgui.entity.node.JNodeFacetOpenItem"));
				newEntity.putAttribute(new Core (null,"icon","node.png"));
				entigrator.save(newEntity);
				entigrator.ent_assignProperty(newEntity, "fields", text$);
				entigrator.ent_assignProperty(newEntity, "node", text$);
				String icons$=entihome$+"/"+Entigrator.ICONS;
				Support.addHandlerIcon(JNodeEditor.class, "node.png", icons$);
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
			if(ACTION_SET_DISPLAY_NODE.equals(action$)){
//				System.out.println("JAddressEditor:response:set display address="+text$);
				entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
				Sack entity=entigrator.getEntityAtKey(entityKey$);
				entity.putElementItem("field", new Core(null,"Display node",text$));
				entigrator.save(entity);
				String feLocator$=getLocator();
				feLocator$=Locator.remove(feLocator$, BaseHandler.HANDLER_METHOD);
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
				entigrator.save(entity);
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
		System.out.println("JNodeEditor.instantiate:begin");
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
	
}
