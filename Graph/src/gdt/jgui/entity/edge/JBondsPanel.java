package gdt.jgui.entity.edge;
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
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Stack;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import gdt.data.entity.BaseHandler;

import gdt.data.entity.EdgeHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.facet.ExtensionHandler;
import gdt.data.entity.facet.FieldsHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Identity;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JFacetRenderer;
import gdt.jgui.console.JItemPanel;
import gdt.jgui.console.JItemsListPanel;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.entity.JEntitiesPanel;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.entity.JEntityPrimaryMenu;
import gdt.jgui.entity.JReferenceEntry;

import gdt.jgui.tool.JTextEditor;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import org.apache.commons.codec.binary.Base64;

//import org.apache.commons.codec.binary.Base64;
/**
 * This class represents a list of web links assigned to the entity.
 * @author imasa
 *
 */
public class JBondsPanel extends JItemsListPanel implements JContext,JFacetRenderer,JRequester{
	private static final long serialVersionUID = 1L;
	/**
	 * The tag of a web link key.
	 */
	public static final String BOND_KEY="bond key" ;
	/**
	 * The tag of a web link name.
	 */
	public static final String EDGE_KEY="edge key" ;
	public static final String BOND_IN_NODE_KEY="bond in node key" ;
	public static final String BOND_OUT_NODE_KEY="bond out node key " ;
	public static final String ACTION_NEW_ENTITY="action new entity";
	public static final String BOND_OUT="out" ;
	public static final String BOND_IN="in" ;
	/**
	 * Indicates the locator type as a web link.
	 */
	public static final String LOCATOR_TYPE_BOND="locator type bond";
String entihome$;
String entityKey$;
String entityLabel$;
JMenuItem[] mia;
String requesterResponseLocator$;
/**
 * The default constructor.
 */
public JBondsPanel() {
		super();
	}
/**
 * Get the context locator.
 * @return the context locator.
 */
	@Override
	public String getLocator() {
		 Properties locator=new Properties();
		    locator.setProperty(Locator.LOCATOR_TYPE, JContext.CONTEXT_TYPE);
		    locator.setProperty(JContext.CONTEXT_TYPE,getType());
		    if(entihome$!=null)
		       locator.setProperty(Entigrator.ENTIHOME,entihome$);
		    if(entityKey$!=null)
			       locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
		    if(entityLabel$!=null)
			       locator.setProperty(EntityHandler.ENTITY_LABEL,entityLabel$);
		    String icon$=Support.readHandlerIcon(JEntitiesPanel.class, "bonds.png");
		    locator.setProperty(Locator.LOCATOR_TITLE, getTitle());
		    if(icon$!=null)
		    	locator.setProperty(Locator.LOCATOR_ICON,icon$);
		    locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
		    locator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
		    locator.setProperty(BaseHandler.HANDLER_LOCATION,EdgeHandler.EXTENSION_KEY);
		    return Locator.toString(locator);
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
			System.out.println("JBondsPanel:instantiate:BEGIN");
			 this.console=console;
			 this.locator$=locator$;
			 Properties locator=Locator.toProperties(locator$);
			 entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			 entihome$=locator.getProperty(Entigrator.ENTIHOME);
			 entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
			 Entigrator entigrator=console.getEntigrator(entihome$);
			 if(entityLabel$==null)
				 entityLabel$=entigrator.indx_getLabel(entityKey$);
			 Sack entity=entigrator.getEntityAtKey(entityKey$);
    		 JItemPanel[] ipa=getItems(console,entity);
        	 putItems(ipa);
        	return this;
        }catch(Exception e){
        Logger.getLogger(getClass().getName()).severe(e.toString());
        }
        return null;
        }

