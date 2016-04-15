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
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Logger;


import gdt.data.entity.facet.ExtensionHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
/**
* This class contains methods to process entities
* @author  Alexander Imas
* @version 1.0
* @since   2016-03-11
*/
public class EntityHandler {
	/**
	* tag of the entity key within locator string
	*/
	 public static final String ENTITY_KEY="entity key";
	 /**
		* value of the LOCATOR_SCOPE  within locator string
	*/
	 public final static String ENTITY_SCOPE="entity scope";
	 /**
		* tag of the entity type within locator string
		*/
	 public final static String ENTITY_TYPE="entity type";
	 /**
		* tag of the entity label within locator string
		*/
	 public static final String ENTITY_LABEL="entity label";
	 /**
		* tag of the list of entities  within locator string
		*/
	 public static final String ENTITY_LIST="entity list";
	 
	 /**
		* tag of the entity action  within locator string
		*/
	 public static final String ENTITY_ACTION="entity action";
	 /**
		* tag of the entity icon  within locator string
		*/
	 public static final String ENTITY_ICON="entity icon";
	 /**
		* tag of the container key  within locator string
		*/
	 public static final String ENTITY_CONTAINER="entity container";
	 
	 /**
		* tag of the component key  within locator string
		*/
	 public static final String ENTITY_COMPONENT="entity component";
	
	/**
	 * Discover if the locator references an entity
	 * belonging to another database.  
	 *  @param entihome$ the database path
	 *  @param entityLocator$ the locator string.
	 * @return true for foreign entity false otherwise.
	 */	 
public static boolean isForeignEntity(String entihome$,String entityLocator$){
	try{
	
		Properties locator=Locator.toProperties(entityLocator$);
		if(locator.getProperty(EntityHandler.ENTITY_KEY)==null)
			return false;
		if(!entihome$.equals(locator.getProperty(Entigrator.ENTIHOME)))
			return true;
		return false;
	}catch(Exception e){
		Logger.getLogger(EntityHandler.class.getName()).severe(e.toString());
		return false;
	}
}
/**
 * Get locator for the entity  
 *  @param entigrator entigrator instance
 *  @param label$ the label of the entity.
 * @return the locator string if success ,null otherwise.
 */	 
public static String getEntityLocator( Entigrator entigrator, String label$){
	try{
	String entityKey$=entigrator.indx_keyAtLabel(label$);
	Sack entity=entigrator.getEntityAtKey(entityKey$);
	return getEntityLocator( entigrator, entity);
	}catch(Exception e){
		Logger.getLogger(EntityHandler.class.getName()).severe(e.toString());
		return null;
	}
}
/**
 * Get locator for the entity  
 *  @param entigrator entigrator instance
 *  @param entityKey$ the key of the entity.
 * @return the locator string if success ,null otherwise.
 */	 
public static String getEntityLocatorAtKey( Entigrator entigrator, String entityKey$){
	try{
	//	System.out.println("EntityHandler:getEntityLocator:at key="+entityKey$);
		Properties locator=new Properties();
	    locator.setProperty(Locator.LOCATOR_TYPE, ENTITY_TYPE);
	    locator.setProperty(Locator.LOCATOR_SCOPE, ENTITY_SCOPE);
	    String entihome$=entigrator.getEntihome();
	    locator.setProperty(Entigrator.ENTIHOME,entihome$);
	    String label$=entigrator.indx_getLabel(entityKey$);
	    locator.setProperty(Locator.LOCATOR_TITLE,label$);
	    locator.setProperty(ENTITY_KEY,entityKey$);
	    locator.setProperty(ENTITY_LABEL,label$);
	    String entityType$=entigrator.getEntityType(entityKey$);
	    if(entityType$!=null)
	    	locator.setProperty(ENTITY_TYPE,entityType$);
	    String iconFile$=entigrator.getEntityIcon(entityKey$);
	    String icon$=entigrator.readIconFromIcons(iconFile$);
	    if(icon$!=null)
	    	locator.setProperty(Locator.LOCATOR_ICON,icon$);
//		System.out.println("EntityHandler:getEntityLocator:locator="+Locator.toString(locator));
	    return Locator.toString(locator);
	}catch(Exception e){
		Logger.getLogger(EntityHandler.class.getName()).severe(e.toString());
		return null;
	}
}
/**
 * Get locator for the entity  
 *  @param entigrator entigrator instance
 *  @param entity the entity.
 * @return the locator string if success ,null otherwise.
 */	 
public static String getEntityLocator( Entigrator entigrator, Sack entity){
	//System.out.println("EntityHandler:getEntityLocator:at entity="+entity.getProperty("label"));
	Properties locator=new Properties();
    locator.setProperty(Locator.LOCATOR_TYPE, ENTITY_TYPE);
    locator.setProperty(Locator.LOCATOR_SCOPE, ENTITY_SCOPE);
    String entihome$=entigrator.getEntihome();
    locator.setProperty(Entigrator.ENTIHOME,entihome$);
    String label$=entity.getProperty("label");
    locator.setProperty(Locator.LOCATOR_TITLE,label$);
    locator.setProperty(ENTITY_KEY,entity.getKey());
    locator.setProperty(ENTITY_LABEL,label$);
    String entityType$=entity.getProperty("entity");
    if(entityType$!=null)
    	locator.setProperty(ENTITY_TYPE,entityType$);
    String icon$=entigrator.readEntityIcon(entity);
    if(icon$!=null)
    	locator.setProperty(Locator.LOCATOR_ICON,icon$);
	return Locator.toString(locator);
}
}
