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
public class NwProductHandler extends FieldsHandler{
	public static final String EXTENSION_KEY="_ebrysyRG3lk1CYHiugIoSapofvo";	
private Logger LOGGER=Logger.getLogger(NwProductHandler.class.getName());
String entihome$;
String entityKey$;
static boolean debug=true;
public final static String PRODUCT="nwProduct";

public NwProductHandler(){
	super();
}
	@Override
	public boolean isApplied(Entigrator entigrator, String locator$) {
		try{
		if(debug)	
			System.out.println("NwProductHandler:isApplied:locator="+locator$);
			Properties locator=Locator.toProperties(locator$);
			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			boolean result=false;
			Sack entity=entigrator.getEntityAtKey(entityKey$);
		if(debug)	
			System.out.println("NwProductHandler:isApplied:entity="+entity.getProperty("label"));
			String product$=entity.getProperty("nwProduct");
			if(product$!=null&&!Locator.LOCATOR_FALSE.equals(product$)){
			   if(entity.getElementItem("fhandler", NwProductHandler.class.getName())==null){	
					if(!entity.existsElement("fhandler"))
						entity.createElement("fhandler");
					entity.putElementItem("fhandler", new Core(null, NwProductHandler.class.getName(),null));
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
		return "nwProduct";
	}

	public String getType() {
		return "nwProduct";
	}
	private void adaptLabel(Entigrator entigrator){
		 try{
				Sack entity=entigrator.getEntityAtKey(entityKey$);
				entigrator.ent_assignProperty(entity, "nwProduct", entityLabel$);
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
	return  NwProductHandler.class.getName();
}
public  static void rebuildProducts(Entigrator entigrator,Document doc,boolean update){
	if(debug)
	System.out.println("NwProductHandler:rebuildProducts:BEGIN");
	
	try{
		Sack id2key=entigrator.getEntityAtKey(entigrator.indx_keyAtLabel("id2key"));
		if(id2key==null){
			entigrator.ent_releaseLabel("id2key");
			id2key=entigrator.ent_new("fields", "id2key");
		}
		if(!update){
			String[] sa=entigrator.indx_listEntities("entity", "nwProduct");
		if(sa!=null){
			Sack product;
			for(String s:sa){
				product=entigrator.getEntityAtKey(s);
				if(product!=null)
				  entigrator.deleteEntity(product);
			}
		}
		}
		if(debug)
		System.out.println("NwProductHandler:rebuildProducts:Root element :" 
	            + doc.getDocumentElement().getNodeName());
	         NodeList nList = doc.getElementsByTagName("Product");
	         if(debug)
	         System.out.println("----------------------------");
	         Sack nwProduct;
	         String nwProduct$;
	         for (int temp = 0; temp < nList.getLength(); temp++) {
	        	 Node nNode = nList.item(temp);
	             if(debug)
	        	 System.out.println("\nCurrent Element :" 
	                + nNode.getNodeName());
	             if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	                 Element eElement = (Element) nNode;
	                 String name$=eElement
	    	                    .getElementsByTagName("ProductName")
	    	                    .item(0)
	    	                    .getTextContent();
	                 if(debug)
	                 System.out.println("Product name : " 
	    	                 + name$);
	                 if(update){ 
		                 nwProduct$=entigrator.indx_keyAtLabel(name$);
		                 if(nwProduct$!=null){
		                	 nwProduct=entigrator.getEntityAtKey(nwProduct$);
		                     if(nwProduct!=null){ 
		                    	 entigrator.ent_reindex(nwProduct);
		                    	 continue;
		                     }
		                }
	                 }
	                 nwProduct=createProduct(entigrator, name$);
	                 if(nwProduct==null){
	                	 if(debug)
	                	 System.out.println("NwProductHandler:rebuildProducts:cannot create category="+name$);
	                	 continue;
	                 }
	                 nwProduct.putElementItem("field", new Core(null,"ProductName",name$));
	                 String id$=eElement.getAttribute("ProductID");
	                 if(debug)
	                 System.out.println("Product ID : " 
	                    +id$ );
	                 nwProduct.putElementItem("field", new Core(null,"ProductID",id$));
	                
	                 String supplierID$=eElement
		 	                    .getElementsByTagName("SupplierID")
			                    .item(0)
			                    .getTextContent();
		                 if(supplierID$!=null)
		                	 nwProduct.putElementItem("field", new Core(null,"SupplierID",supplierID$));
		                 if(debug)
		                 System.out.println("SupplierID : " 
		                    +supplierID$ );
		                
	                 String categoryID$=eElement
	 	                    .getElementsByTagName("CategoryID")
		                    .item(0)
		                    .getTextContent();
	                 if(categoryID$!=null)
	                	 nwProduct.putElementItem("field", new Core(null,"CategoryID",categoryID$));
	                 if(debug)
	                 System.out.println("CategoryID : " 
	                    +categoryID$ );
	                 
	                 String quantityPerUnit$=eElement
		 	                    .getElementsByTagName("QuantityPerUnit")
			                    .item(0)
			                    .getTextContent();
		                 if(categoryID$!=null)
		                	 nwProduct.putElementItem("field", new Core(null,"QuantityPerUnit",quantityPerUnit$));
		                 if(debug)
		                 System.out.println("QuantityPerUnit : " 
		                    +quantityPerUnit$ );
	                 
		            String unitPrice$=eElement
			 	                    .getElementsByTagName("UnitPrice")
				                    .item(0)
				                    .getTextContent();
			                 if(categoryID$!=null)
			                	 nwProduct.putElementItem("field", new Core(null,"UnitPrice",unitPrice$));
			                 if(debug)
			                 System.out.println("UnitPrice : " 
			                    +unitPrice$ );
			         String unitsInStock$=eElement
				 	                  .getElementsByTagName("UnitsInStock")
					                  .item(0)
					                  .getTextContent();
				              if(categoryID$!=null)
				              	 nwProduct.putElementItem("field", new Core(null,"UnitsInStock",unitsInStock$));
				              if(debug)
				                 System.out.println("UnitsInStock : " 
				                    +unitsInStock$ );     
				      String unitsOnOrder$=eElement
				 	            .getElementsByTagName("UnitsOnOrder")
					             .item(0)
					             .getTextContent();
				          if(categoryID$!=null)
				              	 nwProduct.putElementItem("field", new Core(null,"UnitsOnOrder",unitsOnOrder$));
				          if(debug)
				                 System.out.println("UnitsOnOrder : " 
				                    +unitsOnOrder$ ); 
				      String reorderLevel$=eElement
					            .getElementsByTagName("ReorderLevel")
					             .item(0)
					             .getTextContent();
					       if(categoryID$!=null)
					              	 nwProduct.putElementItem("field", new Core(null,"ReorderLevel",unitsOnOrder$));
					        if(debug)
					                 System.out.println("ReorderLevel : " 
					                    +reorderLevel$ ); 
					  String discontinued$=eElement
						         .getElementsByTagName("Discontinued")
						         .item(0)
						         .getTextContent();
						    if(categoryID$!=null)
						       	 nwProduct.putElementItem("field", new Core(null,"Discontinued",discontinued$));
						    if(debug)
						          System.out.println("Discontinued : " 
						               +discontinued$ );   
	              entigrator.replace(nwProduct);
	              nwProduct=entigrator.ent_assignProperty(nwProduct, "nwProduct", name$);
	              nwProduct=entigrator.ent_assignProperty(nwProduct, "fields", name$);
	              entigrator.ent_reindex(nwProduct);
	              if(!id2key.existsElement("nwProduct"))
	            	  id2key.createElement("nwProduct");
	              id2key.putElementItem("nwProduct", new Core(null,id$,nwProduct.getKey()));
	      
	         }
	         }
	         entigrator.replace(id2key);
	}catch(Exception e){
		Logger.getLogger(NwProductHandler.class.getName()).severe(e.toString());
	}
	
}
private  static Sack createProduct(Entigrator entigrator,String label$ ){
	if(debug)
	System.out.println("NwProductHandler:createCategory:"+label$);
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
	Sack newEntity=entigrator.ent_new("nwProduct", label$);
	newEntity.createElement("field");
	newEntity.createElement("fhandler");
	newEntity.putElementItem("fhandler", new Core(null,NwProductHandler.class.getName(),null));
	newEntity.putElementItem("fhandler", new Core(null,FieldsHandler.class.getName(),null));
	newEntity.createElement("jfacet");
	newEntity.putElementItem("jfacet", new Core("gdt.jgui.entity.fields.JFieldsFacetAddItem",FieldsHandler.class.getName(),"gdt.jgui.entity.fields.JFieldsFacetOpenItem"));
	newEntity.putElementItem("jfacet", new Core("gdt.jgui.entity.nwproduct.JNwProductFacetAddItem",NwProductHandler.class.getName(),"gdt.jgui.entity.nwproduct.JNwProductFacetOpenItem"));
	
	return newEntity;

	}catch(Exception e){
		Logger.getLogger(NwProductHandler.class.getName()).severe(e.toString());
		
	}
return null;
}
@Override
public String getLocation() {
	return EXTENSION_KEY;
}
}