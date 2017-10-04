package gdt.jgui.entity.group;


import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.Stack;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.GroupHandler;
import gdt.data.entity.UsersHandler;
import gdt.data.entity.facet.ExtensionHandler;

import gdt.data.grain.Core;
import gdt.data.grain.Identity;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JFacetRenderer;
import gdt.jgui.console.JItemPanel;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.console.JItemsListPanel.ItemPanelComparator;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.entity.JEntityPrimaryMenu;
import gdt.jgui.entity.JReferenceEntry;
import gdt.jgui.entity.bookmark.JBookmarksEditor;
import gdt.jgui.entity.query.JQueryPanel;
import gdt.jgui.entity.users.JUserEditor;
import gdt.jgui.tool.JTextEditor;

public class JGroupEditor extends JPanel implements JContext, JFacetRenderer,JRequester{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String GROUP_KEY="group key";
	public static final String GROUP_MODE="group mode";
	public static final String GROUP_MODE_USERS="group mode users";
	public static final String GROUP_MODE_RESTRICTIONS="group mode restrictions";
	//private static final String GROUP_MODE_RESTRICTION = null;
	//public static final String ACTION_NEW_ENTITY="action new entity";
	
	private String entihome$;
	private String entityKey$;
	private String entityLabel$;
	private String user$;
	private String locator$;
	private JMainConsole console;
	private String mode$;
	private JComboBox<String> itemComboBox; 
	
	private JPanel itemsPanel;
	private JButton itemAdd;
	private JScrollPane scrollPane;
	boolean debug=false;
	public JGroupEditor(){
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{100, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0};
		setLayout(gridBagLayout);

		itemAdd = new JButton();
		
		itemAdd.addActionListener(new ActionListener() {          
			@Override
			public void actionPerformed(ActionEvent arg0) {
				addItem();
				
			}
		});
		 		
		GridBagConstraints gbc_lbl = new GridBagConstraints();
		gbc_lbl.insets = new Insets(5, 5, 5, 5);
		//gbc_lbl.insets = new Insets(0, 0, 0, 0);
		gbc_lbl.gridx = 1;
		gbc_lbl.gridy = 0;
		gbc_lbl.anchor=GridBagConstraints.FIRST_LINE_START;
		add(itemAdd, gbc_lbl);
		
		itemComboBox = new JComboBox<String>();
		GridBagConstraints gbc_ComboBox = new GridBagConstraints();
		gbc_ComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_ComboBox.insets = new Insets(5, 5, 5, 5);
		gbc_ComboBox.gridx = 0;
		gbc_ComboBox.gridy = 0;
		gbc_ComboBox.anchor=GridBagConstraints.FIRST_LINE_START;
		add(itemComboBox, gbc_ComboBox);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.weighty = 1.0;
		gbc_panel.insets = new Insets(0, 0, 0, 5);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx =0;
		gbc_panel.gridy = 1;
		gbc_panel.gridwidth = 2;
		add(panel, gbc_panel);
		//add(panel);
		scrollPane = new JScrollPane();
		panel.setLayout(new BorderLayout());
		panel.add(scrollPane, BorderLayout.CENTER);
		//panel.add(scrollPane);
		itemsPanel = new JPanel();
		itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
		scrollPane.getViewport().add(itemsPanel);
    	revalidate();
    	repaint();

	}
	@Override
	public JPanel getPanel() {
		
		return this;
	}

