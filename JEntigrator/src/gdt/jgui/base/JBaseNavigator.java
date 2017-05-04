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
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Logger;
import gdt.data.entity.ArchiveHandler;
import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntitiesArchiveFilter;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.FacetHandler;
import gdt.data.entity.facet.ExtensionMain;
import gdt.data.grain.Core;
import gdt.data.grain.Identity;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.data.store.FileExpert;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JItemPanel;
import gdt.jgui.console.JItemsListPanel;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.WContext;
import gdt.jgui.console.WUtils;
import gdt.jgui.entity.JArchivePanel;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.entity.JEntityPrimaryMenu;
import gdt.jgui.entity.JReferenceEntry;
import gdt.jgui.entity.bookmark.JBookmarksEditor;
import gdt.jgui.tool.JSearchPanel;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.codec.binary.Base64;
/**
* This context displays items for database navigation:
*  Design - show the general-purpose control for low-level database management
*  Search - show the search control to find an entity by label
*  All categories - display the all categories list.    
* @author  Alexander Imas
* @version 1.0
* @since   2016-03-11
*/
public class JBaseNavigator extends JItemsListPanel implements WContext{
	private static final long serialVersionUID = 1L;
	boolean debug=true;
private Logger LOGGER=Logger.getLogger(JBaseNavigator.class.getName());
	String entihome$;
	boolean keep=true;
	
