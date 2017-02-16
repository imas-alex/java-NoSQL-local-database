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
import java.util.Collections;
import java.util.Comparator;
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
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
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
import gdt.jgui.console.WContext;
import gdt.jgui.entity.JEntitiesPanel;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.entity.JEntityPrimaryMenu;
import gdt.jgui.entity.JReferenceEntry;
import gdt.jgui.entity.folder.JFolderFacetAddItem;
import gdt.jgui.entity.folder.JFolderFacetOpenItem;
import gdt.jgui.tool.JTextEditor;
/**
 * This class represents the query context
 * @author imasa.
 *
 */

public class JQueryPanel extends JPanel implements JFacetRenderer,JRequester{
	
	private static final long serialVersionUID = 1L;
	private static final String LABEL_KEY="_UTqVuBCJrzhoJNrgqBhZDCNpimo";
	private static final String ACTION_CREATE_QUERY="action create query";
	public  static final String QUERY_ALL_ENTITIES_KEY ="_2DOtCo5e_S6ARWU0H_MtyFnnPThE";
	private Logger LOGGER=Logger.getLogger(getClass().getName());
	private JComboBox<String> elementComboBox; 
	private JComboBox<String> itemNameFieldComboBox;
	private JComboBox<String> itemNameComboBox;
	private JComboBox<String> itemValueComboBox;
	private JTable table;
	protected String entihome$;
	protected String entityKey$;
	protected String entityLabel$;
	protected JMainConsole console;
	private JMenu menu;
	static boolean debug=false;
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
		JLabel lblElement = new JLabel("Element");
		
		GridBagConstraints gbc_lblElement = new GridBagConstraints();
		gbc_lblElement.insets = new Insets(5, 5, 5, 5);
		gbc_lblElement.gridx = 0;
		gbc_lblElement.gridy = 0;
		gbc_lblElement.anchor=GridBagConstraints.FIRST_LINE_START;
		add(lblElement, gbc_lblElement);
		
