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
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Stack;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
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
import gdt.jgui.entity.procedure.JProcedureFacetOpenItem;
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
	private TableRowSorter<TableModel> sorter;
	protected String entihome$;
	protected String entityKey$;
	protected String entityLabel$;
	protected Sack entity=null;
	protected JMainConsole console;
	protected Entigrator entigrator;
	private JMenu menu;
	static boolean debug=false;
	protected ArrayList <String>queryScope;
	protected ArrayList <String>elementScope;
	private static int ROW_EQUEL=0;
	private static int ROW_NOT_EQUEL=-1;
	private static int ROW1_INCLUDES_ROW2=1;
	private static int ROW2_INCLUDES_ROW1=2;
	private static int UNKNOWN=3;
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
		scrollPane.setMinimumSize( scrollPane.getPreferredSize() );

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
							}
						} );
						menu.add(selectItem);
						JMenuItem clearHeader = new JMenuItem("Clear all");
						clearHeader.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
							    clearHeader();
							    initTable();
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
						   // addHeader();
							addColumn();
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
					menu.addSeparator();
					JMenuItem doneItem = new JMenuItem("Done");
					doneItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							Entigrator entigrator=console.getEntigrator(entihome$);
							Sack query=entigrator.getEntityAtKey(entityKey$);
							if(query!=null)
							  entigrator.ent_alter(query);
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
		return select( entigrator,entity);
		
	}
	
	/**
	 * Select entities.
	 * @param entigrator the entigrator.
	 * @param query the entity of the 'query' type.
	 * @return the list of keys of selected entities.
	 */
	public static String[] select(Entigrator entigrator,Sack query){
		try{
			String entihome$=entigrator.getEntihome();
			String entityKey$=query.getKey();
 			//System.out.println("JQueryPanel.select:query="+entityKey$);
			File queryHome=new File(entihome$+"/"+entityKey$);
			File classFile=new File(entihome$+"/"+entityKey$+"/"+entityKey$+".class");
			if(!classFile.exists())
				return null;
			
			URL url = queryHome.toURI().toURL();
		    URL[] urls = new URL[]{url};
		    ClassLoader parentLoader = JMainConsole.class.getClassLoader();
		    URLClassLoader cl = new URLClassLoader(urls,parentLoader);
		  Class<?> cls = cl.loadClass(entityKey$);
		  Object obj=cls.newInstance();
		  Method method = obj.getClass().getDeclaredMethod("select",Entigrator.class);
		  //System.out.println("JQueryPanel.select:1");
		  Object value=method.invoke(obj,entigrator);
		  //System.out.println("JQueryPanel.select:2");
 	      return (String[])value;
 	     
		}catch(Exception e){
			Logger.getLogger(JQueryPanel.class.getName()).severe(e.toString());
		}
		return null;
	}