	/**
	 * Default constructor
	 *  
	 */
	public JBaseNavigator() {
		super();

	}
	/**
	 * Get context menu:
	 * Install extension - choose an extension jar and install it.
	 * Import entities 	 - choose archive file containing entities and
	 * 					   import them.
	 * Undo 			-  cancel last changes.
	 * Commit			-  commit all changes.
	 * Reindex			-  rebuild database index.
	 * Archive			-  compress the database into an archive file.
	 * Delete			-  delete the database.
	 * @return the context menu.
	 */	
	@Override
	public JMenu getContextMenu() {
		menu=new JMenu("Context");
				menu.addMenuListener(new MenuListener(){
				@Override
				public void menuSelected(MenuEvent e) {
				menu.removeAll();	
				JMenuItem installExtension = new JMenuItem("Install extension");
				installExtension.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						JFileChooser chooser = new JFileChooser(); 
					    chooser.setCurrentDirectory(new java.io.File(System.getProperty("user.home")));
					    chooser.setDialogTitle("Install extension");
					    FileFilter filter = new FileNameExtensionFilter("JAR file", "jar");
					    chooser.setFileFilter(filter);
					    if (chooser.showOpenDialog(JBaseNavigator.this) == JFileChooser.APPROVE_OPTION) { 
					    	
					    	String extension$=chooser.getSelectedFile().getPath();
					   	if(debug){
					    	System.out.println("BaseNavigator.install extension="+extension$);
					    	System.out.println("Working Directory = " +
					                System.getProperty("user.dir"));
					   	}
					    	try{
					    		String[] args=new String[]{entihome$};
						    	URL[] urls = { new URL("jar:file:"+extension$+"!/") };
								URLClassLoader cl = URLClassLoader.newInstance(urls);
								Class<?>cls=cl.loadClass("gdt.data.extension.Main");
								ExtensionMain em=(ExtensionMain)cls.newInstance();
								em.main(args);
					    	}catch(Exception ee){
					    		LOGGER.severe(ee.toString());
					    	}
					    					      }
					    else {
					    	Logger.getLogger(JMainConsole.class.getName()).info(" no selection");
					      }
					     }
				} );
				
				menu.add(installExtension);
				JMenuItem importEntities = new JMenuItem("Import entities");
				importEntities.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						JFileChooser chooser = new JFileChooser(); 
					    chooser.setCurrentDirectory(new java.io.File(System.getProperty("user.home")));
					    chooser.setDialogTitle("Import entities");
					    EntitiesArchiveFilter filter =new EntitiesArchiveFilter();
					    chooser.setFileFilter(filter);
					    if (chooser.showOpenDialog(JBaseNavigator.this) == JFileChooser.APPROVE_OPTION) { 
					    	String file$=chooser.getSelectedFile().getPath();
					    	String undo$=ArchiveHandler.insertEntities(console, entihome$, file$);
					    	if(undo$!=null){
					    		 Entigrator entigrator=console.getEntigrator(entihome$);
					    		 Sack undo=entigrator.getEntityAtKey(undo$);
					    		 undo.putElementItem("fhandler", new Core(null,"gdt.data.entity.facet.BookmarksHandler",null));
					    		    undo.putElementItem("jfacet", new Core("gdt.jgui.entity.bookmark.JBookmarksFacetAddItem","gdt.data.entity.facet.BookmarksHandler","gdt.jgui.entity.bookmark.JBookmarksFacetOpenItem"));
					    		    entigrator.save(undo);
					    		    entigrator.ent_assignProperty(undo, "bookmarks", undo.getProperty("label"));
					    		    String undoLocator$=EntityHandler.getEntityLocator(entigrator, undo);
					    		    JEntityPrimaryMenu.reindexEntity(console, undoLocator$);
					    			String[]sa=undo.elementList("entity");
					    			if(sa!=null){
					    				String entityLocator$;
					    				Sack entity;
					    				console.clipboard.clear();
					    				FacetHandler[] fha=BaseHandler.listAllHandlers(entigrator);
					    				for(String s:sa){
					    					entity=entigrator.getEntityAtKey(s);
					    					if(entity==null)
					    						continue;
					    					EntityHandler.completeMigration(entigrator, s, fha);
					    					entityLocator$=EntityHandler.getEntityLocator(entigrator, entity);
					    					JEntityPrimaryMenu.reindexEntity(console,entityLocator$);
					    					console.clipboard.putString(entityLocator$);
					    				}
					    				undo=putBookmarks(console,undo);
					    				entigrator.save(undo);
					    				updateBookmarks(entigrator,undo);
					    			}
					    			
					    	}
					    	
				      }
					    else {
					    	Logger.getLogger(JMainConsole.class.getName()).info(" no selection");
					      }
					     }
					
				} );
				
				menu.add(importEntities);
				if(hasToPaste()){
					menu.addSeparator();
					JMenuItem paste = new JMenuItem("Paste");
					paste.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							 int response = JOptionPane.showConfirmDialog(console.getContentPanel(), "Keep existing entities ?", "Confirm",
					  			        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					    		 
							 if (response == JOptionPane.YES_OPTION)
								 keep=true;
					    			  //paste(true);
					    		  else 
					    			  keep=false;
					    		     //paste(false);
					    	ProgressDialog pd=new ProgressDialog(console.getFrame(),Paste,"Wait for paste..");	
					    	pd.setLocationRelativeTo(JBaseNavigator.this);
					    	pd.setVisible(true);
						}
					} );
					menu.add(paste);
				}
				menu.addSeparator();
				if(hasUndo()){	
					JMenuItem undo = new JMenuItem("Undo");
					undo.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							undo(console,entihome$);
						}
					} );
					menu.add(undo);
				
				JMenuItem commit = new JMenuItem("Commit");
				commit.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						  int response = JOptionPane.showConfirmDialog(console.getContentPanel(), "Delete all undo's ?", "Confirm",
							        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						   if (response == JOptionPane.YES_OPTION) {  	
						try{
					    		Entigrator entigrator=console.getEntigrator(entihome$);
					    		String[] sa=entigrator.indx_listEntities("entity", "undo");
					    		if(sa!=null){
					    			Sack undo;
					    			for(String s:sa){
					    				undo=entigrator.getEntityAtKey(s);
					    				entigrator.deleteEntity(undo);
					    			}
					    		}
					    	}catch(Exception ee){
					    		LOGGER.severe(ee.toString());
					    	}
				      }
					}
					    			
				} );
				menu.add(commit);
				menu.addSeparator();
				}
				
				JMenuItem reindexItem = new JMenuItem("Reindex");
				reindexItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
					 //reindex();
						ProgressDialog pd=new ProgressDialog(console.getFrame(),Reindex,"Wait for reindex..");	
				    	pd.setLocationRelativeTo(JBaseNavigator.this);
				    	pd.setVisible(true);
					}
				   
				});
				menu.add(reindexItem);
				
				JMenuItem archiveItem = new JMenuItem("Archive");
				archiveItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						
						JArchivePanel jap=new JArchivePanel();
						String japLocator$=jap.getLocator();
						japLocator$=Locator.append(japLocator$, Entigrator.ENTIHOME, entihome$);
						japLocator$=Locator.append(japLocator$, ArchiveHandler.ARCHIVE_CONTENT,ArchiveHandler.ARCHIVE_CONTENT_DATABASE);
						JConsoleHandler.execute(console, japLocator$);
						
					}
				});
				menu.add(archiveItem);
				menu.addSeparator();
				JMenuItem delete = new JMenuItem("Delete");
				delete.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						  int response = JOptionPane.showConfirmDialog(console.getContentPanel(), "Delete database ?", "Confirm",
							        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						   if (response == JOptionPane.YES_OPTION) {  	
						try{
					    	FileExpert.clear(entihome$);
					    	File entihome=new File (entihome$);
					    	entihome.delete();
					    	console.back();
					    	}catch(Exception ee){
					    		LOGGER.severe(ee.toString());
					    	}
				      }
					}
					    			
				} );
				menu.add(delete);
				}
				@Override
				public void menuDeselected(MenuEvent e) {
					// TODO Auto-generated method stub
				}

				@Override
				public void menuCanceled(MenuEvent e) {
					
				}
			});
		return menu;
	}
	
