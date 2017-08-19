package gdt.jgui.base;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.JMenu;

import org.apache.commons.codec.binary.Base64;

import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.FacetHandler;
import gdt.data.entity.facet.ExtensionHandler;
import gdt.data.grain.Locator;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JFacetRenderer;
import gdt.jgui.console.JItemPanel;
import gdt.jgui.console.JItemsListPanel;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.ReloadDialog;
import gdt.jgui.console.WContext;
import gdt.jgui.console.WUtils;
/**
* This context displays a list of all categories (types of entities).   
* @author  Alexander Imas
* @version 1.0
* @since   2016-03-11
*/


public class JAllCategoriesPanel extends JItemsListPanel implements WContext {
String entihome$;
Hashtable<String,JItemPanel> items;
	private static final long serialVersionUID = 1L;
	boolean debug=false;
	boolean ignoreOutdate=false;
	/**
	 * Default constructor
	 *  
	 */
	public JAllCategoriesPanel() {
		super();
	}
	/**
	 * Get context locator. 
	 * @return the locator.
	 */	
	@Override
	public String getLocator() {
		Properties locator=new Properties();
	    locator.setProperty(Locator.LOCATOR_TYPE, JContext.CONTEXT_TYPE);
	    locator.setProperty(JContext.CONTEXT_TYPE,getType());
	    locator.setProperty(Locator.LOCATOR_TITLE, getTitle());
	    if(entihome$!=null)
	    locator.setProperty(Entigrator.ENTIHOME,entihome$);
	    locator.setProperty(Locator.LOCATOR_ICON_CONTAINER, Locator.LOCATOR_ICON_CONTAINER_CLASS);
	    locator.setProperty(Locator.LOCATOR_ICON_CLASS,getClass().getName());
	    locator.setProperty(Locator.LOCATOR_ICON_FILE, "category.png"); 
	 	    locator.setProperty(Locator.LOCATOR_TITLE, getTitle());
	    locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
	    locator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
		return Locator.toString(locator);

	}
	/**
	 * Create the context.
	 *  @param console the main application console
	 *  @param locator$ the locator string.
	 * @return the context.
	 */		
	