	@Override
	public JMenu getContextMenu() {
		final JMenu menu=new JMenu("Context");
		menu.addMenuListener(new MenuListener(){
			@Override
			public void menuSelected(MenuEvent e) {
//			System.out.println("EntityEditor:getConextMenu:menu selected");
		    menu.removeAll(); 
		    if(mode$==null||GROUP_MODE_USERS.equals(mode$)){
		    JMenuItem restrictionsItem = new JMenuItem("Restrictions");
		    restrictionsItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JGroupEditor ge=new JGroupEditor();
					locator$=ge.getLocator();
					locator$=Locator.append(locator$, EntityHandler.ENTITY_KEY, entityKey$);
					locator$=Locator.append(locator$, Entigrator.ENTIHOME, entihome$);
					locator$=Locator.append(locator$, GROUP_MODE, GROUP_MODE_RESTRICTIONS);
					JConsoleHandler.execute(console, locator$);
				}
			} );
			menu.add(restrictionsItem);
		    }else{
		    	JMenuItem usersItem = new JMenuItem("Users");
			    usersItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						JGroupEditor ge=new JGroupEditor();
						locator$=ge.getLocator();
						locator$=Locator.append(locator$, EntityHandler.ENTITY_KEY, entityKey$);
						locator$=Locator.append(locator$, Entigrator.ENTIHOME, entihome$);
						locator$=Locator.append(locator$, GROUP_MODE, GROUP_MODE_USERS);
						JConsoleHandler.execute(console, locator$);
					}
				} );
				menu.add(usersItem);
		    	
		    }
		    if(hasSelectedItems()){
		    	JMenuItem deleteItem = new JMenuItem("Delete");
			    deleteItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try{
							int response = JOptionPane.showConfirmDialog(console.getContentPanel(), "Delete ?", "Confirm",
							        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						   if (response == JOptionPane.YES_OPTION) {
							  String[] sa=listSelectedItems();
							  if(sa==null)
								  return;
							  
							  Entigrator entigrator=console.getEntigrator(entihome$);
							  Sack group=entigrator.getEntityAtKey(entityKey$);
							  if(mode$==null)
								  mode$=GROUP_MODE_USERS;
							 if(GROUP_MODE_USERS.equals(mode$)){
							  String user$;
							  for(String aSa:sa){
								  user$=Locator.getProperty(aSa, Locator.LOCATOR_TITLE);
			                      group.removeElementItem("user",user$);
							  }
							  entigrator.ent_alter(group);
							 }else{
								 String restriction$=null;
								  for(String aSa:sa){
									  restriction$=Locator.getProperty(aSa, Locator.LOCATOR_TITLE);
				                      group.removeElementItem("restriction",restriction$);
								  }
								  entigrator.ent_alter(group);
							 }
							  JGroupEditor ge=new JGroupEditor();
								String geLocator$=ge.getLocator();
								geLocator$=Locator.append(geLocator$, Entigrator.ENTIHOME,entihome$);
								geLocator$=Locator.append(geLocator$, EntityHandler.ENTITY_KEY,entityKey$);
								geLocator$=Locator.append(geLocator$, GROUP_MODE,mode$);
								JConsoleHandler.execute(console, geLocator$);
						   }
						}catch(Exception ee){
							Logger.getLogger(getClass().getName()).severe(e.toString());
						}
					}
				} );
				menu.add(deleteItem);
		    }
			JMenuItem doneItem = new JMenuItem("Done");
			doneItem.addActionListener(new ActionListener() {
			//	@Override
				public void actionPerformed(ActionEvent e) {
					console.back();
				 
				   	}
			} );
			menu.add(doneItem);
			
			}

			@Override
			public void menuDeselected(MenuEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void menuCanceled(MenuEvent e) {
				// TODO Auto-generated method stub
				
			}
		
	});
		return menu;
	}
	

	@Override
	public String getLocator() {
		try{
			Properties locator=new Properties();
			locator.setProperty(BaseHandler.HANDLER_CLASS,JGroupEditor.class.getName());
			locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
			 locator.setProperty( JContext.CONTEXT_TYPE,getType());
			locator.setProperty(Locator.LOCATOR_TITLE,getTitle());
			locator.setProperty(BaseHandler.HANDLER_LOCATION,UsersHandler.EXTENSION_KEY);
			if(entityLabel$!=null){
				locator.setProperty(EntityHandler.ENTITY_LABEL,entityLabel$);
			}
			if(entityKey$!=null)
				locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
			
			if(entihome$!=null){
				locator.setProperty(Entigrator.ENTIHOME,entihome$);
				//Entigrator entigrator=console.getEntigrator(entihome$);
			}
			
			if(mode$!=null){
				locator.setProperty(GROUP_MODE,mode$);
				//Entigrator entigrator=console.getEntigrator(entihome$);
			}
			locator.setProperty( Locator.LOCATOR_ICON_CONTAINER, Locator.LOCATOR_ICON_CONTAINER_CLASS);
	    	locator.setProperty( Locator.LOCATOR_ICON_CLASS, getClass().getName());
	    	locator.setProperty( Locator.LOCATOR_ICON_FILE, "group.png");
	    	locator.setProperty( Locator.LOCATOR_ICON_LOCATION, UsersHandler.EXTENSION_KEY);
			return Locator.toString(locator);
			}catch(Exception e){
	        Logger.getLogger(getClass().getName()).severe(e.toString());
	        return null;
			}
	}

	@Override
	public JContext instantiate(JMainConsole console, String locator$) {
		try{
			 this.console=console;
			 this.locator$=locator$;
			 Properties locator=Locator.toProperties(locator$);
			 entihome$=locator.getProperty(Entigrator.ENTIHOME);
			 Entigrator entigrator=console.getEntigrator(entihome$);
 			 entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
 			 entityLabel$=entigrator.indx_getLabel(entityKey$);
 			// mode$=locator.getProperty(GROUP_MODE);
			 setItemSelector();
			 return this;
        }catch(Exception e){
        Logger.getLogger(getClass().getName()).severe(e.toString());
        }
        return null;
	}
