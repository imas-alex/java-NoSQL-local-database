package gdt.jgui.tool;
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
import gdt.data.entity.EntityHandler;
import gdt.data.grain.Locator;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.entity.JEntitiesPanel;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.entity.JEntityPrimaryMenu;

import javax.swing.ComboBoxModel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Logger;
/**
 * This class is the search panel. It searches entities by the label. 
 * @author imasa
 *
 */
public class JSearchPanel extends JPanel implements JContext {
private static final long serialVersionUID = 1L;
String entihome$;
JMainConsole console;
AutocompleteJComboBox comboBox;
JMenuItem openItem;
JMenuItem listItem;
/**
 * The default constructor.
 */
	public JSearchPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{200};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0};
		setLayout(gridBagLayout);
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
		 * Get the context locator.
		 * @return the context locator.
		 */
		@Override
		public String getLocator() {
			Properties locator=new Properties();
		    locator.setProperty(Locator.LOCATOR_TYPE, JContext.CONTEXT_TYPE);
		    locator.setProperty(Locator.LOCATOR_TITLE, getTitle());
		    locator.setProperty(JContext.CONTEXT_TYPE,getType());
		    if(entihome$!=null)
			       locator.setProperty(Entigrator.ENTIHOME,entihome$);
		   locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
		   locator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
		   String icon$=Support.readHandlerIcon(JEntityPrimaryMenu.class, "search.png");
		   locator.setProperty( Locator.LOCATOR_ICON,icon$);
		   return Locator.toString(locator);
		}
		@Override
		/**
		 * Create the context.
		 * @param console the main console.
		 * @param locator$ the locator string.
		 * @return the procedure context.
		 */		
public JContext instantiate(JMainConsole console, String locator$) {
			this.console=console;
			 Properties locator=Locator.toProperties(locator$);
			 entihome$=locator.getProperty(Entigrator.ENTIHOME);
			 try{
				 Entigrator entigrator=console.getEntigrator(entihome$);
				 String[] labels=entigrator.indx_listAllLabels();
		//		 System.out.println("SearchPanel:instantiate.labels="+labels.length);
				 comboBox = new AutocompleteJComboBox(labels);
					GridBagConstraints gbc_comboBox = new GridBagConstraints();
					gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
					gbc_comboBox.insets = new Insets(5, 5, 5, 5);
					gbc_comboBox.gridx = 0;
					gbc_comboBox.gridy = 0;
					gbc_comboBox.anchor=GridBagConstraints.FIRST_LINE_START;
					add(comboBox, gbc_comboBox);
					
					JPanel panel = new JPanel();
					GridBagConstraints gbc_panel = new GridBagConstraints();
					gbc_panel.insets = new Insets(0, 0, 5, 5);
					gbc_panel.fill = GridBagConstraints.BOTH;
					gbc_panel.gridx = 0;
					gbc_panel.gridy = 1;
					add(panel, gbc_panel); 
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
			return "Search for label";
		}
		/**
		 * Get context subtitle.
		 * @return the context subtitle.
		 */	
		@Override
		public String getSubtitle() {
			try{
			  return console.getEntigrator(entihome$).getBaseName();	
			}catch(Exception e){
			return null;
			}
		}
		/**
		 * Get context type.
		 * @return the context type.
		 */	
		@Override
		public String getType() {
			return "Search panel";
		}
		/**
		 * No action.
		 */
		@Override
		public void close() {
			// TODO Auto-generated method stub
			
		}
/**
 * Get context menu.
 * @return the context menu. 
 */
		@Override
		
public JMenu getContextMenu() {
			final JMenu menu=new JMenu("Context");
			menu.addMenuListener(new MenuListener(){
				@Override
				public void menuSelected(MenuEvent e) {
//				System.out.println("EntityEditor:getConextMenu:menu selected");
				if(openItem!=null) 
				     menu.remove(openItem);
				if(listItem!=null)
				   menu.remove(listItem);
				ComboBoxModel<String> model=comboBox.getModel();
				if(model.getSize()>0){
				  openItem = new JMenuItem("Open");
				openItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						  JEntityFacetPanel ep=new JEntityFacetPanel();
						  String locator$=ep.getLocator();
						   locator$=Locator.append(locator$, Entigrator.ENTIHOME, entihome$);
						   String entityLabel$=(String)comboBox.getSelectedItem();
						   Entigrator entigrator=console.getEntigrator(entihome$);
						   String entityKey$=entigrator.indx_keyAtLabel(entityLabel$);
						   locator$=Locator.append(locator$, EntityHandler.ENTITY_KEY, entityKey$);
						   JConsoleHandler.execute(console, locator$);
					}
				} );
				menu.add(openItem);
				listItem = new JMenuItem("List");
				listItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
					   JEntitiesPanel ep=new JEntitiesPanel();
					   String locator$=ep.getLocator();
					   locator$=Locator.append(locator$, Entigrator.ENTIHOME, entihome$);
					   ComboBoxModel<String> model=comboBox.getModel();
					   ArrayList<String>sl=new ArrayList<String>();
					   int cnt=model.getSize();
					   for(int i=1;i<cnt;i++)
						   sl.add(model.getElementAt(i));
					   String[] sa=sl.toArray(new String[0]);
					   String sa$=Locator.toString(sa);
					   locator$=Locator.append(locator$, EntityHandler.ENTITY_LIST, sa$);
					   JConsoleHandler.execute(console, locator$);
					   	}
				} );
				menu.add(listItem);
					}
					}
				
				@Override
				public void menuDeselected(MenuEvent e) {
				}
	    		@Override
				public void menuCanceled(MenuEvent e) {
				}
			});
			JMenuItem doneItem = new JMenuItem("Done");
			doneItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
						  console.back();
				}
			} );
			menu.add(doneItem);
			return menu;
		}
}
