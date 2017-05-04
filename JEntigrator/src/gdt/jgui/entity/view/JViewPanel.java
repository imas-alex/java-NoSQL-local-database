package gdt.jgui.entity.view;
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.util.List;
import java.util.Properties;
import java.util.Stack;
import java.util.Vector;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.facet.FolderHandler;
import gdt.data.entity.facet.ViewHandler;
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
import gdt.jgui.entity.JEntityPrimaryMenu;
import gdt.jgui.entity.JReferenceEntry;
import gdt.jgui.entity.folder.JFolderFacetAddItem;
import gdt.jgui.entity.folder.JFolderFacetOpenItem;

import gdt.jgui.tool.JTextEditor;
/**
 * This class represents the view context
 * @author imasa.
 *
 */

public class JViewPanel extends JPanel implements JFacetRenderer,JRequester{
	
	private static final long serialVersionUID = 1L;
	//private static final String LABEL_KEY="_UTqVuBCJrzhoJNrgqBhZDCNpimo";
	private static final String ACTION_CREATE_VIEW="action create view";
	public  static final String SORT_COLUMN_NAME ="sort column name";
	private Logger LOGGER=Logger.getLogger(getClass().getName());
	private JComboBox<String> parameterComboBox; 
	private JComboBox<String> valueComboBox;
	private JTable parametersTable;
	private JTable contentTable;
	private JScrollPane scrollPane;
	protected String entihome$;
	protected String entityKey$;
	protected String entityLabel$;
	protected Sack entity=null;
	protected JMainConsole console;
	private JMenu menu;
	static boolean debug=true;
	protected ArrayList <String>viewScope;
	protected ArrayList <String>elementScope;
	protected View view;
	/**
	 * The default constructor.
	 */
	public JViewPanel() {
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{100, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0};
		setLayout(gridBagLayout);
		GridBagConstraints gbc_scroll_panel = new GridBagConstraints();
		gbc_scroll_panel.anchor = GridBagConstraints.NORTH;
		gbc_scroll_panel.gridwidth = 2;
		gbc_scroll_panel.weighty=1.0;
		//gbc_scroll_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_scroll_panel.fill = GridBagConstraints.BOTH;
		gbc_scroll_panel.gridx = 0;
		gbc_scroll_panel.gridy =0;
		contentTable=new JTable();
		contentTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		scrollPane = new JScrollPane(contentTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(scrollPane, gbc_scroll_panel);
		
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
				System.out.println("JViewPanel:getConextMenu:menu selected");
				 JMenuItem selectItem = new JMenuItem("Select");
						selectItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
							 
							  try{
							  contentTable.setModel(select());
							  setSorter();
							 //scrollPane.revalidate();
							 //scrollPane.repaint();
							  }catch(Exception ee){
								  Logger.getLogger(JViewPanel.class.getName()).severe(ee.toString());
							  }
							}
						} );
						menu.add(selectItem);
						Entigrator entigrator=console.getEntigrator(entihome$);
						Sack view=entigrator.getEntityAtKey(entityKey$);
						
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
					JMenuItem doneItem = new JMenuItem("Done");
					doneItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							Entigrator entigrator=console.getEntigrator(entihome$);
							Sack view=entigrator.getEntityAtKey(entityKey$);
							entigrator.saveNative(view);
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
	/*
	private String[] select(Properties params){
		Entigrator entigrator=console.getEntigrator(entihome$);
	
		return select( entigrator,params);
		
	}
	*/
	protected void initView(){
		try{
			if(debug)
	 			System.out.println("JViewPanel.initView:BEGIN");
			File viewHome=new File(entihome$+"/"+entityKey$);
			//File classFile=new File(entihome$+"/"+entityKey$+"/"+entityKey$+".class");
			//if(!classFile.exists())
				
			//if(debug)
	 		//	System.out.println("JViewPanel.initView:1");
			URL url = viewHome.toURI().toURL();
		    URL[] urls = new URL[]{url};
		    ClassLoader parentLoader = JMainConsole.class.getClassLoader();
		    URLClassLoader cl = new URLClassLoader(urls,parentLoader);
		  Class<?> cls = cl.loadClass(entityKey$);
		  view=(View)cls.newInstance();
		}catch(Exception e){
			Logger.getLogger(JViewPanel.class.getName()).severe(e.toString());
		}
	}
	protected DefaultTableModel select(){
		try{
			Core[] ca=entity.elementGet("parameter");
			Properties properties=new Properties();
			if(ca!=null){
			
			for(Core c:ca)
				 properties.setProperty(c.name, c.value);
			}
			//Method method = view.getClass().getDeclaredMethod("select",Entigrator.class,Properties.class);
			initView();
			Method method = view.getClass().getDeclaredMethod("select",Entigrator.class);
	 	    return (DefaultTableModel)method.invoke(view,console.getEntigrator(entihome$));
		}catch(Exception e){
			Logger.getLogger(JViewPanel.class.getName()).severe(e.toString());
		}
		return null;
	}
	protected void initParameters(){
		try{
			DefaultComboBoxModel<String> pmodel=new DefaultComboBoxModel<String>();
			parameterComboBox.setModel(pmodel);
			Method method = view.getClass().getDeclaredMethod("listParameters");
	 	    String []na=(String[])method.invoke(view);
	 	    if(na!=null)
	 	    	for(String n:na)
	 	    		pmodel.addElement(n);
		}catch(Exception e){
			Logger.getLogger(JViewPanel.class.getName()).severe(e.toString());
		}
	}
	protected void initValues(){
		try{
			DefaultComboBoxModel<String> vmodel=new DefaultComboBoxModel<String>();
			valueComboBox.setModel(vmodel);
			String parameter$=(String)parameterComboBox.getSelectedItem();
			if(parameter$==null)
				return;
			Method method = view.getClass().getDeclaredMethod("listValues",parameter$.getClass());
	 	    String []va=(String[])method.invoke(view,parameter$);
	 	    if(va!=null)
	 	    	for(String v:va)
	 	    		vmodel.addElement(v);
		}catch(Exception e){
			Logger.getLogger(JViewPanel.class.getName()).severe(e.toString());
		}
	}
