package gdt.jgui.console;
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
import gdt.data.entity.BaseHandler;
import gdt.data.grain.Locator;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.logging.Logger;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
/**
 * This context contains a list of clipboard items.
 */
public class JClipboardPanel extends JItemsListPanel {
	
	private static final long serialVersionUID = 1L;
	private Logger LOGGER=Logger.getLogger(getClass().getName());
	JMenuItem	deleteItem;
	boolean debug=false;
	public JClipboardPanel() {
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
		    locator.setProperty(Locator.LOCATOR_TITLE,"Clipboard");
		    locator.setProperty(Locator.LOCATOR_ICON_CONTAINER, Locator.LOCATOR_ICON_CONTAINER_CLASS);
		    locator.setProperty(Locator.LOCATOR_ICON_CLASS,getClass().getName());
		    locator.setProperty(Locator.LOCATOR_ICON_FILE, "clipboard.png"); 
		    locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
		    locator.setProperty(BaseHandler.HANDLER_CLASS,JClipboardPanel.class.getName());
			return Locator.toString(locator);
	}
	/**
	 * Get context title.
	 * @return the title string.
	 */	
	@Override
	public String getTitle() {
		return "Clipboard";
	}
	/**
	 * Get context subtitle.
	 * @return the subtitle string.
	 */	
	@Override
	public String getSubtitle() {
		return null;
	}
	/**
	 * Get context type.
	 * @return the type string.
	 */	
	@Override
	public String getType() {
		return "Clipboard";
	}
	/**
	 * Complete the context after
	 * remove it from the main console.
	 */	
	@Override
	public void close() {
	}
	/**
	 * Create the context.
	 *  @param console the main application console
	 *  @param locator$ the locator string.
	 * @return the context.
	 */	
	@Override
	public JContext instantiate(JMainConsole console, String locator$) {
		try{
        	this.console=console;
			String []sa=console.clipboard.getContent();
        	ArrayList <JItemPanel>ipl=new ArrayList<JItemPanel>();
        	JItemPanel itemPanel;
        	
        	if(sa!=null)
        		for(String aSa:sa){
        			if(debug)
        				System.out.println("JClipboardPanel:instantiate:locator="+aSa);
        			aSa=Locator.append(aSa, Locator.LOCATOR_CHECKABLE, Locator.LOCATOR_TRUE);
        			itemPanel=new JItemPanel(console,aSa);
        			
        			ipl.add(itemPanel);
        		}
        	Collections.sort(ipl,new ItemPanelComparator());
        	putItems(ipl.toArray(new JItemPanel[0]));
        	return this;
        }catch(Exception e){
        LOGGER.severe(e.toString());
        return null;
        }
	}
	/**
	 * Get context menu. 
	 * @return context menu..
	 */	
	@Override
	public JMenu getContextMenu() {
		menu=super.getContextMenu();
		menu.addMenuListener(new MenuListener(){
			@Override
			public void menuSelected(MenuEvent e) {
			if(deleteItem!=null) 
				menu.remove(deleteItem);
			if(hasSelectedItems()){
				deleteItem = new JMenuItem("Delete");
					deleteItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							JItemPanel[] ipa=JClipboardPanel.this.getItems();
							ArrayList<String>sl=new ArrayList<String>();
							for(JItemPanel ip:ipa)
								if(!ip.isChecked())
									sl.add(ip.getLocator());
							
							String[] sa=sl.toArray(new String[0]);
							console.clipboard.clear();
							if(sa!=null)
								for(String aSa:sa)
									console.clipboard.putString(aSa);
							JClipboardPanel clipboardPanel=new JClipboardPanel();
							JConsoleHandler.execute(console, clipboardPanel.getLocator());
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
	@Override
	public void activate() {
		// TODO Auto-generated method stub
		
	}
}
