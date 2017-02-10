package gdt.jgui.console;
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
import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.FacetHandler;
import gdt.data.entity.facet.BookmarksHandler;
import gdt.data.entity.facet.ExtensionHandler;
import gdt.data.entity.facet.FieldsHandler;
import gdt.data.entity.facet.FolderHandler;
import gdt.data.entity.facet.IndexHandler;
import gdt.data.entity.facet.ProcedureHandler;
import gdt.data.entity.facet.QueryHandler;
import gdt.data.entity.facet.WebsetHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.jgui.base.JBaseNavigator;
import gdt.jgui.base.JBasesPanel;
import gdt.jgui.entity.bookmark.JBookmarksEditor;
import gdt.jgui.entity.extension.JExtensionRenderer;
import gdt.jgui.entity.fields.JFieldsEditor;
import gdt.jgui.entity.folder.JFileOpenItem;
import gdt.jgui.entity.folder.JFolderPanel;
import gdt.jgui.entity.index.JIndexPanel;
import gdt.jgui.entity.procedure.JProcedurePanel;
import gdt.jgui.entity.query.JQueryPanel;
import gdt.jgui.entity.webset.JWeblinkEditor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;

import java.util.Collections;
import java.util.Comparator;
import java.util.Properties;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.apache.commons.codec.binary.Base64;
/**
 *  This class serves as a dispatcher for creating 
 *  different contexts.
 * 
 */
public class JConsoleHandler {
	/**
	 * Indicates that an execute request must be handled by this class. 
	 */
	public final static String CONSOLE_SCOPE="console scope";
	final static boolean debug=true;
	/**
	 * Execute a handle request.
	 *  @param console the main console
	 *  @param locator$ the request locator.
	 *  @return the status string.
	 */
		public static String execute(JMainConsole console,String locator$){
        try{
     if(debug)   	
    	 System.out.println("JConsoleHandler:execute:locator="+locator$);	
       	 Properties locator=Locator.toProperties(locator$);
       	 String handlerScope$=locator.getProperty(BaseHandler.HANDLER_SCOPE);
       	String handlerClass$=locator.getProperty(BaseHandler.HANDLER_CLASS);
        String method$=locator.getProperty(BaseHandler.HANDLER_METHOD);
        String entihome$=locator.getProperty(Entigrator.ENTIHOME);
        if(debug)   	
       	 System.out.println("JConsoleHandler:execute:entihome="+entihome$);	
           
        String extension$=locator.getProperty(BaseHandler.HANDLER_LOCATION);
        if(debug)
        System.out.println("ConsoleHandler:execute:handler="+handlerClass$+" method="+method$+" entihome="+entihome$+" extension="+extension$);
       if(CONSOLE_SCOPE.equals(handlerScope$)){
    	   if(JTrackPanel.class.getName().equals(handlerClass$)){
    		   JTrackPanel trackPanel=new JTrackPanel(console);
    		   console.putContext(trackPanel,null);
	    		return locator$; 
	    	 }
    	   if(JClipboardPanel.class.getName().equals(handlerClass$)){
    		   JClipboardPanel clipPanel=new JClipboardPanel();
    		   console.putContext(clipPanel,null);
	    		return locator$; 
	    	 }
    	   if(JRecentPanel.class.getName().equals(handlerClass$)){
    		   JRecentPanel recentPanel=new JRecentPanel();
    		   console.putContext(recentPanel,recentPanel.getLocator());
	    		return locator$; 
	    	 }
    	   Object obj;
    	   Entigrator entigrator=null;
    	   if(entihome$!=null)
    	      entigrator=console.getEntigrator(entihome$);
    	   console.outdatedTreatment$=locator.getProperty(JContext.OUTDATED_TREATMENT);
    	   if(extension$==null)
    	      obj =getHandlerInstance(entigrator, handlerClass$);
    	 else
    		 obj =getHandlerInstance(entigrator, handlerClass$,extension$);
    	   if(obj==null){
    		   if(debug)
    		   System.out.println("ConsoleHandler:execute:cannot instantiate "+handlerClass$);
    	       return null;
    	   }
    	   if (obj instanceof JContext){
    	    	 console.putContext((JContext)obj,locator$);
    	    	
    	    	   if(method$!=null){
    	    		   if(debug)
    	    			   System.out.println("ConsoleHandler:execute:method="+method$+" class="+obj.getClass().getName()+" locator="+locator$); 
   	    	    	Method method = obj.getClass().getDeclaredMethod(method$,JMainConsole.class,String.class);
   	    	    	method.invoke((JContext)obj,console, locator$);
   	    	    	  }
      	    	   return locator$;
    	    }else{
    	    	if(debug)
    	    	   System.out.println("ConsoleHandler:execute:NOT context="+handlerClass$);
    	    	if(method$!=null){
   	    	    	Method method =obj.getClass().getDeclaredMethod(method$,JMainConsole.class,String.class);
   	    	    	method.invoke(obj,console, locator$);
    	    	}
    	    	return locator$;
	    	   }
       }else{
    	   Entigrator entigrator=console.getEntigrator(entihome$);
    		  return  BaseHandler.execute(entigrator,locator$);
    	   }
   	    }catch(Exception e){
   		Logger.getLogger(JConsoleHandler.class.getName()).severe(e.toString());
   	 e.printStackTrace();
   		return null;
   		}
        }
		/**
		 * List all databases in the given directory
		 *  @param console the main console
		 *  @param entiroot$ the directory.
		 *  @return the array of database items in the directory.
		 */

