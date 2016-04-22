package gdt.data.entity;
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
import java.util.Properties;
import java.util.logging.Logger;

import gdt.data.grain.Locator;

import gdt.data.store.Entigrator;
/**
* This abstract class declares methods to process facet handler
* @author  Alexander Imas
* @version 1.0
* @since   2016-03-11
*/
public abstract class FacetHandler {
	/**
	* tag of the status  within method's response string
	*/
	public static final String METHOD_STATUS="method status";
	/**
	* the value of the  METHOD_STATUS indicating success.
	*/
	public static final String METHOD_STATUS_DONE="method status done";
	/**
	* the value of the  METHOD_STATUS indicating error.
	*/
	public static final String METHOD_STATUS_FAILED="method status failed";
	protected String entihome$;
	protected String entityKey$;
	protected String entityLabel$;
	  /**
     * Check if the facet handler is applied to the entity  
     *  @param entigrator entigrator instance
     *  @param locator$ entity's locator 
     * @return true if applied false otherwise.
     */	
    public abstract boolean isApplied( Entigrator entigrator,String locator$);
    /**
     * Get title of the facet handler.  
     * @return the title of the facet handler..
     */	
    public abstract String getTitle();
    /**
     * Get type of the facet handler.  
     * @return  the type of the facet handler..
     */	
    public abstract String getType();
    /**
     * Get the class name of the facet handler.  
     * @return  the class name of the facet handler..
     */	
    public abstract String getClassName();
    /**
     * Adapt the clone of the entity.  
     */	
     public abstract void adaptClone( Entigrator entigrator);
     /**
      * Adapt the the entity after rename.   
      */	
     public abstract void adaptRename( Entigrator entigrator);
     /**
      * Instantiate the facet handler.  
      *  @param locator$ the argument 
      * @return the locator string of the facet handler.
      */	 
     public  String instantiate(String locator$){
    		try{
    		//	System.out.println("FacetHandler:instantiate:locator="+locator$);
    			Properties locator=Locator.toProperties(locator$);
    			entihome$=locator.getProperty(Entigrator.ENTIHOME);
    			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
    			entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
    	        return getLocator();
    			}catch(Exception e){
    	        Logger.getLogger(getClass().getName()).severe(e.toString());
    			}
    			return null; 
     }
     /**
 	 * Build the set of arguments needed to create an instance of FacetHandler 
 	 * and pack them in the special string parameter - locator. 
 	 *  
 	 * @return The locator string.
 	 */	 
     public String getLocator() {
 		try{
    	 Properties locator=new Properties();
 		locator.setProperty(Locator.LOCATOR_TITLE, getTitle());
 		locator.setProperty(Locator.LOCATOR_TYPE, getType());
 		locator.setProperty(BaseHandler.HANDLER_CLASS, getClassName());
 		locator.setProperty(BaseHandler.HANDLER_SCOPE, BaseHandler.BASE_SCOPE);
 		if(entihome$!=null)
 			locator.setProperty(Entigrator.ENTIHOME, entihome$);
 		if(entityKey$!=null)
 			locator.setProperty(EntityHandler.ENTITY_KEY, entityKey$);
 		if(entityLabel$!=null)
 			locator.setProperty(EntityHandler.ENTITY_LABEL, entityLabel$);
 		return Locator.toString(locator);
 		}catch(Exception e){
 			Logger.getLogger(getClass().getName()).severe(e.toString());
 			return null;
 		}
 		
 	}  
}
