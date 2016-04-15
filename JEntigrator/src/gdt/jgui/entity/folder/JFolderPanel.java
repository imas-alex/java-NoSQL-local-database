package gdt.jgui.entity.folder;
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
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Stack;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import org.apache.commons.codec.binary.Base64;
import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.facet.FolderHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Identity;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.data.store.FileExpert;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JFacetRenderer;
import gdt.jgui.console.JItemsListPanel;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.entity.JEntityPrimaryMenu;
import gdt.jgui.entity.JReferenceEntry;
import gdt.jgui.tool.JTextEditor;
/**
 * This class provides the folder functionality.
 * It displays the folder's content and the context menu
 * for basic operations with folder's files. 
 * @author imasa.
 *
 */
public class JFolderPanel extends JItemsListPanel implements JFacetRenderer,JRequester,ClipboardOwner{
	
	private static final long serialVersionUID = 1L;
	protected Logger LOGGER=Logger.getLogger(getClass().getName());
/**
 * Indicates the locator type 'file'.
 */
	public static final String LOCATOR_TYPE_FILE="locator type file";
/**
 * The tag of file's name.
 */
	public static final String FILE_NAME="file name";
/**
 * The tag of file's	 path.
 */
	public static final String FILE_PATH="file path";
	private static final String ACTION_CREATE_FILE="action create file";
	private static final String ACTION_CREATE_FOLDER="action create folder";
	/**
	 * Indicates the edit file action.
	 */
	public static final String ACTION_EDIT_FILE="action edit file";
	protected String entihome$;
	protected String entityKey$;
	protected String entityLabel$;
	protected String requesterResponseLocator$;
	protected JMenuItem[] mia;
/**
 * The default constructor.	
 */
	public JFolderPanel() {
		super();
	}
	/**
	 * Get context locator.
	 * @return the locator string.
	 */
	@Override
	public String getLocator() {
		try{
			Properties locator=new Properties();
			locator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
			locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
			 locator.setProperty( JContext.CONTEXT_TYPE,getType());
			locator.setProperty(Locator.LOCATOR_TITLE,getTitle());
			if(entityLabel$!=null)
				locator.setProperty(EntityHandler.ENTITY_LABEL,entityLabel$);
			if(entityKey$!=null)
				locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
			if(entihome$!=null)
				locator.setProperty(Entigrator.ENTIHOME,entihome$);
			 String icon$=Support.readHandlerIcon(getClass(), "folder.png");
			    if(icon$!=null)
			    	locator.setProperty(Locator.LOCATOR_ICON,icon$);
			return Locator.toString(locator);
			}catch(Exception e){
	        LOGGER.severe(e.toString());
	        return null;
			}
	}
/**
 * Create the folder panel context.
 * @param console the main console.
 * @param locator$ the locator string.
 */
	@Override
	public JFacetRenderer instantiate(JMainConsole console, String locator$) {
		try{
		//	System.out.println("FolderPanel.instantiate:locator="+locator$);
			this.console=console;
			this.locator$=locator$;
			Properties locator=Locator.toProperties(locator$);
			entihome$=locator.getProperty(Entigrator.ENTIHOME);
			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			requesterResponseLocator$=locator.getProperty(JRequester.REQUESTER_RESPONSE_LOCATOR);
			entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
			if(entityLabel$==null){
			 Entigrator entigrator=console.getEntigrator(entihome$);
			  entityLabel$=entigrator.indx_getLabel(entityKey$);
			}
			File folder=new File(entihome$+"/"+entityKey$);
			File[] fa=folder.listFiles();
//			System.out.println("FolderPanel.instantiate:fa="+fa.length);
			if(fa!=null){
				ArrayList<JFileOpenItem> foil=new ArrayList<JFileOpenItem>();
				JFileOpenItem fileOpenItem;
				Properties fileLocator;
				//Icon icon;
				String icon$;
				for(File f:fa){
					fileOpenItem =new JFileOpenItem();
					fileLocator=new Properties();
					fileLocator.setProperty(Locator.LOCATOR_TITLE, f.getName());
					fileLocator.setProperty(Entigrator.ENTIHOME, entihome$);
					fileLocator.setProperty(EntityHandler.ENTITY_KEY, entityKey$);
					fileLocator.setProperty(BaseHandler.HANDLER_SCOPE, JConsoleHandler.CONSOLE_SCOPE);
					fileLocator.setProperty(FILE_NAME, f.getName());
					fileLocator.setProperty(FILE_PATH, f.getPath());
					fileLocator.setProperty(Locator.LOCATOR_TYPE, LOCATOR_TYPE_FILE);
					fileLocator.setProperty(Locator.LOCATOR_CHECKABLE,Locator.LOCATOR_TRUE);
					if(f.isFile())
					icon$=Support.readHandlerIcon(getClass(), "file.png");
					else
						icon$=Support.readHandlerIcon(getClass(), "folder.png");
					fileLocator.setProperty(Locator.LOCATOR_ICON, icon$);
					
					fileLocator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
					fileLocator.setProperty(BaseHandler.HANDLER_METHOD,"openFile");
					fileOpenItem.instantiate(console, Locator.toString(fileLocator));
				    foil.add(fileOpenItem); 
				}
				JFileOpenItem[] foia=foil.toArray(new JFileOpenItem[0]);
				foia=(JFileOpenItem[]) sort(foia);
				putItems(foia);
			}
			
		}catch(Exception e){
	        LOGGER.severe(e.toString());
			}
		return this;
	}
/**
 * Get context title.
 * @return the context title.
 */
	@Override
	public String getTitle() {
		return "Folder";
	}
	/**
	 * Get context subtitle.
	 * @return the context subtitle.
	 */
	@Override
	public String getSubtitle() {
		if(entityLabel$!=null)
			return entityLabel$;
		else
			return "Folder";
	}
	/**
	 * Get context type.
	 * @return the context type.
	 */
	@Override
	public String getType() {
		return "folder";
	}
/**
 * Complete context. No operation.
 */
	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
/**
 * Execute the response locator.
 * @param console the main console.
 * @param locator$ the locator string.
 */
	@Override
	public void response(JMainConsole console, String locator$) {
		try{
//			System.out.println("FolderPanel:response:locator="+locator$);
			Properties locator=Locator.toProperties(locator$);
			String action$=locator.getProperty(JRequester.REQUESTER_ACTION);
			if(ACTION_CREATE_FILE.equals(action$)){
			   String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			   String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			   String text$=locator.getProperty(JTextEditor.TEXT);
			   File file=new File(entihome$+"/"+entityKey$+"/"+text$);
			   if(!file.exists())
				   file.createNewFile();
			   locator$=Locator.remove(locator$, JRequester.REQUESTER_ACTION);
			   JConsoleHandler.execute(console, locator$);
			}
			if(ACTION_EDIT_FILE.equals(action$)){
				   String filePath$=locator.getProperty(FILE_PATH);
				   String text$=locator.getProperty(JTextEditor.TEXT);
				   File file=new File(filePath$);
				   if(!file.exists())
					   file.createNewFile();
				   FileOutputStream  fos = new FileOutputStream(file, false);
			       Writer writer = new OutputStreamWriter(fos, "UTF-8");
			       writer.write(text$);
			       writer.close();
			       fos.close();
				   locator$=Locator.remove(locator$, JRequester.REQUESTER_ACTION);
				   JConsoleHandler.execute(console, locator$);
				}
			if(ACTION_CREATE_FOLDER.equals(action$)){
				   String entihome$=locator.getProperty(Entigrator.ENTIHOME);
				   String text$=locator.getProperty(JTextEditor.TEXT);
				   Entigrator entigrator=console.getEntigrator(entihome$);
				   Sack folder=entigrator.ent_new("folder", text$);
				   folder=entigrator.ent_assignProperty(folder, "folder", folder.getProperty("label"));
				   folder.putAttribute(new Core(null,"icon","folder.png"));
				   entigrator.save(folder);
				   entigrator.saveHandlerIcon(JFolderPanel.class, "folder.png");
				   entityKey$=folder.getKey();
				   File folderHome=new File(entihome$+"/"+entityKey$);
				   if(!folderHome.exists())
				    folderHome.mkdir();
				   JFolderPanel fp=new JFolderPanel();
				   String fLocator$=fp.getLocator();
				   fLocator$=Locator.append(fLocator$, Entigrator.ENTIHOME, entihome$);
				   fLocator$=Locator.append(fLocator$, EntityHandler.ENTITY_KEY, entityKey$);
				   
				   JEntityPrimaryMenu.reindexEntity(console, fLocator$);
				   Stack<String> s=console.getTrack();
				   s.pop();
				   console.setTrack(s);
				   JConsoleHandler.execute(console, fLocator$);
				}
		      
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
		
	}
/**
 * Add facet icon as Base64 string to the locator.
 * @param locator$ the origin locator.
 * @return the locator with the icon added.
 */
	@Override
	public String addIconToLocator(String locator$) {
		// TODO Auto-generated method stub
		return null;
	}
/**
 * Get facet handler class name.
 * @return the name of the facet handler class.
 */
	@Override
	public String getFacetHandler() {
		return FolderHandler.class.getName();
	}
/**
 * Get the type of the entity for this facet.
 * @return the type string. 
 * 
 */
	@Override
	public String getEntityType() {
		return "folder";
	}
	/**
	 * Get category icon as a Base64 string.
	 * @return the category icon string. 
	 */
	@Override
	public String getCategoryIcon() {
		return Support.readHandlerIcon(getClass(), "folder.png");
	}
	/**
	 * Get category title.
	 * @return the category title.
	 */
	@Override
	public String getCategoryTitle() {
		return "Folders";
	}
	/**
	 * Adapt cloned entity.
	 * @param console the main console.
	 * @param locator$ the locator string.
	 * 
	 */
	@Override
	public void adaptClone(JMainConsole console, String locator$) {
		System.out.println("JFolderPanel.adapt clone::locator="+locator$);
	    try{
	    	Properties locator=Locator.toProperties(locator$);
	    	String entihome$=locator.getProperty(Entigrator.ENTIHOME);
	    	String originKey$=locator.getProperty(JEntityPrimaryMenu.ORIGIN_KEY);
	    	String cloneKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
	    	File originFolder=new File(entihome$+"/"+originKey$);
	    	if(originFolder.exists()){
	    		File cloneFolder=new File(entihome$+"/"+cloneKey$);
	    		cloneFolder.mkdir();
	    	}
	    	FileExpert.copyAll(entihome$+"/"+originKey$, entihome$+"/"+cloneKey$);
	    	FolderHandler folderHandler=new FolderHandler();
	    	Entigrator entigrator=console.getEntigrator(entihome$);
	    	Sack entity=entigrator.getEntityAtKey(entityKey$);
			String entityLocator$=EntityHandler.getEntityLocator(entigrator, entity);
			
	    	folderHandler.instantiate(entityLocator$);
			folderHandler.adaptClone(entigrator);
	    }catch(Exception e){
	    	LOGGER.severe(e.toString());
	    }
	}
	/**
	 * Adapt renamed entity.
	 * @param console the main console.
	 * @param locator$ the locator string.
	 * 
	 */
	@Override
	public void adaptRename(JMainConsole console, String locator$) {
		try{
			System.out.println("JFolderPanel:adaptRename:locator="+locator$);
			//if(console==null)
			//	System.out.println("JFolderPanel:adaptRename:console is null");
			Properties locator=Locator.toProperties(locator$);
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			Entigrator entigrator=console.getEntigrator(entihome$);
			Sack entity=entigrator.getEntityAtKey(entityKey$);
			String entityLocator$=EntityHandler.getEntityLocator(entigrator, entity);
			FolderHandler folderHandler=new FolderHandler();
			folderHandler.instantiate(entityLocator$);
			folderHandler.adaptRename(entigrator);
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
	}
	/**
	 * Rebuild entity parameters and indexes.
	 * @param console the main console.
	 * @param entitgrator the entigrator.
	 * @param entity the entity.
	 */
	@Override
	public void reindex(JMainConsole console, Entigrator entigrator, Sack entity) {
		 try{
		    		entity.putElementItem("jfacet", new Core(JFolderFacetAddItem.class.getName(),FolderHandler.class.getName(),JFolderFacetOpenItem.class.getName()));
					entigrator.save(entity);
		    }catch(Exception e){
		    	LOGGER.severe(e.toString());
		    }
	}
	/**
	 * Create a new entity of the facet type.
	 * @param console the main console.
	 * @param locator$ the locator string.
	 * @return the key of the new entity.
	 */
	@Override
	public String newEntity(JMainConsole console, String locator$) {
		try{
			Properties locator=Locator.toProperties(locator$);
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			JTextEditor textEditor=new JTextEditor();
		    String teLocator$=textEditor.getLocator();
		    teLocator$=Locator.append(teLocator$, Entigrator.ENTIHOME,entihome$);
		    teLocator$=Locator.append(teLocator$, JTextEditor.TEXT_TITLE,"New folder");
		    String text$="NewFolder"+Identity.key().substring(0, 4);
		    teLocator$=Locator.append(teLocator$, JTextEditor.TEXT,text$);
		    JFolderPanel fp=new JFolderPanel();
		    String fpLocator$=fp.getLocator();
		    fpLocator$=Locator.append(fpLocator$, Entigrator.ENTIHOME,entihome$);
		    fpLocator$=Locator.append(fpLocator$, EntityHandler.ENTITY_KEY,entityKey$);
		    fpLocator$=Locator.append(fpLocator$, BaseHandler.HANDLER_METHOD,"response");
		    fpLocator$=Locator.append(fpLocator$, JRequester.REQUESTER_ACTION,ACTION_CREATE_FOLDER);
		    String requesterResponseLocator$=Locator.compressText(fpLocator$);
		    teLocator$=Locator.append(teLocator$,JRequester.REQUESTER_RESPONSE_LOCATOR,requesterResponseLocator$);
		    JConsoleHandler.execute(console, teLocator$);
		}catch(Exception ee){   
			LOGGER.severe(ee.toString());
		}
		return null;
	}
	private boolean hasToInsert(){
		try{
			Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		    Transferable clipboardContents = systemClipboard.getContents(null);
		    if(clipboardContents==null)
		    		return false;
		    Object transferData = clipboardContents.getTransferData(DataFlavor.javaFileListFlavor);
		    @SuppressWarnings("unchecked")
			List<File> files = (List<File>)transferData;
		    for (int i = 0; i < files.size(); i++) {
                File file = (File) files.get(i);
                if(file.exists()&&file.isFile())
                	return true;
            }
		}catch(Exception ee){   
			LOGGER.severe(ee.toString());
			
		}
		return false;
	}
	private boolean hasToPaste(){
		String[] sa=console.clipboard.getContent();
		if(sa==null||sa.length<1)
			return false;
		Properties locator;
		for(String aSa:sa){
			try{
			locator=Locator.toProperties(aSa);	
             if(LOCATOR_TYPE_FILE.equals(locator.getProperty(Locator.LOCATOR_TYPE)))
            		 return true;
		}catch(Exception ee){   
			LOGGER.severe(ee.toString());
		}
	}
		return false;
	}
	/**
	 * Get the context menu.
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
				//System.out.println("EntitiesPanel:getConextMenu:menu selected");
				menu.removeAll();
				if(mia!=null){
					for(JMenuItem mi:mia)
						menu.add(mi);
				menu.addSeparator();
				}
				 JMenuItem refreshItem = new JMenuItem("Refresh");
					refreshItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							JConsoleHandler.execute(console, locator$);
						}
					} );
					menu.add(refreshItem);
				 JMenuItem openItem = new JMenuItem("Open folder");
					openItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							try{
							File folder=new File(entihome$+"/"+entityKey$);
							if(!folder.exists())
								folder.mkdir();
							Desktop.getDesktop().open(folder);
							}catch(Exception ee){
								LOGGER.severe(ee.toString());
							}
						}
					} );
					menu.add(openItem);
					JMenuItem importItem = new JMenuItem("Import");
					importItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							try{
							File home= new File(System.getProperty("user.home"));
							Desktop.getDesktop().open(home);
							}catch(Exception ee){
								LOGGER.severe(ee.toString());
							}
						}
					} );
					
					menu.add(importItem);
					JMenuItem newItem = new JMenuItem("New text");
					newItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							try{
							    JTextEditor textEditor=new JTextEditor();
							    String teLocator$=textEditor.getLocator();
							    teLocator$=Locator.append(teLocator$, Entigrator.ENTIHOME,entihome$);
							    String text$="New"+Identity.key().substring(0, 4)+".txt";
							    teLocator$=Locator.append(teLocator$, JTextEditor.TEXT,text$);
							    JFolderPanel fp=new JFolderPanel();
							    String fpLocator$=fp.getLocator();
							    fpLocator$=Locator.append(fpLocator$, Entigrator.ENTIHOME,entihome$);
							    fpLocator$=Locator.append(fpLocator$, EntityHandler.ENTITY_KEY,entityKey$);
							    fpLocator$=Locator.append(fpLocator$, BaseHandler.HANDLER_METHOD,"response");
							    fpLocator$=Locator.append(fpLocator$, JRequester.REQUESTER_ACTION,ACTION_CREATE_FILE);
							    String requesterResponseLocator$=Locator.compressText(fpLocator$);
							    teLocator$=Locator.append(teLocator$,JRequester.REQUESTER_RESPONSE_LOCATOR,requesterResponseLocator$);
							    JConsoleHandler.execute(console, teLocator$);
							}catch(Exception ee){
								LOGGER.severe(ee.toString());
							}
						}
					} );
					
					menu.add(newItem);
					//menu.addSeparator();
				 if(hasToInsert()){
					 menu.addSeparator();
					 JMenuItem insertItem = new JMenuItem("Insert");
						insertItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								try{
									Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
								    Transferable clipboardContents = systemClipboard.getContents(null);
								    if(clipboardContents==null)
								    	return;
								    Object transferData = clipboardContents.getTransferData(DataFlavor.javaFileListFlavor);
								    List<File> files = (List<File>) transferData;
						            for (int i = 0; i < files.size(); i++) {
						                File file = (File) files.get(i);
						                if(file.exists()&&file.isFile()){
						                	 System.out.println("FolderPanel:insert:in="+file.getPath());	
						                  File dir=new File(entihome$+"/"+entityKey$);
						                  if(!dir.exists())
						                	  dir.mkdir();
						                	 File out=new File(entihome$+"/"+entityKey$+"/"+file.getName());
						                  if(!out.exists())
						                  out.createNewFile();
						                  System.out.println("FolderPanel:insert:out="+out.getPath());
						                  FileExpert.copyFile(file, out);
						                  JConsoleHandler.execute(console, getLocator());
						                }
						                   
						          //      System.out.println("FolderPanel:import:file="+file.getPath());
						            }
								}catch(Exception ee){   
									LOGGER.severe(ee.toString());
									
								}	
						
							}
						} );
						menu.add(insertItem);
				  }
				 if(hasToPaste()){
					 menu.addSeparator();
					  JMenuItem pasteItem = new JMenuItem("Paste");
						pasteItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
									String[] sa=console.clipboard.getContent();
									Properties locator;
									String file$;
									File file;
									File target;
									String dir$=entihome$+"/"+entityKey$;
									File dir=new File(dir$);
									if(!dir.exists())
										dir.mkdir();
									for(String aSa:sa){
										try{
										locator=Locator.toProperties(aSa);	
							             if(LOCATOR_TYPE_FILE.equals(locator.getProperty(Locator.LOCATOR_TYPE))){
							            	 file$=locator.getProperty(FILE_PATH);
							            	 file=new File(file$);
							            	 target=new File(dir$+"/"+file.getName());
							            	 if(!target.exists())
							            		 target.createNewFile();
							            	 FileExpert.copyFile(file, target);
							             }
									}catch(Exception ee){   
										LOGGER.info(ee.toString());
									}        
									}
									JConsoleHandler.execute(console,locator$);
									}
						} );
						menu.add(pasteItem);
				  }
				if(hasSelectedItems()){
					menu.addSeparator();
					JMenuItem deleteItem = new JMenuItem("Delete");
						deleteItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								int response = JOptionPane.showConfirmDialog(console.getContentPanel(), "Delete ?", "Confirm",
								        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
							   if (response == JOptionPane.YES_OPTION) {
								  String[] sa=JFolderPanel.this.listSelectedItems();
								  if(sa==null)
									  return;
								  Properties locator;
								  String file$;
								  File file;
								  for(String aSa:sa){
									    locator=Locator.toProperties(aSa);  
								        file$=locator.getProperty(FILE_PATH);
								        file=new File(file$);
								        try{
								        	if(file.isDirectory())
								        		FileExpert.clear(file$);
								        	file.delete();
								        }catch(Exception ee){
								        	LOGGER.info(ee.toString());
								        }
								  }
							   }
							   JConsoleHandler.execute(console, locator$);
							}
						} );
						menu.add(deleteItem);
						JMenuItem copyItem = new JMenuItem("Copy");
						copyItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								 String[] sa=JFolderPanel.this.listSelectedItems();
								 console.clipboard.clear();
								 if(sa!=null)
									 for(String aSa:sa)
										 console.clipboard.putString(aSa);
							}
						} );
						menu.add(copyItem);
						JMenuItem exportItem = new JMenuItem("Export");
						exportItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								try{
								  String[] sa= JFolderPanel.this.listSelectedItems();
								  Properties locator;
								  String file$;
								  File file;
								  ArrayList<File> fileList = new ArrayList<File>();
								  for(String aSa:sa){
									  try{ 
									locator=Locator.toProperties(aSa);
									file$=locator.getProperty(FILE_PATH);
									 file= new File(file$);
							         fileList.add(file);
								}catch(Exception ee){
									LOGGER.severe(ee.toString());
								}
								  }
									  File[] fa=fileList.toArray(new File[0]);
									  if(fa.length<1)
										  return;
//									  System.out.println("Folderpanel:finish:list="+fa.length);
									  JFileChooser chooser = new JFileChooser(); 
									    chooser.setCurrentDirectory(new java.io.File(System.getProperty("user.home")));
									    chooser.setDialogTitle("Export files");
									    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
									    chooser.setAcceptAllFileFilterUsed(false);
									    if (chooser.showSaveDialog(JFolderPanel.this) == JFileChooser.APPROVE_OPTION) { 
									    	String dir$=chooser.getSelectedFile().getPath();
									    	File target;
									    	for(File f:fa){
									    		target=new File(dir$+"/"+f.getName());
									    		if(!target.exists())
									    			target.createNewFile();
									    		FileExpert.copyFile(f, target);
									    	}
								      }
									    else {
									    	Logger.getLogger(JMainConsole.class.getName()).info(" no selection");
									      }
	//								    System.out.println("Folderpanel:finish:list="+fileList.size());  
							}catch(Exception eee){
								LOGGER.severe(eee.toString());
							}
								}
						} );
						menu.add(exportItem);
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
	public static JFileOpenItem[] sort(JFileOpenItem[] foia){
		ArrayList<String>sl=new ArrayList<String>();
		String title$;
		Hashtable<String,JFileOpenItem> map=new Hashtable<String,JFileOpenItem>();
		if(foia==null)
			return null;
		for(JFileOpenItem foi:foia){
		   title$=foi.getTitle();
		   if(title$==null)
			   continue;
		   sl.add(title$);
		   map.put(title$, foi);
		}
		Collections.sort(sl);
		String[] sa=sl.toArray(new String[0]);
		ArrayList<Object>foil=new ArrayList<Object>();
		for(String aSa:sa){
			foil.add(map.get(aSa));
		}
		return foil.toArray(new JFileOpenItem[0]);
	}

	/**
	 * No action.
	 */
	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
	}
	/**
	 * No action.
	 */
	@Override
	public void collectReferences(Entigrator entigrator, String entiyKey$, ArrayList<JReferenceEntry> sl) {
		
	}
	/**
	 * Open file in the default system viewer.
	 * @param console the main console.
	  * @param locator$ the locator string.
	 */
	public void openFile(JMainConsole console,String locator$){
		//	System.out.println("FolderPanel:openFile:locator::"+locator$);
			try{
			Properties locator=Locator.toProperties(locator$);
			String filePath$=locator.getProperty(FILE_PATH);
			File file=new File(filePath$);
			Desktop.getDesktop().open(file);
			String requesterResponseLocator$=locator.getProperty(JRequester.REQUESTER_RESPONSE_LOCATOR);
			if(requesterResponseLocator$!=null){
				byte[] ba=Base64.decodeBase64(requesterResponseLocator$);
				String responseLocator$=new String(ba,"UTF-8");
				JConsoleHandler.execute(console, responseLocator$);
			}
			}catch(Exception e){
				LOGGER.severe(e.toString());
			}
		}
	
}
