package gdt.jgui.entity.index;
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
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Stack;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.apache.commons.codec.binary.Base64;
import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.facet.IndexHandler;
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
import gdt.jgui.console.ReloadDialog;
import gdt.jgui.console.WContext;
import gdt.jgui.console.WUtils;
import gdt.jgui.entity.JEntitiesPanel;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.entity.JEntityPrimaryMenu;
import gdt.jgui.entity.JEntityStructurePanel;
import gdt.jgui.entity.JReferenceEntry;
import gdt.jgui.entity.folder.JFolderPanel;
import gdt.jgui.entity.webset.JWeblinksPanel;
import gdt.jgui.entity.webset.JWebsetFacetOpenItem;
import gdt.jgui.tool.JIconSelector;
import gdt.jgui.tool.JTextEditor;
/**
 * This class represents the index context. The index
 * panel orders links to entities or files in a hierarchical
 * tree structure.
 */
public class JIndexPanel extends JPanel implements JContext , JFacetRenderer,JRequester{

	private static final long serialVersionUID = 1L;
	private static final String INDEX="Index";
	private static final String SELECTION="selection";
	public static final String NODE_KEY="node key";
	private static final String INDEX_KEY="index key";
	public static final String NODE_GROUP_KEY="node group key";
	public static final String NODE_TYPE="node type";
	private static final String NODE_TYPE_ROOT="node type root";
	private static final String NODE_TYPE_GROUP="node type group";
	private static final String NODE_TYPE_REFERENCE="node type reference";
	private Logger LOGGER=Logger.getLogger(getClass().getName());
	private static final String ACTION_CREATE_INDEX="action create index";
	private static final String ACTION_CREATE_GROUP="action create group";
	private static final String ACTION_RENAME_GROUP="action rename group";
	private static final String ACTION_SET_ICON_GROUP="action set icon group";
	private static final String ACTION_SET_ICON_REFERENCE="action set icon reference";
	private static final String ACTION_RENAME_REFERENCE="action rename reference";

	private String entihome$;
    private String entityKey$;
    private String entityLabel$;
    
    String requesterResponseLocator$;
    String locator$;
    private DefaultMutableTreeNode selectedNode;
    private DefaultMutableTreeNode rootNode;
    private DefaultMutableTreeNode parentNode;
    private JMainConsole console;
    String[] facets;
    JScrollPane scrollPane;
    JTree tree;
    JMenu menu;
    boolean isRoot=true;
    boolean isFirst=true;
	String selection$;
	JPopupMenu popup;
	int nodeNumber=0;
	boolean cut=false;
	String message$;
	Sack entity;
	static boolean debug=false;
	boolean ignoreOutdate=false;
/**
 * The default constructor.
 */
	public JIndexPanel() {
		 setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			scrollPane = new JScrollPane();
			add(scrollPane);
			
	}
	/**
	 * Execute the response locator.
	 * @param console the main console.
	 * @param locator$ the locator string.
	 */
	
