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
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
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
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JFacetOpenItem;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.entity.JEntitiesPanel;
import gdt.jgui.entity.JEntityDigestDisplay;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.entity.folder.JFolderPanel;
/**
 * This class represents the bookmarks facet item in the list
 * of  entity's facets.
 * @author imasa
 *
 */
public class JBookmarksFacetOpenItem extends JFacetOpenItem implements JRequester {
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
		if(entihome$!=null)
			locator.setProperty(Entigrator.ENTIHOME,entihome$);
		 String icon$=Support.readHandlerIcon(null,JEntitiesPanel.class, "bookmark.png");
		    if(icon$!=null)
		    	locator.setProperty(Locator.LOCATOR_ICON,icon$);
		    if(entihome$!=null)   
	 	locator.setProperty(Locator.LOCATOR_CHECKABLE,Locator.LOCATOR_TRUE);
		return Locator.toString(locator);
	}
/**
 * Execute the response locator.
 * @param console the main console.
 * @param locator$ the response locator.
 */
	@Override
	public void response(JMainConsole console, String locator$) {
		//System.out.println("JBookmarksFacetItem:response:FACET locator:"+locator$);
		try{
			Properties locator=Locator.toProperties(locator$);
			String requesterAction$=locator.getProperty(JRequester.REQUESTER_ACTION);
			String requesterResponseLocator$=locator.getProperty(JRequester.REQUESTER_RESPONSE_LOCATOR);
			byte[] 	ba=Base64.decodeBase64(requesterResponseLocator$);
			String responseLocator$=new String(ba,"UTF-8");
			//System.out.println("JWebsetFacetItem:response:response locator="+responseLocator$);
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
			//System.out.println("JBookmarkFacetOpenItem:isRemovable.locator="+locator$);
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
	public String getFacetIcon() {
		return Support.readHandlerIcon(null,JEntitiesPanel.class, "bookmark.png");
	}
/**
 * Get the facet renderer class name.
 * @return null.
 */
	@Override
	public String getFacetRenderer() {
		// TODO Auto-generated method stub
		return null;
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
			entigrator.save(entity);
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
	public DefaultMutableTreeNode[] getDigest() {
		try{
//			System.out.println("JBookmarksFacetOpenItem:getDigest:locator="+locator$);
			Properties locator=Locator.toProperties(locator$);
			entihome$=locator.getProperty(Entigrator.ENTIHOME);
			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			Entigrator entigrator=console.getEntigrator(entihome$);
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
				itemLocator$=Locator.append(itemLocator$, BaseHandler.HANDLER_CLASS, getClass().getName());
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
						   String filePath$=locator.getProperty(JFolderPanel.FILE_PATH);
						   File file=new File(filePath$);
							Desktop.getDesktop().open(file);
							return;
					   }
					   if(JEntityDigestDisplay.LOCATOR_FACET_COMPONENT.equals(type$)){
						   String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
						   JBookmarksEditor be=new JBookmarksEditor();
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
}
