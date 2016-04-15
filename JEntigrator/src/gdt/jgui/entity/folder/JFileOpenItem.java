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
import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import gdt.data.entity.ArchiveFileFilter;
import gdt.data.entity.ArchiveHandler;
import gdt.data.entity.BaseHandler;
import gdt.data.grain.Locator;
import gdt.data.store.Entigrator;
import gdt.data.store.FileExpert;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JItemPanel;
import gdt.jgui.console.JRequester;
import gdt.jgui.tool.JTextEditor;
/**
 * This class represents a file item within the
 * folder panel context. 
 * @author imasa
 *
 */
public class JFileOpenItem extends JItemPanel {
	private static final long serialVersionUID = 1L;
	JMenuItem editItem;
	JMenuItem importItem;
/**
 * The constructor.
 */
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
				String filePath$=locator.getProperty(JFolderPanel.FILE_PATH);
				String entihome$=locator.getProperty(Entigrator.ENTIHOME);
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
				String filePath$=locator.getProperty(JFolderPanel.FILE_PATH);
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
		 
		  String filePath$=Locator.getProperty(locator$,JFolderPanel.FILE_PATH);
		  System.out.println("JFileOpenItem:file path="+filePath$);
		  String contentType$=ArchiveHandler.detectContentOfArchive(filePath$);
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
}
