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
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JFacetOpenItem;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
/**
 * Displays the digest view of the entity.
 * @author imasa
 *
 */
public  class JEntityDigestDisplay extends JPanel implements JContext ,JRequester{
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
	/**
	 * The tag of the node type.
	 */
	public static final String NODE_TYPE="node type";
	private static final String NODE_TYPE_ROOT="node type root";
	private static final String NODE_TYPE_PARENT="node type parent";
	private static final String NODE_TYPE_FACET_HEADER="node type facet header";
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
    String[] facets;
    JScrollPane scrollPane;
    JTree tree;
    boolean isRoot=true;
    boolean isFirst=true;
	String selection$;
	JPopupMenu popup;
	int nodeNumber=0;
	/**
	 * The default constructor.
	 */
	public JEntityDigestDisplay() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		scrollPane = new JScrollPane();
		add(scrollPane);
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
		    String icon$=Support.readHandlerIcon(getClass(), "digest.png");
			locator.setProperty(Locator.LOCATOR_ICON, icon$);
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
		this.console=console;
		this.locator$=locator$;
		 Properties locator=Locator.toProperties(locator$);
		 entihome$=locator.getProperty(Entigrator.ENTIHOME);
		 entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		 entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
		 String compressedSelection$=locator.getProperty(SELECTION);
		 if(compressedSelection$!=null){
			try{
			 byte[] ba=Base64.decodeBase64(compressedSelection$);
			 selection$=new String(ba,"UTF-8");
			}catch(Exception ee){
				Logger.getLogger(getClass().getName()).info(ee.toString());
			}
		 }
		 Entigrator entigrator=console.getEntigrator(entihome$);
		 DefaultMutableTreeNode root = new DefaultMutableTreeNode(entityLabel$);
		 locator=new Properties();
		 locator.setProperty(Locator.LOCATOR_TITLE, DIGEST);
		 String icon$=Support.readHandlerIcon(getClass(), "digest.png");
		 locator.setProperty(Locator.LOCATOR_ICON, icon$);
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
			 for(DefaultMutableTreeNode n:na)
				 parentNode.add(n);
		 expandTree(tree,true);
		 select();
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		}
		 return this;
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
	private void instantiateComponentNode(DefaultMutableTreeNode facetComponentNode){
		try{
			String facetComponentLocator$=(String)facetComponentNode.getUserObject();
			Properties locator=Locator.toProperties(facetComponentLocator$);
			Entigrator entigrator=console.getEntigrator(entihome$);
			String facetClass$=locator.getProperty(BaseHandler.HANDLER_CLASS);
			
			String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			JFacetOpenItem facetOpenItem=(JFacetOpenItem)JConsoleHandler.getHandlerInstance(entigrator,facetClass$ );
//			System.out.println("EntityDigestDisplay:instantiateComponentNode:facetOpenItem="+facetOpenItem.getClass().getName());
			String facetOpenItemLocator$=facetOpenItem.getLocator();
			facetOpenItemLocator$=Locator.append(facetComponentLocator$,Entigrator.ENTIHOME, entihome$);
			facetOpenItemLocator$=Locator.append(facetComponentLocator$,EntityHandler.ENTITY_KEY, entityKey$);
			facetOpenItem.instantiate(console, facetOpenItemLocator$);
			
			DefaultMutableTreeNode[] na=facetOpenItem.getDigest();
			if(na==null)
				return;
			String nodeLocator$;
		    for(DefaultMutableTreeNode aNa:na){
		    	nodeLocator$=(String)aNa.getUserObject();
		    	nodeLocator$=Locator.append(nodeLocator$, NODE_NUMBER, String.valueOf(nodeNumber++));
		    	nodeLocator$=Locator.append(nodeLocator$, COMPONENT_KEY, entityKey$);
		    	aNa.setUserObject(nodeLocator$);
		    	facetComponentNode.add(aNa);
		    	setSubnodesNumbers(aNa);
		    }
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		}
	}
