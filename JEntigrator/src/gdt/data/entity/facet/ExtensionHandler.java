package gdt.data.entity.facet;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
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
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.codec.binary.Base64;

import gdt.data.entity.EntityHandler;
import gdt.data.entity.FacetHandler;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
/**
* Contains methods to process an extension entity.
* @author  Alexander Imas
* @version 1.0
* @since   2016-03-11
*/
public class ExtensionHandler extends FacetHandler{
	/**
	* Constant for the entity type.
	*/	
public static final String EXTENSION="extension";
private Logger LOGGER=Logger.getLogger(ExtensionHandler.class.getName());
/**
 * Check if the extension handler is applied to the entity  
 *  @param entigrator entigrator instance
 *  @param locator$ entity's locator 
 * @return true if applied false otherwise.
 */	

	@Override
	public boolean isApplied(Entigrator entigrator, String locator$) {
		try{
			Properties locator=Locator.toProperties(locator$);
			String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			Sack entity=entigrator.getEntityAtKey(entityKey$);
			if(EXTENSION.equals(entity.getProperty("entity")))
				return true;
			else
				return false;
		}catch(Exception e){
		LOGGER.severe(e.toString());
		return false;
		}
		
	}
	/**
	 * Get an array of all facet handlers for all extensions in the database  
	 *  @param entigrator entigrator instance 
	 * @return array of facet handlers.
	 */	
	public static FacetHandler[] listExtensionHandlers( Entigrator entigrator){
		try{
		String[] sa=entigrator.indx_listEntities("entity","extension");
		if(sa==null)
			return null;
			ArrayList<FacetHandler>fl=new ArrayList<FacetHandler>();
		FacetHandler[] fha;	
		for(String aSa:sa){
			fha=listExtensionHandlers( entigrator,aSa);
			if(fha!=null)
				for(FacetHandler fh:fha)
					fl.add(fh);
		}
		return fl.toArray(new FacetHandler[0]);
		}catch(Exception e){
			Logger.getLogger(ExtensionHandler.class.getName()).severe(e.toString());
			return null;
		}
	}
	/**
	 * Get the handler instance.  
	 *  @param entigrator entigrator instance
	 *  @param extension$ extension key
	 *  @param handlerClass$ class name
	 * @return handler instance.
	 */	
	
	public static Object loadHandlerInstance(Entigrator entigrator,String extension$,String handlerClass$){
		try{
	//	System.out.println("ExtensionHandler:loadHandlerInstance:extension="+extension$+" handler="+handlerClass$);
			Object obj=null;
			Sack extension=entigrator.getEntityAtKey(extension$);
			String lib$=extension.getElementItemAt("field", "lib");
			String jar$="jar:file:" +entigrator.getEntihome()+"/"+extension$+"/"+lib$+"!/";
			URL[] urls = { new URL(jar$) };
			URLClassLoader cl = URLClassLoader.newInstance(urls);
			Class<?>cls=entigrator.getClass(handlerClass$);
			if(cls==null){
					cls=cl.loadClass(handlerClass$);
					entigrator.putClass(handlerClass$, cls);
			}
			//else
			//	System.out.println("ExtensionHandler:loadHandlerInstance:found in cache");
			//ClassLoader cll = Thread.currentThread().getContextClassLoader();
			
	//		System.out.println("ExtensionHandler:loadHandlerInstance:cls="+cls.getName());
			obj=cls.newInstance();
	//	System.out.println("ExtensionHandler:loadHandlerInstance:obj="+obj.toString());
		    return obj;
		}catch(Exception e){
			Logger.getLogger(ExtensionHandler.class.getName()).severe(e.toString());
			return null;
		}
		
	}
	
	private static FacetHandler[] listExtensionHandlers( Entigrator entigrator,String extension$){
		try{
		Sack extension=entigrator.getEntityAtKey(extension$);
		String lib$=extension.getElementItemAt("field", "lib");
		String[] sa=extension.elementList("content.fhandler");
		if(sa==null)
			return null;
		ArrayList<FacetHandler>fl=new ArrayList<FacetHandler>();
		FacetHandler fh;
		Class<?> cls;
		String jar$="jar:file:" +entigrator.getEntihome()+"/"+extension$+"/"+lib$+"!/";
		URL[] urls = { new URL(jar$) };
		URLClassLoader cl = URLClassLoader.newInstance(urls);
		for(String aSa:sa){
			try{
	//			System.out.println("ExtesionHandler:listExtensionHandlers:jar="+jar$);
			cls=cl.loadClass(aSa);
			fh=(FacetHandler)cls.newInstance();
			fl.add(fh);
			}catch(Exception ee){
				Logger.getLogger(ExtensionHandler.class.getName()).severe("load class: "+ee.toString());
			}
		}
		return fl.toArray(new FacetHandler[0]);
		}catch(Exception e){
			Logger.getLogger(ExtensionHandler.class.getName()).severe(e.toString());
			return null;
		}
	}
	public static String loadIcon(Entigrator entigrator,String extension$,String resource$){
		try{
			
	//System.out.println("ExtensionHandler:loadIcon:extension="+extension$+" handler="+handlerClass$);
			Sack extension=entigrator.getEntityAtKey(extension$);
			String lib$=extension.getElementItemAt("field", "lib");
			String jar$=entigrator.getEntihome()+"/"+extension$+"/"+lib$;
	//		System.out.println("ExtensionHandler:loadIcon:jar="+jar$);
			  ZipFile zf = new ZipFile(jar$);
			    Enumeration<? extends ZipEntry> entries = zf.entries();
			    ZipEntry ze;
			    String[] sa;
			    while (entries.hasMoreElements()) {
			      try{
			    	ze = entries.nextElement();
			      sa=ze.getName().split("/");
		//	      System.out.println("ExtensionHandler:loadIcon:zip entry="+sa[sa.length-1]);
			      if(resource$.equals(sa[sa.length-1])){
			    	  InputStream is=zf.getInputStream(ze);
		//	    	  System.out.println("ExtensionHandler:loadIcon:input stream="+is.toString());
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
				            byte[] b = new byte[1024];
				            int bytesRead = 0;
				            while ((bytesRead = is.read(b)) != -1) {
				               bos.write(b, 0, bytesRead);
				            }
				            byte[] ba = bos.toByteArray();
				            is.close();
				           return Base64.encodeBase64String(ba);
			      }
			      }catch(Exception e){
			    	  
			      }
			    }
			return null;
		}catch(Exception e){
			Logger.getLogger(ExtensionHandler.class.getName()).severe(e.toString());
			return null;
		}
		
	}
	private void adaptLabel(Entigrator entigrator){
		 try{
				Sack entity=entigrator.getEntityAtKey(entityKey$);
				entigrator.ent_assignProperty(entity, "extension", entityLabel$);
		    }catch(Exception e){
		    	
		    }
	}
	/**
	 * Adapt the clone of the entity.  
	 */	
	@Override
	public void adaptClone(Entigrator entigrator) {
		adaptLabel( entigrator);
	}
	/**
     * Adapt the the entity after rename.   
     */	
	@Override
	public void adaptRename(Entigrator entigrator) {
		adaptLabel( entigrator);
	}
	 /**
     * Get title of the extension handler.  
     * @return the title of the facet handler..
     */	
	@Override
	public String getTitle() {
		return "Extension";
	}
	 /**
     * Get type of the extension handler.  
     * @return the type of the facet handler..
     */	
	@Override
	public String getType() {
			return "extension";
	}
	 /**
     * Get class name of the extension handler.  
     * @return the class name of the facet handler..
     */	
	@Override
	public String getClassName() {
		return getClass().getName();
	}

}
