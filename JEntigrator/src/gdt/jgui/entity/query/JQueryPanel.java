package gdt.jgui.entity.query;
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
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.Stack;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.facet.FolderHandler;
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
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.entity.JEntitiesPanel;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.entity.JEntityPrimaryMenu;
import gdt.jgui.entity.JReferenceEntry;
import gdt.jgui.entity.folder.JFolderFacetAddItem;
import gdt.jgui.entity.folder.JFolderFacetOpenItem;
import gdt.jgui.entity.view.JViewPanel;
import gdt.jgui.tool.JTextEditor;
/**
 * This class represents the query context
 * @author imasa.
 *
 */

public class JQueryPanel extends JPanel implements JFacetRenderer,JRequester{
	
	private static final long serialVersionUID = 1L;
	private static final String ACTION_CREATE_QUERY="action create query";
	private Logger LOGGER=Logger.getLogger(getClass().getName());
	private JComboBox<String> componentComboBox; 
	private JComboBox<String> elementComboBox; 
	private JComboBox<String> itemNameFieldComboBox;
	private JComboBox<String> itemNameComboBox;
	private JComboBox<String> itemValueComboBox;
	private JComboBox<String> itemTypeComboBox;
	private JTable table;
	JScrollPane scrollPane;
	private TableRowSorter<DefaultTableModel> sorter;
	protected String entihome$;
	protected String entityKey$;
	protected String entityLabel$;
	protected Sack entity=null;
	protected JMainConsole console;
	protected Entigrator entigrator;
	private JMenu menu;
	static boolean debug=true;
	protected ArrayList <String>queryScope;
	protected ArrayList <String>elementScope;
	/**
	 * The default constructor.
	 */
	public JQueryPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{100, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0};
		setLayout(gridBagLayout);
		
		JLabel lblComponent = new JLabel("Component");
		
		GridBagConstraints gbc_lblComponent = new GridBagConstraints();
		gbc_lblComponent.insets = new Insets(5, 5, 5, 5);
		gbc_lblComponent.gridx = 0;
		gbc_lblComponent.gridy = 0;
		gbc_lblComponent.anchor=GridBagConstraints.FIRST_LINE_START;
		add(lblComponent, gbc_lblComponent);
		
