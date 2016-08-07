package gdt.data.entity.facet;
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
import gdt.data.entity.EntityHandler;
import gdt.data.entity.FacetHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
/**
* Contains methods to process a webset facet.
* @author  Alexander Imas
* @version 1.0
* @since   2016-03-11
*/
public class WebsetHandler extends FacetHandler{
	private Logger LOGGER=Logger.getLogger(getClass().getName());
	/**
	 * Check if the webset handler is applied to the entity  
	 *  @param entigrator entigrator instance
	 *  @param locator$ entity's locator 
	 * @return true if applied false otherwise.
	 */	
	@Override
	public boolean isApplied(Entigrator entigrator, String locator$) {
		try{
			Properties locator=Locator.toProperties(locator$);
     		entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			boolean result=false;
			Sack entity=entigrator.getEntityAtKey(entityKey$);
	    	String webset$=entity.getProperty("webset");
				if(webset$!=null&&!Locator.LOCATOR_FALSE.equals(webset$)){
				    if(entity.getElementItem("fhandler", getClass().getName())==null){	
						if(!entity.existsElement("fhandler"))
							entity.createElement("fhandler");
							entity.putElementItem("fhandler", new Core(null,getClass().getName(),null));
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
     * Get title of the webset handler.  
     * @return the title of the webset handler..
     */	
	@Override
	public String getTitle() {
		return "Webset";
	}
	 /**
     * Get type of the webset handler.  
     * @return the type of the webset handler..
     */	
	@Override
	public String getType() {
		return "webset";
	}
	 /**
     * Get class name of the webset handler.  
     * @return the class name of the webset handler..
     */	
	@Override
	public String getClassName() {
		return getClass().getName();
	}

	private void adaptLabel(Entigrator entigrator){
		 try{
				Sack entity=entigrator.getEntityAtKey(entityKey$);
				entigrator.ent_assignProperty(entity, "webset", entity.getProperty("label"));
		    }catch(Exception e){
		    	
		    }
	}
	/**
	* Adapt the clone of the entity.  
	*/
	@Override
	public void adaptClone(Entigrator entigrator) {
		adaptLabel(entigrator);
	}
	/**
	 * Adapt the the entity after rename.   
	 */	
	@Override
	public void adaptRename(Entigrator entigrator) {
		adaptLabel(entigrator);
		
	}

}
