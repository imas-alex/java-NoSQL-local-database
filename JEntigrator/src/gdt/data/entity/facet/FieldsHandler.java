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
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.FacetHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
/**
* Contains methods to process a fields entity.
* @author  Alexander Imas
* @version 1.0
* @since   2016-03-11
*/
public class FieldsHandler extends FacetHandler{
private Logger LOGGER=Logger.getLogger(FileHandler.class.getName());

public final static String FIELDS="fields";
	public FieldsHandler(){
		
	}
	/**
	 * Check if the fields handler is applied to the entity  
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
			if(entity.getProperty("fields")!=null&&
				!Locator.LOCATOR_FALSE.equals(entity.getProperty("fields"))){
				String handler$=entity.getElementItemAt("fhandler", FieldsHandler.class.getName());
				if(handler$==null){
					if(!entity.existsElement("fhandler"))
						entity.createElement("fhandler");
					entity.putElementItem("fhandler", new Core(null,FieldsHandler.class.getName(),null));
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
     * Get title of the fields handler.  
     * @return the title of the fields handler..
     */	
	@Override
	public String getTitle() {
		return "Fields";
	}
	 /**
     * Get type of the fields handler.  
     * @return the type of the fields handler..
     */	
	@Override
	public String getType() {
		return "fields";
	}
	 /**
     * Get class name of the fields handler.  
     * @return the class name of the fields handler..
     */	
	@Override
	public String getClassName() {
	 return	getClass().getName();
	}

private void adaptLabel(Entigrator entigrator){
	 try{
		Sack entity=entigrator.getEntityAtKey(entityKey$);
		entigrator.ent_assignProperty(entity, "fields", entityLabel$);
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
@Override
public void completeMigration(Entigrator entigrator) {
	// TODO Auto-generated method stub
	
}
}