private JItemPanel[] getItems(JMainConsole console,Sack entity){
	try{
		ArrayList<JBondItem>ipl=new ArrayList<JBondItem>();
		Core[] ca=entity.elementGet("bond");
		if(ca!=null){
			ca=Core.sortAtType(ca);
			JBondItem ip;
			String ipLocator$;
			Properties ipLocator;
			//String icon$;
			Entigrator entigrator=console.getEntigrator(entihome$);
			String outLabel$;
			String inLabel$;
			String title$;
			String edge$=entity.getProperty("label");
			Core edge;
			JItemPanel bip=new JItemPanel();
			for(Core aCa:ca){
				  try{
					// System.out.println("JBondsPanel:getItems:0");
					 outLabel$=null;
					 inLabel$=null;
					//  System.out.println("JBondsPanel:getItems:1");
					  ipLocator=new Properties();
					  ipLocator.setProperty(Entigrator.ENTIHOME, entihome$);
					  ipLocator.setProperty(EntityHandler.ENTITY_KEY, entityKey$);
					  if(aCa.name!=null)
					   ipLocator.setProperty(BOND_KEY,aCa.name);
					  if(aCa.value!=null){
					     ipLocator.setProperty(BOND_IN_NODE_KEY,aCa.value);
					     inLabel$=entigrator.indx_getLabel(aCa.value);   
					  }
					  if(aCa.type!=null){
					   ipLocator.setProperty(BOND_OUT_NODE_KEY,aCa.type);
					   outLabel$=entigrator.indx_getLabel(aCa.type);
					    }
					  
				//	  edge$=entigrator.indx_getLabel(key$)
					  edge=entity.getElementItem("edge", aCa.name);
					  if(edge!=null){
						  System.out.println("JBondsPanel:getItems:edge key="+edge.value);
						  edge$=entigrator.indx_getLabel(edge.value);
					  }else
						  System.out.println("JBondsPanel:getItems:cannot find edge for bond="+aCa.name);
					  title$=outLabel$+" --("+edge$+")-> "+inLabel$;
					  ipLocator.setProperty(Locator.LOCATOR_TITLE, title$);
					  ipLocator.setProperty(Locator.LOCATOR_TYPE, LOCATOR_TYPE_BOND);
					  ipLocator.setProperty(Locator.LOCATOR_CHECKABLE, Locator.LOCATOR_TRUE);
					  ipLocator$=Locator.toString(ipLocator);
					  ip=new JBondItem(console,ipLocator$); 
					  ipl.add(ip);	  
					   }catch(Exception ee){
						   Logger.getLogger(JBondsPanel.class.getName()).info(ee.toString());
					   }
			}
		}
		Collections.sort(ipl,new ItemPanelComparator());
		return ipl.toArray(new JBondItem[0]);
	}catch(Exception e){
        Logger.getLogger(JBondsPanel.class.getName()).severe(e.toString());
    }
     return null;	
	}
/**
 * Get the context menu.
 * @return the context menu.
 */
@Override
public JMenu getContextMenu() {
menu=super.getContextMenu();
int cnt=menu.getItemCount();
mia=new JMenuItem[cnt];
for (int i=0;i<cnt;i++)
	mia[i]=menu.getItem(i);
menu.addMenuListener(new MenuListener(){
	@Override
	public void menuSelected(MenuEvent e) {
	//System.out.println("WeblinkPanel:getConextMenu:menu selected");
	 menu.removeAll();
	 if(mia!=null){
		 for(JMenuItem mi:mia)
			 menu.add(mi);
	 menu.addSeparator();
	 }
	 if(hasSelectedItems()){
	JMenuItem deleteItem = new JMenuItem("Delete");
	 deleteItem.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			 
			int response = JOptionPane.showConfirmDialog(console.getContentPanel(), "Delete ?", "Confirm",
				        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			   if (response == JOptionPane.YES_OPTION) {
				  String[] sa=JBondsPanel.this.listSelectedItems();
				  if(sa==null)
					  return;
				/*
				  String webLinkKey$;
				  Entigrator entigrator=console.getEntigrator(entihome$);
				  Sack entity=entigrator.getEntityAtKey(entityKey$);
				  for(String aSa:sa){
					  webLinkKey$=Locator.getProperty(aSa, WEB_LINK_KEY);
					  if(webLinkKey$==null)
						  continue;
                   entity.removeElementItem("web", webLinkKey$);
                   entity.removeElementItem("web.login", webLinkKey$);
                   entity.removeElementItem("web.icon", webLinkKey$);
				  }
                   entigrator.save(entity);  
                   
				   JConsoleHandler.execute(console,locator$);
				   */
			   }
			   }
	});
	menu.add(deleteItem);
	 }
	JMenuItem newItem = new JMenuItem("New");
	newItem.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			//System.out.println("JBondsPanel:new:"+locator$);
			
			Entigrator entigrator=console.getEntigrator(entihome$);	
			Sack entity=entigrator.getEntityAtKey(entityKey$);
			if(!entity.existsElement("bond"))
					entity.createElement("bond");
			String bondKey$=Identity.key();
			entity.putElementItem("bond", new Core(null,bondKey$,null));
		//	String icon$=Support.readHandlerIcon(JEntitiesPanel.class, "globe.png");
		    entigrator.save(entity);
		   // JBondsPanel.this.getPanel().removeAll();
		
		    JBondsPanel bp=new JBondsPanel();
			String bpLocator$=bp.getLocator();
			bpLocator$=Locator.append(bpLocator$, Entigrator.ENTIHOME, entihome$);
			bpLocator$=Locator.append(bpLocator$,EntityHandler.ENTITY_KEY,entityKey$);
			//bpLocator$=Locator.append(bpLocator$, BaseHandler.HANDLER_METHOD,"instantiate");
			JConsoleHandler.execute(console, bpLocator$);
		
		}
	} );
	menu.add(newItem);
	
	JMenuItem doneItem = new JMenuItem("Done");
	doneItem.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(requesterResponseLocator$!=null){
				try{
				   byte[] ba=Base64.decodeBase64(requesterResponseLocator$);
				   String responseLocator$=new String(ba,"UTF-8");
				   JConsoleHandler.execute(console, responseLocator$);
					}catch(Exception ee){
						Logger.getLogger(JBondsPanel.class.getName()).severe(ee.toString());
					}
				}else
					console.back();
		}
	} );
	menu.add(doneItem);
	 
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
 * Get context title.
 * @return the context title.
 */	