		elementComboBox = new JComboBox<String>();
		GridBagConstraints gbc_elementComboBox = new GridBagConstraints();
		gbc_elementComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_elementComboBox.insets = new Insets(0, 0, 5, 0);
		gbc_elementComboBox.gridx = 1;
		gbc_elementComboBox.gridy = 0;
		gbc_elementComboBox.anchor=GridBagConstraints.FIRST_LINE_START;
		add(elementComboBox, gbc_elementComboBox);
		elementComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				try{
					initItemNameSelector();
				}catch(Exception ee){
					LOGGER.severe(ee.toString());
				}
			}
		 });
		JLabel lblItemNameField = new JLabel("Item field");
		GridBagConstraints gbc_lblItemField = new GridBagConstraints();
		gbc_lblItemField.insets = new Insets(5, 5, 5, 5);
		gbc_lblItemField.gridx = 0;
		gbc_lblItemField.gridy = 1;
		gbc_lblItemField.anchor=GridBagConstraints.FIRST_LINE_START;
		
		add(lblItemNameField, gbc_lblItemField);

		itemNameFieldComboBox = new JComboBox<String>();
		GridBagConstraints gbc_itemNameFieldComboBox = new GridBagConstraints();
		gbc_itemNameFieldComboBox.insets = new Insets(0, 0, 5, 0);
		gbc_itemNameFieldComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_itemNameFieldComboBox.gridx = 1;
		gbc_itemNameFieldComboBox.gridy = 1;
		gbc_itemNameFieldComboBox.anchor=GridBagConstraints.FIRST_LINE_START;
		add(itemNameFieldComboBox, gbc_itemNameFieldComboBox);
		itemNameFieldComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				try{
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
		gbc_lblItemtitle.gridy = 2;
		gbc_lblItemtitle.anchor=GridBagConstraints.FIRST_LINE_START;
		add(itemTitle, gbc_lblItemtitle);

		itemNameComboBox = new JComboBox<String>();
		GridBagConstraints gbc_itemComboBox = new GridBagConstraints();
		gbc_itemComboBox.insets = new Insets(0, 0, 5, 0);
		gbc_itemComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_itemComboBox.gridx = 1;
		gbc_itemComboBox.gridy = 2;
		gbc_itemComboBox.anchor=GridBagConstraints.FIRST_LINE_START;
		add(itemNameComboBox, gbc_itemComboBox);
		itemNameComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				try{
					initItemValueSelector();
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
		gbc_lblItemValue.gridy = 3;
		gbc_lblItemValue.anchor=GridBagConstraints.FIRST_LINE_START;
		add(itemValue, gbc_lblItemValue);

		itemValueComboBox = new JComboBox<String>();
		GridBagConstraints gbc_itemValueComboBox = new GridBagConstraints();
		gbc_itemValueComboBox.insets = new Insets(0, 0, 5, 0);
		gbc_itemValueComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_itemValueComboBox.gridx = 1;
		gbc_itemValueComboBox.gridy = 3;
		gbc_itemValueComboBox.anchor=GridBagConstraints.FIRST_LINE_START;
		add(itemValueComboBox, gbc_itemValueComboBox);
		table = new JTable();
		JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

   	  table.addMouseListener(new java.awt.event.MouseAdapter() {
   	    @Override
   	    public void mouseClicked(java.awt.event.MouseEvent evt) {
   	        int row = table.rowAtPoint(evt.getPoint());
   	        int col = table.columnAtPoint(evt.getPoint());
   	        System.out.println("JQueryPanel:cell click:row="+row+" column="+col);
   	        if(col==1){
   	        	String label$=(String)table.getValueAt(row, 1);
   	        	System.out.println("JQueryPanel:cell click:label="+label$);
   	            Entigrator entigrator=console.getEntigrator(entihome$);
   	            String entity$=entigrator.indx_keyAtLabel(label$);
   	            JEntityFacetPanel efp=new  JEntityFacetPanel();
   	            String efpLocator$=efp.getLocator();
   	         efpLocator$=Locator.append(efpLocator$, Entigrator.ENTIHOME, entihome$);
   	      efpLocator$=Locator.append(efpLocator$, EntityHandler.ENTITY_KEY, entity$);
   	      JConsoleHandler.execute(console, efpLocator$);
   	        }
   	        }
   	    });
		GridBagConstraints gbc_scroll_panel = new GridBagConstraints();
		gbc_scroll_panel.anchor = GridBagConstraints.NORTH;
		gbc_scroll_panel.gridwidth = 2;
		gbc_scroll_panel.weighty=1.0;
		gbc_scroll_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_scroll_panel.gridx = 0;
		gbc_scroll_panel.gridy =4;
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
					 JMenuItem selectItem = new JMenuItem("Select");
						selectItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
							  showHeader();
							  showContent();
							}
						} );
						menu.add(selectItem);
						JMenuItem clearHeader = new JMenuItem("Clear all");
						clearHeader.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
							    clearHeader();
							    showHeader();
								showContent();
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
					            showHeader();
								showContent();
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
		Entigrator entigrator=console.getEntigrator(entihome$);
		Sack query=entigrator.getEntity(entityKey$);
		return select( entigrator,query);
		
	}
	public static String[] select(Entigrator entigrator,Sack query){
		try{
			String entihome$=entigrator.getEntihome();
			String entityKey$=query.getKey();
			File queryHome=new File(entihome$+"/"+entityKey$);
			URL url = queryHome.toURI().toURL();
		    URL[] urls = new URL[]{url};
		    ClassLoader parentLoader = JMainConsole.class.getClassLoader();
		    URLClassLoader cl = new URLClassLoader(urls,parentLoader);
		  Class<?> cls = cl.loadClass(entityKey$);
		  Object obj=cls.newInstance();
		  Method method = obj.getClass().getDeclaredMethod("select",Entigrator.class);
 	      Object value=method.invoke(obj,entigrator);
 	     
 	      String[] sa=(String[])value;
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
			Entigrator entigrator=console.getEntigrator(entihome$);
			entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
			if(entityLabel$==null)
				entityLabel$=entigrator.indx_getLabel(entityKey$);
			 if(Locator.LOCATOR_TRUE.equals(locator.getProperty(JFacetRenderer.ONLY_ITEM)))
				 return this;
			Sack entity=entigrator.getEntityAtKey(entityKey$);
            entityLabel$=entity.getProperty("label");
            if(debug)
    			System.out.println("JQueryPanel.instantiate:1");    
            initElementSelector();
            if(debug)
    			System.out.println("JQueryPanel.instantiate:2");   
            initItemNameFieldSelector();
            if(debug)
    			System.out.println("JQueryPanel.instantiate:3");   
            initItemNameSelector();
            initItemValueSelector();
            showHeader();
            showContent();
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
	String[]sa=select();
	if(sa==null){
		elementComboBox.setModel(model);
		return;
	}
	 if(debug)
			System.out.println("JQueryPanel. initElementSelector:1");
  
	try{
		Entigrator entigrator=console.getEntigrator(entihome$);
		ArrayList <String>sl=new ArrayList<String>();
		Sack entity;
        String[] ea;		
		for(String s:sa){
			 if(debug)
					System.out.println("JQueryPanel. initElementSelector:s="+s);
			entity=entigrator.getEntityAtKey(s);
			if(entity==null)
				continue;
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
private void initItemNameSelector(){
    DefaultComboBoxModel<String> model=new DefaultComboBoxModel<String>();
    try{
    String element$=(String)elementComboBox.getSelectedItem();
    String constituent$=(String)itemNameFieldComboBox.getSelectedItem();
    Entigrator entigrator=console.getEntigrator(entihome$);
    String[]sa=select();
    ArrayList <String>sl=new ArrayList<String>();
	Sack entity;
    String[] ia;
    String item$;
    Core item;
	for(String s:sa){
		entity=entigrator.getEntityAtKey(s);
		if(entity==null)
			continue;
		ia=entity.elementList(element$);
		if(ia!=null)
			for(String i:ia){
	          item$=null;
			  item=entity.getElementItem(element$, i);		
			  if("name".equals(constituent$))
			     item$=item.name;
			  if("type".equals(constituent$))
				     item$=item.type;
			 if(item$!=null&&!sl.contains(item$))	
				sl.add(item$);
			}
	}
	Collections.sort(sl);
	for(String s:sl)
		model.addElement(s);
    itemNameComboBox.setModel(model);
    }catch(Exception e){
    	Logger.getLogger(getClass().getName()).severe(e.toString());
    	itemNameComboBox.setModel(model);
    }
}
private void initItemValueSelector(){
    DefaultComboBoxModel<String> model=new DefaultComboBoxModel<String>();
    try{
    String element$=(String)elementComboBox.getSelectedItem();
    String field$=(String)itemNameFieldComboBox.getSelectedItem();
    String itemName$=(String)itemNameComboBox.getSelectedItem();
    Entigrator entigrator=console.getEntigrator(entihome$);
    String[]sa=select();
    ArrayList <String>sl=new ArrayList<String>();
	Sack entity;
    String[] ia;
    
    String value$;
    Core item;
	for(String s:sa){
		entity=entigrator.getEntityAtKey(s);
		if(entity==null)
			continue;
		ia=entity.elementList(element$);
		if(ia!=null)
			for(String i:ia){
	          value$=null;
			  item=entity.getElementItem(element$, i);		
			  if("name".equals(field$)&&itemName$.equals(item.name))
				  value$=item.value;
			     
			  if("type".equals(field$)&&itemName$.equals(item.type))
				     value$=item.value;
			 if(value$!=null&&!sl.contains(value$))	
				sl.add(value$);
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
private void addHeader(){
	 try{
		 if(debug)
			 System.out.println("JQueryPanel:addHeader:BEGIN");
		 Entigrator entigrator=console.getEntigrator(entihome$);
		 Sack query=entigrator.getEntityAtKey(entityKey$);
		 String headerKey$=Identity.key();
		 String itemName$=(String)itemNameComboBox.getSelectedItem();
		 Core[]ca=query.elementGet("header.item");
		 for(Core c:ca)
			 if(itemName$.equals(c.value))
				 return;
	    query.putElementItem("header.item", new Core((String)itemNameFieldComboBox.getSelectedItem(),headerKey$,(String)itemNameComboBox.getSelectedItem()));
	    ca=query.elementGet("header.element");
	    query.putElementItem("header.element", new Core(String.valueOf(ca.length),headerKey$,(String)elementComboBox.getSelectedItem()));
	    orderColumns();
		entigrator.replace(query); 
		showHeader();
		showContent();
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
private void orderColumns(){
	try{
	    Entigrator entigrator=console.getEntigrator(entihome$);
	    Sack query=entigrator.getEntityAtKey(entityKey$);
	    Core[]ca=query.elementGet("header.item");
	    // ca=Core.sortAtValue(ca);
	     ArrayList<String>sl=new ArrayList<String>();
	     int i=0;
	     for(Core c:ca)
	     	if(c.value!=null){
	     		sl.add(c.value);
	     	}
	      Collections.sort(sl,new ItemComparator());
	      Core element;
	      for(String s:sl){
	     	//System.out.println("JQueryPanel.showHeader:item="+s);
	     	 for(Core c:ca){
	     		 if(s.equals(c.value)){
	     			 element=query.getElementItem("header.element", c.name);
	     			 element.type=String.valueOf(i++);
	     			 query.putElementItem("header.element", element); 
	     		 }
	     	 }
	      }
	      entigrator.save(query);
	}catch(Exception e ){
		LOGGER.severe(e.toString());
	}
}
private void showHeader(){
	try{
    Entigrator entigrator=console.getEntigrator(entihome$);
    Sack query=entigrator.getEntityAtKey(entityKey$);
    
    if(!query.existsElement("header.item")){
		 query.createElement("header.item");
		 query.putElementItem("header.item", new Core("name",entityKey$,"number"));
		 query.putElementItem("header.item", new Core("type",LABEL_KEY,"label"));
    }
    if(!query.existsElement("header.element")){
		query.createElement("header.element");
		 query.putElementItem("header.element", new Core("0",entityKey$,"none"));
		 query.putElementItem("header.element", new Core("1",LABEL_KEY,"property"));
    }
    Core[]ca=query.elementGet("header.item");
    ArrayList<String>sl=new ArrayList<String>();
    int i=0;
    for(Core c:ca)
    	if(c.value!=null){
    		sl.add(c.value);
    	}
     Collections.sort(sl,new ItemComparator());
     Core element;
     for(String s:sl){
//    	System.out.println("JQueryPanel.showHeader:item="+s);
    	 for(Core c:ca){
    		 if(s.equals(c.value)){
    			 element=query.getElementItem("header.element", c.name);
  //  			 System.out.println("JQueryPanel.showHeader:order="+i);
    			 element.type=String.valueOf(i++);
    			 query.putElementItem("header.element", element); 
    			 break;
    		 }
    	 }
     }
   entigrator.save(query);
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
		    	String col$=String.valueOf(col);
		//    	String itemName$ = table.getColumnName(col);
	            
	//	       System.out.println("Column index=" + col+" item="+ itemName$);
		        String element$=null;
		  //      String field$=null;
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
		        
		        Core item=query.getElementItem("header.item", headerKey$);		
		        	
		 //       System.out.println("JQueryPanel:header listener:element=" +element$ + " item=" + itemName$+" field="+field$);
		        
		        setSelection(elementComboBox,element$);
		        setSelection(itemNameComboBox,item.value);
		        
		        setSelection(itemNameFieldComboBox,item.type);
		    	}catch(Exception ee){
		    		Logger.getLogger(JQueryPanel.class.getName()).severe(ee.toString());
		    	}
		    }
		});
	}catch(Exception e ){
		LOGGER.severe(e.toString());
	}
}
private static String[] getRow(Sack entity,Sack query,int num){
	try{	
	//	 System.out.println("JQuerypanel:getRow:num="+num);
		Core[]ca=query.elementGet("header.item");
	    ArrayList<String>sl=new ArrayList<String>(); 
	    sl.add(String.valueOf(num));
	    sl.add(entity.getProperty("label"));
		Properties props=new Properties();
		String value$="";
		String element$;
		Core[] va;
		 for(Core c:ca){
			 element$=query.getElementItemAt("header.element", c.name);
		//	 System.out.println("JQuerypanel:getRow:element="+element$+" field="+c.type);
			 if("name".equals(c.type)){
				 value$=entity.getElementItemAt(element$,c.value);
				 if(value$==null)
					 value$="";
			 }else{
					value$="";
				va=entity.elementGet(element$);
				if(va!=null)
				{
			//	System.out.println("JQuerypanel:getRow:va="+va.length);
				for(Core v:va){
				//	System.out.println("JQuerypanel:getRow:v.type="+v.type+" c.value="+c.value);
					if(c.value.equals(v.type)){
			           value$=v.value;
    				    break;
					}
				}}
			 }
			 props.put(c.value,value$);
	//		 System.out.println("JQuerypanel:getRow:put name="+c.value+" value="+value$);	 
	         
		 }
		//System.out.println("JQuerypanel:getRow:value="+value$);	 
		 
		va=query.elementGet("header.element");
		String[]sva=new String[va.length];
		
		int col;
		for(Core v:va){
			value$=props.getProperty(query.getElementItemAt("header.item", v.name));
			if(value$==null)
				value$="";
			col=Integer.parseInt(v.type);
			sva[col]=value$;
			if(query.getKey().equals(v.name))
				sva[col]=String.valueOf(num);
		}
		
		// System.out.println("JQuerypanel:getRow:FINISH");
		 return sva;
	    }catch(Exception e ){
	Logger.getLogger(JQueryPanel.class.getName()).severe(e.toString());
}
	return null;
}
private void showContent(){
	try{
	 	
     String[] sa=select();
     Entigrator entigrator=console.getEntigrator(entihome$);
     Sack query=entigrator.getEntityAtKey(entityKey$);
     ArrayList <String>sl=new ArrayList<String>();
     String label$;
     for(String s:sa){
    	 label$=entigrator.indx_getLabel(s);
    	 if(label$==null){
    		 if(debug)
    		    System.out.println("JQueryPanel:showContent:cannot get  label for key="+s);
    		 continue;
    	 }
    	 sl.add(label$);
     }
     Collections.sort(sl,new SortIgnoreCase());
     Sack entity;
     String entity$;
     String[] row;
     StringBuffer sb;
     int num=0;
     DefaultTableModel model=(DefaultTableModel)table.getModel();
     for(String s:sl){
    	// System.out.println("JQueryPanel:showContent:label="+s);
    	 entity$=entigrator.indx_keyAtLabel(s);
    	 entity=entigrator.getEntityAtKey(entity$);
    	 if(entity==null){
    		 if(debug)
    		 System.out.println("JQueryPanel:showContent:cannot get entity="+entity$);
    		 continue;
    	 }
    	 row=getRow(entity, query,num++);
    	 if(row==null){
    		 if(debug)
    		 System.out.println("JQueryPanel:showContent:cannot get row num="+num);
    		 continue;
    	 }
    	 model.addRow(row);
    	 sb=new StringBuffer();
    	 for(String r:row)
    	   sb.append(r+";");
//    	 System.out.println("JQueryPanel:showContent:"+sb.toString());
     }
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
}
private void clearHeader(){
	int response = JOptionPane.showConfirmDialog(this, "Clear header ?", "Confirm",
	        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
   if (response == JOptionPane.YES_OPTION) {
        try{
        	Entigrator entigrator=console.getEntigrator(entihome$);
        	Sack query=entigrator.getEntityAtKey(entityKey$);
        	query.removeElement("header.element");
        	query.removeElement("header.item");
        	query.removeElement("exclude");
        	entigrator.save(query);
        	DefaultTableModel model=new DefaultTableModel();
        	table.setModel(model);
        	
	    } catch(Exception e){
	    	LOGGER.severe(e.toString());
	    }
}
}
private void removeColumn(){
	String itemName$=(String)itemNameComboBox.getSelectedItem();
	if("label".equals(itemName$))
		return;
	int response = JOptionPane.showConfirmDialog(this, "Remove column '"+itemName$+"' ?", "Confirm",
	        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
   if (response == JOptionPane.YES_OPTION) {
        try{
        	Entigrator entigrator=console.getEntigrator(entihome$);
        	Sack query=entigrator.getEntityAtKey(entityKey$);
        	
           	Core[] ca=query.elementGet("header.item");
        	for(Core c:ca){
        		if(itemName$.equals(c.value)){
        			query.removeElementItem("header.element", c.name);
                	query.removeElementItem("header.item",c.name);	
        		}
        	}
        	entigrator.save(query);
        	DefaultTableModel model=new DefaultTableModel();
        	table.setModel(model);
        	showHeader();
        	showContent();
	    } catch(Exception e){
	    	LOGGER.severe(e.toString());
	    }
}
}
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
static class ItemComparator implements Comparator<String>{
    @Override
    public int compare(String l1$, String l2$) {
    	try{
  //  		System.out.println("ItemComparator:compare:l1="+l1$+" l2="+l2$);
    	if(l1$.equals("number"))
    		l1$="$$$$a";
    	if(l2$.equals("number"))
    		l2$="$$$$a";
    	if(l1$.equals("label"))
    		l1$="$$$$b";
    	if(l2$.equals("label"))
    		l2$="$$$$b";
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
public static String getViewItems(Entigrator entigrator,String locator$){
	try{
		//	System.out.println("IndexPanel.instantiate:locator="+locator$);
			
			Properties locator=Locator.toProperties(locator$);
			String entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
			String entityKey$=entigrator.indx_keyAtLabel(entityLabel$);
		    Sack  query=entigrator.getEntityAtKey(entityKey$);
			 StringBuffer sb=new StringBuffer();
			 sb.append("<table style=\"text-align: left;  background-color: transparent;\"  border=\"1\" cellpadding=\"2\" cellspacing=\"2\">");
			 sb.append(getWebHeader(query));
			 sb.append(getWebItems(entigrator,query));
	         sb.append("</table>"); 
			return sb.toString(); 
		}catch(Exception e){
	        Logger.getLogger(JQueryPanel.class.getName()).severe(e.toString());
		}
		return null;
}
private static String getWebHeader(Sack query){
	try{
		Core[] ca=query.elementGet("header.item");
		ArrayList<String>sl=new ArrayList<String>();
		StringBuffer sb=new StringBuffer();
		sb.append("<tr>");
		sb.append("<td><strong>number</strong></td><td><strong>label</strong></td>");
		for(Core c:ca)
			if(!"number".equals(c.value)&&!"label".equals(c.value)){
				sl.add(c.value);
			}
		Collections.sort(sl,new SortIgnoreCase());
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
public static String getWebItems(Entigrator entigrator,Sack query){
	try{
		String[] sa=select(entigrator,query);
	     ArrayList <String>sl=new ArrayList<String>();
	     String label$;
	     for(String s:sa){
	    	 label$=entigrator.indx_getLabel(s);
	    	 if(label$==null){
	    		 if(debug)
	    		    System.out.println("JQueryPanel:showContent:cannot get  label for key="+s);
	    		 continue;
	    	 }
	    	 sl.add(label$);
	     }
	     Collections.sort(sl,new SortIgnoreCase());
	     Sack entity;
	     String entity$;
	     StringBuffer sb=new StringBuffer();
	     int num=0;
	     String row$;
	     for(String s:sl){
	    	// System.out.println("JQueryPanel:showContent:label="+s);
	    	 entity$=entigrator.indx_keyAtLabel(s);
	    	 entity=entigrator.getEntityAtKey(entity$);
	    	 if(entity==null){
	    		 if(debug)
	    		 System.out.println("JQueryPanel:showContent:cannot get entity="+entity$);
	    		 continue;
	    	 }
         	 row$=getWebItem(entity, query, num++);
         	 if(debug)
         	 System.out.println("JQueryPanel:getWebItems:row="+row$);
         	 sb.append(row$);
	     }
	     return sb.toString();
	}catch(Exception e){
		 Logger.getLogger(JQueryPanel.class.getName()).severe(e.toString());
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
private static String getWebItem(Sack entity,Sack query,int num){
	String[] sa=getRow(entity,query, num);
	if(sa==null)
		return null;
	StringBuffer sb=new StringBuffer();
	sb.append("<tr>");
	//for(String s:sa){
	for(int i=0;i<sa.length;i++){
		if(sa[i].startsWith("http://")||sa[i].startsWith("https://"))
			sb.append("<td><a href=\""+sa[i]+"\">"+sa[i]+"</a></td>");
		else{
		if(i==1)
			sb.append("<td  onclick=\"labelClick('"+sa[i]+"')\" style=\"text-decoration:underline;\"><strong>"+sa[i]+"</strong></td>");
		else
			sb.append("<td>"+sa[i]+"</td>");
		}
	}
	sb.append("</tr>");
	return sb.toString();
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
}
