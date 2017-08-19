package gdt.data.store;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;
import gdt.data.grain.Core;
import gdt.data.grain.Identity;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.grain.Support;

public class StoreAdapter {
	public static final String HEADERS="_AM7SyUTiAcrd_hDOtNegtzohEbc";
	private static final String QUICK_MAP="_0Hw7Cb9q5VrmwG6enFmb5GBKIXo";
	private static final String STORE_STATE="_h118ipt7JttV441WtL_BMFD2klA";
	private static final String LOCKED="locked";
	private final Logger LOGGER= Logger.getLogger(getClass().getName());
	Sack entity;
	Sack header;
	Sack quickMap;
//	Sack headers;
	Sack storeState;
	Entigrator entigrator;
	int delay=100;
	String saveId$;
	boolean debug=false;

	boolean singleMode=false;
	boolean bulkMode=false;
	public StoreAdapter(Entigrator entigrator){
		this.entigrator=entigrator;
		saveId$=map_load();
		store_release();
	}
private String  map_load(){
	try{
		 if(debug)
		System.out.println("StoreAdapter:map_load");
		File  mapFile=new File(entigrator.getEntihome()+"/"+QUICK_MAP);	
      if(mapFile.exists()){
    	  //store_lock();  
		quickMap=Sack.parseXML(entigrator,entigrator.getEntihome()+"/"+QUICK_MAP);
      if(quickMap!=null){
    	  if(debug)
    	  System.out.println("StoreAdapter:map_load: found and loaded");
    	  return quickMap.getAttributeAt(Entigrator.SAVE_ID);
      }
      }
      if(debug)
      System.out.println("StoreAdapter:map_load.cannot load map");
     saveId$=Identity.key();
     map_build();
     quickMap.putAttribute(new Core(null,Entigrator.SAVE_ID,saveId$));
     map_save();
    // store_release();  
	}catch(Exception e){
 		 LOGGER.severe(e.toString());
 	}
	  return saveId$;
	}
private void qmInsert(Sack header){
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
	//map_build();
	qmBuild();
}
private void map_build(){
	try{
		 if(debug)
		System.out.println("StoreAdapter:map_build");
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
   			header=Sack.parseXML(entigrator,headersHome$+s);
   			if(header==null)
   				continue;
   			qmInsert(header);
   		}
   		quickMap.putAttribute(new Core(null,Entigrator.TIMESTAMP,String.valueOf(System.currentTimeMillis())));
   		map_save();
   		//quickMap.saveXML(entigrator.getEntihome()+"/"+QUICK_MAP);	
	}catch(Exception e){
  		 LOGGER.severe(e.toString());
  	}
}
private void qmBuild(){
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
Runnable saveMap=new Runnable(){
	public void run(){
			try{
				//System.out.println("StoreAdapter:saveMap");
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
				storeState.putAttribute(new Core(null,Entigrator.SAVE_ID,saveId$));
				storeState.saveXML(entigrator.getEntihome()+"/"+STORE_STATE);
				quickMap.saveXML(entigrator.getEntihome()+"/"+QUICK_MAP);
                store_release();
			}catch(Exception e){
				LOGGER.severe(e.toString());
			}
	}
};

Runnable qmReplace=new Runnable(){
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
        	return 1;
        if(headerTime==mapTime)
        	return 0;
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
	return -1;
}
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

public boolean qmIsBusy(){
	try{
		Sack qmHeader=Sack.parseXML(entigrator,entigrator.getEntihome()+"/"+STORE_STATE);
		if(qmHeader.getAttribute(LOCKED)!=null)
			return true;
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
	return false;
}
public void qmSetBusy(){
	try{
	File file=new File(entigrator.getEntihome()+"/"+STORE_STATE);
	if(file.exists())
		file.delete();
	Sack qmHeader=new Sack();
	qmHeader.setKey(STORE_STATE);
	long timestamp=System.currentTimeMillis();
    qmHeader.putAttribute(new Core (null,Entigrator.LOCK_TIME,String.valueOf(timestamp)));
	qmHeader.putAttribute(new Core(null,Entigrator.SAVE_ID,quickMap.getAttributeAt(Entigrator.SAVE_ID)));
	qmHeader.putAttribute(new Core(null,LOCKED,null));
    qmHeader.saveXML(entigrator.getEntihome() +"/"+STORE_STATE);
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
}
public void qmRelease(){
	try{
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
private void qmRunReplace(){
	try{
		 if(debug)
		System.out.println("StoreAdapter:qmRunReplace");
		  Thread t=new Thread(qmReplace);
		  t.start();
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
}
public boolean entIsBusy(String entityKey$){
	try{
		String header$=entigrator.getEntihome()+"/"+StoreAdapter.HEADERS+"/"+entityKey$;
	    Sack header=Sack.parseXML(entigrator,header$);
	    if(header.getAttribute(LOCKED)!=null)
	    	return true; 
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
	return false;
}
public boolean entIsObsolete(Sack entity){
	try{
		if(debug)
			System.out.println("StoreAdapter: entisObsolete:entity ="+entity.getKey());
		Sack header=Sack.parseXML(entigrator,entigrator.getEntihome()+"/"+HEADERS+"/"+entity.getKey());
		String entSaveId$=entity.getAttributeAt(Entigrator.SAVE_ID);
		if(debug)
			System.out.println("StoreAdapter: ent_outdated:entity save id="+entSaveId$);
		if(entSaveId$==null)
			return true;	
		
		String headerSaveId$=header.getAttributeAt(Entigrator.SAVE_ID);
		if(!entSaveId$.equals(headerSaveId$))
			return true;
		
	}catch(Exception e){
		LOGGER.severe(e.toString());
		return true;
	}
	return false;
}
public boolean entCanReplace(Sack entity){
	try{
		String header$=entigrator.getEntihome()+"/"+StoreAdapter.HEADERS+"/"+entity.getKey();
	    Sack header=Sack.parseXML(entigrator,header$);
	    if(header==null)
	    	if(qmIsBusy())
	    		return false;
	    if(header.getAttribute(LOCKED)!=null)
	    	return false; 
        String newLabel$=entity.getProperty("label");
        String oldLabel$=header.getElementItemAtValue("label", entity.getKey());
        if(!oldLabel$.equals(newLabel$))
        	if(qmIsBusy())
	    		return false;
        return true;
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
	return false;
}
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
private void entReplace(Sack e){
	try{
		//Sack header=entSetBusy(entity);
		if(header==null||e==null){
			System.out.println("StoreAdapter:entReplace:cannot get entity or header ");
			return;
		}
		Sack h=header;
		
		boolean qmLock=false;
	    String newLabel$=e.getProperty("label");
        String oldLabel$=h.getElementItemAtValue("label", e.getKey());
        if(!oldLabel$.equals(newLabel$)){
        	qmSetBusy();
        	qmLock=true;
        	String qmId$=Identity.key();
        	quickMap.putAttribute(new Core(null,Entigrator.TIMESTAMP,String.valueOf(System.currentTimeMillis())));
        	quickMap.putAttribute(new Core(null,Entigrator.SAVE_ID,qmId$));
        	quickMap.removeElementItem("label", e.getProperty("label"));
            quickMap.removeElementItem("key", e.getKey());
            quickMap.putElementItem("label", new Core(e.getAttributeAt("icon"),e.getProperty("label"),e.getKey()));
            quickMap.putElementItem("key", new Core(e.getProperty("label"),e.getKey(),e.getProperty("entity")));
        	h.putElementItem("label", new Core(e.getAttributeAt("icon"),e.getProperty("label"),e.getKey()));
            h.putElementItem("key", new Core(e.getProperty("label"),e.getKey(),e.getProperty("entity")));
        }
	    h.putAttribute(new Core(null,Entigrator.TIMESTAMP,String.valueOf(System.currentTimeMillis())));
        String entId$=Identity.key();
        h.putAttribute(new Core(null,Entigrator.SAVE_ID,entId$));
        String header$=entigrator.getEntihome()+"/"+StoreAdapter.HEADERS+"/"+e.getKey();
        h.saveXML(header$);
        e.saveXML(entigrator.getEntihome() +"/"+Entigrator.ENTITY_BASE+ "/data/" + e.getKey());
        entRelease(e.getKey());
        if(qmLock){
        quickMap.saveXML(entigrator.getEntihome()+"/"+QUICK_MAP);
        qmRelease();
        }
        
	}catch(Exception ee){
		LOGGER.severe(ee.toString());
	}

}
public Sack entSetBusy(Sack entity){
	try{
		header=null;
		String header$=entigrator.getEntihome()+"/"+StoreAdapter.HEADERS+"/"+entity.getKey();
		header=Sack.parseXML(entigrator,header$);
	    if(header==null){
	    	File file=new File(header$);
	    	if(file.exists())
	    		file.delete();
		//Sack entity=Sack.parseXML(entigrator,entigrator.getEntihome()+  "/" + Entigrator.ENTITY_BASE + "/data/" + entityKey$);
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
public boolean entRelease(String entityKey$){
	try{
		
		String header$=entigrator.getEntihome()+"/"+StoreAdapter.HEADERS+"/"+entityKey$;
	    Sack header=Sack.parseXML(entigrator,header$);
	    header.removeAttribute(LOCKED);
        header.saveXML(header$);
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
	return true;
}
private void map_save(){
	try{
		 if(debug)
		System.out.println("StoreAdapter:saveMap");
		  Thread saver=new Thread(saveMap);
		  saver.start();
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
}

public boolean store_replace(){
	try{
		 if(debug)
		System.out.println("StoreAdapter:store_replace:BEGIN");
		if(store_isLocked())
			return false;
		String newKey$=Identity.key();
		 if(debug)
		System.out.println("StoreAdapter:store_replace:save id="+newKey$);
		
		storeState=Sack.parseXML(entigrator,entigrator.getEntihome() +"/"+STORE_STATE);
		storeState.putAttribute(new Core(null,Entigrator.LOCK_TIME,String.valueOf(System.currentTimeMillis())));
		storeState.saveXML(entigrator.getEntihome() +"/"+STORE_STATE);
		quickMap.putAttribute(new Core(null,Entigrator.SAVE_ID,newKey$));
		long begin=System.currentTimeMillis();
        quickMap.saveXML(entigrator.getEntihome() +"/"+QUICK_MAP);
        if(debug)
        System.out.println("StoreAdapter:store_replace:time="+String.valueOf(System.currentTimeMillis()-begin));
        storeState.putAttribute(new Core(null,Entigrator.SAVE_ID,newKey$));
        storeState.removeAttribute(Entigrator.LOCK_TIME);
        boolean success=false;
        int n=0;
		while(!success)
        try{
        n++;
        if(n>10){
        	System.out.println("StoreAdapter:store_replace:fatal error");
        }
        storeState.saveXML(entigrator.getEntihome()+"/"+STORE_STATE);
        success=true;
		}catch(java.nio.channels.OverlappingFileLockException ee){
			LOGGER.severe("try n="+n+"::"+ ee.toString());
		}
		store_release();
		return true;
	}catch(Exception e){
		LOGGER.severe(e.toString());
		return false;
	}
}

private Sack store_getState(){
	 if(debug)
	System.out.println("StroreAdapter:store_getState");
	try{
	 return Sack.parseXML(entigrator,entigrator.getEntihome()+"/"+STORE_STATE);
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
	return null;
}
private void map_update(){
	try{
		 if(debug)
		System.out.println("StoreAdapter:map_update");
		 
		String mapId$=null;
		if(quickMap==null){
		    map_load();
		    if(debug)
		    System.out.println("StoreAdapter:map_update:map reloaded");
		    return;
		}
		if(singleMode)
			return;
		storeState=store_getState();
        if(storeState==null){
        	store_lock();
        	storeState=store_getState();
        	
        }
		if(storeState==null){
		int cnt=0;
		do{
			Thread.sleep(10);
			storeState=store_getState();
			if(storeState!=null)
				break;
		}while(cnt++<3);
		}
		if(storeState==null){
			store_lock();
			LOGGER.severe("cannot read state");
			//return;
		}
		String stateId$=storeState.getAttributeAt(Entigrator.SAVE_ID);
		mapId$=quickMap.getAttributeAt(Entigrator.SAVE_ID);
		if(mapId$!=null&&mapId$.equals(stateId$)){
			 if(debug) 
			System.out.println("StoreAdapter:map_update:up to date");
			return;
		}
		 map_load();
		 mapId$=quickMap.getAttributeAt(Entigrator.SAVE_ID);
		 storeState.putAttribute(new Core(null,Entigrator.SAVE_ID,mapId$));
		 storeState.saveXML(entigrator.getEntihome()+"/"+STORE_STATE);
		 if(debug)
		 System.out.println("StoreAdapter:map_update:FINISH");
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

//// Store operations
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
    store_replace();
    if(debug)
    System.out.println("StoreAdapter:ent_delete:FINISH");
    return true;
    }
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
	return false;
}

public Sack ent_assignIcon(Sack entity, String icon$) {
	try{
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
		if(key$==null)
			return null;
		if(quickMap!=null){
			Core key=quickMap.getElementItem("key", key$);
			if(key!=null)
				return key.type;
		}
		String header$=entigrator.getEntihome()+"/"+StoreAdapter.HEADERS+"/"+key$;
	    if(header!=null){
		Sack header=Sack.parseXML(entigrator,header$);
	    String label$=header.getElementItem("key", key$).type;
		if(label$!=null){
	    Core key=quickMap.getElementItem("key", key$);
	    key.type=label$;
		quickMap.putElementItem("key", key);
			return label$;
	    }
	    }
	    entity=Sack.parseXML(entigrator,entigrator.getEntihome()+  "/" + Entigrator.ENTITY_BASE + "/data/" + key$);
	    if(entity!=null)
	    	return entity.getProperty("label");
	}catch(Exception e){
		 LOGGER.severe(":indx_getLabel:"+e.toString());
	}
      return null;
   }
public String ent_getIconAtKey(String key$) {
	try{
		//System.out.println("StroreAdapter:ent_getIconAtKey:key="+key$);
		String icon$=null;
		Sack header=Sack.parseXML(entigrator,entigrator.getEntihome() +"/"+HEADERS+"/"+key$);
		if(header!=null){
		Core key=header.getElementItem("key", key$);
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
      return null;
   }
public String getEntityIcon(String key$){
	try{
		Sack header=Sack.parseXML(entigrator,entigrator.getEntihome() +"/"+HEADERS+"/"+key$);
		Core key=null;
		if(header!=null)
				key=header.getElementItem("key", key$);
		if(key!=null&&key.type!=null){
			Core label=header.getElementItem("label", key.value);
			quickMap.putElementItem("label", label);
 			return label.type;
		}
		Sack entity=Sack.parseXML(entigrator,entigrator.getEntihome() + "/" + Entigrator.ENTITY_BASE + "/data/"+key$);
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
            
	} catch(Exception ee){
  	 // LOGGER.info(ee.toString());
  	 
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
		ent_save(entigrator,entity);
	    store_replace();
	}catch(Exception e){
    	LOGGER.severe(":ent_assignIcon:"+e.toString());
	}
	return entity;
}
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
		map_save();
	}catch(Exception e){
    	LOGGER.severe(e.toString());
	}
	return false;
}
public boolean ent_save(Entigrator entigrator,Sack entity){
	try{
		 if(debug)
		System.out.println("StoreAdapter:ent_save:entity="+entity.getProperty("label"));
		Sack header=null;
		String header$=entigrator.getEntihome()+"/"+HEADERS+"/"+entity.getKey();
        File headerFile=new File(header$);
            if(headerFile.exists())
            		header=Sack.parseXML(entigrator,header$);
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
            else{
            	saveId$=Identity.key();
            	header.putAttribute(new Core(null,Entigrator.SAVE_ID,saveId$));
            	entity.putAttribute(new Core(null,Entigrator.SAVE_ID,saveId$));
            	
            }
            String jvm$=ManagementFactory.getRuntimeMXBean().getName();
            header.putAttribute(new Core(null,"locked",jvm$));
            header.saveXML(header$);
            entity.saveXML(entigrator.getEntihome() + "/" + Entigrator.ENTITY_BASE + "/data/"+entity.getKey());
            header.removeAttribute("locked");
            header.saveXML(header$);
            if(!bulkMode){
            	if(!singleMode)
                   map_update();
            qmInsert(header);
            map_save();
            }else
            	 qmInsert(header);
            	
            return true;
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
	return false;
}
public String  store_saveId(){
	 if(debug)
	System.out.println("StoreAdapter:store_saveId:begin");
	storeState=Sack.parseXML(entigrator,entigrator.getEntihome() +"/"+STORE_STATE);
	if(storeState==null){
		return null;
	}else
		return storeState.getAttributeAt(Entigrator.SAVE_ID);
	
}

private boolean store_lock(){
	try{
		 if(debug)
		System.out.println("StoreAdapter:store_lock:save id="+saveId$);
		File state=new File(entigrator.getEntihome() +"/"+STORE_STATE);
		if(state.exists())
			storeState=Sack.parseXML(entigrator,entigrator.getEntihome() +"/"+STORE_STATE);
		if(storeState==null)
		{
			if(debug)
		      System.out.println("StoreAdapter:store_lock:state file invalid"); 	
			storeState=new Sack();
			storeState.setKey(STORE_STATE);
		}
		long timestamp=System.currentTimeMillis();
		storeState.putAttribute(new Core (null,Entigrator.LOCK_TIME,String.valueOf(timestamp)));
		saveId$=quickMap.getAttributeAt(Entigrator.SAVE_ID);
		if(saveId$!=null)
			storeState.putAttribute(new Core(null,Entigrator.SAVE_ID,saveId$));
		storeState.saveXML(entigrator.getEntihome() +"/"+STORE_STATE);
			return false;
	
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
	return false;
}
private boolean store_release(){
	try{
		 if(debug)
		System.out.println("StoreAdapter:store_release:BEGIN");
		if(new File(entigrator.getEntihome() +"/"+STORE_STATE).exists())
	    	storeState=Sack.parseXML(entigrator,entigrator.getEntihome() +"/"+STORE_STATE);
		if(storeState==null)
			store_lock();
		saveId$=quickMap.getAttributeAt(Entigrator.SAVE_ID);
		storeState.removeAttribute(Entigrator.LOCK_OWNER);
		storeState.removeAttribute(Entigrator.LOCK_PROCESS);
		storeState.removeAttribute(Entigrator.LOCK_TIME);
		storeState.putAttribute(new Core(null,Entigrator.SAVE_ID,saveId$));
		storeState.saveXML(entigrator.getEntihome() +"/"+STORE_STATE);
		return true;
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
	return false;
}
public String store_reload(){
	try{
		 if(debug)
		System.out.println("StoreAdapter:store_reload");
		quickMap=Sack.parseXML(entigrator,entigrator.getEntihome() +"/"+QUICK_MAP);
		return quickMap.getAttributeAt(Entigrator.SAVE_ID);
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
	return null;
}
public boolean qmOutdated(){
	try{
		 if(debug)
		System.out.println("StoreAdapter:store_outdated:BEGIN");
		saveId$=quickMap.getAttributeAt(Entigrator.SAVE_ID);
		storeState=Sack.parseXML(entigrator,entigrator.getEntihome() +"/"+STORE_STATE);
		 if(saveId$.equals(storeState.getAttributeAt(Entigrator.SAVE_ID)))
				return false;
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
	return true;
}
private boolean store_isLocked(){
	try{
		 if(debug)
		System.out.println("StoreAdapter:store_isLocked:begin");
		if(singleMode)
			return false;
		 File state=new File(entigrator.getEntihome() +"/"+STORE_STATE);
		if(!state.exists())
			return false;
		storeState=Sack.parseXML(entigrator,entigrator.getEntihome() +"/"+STORE_STATE);
		if(storeState==null)
				return false;
		if(!storeState.getAttributeAt(Entigrator.SAVE_ID).equals(quickMap.getAttributeAt(Entigrator.SAVE_ID)))
		{
			 if(debug)
			System.out.println("StoreAdapter:store_isLocked=true");
	       	  return true;
		}
	    else{
	    	 if(debug)
	    	System.out.println("StoreAdapter:store_isLocked=false");
	      	  return false;
	    }
	}catch(Exception e){
		LOGGER.severe(e.toString());
		
	}
	return false;
}



public boolean entExistsAtKey(String entityKey$){
	if(debug)
		System.out.println("StoreAdapter:ent_existsAtKey.entity key="+entityKey$);
    try{
	if(quickMap.getElementItem("key", entityKey$)!=null)
		return true;
	String header$=entigrator.getEntihome()+"/"+StoreAdapter.HEADERS+"/"+entityKey$;
    Sack header=Sack.parseXML(entigrator,header$);
    if(header!=null){
    	Sack entity=Sack.parseXML(entigrator,entigrator.getEntihome()+  "/" + Entigrator.ENTITY_BASE + "/data/" + entityKey$);
    	qmInsert(entity);
    	return true; 
    }
}catch(Exception e){
	LOGGER.severe(e.toString());	
}
	return false;
}
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
public boolean entExistsAtLabel(String label$){
	if(debug)
		System.out.println("StoreAdapter:ent_existsAtLabel:entity label="+label$);
//	map_update();
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
public Sack ent_getAtKey(String entityKey$){
	return Sack.parseXML(entigrator,entigrator.getEntihome() + "/" + Entigrator.ENTITY_BASE + "/data/"+entityKey$);
}
public Sack entGetAtLabel(String label$){
	String entityKey$=quickMap.getElementItemAt("label", label$);
	return Sack.parseXML(entigrator,entigrator.getEntihome() + "/" + Entigrator.ENTITY_BASE + "/data/"+entityKey$);
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
public String indx_keyAtLabel(String label$) {
    
	String key$=quickMap.getElementItemAt("label",label$);
    return key$;

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
	if(debug)
		System.out.println("StoreAdapter:indx_listAllKeys");

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
	if(debug)
		System.out.println("StoreAdapter: indx_listEntitiesAtType:entity type="+entityType$);

	try{
    	//map_update();
       String[]sa=quickMap.elementList("key");
       if(sa==null)
    	   return null;
       ArrayList<String>sl=new ArrayList<String>();
       //System.out.println("StoreAdapter: indx_listEntitiesAtType:1");
       Core[]ca=quickMap.elementGet("key");
      // System.out.println("StoreAdapter: indx_listEntitiesAtType:2");
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
public void store_refresh(){
	if(qmOutdated())
		store_reload();
	if(debug)
		System.out.println("StoreAdapter:store_refresh:store save id="+saveId$);
}
/*
public void store_block(){
	singleMode=true;

}
public void store_unblock(){
	singleMode=false;
}
*/
public void setSingleMode(boolean set){
	if(set)
		singleMode=true;
	else
		singleMode=false;
}
public void setBulkMode(boolean set){
	if(set)
		bulkMode=true;
	else{
		bulkMode=false;
		map_save();
	}
}
public void ent_releaseKey(String key$){
	try{
		if(key$==null)
			return;
		
		File file = new File( entigrator.getEntihome() +"/"+Entigrator.ENTITY_BASE+ "/data/" + key$);
	    if(file.exists()&&file.canWrite()){
		   file.delete();
		   Core key=quickMap.getElementItem("key", key$);
		if(key!=null)
		   quickMap.removeElementItem("label", key.value);
	    quickMap.removeElementItem("key", key$);
	    
	    File header=new File(entigrator.getEntihome()+"/"+HEADERS+"/"+key$);
	    if(header.exists())
	 	   header.delete();
	    store_replace();
	    if(debug)
	    System.out.println("StoreAdapter:ent_releaseKey:FINISH");
	    }
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
		
}
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
	    store_replace();
	    if(debug)
	    System.out.println("StoreAdapter:ent_releaseLabel:FINISH");
	    
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
		
}
}

