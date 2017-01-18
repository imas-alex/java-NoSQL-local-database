package gdt.jgui.entity.graph;
/* Copyright 2016 Alexander Imas
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
import javax.swing.JOptionPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import gdt.data.entity.BaseHandler;
import gdt.data.entity.BondDetailHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.GraphHandler;
import gdt.data.entity.NodeHandler;
import gdt.data.entity.facet.ExtensionHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JItemPanel;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.entity.JEntitiesPanel;
import gdt.jgui.entity.JEntityFacetPanel;
/**
 * This context displays a list of nodes included in the graph. 
 * @author imasa
 */
public class JGraphNodes extends JEntitiesPanel{
	private Logger LOGGER=Logger.getLogger(JGraphNodes.class.getName());
	JMenu menu1;
	private static final long serialVersionUID = 1L;
	String message$;
	Sack entity;
/**
 * The default constructor
 */
	public JGraphNodes (){
	        super();	
	    }
 /**
 * Get the context locator.
		 * @return the context locator.
 */		
	  @Override
		public String getLocator() {
			try{
			String locator$=super.getLocator();
					if(entihome$!=null){
						Entigrator entigrator=console.getEntigrator(entihome$);
					//String icon$=ExtensionHandler.loadIcon(entigrator, GraphHandler.EXTENSION_KEY,"node.png");
					//if(icon$!=null)
					 //   	locator$=Locator.append(locator$, Locator.LOCATOR_ICON,icon$);
					}
					locator$=Locator.append(locator$, Locator.LOCATOR_ICON_CONTAINER, Locator.LOCATOR_ICON_CONTAINER_CLASS);
					locator$=Locator.append(locator$, Locator.LOCATOR_ICON_CLASS, getClass().getName());
					locator$=Locator.append(locator$, Locator.LOCATOR_ICON_FILE, "graph.png");
					locator$=Locator.append(locator$, Locator.LOCATOR_ICON_CLASS_LOCATION,BondDetailHandler.EXTENSION_KEY);
					locator$=Locator.append(locator$,BaseHandler.HANDLER_CLASS,JGraphNodes.class.getName());
					locator$=Locator.append(locator$,BaseHandler.HANDLER_LOCATION,GraphHandler.EXTENSION_KEY);
					return locator$; 
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
				 entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
				 entihome$=locator.getProperty(Entigrator.ENTIHOME);
				 JItemPanel[] ipl= listNodes();
				 if(ipl!=null)
	        	   putItems(ipl);
				 
	        	return this;
	        }catch(Exception e){
	        
	        LOGGER.severe(e.toString());
	        }
		  
		  JContext context=super.instantiate(console, locator$);
		  return context;
	  } 
	  @Override
	  /**
	   * Get the context title.
	   * @return the context title.	
	   */
	
