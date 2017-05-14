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
public class NwTerritoryHandler extends FieldsHandler{
	public static final String EXTENSION_KEY="_ebrysyRG3lk1CYHiugIoSapofvo";	
private Logger LOGGER=Logger.getLogger(NwTerritoryHandler.class.getName());
String entihome$;
String entityKey$;
static boolean debug=true;
public final static String TERRITORY="nwTerritory";

public NwTerritoryHandler(){
	super();
}
	@Override
	public boolean isApplied(Entigrator entigrator, String locator$) {
		try{
		if(debug)	
			System.out.println("NwTerritoryHandler:isApplied:locator="+locator$);
			Properties locator=Locator.toProperties(locator$);
			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			boolean result=false;
			Sack entity=entigrator.getEntityAtKey(entityKey$);
		if(debug)	
			System.out.println("NwTerritoryHandler:isApplied:entity="+entity.getProperty("label"));
			String territory$=entity.getProperty("nwTerritory");
			if(territory$!=null&&!Locator.LOCATOR_FALSE.equals(territory$)){
			   if(entity.getElementItem("fhandler", NwTerritoryHandler.class.getName())==null){	
					if(!entity.existsElement("fhandler"))
						entity.createElement("fhandler");
					entity.putElementItem("fhandler", new Core(null, NwTerritoryHandler.class.getName(),null));
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
	public String getTitle() {
		return "nwTerritory";
	}

	public String getType() {
		return "nwTerritory";
	}
	private void adaptLabel(Entigrator entigrator){
		 try{
				Sack entity=entigrator.getEntityAtKey(entityKey$);
				entigrator.ent_assignProperty(entity, "nwTerritory", entityLabel$);
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
	return  NwTerritoryHandler.class.getName();
}
public  static void rebuildTerritories(Entigrator entigrator,Document doc,boolean update){
	if(debug)
	System.out.println("NwTerritoryHandler:rebuildTerritories:BEGIN");
	
	try{
		Sack id2key=entigrator.getEntityAtKey(entigrator.indx_keyAtLabel("id2key"));
		if(id2key==null){
			entigrator.ent_releaseLabel("id2key");
			id2key=entigrator.ent_new("fields", "id2key");
		}
		if(!update){
		String[] sa=entigrator.indx_listEntities("entity", "nwTerritory");
		if(sa!=null){
			Sack territory;
			for(String s:sa){
				territory=entigrator.getEntityAtKey(s);
				if(territory!=null)
				  entigrator.deleteEntity(territory);
			}
		}
		}
		if(debug)
		System.out.println("NwTerritoryHandler:rebuildTerritories:Root element :" 
	            + doc.getDocumentElement().getNodeName());
	         NodeList nList = doc.getElementsByTagName("Territory");
	         if(debug)
	         System.out.println("----------------------------");
	         Sack nwTerritory;
	         String nwTerritory$;
	         for (int temp = 0; temp < nList.getLength(); temp++) {
	        	 Node nNode = nList.item(temp);
	             if(debug)
	        	 System.out.println("\nCurrent Element :" 
	                + nNode.getNodeName());
	             if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	                 Element eElement = (Element) nNode;
	                 String territory$=eElement
	    	                    .getElementsByTagName("TerritoryDescription")
	    	                    .item(0)
	    	                    .getTextContent();
	                 if(debug)
	                 System.out.println("TerritoryDescription : " 
	    	                 + territory$);
	                 
	                 String id$=eElement.getAttribute("TerritoryID");
	                 if(debug)
	                 System.out.println("Territory ID : " 
	                    +id$ );
	              
	                 if(update){ 
		                 nwTerritory$=entigrator.indx_keyAtLabel(territory$+"-"+id$);
		                 if(nwTerritory$!=null){
		                	 nwTerritory=entigrator.getEntityAtKey(nwTerritory$);
		                     if(nwTerritory!=null){ 
		                    	 entigrator.ent_reindex(nwTerritory);
		                    	 continue;
		                     }
		                }
	                 }
	                 nwTerritory=createTerritory(entigrator, territory$+"-"+id$);
	                 if(nwTerritory==null){
	                	 if(debug)
	                	 System.out.println("NwTerritiryHandler:rebuildTerritories:cannot create category="+territory$);
	                	 continue;
	                 }
	                 nwTerritory.putElementItem("field", new Core(null,"Territory",territory$));
	                   nwTerritory.putElementItem("field", new Core(null,"TerritoryID",id$));
	                
	                 String regionID$=eElement
		 	                    .getElementsByTagName("RegionID")
			                    .item(0)
			                    .getTextContent();
		                 if(regionID$!=null)
		                	 nwTerritory.putElementItem("field", new Core(null,"RegionID",regionID$));
		                 if(debug)
		                 System.out.println("RegionID : " 
		                    +regionID$ );
		        
	            
	              entigrator.replace(nwTerritory);
	              nwTerritory=entigrator.ent_assignProperty(nwTerritory, "nwTerritory", territory$);
	              nwTerritory=entigrator.ent_assignProperty(nwTerritory, "fields",territory$);
	              entigrator.ent_reindex(nwTerritory);
	              if(!id2key.existsElement("nwTerritory"))
	            	  id2key.createElement("nwTerritory");
	              id2key.putElementItem("nwTerritory", new Core(null,id$,nwTerritory.getKey()));
	      
	             }} 
	         entigrator.replace(id2key);
	}catch(Exception e){
		Logger.getLogger(NwTerritoryHandler.class.getName()).severe(e.toString());
	}
	
}
private  static Sack createTerritory(Entigrator entigrator,String label$ ){
	if(debug)
	System.out.println("NwTerritoryHandler:createTerritory:"+label$);
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
	Sack newEntity=entigrator.ent_new("nwTerritory", label$);
	newEntity.createElement("field");
	newEntity.createElement("fhandler");
	newEntity.putElementItem("fhandler", new Core(null,NwTerritoryHandler.class.getName(),null));
	newEntity.putElementItem("fhandler", new Core(null,FieldsHandler.class.getName(),null));
	newEntity.createElement("jfacet");
	newEntity.putElementItem("jfacet", new Core("gdt.jgui.entity.fields.JFieldsFacetAddItem",FieldsHandler.class.getName(),"gdt.jgui.entity.fields.JFieldsFacetOpenItem"));
	newEntity.putElementItem("jfacet", new Core("gdt.jgui.entity.nwterritory.JNwTerritoryFacetAddItem",NwTerritoryHandler.class.getName(),"gdt.jgui.entity.nwterritory.JNwTerritoryFacetOpenItem"));
	
	return newEntity;

	}catch(Exception e){
		Logger.getLogger(NwTerritoryHandler.class.getName()).severe(e.toString());
		
	}
return null;
}
@Override
public String getLocation() {
	return EXTENSION_KEY;
}
}