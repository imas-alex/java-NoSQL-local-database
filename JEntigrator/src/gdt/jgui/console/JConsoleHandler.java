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
import gdt.jgui.entity.bookmark.JBookmarksEditor;
import gdt.jgui.entity.extension.JExtensionRenderer;
import gdt.jgui.entity.fields.JFieldsEditor;
import gdt.jgui.entity.folder.JFolderPanel;
import gdt.jgui.entity.index.JIndexPanel;
import gdt.jgui.entity.procedure.JProcedurePanel;
import gdt.jgui.entity.query.JQueryPanel;
import gdt.jgui.entity.webset.JWeblinkEditor;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Properties;
import java.util.logging.Logger;
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
	final static boolean debug=false;
	/**
	 * Execute a handle request.
	 *  @param console the main console
	 *  @param locator$ the request locator.
	 *  @return the status string.
	 */
		public static String execute(JMainConsole console,String locator$){
        try{
     if(debug)   	
    	 System.out.println("ConsoleHandler:execute:locator="+Locator.remove(locator$, Locator.LOCATOR_ICON));	
       	 Properties locator=Locator.toProperties(locator$);
       	 String handlerScope$=locator.getProperty(BaseHandler.HANDLER_SCOPE);
       	String handlerClass$=locator.getProperty(BaseHandler.HANDLER_CLASS);
        String method$=locator.getProperty(BaseHandler.HANDLER_METHOD);
        String entihome$=locator.getProperty(Entigrator.ENTIHOME);
        String extension$=locator.getProperty(BaseHandler.HANDLER_LOCATION);
        //System.out.println("ConsoleHandler:execute:handler="+handlerClass$+" method="+method$+" entihome="+entihome$);
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
		    	baseLocator$=Locator.append(baseLocator$,Locator.LOCATOR_ICON,icon$);
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
		System.out.println("ConsoleHandler:getHandlerInstance:no extensions");
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
				System.out.println("ConsoleHandler:getHandlerInstance:jfacet: type="+aCa.type+" name="+aCa.name+" value="+aCa.value);	
				if(handlerClass$.equals(aCa.type)||handlerClass$.equals(aCa.value)||handlerClass$.equals(aCa.name)){
						Object obj= ExtensionHandler.loadHandlerInstance( entigrator,extension.getKey(), handlerClass$);
						return obj;
				}
			}
		ca=extension.elementGet("content.jrenderer");
		if(ca!=null)
			for(Core aCa:ca){
				if(debug)
				System.out.println("ConsoleHandler:getHandlerInstance:jrenderer: type="+aCa.type+" name="+aCa.name+" value="+aCa.value);	
				if(handlerClass$.equals(aCa.value)){
					Object obj= ExtensionHandler.loadHandlerInstance( entigrator,extension.getKey(), handlerClass$);
					if(obj!=null)
						entigrator.putHandler(handlerClass$, obj);
					return obj;
				}
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
						Object obj= ExtensionHandler.loadHandlerInstance( entigrator,extension.getKey(), handlerClass$);
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
}
