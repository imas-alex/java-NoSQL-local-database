package gdt.data.entity;
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
import java.util.Properties;
import java.util.logging.Logger;

import gdt.data.entity.facet.FieldsHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Identity;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JMainConsole;
/**
* Contains methods to process the graph entity .
* @author  Alexander Imas
* @version 1.0
* @since   2016-08-08
*/
public class GraphHandler extends FieldsHandler{
	private Logger LOGGER=Logger.getLogger(GraphHandler.class.getName());
	public static final String EXTENSION_KEY="_Tm142C8Sgti2iAKlDEcEXT2Kj1E";	
	public static final String GRAPH_UNDO="graph.undo";
	public static final String GRAPH_VIEWS="graph.views";
	public static final String GRAPH_VIEW_NAME="graph view name";
	String entihome$;
	String entityKey$;
	public final static String GRAPH="graph";
	/**
	 * Default constructor
	 */
	public GraphHandler(){
		super();
	}
	/**
	 * Check if the handler is applied to the entity  
	 *  @param entigrator entigrator instance
	 *  @param locator$ entity's locator 
	 * @return true if applied false otherwise.
	 */		
	@Override
	public boolean isApplied(Entigrator entigrator, String locator$) {
		try{
//		System.out.println("GraphHandler:isApplied:locator="+locator$);
			Properties locator=Locator.toProperties(locator$);
			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			boolean result=false;
			Sack entity=entigrator.getEntityAtKey(entityKey$);
//			System.out.println("GraphHandler:isApplied:entity="+entity.getProperty("label"));
			String graph$=entity.getProperty("graph");
			if(graph$!=null&&!Locator.LOCATOR_FALSE.equals(graph$)){
			   if(entity.getElementItem("fhandler", GraphHandler.class.getName())==null){	
					if(!entity.existsElement("fhandler"))
						entity.createElement("fhandler");
					entity.putElementItem("fhandler", new Core(null, GraphHandler.class.getName(),null));
					entigrator.save(entity);
				}
	            result=true;
			}
			return result;
		}catch(Exception e){
		LOGGER.severe(e.toString());
		return false;
		}
	}
	 /**
     * Get title of the handler.  
     * @return the title of the handler..
     */	
	public String getTitle() {
		return "Graph";
	}
	 /**
     * Get type of the  handler.  
     * @return the type of the handler..
     */	
	public String getType() {
		return "graph";
	}
	private void adaptLabel(Entigrator entigrator){
		 try{
				Sack entity=entigrator.getEntityAtKey(entityKey$);
				entigrator.ent_assignProperty(entity, "graph", entityLabel$);
		    }catch(Exception e){
		    	
		    }
	}
	/**
	 * Adapt the label of the clone.  
	 * *  @param entigrator entigrator instance
	 */	
	@Override
	public void adaptClone(Entigrator entigrator) {
	   adaptLabel(entigrator);
		
	}
	/**
	 * Adapt the label after renaming  
	 * *  @param entigrator entigrator instance
	 */	
	@Override
	public void adaptRename(Entigrator entigrator) {
		adaptLabel(entigrator);
	}