private void setItemSelector(){
	// if(debug)
	//	 System.out.println("JGroupEditor:setItemSelector:locator="+locator$);
	DefaultComboBoxModel<String> model=new  DefaultComboBoxModel<String>();
	try{
		Properties locator=Locator.toProperties(locator$);
		Entigrator entigrator=console.getEntigrator(entihome$);
		entihome$=locator.getProperty(Entigrator.ENTIHOME);
		entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		Sack group=entigrator.getEntityAtKey(entityKey$);
		mode$=locator.getProperty(GROUP_MODE);
		itemsPanel.removeAll();
		if(mode$==null||GROUP_MODE_USERS.equals(mode$)){
		
			 itemAdd.setText("Add user");
		
			 Sack users=entigrator.getEntityAtKey(entigrator.indx_keyAtLabel("users"));
		
			 String[] ua=users.elementList("user");
		
			 String[] ga=group.elementList("user");
			 
			 if(ga==null||ga.length<1){
				if(ua!=null)
				   model=new DefaultComboBoxModel<String>(ua);
				itemComboBox.setModel(model);
			 }else{
			 ArrayList<String>sl=new ArrayList<String>();
			 boolean skip;
			 for(String u:ua){
				 skip=false;
				 for(String g:ga)
					 if(g.equals(u)){
						 skip=true;
						 break;
					 }
				 if(!skip)
					 sl.add(u);
			 }
			Collections.sort(sl);
			model=new DefaultComboBoxModel<String>(sl.toArray(new String[0]));
			itemComboBox.setModel(model);
			ArrayList<JItemPanel>ipl=new ArrayList<JItemPanel>();
			JItemPanel ip;
			 JUserEditor userEditor=new JUserEditor();
			 String itemLocator$=userEditor.getLocator();
			 itemLocator$=Locator.append(itemLocator$, Entigrator.ENTIHOME, entihome$);
     		 itemLocator$=Locator.append(itemLocator$,Locator.LOCATOR_CHECKABLE, Locator.LOCATOR_TRUE);
           	ArrayList<String>gl=new ArrayList<String>();
     		ipl.clear(); 
           	for(String g:ga){
     			     if(gl.contains(g))
     			    	 continue;
     			     gl.add(g);
            		 itemLocator$=Locator.append(itemLocator$, JUserEditor.USER_NAME, g);
            		 itemLocator$=Locator.append(itemLocator$, Locator.LOCATOR_TITLE, g);
            		 ip=new JItemPanel(console,itemLocator$);
            		 ipl.add(ip);
            	}
            	Collections.sort(ipl,new ItemPanelComparator());
            	//panel2.removeAll();
            	itemsPanel.removeAll();
        			 for(JItemPanel aIpl:ipl){
        				 itemsPanel.add(aIpl);
        			 }
			 }
			
			 TitledBorder title= BorderFactory.createTitledBorder("Members");
			 scrollPane.setBorder(title);
			 
		 }else{
			 itemAdd.setText("Add restriction");
			 itemsPanel.removeAll();
			 String[] ra=entigrator.indx_listEntities("entity", "restriction");
			 if(debug&&ra!=null)
				 System.out.println("JGroupEditor:setItemSelector:ra="+ra.length);
			 String[] ga=group.elementList("restriction");
			 if(ga==null||ga.length<1){
				   ArrayList <String>ll=new ArrayList<String>();
				   for(String r:ra)
					   ll.add(entigrator.indx_getLabel(r));
				   Collections.sort(ll);
				   model=new DefaultComboBoxModel<String>(ll.toArray(new String[0]));
				   itemComboBox.setModel(model);
			 }else{
			 ArrayList<String>sl=new ArrayList<String>();
			 boolean skip;
			 String restriction$=null;
			 for(String r:ra){
				 skip=false;
				 
				 for(String g:ga){
					 restriction$=entigrator.indx_getLabel(r);
					 if(g.equals(restriction$)){
						 skip=true;
						 break;
					 }
				 }
				 if(!skip&&restriction$!=null)
					 sl.add(restriction$);
			 }
			Collections.sort(sl);
			model=new DefaultComboBoxModel<String>(sl.toArray(new String[0]));
			itemComboBox.setModel(model);
			ArrayList<JItemPanel>ipl=new ArrayList<JItemPanel>();
			JItemPanel ip;
			JUserEditor userEditor=new JUserEditor();
			 String itemLocator$=userEditor.getLocator();
			 itemLocator$=Locator.append(itemLocator$, Entigrator.ENTIHOME, entihome$);
     		 itemLocator$=Locator.append(itemLocator$,Locator.LOCATOR_CHECKABLE, Locator.LOCATOR_TRUE);
           	ArrayList<String>gl=new ArrayList<String>();
     		ipl.clear();
     		String restrictionKey$;
     		Properties itemLocator=new Properties();
     		itemLocator.setProperty(Entigrator.ENTIHOME, entihome$);
     		itemLocator.setProperty( BaseHandler.HANDLER_CLASS, JEntityFacetPanel.class.getName());
     		itemLocator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE );
     		itemLocator.setProperty(Locator.LOCATOR_CHECKABLE, Locator.LOCATOR_TRUE );
           	for(String g:ga){
           		restrictionKey$=group.getElementItemAt("restriction",g);
           		itemLocator.setProperty( Locator.LOCATOR_TITLE, g);
           		itemLocator.setProperty(EntityHandler.ENTITY_KEY,restrictionKey$);
         		 ip=new JItemPanel(console,Locator.toString(itemLocator));
           		 ipl.add(ip);
            	}
            	Collections.sort(ipl,new ItemPanelComparator());
            	//panel2.removeAll();
            	
        			 for(JItemPanel aIpl:ipl){
        				 itemsPanel.add(aIpl);
        			 }
			 }
			
			 TitledBorder title= BorderFactory.createTitledBorder("Restrictions");
			 scrollPane.setBorder(title);
			 itemComboBox.setModel(model);		 
		 }
	 }catch(Exception e){
	        Logger.getLogger(getClass().getName()).severe(e.toString());
	        }
	
}
private void addItem(){
	 if(debug)
		 System.out.println("JGroupEditor:additem:mode="+mode$+" locator="+locator$);
		
	if(mode$==null||GROUP_MODE_USERS.equals(mode$))
		  addUser();
	else
		addRestriction();
}
private void addUser(){
	try{
		if(debug)
			System.out.println("JGroupEditor:addUser:locator="+locator$);
		Properties locator=Locator.toProperties(locator$);
		Entigrator entigrator=console.getEntigrator(entihome$);
		entihome$=locator.getProperty(Entigrator.ENTIHOME);
		entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		Sack group=entigrator.getEntityAtKey(entityKey$);
		String user$=(String)itemComboBox.getSelectedItem();
		if(!group.existsElement("user"))
			group.createElement("user");
		group.putElementItem("user", new Core(null,user$,null));
		entigrator.ent_alter(group);
		JGroupEditor ge=new JGroupEditor();
		String geLocator$=ge.getLocator();
		geLocator$=Locator.append(geLocator$, Entigrator.ENTIHOME,entihome$);
		geLocator$=Locator.append(geLocator$, EntityHandler.ENTITY_KEY,entityKey$);
		JConsoleHandler.execute(console, geLocator$);
		}catch(Exception e){
	        Logger.getLogger(getClass().getName()).severe(e.toString());
	        }	
}
private void addRestriction(){
	try{
		if(debug)
			System.out.println("JGroupEditor:addRestriction:locator="+locator$);
		Properties locator=Locator.toProperties(locator$);
		Entigrator entigrator=console.getEntigrator(entihome$);
		entihome$=locator.getProperty(Entigrator.ENTIHOME);
		entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		Sack group=entigrator.getEntityAtKey(entityKey$);
		String restriction$=(String)itemComboBox.getSelectedItem();
		if(!group.existsElement("restriction"))
			group.createElement("restriction");
		group.putElementItem("restriction", new Core(null,restriction$,entigrator.indx_keyAtLabel(restriction$)));
		entigrator.ent_alter(group);
		JGroupEditor ge=new JGroupEditor();
		String geLocator$=ge.getLocator();
		geLocator$=Locator.append(geLocator$, Entigrator.ENTIHOME,entihome$);
		geLocator$=Locator.append(geLocator$, EntityHandler.ENTITY_KEY,entityKey$);
		geLocator$=Locator.append(geLocator$, GROUP_MODE,GROUP_MODE_RESTRICTIONS);
		JConsoleHandler.execute(console, geLocator$);
		}catch(Exception e){
	        Logger.getLogger(getClass().getName()).severe(e.toString());
	        }	
}
private JItemPanel[] getItems(){
	 
	  try{
		  int cnt=itemsPanel.getComponentCount();
		  JItemPanel[] ipa=new JItemPanel[cnt];
		  for(int i=0;i<cnt;i++)
			  ipa[i]=(JItemPanel)itemsPanel.getComponent(i);
		  return ipa;
	  }catch(Exception e){
		  Logger.getLogger(getClass().getName()).severe(e.toString());
		  return null;
	  }
	 
 }
