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
public class NwOrderHandler extends FieldsHandler{
	public static final String EXTENSION_KEY="_ebrysyRG3lk1CYHiugIoSapofvo";	
private Logger LOGGER=Logger.getLogger(NwOrderHandler.class.getName());
String entihome$;
String entityKey$;
static boolean debug=true;
public final static String ORDER="nwOrder";

public NwOrderHandler(){
	super();
}
	@Override
	public boolean isApplied(Entigrator entigrator, String locator$) {
		try{
		if(debug)	
			System.out.println("NwOrderHandler:isApplied:locator="+locator$);
			Properties locator=Locator.toProperties(locator$);
			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			boolean result=false;
			Sack entity=entigrator.getEntityAtKey(entityKey$);
		if(debug)	
			System.out.println("NwOrderHandler:isApplied:entity="+entity.getProperty("label"));
			String category$=entity.getProperty("nwOrder");
			if(category$!=null&&!Locator.LOCATOR_FALSE.equals(category$)){
			   if(entity.getElementItem("fhandler", NwOrderHandler.class.getName())==null){	
					if(!entity.existsElement("fhandler"))
						entity.createElement("fhandler");
					entity.putElementItem("fhandler", new Core(null, NwOrderHandler.class.getName(),null));
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
		return "nwOrder";
	}

	public String getType() {
		return "nwOrder";
	}
	private void adaptLabel(Entigrator entigrator){
		 try{
				Sack entity=entigrator.getEntityAtKey(entityKey$);
				entigrator.ent_assignProperty(entity, "nwOrder", entityLabel$);
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
	return  NwOrderHandler.class.getName();
}
public  static void rebuildOrders(Entigrator entigrator,Document doc,boolean update){
	if(debug){
	System.out.println("NwOrderHandler:rebuildOrders:BEGIN");
	//return;
	}
	
	try{
		Sack id2key=entigrator.getEntityAtKey(entigrator.indx_keyAtLabel("id2key"));
		if(id2key==null){
			entigrator.ent_releaseLabel("id2key");
			id2key=entigrator.ent_new("fields", "id2key");
		}
		if(!update){
		String[] sa=entigrator.indx_listEntities("entity", "nwOrderDetail");
		if(sa!=null){
			if(debug)
				System.out.println("NwOrderHandler:rebuildOrders:delete details="+sa.length);
			Sack entity;
			for(String s:sa){
				entity=entigrator.getEntityAtKey(s);
				if(entity!=null)
					entigrator.deleteEntity(entity);
			}
		}
		
		sa=entigrator.indx_listEntities("entity", "nwOrder");
		if(sa!=null){
			if(debug)
				System.out.println("NwOrderHandler:rebuildOrders:delete orders="+sa.length);
			Sack entity;
			for(String s:sa){
				entity=entigrator.getEntityAtKey(s);
				if(entity!=null)
					entigrator.deleteEntity(entity);
			}
		}
		
		}
		
		
		//
		
		
		
		
		
		if(debug)
		System.out.println("NwOrderHandler:rebuildOrders:Root element :" 
	            + doc.getDocumentElement().getNodeName());
	         NodeList nList = doc.getElementsByTagName("Order");
	        if(debug)
	         System.out.println("----------------------------");
	         Sack nwOrder;
	         Sack nwOrderDetail;
	         String nwOrder$;
	         int id=0;
	         for (int temp = 0; temp < nList.getLength(); temp++) {
	        	try{
	        	 Node nNode = nList.item(temp);
	        	//if(debug) 
	            // System.out.println("\nCurrent Element :" 
	             //   + nNode.getNodeName());
	             if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	                 Element eElement = (Element) nNode;
	                 String id$=eElement.getAttribute("OrderID");
	                // if(debug)
	                // System.out.println("=Order ID : " 
	                //    +id$ );
	                 id=Integer.parseInt(id$);
	               
	                 nwOrder=entigrator.getEntityAtKey(entigrator.indx_keyAtLabel("o-"+id$));
	                 if(nwOrder!=null){
	                	 NwSourceHandler.removeIcon(entigrator,nwOrder);
	                	 entigrator.deleteEntity(nwOrder);
	                 }else{
	                	 entigrator.ent_releaseKey(entigrator.indx_keyAtLabel("o-"+id$));
	                 }
	                 if(update){ 
		                 nwOrder$=entigrator.indx_keyAtLabel("o-"+id$);
		                 if(nwOrder$!=null){
		                	 nwOrder=entigrator.getEntityAtKey("o-"+id$);
		                     if(nwOrder!=null){ 
		                    	 entigrator.ent_reindex(nwOrder);
		                    	 continue;
		                     }
		                }
	                 }
	                nwOrder=createOrder(entigrator, "o-"+id$);
	                 if(nwOrder==null){
	                	if(debug) 
	                	 System.out.println("NwOrderHandler:rebuildOrders:cannot create order="+id$);
	                	 continue;
	                 }
	                 nwOrder.putElementItem("field", new Core(null,"OrderID",id$));
	            
	                 String customerID$=eElement
	    	                    .getElementsByTagName("CustomerID")
	    	                    .item(0)
	    	                    .getTextContent();
	                 if(debug)
	                 System.out.println("CustomerID : " 
	    	                 + customerID$);
	                 nwOrder.putElementItem("field", new Core(null,"CustomerID",customerID$));
	                   
	                 String employeeID$=eElement
	 	                    .getElementsByTagName("EmployeeID")
		                    .item(0)
		                    .getTextContent();
	                 if(employeeID$!=null)
	                	 nwOrder.putElementItem("field", new Core(null,"EmployeeID",employeeID$));
	                 if(debug)
	                   System.out.println("EmployeeID : " 
	                    +employeeID$ );
	                 
	                 String orderDate$=eElement
		 	                    .getElementsByTagName("OrderDate")
			                    .item(0)
			                    .getTextContent();
		                 if(orderDate$!=null)
		                	 nwOrder.putElementItem("field", new Core(null,"OrderDate",orderDate$));
	                	 if(debug)	 
		                		 System.out.println("OrderDate : "+orderDate$);
	                	 
		             String requiredDate$=eElement
			 	                    .getElementsByTagName("RequiredDate")
				                    .item(0)
				                    .getTextContent();
			         if(requiredDate$!=null)
			              	 nwOrder.putElementItem("field", new Core(null,"RequiredDate",requiredDate$));
			         if(debug)
			                 System.out.println("RequiredDate : " 
			                    +requiredDate$ );
			         try{        
			          String shippedDate$=eElement
				 	                    .getElementsByTagName("ShippedDate")
					                    .item(0)
					                    .getTextContent();
				      if(shippedDate$!=null)
				              	 nwOrder.putElementItem("field", new Core(null,"ShippedDate",shippedDate$));
				      if(debug)
				                 System.out.println("ShippedDate : " 
				                    +shippedDate$ );
			         }catch(Exception se){
			        	 System.out.println("ShippedDate : "+se.toString()); 
			         }
				      
				     String shipVia$=eElement
					 	                    .getElementsByTagName("ShipVia")
						                    .item(0)
						                    .getTextContent();
					  if(shipVia$!=null)
					              	 nwOrder.putElementItem("field", new Core(null,"ShipVia",shipVia$));
					  if(debug)
					                 System.out.println("ShipVia : " 
					                    +shipVia$ );
					  
					  String freight$=eElement
						 	                    .getElementsByTagName("Freight")
							                    .item(0)
							                    .getTextContent();
						  if(freight$!=null)
						              	 nwOrder.putElementItem("field", new Core(null,"Freight",freight$));
						  if(debug)
						                 System.out.println("Freight : " 
						                    +freight$ );

				    String shipName$=eElement
							 	                    .getElementsByTagName("ShipName")
								                    .item(0)
								                    .getTextContent();
							  if(shipName$!=null)
							              	 nwOrder.putElementItem("field", new Core(null,"ShipName",shipName$));
							  if(debug)
							                 System.out.println("ShipName : " 
							                    +shipName$ );
					 String shipAddress$=eElement
				 	                    .getElementsByTagName("ShipAddress")
					                    .item(0)
					                    .getTextContent();
				  if(shipAddress$!=null)
				              	 nwOrder.putElementItem("field", new Core(null,"ShipAddress",shipAddress$));
				  if(debug)
				                 System.out.println("ShipAddress : " 
				                    +shipAddress$ );
					
				  String shipCity$=eElement
	 	                    .getElementsByTagName("ShipCity")
		                    .item(0)
		                    .getTextContent();
	  if(shipCity$!=null)
	              	 nwOrder.putElementItem("field", new Core(null,"ShipCity",shipCity$));
	  if(debug)
	                 System.out.println("ShipCity : " 
	                    +shipCity$ );
	  String shipRegion$=eElement
               .getElementsByTagName("ShipRegion")
              .item(0)
              .getTextContent();
if(shipRegion$!=null)
    	 nwOrder.putElementItem("field", new Core(null,"ShipRegion",shipRegion$));
if(debug)
       System.out.println("ShipRegion : " 
          +shipRegion$ );

	  
				 String shipPostalCode$=eElement
							 	                    .getElementsByTagName("ShipPostalCode")
								                    .item(0)
								                    .getTextContent();
							  if(shipPostalCode$!=null)
							              	 nwOrder.putElementItem("field", new Core(null,"ShipPostalCode",shipPostalCode$));
							  if(debug)
							                 System.out.println("ShipPostalCode : " 
							                    +shipPostalCode$ );
							                 
				String shipCountry$=eElement
								 	                    .getElementsByTagName("ShipCountry")
									                    .item(0)
									                    .getTextContent();
								  if(shipCountry$!=null)
								              	 nwOrder.putElementItem("field", new Core(null,"ShipCountry",shipCountry$));
								  if(debug)
								                 System.out.println("ShipCountry : " 
								                    +shipCountry$ );
					
					Node odNode= eElement
							.getElementsByTagName("OrderDetails")
							.item(0); 
		            NodeList dList=((Element)odNode).getElementsByTagName("OrderDetail");
		            System.out.println("dList="+dList.getLength());
		            if(dList.getLength()>0){
		            	
		            	Element dElement;
                     for (int j = 0; j < dList.getLength(); j++) {
                    	         dElement=(Element)dList.item(j);
		          	        	 String productID$ = dElement.getElementsByTagName("ProductID").item(0).getTextContent();
		          	        	 nwOrderDetail=createOrderDetail(entigrator, "o-"+id$+"-p-"+productID$, nwOrder);
		          	        	 if(debug)
							         System.out.println("ProductID="+productID$);
		          	        	if(productID$!=null)
					    	   	   nwOrderDetail.putElementItem("field", new Core(null,"ProductID",productID$));
		          	        	
		          	        	 String unitPrice$ = dElement.getElementsByTagName("UnitPrice").item(0).getTextContent();
		          	        	 
		          	        	 if(debug)
							         System.out.println("UnitPrice="+unitPrice$);
		          	        	if(productID$!=null)
					    	   	   nwOrderDetail.putElementItem("field", new Core(null,"UnitPrice",unitPrice$));
		          	        
		          	        	String quantity$ = dElement.getElementsByTagName("Quantity").item(0).getTextContent();
		          	        	 
		          	        	 if(debug)
							         System.out.println("Quantity="+quantity$);
		          	        	if(productID$!=null)
					    	   	   nwOrderDetail.putElementItem("field", new Core(null,"Quantity",quantity$));
		          	        
		          	        	String discount$ = dElement.getElementsByTagName("Discount").item(0).getTextContent();
		          	        	 
		          	        	 if(debug)
							         System.out.println("Discount="+discount$);
		          	        	if(productID$!=null)
					    	   	   nwOrderDetail.putElementItem("field", new Core(null,"Discount",discount$));
		          	        
		          	        	entigrator.replace(nwOrderDetail);
					             }
	              entigrator.replace(nwOrder);
	              entigrator.ent_reindex(nwOrder);
	              if(!id2key.existsElement("nwOrder"))
	            	  id2key.createElement("nwOrder");
	              id2key.putElementItem("nwOrder", new Core(null,id$,nwOrder.getKey()));
	    
	             }
	             }
	             entigrator.replace(id2key);
	             }catch(Exception eee){
	            	 System.out.println("NwOrderHandler:rebuildOrders:"+eee.toString());
	             }
	         
	         }
	}catch(Exception e){
		Logger.getLogger(NwOrderHandler.class.getName()).severe(e.toString());
	}
	
}
private  static Sack createOrder(Entigrator entigrator,String label$ ){
	if(debug)
	System.out.println("NwOrderHandler:createOrder:"+label$);
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
	Sack newEntity=entigrator.ent_new("nwOrder", label$);
	newEntity.createElement("field");
	newEntity.createElement("fhandler");
	newEntity.putElementItem("fhandler", new Core(null,NwOrderHandler.class.getName(),null));
	newEntity.putElementItem("fhandler", new Core(null,FieldsHandler.class.getName(),null));
	newEntity.createElement("jfacet");
	newEntity.putElementItem("jfacet", new Core("gdt.jgui.entity.fields.JFieldsFacetAddItem",FieldsHandler.class.getName(),"gdt.jgui.entity.fields.JFieldsFacetOpenItem"));
	newEntity.putElementItem("jfacet", new Core("gdt.jgui.entity.nworder.JNwOrderFacetAddItem",NwOrderHandler.class.getName(),"gdt.jgui.entity.nweorder.JNwOrderFacetOpenItem"));
	entigrator.replace(newEntity);
	newEntity=entigrator.ent_assignProperty(newEntity, "nwOrder",label$);
    newEntity=entigrator.ent_assignProperty(newEntity, "fields", label$);
	return newEntity;

	}catch(Exception e){
		Logger.getLogger(NwOrderHandler.class.getName()).severe(e.toString());
		
	}
return null;
}
private  static Sack createOrderDetail(Entigrator entigrator,String label$,Sack nwOrder ){
	if(debug)
	System.out.println("NwOrderHandler:createOrder:"+label$);
	if(label$==null||entigrator==null)
		return null;
	try{

		String oldKey$=entigrator.indx_keyAtLabel(label$);
	if(oldKey$!=null){
		Sack old=entigrator.getEntityAtKey(oldKey$);
		
		if(old!=null){
			/*
			String icon$=old.getAttributeAt("icon");
			if(icon$!=null){
				File icon=new File(entigrator.getEntihome()+"/"+Entigrator.ICONS+"/"+icon$);
				if(icon!=null)
					icon.delete();
			}
			*/
		entigrator.deleteEntity(old);
		}
		
	}
	Sack newEntity=entigrator.ent_new("nwOrderDetail", label$);
	newEntity.createElement("field");
	newEntity.createElement("fhandler");
	//newEntity.putElementItem("fhandler", new Core(null,NwOrderHandler.class.getName(),null));
	newEntity.putElementItem("fhandler", new Core(null,FieldsHandler.class.getName(),null));
	newEntity.createElement("jfacet");
	newEntity.putElementItem("jfacet", new Core("gdt.jgui.entity.fields.JFieldsFacetAddItem",FieldsHandler.class.getName(),"gdt.jgui.entity.fields.JFieldsFacetOpenItem"));
	//newEntity.putElementItem("jfacet", new Core("gdt.jgui.entity.nw.JNwEmployeeFacetAddItem",NwOrderHandler.class.getName(),"gdt.jgui.entity.nwemployee.JNwEmployeeFacetOpenItem"));
	entigrator.replace(newEntity);
	newEntity=entigrator.ent_assignProperty(newEntity, "nwOrderDetail",label$);
    newEntity=entigrator.ent_assignProperty(newEntity, "fields", label$);
	entigrator.col_addComponent(nwOrder,newEntity);
	//return entigrator.getEntity(newEntity.getKey());
return newEntity;
	}catch(Exception e){
		Logger.getLogger(NwOrderHandler.class.getName()).severe(e.toString());
		
	}
return null;
}
@Override
public String getLocation() {
	return EXTENSION_KEY;
}
}