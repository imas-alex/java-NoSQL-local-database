package gdt.data.store;
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

import gdt.data.grain.Core;
import gdt.data.grain.Sack;
import gdt.data.grain.Support;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Logger;
/**
* This class provides structures and methods to serve an entities
* cache. Recent entities are keeped in memory during some time in
* order to accelerate multiple sequential access.   
* @author  Alexander Imas
* @version 1.0
* @since   2016-03-11
*/
public class EntitiesCache {
volatile boolean saverIsRunning=false;
Entigrator entigrator;
Hashtable <String,Sack>entities=new Hashtable<String,Sack>();
Thread saver;
final Logger LOGGER= Logger.getLogger(EntitiesCache.class.getName());
final static boolean debug=false;
//boolean storeFinished=true; 
/**
 * Constructor
 *  @param entigrator the entigrator.
 */
public EntitiesCache(Entigrator entigrator){
	this.entigrator=entigrator;
}
/**
	 * Put entity into the cache.
	 *  @param entity the entity.
	 */
public synchronized void put(Sack entity){
	entity.putAttribute(new Core(null,Entigrator.TIMESTAMP,String.valueOf(System.currentTimeMillis())));
	entities.put(entity.getKey(), entity);
	 if(saver==null||Thread.State.TERMINATED==saver.getState())
		try{
		  saver=new Thread(store);
		  saver.start();
		}catch(Exception e){
			LOGGER.severe(":put:"+e.toString());
		}
	}
/**
 * Get entity from the cache.
 *  @param entityKey$ the entity key.
 * @return the entity or null.
 */
public synchronized Sack get(String entityKey$){
	try{
		 if(debug)
		System.out.println("EntitiesCache:get:entity="+entityKey$);
		Sack entity= (Sack)Support.getValue(entityKey$, entities);
	  
		if(entity!=null)
	    	return entity;
		
		boolean reload=false;
		if(entity==null)
			reload=true;
	if(!reload)
		reload=entigrator.ent_entIsObsolete(entity);
	if(reload){
		entity=Sack.parseXML(entigrator,entigrator.getEntihome() + "/" + Entigrator.ENTITY_BASE + "/data/"+entityKey$);
     	if(entity!=null){	
		put(entity);
     	}
	}
	return entity;
	}catch(Exception e){
		LOGGER.severe(":get:"+e.toString());
		return null;
	}
}
/**
 * Delete entity from the cache.
 *  @param entityKey$ the entity key.
 */
public synchronized void delete(String entityKey$){
	Support.removeKey( entityKey$,entities);
}
/**
 * Save entity on  disk.
 *  @param entity the entity .
 */
/**
 * Save all entities on  disk.
 
public synchronized void save(){
	try{
		if(entities==null)
			return;
		Enumeration<String> keys=entities.keys();
		if(keys==null)
			return;
		Sack entity;
	while(keys.hasMoreElements()){	
	   entity=entities.get(keys.nextElement());
	   if(entity!=null)
        	entigrator.saveNative(entity);
	}
	}catch(Exception e){
		LOGGER.severe(":save:"+e.toString());
	}
}
*/
Runnable store=		new Runnable(){
	public void run(){
		// System.out.println("EntitiesCache:store:run:0"); 	
		try{
			if(entities.isEmpty())
				return;
			while(saverIsRunning)
				Thread.sleep(1500);
			saverIsRunning=true;
			
            String entityKey$;
            Sack entity;
		    Iterator<String> itr;
		    Set<String> set;
		    long currentTime;
		    long entityTime;
		    	currentTime=System.currentTimeMillis();
		    	set =entities.keySet();
		    	itr = set.iterator();
		    	if(debug)
		    	 System.out.println("EntitiesCache:store:run:entities="+set.size());
		    ArrayList <String>s=new ArrayList<String>();
		    while (itr.hasNext()) {
		      entityKey$ = itr.next();
		      entity=entities.get(entityKey$);
	//	      System.out.println("EntitiesCache:store:entity="+entity.getProperty("label"));
		//      entigrator.replace(entity);
		
		      try{
		      entityTime=Long.parseLong(entity.getAttributeAt(Entigrator.TIMESTAMP));
		      if(currentTime-entityTime>1000)
			    	if(!s.contains(entityKey$))
			    		s.add(entityKey$);
		 		      }catch(Exception ee){
		    	  LOGGER.severe(":a:store:"+ee.toString());
		      }
		    }
		    //
		    
		    //
		    Sack candidate;
		    if(!s.isEmpty()){
		      for(String key$:s){
		        entityKey$=key$;
		        candidate=entigrator.getEntity( entityKey$);
		        if(candidate==null){
		        	entity=entities.get(entityKey$);
		        	if(entity!=null){
		        	   entigrator.ent_replace(entity);
		        	}
		        }
		        delete(entityKey$);
		    }}
		  //  System.out.println("EntitiesCache:store:run:1"); 
		    saverIsRunning=false;	
		    
		  //System.out.println("EntitiesCache:store:run:2");
		  
			}catch(Exception e){
				LOGGER.severe(":b:store:"+e.toString());
			}
			
		}
};
public void clear(){
	entities.clear();
}
}