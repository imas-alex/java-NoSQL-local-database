package gdt.jgui.entity;
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
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
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
import gdt.data.entity.FacetHandler;
import gdt.data.entity.facet.FolderHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Identity;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.jgui.base.JBaseNavigator;
import gdt.jgui.base.JBasesPanel;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JFacetOpenItem;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.console.ReloadDialog;
import gdt.jgui.console.WContext;
import gdt.jgui.console.WUtils;
import gdt.jgui.entity.fields.JFieldsFacetOpenItem;
import gdt.jgui.entity.folder.JFolderFacetOpenItem;
import gdt.jgui.entity.folder.JFolderPanel;
import gdt.jgui.entity.index.JIndexPanel;
import gdt.jgui.entity.webset.JWeblinksPanel;
/**
 * Displays the digest view of the entity.
 * @author imasa
 *
 */
public  class JEntityDigestDisplay extends JPanel implements JContext ,JRequester,WContext{
	private static final long serialVersionUID = 1L;
	private static final String DIGEST="Digest";
	private static final String LOCATOR_TYPE_FACET="locator type facet";
	/**
	 * The tag of the facet component locator. 
	 */
	public static final String LOCATOR_FACET_COMPONENT="locator facet component";
	/**
	 * The tag of the selected locator.
	 */
	public static final String SELECTION="selection";
	/**
	 * The tag of the component key.
	 */
	public static final String COMPONENT_KEY="component key";
	/**
	 * The tag of the key of the entity (digest owner). 
	 */
	public static final String ROOT_ENTITY_KEY ="root entity key";
	private static final String NODE_NUMBER="node number";
	//private static final String NODE_TYPE_GROUP="node type group";
	/**
	 * The tag of the node type.
	 */
	public static final String NODE_TYPE="node type";
	public static final String NODE_TYPE_PROPERTY="node type property";
	private static final String NODE_TYPE_ROOT="node type root";
	private static final String NODE_TYPE_PARENT="node type parent";
	private static final String NODE_TYPE_FACET_HEADER="node type facet header";
	private static final String NODE_TYPE_GROUP="node type group";
	private static final String NODE_TYPE_REFERENCE="node type reference";
	/**
	 * Indicates that the node represents a facet.
	 */
	public static final String NODE_TYPE_FACET_OWNER="node type facet owner";
	private String entihome$;
    private String entityKey$;
    private String entityLabel$;
    String locator$;
    private DefaultMutableTreeNode node;
    private JMainConsole console;
    private Entigrator entigrator;
    String[] facets;
    JScrollPane scrollPane;
    JTree tree;
    boolean isRoot=true;
    boolean isFirst=true;
	String selection$;
	JPopupMenu popup;
	int nodeNumber=0;
	static boolean debug=false;
	boolean ignoreOutdate=false;
	Sack entity;
	DefaultMutableTreeNode parentNode;
	/**
	 * The default constructor.
	 */
	public JEntityDigestDisplay() {
		super();
    }
	/**
	 * Get the context locator.
	 * @return the context locator.
	 */
	@Override
	public String getLocator() {
		 Properties locator=new Properties();
		    locator.setProperty(Locator.LOCATOR_TYPE, JContext.CONTEXT_TYPE);
		    locator.setProperty(JContext.CONTEXT_TYPE,getType());
		    if(entihome$!=null)
		       locator.setProperty(Entigrator.ENTIHOME,entihome$);
		    if(entityKey$!=null)
			       locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
		    if(entityLabel$!=null)
			       locator.setProperty(EntityHandler.ENTITY_LABEL,entityLabel$);
		    if(selection$!=null)
			       locator.setProperty(SELECTION,Locator.compressText(selection$));
		    locator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
			locator.setProperty(Locator.LOCATOR_ICON_CLASS,getClass().getName());
			locator.setProperty(Locator.LOCATOR_ICON_FILE,"digest.png");
		    locator.setProperty(Locator.LOCATOR_TITLE, getTitle());
		   locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
		   locator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
		   return Locator.toString(locator);
	}
	/**
	 * Create the context.
	 * @param console the main console.
	 * @param locator$ the locator.
	 * @return the digest display.
	 */
	@Override
	public JContext instantiate(JMainConsole console, String locator$) {
		try{
			if(debug)
				 System.out.println("JEntityDigestDisplay:instantiate(console):locator="+locator$);
		this.removeAll();	
		this.console=console;
		this.locator$=locator$;
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		scrollPane = new JScrollPane();
		add(scrollPane);
		 Properties locator=Locator.toProperties(locator$);
		 entihome$=locator.getProperty(Entigrator.ENTIHOME);
		 entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		 entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
		 if(entigrator==null)
		 entigrator=console.getEntigrator(entihome$);
		 entity=entigrator.getEntity(entityKey$);
		 String compressedSelection$=locator.getProperty(SELECTION);
		 if(compressedSelection$!=null){
			try{
			 byte[] ba=Base64.decodeBase64(compressedSelection$);
			 selection$=new String(ba,"UTF-8");
			}catch(Exception ee){
				Logger.getLogger(getClass().getName()).info(ee.toString());
			}
		 }
		 
		 DefaultMutableTreeNode root = new DefaultMutableTreeNode(entityLabel$);
		 locator=new Properties();
		 locator.setProperty(Locator.LOCATOR_TITLE, DIGEST);
		 locator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
		locator.setProperty(Locator.LOCATOR_ICON_CLASS,getClass().getName());
		locator.setProperty(Locator.LOCATOR_ICON_FILE,"digest.png");
		 locator.setProperty(NODE_TYPE, NODE_TYPE_ROOT);
		 root.setUserObject(Locator.toString(locator));
		 DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode(entityLabel$);
		 root.add(parentNode);
		 Sack parent=entigrator.getEntityAtKey(entityKey$);
		 String parentLocator$=EntityHandler.getEntityLocator(entigrator, parent);			
		 parentLocator$=Locator.append(parentLocator$, NODE_TYPE, NODE_TYPE_PARENT);
		 parentNode.setUserObject(parentLocator$);
		 tree=new JTree(root);
		 tree.addTreeSelectionListener(new SelectionListener());
		 tree.setShowsRootHandles(true);
		 tree.setCellRenderer(new NodeRenderer()); 
		 tree.addMouseListener(new MousePopupListener());
		 scrollPane.getViewport().add(tree);
		 facets=listFacetOpenItems();
		 DefaultMutableTreeNode []na=getFacetOpenItems();
		 if(na!=null)
			 for(DefaultMutableTreeNode n:na){
				 parentNode.add(n);
			 }
		 expandTree(tree,true);
		 select();
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		}
		 return this;
	}
	