private void instantiateFacetNode(DefaultMutableTreeNode facetNode){
		try{
		String facetNodeLocator$=(String)facetNode.getUserObject();
		Properties locator=Locator.toProperties(facetNodeLocator$);
		Entigrator entigrator=console.getEntigrator(entihome$);
		String facetClass$=locator.getProperty(BaseHandler.HANDLER_CLASS);
//		System.out.println("EntityDigestDisplay:instantiateFacetNode:facet class="+facetClass$);
		JFacetOpenItem facetOpenItem=(JFacetOpenItem)JConsoleHandler.getHandlerInstance(entigrator,facetClass$ );
		FacetHandler facetHandler=facetOpenItem.getFacetHandler();
//		System.out.println("EntityDigestDisplay:instantiateFacetNode:facet handler="+facetHandler.getClassName());
		Sack entity=entigrator.getEntityAtKey(entityKey$);
		String entityLocator$=EntityHandler.getEntityLocator(entigrator, entity);
//		System.out.println("EntityDigestDisplay:instantiateFacetNode:entity locator="+entityLocator$);
		DefaultMutableTreeNode facetComponentNode;
		if(facetHandler.isApplied(entigrator, entityLocator$)){
	//		System.out.println("EntityDigestDisplay:instantiateFacetNode:entity locator="+entityLocator$);
			entityLocator$=Locator.append(entityLocator$, Locator.LOCATOR_TYPE, LOCATOR_FACET_COMPONENT);
			entityLocator$=Locator.append(entityLocator$, BaseHandler.HANDLER_CLASS, facetClass$);
			entityLocator$=Locator.append(entityLocator$, NODE_TYPE, NODE_TYPE_FACET_OWNER);
			entityLocator$=Locator.append(entityLocator$, NODE_NUMBER, String.valueOf(nodeNumber++));
			facetComponentNode=new DefaultMutableTreeNode();
			facetComponentNode.setUserObject(entityLocator$);
			instantiateComponentNode(facetComponentNode);
			facetNode.add(facetComponentNode);
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
				}
			}
		}
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		}
	}
	private DefaultMutableTreeNode[] getFacetOpenItems(){
		String[] sa=listFacetOpenItems();
		if(sa==null)
			return null;
		DefaultMutableTreeNode facetNode;
		JFacetOpenItem facetOpenItem;
		Properties locator;
		Entigrator entigrator=console.getEntigrator(entihome$);
		ArrayList<DefaultMutableTreeNode>nl=new ArrayList<DefaultMutableTreeNode>();
		for(String aSa:sa){
			try{
			//	System.out.println("EntityDigestDisplay:getFacetOpenItems:foi="+aSa);
				facetOpenItem=(JFacetOpenItem)JConsoleHandler.getHandlerInstance(entigrator,aSa );
			    locator=new Properties();
				locator.setProperty(Locator.LOCATOR_TYPE, LOCATOR_TYPE_FACET);
				locator.setProperty(BaseHandler.HANDLER_CLASS,aSa);
				locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
				locator.setProperty(Locator.LOCATOR_ICON, facetOpenItem.getFacetIcon());
				locator.setProperty(Locator.LOCATOR_TITLE, facetOpenItem.getFacetName());
				locator.setProperty(NODE_TYPE, NODE_TYPE_FACET_HEADER);
				facetNode=new DefaultMutableTreeNode();
				facetNode.setUserObject(Locator.toString(locator));
				nl.add(facetNode);
				instantiateFacetNode(facetNode);
			}catch(Exception ee){
				Logger.getLogger(getClass().getName()).info(ee.toString());	
			}
		}
		Collections.sort(nl, new NodeComparator());
		return nl.toArray(new DefaultMutableTreeNode[0]);
	}
	private String[] listFacetOpenItems(){
		try{
			Entigrator entigrator=console.getEntigrator(entihome$);
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
    private void expandAll(JTree tree, TreePath path, boolean expand) {
        TreeNode node = (TreeNode) path.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            Enumeration enumeration = node.children();
            while (enumeration.hasMoreElements()) {
            	DefaultMutableTreeNode n = (DefaultMutableTreeNode) enumeration.nextElement();
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
				   //System.out.println("JEmailFacetOpenItem:edit:digest locator="+digestLocator$);
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
	        		String icon$=locator.getProperty(Locator.LOCATOR_ICON);
	        		if(icon$==null){
	        			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
	        			String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
	        			Entigrator entigrator=console.getEntigrator(entihome$);
	        			Sack entity=entigrator.getEntityAtKey(entityKey$);
        				icon$=entigrator.readEntityIcon(entity);
	        		}
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
}
