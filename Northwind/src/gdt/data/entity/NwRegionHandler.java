package gdt.data.entity;
/*
 * Copyright 2017 Alexander Imas
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
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
import gdt.data.entity.facet.FieldsHandler;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
public class NwRegionHandler extends FieldsHandler{
	public static final String EXTENSION_KEY="_ebrysyRG3lk1CYHiugIoSapofvo";	
private Logger LOGGER=Logger.getLogger(NwRegionHandler.class.getName());
String entihome$;
String entityKey$;
static boolean debug=false;
public final static String REGION="nwRegion";

public NwRegionHandler(){
	super();
}
	@Override
	public boolean isApplied(Entigrator entigrator, String locator$) {
		try{
		if(debug)	
			System.out.println("NwRegionHandler:isApplied:locator="+locator$);
			Properties locator=Locator.toProperties(locator$);
			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			boolean result=false;
			Sack entity=entigrator.getEntityAtKey(entityKey$);
		if(debug)	
			System.out.println("NwRegionHandler:isApplied:entity="+entity.getProperty("label"));
			String region$=entity.getProperty("nwRegion");
			if(region$!=null&&!Locator.LOCATOR_FALSE.equals(region$)){
			   if(entity.getElementItem("fhandler", NwRegionHandler.class.getName())==null){	
					if(!entity.existsElement("fhandler"))
						entity.createElement("fhandler");
					entity.putElementItem("fhandler", new Core(null, NwRegionHandler.class.getName(),null));
					entigrator.ent_alter(entity);
				}
	            result=true;
			}
			return result;
		}catch(Exception e){
		LOGGER.severe(e.toString());
		return false;
		}
	}
	public String getTitle() {
		return "nwRegion";
	}

	public String getType() {
		return "nwRegion";
	}
	private void adaptLabel(Entigrator entigrator){
		 try{
				Sack entity=entigrator.getEntityAtKey(entityKey$);
				entigrator.ent_assignProperty(entity, "nwRegion", entityLabel$);
		    }catch(Exception e){
		    	
		    }
	}
	@Override
	public void adaptClone(Entigrator entigrator) {
	   adaptLabel(entigrator);
		
	}

	@Override
	public void adaptRename(Entigrator entigrator) {
		adaptLabel(entigrator);
	}


@Override
public String getClassName() {
	return  NwRegionHandler.class.getName();
}
public  static void rebuildRegions(Entigrator entigrator,Document doc,boolean update){
	if(debug)
	System.out.println("NwRegionHandler:rebuildRegions:BEGIN");
	
	try{
		Sack id2key=entigrator.getEntityAtKey(entigrator.indx_keyAtLabel("id2key"));
		if(id2key==null){
			entigrator.ent_releaseLabel("id2key");
			id2key=entigrator.ent_new("fields", "id2key");
		}
		if(!update){
		String[] sa=entigrator.indx_listEntities("entity", "nwRegion");
		if(sa!=null){
			Sack region;
			for(String s:sa){
				region=entigrator.getEntityAtKey(s);
				if(region!=null)
				  entigrator.deleteEntity(region);
			}
		}
		}
		if(debug)
		System.out.println("NwRegionHandler:rebuildRegions:Root element :" 
	            + doc.getDocumentElement().getNodeName());
		NodeList rList = doc.getElementsByTagName("Regions");
		 for (int i = 0; i < rList.getLength(); i++){
			 Node rNode = rList.item(i);
			 NodeList nList = ((Element)rNode).getChildNodes();
			 
			 //NodeList nList = doc.getElementsByTagName("Region");
	         if(debug)
	         System.out.println("----------------------------");
	         Sack nwRegion;
	         String nwRegion$;
	         for (int temp = 0; temp < nList.getLength(); temp++) {
	        	 Node nNode = nList.item(temp);
	             if(debug)
	        	 System.out.println("\nCurrent Element :" 
	                + nNode.getNodeName());
	             if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	                 Element eElement = (Element) nNode;
	                 if(eElement.getElementsByTagName("RegionDescription")==null){
	                	 if(debug)
	        	        	 System.out.println("\nCurrent Element :no element RegionDescription");
	                	 continue;
	                 }
	                 if(eElement.getElementsByTagName("RegionDescription").item(0)==null){
	                	 if(debug)
	        	        	 System.out.println("\nCurrent Element :no item(0)");
	                	 continue;
	                 }
	                
	                 String region$=eElement
	    	                    .getElementsByTagName("RegionDescription")
	    	                    .item(0)
	    	                    .getTextContent();
	                 if(debug)
	                 System.out.println("RegionDescription : " 
	    	                 + region$);
	                 if(update){ 
		                 nwRegion$=entigrator.indx_keyAtLabel(region$);
		                 if(nwRegion$!=null){
		                	 nwRegion=entigrator.getEntityAtKey(nwRegion$);
		                     if(nwRegion!=null){ 
		                    	 entigrator.ent_reindex(nwRegion);
		                    	 continue;
		                     }
		                }
	                 }
	                 nwRegion=createRegion(entigrator, region$);
	                 if(nwRegion==null){
	                	 if(debug)
	                	 System.out.println("NwRegionHandler:rebuildRegions:cannot create category="+region$);
	                	 continue;
	                 }
	                 nwRegion.putElementItem("field", new Core(null,"RegionDescription",region$));
	                 String id$=eElement.getAttribute("RegionID");
	                 if(debug)
	                 System.out.println("RegionID  : " 
	                    +id$ );
	                 nwRegion.putElementItem("field", new Core(null,"RegionID",id$));
	                	              entigrator.ent_alter(nwRegion);
	              nwRegion=entigrator.ent_assignProperty(nwRegion, "nwRegion", region$);
	              nwRegion=entigrator.ent_assignProperty(nwRegion, "fields",region$);
	              entigrator.ent_reindex(nwRegion);
	              if(!id2key.existsElement("nwRegion"))
	            	  id2key.createElement("nwRegion");
	              id2key.putElementItem("nwRegion", new Core(null,id$,nwRegion.getKey()));
	      
	             }
	             } }
		 entigrator.ent_alter(id2key);
	}catch(Exception e){
		Logger.getLogger(NwRegionHandler.class.getName()).severe(e.toString());
	}
	
}
private  static Sack createRegion(Entigrator entigrator,String label$ ){
	if(debug)
	System.out.println("NwRegionHandler:createRegion:"+label$);
	if(label$==null||entigrator==null)
		return null;
	try{
	String oldKey$=entigrator.indx_keyAtLabel(label$);
	if(oldKey$!=null){
		Sack old=entigrator.getEntityAtKey(oldKey$);
		if(old!=null){
			String icon$=old.getAttributeAt("icon");
			if(icon$!=null){
				File icon=new File(entigrator.getEntihome()+"/"+Entigrator.ICONS+"/"+icon$);
				if(icon!=null)
					icon.delete();
			}
		entigrator.deleteEntity(old);
		}
		
	}
	Sack newEntity=entigrator.ent_new("nwRegion", label$);
	newEntity.createElement("field");
	newEntity.createElement("fhandler");
	newEntity.putElementItem("fhandler", new Core(null,NwRegionHandler.class.getName(),null));
	newEntity.putElementItem("fhandler", new Core(null,FieldsHandler.class.getName(),null));
	newEntity.createElement("jfacet");
	newEntity.putElementItem("jfacet", new Core("gdt.jgui.entity.fields.JFieldsFacetAddItem",FieldsHandler.class.getName(),"gdt.jgui.entity.fields.JFieldsFacetOpenItem"));
	newEntity.putElementItem("jfacet", new Core("gdt.jgui.entity.nwregion.JNwRegionFacetAddItem",NwRegionHandler.class.getName(),"gdt.jgui.entity.nwregion.JNwRegionFacetOpenItem"));
	
	return newEntity;

	}catch(Exception e){
		Logger.getLogger(NwRegionHandler.class.getName()).severe(e.toString());
		
	}
return null;
}
@Override
public String getLocation() {
	return EXTENSION_KEY;
}
}