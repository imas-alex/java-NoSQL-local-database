package gdt.data.entity;
import java.util.ArrayList;
import java.util.Arrays;
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
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;


/**
* Contains methods to process the node facet .
* @author  Alexander Imas
* @version 1.0
* @since   2016-08-08
*/
public class NodeHandler extends FieldsHandler{
	private Logger LOGGER=Logger.getLogger(NodeHandler.class.getName());
	public static final String EXTENSION_KEY="_Tm142C8Sgti2iAKlDEcEXT2Kj1E";	
	String entihome$;
	String entityKey$;
	public final static String NODE="node";
	public final static boolean debug=true;
	/**
	 * Default constructor
	 */
	public NodeHandler(){
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
//		System.out.println("AddressHandler:isApplied:locator="+locator$);
			Properties locator=Locator.toProperties(locator$);
			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			boolean result=false;
			Sack entity=entigrator.getEntityAtKey(entityKey$);
//			System.out.println("PhoneHandler:isApplied:entity="+entity.getProperty("label"));
			String node$=entity.getProperty("node");
			if(node$!=null&&!Locator.LOCATOR_FALSE.equals(node$)){
			   if(entity.getElementItem("fhandler", NodeHandler.class.getName())==null){	
					if(!entity.existsElement("fhandler"))
						entity.createElement("fhandler");
					entity.putElementItem("fhandler", new Core(null, NodeHandler.class.getName(),null));
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
		return "Node";
	}
	/**
     * Get type of the  handler.  
     * @return the type of the handler..
     */	
	public String getType() {
		return "node";
	}
	private void adaptLabel(Entigrator entigrator){
		 try{
				Sack entity=entigrator.getEntityAtKey(entityKey$);
				entigrator.ent_assignProperty(entity, "node", entityLabel$);
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
	return  NodeHandler.class.getName();
}
/**
 * No operation
 */
@Override
public void completeMigration(Entigrator entigrator) {
    //System.out.println("NodeHandler:completeMigration");
	
}
public static String[] expandCascade(Entigrator  entigrator, String [] na){
	try{
		ArrayList<String>sl=new ArrayList<String>();
		if(na!=null)
			for(String n:na) 
		       appendList(entigrator,n,sl);
		return sl.toArray(new String[0]);
	}catch(Exception e){
		Logger.getLogger(NodeHandler.class.getName()).severe(e.toString());
	}
return null;	
}
private static void appendList(Entigrator entigrator,String node$, ArrayList<String> sl){
	//System.out.println("NodeHandler:appendList:node="+node$);
	if(sl.contains(node$))
		return;
	sl.add(node$);
	Sack node=entigrator.getEntityAtKey(node$);
	Core[] ca=node.elementGet("bond");
	if(ca!=null)
		for(Core c:ca){
			if(c.type!=null)
				//if(!sl.contains(c.type))
					appendList(entigrator,c.type,sl);
					//sl.add(c.type);
			if(c.value!=null)
				//if(!sl.contains(c.value))
					//sl.add(c.value);
					appendList(entigrator,c.value,sl);
		}
	
	}
public static void rebuild(Entigrator entigrator,String graphKey$){
	try{
		Sack graph=entigrator.getEntityAtKey(graphKey$);
		Core[] ca=graph.elementGet("node");
		graph.removeElement("bond");
		graph.createElement("bond");
		graph.removeElement("edge.entity");
		graph.createElement("edge.entity");
		graph.removeElement("edge");
		graph.createElement("edge");
		graph.removeElement("node.select");
		graph.removeElement("vertex");
		graph.removeElement("bond.select");
		Sack node;
		if(ca==null)
			return;
		Core[]ba;
		Core edge;
		for(Core c:ca){
			node=entigrator.getEntityAtKey(c.name);
			if(node==null)
				continue;
			ba=node.elementGet("bond");
			
			if(ba!=null)
				for( Core b:ba){
					if(graph.getElementItem("bond", b.name)!=null)
						continue;
					
					edge=node.getElementItem("edge", b.name);
					graph.putElementItem("bond", b);
					//graph.putElementItem("edge", new Core(null,b.name,edge.name));
					graph.putElementItem("edge.entity",new Core(null,edge.name,edge.value));
				}
			
		}
	entigrator.save(graph);	
		}catch(Exception e){
		Logger.getLogger(NodeHandler.class.getName()).severe(e.toString());
	}
}
public static String[] getRelatedNodeKeys(Entigrator entigrator, String nodeKey$){
	try{
		 Sack node=entigrator.getEntityAtKey(nodeKey$);
		 Core[] ca=node.elementGet("bond");
		 if(ca==null)
			 return null;
		 ArrayList<String>sl=new ArrayList<String>();
		 sl.add(nodeKey$);
		 for(Core c:ca){
			 if(!sl.contains(c.value))
				 sl.add(c.value);
			 if(!sl.contains(c.type))
				 sl.add(c.type);
		 }
		 return sl.toArray(new String[0]);
	}catch(Exception e){
		Logger.getLogger(NodeHandler.class.getName()).severe(e.toString());	
	}
	return null;
	}
private static void collectRelatedNodes(Entigrator entigrator, String nodeKey$,ArrayList<String>nl){
	try{
		if(nl.contains(nodeKey$))
			return;
		 nl.add(nodeKey$);
		 Sack node=entigrator.getEntityAtKey(nodeKey$);
		 Core[] ca=node.elementGet("bond");
		 if(ca==null)
			 return ;
		 for(Core c:ca){
			 if(!nl.contains(c.value))
				 collectRelatedNodes(entigrator,c.value,nl);
			 if(!nl.contains(c.type))
				 collectRelatedNodes(entigrator,c.type,nl);
		 }
	}catch(Exception e){
		Logger.getLogger(NodeHandler.class.getName()).severe(e.toString());	
	}
	}
public static String[] getExpandedNodeKeys(Entigrator entigrator, String nodeKey$,String[] scope){
	try{
		if(debug)
			 System.out.println("NodeHandler:getExpandedNodeKeys:node="+entigrator.indx_getLabel(nodeKey$));
		if(nodeKey$==null)
			return scope;
		ArrayList<String>sl=new ArrayList<String>();
		if(scope!=null){
			if(debug)
				 System.out.println("NodeHandler:getExpandedNodeKeys:scope="+scope.length);
			
			for(String s:scope){
	        	 if(!sl.contains(s))
	        	  sl.add(s);
	         }
		}
		
		 String[] ra=getRelatedNodeKeys(entigrator, nodeKey$);
		 
		 if(ra!=null){
			 if(debug)
				 System.out.println("NodeHandler:getExpandedNodeKeys:relations="+ra.length);
			
	         for(String s:ra)
	        	 if(!sl.contains(s))
	        	  sl.add(s);
		 }
		
		 if(debug)
			 System.out.println("NodeHandler:getExpandedNodeKeys:all="+sl.size());
	
		 return sl.toArray(new String[0]);
	}catch(Exception e){
		Logger.getLogger(NodeHandler.class.getName()).severe(e.toString());	
	}
	return null;
	}
public static String[] getScopeExpandedNodeKeys(Entigrator entigrator, String nodeKey$,String[] scope){
	try{
		if(debug)
			 System.out.println("NodeHandler:getExpandedScopeNodeKeys:node="+entigrator.indx_getLabel(nodeKey$));
		
		String[] ra=null;	
		
		ArrayList<String>sl=new ArrayList<String>();
		if(nodeKey$!=null){
		 ra=getRelatedNodeKeys(entigrator, nodeKey$);
		  if(ra!=null){
			 if(debug)
				 System.out.println("NodeHandler:getExpandedScopeNodeKeys:relations="+ra.length);
	         for(String s:ra)
	        	 if(!sl.contains(s))
	        	  sl.add(s);
		 }
		}
		if(scope!=null){
			if(debug)
				 System.out.println("NodeHandler:getExpandedScopeNodeKeys:scope="+scope.length);
			
			for(String s:scope){
	        	 if(!sl.contains(s))
	        	  sl.add(s);
	        	 ra=getRelatedNodeKeys(entigrator, s);
	        	 if(ra!=null)
	        		 for(String r:ra)
	    	        	 if(!sl.contains(r))
	    	        	  sl.add(r); 
	         }
		}
		 if(debug)
			 System.out.println("NodeHandler:getExpandedScopeNodeKeys:all="+sl.size());
	
		 return sl.toArray(new String[0]);
	}catch(Exception e){
		Logger.getLogger(NodeHandler.class.getName()).severe(e.toString());	
	}
	return null;
	}
public static String[] getNetwordNodeKeys(Entigrator entigrator,String nodeKey$,String[] scope){
	try{
		ArrayList<String>sl=new ArrayList<String>();
		String[]na;
		if(nodeKey$!=null){
			na=getNetwordNodeKeys( entigrator, nodeKey$);
			if(na!=null)
				  for(String n:na)
					sl.add(n);
		}
		if(scope!=null)
		for(String s:scope){
			na=getNetwordNodeKeys( entigrator, s);
			if(na!=null)
			  for(String n:na)
				sl.add(n);
		}
		return sl.toArray(new String[0]);	
	}catch(Exception e){
		Logger.getLogger(NodeHandler.class.getName()).severe(e.toString());	
	}
	return null;
}
public static String[] getNetwordNodeKeys(Entigrator entigrator,String[] scope){
	try{
		ArrayList<String>sl=new ArrayList<String>();
		String[]na;
		if(scope==null)
			return null;
		for(String s:scope){
			na=getNetwordNodeKeys( entigrator, s);
			if(na!=null)
			  for(String n:na)
				sl.add(n);
		}
		return sl.toArray(new String[0]);	
			
	}catch(Exception e){
		Logger.getLogger(NodeHandler.class.getName()).severe(e.toString());	
	}
	return null;
}
public static String[] getNetwordNodeKeys(Entigrator entigrator, String nodeKey$){
	try{
		 ArrayList <String>nl=new ArrayList<String>();
		 collectRelatedNodes(entigrator,nodeKey$,nl);
         return nl.toArray(new String[0]);
	}catch(Exception e){
		Logger.getLogger(NodeHandler.class.getName()).severe(e.toString());	
	}
	return null;
	}
public static Bond[] getScopeBonds(Entigrator entigrator, String[] scope){
	try{
		 if(scope==null)
			 return null;
		 if(debug)
			 System.out.println("NodeHandler:getScopeBondes:scope="+scope.length);
		 Sack node;
		 Core[] ca;
		 ArrayList<String>sl=new ArrayList<String>(Arrays.asList(scope));
		 ArrayList<Bond>bl=new ArrayList<Bond>();
		 ArrayList<String>kl=new ArrayList<String>();
		 Bond b;
         for(String s:scope){
	        	 node=entigrator.getEntityAtKey(s);
	        	 
	        	 if(node==null)
	        		 continue;
	        	// if(debug)
	    		//	 System.out.println("NodeHandler:getScopeBondes:node label="+node.getProperty("label")+" key="+s);
	        	 ca=node.elementGet("bond");
	        	 if(ca==null)
	        		 continue;
	        	 for(Core c:ca){
	        		 if(sl.contains(c.type)&&sl.contains(c.value))
	        		 if(!kl.contains(c.name)){
	   	        	  kl.add(c.name);
	   	        	  b=new Bond(c.value,c.type,c.name,node.getElementItemAt("edge",c.name));
	   	        	  bl.add(b);
	        		 }
	        	 }
	         }
         if(debug)
			 System.out.println("NodeHandler:getScopeBondes:bons="+bl.size());
        return bl.toArray(new Bond[0]);
	}catch(Exception e){
		Logger.getLogger(NodeHandler.class.getName()).severe(e.toString());	
	}
	return null;
}
}

