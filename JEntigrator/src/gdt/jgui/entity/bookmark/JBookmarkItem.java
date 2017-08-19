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
import java.util.logging.Logger;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import gdt.data.entity.BaseHandler;
import gdt.data.grain.Locator;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JItemPanel;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.entity.folder.JFolderPanel;
import gdt.jgui.tool.JIconSelector;
import gdt.jgui.tool.JTextEditor;

/**
 * This class represent a bookmarked item in the
 * bookmarks list console.
 * @author imasa
 *
 */
public class JBookmarkItem extends JItemPanel {

	private static final long serialVersionUID = 1L;
/**
 * Indicates the rename bookmark action.
 */
	public static final String ACTION_RENAME="action rename";
	/**
	 * Indicates the set bookmark icon action.
	 */
	public static final String ACTION_SET_ICON="action set icon";
	JMenuItem pathItem;
	boolean debug=false;
	/**
	 * The constructor.
	 * @param console the main console.
	 * @param locator$ the locator string.
	 */
	public JBookmarkItem(final JMainConsole console,final String locator$){
		super(console,locator$);
		if(debug)
			System.out.println("JBookmarksItem:locator="+locator$);
		
		
		final String entihome$=Locator.getProperty(locator$, Entigrator.ENTIHOME);
		final String filePath$=entihome$+"/"+Locator.getProperty(locator$, JFolderPanel.FILE_PATH);
		popup = new JPopupMenu();
		JMenuItem renameItem=new JMenuItem("Rename");
		   popup.add(renameItem);
		   renameItem.setHorizontalTextPosition(JMenuItem.RIGHT);
		   renameItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try{
					String bmLocator$=locator$;
					bmLocator$=Locator.append(bmLocator$,JRequester.REQUESTER_ACTION, ACTION_RENAME);
					bmLocator$=Locator.append(bmLocator$,BaseHandler.HANDLER_CLASS,JBookmarksEditor.class.getName());
					bmLocator$=Locator.append(bmLocator$,BaseHandler.HANDLER_METHOD,"response");
					bmLocator$=Locator.append(bmLocator$,Entigrator.ENTIHOME,entihome$);
					JTextEditor te=new JTextEditor();
					String teLocator$=te.getLocator();
					teLocator$=Locator.append(teLocator$, JTextEditor.TEXT, title$);
					teLocator$=Locator.append(teLocator$,JRequester.REQUESTER_RESPONSE_LOCATOR,Locator.compressText(bmLocator$));
					teLocator$=Locator.append(teLocator$,Entigrator.ENTIHOME,entihome$);
					JConsoleHandler.execute(console, teLocator$);
					}catch(Exception ee){
						Logger.getLogger(getClass().getName()).info(ee.toString());
					}
				}
			    });
		  
		   JMenuItem setIconItem=new JMenuItem("Set icon");
		   popup.add(setIconItem);
		   setIconItem.setHorizontalTextPosition(JMenuItem.RIGHT);
		   setIconItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try{
					   String bmLocator$=getLocator();
						  String entihome$=Locator.getProperty(bmLocator$,Entigrator.ENTIHOME);
						   bmLocator$=Locator.append(bmLocator$,JRequester.REQUESTER_ACTION, ACTION_SET_ICON);
							bmLocator$=Locator.append(bmLocator$,BaseHandler.HANDLER_CLASS,JBookmarksEditor.class.getName());
							bmLocator$=Locator.append(bmLocator$,BaseHandler.HANDLER_METHOD,"response");
							 JIconSelector is=new JIconSelector();
							 String isLocator$=is.getLocator();
							 isLocator$=Locator.append(isLocator$,Entigrator.ENTIHOME,entihome$);
							isLocator$=Locator.append(isLocator$,JRequester.REQUESTER_RESPONSE_LOCATOR, Locator.compressText(bmLocator$));
						   JConsoleHandler.execute(console,isLocator$);
					}catch(Exception ee){
						Logger.getLogger(getClass().getName()).info(ee.toString());
					}
				}
			    });
		if(filePath$!=null){
			 JMenuItem pathItem=new JMenuItem("Display path");
			   popup.add(pathItem);
			   pathItem.setHorizontalTextPosition(JMenuItem.RIGHT);
			   pathItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try{
							String bmLocator$=locator$;
							bmLocator$=Locator.append(bmLocator$,BaseHandler.HANDLER_CLASS,JBookmarksEditor.class.getName());
							bmLocator$=Locator.append(bmLocator$,BaseHandler.HANDLER_METHOD,"response");
						JTextEditor jte=new JTextEditor();
						String jteLocator$=jte.getLocator();
						String entihome$=Locator.getProperty(locator$,Entigrator.ENTIHOME);
						jteLocator$=Locator.append(jteLocator$,JRequester.REQUESTER_RESPONSE_LOCATOR, Locator.compressText(bmLocator$));
						jteLocator$=Locator.append(jteLocator$,JTextEditor.TEXT,filePath$);
						jteLocator$=Locator.append(jteLocator$,Entigrator.ENTIHOME,entihome$);
						JConsoleHandler.execute(console,jteLocator$);
						}catch(Exception ee){
							Logger.getLogger(getClass().getName()).info(ee.toString());
						}
					}
				    });
		}
	}
}
