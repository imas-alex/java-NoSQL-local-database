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
public class NwEmployeeHandler extends FieldsHandler{
	public static final String EXTENSION_KEY="_ebrysyRG3lk1CYHiugIoSapofvo";	
private Logger LOGGER=Logger.getLogger(NwEmployeeHandler.class.getName());
String entihome$;
String entityKey$;
static boolean debug=true;
public final static String EMPLOYEE="nwEmployee";


public NwEmployeeHandler(){
	super();
}
	@Override
	public boolean isApplied(Entigrator entigrator, String locator$) {
		try{
		if(debug)	
			System.out.println("NwEmployeeHandler:isApplied:locator="+locator$);
			Properties locator=Locator.toProperties(locator$);
			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			boolean result=false;
			Sack entity=entigrator.getEntityAtKey(entityKey$);
		if(debug)	
			System.out.println("NwEmployeeHandler:isApplied:entity="+entity.getProperty("label"));
			String category$=entity.getProperty("nwEmployee");
			if(category$!=null&&!Locator.LOCATOR_FALSE.equals(category$)){
			   if(entity.getElementItem("fhandler", NwEmployeeHandler.class.getName())==null){	
					if(!entity.existsElement("fhandler"))
						entity.createElement("fhandler");
					entity.putElementItem("fhandler", new Core(null, NwEmployeeHandler.class.getName(),null));
					entigrator.replace(entity);
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
		return "nwEmployee";
	}

	public String getType() {
		return "nwEmployee";
	}
	private void adaptLabel(Entigrator entigrator){
		 try{
				Sack entity=entigrator.getEntityAtKey(entityKey$);
				entigrator.ent_assignProperty(entity, "nwEmployee", entityLabel$);
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
	return  NwEmployeeHandler.class.getName();
}
public  static void rebuildEmployees(Entigrator entigrator,Document doc,boolean update){
	if(debug)
	System.out.println("NwEmployeeHandler:rebuildEmployees:BEGIN");
	try{
		Sack id2key=entigrator.getEntityAtKey(entigrator.indx_keyAtLabel("id2key"));
		if(id2key==null){
			entigrator.ent_releaseLabel("id2key");
			id2key=entigrator.ent_new("fields", "id2key");
		}
		if(!update){
		String[] sa=entigrator.indx_listEntities("entity", "nwEmployee");
		if(sa!=null){
			Sack employee;
			for(String s:sa){
				try{
				employee=entigrator.getEntityAtKey(s);
				
				if(employee!=null) {
						 NwSourceHandler.removeIcon(entigrator,employee);
	                	 entigrator.deleteEntity(employee);
	                 }
				 // entigrator.deleteEntity(employee);
			}catch(Exception ee){
				
			
		}
		}
		}
		}
		if(debug)
		System.out.println("NwEmployeeHandler:rebuildEmployees:Root element :" 
	            + doc.getDocumentElement().getNodeName());
	         NodeList nList = doc.getElementsByTagName("Employee");
	        if(debug)
	         System.out.println("----------------------------");
	         Sack nwEmployee;
	         String nwEmployee$;
	         for (int temp = 0; temp < nList.getLength(); temp++) {
	        	try{
	        	 Node nNode = nList.item(temp);
	        	if(debug) 
	             System.out.println("\nCurrent Element :" 
	                + nNode.getNodeName());
	             if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	                 Element eElement = (Element) nNode;
	               
	                 String name$=eElement
	    	                    .getElementsByTagName("LastName")
	    	                    .item(0)
	    	                    .getTextContent();
	                 if(debug)
	                 System.out.println("Last name : " 
	    	                 + name$);
	                 if(update){ 
		                 nwEmployee$=entigrator.indx_keyAtLabel(name$);
		                 if(nwEmployee$!=null){
		                	 nwEmployee=entigrator.getEntityAtKey(nwEmployee$);
		                     if(nwEmployee!=null){ 
		                    	 entigrator.ent_reindex(nwEmployee);
		                    	 continue;
		                     }
		                }
	                 }
	                
	                 nwEmployee=createEmployee(entigrator, name$);
	                 if(nwEmployee==null){
	                	if(debug) 
	                	 System.out.println("NwEmployeeHandler:rebuildEmployees:cannot create customer="+name$);
	                	 continue;
	                 }
	                 nwEmployee.putElementItem("field", new Core(null,"LastName",name$));
	                 String id$=eElement.getAttribute("EmployeeID");
	                 if(debug)
	                 System.out.println("Employee ID : " 
	                    +id$ );
	                 nwEmployee.putElementItem("field", new Core(null,"EmployeeID",id$));
	                 
	                 String firstName$=eElement
	 	                    .getElementsByTagName("FirstName")
		                    .item(0)
		                    .getTextContent();
	                 if(firstName$!=null)
	                	 nwEmployee.putElementItem("field", new Core(null,"FirstName",firstName$));
	                 if(debug)
	                   System.out.println("FirstName : " 
	                    +firstName$ );
	                 
	                 String title$=eElement
		 	                    .getElementsByTagName("Title")
			                    .item(0)
			                    .getTextContent();
		                 if(title$!=null)
		                	 nwEmployee.putElementItem("field", new Core(null,"Title",title$));
	                	 if(debug)	 
		                		 System.out.println("Title : "+title$); 
		             String titleOfCourtesy$=eElement
			 	                    .getElementsByTagName("TitleOfCourtesy")
				                    .item(0)
				                    .getTextContent();
			         if(titleOfCourtesy$!=null)
			              	 nwEmployee.putElementItem("field", new Core(null,"TitleOfCourtesy",titleOfCourtesy$));
			         if(debug)
			                 System.out.println("TitleOfCourtesy : " 
			                    +titleOfCourtesy$ );
			                 
			          String birthDate$=eElement
				 	                    .getElementsByTagName("BirthDate")
					                    .item(0)
					                    .getTextContent();
				      if(birthDate$!=null)
				              	 nwEmployee.putElementItem("field", new Core(null,"BirthDate",birthDate$));
				      if(debug)
				                 System.out.println("BirthDate : " 
				                    +birthDate$ );
				      
				      
				     String hireDate$=eElement
					 	                    .getElementsByTagName("HireDate")
						                    .item(0)
						                    .getTextContent();
					  if(hireDate$!=null)
					              	 nwEmployee.putElementItem("field", new Core(null,"HireDate",hireDate$));
					  if(debug)
					                 System.out.println("HireDate : " 
					                    +hireDate$ );
					  
					  String address$=eElement
						 	                    .getElementsByTagName("Address")
							                    .item(0)
							                    .getTextContent();
						  if(address$!=null)
						              	 nwEmployee.putElementItem("field", new Core(null,"Address",address$));
						  if(debug)
						                 System.out.println("Address : " 
						                    +address$ );

				    String city$=eElement
							 	                    .getElementsByTagName("City")
								                    .item(0)
								                    .getTextContent();
							  if(address$!=null)
							              	 nwEmployee.putElementItem("field", new Core(null,"City",city$));
							  if(debug)
							                 System.out.println("City : " 
							                    +city$ );
					 String region$=eElement
				 	                    .getElementsByTagName("Region")
					                    .item(0)
					                    .getTextContent();
				  if(region$!=null)
				              	 nwEmployee.putElementItem("field", new Core(null,"Region",region$));
				  if(debug)
				                 System.out.println("Region : " 
				                    +region$ );
					
					  String postalCode$=eElement
							 	                    .getElementsByTagName("PostalCode")
								                    .item(0)
								                    .getTextContent();
							  if(postalCode$!=null)
							              	 nwEmployee.putElementItem("field", new Core(null,"PostalCode",postalCode$));
							  if(debug)
							                 System.out.println("PostalCode : " 
							                    +postalCode$ );
							                 
					String extension$=eElement
								 	                    .getElementsByTagName("Extension")
									                    .item(0)
									                    .getTextContent();
								  if(extension$!=null)
								              	 nwEmployee.putElementItem("field", new Core(null,"Extension",extension$));
								  if(debug)
								                 System.out.println("Extension : " 
								                    +extension$ );
					
					String icon$=eElement
					                         .getElementsByTagName("Photo")
					                         .item(0)
					                         .getTextContent();
					                 if(debug)
					                 System.out.println("Photo : "+icon$);
					                if(icon$!=null){
					                   String key$=nwEmployee.getKey();	
					                	String fileName$=key$+".png";
					                    NwSourceHandler.saveIcon(entigrator, fileName$, icon$);
					                    nwEmployee.putAttribute(new Core(null,"icon",fileName$));
					                    }			  
								  
					String notes$=eElement
									 	                    .getElementsByTagName("Notes")
										                    .item(0)
										                    .getTextContent();
									  if(notes$!=null)
									              	 nwEmployee.putElementItem("field", new Core(null,"Notes",notes$));
									  if(debug)
									                 System.out.println("Notes : " 
									                    +notes$ );  
									  
					String reportsTo$=eElement
										.getElementsByTagName("ReportsTo")
										.item(0)
										.getTextContent();
										  if(reportsTo$!=null)
										              	 nwEmployee.putElementItem("field", new Core(null,"ReportsTo",reportsTo$));
										  if(debug)
										                 System.out.println("ReportsTo : " 
										                    +reportsTo$ );
		            Node etNode= eElement
							.getElementsByTagName("EmployeeTerritories")
							.item(0); 
		            NodeList tList=((Element)etNode).getElementsByTagName("TerritoryID");
		            System.out.println("tList="+tList.getLength());
		            if( nwEmployee.existsElement("EmployeeTerritorie"))
			    		 nwEmployee.createElement("EmployeeTerritorie");
                     for (int j = 0; j < tList.getLength(); j++) {
		          	        	 String territoryID$ = tList.item(j).getTextContent();
		          	        	if(debug)
							         System.out.println("TerritoryID="+territoryID$);
		          	        	if(territoryID$!=null)
					    	   	 nwEmployee.putElementItem("EmployeeTerritorie", new Core(null,territoryID$,null));
					             }
	              entigrator.replace(nwEmployee);
	              nwEmployee=entigrator.ent_assignProperty(nwEmployee, "nwEmployee", name$);
	              nwEmployee=entigrator.ent_assignProperty(nwEmployee, "fields", name$);
	              entigrator.ent_reindex(nwEmployee);
	              if(!id2key.existsElement("nwEmployee"))
	            	  id2key.createElement("nwEmployee");
	              id2key.putElementItem("nwEmployee", new Core(null,id$,nwEmployee.getKey()));
	      
	             }
	             entigrator.replace(id2key);
	             }catch(Exception eee){
	            	 System.out.println("NwEmployeeHandler:rebuildEmployees:"+eee.toString());
	             }
	         
	         }
	
	}catch(Exception e){
		Logger.getLogger(NwEmployeeHandler.class.getName()).severe(e.toString());
	}
	
}
private  static Sack createEmployee(Entigrator entigrator,String label$ ){
	if(debug)
	System.out.println("NwEmployeeHandler:createEmployee:"+label$);
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
	Sack newEntity=entigrator.ent_new("nwEmployee", label$);
	newEntity.createElement("field");
	newEntity.createElement("fhandler");
	newEntity.putElementItem("fhandler", new Core(null,NwEmployeeHandler.class.getName(),null));
	newEntity.putElementItem("fhandler", new Core(null,FieldsHandler.class.getName(),null));
	newEntity.createElement("jfacet");
	newEntity.putElementItem("jfacet", new Core("gdt.jgui.entity.fields.JFieldsFacetAddItem",FieldsHandler.class.getName(),"gdt.jgui.entity.fields.JFieldsFacetOpenItem"));
	newEntity.putElementItem("jfacet", new Core("gdt.jgui.entity.nwemployee.JNwEmployeeFacetAddItem",NwEmployeeHandler.class.getName(),"gdt.jgui.entity.nwemployee.JNwEmployeeFacetOpenItem"));
	
	return newEntity;

	}catch(Exception e){
		Logger.getLogger(NwEmployeeHandler.class.getName()).severe(e.toString());
		
	}
return null;
}
@Override
public String getLocation() {
	return EXTENSION_KEY;
}
}