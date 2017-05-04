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
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Logger;

import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
import gdt.jgui.base.JBaseNavigator;
import gdt.jgui.base.JBasesPanel;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.WContext;
import gdt.jgui.console.WUtils;
import gdt.jgui.tool.JEntityEditor;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.BoxLayout;

import org.apache.commons.codec.binary.Base64;
/**
 * Displays the structure view of the entity.
 * @author imasa
 *
 */
public class JEntityStructurePanel extends JPanel implements JContext,WContext{
	private static final long serialVersionUID = 1L;
	private static final String STRUCTURE="Structure";
	private String entihome$;
    private String entityKey$;
    private String entityLabel$;
    private Entigrator entigrator;
    private Sack parent;
    String locator$;
    private DefaultMutableTreeNode node;
    private JMainConsole console;
    JTree tree;
    JScrollPane scrollPane;
    JPopupMenu popup;
    boolean isRoot=true;
    boolean isFirst=true;
	String selection$;
	static boolean debug=true;
	/**
	 * The default constructor.
	 */
	public JEntityStructurePanel() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		scrollPane = new JScrollPane();
		add(scrollPane);
		popup = new JPopupMenu();
     	popup.addPopupMenuListener(new PopupMenuListener(){
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				popup.removeAll();
				
				JMenuItem facetsItem=new JMenuItem("Facets");
				   popup.add(facetsItem);
				   facetsItem.setHorizontalTextPosition(JMenuItem.RIGHT);
				   facetsItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
						//	System.out.println("EntityStructurePanel:renderer:component locator$="+nodeLocator$);
						    JEntityFacetPanel efp=new JEntityFacetPanel();
						    String efpLocator$=efp.getLocator();
						    Properties locator=Locator.toProperties(selection$);
						    String entihome$=locator.getProperty(Entigrator.ENTIHOME);
						    String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
						    efpLocator$=Locator.append(efpLocator$, Entigrator.ENTIHOME,entihome$);
						    efpLocator$=Locator.append(efpLocator$, EntityHandler.ENTITY_KEY,entityKey$);
						    JConsoleHandler.execute(console, efpLocator$);
						}
					    });
				   JMenuItem copyItem=new JMenuItem("Copy");
				   popup.add(copyItem);
				   copyItem.setHorizontalTextPosition(JMenuItem.RIGHT);
				   copyItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
						    console.clipboard.clear(); 
							String locator$=(String)node.getUserObject();
						     if(locator$!=null)
						    	 console.clipboard.putString(locator$);
						}
					    });
				
				if(!isFirst){
					popup.addSeparator();	
				JMenuItem excludeItem=new JMenuItem("Exclude");
				   popup.add(excludeItem);
				   excludeItem.setHorizontalTextPosition(JMenuItem.RIGHT);
				   excludeItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							int response = JOptionPane.showConfirmDialog(console.getContentPanel(), "Exclude ?", "Confirm",
							        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						   if (response == JOptionPane.YES_OPTION) {
							try{ 
							Properties locator=Locator.toProperties(selection$);
							String entihome$=locator.getProperty(Entigrator.ENTIHOME);
							String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
						    Entigrator entigrator=console.getEntigrator(entihome$);
							Sack component=entigrator.getEntityAtKey(entityKey$);
							if(debug)
								System.out.println("JEntityStructurePanel:exclude:component="+component.getProperty("label"));
						
							String[] sa=entigrator.ent_listContainers(component);
							
							if(sa!=null){
								if(debug)
									System.out.println("JEntityStructurePanel:exclude:containers="+sa.length);
							
							   Sack container;	
								for(String aSa:sa){
									container=entigrator.getEntityAtKey(aSa);
									if(container!=null){
										if(debug)
											System.out.println("JEntityStructurePanel:exclude:container="+container.getProperty("label")+" component="+component.getProperty("label"));
										entigrator.col_breakRelation(container, component);
									}
								}
							}else{
								if(debug)
									System.out.println("JEntityStructurePanel:exclude:no containers");
							
							}
							 JConsoleHandler.execute(console, JEntityStructurePanel.this.locator$);   
							}catch(Exception ee){
								 Logger.getLogger(JEntityStructurePanel.class.getName()).info(ee.toString());
							 }
					    }
						}});
					
				JMenuItem deleteItem=new JMenuItem("Delete");
				   popup.add(deleteItem);
				   deleteItem.setHorizontalTextPosition(JMenuItem.RIGHT);
				   deleteItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							int response = JOptionPane.showConfirmDialog(console.getContentPanel(), "Delete ?", "Confirm",
							        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						   if (response == JOptionPane.YES_OPTION) {
							try{ 
							Properties locator=Locator.toProperties(selection$);
							String entihome$=locator.getProperty(Entigrator.ENTIHOME);
							String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
							Entigrator entigrator=console.getEntigrator(entihome$);
							Sack component=entigrator.getEntityAtKey(entityKey$);
							entigrator.deleteEntity(component);
							 JConsoleHandler.execute(console, JEntityStructurePanel.this.locator$);   
							}catch(Exception ee){
								 Logger.getLogger(JEntityStructurePanel.class.getName()).info(ee.toString());
							 }
					    }
						}});
					}
				if(hasToInclude()){
					popup.addSeparator();
					JMenuItem includeItem=new JMenuItem("Include");
					   popup.add(includeItem);
					   includeItem.setHorizontalTextPosition(JMenuItem.RIGHT);
					   includeItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
							    include();
							    JConsoleHandler.execute(console, JEntityStructurePanel.this.locator$);   
							}
						    });
				}
				
			}
			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
				// TODO Auto-generated method stub
			}
     	});
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
			
			JMenuItem digestItem = new JMenuItem("Digest");
			   digestItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
				       Properties locator=Locator.toProperties(locator$);
				       String entihome$=locator.getProperty(Entigrator.ENTIHOME);
				       String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
				       JEntityDigestDisplay edp=new JEntityDigestDisplay();
				       String edpLocator$=edp.getLocator();
				       edpLocator$=Locator.append(edpLocator$, Entigrator.ENTIHOME, entihome$);
				       edpLocator$=Locator.append(edpLocator$, EntityHandler.ENTITY_KEY, entityKey$);
				       JConsoleHandler.execute(console, edpLocator$);
					}
				} );
				menu.add(digestItem);
				menu.addSeparator();  
				JMenuItem doneItem=new JMenuItem("Done");
				  menu.add(doneItem);
				   doneItem.setHorizontalTextPosition(JMenuItem.RIGHT);
				   doneItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							Entigrator entigrator=console.getEntigrator(entihome$);
							entigrator.replace(parent);
							console.back();
						}
					    });
				 
			return menu;
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
		    locator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
		    locator.setProperty(Locator.LOCATOR_ICON_CLASS,getClass().getName());
		    locator.setProperty(Locator.LOCATOR_ICON_FILE,"tree.png");
 	       locator.setProperty(Locator.LOCATOR_TITLE, getTitle());
		   locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
		   locator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
		   return Locator.toString(locator);
	}
	/**
	 * Create the context.
	 * @param console the main console.
	 * @param locator$ the locator.
	 * @return the structure panel.
	 */
	@Override
	public JContext instantiate(JMainConsole console, String locator$) {
		this.console=console;
		this.locator$=locator$;
		 Properties locator=Locator.toProperties(locator$);
		 entihome$=locator.getProperty(Entigrator.ENTIHOME);
		 entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		 entigrator=console.getEntigrator(entihome$);
		 entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
		 DefaultMutableTreeNode root = new DefaultMutableTreeNode(entityLabel$);
		 locator=new Properties();
		 locator.setProperty(Locator.LOCATOR_TITLE, STRUCTURE);
		 locator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
			locator.setProperty(Locator.LOCATOR_ICON_CLASS,getClass().getName());
			locator.setProperty(Locator.LOCATOR_ICON_FILE,"tree.png");
		 root.setUserObject(Locator.toString(locator));
		 DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode(entityLabel$);
		 root.add(parentNode);
		 Entigrator entigrator=console.getEntigrator(entihome$);
		 parent=entigrator.getEntityAtKey(entityKey$);
		 String parentLocator$=EntityHandler.getEntityLocator(entigrator, parent);			
		 parentNode.setUserObject(parentLocator$);
		 addChildren(parentNode);
		 tree=new JTree(root);
		 tree.addTreeSelectionListener(new SelectionListener());
		 tree.setShowsRootHandles(true);
		 tree.setCellRenderer(new NodeRenderer()); 
		 tree.addMouseListener(new MousePopupListener());
		 scrollPane.getViewport().add(tree);
		 expandTree(tree,true);
		 return this;
	}
	private void addChildren(DefaultMutableTreeNode parentNode){
		try{
			String locator$=(String)parentNode.getUserObject();
			Properties locator=Locator.toProperties(locator$);
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			Entigrator entigrator=console.getEntigrator(entihome$);
			Sack parent=entigrator.getEntityAtKey(entityKey$);
			String[] sa=entigrator.ent_listComponents(parent);
			
			if(sa!=null){
				Sack child;
				String childLocator$;
				DefaultMutableTreeNode childNode;
				for(String aSa:sa){
					//child=entigrator.getEntityAtKey(aSa);
					//childLocator$=EntityHandler.getEntityLocator(entigrator, child);
					childLocator$=EntityHandler.getEntityLocatorAtKey(entigrator,aSa);
					childNode=new DefaultMutableTreeNode();
					childNode.setUserObject(childLocator$);
					parentNode.add(childNode);
					//addChildren(childNode);
				}
				
			}
			
		}catch(Exception e){
			Logger.getLogger(JEntityStructurePanel.class.getName()).severe(e.toString());
		}
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
		return "Structure";
			}catch(Exception e ){
				return "Structure";
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
		return "Entity structure panel";
	}
	/**
	 * Complete the context. No action.
	 */
	@Override
	public void close() {
		// TODO Auto-generated method stub
	}
	private boolean hasToInclude(){
		try{
			String[] sa=console.clipboard.getContent();
			if(sa==null)
				return false;
			for(String aSa:sa )
				if(canInclude(aSa))
					return true;
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).info(e.toString());
		}
		return false;
	}
	private void include(){
		try{
			String[] sa=console.clipboard.getContent();
			if(sa==null){
				if(debug)
					System.out.println("JEntityStructure:empty clipboard");
				return ;
			}
			String selectedLocator$=selection$;
			if(debug)
			System.out.println("EntityStructurePanel:include:selection="+selection$);
			Properties locator=Locator.toProperties(selectedLocator$);
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			String selectedEntityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			Entigrator entigrator=console.getEntigrator(entihome$);
			Sack selectedEntity=entigrator.getEntityAtKey(selectedEntityKey$);
			if(debug)
			System.out.println("EntityStructurePanel:include:selected entity="+selectedEntity.getProperty("label"));
			String candidateKey$;
			Sack candidate;
			for(String aSa:sa )
				if(canInclude(aSa)){
					locator=Locator.toProperties(aSa);
					candidateKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
					candidate=entigrator.getEntityAtKey(candidateKey$);
					entigrator.col_addComponent(selectedEntity, candidate);
					if(debug)
						System.out.println("JEntityStructure:add component:container="+selectedEntity.getProperty("label")+" component="+candidate.getProperty("label"));
				}
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).info(e.toString());
		}
	}
	private boolean canInclude(String candidateLocator$){
		try{
			if(debug)
				System.out.println("JEntityStructure:canInclude:locator="+locator$);
			String selectedLocator$=(String)node.getUserObject();
			Properties locator=Locator.toProperties(selectedLocator$);
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			String selectedEntityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			Entigrator entigrator=console.getEntigrator(entihome$);
			Sack selectedEntity=entigrator.getEntityAtKey(selectedEntityKey$);
			locator=Locator.toProperties(candidateLocator$);
			String candidateKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			Sack candidate=entigrator.getEntityAtKey(candidateKey$);
			if(candidate==null){
				if(debug)
					System.out.println("JEntityStructure:canInclude:cannot find candidate="+candidateKey$);
				return false;
			}
			if(entigrator.col_isComponentDown(selectedEntity,candidate)
					//||entigrator.col_isComponentUp(selectedEntity, candidate)
					){
				if(debug)
					System.out.println("JEntityStructure:canInclude:already in path candidate="+candidateKey$);
			
				return false;
			}
			if(debug)
				System.out.println("JEntityStructure:canInclude:return true");
		
			return true;
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).info(e.toString());
		}
		return false;
	}
	private void expandTree(JTree tree, boolean expand) {
        TreeNode root = (TreeNode) tree.getModel().getRoot();
        expandAll(tree, new TreePath(root), expand);
    }
 
    private void expandAll(JTree tree, TreePath path, boolean expand) {
        TreeNode node = (TreeNode) path.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            @SuppressWarnings("unchecked")
			Enumeration<TreeNode> enumeration = node.children();
            while (enumeration.hasMoreElements()) {
                TreeNode n = (TreeNode) enumeration.nextElement();
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
	        			System.out.println("JEntityStructurePanel:NodeRenderer: user object="+userObject);
	        		if(icon$!=null){
	        			byte[] ba=Base64.decodeBase64(icon$);
	        	      	  ImageIcon icon = new ImageIcon(ba);
	        	      	  Image image= icon.getImage().getScaledInstance(24, 24, 0);
	        	      	  icon.setImage(image);
	        	      	  label.setIcon(icon); 
	        		}
	        		//else
	        		//	System.out.println("EntityStructurePanel:renderer:icon is null");
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
				
				if (e.isPopupTrigger()) {
			      	  if(popup!=null)
			    		  isPopup=true;
			    	  else
			    		  isPopup=false;
			      }else
			    	  isPopup=false;
				
			//	System.out.println("EntityStructurePanel:MousePopupListener:isPopup="+isPopup);
		    }

		    public void mouseClicked(MouseEvent e) {
		    
		    	
		    	int y=scrollPane.getVerticalScrollBar().getValue();
		    	//System.out.println("EntityStructurePanel:MousePopupListener:mouse clicked:y="+y+" e="+e.getY());
		    	if(!isRoot&&isPopup)
		         		 popup.show(JEntityStructurePanel.this, e.getX(), e.getY()-y);
		      
		    }

		    public void mouseReleased(MouseEvent e) {
		    	//System.out.println("EntityStructurePanel:MousePopupListener:mouse released");
		    	if(!isPopup)
		    	if (e.isPopupTrigger()) {
			    	  isPopup=true;
			      }
		    }

		   }
	class SelectionListener implements TreeSelectionListener {
		  public void valueChanged(TreeSelectionEvent se) {
		    JTree tree = (JTree) se.getSource();
		    node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		    if (node.isRoot())
		    	isRoot=true;
		    else
		    	isRoot=false;
		    DefaultMutableTreeNode parent=( DefaultMutableTreeNode)node.getParent();
		    isFirst=false;
		    if(parent==null||parent.isRoot())
		    	  isFirst=true;
		    else
		    	addChildren(node);	
		    Object userObject=node.getUserObject();
        	selection$=(String)userObject;
        	
		    		  }
}
	@Override
	public void activate() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getWebView(Entigrator entigrator, String locator$) {
		try{
			Properties locator=Locator.toProperties(locator$);
			String webHome$=locator.getProperty(WContext.WEB_HOME);
			String entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
			String webRequester$=locator.getProperty(WContext.WEB_REQUESTER);
			String showContainers=locator.getProperty(JEntityEditor.SHOW_CONTAINERS);
			if(debug)
			System.out.println("JEntityStructurePanel:locator="+locator$);
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
		    sb.append("<tr><td>Facet: </td><td><strong>Entity viewer</strong></td></tr>");
		    if(Locator.LOCATOR_TRUE.equals(showContainers))
		    	sb.append("<tr><td>Context: </td><td><strong>Containers</strong></td></tr>");
		    else
		    	sb.append("<tr><td>Context: </td><td><strong>Components</strong></td></tr>");
		    sb.append("</table>");
		    sb.append("\n<div id=\"jstree\">");
		    sb.append("\n<ul>");
		    sb.append(getWebItems(entigrator, locator$));
		    sb.append("\n</ul>");
		    sb.append("\n</div>");
		      sb.append("<script>");
		    
		      sb.append("$(function () {");
		    sb.append("$('#jstree').jstree();");
		    sb.append("$('#jstree').on(\"changed.jstree\", function (e, data) {");
		    sb.append(" var ref=data.instance.get_node(data.selected[0]).li_attr.ref;");
		    sb.append(" console.log(data.selected);");
		    sb.append(" console.log('ref='+ref);");
		    sb.append(" window.location.assign(ref);");
		    sb.append("});");
		    sb.append("});");
		    
		    sb.append("function onLoad() {");
		    sb.append("initBack(\""+this.getClass().getName()+"\",\""+webRequester$+"\");");
		    sb.append("$('#jstree').jstree('open_all');");
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
	private static String getWebItems(Entigrator entigrator,String locator$){
		try{
				Properties locator=Locator.toProperties(locator$);
				String entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
				String entityKey$=entigrator.indx_keyAtLabel(entityLabel$);
				String webHome$=locator.getProperty(WContext.WEB_HOME);
				String showContainers=locator.getProperty(JEntityEditor.SHOW_CONTAINERS);
			//	String webRequester$=locator.getProperty(WContext.WEB_REQUESTER);
				ArrayList<String>sl=new ArrayList<String>();
				Sack entity=entigrator.getEntityAtKey(entityKey$);
				if(Locator.LOCATOR_TRUE.equals(showContainers))
					addItems(entigrator,entity,webHome$,sl,false);
				else
				    addItems(entigrator,entity,webHome$,sl,true);
				StringBuffer sb=new StringBuffer();
				for(String s:sl)
					sb.append(s);
  				return sb.toString(); 
			}catch(Exception e){
		        Logger.getLogger(JEntityStructurePanel.class.getName()).severe(e.toString());
			}
			return null;
	}
	private static void addItems(Entigrator entigrator,Sack entity , String webHome$,ArrayList<String>sl,boolean showComponents){
		try{
			String[] sa=null;
			if(showComponents)
			 sa=entigrator.ent_listComponents(entity);
			else
				sa=entigrator.ent_listContainers(entity);
			if(sa!=null)
				if(debug)
					System.out.println("JEntityStructurePanel:addItems: entity="+entity.getProperty("label")+" components="+sa.length);	
			Properties itemLocator=new Properties();
			itemLocator.setProperty(Entigrator.ENTIHOME, entigrator.getEntihome());
			itemLocator.setProperty(BaseHandler.HANDLER_CLASS,JEntityFacetPanel.class.getName());
			itemLocator.setProperty(WContext.WEB_HOME,webHome$);
			itemLocator.setProperty(WContext.WEB_REQUESTER,JEntityStructurePanel.class.getName());
			itemLocator.setProperty(EntityHandler.ENTITY_LABEL, entity.getProperty("label"));	
			String icon$=entigrator.readIconFromIcons(entigrator.ent_getIconAtKey(entity.getKey()));
			String title$=entity.getProperty("label");
			sl.add("\n<li "+getItem(title$,icon$, webHome$, Locator.toString(itemLocator)));
			Sack child;
			if(sa!=null){
				if(debug)
					System.out.println("JEntityStructurePanel:addItems: sa="+sa.length);	
			for(String s:sa){
				try{
					sl.add("\n<ul>");	
					title$=entigrator.indx_getLabel(s);
					if(debug)
						System.out.println("JEntityStructurePanel:addItems:component="+title$);
					child=entigrator.getEntityAtKey(s);
					icon$=entigrator.ent_getIconAtKey(s);
					itemLocator.setProperty(EntityHandler.ENTITY_LABEL, title$);
					addItems(entigrator,child,webHome$,sl,showComponents);
					sl.add("\n</ul>\n");
				}catch(Exception ee){
					Logger.getLogger(JEntityStructurePanel.class.getName()).info(ee.toString());
				}
			}
			}
			sl.add("\n</li>");
		}catch(Exception e){
			Logger.getLogger(JEntityStructurePanel.class.getName()).severe(e.toString());
		}

	}
	private static String getItem(String title$,String icon$, String url$, String foiLocator$){
		if(debug)
				System.out.println("JEntityStructurePanel:getItem: locator="+foiLocator$);
	   
		icon$=WUtils.scaleIcon(icon$);
	  String iconTerm$=" data-jstree='{\"icon\":\"data:image/png;base64,"+icon$+"\"}' width=\"24\" height=\"24\"";
		  if(debug){
				 
			  System.out.println("JEntityStructurePanel:getItem:icon term="+iconTerm$);
			//  System.out.println("JIndexPanel:getWebView:locator="+locator$);
		  }
		  
		  String enLocator$= Base64.encodeBase64URLSafeString(foiLocator$.getBytes());
		  String   href$=url$+"?"+WContext.WEB_LOCATOR+"="+enLocator$;
		  String refTerm$=" id='"+title$+"' ref='"+href$+"'";
		  if(debug){
				 
			  System.out.println("JEntityStructurePanel:getItem:ref term="+iconTerm$);
			//  System.out.println("JIndexPanel:getWebView:locator="+locator$);
		  }
		  String item$=refTerm$+ iconTerm$+">"+title$;
		  if(debug)
				 System.out.println("JEntityStructurePanel:getItem:item="+item$);
		  return item$;
	}
}