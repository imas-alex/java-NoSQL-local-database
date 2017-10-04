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
public class NwSupplierHandler extends FieldsHandler{
	public static final String EXTENSION_KEY="_ebrysyRG3lk1CYHiugIoSapofvo";	
private Logger LOGGER=Logger.getLogger(NwSupplierHandler.class.getName());
String entihome$;
String entityKey$;
static boolean debug=false;
public final static String SUPPLIER="supplier";

public NwSupplierHandler(){
	super();
}
	@Override
	public boolean isApplied(Entigrator entigrator, String locator$) {
		try{
		if(debug)	
			System.out.println("NwSupplierHandler:isApplied:locator="+locator$);
			Properties locator=Locator.toProperties(locator$);
			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			boolean result=false;
			Sack entity=entigrator.getEntityAtKey(entityKey$);
		if(debug)	
			System.out.println("NwSupplierHandler:isApplied:entity="+entity.getProperty("label"));
			String supplier$=entity.getProperty("nwSupplier");
			if(supplier$!=null&&!Locator.LOCATOR_FALSE.equals(supplier$)){
			   if(entity.getElementItem("fhandler", NwSupplierHandler.class.getName())==null){	
					if(!entity.existsElement("fhandler"))
						entity.createElement("fhandler");
					entity.putElementItem("fhandler", new Core(null, NwSupplierHandler.class.getName(),null));
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
		return "nwSupplier";
	}

	public String getType() {
		return "nwSupplier";
	}
	private void adaptLabel(Entigrator entigrator){
		 try{
				Sack entity=entigrator.getEntityAtKey(entityKey$);
				entigrator.ent_assignProperty(entity, "nwSupplier", entityLabel$);
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
	return  NwSupplierHandler.class.getName();
}
public  static void rebuildSuppliers(Entigrator entigrator,Document doc,boolean update){
	if(debug)
	System.out.println("NwSupplierHandler:rebuildSuppliers:BEGIN");
	
	try{
		Sack id2key=entigrator.getEntityAtKey(entigrator.indx_keyAtLabel("id2key"));
		if(id2key==null){
			entigrator.ent_releaseLabel("id2key");
			id2key=entigrator.ent_new("fields", "id2key");
		}
		if(!update){
		String[] sa=entigrator.indx_listEntities("entity", "nwSupplier");
		if(sa!=null){
			Sack supplier;
			for(String s:sa){
				supplier=entigrator.getEntityAtKey(s);
				if(supplier!=null)
				  entigrator.deleteEntity(supplier);
			}
		}
		}
		if(debug)
		System.out.println("NwSupplierHandler:rebuildSuppliers:Root element :" 
	            + doc.getDocumentElement().getNodeName());
		NodeList rList = doc.getElementsByTagName("Suppliers");
		 for (int i = 0; i < rList.getLength(); i++){
			 Node rNode = rList.item(i);
			 NodeList nList = ((Element)rNode).getChildNodes();
			
	         if(debug)
	         System.out.println("----------------------------");
	         Sack nwSupplier;
	         String nwSupplier$;
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
		                 nwSupplier$=entigrator.indx_keyAtLabel(companyName$);
		                 if(nwSupplier$!=null){
		                	 nwSupplier=entigrator.getEntityAtKey(nwSupplier$);
		                     if(nwSupplier!=null){ 
		                    	 entigrator.ent_reindex(nwSupplier);
		                    	 continue;
		                     }
		                }
	                 }
	                 nwSupplier=createSupplier(entigrator, companyName$);
	                 if(nwSupplier==null){
	                	 if(debug)
	                	 System.out.println("NwSupplierHandler:rebuildSuppliers:cannot create category="+companyName$);
	                	 continue;
	                 }
	                 nwSupplier.putElementItem("field", new Core(null,"CompanyName",companyName$));
	               
	                 
	                 String id$=eElement.getAttribute("SupplierID");
	                 if(debug)
	                 System.out.println("SupplierID  : " 
	                    +id$ );
	                 nwSupplier.putElementItem("field", new Core(null,"SupplierID",id$));
	                 
	                 String contactName$=eElement
	    	                    .getElementsByTagName("ContactName")
	    	                    .item(0)
	    	                    .getTextContent();
	                 if(debug)
	                 System.out.println("ContactName : " 
	    	                 + contactName$);
	                 nwSupplier.putElementItem("field", new Core(null,"ContactName",contactName$));
	                 
	                 String contactTitle$=eElement
	    	                    .getElementsByTagName("ContactTitle")
	    	                    .item(0)
	    	                    .getTextContent();
	                 if(debug)
	                 System.out.println("ContactTitle : " 
	    	                 + contactTitle$);
	                 nwSupplier.putElementItem("field", new Core(null,"ContactTitle",contactTitle$));
	              
	                 String address$=eElement
	    	                    .getElementsByTagName("Address")
	    	                    .item(0)
	    	                    .getTextContent();
	                 if(debug)
	                 System.out.println("Address : " 
	    	                 + address$);
	                 nwSupplier.putElementItem("field", new Core(null,"Address",address$));
	                 
	                 String city$=eElement
	    	                    .getElementsByTagName("City")
	    	                    .item(0)
	    	                    .getTextContent();
	                 if(debug)
	                 System.out.println("City : " 
	    	                 + city$);
	                 nwSupplier.putElementItem("field", new Core(null,"City",city$));
	                 
	                 String region$=eElement
	    	                    .getElementsByTagName("Region")
	    	                    .item(0)
	    	                    .getTextContent();
	                 if(debug)
	                 System.out.println("Region : " 
	    	                 + region$);
	                 nwSupplier.putElementItem("field", new Core(null,"Region",region$));
	                 
	                 String postalCode$=eElement
	    	                    .getElementsByTagName("PostalCode")
	    	                    .item(0)
	    	                    .getTextContent();
	                 if(debug)
	                 System.out.println("PostalCode : " 
	    	                 + postalCode$);
	                 nwSupplier.putElementItem("field", new Core(null,"PostalCode",postalCode$));
	                 
	                 String country$=eElement
	    	                    .getElementsByTagName("Country")
	    	                    .item(0)
	    	                    .getTextContent();
	                 if(debug)
	                 System.out.println("Country : " 
	    	                 + country$);
	                 nwSupplier.putElementItem("field", new Core(null,"Country",country$));
	                 
	                 String phone$=eElement
	    	                    .getElementsByTagName("Phone")
	    	                    .item(0)
	    	                    .getTextContent();
	                 if(debug)
	                 System.out.println("Phone : " 
	    	                 + country$);
	                 nwSupplier.putElementItem("field", new Core(null,"Phone",phone$));
	                 
	                 String fax$=eElement
	    	                    .getElementsByTagName("Fax")
	    	                    .item(0)
	    	                    .getTextContent();
	                 if(debug)
	                 System.out.println("Fax : " 
	    	                 + fax$);
	                 nwSupplier.putElementItem("field", new Core(null,"Fax",fax$));
	                 
	                 String homePage$=eElement
	    	                    .getElementsByTagName("Fax")
	    	                    .item(0)
	    	                    .getTextContent();
	                 if(debug)
	                 System.out.println("HomePage : " 
	    	                 + homePage$);
	                 nwSupplier.putElementItem("field", new Core(null,"HomePage",homePage$));
	              
	              entigrator.ent_alter(nwSupplier);
	              nwSupplier=entigrator.ent_assignProperty(nwSupplier, "nwSupplier", companyName$);
	              nwSupplier=entigrator.ent_assignProperty(nwSupplier, "fields",companyName$);
	              entigrator.ent_reindex(nwSupplier);
	              if(!id2key.existsElement("nwSupplier"))
	            	  id2key.createElement("nwSupplier");
	              id2key.putElementItem("nwSupplier", new Core(null,id$,nwSupplier.getKey()));
	      
	             }} }
		 entigrator.ent_alter(id2key);
	}catch(Exception e){
		Logger.getLogger(NwSupplierHandler.class.getName()).severe(e.toString());
	}
	
}
private  static Sack createSupplier(Entigrator entigrator,String label$ ){
	if(debug)
	System.out.println("NwSupplierHandler:createSupplier:"+label$);
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
	Sack newEntity=entigrator.ent_new("nwSupplier", label$);
	newEntity.createElement("field");
	newEntity.createElement("fhandler");
	newEntity.putElementItem("fhandler", new Core(null,NwSupplierHandler.class.getName(),null));
	newEntity.putElementItem("fhandler", new Core(null,FieldsHandler.class.getName(),null));
	newEntity.createElement("jfacet");
	newEntity.putElementItem("jfacet", new Core("gdt.jgui.entity.fields.JFieldsFacetAddItem",FieldsHandler.class.getName(),"gdt.jgui.entity.fields.JFieldsFacetOpenItem"));
	newEntity.putElementItem("jfacet", new Core("gdt.jgui.entity.nwsupplier.JNwSupplierFacetAddItem",NwSupplierHandler.class.getName(),"gdt.jgui.entity.nwsupplier.JNwSupplierFacetOpenItem"));
	
	return newEntity;

	}catch(Exception e){
		Logger.getLogger(NwSupplierHandler.class.getName()).severe(e.toString());
		
	}
return null;
}
@Override
public String getLocation() {
	return EXTENSION_KEY;
}
}