@Override
	public String getTitle() {
		String title$= "Bonds";
		return title$;
	}
/**
 * Get context subtitle.
 * @return the context subtitle.
 */
	@Override
	public String getSubtitle() {
		return entityLabel$;
	}
	/**
	 * Get context type.
	 * @return the context type.
	 */
	@Override
	public String getType() {
		return "bonds";
	}
	/**
	 * No action.
	 */
	@Override
	public void close() {
		// TODO Auto-generated method stub
	}
	/**
	 * Open URL in the system browser. 
	 * @param console the main console
	 * @param locator$ the locator string
	 */
	@Override
	public void response(JMainConsole console, String locator$) {
		System.out.println("JEdgeEditor:response:"+Locator.remove(locator$,Locator.LOCATOR_ICON ));
		try{
			Properties locator=Locator.toProperties(locator$);
			String action$=locator.getProperty(JRequester.REQUESTER_ACTION);
			entihome$=locator.getProperty(Entigrator.ENTIHOME);
			Entigrator entigrator=console.getEntigrator(entihome$);
			String text$=locator.getProperty(JTextEditor.TEXT);
			if(ACTION_NEW_ENTITY.equals(action$)){
				Sack newEntity=entigrator.ent_new("edge", text$);
				newEntity.createElement("field");
				newEntity.putElementItem("field", new Core(null,"Edge",text$));
				newEntity.createElement("fhandler");
				newEntity.putElementItem("fhandler", new Core(null,EdgeHandler.class.getName(),EdgeHandler.EXTENSION_KEY));
				newEntity.putElementItem("fhandler", new Core(null,FieldsHandler.class.getName(),null));
				newEntity.createElement("jfacet");
				newEntity.putElementItem("jfacet", new Core("gdt.jgui.entity.edge.JEdgeFacetAddItem",EdgeHandler.class.getName(),"gdt.jgui.entity.edge.JEdgeFacetOpenItem"));
				newEntity.putAttribute(new Core (null,"icon","edge.png"));
				entigrator.save(newEntity);
				entigrator.ent_assignProperty(newEntity, "fields", text$);
				entigrator.ent_assignProperty(newEntity, "edge", text$);
				String icons$=entihome$+"/"+Entigrator.ICONS;
				Support.addHandlerIcon(JBondsPanel.class, "edge.png", icons$);
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
			}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		}
		
	}
	@Override
	public String addIconToLocator(String locator$) {
		String icon$=Support.readHandlerIcon(JBondsPanel.class, "edge.png");
	    if(icon$!=null)
		return Locator.append(locator$, Locator.LOCATOR_ICON,icon$);
	    else
	    	return locator$;
	}
	@Override
	public String getFacetHandler() {
		return EdgeHandler.class.getName();
	}
	@Override
	public String getEntityType() {
		return "edge";
	}
	@Override
	public String getCategoryIcon() {
		Entigrator entigrator=console.getEntigrator(entihome$);
		return ExtensionHandler.loadIcon(entigrator,EdgeHandler.EXTENSION_KEY,"edge.png"); 
	}
	
	
	@Override
	public String getCategoryTitle() {
		return "Edges";
	}
	@Override
	public void adaptClone(JMainConsole console, String locator$) {
		 try{
		    	Properties locator=Locator.toProperties(locator$);
		    	entihome$=locator.getProperty(Entigrator.ENTIHOME);
		    	entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		    	Entigrator entigrator=console.getEntigrator(entihome$);
		    	Sack entity=entigrator.getEntityAtKey(entityKey$);
		    	entigrator.ent_assignProperty(entity,"edge",entity.getProperty("label")); 
		    	
		    }catch(Exception e){
		    	Logger.getLogger(JBondsPanel.class.getName()).severe(e.toString());
		    }
		
	}
	@Override
	public void adaptRename(JMainConsole console, String locator$) {
		try{
	    	Properties locator=Locator.toProperties(locator$);
	    	entihome$=locator.getProperty(Entigrator.ENTIHOME);
	    	entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
	    	Entigrator entigrator=console.getEntigrator(entihome$);
	    	Sack entity=entigrator.getEntityAtKey(entityKey$);
	    	entigrator.ent_assignProperty(entity,"edge",entity.getProperty("label")); 
	    	
	    }catch(Exception e){
	    	Logger.getLogger(JBondsPanel.class.getName()).severe(e.toString());
	    } 
		
	}
	@Override
	public void collectReferences(Entigrator entigrator, String entiyKey$, ArrayList<JReferenceEntry> sl) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void reindex(JMainConsole console, Entigrator entigrator, Sack entity) {
		 try{
				// System.out.println("JContactEditor:reindex:0:entity="+entity.getProperty("label"));
			    	String fhandler$=EdgeHandler.class.getName();
			    	if(entity.getElementItem("fhandler", fhandler$)!=null){
						//System.out.println("JContactEditor:reindex:1:entity="+entity.getProperty("label"));
			    		entity.putElementItem("jfacet", new Core(null,fhandler$,JEdgeFacetOpenItem.class.getName()));
						entity.putElementItem("fhandler", new Core(null,fhandler$,EdgeHandler.EXTENSION_KEY));
						entigrator.save(entity);
					}
			    }catch(Exception e){
			    	Logger.getLogger(getClass().getName()).severe(e.toString());
			    }
		
	}
	@Override
	public String newEntity(JMainConsole console, String locator$) {
		JTextEditor textEditor=new JTextEditor();
	    String editorLocator$=textEditor.getLocator();
	    editorLocator$=Locator.append(editorLocator$, JTextEditor.TEXT, "Edge"+Identity.key().substring(0,4));
	    editorLocator$=Locator.append(editorLocator$,Locator.LOCATOR_TITLE,"Edge entity");
	    String icon$=Support.readHandlerIcon(getClass(), "edge.png");
	    editorLocator$=Locator.append(editorLocator$,Locator.LOCATOR_ICON,icon$);
	   // JEdgeEditor fe=new JEdgeEditor();
	    JBondsPanel bp=new JBondsPanel();
	    String bpLocator$=bp.getLocator();
	    Properties responseLocator=Locator.toProperties(bpLocator$);
	    entihome$=Locator.getProperty(locator$,Entigrator.ENTIHOME );
	    if(entihome$!=null)
	      responseLocator.setProperty(Entigrator.ENTIHOME,entihome$);
	    //else
	    //	System.out.println("JNodeEditor:newEntity:entihome is null");	
	   responseLocator.setProperty(BaseHandler.HANDLER_CLASS,JBondsPanel.class.getName());
		responseLocator.setProperty(BaseHandler.HANDLER_METHOD,"response");
		responseLocator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
		responseLocator.setProperty(JRequester.REQUESTER_ACTION,ACTION_NEW_ENTITY);
		responseLocator.setProperty(Locator.LOCATOR_TITLE,"Edge");
		 String responseLocator$=Locator.toString(responseLocator);
    	//System.out.println("FieldsEditor:newEntity:responseLocator:=:"+responseLocator$);
		String requesterResponseLocator$=Locator.compressText(responseLocator$);
		editorLocator$=Locator.append(editorLocator$,JRequester.REQUESTER_RESPONSE_LOCATOR,requesterResponseLocator$);
		JConsoleHandler.execute(console,editorLocator$); 
		return editorLocator$;
	}
private String getNodeToSet(){
		try{
		String[] sa=console.clipboard.getContent();
		if(sa==null||sa.length<1)
			return null;
		Properties clipLocator;
		String foreignEntihome$;
		String entityKey$;
		for(String s:sa){
			clipLocator=Locator.toProperties(s);
			foreignEntihome$=clipLocator.getProperty(Entigrator.ENTIHOME);
			if(foreignEntihome$==null||entihome$.equals(foreignEntihome$))
				continue;
			entityKey$=clipLocator.getProperty(EntityHandler.ENTITY_KEY);
			if(entityKey$!=null)
				return entityKey$;
		}
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		}
		return null;
}

}
