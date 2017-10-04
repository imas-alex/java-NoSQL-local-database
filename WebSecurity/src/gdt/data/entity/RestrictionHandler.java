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
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.Properties;
import java.util.logging.Logger;

import gdt.data.entity.EntityHandler;
import gdt.data.entity.FacetHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;


/**
* Contains methods to process a restriction entity.
* @author  Alexander Imas
* @version 1.0
* @since   2017-06-15
*/
public class RestrictionHandler extends FacetHandler {
	private Logger LOGGER=Logger.getLogger(getClass().getName());
	public static String RESTRICTION_KEY="restriction key";
	static boolean debug=false;
	/**
	 * Check if the restiction handler is applied to the entity  
	 *  @param entigrator entigrator instance
	 *  @param locator$ entity's locator 
	 * @return true if applied false otherwise.
	 */	
	@Override
	public boolean isApplied(Entigrator entigrator, String locator$) {
		try{
			Properties locator=Locator.toProperties(locator$);
     		entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			boolean result=false;
			Sack entity=entigrator.getEntityAtKey(entityKey$);
	    	String procedure$=entity.getProperty("restriction");
				if(procedure$!=null&&!Locator.LOCATOR_FALSE.equals(procedure$)){
				    if(entity.getElementItem("fhandler", getClass().getName())==null){	
						if(!entity.existsElement("fhandler"))
							entity.createElement("fhandler");
							entity.putElementItem("fhandler", new Core(null,getClass().getName(),null));
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
	 /**
     * Get title of the restriction handler.  
     * @return the title of the index handler..
     */	
	@Override
	public String getTitle() {
		return "Restriction";
	}
	 /**
     * Get type of the restriction handler.  
     * @return the type of the restriction handler..
     */	
	@Override
	public String getType() {
		return "restriction";
	}
	 /**
     * Get class name of the restriction handler.  
     * @return the class name of the restriction handler..
     */	
	@Override
	public String getClassName() {
		return getClass().getName();
	}
	private void adaptLabel(Entigrator entigrator){
		 try{
				Sack entity=entigrator.getEntityAtKey(entityKey$);
				entigrator.ent_assignProperty(entity, "restriction", entityLabel$);
		    }catch(Exception e){
		    	
		    }
	}
	/**
	* Adapt the clone of the entity.  
	*/
	@Override
	public void adaptClone(Entigrator entigrator) {
	  adaptLabel(entigrator);
	}
	/**
	 * Adapt the the entity after rename.   
	 */	
	@Override
	public void adaptRename(Entigrator entigrator) {
		adaptLabel(entigrator);
	}
	@Override
	public void completeMigration(Entigrator entigrator) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getLocation() {
		return UsersHandler.EXTENSION_KEY;
	}
	public static boolean denyRequest(Entigrator entigrator,String locator$){
		try{
			if(debug)
			System.out.println("RestrictionHandler:denyRequest:locator="+locator$);
			 String restrictionKey$=Locator.getProperty(locator$, RESTRICTION_KEY);
			if(debug)
					System.out.println("RestrictionHandler:denyRequest:restriction key="+restrictionKey$);

			 File home=new File(entigrator.getEntihome()+"/"+restrictionKey$);

			URL url = home.toURI().toURL();
			String entihome$=entigrator.getEntihome();	
			 File securityJar=new File(entihome$+"/"+UsersHandler.EXTENSION_KEY+"/security.jar");
	    	URL rUrl =  new URL( "jar:file:" + securityJar.getPath()+"!/");
		    //
	    	 String path$ = entigrator.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
             String entigratorJar$ = URLDecoder.decode(path$, "UTF-8");
             if(debug)
 				System.out.println("RestrictionHandler:denyRequest:entigrator jar="+entigratorJar$);
             URL eUrl =  new URL("jar:file:" + entigratorJar$+"!/");
	    	
	    	//
	    	URL[] urls = new URL[]{url,rUrl,eUrl};
		    ClassLoader parentLoader = entigrator.getClass().getClassLoader();
		    URLClassLoader cl = new URLClassLoader(urls,parentLoader);
		   // if(debug)
			//	System.out.println("RestrictionHandler:denyRequest:0");
		    Class<?> cls = cl.loadClass(restrictionKey$);
		    //if(debug)
			//	System.out.println("RestrictionHandler:denyRequest:1");
		  cl.close();
		  Object obj=cls.newInstance();
		  //if(debug)
		//		System.out.println("RestrictionHandler:denyRequest:2");
		  Method method = obj.getClass().getDeclaredMethod("deny",Entigrator.class,String.class);
		  //if(debug)
			//	System.out.println("RestrictionHandler:denyRequest:3");
          boolean denied=(boolean)method.invoke(obj,entigrator,locator$);
          if(debug)
				System.out.println("RestrictionHandler:denied="+denied);
          
		  return denied;
		 
		     
		     
		}catch(Exception e){
			Logger.getLogger(RestrictionHandler.class.getName()).severe(e.toString());
		}
		return true;
	}
		public static boolean isForced(Entigrator entigrator,String restrictionKey$){
			try{
					//System.out.println("JQueryPanel.select:query="+entityKey$);
				File home=new File(entigrator.getEntihome()+"/"+restrictionKey$);
				//File classFile=new File(entigrator.getEntihome()+"/"+restrictionKey$+"/"+restrictionKey$+".class");
				URL url = home.toURI().toURL();
			    URL[] urls = new URL[]{url};
			    ClassLoader parentLoader = Entigrator.class.getClassLoader();
			    URLClassLoader cl = new URLClassLoader(urls,parentLoader);
			  Class<?> cls = cl.loadClass(restrictionKey$);
			  cl.close();
			  Object obj=cls.newInstance();
			  Method method = obj.getClass().getDeclaredMethod("isForced");
			  return (boolean)method.invoke(obj);
			  //System.out.println("JQueryPanel.select:2");
			     
			     
			}catch(Exception e){
				Logger.getLogger(RestrictionHandler.class.getName()).severe(e.toString());
			}
			return false;
		}	 

}