/**
 * Select entities.
	 * @param entigrator the entigrator.
	 * @param query the entity of the 'query' type.
	 * @param sortColumnName$ the column to sort.
	 * @return the list of keys of selected entities.
	  
 */
	public static String[] select(Entigrator entigrator,Sack query,String sortColumnName$){
		//System.out.println("JQueryPanel:select:sort column name="+sortColumnName$);
		String[] sa=select(entigrator,query);
		try{
		
		if(sortColumnName$==null)
			return sa;
		Core[] ca=query.elementGet("header.alias");
		String type$=null;
		String element$=null;
		String field$=null;
    	  for(Core c:ca)
			  if(sortColumnName$.equals(c.value)){
				  type$=c.type;
				  element$=query.getElementItemAt("header.element", c.name);
				  field$=query.getElementItem("header.item", c.name).type;
				  break;
			  }
    	 if(debug) 
    	  System.out.println("JQueryPanel:select:type="+type$+" element="+element$+" field="+field$);
		Sack member;
		Hashtable<String,ArrayList<String>> tab=new Hashtable<String,ArrayList<String>>();
		String sortValue$;
		ArrayList<String>vl=new ArrayList<String>();
		ArrayList<String>rl=new ArrayList<String>();
		ArrayList<String>gl;
		 
		for(String s:sa){
			sortValue$=null;
			member=entigrator.getEntityAtKey(s);
			if(member==null)
				continue;
			if("name".equals(field$))
			sortValue$=member.getElementItemAt(element$, sortColumnName$);
			if("type".equals(field$)){
		       	ca=member.elementGet(element$);
		       	if(ca!=null)
		       		for(Core c:ca)
		       			if(sortColumnName$.equals(c.type)){
		       				sortValue$=c.value;
		       				break;
		       			}
			}
			if(debug) 
		    	  System.out.println("JQueryPanel:sort value="+sortValue$);
			if(sortValue$!=null){
			 gl=tab.get(sortValue$);
			 if(gl==null)
				 gl=new ArrayList<String>();
			 if(!gl.contains(s)){
			 gl.add(s);
			 tab.put(sortValue$,gl);
			 vl.add(sortValue$);
			 }
			}
			else
			 rl.add(s);	
			entigrator.clearCache();
		}
		if(vl.size()>0){
		Comparator comparator=getComparator(type$);
		if(comparator==null){
       	  Collections.sort(vl);
		}
		else{
			//System.out.println("JQueryPanel:select:sort at comparator");
			Collections.sort(vl,comparator);
		}
		}
        ArrayList<String>sl=new ArrayList<String>();		
		for(String v:vl){
			//System.out.println("JQueryPanel:select:v="+v);
			gl=tab.get(v);
			if(gl!=null)
				for(String s:gl)
					if(!sl.contains(s))
					  sl.add(s);
		}
		for(String r:rl )
			if(!sl.contains(r))
			sl.add(r);
        return sl.toArray(new String[0]);
		}catch(Exception e){
			Logger.getLogger(JQueryPanel.class.getName()).severe(e.toString());
		}
		return sa;
	}
	/**
	 * Get comparator.
		 * @param type$ the data type of the column
		 * 
		 * @return the comparator.
		  
	 */
	public static Comparator<?> getComparator(String type$){
		  
	 			 if("int".equals(type$)){
	 				return new JViewPanel.IntComparator();
	 			 }
	 			 if("long".equals(type$))
	 				 return new JViewPanel.LongComparator();
	 			 if("float".equals(type$))
	 				return new JViewPanel.FloatComparator();
	 			 if("double".equals(type$))
	 				 return new JViewPanel.DoubleComparator();
	 			if("date".equals(type$))
	 				 return new JViewPanel.DateComparator();
	 			return  new JViewPanel.StringComparator();
	 		
	}