		componentComboBox = new JComboBox<String>();
		GridBagConstraints gbc_componentComboBox = new GridBagConstraints();
		gbc_componentComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_componentComboBox.insets = new Insets(0, 0, 5, 0);
		gbc_componentComboBox.gridx = 1;
		gbc_componentComboBox.gridy = 0;
		gbc_componentComboBox.anchor=GridBagConstraints.FIRST_LINE_START;
		add(componentComboBox, gbc_componentComboBox);
		componentComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				try{
					if (e.getStateChange() == ItemEvent.SELECTED) 
					initElementSelector();
					initItemValueSelector();
				}catch(Exception ee){
					LOGGER.severe(ee.toString());
				}
			}
		 });
		
		JLabel lblElement = new JLabel("Element");
		
		GridBagConstraints gbc_lblElement = new GridBagConstraints();
		gbc_lblElement.insets = new Insets(5, 5, 5, 5);
		gbc_lblElement.gridx = 0;
		gbc_lblElement.gridy = 1;
		gbc_lblElement.anchor=GridBagConstraints.FIRST_LINE_START;
		add(lblElement, gbc_lblElement);
		
		elementComboBox = new JComboBox<String>();
		GridBagConstraints gbc_elementComboBox = new GridBagConstraints();
		gbc_elementComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_elementComboBox.insets = new Insets(0, 0, 5, 0);
		gbc_elementComboBox.gridx = 1;
		gbc_elementComboBox.gridy = 1;
		gbc_elementComboBox.anchor=GridBagConstraints.FIRST_LINE_START;
		add(elementComboBox, gbc_elementComboBox);
		elementComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				try{
					if (e.getStateChange() == ItemEvent.SELECTED) 
					initItemNameSelector();
					initItemValueSelector();
				}catch(Exception ee){
					LOGGER.severe(ee.toString());
				}
			}
		 });
		JLabel lblItemNameField = new JLabel("Item field");
		GridBagConstraints gbc_lblItemField = new GridBagConstraints();
		gbc_lblItemField.insets = new Insets(5, 5, 5, 5);
		gbc_lblItemField.gridx = 0;
		gbc_lblItemField.gridy = 2;
		gbc_lblItemField.anchor=GridBagConstraints.FIRST_LINE_START;
		
		add(lblItemNameField, gbc_lblItemField);

		itemNameFieldComboBox = new JComboBox<String>();
		GridBagConstraints gbc_itemNameFieldComboBox = new GridBagConstraints();
		gbc_itemNameFieldComboBox.insets = new Insets(0, 0, 5, 0);
		gbc_itemNameFieldComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_itemNameFieldComboBox.gridx = 1;
		gbc_itemNameFieldComboBox.gridy = 2;
		gbc_itemNameFieldComboBox.anchor=GridBagConstraints.FIRST_LINE_START;
		add(itemNameFieldComboBox, gbc_itemNameFieldComboBox);
		itemNameFieldComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				try{
					if (e.getStateChange() == ItemEvent.SELECTED) 
						initItemNameSelector();
					    initItemValueSelector();
				}catch(Exception ee){
					LOGGER.severe(ee.toString());
				}
			}
		 });
		JLabel itemTitle = new JLabel("Item title");
		GridBagConstraints gbc_lblItemtitle = new GridBagConstraints();
		gbc_lblItemtitle.insets = new Insets(5, 5, 5, 5);
		gbc_lblItemtitle.gridx = 0;
		gbc_lblItemtitle.gridy = 3;
		gbc_lblItemtitle.anchor=GridBagConstraints.FIRST_LINE_START;
		add(itemTitle, gbc_lblItemtitle);

		itemNameComboBox = new JComboBox<String>();
		GridBagConstraints gbc_itemComboBox = new GridBagConstraints();
		gbc_itemComboBox.insets = new Insets(0, 0, 5, 0);
		gbc_itemComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_itemComboBox.gridx = 1;
		gbc_itemComboBox.gridy = 3;
		gbc_itemComboBox.anchor=GridBagConstraints.FIRST_LINE_START;
		add(itemNameComboBox, gbc_itemComboBox);
		itemNameComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				try{
					initItemValueSelector();
					initType();
				}catch(Exception ee){
					LOGGER.severe(ee.toString());
				}
			}
		 });
		
		JLabel itemValue = new JLabel("Item value");
		GridBagConstraints gbc_lblItemValue = new GridBagConstraints();
		gbc_lblItemValue.insets = new Insets(5, 5, 5, 5);
		gbc_lblItemValue.weighty=0.0;
		gbc_lblItemValue.gridx = 0;
		gbc_lblItemValue.gridy = 4;
		gbc_lblItemValue.anchor=GridBagConstraints.FIRST_LINE_START;
		itemValue.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
			   System.out.println("JQueryPanel:item titel="+itemNameComboBox.getSelectedItem()+" value="+itemValueComboBox.getSelectedItem());
			   selectRow(itemNameComboBox.getSelectedItem().toString(),itemValueComboBox.getSelectedItem().toString());
			}
		});
		add(itemValue, gbc_lblItemValue);

		itemValueComboBox = new JComboBox<String>();
		GridBagConstraints gbc_itemValueComboBox = new GridBagConstraints();
		gbc_itemValueComboBox.insets = new Insets(0, 0, 5, 0);
		gbc_itemValueComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_itemValueComboBox.gridx = 1;
		gbc_itemValueComboBox.gridy = 4;
		gbc_itemValueComboBox.anchor=GridBagConstraints.FIRST_LINE_START;
		add(itemValueComboBox, gbc_itemValueComboBox);
		
		JLabel itemType = new JLabel("Item type");
		GridBagConstraints gbc_lblItemType = new GridBagConstraints();
		gbc_lblItemType.insets = new Insets(5, 5, 5, 5);
		gbc_lblItemType.weighty=0.0;
		gbc_lblItemType.gridx = 0;
		gbc_lblItemType.gridy = 5;
		gbc_lblItemType.anchor=GridBagConstraints.FIRST_LINE_START;
		add(itemType, gbc_lblItemType);
		
		itemTypeComboBox = new JComboBox<String>();
		GridBagConstraints gbc_itemTypeComboBox = new GridBagConstraints();
		gbc_itemTypeComboBox.insets = new Insets(0, 0, 5, 0);
		gbc_itemTypeComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_itemTypeComboBox.gridx = 1;
		gbc_itemTypeComboBox.gridy = 5;
		gbc_itemTypeComboBox.anchor=GridBagConstraints.FIRST_LINE_START;
		add(itemTypeComboBox, gbc_itemTypeComboBox);
		 scrollPane = new JScrollPane( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		GridBagConstraints gbc_scroll_panel = new GridBagConstraints();
		gbc_scroll_panel.anchor = GridBagConstraints.NORTH;
		gbc_scroll_panel.gridwidth = 2;
		gbc_scroll_panel.weighty=1.0;
		//gbc_scroll_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_scroll_panel.fill = GridBagConstraints.BOTH;
		gbc_scroll_panel.gridx = 0;
		gbc_scroll_panel.gridy =6;
		add(scrollPane, gbc_scroll_panel);
		//add(scrollPane);
		//scrollPane.setMinimumSize( scrollPane.getPreferredSize() );
		scrollPane.setMinimumSize( scrollPane.getPreferredSize() );
		//table.setFillsViewportHeight(true);
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
	 * Get the context menu.
	 * @return the context menu.
	 */
	@Override
	public JMenu getContextMenu() {
		menu=new JMenu("Context");
		menu.addMenuListener(new MenuListener(){
				@Override
				public void menuSelected(MenuEvent e) {
				menu.removeAll();
				if(debug)
				System.out.println("JQueryPanel:getConextMenu:menu selected");
				 JMenuItem initItem = new JMenuItem("Init selector");
					initItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
						  initComponentSelector();
						}
					} );
					menu.add(initItem);	 
				JMenuItem selectItem = new JMenuItem("Select");
						selectItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
							 initTable();
								// if(showHeader())
							  //showContent();
							}
						} );
						menu.add(selectItem);
						JMenuItem clearHeader = new JMenuItem("Clear all");
						clearHeader.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
							    clearHeader();
							   /*
							    showHeader();
								showContent();
								*/
							}
						} );
						menu.add(clearHeader);
						Entigrator entigrator=console.getEntigrator(entihome$);
						Sack query=entigrator.getEntityAtKey(entityKey$);
						if(query.getElementItem("parameter", "noreset")==null){
						JMenuItem resetItem = new JMenuItem("Reset");
						resetItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								 int response = JOptionPane.showConfirmDialog(console.getContentPanel(), "Reset source to default ?", "Confirm",
						  			        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						    		  if (response == JOptionPane.YES_OPTION)
						    			  reset();
							}
						} );
						menu.add(resetItem);
						}
						JMenuItem folderItem = new JMenuItem("Open folder");
						folderItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
							try{
								File file=new File(entihome$+"/"+entityKey$);
								Desktop.getDesktop().open(file);
							}catch(Exception ee){
								Logger.getLogger(getClass().getName()).info(ee.toString());
							}
							}
						} );
						menu.add(folderItem);
						menu.addSeparator();
				 JMenuItem addHeader = new JMenuItem("Add column");
					addHeader.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
						    addHeader();
						}
					} );
				menu.add(addHeader);
				JMenuItem removeColumn = new JMenuItem("Remove column ");
					removeColumn.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
						    removeColumn();
						}
					} );
				menu.add(removeColumn);
				
				JMenuItem setType = new JMenuItem("Set type");
				setType.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
					    setType();
					}
				} );
			menu.add(setType);
					ListSelectionModel lsm = table.getSelectionModel();
					if(!lsm.isSelectionEmpty()){
					JMenuItem excludeRows = new JMenuItem("Exclude rows ");
					excludeRows.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							Entigrator entigrator=console.getEntigrator(entihome$);
							Sack query=entigrator.getEntityAtKey(entityKey$);
							if(!query.existsElement("exclude"))
								query.createElement("exclude");
							ListSelectionModel lsm=table.getSelectionModel();
							 int minIndex = lsm.getMinSelectionIndex();
					            int maxIndex = lsm.getMaxSelectionIndex();
					            for (int i = minIndex; i <= maxIndex; i++) {
					                if (lsm.isSelectedIndex(i)) {
					                	if(debug)
					                    System.out.println("JQueryPanel:exclude rows:label="+table.getValueAt(i, 1));
					                    query.putElementItem("exclude", new Core(null,(String)table.getValueAt(i, 1),null));
					                }
					            }
					            entigrator.save(query);
					           // showHeader();
								//showContent();
					            initTable();
						}
					} );
					menu.add(excludeRows);
					if(debug)
						System.out.println("JQueryPanel:getConextMenu:add done");
					}	
					menu.addSeparator();
					JMenuItem doneItem = new JMenuItem("Done");
					doneItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							Entigrator entigrator=console.getEntigrator(entihome$);
							Sack query=entigrator.getEntityAtKey(entityKey$);
							entigrator.saveNative(query);
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
	private String[] select(){
		//Entigrator entigrator=console.getEntigrator(entihome$);
		//Sack query=entigrator.getEntity(entityKey$);
		//return select( entigrator,query);
		return select( entigrator,entity);
		
	}
	public static String[] select(Entigrator entigrator,Sack query){
		try{
			if(debug)
	 			System.out.println("JQueryPanel.select:SELECT");
			String entihome$=entigrator.getEntihome();
			String entityKey$=query.getKey();
			if(debug)
	 			System.out.println("JQueryPanel.select:query="+entityKey$);
			File queryHome=new File(entihome$+"/"+entityKey$);
			File classFile=new File(entihome$+"/"+entityKey$+"/"+entityKey$+".class");
			if(!classFile.exists())
				return null;
			if(debug)
	 			System.out.println("JQueryPanel.select:1");
			URL url = queryHome.toURI().toURL();
		    URL[] urls = new URL[]{url};
		    ClassLoader parentLoader = JMainConsole.class.getClassLoader();
		    URLClassLoader cl = new URLClassLoader(urls,parentLoader);
		  Class<?> cls = cl.loadClass(entityKey$);
		  Object obj=cls.newInstance();
		  Method method = obj.getClass().getDeclaredMethod("select",Entigrator.class);
 	      Object value=method.invoke(obj,entigrator);
 	     
 	      String[] sa=(String[])value;
 	     if(debug)
	 			System.out.println("JQueryPanel.select:sa="+sa.length);
 	      String []ea=query.elementList("exclude");
 	      if(ea==null){
 	    	 if(debug)
 	 			System.out.println("JQueryPanel.select:FINISH");
 	    	  return sa;
 	      }
 	      else{
 	    	  ArrayList<String>sl=new ArrayList<String>();
 	    	  String label$;
 	    	  for(String s:sa){
 	    		  label$=entigrator.indx_getLabel(s);
 	    		  if(query.getElementItem("exclude", label$)==null)
 	    			  sl.add(s);
 	    	  }
 	    	
          return sl.toArray(new String[0]);
 	      }
		}catch(Exception e){
			Logger.getLogger(JQueryPanel.class.getName()).severe(e.toString());
		}
		return null;
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
 * @return the procedure context.
 */
@Override
	public JContext instantiate(JMainConsole console, String locator$) {
		try{
		if(debug)
			System.out.println("JQueryPanel.instantiate:locator="+locator$);
			this.console=console;
			Properties locator=Locator.toProperties(locator$);
			entihome$=locator.getProperty(Entigrator.ENTIHOME);
			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			entigrator=console.getEntigrator(entihome$);
			entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
			if(entityLabel$==null)
				entityLabel$=entigrator.indx_getLabel(entityKey$);
			 if(Locator.LOCATOR_TRUE.equals(locator.getProperty(JFacetRenderer.ONLY_ITEM)))
				 return this;
			entity=entigrator.getEntityAtKey(entityKey$);
            entityLabel$=entity.getProperty("label");
            initItemNameFieldSelector();
            initItemTypeSelector();
            initComponentSelector();
            initTable();
           // showHeader();
		}catch(Exception e){
	        Logger.getLogger(getClass().getName()).severe(e.toString());
		}
		return this;
	}
public void instantiate(Entigrator entigrator, String locator$) {
	try{
	if(debug)
		System.out.println("JQueryPanel.instantiate:locator="+locator$);
		Properties locator=Locator.toProperties(locator$);
		this.entigrator=entigrator;
		entihome$=entigrator.getEntihome();
		entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
		entityKey$=entigrator.indx_keyAtLabel(entityLabel$);
		entity=entigrator.getEntityAtKey(entityKey$);
	}catch(Exception e){
        Logger.getLogger(getClass().getName()).severe(e.toString());
	}
	
}
/**
 * Get context title.
 * @return the context title.
 */	
@Override
	public String getTitle() {
		if(entityLabel$==null){
			try{
				Entigrator entigrator=console.getEntigrator(entihome$);
				entityLabel$=entigrator.indx_getLabel(entityKey$);
			}catch(Exception e){}
		}
		if(entityLabel$!=null)
			return entityLabel$;
		return "Query";
	}
/**
 * Get context subtitle.
 * @return the context subtitle.
 */
@Override
	public String getSubtitle() {
		return entihome$;	
	}
/**
 * Get context type.
 * @return the context type.
 */
	@Override
	public String getType() {
		return "query panel";
	}
	/**
	 * Complete the context. No action.
	 */
	@Override
	public void close() {
	}
	/**
	 * Add icon string to the locator.
	 * @param locator$ the origin locator.
	 * @return the locator.
	 */
	@Override
	public String addIconToLocator(String locator$) {
	    	return locator$;
	}
	/**
	 * Get facet handler class name.
	 * @return the facet handler class name.
	 */
	@Override
	public String getFacetHandler() {
		return QueryHandler.class.getName();
	}
	/**
	 * Get the type of the entity for the facet.
	 * @return the entity type.
	 */
	@Override
	public String getEntityType() {
		return "query";
	}
	/**
	 * Get facet icon as a Base64 string. 
	 * @return the icon string.
	 */
	@Override
	public String getCategoryIcon(Entigrator entigrator) {
		return Support.readHandlerIcon(null,JEntitiesPanel.class, "query.png");
	}
	/**
	 * Get category title for entities having the facet type.
	 * @return the category title.
	 */

	@Override
	public String getCategoryTitle() {
		return "Queries";
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
    	  QueryHandler qh=new QueryHandler();
    	  String qh$=qh.getLocator();
    	  qh$=Locator.append(qh$, Entigrator.ENTIHOME, entihome$);
    	  qh$=Locator.append(qh$, EntityHandler.ENTITY_KEY, entityKey$);
    	  qh$=Locator.append(qh$, EntityHandler.ENTITY_LABEL, entityLabel$);
          qh.instantiate(qh$);
          qh.adaptClone(entigrator);
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
	    	  QueryHandler qh=new QueryHandler();
	    	  String qh$=qh.getLocator();
	    	  qh$=Locator.append(qh$, Entigrator.ENTIHOME, entihome$);
	    	  qh$=Locator.append(qh$, EntityHandler.ENTITY_KEY, entityKey$);
	    	  qh$=Locator.append(qh$, EntityHandler.ENTITY_LABEL, entityLabel$);
	          qh.instantiate(qh$);
	          qh.adaptRename(entigrator);
	      }catch(Exception e){
	    	  Logger.getLogger(getClass().getName()).severe(e.toString());  
	      }

		
	}
	/**
	 * No action.
	 */
	@Override
	public void collectReferences(Entigrator entigrator, String entiyKey$, ArrayList<JReferenceEntry> sl) {
		// TODO Auto-generated method stub
		
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
		    	String queryHandler$=QueryHandler.class.getName();
		    	if(entity.getElementItem("fhandler", queryHandler$)!=null){
					entity.putElementItem("jfacet", new Core(null,queryHandler$,JQueryFacetOpenItem.class.getName()));
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
		    teLocator$=Locator.append(teLocator$, JTextEditor.TEXT_TITLE,"New query");
		    String text$="NewQuery"+Identity.key().substring(0, 4);
		    teLocator$=Locator.append(teLocator$, JTextEditor.TEXT,text$);
		    JQueryPanel qp=new JQueryPanel();
		    String qpLocator$=qp.getLocator();
		    qpLocator$=Locator.append(qpLocator$, Entigrator.ENTIHOME,entihome$);
		    qpLocator$=Locator.append(qpLocator$, EntityHandler.ENTITY_KEY,entityKey$);
		    qpLocator$=Locator.append(qpLocator$, BaseHandler.HANDLER_METHOD,"response");
		    qpLocator$=Locator.append(qpLocator$, JRequester.REQUESTER_ACTION,ACTION_CREATE_QUERY);
		    String requesterResponseLocator$=Locator.compressText(qpLocator$);
		    teLocator$=Locator.append(teLocator$,JRequester.REQUESTER_RESPONSE_LOCATOR,requesterResponseLocator$);
		    JConsoleHandler.execute(console, teLocator$);
		}catch(Exception ee){   
			LOGGER.severe(ee.toString());
		}
		return null;
	}

	/**
	 * Execute the response locator.
	 * @param console the main console.
	 * @param locator$ the response locator.
	 * 
	 */
	@Override
	public void response(JMainConsole console, String locator$) {
		try{
			Properties locator=Locator.toProperties(locator$);
			String action$=locator.getProperty(JRequester.REQUESTER_ACTION);
			if(ACTION_CREATE_QUERY.equals(action$)){
				String entihome$=locator.getProperty(Entigrator.ENTIHOME);
				String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
				String text$=locator.getProperty(JTextEditor.TEXT);
				Entigrator entigrator=console.getEntigrator(entihome$);  
				Sack query=entigrator.ent_new("query", text$);
				   query=entigrator.ent_assignProperty(query, "query", query.getProperty("label"));
				   query.putAttribute(new Core(null,"icon","query.png"));
				   
				   query.createElement("fhandler");
				   query.putElementItem("fhandler", new Core(null,QueryHandler.class.getName(),null));
					query.putElementItem("fhandler", new Core(null,FolderHandler.class.getName(),null));
					query.createElement("jfacet");
					query.putElementItem("jfacet", new Core(JFolderFacetAddItem.class.getName(),FolderHandler.class.getName(),JFolderFacetOpenItem.class.getName()));
					query.putElementItem("jfacet", new Core(null,QueryHandler.class.getName(),JQueryFacetOpenItem.class.getName()));
					entigrator.save(query);
					entigrator.ent_assignProperty(query, "query", text$);
					entigrator.ent_assignProperty(query, "folder", text$);
					entigrator.saveHandlerIcon(getClass(), "query.png");
				   entigrator.saveHandlerIcon(JEntitiesPanel.class, "query.png");
				   entityKey$=query.getKey();
				   File folderHome=new File(entihome$+"/"+entityKey$);
					if(!folderHome.exists())
					    folderHome.mkdir();
					createSource(entihome$,entityKey$);
					createProjectFile(entihome$,entityKey$);
					createClasspathFile(entihome$,entityKey$);
				   JQueryPanel qp=new JQueryPanel();
				   String qpLocator$=qp.getLocator();
				   qpLocator$=Locator.append(qpLocator$, Entigrator.ENTIHOME, entihome$);
				   qpLocator$=Locator.append(qpLocator$, EntityHandler.ENTITY_KEY, entityKey$);
				   JEntityPrimaryMenu.reindexEntity(console, qpLocator$);
				   Stack<String> s=console.getTrack();
				   s.pop();
				   console.setTrack(s);
				   JConsoleHandler.execute(console, qpLocator$);
				   return;
				}
			}catch(Exception e){
			LOGGER.severe(e.toString());
		}
		
	}
