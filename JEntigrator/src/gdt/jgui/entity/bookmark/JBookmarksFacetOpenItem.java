package gdt.jgui.entity.bookmark;
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
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Logger;

import javax.activation.MimetypesFileTypeMap;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.commons.codec.binary.Base64;
import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.FacetHandler;
import gdt.data.entity.facet.BookmarksHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.jgui.base.JBaseNavigator;
import gdt.jgui.base.JBasesPanel;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JFacetOpenItem;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.console.WContext;
import gdt.jgui.console.WUtils;
import gdt.jgui.entity.JEntitiesPanel;
import gdt.jgui.entity.JEntityDigestDisplay;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.entity.folder.JFileOpenItem;
import gdt.jgui.entity.folder.JFolderPanel;
/**
 * This class represents the bookmarks facet item in the list
 * of  entity's facets.
 * @author imasa
 *
 */
public class JBookmarksFacetOpenItem extends JFacetOpenItem implements JRequester,WContext {
static boolean debug=false;
	/**
 * The default constructor.
 */
	public JBookmarksFacetOpenItem(){
			super();
		}
	private static final long serialVersionUID = 1L;
/**
 * Get the context locator.
 * @return the context locator.
 */
	@Override
	public String getLocator(){
		Properties locator=new Properties();
		locator.setProperty(Locator.LOCATOR_TITLE,"Bookmarks");
		locator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
		locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
		locator.setProperty(BaseHandler.HANDLER_METHOD,METHOD_OPEN_FACET);
		locator.setProperty( JContext.CONTEXT_TYPE,"Bookmarks facet");
		locator.setProperty(FACET_HANDLER_CLASS,BookmarksHandler.class.getName());
		if(entityKey$!=null)
			locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
		if(entihome$!=null){
			locator.setProperty(Entigrator.ENTIHOME,entihome$);
			locator.setProperty(Locator.LOCATOR_CHECKABLE,Locator.LOCATOR_TRUE);
		}
		locator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
		locator.setProperty(Locator.LOCATOR_ICON_CLASS,getClass().getName());
		locator.setProperty(Locator.LOCATOR_ICON_FILE,"bookmark.png");
  
	 	
		return Locator.toString(locator);
	}
/**
 * Execute the response locator.
 * @param console the main console.
 * @param locator$ the response locator.
 */
	@Override
	public void response(JMainConsole console, String locator$) {
		try{
			Properties locator=Locator.toProperties(locator$);
			String requesterAction$=locator.getProperty(JRequester.REQUESTER_ACTION);
			String requesterResponseLocator$=locator.getProperty(JRequester.REQUESTER_RESPONSE_LOCATOR);
			byte[] 	ba=Base64.decodeBase64(requesterResponseLocator$);
			String responseLocator$=new String(ba,"UTF-8");
			locator=Locator.toProperties(responseLocator$);
			entihome$=locator.getProperty(Entigrator.ENTIHOME);
			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			if(ACTION_DIGEST_CALL.equals(requesterAction$)){
			   String encodedSelection$=locator.getProperty(JEntityDigestDisplay.SELECTION);
			   ba=Base64.decodeBase64(encodedSelection$);
			   String selection$=new String(ba,"UTF-8");
			   locator=Locator.toProperties(selection$);
			   JEntityDigestDisplay edd=new JEntityDigestDisplay();
				String eddLocator$=edd.getLocator();
				eddLocator$=Locator.append(eddLocator$, Entigrator.ENTIHOME, entihome$);
				eddLocator$=Locator.append(eddLocator$,  EntityHandler.ENTITY_KEY, Locator.getProperty(responseLocator$,JEntityDigestDisplay.ROOT_ENTITY_KEY ));
				eddLocator$=Locator.append(eddLocator$, JEntityDigestDisplay.SELECTION, Locator.getProperty(responseLocator$,JEntityDigestDisplay.SELECTION ));
				JConsoleHandler.execute(console, eddLocator$);
				return;
			}
				entihome$=locator.getProperty(Entigrator.ENTIHOME);
				entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			JEntityFacetPanel efp=new JEntityFacetPanel();
			String efpLocator$=efp.getLocator();
			 efpLocator$=Locator.append(efpLocator$, Entigrator.ENTIHOME, entihome$);
			 efpLocator$=Locator.append(efpLocator$, EntityHandler.ENTITY_KEY, entityKey$);
			 JConsoleHandler.execute(console, efpLocator$);
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		}
	}
	/**
	 * Check if the facet can be removed from the entity.
	 * @return true if can be removed false otherwise.
	 */
	@Override
	public boolean isRemovable() {
		try{
			entihome$=Locator.getProperty(locator$, Entigrator.ENTIHOME);
			entityKey$=Locator.getProperty(locator$,EntityHandler.ENTITY_KEY);
			Entigrator entigrator=console.getEntigrator(entihome$);
			Sack entity =entigrator.getEntityAtKey(entityKey$);
			 if("bookmarks".equals(entity.getProperty("entity")))
				 return false;
			 return true;
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		return false;
		}
	}
	/**
	 * Get the facet name.
	 * @return the facet name.
	 */
	@Override
	public String getFacetName() {
		return "Bookmarks";
	}
	/**
	 * Get the facet icon as a Base64 string.
	 * @return the facet icon string.
	 */
	@Override
	public String getFacetIcon(Entigrator entigrator) {
		return Support.readHandlerIcon(null,JEntitiesPanel.class, "bookmark.png");
	}
/**
 * Get the facet renderer class name.
 * @return null.
 */
	@Override
	public String getFacetRenderer() {
		return JBookmarksEditor.class.getName();
	}
	/**
	 * Remove the facet from the entity.
	 */
	@Override
	public void removeFacet() {
		try{
		    Entigrator entigrator=console.getEntigrator(entihome$);
			Sack entity=entigrator.getEntityAtKey(entityKey$);
			entity.removeElementItem("fhandler", BookmarksHandler.class.getName());
			 entity.removeElementItem("jfacet", BookmarksHandler.class.getName());
			 entity.removeElement("jbookmark");
			entigrator.ent_alter(entity);
			entigrator.ent_takeOffProperty(entity, "bookmarks");
		}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
		}
	}
	/**
	 * Display the facet console.
	 * @param console the main console.
	 * @param locator$ the locator string.
	 */
	@Override
	public void openFacet(JMainConsole console, String locator$) {
		try{
//			System.out.println("JBookmarksFacetOpenItem:openFacet:locator="+locator$);
			Properties locator=Locator.toProperties(locator$);
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			JBookmarksEditor bookmarksEditor=new JBookmarksEditor();
			String beLocator$=bookmarksEditor.getLocator();
			beLocator$=Locator.append(beLocator$, Entigrator.ENTIHOME, entihome$);
			beLocator$=Locator.append(beLocator$, EntityHandler.ENTITY_KEY, entityKey$);
			beLocator$=Locator.append(beLocator$, BaseHandler.HANDLER_METHOD,"instantiate");
			JConsoleHandler.execute(console, beLocator$);
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		}
	}
