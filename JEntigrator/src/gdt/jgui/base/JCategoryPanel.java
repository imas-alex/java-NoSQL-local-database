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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.apache.commons.codec.binary.Base64;

import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.FacetHandler;
import gdt.data.entity.facet.ExtensionHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
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
import gdt.jgui.entity.JArchivePanel;
import gdt.jgui.entity.JEntitiesPanel;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.entity.JReferenceEntry;
/**
* This context displays of all entities belonging to the category.
* @author  Alexander Imas
* @version 1.0
* @since   2016-03-11
*/
public class JCategoryPanel extends JItemsListPanel implements WContext{
	private static final long serialVersionUID = 1L;
	public static final String RENDERER = "renderer";
	public static final String LIST_MEMBERS = "list members";
	public static final String CATEGORY_TITLE = "category title";
	
	String entihome$;
	String renderer$;
	String entityType$;
	String categoryTitle$;
	String iconClass$;
	String iconFile$;
	JMenu menu;
	JMenuItem deleteItem;
	JMenuItem copyItem;
	private JMenuItem[] mia;
	static boolean debug=false; 
	boolean ignoreOutdate=false;
	boolean refresh=false;
	/**
	 * Default constructor
	 *  
	 */
	public JCategoryPanel() {
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
	    locator.setProperty(JItemsListPanel.POSITION,String.valueOf(getPosition()));
	    if(entihome$!=null)
	    	locator.setProperty(Entigrator.ENTIHOME,entihome$);
	    if(renderer$!=null){
		    	locator.setProperty(RENDERER,renderer$);
	    }
	    if(entityType$!=null){
	    	locator.setProperty(EntityHandler.ENTITY_TYPE,entityType$);
	    if(entihome$!=null){
	 	    Entigrator entigrator=console.getEntigrator(entihome$);	
	    	locator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
	if(debug)
		System.out.println("JCategoryPanel:getLocator:1");
	    	FacetHandler fh=BaseHandler.getHandler(entigrator, entityType$);
	    	
	    	if(debug)
	    		if(fh!=null)
		    		System.out.println("JCategoryPanel:getLocator:fh="+fh.getClass().getName());
		    		else
		    			System.out.println("JCategoryPanel:getLocator:cannot get handler for entity type=="+entityType$);
		    	
	    	JFacetRenderer fr=JConsoleHandler.getFacetRenderer(entigrator, fh);
	    	if(debug)
	    		if(fr!=null)
	    		System.out.println("JCategoryPanel:getLocator:fr="+fr.getClass().getName());
	    		else
	    			System.out.println("JCategoryPanel:getLocator:cannot get renderer for hanler=="+fh.getClass().getName());
	    	
	    	locator.setProperty(Locator.LOCATOR_ICON_CLASS,fr.getClass().getName());
	    	locator.setProperty(Locator.LOCATOR_ICON_FILE,fr.getFacetIcon());
	    	categoryTitle$=fr.getCategoryTitle();
	    }
	    }
		if(categoryTitle$!=null) 
		   locator.setProperty(Locator.LOCATOR_TITLE, categoryTitle$);
	   else
	       locator.setProperty(Locator.LOCATOR_TITLE, getTitle());
	   
	    locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
	    locator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
	    locator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
		
		if(iconFile$!=null)
		locator.setProperty(Locator.LOCATOR_ICON_FILE,iconFile$);
	    if(debug)
    		System.out.println("JCategoryPanel:getLocator finish:locator="+locator.toString());	
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
			System.out.println("JCategoryPanel:instantiate:locator="+locator$);
			
		clearItems();
		try{
		this.console=console;
		Properties locator=Locator.toProperties(locator$);
		entihome$=locator.getProperty(Entigrator.ENTIHOME);
		Entigrator entigrator=console.getEntigrator(entihome$);
		renderer$=locator.getProperty(RENDERER);
		String extension$=locator.getProperty(ExtensionHandler.EXTENSION);
		JFacetRenderer facetRenderer=null;
		if(extension$==null)
			facetRenderer=(JFacetRenderer)JConsoleHandler.getHandlerInstance(entigrator, renderer$);
		else
			facetRenderer=(JFacetRenderer)JConsoleHandler.getHandlerInstance(entigrator,renderer$, extension$);
		if(facetRenderer==null){
			//if(debug)
			 System.out.println("JCategoryPanel:instantiate:ERROR:cannot load renderer="+renderer$);
			 return null;
			
		}
		String frLocator$=facetRenderer.getLocator();
		frLocator$=Locator.append(frLocator$, Entigrator.ENTIHOME, entihome$);
		frLocator$=Locator.append(frLocator$, JFacetRenderer.ONLY_ITEM,Locator.LOCATOR_TRUE);
		facetRenderer.instantiate(console,frLocator$ );
		entityType$=facetRenderer.getEntityType();
		categoryTitle$=facetRenderer.getCategoryTitle();
		if(debug)
		 System.out.println("JCategoryPanel:instantiate:entity type="+entityType$+" category="+categoryTitle$);
		this.locator$=getLocator();
		String onlyItem$=locator.getProperty(JFacetRenderer.ONLY_ITEM);
		if(debug)
			 System.out.println("JCategoryPanel:instantiate:entity type="+entityType$+" category="+categoryTitle$+" only item="+onlyItem$);
			
		if(Locator.LOCATOR_TRUE.equals(onlyItem$))
		    return this;   
		  JItemPanel[] ipa=listCategoryMembers(console, this.locator$);
		  if(debug)
				 System.out.println("JCategoryPanel:instantiate:1");
		
	    if(ipa!=null){
		    putItems(ipa);
			try{
				pos=Integer.parseInt(locator.getProperty(POSITION));
				select(pos);
			}catch(Exception e){
					Logger.getLogger(getClass().getName()).info(e.toString());
			}
	    }
		}catch(Exception e){
		Logger.getLogger(getClass().getName()).info(e.toString());
		}
		return this;
	}
	/**
	 * Get context menu. 
	 * @return the context menu.
	 */	
	@Override
	public JMenu getContextMenu() {
		menu=super.getContextMenu();
		mia=null;
		 int cnt=menu.getItemCount();
		 if(cnt>0){
			 mia=new JMenuItem[cnt];
			for(int i=0;i<cnt;i++) 
			 mia[i]=menu.getItem(i);
		 }

		menu.addMenuListener(new MenuListener(){
			@Override
			public void menuSelected(MenuEvent e) {
			menu.removeAll();
			if(mia!=null){
				for(JMenuItem mi:mia)
					menu.add(mi);
			}
		JMenuItem doneItem = new JMenuItem("Done");
			doneItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JAllCategoriesPanel acp=new JAllCategoriesPanel();
					String acpLocator$=acp.getLocator();
					acpLocator$=Locator.append(acpLocator$,Entigrator.ENTIHOME,entihome$);
					JConsoleHandler.execute(console, acpLocator$);
					
				}
			} );
			menu.add(doneItem);
			JMenuItem newItem = new JMenuItem("New");
			newItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
				//	System.out.println("JCategoryPanel:new:renderer="+renderer$);
					Entigrator entigrator=console.getEntigrator(entihome$);
					if(entigrator.store_isBusy()){
			            	JBusyStorage.show(JCategoryPanel.this);
			            	return;
			            }
					 
					entigrator.store_lock();	
					JFacetRenderer facetRenderer=(JFacetRenderer)JConsoleHandler.getHandlerInstance(entigrator, renderer$);
					String fcLocator$=facetRenderer.getLocator();
					fcLocator$=Locator.append(fcLocator$, Entigrator.ENTIHOME, entihome$);
			    	facetRenderer.newEntity(console, fcLocator$);
			    	entigrator.store_release();
				}
			} );
			menu.add(newItem);
			if(hasSelectedItems()){
				menu.addSeparator();
			deleteItem = new JMenuItem("Delete");
			deleteItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int response = JOptionPane.showConfirmDialog(console.getContentPanel(), "Delete ?", "Confirm",
						        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					   if (response == JOptionPane.YES_OPTION) {
						  String[] sa=JCategoryPanel.this.listSelectedItems();
						  if(sa==null)
							  return;
						  String candidate$;
						  Entigrator entigrator=console.getEntigrator(entihome$);
						  Sack candidate;
						  for(String aSa:sa){
							try{
							  if(debug)
							  System.out.println("JCatehoryPanel:delete:aSa="+aSa);
							  candidate$=Locator.getProperty(aSa, EntityHandler.ENTITY_KEY);
							  candidate=entigrator.getEntityAtKey(candidate$);
							  if(candidate!=null){
								  entigrator.deleteEntity(candidate);
								  JConsoleHandler.execute(console,getLocator());
							  }
							}catch(Exception ee){
								Logger.getLogger(getClass().getName()).info(ee.toString()+":"+aSa);
							}
						  }
						 // entigrator.store_replace();
					   }
					
				}
			} );
			menu.add(deleteItem);
			copyItem = new JMenuItem("Copy");
			copyItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JItemPanel[] ipa=JCategoryPanel.this.getItems();
					ArrayList<String>sl=new ArrayList<String>();
					for(JItemPanel ip:ipa)
						if(ip.isChecked())
							sl.add(ip.getLocator());
					String[]sa=sl.toArray(new String[0]);
					console.clipboard.clear();
					for(String aSa:sa)
						console.clipboard.putString(aSa); 
				}
			} );
			menu.add(copyItem);
			JMenuItem archiveItem = new JMenuItem("Archive");
			archiveItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JItemPanel[] ipa=JCategoryPanel.this.getItems();
					Entigrator entigrator=console.getEntigrator(entihome$);
					Properties locator;
					ArrayList<String>sl=new ArrayList<String>();
					String entityKey$;
					for(JItemPanel ip:ipa){
						if(ip.isChecked()){
						    locator=Locator.toProperties(ip.getLocator());
						    entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
						    sl.add(entityKey$);
						}
					}
					String[] sa=sl.toArray(new String[0]);
					String[] ea=JReferenceEntry.getCoalition(console, entigrator, sa);
					JArchivePanel archivePanel=new JArchivePanel();
		        	String apLocator$=archivePanel.getLocator();
	        	  apLocator$=Locator.append(apLocator$,Entigrator.ENTIHOME,entihome$);
				  apLocator$=Locator.append(apLocator$, EntityHandler.ENTITY_LIST,Locator.toString(ea));
				  apLocator$=Locator.append(apLocator$,Locator.LOCATOR_ICON_CONTAINER, Locator.LOCATOR_ICON_CONTAINER_CLASS);
				  apLocator$=Locator.append(apLocator$,Locator.LOCATOR_ICON_CLASS,getClass().getName());
				  apLocator$=Locator.append(apLocator$,Locator.LOCATOR_ICON_FILE, "category.png"); 
				  JConsoleHandler.execute(console, apLocator$);
					}
			} );
			menu.add(archiveItem);
			
			}
			}
			@Override
			public void menuDeselected(MenuEvent e) {
			}
			@Override
			public void menuCanceled(MenuEvent e) {
			}	
		});

		return menu;
	}
	
	private  static JItemPanel[] listCategoryMembers(JMainConsole console,String locator$){
		try{
			if(debug)
			System.out.println("JCategoryPanel:listCategoryMembers:locator="+locator$);
			   Properties locator=Locator.toProperties(locator$);
			   String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			   String entityType$=locator.getProperty(EntityHandler.ENTITY_TYPE);
			   if(entityType$==null||"null".equals(entityType$))
				   return null;
			   Entigrator entigrator=console.getEntigrator(entihome$);
			   ArrayList <String>sl=new ArrayList<String>();
			   String[] sa=entigrator.indx_listEntities("entity",entityType$);
			   if(sa!=null)
				   for(String s:sa)
				   sl.add(s);
			   sa=entigrator.indx_listEntitiesAtPropertyName(entityType$);
			   if(sa!=null)
				   for(String s:sa)
					  if(!sl.contains(s)) 
					   sl.add(s);
			   sa=sl.toArray(new String[0]);
			   if(sa.length<1)
				   return null;
 		   ArrayList<JItemPanel>ipl=new ArrayList<JItemPanel>();
		   JItemPanel itemPanel;
	       String emLocator$;
	       Core [] ca=entigrator.indx_getMarks(sa);
	       if(debug)
				System.out.println("JCategoryPanel:listCategoryMembers:ca="+ca.length);
				
	       JEntityFacetPanel em;
	       Properties emLocator;
		   String iconFile$;
		   FacetHandler[] fha=BaseHandler.listAllHandlers(entigrator);
		   Hashtable<FacetHandler,JFacetRenderer>frtab=new Hashtable<FacetHandler,JFacetRenderer>();
		   JFacetRenderer facetRenderer;
	       for(Core c:ca){
			   try{
				  if(debug)
						System.out.println("JCategoryPanel:listCategoryMembers:c type="+c.type+" name="+c.name+" value="+c.value);
			   em=new JEntityFacetPanel();
			   emLocator$=em.getLocator();
			   emLocator=Locator.toProperties(emLocator$);
			   emLocator.setProperty(Entigrator.ENTIHOME , entihome$);
			   emLocator.setProperty(EntityHandler.ENTITY_KEY ,c.name);
			   emLocator.setProperty(Locator.LOCATOR_CHECKABLE, Locator.LOCATOR_TRUE);
			   emLocator.setProperty(Locator.LOCATOR_TITLE, c.value);
			   emLocator.setProperty(BaseHandler.HANDLER_SCOPE, JConsoleHandler.CONSOLE_SCOPE);
			   //System.out.println("JCategoryPanel:listCategoryMembers:1");
			   iconFile$=entigrator.ent_getIconAtKey(c.name);
			   if(iconFile$!=null&&!"sack.gif".equals(iconFile$)&&!"null".equals(iconFile$)){
				   emLocator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_ICONS);
				   emLocator.setProperty(Locator.LOCATOR_ICON_FILE,iconFile$);
				   }else{
						String type$=entigrator.getEntityType(c.name);
						boolean found=false;	
						  //System.out.println("JCategoryPanel:listCategoryMembers:2");
				    	   for(FacetHandler fh:fha){
				    		   //System.out.println("JCategoryPanel:listCategoryMembers:3:fh="+fh.getTitle()+" type="+type$);
				    		   if(type$==null)
				    			   continue;
				    		   if(type$.equals(fh.getType())){
				    			 facetRenderer=frtab.get(fh);
				    			 if(facetRenderer==null){
				    					facetRenderer= JConsoleHandler.getFacetRenderer(entigrator, fh);
				    					if(facetRenderer==null)
				    						continue;
				    					frtab.put(fh, facetRenderer);
				    			 }
				    			 emLocator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
				    			 emLocator.setProperty(Locator.LOCATOR_ICON_CLASS,facetRenderer.getClass().getName());
				    			 emLocator.setProperty(Locator.LOCATOR_ICON_FILE,facetRenderer.getFacetIcon());
				    			 found=true;
				    			 break;
				    		}
				    	   }
				    	   if(!found){
				    		 emLocator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
				    		 emLocator.setProperty(Locator.LOCATOR_ICON_CLASS,JEntitiesPanel.class.getName());
				    		 emLocator.setProperty(Locator.LOCATOR_ICON_FILE,"facet.png");
				    		
				    	   }
				    	     	   
				   }
			 
			   itemPanel=new JItemPanel(console,Locator.toString(emLocator));
	    	   	ipl.add(itemPanel); 
			   }catch(Exception ee){
				   Logger.getLogger(JCategoryPanel.class.getName()).info(ee.toString());
			   }
			   
		   }
	       entigrator.clearCache();
		   Collections.sort(ipl,new ItemPanelComparator());
		   return ipl.toArray(new JItemPanel[0]);
					   
		}catch(Exception e) {
        	Logger.getLogger(JEntitiesPanel.class.getName()).severe(e.toString());
            return null;
        }
	}
	
	private  static void listWebItems(Entigrator entigrator,String webHome$,String locator$,StringBuffer sb){
		try{
			Properties locator=Locator.toProperties(locator$);
		   String entityType$=locator.getProperty(EntityHandler.ENTITY_TYPE);
		   String pageBeginCount$=locator.getProperty(JEntitiesPanel.PAGE_BEGIN_COUNT);
		   if(entityType$==null||"null".equals(entityType$))
				   return ;	
		   ArrayList <String>sl=new ArrayList<String>();
			   String[] sa=entigrator.indx_listEntities("entity",entityType$);
			   if(sa!=null)
				   for(String s:sa)
				      sl.add(s);
			   sa=entigrator.indx_listEntitiesAtPropertyName(entityType$);
			   if(sa!=null)
				   for(String s:sa)
					   if(!sl.contains(s))
					   sl.add(s);
			   sa=sl.toArray(new String[0]);
			if(sa!=null){
			    //
				ArrayList<String>page=new ArrayList<String>();
				String[] ia;
			       sa=entigrator.indx_sortKeysAtlabel(sa);
			       if(debug)
			    	   System.out.println("JCategoryPanel:listWebitems:sorted keys="+sa.length);
			       int pageBegin=0;
			       if(pageBeginCount$!=null)
			    		try{ pageBegin=Integer.parseInt(pageBeginCount$);}catch(Exception e){}
			       int pageEnd=pageBegin+JEntitiesPanel.PAGE_SIZE;
			      // page.clear();
			       if(pageEnd<sa.length){
			    	   for(int i=pageBegin;i<pageEnd;i++)
			    		   page.add(sa[i]);
			    	   if(debug)
				    	   System.out.println("JEntitiesPanel:getWebView:page="+page.size());
				     	   
			    	   ia=JEntitiesPanel.listWebItems(entigrator,webHome$,page.toArray(new String[0]));
			    	   if(debug)
				    	   System.out.println("JEntitiesPanel:getWebView:items="+ia.length);
				     	   
			    	
			    	   for(String it:ia)
			    		   sb.append(it+"<br>");
			    	   locator$=Locator.append(locator$, JEntitiesPanel.PAGE_BEGIN_COUNT, String.valueOf(pageEnd));
			    		String moreItem$="<a href=\""+webHome$+"?"+WContext.WEB_LOCATOR+"="+Base64.encodeBase64URLSafeString(locator$.getBytes())+"\" ><strong>Next page </strong></a>";
			    		sb.append(moreItem$);
			       }else{
			    	   for(int i=0;i<sa.length;i++)
			    		   page.add(sa[i]);
			    	   
			    	   ia=JEntitiesPanel.listWebItems(entigrator,webHome$,page.toArray(new String[0]));
			    	   for(String it:ia)
			    		   sb.append(it+"<br>");
			       }
			}
		}catch(Exception e) {
        	Logger.getLogger(JCategoryPanel.class.getName()).severe(e.toString());

        }
	}
	
	private static String getItem(String icon$, String url$, String title$,String foiLocator$){
		if(debug)
				System.out.println("JCategoryPanel:getItem: locator="+foiLocator$);
	    
		String iconTerm$="<img src=\"data:image/png;base64,"+WUtils.scaleIcon(icon$)+
				  "\" width=\"24\" height=\"24\" alt=\""+title$+"\">";
		foiLocator$=Locator.append(foiLocator$,WContext.WEB_HOME, url$);
		foiLocator$=Locator.append(foiLocator$,WContext.WEB_REQUESTER, JCategoryPanel.class.getName());
		  return iconTerm$+"<a href=\""+url$+"?"+WContext.WEB_LOCATOR+"="+Base64.encodeBase64URLSafeString(foiLocator$.getBytes())+"\" >"+" "+title$+"</a>";
	}
	/**
	 * Get context title.
	 * @return the title string.
	 */	
	@Override
	public String getTitle() {
		if(categoryTitle$!=null)
			return categoryTitle$;
		else
		 return "Category panel";
	}
	/**
	 * Get context subtitle.
	 * @return the subtitle string.
	 */
	@Override
	public String getSubtitle() {
		return entihome$;
	}
	/**
	 * Get context type.
	 * @return the type string.
	 */
	@Override
	public String getType() {
		return "Category panel";
	}
	/**
	 * Complete the context after
	 * remove it from the main console.
	 */	
	@Override
	public void close() {
	   	  console.getTrack().pop();
	      console.getTrack().push(getLocator());
	}
	@Override
	public void activate() {
		if(debug)
			System.out.println("JCategoryPanel:activate:begin");
		if(ignoreOutdate){
			ignoreOutdate=false;
			return;
		}
		Entigrator entigrator=console.getEntigrator(entihome$);
		
		if(!entigrator.store_outdated()){
			if(debug)
			System.out.println("JCategoryPanel:activate:up to date");
			if(refresh){
				if(debug)
					System.out.println("JCategoryPanel:refresh");
				refresh=false;
				JConsoleHandler.execute(console,getLocator());
			}
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
			entigrator.store_replace();
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
				System.out.println("JCategoryPanel:BEGIN:locator="+locator$);
				
			Properties locator=Locator.toProperties(locator$);
			String webHome$=locator.getProperty(WContext.WEB_HOME);
			String webRequester$=locator.getProperty(WContext.WEB_REQUESTER);
			String category$=locator.getProperty(CATEGORY_TITLE);
			if(debug)
			System.out.println("JEntityCategoryPanel:web home="+webHome$+ " web requester="+webRequester$);
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
		    sb.append("</ul>");
		    sb.append("<table><tr><td>Base:</td><td><strong>");
		    sb.append(entigrator.getBaseName());
		    sb.append("</strong></td></tr>");
		    sb.append("<tr><td>Category:</td><td><strong>");
		    	    sb.append(category$);
		    	    sb.append("</strong></td></tr>");
		    sb.append("</table>");
		    listWebItems(entigrator,webHome$,locator$,sb);
		  //  if(sa!=null)
		  //  	for(String s:sa)
		   // 	sb.append(s+"<br>");
		  
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
	public static String getCategoryIcon(Entigrator entigrator,String entityType$){
		try{
			 FacetHandler[] fha=BaseHandler.listAllHandlers(entigrator);
			 JFacetRenderer facetRenderer;
			 for(FacetHandler fh:fha){
				 facetRenderer=JConsoleHandler.getFacetRenderer(entigrator, fh);
				 if(facetRenderer!=null&&entityType$.equals(facetRenderer.getEntityType()))
					 return facetRenderer.getCategoryIcon(entigrator);
			 }
		}catch(Exception e){
			Logger.getLogger(JAllCategoriesPanel.class.getName()).severe(e.toString());
		}
		return null;
	}
}