private void initElementSelector(){
    DefaultComboBoxModel<String> model=new DefaultComboBoxModel<String>();
	//String[]sa=select();
     itemNameComboBox.setModel(model);
     itemValueComboBox.setModel(model);
    if(queryScope==null||queryScope.size()<1){
		elementComboBox.setModel(model);
		return;
	}
	if(elementScope==null)
		elementScope=new ArrayList<String>();
	else
		elementScope.clear();
	 if(debug)
			System.out.println("JQueryPanel. initElementSelector:1");
  String component$=(String)componentComboBox.getSelectedItem();
  if(component$==null)
	  return;
	try{
		Entigrator entigrator=console.getEntigrator(entihome$);
		ArrayList <String>sl=new ArrayList<String>();
		Sack entity;
        String[] ea;		
		for(String s:queryScope){
			 if(debug)
					System.out.println("JQueryPanel. initElementSelector:s="+s);
			entity=entigrator.getEntityAtKey(s);
			if(entity==null)
				continue;
			if(!component$.equals(entity.getProperty("entity")))
				continue;
			if(!elementScope.contains(s))
				elementScope.add(s);
			ea=entity.elementsList();
			if(ea!=null)
				for (String e:ea)
				 if(!sl.contains(e))	
					sl.add(e);
		}
		Collections.sort(sl);
		for(String s:sl)
			model.addElement(s);
		elementComboBox.setModel(model);
		System.out.println("JQueryPanel. initElementSelector:element scope:"+elementScope.size());
	}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
	}
}
/*
private void initScopeMember(Entigrator entigrator,Sack member){
	try{
		String entityType$=member.getProperty("entity");
		if(entityType$==null)
			return;
		if(!entity.existsElement("entity"))
			entity.createElement("entity");
		entity.putElementItem("entity", new Core(null,entityType$,null));
		String[] ea=member.elementsList();
		Core[] ca;
		if(ea!=null){
			if(!entity.existsElement("element"))
				entity.createElement("element");
			String elementKey$;
		    for(String e:ea){
		    	elementKey$=Identity.key();
		    	entity.putElementItem("element", new Core(entityType$,elementKey$,e));
		    	ca=member.elementGet(e);
		    	if(ca!=null){
		    		}
		    	}
		    
		}
	}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
	}
}
*/
private void initComponentSelector(){
	queryScope=new ArrayList<String>(Arrays.asList(select()));
	DefaultComboBoxModel<String> model=new DefaultComboBoxModel<String>();
	
	String[]sa=select();
	if(sa==null){
		componentComboBox.setModel(model);
		return;
	}
	 if(debug)
			System.out.println("JQueryPanel. initComponentSelector:1");
  
	try{
		Entigrator entigrator=console.getEntigrator(entihome$);
		ArrayList <String>sl=new ArrayList<String>();
		Sack member;
		String entityType$;
		String memberType$;
        String[] ca;
		for(String s:sa){
			 if(debug)
					System.out.println("JQueryPanel. initComponentSelector:s="+s);
		     member=entigrator.getEntityAtKey(s);
		     if(member==null)
		    	 continue;
		     if(!queryScope.contains(s))
		    	 queryScope.add(s);
			 memberType$=entigrator.getEntityType(s);
			if(memberType$!=null)
				 if(!sl.contains(memberType$))	
					sl.add(memberType$);
			ca=entigrator.ent_listComponentsCascade(member);
			if(ca!=null)
				for(String c:ca){
					if(!queryScope.contains(c))
				    	 queryScope.add(c);
					 entityType$=entigrator.getEntityType(c);
						if(entityType$!=null)
							 if(!sl.contains(entityType$))	
								sl.add(entityType$);
				}
		}
		
		Collections.sort(sl);
		for(String s:sl)
			model.addElement(s);
		componentComboBox.setModel(model);
	    entigrator.replace(entity);
	    initElementSelector();
	}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
	}
}
private void initItemNameFieldSelector(){
    DefaultComboBoxModel<String> model=new DefaultComboBoxModel<String>();
    model.addElement("name");
    model.addElement("type");
    itemNameFieldComboBox.setModel(model);
}
private void initItemTypeSelector(){
    DefaultComboBoxModel<String> model=new DefaultComboBoxModel<String>();
    model.addElement("String");
    model.addElement("int");
    model.addElement("long");
    model.addElement("float");
    model.addElement("double");
    model.addElement("date");
    itemTypeComboBox.setModel(model);
}
private void initItemNameSelector(){
    DefaultComboBoxModel<String> model=new DefaultComboBoxModel<String>();
    try{
    String component$=(String)componentComboBox.getSelectedItem();	
    String element$=(String)elementComboBox.getSelectedItem();
    String constituent$=(String)itemNameFieldComboBox.getSelectedItem();
    Entigrator entigrator=console.getEntigrator(entihome$);
    //String[]sa=select();
    if(elementScope==null||elementScope.size()<1)
    	return;
    String[]sa=elementScope.toArray(new String[0]);
   if(debug)
	   System.out.println("initItemNameSelector:element scope="+elementScope.size());
    ArrayList <String>sl=new ArrayList<String>();
	Sack entity;
    Core[] ca;
	for(String s:sa){
		try{
		entity=entigrator.getEntityAtKey(s);
		if(entity==null)
			continue;
		if(!component$.equals(entity.getProperty("entity")))
			continue;
		ca=entity.elementGet(element$);
		 if(debug)
			   System.out.println("initItemNameSelector:element="+element$+" ca="+ca.length+" constituent="+constituent$);
		if(ca==null)
			continue;
		for(Core c:ca){
			if("type".equals(constituent$))
				if(!sl.contains(c.type))	
			sl.add(c.type);
		if("name".equals(constituent$))
			if(!sl.contains(c.name))
			    sl.add(c.name);
		}
		}catch(Exception ee){
			System.out.println("JQuerypanel:initItemNameSelector:"+ee.toString());	
		}
	}
	Collections.sort(sl);
	if(debug)
		   System.out.println("initItemNameSelector:sl="+sl.size());
	 DefaultComboBoxModel<String> mod=new DefaultComboBoxModel<String>();
	for(String s:sl)
		   mod.addElement(s);
    itemNameComboBox.setModel(mod);
    
    }catch(Exception e){
    	Logger.getLogger(getClass().getName()).severe(e.toString());
    	itemNameComboBox.setModel(model);
    }
    initType();
}
private void initItemValueSelector(){
    DefaultComboBoxModel<String> model=new DefaultComboBoxModel<String>();
    itemValueComboBox.setModel(model);
    if(debug)
		 System.out.println("JQueryPanel:initItemValueSelector:BEGIN");
    try{
    String component$=(String)componentComboBox.getSelectedItem();	
    String element$=(String)elementComboBox.getSelectedItem();
    String constituent$=(String)itemNameFieldComboBox.getSelectedItem();
    String itemName$=(String)itemNameComboBox.getSelectedItem();
    Entigrator entigrator=console.getEntigrator(entihome$);
   // String[]sa=select();
    String[]sa=elementScope.toArray(new String[0]);
    if(sa==null)
    	return;
    	
    if(debug)
		 System.out.println("JQueryPanel:initItemValueSelector:element scope="+sa.length);
    ArrayList <String>sl=new ArrayList<String>();
	Sack entity;
    Core [] ca;
	for(String s:sa){
		if(!component$.equals(entigrator.getEntityType(s)))
			continue;
		entity=entigrator.getEntityAtKey(s);
		if(entity==null)
			continue;
		if(!component$.equals(entity.getProperty("entity")))
			continue;
		ca=entity.elementGet(element$);
		if(ca==null)
			continue;
		for(Core c:ca){
			if("type".equals(constituent$))
				if(itemName$.equals(c.type))
					if(!sl.contains(c.value))
						sl.add(c.value);
			if("name".equals(constituent$))
				if(itemName$.equals(c.name))
					if(!sl.contains(c.value))
						sl.add(c.value);	
		}
	}
	Collections.sort(sl);
	for(String s:sl)
		model.addElement(s);
    itemValueComboBox.setModel(model);
    }catch(Exception e){
    	Logger.getLogger(getClass().getName()).severe(e.toString());
    	itemValueComboBox.setModel(model);
    }
}

