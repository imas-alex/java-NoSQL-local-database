package gdt.data.entity;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import gdt.data.store.Entigrator;
import gdt.data.entity.NwEmployeeHandler;
import gdt.data.entity.facet.FieldsHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Sack;
import gdt.data.grain.Support;
public class NwSourceHandler {
public static Document getSourceDocument(Entigrator entigrator){
	try{
		String northwind$=entigrator.indx_keyAtLabel("northwind");
		File inputFile = new File(entigrator.getEntihome()+"/"+northwind$+"/northwind.xml");
        DocumentBuilderFactory dbFactory 
        = DocumentBuilderFactory.newInstance();
     DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
     Document doc = dBuilder.parse(inputFile);
     doc.getDocumentElement().normalize();
     return doc;
	}catch(Exception e){
		Logger.getLogger(NwSourceHandler.class.getName()).severe(e.toString());
		return null;
	}
}
public  static void rebuildAll(Entigrator entigrator,String procedure$){
	System.out.println("NwSourceHandler:rebuildAll:procedure="+procedure$);
	
	try{
		Document doc=getSourceDocument(entigrator);
		System.out.println("NwSourceHandler:rebuildAll:Root element :" 
	            + doc.getDocumentElement().getNodeName());
		Sack procedure=entigrator.getEntityAtKey(procedure$);
		if(procedure==null||!procedure.existsElement("rebuild")){
		/*
			NwCategoryHandler.rebuildCategories(entigrator,doc,false); 
			NwCustomerHandler.rebuildCustomers(entigrator,doc,false); 
			NwEmployeeHandler.rebuildEmployees(entigrator,doc,false);
			NwProductHandler.rebuildProducts(entigrator,doc,false);
			NwTerritoryHandler.rebuildTerritories(entigrator,doc,false);
			NwOrderHandler.rebuildOrders(entigrator,doc,false);
			NwRegionHandler.rebuildRegions(entigrator,doc,false);
			NwShipperHandler.rebuildShippers(entigrator,doc,false);
			NwSupplierHandler.rebuildSuppliers(entigrator,doc,false);
			*/
			//NwOrderDetailHandler.rebuildOrderDetails(entigrator,doc,false);
			System.out.println("NwSourceHandler:rebuildAll:rebuild");
			return;
		}
		
		System.out.println("NwSourceHandler:rebuildAll:update");
		String nwCategory$=procedure.getElementItemAt("rebuild", "nwCategory");
		System.out.println("NwSourceHandler:rebuildAll:nwcategory="+nwCategory$);
		
		if("update".equals(nwCategory$))
			NwCategoryHandler.rebuildCategories(entigrator,doc,true); 
		if("rebuild".equals(nwCategory$))
			NwCategoryHandler.rebuildCategories(entigrator,doc,false);
		
		String nwCustomer$=procedure.getElementItemAt("rebuild", "nwCustomer");
		if("update".equals(nwCustomer$))
			NwCustomerHandler.rebuildCustomers(entigrator,doc,true); 
		if("rebuild".equals(nwCustomer$))
			NwCustomerHandler.rebuildCustomers(entigrator,doc,false);
		
		String nwEmployee$=procedure.getElementItemAt("rebuild", "nwEmployee");
		if("update".equals(nwEmployee$))
			NwEmployeeHandler.rebuildEmployees(entigrator,doc,true); 
		if("rebuild".equals(nwEmployee$))
			NwEmployeeHandler.rebuildEmployees(entigrator,doc,false);
		
		String nwProduct$=procedure.getElementItemAt("rebuild", "nwProduct");
		if("update".equals(nwProduct$))
			NwProductHandler.rebuildProducts(entigrator,doc,true); 
		if("rebuild".equals(nwProduct$))
			NwProductHandler.rebuildProducts(entigrator,doc,false);
		
		String nwTerritory$=procedure.getElementItemAt("rebuild", "nwTerritory");
		if("update".equals(nwTerritory$))
			NwTerritoryHandler.rebuildTerritories(entigrator,doc,true); 
		if("rebuild".equals(nwTerritory$))
			NwTerritoryHandler.rebuildTerritories(entigrator,doc,false);
		
		String nwOrder$=procedure.getElementItemAt("rebuild", "nwOrder");
		if("update".equals(nwOrder$))
			NwOrderHandler.rebuildOrders(entigrator,doc,true); 
		if("rebuild".equals(nwOrder$))
			NwOrderHandler.rebuildOrders(entigrator,doc,false);
		
		String nwShipper$=procedure.getElementItemAt("rebuild", "nwShipper");
		if("update".equals(nwShipper$))
			NwShipperHandler.rebuildShippers(entigrator,doc,true); 
		if("rebuild".equals(nwShipper$))
			NwShipperHandler.rebuildShippers(entigrator,doc,false);
		

		String nwSupplier$=procedure.getElementItemAt("rebuild", "nwSupplier");
		if("update".equals(nwSupplier$))
			NwSupplierHandler.rebuildSuppliers(entigrator,doc,true); 
		if("rebuild".equals(nwSupplier$))
			NwSupplierHandler.rebuildSuppliers(entigrator,doc,false);
		
		String nwOrderDetail$=procedure.getElementItemAt("rebuild", "nwOrderDetail");
		if("update".equals(nwOrderDetail$))
			NwOrderDetailHandler.rebuildOrderDetails(entigrator,doc,true); 
		if("rebuild".equals(nwOrderDetail$))
			NwOrderDetailHandler.rebuildOrderDetails(entigrator,doc,false); 
		//entigrator.indx_reindex(null);
		  
	}catch(Exception e){
		Logger.getLogger(NwSourceHandler.class.getName()).severe(e.toString());
	}
	
}
public  static void rebuildId2key(Entigrator entigrator){
	System.out.println("NwSourceHandler:rebuildId2key:BEGIN");
	
	try{
		Sack id2key=null;
		String id2key$=entigrator.indx_keyAtLabel("id2key");
		if(id2key$!=null)
		    id2key=entigrator.getEntityAtKey(id2key$);
		if(id2key!=null)
			entigrator.deleteEntity(id2key);
		 id2key=entigrator.ent_new("fields", "id2key");
			id2key.createElement("fhandler");
			id2key.putElementItem("fhandler", new Core(null,FieldsHandler.class.getName(),null));
			id2key.putAttribute(new Core (null,"icon","fields.png"));
			entigrator.replace(id2key);
			id2key=entigrator.ent_reindex(id2key);
		Sack candidate;
		String id$;
		if(id2key.existsElement("CategoryID"))
		   id2key.removeElement("CategoryID");
		String [] sa=entigrator.indx_listEntities("entity", "nwCategory");
		if(sa!=null){
			id2key.createElement("CategoryID");
			for(String s:sa){
			candidate=entigrator.getEntityAtKey(s);
			if(candidate!=null){
				id$=candidate.getElementItemAt("field", "CategoryID");
				if(id$!=null)
					id2key.putElementItem("CategoryID", new Core(null,id$,s));
			}
			}
		}
		if(id2key.existsElement("CustomerID"))
			   id2key.removeElement("CustomerID");
			sa=entigrator.indx_listEntities("entity", "nwCustomer");
			if(sa!=null){
				id2key.createElement("CustomerID");
				for(String s:sa){
				candidate=entigrator.getEntityAtKey(s);
				if(candidate!=null){
					id$=candidate.getElementItemAt("field", "CustomerID");
					if(id$!=null)
						id2key.putElementItem("CustomerID", new Core(null,id$,s));
				}
				}
			}
			if(id2key.existsElement("EmployeeID"))
				   id2key.removeElement("EmployeeID");
				sa=entigrator.indx_listEntities("entity", "nwEmployee");
				if(sa!=null){
					id2key.createElement("EmployeeID");
					for(String s:sa){
					candidate=entigrator.getEntityAtKey(s);
					if(candidate!=null){
						id$=candidate.getElementItemAt("field", "EmployeeID");
						if(id$!=null)
							id2key.putElementItem("EmployeeID", new Core(null,id$,s));
					}
					}
				}
				if(id2key.existsElement("OrderID"))
					   id2key.removeElement("OrderID");
					sa=entigrator.indx_listEntities("entity", "nwOrder");
					if(sa!=null){
						id2key.createElement("OrderID");
						for(String s:sa){
						candidate=entigrator.getEntityAtKey(s);
						if(candidate!=null){
							id$=candidate.getElementItemAt("field", "OrderID");
							if(id$!=null)
								id2key.putElementItem("OrderID", new Core(null,id$,s));
						}
						}
					}	
				if(id2key.existsElement("ProductID"))
						   id2key.removeElement("ProductID");
						sa=entigrator.indx_listEntities("entity", "nwProduct");
						if(sa!=null){
							id2key.createElement("ProductID");
							for(String s:sa){
							candidate=entigrator.getEntityAtKey(s);
							if(candidate!=null){
								id$=candidate.getElementItemAt("field", "ProductID");
								if(id$!=null)
									id2key.putElementItem("ProductID", new Core(null,id$,s));
							}
							}
						}
				if(id2key.existsElement("RegionID"))
							   id2key.removeElement("RegionID");
							sa=entigrator.indx_listEntities("entity", "nwRegion");
							if(sa!=null){
								id2key.createElement("RegionID");
								for(String s:sa){
								candidate=entigrator.getEntityAtKey(s);
								if(candidate!=null){
									id$=candidate.getElementItemAt("field", "RegionID");
									if(id$!=null)
										id2key.putElementItem("RegionID", new Core(null,id$,s));
								}
								}
							}
				if(id2key.existsElement("ShipperID"))
								   id2key.removeElement("ShipperID");
								sa=entigrator.indx_listEntities("entity", "nwShipper");
								if(sa!=null){
									id2key.createElement("ShipperID");
									for(String s:sa){
									candidate=entigrator.getEntityAtKey(s);
									if(candidate!=null){
										id$=candidate.getElementItemAt("field", "ShipperID");
										if(id$!=null)
											id2key.putElementItem("ShipperID", new Core(null,id$,s));
									}
									}
								}
					if(id2key.existsElement("SupplierID"))
									   id2key.removeElement("SupplierID");
									sa=entigrator.indx_listEntities("entity", "nwSupplier");
									if(sa!=null){
										id2key.createElement("SupplierID");
										for(String s:sa){
										candidate=entigrator.getEntityAtKey(s);
										if(candidate!=null){
											id$=candidate.getElementItemAt("field", "SupplierID");
											if(id$!=null)
												id2key.putElementItem("SupplierID", new Core(null,id$,s));
										}
										}
									}								
		entigrator.replace(id2key);
		entigrator.ent_reindex(id2key);
	}catch(Exception e){
		Logger.getLogger(NwSourceHandler.class.getName()).severe(e.toString());
	}
	
}
public  static void rebuildRelations(Entigrator entigrator){
	System.out.println("NwSourceHandler:rebuildRelations:BEGIN");
	
	try{
		String id2key$=entigrator.indx_keyAtLabel("id2key");
		Sack id2key=entigrator.getEntityAtKey(id2key$);
		if(id2key==null){
			rebuildId2key(entigrator);
			id2key=entigrator.getEntityAtKey(id2key$);
			if(id2key==null){
				System.out.println("NwSourceHandler:rebuildRelations:cannot index IDs. ");
				return;
			}
		}
			
		String[] sa;
		sa=entigrator.indx_listEntities("entity", "nwOrderDetail");
		if(sa!=null){
			System.out.println("NwSourceHandler:rebuildRelations:OrderDetails="+sa.length);
			Sack orderDetail=null;
			String product$;
			String productId$;
			Sack product;
			int flag=0;
			for(String s:sa){
				try{
				flag=0;
				orderDetail=entigrator.getEntityAtKey(s);
				if(orderDetail!=null){
					productId$=orderDetail.getElementItemAt("field", "ProductID");
					
					if(productId$!=null){
						flag=1;
						product$=id2key.getElementItemAt("ProductID", productId$);
						flag=2;
						if(product$!=null){
							product=entigrator.getEntityAtKey(product$);
							if(product!=null){
								flag=3;
								entigrator.col_breakRelation(orderDetail, product);
								entigrator.col_addComponent( product,orderDetail);
							}
						}
					}
					
				}
				}catch(Exception e){
					if(orderDetail!=null)
					System.out.println("NwSourceHandler:rebuildRelations:order detail="+orderDetail.getProperty("label")+" : flag="+flag);
				}
			}
			
		}
		
		
	}catch(Exception e){
		Logger.getLogger(NwSourceHandler.class.getName()).severe(e.toString());
	}
	
}
public static void saveIcon(Entigrator entigrator,String fileName$,String icon$){
	try{
	//byte[] ba = Base64.getDecoder().decode(icon$);
	byte[] ba = Base64.getMimeDecoder().decode(icon$);
	ByteArrayInputStream bais = new ByteArrayInputStream(ba);
	BufferedImage img =ImageIO.read(bais);
	BufferedImage newImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
	float weight = 1.0f/16.0f;
	float[] elements = new float[16]; // create 2D array
	for (int i = 0; i < 16; i++) {
   	   elements[i] = weight;
	}
	Kernel myKernel = new Kernel(4, 4, elements);
	ConvolveOp simpleBlur = new ConvolveOp(myKernel,ConvolveOp.EDGE_ZERO_FILL,null);
	simpleBlur.filter(img,newImage); // blur the image
	
	BufferedImage endImage = new BufferedImage(newImage.getWidth()+4,newImage.getHeight()+4, BufferedImage.TYPE_INT_RGB);
	Graphics2D g = endImage.createGraphics();
	g.setColor(Color.WHITE);
    g.fillRect(0, 0, endImage.getWidth(), endImage.getHeight());
	g.drawImage(newImage, 2, 2, img.getWidth(), img.getHeight(), null);
	g.dispose();
	File file=new File(entigrator.getEntihome()+"/"+Entigrator.ICONS, fileName$);
	if(!file.exists())
		file.createNewFile();
	ImageIO.write(endImage, "png",file );
	}catch(Exception e){
		Logger.getLogger(NwSourceHandler.class.getName()).severe(e.toString());	
	}
}
public static void removeIcon(Entigrator entigrator,Sack entity){
	try{
	String icon$=entity.getAttributeAt("icon");
	if(icon$==null)
		return;
	File file=new File(entigrator.getEntihome()+"/"+Entigrator.ICONS, icon$);
	if(file.exists())
		file.delete();
	
	}catch(Exception e){
		Logger.getLogger(NwSourceHandler.class.getName()).severe(e.toString());	
	}
	}
}
