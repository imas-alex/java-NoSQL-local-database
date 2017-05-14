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
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.data.entity.facet.FieldsHandler;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
public class NwCustomerHandler extends FieldsHandler{
	public static final String EXTENSION_KEY="_ebrysyRG3lk1CYHiugIoSapofvo";	
private Logger LOGGER=Logger.getLogger(NwCustomerHandler.class.getName());
String entihome$;
String entityKey$;
static boolean debug=true;
public final static String CUSTOMER="nwCustomer";

public NwCustomerHandler(){
	super();
}
	@Override
	public boolean isApplied(Entigrator entigrator, String locator$) {
		try{
		if(debug)	
			System.out.println("nwCustomerHandler:isApplied:locator="+locator$);
			Properties locator=Locator.toProperties(locator$);
			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			boolean result=false;
			Sack entity=entigrator.getEntityAtKey(entityKey$);
		if(debug)	
			System.out.println("NwCustomerHandler:isApplied:entity="+entity.getProperty("label"));
			String category$=entity.getProperty("nwCustomer");
			if(category$!=null&&!Locator.LOCATOR_FALSE.equals(category$)){
			   if(entity.getElementItem("fhandler", NwCustomerHandler.class.getName())==null){	
					if(!entity.existsElement("fhandler"))
						entity.createElement("fhandler");
					entity.putElementItem("fhandler", new Core(null, NwCustomerHandler.class.getName(),null));
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
		return "nwCustomer";
	}

	public String getType() {
		return "nwCustomer";
	}
	private void adaptLabel(Entigrator entigrator){
		 try{
				Sack entity=entigrator.getEntityAtKey(entityKey$);
				entigrator.ent_assignProperty(entity, "nwCustomer", entityLabel$);
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
	return  NwCustomerHandler.class.getName();
}
public  static void rebuildCustomers(Entigrator entigrator,Document doc,boolean update){
	if(debug)
	System.out.println("NwCustomerHandler:rebuildCategories:BEGIN");
	Sack id2key=entigrator.getEntityAtKey(entigrator.indx_keyAtLabel("id2key"));
	if(id2key==null){
		entigrator.ent_releaseLabel("id2key");
		id2key=entigrator.ent_new("fields", "id2key");
	}
	try{
		if(!update){
			String[] sa=entigrator.indx_listEntities("entity", "nwCustomer");
			if(sa!=null){
				Sack customer;
				for(String s:sa){
					customer=entigrator.getEntityAtKey(s);
					if(customer!=null)
					  entigrator.deleteEntity(customer);
				}
			}
		}
		if(debug)
		System.out.println("NwCustomerHandler:rebuildCustomers:Root element :" 
	            + doc.getDocumentElement().getNodeName());
	         NodeList nList = doc.getElementsByTagName("Customer");
	        if(debug)
	         System.out.println("----------------------------");
	         Sack nwCustomer=null;
	         String nwCustomer$;
	         for (int temp = 0; temp < nList.getLength(); temp++) {
	        	try{
	        	 Node nNode = nList.item(temp);
	        	if(debug) 
	             System.out.println("\nCurrent Element :" 
	                + nNode.getNodeName());
	             if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	                 Element eElement = (Element) nNode;
	               
	                 String name$=eElement
	    	                    .getElementsByTagName("CompanyName")
	    	                    .item(0)
	    	                    .getTextContent();
	                 if(debug)
	                 System.out.println("Company name : " 
	    	                 + name$);
	                if(update){ 
	                 nwCustomer$=entigrator.indx_keyAtLabel(name$);
	                 if(nwCustomer$!=null){
	                	 nwCustomer=entigrator.getEntityAtKey(nwCustomer$);
	                     if(nwCustomer!=null){ 
	                    	 entigrator.ent_reindex(nwCustomer);
	                    	 continue;
	                     }
	                }
	                }
	                 nwCustomer=createCustomer(entigrator, name$);
	                 if(nwCustomer==null){
	                	if(debug) 
	                	 System.out.println("NwCustomerHandler:rebuildCustomers:cannot create customer="+name$);
	                	 continue;
	                 }
	                 nwCustomer.putElementItem("field", new Core(null,"CompanyName",name$));
	                 String id$=eElement.getAttribute("CustomerID");
	                 if(debug)
	                 System.out.println("Customer ID : " 
	                    +id$ );
	                 nwCustomer.putElementItem("field", new Core(null,"CustomerID",id$));
	                 
	                 String contactName$=eElement
		 	                    .getElementsByTagName("ContactName")
			                    .item(0)
			                    .getTextContent();
		                 if(contactName$!=null)
		                	 nwCustomer.putElementItem("field", new Core(null,"ContactName",contactName$));
		                 if(contactName$!=null)
		                	 if(debug)	 
		                 System.out.println("ContactName : " 
		                    +contactName$ );
		                 
		             String contactTitle$=eElement
			 	                    .getElementsByTagName("ContactTitle")
				                    .item(0)
				                    .getTextContent();
			         if(contactTitle$!=null)
			              	 nwCustomer.putElementItem("field", new Core(null,"ContactTitle",contactTitle$));
			         if(debug)
			                 System.out.println("ContactTitle : " 
			                    +contactTitle$ );
			                 
			          String address$=eElement
				 	                    .getElementsByTagName("Address")
					                    .item(0)
					                    .getTextContent();
				      if(address$!=null)
				              	 nwCustomer.putElementItem("field", new Core(null,"Address",address$));
				      if(debug)
				                 System.out.println("Address : " 
				                    +address$ );
				      
				     String city$=eElement
					 	                    .getElementsByTagName("City")
						                    .item(0)
						                    .getTextContent();
					  if(city$!=null)
					              	 nwCustomer.putElementItem("field", new Core(null,"City",city$));
					  if(debug)
					                 System.out.println("City : " 
					                    +city$ );
					  
					  String region$=eElement
						 	                    .getElementsByTagName("Region")
							                    .item(0)
							                    .getTextContent();
						  if(region$!=null)
						              	 nwCustomer.putElementItem("field", new Core(null,"Region",region$));
						  if(debug)
						                 System.out.println("Region : " 
						                    +region$ );
						
						         String postalCode$=eElement
							 	                    .getElementsByTagName("PostalCode")
								                    .item(0)
								                    .getTextContent();
							  if(postalCode$!=null)
							              	 nwCustomer.putElementItem("field", new Core(null,"PostalCode",postalCode$));
							  if(debug)
							                 System.out.println("PostalCode : " 
							                    +postalCode$ );
							                 
							     String country$=eElement
								 	                    .getElementsByTagName("Country")
									                    .item(0)
									                    .getTextContent();
								  if(country$!=null)
								              	 nwCustomer.putElementItem("field", new Core(null,"Country",country$));
								  if(debug)
								                 System.out.println("PostalCode : " 
								                    +country$ );
								   String phone$=eElement
									 	                    .getElementsByTagName("Phone")
										                    .item(0)
										                    .getTextContent();
									  if(phone$!=null)
									              	 nwCustomer.putElementItem("field", new Core(null,"Phone",phone$));
									  if(debug)
									                 System.out.println("Phone : " 
									                    +phone$ );  
									  String fax$=eElement
										 	                    .getElementsByTagName("Fax")
											                    .item(0)
											                    .getTextContent();
										  if(fax$!=null)
										              	 nwCustomer.putElementItem("field", new Core(null,"Fax",fax$));
										  if(debug)
										                 System.out.println("Fax : " 
										                    +fax$ );  	                 
								
	              entigrator.replace(nwCustomer);
	              nwCustomer=entigrator.ent_assignProperty(nwCustomer, "nwCustormer", name$);
	              nwCustomer=entigrator.ent_assignProperty(nwCustomer, "fields", name$);
	              entigrator.ent_reindex(nwCustomer);
	              if(!id2key.existsElement("nwCustomer"))
	            	  id2key.createElement("nwCustomer");
	              id2key.putElementItem("nwCustomer", new Core(null,id$,nwCustomer.getKey()));
	      
	             }
	             entigrator.replace(id2key);
	             }catch(Exception ee){
	            	 System.out.println("NwCustomerHandler:rebuildCustomers:"+ee.toString());
	             }
	         
	         }
	}catch(Exception e){
		Logger.getLogger(NwCustomerHandler.class.getName()).severe(e.toString());
	}
	
}
private  static Sack createCustomer(Entigrator entigrator,String label$ ){
	if(debug)
	System.out.println("NwCustomerHandler:createCustomer:"+label$);
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
	Sack newEntity=entigrator.ent_new("nwCustomer", label$);
	newEntity.createElement("field");
	newEntity.createElement("fhandler");
	newEntity.putElementItem("fhandler", new Core(null,NwCustomerHandler.class.getName(),null));
	newEntity.putElementItem("fhandler", new Core(null,FieldsHandler.class.getName(),null));
	newEntity.createElement("jfacet");
	newEntity.putElementItem("jfacet", new Core("gdt.jgui.entity.fields.JFieldsFacetAddItem",FieldsHandler.class.getName(),"gdt.jgui.entity.fields.JFieldsFacetOpenItem"));
	newEntity.putElementItem("jfacet", new Core("gdt.jgui.entity.nwcustomer.JNwCustomerFacetAddItem",NwCustomerHandler.class.getName(),"gdt.jgui.entity.nwcustomer.JNwCustomerFacetOpenItem"));
	
	return newEntity;

	}catch(Exception e){
		Logger.getLogger(NwCustomerHandler.class.getName()).severe(e.toString());
		
	}
return null;
}
@Override
public String getLocation() {
	return EXTENSION_KEY;
}
}