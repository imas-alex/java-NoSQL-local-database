package gdt.data.entity;
/*
 * Copyright 2016 Alexander Imas
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

import gdt.data.entity.facet.BookmarksHandler;
import gdt.data.entity.facet.ExtensionHandler;
import gdt.data.entity.facet.FieldsHandler;
import gdt.data.entity.facet.FolderHandler;
import gdt.data.entity.facet.IndexHandler;
import gdt.data.entity.facet.ProcedureHandler;
import gdt.data.entity.facet.QueryHandler;
import gdt.data.entity.facet.ViewHandler;
import gdt.data.entity.facet.WebsetHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
import gdt.jgui.base.JAllCategoriesPanel;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JFacetRenderer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Stack;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;
/**
* This class contains methods to process the database
* @author  Alexander Imas
* @version 1.0
* @since   2016-03-11
*/
public class BaseHandler {
	/**
	* the parent directory of the database
	*/
	public final static String ENTIROOT="entiroot";
	/**
	* tag of handler method within locator string
	*/
	public final static String HANDLER_METHOD="handler method";
	/**
	* tag of handler class within locator string
	*/
	public final static String HANDLER_CLASS="handler class";
	/**
	* tag of handler location within locator string
	*/
	public final static String HANDLER_LOCATION="handler location";
	/**
	* tag of handler scope within locator string
	*/
	public final static String HANDLER_SCOPE="handler scope";
	/**
	* value of handler scope within locator string that indicates the BaseHandler
	* as executor.  
	* 
	*/
	public final static String BASE_SCOPE="base scope";
	/**
	* value of handler method that indicates the 'deleteEntities' method 
	* as executor.  
	* 
	*/
	public final static String BASE_METHOD_DELETE_ENTITIES="deleteEntities";
	 /**
		 * Build the set of arguments needed to create an instance of BaseHandler 
		 * and pack them in the special string parameter - locator. 
		 *  
		 * @return The locator string.
		 */	 
	static boolean debug=false;
	public static String getLocator(){
			Properties locator=new Properties();
			locator.setProperty(Locator.LOCATOR_TITLE, "Base handler");
			locator.setProperty(BaseHandler.HANDLER_CLASS, BaseHandler.class.getName());
			locator.setProperty(BaseHandler.HANDLER_SCOPE, BaseHandler.BASE_SCOPE);
			return Locator.toString(locator);
		}
	/**
	 * List all databases in the parent directory
	 *  @param entiroot$ the parent directory.
	 * @return The locator string.
	 */	 
	public static String[] bases(String entiroot$) {
        try {
            if (entiroot$ == null)
                return null;
            File entiroot = new File(entiroot$);
            File[] dirs = entiroot.listFiles();
            if (dirs == null)
                return null;
            File propertyIndex;
            Stack<String> s = new Stack<String>();
            for (int i = 0; i < dirs.length; i++) {
                if (!dirs[i].isDirectory())
                    continue;
                propertyIndex = new File(dirs[i] + "/" + Entigrator.PROPERTY_INDEX);
                if (propertyIndex.exists() && propertyIndex.isFile())
                    s.push(dirs[i].getPath());
               
            }
            int cnt = s.size();
            if (cnt < 1)
                return null;
            String[] sa = new String[cnt];
            for (int i = 0; i < cnt; i++)
                sa[i] = (String) s.pop();
            return sa;
        } catch (Exception e) {
        	Logger.getLogger(BaseHandler.class.getName()).severe(e.toString());;
            return null;
        }
    }
	/**
	 * Execute an action
	 *  @param entigrator instance of  the Entigrator class
	 *  @param locator$ the arguments string.
	 * @return response string.
	 */	 	
	
public static String execute(Entigrator entigrator,String locator$){
	try{
	Properties locator=Locator.toProperties(locator$);
	String handlerClass$=locator.getProperty(HANDLER_CLASS);
	String method$=locator.getProperty(HANDLER_METHOD);
	Class<?> cls = Class.forName(handlerClass$);
    Object obj = cls.newInstance();
    Method method = cls.getDeclaredMethod(method$,Entigrator.class,String.class);
   	 return (String)method.invoke(obj,entigrator, locator$);
   	   }catch(Exception e){
    		Logger.getLogger(BaseHandler.class.getName()).severe(e.toString());
    	    return Locator.append(locator$, FacetHandler.METHOD_STATUS, FacetHandler.METHOD_STATUS_FAILED);	
    	   }
    	   
}
/**
 * Get an array of all facet handlers in the database
 *  @param entigrator instance of  the Entigrator class
 * @return array of facet handlers.
 */	 
public static FacetHandler[] listAllHandlers( Entigrator entigrator){
	try{
	FacetHandler[] fha=entigrator.getAllFacetHandlers();
		if(fha!=null)
		   return fha;
	
		ArrayList<FacetHandler>fl=new ArrayList<FacetHandler>();
	if(debug)
	System.out.println("BaseHandler:listAllHandlers:BEGIN");
	FieldsHandler fh=new FieldsHandler();
	fl.add(fh);
	entigrator.putHandlerAtType(fh.getType(),fh);
	FolderHandler foh=new FolderHandler();
	fl.add(foh);
	entigrator.putHandlerAtType(foh.getType(),foh);

	WebsetHandler wh=new WebsetHandler();
	fl.add(wh);
	entigrator.putHandlerAtType(wh.getType(),wh);
    
	BookmarksHandler bh=new BookmarksHandler();
	fl.add(bh);
	entigrator.putHandlerAtType(bh.getType(),bh);
	
	IndexHandler ih=new IndexHandler();
	fl.add(ih);
	entigrator.putHandlerAtType(ih.getType(),ih);
	
	ExtensionHandler eh=new ExtensionHandler();
	fl.add(eh);
	entigrator.putHandlerAtType(eh.getType(),eh);
	
	QueryHandler qh=new QueryHandler();
	fl.add(qh);
	entigrator.putHandlerAtType(qh.getType(),qh);
	
	ProcedureHandler ph=new ProcedureHandler();
	fl.add(ph);
	entigrator.putHandlerAtType(ph.getType(),ph);
	
	ViewHandler vh=new ViewHandler();
	fl.add(vh);
	entigrator.putHandlerAtType(vh.getType(),vh);
	

	if(debug)
	System.out.println("BaseHandler:listAllHandlers:END EMBEDDED");
	
	fha=ExtensionHandler.listExtensionHandlers(entigrator);
	
	if(fha!=null){
		
		for(FacetHandler h:fha){
		//if(debug)	
		//	System.out.println("BaseHandler:listAllHandlers:fh="+fh.getClass().getName());
			fl.add(h);
			entigrator.putHandlerAtType(h.getType(),h);
		}
	}
	
    fha=fl.toArray(new FacetHandler[0]);	
	if(debug)
	for(FacetHandler h:fha)
		System.out.println("BaseHandler:listAllHandlers:h="+h.getClass().getName());

	return fha;
	}catch(Exception e){
		Logger.getLogger(BaseHandler.class.getName()).severe(e.toString());
		return null;
	}
}
/**
 * Create an empty database
 *  @param entihome$ the path of the new database
 * @return return null if succeed  or an error message otherwise.
 */	 
public static String createBlankDatabase(String entihome$){
	try{
		if(debug)
		  System.out.println("BaseHandler:createBlankDatabase.entihome="+entihome$);
		File entihome=new File(entihome$);
		if(!entihome.exists()){
			entihome.mkdir();
		}
		
		File propertyBase=new File(entihome$+"/"+Entigrator.PROPERTY_BASE+"/data/");
		if(!propertyBase.exists())
		    propertyBase.mkdirs();
		File propertyMap=new File(entihome$+"/"+Entigrator.PROPERTY_MAP+"/data/");
		if(!propertyMap.exists())
			propertyMap.mkdirs();
		File entityBase=new File(entihome$+"/"+Entigrator.ENTITY_BASE+"/data/");
		if(!entityBase.exists())
		entityBase.mkdirs();
		Entigrator entigrator=new Entigrator(new String[]{entihome$});
		 String[] sa=entigrator.indx_listEntities("label", "Icons");
		 Sack folder=null;
		 if(sa==null){
			 folder=entigrator.ent_new("folder", "Icons",Entigrator.ICONS);
   	         folder.putAttribute(new Core(null,"icon","folder.png"));
	         //entigrator.replace(folder);
   	         entigrator.ent_alter(folder);
	         folder=entigrator.ent_assignProperty(folder, "folder", folder.getProperty("label"));
		 }
	   File folderHome=new File(entihome$+"/"+Entigrator.ICONS);
	   if(!folderHome.exists())
		    folderHome.mkdir();
	  
    return null;	      
	}catch(Exception e){
		Logger.getLogger(BaseHandler.class.getName()).severe(e.toString());
		return e.toString();
	}
	
}
public static FacetHandler getHandler(Entigrator entigrator,String entityType$){
	if(entigrator==null||entityType$==null)
		return null;
	FacetHandler fh=(FacetHandler)entigrator.getHandlerAtType(entityType$);
	if(fh!=null)
		return fh;
	FacetHandler[] fha=listAllHandlers(entigrator);
	if(fha==null||fha.length<1)
		return null;
	if(debug)
		for(FacetHandler h:fha)
			System.out.println("BaseHandler:getHandler:h="+h.getClass().getName());


	for(FacetHandler h:fha){
		if(debug)
			System.out.println("BaseHandler:getHandler:entity type="+entityType$+" handler type="+h.getType()+" handler="+h.getClass().getName());
		if(entityType$.equals(h.getType())){
			entigrator.putHandlerAtType(entityType$, h);
			if(debug)
				System.out.println("BaseHandler:getHandler:return handler="+h.getClassName());
			return h;
		}
	
	}
	return null;
}
}
