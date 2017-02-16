package gdt.jgui.console;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import gdt.data.entity.BaseHandler;
import gdt.data.grain.Locator;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JItemPanel;
import gdt.jgui.console.JItemsListPanel;
import gdt.jgui.console.JMainConsole;
/**
* This context displays a list of all categories (types of entities).   
* @author  Alexander Imas
* @version 1.0
* @since   2016-03-11
*/


public class JRecentPanel extends JItemsListPanel {
	private static final String RECENT="recent";
	String entihome$;
Hashtable<String,JItemPanel> items;
JMenu menu;
static boolean debug=false;
	private static final long serialVersionUID = 1L;
	/**
	 * Default constructor
	 *  
	 */
	public JRecentPanel() {
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
	    locator.setProperty(Locator.LOCATOR_TITLE, getTitle());
	    if(entihome$!=null){
	    locator.setProperty(Entigrator.ENTIHOME,entihome$);
	    
	    }
	    locator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
		locator.setProperty(Locator.LOCATOR_ICON_CLASS,getClass().getName());
		locator.setProperty(Locator.LOCATOR_ICON_FILE,"recent.png");
	   // 
	    locator.setProperty(Locator.LOCATOR_TITLE, getTitle());
	    locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
	    locator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
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
		this.console=console;
		//if(debug)
        //System.out.println("JRecentPanel:instantiate:locator="+Locator.remove(locator$,Locator.LOCATOR_ICON));
		try{
        Properties locator=Locator.toProperties(locator$);
		entihome$=locator.getProperty(Entigrator.ENTIHOME);
		if(entihome$!=null){
		    locator.setProperty(Entigrator.ENTIHOME,entihome$);
		}
		 
		 locator.setProperty(Locator.LOCATOR_TITLE, getTitle());
		ArrayList<JItemPanel>ipl=new ArrayList<JItemPanel>();
		@SuppressWarnings("unchecked")
		Hashtable<String,String> recents=(Hashtable<String,String>)console.getRecents();
		Enumeration<String> en=recents.keys();
		JItemPanel itemPanel;
		 String itemLocator$;
		while(en.hasMoreElements()){
			itemLocator$=recents.get(en.nextElement());
			itemLocator$=Locator.append(itemLocator$,Locator.LOCATOR_CHECKABLE,Locator.LOCATOR_TRUE);
			itemPanel=new JItemPanel(console,itemLocator$);
			ipl.add(itemPanel); 
		}
		Collections.sort(ipl,new ItemPanelComparator()); 
		putItems(ipl.toArray(new JItemPanel[0]));
		   }catch(Exception e){
				Logger.getLogger(getClass().getName()).info(e.toString());
			}	 

		return this;
		
	}
	/**
	 * Get context title.
	 * @return the title string.
	 */
	@Override
	public String getTitle() {
		return "Recents";
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
		return "recents";
	}
	/**
	 * Complete the context after
	 * remove it from the main console.
	 */	
	@Override
	public void close() {
	
	}
	/**
	 * Get context menu.
	 * @return the context menu.
	 */		
	@Override
	public JMenu getContextMenu() {
		menu=super.getContextMenu();
		menu.addMenuListener(new MenuListener(){
			@Override
			public void menuSelected(MenuEvent e) {
			menu.removeAll();
			if(hasSelectedItems()){
				JMenuItem deleteItem = new JMenuItem("Delete");
					deleteItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							JItemPanel[] ipa=JRecentPanel.this.getItems();
							ArrayList<String>sl=new ArrayList<String>();
							for(JItemPanel ip:ipa)
								if(!ip.isChecked())
									sl.add(ip.getLocator());
							
							String[] sa=sl.toArray(new String[0]);
							console.recents.clear();
							if(sa!=null){
								String title$;
								for(String aSa:sa){
									title$=Locator.getProperty(aSa,Locator.LOCATOR_TITLE);
									if(title$!=null)
									console.recents.put(title$,aSa);
								}
							}
							JRecentPanel recentPanel=new JRecentPanel();
							JConsoleHandler.execute(console, recentPanel.getLocator());
						}
					} );
					menu.add(deleteItem);
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
	
	public static void store(JMainConsole console){
		try{
			
			File home=new File(System.getProperty("user.home")+"/.entigrator");
			if(!home.exists())
				home.mkdir();
			File recentFile=new File(home,RECENT);
			if(recentFile.exists())
				recentFile.delete();
			if(console.recents==null||console.recents.isEmpty())
				return ;
			ArrayList <String>sl=new ArrayList<String>();
			Enumeration<String> en=console.recents.keys();
			//String locator$;
			while(en.hasMoreElements()){
				sl.add(console.recents.get(en.nextElement()));
			}
			String[] sa= sl.toArray(new String[0]);
			if(sa==null){
				if(debug)
				System.out.println("JRecentPanel:store:no content");
				return;
			}
			if(debug)
			System.out.println("JRecentPanel:store:sa="+sa.length);
   	     FileOutputStream fos = new FileOutputStream(recentFile);
	     OutputStreamWriter osw = new OutputStreamWriter(fos);
	     for(String aSa:sa)
	            osw.write(aSa+Locator.GROUP_DELIMITER);
	     osw.close();
		}catch(Exception e){
			Logger.getLogger(JRecentPanel.class.getName()).severe(e.toString());
		}
	}
	/**
	 * Restore clipboard from the disk. 
	 * @param console the main console.
	 * 
	 */
	public static void restore(JMainConsole console){
		try{
			
			File home=new File(System.getProperty("user.home")+"/.entigrator");
			if(!home.exists())
				home.mkdir();
			File recentFile=new File(home,RECENT);
			if(!recentFile.exists())
				return;
			FileInputStream fis = new FileInputStream(recentFile);
	        InputStreamReader inp = new InputStreamReader(fis, "UTF-8");
	        BufferedReader rd = new BufferedReader(inp);
			console.recents.clear();
			String line$=rd.readLine();
			rd.close();
			fis.close();
			String[] sa=line$.split(Locator.GROUP_DELIMITER);
			String title$;
			for(String s:sa){
				title$=Locator.getProperty(s, Locator.LOCATOR_TITLE);
				 console.recents.put(title$,s);
			}
			rd.close();
			fis.close();
		}catch(Exception e){
			Logger.getLogger(JClipboard.class.getName()).severe(e.toString());
		}
	}
	@Override
	public void activate() {
		// TODO Auto-generated method stub
		
	}	
}
