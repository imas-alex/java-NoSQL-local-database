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
boolean saverIsRunning=false;
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
	boolean reload=false;
		if(entity==null)
			reload=true;
	if(!reload)
		reload=entigrator.ent_outdated(entity);
	if(reload){
		entity=Sack.parseXML(entigrator.getEntihome() + "/" + Entigrator.ENTITY_BASE + "/data/"+entityKey$);
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
 */
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

Runnable store=		new Runnable(){
	public void run(){
			try{
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
	//	    	 System.out.println("EntitiesCache:store:run:entities="+set.size());
		    Stack <String>s=new Stack<String>();
		    while (itr.hasNext()) {
		      entityKey$ = itr.next();
		      entity=entities.get(entityKey$);
	//	      System.out.println("EntitiesCache:store:entity="+entity.getProperty("label"));
		      entigrator.saveNative(entity);
		
		      try{
		      entityTime=Long.parseLong(entity.getAttributeAt(Entigrator.TIMESTAMP));
		      if(currentTime-entityTime>1000)
			    	  s.push(entityKey$);
		      }catch(Exception ee){
		    	  LOGGER.severe(":store:"+ee.toString());
		      }
		    }
		    Sack candidate;
		    while(!s.isEmpty()){
		        entityKey$=s.pop();
		        candidate=entigrator.getEntity( entityKey$);
		        if(candidate==null){
		        	entity=entities.get(entityKey$);
		        	if(entity!=null){
		        	   entigrator.saveNative(entity);
		        	}
		        }
		    	delete(entityKey$);
		    }
		    saverIsRunning=false;	
			}catch(Exception e){
				LOGGER.severe(":store:"+e.toString());
			}
			
		}
};
public void clear(){
	entities.clear();
}
}