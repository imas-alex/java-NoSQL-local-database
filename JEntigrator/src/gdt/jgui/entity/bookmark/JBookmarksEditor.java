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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.Stack;
import java.util.logging.Logger;
import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.facet.BookmarksHandler;
import gdt.data.entity.facet.QueryHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Identity;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JFacetRenderer;
import gdt.jgui.console.JItemPanel;
import gdt.jgui.console.JItemsListPanel;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.console.ReloadDialog;
import gdt.jgui.entity.JEntitiesPanel;
import gdt.jgui.entity.JEntityPrimaryMenu;
import gdt.jgui.entity.JReferenceEntry;
import gdt.jgui.tool.JIconSelector;
import gdt.jgui.tool.JTextEditor;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import org.apache.commons.codec.binary.Base64;
/**
 * The bookmarks editor context.
 * @author imasa
 *
 */
public class JBookmarksEditor extends JItemsListPanel implements JFacetRenderer,JRequester{
	private Logger LOGGER=Logger.getLogger(getClass().getName());
	/**
	 * The tag of the bookmark key.
	 */
	public final static String BOOKMARK_KEY="bookmark key";
	private static final String ACTION_CREATE_BOOKMARKS="action create bookmarks";
	String entihome$;
	String entityKey$;
	String entityLabel$;
	String requesterResponseLocator$;
	JMainConsole console;
	String locator$;
	JMenuItem[] mia;
	int cnt=0;
	protected String message$;
	Sack entity;
	boolean debug=false;
	boolean ignoreOutdate=false;
	private static final long serialVersionUID = 1L;
	/**
	 * The default constructor.
	 */
	public JBookmarksEditor() {
        super();
	}
	/**
	 * Get the panel to insert into the main console.
	 * @return the panel.
	 */
	@Override
	public JPanel getPanel() {
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
//				System.out.println("BookmarksEditor:getConextMenu:menu selected");
				menu.removeAll();
				if(mia!=null){
					for(JMenuItem mi:mia)
						menu.add(mi);
				}
				if(hasSelectedItems()){
					menu.addSeparator();	
				JMenuItem copyItem = new JMenuItem("Copy");
					copyItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							copy();
						}
					} );
					menu.add(copyItem);
				 JMenuItem deleteItem = new JMenuItem("Delete");
					deleteItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							try{
								int response = JOptionPane.showConfirmDialog(console.getContentPanel(), "Delete ?", "Confirm",
								        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
							   if (response == JOptionPane.YES_OPTION) {
								  String[] sa=JBookmarksEditor.this.listSelectedItems();
								  if(sa==null)
									  return;
								  String bookmarkKey$;
								  Entigrator entigrator=console.getEntigrator(entihome$);
								  entity=entigrator.getEntityAtKey(entityKey$);
								  for(String aSa:sa){
									  bookmarkKey$=Locator.getProperty(aSa, BOOKMARK_KEY);
									  if(bookmarkKey$==null)
										  continue;
				                   entity.removeElementItem("jbookmark", bookmarkKey$);
								  }
				                   
								  //entigrator.save(entity);
								  entigrator.replace(entity);
								  JConsoleHandler.execute(console,getLocator());
							   }
							}catch(Exception ee){
								LOGGER.severe(ee.toString());
							}
						}
					} );
					menu.add(deleteItem);
				}
				if(hasToPaste()){
					JMenuItem pasteItem = new JMenuItem("Paste");
					pasteItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							paste();
						}
					} );
					menu.add(pasteItem);
				}
				menu.addSeparator();
				JMenuItem doneItem = new JMenuItem("Done");
				doneItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						Entigrator entigrator=console.getEntigrator(entihome$);
						//entigrator.save(entity);
						entigrator.replace(entity);
						console.back();
					}
				} );
				menu.add(doneItem);
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

	private void copy(){
	try{
		console.clipboard.clear();
		String[] sa=listSelectedItems();
		if(sa!=null)
			for(String aSa:sa)
				console.clipboard.putString(aSa);
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
}
private boolean hasToPaste(){
	String[] sa=console.clipboard.getContent();
	if(sa==null||sa.length<1){
		return false;
	}
	return true;
}
private void paste(){
    try{
    	String[] sa=console.clipboard.getContent();
    	if(sa==null||sa.length<1)
    		return;
    	ArrayList<Core>cl=new ArrayList<Core>();
    	Entigrator entigrator=console.getEntigrator(entihome$);
    	entity=entigrator.getEntityAtKey(entityKey$);
    	Core[] ca=entity.elementGet("jbookmark");
    	if(ca==null){
    		entity.createElement("jbookmark");
    	}else
    	for(Core aCa:ca)
    		cl.add(aCa);
    	String title$;
    	String bookmarkKey$;
    	locator$=getLocator();
    	String requesterResponseLocator$=Locator.compressText(locator$);
    	for(String aSa:sa){
    		title$=Locator.getProperty(aSa, Locator.LOCATOR_TITLE);
    		if(title$==null)
    			continue;
    		bookmarkKey$=Locator.getProperty(aSa, BOOKMARK_KEY);
    		if(bookmarkKey$==null){
    			bookmarkKey$=Identity.key();
    			aSa=Locator.append(aSa, BOOKMARK_KEY, bookmarkKey$);
    			aSa=Locator.append(aSa,JRequester.REQUESTER_RESPONSE_LOCATOR, requesterResponseLocator$);
    			aSa=Locator.append(aSa, Locator.LOCATOR_CHECKABLE, Locator.LOCATOR_TRUE);
    		}
    		cl.add( new Core(title$,bookmarkKey$,aSa));
    	}
    	ca=cl.toArray(new Core[0]);
    	entity.elementReplace("jbookmark", ca);
    	entigrator.replace(entity);
    	JConsoleHandler.execute(console, getLocator());
    }catch(Exception e){
    	LOGGER.severe(e.toString());
    }
}
/**
 * Get the context locator.
 * @return the context locator.
 */
	@Override
	public String getLocator() {
		try{
			Properties locator=new Properties();
			locator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
			locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
			 locator.setProperty( JContext.CONTEXT_TYPE,getType());
			locator.setProperty(Locator.LOCATOR_TITLE,getTitle());
			if(entityLabel$!=null){
				locator.setProperty(EntityHandler.ENTITY_LABEL,entityLabel$);
			}
			if(entityKey$!=null)
				locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
			if(entihome$!=null)
				locator.setProperty(Entigrator.ENTIHOME,entihome$);
			if(entityLabel$!=null)
				locator.setProperty(EntityHandler.ENTITY_LABEL,entityLabel$);
			String icon$=Support.readHandlerIcon(null,JEntitiesPanel.class, "bookmark.png");
	    	locator.setProperty(Locator.LOCATOR_ICON,icon$);
			return Locator.toString(locator);
			}catch(Exception e){
	        Logger.getLogger(getClass().getName()).severe(e.toString());
	        return null;
			}
	}
	/**
	 * Create the context.
	 * @param console the main console.
	 * @param locator$ the locator string.
	 * @return the bookmarks editor context.
	 */
	@Override
	public JContext instantiate(JMainConsole console, String locator$) {
		try{
//			System.out.println("BookmarkskEditor.instantiate:locator="+locator$);
			this.console=console;
			Properties locator=Locator.toProperties(locator$);
			entihome$=locator.getProperty(Entigrator.ENTIHOME);
			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			Entigrator entigrator=console.getEntigrator(entihome$);
			entityLabel$=entigrator.indx_getLabel(entityKey$);
			 if(Locator.LOCATOR_TRUE.equals(locator.getProperty(JFacetRenderer.ONLY_ITEM)))
				 return this;
			requesterResponseLocator$=locator.getProperty(JRequester.REQUESTER_RESPONSE_LOCATOR);
            entity=entigrator.getEntityAtKey(entityKey$);
           // if(!entigrator.lock_set(entity)){
				//JOptionPane.showMessageDialog(this, entigrator.lock_message(entity));
		  //message$=entigrator.lock_message(entity);
	  //} 
            entityLabel$=entity.getProperty("label");
            Core[] ca=entity.elementGet("jbookmark");
            Core.sortAtType(ca);
            if(ca!=null){
            	ArrayList<JItemPanel>ipl=new ArrayList<JItemPanel>();
            	JBookmarkItem bookmarkItem;
            	for(Core aCa:ca){
            		aCa.value=Locator.append(aCa.value, BOOKMARK_KEY, aCa.name);
            		aCa.value=Locator.append(aCa.value, Locator.LOCATOR_TITLE, aCa.type);
            		bookmarkItem=new JBookmarkItem(console,aCa.value);
            		ipl.add(bookmarkItem);
            	}
            	Collections.sort(ipl,new ItemPanelComparator());
            	putItems(ipl.toArray(new JItemPanel[0]));
            }
		}catch(Exception e){
	        Logger.getLogger(getClass().getName()).severe(e.toString());
		}
		return this;
	}
/**
 * Get context title.
 * @return the context title.
 */
	@Override
	public String getTitle() {
		if(message$==null)
			return "Bookmarks";
		else
			return "Bookmarks"+message$;
		//return "Bookmarks";
	}
	/**
	 * Get context subtitle.
	 * @return the context subtitle.
	 */
	@Override
	public String getSubtitle() {
		if(entityLabel$!=null)
			return entityLabel$;
		return entihome$;
	}
	/**
	 * Get context type.
	 * @return the context type.
	 */
	@Override
	public String getType() {
		return "bookmarks editor";
	}
/**
 * Complete the context. No action.
 */
	@Override
	public void close() {
		Entigrator entigrator=console.getEntigrator(entihome$);
        entity=entigrator.getEntityAtKey(entityKey$);
	
	}
/**
 * Execute the response locator.
 * @param console the main console.
 * @param locator$ the response locator.
 * 
 */
	@Override
	public void response(JMainConsole console, String locator$) {
//		System.out.println("BookmarkEditor:response:locator="+locator$);
		try{
			Properties locator=Locator.toProperties(locator$);
			String action$=locator.getProperty(JRequester.REQUESTER_ACTION);
			if(ACTION_CREATE_BOOKMARKS.equals(action$)){
				String entihome$=locator.getProperty(Entigrator.ENTIHOME);
				String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
				String text$=locator.getProperty(JTextEditor.TEXT);
				Entigrator entigrator=console.getEntigrator(entihome$);  
				Sack bookmarks=entigrator.ent_new("bookmarks", text$);
				   bookmarks=entigrator.ent_assignProperty(bookmarks, "bookmarks", bookmarks.getProperty("label"));
				   bookmarks.putAttribute(new Core(null,"icon","bookmark.png"));
				   entigrator.save(bookmarks);
				   entigrator.saveHandlerIcon(JEntitiesPanel.class, "bookmark.png");
				   entityKey$=bookmarks.getKey();
				   JBookmarksEditor be=new JBookmarksEditor();
				   String beLocator$=be.getLocator();
				   beLocator$=Locator.append(beLocator$, Entigrator.ENTIHOME, entihome$);
				   beLocator$=Locator.append(beLocator$, EntityHandler.ENTITY_KEY, entityKey$);
				   JEntityPrimaryMenu.reindexEntity(console, beLocator$);
				   Stack<String> s=console.getTrack();
				   s.pop();
				   console.setTrack(s);
				   entigrator.store_replace();
				   JConsoleHandler.execute(console, beLocator$);
				   return;
				}
			String icon$=locator.getProperty(JIconSelector.ICON);
			String requesterResponseLocator$=locator.getProperty(JRequester.REQUESTER_RESPONSE_LOCATOR);
	        byte[] ba=Base64.decodeBase64(requesterResponseLocator$); 
			String bmLocator$=new String(ba,"UTF-8");
//			System.out.println("BookmarkEditor:response:bm locator="+bmLocator$);
		    Properties bmLocator=Locator.toProperties(bmLocator$);
			String entihome$=bmLocator.getProperty(Entigrator.ENTIHOME);
			String entityKey$=bmLocator.getProperty(EntityHandler.ENTITY_KEY);
			String bookmarkKey$=locator.getProperty(BOOKMARK_KEY);
			String text$=locator.getProperty(JTextEditor.TEXT);
			Entigrator entigrator=console.getEntigrator(entihome$);
			entity=entigrator.getEntityAtKey(entityKey$);
			Core bookmark=entity.getElementItem("jbookmark", bookmarkKey$);
			//System.out.println("BookmarkEditor:response:bookmark="+bookmarkKey$);
			if(JBookmarkItem.ACTION_RENAME.equals(action$)){
			bookmark.type=text$;
//			System.out.println("BookmarkEditor:response:text="+text$);
			}
			if(JBookmarkItem.ACTION_SET_ICON.equals(action$)){
			//	System.out.println("BookmarkEditor:response  icon="+icon$);
				String bookmarkIcon$=entigrator.readIconFromIcons(icon$);
			//	System.out.println("BookmarkEditor:response  bookmark icon="+icon$);
				String bookmarkLocator$=bookmark.value;
				bookmarkLocator$=Locator.append(bookmarkLocator$, Locator.LOCATOR_ICON, bookmarkIcon$);
				bookmark.value=  bookmarkLocator$;
			}
			entity.putElementItem("jbookmark", bookmark);
			entigrator.save(entity);
			String bmeLocator$=getLocator();
			bmeLocator$=Locator.append(bmeLocator$, Entigrator.ENTIHOME, entihome$);
			bmeLocator$=Locator.append(bmeLocator$, EntityHandler.ENTITY_KEY, entityKey$);
			JConsoleHandler.execute(console, bmeLocator$);
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
		}
/**
 * Add icon string to the locator.
 * @param locator$ the origin locator.
 * @return the locator.
 */
	@Override
	public String addIconToLocator(String locator$) {
		String icon$=Support.readHandlerIcon(null,JEntitiesPanel.class, "bookmarks.png");
	    if(icon$!=null)
	    	return Locator.append(locator$, Locator.LOCATOR_ICON,icon$);
	    else
	    	return locator$;
	}
	/**
	 * Get facet handler class name.
	 * @return the facet handler class name.
	 */
	@Override
	public String getFacetHandler() {
		return BookmarksHandler.class.getName();
	}
	/**
	 * Get the type of the entity for the facet.
	 * @return the entity type.
	 */
	@Override
	public String getEntityType() {
		return "bookmarks";
	}
	/**
	 * Get facet icon as a Base64 string. 
	 * @return the icon string.
	 */
	@Override
	public String getCategoryIcon() {
		return Support.readHandlerIcon(null,JEntitiesPanel.class, "bookmark.png");
	}
/**
 * Get category title for entities having the facet type.
 * @return the category title.
 */
	@Override
	public String getCategoryTitle() {
		return "Bookmarks";
	}
	/**
	 * Adapt cloned entity.
	 * @param console the main console.
	 * @param locator$ the locator string.
	 */
	@Override
	public void adaptClone(JMainConsole console, String locator$) {
		 try{
	    	  Entigrator entigrator=console.getEntigrator(entihome$);
	    	  BookmarksHandler bh=new BookmarksHandler();
	    	  String bh$=bh.getLocator();
	    	  bh$=Locator.append(bh$, Entigrator.ENTIHOME, entihome$);
	    	  bh$=Locator.append(bh$, EntityHandler.ENTITY_KEY, entityKey$);
	    	  bh$=Locator.append(bh$, EntityHandler.ENTITY_LABEL, entityLabel$);
	          bh.instantiate(bh$);
	          bh.adaptClone(entigrator);
	      }catch(Exception e){
	    	  Logger.getLogger(getClass().getName()).severe(e.toString());  
	      }

	}
	/**
	 * Adapt renamed entity.
	 * @param console the main console.
	 * @param locator$ the locator string.
	 */
	@Override
	public void adaptRename(JMainConsole console, String locator$) {
		 try{
	    	  Entigrator entigrator=console.getEntigrator(entihome$);
	    	  BookmarksHandler bh=new BookmarksHandler();
	    	  String bh$=bh.getLocator();
	    	  bh$=Locator.append(bh$, Entigrator.ENTIHOME, entihome$);
	    	  bh$=Locator.append(bh$, EntityHandler.ENTITY_KEY, entityKey$);
	    	  bh$=Locator.append(bh$, EntityHandler.ENTITY_LABEL, entityLabel$);
	          bh.instantiate(bh$);
	          bh.adaptRename(entigrator);
	      }catch(Exception e){
	    	  Logger.getLogger(getClass().getName()).severe(e.toString());  
	      }
		
	}
	/**
	 * Rebuild entity's facet related parameters.
	 * @param console the main console
	 * @param entigrator the entigrator.
	 * @param entity the entity.
	 */
	@Override
	public void reindex(JMainConsole console, Entigrator entigrator, Sack entity) {
    try{	
    	String bookmarksHandler$=BookmarksHandler.class.getName();
    	if(entity.getElementItem("fhandler", bookmarksHandler$)!=null){
			entity.putElementItem("jfacet", new Core(JBookmarksFacetAddItem.class.getName(),bookmarksHandler$,JBookmarksFacetOpenItem.class.getName()));
			entigrator.save(entity);
		}
    }catch(Exception e){
    	Logger.getLogger(getClass().getName()).severe(e.toString());
    }
	}
/**
 * Create a new entity of the facet type.
 * @param console the main console.
 * @param locator$ the locator string.
 * @return the new entity key.
 */
	@Override
	public String newEntity(JMainConsole console, String locator$) {
		try{
			Properties locator=Locator.toProperties(locator$);
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			JTextEditor textEditor=new JTextEditor();
		    String teLocator$=textEditor.getLocator();
		    teLocator$=Locator.append(teLocator$, Entigrator.ENTIHOME,entihome$);
		    teLocator$=Locator.append(teLocator$, JTextEditor.TEXT_TITLE,"New bookmrks");
		    String text$="NewBookmarks"+Identity.key().substring(0, 4);
		    teLocator$=Locator.append(teLocator$, JTextEditor.TEXT,text$);
		    JBookmarksEditor be=new JBookmarksEditor();
		    String beLocator$=be.getLocator();
		    beLocator$=Locator.append(beLocator$, Entigrator.ENTIHOME,entihome$);
		    beLocator$=Locator.append(beLocator$, EntityHandler.ENTITY_KEY,entityKey$);
		    beLocator$=Locator.append(beLocator$, BaseHandler.HANDLER_METHOD,"response");
		    beLocator$=Locator.append(beLocator$, JRequester.REQUESTER_ACTION,ACTION_CREATE_BOOKMARKS);
		    String requesterResponseLocator$=Locator.compressText(beLocator$);
		    teLocator$=Locator.append(teLocator$,JRequester.REQUESTER_RESPONSE_LOCATOR,requesterResponseLocator$);
		    
		    JConsoleHandler.execute(console, teLocator$);
		}catch(Exception ee){   
			LOGGER.severe(ee.toString());
		}
		return null;
	}
/**
 * Add referenced entities into the referenced entities list.
 * @param entigrator the entigrator.
 * @param entityKey$ the entity key.
 * @param rel the referenced entities list. 
 */
	@Override
	public void collectReferences(Entigrator entigrator, String entityKey$, ArrayList<JReferenceEntry> rel) {
		try{
			BookmarksHandler bh=new BookmarksHandler();
			String entityLocator$=EntityHandler.getEntityLocatorAtKey(entigrator, entityKey$);
			if(!bh.isApplied(entigrator, entityLocator$))
				return ;
			entity=entigrator.getEntityAtKey(entityKey$);
			Core[] ca=entity.elementGet("jbookmark");
			if(ca!=null){
			String referenceKey$;
			for(Core c:ca){
				try{
				//	System.out.println("JBookmarksEditor:collectReferences:c.name="+c.name);
					referenceKey$=Locator.getProperty(c.value, EntityHandler.ENTITY_KEY);
					if(referenceKey$==null||entityKey$.equals(referenceKey$)){
					//	System.out.println("JBookmarksEditor:collectReferences:cannot get reference locator");
						continue;
					}
					JReferenceEntry.getReference(entigrator,referenceKey$, rel);
			}catch(Exception ee){
					Logger.getLogger(getClass().getName()).info(ee.toString());
				}
			}
			}
			//System.out.println("JBookmarksEditor:collectReferences:FINISH");
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		}
	}
@Override
public void activate() {
	if(debug)
		System.out.println("JBookmarksEditor:activate:begin");
	if(ignoreOutdate){
		ignoreOutdate=false;
		return;
	}
	Entigrator entigrator=console.getEntigrator(entihome$);
	if(entity==null)
		return;
	if(!entigrator.ent_outdated(entity)){
		System.out.println("JBookmarksEditor:activate:up to date");
		return;
	}
	int n=new ReloadDialog(this).show();
	if(2==n){
		ignoreOutdate=true;
		return;
	}
	if(1==n){
		entigrator.save(entity);
		//JConsoleHandler.execute(console, getLocator());
	}
	if(0==n){
		 JConsoleHandler.execute(console, getLocator());
		}
	
}

}
