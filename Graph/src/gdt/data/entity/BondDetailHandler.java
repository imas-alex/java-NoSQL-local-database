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
import gdt.data.entity.EntityHandler;
import gdt.data.entity.FacetHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Identity;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
import gdt.jgui.entity.edge.JBondsPanel;
/**
* Contains methods to process a bond details.
* @author  Alexander Imas
* @version 1.0
* @since   2016-08-08
*/
public class BondDetailHandler extends FacetHandler{
private Logger LOGGER=Logger.getLogger(BondDetailHandler.class.getName());
public static final String EXTENSION_KEY="_Tm142C8Sgti2iAKlDEcEXT2Kj1E";
public final static String BOND_DETAIL="bond detail";
static boolean debug=false;
	public BondDetailHandler(){
		
	}
	/**
	 * Check if the bond detail handler is applied to the entity  
	 *  @param entigrator entigrator instance
	 *  @param locator$ entity's locator 
	 * @return true if applied false otherwise.
	 */	
	@Override
	public boolean isApplied(Entigrator entigrator, String locator$) {
		try{
			//System.out.println("FieldsHandler:isApplied:locator="+locator$);
			Properties locator=Locator.toProperties(locator$);
			String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			Sack entity=entigrator.getEntityAtKey(entityKey$);
			boolean result=false;
			if(entity.getElementItemAt("fhandler", BondDetailHandler.class.getName())!=null)
				return true;
			else
				return false;
		}catch(Exception e){
		LOGGER.severe(e.toString());
	     return false;
		}
	
	}
	 /**
     * Get title of the handler.  
     * @return the title of the handler..
     */	
	@Override
	public String getTitle() {
		return "Bond detail";
	}
	 /**
     * Get type of the  handler.  
     * @return the type of the handler..
     */	
	@Override
	public String getType() {
		return "bond.detail";
	}
	 /**
     * Get class name of the handler.  
     * @return the class name of the handler..
     */	
	@Override
	public String getClassName() {
	 return	BondDetailHandler.class.getName();
	}

/**
 * Adapt the clone of the entity.  
 */	
@Override
public void adaptClone(Entigrator entigrator) {

}

/**
 * Adapt the the entity after rename.   
 */	
@Override
public void adaptRename(Entigrator entigrator) {

}
/**
 * Delete the bond detail  
 *  @param entigrator entigrator instance
 *  @param locator$ action's locator 
 * 
 */	
public static void deleteDetail(Entigrator entigrator, String locator$){
	try{
//	System.out.println("BondDetailHandler:deleteDetail:locator="+locator$);	
	Properties locator=Locator.toProperties(locator$);
	String detailKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
	Sack detail=entigrator.getEntityAtKey(detailKey$);
    Sack edge=entigrator.getEntityAtKey(locator.getProperty(JBondsPanel.EDGE_KEY));
    String bondKey$=locator.getProperty(JBondsPanel.BOND_KEY);
    if(detail!=null){
    detail.removeElementItem("edge", bondKey$);
    detail.removeElementItem("bond", bondKey$);
    }
    Core[]ca=edge.elementGet("detail");
    if(ca!=null)
    	for(Core c:ca)
    		if(c.type.equals(bondKey$)&&c.value.equals(detailKey$)){
    		
    			edge.removeElementItem("detail", c.name);
    			//break;
    		}
    entigrator.replace(edge);
    if(detail!=null)	
    	if(detail.elementGet("bond")==null){
    		detail.removeElementItem("fhandler", BondDetailHandler.class.getName());
    		detail.removeElementItem("jfacet", BondDetailHandler.class.getName());
    		entigrator.replace(detail);
    		entigrator.ent_takeOffProperty(detail, "detail");
    	}else
   entigrator.replace(detail);
	}catch(Exception e){
		Logger.getLogger(BondDetailHandler.class.getName()).severe(e.toString());
	}
}
/**
 * Check if the bond detail already attached to the entity  
 *  @param edge the edge entity
 *  @param bondKey$ bond's key
 *  @param detilKey$ detail's key 
 * @return true if already attached false otherwise.
 */	
private static boolean isDetailAlreadyAttached(Entigrator entigrator,Sack edge,String bondKey$,String detailKey$){
	try{
		if(debug)
			System.out.println("BondDetailHandler:isDetailAlreadyAttached:bond="+bondKey$+" detail="+detailKey$);
		boolean edgeModified=false;
		boolean entryFound=false;
		Core[] ca=edge.elementGet("detail");
		if(ca!=null)
			for(Core c:ca)
				if(bondKey$.equals(c.type)&&detailKey$.equals(c.value)){
				   entryFound=true; 	
				   Sack detail=entigrator.getEntityAtKey(detailKey$);
				   
				   if(detail==null){
					   edge.removeElementItem("detail", c.name);
					   edgeModified=true;
				   }else{
					   if(debug)
							System.out.println("BondDetailHandler:isDetailAlreadyAttached:try detail="+detail.getProperty("label"));
					 
					   if(detail.getElementItem("bond", c.type)!=null){
						   if(debug)
								System.out.println("BondDetailHandler:isDetailAlreadyAttached:bond found");
						   return true;
					   }
					   else{
						   if(debug)
								System.out.println("BondDetailHandler:isDetailAlreadyAttached:bond not found");
						 
						   edge.removeElementItem("detail", c.name);
						   edgeModified=true;
					   }
				   }
					
				}
		if(!entryFound)
			return false;
		if(!edgeModified)
			return true;
		entigrator.replace(edge);
	}catch(Exception e){
			
	}
	return false;
}
/**
 * Add detail   
 *  @param entigrator the entigrator
 *  @param locator$ action's locator 
 */	
public static void addDetail(Entigrator entigrator, String locator$){
	try{
		if(debug)
		System.out.println("BondDetailHandler:addDetail:locator="+locator$);	
	Properties locator=Locator.toProperties(locator$);
	String detailKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
	String edgeKey$=locator.getProperty(JBondsPanel.EDGE_KEY);
	Sack edge=entigrator.getEntityAtKey(edgeKey$);
	String bondKey$=locator.getProperty(JBondsPanel.BOND_KEY);
	 if(isDetailAlreadyAttached(entigrator,edge,bondKey$,detailKey$)){
		 if(debug)
			 System.out.println("BondDetailHandler:addDetail:already attached"); 
		 return;
	 }
	Sack detail=entigrator.getEntityAtKey(detailKey$);

    if(!detail.existsElement("bond"))
    	detail.createElement("bond");
    if(!detail.existsElement("edge"))
    	detail.createElement("edge");
    if(!edge.existsElement("detail"))
    	edge.createElement("detail");
    Core bond=edge.getElementItem("bond", bondKey$);
    detail.putElementItem("bond", bond);
    detail.putElementItem("edge", new Core(null,bondKey$,edgeKey$));
  /*
    if(!isDetailAlreadyAttached(entigrator,edge,bondKey$,detailKey$))
    	edge.putElementItem("detail", new Core(bondKey$,Identity.key(),detailKey$));
    else
    	System.out.println("BondDetailHandler:addDetail:already attached="+detailKey$);
    	*/	
    edge.putElementItem("detail", new Core(bondKey$,Identity.key(),detailKey$));
    entigrator.replace(edge);
   detail.putElementItem("fhandler",new Core(null, BondDetailHandler.class.getName(),EXTENSION_KEY));
   detail.putElementItem("jfacet",new Core(null, "gdt.data.entity.BondDetailHandler","gdt.jgui.entity.bonddetail.JBondDetailFacetOpenItem"));
   
   entigrator.replace(detail);
   entigrator.ent_assignProperty(detail, "detail", detail.getProperty("label"));
 	}catch(Exception e){
		Logger.getLogger(BondDetailHandler.class.getName()).severe(e.toString());
	}
	
	
	
}
/**
No operation here
 */	
@Override
public void completeMigration(Entigrator entigrator) {
	// TODO Auto-generated method stub
	
}
public static void refresh(Entigrator entigrator,String entityKey$){
	try{
		String[]ea=entigrator.indx_listEntities("entity", "edge");
		if(ea==null)
			return;
		Sack detail=entigrator.getEntityAtKey(entityKey$);
		detail.removeElement("bond");
		detail.removeElement("edge");
		detail.createElement("bond");
		detail.createElement("edge");
		Sack edge;
		Core[] ca;
		Core bond;
		for(String e:ea){
			edge=entigrator.getEntityAtKey(e);
			ca=edge.elementGet("detail");
			if(ca!=null)
			for(Core c:ca){
				if(entityKey$.equals(c.value)){
					bond=edge.getElementItem("bond",c.type);
					detail.putElementItem("bond",bond);
					detail.putElementItem("edge", new Core(null,c.type,e));
				}
			}
		}
		entigrator.ent_alter(detail);
	}catch(Exception e){
		Logger.getLogger(BondDetailHandler.class.getName()).severe(e.toString());	
	}
}
@Override
public String getLocation() {
	// TODO Auto-generated method stub
	return EXTENSION_KEY;
}
}