	/**
     * Get class name of the handler.  
     * @return the class name of the handler..
     */	

@Override
public String getClassName() {
	return  GraphHandler.class.getName();
}
/**
 * Get the undo  entity of the graph.  
 *  @param console main console
 *  @param locator$ action's locator 
 * @return the undo entity.
 */		
public static Sack undoGet(JMainConsole console,String locator$){
	 try{
		Properties locator=Locator.toProperties(locator$);
		String entihome$=locator.getProperty(Entigrator.ENTIHOME);
		Entigrator entigrator=console.getEntigrator(entihome$);
		String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		String entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
		if(entityLabel$==null)
			entityLabel$=entigrator.indx_getLabel(entityKey$);
		String undoLabel$=entityLabel$+".undo";
		String undoKey$=entigrator.indx_keyAtLabel(undoLabel$);
		if(undoKey$!=null)
			return entigrator.getEntityAtKey(undoKey$);
		Sack undo=entigrator.ent_new(GRAPH_UNDO, undoLabel$);
		Sack graph=entigrator.getEntityAtKey(entityKey$);
		entigrator.col_addComponent(graph, undo);
		return undo; 
	    }catch(Exception e){
	    	Logger.getLogger(GraphHandler.class.getName()).severe(e.toString());
	    }
	return null;
}
/**
 * check, if the undo  entity of the graph exists.  
 *  @param console main console
 *  @param locator$ action's locator 
 * @return true if the undo exists, false otherwise
 */	
public static boolean undoExists(JMainConsole console,String locator$){
	 try{
		Properties locator=Locator.toProperties(locator$);
		String entihome$=locator.getProperty(Entigrator.ENTIHOME);
		Entigrator entigrator=console.getEntigrator(entihome$);
		String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		String entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
		if(entityLabel$==null)
			entityLabel$=entigrator.indx_getLabel(entityKey$);
		String undoLabel$=entityLabel$+".undo";
		String undoKey$=entigrator.indx_keyAtLabel(undoLabel$);
		if(undoKey$==null)
			return false;
		else
			return true;
		 
	    }catch(Exception e){
	    	Logger.getLogger(GraphHandler.class.getName()).severe(e.toString());
	    }
	return false;
}
/**
 * check, if the undo  is possible  
 *  @param console main console
 *  @param locator$ action's locator 
 * @return true if undo possible, false otherwise
 */	
public static boolean undoCan(JMainConsole console,String locator$){
	 try{
		if(!undoExists(console,locator$)){
	
			return false;
		}
		Sack undo=undoGet(console,locator$);
		if(undo.existsElement("undo")){
			return true;
		}
	
	    }catch(Exception e){
	    	Logger.getLogger(GraphHandler.class.getName()).severe(e.toString());
	    }
	return false;
}
/**
 * Clear all undo history  
 *  @param console main console
 *  @param locator$ action's locator 
*/	
public static void undoReset(JMainConsole console,String locator$){
	 try{
		if(!undoExists(console,locator$)){
			return;
			
		}
		Sack undo=undoGet(console,locator$);
		undo.removeElement("undo");
		Properties locator=Locator.toProperties(locator$);
		String entihome$=locator.getProperty(Entigrator.ENTIHOME);
		Entigrator entigrator=console.getEntigrator(entihome$);
		entigrator.save(undo);
	    }catch(Exception e){
	    	Logger.getLogger(GraphHandler.class.getName()).severe(e.toString());
	    }
	
}
/**
 * Save current graph state in the undo stack  
 *  @param console main console
 *  @param locator$ action's locator 
*/	
public static void undoPush(JMainConsole console,String locator$){
	 try{
		Properties locator=Locator.toProperties(locator$);
		String entihome$=locator.getProperty(Entigrator.ENTIHOME);
		Entigrator entigrator=console.getEntigrator(entihome$);
		String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		Sack graph=entigrator.getEntityAtKey(entityKey$);
		String[] sa=graph.elementListNoSorted("node.select");
		if(sa==null||sa.length<1)
			return;
		int cnt=0;
		Sack undo=undoGet(console,locator$);
		if(!undo.existsElement("undo"))
			undo.createElement("undo");
		else
		     cnt=undo.elementListNoSorted("undo").length;
		String undoName$=Identity.key();
		undo.putElementItem("undo", new Core(null,String.valueOf(cnt),undoName$));
		undo.createElement(undoName$);
		for(String s:sa)
			undo.putElementItem(undoName$, new Core(null,s,null));
		entigrator.save(undo);
	    }catch(Exception e){
	    	Logger.getLogger(GraphHandler.class.getName()).severe(e.toString());
	    }
	
}
/**
 * Restore graph state from the undo stack  
 *  @param console main console
 *  @param locator$ action's locator 
*/	
public static void undoPop(JMainConsole console,String locator$){
	 try{
		if(!undoExists(console,locator$))
				return;
		Properties locator=Locator.toProperties(locator$);
		String entihome$=locator.getProperty(Entigrator.ENTIHOME);
		Entigrator entigrator=console.getEntigrator(entihome$);
		String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		Sack graph=entigrator.getEntityAtKey(entityKey$);
		Sack undo=undoGet(console,locator$);
		
		Core[] ca=undo.elementGet("undo");
		String undoName$="";
		int undoName;
		int undoMax=0;
		for(Core c:ca){
			undoName=Integer.parseInt(c.name);
			if(undoName>=undoMax){
				undoMax=undoName;
				undoName$=c.value;
			}
		}
		String[] sa=undo.elementListNoSorted(undoName$);
	    if(graph.existsElement("node.select"))
	    	graph.clearElement("node.select");
	    else
	    	graph.createElement("node.select");
		for(String s:sa)
			graph.putElementItem("node.select", new Core(null,s,null));
		undo.removeElement(undoName$);
		undo.removeElementItem("undo", String.valueOf(undoMax));
		entigrator.save(undo);
		entigrator.save(graph);
	    }catch(Exception e){
	    	Logger.getLogger(GraphHandler.class.getName()).severe(e.toString());
	    }
	
}
/**
 * Get the views  entity of the graph.  
 *  @param console main console
 *  @param locator$ action's locator 
 * @return the views entity.
 */		
public static Sack viewsGet(JMainConsole console,String locator$){
	 try{
		Properties locator=Locator.toProperties(locator$);
		String entihome$=locator.getProperty(Entigrator.ENTIHOME);
		Entigrator entigrator=console.getEntigrator(entihome$);
		String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		String entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
		if(entityLabel$==null)
			entityLabel$=entigrator.indx_getLabel(entityKey$);
		String viewsLabel$=entityLabel$+".views";
		String viewsKey$=entigrator.indx_keyAtLabel(viewsLabel$);
		if(viewsKey$!=null)
			return entigrator.getEntityAtKey(viewsKey$);
		Sack views=entigrator.ent_new(GRAPH_VIEWS, viewsLabel$);
		Sack graph=entigrator.getEntityAtKey(entityKey$);
		entigrator.col_addComponent(graph, views);
		return views; 
	    }catch(Exception e){
	    	Logger.getLogger(GraphHandler.class.getName()).severe(e.toString());
	    }
	return null;
}
/**
 * Save current graph state in the view entity.
 *  @param console main console
 *  @param locator$ action's locator 
 *   @return the key of the view entity. 
*/	
public static String viewsPutView(JMainConsole console,String locator$){
	 try{
		Properties locator=Locator.toProperties(locator$);
		String entihome$=locator.getProperty(Entigrator.ENTIHOME);
		Entigrator entigrator=console.getEntigrator(entihome$);
		String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		String viewName$=locator.getProperty(GRAPH_VIEW_NAME);
		Sack graph=entigrator.getEntityAtKey(entityKey$);
		String[] sa=graph.elementListNoSorted("node.select");
		if(sa==null||sa.length<1)
			return null;
		int cnt=0;
		Sack views=viewsGet(console,locator$);
		if(!views.existsElement("view"))
			views.createElement("view");
		else
		     cnt=views.elementListNoSorted("view").length;
		if(viewName$==null)
			viewName$=String.valueOf(cnt);
		String viewKey$=Identity.key();
		views.putElementItem("view", new Core(null,viewName$,viewKey$));
		entigrator.save(views);
		return viewKey$;
	    }catch(Exception e){
	    	Logger.getLogger(GraphHandler.class.getName()).severe(e.toString());
	    }
	return null;
}
/**
 * No operation
 */
@Override
public void completeMigration(Entigrator entigrator) {
   // System.out.println("GraphHandler:completeMigration");
	
}
@Override
public String getLocation() {
	// TODO Auto-generated method stub
	return EXTENSION_KEY;
}
}