/**
 * Get children nodes of the facet node for the digest view.
 * @return the children nodes of the facet node.
 */
	@Override
	public DefaultMutableTreeNode[] getDigest(Entigrator entigrator,String locator$) {
		try{
//			System.out.println("JBookmarksFacetOpenItem:getDigest:locator="+locator$);
			Properties locator=Locator.toProperties(locator$);
			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			Sack entity=entigrator.getEntityAtKey(entityKey$);
			Core[]ca=entity.elementGet("jbookmark");
			if(ca==null)
				return null;
			DefaultMutableTreeNode bookmarkNode;
				ArrayList<DefaultMutableTreeNode>nl=new ArrayList<DefaultMutableTreeNode>();
			String itemLocator$;
				for(Core aCa:ca){
				bookmarkNode=new DefaultMutableTreeNode();
				itemLocator$=aCa.value;
				if(debug)
					System.out.println("JBookmarksFacetOpenItem:getDigest:bookmark locator="+aCa.value);
				itemLocator$=Locator.append(itemLocator$, BaseHandler.HANDLER_CLASS, getClass().getName());
				itemLocator$=Locator.append(itemLocator$, Entigrator.ENTIHOME, entigrator.getEntihome());
				bookmarkNode.setUserObject(itemLocator$);
				nl.add(bookmarkNode);
			}
	//		System.out.println("JBookmarkFacetOpenItem:getDigest:nl="+nl.size());
			return nl.toArray(new DefaultMutableTreeNode[0]);
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		}
		return null;
	}
/**
 * Get the facet handler instance.
 * @return the facet handler instance.	
 */
	@Override
	public FacetHandler getFacetHandler() {
		return new BookmarksHandler();
	}