	@Override
	public void response(JMainConsole console, String locator$) {
	//	System.out.println("JIndexPanel:response:locator="+locator$);
		try{
			Properties locator=Locator.toProperties(locator$);
			String action$=locator.getProperty(JRequester.REQUESTER_ACTION);
		//	System.out.println("IndexPanel:response:action="+action$);
			if(ACTION_CREATE_INDEX.equals(action$)){
				String entihome$=locator.getProperty(Entigrator.ENTIHOME);
				String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
				String text$=locator.getProperty(JTextEditor.TEXT);
				Entigrator entigrator=console.getEntigrator(entihome$);  
				entity=entigrator.ent_new("index", text$);
				entity=entigrator.ent_assignProperty(entity, "index", entity.getProperty("label"));
				entity.putAttribute(new Core(null,"icon","index.png"));
				entigrator.replace(entity);
				entigrator.ent_reindex(entity);
				entigrator.saveHandlerIcon(JEntitiesPanel.class, "index.png");
				entityKey$=entity.getKey();
				   String eLocator$=EntityHandler.getEntityLocatorAtKey(entigrator, entity.getKey());
				   JEntityPrimaryMenu.reindexEntity(console, eLocator$);
				   reindex(console, entigrator, entity);
				   JIndexPanel ip=new JIndexPanel();
				   String ipLocator$=ip.getLocator();
				   ipLocator$=Locator.append(ipLocator$, Entigrator.ENTIHOME, entihome$);
				   ipLocator$=Locator.append(ipLocator$, EntityHandler.ENTITY_KEY, entityKey$);
				   Stack<String> s=console.getTrack();
				   s.pop();
				   console.setTrack(s);
				   JConsoleHandler.execute(console, ipLocator$);
				   return;
				}
			if(ACTION_CREATE_GROUP.equals(action$)){
				String entihome$=locator.getProperty(Entigrator.ENTIHOME);
				String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
				String text$=locator.getProperty(JTextEditor.TEXT);
				if(debug)
					System.out.println("IndexPanel:response:create group text="+text$);
					
				Entigrator entigrator=console.getEntigrator(entihome$);  
				entity=entigrator.getEntityAtKey(entityKey$);
				String encodedSelection$ =locator.getProperty(SELECTION);
				byte[] ba=Base64.decodeBase64(encodedSelection$);
			    String selection$=new String(ba,"UTF-8");
			   // System.out.println("IndexPanel:response:create group:selection="+Locator.remove(Locator.LOCATOR_ICON, selection$));
			    locator=Locator.toProperties(selection$);
			    String nodeType$=locator.getProperty(NODE_TYPE);
			    String parentKey$=locator.getProperty(NODE_KEY);
			  //  System.out.println("IndexPanel:response:create group:parent key="+parentKey$);
			   if(NODE_TYPE_GROUP.equals(nodeType$)){
			    	if(!entity.existsElement("index.jlocator"))
			    		entity.createElement("index.jlocator");
			    	String groupKey$=Identity.key();
			    	Properties groupLocator=new Properties();
			    	groupLocator.setProperty(Locator.LOCATOR_TITLE, text$);
			    	groupLocator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
			    	groupLocator.setProperty(Locator.LOCATOR_ICON_CLASS,JEntitiesPanel.class.getName());
			    	groupLocator.setProperty(Locator.LOCATOR_ICON_FILE,"group.png");
			    	groupLocator.setProperty(NODE_TYPE,NODE_TYPE_GROUP);
			    	groupLocator.setProperty(NODE_KEY,groupKey$);
			    	groupLocator.setProperty(NODE_GROUP_KEY,parentKey$);
			    	groupLocator.setProperty(INDEX_KEY,entityKey$);
			    	String groupLocator$=Locator.toString(groupLocator);
			    	
			    	entity.putElementItem("index.jlocator", new Core(null,groupKey$,groupLocator$));
			    	entity.putElementItem("index.selection", new Core(null,"selection",groupKey$));
			    	entigrator.save(entity);
			    	JConsoleHandler.execute(console, getLocator());
			    }
			}
			if(ACTION_RENAME_GROUP.equals(action$)){
				String entihome$=locator.getProperty(Entigrator.ENTIHOME);
				String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
				String text$=locator.getProperty(JTextEditor.TEXT);
				Entigrator entigrator=console.getEntigrator(entihome$);  
				entity=entigrator.getEntityAtKey(entityKey$);
				String encodedSelection$ =locator.getProperty(SELECTION);
				byte[] ba=Base64.decodeBase64(encodedSelection$);
			    String selection$=new String(ba,"UTF-8");
			  //  System.out.println("IndexPanel:response:rename group:selection="+selection$);
			    locator=Locator.toProperties(selection$);
			    String nodeKey$=locator.getProperty(NODE_KEY);
			    locator.setProperty(Locator.LOCATOR_TITLE, text$);
			    Core core=entity.getElementItem("index.jlocator", nodeKey$);
			    core.value=Locator.toString(locator);
			    entity.putElementItem("index.jlocator",core );
			    entigrator.save(entity);
			    JConsoleHandler.execute(console, getLocator());
			    return;
			}
			if(ACTION_RENAME_REFERENCE.equals(action$)){
				String entihome$=locator.getProperty(Entigrator.ENTIHOME);
				String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
				String text$=locator.getProperty(JTextEditor.TEXT);
				Entigrator entigrator=console.getEntigrator(entihome$);  
				entity=entigrator.getEntityAtKey(entityKey$);
				String encodedSelection$ =locator.getProperty(SELECTION);
				byte[] ba=Base64.decodeBase64(encodedSelection$);
			    String selection$=new String(ba,"UTF-8");
			    locator=Locator.toProperties(selection$);
			    String nodeKey$=locator.getProperty(NODE_KEY);
			    if(!entity.existsElement("index.title"))
			        entity.createElement("index.title");
			    
			    Core core=entity.getElementItem("index.title", nodeKey$);
			    if(core==null)
			    	core=new Core(null,nodeKey$,text$);
			    else
			    	core.value=text$;
			    
			    entity.putElementItem("index.title",core );
			    entigrator.save(entity);
			    JConsoleHandler.execute(console, getLocator());
			    return;
			}
			
			if(ACTION_SET_ICON_GROUP.equals(action$)){
				String icon$=locator.getProperty(JIconSelector.ICON);
				Entigrator entigrator=console.getEntigrator(entihome$);  
				entity=entigrator.getEntityAtKey(entityKey$);
				String encodedSelection$ =locator.getProperty(SELECTION);
				byte[] ba=Base64.decodeBase64(encodedSelection$);
			    String selection$=new String(ba,"UTF-8");
			    //System.out.println("IndexPanel:response:set icon group:selection="+selection$);
			    locator=Locator.toProperties(selection$);
			    String nodeKey$=locator.getProperty(NODE_KEY);
			    locator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_ICONS);
			    locator.setProperty(Locator.LOCATOR_ICON_FILE,icon$);
			    Core core=entity.getElementItem("index.jlocator", nodeKey$);
			    core.value=Locator.toString(locator);
			    entity.putElementItem("index.jlocator",core );
			    entigrator.save(entity);
			    JConsoleHandler.execute(console, getLocator());
			    return;
			}
			if(ACTION_SET_ICON_REFERENCE.equals(action$)){
				String icon$=locator.getProperty(JIconSelector.ICON);
				Entigrator entigrator=console.getEntigrator(entihome$);  
				entity=entigrator.getEntityAtKey(entityKey$);
				String encodedSelection$ =locator.getProperty(SELECTION);
				byte[] ba=Base64.decodeBase64(encodedSelection$);
			    String selection$=new String(ba,"UTF-8");
			    locator=Locator.toProperties(selection$);
			    String nodeKey$=locator.getProperty(NODE_KEY);
			    if(!entity.existsElement("index.title"))
			    	entity.createElement("index.title");
  			    Core core=entity.getElementItem("index.title", nodeKey$);
  			  if(core!=null)
			       core.type=icon$;
			    else
			       core=new Core(icon$,nodeKey$,null);
			    entity.putElementItem("index.title",core );
			    entigrator.save(entity);
			    JConsoleHandler.execute(console, getLocator());
			    return;
			}
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
	}
	/**
	 * Get the panel to insert into the main console.
	 * @return this panel.
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
			//System.out.println("IndexPanel:getConextMenu:menu selected");
				menu.removeAll();
				JMenuItem expandItem = new JMenuItem("Expand");
				   expandItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							expandAll(tree, new TreePath(rootNode),true);
						}
					} );
					menu.add(expandItem);
					 JMenuItem collapseItem = new JMenuItem("Collapse");
					   collapseItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								expandAll(tree, new TreePath(rootNode),false);
							}
						} );
						menu.add(collapseItem);
			
						final TreePath[] tpa=tree.getSelectionPaths();
						if(hasSelectedItems())

			{
				//hasSelectedItems();
				   menu.addSeparator();
				  JMenuItem copyItem = new JMenuItem("Copy");
				   copyItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
					       cut=false;
						   console.clipboard.clear();
					       DefaultMutableTreeNode node;
					       String locator$;
					       String[] la;
					       for(TreePath tp:tpa){
					    	   node=(DefaultMutableTreeNode)tp.getLastPathComponent();
					    	   locator$=(String)node.getUserObject();
					    	   la=console.clipboard.getContent();
					    	   if(locator$!=null)
					    		   if(!contains(locator$,la))
					    		     console.clipboard.putString(locator$);
					       }
						}
					} );
					menu.add(copyItem);
					 JMenuItem cutItem = new JMenuItem("Cut");
					   cutItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								cut=true;
								   console.clipboard.clear();
							       DefaultMutableTreeNode node;
							       String locator$;
							       for(TreePath tp:tpa){
							    	   node=(DefaultMutableTreeNode)tp.getLastPathComponent();
							    	   locator$=(String)node.getUserObject();
							    	   if(locator$!=null)
							    		   console.clipboard.putString(locator$);
							}
							}
						} );
						menu.add(cutItem);
						 JMenuItem deleteItem = new JMenuItem("Delete");
						   deleteItem.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									int response = JOptionPane.showConfirmDialog(console.getContentPanel(), "Delete ?", "Confirm",
									        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
								   if (response == JOptionPane.YES_OPTION) {
								     try{
									DefaultMutableTreeNode node;
								    String locator$;
								    String nodeKey$;
								       for(TreePath tp:tpa){
								    	   node=(DefaultMutableTreeNode)tp.getLastPathComponent();
								    	   locator$=(String)node.getUserObject();
								    	   nodeKey$=Locator.getProperty(locator$, NODE_KEY);
								    	   entity.removeElementItem("index.title", nodeKey$);
								    	   entity.removeElementItem("index.jlocator", nodeKey$);
								       }
								    Entigrator entigrator=console.getEntigrator(entihome$);
								    entigrator.save(entity);
								    JConsoleHandler.execute(console, getLocator());
								     }catch(Exception ee){
								    	 LOGGER.info(ee.toString());
								     }
								}
								   }
							} );
							menu.add(deleteItem);		
			}
			 menu.addSeparator();
			   JMenuItem doneItem = new JMenuItem("Done");
				doneItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						Entigrator entigrator=console.getEntigrator(entihome$);
						entigrator.replace(entity);
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
	private void expandAll(JTree tree, TreePath parent, boolean expand) 
    {
      
        TreeNode node = (TreeNode)parent.getLastPathComponent();

        if (node.getChildCount() >= 0) 
        {
            for (Enumeration<TreeNode> e=node.children();
            		e.hasMoreElements(); ) 
            {
                TreeNode n = e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path, expand);
            }
        }
        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }
    }
	/**
	 * Get the context locator.
	 * @return the locator string.
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
			if(selection$!=null)
				locator.setProperty(SELECTION,Locator.compressText(selection$));
			locator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
			locator.setProperty(Locator.LOCATOR_ICON_CLASS,JEntitiesPanel.class.getName());
			locator.setProperty(Locator.LOCATOR_ICON_FILE,"index.png");

			return Locator.toString(locator);
			}catch(Exception e){
	        Logger.getLogger(getClass().getName()).severe(e.toString());
	        return null;
			}
	}
private DefaultMutableTreeNode instantiateNode(Sack index ,String nodeKey$){
	try{
	//	System.out.println("IndexPanel:instantiateNode:"+nodeKey$);
		String nodeLocator$=index.getElementItemAt("index.jlocator", nodeKey$);
		if(nodeLocator$==null)
			return null;
	//	System.out.println("IndexPanel:instantiateNode:locator="+nodeLocator$);
		DefaultMutableTreeNode node=new DefaultMutableTreeNode();
		node.setUserObject(nodeLocator$);
	    Properties locator=Locator.toProperties(nodeLocator$);
	    if(!NODE_TYPE_GROUP.equals(locator.getProperty(NODE_TYPE)))
	    	return node;
     	String[]sa=listOrderedGroupMembers(index,nodeKey$);
	    if(sa==null||sa.length<1){
	    	System.out.println("IndexPanel:instantiateNode:no member in group="+nodeKey$);
	    	return node;
	    }
	    DefaultMutableTreeNode member;
	  //  System.out.println("IndexPanel:instantiateNode:members="+sa.length);
	   
		for(String aSa:sa){
			member=instantiateNode(index,aSa);
			if(member!=null)
				node.add(member);
		}
		return node;
		}catch(Exception e){
		LOGGER.severe(e.toString());
	}
	return null;
}
/**
 * Create a new facet renderer.
 * @param console the main console.
 * @param locator$ the locator string.
 * @return the fields editor.
 */	
@Override
	public JContext instantiate(JMainConsole console, String locator$) {
		try{
		//	System.out.println("IndexPanel.instantiate:locator="+locator$);
			this.console=console;
			Properties locator=Locator.toProperties(locator$);
			entihome$=locator.getProperty(Entigrator.ENTIHOME);
			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			Entigrator entigrator=console.getEntigrator(entihome$);
			entityLabel$=entigrator.indx_getLabel(entityKey$);
			if(Locator.LOCATOR_TRUE.equals(locator.getProperty(JFacetRenderer.ONLY_ITEM)))
				 return this;
			requesterResponseLocator$=locator.getProperty(JRequester.REQUESTER_RESPONSE_LOCATOR);
            entity=entigrator.getEntityAtKey(entityKey$);
            entityLabel$=entity.getProperty("label");
    		rootNode = new DefaultMutableTreeNode(entityLabel$);
   		    locator=new Properties();
   		 locator.setProperty(Locator.LOCATOR_TITLE, INDEX);
   		 locator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
 		locator.setProperty(Locator.LOCATOR_ICON_CLASS,JEntitiesPanel.class.getName());
 		locator.setProperty(Locator.LOCATOR_ICON_FILE,"index.png");
   		 rootNode.setUserObject(Locator.toString(locator));
   		 parentNode = new DefaultMutableTreeNode(entityLabel$);
   		 rootNode.add(parentNode);
   		 Properties parentLocator=new Properties();
   		 parentLocator.setProperty(Locator.LOCATOR_TITLE, entity.getProperty("label"));
   		parentLocator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_ICONS);
   		 parentLocator.setProperty(Locator.LOCATOR_ICON_FILE, entigrator.ent_getIconAtKey(entityKey$));
   		 parentLocator.setProperty(NODE_TYPE,NODE_TYPE_GROUP);
   		 parentLocator.setProperty(NODE_KEY,"parent");
   		 parentLocator.setProperty(Entigrator.ENTIHOME,entihome$);
   		 parentLocator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
   		 parentNode.setUserObject(Locator.toString(parentLocator));
   		 //String[] sa=listGroupMembers(index,"parent");
   		String[] sa=listOrderedGroupMembers(entity,"parent");
   		 DefaultMutableTreeNode child;
   		 if(sa!=null)
   			 for(String aSa:sa){
   				 child=instantiateNode(entity, aSa);
   				 if(child!=null)
   				  parentNode.add(child);
   			 }
   		 tree=new JTree(rootNode);
   		 tree.addTreeSelectionListener(new SelectionListener());
   		 tree.setShowsRootHandles(true);
   		 tree.setCellRenderer(new NodeRenderer()); 
   		 tree.addMouseListener(new MousePopupListener());
   		 scrollPane.getViewport().add(tree);
   		 String selectedNodeKey$=entity.getElementItemAt("index.selection","selection");
   		 if(selectedNodeKey$!=null)
   		    select(selectedNodeKey$);
		}catch(Exception e){
	        Logger.getLogger(getClass().getName()).severe(e.toString());
		}
		return this;
	}
	private void select(String  selectedNodeKey$){
       
       try{
    	
        DefaultMutableTreeNode root=(DefaultMutableTreeNode)tree.getModel().getRoot();	 
        @SuppressWarnings("unchecked")
		Enumeration< DefaultMutableTreeNode> en = root.preorderEnumeration();
        DefaultMutableTreeNode node;
       // System.out.println("EntityDigestDisplay:select:selection node="+selectedNodeKey$);
        String nodeLocator$;
        Properties locator;
        while(en.hasMoreElements()){
      	 node=(DefaultMutableTreeNode ) en.nextElement();
      	 nodeLocator$=(String)node.getUserObject();
      	 locator=Locator.toProperties(nodeLocator$);
      	 //System.out.println("EntityDigestDisplay:select:node number="+nodeNumber$);
      	 if(selectedNodeKey$.equals(locator.getProperty(NODE_KEY))){
      		 TreeNode[] nodes = ((DefaultTreeModel) tree.getModel()).getPathToRoot(node);
           TreePath tpath = new TreePath(nodes);
           tree.scrollPathToVisible(tpath);
           tree.setSelectionPath(tpath);
           break;
      	 }
        }
       }catch(Exception e){
      	 Logger.getLogger(getClass().getName()).severe(e.toString());
       }
      	  
      
  }
	/**
	 * Get the context title.
	 * @return the title of the context.
	 */
	@Override
	public String getTitle() {
		if(message$==null)
			return "Index";
		else
			return "Index"+message$;
			}
