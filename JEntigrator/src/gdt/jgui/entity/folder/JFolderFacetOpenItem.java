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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Logger;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.commons.codec.binary.Base64;
import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.FacetHandler;
import gdt.data.entity.facet.FolderHandler;
import gdt.data.store.FileExpert;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JFacetOpenItem;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.entity.JEntityDigestDisplay;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.entity.fields.JFieldsFacetOpenItem;
import gdt.jgui.tool.JTextEditor;
/**
 * This class represents the folder facet item in the list
 * of  entity's facets.
 * @author imasa
 *
 */

public class JFolderFacetOpenItem extends JFacetOpenItem implements JRequester {
	private static final long serialVersionUID = 1L;
	public static final String NODE_TYPE_FILE_NODE = "node type file node";
	private Logger LOGGER=Logger.getLogger(JFieldsFacetOpenItem.class.getName());
	/**
	 * The default constructor.
	 */
	public JFolderFacetOpenItem(){
			super();
		}
	/**
	 * Execute the response locator.
	 * @param console the main console.
	 * @param locator$ the response locator.
	 */
  	@Override
	public void response(JMainConsole console, String locator$) {
//		System.out.println("JFolderFacetItem:response:locator:"+locator$);
		try{
			Properties locator=Locator.toProperties(locator$);
			entihome$=locator.getProperty(Entigrator.ENTIHOME);
			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			String action$=locator.getProperty(JRequester.REQUESTER_ACTION);
			if(JFolderPanel.ACTION_EDIT_FILE.equals(action$)){
				String text$=locator.getProperty(JTextEditor.TEXT);
				String filePath$=locator.getProperty(JFolderPanel.FILE_PATH);
				String selection$=locator.getProperty(JEntityDigestDisplay.SELECTION);
				File file=new File(filePath$);
				   if(!file.exists())
					   file.createNewFile();
				   FileOutputStream  fos = new FileOutputStream(file, false);
			       Writer writer = new OutputStreamWriter(fos, "UTF-8");
			       writer.write(text$);
			       writer.close();
			       fos.close();
			       JEntityDigestDisplay edd=new JEntityDigestDisplay();
					String eddLocator$=edd.getLocator();
					eddLocator$=Locator.append(eddLocator$,EntityHandler.ENTITY_KEY ,entityKey$);
					eddLocator$=Locator.append(eddLocator$,Entigrator.ENTIHOME ,entihome$);
					eddLocator$=Locator.append(eddLocator$,JEntityDigestDisplay.SELECTION,selection$);
					JConsoleHandler.execute(console, eddLocator$);
				return;	  
			}
			JEntityFacetPanel efp=new JEntityFacetPanel();
			String efpLocator$=efp.getLocator();
			 efpLocator$=Locator.append(efpLocator$, Entigrator.ENTIHOME, entihome$);
			 efpLocator$=Locator.append(efpLocator$, EntityHandler.ENTITY_KEY, entityKey$);
			 JConsoleHandler.execute(console, efpLocator$);
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
	}
	@Override
	/**
	 * Check if the facet can be removed from the entity.
	 * @return true if can be removed false otherwise.
	 */
	
public boolean isRemovable() {
		try{
			entihome$=Locator.getProperty(locator$, Entigrator.ENTIHOME);
			entityKey$=Locator.getProperty(locator$,EntityHandler.ENTITY_KEY);
			Entigrator entigrator=console.getEntigrator(entihome$);
			 Sack entity =entigrator.getEntityAtKey(entityKey$);
			 if("folder".equals(entity.getProperty("entity")))
				 return false;
			 return true;
		}catch(Exception e){
			LOGGER.severe(e.toString());
		return false;
		}
	}
	/**
	 * Get the facet name.
	 * @return the facet name.
	 */
	@Override
	public String getFacetName() {
		return "Folder";
	}
	/**
	 * Get the facet icon as a Base64 string.
	 * @return the facet icon string.
	 */
	@Override
	public String getFacetIcon() {
		return Support.readHandlerIcon(getClass(), "folder.png");
	}
	/**
	 * Get the facet renderer class name.
	 * @return null.
	 */
	@Override
	public String getFacetRenderer() {
		return JFolderPanel.class.getName();
	}
	/**
	 * Remove the facet from the entity.
	 */
	@Override
	public void removeFacet() {
		try{
		    Entigrator entigrator=console.getEntigrator(entihome$);
			Sack entity=entigrator.getEntityAtKey(entityKey$);
			entity.removeElementItem("fhandler", FolderHandler.class.getName());
			entity.removeElementItem("jfacet", FolderHandler.class.getName());
			entigrator.save(entity);
			entigrator.ent_takeOffProperty(entity, "folder");
			String entityHome$=entigrator.ent_getHome(entityKey$);
			FileExpert.clear(entityHome$);
			File entityHome=new File(entityHome$);
			entityHome.delete();
		}catch(Exception e){
		LOGGER.severe(e.toString());
		}
	}
	/**
	 * Display the folder context.
	 * @param console the main console
	 * @param locator$ the locator string. 
	 */
	@Override
	public void openFacet(JMainConsole console, String locator$) {
		try{
//			System.out.println("JFolderFacetOpenItem:openFacet:locator="+locator$);
			Properties locator=Locator.toProperties(locator$);
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			String responseLocator$=getLocator();
			Properties responseLocator=Locator.toProperties(responseLocator$);
			responseLocator.setProperty(Entigrator.ENTIHOME, entihome$);
			responseLocator.setProperty(EntityHandler.ENTITY_KEY, entityKey$);
			responseLocator.setProperty(BaseHandler.HANDLER_METHOD, JFacetOpenItem.METHOD_RESPONSE);
			responseLocator$=Locator.toString(responseLocator);
			String requesterResponseLocator$=Locator.compressText(responseLocator$);
			JFolderPanel folderPanel=new JFolderPanel();
			String feLocator$=folderPanel.getLocator();
			feLocator$=Locator.append(feLocator$, Entigrator.ENTIHOME, entihome$);
			feLocator$=Locator.append(feLocator$, EntityHandler.ENTITY_KEY, entityKey$);
			feLocator$=Locator.append(feLocator$, JRequester.REQUESTER_RESPONSE_LOCATOR, requesterResponseLocator$);
			feLocator$=Locator.append(feLocator$, BaseHandler.HANDLER_METHOD,"instantiate");
			JConsoleHandler.execute(console, feLocator$);
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
	}
	/**
	 * Get the open facet item locator.
	 * @return the locator string.
	 */
	@Override
	public String getLocator(){
		Properties locator=new Properties();
		locator.setProperty(Locator.LOCATOR_TITLE,"Folder");
		locator.setProperty(BaseHandler.HANDLER_CLASS,JFolderFacetOpenItem.class.getName());
		locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
		locator.setProperty(BaseHandler.HANDLER_METHOD,METHOD_OPEN_FACET);
		locator.setProperty( JContext.CONTEXT_TYPE,"Folder facet");
		locator.setProperty(Locator.LOCATOR_TITLE,"Folder");
		locator.setProperty(FACET_HANDLER_CLASS,FolderHandler.class.getName());
		if(entityKey$!=null)
			locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
		if(entihome$!=null)
			locator.setProperty(Entigrator.ENTIHOME,entihome$);
		 String icon$=Support.readHandlerIcon(JFolderPanel.class, "folder.png");
		    if(icon$!=null)
		    	locator.setProperty(Locator.LOCATOR_ICON,icon$);
		    if(entihome$!=null){   
		 	locator.setProperty(Locator.LOCATOR_CHECKABLE,Locator.LOCATOR_TRUE);
		    }
		return Locator.toString(locator);
	}
	/**
	 * Get children nodes of the facet node for the digest view.
	 * @return the children nodes of the facet node.
	 */
	@Override
	public DefaultMutableTreeNode[] getDigest() {
		try{
//			System.out.println("JFolderFacetOpenItem:getDigest:locator="+locator$);
			Properties locator=Locator.toProperties(locator$);
			entihome$=locator.getProperty(Entigrator.ENTIHOME);
			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			String folderPath$=entihome$+"/"+entityKey$;
			File folder=new File(folderPath$);
			DefaultMutableTreeNode fileNode;
			File[] fa=folder.listFiles();
			if(fa==null)
				return null;
				ArrayList<DefaultMutableTreeNode> fnl=new ArrayList<DefaultMutableTreeNode>();
				Properties fileLocator;
				String icon$;
				for(File f:fa){
					fileLocator=new Properties();
					fileLocator.setProperty(Locator.LOCATOR_TITLE, f.getName());
					fileLocator.setProperty(Entigrator.ENTIHOME, entihome$);
					fileLocator.setProperty(EntityHandler.ENTITY_KEY, entityKey$);
					fileLocator.setProperty(BaseHandler.HANDLER_SCOPE, JConsoleHandler.CONSOLE_SCOPE);
					fileLocator.setProperty(JFolderPanel.FILE_NAME, f.getName());
//					System.out.println("JFolderFacetOpenItem:getDigest:file="+f.getName());
					fileLocator.setProperty(JFolderPanel.FILE_PATH, f.getPath());
					fileLocator.setProperty(Locator.LOCATOR_TYPE, JFolderPanel.LOCATOR_TYPE_FILE);
					fileLocator.setProperty(Locator.LOCATOR_CHECKABLE,Locator.LOCATOR_TRUE);
					fileLocator.setProperty(JEntityDigestDisplay.NODE_TYPE,NODE_TYPE_FILE_NODE);
					icon$=Support.readHandlerIcon(getClass(), "file.png");
					fileLocator.setProperty(Locator.LOCATOR_ICON, icon$);
					fileLocator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
					fileLocator.setProperty(BaseHandler.HANDLER_METHOD,"openFile");
					fileNode=new DefaultMutableTreeNode();
					fileNode.setUserObject(Locator.toString(fileLocator));
				    fnl.add(fileNode); 
				}
				return fnl.toArray(new DefaultMutableTreeNode[0]);
		
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
		return new FolderHandler();
	}
	/**
	 * Get the popup menu for the child node of the facet node 
	 * in the digest view.
	 * @return the popup menu.	
	 */
	@Override
	public JPopupMenu getPopupMenu(final String digestLocator$) {
		JPopupMenu	popup = new JPopupMenu();
		try{ 
		Properties locator=Locator.toProperties(digestLocator$);
		 final  String encodedSelection$=locator.getProperty(JEntityDigestDisplay.SELECTION);
		   byte[]ba=Base64.decodeBase64(encodedSelection$);
		final   String selection$=new String(ba,"UTF-8");
//		System.out.println("JFolderFacetOpenItem:getPopupMenu:selection:="+selection$);
		locator=Locator.toProperties(selection$);
		String nodeType$=locator.getProperty(JEntityDigestDisplay.NODE_TYPE);
		if(JEntityDigestDisplay.NODE_TYPE_FACET_OWNER.equals(nodeType$)){
			 JMenuItem openItem=new JMenuItem("Open");
			   popup.add(openItem);
			   openItem.setHorizontalTextPosition(JMenuItem.RIGHT);
			   openItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
					   try{
						   Properties locator=Locator.toProperties(selection$);
						   String entihome$=locator.getProperty(Entigrator.ENTIHOME);
						   String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
						   File file=new File(entihome$+"/"+entityKey$);
						   Desktop.getDesktop().open(file);
					   }catch(Exception ee){
						   Logger.getLogger(JFieldsFacetOpenItem.class.getName()).info(ee.toString());
					   }
					}
				    }); 
			JMenuItem editItem=new JMenuItem("Edit");
			   popup.add(editItem);
			   editItem.setHorizontalTextPosition(JMenuItem.RIGHT);
			   editItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
					   try{
						   Properties locator=Locator.toProperties(selection$);
						   String entihome$=locator.getProperty(Entigrator.ENTIHOME);
						   String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
						   JFolderPanel fp=new JFolderPanel();
					       String fpLocator$=fp.getLocator();
					       fpLocator$=Locator.append(fpLocator$, Entigrator.ENTIHOME, entihome$);
					       fpLocator$=Locator.append(fpLocator$, EntityHandler.ENTITY_KEY, entityKey$);
						   JConsoleHandler.execute(console, fpLocator$);
					   }catch(Exception ee){
						   Logger.getLogger(JFieldsFacetOpenItem.class.getName()).info(ee.toString());
					   }
					}
				    });
		}
		if(NODE_TYPE_FILE_NODE.equals(nodeType$)){
			JMenuItem openItem=new JMenuItem("Open");
			   popup.add(openItem);
			   openItem.setHorizontalTextPosition(JMenuItem.RIGHT);
			   openItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
					   try{
						   Properties locator=Locator.toProperties(selection$);
						   String filePath$=locator.getProperty(JFolderPanel.FILE_PATH);
						   File file=new File(filePath$);
						   Desktop.getDesktop().open(file);
					   }catch(Exception ee){
						   Logger.getLogger(JFieldsFacetOpenItem.class.getName()).info(ee.toString());
					   }
					}
				    });
			 String filePath$=locator.getProperty(JFolderPanel.FILE_PATH);  
		     final File file=new File(filePath$);
		     if(isTextFile(file)){
		    	 JMenuItem editItem=new JMenuItem("Edit");
				   popup.add(editItem);
				   editItem.setHorizontalTextPosition(JMenuItem.RIGHT);
				   editItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							try{
								InputStream is=new FileInputStream(file);
					   	         ByteArrayOutputStream bos = new ByteArrayOutputStream();
					 	            byte[] b = new byte[1024];
					 	            int bytesRead = 0;
					 	            while ((bytesRead = is.read(b)) != -1) {
					 	               bos.write(b, 0, bytesRead);
					 	            }
					 	            byte[] ba = bos.toByteArray();
					 	            is.close();
				 	           String text$ = new String(ba, "UTF-8");
								JTextEditor te=new JTextEditor();
								String teLocator$=te.getLocator();
								teLocator$=Locator.append(teLocator$, Entigrator.ENTIHOME, entihome$);
								teLocator$=Locator.append(teLocator$, JTextEditor.TEXT,text$);
								locator$=Locator.append(locator$, JRequester.REQUESTER_ACTION,JFolderPanel.ACTION_EDIT_FILE);
								locator$=Locator.append(locator$, JFolderPanel.FILE_PATH,file.getPath());
								locator$=Locator.append(locator$, BaseHandler.HANDLER_METHOD,"response");
								locator$=Locator.append(locator$, JEntityDigestDisplay.SELECTION,encodedSelection$);
								String requesterResponceLocator$=Locator.compressText(locator$);
								teLocator$=Locator.append(teLocator$,JRequester.REQUESTER_RESPONSE_LOCATOR,requesterResponceLocator$);
								JConsoleHandler.execute(console,teLocator$);
							}catch(Exception ee){
								Logger.getLogger(getClass().getName()).info(ee.toString());
							}
						}
					    }); 
		     }
		}
		return popup;
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		return null;
		}
	}
	private static  boolean isTextFile(File file){
		try{
			String mime$=Files.probeContentType(file.toPath());
			//System.out.println("FileOpenItem: isTextFile:mime="+mime$);
			if (mime$.equals("text/plain"))
					 return true;
			return false;
		}catch(Exception e){
			Logger.getLogger(JFileOpenItem.class.getName()).info(e.toString());
			return false;
		}
	}
}