//	Runnable reindex=new Runnable 
	
	//private void reindex(){
	Runnable Reindex=new Runnable(){
	public void run(){
		Entigrator entigrator=console.getEntigrator(entihome$);
		entigrator.setSingleMode(true);
		entigrator.setBulkMode(true);
		entigrator.indx_reindex(null);
		String [] sa=entigrator.indx_listEntities();
		Sack entity;
		String entityLocator$;
		for(String s:sa){
			//System.out.println("Entigrator:Reindex:s="+s);
			entity=entigrator.getEntityAtKey(s);
			if(entity==null)
				continue;
			if(!"extension".equals(entity.getProperty("entity")))
				continue;
		entityLocator$=EntityHandler.getEntityLocator(entigrator, entity);
		 JEntityPrimaryMenu.reindexEntity(console,entityLocator$);
		}
		//System.out.println("Entigrator:Reindex:finish");
		
		entigrator.setSingleMode(false);
		entigrator.setBulkMode(false);
	}
	};
	/**
	 * Get context locator. 
	 * @return the locator.
	 */
	
	
	@Override
	public String getLocator() {
		Properties locator=new Properties();
	    locator.setProperty(Locator.LOCATOR_TYPE, JContext.CONTEXT_TYPE);
	    locator.setProperty(JContext.CONTEXT_TYPE,getType());
	    if(entihome$!=null){
	      locator.setProperty(Entigrator.ENTIHOME,entihome$);
	    File file = new File(entihome$);
	    locator.setProperty(Locator.LOCATOR_TITLE, file.getName());
	    }
	    locator.setProperty(Locator.LOCATOR_ICON_CONTAINER, Locator.LOCATOR_ICON_CONTAINER_CLASS);
	    locator.setProperty(Locator.LOCATOR_ICON_CLASS,getClass().getName());
	    locator.setProperty(Locator.LOCATOR_ICON_FILE, "base.png"); 
	    locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
	    locator.setProperty(BaseHandler.HANDLER_CLASS,JBaseNavigator.class.getName());
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
	//	System.out.println("JBaseNavigator:instantiate:locator="+Locator.remove(locator$,Locator.LOCATOR_ICON));
		this.console=console;
		Properties locator=Locator.toProperties(locator$);
		entihome$=locator.getProperty(Entigrator.ENTIHOME);
		if(entihome$!=null){
		    locator.setProperty(Entigrator.ENTIHOME,entihome$);
		    File file = new File(entihome$);
		    locator.setProperty(Locator.LOCATOR_TITLE, file.getName());
		    }
		ArrayList<JItemPanel>ipl=new ArrayList<JItemPanel>();
		JDesignPanel designPanel=new JDesignPanel();
		String itemLocator$=designPanel.getLocator();
		itemLocator$=Locator.append(itemLocator$, Locator.LOCATOR_ICON_CONTAINER, Locator.LOCATOR_ICON_CONTAINER_CLASS);
		itemLocator$=Locator.append(itemLocator$, Locator.LOCATOR_ICON_CLASS, getClass().getName());
		itemLocator$=Locator.append(itemLocator$, Locator.LOCATOR_ICON_FILE, "design.png");
		itemLocator$=Locator.append(itemLocator$,Entigrator.ENTIHOME,entihome$);
		//designPanel.instantiate(console, itemLocator$);
		JItemPanel designItem=new JItemPanel(console,itemLocator$);
		ipl.add(designItem);
		//System.out.println("JBaseNavigator:design panel done");
		JSearchPanel searchPanel=new JSearchPanel();
		 itemLocator$=searchPanel.getLocator();
		itemLocator$=Locator.append(itemLocator$, Locator.LOCATOR_ICON_CONTAINER, Locator.LOCATOR_ICON_CONTAINER_CLASS);
		itemLocator$=Locator.append(itemLocator$, Locator.LOCATOR_ICON_CLASS, getClass().getName());
		itemLocator$=Locator.append(itemLocator$, Locator.LOCATOR_ICON_FILE, "zoom.png");
		itemLocator$=Locator.append(itemLocator$,Entigrator.ENTIHOME,entihome$);
		JItemPanel searchItem=new JItemPanel(console, itemLocator$);
		ipl.add(searchItem);
		//System.out.println("JBaseNavigator:search panel done");
		JAllCategoriesPanel allCategoriesPanel=new JAllCategoriesPanel();
		 itemLocator$=allCategoriesPanel.getLocator();
			itemLocator$=Locator.append(itemLocator$, Locator.LOCATOR_ICON_CONTAINER, Locator.LOCATOR_ICON_CONTAINER_CLASS);
			itemLocator$=Locator.append(itemLocator$, Locator.LOCATOR_ICON_CLASS, getClass().getName());
			itemLocator$=Locator.append(itemLocator$, Locator.LOCATOR_ICON_FILE, "category.png");
			itemLocator$=Locator.append(itemLocator$,Entigrator.ENTIHOME,entihome$);
		
		JItemPanel allCategoriesItem=new JItemPanel(console, itemLocator$);
		ipl.add(allCategoriesItem);
		//System.out.println("JBaseNavigator:categories panel done");
		putItems(ipl.toArray(new JItemPanel[0]));
		return this;
	}
	/**
	 * Get context title.
	 * @return the title string.
	 */

	@Override
	public String getTitle() {
		try{
		File entihome=new File(entihome$);
		return entihome.getName();
		}catch(Exception e){
		return "Entigrator";
		}
	}
	/**
	 * Get context type.
	 * @return the type string.
	 */
	@Override
	public String getType() {
		return "Base navigator";
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
	 * Get context subtitle.
	 * @return the subtitle string.
	 */	
	@Override
	public String getSubtitle() {

		return entihome$;
	}
	private boolean hasUndo(){
		Entigrator entigrator =console.getEntigrator(entihome$);
		String[] sa=entigrator.indx_listEntities("entity", "undo");
		if(sa!=null&&sa.length>0)
			return true;
		else
			return false;
	}
	private static void undo(JMainConsole console ,String entihome$){
		try{
			Entigrator entigrator =console.getEntigrator(entihome$);
			String[] sa=entigrator.indx_listEntities("entity", "undo");
			if(sa==null)
				return;
			String label$;
			Sack undo=null;
			int cnt;
			int max=0;
			for(String s:sa){
				label$=entigrator.indx_getLabel(s);
				cnt=Integer.parseInt(label$.substring(5, label$.length()));
				if (cnt>max){
					max=cnt;
					undo=entigrator.getEntityAtKey(s);
				}
			}

			Sack entity;
			sa=undo.elementList("entity");
			if(sa!=null)
				for(String s:sa){
					entity=entigrator.getEntityAtKey(s);
					if(entity!=null)
					    entigrator.deleteEntity(entity);
				}
			sa=undo.elementList("icon");
			if(sa!=null){
				String icons$=entihome$+"/"+Entigrator.ICONS;
				File icon;
				for(String s:sa){
					//System.out.println("BaseNavigator:undo:icon="+s);
					icon =new File(icons$+"/"+s);
					if (icon.exists())
						icon.delete();
				}
			}
			sa=ArchiveHandler.insertCache(entigrator, entigrator.ent_getHome(undo.getKey()), false);
			if(sa!=null){
				String entityLocator$;
				
				for(String s:sa){
				entity=entigrator.getEntityAtKey(s);
				entityLocator$=EntityHandler.getEntityLocator(entigrator, entity);
				JEntityPrimaryMenu.reindexEntity(console, entityLocator$);
				}
			}
			entigrator.deleteEntity(undo);
		}catch(Exception e){
			Logger.getLogger(JBaseNavigator.class.getName()).severe(e.toString());
		}
	}
private  boolean hasToPaste(){
	try{
	String[] sa=console.clipboard.getContent();
	if(sa==null||sa.length<1)
		return false;
	Properties clipLocator;
	String foreignEntihome$;
	for(String s:sa){
		clipLocator=Locator.toProperties(s);
		foreignEntihome$=clipLocator.getProperty(Entigrator.ENTIHOME);
		if(foreignEntihome$==null||entihome$.equals(foreignEntihome$))
			continue;
		if(clipLocator.getProperty(EntityHandler.ENTITY_KEY)!=null)
			return true;
	}
	}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
	}
	return false;
}
private  JReferenceEntry[] getToPaste(){
	try{
	String[] sa=console.clipboard.getContent();
	if(sa==null||sa.length<1)
		return null;
	Properties clipLocator;
	String foreignEntihome$=null;
	ArrayList<String>sl=new ArrayList<String>();
	ArrayList<String>eka=new ArrayList<String>();
	String entityKey$;
	for(String s:sa){
		clipLocator=Locator.toProperties(s);
		foreignEntihome$=clipLocator.getProperty(Entigrator.ENTIHOME);
		if(foreignEntihome$==null||entihome$.equals(foreignEntihome$))
			continue;
		entityKey$=clipLocator.getProperty(EntityHandler.ENTITY_KEY);
		if(entityKey$!=null){
			eka.add(entityKey$);
			sl.add(s);
		}
	}
	sa=eka.toArray(new String[0]);
	if(sa==null||foreignEntihome$==null)
		return null;
	Entigrator foreignEntigrator=console.getEntigrator(foreignEntihome$);
	ArrayList<JReferenceEntry>rel=JReferenceEntry.collectReferences(console, foreignEntigrator, sa);
	return rel.toArray(new JReferenceEntry[0]);
	}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
	}
	return null;
}
Runnable Paste=new Runnable(){
	public void run(){
	try{
		JReferenceEntry[] jrea= getToPaste();
	Entigrator entigrator=console.getEntigrator(entihome$);
	int cnt=0;
	String[] sa=entigrator.indx_listEntities("entity", "undo");
	if(sa!=null){
		String label$;
		int max=0;
		for(String s:sa){
			label$=entigrator.indx_getLabel(s);
			if(label$==null)
				continue;
			try{
			cnt=Integer.parseInt(label$.substring(5, label$.length()));
			}catch(Exception ee){}
			if (cnt>max){
				max=cnt;
			}
		}
		cnt=max+1;
	}
	if(debug)
		System.out.println("JBasenavigator:Paste:BEGIN");
	Sack undo=entigrator.ent_new("undo", "undo_"+String.valueOf(cnt));
	entigrator.save(undo);
	entigrator.ent_reindex(undo);
	entigrator.ent_assignProperty(undo, "folder", undo.getProperty("label"));
	undo.createElement("entity");
	undo.createElement("icon");
	undo.createElement("jbookmark");
	File sourceEntity;
	File undoEntity;
	File oldEntity;
	Sack pastedEntity;
	File undoIcon;
	File oldIcon;
	File newIcon;
	File undoHome=new File(entigrator.ent_getHome(undo.getKey()));
	File undoBodies=new File(undoHome.getPath()+"/"+Entigrator.ENTITY_BASE+"/data/");
	File undoIcons=new File(undoHome.getPath()+"/"+Entigrator.ICONS);
	File oldEntityHome;
	File undoEntityHome;
	File sourceEntityHome;
	File oldIcons=new File(entigrator.getEntihome()+"/"+Entigrator.ICONS);
	String entityBodies$=entigrator.getEntihome()+"/"+Entigrator.ENTITY_BASE+"/data/";
    String icon$;
    if(debug)
		System.out.println("JBasenavigator:Paste:jrea="+jrea.length);
	for(JReferenceEntry jre:jrea){
		try{
			 if(debug)
					System.out.println("JBasenavigator:Paste:jre type="+jre.type+" name="+jre.name+" value="+jre.value);
		if(keep&&entigrator.indx_getLabel(jre.name)!=null)
			continue;
		
		oldEntity=new File(entityBodies$+jre.name);
		if(oldEntity.exists()){
			if(!undoBodies.exists())
				undoBodies.mkdirs();
			undoEntity=new File(undoBodies.getPath()+"/"+jre.name);
			FileExpert.copyFile(oldEntity, undoEntity);
			//icon$=entigrator.indx_getIcon(jre.name);
			icon$=entigrator.ent_getIconAtKey(jre.name);
			if(icon$!=null){
				oldIcon=new File(oldIcons.getPath()+"/"+icon$);
				if(oldIcon.exists()){
				if(!undoIcons.exists())
					undoIcons.mkdir();
				undoIcon=new File(undoIcons.getPath()+"/"+icon$);
				undoIcon.createNewFile();
				oldIcon=new File(oldIcons.getPath()+"/"+icon$);
				FileExpert.copyFile(oldIcon, undoIcon);
				undo.putElementItem("icon", new Core(null,icon$,null));
				}
			}
			oldEntityHome=new File(entihome$+"/"+jre.name);
			if(oldEntityHome.exists()){
				undoEntityHome=new File(undoHome.getPath()+"/"+jre.name);
				undoEntityHome.mkdir();
				FileExpert.copyAll(oldEntityHome.getPath(), undoEntityHome.getPath());
			}
		}
		
		undo.putElementItem("entity", new Core(Locator.getProperty(jre.value,Entigrator.ENTIHOME),jre.name,jre.value));
		if(debug)
			System.out.println("JBasenavigator:Paste:source entihome="+jre.type+" entity="+jre.name);
	    sourceEntity=new File(jre.type+"/"+Entigrator.ENTITY_BASE+"/data/"+jre.name);
	   //  System.out.println("BaseNavigator:source entity="+sourceEntity.getPath());
	    if(!oldEntity.exists())
	    	oldEntity.createNewFile();
	    FileExpert.copyFile(sourceEntity,oldEntity);
	    pastedEntity=Sack.parseXML(entigrator,oldEntity.getPath());
	    entigrator.ent_reindex(pastedEntity);
	    pastedEntity.putAttribute(new Core(null,JReferenceEntry.ORIGIN_ENTIHOME,jre.type));
	    entigrator.save(pastedEntity);
	    icon$=pastedEntity.getAttributeAt("icon");
	    //System.out.println("BaseNavigator:paste:icon="+icon$);
	    if(icon$!=null){
	    	undo.putElementItem("icon", new Core(null,icon$,null));
	    	newIcon=new File(jre.type+"/"+Entigrator.ICONS+"/"+icon$);
	    	if(newIcon.exists()){
	    		
	    		oldIcon=new File(oldIcons.getPath()+"/"+icon$);
	    		if(!oldIcon.exists())
	    			oldIcon.createNewFile();
	    		else{
	    			if(!undoIcons.exists())
	    				undoIcons.mkdir();
	    			undoIcon=new File(undoIcons.getPath()+"/"+icon$);
	    			FileExpert.copyFile(oldIcon, undoIcon);
	    		}
	    		FileExpert.copyFile(newIcon, oldIcon);
	    	}
	    }
	    sourceEntityHome=new File(jre.type+"/"+jre.name);
	    if( sourceEntityHome.exists()){
	    	oldEntityHome=new File(entihome$+"/"+jre.name);
	    	if(!oldEntityHome.exists())
	    		oldEntityHome.mkdir();
	    	if(debug)
	    		System.out.println("JBasenavigator:Paste:source="+sourceEntityHome.getPath()+" target="+oldEntityHome.getPath());
	    	FileExpert.copyAll(sourceEntityHome.getPath(), oldEntityHome.getPath());
	    }
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).info(e.toString());
		}
	}
	
  //  
    undo.putElementItem("fhandler", new Core(null,"gdt.data.entity.facet.BookmarksHandler",null));
    undo.putElementItem("jfacet", new Core("gdt.jgui.entity.bookmark.JBookmarksFacetAddItem","gdt.data.entity.facet.BookmarksHandler","gdt.jgui.entity.bookmark.JBookmarksFacetOpenItem"));
    entigrator.save(undo);
    entigrator.ent_assignProperty(undo, "bookmarks", undo.getProperty("label"));
    String undoLocator$=EntityHandler.getEntityLocator(entigrator, undo);
    JEntityPrimaryMenu.reindexEntity(console, undoLocator$);
	sa=undo.elementList("entity");
	if(sa!=null){
		String entityLocator$;
		Sack entity;
		console.clipboard.clear();
		FacetHandler[] fha=BaseHandler.listAllHandlers(entigrator);
		for(String s:sa){
			entity=entigrator.getEntityAtKey(s);
			
			if(entity==null)
				continue;
			EntityHandler.completeMigration(entigrator, s, fha);
			entityLocator$=EntityHandler.getEntityLocator(entigrator, entity);
			JEntityPrimaryMenu.reindexEntity(console,entityLocator$);
			console.clipboard.putString(entityLocator$);
		}
		undo=putBookmarks(console,undo);
		entigrator.save(undo);
		updateBookmarks(entigrator,undo);
	}
    JConsoleHandler.execute(console, getLocator());
	
}catch(Exception ee){
	Logger.getLogger(getClass().getName()).severe(ee.toString());
}	
}};
/*
private void paste(boolean keep){
	try{
	JReferenceEntry[] jrea= getToPaste();
	Entigrator entigrator=console.getEntigrator(entihome$);
	int cnt=0;
	String[] sa=entigrator.indx_listEntities("entity", "undo");
	if(sa!=null){
		String label$;
		int max=0;
		for(String s:sa){
			label$=entigrator.indx_getLabel(s);
			cnt=Integer.parseInt(label$.substring(5, label$.length()));
			if (cnt>max){
				max=cnt;
			}
		}
		cnt=max+1;
	}
	Sack undo=entigrator.ent_new("undo", "undo_"+String.valueOf(cnt));
	entigrator.save(undo);
	entigrator.ent_reindex(undo);
	entigrator.ent_assignProperty(undo, "folder", undo.getProperty("label"));
	undo.createElement("entity");
	undo.createElement("icon");
	undo.createElement("jbookmark");
	File sourceEntity;
	File undoEntity;
	File oldEntity;
	Sack pastedEntity;
	File undoIcon;
	File oldIcon;
	File newIcon;
	File undoHome=new File(entigrator.ent_getHome(undo.getKey()));
	File undoBodies=new File(undoHome.getPath()+"/"+Entigrator.ENTITY_BASE+"/data/");
	File undoIcons=new File(undoHome.getPath()+"/"+Entigrator.ICONS);
	File oldEntityHome;
	File undoEntityHome;
	File sourceEntityHome;
	File oldIcons=new File(entigrator.getEntihome()+"/"+Entigrator.ICONS);
	String entityBodies$=entigrator.getEntihome()+"/"+Entigrator.ENTITY_BASE+"/data/";
    String icon$;
	for(JReferenceEntry jre:jrea){
		try{
		if(keep&&entigrator.indx_getLabel(jre.name)!=null)
			continue;
		oldEntity=new File(entityBodies$+jre.name);
		if(oldEntity.exists()){
			if(!undoBodies.exists())
				undoBodies.mkdirs();
			undoEntity=new File(undoBodies.getPath()+"/"+jre.name);
			FileExpert.copyFile(oldEntity, undoEntity);
			//icon$=entigrator.indx_getIcon(jre.name);
			icon$=entigrator.ent_getIconAtKey(jre.name);
			if(icon$!=null){
				oldIcon=new File(oldIcons.getPath()+"/"+icon$);
				if(oldIcon.exists()){
				if(!undoIcons.exists())
					undoIcons.mkdir();
				undoIcon=new File(undoIcons.getPath()+"/"+icon$);
				undoIcon.createNewFile();
				oldIcon=new File(oldIcons.getPath()+"/"+icon$);
				FileExpert.copyFile(oldIcon, undoIcon);
				undo.putElementItem("icon", new Core(null,icon$,null));
				}
			}
			oldEntityHome=new File(entihome$+"/"+jre.name);
			if(oldEntityHome.exists()){
				undoEntityHome=new File(undoHome.getPath()+"/"+jre.name);
				undoEntityHome.mkdir();
				FileExpert.copyAll(oldEntityHome.getPath(), undoEntityHome.getPath());
			}
		}
		
		undo.putElementItem("entity", new Core(Locator.getProperty(jre.value,Entigrator.ENTIHOME),jre.name,jre.value));
	    sourceEntity=new File(jre.type+"/"+Entigrator.ENTITY_BASE+"/data/"+jre.name);
	   //  System.out.println("BaseNavigator:source entity="+sourceEntity.getPath());
	    if(!oldEntity.exists())
	    	oldEntity.createNewFile();
	    FileExpert.copyFile(sourceEntity,oldEntity);
	    pastedEntity=Sack.parseXML(oldEntity.getPath());
	    entigrator.ent_reindex(pastedEntity);
	    pastedEntity.putAttribute(new Core(null,JReferenceEntry.ORIGIN_ENTIHOME,jre.type));
	    entigrator.save(pastedEntity);
	    icon$=pastedEntity.getAttributeAt("icon");
	    //System.out.println("BaseNavigator:paste:icon="+icon$);
	    if(icon$!=null){
	    	undo.putElementItem("icon", new Core(null,icon$,null));
	    	newIcon=new File(jre.type+"/"+Entigrator.ICONS+"/"+icon$);
	    	if(newIcon.exists()){
	    		
	    		oldIcon=new File(oldIcons.getPath()+"/"+icon$);
	    		if(!oldIcon.exists())
	    			oldIcon.createNewFile();
	    		else{
	    			if(!undoIcons.exists())
	    				undoIcons.mkdir();
	    			undoIcon=new File(undoIcons.getPath()+"/"+icon$);
	    			FileExpert.copyFile(oldIcon, undoIcon);
	    		}
	    		FileExpert.copyFile(newIcon, oldIcon);
	    	}
	    }
	    sourceEntityHome=new File(jre.type+"/"+jre.name);
	    if( sourceEntityHome.exists()){
	    	oldEntityHome=new File(entihome$+"/"+jre.name);
	    	if(!oldEntityHome.exists())
	    		oldEntityHome.mkdir();
	    	FileExpert.copyAll(sourceEntityHome.getPath(), oldEntityHome.getPath());
	    }
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).info(e.toString());
		}
	}
  //  
    undo.putElementItem("fhandler", new Core(null,"gdt.data.entity.facet.BookmarksHandler",null));
    undo.putElementItem("jfacet", new Core("gdt.jgui.entity.bookmark.JBookmarksFacetAddItem","gdt.data.entity.facet.BookmarksHandler","gdt.jgui.entity.bookmark.JBookmarksFacetOpenItem"));
    entigrator.save(undo);
    entigrator.ent_assignProperty(undo, "bookmarks", undo.getProperty("label"));
    String undoLocator$=EntityHandler.getEntityLocator(entigrator, undo);
    JEntityPrimaryMenu.reindexEntity(console, undoLocator$);
	sa=undo.elementList("entity");
	if(sa!=null){
		String entityLocator$;
		Sack entity;
		console.clipboard.clear();
		FacetHandler[] fha=BaseHandler.listAllHandlers(entigrator);
		for(String s:sa){
			entity=entigrator.getEntityAtKey(s);
			
			if(entity==null)
				continue;
			EntityHandler.completeMigration(entigrator, s, fha);
			entityLocator$=EntityHandler.getEntityLocator(entigrator, entity);
			JEntityPrimaryMenu.reindexEntity(console,entityLocator$);
			console.clipboard.putString(entityLocator$);
		}
		undo=putBookmarks(console,undo);
		entigrator.save(undo);
		updateBookmarks(entigrator,undo);
	}
    JConsoleHandler.execute(console, getLocator());
	}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
	}
}
*/
private  static Sack putBookmarks(JMainConsole console,Sack undo ){
    try{
    	String[] sa=console.clipboard.getContent();
    	if(sa==null||sa.length<1)
    		return undo;
    	ArrayList<Core>cl=new ArrayList<Core>();
    	Core[] ca=undo.elementGet("jbookmark");
    	if(ca==null){
    		undo.createElement("jbookmark");
    	}else
    	for(Core aCa:ca)
    		cl.add(aCa);
    	String title$;
    	String bookmarkKey$;
    	for(String aSa:sa){
    		title$=Locator.getProperty(aSa, Locator.LOCATOR_TITLE);
    		if(title$==null)
    			continue;
    		bookmarkKey$=Locator.getProperty(aSa, JBookmarksEditor.BOOKMARK_KEY);
    		if(bookmarkKey$==null){
    			bookmarkKey$=Identity.key();
    			aSa=Locator.append(aSa, JBookmarksEditor.BOOKMARK_KEY, bookmarkKey$);
    			aSa=Locator.append(aSa, Locator.LOCATOR_CHECKABLE, Locator.LOCATOR_TRUE);
    			aSa=Locator.append(aSa, BaseHandler.HANDLER_CLASS, JEntityFacetPanel.class.getName());
       			aSa=Locator.append(aSa, BaseHandler.HANDLER_SCOPE, JConsoleHandler.CONSOLE_SCOPE);
    		}
    		cl.add( new Core(title$,bookmarkKey$,aSa));
    	}
    	ca=cl.toArray(new Core[0]);
    	undo.elementReplace("jbookmark", ca);
    }catch(Exception e){
    	Logger.getLogger(JBaseNavigator.class.getName()).severe(e.toString());
    }
    return undo;
}
private static void updateBookmarks(Entigrator entigrator,Sack undo){
	try{
		Core[] ca=undo.elementGet("jbookmark");
		if(ca==null)
			return;
		Sack entity;
		String entityKey$;
		Core []jbma;
		for(Core c:ca){
			entityKey$=Locator.getProperty(c.value, EntityHandler.ENTITY_KEY);
			if(entityKey$==null)
				continue;
			entity=entigrator.getEntityAtKey(entityKey$);
			if(entity==null)
				continue;
			jbma=entity.elementGet("jbookmark");
			if(jbma==null)
				continue;
			for(Core jbm:jbma){
				    jbm.value=Locator.append(jbm.value,Entigrator.ENTIHOME, entigrator.getEntihome()); 
					entity.putElementItem("jbookmark", jbm);
			}
		entigrator.save(entity);	
		}
		
	}catch(Exception e){
		Logger.getLogger(JBaseNavigator.class.getName()).severe(e.toString());
	}
}