/**
 * Get the popup menu for the child node of the facet node 
 * in the digest view.
 * @return the popup menu.	
 */
	@Override
	public JPopupMenu getPopupMenu(final String digestLocator$) {
		JPopupMenu	popup = new JPopupMenu();
		JMenuItem openItem=new JMenuItem("Open");
		   popup.add(openItem);
		   openItem.setHorizontalTextPosition(JMenuItem.RIGHT);
		   openItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
				   try{
//					  System.out.println("JBookmarkFacetOpenItem:open:digest locator="+digestLocator$); 
					   Properties locator=Locator.toProperties(digestLocator$);
					   String encodedSelection$=locator.getProperty(JEntityDigestDisplay.SELECTION);
					   byte[]ba=Base64.decodeBase64(encodedSelection$);
					   String selection$=new String(ba,"UTF-8");
//					   System.out.println("JBookmarkFacetOpenItem:open:selection="+selection$);
					   locator=Locator.toProperties(selection$);
					   String entihome$=locator.getProperty(Entigrator.ENTIHOME);
					   String type$=locator.getProperty(Locator.LOCATOR_TYPE);
					   if(JFolderPanel.LOCATOR_TYPE_FILE.equals(type$)){
						   String filePath$=entihome$+"/"+locator.getProperty(JFolderPanel.FILE_PATH);
						   File file=new File(filePath$);
							Desktop.getDesktop().open(file);
							return;
					   }
					   if(JEntityDigestDisplay.LOCATOR_FACET_COMPONENT.equals(type$)){
						   String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
						   JBookmarksFacetOpenItem be=new JBookmarksFacetOpenItem();
						   String beLocator$=be.getLocator();
						   beLocator$=Locator.append(beLocator$, Entigrator.ENTIHOME, entihome$);
						   beLocator$=Locator.append(beLocator$, EntityHandler.ENTITY_KEY, entityKey$);
						   JConsoleHandler.execute(console,beLocator$);
						   return;
					   }
					   String bookmarkKey$=locator.getProperty(JBookmarksEditor.BOOKMARK_KEY);
					   Entigrator entigrator=console.getEntigrator(entihome$);
					   String componentKey$=locator.getProperty(JEntityDigestDisplay.COMPONENT_KEY);
					   Sack entity=entigrator.getEntityAtKey(componentKey$);
					   Core bookmark=entity.getElementItem("jbookmark", bookmarkKey$);
//					   System.out.println("JBookmarkFacetOpenItem:open:selection="+selection$);
					   JConsoleHandler.execute(console, bookmark.value);
				   }catch(Exception ee){
					   Logger.getLogger(JBookmarksFacetOpenItem.class.getName()).info(ee.toString());
				   }
				}
			    });
		return popup;
	}
