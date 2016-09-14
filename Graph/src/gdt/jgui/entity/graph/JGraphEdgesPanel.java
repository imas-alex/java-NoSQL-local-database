package gdt.jgui.entity.graph;

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
import gdt.data.entity.EdgeHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.GraphHandler;
import gdt.data.entity.facet.ExtensionHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JItemPanel;
import gdt.jgui.console.JItemsListPanel;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.console.JItemsListPanel.ItemPanelComparator;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.tool.JTextEditor;


public class JGraphEdgesPanel extends JItemsListPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String entihome$;
    private String entityKey$;
    private String entityLabel$;
    JMenu menu1;
    public JGraphEdgesPanel()
   	{
   	    super();
   	    
   	}  
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
			Entigrator entigrator=console.getEntigrator(entihome$);
			String icon$= ExtensionHandler.loadIcon(entigrator, EdgeHandler.EXTENSION_KEY,"edge.png");
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
			   edgeIcon$=entigrator.readIconFromIcons(entigrator.getEntityIcon(s));
			   if(edgeIcon$!=null)
				   emLocator$=Locator.append(emLocator$,Locator.LOCATOR_ICON,edgeIcon$);
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
private void filter1(){
	try{
		String[] sa=listSelectedItems();
		if(sa==null)
			return;
		Properties locator;
		String edgeKey$;
		ArrayList<String>el=new ArrayList<String>();
		for(String s:sa){
			
			locator=Locator.toProperties(s);
			edgeKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			el.add(edgeKey$);
			//System.out.println("JGraphEdgesPanel:filter:edge key="+edgeKey$);
		}
	}catch(Exception e){
		Logger.getLogger(JGraphEdgesPanel.class.getName()).severe(e.toString());
	}
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
		/*
		if(sa==null){
			graphEntity.createElement("node.select");
			for(String n:nl)
				graphEntity.putElementItem("node.select", new Core(null,n,null));
		}else{
			graphEntity.clearElement("node.select");
			for(String s:sa){
				if(nl.contains(s))
					graphEntity.putElementItem("node.select", new Core(null,s,null));
			}
			
		}
		*/
		entigrator.save(graphEntity);
		JGraphRenderer gr=new JGraphRenderer();
		String gr$=gr.getLocator();
		gr$=Locator.append(gr$, Entigrator.ENTIHOME, entihome$);
		gr$=Locator.append(gr$, EntityHandler.ENTITY_KEY, entityKey$);
		JConsoleHandler.execute(console,gr$);
	}catch(Exception e){
		Logger.getLogger(JGraphEdgesPanel.class.getName()).severe(e.toString());
	}
}
	@Override
	public String getTitle() {
		return "Edges";
	}

	@Override
	public String getSubtitle() {
		
		return entityLabel$;
	}

	@Override
	public String getType() {
		
		return "Graph edges";
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

}