@Override
public void activate() {
	if(debug)
		System.out.println("JBaseNavigator:activate:begin");
	Entigrator entigrator=console.getEntigrator(entihome$);
	if(entigrator.store_outdated())
		entigrator.store_reload();
	
}
@Override
public String getWebView(Entigrator entigrator,String locator$) {
	try{
		Properties locator=Locator.toProperties(locator$);
		String webHome$=locator.getProperty(WContext.WEB_HOME);
		entihome$=locator.getProperty(Entigrator.ENTIHOME);
		String webRequester$=locator.getProperty(WContext.WEB_REQUESTER);
	
		if(debug)
		System.out.println("JBasNavigator:web home="+webHome$+" locator="+locator$);
		String iconDesign$=Support.readHandlerIcon(null,JBaseNavigator.class, "design.png");
		String iconSearch$=Support.readHandlerIcon(null,JBaseNavigator.class, "zoom.png");
		String iconCategory$=Support.readHandlerIcon(null,JBaseNavigator.class, "category.png");
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
	    sb.append("<li class=\"menu_item\"><a href=\""+WContext.ABOUT+"\">About</a></li>");
	     sb.append("</ul>");
	    sb.append("<table><tr><td>Base:</td><td><strong>");
	    sb.append(entigrator.getBaseName());
	    sb.append("</strong></td></tr>");
	    sb.append("<tr><td>Context:</td><td><strong>");
	    sb.append("Base navigator");
	    sb.append("</strong></td></tr>");
	    sb.append("</table>");
	    Properties itemLocator=new Properties();
	    itemLocator.setProperty(WContext.WEB_HOME, webHome$);
	    itemLocator.setProperty(Entigrator.ENTIHOME, entihome$);
	    itemLocator.setProperty(BaseHandler.HANDLER_CLASS, JDesignPanel.class.getName());
	    itemLocator.setProperty(Entigrator.ENTIHOME,entigrator.getEntihome());
	    itemLocator.setProperty(WContext.WEB_REQUESTER, this.getClass().getName());
	    String title$="Design";
	    String designLocator$=Locator.toString(itemLocator);
	    if(debug){
	    System.out.println("JBasNavigator:design locator="+designLocator$);
	    System.out.println("JBasNavigator:design url="+getItem(iconDesign$, webHome$,title$,designLocator$));
	    }
	    sb.append(getItem(iconDesign$, webHome$,title$,designLocator$)+"<br>");
	    title$="Search for label";
	    itemLocator.setProperty(BaseHandler.HANDLER_CLASS, JSearchPanel.class.getName());
	    String searchLocator$=Locator.toString(itemLocator);
	    sb.append(getItem(iconSearch$, webHome$,title$,searchLocator$)+"<br>");
	    title$="All categories";
	    itemLocator.setProperty(BaseHandler.HANDLER_CLASS, JAllCategoriesPanel.class.getName());
	    String catLocator$=Locator.toString(itemLocator);;
	    sb.append(getItem(iconCategory$, webHome$,title$,catLocator$)+"<br>");
	    sb.append("<script>");
	    sb.append("window.localStorage.setItem(\"back."+JDesignPanel.class.getName()+"\",\""+this.getClass().getName()+"\");");
	    sb.append("window.localStorage.setItem(\"back."+JAllCategoriesPanel.class.getName()+"\",\""+this.getClass().getName()+"\");");
	    sb.append("window.localStorage.setItem(\""+this.getClass().getName()+"\",\""+Base64.encodeBase64URLSafeString(locator$.getBytes())+"\");");
	    if(debug)
		sb.append("console.log(window.localStorage.getItem(\""+this.getClass().getName()+"\"));");
		sb.append("function onLoad() {");
	    sb.append("initBack(\""+this.getClass().getName()+"\",\""+webRequester$+"\");");
	    sb.append("}");
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
public String getWebConsole(Entigrator entigrator,String locator$) {
	// TODO Auto-generated method stub
	return null;
}
private String getItem(String icon$, String url$,String title$,String locator$ ){
	  locator$=Locator.append(locator$,Entigrator.ENTIHOME, entihome$);
	  String iconTerm$="<img src=\"data:image/png;base64,"+icon$+
			  "\" width=\"24\" height=\"24\" alt=\""+title$+"\">";
	  return iconTerm$+"<a href=\""+url$+"?"+WContext.WEB_LOCATOR+"="+Base64.encodeBase64URLSafeString(locator$.getBytes())+"\" >"+" "+title$+"</a>";
}
}
