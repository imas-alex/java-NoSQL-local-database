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
public class NwShipperHandler extends FieldsHandler{
	public static final String EXTENSION_KEY="_ebrysyRG3lk1CYHiugIoSapofvo";	
private Logger LOGGER=Logger.getLogger(NwShipperHandler.class.getName());
String entihome$;
String entityKey$;
static boolean debug=false;
public final static String SHIPPER="shipper";

public NwShipperHandler(){
	super();
}
	@Override
	public boolean isApplied(Entigrator entigrator, String locator$) {
		try{
		if(debug)	
			System.out.println("NwShipperHandler:isApplied:locator="+locator$);
			Properties locator=Locator.toProperties(locator$);
			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			boolean result=false;
			Sack entity=entigrator.getEntityAtKey(entityKey$);
		if(debug)	
			System.out.println("NwShipperHandler:isApplied:entity="+entity.getProperty("label"));
			String shipper$=entity.getProperty("nwShipper");
			if(shipper$!=null&&!Locator.LOCATOR_FALSE.equals(shipper$)){
			   if(entity.getElementItem("fhandler", NwShipperHandler.class.getName())==null){	
					if(!entity.existsElement("fhandler"))
						entity.createElement("fhandler");
					entity.putElementItem("fhandler", new Core(null, NwShipperHandler.class.getName(),null));
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
		return "nwShipper";
	}

	public String getType() {
		return "nwShipper";
	}
	private void adaptLabel(Entigrator entigrator){
		 try{
				Sack entity=entigrator.getEntityAtKey(entityKey$);
				entigrator.ent_assignProperty(entity, "nwShipper", entityLabel$);
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
	return  NwShipperHandler.class.getName();
}
public  static void rebuildShippers(Entigrator entigrator,Document doc,boolean update){
	if(debug)
	System.out.println("NwShipperHandler:rebuildShippers:BEGIN");
	
	try{
		Sack id2key=entigrator.getEntityAtKey(entigrator.indx_keyAtLabel("id2key"));
		if(id2key==null){
			entigrator.ent_releaseLabel("id2key");
			id2key=entigrator.ent_new("fields", "id2key");
		}
		if(!update){
		String[] sa=entigrator.indx_listEntities("entity", "nwShipper");
		if(sa!=null){
			Sack shipper;
			for(String s:sa){
				shipper=entigrator.getEntityAtKey(s);
				if(shipper!=null)
				  entigrator.deleteEntity(shipper);
			}
		}
		}
		if(debug)
		System.out.println("NwShipperHandler:rebuildShippers:Root element :" 
	            + doc.getDocumentElement().getNodeName());
		NodeList rList = doc.getElementsByTagName("Shippers");
		 for (int i = 0; i < rList.getLength(); i++){
			 Node rNode = rList.item(i);
			 NodeList nList = ((Element)rNode).getChildNodes();
			
	         if(debug)
	         System.out.println("----------------------------");
	         Sack nwShipper;
	         String nwShipper$;
	         for (int temp = 0; temp < nList.getLength(); temp++) {
	        	 Node nNode = nList.item(temp);
	             if(debug)
	        	 System.out.println("\nCurrent Element :" 
	                + nNode.getNodeName());
	             if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	                 Element eElement = (Element) nNode;
	                 
	                
	                 String companyName$=eElement
	    	                    .getElementsByTagName("CompanyName")
	    	                    .item(0)
	    	                    .getTextContent();
	                 if(debug)
	                 System.out.println("CompanyName : " 
	    	                 + companyName$);
	                 if(update){ 
		                 nwShipper$=entigrator.indx_keyAtLabel(companyName$);
		                 if(nwShipper$!=null){
		                	 nwShipper=entigrator.getEntityAtKey(nwShipper$);
		                     if(nwShipper!=null){ 
		                    	 entigrator.ent_reindex(nwShipper);
		                    	 continue;
		                     }
		                }
	                 }
	                 nwShipper=createShipper(entigrator, companyName$);
	                 if(nwShipper==null){
	                	 if(debug)
	                	 System.out.println("NwShipperHandler:rebuildShippers:cannot create category="+companyName$);
	                	 continue;
	                 }
	                 nwShipper.putElementItem("field", new Core(null,"CompanyName",companyName$));
	               
	                 String phone$=eElement
	    	                    .getElementsByTagName("Phone")
	    	                    .item(0)
	    	                    .getTextContent();
	                 if(debug)
	                 System.out.println("Phone : " 
	    	                 + phone$);
	                 nwShipper.putElementItem("field", new Core(null,"Phone",phone$));
	               
	                 String id$=eElement.getAttribute("ShipperID");
	                 if(debug)
	                 System.out.println("ShipperID  : " 
	                    +id$ );
	                 nwShipper.putElementItem("field", new Core(null,"ShipperID",id$));
	                	              entigrator.ent_alter(nwShipper);
	              nwShipper=entigrator.ent_assignProperty(nwShipper, "nwShipper", companyName$);
	              nwShipper=entigrator.ent_assignProperty(nwShipper, "fields",companyName$);
	              entigrator.ent_reindex(nwShipper);
	              if(!id2key.existsElement("nwShipper"))
	            	  id2key.createElement("nwShipper");
	              id2key.putElementItem("nwShiiper", new Core(null,id$,nwShipper.getKey()));
	      
	             }} }
		 entigrator.ent_alter(id2key);
	}catch(Exception e){
		Logger.getLogger(NwShipperHandler.class.getName()).severe(e.toString());
	}
	
}
private  static Sack createShipper(Entigrator entigrator,String label$ ){
	if(debug)
	System.out.println("NwShipperHandler:createShipper:"+label$);
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
	Sack newEntity=entigrator.ent_new("nwShipper", label$);
	newEntity.createElement("field");
	newEntity.createElement("fhandler");
	newEntity.putElementItem("fhandler", new Core(null,NwShipperHandler.class.getName(),null));
	newEntity.putElementItem("fhandler", new Core(null,FieldsHandler.class.getName(),null));
	newEntity.createElement("jfacet");
	newEntity.putElementItem("jfacet", new Core("gdt.jgui.entity.fields.JFieldsFacetAddItem",FieldsHandler.class.getName(),"gdt.jgui.entity.fields.JFieldsFacetOpenItem"));
	newEntity.putElementItem("jfacet", new Core("gdt.jgui.entity.nwshipper.JNwShipperFacetAddItem",NwShipperHandler.class.getName(),"gdt.jgui.entity.nwshipper.JNwShipperFacetOpenItem"));
	
	return newEntity;

	}catch(Exception e){
		Logger.getLogger(NwShipperHandler.class.getName()).severe(e.toString());
		
	}
return null;
}
@Override
public String getLocation() {
	return EXTENSION_KEY;
}
}