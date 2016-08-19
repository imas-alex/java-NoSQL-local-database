package gdt.data.entity;
/*
 * Copyright 2016 Alexander Imas
 * This file is extension of JEntigrator.

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
import java.util.Properties;
import java.util.logging.Logger;

import gdt.data.entity.facet.FieldsHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
import gdt.data.store.FileExpert;
import gdt.jgui.entity.JReferenceEntry;
/**
* Contains methods to process an edge entity .
* @author  Alexander Imas
* @version 1.0
* @since   2016-08-08
*/
public class EdgeHandler extends FieldsHandler{
	private Logger LOGGER=Logger.getLogger(EdgeHandler.class.getName());
	public static final String EXTENSION_KEY="_Tm142C8Sgti2iAKlDEcEXT2Kj1E";
	String entihome$;
	String entityKey$;
	public final static String EDGE="edge";
	/**
	 * Default constructor
	 */
	public EdgeHandler(){
		super();
	}
	/**
	 * Check if the handler is applied to the entity  
	 *  @param entigrator entigrator instance
	 *  @param locator$ entity's locator 
	 * @return true if applied false otherwise.
	 */		
	@Override
	public boolean isApplied(Entigrator entigrator, String locator$) {
		try{
//		System.out.println("AddressHandler:isApplied:locator="+locator$);
			Properties locator=Locator.toProperties(locator$);
			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			boolean result=false;
			Sack entity=entigrator.getEntityAtKey(entityKey$);
//			System.out.println("PhoneHandler:isApplied:entity="+entity.getProperty("label"));
			String edge$=entity.getProperty("edge");
			if(edge$!=null&&!Locator.LOCATOR_FALSE.equals(edge$)){
			   if(entity.getElementItem("fhandler", EdgeHandler.class.getName())==null){	
					if(!entity.existsElement("fhandler"))
						entity.createElement("fhandler");
					entity.putElementItem("fhandler", new Core(null, EdgeHandler.class.getName(),null));
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
     * Get title of the handler.  
     * @return the title of the handler..
     */	
	public String getTitle() {
		return "Edge";
	}
	 /**
     * Get type of the  handler.  
     * @return the type of the handler..
     */	
	public String getType() {
		return "edge";
	}
	
	private void adaptLabel(Entigrator entigrator){
		 try{
				Sack entity=entigrator.getEntityAtKey(entityKey$);
				entigrator.ent_assignProperty(entity, "edge", entityLabel$);
		    }catch(Exception e){
		    	
		    }
	}
	/**
	 * Adapt the label of the clone.  
	 * *  @param entigrator entigrator instance
	 */	
	@Override
	public void adaptClone(Entigrator entigrator) {
	   adaptLabel(entigrator);
		
	}
	/**
	 * Adapt the label after renaming  
	 * *  @param entigrator entigrator instance
	 */	
	@Override
	public void adaptRename(Entigrator entigrator) {
		adaptLabel(entigrator);
	}
	/**
     * Get class name of the handler.  
     * @return the class name of the handler..
     */	

@Override
public String getClassName() {
	return  EdgeHandler.class.getName();
}
/**
 * Complete migration after transfer 
 * *  @param entigrator entigrator instance
 */	
@Override
public void completeMigration(Entigrator entigrator) {
 //   System.out.println("EdgeHandler.completeMigration:entity key="+entityKey$);
	try{
    String[] sa=entigrator.indx_listEntitiesAtPropertyName("node");
    Sack edge=entigrator.getEntityAtKey(entityKey$);
    if(sa==null){
    	edge.removeElement("bond");
    	edge.removeElement("detail");
    	entigrator.save(edge);
    	return;
    }
    
    ArrayList <Core>cl=new ArrayList<Core>();
    Core[] ca=edge.elementGet("bond");
    //check valid bonds
    boolean valid;
    for(Core c:ca){
    	valid=false;
        for(String s:sa)
        	if(s.equals(c.type)){
        		valid=true;
        		break;
        	}
        if(!valid)
        	continue;
        for(String s:sa)
        	if(s.equals(c.value)){
        		cl.add(c);
        		break;
        	}
        	
    }
    ca=cl.toArray(new Core[0]);
    edge.elementReplace("bond", ca);
    Core[] da=edge.elementGet("detail");
    cl.clear();
    for(Core d:da)
       for( Core c:ca)
    	  if(d.type.equals(c.name))
    		  cl.add(d);
    da=cl.toArray(new Core[0]);
    edge.elementReplace("detail", da);
    entigrator.save(edge);
    String originEntihome$=edge.getAttributeAt(JReferenceEntry.ORIGIN_ENTIHOME);
    File sourceDetail;
    File targetDetail;
    Sack pastedEntity;
    entihome$=entigrator.getEntihome();
    String icon$;
    File sourceIcon;
    File targetIcon;
    for(Core d:da){
     try{
    	sourceDetail=new File(originEntihome$+"/"+Entigrator.ENTITY_BASE+"/data/"+d.value);
	    if(!sourceDetail.exists()||sourceDetail.length()<10)
	    	continue;
    	targetDetail=new File(entihome$+"/"+Entigrator.ENTITY_BASE+"/data/"+d.value);
	   if(!targetDetail.exists())
	    	targetDetail.createNewFile();
	    FileExpert.copyFile(sourceDetail,targetDetail);
	  
	    pastedEntity=Sack.parseXML(targetDetail.getPath());
	   
	    icon$=pastedEntity.getAttributeAt("icon");
	    sourceIcon= new File(originEntihome$+"/"+Entigrator.ICONS+"/"+icon$);
	    targetIcon=new File(entihome$+"/"+Entigrator.ICONS+"/"+icon$);
	    if(!targetIcon.exists())
	    	targetIcon.createNewFile();
	    FileExpert.copyFile(sourceIcon,targetIcon);
	    entigrator.ent_reindex(pastedEntity);
     }catch(Exception ee){
    	 System.out.println("EdgeHandler:completeMigration: "+ee.toString()); 
     }
    }
	}catch(Exception e){
		Logger.getLogger(EdgeHandler.class.getName()).severe(e.toString());
	}
}
}