@Override
public String getFacetIconName() {
	return "bookmark.png";
}
@Override
public String getWebView(Entigrator entigrator, String locator$) {
	try{
		Properties locator=Locator.toProperties(locator$);
		String webHome$=locator.getProperty(WContext.WEB_HOME);
		String entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
		String webRequester$=locator.getProperty(WContext.WEB_REQUESTER);
		if(debug)
		System.out.println("JBookmarksFacetOpenItem:web home="+webHome$+ " web requester="+webRequester$);
		 entityKey$=entigrator.indx_keyAtLabel(entityLabel$);
		    Sack entity=entigrator.getEntityAtKey(entityKey$);
	        Core[]	ca=entity.elementGet("jbookmark");
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
	    if(hasMultipleImages(ca)){
	    	String shLocator$=Locator.append(locator$, BaseHandler.HANDLER_CLASS, JFileOpenItem.class.getName());
		    shLocator$=Locator.append(shLocator$, Entigrator.ENTIHOME, entigrator.getEntihome());
		    shLocator$=Locator.append(shLocator$, EntityHandler.ENTITY_KEY, entityKey$);
		    shLocator$=Locator.append(shLocator$, WContext.WEB_REQUESTER, this.getClass().getName());
		    shLocator$=Locator.append(shLocator$,FACET_HANDLER_CLASS,BookmarksHandler.class.getName());
		    String shUrl$=webHome$+"?"+WContext.WEB_LOCATOR+"="+Base64.encodeBase64URLSafeString(shLocator$.getBytes());
	    	sb.append("<li class=\"menu_item\"><a href=\""+shUrl$+"\">Slideshow</a></li>");
	    }
	    sb.append("<li class=\"menu_item\"><a href=\""+WContext.ABOUT+"\">About</a></li>");
	    sb.append("</ul>");
	    sb.append("<table><tr><td>Base:</td><td><strong>");
	    sb.append(entigrator.getBaseName());
	    sb.append("</strong></td></tr><tr><td>Entity: </td><td><strong>");
	    sb.append(entityLabel$);
	    sb.append("</strong></td></tr>");
	    sb.append("<tr><td>Facet: </td><td><strong>Bookmarks</strong></td></tr>");
	    sb.append("</table>");
	   
        if(ca!=null){
        	sb.append("<script>");
        	String foiTitle$;
        	String foiLocator$;
        	String foiIcon$;
        	Properties foiLocator;
        	  Hashtable<String,String> tab=new Hashtable<String,String>();
            ArrayList <String>sl=new ArrayList<String>();
            String foiItem$;
            JEntityFacetPanel facetPanel=new JEntityFacetPanel();
            String facetPanelType$=facetPanel.getType();
            String foiType$;
           
            for(Core c:ca){
        		try{
        		foiLocator$=c.value;
        		if(debug)
            		System.out.println("JBookmarksFacetOpenItem:getWebView: bm locator="+foiLocator$);
        		foiTitle$=c.type;
        		foiLocator=Locator.toProperties(foiLocator$);
        		foiLocator.setProperty(Entigrator.ENTIHOME,entigrator.getEntihome());
                foiType$=foiLocator.getProperty(JContext.CONTEXT_TYPE);
                foiIcon$=JConsoleHandler.getIcon(entigrator,c.value);
                if(debug)
            		System.out.println("JBookmarksFacetOpenItem:getWebView: foiType="+foiType$+" facet panel type="+facetPanelType$);
        		
                if(facetPanelType$.equals(foiType$)){
                	if(debug)
                		System.out.println("JBookmarksFacetOpenItem:getWebView: make facet panel locator");
            	
                	foiLocator.setProperty(BaseHandler.HANDLER_CLASS,JEntityFacetPanel.class.getName());
                	//foiLocator.setProperty(Entigrator.ENTIHOME,entigrator.getEntihome());
                	foiLocator.setProperty(EntityHandler.ENTITY_LABEL,foiTitle$);
                	if(debug)
                	System.out.println("JBookmarksFacetOpenItem:getWebView:foi locator(prop)="+foiLocator);
                	sb.append("window.localStorage.setItem(\"back."+JEntityFacetPanel.class.getName()+"\",\""+this.getClass().getName()+"\");");
                }
               
 			   if(debug)
 			      System.out.println("JBookmarksFacetOpenItem:getWebView: foiLocator="+Locator.toString(foiLocator));
			foiItem$=getItem(foiIcon$, webHome$,foiTitle$,Locator.toString(foiLocator));
 			sl.add(foiTitle$);
 			tab.put(foiTitle$, foiItem$);
        	  }catch(Exception ee){
        		  System.out.println("JBookmarksFacetOpenItem:getWebView:"+ee.toString());
        	  }
        	}
            
            sb.append("</script>");
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
private boolean hasMultipleImages(Core[] ca){
	if(ca==null)
		return false;
	int i=0;
	MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
	mimetypesFileTypeMap.addMimeTypes("image png tif jpg jpeg bmp gif tiff"); 
	String fname$;
	 
	for(Core c:ca){
		fname$=Locator.getProperty(c.value, JFolderPanel.FILE_NAME);
		if(fname$!=null)
		if("image".equalsIgnoreCase(mimetypesFileTypeMap.getContentType(fname$)))
				return true;
	}
	
	 
		return false;
}
@Override
public String getWebConsole(Entigrator entigrator, String locator$) {
	return null;
}
private String getItem(String icon$, String url$, String title$,String foiLocator$){
	if(debug)
			System.out.println("JBookmarksFacetOpenItem:getItem: locator="+foiLocator$);
  
	String iconTerm$="<img src=\"data:image/png;base64,"+icon$+
			  "\" width=\"24\" height=\"24\" alt=\""+title$+"\">";
	foiLocator$=Locator.append(foiLocator$,WContext.WEB_HOME, url$);
	foiLocator$=Locator.append(foiLocator$,WContext.WEB_REQUESTER, this.getClass().getName());
	  return iconTerm$+"<a href=\""+url$+"?"+WContext.WEB_LOCATOR+"="+Base64.encodeBase64URLSafeString(foiLocator$.getBytes())+"\" >"+" "+title$+"</a>";
}
}