/**
 * Get the context locator.
 * @return the context locator string.
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
 * @return the query context.
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
/**
 * Instantiate the JQueryPanel.
 * @param entigrator the entigrator.
 * @param locator$ the locator string.
 */
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
		    	if(entity.getElementItem("fhandler", queryHandler$)==null)
					return;
			     if(entity.getElementItem("jfacet", queryHandler$)==null){
					entity.putElementItem("jfacet", new Core(null,queryHandler$,JQueryFacetOpenItem.class.getName()));
					entigrator.ent_alter(entity);
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
					entigrator.ent_alter(query);
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
		//System.out.println("JQueryPanel. initElementSelector:element scope:"+elementScope.size());
	}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
	}
}

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
		//model.addElement("any");
		Collections.sort(sl);
		for(String s:sl)
			model.addElement(s);
		componentComboBox.setModel(model);
	    entigrator.ent_alter(entity);
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
	//table=getTable(entigrator ,entity);
	//scrollPane.getViewport().add(table);
	 
	try{
		JTable  originTable=buildTable(entigrator, entity);
		
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
		// System.out.println("JQueryPanel:initTable:origin rows="+originRows);
		 String [] row;
		 for(int i=0;i<originRows;i++){
			 row=new String[originColumns+1];
			 row[0]=String.valueOf(i);
			 //model.setValueAt(String.valueOf(i),i,0);
			 for(int j=1;j<originColumns+1;j++){
				 row[j]=(String)originModel.getValueAt(i, j-1);
			 }
			// if(!JViewPanel.containsRow(model, row))
			     model.addRow(row);
		 }
		 table=new JTable(model);
		
		 table.setAutoCreateRowSorter(true);
		 table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		 table.addMouseListener(new java.awt.event.MouseAdapter() {
	   	    @Override
	   	    public void mouseClicked(java.awt.event.MouseEvent evt) {
	   	       try{
	   	    	Sack id2key =entigrator.getEntityAtKey(entigrator.indx_keyAtLabel("id2key"));
	   	    	int row = table.rowAtPoint(evt.getPoint());
	   	        int col = table.columnAtPoint(evt.getPoint());
	   	        String value$=(String)table.getValueAt(row, col);
	   	        String header$=table.getColumnName(col);
   	            System.out.println("JQueryPanel:cell click:row="+row+" column="+col+" value="+value$+" header="+header$);
   	            if(id2key==null)
   	            	return;
   	            String entityKey$=id2key.getElementItemAt(header$, value$);
   	            if(entityKey$==null)
   	            	return;
   	         JEntityFacetPanel efp=new  JEntityFacetPanel();
	            String efpLocator$=efp.getLocator();
	         efpLocator$=Locator.append(efpLocator$, Entigrator.ENTIHOME, entihome$);
	      efpLocator$=Locator.append(efpLocator$, EntityHandler.ENTITY_KEY, entityKey$);
	      JConsoleHandler.execute(console, efpLocator$);
	   	   
	   	       }catch(Exception e){
	   	    	   System.out.println("JQueryPanel:initTable:mouse clicked:"+e.toString());
	   	       }
	   	        }
	   	    });
		  sorter = new TableRowSorter<>(table.getModel());
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
			
			int cnt=table.getModel().getColumnCount();
		    String column$;
		    Core[]ca=entity.elementGet("header.alias");
		    for(int i=1;i<cnt;i++){
		 	   column$=table.getModel().getColumnName(i);
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
		 			if("date".equals(c.type))
		 				 sorter.setComparator(i,new JViewPanel.DateComparator());
		 		   }
		 		   
		 	   }
		    }

			scrollPane.getViewport().add(table);
			
	  }catch(Exception e){
	    	Logger.getLogger(getClass().getName()).severe(e.toString());
	    
	    }
	
}
private void addColumn(){
	Object[] options = {"Only '"+componentComboBox.getSelectedItem()+"'",
    "Any component"};
		int n = JOptionPane.showOptionDialog(this,
		    "Add column '"+itemNameComboBox.getSelectedItem()+"'",
		    null,
		    JOptionPane.YES_NO_OPTION,
		    JOptionPane.QUESTION_MESSAGE,
		    null,     //do not use a custom Icon
		    options,  //the titles of buttons
		    options[0]); //default button title
         if(n==0)
        	 addHeader(false);
         else
        	 addHeader(true);
}
private void addHeader(boolean anyComponent){
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
	    if(anyComponent)
	    	entity.putElementItem("header.component", new Core(null,headerKey$,"any"));
	    else
	    	entity.putElementItem("header.component", new Core(null,headerKey$,(String)componentComboBox.getSelectedItem()));
		entigrator.ent_alter(entity); 
		initTable();
	 }catch(Exception e){
	    	Logger.getLogger(getClass().getName()).severe(e.toString());
    }
}
private static String getRowItem(Sack query,Sack member,int col){
	try{
		Core[] ca=query.elementGet("header.element");
		ca=Core.sortAtIntType(ca);
		Core c=ca[col];
		String component$=query.getElementItemAt("header.component", c.name);
	   if(component$!=null&&!"any".equals(component$))
		 if(!member.getProperty("entity").equals(component$))
			return null;
		String element$=c.value;
		Core item=query.getElementItem("header.item", c.name);
		String field$=item.type;
		if("name".equals(field$))
		 return member.getElementItemAt(element$, item.value);
		if("type".equals(field$)){
			ca=member.elementGet(element$);
			for( Core core:ca){
				if(item.value.equals(core.type))
				 return core.value;
			}
		}
	}catch(Exception e){
		Logger.getLogger(JQueryPanel.class.getName()).severe(e.toString());	
	}
	return null;
}
private static ArrayList<String[]> appendComponents(Entigrator entigrator,ArrayList<String[]> rl,Sack query,Sack[] components){
	if(components==null||components.length<1)
		return rl;
	ArrayList<String[]> nrl=new ArrayList<String[]>();
	String[] newRow;
	
	String itemValue$;
	 try{
		for( String[]row:rl){
			for(Sack component:components){
				System.out.println("JQueryPanel:appendComponent:component="+component.getProperty("label"));
				newRow=new String[row.length];
				for(int i=0;i<row.length;i++){
					if(row[i]!=null)
						newRow[i]=row[i];
					else{
						//newRow[i]=getRowItem(query, component, i);
						itemValue$=getRowItem(query, component, i);
						if(!row[i].equals(itemValue$)){
					         		
						}
						
					}
				}
				//if(!containsRow(nrl,newRow))
				nrl.add(newRow);
				String[] sa=entigrator.ent_listComponents(component);
				Sack subComponent;
				if(sa!=null){
					ArrayList <Sack>cl=new ArrayList<Sack>();
					for(String s:sa){
						
						subComponent=entigrator.getEntityAtKey(s);
						if(subComponent!=null){
							System.out.println("JQueryPanel:appendComponent:subcomponent="+subComponent.getProperty("label"));
							cl.add(subComponent);
						}
						
					}
					nrl=appendComponents(entigrator,nrl,query,cl.toArray(new Sack[0]));
				}
			}
		}
		ArrayList<String[]> l=new ArrayList<String[]>();
		 for(String[] r:nrl)
			if(!containsRow(l,r))
				 l.add(r);
	    return l;	
		 //return nrl;
	 }catch(Exception e){
		 Logger.getLogger(JQueryPanel.class.getName()).severe(e.toString());
	 }
	 return nrl;
	 }


