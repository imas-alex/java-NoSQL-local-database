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
    			break;
    		}
    entigrator.save(edge);
    if(detail!=null)	
    	if(detail.elementGet("bond")==null){
    		detail.removeElementItem("fhandler", BondDetailHandler.class.getName());
    		entigrator.save(detail);
    		entigrator.ent_takeOffProperty(detail, "detail");
    	}else
    		entigrator.save(detail);
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
private static boolean isDetailAlreadyAttached(Sack edge,String bondKey$,String detailKey$){
	try{
		
		Core[] ca=edge.elementGet("detail");
		if(ca!=null)
			for(Core c:ca)
				if(bondKey$.equals(c.type)&&detailKey$.equals(c.value))
					return true;
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
	System.out.println("BondDetailHandler:addDetail:locator="+locator$);	
	Properties locator=Locator.toProperties(locator$);
	String detailKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
	String edgeKey$=locator.getProperty(JBondsPanel.EDGE_KEY);
	String bondKey$=locator.getProperty(JBondsPanel.BOND_KEY);
	Sack detail=entigrator.getEntityAtKey(detailKey$);
    Sack edge=entigrator.getEntityAtKey(edgeKey$);
    if(!detail.existsElement("bond"))
    	detail.createElement("bond");
    if(!detail.existsElement("edge"))
    	detail.createElement("edge");
    if(!edge.existsElement("detail"))
    	edge.createElement("detail");
    Core bond=edge.getElementItem("bond", bondKey$);
    detail.putElementItem("bond", bond);
    detail.putElementItem("edge", new Core(null,bondKey$,edgeKey$));
    if(!isDetailAlreadyAttached(edge,bondKey$,detailKey$))
    	edge.putElementItem("detail", new Core(bondKey$,Identity.key(),detailKey$));
    entigrator.save(edge);
   	detail.putElementItem("fhandler",new Core(null, BondDetailHandler.class.getName(),EXTENSION_KEY));
   entigrator.save(detail);
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
}