/*	
public  String[] select(Entigrator entigrator,Properties params){
		try{
			if(debug)
	 			System.out.println("JViewPanel.select:SELECT");
			if(debug)
	 			System.out.println("JViewPanel.select:view="+entityKey$);
			File viewHome=new File(entihome$+"/"+entityKey$);
			File classFile=new File(entihome$+"/"+entityKey$+"/"+entityKey$+".class");
			if(!classFile.exists())
				return null;
			if(debug)
	 			System.out.println("JViewPanel.select:1");
			URL url = viewHome.toURI().toURL();
		    URL[] urls = new URL[]{url};
		    ClassLoader parentLoader = JMainConsole.class.getClassLoader();
		    URLClassLoader cl = new URLClassLoader(urls,parentLoader);
		  Class<?> cls = cl.loadClass(entityKey$);
		  Object obj=cls.newInstance();
		  Method method = obj.getClass().getDeclaredMethod("select",Entigrator.class,Properties.class);
 	      Object value=method.invoke(obj,entigrator);
 	     
 	      String[] sa=(String[])value;
 	     if(debug)
	 			System.out.println("JViewPanel.select:sa="+sa.length);
 	      //String []ea=entity.elementList("exclude");
 	      return sa;
 	       
		}catch(Exception e){
			Logger.getLogger(JViewPanel.class.getName()).severe(e.toString());
		}
		return null;
	}
	*/
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
			System.out.println("JViewPanel.instantiate:locator="+locator$);
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
			entity=entigrator.getEntityAtKey(entityKey$);
            entityLabel$=entity.getProperty("label");
            if(contentTable!=null)
            	contentTable.setModel(new DefaultTableModel());
            if(parametersTable!=null)
            	parametersTable.setModel(new DefaultTableModel());
            initView();
            initValues();
    		initParameters();
    		displayParameters();
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
		return "View";
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
		return "view panel";
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
		return ViewHandler.class.getName();
	}
	/**
	 * Get the type of the entity for the facet.
	 * @return the entity type.
	 */
	@Override
	public String getEntityType() {
		return "view";
	}
	/**
	 * Get facet icon as a Base64 string. 
	 * @return the icon string.
	 */
	@Override
	public String getCategoryIcon(Entigrator entigrator) {
		return Support.readHandlerIcon(null,getClass(), "view.png");
	}
	/**
	 * Get category title for entities having the facet type.
	 * @return the category title.
	 */

	@Override
	public String getCategoryTitle() {
		return "Views";
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
    	  ViewHandler vh=new ViewHandler();
    	  String vh$=vh.getLocator();
    	  vh$=Locator.append(vh$, Entigrator.ENTIHOME, entihome$);
    	  vh$=Locator.append(vh$, EntityHandler.ENTITY_KEY, entityKey$);
    	  vh$=Locator.append(vh$, EntityHandler.ENTITY_LABEL, entityLabel$);
          vh.instantiate(vh$);
          vh.adaptClone(entigrator);
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
	    	  ViewHandler vh=new ViewHandler();
	    	  String vh$=vh.getLocator();
	    	  vh$=Locator.append(vh$, Entigrator.ENTIHOME, entihome$);
	    	  vh$=Locator.append(vh$, EntityHandler.ENTITY_KEY, entityKey$);
	    	  vh$=Locator.append(vh$, EntityHandler.ENTITY_LABEL, entityLabel$);
	          vh.instantiate(vh$);
	          vh.adaptRename(entigrator);
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
		    	String viewHandler$=ViewHandler.class.getName();
		    	if(entity.getElementItem("fhandler", viewHandler$)!=null){
					entity.putElementItem("jfacet", new Core(null,viewHandler$,JViewFacetOpenItem.class.getName()));
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
		    teLocator$=Locator.append(teLocator$, JTextEditor.TEXT_TITLE,"New view");
		    String text$="NewView"+Identity.key().substring(0, 4);
		    teLocator$=Locator.append(teLocator$, JTextEditor.TEXT,text$);
		    JViewPanel qp=new JViewPanel();
		    String qpLocator$=qp.getLocator();
		    qpLocator$=Locator.append(qpLocator$, Entigrator.ENTIHOME,entihome$);
		    qpLocator$=Locator.append(qpLocator$, EntityHandler.ENTITY_KEY,entityKey$);
		    qpLocator$=Locator.append(qpLocator$, BaseHandler.HANDLER_METHOD,"response");
		    qpLocator$=Locator.append(qpLocator$, JRequester.REQUESTER_ACTION,ACTION_CREATE_VIEW);
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
			if(ACTION_CREATE_VIEW.equals(action$)){
				String entihome$=locator.getProperty(Entigrator.ENTIHOME);
				String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
				String text$=locator.getProperty(JTextEditor.TEXT);
				Entigrator entigrator=console.getEntigrator(entihome$);  
				Sack view=entigrator.ent_new("view", text$);
				   view=entigrator.ent_assignProperty(view, "view", view.getProperty("label"));
				   view.putAttribute(new Core(null,"icon","viewy.png"));
				   
				   view.createElement("fhandler");
				   view.putElementItem("fhandler", new Core(null,ViewHandler.class.getName(),null));
					view.putElementItem("fhandler", new Core(null,FolderHandler.class.getName(),null));
					view.createElement("jfacet");
					view.putElementItem("jfacet", new Core(JFolderFacetAddItem.class.getName(),FolderHandler.class.getName(),JFolderFacetOpenItem.class.getName()));
					view.putElementItem("jfacet", new Core(null,ViewHandler.class.getName(),JViewFacetOpenItem.class.getName()));
					entigrator.save(view);
					entigrator.ent_assignProperty(view, "view", text$);
					entigrator.ent_assignProperty(view, "folder", text$);
					entigrator.saveHandlerIcon(getClass(), "view.png");
				   entigrator.saveHandlerIcon(JEntitiesPanel.class, "view.png");
				   entityKey$=view.getKey();
				   File folderHome=new File(entihome$+"/"+entityKey$);
					if(!folderHome.exists())
					    folderHome.mkdir();
					createSource(entihome$,entityKey$);
					createProjectFile(entihome$,entityKey$);
					createClasspathFile(entihome$,entityKey$);
				   JViewPanel qp=new JViewPanel();
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
/*
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
			System.out.println("JViewPanel. initElementSelector:1");
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
					System.out.println("JViewPanel. initElementSelector:s="+s);
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
		System.out.println("JViewPanel. initElementSelector:element scope:"+elementScope.size());
	}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
	}
}
*/
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
/*
private void initComponentSelector(){
	
	
	queryScope=new ArrayList<String>(Arrays.asList(select()));
	DefaultComboBoxModel<String> model=new DefaultComboBoxModel<String>();
	
	String[]sa=select();
	if(sa==null){
		componentComboBox.setModel(model);
		return;
	}
	 if(debug)
			System.out.println("JViewPanel. initComponentSelector:1");
  
	try{
		Entigrator entigrator=console.getEntigrator(entihome$);
		ArrayList <String>sl=new ArrayList<String>();
		Sack member;
		String entityType$;
		String memberType$;
        String[] ca;
       // if(!entity.existsElement("header.container"))
       // 	entity.createElement("header.container");
       // else
       // 	entity.clearElement("header.container");
		for(String s:sa){
			 if(debug)
					System.out.println("JViewPanel. initComponentSelector:s="+s);
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
		//System.out.println("JViewPanel. initComponentSelector:scope="+scope.size());
	    entigrator.replace(entity);
	    initElementSelector();
	}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
	}
}
*/
	/*
private void initItemNameFieldSelector(){
    DefaultComboBoxModel<String> model=new DefaultComboBoxModel<String>();
    model.addElement("name");
    model.addElement("type");
    itemNameFieldComboBox.setModel(model);
}
*/
	/*
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
			System.out.println("JViewPanel:initItemNameSelector:"+ee.toString());	
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
}
*/
	/*
private void initItemValueSelector(){
    DefaultComboBoxModel<String> model=new DefaultComboBoxModel<String>();
    itemValueComboBox.setModel(model);
    if(debug)
		 System.out.println("JViewPanel:initItemValueSelector:BEGIN");
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
		 System.out.println("JViewPanel:initItemValueSelector:element scope="+sa.length);
    ArrayList <String>sl=new ArrayList<String>();
	Sack entity;
    String[] ia;
    
    String value$;
    Core item;
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
*/
private void setParameter(){
	
	try{
		 if(debug)
			 System.out.println("JViewPanel:setParameter:BEGIN");
		 Entigrator entigrator=console.getEntigrator(entihome$);
			 String parameter$=(String)parameterComboBox.getSelectedItem();
		 String value$=(String)valueComboBox.getSelectedItem();
		 if(parameter$!=null&&value$!=null){
		if(!entity.existsElement("parameter"))
				entity.createElement("parameter");
		 entity.putElementItem("parameter", new Core(null,parameter$,value$));
	    
		 }
		 entigrator.replace(entity);
	    displayParameters();
	 }catch(Exception e){
	    	Logger.getLogger(getClass().getName()).severe(e.toString());
    }
   
}
private void displayParameters(){
	
	try{
		 if(debug)
			 System.out.println("JViewPanel:displayParameters:BEGIN");
		 DefaultTableModel model=new DefaultTableModel();
	Core[] ca=entity.elementGet("parameter");
	if(debug)
		 System.out.println("JViewPanel:displayParameters:ca="+ca.length);
	   if(ca!=null){
		   if(ca.length>1)
		    ca=Core.sortAtName(ca);
		   Vector <String>n=new Vector<String>();
		   ArrayList <String>sl=new ArrayList<String>();
		   for(int i=0;i<ca.length;i++){
			   n.add(ca[i].name);
			   sl.add(ca[i].value);
		   }
		   model.setColumnIdentifiers(n);
		   String[] sa=sl.toArray(new String[0]);
		   model.addRow(sa);
		  // if(debug)
		//		 System.out.println("JViewPanel:displayParameters:sa="+sa.length);
		   parametersTable.setModel(model);
	   }
	 }catch(Exception e){
	    	Logger.getLogger(getClass().getName()).severe(e.toString());
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

private static void createClasspathFile(String entihome$,String viewKey$){
	try{
	//	System.out.println("JProcedurePanel:createClasspathFile.procedure key="+procedureKey$);
		File classpath=new File(entihome$+"/"+viewKey$+"/.classpath");
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
		Logger.getLogger(JViewPanel.class.getName()).severe(e.toString());
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
		Logger.getLogger(JViewPanel.class.getName()).severe(e.toString());
	}
}
private static void createSource(String entihome$,String viewKey$){

	try{
		System.out.println("JProcedurePanel:createView:view key="+viewKey$);
		File viewJava=new File(entihome$+"/"+viewKey$+"/"+viewKey$+".java");
		if(!viewJava.exists())
			viewJava.createNewFile();
		
		 FileOutputStream fos = new FileOutputStream(viewJava, false);
		 Writer writer = new OutputStreamWriter(fos, "UTF-8");
		 writer.write("import java.util.logging.Logger;\n");
		 writer.write("import gdt.data.store.Entigrator;\n");
		 writer.write("import gdt.jgui.console.JMainConsole;\n");
		 writer.write("import gdt.jgui.entity.view.View;\n");
		 
		 writer.write("public class "+viewKey$+"  implements View {\n");
		 writer.write("private final static String ENTITY_KEY=\""+viewKey$+"\";\n");
		 writer.write("@Override\n");
		 writer.write("public String[] select(Entigrator entigrator){\n");
		 writer.write("try{\n");
		 writer.write("//Do NOT change this section of the code\n"); 
		 writer.write("String [] sa;\n");
		 writer.write("// Put view code here\n");
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
		Logger.getLogger(JViewPanel.class.getName()).severe(e.toString());
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
			String sortColumnName$=locator.getProperty(SORT_COLUMN_NAME);
			String entityKey$=entigrator.indx_keyAtLabel(entityLabel$);
		    Sack  view=entigrator.getEntityAtKey(entityKey$);
			 StringBuffer sb=new StringBuffer();
			 sb.append("<table style=\"text-align: left;  background-color: transparent;\"  border=\"1\" cellpadding=\"2\" cellspacing=\"2\">");
			 sb.append(getWebHeader(entigrator,view));
			 sb.append(getWebItems(entigrator,view,sortColumnName$));
	         sb.append("</table>"); 
			return sb.toString(); 
		}catch(Exception e){
	        Logger.getLogger(JViewPanel.class.getName()).severe(e.toString());
		}
		return null;
}
private static String getWebHeader(Entigrator entigrator,Sack entity){
	try{
		File viewHome=new File(entigrator.getEntihome()+"/"+entity.getKey());
		URL url = viewHome.toURI().toURL();
	    URL[] urls = new URL[]{url};
	    ClassLoader parentLoader = JMainConsole.class.getClassLoader();
	    URLClassLoader cl = new URLClassLoader(urls,parentLoader);
	  Class<?> cls = cl.loadClass(entity.getKey());
	  View view=(View)cls.newInstance();
	  StringBuffer sb=new StringBuffer();
		sb.append("<tr>");
		sb.append("<td><strong>number</strong></td></td>");
	  DefaultTableModel model=view.select(entigrator);
      int cnt=model.getColumnCount();
      for(int i=1;i<cnt;i++)
    	 // sb.append("<td><strong>"+model.getColumnName(i)+"</strong></td>");
    	  sb.append("<td  onclick=\"headerClick('"+model.getColumnName(i)+"')\" style=\"text-decoration:underline;\"><strong>"+model.getColumnName(i)+"</strong></td>");
      sb.append("</tr>");
		return sb.toString();
	}catch(Exception e){
		 Logger.getLogger(JViewPanel.class.getName()).severe(e.toString());
	}
	return null;
}
public static String getWebItems(Entigrator entigrator,Sack entity,String sortColumnName$){
	try{
	if(debug)
		System.out.println("JViewPanel:getViewItems: sort column="+sortColumnName$);
		File viewHome=new File(entigrator.getEntihome()+"/"+entity.getKey());
	URL url = viewHome.toURI().toURL();
    URL[] urls = new URL[]{url};
    ClassLoader parentLoader = JMainConsole.class.getClassLoader();
    URLClassLoader cl = new URLClassLoader(urls,parentLoader);
  Class<?> cls = cl.loadClass(entity.getKey());
  View view=(View)cls.newInstance();
  StringBuffer sb=new StringBuffer();
 DefaultTableModel model=view.select(entigrator);
 JTable table=new JTable(model);
 Sack id2key=entigrator.getEntityAtKey(entigrator.indx_keyAtLabel("id2key"));
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
	  TableRowSorter sorter=JViewPanel.getSorter(view,model);
	  sorter.setSortKeys(sortKeys);
	  table.setRowSorter(sorter);
	  sorter.sort();
	  int indexOfNoColumn = 0;
      for (int i = 0; i < table.getRowCount(); i++) {
      	table.setValueAt(String.valueOf(i + 1), i, indexOfNoColumn);
      }
	  if(debug)
			System.out.println("JViewPanel:getViewItems: sort column="+sortColumnName$);
		
  }
  int rows= table.getRowCount();
  String columnName$;
  String valueId$;
  String valueKey$=null;
  String valueLabel$;
  for(int i=0;i<rows;i++){
	  sb.append("<tr>");
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
				  System.out.println("JViewPanel:getWebItems: column name="+columnName$+" id="+valueId$+" key="+valueKey$+" label="+valueLabel$);
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
	return JViewFacetOpenItem.class.getName();
}
@Override
public String getFacetIcon() {
	// TODO Auto-generated method stub
	return "view.png";
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
 public static class IntComparator implements Comparator<String> {
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
public static  class LongComparator implements Comparator<String> {
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
 public static class FloatComparator implements Comparator<String> {
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
public static class DoubleComparator implements Comparator<String> {
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

 private void setSorter(){
	 DefaultTableModel model=(DefaultTableModel)contentTable.getModel();
    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
	contentTable.setRowSorter(sorter);
		sorter.addRowSorterListener(new RowSorterListener() {
			    @Override
			    public void sorterChanged(RowSorterEvent evt) {
			    	if(debug)
						System.out.println("JViewPanel:show content:sorter changed="+evt.toString());
			    	int indexOfNoColumn = 0;
			        for (int i = 0; i < contentTable.getRowCount(); i++) {
			        	contentTable.setValueAt(i + 1, i, indexOfNoColumn);
			        }
			    }
			});
			
		//sorter.setComparator(1,new IntComparator());
		int cnt=model.getColumnCount();
//		String columnName$;
		String columnType$;
		for(int i=0;i<cnt;i++){
			columnType$=view.getColumnType(model.getColumnName(i));
			//System.out.println("JQueryPanel:setSorter: column name="+model.getColumnName(i));
			if("int".equals(columnType$)){
 				//if(debug)
 			  	//	System.out.println("JQueryPanel:showContent:set int comparator: i="+i);
 				 sorter.setComparator(i,new IntComparator());
 			 }
 			 if("long".equals(columnType$))
 				 sorter.setComparator(i,new LongComparator());
 			 if("float".equals(columnType$))
 				 sorter.setComparator(i,new FloatComparator());
 			 if("double".equals(columnType$))
 				 sorter.setComparator(i,new DoubleComparator());
		}
	    
 }
 //
 private static TableRowSorter getSorter(View view,DefaultTableModel model){
    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
		int cnt=model.getColumnCount();
		String columnType$;
		for(int i=0;i<cnt;i++){
			columnType$=view.getColumnType(model.getColumnName(i));
			if("int".equals(columnType$)){
 				 sorter.setComparator(i,new IntComparator());
 			 }
 			 if("long".equals(columnType$))
 				 sorter.setComparator(i,new LongComparator());
 			 if("float".equals(columnType$))
 				 sorter.setComparator(i,new FloatComparator());
 			 if("double".equals(columnType$))
 				 sorter.setComparator(i,new DoubleComparator());
		}
	    return sorter;
 }
}