private static String[] appendRow(Entigrator entigrator,String[]row,Sack query,Sack member,DefaultTableModel model){
	 try{
		for( int i=0;i<row.length;i++)
			row[i]=getRowItem(query,member,i);
		 String entityType$=member.getProperty("entity");
			Core[] ca=query.elementGet("header.component");
			ArrayList <Core> ikl=new ArrayList<Core>();
			for(Core c:ca)
				if(entityType$.equals(c.value))
					ikl.add(c);
	
			ca=query.elementGet("header.element");
			ca=Core.sortAtIntType(ca);
			ikl.clear();
			int index;
			String element$;
			String name$;
			String value$=null;
			String component$;
			String field$;
			Core item;	
			Core []va;
			for(int i=0;i<ca.length;i++){
				value$=null;
				component$=query.getElementItemAt("header.component", ca[i].name);
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
					if(debug)
						System.out.println("JQueryPanel:addRow:member="+member.getProperty("label")+" element="+element$+" field="+name$+" value="+value$+" index="+index);
					row[index]=value$;
			}
		 String[] sa=entigrator.ent_listComponents(member);
		if(sa!=null){
			Sack newMember;
			for(String s:sa){
				newMember=entigrator.getEntityAtKey(s);
				if(newMember!=null){
					row=appendRow(entigrator,row,query,newMember,model);
				}
			}
		}else{
			if(!JViewPanel.containsRow(model,row));
		       model.addRow(row);
		    if(debug){
		    	for(int i=0;i<row.length;i++)
				   System.out.print("["+i+"]="+row[i]+" ");
		       System.out.println();
		    }
			}
	 }catch(Exception e){
		 Logger.getLogger(JQueryPanel.class.getName()).severe(e.toString());
	 }
	 return row;
}
private static String[] getMemberRow(Entigrator entigrator,Sack query,Sack member,DefaultTableModel model){
	 try{
		 String[] row=new String[model.getColumnCount()];
		 for( int i=0;i<row.length;i++)
			row[i]=getRowItem(query,member,i);
		 return row;
	 }catch(Exception e){
		 Logger.getLogger(JQueryPanel.class.getName()).severe(e.toString());
	 }
	 return null;
}
private static String[] getMemberRow(Entigrator entigrator,Sack query,Sack member,int columnCount){
	 try{
		 String[] row=new String[columnCount];
		 for( int i=0;i<row.length;i++)
			row[i]=getRowItem(query,member,i);
		 return row;
	 }catch(Exception e){
		 Logger.getLogger(JQueryPanel.class.getName()).severe(e.toString());
	 }
	 return null;
}
private static void printRow(String[] row){
	try{
		if(row==null){
		System.out.println("JQueryPanel:printRow: row is null");
		return;
		}
		
		for(String s:row)
			if(s==null)
				System.out.print("null;");
			else
			 System.out.print(s+";");
		System.out.println();
	}catch(Exception e){
		 Logger.getLogger(JQueryPanel.class.getName()).severe(e.toString());
	 }
}
private static int compareRows(String[] r1,String[]r2){
	try{
	
	int r=ROW_NOT_EQUEL;
	int prev=UNKNOWN;
	  for(int i=0;i<r1.length;i++){
		  if(r1[i]==null&&r2[i]!=null)
				r=ROW2_INCLUDES_ROW1;
		  else
		    if(r1[i]!=null&&r2[i]==null)
			    r=ROW1_INCLUDES_ROW2;
		    else
		        if(r1[i]==r2[i])
			       r=ROW_EQUEL;
		        else
			       return ROW_NOT_EQUEL;
		  if(prev==UNKNOWN)
			  prev=r;
		  else{
			  if(prev==ROW2_INCLUDES_ROW1&&r==ROW1_INCLUDES_ROW2)
				  return ROW_NOT_EQUEL;
			  if(prev==ROW1_INCLUDES_ROW2&&r==ROW2_INCLUDES_ROW1)
				  return ROW_NOT_EQUEL;
			  if(prev==ROW_EQUEL)
				  prev=r;
		  }
	}
	   return prev;
	}catch(Exception e){
		 Logger.getLogger(JQueryPanel.class.getName()).severe(e.toString());
	 }
	return ROW_NOT_EQUEL;
}
private static ArrayList<String[]> pasteRows(String[] row1,String[] row2){
	try{
	 
		ArrayList<String[]>sl=new ArrayList<String[]>();	
	  for(int i=0;i<row1.length;i++)
		  if(row1[i]==null)
			  row1[i]=row2[i];
	 
	  for(int i=0;i<row2.length;i++)
		  if(row2[i]==null)
			  row2[i]=row1[i];
	  int res=compareRows(row1,row2);
	
	  if(ROW_EQUEL==res)
	      if(!containsRow(sl,row1))
		       sl.add(row1);
	  if(ROW1_INCLUDES_ROW2==res)
	      if(!containsRow(sl,row1))
		       sl.add(row1);
	  if(ROW2_INCLUDES_ROW1==res)
	      if(!containsRow(sl,row2))
		       sl.add(row2);
	  if(ROW_NOT_EQUEL==res){
	      if(!containsRow(sl,row1))
		       sl.add(row1);
	      if(!containsRow(sl,row2))
	    	   sl.add(row2);
	  }
	  return sl;
	}catch(Exception e){
		 Logger.getLogger(JQueryPanel.class.getName()).severe(e.toString());
	 }
	return null;
}
private static ArrayList<String[]> pasteRows(String[] root,ArrayList<String[]> rl){
	try{
			ArrayList<String[]>sl=new ArrayList<String[]>();
		ArrayList<String[]>pl=new ArrayList<String[]>();
		if(!rl.isEmpty())
		for(String[] r1:rl){
			pl=pasteRows(root,r1);
			if(!containsRow(sl,r1))
					pl.add(r1);
		}else
			pl.add(root);
		return pl;
	}catch(Exception e){
		 Logger.getLogger(JQueryPanel.class.getName()).severe(e.toString());
	 }
	return null;
}
private static JTable buildTable(Entigrator entigrator,Sack query){
	try{
		 if(debug)
	    	   System.out.println("JQueryPanel:buildTable: BEGIN");
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
 			if("date".equals(columnType$))
				 sorter.setComparator(i,new JViewPanel.DateComparator());
		  
      }
    
       Sack member;
       //String[]	row;
       //=new String[columnCount];
    
	   String[] ca;	  
    
       //ArrayList<Sack>ml=new ArrayList<Sack>();
       ArrayList<String[]>rl=new ArrayList<String[]>();
       ArrayList<String[]> cl;
       ArrayList<String> done=new ArrayList<String>();
       String[] sa=select(entigrator,query);
       String[] root;
       Sack component;
       String[] componentRow;
       for(String s:sa){
          member=entigrator.getEntityAtKey(s);
    	 if(member==null)
    		 continue;
    	 root=getMemberRow(entigrator, query, member, model);
    	 ca=entigrator.ent_listComponentsCascade(member);
    	 
    	// System.out.println("JQueryPanel:buildTable:member="+member.getProperty("label"));
    	 cl=new ArrayList<String[]>();
    	 if(ca!=null){
    		// System.out.println("JQueryPanel:buildTable:components="+ca.length);
    		 for (String c:ca){
    			 if(c.equals(s))
    				 continue;
    			 component=entigrator.getEntityAtKey(c);
    			// System.out.println("JQueryPanel:buildTable:component="+component.getProperty("label"));
    			 if(component!=null){
    				componentRow=getMemberRow(entigrator, query, component, model);
    				//printRow(componentRow);
    				if(!containsRow(cl,componentRow))
    					cl.add(componentRow);
    			 }
    		 }
    		 
    	 }
    	 cl=pasteRows(root,cl);
    	 for(String[] c:cl)
      	   if(!containsRow(rl,c))
      		   rl.add(c);

       }

       
       for(String[] r:rl)
    	   model.addRow(r);
       return table;
   	}catch(Exception e){
		Logger.getLogger(JQueryPanel.class.getName()).severe(e.toString());
	}
	return null;
}

