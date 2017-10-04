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
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Logger;

import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.apache.commons.codec.binary.Base64;

import gdt.data.entity.ArchiveFileFilter;
import gdt.data.entity.ArchiveHandler;
import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.facet.BookmarksHandler;
import gdt.data.entity.facet.FolderHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
import gdt.data.store.FileExpert;
import gdt.jgui.base.JBaseNavigator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JFacetOpenItem;
import gdt.jgui.console.JItemPanel;
import gdt.jgui.console.JRequester;
import gdt.jgui.console.WContext;
import gdt.jgui.console.WUtils;
import gdt.jgui.tool.JTextEditor;
/**
 * This class represents a file item within the
 * folder panel context. 
 * @author imasa
 *
 */
public class JFileOpenItem extends JItemPanel implements WContext{
	private static final long serialVersionUID = 1L;
	JMenuItem editItem;
	JMenuItem importItem;
	boolean debug=false;
/**
 * The constructor.
 */
	 public static final String[] IMAGE_EXTENSIONS = new String[]{
		        "gif", "png", "bmp","jpg","jpeg" 
		    };
	public JFileOpenItem(){
		 super();
		 
	 popup = new JPopupMenu();
	 popup.addPopupMenuListener(new PopupMenuListener(){
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				popup.removeAll();
				//System.out.println("JFileOpenItem:locator="+locator$);
				//System.out.println("JFileOpenItem:file name="+Locator.getProperty(locator$, JFolderPanel.FILE_NAME));
			popup.removeAll();
	 JMenuItem openItem=new JMenuItem("Open");
	   popup.add(openItem);
	   openItem.setHorizontalTextPosition(JMenuItem.RIGHT);
	   openItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(timer==null)
	  				createTimer();
	  			timer.start();
			}
		    });
	   JMenuItem showPath=new JMenuItem("Show path");
	   popup.add(showPath);
	   showPath.setHorizontalTextPosition(JMenuItem.RIGHT);
	   showPath.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
				Properties locator=Locator.toProperties(locator$);
				String entihome$=locator.getProperty(Entigrator.ENTIHOME);
				String filePath$=entihome$+"/"+locator.getProperty(JFolderPanel.FILE_PATH);
				JTextEditor te=new JTextEditor();
				String teLocator$=te.getLocator();
				teLocator$=Locator.append(teLocator$, Entigrator.ENTIHOME, entihome$);
				teLocator$=Locator.append(teLocator$, JTextEditor.TEXT,filePath$);
				locator$=Locator.remove(locator$, BaseHandler.HANDLER_METHOD);
				String requesterResponceLocator$=Locator.compressText(locator$);
				teLocator$=Locator.append(teLocator$,JRequester.REQUESTER_RESPONSE_LOCATOR,requesterResponceLocator$);
				JConsoleHandler.execute(console,teLocator$);
				}catch(Exception ee){
					Logger.getLogger(JFileOpenItem.class.getName()).info(ee.toString());
				}
			}
		    });
	   JMenuItem folderItem=new JMenuItem("Open folder");
	   popup.add(folderItem);
	   folderItem.setHorizontalTextPosition(JMenuItem.RIGHT);
	   folderItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
				Properties locator=Locator.toProperties(locator$);
				String entihome$=locator.getProperty(Entigrator.ENTIHOME);
				String filePath$=entihome$+"/"+locator.getProperty(JFolderPanel.FILE_PATH);
				File itemFile=new File(filePath$);
				File folder=itemFile.getParentFile();
				Desktop.getDesktop().open(folder);
				}catch(Exception ee){
					Logger.getLogger(JFileOpenItem.class.getName()).info(ee.toString());
				}
			}
		    });
	  String fname$=Locator.getProperty(locator$, JFolderPanel.FILE_NAME);
	  if(isArchiveFile(fname$)){
		  String entihome$=Locator.getProperty(locator$,Entigrator.ENTIHOME);
		  String filePath$=entihome$+"/"+Locator.getProperty(locator$,JFolderPanel.FILE_PATH);
		 if(debug)
		   System.out.println("JFileOpenItem:file path="+filePath$);
		  String contentType$=ArchiveHandler.detectContentOfArchive(filePath$);
		  if(debug)
		  System.out.println("JFileOpenItem:content type="+contentType$);
		  if(ArchiveHandler.ARCHIVE_CONTENT_ENTITIES.equals(contentType$)){
			  popup.addSeparator();
			  JMenuItem importItem=new JMenuItem("Import");
		   popup.add(importItem);
		   importItem.setHorizontalTextPosition(JMenuItem.RIGHT);
		   importItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try{
					Properties locator=Locator.toProperties(locator$);
					String filePath$=locator.getProperty(JFolderPanel.FILE_PATH);
					if(debug)
					System.out.println("FileOpenItem:import file="+filePath$);
					}catch(Exception ee){
						Logger.getLogger(JFileOpenItem.class.getName()).info(ee.toString());
					}
				}
			    });
		  }
		  if(ArchiveHandler.ARCHIVE_CONTENT_DATABASE.equals(contentType$)){
			  popup.addSeparator();
			  JMenuItem extractItem=new JMenuItem("Extract");
			   popup.add(extractItem);
			   extractItem.setHorizontalTextPosition(JMenuItem.RIGHT);
			   extractItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try{
						Properties locator=Locator.toProperties(locator$);
						String filePath$=locator.getProperty(JFolderPanel.FILE_PATH);
						if(debug)
						System.out.println("FileOpenItem:extract file="+filePath$);
						JFileChooser chooser = new JFileChooser(); 
					    chooser.setCurrentDirectory(new java.io.File(System.getProperty("user.home")));
					    chooser.setDialogTitle("Extract database");
					    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					    chooser.setAcceptAllFileFilterUsed(false);
					    if (chooser.showOpenDialog(JFileOpenItem.this) == JFileChooser.APPROVE_OPTION) { 
					    	String parentDir$=chooser.getSelectedFile().getPath();
					    	String base$=FileExpert.getBasicFilename(filePath$);
					    	File entihome=new File(parentDir$+"/"+base$);
					    	if(entihome.exists()){
					    		if(entihome.isFile())
					    			entihome.delete();
					    		else{
					    			try{FileExpert.clear(entihome.getPath());}catch(Exception ee){}
					    			entihome.delete();
					    		}
					    	}
					    	entihome.mkdir();
					    	//System.out.println("BaseNavigator.install extension="+extension$);
					    ArchiveHandler.extractEntities(entihome.getPath(), filePath$);
					    }
					     
						}catch(Exception ee){
							Logger.getLogger(JFileOpenItem.class.getName()).info(ee.toString());
						}
					}
				    });
			  }
	  }
	   
	   
	 }
	 @Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		}

		@Override
		public void popupMenuCanceled(PopupMenuEvent e) {
			// TODO Auto-generated method stub
		}
	});
	 }

 