private boolean hasSelectedItems(){

	   JItemPanel[] ipa=getItems();
	   if(ipa==null)
		   return false;
	   for(int i=0;i<ipa.length;i++)
		   if(ipa[i].isChecked())
		      return true;
	   return false;
}
protected String[] listSelectedItems(){
	   JItemPanel[] ipa=getItems();
	   if(ipa==null)
		   return null;
	   ArrayList<String>sl=new ArrayList<String>();
	   for(int i=0;i<ipa.length;i++)
		   if(ipa[i].isChecked())
		      sl.add(ipa[i].getLocator());
	   return sl.toArray(new String[0]);
}
	@Override
	public String getTitle() {
		if(entityLabel$!=null)
			return entityLabel$;
		else
			return "Group";
			
	}

	@Override
	public String getSubtitle() {

		return entihome$;
	}

	@Override
	public String getType() {
		return "group";
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void activate() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getCategoryTitle() {
		return "Groups";
	}

	@Override
	public void response(JMainConsole console, String locator$) {
		try{
			Properties locator=Locator.toProperties(locator$);
			entihome$=locator.getProperty(Entigrator.ENTIHOME);
			Entigrator entigrator=console.getEntigrator(entihome$);
			String text$=locator.getProperty(JTextEditor.TEXT);
				Sack newEntity=entigrator.ent_new("group", text$);
				newEntity.createElement("fhandler");
				newEntity.putElementItem("fhandler", new Core(null,GroupHandler.class.getName(),UsersHandler.EXTENSION_KEY));
				
				newEntity.createElement("jfacet");
				newEntity.putElementItem("jfacet", new Core(null,GroupHandler.class.getName(),"gdt.jgui.entity.group.JGroupFacetOpenItem"));
				
				newEntity.putAttribute(new Core (null,"icon","group.png"));
				entigrator.ent_alter(newEntity);

				entigrator.ent_assignProperty(newEntity, "group", text$);
				String icons$=entihome$+"/"+Entigrator.ICONS;
				Support.addHandlerIcon(JGroupEditor.class, "group.png", icons$);
				newEntity=entigrator.ent_reindex(newEntity);
				reindex(console, entigrator, newEntity);
				JEntityFacetPanel efp=new JEntityFacetPanel(); 
				String efpLocator$=efp.getLocator();
				efpLocator$=Locator.append(efpLocator$,Locator.LOCATOR_TITLE,newEntity.getProperty("label"));
				efpLocator$=Locator.append(efpLocator$, Entigrator.ENTIHOME, entihome$);
				efpLocator$=Locator.append(efpLocator$, EntityHandler.ENTITY_KEY, newEntity.getKey());
				efpLocator$=Locator.append(efpLocator$, EntityHandler.ENTITY_LABEL, newEntity.getProperty("label"));
				JEntityPrimaryMenu.reindexEntity(console, efpLocator$);
				Stack<String> s=console.getTrack();
				s.pop();
				console.setTrack(s);
				JConsoleHandler.execute(console, efpLocator$);
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		}
		
	}

	@Override
	public String addIconToLocator(String locator$) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFacetHandler() {
		return GroupHandler.class.getName();
	}

	@Override
	public String getEntityType() {
		return "group";
	}

	@Override
	public String getCategoryIcon(Entigrator entigrator) {
		return ExtensionHandler.loadIcon(entigrator, UsersHandler.EXTENSION_KEY, "group.png");
	}

	@Override
	public String getFacetIcon() {
		return "group.png";
	}

	@Override
	public void adaptClone(JMainConsole console, String locator$) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void adaptRename(JMainConsole console, String locator$) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void collectReferences(Entigrator entigrator, String entiyKey$, ArrayList<JReferenceEntry> sl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reindex(JMainConsole console, Entigrator entigrator, Sack entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String newEntity(JMainConsole console, String locator$) {
		if(debug)
			System.out.println("JGroupEditor:newEntity:locator="+locator$);
		JTextEditor textEditor=new JTextEditor();
	    String editorLocator$=textEditor.getLocator();
	    editorLocator$=Locator.append(editorLocator$, JTextEditor.TEXT, "Group"+Identity.key().substring(0,4));
	    editorLocator$=Locator.append(editorLocator$,Locator.LOCATOR_TITLE,"Group entity");
	    JGroupEditor fe=new JGroupEditor();
	    String feLocator$=fe.getLocator();
	    Properties responseLocator=Locator.toProperties(feLocator$);
	    entihome$=Locator.getProperty(locator$,Entigrator.ENTIHOME );
	    if(entihome$!=null)
	      responseLocator.setProperty(Entigrator.ENTIHOME,entihome$);
	   responseLocator.setProperty(BaseHandler.HANDLER_CLASS,JGroupEditor.class.getName());
		responseLocator.setProperty(BaseHandler.HANDLER_METHOD,"response");
		responseLocator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
		responseLocator.setProperty(BaseHandler.HANDLER_METHOD,"response");
		//responseLocator.setProperty(JRequester.REQUESTER_ACTION,ACTION_NEW_ENTITY);
		responseLocator.setProperty(Locator.LOCATOR_TITLE,"Group");
		 String responseLocator$=Locator.toString(responseLocator);
    	//System.out.println("FieldsEditor:newEntity:responseLocator:=:"+responseLocator$);
		String requesterResponseLocator$=Locator.compressText(responseLocator$);
		editorLocator$=Locator.append(editorLocator$,JRequester.REQUESTER_RESPONSE_LOCATOR,requesterResponseLocator$);
		editorLocator$=Locator.append(editorLocator$,Entigrator.ENTIHOME,entihome$);JConsoleHandler.execute(console,editorLocator$); 
		return editorLocator$;
	}

	@Override
	public String getFacetOpenItem() {
		return JGroupFacetOpenItem.class.getName();
	}
	
	
}