		public  JItemPanel[] listBases(JMainConsole console,String entiroot$){
		try{
			if(debug)
			System.out.println("ConsoleHandler:listBases:entiroot="+entiroot$);
		   String[] sa=BaseHandler.bases(entiroot$);
		   if(sa==null){
			   return null;
		   }
		   ArrayList<JItemPanel>ipl=new ArrayList<JItemPanel>();
		   JItemPanel itemPanel;
		   JBaseNavigator baseNavigator=new JBaseNavigator();
		   String baseLocator$=baseNavigator.getLocator();
		   String icon$=Support.readHandlerIcon(null,JConsoleHandler.class, "base.png");
		    if(icon$!=null)
		    	//baseLocator$=Locator.append(baseLocator$,Locator.LOCATOR_ICON,icon$);
		    	baseLocator$=Locator.append(baseLocator$,Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
		    baseLocator$=Locator.append(baseLocator$,Locator.LOCATOR_ICON_CLASS,JBasesPanel.class.getName());
		    baseLocator$=Locator.append(baseLocator$,Locator.LOCATOR_ICON_FILE,"base.png");
		    
		//    System.out.println("ConsoleHandler:listBases:icon="+icon$);
		    for(String aSa:sa){
			   baseLocator$=Locator.append(baseLocator$, Entigrator.ENTIHOME, aSa);
			   File file = new File(aSa);
			   baseLocator$=Locator.append(baseLocator$,Locator.LOCATOR_TITLE, file.getName());
	
	//		   System.out.println("ConsoleHandler:listBases:base="+Locator.remove(baseLocator$,Locator.LOCATOR_ICON));
			   itemPanel=new JItemPanel(console,baseLocator$);
			   ipl.add(itemPanel);
		   }
		   Collections.sort(ipl,new JItemsListPanel.ItemPanelComparator());
		   return ipl.toArray(new JItemPanel[0]);
		}catch(Exception e) {
        	Logger.getLogger(BaseHandler.class.getName()).severe(e.toString());
            return null;
        }
	}
		/**
		 * Get the facet renderer context.
		 *  @param entigrator the entigrator
		 *  @param fhandler$ the facet handler class name.
		 *  
		 *  @return the instance of facet renderer corresponding the given handler.
		 */
public static JFacetRenderer getFacetRenderer(Entigrator entigrator,String fhandler$){
//	  System.out.println("JConsoleHandler:getFacetRenderer:handler="+fhandler$);	
	if(FieldsHandler.class.getName().equals(fhandler$))
		   return new JFieldsEditor();
	if(FolderHandler.class.getName().equals(fhandler$))
			return new JFolderPanel();
	if(WebsetHandler.class.getName().equals(fhandler$))
		return new JWeblinkEditor();
	if(BookmarksHandler.class.getName().equals(fhandler$))
		return new JBookmarksEditor();
	if(IndexHandler.class.getName().equals(fhandler$))
		return new JIndexPanel();
	if(ExtensionHandler.class.getName().equals(fhandler$))
		return new JExtensionRenderer();
	if(QueryHandler.class.getName().equals(fhandler$))
		return new JQueryPanel();
	if(ProcedureHandler.class.getName().equals(fhandler$))
		return new JProcedurePanel();
		String[]sa=entigrator.indx_listEntities("entity", "extension");
		if(sa!=null){
		Sack extension;
		Core[] ca;
		JFacetOpenItem facetOpenItem;
		String facetRenderer$;
	for(String aSa:sa){
		
		try{
			extension=entigrator.getEntityAtKey(aSa);
	//		System.out.println("ConsoleHandler:getFacetRenderer:extension="+extension.getProperty("label"));
			ca=extension.elementGet("content.jfacet");
			if(ca!=null)
				for(Core aCa:ca)
				     if(aCa.name.equals(fhandler$)){
		//		    System.out.println("ConsoleHandler:getFacetRenderer:aca.value="+aCa.value);	 
					facetOpenItem=(JFacetOpenItem)getHandlerInstance(entigrator,aCa.value);
					facetRenderer$= facetOpenItem.getFacetRenderer();
					if(debug)
					System.out.println("JConsoleHandler:getFacetRenderer:facet renderer="+facetRenderer$);	 
						return (JFacetRenderer)ExtensionHandler.loadHandlerInstance( entigrator,extension.getKey(), facetRenderer$);
				}
			}catch(Exception e){
				Logger.getLogger(JConsoleHandler.class.getName()).severe(e.toString());
				}
		    }
		}
		return null;
}
public static JFacetRenderer getExtensionFacetRenderer(Entigrator entigrator,String fhandler$,String extension$){
//	  System.out.println("JConsoleHandler:getFacetRenderer:handler="+fhandler$);	
	try{
			Sack extension=entigrator.getEntityAtKey(extension$);
	//		System.out.println("ConsoleHandler:getFacetRenderer:extension="+extension.getProperty("label"));
			Core[]ca=extension.elementGet("content.jfacet");
			if(ca==null)
				return null;
			for(Core aCa:ca)
				     if(aCa.name.equals(fhandler$)){
		//		    System.out.println("ConsoleHandler:getFacetRenderer:aca.value="+aCa.value);	 
				    	 JFacetOpenItem facetOpenItem=(JFacetOpenItem)getHandlerInstance(entigrator,aCa.value);
					String facetRenderer$= facetOpenItem.getFacetRenderer();
					if(debug)
					System.out.println("JConsoleHandler:getFacetRenderer:facet renderer="+facetRenderer$);	 
						return (JFacetRenderer)ExtensionHandler.loadHandlerInstance( entigrator,extension.getKey(), facetRenderer$);
				}
			}catch(Exception e){
				Logger.getLogger(JConsoleHandler.class.getName()).severe(e.toString());
				}
	return null;    
		}
		

/**
 * Get instance of the facet handler.
 *  @param entigrator the entigrator
 *  @param handlerClass$ the facet handler class name.
 *  @return the instance of facet handler as an object or null.
 */
public static Object getHandlerInstance(Entigrator entigrator,String handlerClass$){
try{
	if(debug)
	System.out.println("ConsoleHandler:getHandlerInstance:handler class="+handlerClass$);
	if(handlerClass$==null||"null".equals(handlerClass$)){
		System.out.println("ConsoleHandler:getHandlerInstance:argument null");
		return null;
	}
	if(handlerClass$.equals(JBasesPanel.class.getName())){
		return new JBasesPanel();
	}
	
	try{
		if(debug)
		System.out.println("ConsoleHandler:getHandlerInstance:forName");
		//Object 	handler=null;
		Object 	handler=entigrator.getHandler(handlerClass$);
		if(handler!=null){
			if(debug)
			System.out.println("ConsoleHandler:getHandlerInstance:found handler="+handler.toString());
			return handler;
		}
		
		 handler= Class.forName(handlerClass$).newInstance();
		if(handler!=null){
			if(debug)
				System.out.println("ConsoleHandler:getHandlerInstance:found handler for name="+handlerClass$);
			entigrator.putHandler(handlerClass$, handler);
			return handler;
		}else{
			if(debug)
				System.out.println("ConsoleHandler:getHandlerInstance:cannot get class for name="+handlerClass$);
				
		}
	}catch(ClassNotFoundException ee){
		if(debug)
		System.out.println("ConsoleHandler:getHandlerInstance:embedded class not found");
	String[]sa=entigrator.indx_listEntities("entity", "extension");
	if(sa==null){
		System.out.println("ConsoleHandler:getHandlerInstance:no extensions:"+entigrator.getEntihome());
		return null;
	}
	Sack extension;
	Core[] ca;
	for(String aSa:sa){
		extension=entigrator.getEntityAtKey(aSa);
		if(debug)
		System.out.println("ConsoleHandler:getHandlerInstance:try extension="+extension.getProperty("label"));
		ca=extension.elementGet("content.fhandler");
		if(ca!=null)
			for(Core aCa:ca){
				if(handlerClass$.equals(aCa.name)){
					if(debug)
					System.out.println("ConsoleHandler:getHandlerInstance:aCa.name="+aCa.name);	
					Object obj= ExtensionHandler.loadHandlerInstance( entigrator,extension.getKey(), handlerClass$);
					if(obj!=null)
						entigrator.putHandler(handlerClass$, obj);
					if(debug)
					System.out.println("ConsoleHandler:getHandlerInstance:handler="+handlerClass$);
					return obj;
				}
			}
		ca=extension.elementGet("content.jfacet");
		if(ca!=null)
			for(Core aCa:ca){
				if(debug)
				System.out.println("ConsoleHandler:getHandlerInstance: check jfacet: type="+aCa.type+" name="+aCa.name+" value="+aCa.value);	
				if(handlerClass$.equals(aCa.type)||handlerClass$.equals(aCa.value)||handlerClass$.equals(aCa.name)){
						
					Object obj= ExtensionHandler.loadHandlerInstance( entigrator,extension.getKey(), handlerClass$);
						return obj;
				}
			}
		ca=extension.elementGet("content.jrenderer");
		if(ca!=null)
			for(Core aCa:ca){
				if(debug)
				System.out.println("JConsoleHandler:getHandlerInstance:jrenderer: type="+aCa.type+" name="+aCa.name+" value="+aCa.value);	
				if(handlerClass$.equals(aCa.value)){
					if(debug)
						System.out.println("JConsoleHandler:getHandlerInstance:try instantiate jrenderer: type="+aCa.type+" name="+aCa.name+" value="+aCa.value);	
						
					Object obj= ExtensionHandler.loadHandlerInstance( entigrator,extension.getKey(), handlerClass$);
					if(obj!=null){
						if(debug)
							System.out.println("JConsoleHandler:getHandlerInstance:got jrenderer instance: type="+aCa.type+" name="+aCa.name+" value="+aCa.value);	
						
						entigrator.putHandler(handlerClass$, obj);
					}
					return obj;
				}else
					if(debug)
						System.out.println("JConsoleHandler:getHandlerInstance:unknown handler="+handlerClass$);	
					
			}
		ca=extension.elementGet("content.super");
		if(ca!=null)
			for(Core aCa:ca){
				if(debug)
				System.out.println("JConsoleHandler:getHandlerInstance:super:  handler="+aCa.name+" super="+aCa.value);	
				if(handlerClass$.equals(aCa.name)){
					if(debug)
						System.out.println("JConsoleHandler:getHandlerInstance:try instantiate at super:  handler="+aCa.name+" super="+aCa.value);	
						
					Object obj= ExtensionHandler.loadHandlerInstance( entigrator,extension.getKey(), handlerClass$);
					if(obj!=null){
						if(debug)
							System.out.println("JConsoleHandler:getHandlerInstance:got jrenderer instance: handler="+aCa.name+" super="+aCa.value);	
						entigrator.putHandler(handlerClass$, obj);
					}
					return obj;
				}else
					if(debug)
						System.out.println("JConsoleHandler:getHandlerInstance:unknown handler="+handlerClass$);	
					
			}
		}
	    }
	return null;
	}catch(Exception e){
		Logger.getLogger(ExtensionHandler.class.getName()).severe(e.toString());
		return null;
	}
}
public static Object getHandlerInstance(Entigrator entigrator,String handlerClass$,String extension$){
try{
	if(debug)
		System.out.println("ConsoleHandler:getHandlerInstance:handler class="+handlerClass$ +" extension="+extension$);
		
	Object 	handler=entigrator.getHandler(handlerClass$);
	if(handler!=null)
		return handler;
	if(extension$==null||"null".equals(extension$))
		return getHandlerInstance(entigrator, handlerClass$);
	
	if(handlerClass$==null||"null".equals(handlerClass$)|extension$==null){
		if(debug)
		System.out.println("ConsoleHandler:getHandlerInstance:argument null");
		return null;
	}
	Sack extension=entigrator.getEntityAtKey(extension$);
	if(extension==null){
		if(debug)
		System.out.println("ConsoleHandler:getHandlerInstance:extension is null");
		return null;
	}
	Core[] ca;
	if(debug)
	System.out.println("ConsoleHandler:getHandlerInstance:try extension="+extension.getProperty("label"));
		ca=extension.elementGet("content.fhandler");
		if(ca!=null)
			for(Core aCa:ca){
				if(handlerClass$.equals(aCa.name)){
					if(debug)
					System.out.println("ConsoleHandler:getHandlerInstance:aCa.name="+aCa.name);	
					Object obj= ExtensionHandler.loadHandlerInstance( entigrator,extension.getKey(), handlerClass$);
					if(obj!=null)
						entigrator.putHandler(handlerClass$, handler);
					if(debug)
					System.out.println("ConsoleHandler:getHandlerInstance:handler="+handlerClass$);
					return obj;
				}
			}
		ca=extension.elementGet("content.jfacet");
		if(ca!=null)
			for(Core aCa:ca){
				if(debug)
				System.out.println("ConsoleHandler:getHandlerInstance:jfacet: type="+aCa.type+" name="+aCa.name+" value="+aCa.value);	
				if(handlerClass$.equals(aCa.type)||handlerClass$.equals(aCa.value)||handlerClass$.equals(aCa.name)){
					if(debug)
						System.out.println("ConsoleHandler:getHandlerInstance:handler="+handlerClass$+" extension="+extension$);	
								
					Object obj= ExtensionHandler.loadHandlerInstance( entigrator,extension$, handlerClass$);
						if(obj!=null)
							entigrator.putHandler(handlerClass$, obj);
						return obj;
				}
			}
		ca=extension.elementGet("content.jrenderer");
		if(ca!=null)
			for(Core aCa:ca){
		//		System.out.println("ConsoleHandler:getHandlerInstance:jrenderer: type="+aCa.type+" name="+aCa.name+" value="+aCa.value);	
				if(handlerClass$.equals(aCa.value)){
					Object obj= ExtensionHandler.loadHandlerInstance( entigrator,extension.getKey(), handlerClass$);
					//System.out.println("ConsoleHandler:getHandlerInstance:2");
					if(obj!=null)
						entigrator.putHandler(handlerClass$, obj);
					return obj;
				}
			}
		try{
			Object obj= ExtensionHandler.loadHandlerInstance( entigrator,extension.getKey(), handlerClass$);
			if(obj!=null)
				return obj;
		}catch(Exception ee){
			Logger.getLogger(JConsoleHandler.class.getName()).info(ee.toString());
		}
	return null;
	}catch(Exception e){
		Logger.getLogger(ExtensionHandler.class.getName()).severe(e.toString());
		return null;
	}
}
public static class TitleComparator implements Comparator<JItemPanel>{
    @Override
    public int compare(JItemPanel o1, JItemPanel o2) {
    	try{
    		return o1.getTitle().compareToIgnoreCase(o2.getTitle());
    	}catch(Exception e){
    		return 0;
    	}
    }
}
public static String getIcon(Entigrator entigrator,String locator$){
try{
    //System.out.println("JConsoleHandler:getIcon:locator="+locator$);		
		 Properties locator=Locator.toProperties(locator$);
		String contextType$=locator.getProperty(JContext.CONTEXT_TYPE);
		if(contextType$!=null){
		JBasesPanel bp=new JBasesPanel(); 
		if(contextType$.equals(bp.getType())){
			return Support.readHandlerIcon(null,JBasesPanel.class , "bases.png");
		}
		}
		 String fileName$=locator.getProperty(JFolderPanel.FILE_NAME);
		 String filePath$=locator.getProperty(JFolderPanel.FILE_PATH);
		if(debug)
			System.out.println("JConsoleHandler:getIcon:file path="+filePath$);
		 if(fileName$!=null){
			 for (final String ext : JFileOpenItem.IMAGE_EXTENSIONS) {
	                if (fileName$.toLowerCase().endsWith("." + ext)) {
	                	BufferedImage img =ImageIO.read(new File(filePath$));	
	                	BufferedImage newImage = new BufferedImage(24, 24, BufferedImage.TYPE_INT_RGB);
	                	Graphics2D g = newImage.createGraphics();
	                	g.setColor(Color.WHITE);
	                    g.fillRect(0, 0, 24, 24);
	                	g.drawImage(img, 0, 0, 24, 24, null);
	                	g.dispose();
	                	Image im =img.getScaledInstance( 24, 24,  java.awt.Image.SCALE_SMOOTH ) ;  
	                     ByteArrayOutputStream b =new ByteArrayOutputStream();
	    	            ImageIO.write(newImage, "png", b );
	    		            b.close();
	    		 		byte[]	ba = b.toByteArray();
	    		       return  Base64.encodeBase64String(ba);
	                }
			 }
				return Support.readHandlerIcon(entigrator, JFileOpenItem.class, "file.png");
             }
		 String iconFile$=locator.getProperty(Locator.LOCATOR_ICON_FILE);
		 if(iconFile$!=null&&!"null".equals(iconFile$)){
		 if(Locator.LOCATOR_ICON_CONTAINER_ICONS.equals(locator.getProperty(Locator.LOCATOR_ICON_CONTAINER))){
        String extension$=locator.getProperty(Locator.LOCATOR_ICON_LOCATION);
		  if(extension$==null)
		  {	 
			  
			  String path$ = entigrator.getEntihome() + "/" + Entigrator.ICONS+"/"+iconFile$;
			  if(new File(path$).exists()){
				 FileInputStream is=new FileInputStream(path$);
		         ByteArrayOutputStream bos = new ByteArrayOutputStream();
		            byte[] b = new byte[1024];
		            int bytesRead = 0;
		            while ((bytesRead = is.read(b)) != -1) {
		               bos.write(b, 0, bytesRead);
		            }
		            byte[] ba = bos.toByteArray();
		            is.close();
		           return Base64.encodeBase64String(ba);
			  }else{
				  String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
				  if(entityKey$!=null){
					  String entityType$=entigrator.getEntityType(entityKey$);
					  FacetHandler fh=BaseHandler.getHandler(entigrator, entityType$);
					  JFacetRenderer facetRenderer=getFacetRenderer(entigrator, fh.getClass().getName()); 
   					  return  Support.readHandlerIcon(entigrator, facetRenderer.getClass(), facetRenderer.getFacetIcon());
				
				  }
			  }
		  }else{
			 return  ExtensionHandler.loadIcon(entigrator, extension$, iconFile$);
		  }
			 }
		
		 if(Locator.LOCATOR_ICON_CONTAINER_CLASS.equals(locator.getProperty(Locator.LOCATOR_ICON_CONTAINER))){
			
			 String iconHandler$=locator.getProperty(Locator.LOCATOR_ICON_CLASS);
			 Class iconHandler=JConsoleHandler.getHandlerInstance(entigrator, iconHandler$).getClass();
			 String iconLocation$=locator.getProperty(Locator.LOCATOR_ICON_CLASS_LOCATION);
			if(iconLocation$==null)
				return Support.readHandlerIcon(entigrator, iconHandler, iconFile$);
			else
				return ExtensionHandler.loadIcon(entigrator, iconLocation$, iconFile$);
		 }
		
		 }
		 String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		 
		 if(Locator.LOCATOR_ICON_CONTAINER_ENTITY.equals(locator.getProperty(Locator.LOCATOR_ICON_CONTAINER))){
			// String entityKey$=locator.getProperty(Locator.LOCATOR_ICON_ENTITY_KEY);
			
			
			 Sack entity=entigrator.getEntityAtKey(entityKey$);
			 String element$=locator.getProperty(Locator.LOCATOR_ICON_ELEMENT);
			 String core$=locator.getProperty(Locator.LOCATOR_ICON_CORE);
			 String field$=locator.getProperty(Locator.LOCATOR_ICON_FIELD);
			 Core core=entity.getElementItem(element$, core$);
			 if(Locator.LOCATOR_ICON_FIELD_VALUE.equals(field$))
				 return core.value;
			 if(Locator.LOCATOR_ICON_FIELD_TYPE.equals(field$))
				 return core.type;
			 return null;
		 }
		 if(entityKey$!=null){
			 String entityType$=entigrator.getEntityType(entityKey$);
			 FacetHandler fh=BaseHandler.getHandler(entigrator, entityType$);
			 if(fh!=null){
				 JFacetRenderer facetRenderer=getFacetRenderer(entigrator, fh.getClass().getName()); 
			 
				return  Support.readHandlerIcon(entigrator, facetRenderer.getClass(), facetRenderer.getFacetIcon());
			 }
			 }
		 /*
		 String iconField$=locator.getProperty(Locator.LOCATOR_ICON_FIELD);
		 if(iconField$!=null){
			// String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);	
			 Sack entity=getEntityAtKey(entityKey$);
			 String element$=locator.getProperty(Locator.LOCATOR_ICON_ELEMENT);
			 String item$=locator.getProperty(Locator.LOCATOR_ICON_CORE);
			 Core core=entity.getElementItem(element$, item$);
			 if(Locator.LOCATOR_ICON_FIELD_VALUE.equals(iconField$))
				 return core.value;
			 if(Locator.LOCATOR_ICON_FIELD_TYPE.equals(iconField$))
				 return core.type;
			 
				 
		 }
		 */
		 return null;
	 }catch(Exception e){
		 Logger.getLogger(JConsoleHandler.class.getName()).severe(e.toString());
	 }
	 return null;
}
}
