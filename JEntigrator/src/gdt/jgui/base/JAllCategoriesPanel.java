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

import gdt.data.entity.BaseHandler;
import gdt.data.entity.FacetHandler;
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
/**
* This context displays a list of all categories (types of entities).   
* @author  Alexander Imas
* @version 1.0
* @since   2016-03-11
*/


public class JAllCategoriesPanel extends JItemsListPanel {
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
	
	    String icon$=Support.readHandlerIcon(null,JAllCategoriesPanel.class, "category.png");
	    //System.out.println("JAllCategoriesPanel:getLocator:icon="+icon$);
	    if(icon$!=null)
	    	locator.setProperty(Locator.LOCATOR_ICON,icon$);
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
      
		//System.out.println("BaseNavigator:instantiate:locator="+Locator.remove(locator$,Locator.LOCATOR_ICON));
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
		 if(fha!=null){
			 JFacetRenderer facetRenderer;
			 Properties cpLocator;
			 String cpLocator$;
			 JItemPanel itemPanel;
			 JCategoryPanel cp;
			 cp=new JCategoryPanel();
			  cpLocator$=cp.getLocator();
			  if(debug)
			  System.out.println("AllCategoriesPanel:instantiate:cpLocator="+cpLocator$);
			  cpLocator=Locator.toProperties(cpLocator$);
			  cpLocator.setProperty(Entigrator.ENTIHOME,entihome$);
			 String fh$;
			 for(FacetHandler fh:fha){
				 try{
			  fh$=fh.getClassName();
			 if(debug) 
			     System.out.println("AllCategoriesPanel:instantiate:fh="+fh.getClass().getName());		 
			  itemPanel=getItem(fh$);
			   if(itemPanel==null){
			  {
			  
			  facetRenderer=JConsoleHandler.getFacetRenderer(entigrator, fh.getClass().getName());
			if(debug)
			  System.out.println("AllCategoriesPanel:instantiate:renderer="+facetRenderer.getClass().getName());		 
			 
			  cpLocator.setProperty(JCategoryPanel.RENDERER,facetRenderer.getClass().getName());
			  cpLocator$=Locator.toString(cpLocator);
			  if(debug)
			 System.out.println("AllCategoriesPanel:instantiate:category panel(begin)="+cpLocator$);		 
				
			  cp.instantiate(console, cpLocator$);
			  if(debug)
			  System.out.println("AllCategoriesPanel:instantiate:finish category panel(finish)="+cpLocator$); 
			  cpLocator$=cp.getLocator();
			  cpLocator$=Locator.append(cpLocator$, JCategoryPanel.LIST_MEMBERS,Locator.LOCATOR_TRUE);
//			  if(debug)
	//			  System.out.println("AllCategoriesPanel:instantiate:category panel(2)="+cpLocator$);		 
			  
			  entigrator.putLocator(fh$, cpLocator$);
			  }
			  if(debug)
			  System.out.println("AllCategoriesPanel:instantiate:cpLocator(2)="+cpLocator$);
			  itemPanel=new JItemPanel(console,cpLocator$);
			  putItem(fh$, itemPanel);
			   }
			   if(itemPanel!=null&&
				   !ipl.contains(itemPanel))
				      ipl.add(itemPanel); 
			
     		   }catch(Exception e){
     				Logger.getLogger(getClass().getName()).info(e.toString());
     			}	 
			 }
		 }
	//	 System.out.println("AllCategoriesPanel:instantiate:END MAKE CATEGORY PANELS");
			
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
}
