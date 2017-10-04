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
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;
import gdt.data.grain.Core;
import gdt.data.grain.Identity;
import gdt.data.grain.Sack;
import gdt.data.grain.Support;
import gdt.jgui.entity.JEntityPrimaryMenu;
/**
* This class collects methods to save entities on the disk.
* It provides quick access to  labels, icons and types of entities
* through the additional indexing structures. 
* @author  Alexander Imas
* @version 1.0
* @since   2016-03-11
*/


public class StoreAdapter {
	private static final String HEADERS="_AM7SyUTiAcrd_hDOtNegtzohEbc";
	private static final String QUICK_MAP="_0Hw7Cb9q5VrmwG6enFmb5GBKIXo";
	private static final String STORE_STATE="_h118ipt7JttV441WtL_BMFD2klA";
	private static final String LOCKED="locked";
	private final Logger LOGGER= Logger.getLogger(getClass().getName());
	Sack entity;
	Sack header;
	Sack quickMap;
	Sack qmHeader;
	Entigrator entigrator;
	int delay=100;
	String saveId$;
	boolean debug=false;

//	boolean singleMode=false;
	boolean bulkMode=false;
	/**
	    * Default constructor.
	    * @param entigrator the entigrator.
	     
	    */
	public StoreAdapter(Entigrator entigrator){
		this.entigrator=entigrator;
		qmReload();
	}
	
private void qmInsert(Sack header){
	try{
		if(header==null)
			return;
		
		if(quickMap==null){
			quickMap=new Sack();
	   		quickMap.setKey(QUICK_MAP);
		}
		String key$=header.getKey();
		//System.out.println("StoreAdapter:qmInsert:1");
		Core key=header.elementGet("key")[0];
		Core label=header.getElementItem("label", key.type);
		if(!quickMap.existsElement("key"))
			quickMap.createElement("key");
		if(!quickMap.existsElement("label"))
			quickMap.createElement("label");
		//System.out.println("StoreAdapter:qmInsert:2");
		Core[] la=null;
		try{
		    la=quickMap.elementGet("label");
		    if(la!=null)
				for(Core l:la)
					if(key$.equals(l.value)){
						   quickMap.removeElementItem("label", l.name);
						break;
					}
		    quickMap.removeElementItem("key", key$);
		}catch(Exception ee){}
		quickMap.putElementItem("label", label);
        quickMap.putElementItem("key", key );
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
}
/**
 * Rebuild all  headers of entities and the quick map.
  
 */
public void qmRebuildAll(){
	buildHeaders();
	qmBuild();
}
/**
 * Rebuild the quick map.
  
 */
public void qmBuild(){
	try{
		 if(debug)
		System.out.println("StoreAdapter:qmBuild");
		quickMap=null;
		String headersHome$=entigrator.getEntihome()+"/"+HEADERS+"/";
   		Sack header;
   		String[] sa=new File(headersHome$).list();
   		if(sa==null)
   			return;
   		quickMap=new Sack();
   		quickMap.setKey(QUICK_MAP);
   		for(String s:sa){
   			header=Sack.parseXML(entigrator,headersHome$+s);
   			if(header==null)
   				continue;
   			qmInsert(header);
   		}
   		quickMap.putAttribute(new Core(null,Entigrator.TIMESTAMP,String.valueOf(System.currentTimeMillis())));
   		//map_save();
   		qmRunReplace();
	}catch(Exception e){
  		 LOGGER.severe(e.toString());
  	}
}
Runnable QmReplace=new Runnable(){
	public void run(){
			try{
				if(debug)
				System.out.println("StoreAdapter:qmReplace");
			qmSetBusy();
			String	mapId$=Identity.key();
			long timestamp=System.currentTimeMillis();
			quickMap.putAttribute(new Core(null,Entigrator.SAVE_ID,mapId$));
			quickMap.putAttribute(new Core (null,Entigrator.LOCK_TIME,String.valueOf(timestamp)));
			quickMap.saveXML(entigrator.getEntihome()+"/"+QUICK_MAP);
			qmRelease();
			}catch(Exception e){
				LOGGER.severe(e.toString());
			}
	}
};
 Runnable EntReplace=new Runnable(){
	public void run(){
			try{
				if(debug)
				System.out.println("StoreAdapter:EntReplace");
				entReplace(entity);
			}catch(Exception e){
				LOGGER.severe(e.toString());
			}
	}
};

/**
 * Check if the quick map on the disk is newer as in the memory.
 * @return 0 if both maps have the same identifier, -1 if disk map is older
 * and 1 if the disk map is newer. 
 */
public int qmIsObsolete(){
	try{
		Sack qmHeader=Sack.parseXML(entigrator,entigrator.getEntihome()+"/"+STORE_STATE);
		if(quickMap.getAttribute(Entigrator.SAVE_ID).equals(qmHeader.getAttribute(Entigrator.SAVE_ID)))
			return 0;
        String mapTime$=quickMap.getAttributeAt(Entigrator.LOCK_TIME);
        String headerTime$=qmHeader.getAttributeAt(Entigrator.LOCK_TIME);
        long mapTime=Long.parseLong(mapTime$);
        long headerTime=Long.parseLong(headerTime$);
        if(headerTime>mapTime)
        	return -1;
        if(headerTime==mapTime)
        	return 0;
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
	return 1;
}
/**
 * Reload the quick map.
 * @return true if succeed false otherwise.
 */
public boolean qmReload(){
	try{
		 if(debug)
		System.out.println("StoreAdapter:qmReload");
		quickMap=Sack.parseXML(entigrator,entigrator.getEntihome()+"/"+QUICK_MAP);
		if(quickMap==null)
			qmBuild();
		return true;
	}catch(Exception e){
 		 LOGGER.severe(e.toString());
 		 return false; 
 	}
	}
/**
 * Check if the quick map is busy.
 *@return true if busy false otherwise.
 */
public boolean qmIsBusy(){
	try{
		Sack qmHeader=Sack.parseXML(entigrator,entigrator.getEntihome()+"/"+STORE_STATE);
		//System.out.println("StoreAdapter:qmIsBusy:"+qmHeader.getAttribute(LOCKED));
		if(qmHeader.getAttribute(LOCKED)!=null)
			return true;
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
	return false;
}
/**
 * Set 'busy' flag for the quick map..
 */
public void qmSetBusy(){
	try{

	File file=new File(entigrator.getEntihome()+"/"+STORE_STATE);
	if(file.exists())
		file.delete();
	Sack qmHeader=new Sack();
	qmHeader.setKey(STORE_STATE);
	long timestamp=System.currentTimeMillis();
    qmHeader.putAttribute(new Core (null,Entigrator.LOCK_TIME,String.valueOf(timestamp)));
    if(quickMap!=null)
       qmHeader.putAttribute(new Core(null,Entigrator.SAVE_ID,quickMap.getAttributeAt(Entigrator.SAVE_ID)));
	qmHeader.putAttribute(new Core(null,LOCKED,null));
    qmHeader.saveXML(entigrator.getEntihome() +"/"+STORE_STATE);
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
}
/**
 * Delete 'busy' flag for the quick map..
 */
public void qmRelease(){
	try{
	//System.out.println("StoreAdapter:qmRelease");
		Sack qmHeader=new Sack();
	qmHeader.setKey(STORE_STATE);
	long timestamp=System.currentTimeMillis();
    qmHeader.putAttribute(new Core (null,Entigrator.LOCK_TIME,String.valueOf(timestamp)));
	qmHeader.putAttribute(new Core(null,Entigrator.SAVE_ID,quickMap.getAttributeAt(Entigrator.SAVE_ID)));
	qmHeader.removeAttribute(LOCKED);
    qmHeader.saveXML(entigrator.getEntihome() +"/"+STORE_STATE);
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
}
/**
 * Save the quick map in the separate thread.
 */
public void qmRunReplace(){
	try{
		 if(debug)
		System.out.println("StoreAdapter:qmRunReplace");
		 if(bulkMode)
			 return;
		 Thread t=new Thread(QmReplace);
		  t.start();
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
}
/**
 * Check if the entity is busy.
 * @param entityKey$ the key of the entity
 * @return true if the entity is locked, false otherwise.
 */
public boolean entIsBusy(String entityKey$){
	try{
		
		String header$=entigrator.getEntihome()+"/"+StoreAdapter.HEADERS+"/"+entityKey$;
	    Sack header=Sack.parseXML(entigrator,header$);
	    if(header!=null){
	    	if(header.getAttribute(LOCKED)!=null)
	    		return true;
	    	else
	    		return false;
	    }else{
	    	Sack entity=ent_reloadAtKey(entityKey$);
	    	if(entity!=null){
	    	String headersHome$=entigrator.getEntihome()+"/"+HEADERS+"/";
			header=new Sack();
	        header.createElement("label");
	        header.createElement("key");
	        header.setKey(entityKey$);
	        header.setPath(headersHome$+entityKey$);
	        header.putElementItem("label", new Core(entity.getAttributeAt("icon"),entity.getProperty("label"),entityKey$));
	        header.putElementItem("key", new Core(entity.getProperty("label"),entity.getKey(),entity.getProperty("entity")));
		    header.putAttribute(new Core(null,Entigrator.TIMESTAMP,String.valueOf(System.currentTimeMillis())));
	        String entId$=entity.getAttributeAt(Entigrator.SAVE_ID);
	        if(entId$!=null)
	         header.putAttribute(new Core(null,Entigrator.SAVE_ID,entId$));
	        header.saveXML(headersHome$+entityKey$);
	    	}
	        
	    }
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
	return false;
}
/**
 * Check if the entity is obsolete.
 * @param entity the entity
 * @return true if ID of the entity is not equal
 * ID of the same entity saved on the disk, false 
 * otherwise.
 */
public boolean entIsObsolete(Sack entity){
	try{
		if(debug)
			System.out.println("StoreAdapter: entisObsolete:entity ="+entity.getKey());
		Sack header=Sack.parseXML(entigrator,entigrator.getEntihome()+"/"+HEADERS+"/"+entity.getKey());
		String entSaveId$=entity.getAttributeAt(Entigrator.SAVE_ID);
		if(debug)
			System.out.println("StoreAdapter: entIsObsolete:entity save id="+entSaveId$);
		if(entSaveId$==null)
			return true;	
		
		String headerSaveId$=header.getAttributeAt(Entigrator.SAVE_ID);
		//System.out.println("StoreAdapter: entIsObsolete:header save id="+headerSaveId$);
		if(entSaveId$.equals(headerSaveId$)){
			//System.out.println("StoreAdapter: entIsObsolete:FALSE");
			return false;
		}
		Sack origin=ent_reloadAtKey(entity.getKey());
		String orgSaveId$=origin.getAttributeAt(Entigrator.SAVE_ID);
		if(!orgSaveId$.equals(headerSaveId$)){
			entRunReplace(origin);
		}
		//System.out.println("StoreAdapter: entIsObsolete:TRUE");
	}catch(Exception e){
		LOGGER.severe(e.toString());
		return true;
	}
	return true;
}
/**
 * Save the entity in the separate thread.
 * @param entity the entity to save.
 */
public void entRunReplace(Sack entity){
	this.entity=entity;
	try{
		  header=entSetBusy(entity);
		  Thread t=new Thread(EntReplace);
		  t.start();
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
}
/**
 * Save the entity directly.
 * @param e the entity to save.
 */
public void entReplace(Sack e){
	try{
		if(e==null||entigrator==null){
			System.out.println("StoreAdapter:entReplace:cannot get entity or header ");
			return;
		}
		String oldLabel$=null;
		String oldIcon$=null;
		String headersHome$=entigrator.getEntihome()+"/"+HEADERS+"/";
		
		Sack header=Sack.parseXML(entigrator,headersHome$+e.getKey() );
	//	System.out.println("StoreAdapter:entReplace:0");
		boolean qmLock=false;
		if(header==null){
		header=new Sack();
        header.createElement("label");
        header.createElement("key");
        header.setKey(e.getKey());
        header.setPath(headersHome$+e.getKey());
		}else{
		//	System.out.println("StoreAdapter:entReplace:1");
		
			oldLabel$=header.elementGet("label")[0].value;
		
			oldIcon$=header.elementGet("label")[0].type;
			//System.out.println("StoreAdapter:entReplace:2");
		}
		
	    String newLabel$=e.getProperty("label");
	   
	    String newIcon$=e.getAttributeAt("icon");
       
	    
	    if(oldLabel$!=null&&!oldLabel$.equals(newLabel$)
	    		||oldIcon$!=null&&!oldIcon$.equals(newIcon$)){
        if(!qmIsBusy()&&!bulkMode){
	    	     qmSetBusy();
	    	     qmLock=true;
        }
        	String qmId$=Identity.key();
        	quickMap.putAttribute(new Core(null,Entigrator.TIMESTAMP,String.valueOf(System.currentTimeMillis())));
        	quickMap.putAttribute(new Core(null,Entigrator.SAVE_ID,qmId$));
        	quickMap.removeElementItem("label", e.getProperty("label"));
            quickMap.removeElementItem("key", e.getKey());
            quickMap.putElementItem("label", new Core(e.getAttributeAt("icon"),e.getProperty("label"),e.getKey()));
            quickMap.putElementItem("key", new Core(e.getProperty("label"),e.getKey(),e.getProperty("entity")));
        }
	    header.clearElement("label");
	    header.clearElement("key");
	    header.putElementItem("label", new Core(e.getAttributeAt("icon"),e.getProperty("label"),e.getKey()));
        header.putElementItem("key", new Core(e.getProperty("label"),e.getKey(),e.getProperty("entity")));
	    header.putAttribute(new Core(null,Entigrator.TIMESTAMP,String.valueOf(System.currentTimeMillis())));
        String entId$=Identity.key();
        header.putAttribute(new Core(null,Entigrator.SAVE_ID,entId$));
        header.putAttribute(new Core(null,LOCKED,null));
        header.saveXML(headersHome$+e.getKey());
        e.putAttribute(new Core(null,Entigrator.SAVE_ID,entId$));
        e.saveXML(entigrator.getEntihome() +"/"+Entigrator.ENTITY_BASE+ "/data/" + e.getKey());
        header.removeAttribute(LOCKED);
        header.saveXML(headersHome$+e.getKey());
        qmInsert(header);
        if(qmLock){
        quickMap.saveXML(entigrator.getEntihome()+"/"+QUICK_MAP);
        qmRelease();
        }
        //System.out.println("StoreAdapter:entReplace:save id="+entId$);
	}catch(Exception ee){
		LOGGER.severe(ee.toString());
	}

}
/**
 * Save the 'busy' flag for the entity.
 * @param entity the entity .
 * @return the header of the entity.
 */
public Sack entSetBusy(Sack entity){
	return entSetBusy(entity.getKey());
}
/**
 * Save the 'busy' flag for the entity.
 * @param entityKey$ the entity key .
 * @return the header of the entity.
 */
public Sack entSetBusy(String entityKey$){
	try{
		Sack header=null;
		String header$=entigrator.getEntihome()+"/"+StoreAdapter.HEADERS+"/"+entityKey$;
		header=Sack.parseXML(entigrator,header$);
	    if(header==null){
	    	File file=new File(header$);
	    	if(file.exists())
	    		file.delete();

		 header=new Sack();
         header.createElement("label");
         header.createElement("key");
         header.setKey(entity.getKey());
         header.setPath(header$);
         if(entity.getProperty("label")!=null)
             	header.putElementItem("label", new Core(entity.getAttributeAt("icon"),entity.getProperty("label"),entity.getKey()));
         else
               	header.putElementItem("label", new Core(entity.getAttributeAt("icon"),entity.getKey(),entity.getKey()));
         header.putElementItem("key", new Core(entity.getProperty("label"),entity.getKey(),entity.getProperty("entity")));
	    }
	    header.putAttribute(new Core(null,LOCKED,null));
	    header.saveXML(header$);
	    return header;
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
	return null;
}
/**
 * Delete the 'busy' flag for the entity.
 * @param entityKey$ the entity key .
 */
public void entRelease(String entityKey$){
	try{
		
		String header$=entigrator.getEntihome()+"/"+StoreAdapter.HEADERS+"/"+entityKey$;
	    Sack header=Sack.parseXML(entigrator,header$);
	    header.removeAttribute(LOCKED);
        header.saveXML(header$);
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}

}
private void buildHeaders(){
   try{
	File entitiesHome=new File(entigrator.getEntihome()+  "/" + Entigrator.ENTITY_BASE + "/data");
	String[] sa=entitiesHome.list();
   	if(sa!=null){
     Sack entity=null;
	File headerFile;
   		String headersHome$=entigrator.getEntihome()+"/"+HEADERS+"/";
   		Sack header;
   		for(String aSa:sa){
           	header=null; 
          	entity=Sack.parseXML(entigrator,entigrator.getEntihome()+  "/" + Entigrator.ENTITY_BASE + "/data/" + aSa);
           	if(entity==null){
           	  try{	
           		File entityFile= new File(entigrator.getEntihome()+  "/" + Entigrator.ENTITY_BASE + "/data/" + aSa);
           		if(entityFile.exists())
           			entityFile.delete();
           		headerFile=new File(headersHome$+aSa);
           		if(headerFile.exists())
                   			headerFile.delete();	
           	  continue;
           	}catch(Exception ee){}
           	}
            header=new Sack();
            header.createElement("label");
            header.createElement("key");
            header.setKey(entity.getKey());
            header.setPath(headersHome$+aSa);
            if(entity.getProperty("label")!=null)
                	header.putElementItem("label", new Core(entity.getAttributeAt("icon"),entity.getProperty("label"),entity.getKey()));
            else
                  	header.putElementItem("label", new Core(entity.getAttributeAt("icon"),entity.getKey(),entity.getKey()));
            header.putElementItem("key", new Core(entity.getProperty("label"),entity.getKey(),entity.getProperty("entity")));
            header.saveXML(headersHome$+aSa);
               	}
   		}
   	}catch(Exception e){
   		 LOGGER.severe(e.toString());
   	}
   }
/**
 * Delete the entity.
 * @param entity the entity .
 * @return true if succeed false otherwise.
 */

public boolean ent_delete(Sack entity){
	try{
	if(entity==null)
		return true;
	File file = new File( entigrator.getEntihome() +"/"+Entigrator.ENTITY_BASE+ "/data/" + entity.getKey());
    if(file.exists()&&file.canWrite()){
	   file.delete();
    quickMap.removeElementItem("label", entity.getProperty("label"));
    quickMap.removeElementItem("key", entity.getKey());
    File header=new File(entigrator.getEntihome()+"/"+HEADERS+"/"+entity.getKey());
    if(header.exists())
 	   header.delete();
   // store_replace();
    qmRunReplace();
    if(debug)
    System.out.println("StoreAdapter:ent_delete:FINISH");
    return true;
    }
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
	return false;
}
/**
 * Get the label of the entity.
 * @param key$ the key of the entity .
 * @return the label of the entity.
 */
public String indx_getLabel(String key$) {
	try{
		if(key$==null)
			return null;
		if(quickMap!=null){
			Core key=quickMap.getElementItem("key", key$);
			if(key!=null)
				return key.type;
		}
		String header$=entigrator.getEntihome()+"/"+StoreAdapter.HEADERS+"/"+key$;
	    if(header$!=null){
		Sack header=Sack.parseXML(entigrator,header$);
	   // String label$=header.getElementItem("key", key$).type;
		String label$=header.elementGet("key")[0].type;
		if(label$!=null){
	    Core key=quickMap.getElementItem("key", key$);
	    if(key!=null){
	    key.type=label$;
		quickMap.putElementItem("key", key);
			return label$;
	    		}
			}
	    }
	    Sack entity=Sack.parseXML(entigrator,entigrator.getEntihome()+  "/" + Entigrator.ENTITY_BASE + "/data/" + key$);
	    if(entity!=null){
	    	entReplace(entity);
	    	return entity.getProperty("label");
	    }
	}catch(Exception e){
		 LOGGER.severe(":indx_getLabel:"+e.toString());
	}
      return null;
   }
/**
 * Get the name of the icon file for the entity.
 * @param key$ the key of the entity .
 * @return the name of the icon file.
 */
public String ent_getIconAtKey(String key$) {
	try{
		//System.out.println("StroreAdapter:ent_getIconAtKey:key="+key$);
		String icon$=null;
		Core key=quickMap.getElementItem("key", key$);
		if(key!=null){
			Core label=quickMap.getElementItem("label", key.type);
			if(label!=null)
				return label.type;
		}
		Sack header=Sack.parseXML(entigrator,entigrator.getEntihome() +"/"+HEADERS+"/"+key$);
		if(header!=null){
		 key=header.getElementItem("key", key$);
		if(key!=null&&key.type!=null){
			icon$=header.getElementItem("label", key.type).type;
 			return icon$;
		}
		}
		Sack entity=Sack.parseXML(entigrator,entigrator.getEntihome() + "/" + Entigrator.ENTITY_BASE + "/data/"+key$);
             icon$=entity.getAttributeAt("icon");
            if(icon$!=null){
             	if(header!=null){
             		header.putElementItem("label",new Core(entity.getAttributeAt("icon"),entity.getProperty("label"),entity.getKey()));
                	header.putElementItem("key",new Core(entity.getProperty("label"),key$,entity.getProperty("entity")));
                	header.saveXML(entigrator.getEntihome() +"/"+HEADERS+"/"+key$);
                	qmInsert(header);
             	}
             	if(new File(entigrator.getEntihome() + "/" + Entigrator.ICONS+"/"+icon$ ).exists())
            	    return icon$;
            }

   	  }catch(Exception e){
   	     	 LOGGER.severe(":indx_getLabel:"+e.toString());
   	  }
      return Support.readHandlerIcon(entigrator, JEntityPrimaryMenu.class, "entity.png");
   }
/**
 * Get the type of the entity.
 * @param key$ the key of the entity .
 * @return the type of the entity.
 */
public String getEntityType(String key$){
	try{
		return quickMap.getElementItem("key",key$).value;	
	} catch(Exception ee){
  	 // LOGGER.info(ee.toString());
  	 return null;
    }
}
/**
 * Assign the label to the entity.
 * @param entity the entity .
 * @param label$ the new label.
 * @return the modified entity.
 */

public Sack ent_assignLabel(Sack entity,String label$){
	try{
		Core[]ca= entity.elementGet("property");
		if(ca!=null)
			for(Core aCa:ca)
				if("label".equals(aCa.type))    					
					entity.removeElementItem("property", aCa.name);
		String newLabel$=label$;
		Core old=quickMap.getElementItem("label", label$);
		String key$=entity.getKey();
		if(old!=null&&!key$.equals(old.value))
			newLabel$=label$+Identity.key().substring(0,4);
		
		entity.putElementItem("property", new Core("label",key$,newLabel$));
		entReplace(entity);
	}catch(Exception e){
    	LOGGER.severe(":ent_assignLabel:"+e.toString());
	}
	return entity;
}
/**
 * Remove the label from the index if an entity
 * with this label doesn't exists.  
 * @param label$ the suspended label.
 * @return true if deleted, false when keep. 
 */
public boolean indx_deleteWrongLabel(String label$){
	try{
		
		Core entry=quickMap.getElementItem("label", label$);
		Sack candidate=entigrator.getEntityAtKey(entry.value);
		if(candidate!=null)
			return false;
		File header=new File(entigrator.getEntihome() +"/"+HEADERS+"/"+entry.value);
		if(header!=null)
			header.delete();
		quickMap.removeElementItem("label", label$);
		quickMap.removeElementItem("key", entry.value);
		//map_save();
		qmRunReplace();
	}catch(Exception e){
    	LOGGER.severe(e.toString());
	}
	return false;
}
/**
 * Check if the quick map on the disk has the same ID
 * as the quick map in memory.
 * @return true if IDs are different, false otherwise.
 */
public boolean qmOutdated(){
	try{
		 if(debug)
		System.out.println("StoreAdapter:store_outdated:BEGIN");
		saveId$=quickMap.getAttributeAt(Entigrator.SAVE_ID);
		qmHeader=Sack.parseXML(entigrator,entigrator.getEntihome() +"/"+STORE_STATE);
		 if(saveId$.equals(qmHeader.getAttributeAt(Entigrator.SAVE_ID)))
				return false;
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
	return true;
}
/**
* Check if an entity exists.
* @param entityKey$ the of the entity
* @return true if an entity having the given key
* exists.
*/
public boolean entExistsAtKey(String entityKey$){
	if(debug)
		System.out.println("StoreAdapter:ent_existsAtKey.entity key="+entityKey$);
    try{
	if(quickMap.getElementItem("key", entityKey$)!=null)
		return true;
	String header$=entigrator.getEntihome()+"/"+StoreAdapter.HEADERS+"/"+entityKey$;
    Sack header=Sack.parseXML(entigrator,header$);
    if(header==null){
    	Sack entity=Sack.parseXML(entigrator,entigrator.getEntihome()+  "/" + Entigrator.ENTITY_BASE + "/data/" + entityKey$);
    	if(entity!=null){
    		entReplace(entity);
    		return true;
    	}
    }else
        return true;
}catch(Exception e){
	LOGGER.severe(e.toString());	
}
	return false;
}
/**
* Check if an entity exists.
* @param type$ the type of the entity
* @return true if at least one entity having the given 
* type exists.
*/
public boolean ent_existsAtType(String type$){
    if(type$==null)
    	return false;
	if(debug)
		System.out.println("StoreAdapter:ent_existsAtType.entity type="+type$);
	Core[] ca=quickMap.elementGet("key");
	if(ca!=null)
		for(Core c:ca)
			if(type$.equals(c.value))
				return true;
	String[] sa=entigrator.indx_listEntitiesAtPropertyName(type$);
	if(sa!=null&&sa.length>0)
		return true;
	return false;
}
private boolean isValidKey(String entityKey$){
	if(entityKey$==null)
		return false;
	if( entigrator.keyExistsInCache(entityKey$))
		return true;
	 File header=new File(entigrator.getEntihome()+"/"+HEADERS+"/"+entityKey$);
	 if(header.exists())
		 return true;
	 return false;
	
}
/**
* Get array of descriptors for entities.
* @param keys the array of keys for entities.
* @return the array of cores containing labels and icon file names
* for given entities.
*/
public Core[] indx_getMarks(String[] keys) {
	 try{
		 ArrayList<Core>cl=new ArrayList<Core>();
    	 String label$=null;
    	 String icon$;
    	 Core key;
    	 
       for(String aKeys:keys){
       	try{
        key=quickMap.getElementItem("key", aKeys);
        if(!isValidKey(aKeys)){
        	 if(debug)
        	System.out.println("StroreAdapter:indx_getMarks: invalid key="+aKeys);
        	continue;
        }
       	label$=key.type;
        icon$=quickMap.getElementItem("label", label$).type;
        cl.add(new Core(icon$,aKeys,label$));
       	}catch(Exception ee){
       		System.out.println("StoreAdapter:indx_getMarks:invalid key="+aKeys+" label="+label$);	
       	}
       	entigrator.clearCache();
       }
       return cl.toArray(new Core[0]);
       }catch(Exception e){
       	 LOGGER.severe(":indx_getMarks:"+e.toString());
  	   }
       return null;
    }
/**
* Reload an entity from the disk.
* @param entityKey$ the key of the entity.
* @return the entity.
*/
public Sack ent_reloadAtKey(String entityKey$){
	return Sack.parseXML(entigrator,entigrator.getEntihome() + "/" + Entigrator.ENTITY_BASE + "/data/"+entityKey$);
}
/**
* Reload an entity from the disk.
* @param label$ the label of the entity.
* @return the entity.
*/
public Sack entReloadAtLabel(String label$){
	String entityKey$=quickMap.getElementItemAt("label", label$);
	return Sack.parseXML(entigrator,entigrator.getEntihome() + "/" + Entigrator.ENTITY_BASE + "/data/"+entityKey$);
}
/**
* Get labels for entities.
* @param keys the array of entity keys.
* @return the array of labels.
*/
public String[] indx_getLabels(String[] keys) {
	ArrayList<String>sl=new ArrayList<String>();
 	 String label$;
    for(String aKeys:keys){
   	try{
    	label$=quickMap.getElementItem("key", aKeys).type;
   	 if(label$!=null)
   		 sl.add(label$);
    }catch(Exception e){
 	  LOGGER.severe(e.toString());
    }
 }
    return sl.toArray(new String[0]);
}
/**
 * Sort keys of entities by  labels. 
 *  @param keys input keys.
 * @return array of sorted keys.
 */ 
public String[] indx_sortKeysAtlabel(String[] keys) {
	ArrayList<String>sl=new ArrayList<String>();
 	 String label$;
    for(String aKeys:keys){
   	label$=quickMap.getElementItem("key", aKeys).type;
   	 if(label$!=null)
   		 sl.add(label$);
    }
   	Collections.sort(sl);
   	ArrayList<String>kl=new ArrayList<String>();
   	for(String s:sl)
   		kl.add(indx_keyAtLabel(s));
   	return kl.toArray(new String[0]);
 
}
/**
 * Get entity key 
 *  @param label$ the label of the entity.
 * @return the key of the entity.
 */ 
public String indx_keyAtLabel(String label$) {
	String key$=quickMap.getElementItemAt("label",label$);
    return key$;
}
/**
 * Get  labels for all entities. 
 * @return the array of all labels.
 */ 
public String[] indx_listAllLabels() {
    try{
         String[]sa=quickMap.elementList("label");
       Support.sortStrings(sa);
       return sa;
    }catch(Exception e){
   	 LOGGER.severe(e.toString());
   	 return null;
    }
   }
/**
 * Get  keys for all entities. 
 * @return the array of all keys.
 */ 

public String[] indx_listAllKeys() {
	if(debug)
		System.out.println("StoreAdapter:indx_listAllKeys");

	try{
       // map_update(); 
    	String[]sa=quickMap.elementListNoSorted("key");
             return sa;
    }catch(Exception e){
   	 LOGGER.severe(e.toString());
   	 return null;
    }
   }
/**
 * Get  keys for entities having given type.
 * @param entityType$ the entity type. 
 * @return the array of keys.
 */ 
public String[] indx_listEntitiesAtType(String entityType$) {
	//if(debug)
	//	System.out.println("StoreAdapter: indx_listEntitiesAtType:entity type="+entityType$);
    if(quickMap==null)
    	return null;
    
	try{
    
	   String[]sa=null;
	   try{sa=quickMap.elementList("key"); }catch(Exception ee){}
       if(sa==null)
    	   return null;
       ArrayList<String>sl=new ArrayList<String>();
    //   System.out.println("StoreAdapter: indx_listEntitiesAtType:1");
       Core[]ca=quickMap.elementGet("key");
      // System.out.println("StoreAdapter: indx_listEntitiesAtType:2");
     if(ca==null)
      		return null;
       for(Core aCa:ca){
      		if(entityType$.equals(aCa.value))
      		   sl.add(aCa.name);
      	}
      	sa=sl.toArray(new String[0]);
      //	System.out.println("StoreAdapter: indx_listEntitiesAtType:sa="+sa.length);
      	 return sa;
       
    }catch(Exception e){
   	 LOGGER.severe(e.toString());
   	 return null;
    }
   }
/**
 * Reload quick map if its ID is different
 * from the ID saved on the disk.
 */ 
public void qmRefresh(){
	if(qmOutdated())
		qmReload();
	if(debug)
		System.out.println("StoreAdapter:store_refresh:store save id="+saveId$);
}

/**
 * Set bulk mode flag
 * @param set the flag value.
 */ 
public void setBulkMode(boolean set){
	if(set){
		bulkMode=true;
		qmSetBusy();
	}else{
		bulkMode=false;
		qmRunReplace();
		
	}
}
/**
 * Remove the label from the index
 * if it doesn't belong to any entity.
 * @param label$ the label.
 */ 
public void ent_releaseLabel(String label$){
	try{
		 if(debug)
			    System.out.println("StoreAdapter:ent_releaseLabel:"+label$);
		if(label$==null)
			return;
		Core label=quickMap.getElementItem("label", label$);
		if(label==null){
			if(debug)
			    System.out.println("StoreAdapter:ent_releaseLabel:not exists:"+label$);	
			return;
		}
		Sack entity=Sack.parseXML(entigrator,entigrator.getEntihome() +"/"+Entigrator.ENTITY_BASE+ "/data/" + label.value);
		if(entity!=null){
			if(debug)
			    System.out.println("StoreAdapter:ent_releaseLabel:exists entity=:"+label.value);	
			return;
		}
		File file = new File( entigrator.getEntihome() +"/"+Entigrator.ENTITY_BASE+ "/data/" + label.value);
		if(file.exists())
			   file.delete();
		File header=new File(entigrator.getEntihome()+"/"+HEADERS+"/"+label.value);
		if(header.exists())
		 	   header.delete();
  	    quickMap.removeElementItem("label", label$);
	    quickMap.removeElementItem("key", label.value);
	    qmRunReplace();
	    if(debug)
	    System.out.println("StoreAdapter:ent_releaseLabel:FINISH");
	    
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
		
}

}

