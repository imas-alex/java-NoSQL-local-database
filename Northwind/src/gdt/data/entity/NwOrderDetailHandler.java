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
public class NwOrderDetailHandler extends FieldsHandler{
	public static final String EXTENSION_KEY="_ebrysyRG3lk1CYHiugIoSapofvo";	
private Logger LOGGER=Logger.getLogger(NwOrderDetailHandler.class.getName());
String entihome$;
String entityKey$;
static boolean debug=false;
public final static String ORDER_DETAIL="nwOrderDetail";

public NwOrderDetailHandler(){
	super();
}
	@Override
	public boolean isApplied(Entigrator entigrator, String locator$) {
		try{
		if(debug)	
			System.out.println("NwOrderDetailHandler:isApplied:locator="+locator$);
			Properties locator=Locator.toProperties(locator$);
			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			boolean result=false;
			Sack entity=entigrator.getEntityAtKey(entityKey$);
		if(debug)	
			System.out.println("NwOrderDetailHandler:isApplied:entity="+entity.getProperty("label"));
			String category$=entity.getProperty("nwOrderDetail");
			if(category$!=null&&!Locator.LOCATOR_FALSE.equals(category$)){
			   if(entity.getElementItem("fhandler", NwOrderDetailHandler.class.getName())==null){	
					if(!entity.existsElement("fhandler"))
						entity.createElement("fhandler");
					entity.putElementItem("fhandler", new Core(null, NwOrderDetailHandler.class.getName(),null));
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
		return "nwOrderDetail";
	}

	public String getType() {
		return "nwOrderDetail";
	}
	private void adaptLabel(Entigrator entigrator){
		 try{
				Sack entity=entigrator.getEntityAtKey(entityKey$);
				entigrator.ent_assignProperty(entity, "nwOrderDetail", entityLabel$);
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
	return  NwOrderDetailHandler.class.getName();
}
public  static void rebuildOrderDetails(Entigrator entigrator,Document doc,boolean update){
	if(debug){
	System.out.println("NwOrderDetailHandler:rebuildOrders:BEGIN");
	//return;
	}
	int n=0;
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
			}
		
		
		//
		
		
		
		
		
		if(debug)
		System.out.println("NwOrderDetailHandler:rebuildOrderDetails:Root element :" 
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
	             if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	                 Element eElement = (Element) nNode;
	                 String id$=eElement.getAttribute("OrderID");
	                // if(debug)
	                // System.out.println("=Order ID : " 
	                //    +id$ );
	                 id=Integer.parseInt(id$);
	               
	                 nwOrder=entigrator.getEntityAtKey(entigrator.indx_keyAtLabel("o-"+id$));
	                 if(nwOrder==null){
	                	 if(debug)
	                			System.out.println("NwOrderDetailHandler:rebuildOrderDetails:cannot find order="+"o-"+id$);
	                	 continue;
	                		    
	                 }
	             
					Node odNode= eElement
							.getElementsByTagName("OrderDetails")
							.item(0); 
		            NodeList dList=((Element)odNode).getElementsByTagName("OrderDetail");
		            System.out.println("dList="+dList.getLength());
		            if(dList.getLength()>0){
		            	Element dElement;
		            String   nwOrderDetail$;	
		            String productID$;
                     for (int j = 0; j < dList.getLength(); j++) {
                    	 n++;
                    	 System.out.println("NwOrderDetailHandler:rebuildOrderDetails:n="+n);
                    	         dElement=(Element)dList.item(j);
		          	        	 productID$ = dElement.getElementsByTagName("ProductID").item(0).getTextContent();
		          	        	nwOrderDetail=entigrator.getEntityAtKey(entigrator.indx_keyAtLabel("o-"+id$+"-p-"+productID$));
		    	                 if(update&&nwOrderDetail!=null) {
		    		                    	 entigrator.ent_reindex(nwOrderDetail);
		    		                    	 continue;
		    	                 }
		          	        	 nwOrderDetail=createOrderDetail(entigrator, "o-"+id$+"-p-"+productID$, nwOrder);
		          	        	 nwOrderDetail.putElementItem("field", new Core(null,"OrderID",id$));
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
		          	        
		          	        	entigrator.ent_alter(nwOrderDetail);
					             
	              entigrator.ent_alter(nwOrderDetail);
	              entigrator.ent_reindex(nwOrderDetail);
	              if(!id2key.existsElement("nwOrderDetail"))
	            	  id2key.createElement("nwOrderDetail");
	              id2key.putElementItem("nwOrderDetail", new Core(null,"o-"+id$+"-p-"+productID$,nwOrder.getKey()));
                     }
	             }
	             }
	             System.out.println("NwOrderHandler:rebuildOrderDetails:n="+n);
	             entigrator.ent_alter(id2key);
	             }catch(Exception eee){
	            	 System.out.println("NwOrderHandler:rebuildOrderDetails:"+eee.toString());
	             }
	         
	         }
	}catch(Exception e){
		Logger.getLogger(NwOrderDetailHandler.class.getName()).severe(e.toString());
	}
	
}
private  static Sack createOrderDetail(Entigrator entigrator,String label$,Sack nwOrder ){
	if(debug)
	System.out.println("NwOrderHandler:createOrderDetail:"+label$);
	if(label$==null||entigrator==null)
		return null;
	try{

		String oldKey$=entigrator.indx_keyAtLabel(label$);
	if(oldKey$!=null){
		Sack old=entigrator.getEntityAtKey(oldKey$);
		if(old!=null){
			
		entigrator.deleteEntity(old);
		}
		
	}
	Sack newEntity=entigrator.ent_new("nwOrderDetail", label$);
	newEntity.createElement("field");
	newEntity.createElement("fhandler");
	newEntity.putElementItem("fhandler", new Core(null,FieldsHandler.class.getName(),null));
	newEntity.createElement("jfacet");
	newEntity.putElementItem("jfacet", new Core("gdt.jgui.entity.fields.JFieldsFacetAddItem",FieldsHandler.class.getName(),"gdt.jgui.entity.fields.JFieldsFacetOpenItem"));
	entigrator.ent_alter(newEntity);
	newEntity=entigrator.ent_assignProperty(newEntity, "nwOrderDetail",label$);
    newEntity=entigrator.ent_assignProperty(newEntity, "fields", label$);
	entigrator.col_addComponent(nwOrder,newEntity);
	//return entigrator.getEntity(newEntity.getKey());
return newEntity;
	}catch(Exception e){
		Logger.getLogger(NwOrderDetailHandler.class.getName()).severe(e.toString());
		
	}
return null;
}
@Override
public String getLocation() {
	return EXTENSION_KEY;
}
public  static void repairOrderDetails(Entigrator entigrator){
	if(debug){
	System.out.println("NwOrderDetailHandler:repairOrderDetail:BEGIN");
	//return;
	}
	Document doc=NwSourceHandler.getSourceDocument(entigrator);
	int n=0;
	try{
		Sack id2key=entigrator.getEntityAtKey(entigrator.indx_keyAtLabel("id2key"));
		if(id2key==null){
			entigrator.ent_releaseLabel("id2key");
			id2key=entigrator.ent_new("fields", "id2key");
		}
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
		
		
		if(debug)
		System.out.println("NwOrderDetailHandler:repairOrderDetails:Root element :" 
	            + doc.getDocumentElement().getNodeName());
	         NodeList nList = doc.getElementsByTagName("Order");
	        if(debug)
	         System.out.println("----------------------------");
	         Sack nwOrder;
	         Sack nwOrderDetail;
	         String nwOrder$;
	         int id=0;
	         int num=0;
	         for (int temp = 0; temp < nList.getLength(); temp++) {
	        	try{
	        	 Node nNode = nList.item(temp);
	             if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	                 Element eElement = (Element) nNode;
	                 String id$=eElement.getAttribute("OrderID");
	                 id=Integer.parseInt(id$);
	               
	                 nwOrder=entigrator.getEntityAtKey(entigrator.indx_keyAtLabel("o-"+id$));
	                 if(nwOrder==null){
	                	 if(debug)
	                			System.out.println("NwOrderDetailHandler:repairOrderDetails:cannot find order="+"o-"+id$);
	                	 continue;
	                		    
	                 }
	             
					Node odNode= eElement
							.getElementsByTagName("OrderDetails")
							.item(0); 
		            NodeList dList=((Element)odNode).getElementsByTagName("OrderDetail");
		           // System.out.println("dList="+dList.getLength());
		            if(dList.getLength()>0){
		            	Element dElement;
		            String   nwOrderDetail$;	
		            String productID$;
                     for (int j = 0; j < dList.getLength(); j++) {
                    	 n++;
                    	//if(debug)
                    	// System.out.println("NwOrderDetailHandler:repairOrderDetails:n="+n);
                    	         dElement=(Element)dList.item(j);
		          	        	 productID$ = dElement.getElementsByTagName("ProductID").item(0).getTextContent();
		          	        	
		          	        	 nwOrderDetail=entigrator.getEntityAtKey(entigrator.indx_keyAtLabel("o-"+id$+"-p-"+productID$+"-"+String.valueOf(j)));
		    		       
		    		                 if(nwOrderDetail!=null){
		    		                	 if(debug)
			    	                    	 System.out.println("NwOrderDetailHandler:repairOrderDetails:already exists  detail=o-"+id$+"-p-"+productID$+"-"+String.valueOf(j));
			    	                	 
		    		                	 //entigrator.ent_reindex(nwOrderDetail);
		    		                    	 continue;
		    		                     }
		    		                

		    	                 if(debug)
		    	                    	 System.out.println("NwOrderDetailHandler:repairOrderDetails:cannot find detail=o-"+id$+"-p-"+productID$);
		    	                
		          	        	 nwOrderDetail=createOrderDetail(entigrator, "o-"+id$+"-p-"+productID$+"-"+String.valueOf(j), nwOrder);
		          	        	 if(debug)
							         System.out.println("ProductID="+productID$);
		          	        	if(productID$!=null)
					    	   	   nwOrderDetail.putElementItem("field", new Core(null,"ProductID",productID$));
				    	   	   nwOrderDetail.putElementItem("field", new Core(null,"OrderID",id$));
			          	        	
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
		          	        
		          	        	entigrator.ent_alter(nwOrderDetail);
					             
	              entigrator.ent_alter(nwOrderDetail);
	              entigrator.ent_reindex(nwOrderDetail);
	              if(!id2key.existsElement("nwOrderDetail"))
	            	  id2key.createElement("nwOrderDetail");
	              id2key.putElementItem("nwOrderDetail", new Core(null,"o-"+id$+"-p-"+productID$,nwOrder.getKey()));
	              
                     }
	             }
	             }
	            // System.out.println("NwOrderHandler:repairOrderDetails:n="+n);
	             
	             }catch(Exception eee){
	            	 System.out.println("NwOrderHandler:repairOrderDetails:"+eee.toString());
	             }
	        	entigrator.ent_alter(id2key);
	         }
	}catch(Exception e){
		Logger.getLogger(NwOrderDetailHandler.class.getName()).severe(e.toString());
	}
	
}

}