private static  boolean isArchiveFile(String file$){
	try{
		ArchiveFileFilter aff=new ArchiveFileFilter();
		return aff.accept(null, file$);
	}catch(Exception e){
		Logger.getLogger(JFileOpenItem.class.getName()).info(e.toString());
		return false;
	}
}


@Override
public String getWebView(Entigrator entigrator, String locator$) {
	try{
		if(debug)
			System.out.println("JFileOpenItem:getWebView:locator="+locator$);
		Properties locator=Locator.toProperties(locator$);
		String webHome$=locator.getProperty(WContext.WEB_HOME);
		String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		String entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
		String facetHandlerClass$=locator.getProperty(JFacetOpenItem.FACET_HANDLER_CLASS);
		if(entityLabel$==null&&entityKey$!=null)
			entityLabel$=entigrator.indx_getLabel(entityKey$);
		String webRequester$=locator.getProperty(WContext.WEB_REQUESTER);
		String title$=locator.getProperty(Locator.LOCATOR_TITLE);
StringBuffer sb=new StringBuffer();
sb.append("<!DOCTYPE html>");
sb.append("<html>");
sb.append("<head>");
sb.append(WUtils.getMenuBarScript());
sb.append(WUtils.getMenuBarStyle());
sb.append("</head>");
sb.append("<body onload=\"onLoad()\">");
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
sb.append("</strong></td></tr><tr><td>Entity:</td><td><strong>");
sb.append(entityLabel$);
sb.append("</strong></td></tr>");
sb.append("<tr><td>Facet:</td><td><strong>");
if(BookmarksHandler.class.getName().equals(facetHandlerClass$))
sb.append("Bookmarks");
if(FolderHandler.class.getName().equals(facetHandlerClass$))
sb.append("Folder");
sb.append("</strong></td></tr>");
sb.append("<tr><td>Context:</td><td><strong>");

sb.append("Slideshow");
sb.append("</strong></td></tr>");

sb.append("</table>");
String[]sa=null;
Properties imLocator=new Properties();
MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
mimetypesFileTypeMap.addMimeTypes("image png jpg jpeg bmp gif "); 
ArrayList<String>sl=new ArrayList<String>();
Hashtable<String,String> ht=new Hashtable<String,String>();
String fname$;
String fpath$;
String ftitle$;
if(BookmarksHandler.class.getName().equals(facetHandlerClass$)){
	Sack entity=entigrator.getEntityAtKey(entityKey$);
	Core[] ca=entity.elementGet("jbookmark");
	for(Core c:ca){
		fname$=Locator.getProperty(c.value, JFolderPanel.FILE_NAME);
		if(fname$!=null)
		if("image".equalsIgnoreCase(mimetypesFileTypeMap.getContentType(fname$))){
		imLocator=Locator.toProperties(c.value);
		  ftitle$=c.type;
		  
		  fpath$=entigrator.getEntihome()+"/"+imLocator.getProperty(JFolderPanel.FILE_PATH);
		  if(fname$!=null&&fpath$!=null)
			  sl.add(ftitle$);
		      ht.put(ftitle$, fpath$);
		}
	}
}
if(FolderHandler.class.getName().equals(facetHandlerClass$)){
	File folder=new File(entigrator.getEntihome()+"/"+entityKey$);
	File[] fa=folder.listFiles();
	for(File f:fa){
		fname$=f.getName();
		if("image".equalsIgnoreCase(mimetypesFileTypeMap.getContentType(fname$))){
		  ftitle$=f.getName();
		  fpath$=f.getPath();
		  sl.add(ftitle$);
	      ht.put(ftitle$, fpath$);
		}
	}
}
	if(sl.size()>0){
	Collections.sort(sl);
	sb.append("<table><tr><td>");
	sb.append("<select id=\"selector\" size=\"1\" onchange=\"setTitle()\">");
   	for(String s:sl){
   		if(s.equals(title$))
   		   sb.append("<option value=\""+s+"\" selected=\"selected\" >"+s+"</option>");
   		else
   		sb.append("<option value=\""+s+"\">"+s+"</option>");
   	}
	  sb.append("</select><td>");
	}
	sa=getNavigators(sl,title$);
	if(sa!=null){
		if(sa[0]!=null){
			sb.append("<td><button onclick=\"prev()\">Prev</button></td>");	
		}
		if(sa[1]!=null){
			sb.append("<td><button onclick=\"next()\">Next</button></td>");	
		}
		if(debug){
			System.out.println("JFileOpenItem:getWebView:prev locator="+sa[0]);	
			System.out.println("JFileOpenItem:getWebView:next locator="+sa[1]);
		}
	}
	sb.append("</tr><table>");
	sb.append("<P></P>");
	ftitle$=sl.get(0);
    if(title$!=null)
    	ftitle$=title$;
    fpath$=ht.get(ftitle$);
    
    BufferedImage bm=ImageIO.read(new File(fpath$));
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ImageIO.write( bm, "png", bos );
    bos.flush();
       byte[] ba = bos.toByteArray();
    bos.close();
      String icon$= Base64.encodeBase64String(ba);
      sb.append("<img src=\"data:image/png;base64,"+icon$+"\" alt=\""+ftitle$+"\">");

sb.append("<script>");
sb.append("function prev() {");
if(sa!=null&&sa[0]!=null){
	String urlHeader$=webHome$+"?"+WContext.WEB_LOCATOR+"=";
	locator.setProperty(Locator.LOCATOR_TITLE, sa[0]);
	sb.append("var url=\""+urlHeader$+Base64.encodeBase64URLSafeString(Locator.toString(locator).getBytes())+"\";");
	sb.append("window.location.assign(url);");	
}
sb.append("}");
sb.append("function next() {");
if(sa!=null&&sa[1]!=null){
	String urlHeader$=webHome$+"?"+WContext.WEB_LOCATOR+"=";
	locator.setProperty(Locator.LOCATOR_TITLE, sa[1]);
	sb.append("var url=\""+urlHeader$+Base64.encodeBase64URLSafeString(Locator.toString(locator).getBytes())+"\";");
	sb.append("window.location.assign(url);");	
}
sb.append("}");

sb.append("function onLoad() {");
sb.append("initBack(\""+this.getClass().getName()+"\",\""+webRequester$+"\");");
sb.append("}");
sb.append("function setTitle() {");
sb.append("var locator =\""+locator$+"\";");
sb.append("var title = document.getElementById(\"selector\").value;");
sb.append("locator=appendProperty(locator,\""+Locator.LOCATOR_TITLE+"\",title);");
String urlHeader$=webHome$+"?"+WContext.WEB_LOCATOR+"=";
sb.append("console.log(locator);");
sb.append("var url=\""+urlHeader$+"\"+window.btoa(locator);");
sb.append("window.location.assign(url);");
sb.append("}");
sb.append("window.localStorage.setItem(\""+this.getClass().getName()+"\",\""+Base64.encodeBase64URLSafeString(locator$.getBytes())+"\");");
sb.append("</script>");
sb.append("</body>");
sb.append("</html>"); 
return sb.toString();
	}catch(Exception e){
		Logger.getLogger(JFileOpenItem.class.getName()).severe(e.toString());	
	}
	return null;
}


@Override
public String getWebConsole(Entigrator entigrator, String locator$) {
	// TODO Auto-generated method stub
	return null;
}
private String[] getNavigators(ArrayList<String>sl,String title$){
	try{
		if(title$==null)
			title$=sl.get(0);
		int i=0;
		for(String s:sl){
			if(title$.equals(s)){
				String[] sa=new String[2];
				if(i>0)
					sa[0]=sl.get(i-1);
				else
					sa[0]=null;
				if(sl.size()>(i+1))
					sa[1]=sl.get(i+1);
				else
					sa[1]=null;
				return sa;
			}
			i++;
		}
	}catch(Exception e){
		Logger.getLogger(JFileOpenItem.class.getName()).severe(e.toString());
	}
	return null;
}
}