private static Comparator getRowComparator(Sack query,String sortColumnName$){
	try{
	Core[] ca=query.elementGet("header.alias");
	String type$=null;
	String key$=null;
	for(Core c:ca){
		if(sortColumnName$.equals(c.value)){
			type$=c.type;
		     key$=c.name;
		     break;
		}
	}
	int column=Integer.parseInt(query.getElementItem("header.element", key$).type);
    System.out.println("JQueryPanel:getRowComparator: column name="+sortColumnName$+" index="+column+" key="+key$);
	//Comparator columnComparator=getComparator(type$);
   return new RowComparator(column,type$);
	}catch(Exception e){
		Logger.getLogger(JQueryPanel.class.getName()).severe(e.toString());
	}
	return null;
}
private static void saveTable(Entigrator entigrator,Sack query,String sortColumnName$){
	try{
	
		String[] sa=select(entigrator,query);
	
		Core[] hea=query.elementGet("header.element");
       hea=Core.sortAtIntType(hea);
       ArrayList <String>headers=new ArrayList<String>();
    
       for(int i=0;i<hea.length;i++)
             headers.add(query.getElementItem("header.alias", hea[i].name).value);
       if(debug){
    	   System.out.println("JQueryPanel:saveTable: headers="+headers.size());
       }
       Sack member;
        File out=new File(entigrator.getEntihome()+"/"+query.getKey()+"/out.txt");
       if(!out.exists())
    	   out.createNewFile();
       Writer file = new BufferedWriter(new OutputStreamWriter(
    		    new FileOutputStream(out), "UTF-8"));
       
       //FileWriter file=new FileWriter(out);
       int i=0;
       String[] columns=headers.toArray(new String[0]);
       
       ArrayList<String[]>crl;
       ArrayList<String[]>sl=new ArrayList<String[]>();
       for(String s:sa){
          member=entigrator.getEntityAtKey(s);
    	 if(member==null)
    		 continue;
    	 crl=expandMember(entigrator,query, member,file,columns);
    	 if(crl!=null)
    		 for(String[] cr:crl)
    			 sl.add(cr);
    	 entigrator.clearCache();
       }
      try{ 
          Collections.sort(sl,getRowComparator(query,sortColumnName$));
      }catch(Exception ee){
    	  Logger.getLogger(JQueryPanel.class.getName()).info(ee.toString());
      }
      for(String[]s:sl){
    	  file.append("<tr>");
 		 file.append("<td>"+String.valueOf(i++)+"</td>");
 		 for(int j=0;j<s.length;j++)
 			  file.append("<td>"+s[j]+"</td>");
 		 file.append("</tr>");
     	 }
      
       file.close();

   	}catch(Exception e){
		Logger.getLogger(JQueryPanel.class.getName()).severe(e.toString());
	}

}