	public JTree instantiate(Entigrator entigrator, String locator$) {
		try{
		 if(debug)
			 System.out.println("JEntityDigestDisplay:instantiate:locator="+locator$);
		 this.entigrator=entigrator;
		 Properties locator=Locator.toProperties(locator$);
		 entihome$=locator.getProperty(Entigrator.ENTIHOME);
		 if(entigrator==null)
			 entigrator=console.getEntigrator(entihome$);
		 entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		 entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
		 entity=entigrator.getEntity(entityKey$);
		 DefaultMutableTreeNode root = new DefaultMutableTreeNode(entityLabel$);
		 locator=new Properties();
		 locator.setProperty(Locator.LOCATOR_TITLE, DIGEST);
		 locator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
			locator.setProperty(Locator.LOCATOR_ICON_CLASS,getClass().getName());
			locator.setProperty(Locator.LOCATOR_ICON_FILE,"digest.png");
		 locator.setProperty(NODE_TYPE, NODE_TYPE_ROOT);
		 root.setUserObject(Locator.toString(locator));
		 parentNode = new DefaultMutableTreeNode(entityLabel$);
		 root.add(parentNode);
		 Sack parent=entigrator.getEntityAtKey(entityKey$);
		 String parentLocator$=EntityHandler.getEntityLocatorAtKey(entigrator,parent.getKey());			
		 parentLocator$=Locator.append(parentLocator$, NODE_TYPE, NODE_TYPE_PARENT);
		 parentNode.setUserObject(parentLocator$);
		 tree=new JTree(root);
		 facets=listFacetOpenItems();
		 return tree;
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		}
		 return null;
	}
	/**
	 * Get the context title.
	 * @return the context title.	
	 */
	@Override
	public String getTitle() {
		try{
			if(entityLabel$!=null)
				return entityLabel$;	
			entityLabel$= console.getEntigrator(entihome$).indx_getLabel(entityKey$);
			if(entityLabel$!=null)
				return entityLabel$;	
		return "Digest";
			}catch(Exception e ){
				return "Digest";
			}
	}
	/**
	 * Get the context subtitle.
	 * @return the context subtitle.	
	 */
	@Override
	public String getSubtitle() {
		return entihome$;
	}
	/**
	 * Get the context type.
	 * @return the context type.	
	 */
	@Override
	public String getType() {
		return "Entity digest panel";
	}
	/**
	 * Complete the context. No action.
	 */
	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
	/**
	 * Get the panel to put into the main console.
	 * @return the instance of the digest display.
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
		JMenu menu=new JMenu("Context");
		   menu.setName("Context");
		   JMenuItem facetItem = new JMenuItem("Facets");
		   facetItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
			       Properties locator=Locator.toProperties(locator$);
			       String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			       String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			       JEntityFacetPanel efp=new JEntityFacetPanel();
			       String efpLocator$=efp.getLocator();
			       efpLocator$=Locator.append(efpLocator$, Entigrator.ENTIHOME, entihome$);
			       efpLocator$=Locator.append(efpLocator$, EntityHandler.ENTITY_KEY, entityKey$);
			       JConsoleHandler.execute(console, efpLocator$);
				}
			} );
			menu.add(facetItem);
			
			JMenuItem structureItem = new JMenuItem("Structure");
			   structureItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
				       Properties locator=Locator.toProperties(locator$);
				       String entihome$=locator.getProperty(Entigrator.ENTIHOME);
				       String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
				       JEntityStructurePanel esp=new JEntityStructurePanel();
				       String espLocator$=esp.getLocator();
				       espLocator$=Locator.append(espLocator$, Entigrator.ENTIHOME, entihome$);
				       espLocator$=Locator.append(espLocator$, EntityHandler.ENTITY_KEY, entityKey$);
				       JConsoleHandler.execute(console, espLocator$);
	
					}
				} );
				menu.add(structureItem);
				
				JMenuItem refreshItem = new JMenuItem("Refresh");
				   refreshItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							 Properties locator=Locator.toProperties(locator$);
						       String entihome$=locator.getProperty(Entigrator.ENTIHOME);
						       String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
						       JEntityDigestDisplay edp=new JEntityDigestDisplay();
						       String edpLocator$=edp.getLocator();
						       edpLocator$=Locator.append(edpLocator$, Entigrator.ENTIHOME, entihome$);
						       if(selection$!=null)
						    	   edpLocator$=Locator.append(edpLocator$, SELECTION, Locator.compressText(selection$));
						       edpLocator$=Locator.append(edpLocator$, EntityHandler.ENTITY_KEY, entityKey$);
						       JConsoleHandler.execute(console, edpLocator$);
					}
					} );
					menu.add(refreshItem);
			return menu;
	}
	private boolean instantiateComponentNode(DefaultMutableTreeNode facetComponentNode){
		try{
			if(debug)
				System.out.println("JEntityDigestDisplay:instantiateComponentNode");
			String facetComponentLocator$=(String)facetComponentNode.getUserObject();
			if(debug)
				System.out.println("JEntityDigestDisplay:instantiateComponentNode:facet component locator="+facetComponentLocator$);
			Properties locator=Locator.toProperties(facetComponentLocator$);
			if(entigrator==null&&console!=null)
			    entigrator=console.getEntigrator(entihome$);
			String facetClass$=locator.getProperty(BaseHandler.HANDLER_CLASS);
			
			String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			JFacetOpenItem facetOpenItem=(JFacetOpenItem)JConsoleHandler.getHandlerInstance(entigrator,facetClass$ );
//			System.out.println("EntityDigestDisplay:instantiateComponentNode:facetOpenItem="+facetOpenItem.getClass().getName());
			String facetOpenItemLocator$=facetOpenItem.getLocator();
			facetOpenItemLocator$=Locator.append(facetComponentLocator$,Entigrator.ENTIHOME, entihome$);
			facetOpenItemLocator$=Locator.append(facetComponentLocator$,EntityHandler.ENTITY_KEY, entityKey$);
			if(console!=null)
			   facetOpenItem.instantiate(console, facetOpenItemLocator$);
			else
				facetOpenItem.instantiate(entigrator, facetOpenItemLocator$);
			if(debug)
				System.out.println("JEntityDigestDisplay:instantiateComponentNode:facet open item ="+facetOpenItem.getClass().getName());
			
			DefaultMutableTreeNode[] na=facetOpenItem.getDigest(entigrator,facetOpenItemLocator$);
			if(na==null){
				return false;
			}
			String nodeLocator$;
		    for(DefaultMutableTreeNode aNa:na){
		    	nodeLocator$=(String)aNa.getUserObject();
		    	nodeLocator$=Locator.append(nodeLocator$, NODE_NUMBER, String.valueOf(nodeNumber++));
		    	nodeLocator$=Locator.append(nodeLocator$, COMPONENT_KEY, entityKey$);
		    	aNa.setUserObject(nodeLocator$);
		    	facetComponentNode.add(aNa);
		    	setSubnodesNumbers(aNa);
		    }
		    return true;
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		}
		return false;
	}
private boolean instantiateFacetNode(DefaultMutableTreeNode facetNode){
		try{
		boolean notEmpty=false;
			String facetNodeLocator$=(String)facetNode.getUserObject();
		Properties locator=Locator.toProperties(facetNodeLocator$);
		if(console!=null&&entigrator==null)
		   entigrator=console.getEntigrator(entihome$);
		String facetClass$=locator.getProperty(BaseHandler.HANDLER_CLASS);
		if(debug)
		System.out.println("JEntityDigestDisplay:instantiateFacetNode:facet class="+facetClass$);
		JFacetOpenItem facetOpenItem=(JFacetOpenItem)JConsoleHandler.getHandlerInstance(entigrator,facetClass$ );
		FacetHandler facetHandler=facetOpenItem.getFacetHandler();
//		System.out.println("EntityDigestDisplay:instantiateFacetNode:facet handler="+facetHandler.getClassName());
		
		if(debug){
			Sack entity=entigrator.getEntityAtKey(entityKey$);
			System.out.println("JEntityDigestDisplay:instantiateFacetNode:entity key="+entityKey$);
			if(entity==null)
				System.out.println("JEntityDigestDisplay:instantiateFacetNode:entity is NULL");
		}
		

		String entityLocator$=EntityHandler.getEntityLocatorAtKey(entigrator, entityKey$);
//		System.out.println("EntityDigestDisplay:instantiateFacetNode:entity locator="+entityLocator$);
		DefaultMutableTreeNode facetComponentNode;
		if(debug)
			System.out.println("JEntityDigestDisplay:instantiateFacetNode:entity locator="+entityLocator$);
			
		if(facetHandler.isApplied(entigrator, entityLocator$)){
			if(debug)
			System.out.println("EntityDigestDisplay:instantiateFacetNode:applied facet="+facetClass$);
			entityLocator$=Locator.append(entityLocator$, Locator.LOCATOR_TYPE, LOCATOR_FACET_COMPONENT);
			entityLocator$=Locator.append(entityLocator$, BaseHandler.HANDLER_CLASS, facetClass$);
			entityLocator$=Locator.append(entityLocator$, NODE_TYPE, NODE_TYPE_FACET_OWNER);
			entityLocator$=Locator.append(entityLocator$, NODE_NUMBER, String.valueOf(nodeNumber++));
			facetComponentNode=new DefaultMutableTreeNode();
			facetComponentNode.setUserObject(entityLocator$);
			if(instantiateComponentNode(facetComponentNode)){
			facetNode.add(facetComponentNode);
			notEmpty=true;
			}
		}else{
			if(debug)
				System.out.println("EntityDigestDisplay:instantiateFacetNode:not applied facet="+facetClass$);
		}
		
		String[] sa=entigrator.ent_listComponents(entity);
		if(sa!=null){
			Sack component;
			for(String aSa:sa){
				component=entigrator.getEntityAtKey(aSa);
				if(component==null)
					continue;
				entityLocator$=EntityHandler.getEntityLocator(entigrator, component);
				if(facetHandler.isApplied(entigrator, entityLocator$)){
					entityLocator$=Locator.append(entityLocator$, Locator.LOCATOR_TYPE, LOCATOR_FACET_COMPONENT);
					entityLocator$=Locator.append(entityLocator$, BaseHandler.HANDLER_CLASS, facetClass$);
					facetComponentNode=new DefaultMutableTreeNode();
					facetComponentNode.setUserObject(entityLocator$);
					instantiateComponentNode(facetComponentNode);
					facetNode.add(facetComponentNode);
					notEmpty=true;
				}
			}
		}
		int cnt=facetNode.getChildCount();
		if(cnt<1){
			if(debug)
				System.out.println("EntityDigestDisplay:instantiateFacetNode:empty facet node");
			notEmpty=false;
		}
		return notEmpty;
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		}
		return false;
	}
	private DefaultMutableTreeNode[] getFacetOpenItems(){
		String[] sa=listFacetOpenItems();
		if(sa==null)
			return null;
		DefaultMutableTreeNode facetNode;
		JFacetOpenItem facetOpenItem;
		Properties locator;
		//entigrator=console.getEntigrator(entihome$);
		ArrayList<DefaultMutableTreeNode>nl=new ArrayList<DefaultMutableTreeNode>();
		String foiLocator$;
		for(String aSa:sa){
			try{
				if(debug)
			System.out.println("JEntityDigestDisplay:getFacetOpenItems:foi="+aSa);
				facetOpenItem=(JFacetOpenItem)JConsoleHandler.getHandlerInstance(entigrator,aSa );
			    foiLocator$=facetOpenItem.getLocator();
				locator=Locator.toProperties(foiLocator$);
				locator.setProperty(NODE_TYPE, NODE_TYPE_FACET_HEADER);
				locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
				facetNode=new DefaultMutableTreeNode();
				facetNode.setUserObject(Locator.toString(locator));
				//nl.add(facetNode);
				if(instantiateFacetNode(facetNode)){
					if(debug)
						System.out.println("JEntityDigestDisplay:getFacetOpenItems:add facet node="+facetNode.getUserObject());
						
					nl.add(facetNode);
				}
			}catch(Exception ee){
				Logger.getLogger(getClass().getName()).info(ee.toString());	
			}
		}
		Collections.sort(nl, new NodeComparator());
		return nl.toArray(new DefaultMutableTreeNode[0]);
	}
	private String[] listFacetOpenItems(){
		try{
			Sack entity=entigrator.getEntityAtKey(entityKey$);
			String[] sa=entigrator.ent_listComponentsCascade(entity);
			Core[]ca=entity.elementGet("jfacet");
			Stack<String> s=new Stack<String>();
			if(ca!=null)
				   for(Core c:ca)
					   Support.addItem(c.value, s);
			if(sa!=null){
				Sack component;
				for(String aSa:sa){
					component=entigrator.getEntityAtKey(aSa);
					if(component==null)
						continue;
					ca=component.elementGet("jfacet");
					if(ca!=null)
						   for(Core c:ca)
							   Support.addItem(c.value, s);
				}
			}
			if(debug)
				System.out.println("JEntityDigestDisplay:listFacetOpenItems:s="+s.size());
			return s.toArray(new String[0]);
		}catch(Exception e ){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		}
		return null;
	}
	private void expandTree(JTree tree, boolean expand) {
        TreeNode root = (TreeNode) tree.getModel().getRoot();
        expandAll(tree, new TreePath(root), expand);
    }
    private static void expandAll(JTree tree, TreePath path, boolean expand) {
        TreeNode node = (TreeNode) path.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            @SuppressWarnings("unchecked")
			Enumeration<DefaultMutableTreeNode> enumeration = node.children();
            while (enumeration.hasMoreElements()) {
            	DefaultMutableTreeNode n =  enumeration.nextElement();
                TreePath p = path.pathByAddingChild(n);
                expandAll(tree, p, expand);
            }
        }
        if (expand) {
            tree.expandPath(path);
        } else {
            tree.collapsePath(path);
        }
    }
   private void setSubnodesNumbers(DefaultMutableTreeNode node){
	   try{
		   Enumeration en = node.preorderEnumeration();
	       DefaultMutableTreeNode child;
	       String locator$;
	       while(en.hasMoreElements()){
	        	 child=(DefaultMutableTreeNode ) en.nextElement();
	        	 locator$=(String)child.getUserObject();
	        	 locator$=Locator.append(locator$, NODE_NUMBER,String.valueOf(nodeNumber++));
	        	 child.setUserObject(locator$);
	       }
	   }catch(Exception e){
		   Logger.getLogger(getClass().getName()).severe(e.toString());
	   }
   }
    private void select(){
          if(selection$==null)
        	  return;
         try{
          DefaultMutableTreeNode root=(DefaultMutableTreeNode)tree.getModel().getRoot();	 
          Enumeration en = root.preorderEnumeration();
          DefaultMutableTreeNode node;
         // System.out.println("EntityDigestDisplay:select:selection="+selection$);
          Properties locator=Locator.toProperties(selection$);
          String selectionNumber$=locator.getProperty(NODE_NUMBER);
//          System.out.println("EntityDigestDisplay:select:selection number="+selectionNumber$);
          String nodeLocator$;
          String nodeNumber$; 
          while(en.hasMoreElements()){
        	 node=(DefaultMutableTreeNode ) en.nextElement();
        	 nodeLocator$=(String)node.getUserObject();
        	 locator=Locator.toProperties(nodeLocator$);
        	 nodeNumber$=locator.getProperty(NODE_NUMBER);
//        	 System.out.println("EntityDigestDisplay:select:node number="+nodeNumber$);
        	 if(selectionNumber$.equals(nodeNumber$)){
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
    private JPopupMenu getFacetComponentMenu(){

		JPopupMenu	popup = new JPopupMenu();
		JMenuItem openItem=new JMenuItem("Open");
		   popup.add(openItem);
		   openItem.setHorizontalTextPosition(JMenuItem.RIGHT);
		   openItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
				   if(debug)
					System.out.println("JEntityDigestDisplay:open: locator="+selection$);
				   try{
					   Properties locator=Locator.toProperties(selection$);
					   String entihome$=locator.getProperty(Entigrator.ENTIHOME);
					   String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
					   JEntityFacetPanel efp=new JEntityFacetPanel();
					   String efpLocator$=efp.getLocator();
					   efpLocator$=Locator.append(efpLocator$, Entigrator.ENTIHOME, entihome$);
					   efpLocator$=Locator.append(efpLocator$, EntityHandler.ENTITY_KEY, entityKey$);
					   //System.out.println("JEmailFacetOpenItem:edit:text editor="+teLocator$);
					   JConsoleHandler.execute(console, efpLocator$);
				   }catch(Exception ee){
					   Logger.getLogger(JEntityDigestDisplay.class.getName()).info(ee.toString());
				   }
				}
			    });
		return popup;
    }
    private JPopupMenu getCollapsePopupMenu() {
    	//System.out.println("JEntityDigestDisplay:getCollapsePopupMenu:selection="+Locator.remove(selection$, Locator.LOCATOR_ICON));
    	JPopupMenu	popup = new JPopupMenu();
    	JMenuItem collapseItem=new JMenuItem("Collapse");
    	   popup.add(collapseItem);
    	   collapseItem.setHorizontalTextPosition(JMenuItem.RIGHT);
    	   collapseItem.addActionListener(new ActionListener() {
    			@Override
    			public void actionPerformed(ActionEvent e) {
    			   try{
    				   node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent(); 
    				   int cnt=node.getChildCount();
    				  Stack<DefaultMutableTreeNode>s=new Stack<DefaultMutableTreeNode>();  
    				   if(cnt>0){
    					   DefaultMutableTreeNode child;
    					   for(int i=0;i<cnt;i++){
    						   child=(DefaultMutableTreeNode)node.getChildAt(i);
    						   s.push(child);
    					   }
    				   }
    				   while(!s.isEmpty())
    					   tree.collapsePath(new TreePath(s.pop().getPath()));	   
    				      }catch(Exception ee){
    			   }
    			}
    		    });
    	return popup;
    }
    private void initPopup(){
    	try{
//   		System.out.println("JEntityDigestDisplay:initPopup:selection="+selection$);
    		Properties locator=Locator.toProperties(selection$);
    	    String nodeType$=locator.getProperty(NODE_TYPE);
    	    if(NODE_TYPE_ROOT.equals(nodeType$)){
        	    	popup=null;
        	    	return;
        	    }
    	    if(NODE_TYPE_PARENT.equals(nodeType$)
        	    	||NODE_TYPE_FACET_HEADER.equals(nodeType$)){
        	    	popup=getCollapsePopupMenu();
        	    	return;
        	    }
    	    
    	   if(LOCATOR_FACET_COMPONENT.equals(locator.getProperty(Locator.LOCATOR_TYPE))){
    	//	   System.out.println("JEntityDigestDisplay:initPopup:locator facet component"); 
    	       popup=getFacetComponentMenu();
    	       return;
    	   }
    		String componentKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
    		String facetClass$=locator.getProperty(BaseHandler.HANDLER_CLASS);
    	    Entigrator entigrator=console.getEntigrator(entihome$);
    	    JFacetOpenItem facetOpenItem=(JFacetOpenItem)JConsoleHandler.getHandlerInstance(entigrator, facetClass$);
    	    String foiLocator$=facetOpenItem.getLocator();
    	    foiLocator$=Locator.append(foiLocator$,Entigrator.ENTIHOME, entihome$);
    	    foiLocator$=Locator.append(foiLocator$,EntityHandler.ENTITY_KEY,componentKey$);
    	    foiLocator$=Locator.append(foiLocator$,ROOT_ENTITY_KEY,entityKey$);
    	    foiLocator$=Locator.append(foiLocator$,JFacetOpenItem.DO_NOT_OPEN,Locator.LOCATOR_TRUE);
    	    facetOpenItem.instantiate(console, foiLocator$);
    	    String digestLocator$=getLocator();
    	    digestLocator$=Locator.append(digestLocator$,BaseHandler.HANDLER_METHOD, "response");
    	    digestLocator$=Locator.append(digestLocator$,EntityHandler.ENTITY_KEY,componentKey$);
    	    digestLocator$=Locator.append(digestLocator$,ROOT_ENTITY_KEY,entityKey$);
  //  	    System.out.println("EntityDigestDisplay:initPopup:digest locator="+digestLocator$);
   // 	    System.out.println("EntityDigestDisplay:facet open item="+facetOpenItem.getClass().getName());
    	    popup=facetOpenItem.getPopupMenu(digestLocator$);
    	}catch(Exception e){
    	Logger.getLogger(getClass().getName()).severe(e.toString());	
    	}
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
	        	node=(DefaultMutableTreeNode)value;
	        	Object userObject=((DefaultMutableTreeNode)value).getUserObject();
	        	try{
	        		Properties locator=Locator.toProperties((String)userObject);
	        		String title$=locator.getProperty(Locator.LOCATOR_TITLE);
	        		label.setText(title$);
	        		String icon$=JConsoleHandler.getIcon(entigrator,(String)userObject);
	        		if(debug)
	        			System.out.println("JEntityDigestDisplay:NodeRenderer: user object="+userObject);
	        		if(icon$!=null){
	        			byte[] ba=Base64.decodeBase64(icon$);
	        	      	  ImageIcon icon = new ImageIcon(ba);
	        	      	  Image image= icon.getImage().getScaledInstance(24, 24, 0);
	        	      	  icon.setImage(image);
	        	      	  label.setIcon(icon); 
	        		}
	        	}catch(Exception e){
	        		Logger.getLogger(JEntityStructurePanel.class.getName()).severe(e.toString());
	        	}
	        }
	        return this;
	    }

}
	class SelectionListener implements TreeSelectionListener {
		  public void valueChanged(TreeSelectionEvent se) {
		    JTree tree = (JTree) se.getSource();
		    node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		    if(node==null){
		    	selection$=null;
		    	return;
		    }
		    if (node.isRoot())
		    	isRoot=true;
		    else
		    	isRoot=false;
		    DefaultMutableTreeNode parent=( DefaultMutableTreeNode)node.getParent();
		    isFirst=false;
		    if(parent==null||parent.isRoot())
		    	  isFirst=true;
		    Object userObject=node.getUserObject();
        	selection$=(String)userObject;
        	
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
	    	
	    	if(selection$!=null){
	    		System.out.println("EntityDigestDisplay:MousePopupListener:mouse clicked:selection="+selection$);
	    		
	    	Properties locator=Locator.toProperties(selection$);
			String title$=locator.getProperty(Locator.LOCATOR_TITLE);
		//	System.out.println("EntityDigestDisplay:MousePopupListener:node title="+title$);
	    	}
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
	class NodeComparator implements Comparator<DefaultMutableTreeNode>{
	    @Override
	    public int compare(DefaultMutableTreeNode o1, DefaultMutableTreeNode o2) {
	    	try{
	    	    
	    		String l1$=(String)o1.getUserObject();
	    		Properties locator=Locator.toProperties(l1$);
	    		String t1$=locator.getProperty(Locator.LOCATOR_TITLE);
	    		String l2$=(String)o2.getUserObject();
	    		locator=Locator.toProperties(l2$);
	    		String t2$=locator.getProperty(Locator.LOCATOR_TITLE);
	    		return t1$.compareToIgnoreCase(t2$);
	    	}catch(Exception e){
	    		System.out.println("EntityDigestDisplay:compare:"+e.toString());
	    		return 0;
	    		
	    	}
	    }
	}
	/**
	 * Execute the response locator. No action.
	 */
	@Override
	public void response(JMainConsole console, String locator$) {
		//System.out.println("EntityDigestDisplay:response:locator="+locator$);
	}
	@Override
	public void activate() {
		if(debug)
			System.out.println("JEntityDigestDisplay:activate:begin");
		if(ignoreOutdate){
			ignoreOutdate=false;
			return;
		}
		Entigrator entigrator=console.getEntigrator(entihome$);
		if(entity==null)
			return;
		if(!entigrator.ent_entIsObsolete(entity)){
			if(debug)
			System.out.println("JEntityDigestDisplay:activate:up to date");
			return;
		}
		int n=new ReloadDialog(this).show();
		if(2==n){
			ignoreOutdate=true;
			return;
		}
		if(1==n){
			entigrator.ent_replace(entity);
			
		}
		if(0==n){
			 JConsoleHandler.execute(console, getLocator());
			}
		
		
		
	}
	@Override
	public String getWebView(Entigrator entigrator, String locator$) {
		try{
			Properties locator=Locator.toProperties(locator$);
			String webHome$=locator.getProperty(WContext.WEB_HOME);
			//String entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
			String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			String entityLabel$=null;
			if(entityKey$==null){
				String encodedLabel$=locator.getProperty(JEntitiesPanel.ENCODED_LABEL);
				if(encodedLabel$!=null){
						byte[] ba=Base64.decodeBase64(encodedLabel$);
						entityLabel$=new String(ba,"UTF-8");
						entityKey$=entigrator.indx_keyAtLabel(entityLabel$);
					}else{
				    entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
				    entityKey$=entigrator.indx_keyAtLabel(entityLabel$);
				    if(entityKey$==null){
				    	byte[] ba=Base64.decodeBase64(entityLabel$);
						entityLabel$=new String(ba,"UTF-8");
						entityKey$=entigrator.indx_keyAtLabel(entityLabel$);
				    }
					}
			}else{
				entityLabel$=entigrator.indx_getLabel(entityKey$);
				if(debug)
					System.out.println("JEntityDigestDisplay:found label="+entityLabel$+" at key="+entityKey$);
			}
			String webRequester$=locator.getProperty(WContext.WEB_REQUESTER);
			if(debug)
			System.out.println("JEntityDigestDisplay:locator="+locator$);
			entityKey$=entigrator.indx_keyAtLabel(entityLabel$);
			StringBuffer sb=new StringBuffer();
			sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
			sb.append("<html>");
			sb.append("<head>");
			sb.append(WUtils.getMenuBarScript());
			sb.append(WUtils.getMenuBarStyle());
			sb.append(WUtils.getJquery(entigrator));
			sb.append(WUtils.getJstree(entigrator));
			sb.append("</head>");
		    sb.append("<body onload=\"onLoad()\" >");
		    sb.append("<ul class=\"menu_list\">");
		    sb.append("<li class=\"menu_item\"><a id=\"back\">Back</a></li>");
		    sb.append("<li class=\"menu_item\"><a href=\""+webHome$+"\">Home</a></li>");
		    String navLocator$=Locator.append(locator$, BaseHandler.HANDLER_CLASS, JBaseNavigator.class.getName());
		    navLocator$=Locator.append(navLocator$, Entigrator.ENTIHOME, entigrator.getEntihome());
		    String navUrl$=webHome$+"?"+WContext.WEB_LOCATOR+"="+Base64.encodeBase64URLSafeString(navLocator$.getBytes());
		    sb.append("<li class=\"menu_item\"><a href=\""+navUrl$+"\">Base</a></li>");
		    sb.append("<li class=\"dropdown\">");
		    sb.append("<a href=\"javascript:void(0)\" class=\"dropbtn\">Context</a>");
		    sb.append("<ul class=\"dropdown-content\">");
		    sb.append("<li id=\"expand\" onclick=\"expand()\"><a href=\"#\">Expand</a></li>");
		    sb.append("<li id=\"collapse\" onclick=\"collapse()\"><a href=\"#\">Collaps</a></li>");
		    sb.append("</ul>");
		    sb.append("</li>");
		    sb.append("<li class=\"menu_item\"><a href=\""+WContext.ABOUT+"\">About</a></li>");
		    sb.append("</ul>");
		    sb.append("<table><tr><td>Base:</td><td><strong>");
		    sb.append(entigrator.getBaseName());
		    sb.append("</strong></td></tr><tr><td>Entity: </td><td><strong>");
		    sb.append(entityLabel$);
		    sb.append("</strong></td></tr>");
		    sb.append("<tr><td>Facet: </td><td><strong>EntityViewer</strong></td></tr>");
		    sb.append("<tr><td>Context: </td><td><strong>Digest</strong></td></tr>");
		    sb.append("</table>");
		    sb.append("<div id=\"jstree\">");
		    sb.append("<ul>");
		    sb.append(JEntityDigestDisplay.getWebItems(entigrator, locator$));
		    sb.append("</ul>");
		    sb.append("</div>");
		      sb.append("<script>");
		    
		      sb.append("$(function () {");
		    sb.append("$('#jstree').jstree();");
		    sb.append("$('#jstree').on(\"changed.jstree\", function (e, data) {");
		    sb.append(" var ref=data.instance.get_node(data.selected[0]).li_attr.ref;");
		    sb.append(" var type=data.instance.get_node(data.selected[0]).li_attr.type;");
		    sb.append(" console.log(data.selected);");
		    sb.append(" console.log('type='+type);");
		    sb.append(" console.log('ref='+ref);");
		    sb.append(" if('node type reference'==type){");
		    sb.append(" window.location.assign(ref);");
		    sb.append("}");
		    sb.append("});");
		    sb.append("});");
		    
		    sb.append("function onLoad() {");
		    sb.append("initBack(\""+this.getClass().getName()+"\",\""+webRequester$+"\");");
		    sb.append("}");
		    
		    sb.append("function expand(){");
		    sb.append("$('#jstree').jstree('open_all');");
		    sb.append("}");

		    sb.append("function collapse(){");
		    sb.append("$('#jstree').jstree('close_all');");
		    sb.append("}");
		    sb.append("window.localStorage.setItem(\""+this.getClass().getName()+"\",\""+Base64.encodeBase64URLSafeString(locator$.getBytes())+"\");");
		   
		    sb.append("</script>");
		    sb.append("</body>");
		    sb.append("</html>");
		    return sb.toString();
	        
		}catch(Exception e){
			Logger.getLogger(JBasesPanel.class.getName()).severe(e.toString());	
		}
		return null;
	}
	@Override
	public String getWebConsole(Entigrator entigrator, String locator$) {
		// TODO Auto-generated method stub
		return null;
	}
	public static String getWebItems(Entigrator entigrator, String locator$) {
		try{
			if(debug)
			System.out.println("JEntityDigestDisplay.getWebItems:locator="+locator$);
			
			Properties locator=Locator.toProperties(locator$);
			//String entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
			//String entityKey$=entigrator.indx_keyAtLabel(entityLabel$);
			///
			String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			String entityLabel$=null;
			if(entityKey$==null){
				String encodedLabel$=locator.getProperty(JEntitiesPanel.ENCODED_LABEL);
				if(encodedLabel$!=null){
						byte[] ba=Base64.decodeBase64(encodedLabel$);
						entityLabel$=new String(ba,"UTF-8");
						entityKey$=entigrator.indx_keyAtLabel(entityLabel$);
					}else{
				    entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
				    entityKey$=entigrator.indx_keyAtLabel(entityLabel$);
				    if(entityKey$==null){
				    	byte[] ba=Base64.decodeBase64(entityLabel$);
						entityLabel$=new String(ba,"UTF-8");
						entityKey$=entigrator.indx_keyAtLabel(entityLabel$);
				    }
					}
			}else{
				entityLabel$=entigrator.indx_getLabel(entityKey$);
				if(debug)
					System.out.println("JEntityStructurePanel:found label="+entityLabel$+" at key="+entityKey$);
			}
			
			///
			String webHome$=locator.getProperty(WContext.WEB_HOME);
			//String webRequester$=locator.getProperty(WContext.WEB_REQUESTER);
			Sack  entity=entigrator.getEntityAtKey(entityKey$);
			JEntityDigestDisplay dd=new JEntityDigestDisplay();
			
			String ddLocator$=dd.getLocator();
			ddLocator$=Locator.append(ddLocator$, Entigrator.ENTIHOME,entigrator.getEntihome());
			ddLocator$=Locator.append(ddLocator$, EntityHandler.ENTITY_KEY,entityKey$);
			ddLocator$=Locator.append(ddLocator$, EntityHandler.ENTITY_LABEL,entityLabel$);
			dd.instantiate(entigrator, ddLocator$);
			DefaultMutableTreeNode []na=dd.getFacetOpenItems();
			 if(na!=null)
				 for(DefaultMutableTreeNode n:na)
					 dd.parentNode.add(n);
			
			StringBuffer sb=new StringBuffer();
			visitAllNodes(entigrator,webHome$,dd.parentNode,sb);
			 
			return sb.toString(); 
		}catch(Exception e){
	        Logger.getLogger(JIndexPanel.class.getName()).severe(e.toString());
		}
		return null;
	}
	private static String getItem(Entigrator entigrator,String url$, String locator$){
		try{
		if(debug)
			 System.out.println("JEntityDigestDisplay:getItem:locator="+locator$);
	    Properties locator=Locator.toProperties(locator$);
		String title$=locator.getProperty(Locator.LOCATOR_TITLE);
		String icon$=null;
		String entihome$=entigrator.getEntihome();
		String iconTerm$=null;
		String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		String nodeType$=locator.getProperty(NODE_TYPE);
		String type$=locator.getProperty(Locator.LOCATOR_TYPE);
		String contextType$=locator.getProperty(JContext.CONTEXT_TYPE);
		boolean facetComponent=false;
		if(LOCATOR_FACET_COMPONENT.equals(type$)||JEntityFacetPanel.ENTITY_FACET_PANEL.equals(contextType$))
			facetComponent=true;
	    String href$="";
		String enLocator$=null;
		
		if(NODE_TYPE_PARENT.equals(nodeType$)||NODE_TYPE_FACET_OWNER.equals(nodeType$)){
			String eLocator$=EntityHandler.getEntityLocatorAtKey(entigrator, entityKey$);
			if(debug)
				 System.out.println("JEntityDigestDisplay:getItem:parent locator="+eLocator$);
			icon$=JConsoleHandler.getIcon(entigrator,eLocator$);
			if(icon$!=null)
			    iconTerm$=" data-jstree='{\"icon\":\"data:image/png;base64,"+WUtils.scaleIcon(icon$)+"\"}' width=\"24\" height=\"24\"";
			 String foiLocator$=new JEntityFacetPanel().getLocator();
			 Properties foiLocator=Locator.toProperties(foiLocator$);
       	     foiLocator.setProperty(Locator.LOCATOR_TITLE,"Facets" );
       		 foiLocator.setProperty(Entigrator.ENTIHOME, entihome$);
       		 foiLocator.setProperty(EntityHandler.ENTITY_KEY, entityKey$);
       		 foiLocator.setProperty(EntityHandler.ENTITY_LABEL,entigrator.indx_getLabel(entityKey$));
			 foiLocator.setProperty(WContext.WEB_HOME,url$);
			 foiLocator.setProperty(WContext.WEB_REQUESTER, JEntityDigestDisplay.class.getName());
				if(debug)
					 System.out.println("JEntityDigestDisplay:getItem:node type facet component:foi="+Locator.toString(foiLocator));
				enLocator$= Base64.encodeBase64URLSafeString(Locator.toString(foiLocator).getBytes());			      
		        href$=url$+"?"+WContext.WEB_LOCATOR+"="+enLocator$;
		        
		       // if(debug)
				//	 System.out.println("JEntityDigestDisplay:getItem:facet component ="+href$);
				nodeType$=NODE_TYPE_REFERENCE;
		}
			if(facetComponent){
			String iconContainer$=locator.getProperty(Locator.LOCATOR_ICON_CONTAINER);
			if(debug)
				 System.out.println("JEntityDigestDisplay:getItem:facet component locator="+locator$);
			if(iconContainer$!=null&&iconContainer$.equals(Locator.LOCATOR_ICON_CONTAINER_CLASS)){
				icon$=JConsoleHandler.getIcon(entigrator,locator$);
			}else{
				String eLocator$=EntityHandler.getEntityLocatorAtKey(entigrator, entityKey$);
				if(debug)
					 System.out.println("JEntityDigestDisplay:getItem:facet component locator="+eLocator$);
					icon$=JConsoleHandler.getIcon(entigrator,eLocator$);
			}
			if(icon$!=null)
			    iconTerm$=" data-jstree='{\"icon\":\"data:image/png;base64,"+WUtils.scaleIcon(icon$)+"\"}' width=\"24\" height=\"24\"";
	         String foiLocator$=new JEntityFacetPanel().getLocator();
			 Properties foiLocator=Locator.toProperties(foiLocator$);
       	     foiLocator.setProperty(Locator.LOCATOR_TITLE,"Facets" );
       		 foiLocator.setProperty(Entigrator.ENTIHOME, entihome$);
       		 foiLocator.setProperty(EntityHandler.ENTITY_KEY, entityKey$);
       		 foiLocator.setProperty(EntityHandler.ENTITY_LABEL,entigrator.indx_getLabel(entityKey$));
			 foiLocator.setProperty(WContext.WEB_HOME,url$);
			 foiLocator.setProperty(WContext.WEB_REQUESTER, JEntityDigestDisplay.class.getName());
				if(debug)
					 System.out.println("JEntityDigestDisplay:getItem:node type facet component:foi="+Locator.toString(foiLocator));
				enLocator$= Base64.encodeBase64URLSafeString(Locator.toString(foiLocator).getBytes());			      
		        href$=url$+"?"+WContext.WEB_LOCATOR+"="+enLocator$;
		        
		       // if(debug)
				//	 System.out.println("JEntityDigestDisplay:getItem:facet component ="+href$);
				nodeType$=NODE_TYPE_REFERENCE;
		}
		if(NODE_TYPE_FACET_HEADER.equals(nodeType$)){
			icon$=JConsoleHandler.getIcon(entigrator,locator$);
			if(icon$!=null)
			    iconTerm$=" data-jstree='{\"icon\":\"data:image/png;base64,"+WUtils.scaleIcon(icon$)+"\"}' width=\"24\" height=\"24\"";
		}
		String filePath$=null;
		if(locator.getProperty(JFolderPanel.FILE_PATH)!=null){
		 filePath$=entigrator.getEntihome()+"/"+locator.getProperty(JFolderPanel.FILE_PATH);
		}
		  
		 boolean print=true;
		 boolean typeDone=false;
		
		if(JFieldsFacetOpenItem.NODE_TYPE_FIELD_VALUE.equals(nodeType$)){
				 String fieldName$=locator.getProperty(JFieldsFacetOpenItem.FIELD_NAME);
				 String fieldValue$=locator.getProperty(JFieldsFacetOpenItem.FIELD_VALUE);
				 if(debug)
					 System.out.println("JEntityDigestDisplay:getItem:field name="+fieldName$+" value="+fieldValue$);
		if(fieldValue$==null)
					 fieldValue$="";
		         String foiLocator$=new JFieldsFacetOpenItem().getLocator();
				 Properties foiLocator=Locator.toProperties(foiLocator$);
	        	 foiLocator.setProperty(Locator.LOCATOR_TITLE,"Fields" );
	        		foiLocator.setProperty(Entigrator.ENTIHOME, entihome$);
	        		foiLocator.setProperty(EntityHandler.ENTITY_KEY, entityKey$);
	        		foiLocator.setProperty(EntityHandler.ENTITY_LABEL,entigrator.indx_getLabel(entityKey$));
					foiLocator.setProperty(WContext.WEB_HOME,url$);
					foiLocator.setProperty(WContext.WEB_REQUESTER, JEntityDigestDisplay.class.getName());
					if(debug)
						 System.out.println("JEntityDigestDisplay:getItem:node type field value:foi="+Locator.toString(foiLocator));
					enLocator$= Base64.encodeBase64URLSafeString(Locator.toString(foiLocator).getBytes());			      
			        href$=url$+"?"+WContext.WEB_LOCATOR+"="+enLocator$;
			        
			        if(debug)
						 System.out.println("JEntityDigestDisplay:getItem:field href="+href$);
					nodeType$=NODE_TYPE_REFERENCE;
					typeDone=true;
					icon$=Support.readHandlerIcon(entigrator,JEntityDigestDisplay.class ,"field.png");
					if(icon$!=null)
					    iconTerm$=" data-jstree='{\"icon\":\"data:image/png;base64,"+WUtils.scaleIcon(icon$)+"\"}' width=\"24\" height=\"24\"";
			
				//	item$= "<li id='"+Identity.key()+"' type='"+nodeType$+"' ref='"+href$+"'"+" locator='"+enLocator$+"'"+iconTerm$+">"+title$;
			 }
		
		if(JWeblinksPanel.WEB_LINK_NAME.equals(type$)){
				icon$=JConsoleHandler.getIcon(entigrator,locator$);
			if(icon$!=null)
			    iconTerm$=" data-jstree='{\"icon\":\"data:image/png;base64,"+WUtils.scaleIcon(icon$)+"\"}' width=\"24\" height=\"24\"";
	  		nodeType$=NODE_TYPE_REFERENCE;
				if(title$!=null){
			      String[] sa=title$.split(" > ");
			      if(sa.length>1){
		    	  href$=sa[1];
					 print=false;
					 typeDone=true;
		      }
				}
		}	
		
		
    		 if(filePath$!=null){
					 String fileName$=Locator.getProperty(locator$,JFolderPanel.FILE_NAME);
					 Properties foiLocator=new Properties();
					 String foiTitle$=fileName$;
		        		foiLocator.setProperty(Locator.LOCATOR_TITLE,foiTitle$ );
		        		foiLocator.setProperty(JFolderFacetOpenItem.FACET_HANDLER_CLASS,FolderHandler.class.getName());
		        		foiLocator.setProperty(BaseHandler.HANDLER_CLASS,JFolderPanel.class.getName());
		        		foiLocator.setProperty(Entigrator.ENTIHOME, entihome$);
						foiLocator.setProperty(EntityHandler.ENTITY_KEY, entityKey$);
						foiLocator.setProperty(JFolderPanel.FILE_NAME, foiTitle$);
						//foiLocator.setProperty(JFolderPanel.FILE_PATH, filePath$);
						foiLocator.setProperty(JFolderPanel.FILE_PATH, locator.getProperty(JFolderPanel.FILE_PATH));
						foiLocator.setProperty(Locator.LOCATOR_TYPE, JFolderPanel.LOCATOR_TYPE_FILE);
						foiLocator.setProperty(WContext.WEB_HOME,url$);
						enLocator$= Base64.encodeBase64URLSafeString(Locator.toString(foiLocator).getBytes());			      
				        href$=url$+"?"+WContext.WEB_LOCATOR+"="+enLocator$;
						icon$=JConsoleHandler.getIcon(entigrator,locator$);
				        if(icon$!=null)
				        	 iconTerm$=" data-jstree='{\"icon\":\"data:image/png;base64,"+WUtils.scaleIcon(icon$)+"\"}' width=\"24\" height=\"24\"";
				        nodeType$=NODE_TYPE_REFERENCE;
				        typeDone=true;
				 }			 
    	
		 if(!typeDone){
				 locator$=Locator.append(locator$, BaseHandler.HANDLER_CLASS, JEntityFacetPanel.class.getName());
				 locator$=Locator.append(locator$, WContext.WEB_HOME, url$);
				 locator$=Locator.append(locator$, WContext.WEB_REQUESTER, JEntityDigestDisplay.class.getName());
			   //   if(debug)
			   // 	  System.out.println("JEntityDigestDisplay:getItem:no link item locator="+Locator.remove(locator$,Locator.LOCATOR_ICON));
				  enLocator$= Base64.encodeBase64URLSafeString(locator$.getBytes());			      
		         href$=url$+"?"+WContext.WEB_LOCATOR+"="+enLocator$;
		         if(debug)
					 System.out.println("JEntityDigestDisplay:getItem:3");
			
		 }
		String  item$= "<li id='"+Identity.key()+"' type='"+nodeType$+"' ref='"+href$+"'"+" locator='"+enLocator$+"'"+iconTerm$+">"+title$;
		  if(debug&&print)
				 System.out.println("JEntityDigestDisplay:getItem:item="+"<li id='"+Identity.key()+"' type='"+nodeType$+"' ref='"+href$+"'"+" locator='' >"+title$);
		  return item$;
		}catch(Exception e){
			Logger.getLogger(JEntityDigestDisplay.class.getName()).info(e.toString());
			return null;
		}
	}
	
	public static void visitAllNodes(Entigrator entigrator,String webHome$,DefaultMutableTreeNode node, StringBuffer sb) {
		
		String locator$=(String)node.getUserObject();
		locator$=Locator.append(locator$, Entigrator.ENTIHOME, entigrator.getEntihome());
		// if(debug)
		//	 System.out.println("JEntityDigestDisplay:visitAllNodes:locator="+Locator.remove(locator$, Locator.LOCATOR_ICON));
	    String item$=getItem(entigrator,webHome$,locator$);
	    if(item$==null)
	    	return;
		sb.append(getItem(entigrator,webHome$,locator$));
	    if (node.getChildCount() > 0) {
	    sb.append("<ul>"); 	
	            Enumeration<DefaultMutableTreeNode> enumeration = node.children();
	            while (enumeration.hasMoreElements()) {
	            	DefaultMutableTreeNode nn =  enumeration.nextElement();
	            	visitAllNodes(entigrator,webHome$,nn,sb);
	            }
	    sb.append("</ul>"); 
	    }
	    sb.append("</li>");
	  }
	 
	
}
