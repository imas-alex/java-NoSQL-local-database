package gdt.data.store;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Logger;

import gdt.data.grain.Core;
import gdt.data.grain.Identity;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.grain.Support;

public class StoreAdapter {
	private static final String HEADERS="_AM7SyUTiAcrd_hDOtNegtzohEbc";
	private static final String QUICK_MAP="_0Hw7Cb9q5VrmwG6enFmb5GBKIXo";
	private static final String STORE_STATE="_h118ipt7JttV441WtL_BMFD2klA";
	private static final String MAP_TIME="map time";
	private static final String IS_LOCKED="is locked";
	private final Logger LOGGER= Logger.getLogger(getClass().getName());
	
	Sack quickMap;
	Sack headers;
	Sack storeState;
	Entigrator entigrator;
	int delay=1000;
	String saveId$;
	public StoreAdapter(Entigrator entigrator){
		this.entigrator=entigrator;
		map_load();
	}
private String  map_load(){
	try{
      quickMap=Sack.parseXML(entigrator.getEntihome()+"/"+QUICK_MAP);
      if(quickMap!=null)
    	  return quickMap.getAttributeAt(Entigrator.SAVE_ID);
      map_build();
      if(quickMap==null){
    	  buildHeaders();
    	  map_build();
      }
      storeState=Sack.parseXML(entigrator.getEntihome() +"/"+STORE_STATE);
      
      saveId$=storeState.getAttributeAt(Entigrator.SAVE_ID);
      if(saveId$!=null)
        quickMap.putAttribute(new Core(null,Entigrator.SAVE_ID,saveId$));
      map_save();
       
	}catch(Exception e){
 		 LOGGER.severe(e.toString());
 	}
	  return saveId$;
	}
private void map_insert(Sack header){
	try{
		String key$=header.getKey();
		Core key=header.getElementItem("key", key$);
		Core label=header.getElementItem("label", key.type);
		Core[] la=quickMap.elementGet("label");
		//ArrayList<String>sl=new ArrayList<String>(); 
		if(la!=null)
		for(Core l:la)
			if(key$.equals(l.value)){
				quickMap.removeElementItem("label", l.name);
				break;
			}
		quickMap.removeElementItem("key", key$);
		if(!quickMap.existsElement("key"))
			quickMap.createElement("key");
		if(!quickMap.existsElement("label"))
			quickMap.createElement("label");
		quickMap.putElementItem("label", label);
        quickMap.putElementItem("key", key );
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
}
public void map_rebuild(){
	buildHeaders();
	map_build();
}
private void map_build(){
	try{
		quickMap=null;
		String headersHome$=entigrator.getEntihome()+"/"+HEADERS+"/";
   		Sack header;
   		String[] sa=new File(headersHome$).list();
   		if(sa==null)
   			return;
   		quickMap=new Sack();
   		quickMap.setKey(QUICK_MAP);
   	//	Core label;
   	//	Core key;
   		for(String s:sa){
   			header=Sack.parseXML(headersHome$+s);
   			if(header==null)
   				continue;
   			map_insert(header);
   		}
   		quickMap.putAttribute(new Core(null,Entigrator.TIMESTAMP,String.valueOf(System.currentTimeMillis())));
   		map_save();
   		//quickMap.saveXML(entigrator.getEntihome()+"/"+QUICK_MAP);	
	}catch(Exception e){
  		 LOGGER.severe(e.toString());
  	}
}
Runnable saveMap=new Runnable(){
	public void run(){
			try{
				storeState=store_getState();
				boolean storeIsLocked=store_isLocked();
				int cnt=0;
				while(storeIsLocked){
					Thread.sleep(delay);
					storeIsLocked=store_isLocked();
					cnt++;
					if(cnt>3){
						LOGGER.severe("Timeout. Cannot save quick map");
						return;
					}
				}
				String mapId$=quickMap.getAttributeAt(Entigrator.SAVE_ID);
				saveId$=storeState.getAttributeAt(Entigrator.SAVE_ID);
				if(mapId$!=null)
						if(mapId$.equals(saveId$))
					          return;
						else
							saveId$=mapId$;
						
				else{
					mapId$=Identity.key();
					quickMap.putAttribute(new Core(null,Entigrator.SAVE_ID,mapId$));
					saveId$=mapId$;
				}	
				store_lock();
				quickMap.saveXML(entigrator.getEntihome()+"/"+QUICK_MAP);
                //store_release();
			}catch(Exception e){
				LOGGER.severe(e.toString());
			}
	}
};

private void map_save(){
	try{
		  Thread saver=new Thread(saveMap);
		  saver.start();
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
}

public boolean store_replace(){
	try{
		System.out.println("StoreAdapter:store_replace:BEGIN");
		if(store_isLocked())
			return false;
		String newKey$=Identity.key();
		System.out.println("StoreAdapter:store_replace:save id="+newKey$);
		
		storeState=Sack.parseXML(entigrator.getEntihome() +"/"+STORE_STATE);
		storeState.putAttribute(new Core(null,Entigrator.LOCK_TIME,String.valueOf(System.currentTimeMillis())));
		storeState.saveXML(entigrator.getEntihome() +"/"+STORE_STATE);
		quickMap.putAttribute(new Core(null,Entigrator.SAVE_ID,newKey$));
		long begin=System.currentTimeMillis();
        quickMap.saveXML(entigrator.getEntihome() +"/"+QUICK_MAP);
        System.out.println("StoreAdapter:store_replace:time="+String.valueOf(System.currentTimeMillis()-begin));
        storeState.putAttribute(new Core(null,Entigrator.SAVE_ID,newKey$));
        storeState.removeAttribute(Entigrator.LOCK_TIME);
		storeState.saveXML(entigrator.getEntihome()+"/"+STORE_STATE);
		
		return true;
	}catch(Exception e){
		LOGGER.severe(e.toString());
		return false;
	}
}
public void store_newId(){
	try{
System.out.println("StroreAdapter:store_newId:saveId old="+saveId$);
		if(!store_tryLocked()){
		storeState=store_getState();
		saveId$=Identity.key();
		System.out.println("StroreAdapter:store_newId:saveId new="+saveId$);
		storeState.putAttribute(new Core(null,Entigrator.SAVE_ID,saveId$));
		quickMap.putAttribute(new Core(null,Entigrator.SAVE_ID,saveId$));
		
		//System.out.println("StroreAdapter:store_newId:1");
		storeState.saveXML(entigrator.getEntihome()+"/"+STORE_STATE);
		//map_save();
		System.out.println("StroreAdapter:store_newId:2");
	}else
		System.out.println("StroreAdapter:store locked");
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
}
private Sack store_getState(){
	return Sack.parseXML(entigrator.getEntihome()+"/"+STORE_STATE);
}
private void map_update(){
	try{
		if(quickMap==null)
		    map_load();
		storeState=store_getState();
		int cnt=0;
		do{
			Thread.sleep(100);
			storeState=store_getState();
			if(storeState!=null)
				break;
		}while(cnt++<3);
		if(storeState==null){
			LOGGER.severe("cannot read state");
			return;
		}
		String stateId$=storeState.getAttributeAt(Entigrator.SAVE_ID);
		String mapId$=quickMap.getAttributeAt(Entigrator.SAVE_ID);
		if(mapId$!=null&&mapId$.equals(stateId$))
			return;
		 map_load();
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
}
private void buildHeaders(){
   try{
	File entitiesHome=new File(entigrator.getEntihome()+  "/" + Entigrator.ENTITY_BASE + "/data");
	String[] sa=entitiesHome.list();
	//   String[] sa=entigrator.indx_listEntities();
   	if(sa!=null){
     Sack entity=null;
	File headerFile;
   		String headersHome$=entigrator.getEntihome()+"/"+HEADERS+"/";
   		Sack header;
   		for(String aSa:sa){
           	header=null; 
   			
          	entity=Sack.parseXML(entigrator.getEntihome()+  "/" + Entigrator.ENTITY_BASE + "/data/" + aSa);
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
private boolean lockStore(){
	try{
		System.out.println("StoreAdapter:lockStore:begin");
		if(!isReleased())
			return false;
		storeState=store_getState();
				//Sack.parseXML(entigrator.getEntihome()+"/"+STORE_STATE);
		if(storeState==null){
			storeState=new Sack();
			storeState.setKey(STORE_STATE);
		}
		String owner$=storeState.getAttributeAt(Entigrator.LOCK_OWNER);
		String process$=storeState.getAttributeAt(Entigrator.LOCK_PROCESS);
		if(owner$==null||process$==null){
			process$=ManagementFactory.getRuntimeMXBean().getName();
			owner$=System.getProperty("user.name");
			storeState.putAttribute(new Core (null,Entigrator.LOCK_OWNER,owner$));
			storeState.putAttribute(new Core (null,Entigrator.LOCK_PROCESS,process$));
			storeState.putAttribute(new Core (null,Entigrator.LOCK_TIME,String.valueOf(System.currentTimeMillis())));
			storeState.saveXML(entigrator.getEntihome()+"/"+STORE_STATE);
			return true;
		}
		if(owner$.equals(System.getProperty("user.name"))&&process$.equals(ManagementFactory.getRuntimeMXBean().getName())){
			storeState.putAttribute(new Core (null,Entigrator.LOCK_TIME,String.valueOf(System.currentTimeMillis())));
			storeState.saveXML(entigrator.getEntihome()+"/"+STORE_STATE);
			return true;
		}
		long timestamp=Long.parseLong(storeState.getAttributeAt(Entigrator.LOCK_TIME));
		long diff=System.currentTimeMillis()-timestamp;
		if(diff>60000){
			process$=ManagementFactory.getRuntimeMXBean().getName();
			owner$=System.getProperty("user.name");
			storeState.putAttribute(new Core (null,Entigrator.LOCK_OWNER,owner$));
			storeState.putAttribute(new Core (null,Entigrator.LOCK_PROCESS,process$));
			storeState.putAttribute(new Core (null,Entigrator.LOCK_TIME,String.valueOf(System.currentTimeMillis())));
			storeState.saveXML(entigrator.getEntihome()+"/"+STORE_STATE);
			return true;
		}
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
	return false;
}
private void unlockStore(){
	try{
		storeState=store_getState();
				//Sack.parseXML(entigrator.getEntihome()+"/"+STORE_STATE);
		if(storeState==null)
			return;
		storeState.removeAttribute(Entigrator.LOCK_OWNER);
		storeState.removeAttribute(Entigrator.LOCK_PROCESS);
		storeState.removeAttribute(Entigrator.LOCK_TIME);
		storeState.saveXML(entigrator.getEntihome()+"/"+STORE_STATE);
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
}
/*
private boolean checkReleased(){
	boolean released=isReleased();
	if(released)
		return true;
	try{
	
	Thread.sleep(1000);
	if(isReleased())
		return true;
	Thread.sleep(1000);
	if(isReleased())
		return true;
	Thread.sleep(1000);
	if(isReleased())
		return true;
	
	}catch(Exception e){}
	return false;
}
*/
private boolean isReleased(){
	try{
		storeState=store_getState();
				//Sack.parseXML(entigrator.getEntihome()+"/"+STORE_STATE);
		if(storeState==null)
			return true;
		String owner$=storeState.getAttributeAt(Entigrator.LOCK_OWNER);
		String process$=storeState.getAttributeAt(Entigrator.LOCK_PROCESS);
		if(owner$==null||process$==null)
			return true;
		if(owner$.equals(System.getProperty("user.name"))&&process$.equals(ManagementFactory.getRuntimeMXBean().getName()))
			return true;
		long timestamp=Long.parseLong(storeState.getAttributeAt(Entigrator.LOCK_TIME));
		long diff=System.currentTimeMillis()-timestamp;
		if(diff>60000)
			return true;
	}catch(Exception e){
	LOGGER.severe(e.toString());
}
return false;
}
public boolean isChanged(){
	try{
		storeState=store_getState();
				//Sack.parseXML(entigrator.getEntihome()+"/"+STORE_STATE);
		if(storeState==null)
			return false;
		String owner$=storeState.getAttributeAt(Entigrator.LOCK_OWNER);
		String process$=storeState.getAttributeAt(Entigrator.LOCK_PROCESS);
		if(owner$==null||process$==null)
			return false;
		if(owner$.equals(System.getProperty("user.name"))&&process$.equals(ManagementFactory.getRuntimeMXBean().getName()))
			return false;
		else
			return true;
	}catch(Exception e){
	LOGGER.severe(e.toString());
}
return false;
}
//// Store operations
public boolean ent_delete(Sack entity){
	try{
	if(entity==null)
		return true;
	File fname = new File( entigrator.getEntihome() +"/"+Entigrator.ENTITY_BASE+ "/data/" + entity.getKey());
    if(fname!=null)
	   fname.delete();
    quickMap.removeElementItem("label", entity.getProperty("label"));
    quickMap.removeElementItem("key", entity.getKey());
    File header=new File(entigrator.getEntihome()+"/"+HEADERS+"/"+entity.getKey());
    if(header.exists())
 	   header.delete();
    map_save();
   // store_newId();
    store_replace();
    return true;
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
	return false;
}
/*
public boolean deleteEntity(Sack entity) {
   System.out.println("StoreAdapter:deleteEntity");
	if (entity == null) {
    	return false;
    }
	System.out.println("StoreAdapter:deleteEntity.check released="+checkReleased());
    if(!checkReleased()){
    	return false;
    }
    lockStore();
   
    String key$=entity.getKey();
    String[] ra = entigrator.ent_listContainers(entity);
    try {
        if (ra != null && ra.length > 0) {
            Sack container;
            for (String aRa : ra) {
                container = entigrator.getMember("entity.base", aRa);
                if (container != null)
                    entigrator.col_breakRelation(container, entity);
            }
        }
    } catch (Exception e) {
    	LOGGER.severe(e.toString());
    }
    ra = entigrator.ent_listComponents(entity);
    try {
        if (ra != null && ra.length > 0) {
            Sack component;
            for (String aRa : ra) {
                component = entigrator.getMember("entity.base", aRa);
                if (component != null) {
                    entigrator.col_breakRelation(entity, component);
                }
            }
        }
    } catch (Exception e) {
    	LOGGER.severe(e.toString());
    }
    if (entity.getAttributeAt(Entigrator.PRESERVED) != null){
    	LOGGER.severe(":cannot delete preserverd entity");
    	unlockStore();
    	return false;
    }
    String label$ = null;
    try {
        String[] sa = entity.elementList("property");
        label$ = entity.getProperty("label");
        if (sa != null) {
            Sack map;
            for (String aSa : sa) {
                map = entigrator.getMember("property.map.base", aSa);
                if (map == null)
                    continue;
                map.removeElementItem("entity", key$);
                entigrator.save(map);
            }
        }

        if (label$ != null)
            entigrator.prp_deletePropertyValue(entity.getProperty("entity"), label$);
   
    } catch (Exception e) {
    	//LOGGER.info(":deleteEntity:"+e.toString());
    }

    try {
    	entigrator.entitiesCache.delete(key$);
    	File fname = new File( entigrator.getEntihome() +"/"+Entigrator.ENTITY_BASE+ "/data/" + key$);
        fname.delete();
    } catch (Exception ee) {
       // LOGGER.info(":deleteEntity:"+ee.toString());
    }
    try {
        String record$ = entigrator.getEntihome() + "/" + key$;
        FileExpert.delete(record$);
        if (label$ != null)
            entigrator.prp_deletePropertyValue("label", label$);
    } catch (Exception ee) {
    //	LOGGER.info(":deleteEntity:"+ee.toString());
    }
   quickMap.removeElementItem("label", label$);
   quickMap.removeElementItem("key", key$);
   
   try{
   File header=new File(entigrator.getEntihome()+"/"+HEADERS+"/"+key$);
   if(header.exists())
	   header.delete();
   }catch (Exception ee) {
	 //  LOGGER.info(":deleteEntity:"+ee.toString());
   }
   unlockStore();
   return true;
}
*/
public Sack ent_assignIcon(Sack entity, String icon$) {
	try{
		if(ent_tryLocked(entity.getKey()))
			return addlock_message(entity);
		entity.putAttribute(new Core(null,"icon",icon$));
		entity.putAttribute(new Core(null,Entigrator.SAVE_ID,Identity.key()));
		
		ent_save(entigrator,entity);
	    store_replace(); 
		//store_newId();
	}catch(Exception e){
    	LOGGER.severe(":ent_assignIcon:"+e.toString());
	}
	
	return entity;
}
public String indx_getLabel(String key$) {
	try{
		//System.out.println("StroreAdapter:indx_getLabel:key="+key$);
		if(key$==null)
			return null;
		String label$=null;
		Sack header=Sack.parseXML(entigrator.getEntihome() +"/"+HEADERS+"/"+key$);
		if(header!=null){
		Core key=header.getElementItem("key", key$);
		if(key!=null&&key.type!=null){
			label$=key.type;
 			return label$;
		}
		}
		Sack entity=Sack.parseXML(entigrator.getEntihome() + "/" + Entigrator.ENTITY_BASE + "/data/"+key$);
             label$=entity.getProperty("label");
            if(label$!=null){
              //	quickMap.putElementItem("label",new Core(entity.getAttributeAt("icon"),label$,entity.getKey()));
            //	quickMap.putElementItem("label",new Core(entity.getProperty("entity"),key$,label$));
             	if(header!=null){
             		header.putElementItem("label",new Core(entity.getAttributeAt("icon"),label$,entity.getKey()));
                	header.putElementItem("key",new Core(label$,key$,entity.getProperty("entity")));
                	header.saveXML(entigrator.getEntihome() +"/"+HEADERS+"/"+key$);
                	map_insert(header);
             	}
            	return label$;
            }

   	  }catch(Exception e){
   	     	 LOGGER.severe(":indx_getLabel:"+e.toString());
   	  }
      return null;
   }
public String ent_getIconAtKey(String key$) {
	try{
		//System.out.println("StroreAdapter:ent_getIconAtKey:key="+key$);
		String icon$=null;
		Sack header=Sack.parseXML(entigrator.getEntihome() +"/"+HEADERS+"/"+key$);
		if(header!=null){
		Core key=header.getElementItem("key", key$);
		if(key!=null&&key.type!=null){
			icon$=header.getElementItem("label", key.type).type;
 			return icon$;
		}
		}
		Sack entity=Sack.parseXML(entigrator.getEntihome() + "/" + Entigrator.ENTITY_BASE + "/data/"+key$);
             icon$=entity.getAttributeAt("icon");
            if(icon$!=null){
              	//quickMap.putElementItem("label",new Core(entity.getAttributeAt("icon"),label$,entity.getKey()));
            	//quickMap.putElementItem("label",new Core(entity.getProperty("entity"),key$,label$));
             	if(header!=null){
             		header.putElementItem("label",new Core(entity.getAttributeAt("icon"),entity.getProperty("label"),entity.getKey()));
                	header.putElementItem("key",new Core(entity.getProperty("label"),key$,entity.getProperty("entity")));
                	header.saveXML(entigrator.getEntihome() +"/"+HEADERS+"/"+key$);
                	map_insert(header);
             	}
            	return icon$;
            }

   	  }catch(Exception e){
   	     	 LOGGER.severe(":indx_getLabel:"+e.toString());
   	  }
      return null;
   }
public String getEntityIcon(String key$){
	try{
		Sack header=Sack.parseXML(entigrator.getEntihome() +"/"+HEADERS+"/"+key$);
		Core key=null;
		if(header!=null)
				key=header.getElementItem("key", key$);
		if(key!=null&&key.type!=null){
			Core label=header.getElementItem("label", key.value);
			quickMap.putElementItem("label", label);
 			return label.type;
		}
		Sack entity=Sack.parseXML(entigrator.getEntihome() + "/" + Entigrator.ENTITY_BASE + "/data/"+key$);
        String label$=entity.getProperty("label");
        String iconFile$=entity.getAttributeAt("icon");
            if(label$!=null){
              	quickMap.putElementItem("label",new Core(entity.getAttributeAt("icon"),label$,entity.getKey()));
             	if(header!=null){
             		header.putElementItem("label",new Core(entity.getAttributeAt("icon"),label$,entity.getKey()));
                	header.saveXML(entigrator.getEntihome() +"/"+HEADERS+"/"+key$);
             	}
            	
            }
            return iconFile$;
		//String label$=quickMap.getElementItem("key", key$).type;
		//return quickMap.getElementItem("label",label$).type;
            
	} catch(Exception ee){
  	//  LOGGER.info(ee.toString());
  	 
    }
	return null;
}
public String getEntityType(String key$){
	try{
		return quickMap.getElementItem("key",key$).value;	
	} catch(Exception ee){
  	 // LOGGER.info(ee.toString());
  	 return null;
    }
}

public Sack ent_assignLabel(Sack entity,String label$){
	try{
		if(ent_tryLocked(entity.getKey()))
			return addlock_message(entity);
		Core[]ca= entity.elementGet("property");
		if(ca!=null)
			for(Core aCa:ca)
				if("label".equals(aCa.type))    					
					entity.removeElementItem("property", aCa.name);
		String newLabel$=label$;
		Core old=quickMap.getElementItem("label", label$);
		String key$=entity.getKey();
		//String icon$=entity.getAttributeAt("icon");
		if(old!=null&&!key$.equals(old.value))
			newLabel$=label$+Identity.key().substring(0,4);
		
		entity.putElementItem("property", new Core("label",key$,newLabel$));
		//entity.putAttribute(new Core(null,Entigrator.SAVE_ID,Identity.key()));
		ent_save(entigrator,entity);
	    store_replace();
	}catch(Exception e){
    	LOGGER.severe(":ent_assignIcon:"+e.toString());
	}
	return entity;
}
/*
public Sack ent_assignLabelOld(Sack entity, String label$) {
	try{
		if(ent_tryLocked(entity.getKey()))
			return addlock_message(entity);
		Core[]ca= entity.elementGet("property");
		if(ca!=null)
			for(Core aCa:ca)
				if("label".equals(aCa.type))    					
					entity.removeElementItem("property", aCa.name);
		String newLabel$=label$;
		Core old=quickMap.getElementItem("label", label$);
		String key$=entity.getKey();
		//String icon$=entity.getAttributeAt("icon");
		if(old!=null&&!key$.equals(old.value))
			newLabel$=label$+Identity.key().substring(0,4);
		
		entity.putElementItem("property", new Core("label",key$,newLabel$));
		//saveNative(entity);
		entity.saveXML(entigrator.getEntihome() + "/" + Entigrator.ENTITY_BASE + "/data/"+key$);
		
		//Sack header=Sack.parseXML(entigrator.getEntihome() +"/"+HEADERS+"/"+entity.getKey());
		Sack header=new Sack();
		header.setKey(key$);
        header.createElement("label");
        header.createElement("key");
        header.setKey(entity.getKey());
        header.setPath(entigrator.getEntihome() + "/" + HEADERS+"/"+key$);
        if(entity.getProperty("label")!=null)
        	header.putElementItem("label", new Core(entity.getAttributeAt("icon"),entity.getProperty("label"),key$));
        else
        	header.putElementItem("label", new Core(entity.getAttributeAt("icon"),key$,entity.getKey()));
        header.putElementItem("key", new Core(entity.getProperty("label"),entity.getKey(),entity.getProperty("entity")));
        header.putAttribute(new Core(null,Entigrator.TIMESTAMP,String.valueOf(System.currentTimeMillis())));
        header.saveXML(entigrator.getEntihome() + "/" + HEADERS+"/"+key$);
        map_update();
        map_insert(header);
        quickMap.putAttribute(new Core(null,Entigrator.SAVE_ID,Identity.key()));
        map_save();
	}catch(Exception e){
		LOGGER.severe(":ent_assignLabel:"+e.toString());
	}
	ent_release(entity.getKey());
   return entity;
}
*/
public boolean ent_lock(String entityKey$){
	try{
		System.out.println("StoreAdapter:ent_lock:BEGIN");
		//if(!lock_ignore(entity))
		//	return false;
		Sack header=Sack.parseXML(entigrator.getEntihome() +"/"+HEADERS+"/"+entityKey$);
		String owner$=System.getProperty("user.name");
		String process$=ManagementFactory.getRuntimeMXBean().getName();
		long timestamp=System.currentTimeMillis();
		header.putAttribute(new Core (null,Entigrator.LOCK_OWNER,owner$));
		header.putAttribute(new Core (null,Entigrator.LOCK_PROCESS,process$));
		header.putAttribute(new Core (null,Entigrator.LOCK_TIME,String.valueOf(timestamp)));
		header.putAttribute(new Core (null,Entigrator.TIMESTAMP,String.valueOf(timestamp)));
		header.saveXML(entigrator.getEntihome() +"/"+HEADERS+"/"+entityKey$);
		return true;
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
	return false;
}
public boolean ent_release(String entityKey$){
	try{
		System.out.println("StoreAdapter:ent_release:BEGIN");
		Sack header=Sack.parseXML(entigrator.getEntihome() +"/"+HEADERS+"/"+entityKey$);
		header.removeAttribute(Entigrator.LOCK_OWNER);
		header.removeAttribute(Entigrator.LOCK_PROCESS);
		header.removeAttribute(Entigrator.LOCK_TIME);
		header.putAttribute(new Core(null,Entigrator.TIMESTAMP,String.valueOf(System.currentTimeMillis())));
		header.saveXML(entigrator.getEntihome() +"/"+HEADERS+"/"+entityKey$);
		return true;
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
	return false;
}
public boolean ent_save(Entigrator entigrator,Sack entity){
	try{
		if(!ent_tryLocked(entity.getKey())){
			
			Sack header=null;
			//if(ent_existsAtKey(entity.getKey()))
			//	ent_lock(entity.getKey());
				String header$=entigrator.getEntihome()+"/"+HEADERS+"/"+entity.getKey();
            File headerFile=new File(header$);
            if(headerFile.exists())
            		header=Sack.parseXML(header$);
            if(header==null){
            	header=new Sack();
                header.createElement("label");
                header.createElement("key");
                header.setKey(entity.getKey());
                header.setPath(header$);
            }
            header.putElementItem("label", new Core(entity.getAttributeAt("icon"),entity.getProperty("label"),entity.getKey()));
            header.putElementItem("key", new Core(entity.getProperty("label"),entity.getKey(),entity.getProperty("entity")));
            header.putAttribute(new Core(null,Entigrator.TIMESTAMP,String.valueOf(System.currentTimeMillis())));
            String saveId$=entity.getAttributeAt(Entigrator.SAVE_ID);
            if(saveId$!=null)
            	header.putAttribute(new Core(null,Entigrator.SAVE_ID,saveId$));
            header.saveXML(header$);
            entity.saveXML(entigrator.getEntihome() + "/" + Entigrator.ENTITY_BASE + "/data/"+entity.getKey());
            map_update();
            map_insert(header);
            map_save();
            //entity.putAttribute(new Core(null,Entigrator.SAVE_ID,saveId$));
//            entity.saveXML(entigrator.getEntihome() + "/" + Entigrator.ENTITY_BASE + "/data/"+entity.getKey());
           // ent_release(entity.getKey());
            return true;
		}
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
	return false;
}
public boolean ent_tryLocked(String entityKey$){
	boolean isLocked;
	isLocked=ent_isLocked(entityKey$);
	if(isLocked){
		try{Thread.sleep(delay);}catch(Exception ee){};
		isLocked=ent_isLocked(entityKey$);
	}else
		return isLocked;
	if(isLocked){
		try{Thread.sleep(delay);}catch(Exception ee){};
		isLocked=ent_isLocked(entityKey$);
	}else
		return isLocked;
	if(isLocked){
		try{Thread.sleep(delay);}catch(Exception ee){};
		isLocked=ent_isLocked(entityKey$);
	}
	return isLocked;
}
public String  ent_lockInfo(String entityKey$){
	
		//System.out.println("StoreAdapter:ent_lockInfo:BEGIN");
		Sack header=Sack.parseXML(entigrator.getEntihome() +"/"+HEADERS+"/"+entityKey$);
		if(header==null)
			return null;
		String owner$=header.getAttributeAt(Entigrator.LOCK_OWNER);
		String process$=header.getAttributeAt(Entigrator.LOCK_PROCESS);
		if(owner$!=null&&process$!=null)
			 return ": locked by "+owner$+":"+process$;
		else
			return null;
		
}
public boolean ent_isLocked(String entityKey$){
	try{
		boolean free=true;
		File header=new File(entigrator.getEntihome() +"/"+HEADERS+"/"+entityKey$);
		File entity=new File(entigrator.getEntihome() + "/" + Entigrator.ENTITY_BASE + "/data/"+entityKey$);
		if(header.exists()&&!header.canWrite())
			free=false;
		if(entity.exists()&&!entity.canWrite())
			free=false;
        return !free; 
	}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
	}
	return false;
}
public boolean ent_isLockedOld(String entityKey$){
	try{
		boolean free=false;
		System.out.println("StoreAdapter:ent_isLocked:BEGIN");
		Sack header=Sack.parseXML(entigrator.getEntihome() +"/"+HEADERS+"/"+entityKey$);
		String owner$=header.getAttributeAt(Entigrator.LOCK_OWNER);
		String process$=header.getAttributeAt(Entigrator.LOCK_PROCESS);
		if(owner$==null||process$==null)
		    free=true;
		if(!free)
				if(owner$.equals(System.getProperty("user.name")))
					if(process$.equals(ManagementFactory.getRuntimeMXBean().getName()))
						free=true;
		if(!free){
			long timestamp=Long.parseLong(header.getAttributeAt(Entigrator.LOCK_TIME));
			long diff=System.currentTimeMillis()-timestamp;
			if(!free&&diff>60000)
				free=true;
		}
		 if(free){
			header.removeAttribute(Entigrator.LOCK_OWNER);
			header.removeAttribute(Entigrator.LOCK_PROCESS);
			header.removeAttribute(Entigrator.LOCK_TIME);
			header.putAttribute(new Core (null,Entigrator.TIMESTAMP,String.valueOf(System.currentTimeMillis())));
			header.saveXML(entigrator.getEntihome() +"/"+HEADERS+"/"+entityKey$);
			return false;
		}
		return true;
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
	return false;
}
public String  store_lockInfo(){
	
	System.out.println("StoreAdapter:store_lockInfo:BEGIN");
	storeState=Sack.parseXML(entigrator.getEntihome() +"/"+STORE_STATE);
	if(storeState==null)
		return null;
	String owner$=storeState.getAttributeAt(Entigrator.LOCK_OWNER);
	String process$=storeState.getAttributeAt(Entigrator.LOCK_PROCESS);
	if(owner$!=null&&process$!=null)
		 return ": database locked by "+owner$+":"+process$;
	else
		return null;
	
}
public String  ent_savedId(String entityKey$){
	System.out.println("StoreAdapter:ent_saveId:BEGIN");
	Sack header=Sack.parseXML(entigrator.getEntihome() +"/"+HEADERS+"/"+entityKey$);
	if(header==null)
		return null;
	return header.getAttributeAt(Entigrator.SAVE_ID);
		
}
public String  store_saveId(){
	storeState=Sack.parseXML(entigrator.getEntihome() +"/"+STORE_STATE);
	if(storeState==null){
		storeState=new Sack();
		storeState.setKey(STORE_STATE);
		//String owner$=storeState.getAttributeAt(Entigrator.LOCK_OWNER);
		storeState.putAttribute(new Core (null,Entigrator.TIMESTAMP,String.valueOf(System.currentTimeMillis())));
		//storeState.saveXML(entigrator.getEntihome()+"/"+STORE_STATE);
		//String saveId$=quickMap.getAttributeAt(Entigrator.SAVE_ID);
	/*
		if(saveId$==null){
			saveId$=Identity.key();
			quickMap.putAttribute(new Core(null,Entigrator.SAVE_ID,saveId$));
			map_save();
		}
		*/
		
	}
	String saveId$=quickMap.getAttributeAt(Entigrator.SAVE_ID);
	storeState.putAttribute(new Core(null,Entigrator.SAVE_ID,saveId$));
	storeState.saveXML(entigrator.getEntihome()+"/"+STORE_STATE);
	System.out.println("StoreAdapter:store_saveId="+storeState.getAttributeAt(Entigrator.SAVE_ID));
	return saveId$;
}
/*
public boolean store_lock(){
	return store_lock(null);
}
*/
public boolean store_lock(){
	try{
		System.out.println("StoreAdapter:store_lock:save id="+saveId$);
		File state=new File(entigrator.getEntihome() +"/"+STORE_STATE);
		if(state.exists()&&!state.canWrite())
			return true;
		if(!state.exists()){
			storeState=new Sack();
			storeState.setKey(STORE_STATE);
			long timestamp=System.currentTimeMillis();
			storeState.putAttribute(new Core (null,Entigrator.LOCK_TIME,String.valueOf(timestamp)));
			if(saveId$==null){
				saveId$=Identity.key();
				quickMap.putAttribute(new Core (null,Entigrator.SAVE_ID,saveId$));
				map_save();
			}
		storeState.putAttribute(new Core (null,Entigrator.SAVE_ID,saveId$));
		storeState.saveXML(entigrator.getEntihome() +"/"+STORE_STATE);
		}
		return false;
	
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
	return false;
}
public boolean store_release(){
	try{
		System.out.println("StoreAdapter:store_release:BEGIN");
		storeState=Sack.parseXML(entigrator.getEntihome() +"/"+STORE_STATE);
		
		if(storeState==null)
			return true;
		storeState.removeAttribute(Entigrator.LOCK_OWNER);
		storeState.removeAttribute(Entigrator.LOCK_PROCESS);
		storeState.removeAttribute(Entigrator.LOCK_TIME);
		storeState.saveXML(entigrator.getEntihome() +"/"+STORE_STATE);
		return true;
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
	return false;
}
public String store_reload(){
	try{
		System.out.println("StoreAdapter:store_reload:BEGIN");
		quickMap=Sack.parseXML(entigrator.getEntihome() +"/"+STORE_STATE);
		return quickMap.getAttributeAt(Entigrator.SAVE_ID);
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
	return null;
}
public boolean store_outdated(String saveId$){
	try{
		System.out.println("StoreAdapter:store_outdated:BEGIN");
		storeState=Sack.parseXML(entigrator.getEntihome() +"/"+STORE_STATE);
		if(saveId$.equals(storeState.getAttributeAt(Entigrator.SAVE_ID)))
				return false;
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
	return true;
}
public boolean store_tryLocked(){
	boolean isLocked;
	isLocked=store_isLocked();
	if(isLocked){
		try{Thread.sleep(delay);}catch(Exception ee){};
		isLocked=store_isLocked();
	}else
		return isLocked;
	if(isLocked){
		try{Thread.sleep(delay);}catch(Exception ee){};
		isLocked=store_isLocked();
	}else
		return isLocked;
	if(isLocked){
		try{Thread.sleep(delay);}catch(Exception ee){};
		isLocked=store_isLocked();
	}
	return isLocked;
}
public boolean store_isLocked(){
	try{
		File header=new File(entigrator.getEntihome() +"/"+STORE_STATE);
		if(!header.exists())
			return false;
		storeState=Sack.parseXML(entigrator.getEntihome() +"/"+STORE_STATE);
		if(storeState==null){
			Thread.sleep(1000);
			storeState=Sack.parseXML(entigrator.getEntihome() +"/"+STORE_STATE);
			if(storeState==null){
				Thread.sleep(1000);
				storeState=Sack.parseXML(entigrator.getEntihome() +"/"+STORE_STATE);
				if(storeState==null){
					Thread.sleep(1000);
					storeState=Sack.parseXML(entigrator.getEntihome() +"/"+STORE_STATE);
					if(storeState==null)
						return true;
				}
			}
		}
		if(storeState.getAttribute(Entigrator.LOCK_TIME)!=null)
	       	  return true;
	    else
	      	  return false;
	}catch(Exception e){
		LOGGER.severe(e.toString());
		
	}
	return false;
}
public boolean store_isLockedOld(){
	try{
		boolean free=false;
		//System.out.println("StoreAdapter:store_isLocked:BEGIN");
		storeState=Sack.parseXML(entigrator.getEntihome() +"/"+STORE_STATE);
		if(storeState==null)
			return false;
		String oldId$=storeState.getAttributeAt(Entigrator.SAVE_ID);
		if(oldId$!=null&&oldId$.equals(saveId$))
			return false;
		long timestamp=Long.parseLong(storeState.getAttributeAt(Entigrator.LOCK_TIME));
		long diff=System.currentTimeMillis()-timestamp;
		System.out.println("StoreAdapter:store_isLocked:diff="+diff);
		if(!free&&diff>60000){
			if(saveId$==null)
				saveId$=Identity.key();
			storeState.removeAttribute(Entigrator.SAVE_ID);
			store_release();
			return false;
		}
			
		
		/*
		String owner$=storeState.getAttributeAt(Entigrator.LOCK_OWNER);
		String process$=storeState.getAttributeAt(Entigrator.LOCK_PROCESS);
		
		if(owner$==null||process$==null)
		    free=true;
		if(!free)
				if(owner$.equals(System.getProperty("user.name")))
					if(process$.equals(ManagementFactory.getRuntimeMXBean().getName()))
						free=true;
		if(!free){
			long timestamp=Long.parseLong(state.getAttributeAt(Entigrator.LOCK_TIME));
			long diff=System.currentTimeMillis()-timestamp;
			System.out.println("StoreAdapter:store_isLocked:diff="+diff);
			if(!free&&diff>60000)
				free=true;
		}
		 if(free){
			state.removeAttribute(Entigrator.LOCK_OWNER);
			state.removeAttribute(Entigrator.LOCK_PROCESS);
		    state.removeAttribute(Entigrator.LOCK_TIME);
			state.saveXML(entigrator.getEntihome() +"/"+STORE_STATE);
			return false;
		}
		*/
		return true;
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
	return false;
}
private Sack addlock_message(Sack entity){
	String process$=entity.getAttributeAt(Entigrator.LOCK_PROCESS);
	String owner$=entity.getAttributeAt(Entigrator.LOCK_OWNER);
	String message$=": locked by "+owner$+":"+process$;
	entity.putAttribute(new Core(null,Entigrator.LOCK_MESSAGE,message$));
	return entity;
}
/*
public boolean store_isSelfLocked(){
	try{
		System.out.println("StoreAdapter:store_isLocked:BEGIN");
		storeState=Sack.parseXML(entigrator.getEntihome() +"/"+STORE_STATE);
		if(storeState==null)
			return false;
		
		String owner$=state.getAttributeAt(Entigrator.LOCK_OWNER);
		String process$=state.getAttributeAt(Entigrator.LOCK_PROCESS);
		if((System.getProperty("user.name").equals(owner$))
				&&(ManagementFactory.getRuntimeMXBean().getName().equals(process$)))
		 return true;
		
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
  return false;
}
*/
public boolean ent_existsAtKey(String entityKey$){
	map_update();
	if(quickMap.getElementItem("key", entityKey$)!=null)
		return true;
	else
		return false;
}
public boolean ent_existsAtLabel(String label$){
	map_update();
	if(quickMap.getElementItem("label", label$)!=null)
		return true;
	else
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
public Core[] indx_getMarks(String[] keys) {
	 try{
		 map_update();
         ArrayList<Core>cl=new ArrayList<Core>();
    	 String label$=null;
    	 String icon$;
    	 Core key;
    	 
       for(String aKeys:keys){
       	try{
        key=quickMap.getElementItem("key", aKeys);
        if(!isValidKey(aKeys)){
        	System.out.println("StroreAdapter:indx_getMarks: invalid key="+aKeys);
        	continue;
        }
       	label$=key.type;
        icon$=quickMap.getElementItem("label", label$).type;
        cl.add(new Core(icon$,aKeys,label$));
       	}catch(Exception ee){
       		System.out.println("StroreAdapter:indx_getMarks:invalid key="+aKeys+" label="+label$);	
       	}
       }
       return cl.toArray(new Core[0]);
       }catch(Exception e){
       	 LOGGER.severe(":indx_getMarks:"+e.toString());
  	   }
       return null;
    }
public Sack ent_getAtKey(String entityKey$){
	return Sack.parseXML(entigrator.getEntihome() + "/" + Entigrator.ENTITY_BASE + "/data/"+entityKey$);
}
public Sack ent_getAtLabel(String label$){
	String entityKey$=quickMap.getElementItemAt("label", label$);
	return Sack.parseXML(entigrator.getEntihome() + "/" + Entigrator.ENTITY_BASE + "/data/"+entityKey$);
}
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
public String indx_keyAtLabel(String label$) {
    return quickMap.getElementItemAt("label",label$);

}
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
public String[] indx_listAllKeys() {
    try{
        map_update(); 
    	String[]sa=quickMap.elementListNoSorted("key");
             return sa;
    }catch(Exception e){
   	 LOGGER.severe(e.toString());
   	 return null;
    }
   }
public String[] indx_listEntitiesAtType(String entityType$) {
    try{
    	map_update();
       String[]sa=quickMap.elementList("key");
       if(sa==null)
    	   return null;
       ArrayList<String>sl=new ArrayList<String>();
       Core[]ca=quickMap.elementGet("key");
      	for(Core aCa:ca){
      		if(entityType$.equals(aCa.value))
      		   sl.add(aCa.name);
      	}
      	 return sl.toArray(new String[0]);
       
    }catch(Exception e){
   	 LOGGER.severe(e.toString());
   	 return null;
    }
   }
public boolean ent_outdated(Sack entity){
	try{
		Sack header=Sack.parseXML(entigrator.getEntihome()+"/"+HEADERS+"/"+entity.getKey());
		String entSaveId$=entity.getAttributeAt(Entigrator.SAVE_ID);
		String headerSaveId$=header.getAttributeAt(Entigrator.SAVE_ID);
		if(entSaveId$!=null
				&&entSaveId$.equals(headerSaveId$))
			return false;
		long entTime=Long.parseLong(entity.getAttributeAt(Entigrator.TIMESTAMP));
		long headerTime=Long.parseLong(header.getAttributeAt(Entigrator.TIMESTAMP));
		if(headerTime>entTime){
			return true;
		}
		else
			return false;
	}catch(Exception e){
		LOGGER.severe(e.toString());
		
	}
	return true;
}
/*
public boolean map_outdated(){
	try{
		storeState=Sack.parseXML(entigrator.getEntihome() +"/"+STORE_STATE);
		saveId$=storeState.getAttributeAt(Entigrator.SAVE_ID);
		String mapSaveId$=quickMap.getAttributeAt(Entigrator.SAVE_ID);
		if(saveId$!=null
				&&saveId$.equals(mapSaveId$))
			return false;
	}catch(Exception e){
		LOGGER.severe(e.toString());
		
	}
	return true;
}
*/
public void store_refresh(String entityKey$){
	String header$=entigrator.getEntihome()+"/"+HEADERS+"/"+entityKey$;
	Sack header=Sack.parseXML(header$);
	map_insert(header);

}
}