private void initTable(){
	 try{
		 JTable originTable=getTable(entigrator, entity);
		 if(debug)
			 System.out.println("JQueryPanel:initTable:origin table rows="+originTable.getRowCount());
		 DefaultTableModel originModel=(DefaultTableModel)originTable.getModel();
		 DefaultTableModel model=new DefaultTableModel();
		 int originColumns=originModel.getColumnCount();
		 if(debug)
			 System.out.println("JQueryPanel:initTable:origin table columns="+originColumns);
		
		 String[] headers=new String[originColumns+1];
		 headers[0]="number";
		 for(int i=1;i<originColumns+1;i++)
			 headers[i]=originModel.getColumnName(i-1);
		 if(debug)
			 System.out.println("JQueryPanel:initTable:1");
	
		 model.setColumnIdentifiers(headers);
		 int originRows=originModel.getRowCount();
		 String [] row;
		 for(int i=0;i<originRows;i++){
			 row=new String[originColumns+1];
			 row[0]=String.valueOf(i);
			 //model.setValueAt(String.valueOf(i),i,0);
			 for(int j=1;j<originColumns+1;j++){
				 row[j]=(String)originModel.getValueAt(i, j-1);
			 }
			 model.addRow(row);
		 }
		 table=new JTable(model);
		 table.setAutoCreateRowSorter(true);
		 table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		 table.addMouseListener(new java.awt.event.MouseAdapter() {
	   	    @Override
	   	    public void mouseClicked(java.awt.event.MouseEvent evt) {
	   	       try{
	   	    	int row = table.rowAtPoint(evt.getPoint());
	   	        int col = table.columnAtPoint(evt.getPoint());
//	   	        System.out.println("JQueryPanel:cell click:row="+row+" column="+col);
	   	        if(col==1){
	   	        	String label$=(String)table.getValueAt(row, 1);
	   	        	//System.out.println("JQueryPanel:cell click:label="+label$);
	   	            Entigrator entigrator=console.getEntigrator(entihome$);
	   	            String entity$=entigrator.indx_keyAtLabel(label$);
	   	            JEntityFacetPanel efp=new  JEntityFacetPanel();
	   	            String efpLocator$=efp.getLocator();
	   	         efpLocator$=Locator.append(efpLocator$, Entigrator.ENTIHOME, entihome$);
	   	      efpLocator$=Locator.append(efpLocator$, EntityHandler.ENTITY_KEY, entity$);
	   	      JConsoleHandler.execute(console, efpLocator$);
	   	        }
	   	       }catch(Exception e){
	   	    	   System.out.println("JQueryPanel:initTable:mouse clicked:"+e.toString());
	   	       }
	   	        }
	   	    });
		  sorter = new TableRowSorter<>(model);
		  table.setRowSorter(sorter);
		  sorter.addRowSorterListener(new RowSorterListener() {
				    @Override
				    public void sorterChanged(RowSorterEvent evt) {
				    	//if(debug)
						//	System.out.println("JQueryPanel:show content:sorter changed="+evt.toString());
							
				    	int indexOfNoColumn = 0;
				        for (int i = 0; i < table.getRowCount(); i++) {
				            table.setValueAt(i + 1, i, indexOfNoColumn);
				        }
				    }
				});
			
			int cnt=model.getColumnCount();
		    String column$;
		    Core[]ca=entity.elementGet("header.alias");
		    for(int i=1;i<cnt;i++){
		 	   column$=model.getColumnName(i);
		 	    for(Core c:ca){
		 	    	if(debug)
		 	    		System.out.println("JQueryPanel:initTable:c type="+c.type+" value="+c.value); 
		 		   if(c.value.equals(column$)){
		 			 if("int".equals(c.type)){
		 				if(debug)
		 			  		System.out.println("JQueryPanel:initTable:set int comparator: i="+i);
		 				 sorter.setComparator(i,new JViewPanel.IntComparator());
		 			 }
		 			 if("long".equals(c.type))
		 				 sorter.setComparator(i,new JViewPanel.LongComparator());
		 			 if("float".equals(c.type))
		 				 sorter.setComparator(i,new JViewPanel.FloatComparator());
		 			 if("double".equals(c.type))
		 				 sorter.setComparator(i,new JViewPanel.DoubleComparator());
		 		   }
		 		   
		 	   }
		    }

			scrollPane.getViewport().add(table);	
	  }catch(Exception e){
	    	Logger.getLogger(getClass().getName()).severe(e.toString());
	    
	    }
}
private void addHeader(){
	 try{
		 if(debug)
			 System.out.println("JQueryPanel:addHeader:BEGIN");
		 Entigrator entigrator=console.getEntigrator(entihome$);
		 String headerKey$=Identity.key();
		 String itemName$=(String)itemNameComboBox.getSelectedItem();
		 if(!entity.existsElement("header.item"))
			 entity.createElement("header.item");
		
		 if(!entity.existsElement("header.element"))
			 entity.createElement("header.element");
		 if(!entity.existsElement("header.component"))
			 entity.createElement("header.component");
		 if(!entity.existsElement("header.alias"))
			 entity.createElement("header.alias");
		
		 Core[]ca=entity.elementGet("header.item");
		 if(ca!=null)
		 for(Core c:ca)
			 if(itemName$.equals(c.value))
				 return;
	    entity.putElementItem("header.item", new Core((String)itemNameFieldComboBox.getSelectedItem(),headerKey$,(String)itemNameComboBox.getSelectedItem()));
	    entity.putElementItem("header.alias", new Core("string",headerKey$,(String)itemNameComboBox.getSelectedItem()));
	    ca=entity.elementGet("header.element");
	    if(ca!=null)
	    entity.putElementItem("header.element", new Core(String.valueOf(ca.length),headerKey$,(String)elementComboBox.getSelectedItem()));
	    else
	    	entity.putElementItem("header.element", new Core("0",headerKey$,(String)elementComboBox.getSelectedItem()));
	    entity.putElementItem("header.component", new Core(null,headerKey$,(String)componentComboBox.getSelectedItem()));
	   // orderColumns();
		entigrator.replace(entity); 
		//if(showHeader())
		//showContent();
		initTable();
	 }catch(Exception e){
	    	Logger.getLogger(getClass().getName()).severe(e.toString());
    }
}
private class SimpleHeaderRenderer extends JLabel implements TableCellRenderer {
	private static final long serialVersionUID = 1L;
	public SimpleHeaderRenderer() {
        setFont(new Font("Consolas", Font.BOLD, 14));
        setForeground(Color.BLUE);
        setBorder(BorderFactory.createEtchedBorder());
        setHorizontalAlignment( JLabel.CENTER );
    }
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        setText(value.toString());
        return this;
    }
}
/*
private boolean showHeader(){
	try{
    Entigrator entigrator=console.getEntigrator(entihome$);
    if(debug)        
	       System.out.println("JQueryPanel:showHeader:BEGIN");
    entity=entigrator.get(entity);
    Core[]ca=entity.elementGet("header.element");
    if(ca==null){
    	if(debug)        
 	       System.out.println("JQueryPanel:showHeader:header.element is empty");
    	return false;
    }
    ca=Core.sortAtIntType(ca);
    
    ArrayList<String>sl=new ArrayList<String>();
    int i=0;
    sl.add("num");
    String header$;
    for(Core c:ca){
    	header$=entity.getElementItemAt("header.alias", c.name);
    	if(header$!=null)
    		sl.add(header$);
    	
    	}
    if(debug)        
	       System.out.println("JQueryPanel:showHeader:sl="+sl.size());
  DefaultTableModel model=new DefaultTableModel(
			  null
			  ,
				sl.toArray(new String[0])
			);
  	  table.setModel(model);
	  table.getTableHeader().setDefaultRenderer(new SimpleHeaderRenderer());
	  table.getTableHeader().addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		    	try{
		    	int col = table.columnAtPoint(e.getPoint());
		    	String col$=String.valueOf(col-1);
		    	if(col==0)
		    		col$="";
		    	
		    	String itemName$ = table.getColumnName(col);
	      if(debug)        
		       System.out.println("Column index=" + col+" name="+itemName$);
		        String element$=null;
		        Entigrator entigrator=console.getEntigrator(entihome$);
		        Sack query=entigrator.getEntityAtKey(entityKey$);
		        Core[] ca=query.elementGet("header.element");
		        String headerKey$=null;
		//      System.out.println("JQueryPanel:header listener:item name="+itemName$);
		        for(Core c:ca)
		        	
		        		if(col$.equals(c.type)){
		        			element$=c.value;
		        			 headerKey$=c.name;
		        			break;
		        		}
		        if(debug)        
				       System.out.println("Column index=" + col+" name="+itemName$+" header="+headerKey$);
		       if(headerKey$==null)
		    	   return;
		    	   
		       if(debug)        
			       System.out.println("JQueryPanel:showHeader:1");		     
		        Core item=query.getElementItem("header.item", headerKey$);		
		        String component$=query.getElementItemAt("header.component", headerKey$);	
		 //       System.out.println("JQueryPanel:header listener:element=" +element$ + " item=" + itemName$+" field="+field$);
		      
		        if(componentComboBox.getItemCount()<1)
		        	initComponentSelector();
		        
		        setSelection(componentComboBox,component$);
		        setSelection(elementComboBox,element$);   //
			  
		        setSelection(itemNameComboBox,item.value);
		        
		        setSelection(itemNameFieldComboBox,item.type);
		      
		    	}catch(Exception ee){
		    		Logger.getLogger(JQueryPanel.class.getName()).severe(ee.toString());
		    	}
		    }
		});
	  if(debug)        
	       System.out.println("JQueryPanel:showHeader:return true");	
	  return true;
	}catch(Exception e ){
		LOGGER.severe(e.toString());
	}
	return false;
}
*/
private static String getColumnValue(Entigrator entigrator, Sack entity,String element$,String itemType$, String itemValue$ ){
	try{
		if(debug)
			  System.out.println("JQuerypanel:getColumnValue:element="+element$+" item type="+itemType$+" value="+itemValue$);
		if("name".equals(itemType$)){
			 return entity.getElementItemAt(element$,itemValue$);
		 }else{
		Core[]	va=entity.elementGet(element$);
			if(va!=null)
			{
		//	System.out.println("JQuerypanel:getRow:va="+va.length);
			for(Core v:va){
			//	System.out.println("JQuerypanel:getRow:v.type="+v.type+" c.value="+c.value);
				if(itemValue$.equals(v.type)){
		           return v.value;
				}
			}
		  }
		 } 
		
	}catch(Exception e){
		Logger.getLogger(JQueryPanel.class.getName()).severe(e.toString());
	}
	return null;
}
private String[] getRow(Entigrator entigrator,ArrayList<String>rowScope,int num){
	try{
		Core[] ca=entity.elementGet("header.element");
		ca=Core.sortAtIntType(ca);
		if(ca==null)		return null;
		ArrayList<String>sl=new ArrayList<String>();
		String value$;
		sl.add(String.valueOf(num));
		boolean empty=true;
		for(Core c:ca){
			value$=getColumnValue(entigrator, c.name, rowScope);
			if(value$==null)
				value$="";
			else
				empty=false;
			sl.add(value$);
		}
	if(empty)
		return null;
	return sl.toArray(new String[0]);	
	}catch(Exception e){
		Logger.getLogger(JQueryPanel.class.getName()).severe(e.toString());
	}
	return null;
}
private String getColumnValue(Entigrator entigrator,String headerKey$, ArrayList<String>rowScope){
	try{
		String element$=entity.getElementItemAt("header.element", headerKey$);
		String component$=entity.getElementItemAt("header.component", headerKey$);
		Core item=entity.getElementItem("header.item", headerKey$);
		String constituent$=item.type;
		String item$=item.value;
		Sack member;
		for(String r:rowScope){
			member=entigrator.getEntityAtKey(r);
			if(member==null)
				continue;
			if(!component$.equals(member.getProperty("entity")))
				continue;
			if("name".equals(constituent$))
				return member.getElementItemAt(element$, item$);
			else{
				Core []ca=member.elementGet(element$);
				if(ca!=null)
					for(Core c:ca)
						if(item$.equals(c.type))
							return c.value;
			}
		}
	}catch(Exception e ){
		Logger.getLogger(JQueryPanel.class.getName()).severe(e.toString());
	}
		return null;
}
private static void addRow(Entigrator entigrator,String[]row,Sack query,Sack member,DefaultTableModel model){
	 try{
		 
		 String entityType$=member.getProperty("entity");
			Core[] ca=query.elementGet("header.component");
			ArrayList <Core> ikl=new ArrayList<Core>();
			for(Core c:ca)
				if(entityType$.equals(c.value))
					ikl.add(c);
			Core[] ika=ikl.toArray(new Core[0]);
			ca=query.elementGet("header.element");
			ca=Core.sortAtIntType(ca);
			ikl.clear();
			int index;
			String element$;
			String name$;
			String value$=null;
			String component$;
			String field$;
			String[] newRow=new String[row.length];
				 for(int i=0;i<row.length;i++)
					 newRow[i]=row[i];
			Core item;	
			Core []va;
			for(int i=0;i<ca.length;i++){
				value$=null;
				component$=query.getElementItemAt("header.component", ca[i].name);
				if(!component$.equals(member.getProperty("entity")))
					continue;
				element$=ca[i].value;
				item=query.getElementItem("header.item", ca[i].name);
				name$=item.value;
				field$=item.type;
				index=i;
				va=member.elementGet(element$);
				if(va!=null)
					for(Core v:va){
						if("type".equals(field$))
							if(name$.equals(v.type))
								value$=v.value;
						if("name".equals(field$))
							if(name$.equals(v.name))
								value$=v.value;		
								
					}
						
				//for(Core ik:ika)
				//if(ik.name.equals(ca[i].name)){
					//index=Integer.parseInt(c.type);
					
					
					//value$=member.getElementItemAt(element$, name$);
					if(debug)
						System.out.println("JQueryPanel:addRow:member="+member.getProperty("label")+" element="+element$+" field="+name$+" value="+value$+" index="+index);
					newRow[index]=value$;
				//}
			}
		 String[] sa=entigrator.ent_listComponents(member);
		if(sa!=null){
			Sack newMember;
			for(String s:sa){
				newMember=entigrator.getEntityAtKey(s);
				if(newMember!=null){
					addRow(entigrator,newRow,query,newMember,model);
				}
			}
		}else{
		    model.addRow(newRow);
		    if(debug){
		    	for(int i=0;i<newRow.length;i++)
				   System.out.print("["+i+"]="+newRow[i]+" ");
		       System.out.println();
		    }
			}
	 }catch(Exception e){
		 Logger.getLogger(JQueryPanel.class.getName()).severe(e.toString());
	 }
}
private static JTable getTable(Entigrator entigrator,Sack query){
	try{
	    String[] sa=select(entigrator,query);
       DefaultTableModel model=new DefaultTableModel();
       JTable table=new JTable(model);
       TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
   	   table.setRowSorter(sorter);
       Core[] hea=query.elementGet("header.element");
       hea=Core.sortAtIntType(hea);
       ArrayList <String>headers=new ArrayList<String>();
       Core alias;
       String columnType$;
       for(int i=0;i<hea.length;i++)
             headers.add(query.getElementItem("header.alias", hea[i].name).value);
       if(debug){
    	   System.out.println("JQueryPanel:getTable: headers="+headers.size());
       }
        model.setColumnIdentifiers(headers.toArray(new String[0]) );
        for(int i=0;i<hea.length;i++){
            alias=query.getElementItem("header.alias", hea[i].name);
            columnType$=alias.type;
			//System.out.println("JQueryPanel:setSorter: column name="+model.getColumnName(i));
			if("int".equals(columnType$))
 				 sorter.setComparator(i,new JViewPanel.IntComparator());
 			 if("long".equals(columnType$))
 				 sorter.setComparator(i,new JViewPanel.LongComparator());
 			 if("float".equals(columnType$))
 				 sorter.setComparator(i,new JViewPanel.FloatComparator());
 			 if("double".equals(columnType$))
 				 sorter.setComparator(i,new JViewPanel.DoubleComparator());
      }
       int columnCount=model.getColumnCount();
       Sack member;
       String[]	row=new String[columnCount];
       //
       if(debug){
    	   System.out.println("JQueryPanel:getTable: row="+row.length+" model="+model.getColumnCount());
       }
       //
       for(String s:sa){
          member=entigrator.getEntityAtKey(s);
    	 if(member==null)
    		 continue;
    	 addRow(entigrator, row, query, member, model); 
       }
       return table;
   	}catch(Exception e){
		Logger.getLogger(JQueryPanel.class.getName()).severe(e.toString());
	}
	return null;
}
/*
private void showContent(){
	try{
	    String[] sa=select();
        Sack rowMember;
     ArrayList <String>rowScope=new ArrayList<String>();
     ArrayList <String>ensemble=new ArrayList<String>();
     String[] ma;
     String[] ta;
     String[] row;
     int num=0;
     ArrayList<String[]>rows=new ArrayList<String[]>();
     DefaultTableModel model=(DefaultTableModel)table.getModel();
     Core[] ca=entity.elementGet("header.component");
     ArrayList<String> components=new ArrayList<String>();
//     System.out.println("JQueryPanel:showContent:1");
     for(Core c:ca)
    	 if(!components.contains(c.value))
    		 components.add(c.value);
     for(String s:sa){
    	
    	 rowScope.clear();
    	 ensemble.clear();
    	 rowMember=entigrator.getEntityAtKey(s);
    	 if(rowMember==null)
    		 continue;
      	 ma=entigrator.ent_getEnsemble(rowMember);
    	 String component$;
      	 if(ma!=null)
    		for(String m:ma){
    		 component$=entigrator.getEntityType(m);
    		 if(components.contains(component$))
    		   if(!ensemble.contains(m))	
    		       ensemble.add(m);
    		}
    	 row=getRow(entigrator, ensemble, num);
    	 if(row!=null)
    	 if(!containsRow(rows,row)){
    		 rows.add(row);
    		 num++;
    	 }
     }
     if(rows.size()>0)
		 for(String[] r:rows)
			 model.addRow(r);
     
      sorter = new TableRowSorter<>(model);
	table.setRowSorter(sorter);
	sorter.addRowSorterListener(new RowSorterListener() {
		    @Override
		    public void sorterChanged(RowSorterEvent evt) {
		    	if(debug)
					System.out.println("JQueryPanel:show content:sorter changed="+evt.toString());
					
		    	int indexOfNoColumn = 0;
		        for (int i = 0; i < table.getRowCount(); i++) {
		            table.setValueAt(i + 1, i, indexOfNoColumn);
		        }
		    }
		});
	int cnt=model.getColumnCount();
    String column$;
    ca=entity.elementGet("header.alias");
    for(int i=1;i<cnt;i++){
    	
 	   column$=model.getColumnName(i);
 	    for(Core c:ca){
 	//	  System.out.println("JQueryPanel:showContent:c type="+c.type+" value="+c.value); 
 		   if(c.value.equals(column$)){
 			 if("int".equals(c.type)){
 				if(debug)
 			  		System.out.println("JQueryPanel:showContent:set int comparator: i="+i);
 				 sorter.setComparator(i,new JViewPanel.IntComparator());
 			 }
 			 if("long".equals(c.type))
 				 sorter.setComparator(i,new JViewPanel.LongComparator());
 			 if("float".equals(c.type))
 				 sorter.setComparator(i,new JViewPanel.FloatComparator());
 			 if("double".equals(c.type))
 				 sorter.setComparator(i,new JViewPanel.DoubleComparator());
 		   }
 		   
 	   }
    }

   	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
}
*/
private void clearHeader(){
	int response = JOptionPane.showConfirmDialog(this, "Clear header ?", "Confirm",
	        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
   if (response == JOptionPane.YES_OPTION) {
        try{
        	//DefaultTableModel model=new DefaultTableModel();
        	 DefaultTableModel model=new DefaultTableModel(
       			  null
       			  ,
       				new String[]{"num"}
       			);
         	  table.setModel(model);
        	
        	
        	Entigrator entigrator=console.getEntigrator(entihome$);
        	Sack query=entigrator.getEntityAtKey(entityKey$);
        	query.removeElement("header.element");
        	query.removeElement("header.component");
        	//query.removeElement("header.container");
        	query.removeElement("header.item");
        	query.removeElement("header.alias");
        	query.removeElement("exclude");
        	entigrator.replace(query);
	    } catch(Exception e){
	    	LOGGER.severe(e.toString());
	    }
}
}
private void removeColumn(){
	String itemName$=(String)itemNameComboBox.getSelectedItem();
	int response = JOptionPane.showConfirmDialog(this, "Remove column '"+itemName$+"' ?", "Confirm",
	        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
   if (response == JOptionPane.YES_OPTION) {
        try{
        	Entigrator entigrator=console.getEntigrator(entihome$);
        	entity=entigrator.getEntityAtKey(entityKey$);
        	
           	Core[] ca=entity.elementGet("header.item");
        	for(Core c:ca){
        		if(itemName$.equals(c.value)){
        			entity.removeElementItem("header.element", c.name);
                	entity.removeElementItem("header.item",c.name);	
                	entity.removeElementItem("header.component", c.name);
                	entity.removeElementItem("header.alias", c.name);
        		}
        	}
        	entigrator.replace(entity);
        	//showHeader();
        	initTable();
	    } catch(Exception e){
	    	LOGGER.severe(e.toString());
	    }
}
}
private void setType(){
	try{
	        String itemName$=(String)itemNameComboBox.getSelectedItem();
	    	Entigrator entigrator=console.getEntigrator(entihome$);
        	entity=entigrator.getEntityAtKey(entityKey$);
        	String itemKey$=entity.getElementItemAtValue("header.item", itemName$);
        	String itemType$=(String)itemTypeComboBox.getSelectedItem();
        	Core alias=entity.getElementItem("header.alias", itemKey$);
        	alias.type=itemType$;
        	entity.putElementItem("header.alias",alias);
        	entigrator.replace(entity);
        	
	    } catch(Exception e){
	    	LOGGER.severe(e.toString());
	    }
}
private void initType(){
	try{
	        String itemName$=(String)itemNameComboBox.getSelectedItem();
	    if(debug)
	    	System.out.println("JQueryPanel.initType:item name="+itemName$);
	        Entigrator entigrator=console.getEntigrator(entihome$);
        	entity=entigrator.getEntityAtKey(entityKey$);
        	String itemKey$=entity.getElementItemAtValue("header.item", itemName$);
        	//String itemType$=(String)itemTypeComboBox.getSelectedItem();
        	Core alias=entity.getElementItem("header.alias", itemKey$);
        	 if(debug)
     	    	System.out.println("JQueryPanel.initType:item type="+alias.type);
        	int cnt=itemTypeComboBox.getModel().getSize();
        	for(int i=0;i<cnt;i++)
        		if(alias.type.equals(itemTypeComboBox.getItemAt(i)))
        	      itemTypeComboBox.setSelectedIndex(i);
	    } catch(Exception e){
	    	LOGGER.severe(e.toString());
	    }
}
/*
private void setSelection(JComboBox <String>comboBox,String item$){
	 ComboBoxModel<String> model=comboBox.getModel();
	 if (model != null) {
        int cnt = model.getSize();
        String[] sa = null;
        if (cnt > 0) {
            sa = new String[cnt];
            for (int i = 0; i < cnt; i++)
                if (item$.equals(model.getElementAt(i))) {
                     comboBox.setSelectedIndex(i);
                    return;
                }
        }
    }
}
*/
static class ItemComparator implements Comparator<String>{
    @Override
    public int compare(String l1$, String l2$) {
    	try{
  //  		System.out.println("ItemComparator:compare:l1="+l1$+" l2="+l2$);
    	if(l1$.equals("number"))
    		l1$="$$$$a";
    	if(l2$.equals("number"))
    		l2$="$$$$a";
    	int ret=l1$.compareToIgnoreCase(l2$);
    		return 	ret;
    	}catch(Exception e){
    		System.out.println("ItemComparator:compare:"+e.toString());
    		return 0;
    		
    	}
    }
}

private static void createClasspathFile(String entihome$,String queryKey$){
	try{
	//	System.out.println("JProcedurePanel:createClasspathFile.procedure key="+procedureKey$);
		File classpath=new File(entihome$+"/"+queryKey$+"/.classpath");
		if(!classpath.exists())
			classpath.createNewFile();
		 FileOutputStream fos = new FileOutputStream(classpath, false);
		 Writer writer = new OutputStreamWriter(fos, "UTF-8");
	     writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
	     writer.write("<classpath>\n");
	     writer.write("<classpathentry kind=\"src\" path=\"\"/>\n");
	     writer.write("<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER\"/>\n");
	     writer.write("<classpathentry combineaccessrules=\"false\" kind=\"src\" path=\"/JEntigrator\"/>\n");
	     writer.write("<classpathentry kind=\"output\" path=\"\"/>\n");
	      writer.write("</classpath>\n");
		 writer.close();   
	}catch(Exception e){
		Logger.getLogger(JQueryPanel.class.getName()).severe(e.toString());
	}
}

private static void createProjectFile(String entihome$,String procedureKey$){
	try{
	//	System.out.println("JProcedurePanel:createProjectFile.procedure key="+procedureKey$);
		File project=new File(entihome$+"/"+procedureKey$+"/.project");
		if(!project.exists())
			project.createNewFile();
		 FileOutputStream fos = new FileOutputStream(project, false);
		 Writer writer = new OutputStreamWriter(fos, "UTF-8");
	     writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
	     writer.write("<projectDescription>\n");
	     writer.write("<name>"+procedureKey$+"</name>\n");
	     writer.write("<comment></comment>\n");
	     writer.write("<projects></projects>\n");
	     writer.write("<buildSpec>\n");
	     writer.write("<buildCommand>\n");
	     writer.write("<name>org.eclipse.jdt.core.javabuilder</name>\n");
	     writer.write("<arguments></arguments>\n");
	     writer.write("</buildCommand>\n");
	     writer.write("</buildSpec>\n");
	     writer.write("<natures>\n");
	     writer.write("<nature>org.eclipse.jdt.core.javanature</nature>\n");
   		 writer.write("</natures>\n");
		 writer.write("</projectDescription>\n");
		 writer.close();   
	}catch(Exception e){
		Logger.getLogger(JQueryPanel.class.getName()).severe(e.toString());
	}
}
private static void createSource(String entihome$,String queryKey$){

	try{
		System.out.println("JProcedurePanel:createQuery:query key="+queryKey$);
		File queryJava=new File(entihome$+"/"+queryKey$+"/"+queryKey$+".java");
		if(!queryJava.exists())
			queryJava.createNewFile();
		
		 FileOutputStream fos = new FileOutputStream(queryJava, false);
		 Writer writer = new OutputStreamWriter(fos, "UTF-8");
		 writer.write("import java.util.logging.Logger;\n");
		 writer.write("import gdt.data.store.Entigrator;\n");
		 writer.write("import gdt.jgui.console.JMainConsole;\n");
		 writer.write("import gdt.jgui.entity.query.Query;\n");
		 
		 writer.write("public class "+queryKey$+"  implements Query {\n");
		 writer.write("private final static String ENTITY_KEY=\""+queryKey$+"\";\n");
		 writer.write("@Override\n");
		 writer.write("public String[] select(Entigrator entigrator){\n");
		 writer.write("try{\n");
		 writer.write("//Do NOT change this section of the code\n"); 
		 writer.write("String [] sa;\n");
		 writer.write("// Put query code here\n");
		 writer.write("sa=entigrator.indx_listEntitiesAtPropertyName(\"entity\");\n");
		 writer.write("//\n");
	     writer.write("//Do NOT change this section of the code\n"); 
	     writer.write("return sa;\n");
	     writer.write("}catch(Exception e){\n");
		 writer.write("Logger.getLogger(getClass().getName());\n");
		 writer.write("return null;\n");
		 writer.write("}\n");
		 writer.write("}\n");
		 writer.write("}\n");
		 writer.close();   
		
	}catch(Exception e){
		Logger.getLogger(JQueryPanel.class.getName()).severe(e.toString());
	}
}


private void reset(){
	try{
	File folder=new File(entihome$+"/"+entityKey$);
	if(!folder.exists()){
		folder.mkdir();
	}
	createClasspathFile(entihome$,entityKey$);
	createProjectFile(entihome$,entityKey$);
	createSource(entihome$,entityKey$);
	
	}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
}
}
@Override
public void activate() {
	// TODO Auto-generated method stub
	
}
public  static String getWebItems(Entigrator entigrator,String locator$){
	try{
		//	System.out.println("IndexPanel.instantiate:locator="+locator$);
			
			Properties locator=Locator.toProperties(locator$);
			String entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
			String sortColumnName$=locator.getProperty(JViewPanel.SORT_COLUMN_NAME);
			String entityKey$=entigrator.indx_keyAtLabel(entityLabel$);
		    Sack  query=entigrator.getEntityAtKey(entityKey$);
			 StringBuffer sb=new StringBuffer();
			 sb.append("<table style=\"text-align: left;  background-color: transparent;\"  border=\"1\" cellpadding=\"2\" cellspacing=\"2\">");
			 sb.append(getWebHeader(query));
			 sb.append(getWebItems(entigrator,query,sortColumnName$));
	         sb.append("</table>"); 
			return sb.toString(); 
		}catch(Exception e){
	        Logger.getLogger(JQueryPanel.class.getName()).severe(e.toString());
		}
		return null;
}
private static String getWebHeader(Sack query){
	try{
		Core[] ca=query.elementGet("header.element");
		ArrayList<String>sl=new ArrayList<String>();
		StringBuffer sb=new StringBuffer();
		sb.append("<tr>");
		sb.append("<td><strong>number</strong></td></td>");
		ca=Core.sortAtIntType(ca);
		String value$;
		//
//		for(Core c:ca)
//			System.out.println("JQueryPanel:getWebHeader:c type="+c.type+" name="+c.name+" value="+c.value);
		//
		for(Core c:ca){
			value$=query.getElementItemAt("header.item", c.name);
				if(!"number".equals(value$))
				  sb.append("<td  onclick=\"headerClick('"+value$+"')\" style=\"text-decoration:underline;\"><strong>"+value$+"</strong></td>");
	
			
		}
	for(String s:sl){
			sb.append("<td><strong>"+s+"</strong></td>");
		}
		sb.append("</tr>");
		return sb.toString();
	}catch(Exception e){
		 Logger.getLogger(JQueryPanel.class.getName()).severe(e.toString());
	}
	return null;
}
public  static String getWebItems(Entigrator entigrator,Sack query,String sortColumnName$){
	try{
		Sack id2key=entigrator.getEntityAtKey(entigrator.indx_keyAtLabel("id2key"));
		JTable table=getTable(entigrator, query);
		int columns= table.getColumnCount();
		  int sortColumnIndex=-1;
		  if(sortColumnName$!=null){
			  for(int j=0;j<columns;j++)
				  if(sortColumnName$.equals(table.getColumnName(j))){
					  sortColumnIndex=j;
					  break;
				  }
			  List <RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
			  sortKeys.add(new RowSorter.SortKey(sortColumnIndex, SortOrder.ASCENDING));
			  TableRowSorter sorter=(TableRowSorter)table.getRowSorter();
			  sorter.setSortKeys(sortKeys);
			  sorter.sort();
		  }
		  StringBuffer sb=new StringBuffer();
			//sb.append("<tr>");
			//sb.append("<td><strong>number</strong></td></td>");
		  int rows= table.getRowCount();
		  String columnName$;
		  String valueId$;
		  String valueKey$=null;
		  String valueLabel$;
		  for(int i=0;i<rows;i++){
			  sb.append("<tr>");
			  sb.append("<td>"+String.valueOf(i)+"</td>");
			  for(int j=0;j<columns;j++){
				  columnName$=table.getColumnName(j);
				  valueId$=(String)table.getValueAt(i,j);
				  valueLabel$=null;
				  valueKey$=null;
				  try{
				  valueKey$=id2key.getElementItemAt(columnName$, valueId$);
				  }catch(Exception ee){}
				  if(valueKey$==null)
				     sb.append("<td>"+valueId$+"</td>");
				  else{
					  if(debug)
						  System.out.println("JQueryPanel:getWebItems: column name="+columnName$+" id="+valueId$+" key="+valueKey$+" label="+valueLabel$);
					  sb.append("<td  onclick=\"keyClick('"+valueKey$+"')\" style=\"text-decoration:underline;\"><strong>"+valueId$+"</strong></td>");
				  }
			  }
			  sb.append("</tr>");
		  }
		  return sb.toString();
			}catch(Exception e){
				 Logger.getLogger(JViewPanel.class.getName()).severe(e.toString());
			}
			
			return null;
			
}
public static class SortIgnoreCase implements Comparator<Object> {
    public int compare(Object o1, Object o2) {
        String s1 = (String) o1;
        String s2 = (String) o2;
        return s1.toLowerCase().compareTo(s2.toLowerCase());
    }
}

@Override
public String getFacetOpenItem() {
	// TODO Auto-generated method stub
	return JQueryFacetOpenItem.class.getName();
}
@Override
public String getFacetIcon() {
	// TODO Auto-generated method stub
	return "query.png";
}
 private boolean containsRow(ArrayList <String[]> scope,String[] row){
	 if(scope.size()<1)
		 return false;
	 RowComparator rowComparator=new RowComparator(); 
	 for(String[] r:scope){
		  if(rowComparator.compare(r, row)==0){
			
			  return true;
		  }
	 }
	 return false;
 }
 class RowComparator implements Comparator<String[]> {
	 
    @Override
    public int compare(String[] row1, String[]row2) {
        int cnt=row1.length-row2.length;
        if(cnt!=0)
        	return -1;
        for(int i=1;i<row1.length;i++)
        	if(!row1[i].equals(row2[i]))
        		return -1;
       // System.out.println("r1="+row1+" r2="+row2);
        return 0;
    }
}
 class IntComparator implements Comparator<String> {
	    @Override
	    public int compare(String a, String b) {
	        try{
	    	Integer a1=new Integer(a);
	    	Integer a2=new Integer(b);
	        return a1.compareTo(a2);
	        }catch(Exception e){
	        	return 0;
	        }
	    }
	}
 class LongComparator implements Comparator<String> {
	    @Override
	    public int compare(String a, String b) {
	        try{
	    	Long a1=new Long(a);
	    	Long a2=new Long(b);
	        return a1.compareTo(a2);
	        }catch(Exception e){
	        	return 0;
	        }
	    }
	}
 class FloatComparator implements Comparator<String> {
	    @Override
	    public int compare(String a, String b) {
	        try{
	    	Float a1=new Float(a);
	    	Float a2=new Float(b);
	        return a1.compareTo(a2);
	        }catch(Exception e){
	        	return 0;
	        }
	    }
	}
 class DoubleComparator implements Comparator<String> {
	    @Override
	    public int compare(String a, String b) {
	        try{
	    	Double a1=new Double(a);
	    	Double a2=new Double(b);
	        return a1.compareTo(a2);
	        }catch(Exception e){
	        	return 0;
	        }
	    }
	}
 private void selectRow(String itemTitle$,String itemValue$){
	 try{
		 int colCnt=table.getColumnCount();
		 int rowCnt=table.getRowCount();
		 for(int i=0;i<colCnt;i++){
			 if (itemTitle$.equals(table.getColumnName(i)))
			   for(int j=0;j<rowCnt;j++){
				    if(itemValue$.equals(table.getValueAt(j, i)))
				    	table.setRowSelectionInterval(j, j);
			 }
		 }
			 
		 
		 
	 }catch(Exception e){
		 Logger.getLogger(getClass().getName()).severe(e.toString());
	 }
 }
}