	@Override
	public JContext instantiate(JMainConsole console, String locator$) {
		 if(debug)
		System.out.println("JAllCategoriesPanel::instantiate:locator="+locator$);
		Properties locator=Locator.toProperties(locator$);
		entihome$=locator.getProperty(Entigrator.ENTIHOME);
		if(entihome$!=null){
		    locator.setProperty(Entigrator.ENTIHOME,entihome$);
		    File file = new File(entihome$);
		    locator.setProperty(Locator.LOCATOR_TITLE, file.getName());
		    }
		ArrayList<JItemPanel>ipl=new ArrayList<JItemPanel>();
		
		 Entigrator entigrator=console.getEntigrator(entihome$);
		 FacetHandler[] fha=BaseHandler.listAllHandlers(entigrator);
		 if(debug)
			  System.out.println("JAllCategoriesPanel:instantiate:fha="+fha.length);
			 
		 if(fha!=null){
			 JFacetRenderer facetRenderer;
			 Properties cpLocator;
			 String cpLocator$;
			 JItemPanel itemPanel;
			 JCategoryPanel cp;
			 cp=new JCategoryPanel();
			  cpLocator$=cp.getLocator();
			  if(debug)
			  System.out.println("JAllCategoriesPanel:instantiate:cpLocator="+cpLocator$);
			  cpLocator=Locator.toProperties(cpLocator$);
			  cpLocator.setProperty(Entigrator.ENTIHOME,entihome$);
			 String fh$;
			 String extension$;
			 String frLocator$;
		for(FacetHandler fh:fha){
				 try{
			  fh$=fh.getClassName();
			 if(debug) 
			     System.out.println("JAllCategoriesPanel:instantiate:fh="+fh.getClass().getName());		 
			  facetRenderer=JConsoleHandler.getFacetRenderer(entigrator, fh);	  
			  frLocator$=facetRenderer.getLocator();
			  if(debug)
			  System.out.println("JAllCategoriesPanel:instantiate:renderer locator="+frLocator$);		 
			  cpLocator.setProperty(JCategoryPanel.RENDERER,facetRenderer.getClass().getName());
			  cpLocator.setProperty(Locator.LOCATOR_ICON_FILE,facetRenderer.getFacetIcon());
			
			  extension$=fh.getLocation();
			  if(extension$!=null){
				  cpLocator.setProperty(ExtensionHandler.EXTENSION,extension$);
				  cpLocator.setProperty(Locator.LOCATOR_ICON_CLASS_LOCATION,extension$);
			  }
			 // cpLocator$=Locator.toString(cpLocator);
			  if(debug)
			 System.out.println("AllCategoriesPanel:instantiate:category panel(begin)="+cpLocator$);		 
			  cpLocator.setProperty(JFacetRenderer.ONLY_ITEM,Locator.LOCATOR_TRUE);
			  cpLocator.setProperty(Locator.LOCATOR_TITLE,facetRenderer.getCategoryTitle());
			  if(debug)
			  System.out.println("AllCategoriesPanel:instantiate:finish category panel(finish)="+cpLocator$);
			  if(debug)
			  System.out.println("JAllCategoriesPanel:instantiate:cpLocator(2)="+cpLocator$);
			  cp.instantiate(console, Locator.toString(cpLocator));
			  cpLocator$=cp.getLocator();
			  itemPanel=new JItemPanel(console,cpLocator$);
			  putItem(fh$, itemPanel);
			   if(itemPanel!=null&&
				   !ipl.contains(itemPanel))
				      ipl.add(itemPanel); 
     		   }catch(Exception e){
     				Logger.getLogger(getClass().getName()).info(e.toString());
     			}	 
			 }
		 }
			
		Collections.sort(ipl,new ItemPanelComparator()); 
		putItems(ipl.toArray(new JItemPanel[0]));
		return this;
	}
	/**
	 * Get context title.
	 * @return the title string.
	 */
	@Override
	public String getTitle() {
		return "All categories";
	}
	/**
	 * Get context subtitle.
	 * @return the subtitle string.
	 */	
	@Override
	public String getSubtitle() {
		try{
		  File file = new File(entihome$);
		  return file.getName();
		}catch(Exception e){
			return null;
		}
	}
	/**
	 * Get context type.
	 * @return the type string.
	 */	
	@Override
	public String getType() {
		return "All categories";
	}
	/**
	 * Complete the context after
	 * remove it from the main console.
	 */	
	@Override
	public void close() {
		// TODO Auto-generated method stub
	}
	/**
	 * Get context menu.
	 * @return the context menu.
	 */		
	@Override
	public JMenu getContextMenu() {
		return null;
	}
	private void putItem(String key$,JItemPanel item){
		if(items==null)
			items=new Hashtable<String,JItemPanel>();
		items.put(key$, item);
	}
	private JItemPanel getItem(String key$){
		if(items==null)
			return null;
		return (JItemPanel)Support.getValue(key$, items);
	}
	@Override
	public void activate() {
		if(debug)
			System.out.println("JAllCatigoriesPanel:activate:begin");
		if(ignoreOutdate){
			
			ignoreOutdate=false;
			return;
		}
		if(debug)
			System.out.println("JAllCatigoriesPanel:activate:0");
		if(entihome$==null||console==null)
			return;
		Entigrator entigrator=console.getEntigrator(entihome$);
		if(!entigrator.store_outdated()){
			if(debug)
			System.out.println("JAllCatigoriesPanel:activate:up to date");
			return;
		}
		
		int n=new ReloadDialog(this).show();
		if(2==n){
			//cancel
			ignoreOutdate=true;
			return;
		}
		if(1==n){
			//replace
			entigrator.store_replace();//JConsoleHandler.execute(console, getLocator());
		}
		if(0==n){
			//reload
			entigrator.store_reload();
			 JConsoleHandler.execute(console, getLocator());
			}
		
		
	}
	@Override
	public String getWebView(Entigrator entigrator, String locator$) {
		try{
			if(debug)
				System.out.println("JAllCategoriesPanel:BEGIN:locator="+locator$);
				
			Properties locator=Locator.toProperties(locator$);
			String webHome$=locator.getProperty(WContext.WEB_HOME);
			String webRequester$=locator.getProperty(WContext.WEB_REQUESTER);
			if(debug)
			System.out.println("JAllCategoriesPanel:web home="+webHome$+ " web requester="+webRequester$);
			StringBuffer sb=new StringBuffer();
			sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
			sb.append("<html>");
			sb.append("<head>");
			sb.append(WUtils.getMenuBarScript());
			sb.append(WUtils.getMenuBarStyle());
		    sb.append("</head>");
		    sb.append("<body onload=\"onLoad()\" >");
		    sb.append("<ul class=\"menu_list\">");
		    sb.append("<li class=\"menu_item\"><a id=\"back\">Back</a></li>");
		    sb.append("<li class=\"menu_item\"><a href=\""+webHome$+"\">Home</a></li>");
		    String navLocator$=Locator.append(locator$, BaseHandler.HANDLER_CLASS, JBaseNavigator.class.getName());
		    navLocator$=Locator.append(navLocator$, Entigrator.ENTIHOME, entigrator.getEntihome());
		    String navUrl$=webHome$+"?"+WContext.WEB_LOCATOR+"="+Base64.encodeBase64URLSafeString(navLocator$.getBytes());
		    sb.append("<li class=\"menu_item\"><a href=\""+navUrl$+"\">Base</a></li>");
		    sb.append("<li class=\"menu_item\"><a href=\""+WContext.ABOUT+"\">About</a></li>");
		    sb.append("</ul>");
		    sb.append("<table><tr><td>Base:</td><td><strong>");
		    sb.append(entigrator.getBaseName());
		    sb.append("</strong></td></tr>");
		    sb.append("<tr><td>Context:</td><td><strong>");
		    	    sb.append("All categories");
		    	    sb.append("</strong></td></tr>");
		    sb.append("</table>");
		    FacetHandler[] fha=BaseHandler.listAllHandlers(entigrator);
			 if(fha!=null){
				
				 JFacetRenderer facetRenderer;

				 String fIcon$;
				 String fTitle$;
				 Properties fLocator=new Properties();
				 fLocator.setProperty(Entigrator.ENTIHOME, entigrator.getEntihome());
				 fLocator.setProperty(WContext.WEB_HOME, webHome$);
				 fLocator.setProperty(WContext.WEB_REQUESTER,getClass().getName());
				 ArrayList<String> sl=new ArrayList<String>();
				 Hashtable <String,String>tab=new Hashtable<String,String>();
				 String item$;
				 for(FacetHandler fh:fha){
					 try{
						 if(!entigrator.ent_existsAtType(fh.getType()))
							 continue;
				     //  fHandler$=fh.getClassName();
				       if(debug) 
				    	   System.out.println("JAllCategoriesPanel:getWebView:facet handler="+fh.getClass().getName());		 
				       facetRenderer=JConsoleHandler.getFacetRenderer(entigrator, fh);
                   	   fTitle$=facetRenderer.getCategoryTitle();
                   	   fIcon$=facetRenderer.getCategoryIcon(entigrator);
                   	 if(debug) 
				    	   System.out.println("JAllCategoriesPanel:getWebView:facet icon="+fIcon$);		 
				     
                   	   fLocator.setProperty(BaseHandler.HANDLER_CLASS,JCategoryPanel.class.getName());
                   	   fLocator.setProperty(JCategoryPanel.CATEGORY_TITLE,facetRenderer.getCategoryTitle());
                   	   fLocator.setProperty(EntityHandler.ENTITY_TYPE,facetRenderer.getEntityType());
                   	   fLocator.setProperty(JCategoryPanel.RENDERER,facetRenderer.getClass().getName());
                   	   
                   	   item$=getItem(fTitle$,fIcon$,webHome$,Locator.toString(fLocator));
                   	   if(!sl.contains(fTitle$))
                   	        sl.add(fTitle$);
                   	   tab.put(fTitle$, item$);

					 }catch(Exception ee){
						 Logger.getLogger(JAllCategoriesPanel.class.getName()).info(ee.toString());
					 }
				 }
				 Collections.sort(sl);
				 for(String s:sl)
					 sb.append(tab.get(s)+"<br>");
			 }
		    sb.append("<script>");
	        sb.append("function onLoad() {");
		    sb.append("initBack(\""+this.getClass().getName()+"\",\""+webRequester$+"\");");
		    sb.append("}");
		    sb.append("window.localStorage.setItem(\""+this.getClass().getName()+"\",\""+Base64.encodeBase64URLSafeString(locator$.getBytes())+"\");");
		    
	 	    sb.append("</script>");
		    sb.append("</body>");
		    sb.append("</html>");
		    return sb.toString();
		}catch(Exception e){
			Logger.getLogger(JBasesPanel.class.getName()).severe(e.toString());	
		}
		return null;
	}
	@Override
	public String getWebConsole(Entigrator entigrator, String locator$) {
		return null;
	}
	private String getItem(String fTitle$,String fIcon$, String webHome$,String locator$ ){
		  locator$=Locator.append(locator$,Entigrator.ENTIHOME, entihome$);
		  
		  //String iconTerm$="<img src=\"data:image/png;base64,"+WUtils.scaleIcon(fIcon$)+
		//		  "\" width=\"24\" height=\"24\" alt=\""+fTitle$+"\">";
		  String iconTerm$="<img src=\"data:image/png;base64,"+fIcon$+
				  "\" width=\"24\" height=\"24\" alt=\""+fTitle$+"\">";
	 
		  return iconTerm$+"<a href=\""+webHome$+"?"+WContext.WEB_LOCATOR+"="+Base64.encodeBase64URLSafeString(locator$.getBytes())+"\" >"+" "+fTitle$+"</a>";
	}
}
