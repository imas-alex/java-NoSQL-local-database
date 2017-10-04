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
import java.sql.Timestamp;
import java.util.ArrayList;

import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.Stack;
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
import gdt.jgui.entity.JEntitiesPanel;
import gdt.jgui.entity.JEntityFacetPanel;
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
	static boolean debug=false;
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
							if(view!=null)
							entigrator.ent_alter(view);
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
	protected void initView(){
		try{
			if(debug)
	 			System.out.println("JViewPanel.initView:BEGIN");
			File viewHome=new File(entihome$+"/"+entityKey$);
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
        	initView();
			Method method = view.getClass().getDeclaredMethod("select",Entigrator.class);
	 	    return (DefaultTableModel)method.invoke(view,console.getEntigrator(entihome$));
		}catch(Exception e){
			Logger.getLogger(JViewPanel.class.getName()).severe(e.toString());
		}
		return null;
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
			final Entigrator entigrator=console.getEntigrator(entihome$);
			entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
			if(entityLabel$==null)
				entityLabel$=entigrator.indx_getLabel(entityKey$);
			 if(Locator.LOCATOR_TRUE.equals(locator.getProperty(JFacetRenderer.ONLY_ITEM)))
				 return this;
			entity=entigrator.getEntityAtKey(entityKey$);
            entityLabel$=entity.getProperty("label");
            if(parametersTable!=null)
            	parametersTable.setModel(new DefaultTableModel());
            initView();
            initValues();
    		 contentTable.addMouseListener(new java.awt.event.MouseAdapter() {
    		   	    @Override
    		   	    public void mouseClicked(java.awt.event.MouseEvent evt) {
    		   	       try{
    		   	    	Sack id2key =entigrator.getEntityAtKey(entigrator.indx_keyAtLabel("id2key"));
    		   	    	int row = contentTable.rowAtPoint(evt.getPoint());
    		   	        int col = contentTable.columnAtPoint(evt.getPoint());
    		   	        String value$=(String)contentTable.getValueAt(row, col);
    		   	        String header$=contentTable.getColumnName(col);
    	   	            //System.out.println("JViewPanel:cell click:row="+row+" column="+col+" value="+value$+" header="+header$);
    	   	            if(id2key==null)
    	   	            	return;
    	   	            String entityKey$=id2key.getElementItemAt(header$, value$);
    	   	            if(entityKey$==null)
    	   	            	return;
    	   	         JEntityFacetPanel efp=new  JEntityFacetPanel();
    		         String efpLocator$=efp.getLocator();
    		         efpLocator$=Locator.append(efpLocator$, Entigrator.ENTIHOME, entihome$);
    		         efpLocator$=Locator.append(efpLocator$, EntityHandler.ENTITY_KEY, entityKey$);
    		         JConsoleHandler.execute(JViewPanel.this.console, efpLocator$);
    		   	   
    		   	       }catch(Exception e){
    		   	    	   System.out.println("ViewPanel:instantiate:mouse clicked:"+e.toString());
    		   	       }
    		   	        }
    		   	    });
    		contentTable.setModel(select());
    		setSorter();
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
		    	if(entity.getElementItem("fhandler", viewHandler$)!=null)
		    		return;
		    	if(entity.getElementItem("jfacet", viewHandler$)==null){
					entity.putElementItem("jfacet", new Core(null,viewHandler$,JViewFacetOpenItem.class.getName()));
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
					entigrator.ent_alter(view);
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
*/
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
/**
 * No action
 */
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
/**
 * Get table rows as an array of strings in http form.
 * @param entigrator the entigrator
 * @param entity the query entity
 * @param sortColumnName$ sort rows by this column
 * @return table rows as a string array.
 */	
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


/**
 * Get the name of the class  for the facet open item.
 * @return the name of the class.
 */
@Override
public String getFacetOpenItem() {
	
	return JViewFacetOpenItem.class.getName();
}
/**
 * Get the name of the icon file for the facet.
 * @return the name of file.
 */
@Override
public String getFacetIcon() {
	return "view.png";
}
  /**
  * The  comparator for date strings 
  * @author imasa.
  *
  */
 public static class DateComparator  implements Comparator<String> {
	    
	 @Override
	 /**
		 * Compare two date strings.
		 * @param a the first  date string
		 * @param b the second date string
		 * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
		 */
	    public int compare(String a, String b) {
	        try{
	    	Timestamp a1=DateHandler.getTimestamp(a,null);
	    	Timestamp b1=DateHandler.getTimestamp(b,null);
	        return a1.compareTo(b1);
	        }catch(Exception e){
	        	return 0;
	        }
	    }
	} 
 /**
  * The  comparator for integers strings 
  * @author imasa.
  *
  */
 public static class IntComparator implements Comparator<String> {
	    @Override
	    /**
		 * Compare two integer strings.
		 * @param a the first  integer string
		 * @param b the second integer string
		 * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
		 */
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
 /**
  * The  comparator for integers strings 
  * @author imasa.
  *
  */
public static  class LongComparator implements Comparator<String> {
	    @Override
	    /**
		 * Compare two long strings.
		 * @param a the first  long string
		 * @param b the second long string
		 * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
		 */
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
/**
 * The  comparator for  strings 
 * @author imasa.
 *
 */
public static  class StringComparator implements Comparator<String> {
	    @Override
	    /**
		 * Compare two long strings.
		 * @param a the first  long string
		 * @param b the second long string
		 * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
		 */
	    public int compare(String a, String b) {
	        try{
	    	
	        return a.compareTo(b);
	        }catch(Exception e){
	        	return 0;
	        }
	    }
	}
/**
 * The  comparator for float strings 
 * @author imasa.
 *
 */
 public static class FloatComparator implements Comparator<String> {
	    @Override
	    /**
		 * Compare two long strings.
		 * @param a the first  float string
		 * @param b the second float string
		 * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
		 */
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
 /**
  * The  comparator for double strings 
  * @author imasa.
  *
  */

public static class DoubleComparator implements Comparator<String> {
	    @Override
	    /**
		 * Compare two double strings.
		 * @param a the first  double string
		 * @param b the second double string
		 * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
		 */

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
public static class RowComparator implements Comparator<String[]> {
	 
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
public static boolean containsRow(DefaultTableModel model,String[] row){
	try{
	if(model==null)
		 return false;
	 RowComparator rowComparator=new RowComparator();
	 int rowCnt =model.getRowCount();
	 int colCnt=model.getColumnCount();
	 String[] currentRow=new String[colCnt];
	// for(String[] r:scope){
	 for(int i=0;i<rowCnt;i++){
		 
		 for(int j=0;j<colCnt;j++)
		  currentRow [j]=(String)model.getValueAt(i,j);  	 
		  if(rowComparator.compare(currentRow, row)==0){
			  return true;
		  }
	 }
	}catch(Exception e){
		
	}
	
	 return false;
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
			
		int cnt=model.getColumnCount();
		String columnType$;
		for(int i=0;i<cnt;i++){
			columnType$=view.getColumnType(model.getColumnName(i));
			//System.out.println("JQueryPanel:setSorter: column name="+model.getColumnName(i));
			if("int".equals(columnType$))
 				 sorter.setComparator(i,new IntComparator());
 			 
 			 if("long".equals(columnType$))
 				 sorter.setComparator(i,new LongComparator());
 			 if("float".equals(columnType$))
 				 sorter.setComparator(i,new FloatComparator());
 			 if("double".equals(columnType$))
 				 sorter.setComparator(i,new DoubleComparator());
 			if("date".equals(columnType$))
				 sorter.setComparator(i,new JViewPanel.DateComparator());
		  
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
 			if("date".equals(columnType$))
				 sorter.setComparator(i,new JViewPanel.DateComparator());
		  
		}
	    return sorter;
 }
}
