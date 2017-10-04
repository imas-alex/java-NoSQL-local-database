package gdt.jgui.entity.graph;
/*
 * Copyright 2016 Alexander Imas
 * This file is extension of JEntigrator.

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
import java.util.Collections;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import gdt.data.entity.BaseHandler;
import gdt.data.entity.BondDetailHandler;
import gdt.data.entity.EdgeHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.GraphHandler;
import gdt.data.entity.facet.ExtensionHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Identity;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JItemPanel;
import gdt.jgui.console.JItemsListPanel;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.ReloadDialog;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.entity.edge.JEdgeEditor;
/**
 * This context displays a list of edges included in the graph. 
 * @author imasa
 */

public class JGraphEdgesPanel extends JItemsListPanel{
	
	private static final long serialVersionUID = 1L;
	private String entihome$;
    private String entityKey$;
    private String entityLabel$;
    boolean ignoreOutdate=false;
    JMenu menu1;
  /**
   * Default constructor
   */
    public JGraphEdgesPanel()
   	{
   	    super();
   	    
   	}  
    /**
	 * Get the context locator.
	 * @return the context locator.
	 */		
    @Override
	public String getLocator() {
		try{
			Properties locator=new Properties();
			locator.setProperty(BaseHandler.HANDLER_CLASS,JGraphEdgesPanel.class.getName());
			locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
			 locator.setProperty( JContext.CONTEXT_TYPE,getType());
			locator.setProperty(Locator.LOCATOR_TITLE,getTitle());
			locator.setProperty(BaseHandler.HANDLER_LOCATION,GraphHandler.EXTENSION_KEY);
			if(entityLabel$!=null){
				locator.setProperty(EntityHandler.ENTITY_LABEL,entityLabel$);
			}
			if(entityKey$!=null)
				locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
			if(entihome$!=null){
				locator.setProperty(Entigrator.ENTIHOME,entihome$);
			//Entigrator entigrator=console.getEntigrator(entihome$);
			//String icon$= ExtensionHandler.loadIcon(entigrator, EdgeHandler.EXTENSION_KEY,"edge.png");
			 //   if(icon$!=null)
			  //  	locator.setProperty(Locator.LOCATOR_ICON,icon$);
			}
			locator.setProperty( Locator.LOCATOR_ICON_CONTAINER, Locator.LOCATOR_ICON_CONTAINER_CLASS);
			locator.setProperty( Locator.LOCATOR_ICON_CLASS, JEdgeEditor.class.getName());
			locator.setProperty( Locator.LOCATOR_ICON_FILE, "edge.png");
			locator.setProperty( Locator.LOCATOR_ICON_CLASS_LOCATION,GraphHandler.EXTENSION_KEY);
			return Locator.toString(locator);
			}catch(Exception e){
	        Logger.getLogger(getClass().getName()).severe(e.toString());
	        return null;
			}
	
}
    /**
	 * Create a new facet renderer.
	 * @param console the main console.
	 * @param locator$ the locator string.
	 * @return the fields editor.
	 */
    @Override
public JContext instantiate(JMainConsole console, String locator$) {
	
	try{
			this. console=console;
			this.locator$=locator$;
			Properties locator=Locator.toProperties(locator$);
			 //list$=locator.getProperty(EntityHandler.ENTITY_LIST);
			 entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			 entihome$=locator.getProperty(Entigrator.ENTIHOME);
			 Entigrator  entigrator=console.getEntigrator(entihome$);
			 entityLabel$=entigrator.indx_getLabel(entityKey$);
			 Sack graph=entigrator.getEntityAtKey(entityKey$);
			 Core[] ca=graph.elementGet("edge.entity");
			 ArrayList <String>sl=new ArrayList<String>(); 
			 if(ca!=null){
			  for(Core c:ca){
				  if(!sl.contains(c.value))
					  sl.add(c.value);
			  }
			  ArrayList <JItemPanel>ipl=new ArrayList<JItemPanel>(); 
			  String edgeLocator$;
			  String edgeIcon$;
			  JItemPanel itemPanel;
		for(String s:sl){
			edgeLocator$=EntityHandler.getEntityLocatorAtKey(entigrator, s);
			edgeLocator$=Locator.append(edgeLocator$,Entigrator.ENTIHOME , entihome$);
			edgeLocator$=Locator.append(edgeLocator$,Locator.LOCATOR_CHECKABLE , Locator.LOCATOR_TRUE);
			  JEntityFacetPanel em=new JEntityFacetPanel();
			   em.instantiate(console, edgeLocator$);
			   String emLocator$=em.getLocator();
			   emLocator$=Locator.append(emLocator$,Locator.LOCATOR_CHECKABLE, Locator.LOCATOR_TRUE);
			   emLocator$=Locator.append(emLocator$,Locator.LOCATOR_TITLE,entigrator.indx_getLabel(s));
			  // edgeIcon$=entigrator.readIconFromIcons(entigrator.ent_getIconAtKey(s));
			  // if(edgeIcon$!=null)
				//   emLocator$=Locator.append(emLocator$,Locator.LOCATOR_ICON,edgeIcon$);
			   itemPanel=new JItemPanel(console,emLocator$);
			   ipl.add(itemPanel);
			
		}
		Collections.sort(ipl,new ItemPanelComparator());
      	 putItems(ipl.toArray(new JItemPanel[0]));
			 }	
      	 return this;
      }catch(Exception e){
      
      Logger.getLogger(JGraphEdgesPanel.class.getName()).severe(e.toString());
      }
	  
	return null;  
	  
} 
    /**
	 * Get the context menu.
	 * @return the context menu.
	 */
    @Override
public JMenu getContextMenu() {
	 menu1=new JMenu("Context");
	  menu=super.getContextMenu();
	  if(menu!=null){
    int cnt=menu.getItemCount();
	
	  mia=new JMenuItem[cnt];
	  for (int i=0;i<cnt;i++)
		  mia[i]=menu.getItem(i);
	  }
	  	
	  menu1.addMenuListener(new MenuListener(){
	  	@Override
	  	public void menuSelected(MenuEvent e) {
	  	
	  		 menu1.removeAll();
	  		 if(mia!=null){
	  		 for(JMenuItem mi:mia)
	  			try{
	  			 if(mi!=null&&mi.getText()!=null){ 
	  			    menu1.add(mi);
	  			 }
	  			}catch(Exception ee){
	  				 System.out.println("JGraphNode:getConextMenu:"+ee.toString());
	  			}
	  	
	  	 }
	  	
	  //	 System.out.println("JGraphNode:getContextMenu:2:menu cnt="+menu1.getItemCount());
	  	 menu1.addSeparator();
	  
	  	 if(hasSelectedItems()){
	  	JMenuItem FilterItem = new JMenuItem("Filter");
	  	 FilterItem.addActionListener(new ActionListener() {
	  		@Override
	  		public void actionPerformed(ActionEvent e) {
	  			 filter();
	  			   }
	  	});
	  	menu1.add(FilterItem);
	  	 }
		JMenuItem mapItem = new JMenuItem("Map");
	  	mapItem.addActionListener(new ActionListener() {
	  		@Override
	  		public void actionPerformed(ActionEvent e) {
	  			try{
	  				JGraphRenderer gr=new JGraphRenderer();
					String gr$=gr.getLocator();
					gr$=Locator.append(gr$, Entigrator.ENTIHOME, entihome$);
					gr$=Locator.append(gr$, EntityHandler.ENTITY_KEY, entityKey$);
					JConsoleHandler.execute(console,gr$);
	  				
	  			}catch(Exception ee){
	  				System.out.println("JGraphEdges:getContextMenu:map:"+ee.toString());
	  			}
	  		}
	  	
	  	});
	  	menu1.add(mapItem);  
	  	}
	  	@Override
		public void menuDeselected(MenuEvent e) {
		}
		@Override
		public void menuCanceled(MenuEvent e) {
		}	
	});
	return menu1;
	}
private void filter(){
	try{
		String[] sa=listSelectedItems();
		if(sa==null)
			return;
		Properties locator;
		String edgeEntity$;
		ArrayList<String>el=new ArrayList<String>();
		for(String s:sa){
			locator=Locator.toProperties(s);
			edgeEntity$=locator.getProperty(EntityHandler.ENTITY_KEY);
			el.add(edgeEntity$);
			//System.out.println("JGraphEdgesPanel:filter:edge entity="+edgeEntity$);
		}
		Entigrator entigrator=console.getEntigrator(entihome$);
		Sack graphEntity=entigrator.getEntityAtKey(entityKey$);
		graphEntity.removeElement("bond.select");
		graphEntity.createElement("bond.select");
		Core[] ca=graphEntity.elementGet("edge.entity");
		ArrayList<String>bl=new ArrayList<String>();
		for(Core c:ca ){
			if(el.contains(c.value)){
				//System.out.println("JGraphEdgesPanel:filter:bond key="+c.name);
				bl.add(c.name);
				graphEntity.putElementItem("bond.select", graphEntity.getElementItem("bond", c.name));
				
			}
		}
		ArrayList <String>nl=new ArrayList<String>();
		ca=graphEntity.elementGet("bond");
		for(Core c:ca ){
			if(!nl.contains(c.type))
				nl.add(c.type);
			if(!nl.contains(c.value))
				nl.add(c.value);
		}
		//System.out.println("JGraphEdgesPanel:filter:nl="+nl.size());
		GraphHandler.undoPush(console, getLocator());
		sa=graphEntity.elementListNoSorted("node.select");
		if(sa==null){
			graphEntity.createElement("node.select");
			sa=graphEntity.elementListNoSorted("node");
		}else
			graphEntity.clearElement("node.select");
		for(String s:sa){
			if(nl.contains(s)){
				graphEntity.putElementItem("node.select", new Core(null,s,null));
				//System.out.println("JGraphEdgesPanel:filter:node key="+s);
			}
		}
		
		entigrator.ent_alter(graphEntity);
		JGraphRenderer gr=new JGraphRenderer();
		String gr$=gr.getLocator();
		gr$=Locator.append(gr$, Entigrator.ENTIHOME, entihome$);
		gr$=Locator.append(gr$, EntityHandler.ENTITY_KEY, entityKey$);
		JConsoleHandler.execute(console,gr$);
	}catch(Exception e){
		Logger.getLogger(JGraphEdgesPanel.class.getName()).severe(e.toString());
	}
}
/**
 * Get title of the context.  
 * @return the title of the context.
 */	
@Override
	public String getTitle() {
		return "Edges";
	}
/**
 * Get subtitle of the context.  
 * @return the subtitle of the context.
 */	
	@Override
	public String getSubtitle() {
		
		return entityLabel$;
	}
	 /**
     * Get type of the  context.  
     * @return the type of the context.
     */	
	@Override
	public String getType() {
		
		return "Graph edges";
	}
/**
 * no action
 */
	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
@Override
public void activate() {
	Entigrator  entigrator=console.getEntigrator(entihome$);
	Sack entity=entigrator.getEntityAtKey(entityKey$);
	if(entity==null)
		return;
	if(ignoreOutdate){
		ignoreOutdate=false;
		return;
	}
	
	if(!entigrator.ent_outdated(entity))
		return;
	int n=new ReloadDialog(this).show();
	if(2==n){
		ignoreOutdate=true;
	
		return;
	}
	if(1==n){
		entity.putAttribute(new Core(null,Entigrator.SAVE_ID,Identity.key()));
		entigrator.ent_alter(entity);
		
	}
	if(0==n){
			entity=entigrator.ent_reload(entityKey$);
			instantiate(console,getLocator());
		}
	
}

}