  public String getTitle() {
		  if(message$==null)
				return "Nodes";
			else
				return "Nodes"+message$;
		  
	  		
	  	}
	  /**
		 * Get the context menu.
		 * @return the context menu.
		 */ 
	  @Override
	  public JMenu getContextMenu() {
		  //menu=super.getContextMenu();
	   menu1=new JMenu("Context");
		  //menu.setName("Context");
		  menu=super.getContextMenu();
		  if(menu!=null){
	      int cnt=menu.getItemCount();
		//  System.out.println("JGraphNode:getContextMenu:super menu cnt="+cnt);
		  mia=new JMenuItem[cnt];
		  for (int i=0;i<cnt;i++)
			  mia[i]=menu.getItem(i);
		  
		  }
		  	
		  menu1.addMenuListener(new MenuListener(){
		  	@Override
		  	public void menuSelected(MenuEvent e) {
		  		// System.out.println("JGraphNode:getContextMenu:menu selected");
		  		 menu1.removeAll();
		  		 if(mia!=null){
		  		 for(JMenuItem mi:mia)
		  			try{
		  			 if(mi!=null&&mi.getText()!=null){ 
		  			    menu1.add(mi);
		  			//  System.out.println("JGraphNode:getConextMenu:add item="+mi.getText());
		  			 }
		  			}catch(Exception ee){
		  				 System.out.println("JGraphNode:getConextMenu:"+ee.toString());
		  			}
		  		menu1.addSeparator();
		  	 }
		  	
		  //	 System.out.println("JGraphNode:getContextMenu:2:menu cnt="+menu1.getItemCount());
		  	
		  
		  	 if(hasSelectedItems()){
		  	JMenuItem deleteItem = new JMenuItem("Delete");
		  	 deleteItem.addActionListener(new ActionListener() {
		  		@Override
		  		public void actionPerformed(ActionEvent e) {
		  			/* 
		  			String[] sa=JGraphNodes.this.listSelectedItems();
	  				  if(sa==null)
	  					  return;
	  					  */
		  			int response = JOptionPane.showConfirmDialog(console.getContentPanel(), "Delete ?", "Confirm",
		  				        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		  			   if (response == JOptionPane.YES_OPTION) {
		  				  deleteNodes();
		  				JGraphNodes gn=new JGraphNodes();
		  				String gnLocator$=gn.getLocator();
		  				gnLocator$=Locator.append(gnLocator$,Entigrator.ENTIHOME, entihome$);
		  				gnLocator$=Locator.append(gnLocator$,EntityHandler.ENTITY_KEY, entityKey$);
		  				JConsoleHandler.execute(console, gnLocator$);
		  			   }
		  			   }
		  	});
		  	menu1.add(deleteItem);
		  	 }
		  
		  //	System.out.println("JGraphNode:getContextMenu:add 'All nodes'"); 
		  	//JMenuItem allNodesItem = new JMenuItem("Insert all nodes");
		  	JMenuItem allNodesItem = new JMenuItem("Expand cascade");
		  	allNodesItem.addActionListener(new ActionListener() {
		  		@Override
		  		public void actionPerformed(ActionEvent e) {
		  			try{
		  				expandCascade();
		  				JGraphNodes gn=new JGraphNodes();
		  				String gnLocator$=gn.getLocator();
		  				gnLocator$=Locator.append(gnLocator$,Entigrator.ENTIHOME, entihome$);
		  				gnLocator$=Locator.append(gnLocator$,EntityHandler.ENTITY_KEY, entityKey$);
		  				JConsoleHandler.execute(console, gnLocator$);
		  				
		  				
		  				//JConsoleHandler.execute(console, getLocator());
		  				/*
		  				Entigrator entigrator=console.getEntigrator(entihome$);
		  				
		  				
		  				String []sa= entigrator.indx_listEntitiesAtPropertyName("node");
		  				if(sa!=null){
		  					ArrayList <String> la=new ArrayList<String>();
		  					String label$;
		  					for(String s:sa){
		  						label$=entigrator.indx_getLabel(s);
		  						if(label$!=null)
		  							la.add(label$);
		  					}
		  					sa=la.toArray(new String[0]);
		  					if(sa.length<1)
		  						return;
		  					String list$=Locator.toString(sa);
		  					JEntitiesPanel ep=new JEntitiesPanel();
		  					String locator$=ep.getLocator();
		  					locator$=Locator.append(locator$, Entigrator.ENTIHOME, entihome$);
		  					locator$=Locator.append(locator$, EntityHandler.ENTITY_LIST, list$);
		  					JConsoleHandler.execute(console, locator$);
		  					
		  				}
		  				*/
		  			}catch(Exception ee){
		  				System.out.println("JGraphNode:getConextMenu:all nodes:"+ee.toString());
		  			}
		  		}
		  	
		  	});
		  	allNodesItem.setVisible(true);
		  	menu1.add(allNodesItem);  
			 if(hasNodesToPaste()){
				  	JMenuItem pasteItem = new JMenuItem("Paste");
				  	 pasteItem.addActionListener(new ActionListener() {
				  		@Override
				  		public void actionPerformed(ActionEvent e) {
				  			   pasteNodes(); 
				  			   }
				  	});
				  	menu1.add(pasteItem);
				  	 }
			 if(hasSelectedItems()){
				  	JMenuItem neighboursItem = new JMenuItem("Select  neighbours");
				  	neighboursItem.addActionListener(new ActionListener() {
				  		@Override
				  		public void actionPerformed(ActionEvent e) {
				  			   selectNeighbours(); 
				  			   }
				  	});
				  	menu1.add(neighboursItem);
				  	 }
			 menu1.addSeparator();
			 JMenuItem mapItem = new JMenuItem("Map");
			  	mapItem.addActionListener(new ActionListener() {
			  		@Override
			  		public void actionPerformed(ActionEvent e) {
			  			JGraphRenderer gr=new JGraphRenderer();
			  			String gr$=gr.getLocator();
			  			gr$=Locator.append(gr$, Entigrator.ENTIHOME, entihome$);
			  			gr$=Locator.append(gr$, EntityHandler.ENTITY_KEY, entityKey$);
			  			Entigrator entigrator=console.getEntigrator(entihome$);
			  			//String icon$=ExtensionHandler.loadIcon(entigrator, GraphHandler.EXTENSION_KEY, "map.png");
			  			//gr$=Locator.append(gr$,Locator.LOCATOR_ICON,icon$);
			  			JConsoleHandler.execute(console, gr$);
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
  private   boolean hasNodesToPaste(){
		try{
			String[] sa=console.clipboard.getContent();
			if(sa==null)
				return false;
			//System.out.println("JGraphNode:hasNodesToPaste:sa="+sa.length);
			Properties locator;
			Sack node;
			Entigrator entigrator=console.getEntigrator(entihome$);
			for(String s:sa){
				try{
				//	System.out.println("JGraphNode:hasNodesToPaste:node locator="+s);
			    locator=Locator.toProperties(s);
			    node=entigrator.getEntityAtKey(locator.getProperty(EntityHandler.ENTITY_KEY));
			    if(node.getProperty("node")!=null)
			    		return true;
				}catch(Exception ee){}
			}
		}catch(Exception e){
			Logger.getLogger(JGraphNodes.class.getName()).severe(e.toString());
		}
		return false;
	}
  private   void pasteNodes(){
		try{
			String[] sa=console.clipboard.getContent();
			
			Properties locator;
			Entigrator entigrator=console.getEntigrator(entihome$);
			Sack graph= entigrator.getEntityAtKey(entityKey$);
			if(!graph.existsElement("node"))
				graph.createElement("node");
			String nodeKey$;
			String nodeIcon$;
			Sack node;
			for(String s:sa){
				try{
			    locator=Locator.toProperties(s);
			    nodeKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			    node=entigrator.getEntityAtKey(nodeKey$);
			   nodeIcon$=entigrator.ent_getIconAtKey(nodeKey$);
		    	if(node.getProperty("node")!=null){
		                graph.putElementItem("node", new Core(nodeIcon$,nodeKey$,node.getProperty("label")));
			    	}
				}catch(Exception ee){}
			}
		 entigrator.save(graph);
		 JConsoleHandler.execute(console, getLocator());
		}catch(Exception e){
			Logger.getLogger(JGraphNodes.class.getName()).severe(e.toString());
		}
		
	}
  /**
		 * List node item panels in the graph.
		 * @return the array of node item panels.
		 */ 
  public  JItemPanel[] listNodes(){
		try{
			Entigrator entigrator=console.getEntigrator(entihome$);
			Sack graph=entigrator.getEntityAtKey(entityKey$);
			String[] la=graph.elementList("node");
			if(la==null)
				return null;
			String list$=Locator.toString(la);
			Properties locator=new Properties();
			locator.setProperty(Entigrator.ENTIHOME, entihome$);
			locator.setProperty(EntityHandler.ENTITY_LIST,list$);
			return JEntitiesPanel.listEntitiesAtList(console, Locator.toString(locator));
		}catch(Exception e) {
      	Logger.getLogger(JGraphNodes.class.getName()).severe(e.toString());
          return null;
      }
	}
 /**
  * Complete the closing of the context.
  */
  @Override
 	public void close() {
 	 // System.out.println("JGraphNode:close:BEGIN");
 	  
 	  try{
 	  
 	    	Entigrator entigrator=console.getEntigrator(entihome$);
 			entity=entigrator.getEntityAtKey(entityKey$);
 			
 			if(entity.existsElement("node.select"))
 				entity.clearElement("node.select");
 			else
 				entity.createElement("node.select");
 			String[]  sa=listSelectedItems();
 		 	if(sa!=null)	
 			for(String s:sa)
 				entity.putElementItem("node.select", new Core(null,Locator.getProperty(s, EntityHandler.ENTITY_KEY),null));
 			
 			entigrator.save(entity);
 	    
 	  }catch(Exception e){
 		  Logger.getLogger(JGraphNodes.class.getName()).severe(e.toString());
 	  }
 	  super.close();
 	}
  private void expandCascade(){
	  try{
	    	Entigrator entigrator=console.getEntigrator(entihome$);
			Sack graph=entigrator.getEntityAtKey(entityKey$);
			String[]  sa=graph.elementListNoSorted("node");
			String []na=NodeHandler.expandCascade(entigrator, sa);
			String nodeIcon$;
			String nodeLabel$;
			if(na!=null)
				for(String n:na){
					//System.out.println("JGraphNodes:expandCascade:try node="+n);
					try{
				    nodeLabel$=entigrator.indx_getLabel(n);
				    nodeIcon$=entigrator.ent_getIconAtKey(n);
		                graph.putElementItem("node", new Core(nodeIcon$,n,nodeLabel$));
					}catch(Exception ee){
						System.out.println("JGraphNodes:expandCascade:"+ee.toString());
					}
				}
			 entigrator.save(graph);
			 
			 NodeHandler.rebuild(entigrator, entityKey$);
			 graph=removeStandAloneNodes();
			// graph.print();
			 
			  }catch(Exception e){
		  Logger.getLogger(JGraphNodes.class.getName()).severe(e.toString());
	  }
  }
  private void  selectNeighbours(){
	 try{
	 	  
	    	Entigrator entigrator=console.getEntigrator(entihome$);
			Sack graph=entigrator.getEntityAtKey(entityKey$);
			String[]  sa=listSelectedItems();
			String nodeKey$;
		 	if(sa!=null){
		 		Core[] ca=graph.elementGet("bond");
		    ArrayList<String>nbl=new ArrayList<String>();		
			for(String s:sa){
				nodeKey$=Locator.getProperty(s, EntityHandler.ENTITY_KEY);
				for(Core c:ca){
					if(c.value.equals(nodeKey$))
						if(!nbl.contains(c.type))
							nbl.add(c.type);
					if(c.type.equals(nodeKey$))
						if(!nbl.contains(c.value))
								nbl.add(c.value);
				}
					
			}
           	for(String nb:nbl)
            	graph.putElementItem("node.select", new Core(null,nb,null));
           	
			entigrator.save(graph);
			JItemPanel[] ipa=getItems();
			String [] nka=graph.elementListNoSorted("node.select");
			
			for(JItemPanel ip:ipa){
				nodeKey$=Locator.getProperty(ip.getLocator(),EntityHandler.ENTITY_KEY);
				for(String nk:nka)
					if(nk.equals(nodeKey$))
						ip.setChecked(true);
			}
				
		 	}
			
	  }catch(Exception e){
		  Logger.getLogger(JGraphNodes.class.getName()).severe(e.toString());
	  }
  }
  private boolean nodeIsSelected(Sack graph,String nodeKey$){
	  try{
		  if(nodeKey$==null)
			  return false;
		  Core[] ca=graph.elementGet("node.select");
		  for(Core c:ca)
			  if(c.name.equals(nodeKey$))
			  return true;
	  }catch(Exception e){
		  Logger.getLogger(JGraphNodes.class.getName()).severe(e.toString());
	  }
	  return false; 
  }
  private boolean nodeIsConnected(Sack graph,String nodeKey$){
	  try{
		  if(nodeKey$==null)
			  return false;
		  if(!nodeIsSelected(graph,nodeKey$))
			  return false;
          Core[] ba=graph.elementGet("bond");
          for(Core b:ba){
        	  if(nodeKey$.equals(b.type))
        	    if(nodeIsSelected(graph,b.value))
        		  return true;
          	if(nodeKey$.equals(b.value))
          		if(nodeIsSelected(graph,b.type))
      		  return true;
          }
	  }catch(Exception e){
		  Logger.getLogger(JGraphNodes.class.getName()).severe(e.toString());
	  }
	  return false;
  }
  private Sack removeStandAloneNodes(){
	  try{
			//System.out.println("JGraphNode:listNodes:entity key=" + entityKey$); 
			Entigrator entigrator=console.getEntigrator(entihome$);
			Sack graph=entigrator.getEntityAtKey(entityKey$);
			Core[] ca=graph.elementGet("node.select");
			if(ca==null)
				return graph;
			ArrayList<String>sl=new ArrayList<String>();
			for( Core c:ca){
				if(nodeIsConnected(graph,c.name)){
					//System.out.println("JGraphNode:removeStandAloneNodes:connected node="+c.name);
						continue;
				}
				sl.add(c.name);
				//System.out.println("JGraphNode:removeStandAloneNodes:remove node="+c.name);
				}
	      for(String s:sl){
	    	  graph.removeElementItem("node.select", s);
	    	  graph.removeElementItem("vertex", s);
	    	  //System.out.println("JGraphNode:removeStandAloneNodes:remove node="+s);
	      }
	      
	      entigrator.save(graph);
	      return graph;
	  }catch(Exception e){
		  Logger.getLogger(JGraphNodes.class.getName()).severe(e.toString());
		  return  null;
	  }	
			
  }
  private void deleteNodes(){
	  try{
		  String[] sa=JGraphNodes.this.listSelectedItems();
		
		  if(sa==null)
				  return;
		//  System.out.println("JGraphNodes:deleteNodes:sa="+sa.length);
			Entigrator entigrator=console.getEntigrator(entihome$);
			Sack graph=entigrator.getEntityAtKey(entityKey$);
			Sack node;
			Core[] ba;
			String nodeKey$;
			for(String s:sa){
				
				nodeKey$=Locator.getProperty(s, EntityHandler.ENTITY_KEY);
			//	System.out.println("JGraphNodes:deleteNodes:node="+nodeKey$);
				node=entigrator.getEntityAtKey(nodeKey$);
				if(node!=null){
					ba=node.elementGet("bond");
					if(ba!=null)
						for(Core b:ba)
							graph.removeElementItem("bond", b.name);
				}
				graph.removeElementItem("node", nodeKey$);
			}
			entigrator.save(graph);
			NodeHandler.rebuild(entigrator, entityKey$);
			//graph.print();
	  }catch(Exception e){
		  Logger.getLogger(JGraphNodes.class.getName()).severe(e.toString());
		  return  ;
	  }	
  }
}
