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
public class NwCategoryHandler extends FieldsHandler{
	public static final String EXTENSION_KEY="_ebrysyRG3lk1CYHiugIoSapofvo";	
private Logger LOGGER=Logger.getLogger(NwCategoryHandler.class.getName());
String entihome$;
String entityKey$;
static boolean debug=true;
public final static String CATEGORY="nwCategory";

public NwCategoryHandler(){
	super();
}
	@Override
	public boolean isApplied(Entigrator entigrator, String locator$) {
		try{
		if(debug)	
			System.out.println("NwCategoryHandler:isApplied:locator="+locator$);
			Properties locator=Locator.toProperties(locator$);
			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			boolean result=false;
			Sack entity=entigrator.getEntityAtKey(entityKey$);
		if(debug)	
			System.out.println("NwCategoryHandler:isApplied:entity="+entity.getProperty("label"));
			String category$=entity.getProperty("nwCategory");
			if(category$!=null&&!Locator.LOCATOR_FALSE.equals(category$)){
			   if(entity.getElementItem("fhandler", NwCategoryHandler.class.getName())==null){	
					if(!entity.existsElement("fhandler"))
						entity.createElement("fhandler");
					entity.putElementItem("fhandler", new Core(null, NwCategoryHandler.class.getName(),null));
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
		return "nwCategory";
	}

	public String getType() {
		return "nwCategory";
	}
	private void adaptLabel(Entigrator entigrator){
		 try{
				Sack entity=entigrator.getEntityAtKey(entityKey$);
				entigrator.ent_assignProperty(entity, "nwCategory", entityLabel$);
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
	return  NwCategoryHandler.class.getName();
}
public  static void rebuildCategories(Entigrator entigrator,Document doc,boolean update){
	if(debug)
	System.out.println("NwCategoryHandler:rebuildCategories:BEGIN");
	Sack id2key=entigrator.getEntityAtKey(entigrator.indx_keyAtLabel("id2key"));
	if(id2key==null){
		entigrator.ent_releaseLabel("id2key");
		id2key=entigrator.ent_new("fields", "id2key");
	}
	try{
		if(!update){
		String[] sa=entigrator.indx_listEntities("entity", "nwCategory");
		if(sa!=null){
			Sack category;
			for(String s:sa){
				category=entigrator.getEntityAtKey(s);
				if(category!=null)
				  entigrator.deleteEntity(category);
			}
		}
		}
		if(debug)
		System.out.println("NwCategoryHandler:rebuildCategories:Root element :" 
	            + doc.getDocumentElement().getNodeName());
	         NodeList nList = doc.getElementsByTagName("Category");
	         if(debug)
	         System.out.println("----------------------------");
	         Sack nwCategory;
	         String nwCategory$;
	         for (int temp = 0; temp < nList.getLength(); temp++) {
	        	 Node nNode = nList.item(temp);
	             if(debug)
	        	 System.out.println("\nCurrent Element :" 
	                + nNode.getNodeName());
	             if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	                 Element eElement = (Element) nNode;
	                 String name$=eElement
	    	                    .getElementsByTagName("CategoryName")
	    	                    .item(0)
	    	                    .getTextContent();
	                 if(debug)
	                 System.out.println("Category name : " 
	    	                 + name$);
	                 if(update){ 
		                 nwCategory$=entigrator.indx_keyAtLabel(name$);
		                 if(nwCategory$!=null){
		                	 nwCategory=entigrator.getEntityAtKey(nwCategory$);
		                     if(nwCategory!=null){ 
		                    	 entigrator.ent_reindex(nwCategory);
		                    	 continue;
		                     }
		                }
	                 }
	                 nwCategory=createCategory(entigrator, name$);
	                 if(nwCategory==null){
	                	 if(debug)
	                	 System.out.println("NwCategoryHandler:rebuildCategories:cannot create category="+name$);
	                	 continue;
	                 }
	                 nwCategory.putElementItem("field", new Core(null,"CategoryName",name$));
	                 String id$=eElement.getAttribute("CategoryID");
	                 if(debug)
	                 System.out.println("Category ID : " 
	                    +id$ );
	                 nwCategory.putElementItem("field", new Core(null,"CategoryID",id$));
	                 String description$=eElement
	 	                    .getElementsByTagName("Description")
		                    .item(0)
		                    .getTextContent();
	                 if(description$!=null)
	                	 nwCategory.putElementItem("field", new Core(null,"description",description$));
	                 if(debug)
	                 System.out.println("Description : " 
	                    +description$ );
	                 String icon$=eElement
	                         .getElementsByTagName("Picture")
	                         .item(0)
	                         .getTextContent();
	                 if(debug)
	                 System.out.println("Icon : "+icon$);
	                if(icon$!=null){
	                   String key$=nwCategory.getKey();	
	                	String fileName$=key$+".png";
	                    NwSourceHandler.saveIcon(entigrator, fileName$, icon$);
	                    nwCategory.putAttribute(new Core(null,"icon",fileName$));
	                    }
	              entigrator.replace(nwCategory);
	              nwCategory=entigrator.ent_assignProperty(nwCategory, "nwCategory", name$);
	              nwCategory=entigrator.ent_assignProperty(nwCategory, "fields", name$);
	              entigrator.ent_reindex(nwCategory);
	              if(!id2key.existsElement("nwCategory"))
	            	  id2key.createElement("nwCategory");
	              id2key.putElementItem("nwCategory", new Core(null,id$,nwCategory.getKey()));
	         }
	         }
	         entigrator.replace(id2key);
	}catch(Exception e){
		Logger.getLogger(NwCategoryHandler.class.getName()).severe(e.toString());
	}
	
}
private  static Sack createCategory(Entigrator entigrator,String label$ ){
	if(debug)
	System.out.println("NwCategoryHandler:createCategory:"+label$);
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
	Sack newEntity=entigrator.ent_new("nwCategory", label$);
	newEntity.createElement("field");
	newEntity.createElement("fhandler");
	newEntity.putElementItem("fhandler", new Core(null,NwCategoryHandler.class.getName(),null));
	newEntity.putElementItem("fhandler", new Core(null,FieldsHandler.class.getName(),null));
	newEntity.createElement("jfacet");
	newEntity.putElementItem("jfacet", new Core("gdt.jgui.entity.fields.JFieldsFacetAddItem",FieldsHandler.class.getName(),"gdt.jgui.entity.fields.JFieldsFacetOpenItem"));
	newEntity.putElementItem("jfacet", new Core("gdt.jgui.entity.nwcategory.JNwCategoryFacetAddItem",NwCategoryHandler.class.getName(),"gdt.jgui.entity.nwcategory.JNwCategoryFacetOpenItem"));
	
	return newEntity;

	}catch(Exception e){
		Logger.getLogger(NwCategoryHandler.class.getName()).severe(e.toString());
		
	}
return null;
}
@Override
public String getLocation() {
	return EXTENSION_KEY;
}
}