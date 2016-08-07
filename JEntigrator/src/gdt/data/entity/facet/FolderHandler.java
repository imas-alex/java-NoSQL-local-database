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

import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;
import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.FacetHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
/**
* Contains methods to process a folder entity.
* @author  Alexander Imas
* @version 1.0
* @since   2016-03-11
*/
public class FolderHandler extends FacetHandler{
	private Logger LOGGER=Logger.getLogger(getClass().getName());
	/**
	 * Check if the folder handler is applied to the entity  
	 *  @param entigrator entigrator instance
	 *  @param locator$ entity's locator 
	 * @return true if applied false otherwise.
	 */	
	@Override
	public boolean isApplied(Entigrator entigrator, String locator$) {
		try{
//		System.out.println("FolderHandler:isApplied:locator="+locator$);
			Properties locator=Locator.toProperties(locator$);
			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			boolean result=false;
			Sack entity=entigrator.getEntityAtKey(entityKey$);
//			System.out.println("FolderHandler:isApplied:entity="+entity.getProperty("label"));
			String folder$=entity.getProperty("folder");
			if(folder$!=null&&!Locator.LOCATOR_FALSE.equals(folder$)){
//				System.out.println("FolderHandler:isApplied:2");
			    if(entity.getElementItem("fhandler", getClass().getName())==null){	
//					System.out.println("FolderHandler:isApplied:3");
					if(!entity.existsElement("fhandler"))
						entity.createElement("fhandler");
					entity.putElementItem("fhandler", new Core(null,getClass().getName(),null));
					entigrator.save(entity);
				}
				File folderHome=new File(entigrator.ent_getHome(entityKey$));
				if(folderHome.exists()&&folderHome.isFile())
		        	folderHome.delete();
		        if(!folderHome.exists())
		        	folderHome.mkdir();
		            result=true;
			}
			return result;
		}catch(Exception e){
		LOGGER.severe(e.toString());
		return false;
		}
	}
	 /**
     * Get title of the folder handler.  
     * @return the title of the folder handler..
     */	
	@Override
	public String getTitle() {
		return "Folder";
	}
	/**
     * Get type of the folder handler.  
     * @return the type of the folder handler..
     */	
	@Override
	public String getType() {
		return "folder";
	}

private void adaptLabel(Entigrator entigrator){
		 try{
				Sack entity=entigrator.getEntityAtKey(entityKey$);
				entigrator.ent_assignProperty(entity, "folder", entityLabel$);
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
	 /**
     * Get class name of the folder handler.  
     * @return the class name of the folder handler..
     */	
	@Override
	public String getClassName() {
		return getClass().getName();
	}
	@Override
	public void completeMigration(Entigrator entigrator) {
		// TODO Auto-generated method stub
		
	}

}