private void clearHeader(){
	int response = JOptionPane.showConfirmDialog(this, "Clear header ?", "Confirm",
	        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
   if (response == JOptionPane.YES_OPTION) {
        try{
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
        	query.removeElement("header.item");
        	query.removeElement("header.alias");
        	
        	entigrator.ent_alter(query);
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
        	entigrator.ent_alter(entity);
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
        	entigrator.ent_alter(entity);
        	
	    } catch(Exception e){
	    	LOGGER.severe(e.toString());
	    }
}
private void initType(){
	try{
	        String itemName$=(String)itemNameComboBox.getSelectedItem();
	    if(debug)
	    	System.out.println("JQueryPanel.initType:item name="+itemName$);
	        entigrator=console.getEntigrator(entihome$);
        	entity=entigrator.getEntityAtKey(entityKey$);
        	String itemKey$=entity.getElementItemAtValue("header.item", itemName$);
        	Core alias=entity.getElementItem("header.alias", itemKey$);
        	if(debug)
     	    	System.out.println("JQueryPanel.initType:item type="+alias.type);
        	int cnt=itemTypeComboBox.getModel().getSize();
        	for(int i=0;i<cnt;i++)
        		if(alias.type.equals(itemTypeComboBox.getItemAt(i)))
        	      itemTypeComboBox.setSelectedIndex(i);
	    } catch(Exception e){
	    	if(debug)
	    	LOGGER.severe(e.toString());
	    }
}
private static void createClasspathFile(String entihome$,String queryKey$){
	try{
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
/**
 * No action
 */	
@Override
public void activate() {
	
}
/**
 * Get table rows as an array of strings in http form.
 * @param entigrator the entigrator
 * @param locator$ the locator string
 * @return table rows as a string array.
 */	
public  static String getWebItems(Entigrator entigrator,String locator$){
	try{
		System.out.println("JQueryPanel:getWebItems:BEGIN");	
		Properties locator=Locator.toProperties(locator$);
			String entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
			String sortColumnName$=locator.getProperty(JViewPanel.SORT_COLUMN_NAME);
			String entityKey$=entigrator.indx_keyAtLabel(entityLabel$);
		    Sack  query=entigrator.getEntityAtKey(entityKey$);
		    saveTable(entigrator, query,sortColumnName$);
			StringBuffer sb=new StringBuffer();
			 sb.append("<table style=\"text-align: left;  background-color: transparent;\"  border=\"1\" cellpadding=\"2\" cellspacing=\"2\">");
			 sb.append(getWebHeader(query));
			 //
			 File out=new File(entigrator.getEntihome()+"/"+query.getKey()+"/out.txt");
			 BufferedReader br = new BufferedReader(new InputStreamReader(
					    new FileInputStream(out), "UTF-8"));
			 int value = 0;
			 while((value = br.read()) != -1) {
		            char c = (char)value;
		            sb.append(c);
			 }
  		     br.close();
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
/**
 * Get table rows as an array of strings in http form.
 * @param entigrator the entigrator
 * @param query the query entity
 * @param sortColumnName$ sort rows by this column
 * @return table rows as a string array.
 */

/**
 * Get facet open class.
 * @return the name of the facet open class.
 */	
@Override
public String getFacetOpenItem() {
	return JQueryFacetOpenItem.class.getName();
}
/**
 * Get facet icon.
 * @return the name of the facet icon file.
 */	
@Override
public String getFacetIcon() {
	return "query.png";
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
private static ArrayList<String[]> expandMember(Entigrator entigrator,Sack query,Sack member,Writer file,String[] columns){
	 try{
		 String[] rootRow=getMemberRow(entigrator, query, member, columns.length);
		 String[] ca=entigrator.ent_listComponentsCascade(member);
    	 Sack component;
    	 String[] componentRow;

    	 ArrayList<String> cl=new ArrayList<String>();
    	 ArrayList<String[]> crl=new ArrayList<String[]>();
    	 if(ca!=null){

    		 for (String c:ca){
    			 if(c.equals(member.getKey()))
    				 continue;
    			 if(cl.contains(c))
    				 continue;
    			 cl.add(c);
    			 component=entigrator.getEntityAtKey(c);
    			// System.out.println("JQueryPanel:buildTable:component="+component.getProperty("label"));
    			 if(component!=null){
    				componentRow=getMemberRow(entigrator, query, component, columns.length);
    				//printRow(componentRow);
    				if(!containsRow(crl,componentRow))
    					crl.add(componentRow);
    				}
    			 }
    	      }
    	 
    	 
	 crl=pasteRows(rootRow,crl);
	 ArrayList<String[]> sl=new ArrayList<String[]>();
	 for (String[] cr:crl)
		 if(!containsRow(sl,cr))
				sl.add(cr);
	 return sl;
	 }catch(Exception e){
		 Logger.getLogger(JQueryPanel.class.getName()).severe(e.toString());
	 }
	 return null;
}

private static boolean containsRow(ArrayList<String[]>rl,String[] row){
	try{
	boolean next;	
	
	for(String[] r:rl){
		if(row.length!=r.length)
			return false;
		next=false;
		for(int i=0;i<r.length;i++){
			if(r[i]==null&&row[i]!=null){
			  next=true;
			  break;
			}
			
			if(r[i]!=null&&row[i]==null){
				  next=true;
				  break;
				}
			
			if(r[i]!=null&&row[i]!=null)
			   if(!r[i].equals(row[i])){
				next=true;
				  break;
			}
			
			
		}
		if(next)
			continue;
		else
			return true;
	}
	}catch(Exception e){
		Logger.getLogger(JQueryPanel.class.getName()).severe(e.toString());
		
	}
	return false;
}
public static class RowComparator implements Comparator<String[]> {
	int column;
	String type$;
	public RowComparator(int column,String type$){
		this.column=column;
		this.type$=type$;
	}
    @Override
    public int compare(String[] row1, String[]row2) {
        try{
       // 	System.out.println("JQueryPanel:compare:type="+type$+" column="+column);
        if(row1[column]==null||"null".equals(row1[column]))
        	if(row2[column]!=null||!"null".equals(row2[column]))
        		return -1;
        	if(row2[column]==null||"null".equals(row2[column]))
            	if(row1[column]!=null||!"null".equals(row1[column]))
            		return 1;
           if(row2[column]==null||"null".equals(row2[column]))
        	   if(row1[column]==null||"null".equals(row1[column]))
        		   return 0;
        
    	//printRow(row1);
    	//printRow(row2);
        Comparator c=getComparator(type$);
        int ret= c.compare(row1[column], row2[column]);
        //System.out.println("JQueryPanel:compare:column="+column+" ret="+ret);
        return ret;
        }catch(Exception e){
        	return 0;
        }
    }
}
}