private String[] listGroupMembers(Sack index,String groupKey$){
	try{
		Core[] ca=index.elementGet("index.jlocator");
		if(ca==null||ca.length<1)
			return null;
		Properties memberLocator;
		ArrayList<String>sl=new ArrayList<String>();
		for(Core c:ca){
			try{
				memberLocator=Locator.toProperties(c.value);
				if(groupKey$.equals(memberLocator.getProperty(NODE_GROUP_KEY)))
					sl.add(c.name);
			}catch(Exception ee){
				LOGGER.info(ee.toString());
			}
		}
		return sl.toArray(new String[0]);
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
	return null;
}
private String[] listGroupLocators(Sack index,String groupKey$){
	try{
		Core[] ca=index.elementGet("index.jlocator");
		if(ca==null||ca.length<1)
			return null;
		Properties memberLocator;
		ArrayList<String>sl=new ArrayList<String>();
		for(Core c:ca){
			try{
				memberLocator=Locator.toProperties(c.value);
				if(groupKey$.equals(memberLocator.getProperty(NODE_GROUP_KEY)))
					sl.add(c.value);
			}catch(Exception ee){
				LOGGER.info(ee.toString());
			}
		}
		return sl.toArray(new String[0]);
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
	return null;
}
private static String[] listOrderedGroupMembers(Sack index,String groupKey$){
	try{
		Core[] ca=index.elementGet("index.jlocator");
		if(ca==null||ca.length<1)
			return null;
		Properties memberLocator;
		ArrayList<Core>cl=new ArrayList<Core>();
		for(Core c:ca){
			try{
				memberLocator=Locator.toProperties(c.value);
				if(groupKey$.equals(memberLocator.getProperty(NODE_GROUP_KEY)))
					cl.add(c);
			}catch(Exception ee){
				Logger.getLogger(JIndexPanel.class.getName()).info(ee.toString());
			}
		}
	//	System.out.println("JIndexPanel:listOrderedGroupMembers.cl="+cl.size());
		Collections.sort(cl, new NumberNodeComparator());
		ca=cl.toArray(new Core[0]);
		String[] sa=new String[ca.length];
		for(int i=0;i<ca.length;i++)
			sa[i]=ca[i].name;
		return sa;
	}catch(Exception e){
		Logger.getLogger(JIndexPanel.class.getName()).severe(e.toString());
	}
	return null;
}
private Sack removeNode(Sack index,String nodeKey$){
	try{
		String locator$=index.getElementItemAt("index.jlocator", nodeKey$);
		if(NODE_TYPE_REFERENCE.equals(Locator.getProperty(locator$, NODE_TYPE))){
			index.removeElementItem("index.jlocator", nodeKey$);
			return index;
		}else{
		String[] sa=listGroupMembers(index, nodeKey$);
		if(sa!=null)
			for(String aSa:sa)
				index.removeElementItem("index.jlocator", aSa);
		index.removeElementItem("index.jlocator", nodeKey$);
		}
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
	return index;
}
private Sack orderGroupDefault(Sack index,String groupKey$){
	try{
		String[] sa=listGroupMembers(index, groupKey$);
		if(sa!=null){
			ArrayList <Core>cl=new ArrayList<Core>();
			for(String aSa:sa){
				cl.add(index.getElementItem("index.jlocator",aSa));
			}
			Collections.sort(cl,new DefaultNodeComparator());
			Core[]ca=cl.toArray(new Core[0]);
			for(int i=0;i<ca.length;i++){
				ca[i].type=String.valueOf(i);
				index.putElementItem("index.jlocator", ca[i]);
			}
		}
		
	}catch(Exception e ){
		LOGGER.severe(e.toString());
	}
	return index;
}
/**
 * Get the context subtitle.
 * @return the subtitle of the context.
 */
	@Override
	public String getSubtitle() {
		return entityLabel$;
	}
	/**
	 * Get the context type.
	 * @return the type of the context.
	 */
	@Override
	public String getType() {

		return "index";
	}
	/**
	 * Get category icon as a Base64 string.
	 * @return the category icon string. 
	 */
	@Override
	public String getCategoryIcon(Entigrator entigrator) {
		return Support.readHandlerIcon(null,JEntitiesPanel.class, "index.png");
	}

	@Override
	public String getCategoryTitle() {
		
		return "Indexes";
	}
	/**
	 * Complete the context. 
	 */
	@Override
	public void close() {
	}
	/**
	 * Add the renderer's icon to the locator.
	 * @param locator$ the origin locator.
	 * @return the locator with the icon added.
	 */
	@Override
	public String addIconToLocator(String locator$) {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * Get facet handler class name.
	 * @return facet handler class name.
	 */
	@Override
	public String getFacetHandler() {
		return IndexHandler.class.getName();
	}
	/**
	 * Get the context type.
	 * @return the type of the context.
	 */
	@Override
	public String getEntityType() {
		return "index";
	}
	/**
	 * Adapt cloned entity.
	 * @param console the main console.
	 * @param locator$ the locator string.
	 * 
	 */
	@Override
	public void adaptClone(JMainConsole console, String locator$) {
		try{
			Properties locator=Locator.toProperties(locator$);
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			Entigrator entigrator=console.getEntigrator(entihome$);
			entity=entigrator.getEntityAtKey(entityKey$);
			String entityLocator$=EntityHandler.getEntityLocator(entigrator, entity);
			IndexHandler indexHandler=new IndexHandler();
			indexHandler.instantiate(entityLocator$);
			indexHandler.adaptClone(entigrator);
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
	}
	/**
	 * Adapt renamed entity.
	 * @param console the main console.
	 * @param locator$ the locator string.
	 * 
	 */
	@Override
	public void adaptRename(JMainConsole console, String locator$) {
		try{
			Properties locator=Locator.toProperties(locator$);
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			Entigrator entigrator=console.getEntigrator(entihome$);
			Sack entity=entigrator.getEntityAtKey(entityKey$);
			String entityLocator$=EntityHandler.getEntityLocator(entigrator, entity);
			IndexHandler indexHandler=new IndexHandler();
			indexHandler.instantiate(entityLocator$);
			indexHandler.adaptRename(entigrator);
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
	}
	/**
	 * Rebuild entity parameters and indexes.
	 * @param console the main console.
	 * @param entigrator the entigrator.
	 * @param entity the entity.
	 */
	@Override
	public void reindex(JMainConsole console, Entigrator entigrator, Sack entity) {
		try{	
	    	String indexHandler$=IndexHandler.class.getName();
	    	if(entity.getElementItem("fhandler", indexHandler$)!=null){
				entity.putElementItem("jfacet", new Core(null,indexHandler$,JIndexFacetOpenItem.class.getName()));
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
	 * @return the key of the new entity.
	 */
	@Override
	public String newEntity(JMainConsole console, String locator$) {
		try{
			Properties locator=Locator.toProperties(locator$);
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			JTextEditor textEditor=new JTextEditor();
		    String teLocator$=textEditor.getLocator();
		    teLocator$=Locator.append(teLocator$, Entigrator.ENTIHOME,entihome$);
		    teLocator$=Locator.append(teLocator$, JTextEditor.TEXT_TITLE,"New index");
		    String text$="NewIndex"+Identity.key().substring(0, 4);
		    teLocator$=Locator.append(teLocator$, JTextEditor.TEXT,text$);
		    JIndexPanel ip=new JIndexPanel();
		    String ipLocator$=ip.getLocator();
		    ipLocator$=Locator.append(ipLocator$, Entigrator.ENTIHOME,entihome$);
		    ipLocator$=Locator.append(ipLocator$, EntityHandler.ENTITY_KEY,entityKey$);
		    ipLocator$=Locator.append(ipLocator$, BaseHandler.HANDLER_METHOD,"response");
		    ipLocator$=Locator.append(ipLocator$, JRequester.REQUESTER_ACTION,ACTION_CREATE_INDEX);
		    String requesterResponseLocator$=Locator.compressText(ipLocator$);
		    teLocator$=Locator.append(teLocator$,JRequester.REQUESTER_RESPONSE_LOCATOR,requesterResponseLocator$);
		    JConsoleHandler.execute(console, teLocator$);
		    
			
		}catch(Exception ee){   
			LOGGER.severe(ee.toString());
			
		}
		return null;
	}
	 private void initPopup(){
	    	try{
	    		//System.out.println("IndexPanel:initPopup:selection="+selection$);
	    		Properties locator=Locator.toProperties(selection$);
	    	    String nodeType$=locator.getProperty(NODE_TYPE);
	    	    //System.out.println("IndexPanel:initPopup:node type="+nodeType$);
	    	    if(NODE_TYPE_ROOT.equals(nodeType$)
	    	    	){
	    	    	popup=null;
	    	    	return;
	    	    }
	    	    if(NODE_TYPE_GROUP.equals(nodeType$)){
	    	    	  popup=new JPopupMenu();
			    	   JMenuItem newGroupItem=new JMenuItem("New group");
			    	   newGroupItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
							   try{
								 // System.out.println("JIndexPanel:popup:new parent group:  selection="+selection$); 
								  Properties locator=Locator.toProperties(selection$);
								  String entihome$=locator.getProperty(Entigrator.ENTIHOME);
								  String title$="New group"+Identity.key().substring(0, 4);
								  JTextEditor te=new JTextEditor();
								  String teLocator$=te.getLocator();
								  teLocator$=Locator.append(teLocator$,Entigrator.ENTIHOME, entihome$);
								  teLocator$=Locator.append(teLocator$,JTextEditor.TEXT, title$);
								  teLocator$=Locator.append(teLocator$,JTextEditor.TEXT_TITLE, "Create group");
								  String ipLocator$=getLocator();
								  ipLocator$=Locator.append(ipLocator$,JRequester.REQUESTER_ACTION, ACTION_CREATE_GROUP);
								  ipLocator$=Locator.append(ipLocator$,SELECTION, Locator.compressText(selection$));
								  ipLocator$=Locator.append(ipLocator$,BaseHandler.HANDLER_METHOD,"response");
								  teLocator$=Locator.append(teLocator$,JRequester.REQUESTER_RESPONSE_LOCATOR,Locator.compressText(ipLocator$));
							      JConsoleHandler.execute(console, teLocator$);
							   }catch(Exception ee){
								   Logger.getLogger(JIndexPanel.class.getName()).info(ee.toString());
							   }
							}
						    });
			    	   popup.add(newGroupItem);
			    	   popup.addSeparator();
			    	   JMenuItem renameItem=new JMenuItem("Rename");
			    	   renameItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
							   try{
								  
								  Properties locator=Locator.toProperties(selection$);
								  String title$=locator.getProperty(Locator.LOCATOR_TITLE);
								  JTextEditor te=new JTextEditor();
								  String teLocator$=te.getLocator();
								  teLocator$=Locator.append(teLocator$,Entigrator.ENTIHOME, entihome$);
								  teLocator$=Locator.append(teLocator$,JTextEditor.TEXT,title$);
								  String ipLocator$=getLocator();
								  ipLocator$=Locator.append(ipLocator$,JRequester.REQUESTER_ACTION, ACTION_RENAME_GROUP);
								  selection$=Locator.append(selection$,Entigrator.ENTIHOME,entihome$);
								  ipLocator$=Locator.append(ipLocator$,SELECTION, Locator.compressText(selection$));
								  ipLocator$=Locator.append(ipLocator$,BaseHandler.HANDLER_METHOD,"response");
								  teLocator$=Locator.append(teLocator$,JRequester.REQUESTER_RESPONSE_LOCATOR,Locator.compressText(ipLocator$));
							      JConsoleHandler.execute(console, teLocator$);
							   }catch(Exception ee){
								   Logger.getLogger(JIndexPanel.class.getName()).info(ee.toString());
							   }
							}
						    });
			    	   popup.add(renameItem);
			    	   JMenuItem setIconItem=new JMenuItem("Set icon");
			    	   setIconItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
							   try{
								  JIconSelector is=new JIconSelector();
								  String isLocator$=is.getLocator();
								  isLocator$=Locator.append(isLocator$,Entigrator.ENTIHOME, entihome$);
								  String ipLocator$=getLocator();
								  ipLocator$=Locator.append(ipLocator$,JRequester.REQUESTER_ACTION, ACTION_SET_ICON_GROUP);
								  ipLocator$=Locator.append(ipLocator$,SELECTION, Locator.compressText(selection$));
								  ipLocator$=Locator.append(ipLocator$,BaseHandler.HANDLER_METHOD,"response");
								  isLocator$=Locator.append(isLocator$,JRequester.REQUESTER_RESPONSE_LOCATOR,Locator.compressText(ipLocator$));
							      JConsoleHandler.execute(console, isLocator$);
							   }catch(Exception ee){
								   Logger.getLogger(JIndexPanel.class.getName()).info(ee.toString());
							   }
							}
						    });
			    	   popup.add(setIconItem);
			    	   JMenuItem orderItem=new JMenuItem("Order");
			    	   orderItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								try{
								  Properties locator=Locator.toProperties(selection$);
								  Entigrator entigrator=console.getEntigrator(entihome$);
								  entity=entigrator.getEntityAtKey(entityKey$);
								  String nodeKey$=locator.getProperty(NODE_KEY);
								  entity=orderGroupDefault(entity, nodeKey$);
								  entity.putElementItem("index.selection", new Core(null,"selection",nodeKey$));
								  entigrator.save(entity);
								  JConsoleHandler.execute(console,getLocator());
							   }catch(Exception ee){
								   Logger.getLogger(JIndexPanel.class.getName()).info(ee.toString());
							   }
							  
							}
						    });
			    	   popup.add(orderItem);
			    	   popup.addSeparator();
			    	   JMenuItem copyItem=new JMenuItem("Copy");
			    	   copyItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								try{
								  cut=false;	
								  console.clipboard.clear();
								  if(selection$!=null)
									  console.clipboard.putString(selection$);
							   }catch(Exception ee){
								   Logger.getLogger(JIndexPanel.class.getName()).info(ee.toString());
							   }
							   }
							
						    });
			    	   popup.add(copyItem);
			    	   JMenuItem cutItem=new JMenuItem("Cut");
			    	   cutItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								try{
								  cut=true;	
								  console.clipboard.clear();
								  if(selection$!=null)
									  console.clipboard.putString(selection$);
							   }catch(Exception ee){
								   Logger.getLogger(JIndexPanel.class.getName()).info(ee.toString());
							   }
							   }
							
						    });
			    	   popup.add(cutItem);
			    	  
			    	   final String [] sa=console.clipboard.getContent();
			    	   if(sa!=null&&sa.length>0){
			    		   JMenuItem pasteItem=new JMenuItem("Paste");
				    	   pasteItem.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
								   try{
									//  System.out.println("JIndexPanel:popup:new parent group:  selection="+selection$); 
									  Properties selectionLocator=Locator.toProperties(selection$);
									  String indexLocator$=getLocator();
									  String groupKey$=selectionLocator.getProperty(NODE_KEY);
									  Properties indexLocator=Locator.toProperties(indexLocator$);
									  String entihome$=indexLocator.getProperty(Entigrator.ENTIHOME);
									  String entityKey$=indexLocator.getProperty(EntityHandler.ENTITY_KEY);
									  Entigrator entigrator=console.getEntigrator(entihome$);
									  entity=entigrator.getEntityAtKey(entityKey$);
									  for(String aSa:sa){
										  entity=pasteItemToGroup(entity,groupKey$,aSa);
									  }
									  entigrator.replace(entity);
									  cut=false;
								      JConsoleHandler.execute(console, getLocator());
								   }catch(Exception ee){
									   Logger.getLogger(JIndexPanel.class.getName()).info(ee.toString());
								   }
								}
							    });
				    	   popup.add(pasteItem);
				    	   
			    	   }
			    	   popup.addSeparator();
			    	   JMenuItem deleteItem=new JMenuItem("Delete");
			    	   deleteItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								int response = JOptionPane.showConfirmDialog(console.getContentPanel(), "Delete ?", "Confirm",
								        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
							   if (response == JOptionPane.YES_OPTION) { 
								try{
								   
								  Properties locator=Locator.toProperties(selection$);
								  Entigrator entigrator=console.getEntigrator(entihome$);
								  entity=entigrator.getEntityAtKey(entityKey$);
								  String nodeKey$=locator.getProperty(NODE_KEY);
								  String groupKey$=locator.getProperty(NODE_GROUP_KEY);
								  
								  entity=removeNode(entity,nodeKey$);
								  entity.putElementItem("index.selection", new Core(null,"selection",groupKey$));
								  entigrator.save(entity);
								  JConsoleHandler.execute(console,getLocator());
							   }catch(Exception ee){
								   Logger.getLogger(JIndexPanel.class.getName()).info(ee.toString());
							   }
							   }
							}
						    });
			    	   popup.add(deleteItem);
			    	  
	    	    return ;
	    	    }
	    	    if(NODE_TYPE_REFERENCE.equals(nodeType$)){
	    	    	  popup=new JPopupMenu();
	    	    	  final String locatorType$=locator.getProperty(Locator.LOCATOR_TYPE);
	    	    	  
	    	    	  JMenuItem openItem=new JMenuItem("Open");
			    	  openItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) { 
								Properties locator=Locator.toProperties(selection$);
								if(JFolderPanel.LOCATOR_TYPE_FILE.equals(locatorType$)){
									String entihome$=locator.getProperty(Entigrator.ENTIHOME);
									String filePath$=entihome$+"/"+locator.getProperty(JFolderPanel.FILE_PATH);
									File itemFile=new File(filePath$);
									try{
									Desktop.getDesktop().open(itemFile);
									}catch(Exception ee){
										LOGGER.info(ee.toString());
									}
									return;
								}
								if(JWeblinksPanel.LOCATOR_TYPE_WEB_LINK.equals(locatorType$)){
								try{
									String url$=locator.getProperty(JWeblinksPanel.WEB_LINK_URL);
									Desktop.getDesktop().browse(new URI(url$));
									}catch(Exception ee){
										LOGGER.info(ee.toString());
									}
								return;
								}
								String responseLocator$=getLocator();
							//	System.out.println("IndexPanel:open:response locator="+Locator.remove(responseLocator$,Locator.LOCATOR_ICON));
								
								selection$=Locator.append(selection$, JRequester.REQUESTER_RESPONSE_LOCATOR, Locator.compressText(responseLocator$));
							//	System.out.println("IndexPanel:open:selection="+Locator.remove(Locator.remove(selection$, Locator.LOCATOR_ICON),JRequester.REQUESTER_RESPONSE_LOCATOR));
								selection$=Locator.append(selection$,Entigrator.ENTIHOME,entihome$);
								JConsoleHandler.execute(console, selection$);
								
							}
			    	   });
			    	   popup.add(openItem);
			    	   if(JFolderPanel.LOCATOR_TYPE_FILE.equals(locator.getProperty(Locator.LOCATOR_TYPE))){
			    		   JMenuItem openFolderItem=new JMenuItem("Open folder");
				    	   openFolderItem.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) { 
									Properties locator=Locator.toProperties(selection$);
									String entihome$=locator.getProperty(Entigrator.ENTIHOME);	
									String filePath$=locator.getProperty(JFolderPanel.FILE_PATH);
										File itemFile=new File(filePath$);
										try{
										Desktop.getDesktop().open(itemFile.getParentFile());
										}catch(Exception ee){
											LOGGER.info(ee.toString());
										}
										return;
								}
				    	   });
				    	   popup.add(openFolderItem);
			    	   }
			    	   JMenuItem deleteItem=new JMenuItem("Delete");
			    	   deleteItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) { 
							int response = JOptionPane.showConfirmDialog(console.getContentPanel(), "Delete ?", "Confirm",
							        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						   if (response == JOptionPane.YES_OPTION) { 
							Properties selectionLocator=Locator.toProperties(selection$);
							  String indexLocator$=getLocator();
							  Properties indexLocator=Locator.toProperties(indexLocator$);
							  String entihome$=indexLocator.getProperty(Entigrator.ENTIHOME);
							  String entityKey$=indexLocator.getProperty(EntityHandler.ENTITY_KEY);
							  Entigrator entigrator=console.getEntigrator(entihome$);
							  entity=entigrator.getEntityAtKey(entityKey$);
							  String nodeKey$=selectionLocator.getProperty(NODE_KEY);
							  String groupKey$=selectionLocator.getProperty(NODE_GROUP_KEY);
							
							  entity.removeElementItem("index.jlocator", nodeKey$);
							  entity.putElementItem("index.selection", new Core(null,"selection",groupKey$));
							  entigrator.save(entity);
							  JConsoleHandler.execute(console, getLocator());
						   }
								
							}
							});
			    	   popup.add(deleteItem);
			    	   popup.addSeparator();
			    	   JMenuItem renameItem=new JMenuItem("Rename");
			    	   renameItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) { JConsoleHandler.execute(console, selection$);
							try{
								  
								  Properties locator=Locator.toProperties(selection$);
								  String entihome$=locator.getProperty(Entigrator.ENTIHOME);
								  String nodeKey$=locator.getProperty(NODE_KEY);
								  String title$;
								  Core title=entity.getElementItem("index.title", nodeKey$);
								  if(title!=null&&title.value!=null)
									  title$=title.value;
								  else
								     title$=locator.getProperty(Locator.LOCATOR_TITLE);
								  JTextEditor te=new JTextEditor();
								  String teLocator$=te.getLocator();
								  teLocator$=Locator.append(teLocator$,Entigrator.ENTIHOME, entihome$);
								  teLocator$=Locator.append(teLocator$,JTextEditor.TEXT,title$);
								  String ipLocator$=getLocator();
								  ipLocator$=Locator.append(ipLocator$,JRequester.REQUESTER_ACTION, ACTION_RENAME_REFERENCE);
								  ipLocator$=Locator.append(ipLocator$,SELECTION, Locator.compressText(selection$));
								  ipLocator$=Locator.append(ipLocator$,BaseHandler.HANDLER_METHOD,"response");
								  teLocator$=Locator.append(teLocator$,JRequester.REQUESTER_RESPONSE_LOCATOR,Locator.compressText(ipLocator$));
							      JConsoleHandler.execute(console, teLocator$);
							   }catch(Exception ee){
								   Logger.getLogger(JIndexPanel.class.getName()).info(ee.toString());
							   }
							}
			    	   });
			    	   popup.add(renameItem);
			    	   JMenuItem setIconItem=new JMenuItem("Set icon");
			    	   setIconItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) { JConsoleHandler.execute(console, selection$);
							 try{
								   
								  Properties locator=Locator.toProperties(selection$);
							  	  JIconSelector is=new JIconSelector();
								  String isLocator$=is.getLocator();
								  isLocator$=Locator.append(isLocator$,Entigrator.ENTIHOME, entihome$);
								  String ipLocator$=getLocator();
								  ipLocator$=Locator.append(ipLocator$,JRequester.REQUESTER_ACTION, ACTION_SET_ICON_REFERENCE);
								  ipLocator$=Locator.append(ipLocator$,SELECTION, Locator.compressText(selection$));
								  ipLocator$=Locator.append(ipLocator$,BaseHandler.HANDLER_METHOD,"response");
								  isLocator$=Locator.append(isLocator$,JRequester.REQUESTER_RESPONSE_LOCATOR,Locator.compressText(ipLocator$));
							      JConsoleHandler.execute(console, isLocator$);
							   }catch(Exception ee){
								   Logger.getLogger(JIndexPanel.class.getName()).info(ee.toString());
							   }
							}
			    	   });
			    	   popup.add(setIconItem);
			    	   JMenuItem resetItem=new JMenuItem("Reset");
			    	   resetItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) { JConsoleHandler.execute(console, selection$);
							 try{
								   
								  Properties locator=Locator.toProperties(selection$);
								  String nodeKey$=locator.getProperty(NODE_KEY);
								  Core title=entity.getElementItem("index.title", nodeKey$);
								  if(title!=null){
								  entity.removeElementItem("index.title", nodeKey$);
								  Entigrator entigrator=console.getEntigrator(entihome$);
							  	  entigrator.save(entity);
								  JConsoleHandler.execute(console, getLocator());
								  }
							   }catch(Exception ee){
								   Logger.getLogger(JIndexPanel.class.getName()).info(ee.toString());
							   }    
							}
			    	   });
			    	   popup.add(resetItem);
			    	   popup.addSeparator();
			    	   JMenuItem copyItem=new JMenuItem("Copy");
			    	   copyItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) { 
								 cut=false;
								 console.clipboard.clear();
								console.clipboard.putString(selection$);

			    	   }
			    	   });
			    	   popup.add(copyItem);
	    	    	  JMenuItem cutItem=new JMenuItem("Cut");
			    	   cutItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) { 
								 cut=true;
								 console.clipboard.clear();
								console.clipboard.putString(selection$); 
							}
			    	   });
			    	   popup.add(cutItem);

	    	    }
	    	}catch(Exception e){
	    	Logger.getLogger(getClass().getName()).severe(e.toString());	
	    	}
	    }

 private Sack  copyGroupContent(Sack sourceIndex,String sourceGroupKey$,String targetGroupKey$){
		try{
			  String[] sa=listGroupMembers(sourceIndex,sourceGroupKey$);
              String itemLocator$;
			  String newItemKey$;
              Properties newItemLocator;
              String newItemLocator$;
              String itemType$;
              String itemKey$;
              String[] la;
              if(sa!=null){
            	  for(String aSa:sa){
            		  itemLocator$=sourceIndex.getElementItemAt("index.jlocator", aSa);
            		  newItemLocator=Locator.toProperties(itemLocator$);
            		  itemType$=newItemLocator.getProperty( NODE_TYPE);
            		  itemKey$=newItemLocator.getProperty( NODE_KEY);
            		  newItemKey$=Identity.key();
            		  newItemLocator.setProperty(NODE_KEY, newItemKey$);
            		  newItemLocator.setProperty( NODE_GROUP_KEY, targetGroupKey$);
            		  la=listGroupLocators(entity, targetGroupKey$);
            		  newItemLocator$=Locator.toString(newItemLocator);
            		  if(!contains(newItemLocator$, la))
            		      entity.putElementItem("index.jlocator", new Core(null,newItemKey$,newItemLocator$));
            		  if(NODE_TYPE_GROUP.equals(itemType$))
            			  copyGroupContent(sourceIndex,itemKey$,newItemKey$);
            	  }
              }
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
		
		
		return sourceIndex;
	}
	
private Sack pasteItemToGroup(Sack index,String groupKey$,String itemLocator$){
		try{
			if(debug)
			System.out.println("JIndexPanel:pasteItemToGroup:item locator="+itemLocator$);
			Properties itemLocator=Locator.toProperties(itemLocator$);
			String nodeType$=itemLocator.getProperty(NODE_TYPE);
			String nodeKey$=itemLocator.getProperty(NODE_KEY);
			String indexKey$=itemLocator.getProperty(INDEX_KEY);
			String itemEntityKey$=itemLocator.getProperty(EntityHandler.ENTITY_KEY);
			if(NODE_TYPE_GROUP.equals(nodeType$)){
                 if(cut){
                	 itemLocator$=Locator.append(itemLocator$, NODE_GROUP_KEY, groupKey$);
                	 index.putElementItem("index.jlocator", new Core(null,nodeKey$,itemLocator$));
                	
                 }else{
                      Properties newGroupLocator=Locator.toProperties(itemLocator$);
                      String newGroupKey$= Identity.key();
                      newGroupLocator.setProperty(NODE_KEY,newGroupKey$);
                      newGroupLocator.setProperty(NODE_GROUP_KEY, groupKey$);
                      index.putElementItem("index.jlocator", new Core(null,newGroupKey$,Locator.toString(newGroupLocator)));
                      Sack sourceIndex=index;
                      if(!entityKey$.equals(indexKey$)){
                    	  Entigrator entigrator=console.getEntigrator(entihome$);
                    	  sourceIndex=entigrator.getEntityAtKey(indexKey$);
                      }
                      String[] sa=listGroupMembers(sourceIndex,nodeKey$);
                      String newItemKey$;
                      Properties newItemLocator;
                      String newItemLocator$;
                      String itemType$;
                      String itemKey$;
                      String[] la;
                      if(sa!=null){
                    	  for(String aSa:sa){
                    		  itemLocator$=sourceIndex.getElementItemAt("index.jlocator", aSa);
                    		  newItemLocator=Locator.toProperties(itemLocator$);
                    		  itemType$=newItemLocator.getProperty( NODE_TYPE);
                    		  itemKey$=newItemLocator.getProperty( NODE_KEY);
                    		  newItemKey$=Identity.key();
                    		  newItemLocator.setProperty(NODE_KEY, newItemKey$);
                    		  newItemLocator.setProperty( NODE_GROUP_KEY, newGroupKey$);
                    		  la=listGroupLocators(entity, newGroupKey$);
                    		  newItemLocator$=Locator.toString(newItemLocator);
                    		  if(!contains(newItemLocator$, la))
                    		      index.putElementItem("index.jlocator", new Core(null,newItemKey$,newItemLocator$));
                    
                    		  if(NODE_TYPE_GROUP.equals(itemType$))
                    			  copyGroupContent(sourceIndex,itemKey$,newItemKey$);
                    	  }
                      index=orderGroupDefault(index, newGroupKey$);
                      }
                 }
                
			}else{
				if(nodeKey$==null||!cut)
					 nodeKey$=Identity.key();
			    itemLocator.setProperty(NODE_GROUP_KEY, groupKey$);
			    itemLocator.setProperty(NODE_KEY, nodeKey$);
			    itemLocator.setProperty(NODE_TYPE, NODE_TYPE_REFERENCE);
			    String[]  la=listGroupLocators(entity, groupKey$);
      		    itemLocator$=Locator.toString(itemLocator);
      		  if(!contains(itemLocator$, la)){
    		      index.putElementItem("index.jlocator", new Core(null,nodeKey$,itemLocator$));
    		      index=orderGroupDefault(index, groupKey$);
      		  }
    		
			}
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}

		return index;
	}
	class NodeRenderer extends DefaultTreeCellRenderer {
		private static final long serialVersionUID = 1L;
		public NodeRenderer() {
	    }
       
	    public Component getTreeCellRendererComponent(
	                        JTree tree,
	                        Object value,
	                        boolean sel,
	                        boolean expanded,
	                        boolean leaf,
	                        int row,
	                        boolean hasFocus) {

	        super.getTreeCellRendererComponent(
	                        tree, value, sel,
	                        expanded, leaf, row,
	                        hasFocus);
	        JLabel label = (JLabel) this ;
            label.setText("Node"); 
	        if(value!=null){
	        	selectedNode=(DefaultMutableTreeNode)value;
	        	Object userObject=((DefaultMutableTreeNode)value).getUserObject();
	        	if(debug)
	        	System.out.println("JIndexPanel.NodeRenderer:user objec="+userObject);
	        	try{
	        		Properties locator=Locator.toProperties((String)userObject);
	        		String nodeKey$=locator.getProperty(NODE_KEY);
	        		String title$;
	        		Core title=entity.getElementItem("index.title", nodeKey$);
	        		   if(title!=null&&title.value!=null)
	        			   title$=title.value;
	        		   else
	        		       title$=locator.getProperty(Locator.LOCATOR_TITLE);
	        		label.setText(title$); 
	        		Entigrator entigrator=console.getEntigrator(entihome$);
	        		String icon$=JConsoleHandler.getIcon(entigrator,(String)userObject);
	        		if(icon$!=null){
	        			byte[] ba=Base64.decodeBase64(icon$);
	        	      	  ImageIcon icon = new ImageIcon(ba);
	        	      	  Image image= icon.getImage().getScaledInstance(24, 24, 0);
	        	      	  icon.setImage(image);
	        	      	  label.setIcon(icon); 
	        		}else
	        			if(debug)
	        			  System.out.println("IndexPanel:renderer:icon is null");
	        	}catch(Exception e){
	        		Logger.getLogger(JEntityStructurePanel.class.getName()).severe(e.toString());
	        	}
	        }
	        return this;
	    }

}
		class MousePopupListener extends MouseAdapter {
		  boolean isPopup=false;
			public void mousePressed(MouseEvent e) {
				//System.out.println("EntityStructurePanel:MousePopupListener:mouse pressed");
				if (e.isPopupTrigger())
					isPopup=true;
				else
					isPopup=false;
				//	System.out.println("EntityStructurePanel:MousePopupListener:isPopup="+isPopup);
		    }

		    public void mouseClicked(MouseEvent e) {
		    	if(!isRoot&&isPopup){
		    		     initPopup();
		    		     if(popup!=null)
		         		 popup.show(tree, e.getX(), e.getY());
		    	}
		    }
		    public void mouseReleased(MouseEvent e) {
		    	if(!isPopup)
		    	if (e.isPopupTrigger()) 
			    	  isPopup=true;
		    	}
		   }
	class SelectionListener implements TreeSelectionListener {
		  public void valueChanged(TreeSelectionEvent se) {
		    JTree tree = (JTree) se.getSource();
		    selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		    if(selectedNode==null){
		    	selection$=null;
		    	return;
		    }
		    if (selectedNode.isRoot())
		    	isRoot=true;
		    else
		    	isRoot=false;
		    DefaultMutableTreeNode parent=( DefaultMutableTreeNode)selectedNode.getParent();
		    isFirst=false;
		    if(parent==null||parent.isRoot())
		    	  isFirst=true;
		    Object userObject=selectedNode.getUserObject();
      	selection$=(String)userObject;
      	   //
      	 Properties locator=Locator.toProperties(selection$);
         String selectedNodeKey$=locator.getProperty(NODE_KEY);
         Entigrator entigrator=console.getEntigrator(entihome$);
         entity=entigrator.getEntityAtKey(entityKey$);
         if(!entity.existsElement("index.selection"))
        	 entity.createElement("index.selection");
         entity.putElementItem("index.selection", new Core(null,"selection",selectedNodeKey$));
         entigrator.save(entity);
      	  //
  		  }
}
	static class DefaultNodeComparator implements Comparator<Core>{
	    @Override
	    public int compare(Core c1, Core c2) {
	    	try{
	    		Properties locator=Locator.toProperties(c1.value);
	    		String ty1=locator.getProperty(NODE_TYPE);
	    		String ti1$=locator.getProperty(Locator.LOCATOR_TITLE);
	    		locator=Locator.toProperties(c2.value);
	    		String ty2=locator.getProperty(NODE_TYPE);
	    		String ti2$=locator.getProperty(Locator.LOCATOR_TITLE);
	    		if(!ty1.equals(ty2)){
	    			if(NODE_TYPE_GROUP.equals(ty1)&& !NODE_TYPE_GROUP.equals(ty2))
	    				return -1;
	    			if(!NODE_TYPE_GROUP.equals(ty1)&& NODE_TYPE_GROUP.equals(ty2))
	    				return 1;
	    		}
	    		System.out.println("DefaultNodeComparator:compare:ti1="+ti1$+" ti2="+ti2$+" ret="+ti1$.compareToIgnoreCase(ti2$));
	    		return +ti1$.compareToIgnoreCase(ti2$);
	    	}catch(Exception e){
	    		System.out.println("DefaultNodeComparator:compare:"+e.toString());
	    		return 0;
	    		
	    	}
	    }
	}
	static class NumberNodeComparator implements Comparator<Core>{
	    @Override
	    public int compare(Core c1, Core c2) {
	    	try{
	    		int i1=Integer.parseInt(c1.type);
	    		int i2=Integer.parseInt(c2.type);
	    		
	    		return i1-i2;
	    	}catch(Exception e){
	    		//System.out.println("NumberNodeComparator:compare:"+e.toString());
	    		return 0;
	    		
	    	}
	    }
	}
	/**
	 * Add referenced entities into the referenced entities list.
	 * @param entigrator the entigrator.
	 * @param entityKey$ the entity key.
	 * @param rel the referenced entities list. 
	 */	
	@Override
	public void collectReferences(Entigrator entigrator, String entityKey$, ArrayList<JReferenceEntry> rel) {
	    try{
	//    	System.out.println("JIndexPanel:collectReferences:BEGIN:entity key="+entityKey$+ " base ="+entigrator.getEntihome());
	    	JReferenceEntry jre;
	    	entity =entigrator.getEntityAtKey(entityKey$);
	    	Core[] ca=entity.elementGet("index.jlocator");
//	    	System.out.println("JIndexPanel:collectReferences:ca="+ca.length);
	    	if(ca!=null){
	    		String memberKey$;
	    		for(Core c:ca){
	//    			System.out.println("JIndexPanel:collectReferences:c value="+c.value);
	    			memberKey$=Locator.getProperty(c.value,EntityHandler.ENTITY_KEY);
	 //   			System.out.println("JIndexPanel:collectReferences:member="+memberKey$);
	    			if(memberKey$!=null){
	    				jre=new JReferenceEntry(entigrator,memberKey$,getFacetHandler());
	    				if(jre!=null)
	    					JReferenceEntry.putReference(jre, rel);
	    			}
	    		}
	    	}
	   // 	System.out.println("JIndexPanel:collectReferences:rel="+rel.size());	
	    }catch(Exception e){
	   	 Logger.getLogger(getClass().getName()).severe(e.toString());
	   	  }
	   }
	
private boolean hasSelectedItems(){
	 TreePath[] tpa=tree.getSelectionPaths();
	 if(tpa==null)
		 return false;
	 else
		 if(tpa.length>0){
			 for(TreePath tp:tpa){
				 Properties locator=Locator.toProperties((String)((DefaultMutableTreeNode)tp.getLastPathComponent()).getUserObject());
				 String nodeType$=locator.getProperty(NODE_TYPE);
				 if(NODE_TYPE_ROOT.equals(nodeType$))
					 return false;
				
				 if(NODE_TYPE_GROUP.equals(nodeType$))//&&"parent".equals(nodeKey$))
					 return false;
				// System.out.println("JIndexPanel:hasSelectedItems:tp="+tp.getLastPathComponent().toString());
			 }
			return true;	 
		 }
	 return false;
}
private boolean contains(String locator$,String[] la){
	try{
       Properties locator=Locator.toProperties(locator$);
       String candidateKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
       String memberKey$;
       String type$=locator.getProperty(Locator.LOCATOR_TYPE);
       String candidateFile$=locator.getProperty(JFolderPanel.FILE_NAME);
       String memberFile$;
       for(String l:la){
    	   locator=Locator.toProperties(l);
    	   memberKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
    	   if(debug)
    	   System.out.println("JIndexPanel:contains:key member="+memberKey$+" candidate="+candidateKey$);
    	   if(candidateKey$.equals(memberKey$)){
    		   if(!JFolderPanel.LOCATOR_TYPE_FILE.equals(type$))
    			   return true;
    		   else{
    			   memberFile$=locator.getProperty(JFolderPanel.FILE_NAME);
    			   if(candidateFile$!=null&&candidateFile$.equals(memberFile$))
    				   return true;
    		   }
		   }
       }
	}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
	}
	return false;
	
}
@Override
public void activate() {
	if(debug)
		System.out.println("JIndexPanel:activate:begin");
	if(ignoreOutdate){
		ignoreOutdate=false;
		return;
	}
	Entigrator entigrator=console.getEntigrator(entihome$);
	if(entity==null)
		return;
	if(!entigrator.ent_outdated(entity)){
		System.out.println("JIndexPanel:activate:up to date");
		return;
	}
	int n=new ReloadDialog(this).show();
	if(2==n){
		ignoreOutdate=true;
		return;
	}
	if(1==n){
		entigrator.save(entity);
		//JConsoleHandler.execute(console, getLocator());
	}
	if(0==n){
		 JConsoleHandler.execute(console, getLocator());
		}
	
	
}
public static String getWebItems(Entigrator entigrator, String locator$) {
	try{
	//	System.out.println("IndexPanel.instantiate:locator="+locator$);
		
		Properties locator=Locator.toProperties(locator$);
		String entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
		String entityKey$=entigrator.indx_keyAtLabel(entityLabel$);
		String webHome$=locator.getProperty(WContext.WEB_HOME);
		String webRequester$=locator.getProperty(WContext.WEB_REQUESTER);
        Sack  index=entigrator.getEntityAtKey(entityKey$);
		 StringBuffer sb=new StringBuffer();
		 String icon$=entigrator.readIconFromIcons(index.getAttributeAt("icon"));
		 sb.append(getItem(entityKey$,NODE_TYPE_GROUP,icon$, webHome$, entityLabel$, locator$));
		 sb.append("<ul>");
         ArrayList<String>sl=listItems( entigrator,index,webHome$);
         if(sl!=null&&sl.size()>0)
        	 for(String s:sl)
        		 sb.append(s);
         sb.append("</ul></li>"); 
		return sb.toString(); 
	}catch(Exception e){
        Logger.getLogger(JIndexPanel.class.getName()).severe(e.toString());
	}
	return null;
}
private static String getItem(String nodeKey$,String nodeType$,String icon$, String url$, String title$,String locator$){
	  String iconTerm$=" data-jstree='{\"icon\":\"data:image/png;base64,"+icon$+"\"}' width=\"24\" height=\"24\"";
	  if(debug){
			 
		  System.out.println("JIndexPanel:getWebView:icon term="+iconTerm$);
		  System.out.println("JIndexPanel:getWebView:locator="+locator$);
	  }
	  String href$="";
	  String item$= "<li id='"+nodeKey$+"' type='"+nodeType$+"'"+iconTerm$+">"+title$;
	  if(locator$!=null){
        
		 String enLocator$= Base64.encodeBase64URLSafeString(locator$.getBytes());
	     href$=url$+"?"+WContext.WEB_LOCATOR+"="+enLocator$;
    	 item$= "<li id='"+nodeKey$+"' type='"+nodeType$+"' ref='"+href$+"'"+" locator='"+enLocator$+"'"+iconTerm$+">"+title$;
	  }
	  if(debug)
			 System.out.println("JIndexPanel:getWebView:item="+item$);
	  return item$;
}
private static ArrayList<String> listItems(Entigrator  entigrator,Sack index,String url$){
	ArrayList<String>sl=new ArrayList<String>();
	addItems(entigrator,index,url$,"parent", sl);
	return sl;
}
private static Core[] getGroupItems(Sack index,String groupKey$){
	ArrayList<Core>cl=new ArrayList<Core>();
	Core[] ca=index.elementGet("index.jlocator");
	if(ca==null||ca.length<1||groupKey$==null)
		return null;
	String groupCandidate$;
	for(Core c:ca){
		groupCandidate$=Locator.getProperty(c.value, NODE_GROUP_KEY);
		if(groupKey$.equals(groupCandidate$))
			cl.add(c);
	}
	Core[]ia= cl.toArray(new Core[0]);
	if(debug)
	System.out.println("JIndexPanel:getWebView:groupKey="+groupKey$+" selection="+ia.length);
	Core.sortAtIntType(ia);
	if(debug)
	for(Core c:ia){
		System.out.println("JIndexPanel:getWebView:ia type="+c.type);
	}
	return ia;
}
private static void addItems(Entigrator entigrator,Sack index,String url$,String groupKey$, ArrayList<String>sl){
	try{
		Core[] ca=getGroupItems(index,groupKey$);
		if(ca==null||ca.length<1)
			return ;
		Properties itemLocator;
		String icon$;
		String title$;
		//String itemKey$;
		String nodeType$;
		String itemType$;
		for(Core c:ca){
			try{
				itemLocator=Locator.toProperties(c.value);
				itemType$=itemLocator.getProperty(Locator.LOCATOR_TYPE);
				if(groupKey$.equals(itemLocator.getProperty(NODE_GROUP_KEY))){
					//icon$=WUtils.scaleIcon(itemLocator.getProperty(Locator.LOCATOR_ICON));
					icon$=WUtils.scaleIcon(JConsoleHandler.getIcon(entigrator, c.value));
					title$=itemLocator.getProperty(Locator.LOCATOR_TITLE);
					nodeType$=itemLocator.getProperty(NODE_TYPE);
					if(NODE_TYPE_GROUP.equals(nodeType$)){
						sl.add(getItem(c.name,NODE_TYPE_GROUP,icon$, url$, title$,null));
						sl.add("<ul>");
						addItems( entigrator, index, url$,c.name, sl);
						sl.add("</ul></li>");
						continue;
					}
					itemLocator.setProperty(Entigrator.ENTIHOME, entigrator.getEntihome());
					itemLocator.setProperty(EntityHandler.ENTITY_LABEL, title$);
					if(JFolderPanel.LOCATOR_TYPE_FILE.equals(itemType$))
						itemLocator.setProperty(BaseHandler.HANDLER_CLASS,JFolderPanel.class.getName());
					else
						if(JWeblinksPanel.LOCATOR_TYPE_WEB_LINK.equals(itemType$))
							
					      itemLocator.setProperty(BaseHandler.HANDLER_CLASS,JWebsetFacetOpenItem.class.getName());
						else
							itemLocator.setProperty(BaseHandler.HANDLER_CLASS,JEntityFacetPanel.class.getName());
					itemLocator.setProperty(WContext.WEB_HOME,url$);
					itemLocator.setProperty(WContext.WEB_REQUESTER,JIndexFacetOpenItem.class.getName());
					if(debug)
						System.out.println("JIndexPanel:addItems:itemLocator="+Locator.toString(itemLocator));
					sl.add(getItem(c.name,NODE_TYPE_REFERENCE,icon$, url$, title$,Locator.toString(itemLocator))+"</li>");
					
				}
			}catch(Exception ee){
				//LOGGER.info(ee.toString());
				Logger.getLogger(JIndexPanel.class.getName()).info(ee.toString());
			}
		}
	}catch(Exception e){
		Logger.getLogger(JIndexPanel.class.getName()).severe(e.toString());
	}

}
@Override
public String getFacetOpenItem() {
	// TODO Auto-generated method stub
	return JIndexFacetOpenItem.class.getName();
}
@Override
public String getFacetIcon() {
	
	return "index.png";
}
}