package gdt.jgui.entity.graph;
/*
 * Copyright 2016 Alexander Imas
 * This file is extension of JEntigrator.

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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.Timer;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.util.RandomLocationTransformer;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.samples.VertexImageShaperDemo.DemoVertexIconShapeTransformer;
import edu.uci.ics.jung.samples.VertexImageShaperDemo.DemoVertexIconTransformer;
import edu.uci.ics.jung.samples.VertexImageShaperDemo.PickWithIconListener;
import edu.uci.ics.jung.samples.VertexImageShaperDemo.VertexStringerImpl;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.EllipseVertexShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.PickableEdgePaintTransformer;
import edu.uci.ics.jung.visualization.decorators.PickableVertexPaintTransformer;

import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.renderers.DefaultEdgeLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.DefaultVertexLabelRenderer;
import gdt.data.entity.BaseHandler;
import gdt.data.entity.Bond;
import gdt.data.entity.EdgeHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.GraphHandler;
import gdt.data.entity.NodeHandler;

import gdt.data.entity.facet.FieldsHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Identity;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.data.store.StoreAdapter;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JFacetRenderer;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.entity.JEntityPrimaryMenu;
import gdt.jgui.entity.bonddetail.JBondDetailPanel;
import gdt.jgui.entity.edge.JBondsPanel;
import gdt.jgui.tool.JTextEditor;
/**
 * This context visualize the graph.
 *  * @author imasa
 *
 */

public class JGraphRenderer extends JPanel implements JContext , JRequester
, MouseMotionListener{
//, MouseMotionListener,WContext{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Logger LOGGER=Logger.getLogger(getClass().getName());
	private static final String ACTION_CREATE_GRAPH="action create graph";
	private JMainConsole console;
	private String entihome$;
    private String entityKey$;
    private String entityLabel$;
    private Sack graphEntity;
   public static String ACTION_ENTITY="action entity";
	public static String ACTION_RELATIONS="action relations";
	public static String ACTION_SCOPE_RELATIONS="action scope relations";
	public static String ACTION_NETWORK_RELATIONS="action network relations";
	public static String ACTION_EDGE="action edge";
	public  static final String ACTION_EXPAND="action expand";
	public static String ACTION_NETWORK="action network";
	public static String SELECTED_NODE_LABEL="selected node label";
	public static final String SHOWN_NODES_LABELS="shown nodes labels";
	public static String SELECTED_BOND_KEY="selected bond key";
	public static String SELECTED_EDGE_LABEL="selected edge label";
  // int n=-1;
  // int b=-1;
  private JPopupMenu popup;
    String requesterResponseLocator$;
  
    String title$="Map";
    private VisualizationViewer<Number,Number> vv = null;
    String[] scope;
   // MousePopupListener mouseListener;
    Stack<Core[]>undo;
    
//private AbstractLayout<Number,Number> layout = null;

    Timer timer;
    DirectedSparseGraph<Number, Number> graph;
    static boolean debug=false;

    protected JButton switchLayout;
    
    public static final int EDGE_LENGTH = 100;
    Integer v_prev = null;
    Map<String,Number> v;//=new HashMap<String,Number>();
    Map<Number,String> e;
    Map<Number,Bond> b;
    Map<Core[],String> filter;
    final ScalingControl scaler = new CrossoverScalingControl();
    final DefaultModalGraphMouse<Number,Number> graphMouse = new DefaultModalGraphMouse<Number,Number>();
    FRLayout<Number, Number> layout;
    //KKLayout<Number, Number> layout;
   // final SpringLayout2 <Number, Number> layout;
    final Map<Number,String> vLabels = new HashMap<Number,String>();
    String edge$;
    String locator$;
/**
 * The default constructor
 */
    public JGraphRenderer()
  	{
    	super();
    	if(debug)
			System.out.println("JGraphRenderer: 0");	
  	    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
  	    undo=new Stack<Core[]>();
  	  v=new HashMap<String,Number>();
  	 e=new HashMap<Number,String>();
  	 b=new HashMap<Number,Bond>();    
  	filter=new HashMap<Core[],String>();    
  	}
    /**
     * Response on call from the other context.
     *	@param console main console
     *  @param locator$ action's locator 
     */  
	@Override
	public void response(JMainConsole console, String locator$) {
   if (debug)
		System.out.println("JGraphrenderer:response:"+locator$);
		try{
			Properties locator=Locator.toProperties(locator$);
			String action$=locator.getProperty(JRequester.REQUESTER_ACTION);
			entihome$=locator.getProperty(Entigrator.ENTIHOME);
			Entigrator entigrator=console.getEntigrator(entihome$);
			String text$=locator.getProperty(JTextEditor.TEXT);

			if(ACTION_CREATE_GRAPH.equals(action$)){
				Sack newEntity=entigrator.ent_new("graph", text$);
				newEntity.createElement("field");
				newEntity.putElementItem("field", new Core(null,"name","value"));
				newEntity.createElement("fhandler");
				newEntity.putElementItem("fhandler", new Core(null,GraphHandler.class.getName(),GraphHandler.EXTENSION_KEY));
				newEntity.putElementItem("fhandler", new Core(null,FieldsHandler.class.getName(),null));
				newEntity.createElement("jfacet");
				newEntity.putElementItem("jfacet", new Core("gdt.jgui.entity.graph.JGraphFacetAddItem",EdgeHandler.class.getName(),"gdt.jgui.entity.graph.JGraphFacetOpenItem"));
				newEntity.putAttribute(new Core (null,"icon","graph.png"));
				entigrator.save(newEntity);
				entigrator.ent_assignProperty(newEntity, "fields", text$);
				entigrator.ent_assignProperty(newEntity, "graph", text$);
				String icons$=entihome$+"/"+Entigrator.ICONS;
				Support.addHandlerIcon(JGraphRenderer.class, "graph.png", icons$);
				newEntity=entigrator.ent_reindex(newEntity);
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
				return;
			}
			if(JGraphViews.ACTION_SAVE_VIEW.equals(action$)){
				 if (debug)
						System.out.println("JGraphrenderer:response:save");
					
				String viewTitle$=locator.getProperty(JTextEditor.TEXT);
			    String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			    String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
				//Entigrator	entigrator=console.getEntigrator(entihome$);
				Sack graph=entigrator.getEntityAtKey(entityKey$);
				Core[] ca=graph.elementGet("node.select");
				if(ca==null){
					System.out.println("JGraphViews.response:no selection");
					return;
				}
				//
				entityLabel$=entigrator.indx_getLabel(entityKey$);
				action$=locator.getProperty(JRequester.REQUESTER_ACTION);
				String viewComponentLabel$=entityLabel$+".view";
				String viewComponentKey$=entigrator.indx_keyAtLabel(viewComponentLabel$);
				Sack viewComponent=null;
				if(viewComponentKey$==null){
					viewComponent=entigrator.ent_new("graph.vew", viewComponentLabel$);
					viewComponentKey$=viewComponent.getKey();
					entigrator.col_addComponent(graph, viewComponent);
				}else
					 viewComponent=entigrator.getEntityAtKey(viewComponentKey$);	
			//	Sack views=entigrator.getEntityAtKey(viewComponentKey$);
				
				if(!viewComponent.existsElement("views"))
					viewComponent.createElement("views");
				String viewKey$=Identity.key();
				viewComponent.putElementItem("views", new Core(null,viewKey$,viewTitle$));
				viewComponent.createElement(viewKey$);
				viewComponent.elementReplace(viewKey$, ca);
				entigrator.save(viewComponent);
				String gv$=new JGraphViews().getLocator();
				gv$=Locator.append(gv$,Entigrator.ENTIHOME,entihome$);
				gv$=Locator.append(gv$,EntityHandler.ENTITY_KEY,entityKey$);
				JConsoleHandler.execute(console,gv$);
			}
		
			}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		}
	}
/**
 * Get the context panel.
 * @return the context panel.
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
		final JMenu	menu=new JMenu("Context");
		   menu.setName("Context");
		   menu.addMenuListener(new MenuListener(){
				@Override
				public void menuSelected(MenuEvent e) {
					menu.removeAll();
					if(undo.size()>0){
					JMenuItem undoItem = new JMenuItem("Undo");
					 undoItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
						undoPop();
						// visualize(console.getEntigrator(entihome$),scope);
						//	init();
							   }
					});
					menu.add(undoItem);
					}
					//if(undo.size()>0){   
						JMenuItem  resetItem = new JMenuItem("Reset");
						   resetItem.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									while(!undo.isEmpty())
									  undo.pop();
									edge$=null;
									filter.clear();
									Entigrator entigrator=console.getEntigrator(entihome$);
									   Sack graph=entigrator.getEntityAtKey(entityKey$);
									   if(graph!=null){
										   graph.removeElement("node");
										   entigrator.replace(graph);
									   }
									   init();
								
								}
							} );
						   menu.add(resetItem);
					//}
					   JMenuItem  unmarkItem = new JMenuItem("Unmark all");
							   unmarkItem.addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent e) {
									unmarkAll();
									}
								} );
							   menu.add(unmarkItem);
					   JMenuItem  expandItem = new JMenuItem("Expand");
						   expandItem.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									//markSelectedNode();
									//expand();
									Entigrator entigrator=console.getEntigrator(entihome$);
									
									undoPush();
									scope=NodeHandler.getScopeExpandedNodeKeys(entigrator, null, scope);
									visualize(entigrator,scope,null);
									
								}
						} );
						   JMenuItem  relocateItem = new JMenuItem("Relocate");
						   relocateItem.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									Entigrator entigrator=console.getEntigrator(entihome$);
									undoPush();
									visualize(entigrator,scope,null);
									
								}
						} );
						   menu.add(relocateItem);
						   menu.addSeparator();
						   JMenuItem  zoomItem = new JMenuItem("Zoom +");
						   zoomItem.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									 scaler.scale(vv, 1.1f, vv.getCenter());
								}
						} );
						   menu.add(zoomItem);
						   JMenuItem  unzoomItem = new JMenuItem("Zoom -");
						   unzoomItem.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									 scaler.scale(vv, 1/1.1f, vv.getCenter());
								}
						} );
						   
						   menu.add(unzoomItem);
						 if(hasSelectedNodes()){  
						   JMenuItem  lensItem = new JMenuItem("Lens");
						   lensItem.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									 lens();
								}
						} );
						   menu.add(lensItem);
						   JMenuItem  hideItem = new JMenuItem("Cut");
						   hideItem.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									 hideNodes();
								}
						} );
						   menu.add(hideItem);
						 }
						   menu.addSeparator();
						   JMenuItem  transItem = new JMenuItem("Transforming");
						   transItem.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									graphMouse.setMode(edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode.TRANSFORMING);
								}
						} );
						   menu.add(transItem);
						   JMenuItem  pickItem = new JMenuItem("Picking");
						   pickItem.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									graphMouse.setMode(edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode.PICKING);
								}
						} );
						   menu.add(pickItem);
				    menu.addSeparator();
				    JMenuItem saveItem = new JMenuItem("Save");
				  	saveItem.addActionListener(new ActionListener() {
				  		@Override
				  		public void actionPerformed(ActionEvent e) {
				  			try{
				  				Entigrator entigrator=console.getEntigrator(entihome$);
				  				Sack entity=entigrator.getEntityAtKey(entityKey$);
				  				//if(!"graph".equals(entity.getProperty("entity")))
				  				//	return;
				  				if(scope==null)
				  					return;
				  			    if(entity.existsElement("node"))
				  			    		entity.removeElement("node");
				  			    entity.createElement("node");
				  				Collection <Number>vc=graph.getVertices();
				  				//final PickedState<Number> pickedState = vv.getPickedVertexState();
				  				Layout layout=vv.getModel().getGraphLayout();
								Point2D point;
								ArrayList<Point2D>pl=new ArrayList<Point2D>();
								Map<Point2D,Number>lm=new HashMap<Point2D,Number>();
				  			    for( Number n:vc){
				  					point=(Point2D)layout.transform(n);
				  					pl.add(point);
				  					lm.put(point, n);
				  				    //System.out.println("JGraphRenderer:visualize:save n="+n+" point="+point);
				  				    //entity.putElementItem("node", new Core(String.valueOf(point.getX()),vLabels.get(n),String.valueOf(point.getY())));
				  					 
				  				}
				  			  System.out.println("JGraphRenderer:visualize:pl="+pl.size());
				  			    double minX=5000;
				  			    double minY=5000;
				  			  for(Point2D p:pl){
				  				  if(p.getX()<minX)
				  					  minX=p.getX();
				  				if(p.getY()<minY)
				  				  minY=p.getY();
				  			  }
				  			  minX=minX-20;
				  			minY=minY-20;
				  			 for(Point2D p:pl){
				  				entity.putElementItem("node", new Core(String.valueOf(p.getX()-minX),vLabels.get(lm.get(p)),String.valueOf(p.getY()-minY)));
				  			 }
				             entigrator.replace(entity);  				
				  			
				  			}catch(Exception ee){
				  				System.out.println("JGraphViews:getContextMenu:new:"+ee.toString());
				  			}
				  		}
				  	
				  	});
				  
				  	menu.add(saveItem);  
				    JMenuItem  exportItem = new JMenuItem("Export");
					   exportItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								saveAsPicture();
							}
						} );
					   menu.add(exportItem);	
					   JMenuItem  copyItem = new JMenuItem("Copy");
					   copyItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								console.clipboard.clear();
								Entigrator entigrator=console.getEntigrator(entihome$);
								String graphLocator$=EntityHandler.getEntityLocatorAtKey(entigrator,  entityKey$);
								console.clipboard.putString(graphLocator$);
							}
						} );
					   menu.add(copyItem);	
					   menu.addSeparator();
					   JMenuItem  doneItem = new JMenuItem("Done");
					   doneItem.addActionListener(new ActionListener() {
							@Override
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
	  /**
		 * Get the context locator.
		 * @return the context locator.
		 */	
	@Override
	public String getLocator() {
		try{
			Properties locator=new Properties();
			locator.setProperty(BaseHandler.HANDLER_CLASS,JGraphRenderer.class.getName());
			locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
			locator.setProperty( JContext.CONTEXT_TYPE,getType());
			locator.setProperty(Locator.LOCATOR_TITLE,getTitle());
			locator.setProperty(BaseHandler.HANDLER_LOCATION,GraphHandler.EXTENSION_KEY);
			if(entityLabel$!=null){
				locator.setProperty(EntityHandler.ENTITY_LABEL,entityLabel$);
			}
			if(entityKey$!=null)
				locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
			if(entihome$!=null){
				locator.setProperty(Entigrator.ENTIHOME,entihome$);
				
					locator.setProperty(Entigrator.ENTIHOME,entihome$);
			//		Entigrator entigrator=console.getEntigrator(entihome$);
			//	String icon$=ExtensionHandler.loadIcon(entigrator, GraphHandler.EXTENSION_KEY,"map.png");
			//	if(icon$!=null)
			//	    	locator.setProperty(Locator.LOCATOR_ICON,icon$);
				
				
			}
			locator.setProperty( Locator.LOCATOR_ICON_CONTAINER, Locator.LOCATOR_ICON_CONTAINER_CLASS);
			locator.setProperty( Locator.LOCATOR_ICON_CLASS, getClass().getName());
			locator.setProperty( Locator.LOCATOR_ICON_FILE, "map.png");
			locator.setProperty( Locator.LOCATOR_ICON_CLASS_LOCATION,EdgeHandler.EXTENSION_KEY);
		
			if(entityLabel$!=null)
				locator.setProperty(EntityHandler.ENTITY_LABEL,entityLabel$);
			return Locator.toString(locator);
			}catch(Exception e){
	        Logger.getLogger(getClass().getName()).severe(e.toString());
	        return null;
			}
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
			if(debug)
			  System.out.println("JGraphRenderer:instantiate:locator="+locator$);
				this.console=console;
				this.locator$=locator$;
				
				Properties locator=Locator.toProperties(locator$);
				entihome$=locator.getProperty(Entigrator.ENTIHOME);
				entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
				String action$=locator.getProperty(JRequester.REQUESTER_ACTION);
				Entigrator entigrator=console.getEntigrator(entihome$);
				 if(Locator.LOCATOR_TRUE.equals(locator.getProperty(JFacetRenderer.ONLY_ITEM)))
					 return this;
				requesterResponseLocator$=locator.getProperty(JRequester.REQUESTER_RESPONSE_LOCATOR);
	            graphEntity=entigrator.getEntityAtKey(entityKey$);
	             
	            entityLabel$=graphEntity.getProperty("label");
	            title$=entityLabel$;
	            String viewComponentKey$=locator.getProperty(JGraphViews.VIEW_COMPONENT_KEY);
	            String viewKey$=locator.getProperty(JGraphViews.VIEW_KEY);
	   		    locator=new Properties();
   	   		 locator.setProperty(Locator.LOCATOR_TITLE, "Graph");
   	  	      locator.setProperty(Entigrator.ENTIHOME,entihome$);
   	  //	String icon$=ExtensionHandler.loadIcon(entigrator, GraphHandler.EXTENSION_KEY,"graph.png");
   	  //	if(icon$!=null)
	   // 	locator.setProperty(Locator.LOCATOR_ICON,icon$);
   	 //System.out.println("JGraphRenderer:instantiate:action="+action$);
   	  	if(JGraphViews.ACTION_SHOW_VIEW.equals(action$)){
   	  	//System.out.println("JGraphRenderer:instantiate:show view");
   	  	     try{
   	  	    
   	  	    	 Sack viewComponent=entigrator.getEntityAtKey(viewComponentKey$);
   	  		title$=viewComponent.getElementItemAt("views", viewKey$); 
   	  	     Core[]ca=viewComponent.elementGet(viewKey$);
   	  	entigrator.save(graphEntity);
   	  	     }catch(Exception ee){
   	  	    	 Logger.getLogger(JGraphRenderer.class.getName()).info(ee.toString()); 
   	  	     }
   	  	}
   	  	init();
   	  	//displayGraph();
		}catch(Exception e){
		        Logger.getLogger(getClass().getName()).severe(e.toString());
			}
		//System.out.println("JGraphRenderer:instantiate:finish");
			return this;
			
	}
	/**
	 * Get title of the context.  
	 * @return the title of the context.
	 */	
	@Override
	public String getTitle() {
		return title$;
		
	}
	/**
	 * Get subtitle of the context.  
	 * @return the subtitle of the context.
	 */	
	@Override
	public String getSubtitle() {
		return entityLabel$;
	}
	 /**
     * Get type of the  context.  
     * @return the type of the context.
     */	
	@Override
	public String getType() {
		return "graph";
	}
/**
 * Complete facet.
 */
	@Override
	public void close() {
		//Entigrator entigrator=console.getEntigrator(entihome$);
	
		
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		//System.out.println("JGraphRenderer: mouseDragged:BEGIN");
		
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		//System.out.println("JGraphRenderer: mouseMoved:BEGIN");
		
	}
	


private void hideNodes(){
	if(scope==null)
		return;
	
	Entigrator entigrator=console.getEntigrator(entihome$);
	PickedState<Number> pickedState = vv.getPickedVertexState();
	Set<Number>ns=pickedState.getPicked();
	if(ns.isEmpty())
		return;
	undoPush();
	if(debug){
		System.out.println("JGraphRenderer:hideNodes:selected="+ns.size());
	}
	//ArrayList<String>nl=new ArrayList<String>();
	
	//Bond bond;
	ArrayList<String> nkl=new ArrayList<String>();
	boolean skip;
	//String nodeKey$=null;
	for(int i=0;i<scope.length;i++){
		    skip=false;
	       for(Number n:ns){
	    	   if(i==(int)n)
	    		   skip=true;
	       }
	       if(!skip)
	    	   nkl.add(scope[i]);
	}

	scope=nkl.toArray(new String[0]);
	visualize(entigrator,scope,null);
	relocate();
	repaint();
}
private void entity(int n){
	try{
		 if(scope==null)
			 return;
		 String scope$=Locator.toString(scope);
		 console.cache.put(entityKey$,scope$);
		 //String nodeKey$=scope[n];
		 JEntityFacetPanel fp=new JEntityFacetPanel();
		 String fp$=fp.getLocator();
		fp$=Locator.append(fp$, Entigrator.ENTIHOME, entihome$);
		fp$=Locator.append(fp$, EntityHandler.ENTITY_KEY,scope[n] );
		JConsoleHandler.execute(console, fp$);
	      
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
	
}
private void init(){
	if(debug)
	System.out.println("JGraphRenderer:init:entity key="+entityKey$+" entihome="+entihome$);
	
	removeAll();
	//filter.clear();
	 Entigrator entigrator=console.getEntigrator(entihome$);
	 Sack graphEntity=entigrator.getEntityAtKey(entityKey$);
	 Core [] nodes=graphEntity.elementGet("node");
	 String nodeKey$;
	 ArrayList<String>nkl=new ArrayList<String>();
	 
	 if(nodes!=null){
		 Map<String,Number>ln=new HashMap<String,Number>();
		 for (int i = 0; i <nodes.length; i++){
			 ln.put(nodes[i].name,i);
			 nodeKey$=entigrator.indx_keyAtLabel(nodes[i].name);
			 if(!nkl.contains(nodeKey$))
				 nkl.add(nodeKey$);
		 }
		 scope=nkl.toArray(new String[0]);
		 visualize(entigrator,scope,null);
		 relocate(nodes,ln);
         repaint();	
         return;
	 }
	 if(graphEntity!=null&&"graph".equals(graphEntity.getProperty("entity"))){
	
		 Core[] ca=graphEntity.elementGet("jbookmark");
		 if(ca==null)
			 return;
		 if(debug)
				System.out.println("JGraphRenderer:init:ca="+ca.length);
		 
		 for (int i = 0; i <ca.length; i++){
			 try{
			 nodeKey$=Locator.getProperty(ca[i].value, EntityHandler.ENTITY_KEY);
			 if(!nkl.contains(nodeKey$))
				 nkl.add(nodeKey$);
			 }catch(Exception ee){
				 Logger.getLogger(getClass().getName()).info(ee.toString());
			 }
		 }
		 scope=NodeHandler.getScopeExpandedNodeKeys(entigrator, null,nkl.toArray(new String[0]));
		 visualize(entigrator,scope,null);	 
		 return;
 }
 if(graphEntity!=null&&(graphEntity.getProperty("node")!=null)
		 //||graphEntity.getProperty("bond.detail")!=null
		 )
		 //||(graphEntity!=null&&"edge".equals(graphEntity.getProperty("entity"))))
 {
		 Core[] ca=graphEntity.elementGet("bond");
		 if(ca==null)
			 return;
		 ArrayList<String>sl=new ArrayList<String>();
		 for(Core c:ca){
			 if(c.type!=null&&!sl.contains(c.type))
				 sl.add(c.type);
			 if(c.value!=null&&!sl.contains(c.value))
				 sl.add(c.value);
		 }
		 //scope=NodeHandler.getScopeExpandedNodeKeys(entigrator, null,sl.toArray(new String[0]));
		  
		 scope=sl.toArray(new String[0]);
		 //scope=NodeHandler.(entigrator, null,sl.toArray(new String[0]));
		 visualize(entigrator,scope,null);	 
		 return; 
 }
 //if(graphEntity!=null&&(graphEntity.getProperty("node")!=null)
	 if(graphEntity.getProperty("bond.detail")!=null)
		 {
		 Core[] ca=graphEntity.elementGet("bond");
		 if(ca==null)
			 return;
		 ArrayList<String>sl=new ArrayList<String>();
		 
		 for(Core c:ca){
			 
			 if(c.type!=null&&!sl.contains(c.type))
				 sl.add(c.type);
			 if(c.value!=null&&!sl.contains(c.value))
				 sl.add(c.value);
		 }
		 scope=sl.toArray(new String[0]);
		 visualize(entigrator,scope,null);	 
		 return; 
 }
 if(graphEntity!=null&&"edge".equals(graphEntity.getProperty("entity"))){
	 Core[] ca=graphEntity.elementGet("bond");
	 if(ca==null)
		 return;
	 ArrayList<String>sl=new ArrayList<String>();
	 for(Core c:ca){
		 if(c.type!=null&&!sl.contains(c.type))
			 sl.add(c.type);
		 if(c.value!=null&&!sl.contains(c.value))
			 sl.add(c.value);
	 }
	// scope=sl.toArray(new String[0]);
	 
	// visualize(entigrator,scope,null);
	
	 ArrayList<String>nl=new ArrayList<String>();
	 for(String s:sl)
		  nl.add(entigrator.indx_getLabel(s));
	  String[] nodeLabels=nl.toArray(new String[0]);
	//  if(debug)
	//	  System.out.println("JGraphRenderer:labels="+nodeLabels.length); 
	  String[] scopeLabels=EdgeHandler.filterNodesAtEdge(entigrator, nodeLabels, graphEntity.getProperty("label"));
	  if(scopeLabels==null||scopeLabels.length<1)
		  return;
	 nl.clear();
	 for(String s:scopeLabels){
		// System.out.println("JGraphRenderer:init.edge:s="+s+" l="+entigrator.indx_keyAtLabel(s)); 
	   nl.add(entigrator.indx_keyAtLabel(s));
	   
	 }
	 // JGraphRenderer.this.scope=nl.toArray(new String[0]);
	 // visualize(entigrator,nl.toArray(new String[0]),entityKey$);
	//scope=NodeHandler.getScopeExpandedNodeKeys(entigrator, null,sl.toArray(new String[0]));
	 scope=nl.toArray(new String[0]);
	 
	 visualize(entigrator,scope,graphEntity.getKey());	
	 return;
} 

}
private void relocate(){
	Entigrator entigrator=console.getEntigrator(entihome$);
	if(undo.isEmpty())
		return;
	Core[] nla=undo.peek();
	Map <String,Number>ln=new HashMap<String,Number>();
	//Set<String> keys=v.keySet();
	String nodeLabel$;
	//for(String s:keys){
		for(String s:scope){
		   nodeLabel$=entigrator.indx_getLabel(s);
		   ln.put(nodeLabel$,v.get(s));
	}
    visualize(entigrator,scope,edge$);
	relocate(nla, ln);
}
private void relocate(final Core[] nodes,Map<String,Number>ln){
	try{
		if(nodes==null)
			return;
		Layout layout=vv.getGraphLayout();
		//Dimension d=layout.getSize();
		//float offsetX=d.width/2;
		//float offsetY=d.height/2;
		//System.out.println("JGraphRenderer:relocate:layout dimension="+d);
		Number n;
		for(Core node:nodes){
		    	float x=Float.parseFloat(node.type);//+offsetX;
		    	float y=Float.parseFloat(node.value);//+offsetY;
		    	n=ln.get(node.name);
		    	//System.out.println("JGraphRenderer:relocate:label="+node.name+" number="+n+" x="+x+" y="+y);
		    	Point2D point=new Point2D.Float(x,y);
		        layout.setLocation(n, point);
		    }
		
		  vv.repaint();
	}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
	}
	
}
public  void visualize(Entigrator entigrator,final String[] scope ,String edgeKey$){
	if(scope==null)
		return;
	if(debug)
	System.out.println("JGraphRenderer:visualize:nodes="+scope.length);
		removeAll();
	
	 graph = new DirectedSparseGraph<Number,Number>();
	// Entigrator entigrator=console.getEntigrator(entihome$);
//	 String[] nka=NodeHandler.getScopeExpandedNodeKeys(entigrator, null,scope); 		 
//	 scope=nka;
	 String icon$;
	 byte[] bar;
	 ImageIcon icon;
	 //final Map<Number,String> vLabels = new HashMap<Number,String>();
	 Map<Number,Icon> iconMap = new HashMap<Number,Icon>();
	 String vLabel$;
	 for (int i = 0; i <scope.length; i++) {
		 vLabel$=entigrator.indx_getLabel(scope[i]);
		 if(debug)
				System.out.println("JGraphRenderer:visualize:node label="+vLabel$+" i="+i);
		// if(!ll.contains(vLabel$))
		//	 ll.add(vLabel$);
		 vLabels.put(i, vLabel$);
		 v.put(scope[i], i);
		 icon$=getNodeIcon(entigrator, scope[i]);
		 if(icon$!=null){
			   bar=Base64.decodeBase64(icon$);
	      	  icon = new ImageIcon(bar);
	      	  Image image= icon.getImage().getScaledInstance(24, 24, 0);
	      	  icon.setImage(image);
              iconMap.put(i, icon);
				}
	 }
	 if(debug)
		 System.out.println("JGraphRenderer:visualize:expanded nodes="+scope.length);
	 Bond[] ba;//=NodeHandler.getScopeBonds(entigrator, scope);
	 if(edgeKey$==null)
		 ba=NodeHandler.getScopeBonds(entigrator, scope);
	 else
		 ba=NodeHandler.getScopeBonds(entigrator, scope,edgeKey$);
	 if(debug)
		 System.out.println("JGraphRenderer:visualize: bonds="+ba.length);
	 Number in;
	 Number out;
	 ArrayList<String>ll=new ArrayList<String>();
	 ArrayList <String>ekl=new ArrayList<String>();
	 ArrayList<String>el=new ArrayList<String>();
	 String edgeLabel$;
		 for(int j=0;j<ba.length;j++){
			 if(debug)
				 System.out.println("JGraphRenderer:visualize:add edge  by keys out="+ba[j].outNodeKey$+ " in="+ba[j].inNodeKey$);
			try{
			 if(edge$!=null&&!edge$.equals(ba[j].edgeKey$)){
				// if(debug)
				//	 System.out.println("JGraphRenderer:visualize:skip edge="+edge$);
				
			    	continue;
			 }
			     out=v.get(ba[j].outNodeKey$);
			     vLabel$=entigrator.indx_getLabel(ba[j].outNodeKey$);
			     if(vLabel$==null)
			    	 continue;
			     if(!ll.contains(vLabel$))
			    	 ll.add(vLabel$);
			     in=v.get(ba[j].inNodeKey$);
			     vLabel$=entigrator.indx_getLabel(ba[j].inNodeKey$);
			     if(vLabel$==null)
			    	 continue;
			     if(!ll.contains(vLabel$))
			    	 ll.add(vLabel$);
			     edgeLabel$=entigrator.indx_getLabel(ba[j].edgeKey$);
				 if(!el.contains(edgeLabel$))
					 el.add(edgeLabel$);
			     e.put(j,edgeLabel$);
				 if(!ekl.contains(ba[j].edgeKey$))
					 ekl.add(ba[j].edgeKey$);
				if(debug) 
				 System.out.println("JGraphRenderer:visualize:add edge out="+out+ " in="+in+" number="+j);
				 b.put(j, ba[j]);
				 graph.addEdge(j, out, in, EdgeType.DIRECTED);
			}catch(Exception ee){
				System.out.println("JGraphRenderer:visualize:bond="+ba[j]);
			}
		 	}
	Collections.sort(el);	 
	 if(debug)
			 System.out.println("JGraphRenderer:visualize: el="+el.size());
	 
    layout = new FRLayout<Number, Number>(graph);
	// layout = new KKLayout<Number, Number>(graph);
    layout.setMaxIterations(500);
    layout.setInitializer(new RandomLocationTransformer<Number>(new Dimension(400,400), 0));
    vv =  new VisualizationViewer<Number, Number>(layout, new Dimension(400,400));
    Transformer<Number,Paint> vpf = 
            new PickableVertexPaintTransformer<Number>(vv.getPickedVertexState(), Color.white, Color.yellow);
        vv.getRenderContext().setVertexFillPaintTransformer(vpf);
        vv.getRenderContext().setEdgeDrawPaintTransformer(new PickableEdgePaintTransformer<Number>(vv.getPickedEdgeState(), Color.black, Color.cyan));
        vv.setBackground(Color.white);
       
        final Transformer<Number,String> vertexStringerImpl = 
                new VertexStringerImpl<Number,String>(vLabels);
            vv.getRenderContext().setVertexLabelTransformer(vertexStringerImpl);
            vv.getRenderContext().setVertexLabelRenderer(new VertexLabelRenderer(Color.cyan));
            vv.getRenderContext().setEdgeLabelRenderer(new DefaultEdgeLabelRenderer(Color.cyan));
            final DemoVertexIconShapeTransformer<Number> vertexIconShapeTransformer =
                    new DemoVertexIconShapeTransformer<Number>(new EllipseVertexShapeTransformer<Number>());
                
                final DemoVertexIconTransformer<Number> vertexIconTransformer =
                	new DemoVertexIconTransformer<Number>();
                
                vertexIconShapeTransformer.setIconMap(iconMap);
                vertexIconTransformer.setIconMap(iconMap);
                vv.getRenderContext().setVertexShapeTransformer(vertexIconShapeTransformer);
                vv.getRenderContext().setVertexIconTransformer(vertexIconTransformer);
                 PickedState<Number> ps = vv.getPickedVertexState();
                ps.addItemListener(new PickWithIconListener<Number>(vertexIconTransformer));
                if(debug)
          			 System.out.println("JGraphRenderer:visualize: 2");         
                //vv.setVertexToolTipTransformer(new ToStringLabeller<Number>());
               // vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<Number>());
                vv.getRenderContext().setEdgeFontTransformer(new
                		ConstantTransformer(new Font("Helvetica", Font.PLAIN, 8)));
                vv.getRenderContext().setEdgeLabelTransformer(new Transformer<Number, String>() {
					@Override
					public String transform(Number n) {
						return (String.valueOf(e.get(n)));
					}
                });
                final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
                add(panel);
                vv.setGraphMouse(graphMouse);
                vv.addKeyListener(graphMouse.getModeKeyListener());
           
                MousePopupListener mpl=new MousePopupListener();
                vv.addMouseListener(mpl);
                if(debug)
         			 System.out.println("JGraphRenderer:visualize: 3");         
              
                JPanel controls = new JPanel();
                controls.setLayout(new BoxLayout(controls, BoxLayout.X_AXIS));
                //
                  
                //
               Collections.sort(ll);
               String[] la=ll.toArray(new String[0]);
            	
                final JComboBox <String>nodesBox =new JComboBox<String>(la);
                nodesBox.addItemListener(new ItemListener() {
					@Override
					public void itemStateChanged(ItemEvent e) {
						if(ItemEvent.SELECTED==e.getStateChange()){
						  
						  Entigrator entigrator=console.getEntigrator(entihome$);
						  String nodeKey$=entigrator.indx_keyAtLabel((String)e.getItem());
					  Collection <Number>vc=graph.getVertices();
						for( Number n:vc)
								vv.getPickedVertexState().pick(n, false);
						  vv.getPickedVertexState().pick(v.get(nodeKey$), true);
						}
					}
                });
                JButton nodeMenu=new JButton("Node");
                nodeMenu.addActionListener(new ActionListener() { 
                	  public void actionPerformed(ActionEvent e) { 
                		   
                		  final String nodeLabel$=(String)nodesBox.getSelectedItem();
                		  int cnt=vLabels.size();
                		  Entigrator entigrator=console.getEntigrator(entihome$);
						String nodeKey$=entigrator.indx_keyAtLabel(nodeLabel$);
						
                		  final JPopupMenu	popup=getNodeMenu(nodeKey$);
                		
  				      popup.show((JComponent)e.getSource(),0,0);
                		  } 
                		} );
                JPanel nodePanel = new JPanel();
                nodePanel.setBorder(BorderFactory.createTitledBorder("Node"));
                nodePanel.setLayout(new BoxLayout(nodePanel, BoxLayout.X_AXIS));
                nodePanel.add(nodesBox);
                nodePanel.add(nodeMenu);
                controls.add(nodePanel);
               
                final JComboBox <String>edgesBox =new JComboBox<String>(el.toArray(new String[0]));
                edgesBox.addItemListener(new ItemListener() {
					@Override
					public void itemStateChanged(ItemEvent ie) {
						if(ItemEvent.SELECTED==ie.getStateChange()){
						//  System.out.println("JGraphRenderer:edges box item="+ie.getItem());
						  Entigrator entigrator=console.getEntigrator(entihome$);
						  //String edgeKey$=entigrator.indx_keyAtLabel((String)ie.getItem());
						  Collection <Number>vc=graph.getEdges();
						  if(vc.size()>1)
							  edge$=null;
						  for( Number n:vc){
							  //System.out.println("JGraphRenderer:edge number="+n+ " key="+e.get(n));
							    if(ie.getItem().equals(e.get(n)))
								vv.getPickedEdgeState().pick(n, true);
							    else
							    	vv.getPickedEdgeState().pick(n, false);
						  }
						}
					}
                });
                JButton edgeMenu=new JButton("Edge");
                edgeMenu.addActionListener(new ActionListener() { 
                	  public void actionPerformed(ActionEvent e) { 
                		   
                		  final String edgeLabel$=(String)edgesBox.getSelectedItem();
                		  Entigrator entigrator=console.getEntigrator(entihome$);
                		 // Set<String> labels=v.keySet();
                		  ArrayList <String>nl=new ArrayList<String>();
                		  for(String s:scope)
                			  nl.add(entigrator.indx_getLabel(s));
                		  String[] nodeLabels=nl.toArray(new String[0]);
                		//  if(debug)
                		//	  System.out.println("JGraphRenderer:labels="+nodeLabels.length); 
                		  String[] scopeLabels=EdgeHandler.filterNodesAtEdge(entigrator, nodeLabels, edgeLabel$);
                		  if(scopeLabels==null||scopeLabels.length<1)
                			  return;
                		 undoPush();
                		 nl.clear();
                		 for(String s:scopeLabels)
                		   nl.add(entigrator.indx_keyAtLabel(s));
                		  JGraphRenderer.this.scope=nl.toArray(new String[0]);
                		  edge$= entigrator.indx_keyAtLabel(edgeLabel$);
                		  visualize(entigrator,nl.toArray(new String[0]),edge$);
                		  edgesBox.setModel(new DefaultComboBoxModel<String>(new String[]{edgeLabel$}));
          				  repaint();
                		  } 
                		} );
                JPanel edgesPanel = new JPanel();
                edgesPanel.setBorder(BorderFactory.createTitledBorder("Edge"));
                edgesPanel.setLayout(new BoxLayout(edgesPanel, BoxLayout.X_AXIS));
                edgesPanel.add(edgesBox);
                if(edgesBox.getModel().getSize()>1)
                   edgesPanel.add(edgeMenu);
                controls.add(edgesPanel);
                add(controls, BorderLayout.SOUTH);
                revalidate();
				repaint();
				         
             
}
private JPopupMenu getEdgeMenu(final Number n){
	  final JPopupMenu	popup = new JPopupMenu();
	  popup.addPopupMenuListener(new PopupMenuListener(){
			@Override
		public void popupMenuWillBecomeVisible(PopupMenuEvent ev1) {
			JMenuItem edgeItem=new JMenuItem("Edge");
			   popup.add(edgeItem);
			   edgeItem.setHorizontalTextPosition(JMenuItem.RIGHT);
			   edgeItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ev2) {
						try{
						Entigrator entigrator=console.getEntigrator(entihome$);
						String edgeKey$=entigrator.indx_keyAtLabel(e.get(n));
						JEntityFacetPanel fp=new JEntityFacetPanel();
						String fp$=fp.getLocator();
						fp$=Locator.append(fp$, Entigrator.ENTIHOME, entihome$);
						fp$=Locator.append(fp$, EntityHandler.ENTITY_KEY, edgeKey$);
						JConsoleHandler.execute(console,fp$);
						}catch(Exception ee){
							Logger.getLogger(getClass().getName()).info(ee.toString());
						}
					}
				    });
			   JMenuItem detailsItem=new JMenuItem("Details");
			   popup.add(detailsItem);
			   detailsItem.setHorizontalTextPosition(JMenuItem.RIGHT);
			   detailsItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try{
					       displayDetails(n);
						}catch(Exception ee){
							Logger.getLogger(getClass().getName()).info(ee.toString());
						}
					}
				    });
				
		}

		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}

		@Override
		public void popupMenuCanceled(PopupMenuEvent e) {}
});
  return popup;
}
  
  
private JPopupMenu getNodeMenu(final String nodeKey$){
	  final JPopupMenu	popup = new JPopupMenu();
    popup.addPopupMenuListener(new PopupMenuListener(){
		@Override
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			   JMenuItem pickOutItem=new JMenuItem("Relations");
			   popup.add(pickOutItem);
			   pickOutItem.setHorizontalTextPosition(JMenuItem.RIGHT);
			   pickOutItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try{
							Entigrator entigrator=console.getEntigrator(entihome$);
							//String nodeKey$=entigrator.indx_keyAtLabel(nodeLabel$);
							String[] rna=NodeHandler.getRelatedNodeKeys(entigrator, nodeKey$);
								ArrayList<String>nkl=new ArrayList<String>();	
								for(String s:rna)
									if(!nkl.contains(s))
										nkl.add(s);
								
									undoPush();
								edge$=null;
								visualize(entigrator,nkl.toArray(new String[0]),null);
						}catch(Exception ee){
							Logger.getLogger(getClass().getName()).info(ee.toString());
						}
						
					}
				    });
			   JMenuItem expandItem=new JMenuItem("Expand");
			   popup.add(expandItem);
			   expandItem.setHorizontalTextPosition(JMenuItem.RIGHT);
			   expandItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						
						try{
							Entigrator entigrator=console.getEntigrator(entihome$);
							undoPush();
							edge$=null;
							String[] eka=NodeHandler.getRelatedNodeKeys(entigrator, nodeKey$);
							ArrayList<String>nkl=new ArrayList<String>();
							if(eka!=null)
								for(String s:eka)
									nkl.add(s);
							if(scope!=null)
								for(String s:scope)
									nkl.add(s);
							scope=nkl.toArray(new String[0]);
							visualize(entigrator, scope,null);
							Core[] nla=undo.peek();
							Map <String,Number>ln=new HashMap<String,Number>();
							Set<String> keys=v.keySet();
							String nodeLabel$;
							for(String s:keys){
								nodeLabel$=entigrator.indx_getLabel(s);
								ln.put(nodeLabel$,v.get(s));
							}
						
							relocate(nla, ln);
							vv.repaint();
						}catch(Exception ee){
							Logger.getLogger(getClass().getName()).info(ee.toString());
						}
						
					}
				    }); 
			   JMenuItem networkItem=new JMenuItem("Network");
			   popup.add(networkItem);
			   networkItem.setHorizontalTextPosition(JMenuItem.RIGHT);
			   networkItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						
						try{
							Entigrator entigrator=console.getEntigrator(entihome$);
							undoPush();
							edge$=null;
						    scope=NodeHandler.getNetwordNodeKeys(entigrator, nodeKey$);
							visualize(entigrator, scope,null);
						}catch(Exception ee){
							Logger.getLogger(getClass().getName()).info(ee.toString());
						}
						
					}
				    }); 
			   JMenuItem cutItem=new JMenuItem("Cut");
			   popup.add(cutItem);
			   cutItem.setHorizontalTextPosition(JMenuItem.RIGHT);
			   cutItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						ArrayList<String> nkl=new ArrayList<String>();
						for(int i=0;i<scope.length;i++){
							if(nodeKey$!=scope[i])
						    	   nkl.add(scope[i]);
						}
						undoPush();
						scope=nkl.toArray(new String[0]);
						Entigrator entigrator=console.getEntigrator(entihome$);
						visualize(entigrator,scope,null);
						relocate();
						repaint();
					}
				    }); 
			   //popup.addSeparator();
			   JMenuItem entityItem=new JMenuItem("Entity");
			   popup.add(entityItem);
			   entityItem.setHorizontalTextPosition(JMenuItem.RIGHT);
			   entityItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try{
							Entigrator entigrator=console.getEntigrator(entihome$);
							
							String nodeLabel$=entigrator.indx_getLabel(nodeKey$);
							if(debug)
								System.out.println("JGraphRenderer:entity popup:node key="+nodeKey$+" label="+nodeLabel$);
							 JEntityFacetPanel fp=new JEntityFacetPanel();
							 String fp$=fp.getLocator();
							fp$=Locator.append(fp$, Entigrator.ENTIHOME, entihome$);
							fp$=Locator.append(fp$, EntityHandler.ENTITY_KEY,nodeKey$);
							JConsoleHandler.execute(console, fp$);
							
						}catch(Exception ee){
							Logger.getLogger(getClass().getName()).info(ee.toString());
						}
					}
				    });  
		//end 	popupMenuWillBecomeVisible
		}

		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}

		@Override
		public void popupMenuCanceled(PopupMenuEvent e) {}
}); 
return popup;	///  

}

private void unmarkAll(){
	Collection <Number>vc=graph.getVertices();
	for( Number n:vc)
		vv.getPickedVertexState().pick(n, false);
	Collection <Number>ec=graph.getEdges();
	for( Number n:ec)
		vv.getPickedEdgeState().pick(n, false);
}
private void openEdge(int n){
	try{
		//System.out.println("JGraphRenderer:openEdge:b="+b);
		Entigrator entigrator=console.getEntigrator(entihome$);
		String edgeKey$=entigrator.indx_keyAtLabel(e.get(n));
			JEntityFacetPanel fp=new JEntityFacetPanel();
			String fp$=fp.getLocator();
			fp$=Locator.append(fp$, Entigrator.ENTIHOME, entihome$);
			fp$=Locator.append(fp$, EntityHandler.ENTITY_KEY, edgeKey$);
			JConsoleHandler.execute(console,fp$);
		
	}catch(Exception e){
		Logger.getLogger(JGraphRenderer.class.getName()).severe(e.toString());
	}
	
}
private void displayDetails(Number n){
	try{
		if(debug)
		   System.out.println("JGraphRenderer:displayDetails:n="+n+" b="+b.size());
		Entigrator entigrator=console.getEntigrator(entihome$);
		Bond bond=b.get(n);
		Sack edge=entigrator.getEntityAtKey(bond.edgeKey$);
		
		ArrayList<String>sl=new ArrayList<String>();
		Core[] ca=edge.elementGet("detail");
		if(debug)
			   System.out.println("JGraphRenderer:displayDetail:ca="+ca.length);
		if(ca==null)
			return;
		for(Core c:ca){
			if(bond.bondKey$.equals(c.type))
   		   sl.add(entigrator.indx_getLabel(c.value));
		}
		String sa$=null;
		if(sl.size()>0){
				
		Collections.sort(sl);
		String[] sa=sl.toArray(new String[0]);
		sa$=Locator.toString(sa);
		}
		   //JEntitiesPanel jep=new JEntitiesPanel();
		JBondDetailPanel jep=new JBondDetailPanel();
		   String jepLocator$=jep.getLocator();
		   Properties jepLocator=Locator.toProperties(jepLocator$);
		   jepLocator.setProperty(Entigrator.ENTIHOME, entihome$);
		   //jepLocator$=Locator.append(jepLocator$, Entigrator.ENTIHOME, entihome$);
		   if(sa$!=null)
		      //jepLocator$=Locator.append(jepLocator$,EntityHandler.ENTITY_LIST,sa$);
			   jepLocator.setProperty(EntityHandler.ENTITY_LIST,sa$);
		   jepLocator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
		   jepLocator.setProperty(JBondsPanel.BOND_KEY,bond.bondKey$);
		   jepLocator.setProperty(JBondsPanel.EDGE_KEY,bond.edgeKey$);
		   if(debug)
			   System.out.println("JGraphRenderer:displayDetail:jepLocator="+Locator.toString(jepLocator));
		   JConsoleHandler.execute(console, Locator.toString(jepLocator));
		
	}catch(Exception e){
		Logger.getLogger(JGraphRenderer.class.getName()).severe(e.toString());
	}
	
}
private void saveAsPicture(){
	try{
		String fileName$=System.getProperty("graph.png");
		JFileChooser chooser = new JFileChooser(); 
	    chooser.setCurrentDirectory(new java.io.File(System.getProperty("user.home")));
	    chooser.setDialogTitle(fileName$);
	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    chooser.setAcceptAllFileFilterUsed(false);
	    if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
	    	String folder$=chooser.getSelectedFile().getPath();
	    	String file$ =(String)JOptionPane.showInputDialog("File");
	    if ((file$ != null) && (file$.length() > 0)) {
    	int width = vv.getWidth();
        int height = vv.getHeight();
        BufferedImage bi = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics =bi.createGraphics();
        vv.paint(graphics);
        graphics.dispose();
		File outputfile = new File(folder$+"/"+file$+".png");
		if(!outputfile.exists())
			outputfile.createNewFile();
		ImageIO.write(bi, "png", outputfile);
	    }
	    }
	}catch(Exception e){
		Logger.getLogger(JGraphRenderer.class.getName()).severe(e.toString());
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
				//System.out.println("JGraphRenderer:MousePopupListener:isPopup="+isPopup);
		}

	    public void mouseClicked(MouseEvent e) {
	    	if(!isPopup)
	    		return;
	    	    final VisualizationViewer vv =(VisualizationViewer)e.getSource();
	            final Layout layout = vv.getGraphLayout();
	            //final Graph graph = layout.getGraph();
	            final Point2D p = e.getPoint();
	            int n=-1;
	            //int b=-1;
	            final Point2D ivp = p;
	            GraphElementAccessor pickSupport = vv.getPickSupport();
	            if(pickSupport != null) {
	                Object vertex = pickSupport.getVertex(layout, ivp.getX(), ivp.getY());
	                Object edge = pickSupport.getEdge(layout, ivp.getX(), ivp.getY());
	                if(vertex!=null){
	                	//System.out.println("JGraphRenderer:MousePopupListener:vertex="+vertex);
	                	n=((Integer)vertex).intValue();
	                	String nodeKey$=scope[n];
	                	popup=getNodeMenu(nodeKey$);
	                	popup.show(JGraphRenderer.this,(int)p.getX(),(int)p.getY());
	                	//n=((Integer)vertex).intValue();
	                }
	                if(edge!=null){
	                	//System.out.println("JGraphRenderer:MousePopupListener:edge="+edge);
	                	n=((Integer)edge).intValue();
	                	//String nodeKey$=scope[n];
	                	popup=getEdgeMenu(n);
	                	popup.show(JGraphRenderer.this,(int)p.getX(),(int)p.getY());
	                }
	            
	            }
	    	
	    }
	    public void mouseReleased(MouseEvent e) {
	    	
	    	if(!isPopup)
		    	if (e.isPopupTrigger()) 
			    	  isPopup=true;
	    		//System.out.println("JGraphRenderer:MousePopupListener:is Popup");
	    	}
	   }

class VertexLabelRenderer extends DefaultVertexLabelRenderer {
	public VertexLabelRenderer(Color pickedVertexLabelColor) {
		super(pickedVertexLabelColor);
	}
	@Override
	 public <V> Component getVertexLabelRendererComponent(JComponent vv, Object value,
	            Font font, boolean isSelected, V vertex) {

		super.setForeground(vv.getForeground());
	        if(isSelected){
	        	//System.out.println("JGraphRenderer:VertexLabelRenderer: font="+font+" vertex="+vertex+" selected="+isSelected);
	        	setForeground(pickedVertexLabelColor);
	        }
	        super.setBackground(vv.getBackground());
	        if(font == null)
        	font=vv.getFont();
        	if(isSelected)
	        		font=font.deriveFont(Font.BOLD | Font.ITALIC);
            setFont(font);
	        setIcon(null);
	        setBorder(noFocusBorder);
	        setValue(value); 
	        return this;
	    }
}

@Override
public void activate() {
	// TODO Auto-generated method stub
	
}

public static String getNodeIcon(Entigrator entigrator,String nodeKey$){
try{
	//Core [] ca=entigrator.indx_getMarks(new String[]{nodeKey$});
	String header$=entigrator.getEntihome()+"/"+StoreAdapter.HEADERS+"/"+nodeKey$;
    if(!new File(header$).exists())
    	return null;
	Sack header=Sack.parseXML(header$);
    String nodeLabel$=header.getElementItem("key", nodeKey$).type;
   // if(debug)
   // System.out.println("JGraphRenderer:getNodeIcon:header="+header$+" key="+nodeKey$+" label="+nodeLabel$);
    String iconFile$=header.getElementItem("label", nodeLabel$).type;
	return entigrator.readIconFromIcons(iconFile$);
}catch(Exception e){
	Logger.getLogger(JGraphRenderer.class.getName()).info(e.toString());	
}
return null;
}
private void undoPush(){
	Collection <Number>vc=graph.getVertices();
		//final PickedState<Number> pickedState = vv.getPickedVertexState();
	Layout layout=vv.getModel().getGraphLayout();
	Point2D point;
	ArrayList<Point2D>pl=new ArrayList<Point2D>();
	Map<Point2D,Number>lm=new HashMap<Point2D,Number>();
	    for( Number n:vc){
			point=(Point2D)layout.transform(n);
			pl.add(point);
			lm.put(point, n);
		}
	    double minX=5000;
	    double minY=5000;
	  for(Point2D p:pl){
		  if(p.getX()<minX)
			  minX=p.getX();
		if(p.getY()<minY)
		  minY=p.getX();
	  }
	  minX=minX-20;
	 minY=minY-20;
	 ArrayList<Core>cl= new ArrayList<Core>();
	 for(Point2D p:pl){
		cl.add( new Core(String.valueOf(p.getX()-minX),vLabels.get(lm.get(p)),String.valueOf(p.getY()-minY)));
	 }
	 Core[] ca=cl.toArray(new Core[0]);
	 //Integer hc=new Integer( ca.hashCode());
	 undo.push(ca);
	 if(edge$!=null)
	 filter.put(ca, edge$);
}
private void undoPop(){
	if(debug)
		System.out.println("JGraphRenderer:undoPop:undosize="+undo.size());
	edge$=null;
	if(undo.isEmpty())
		return;
	 Core[] nodes=undo.pop();
	 edge$=filter.get(nodes);
	 Entigrator entigrator=console.getEntigrator(entihome$);
	 String nodeKey$;
	 ArrayList<String>nkl=new ArrayList<String>();
	 if(nodes!=null){
		 Map<String,Number>ln=new HashMap<String,Number>();
		 for (int i = 0; i <nodes.length; i++){
			 ln.put(nodes[i].name,i);
			 nodeKey$=entigrator.indx_keyAtLabel(nodes[i].name);
			 if(!nkl.contains(nodeKey$))
				 nkl.add(nodeKey$);
		 }
		 scope=nkl.toArray(new String[0]);
		 visualize(entigrator,scope,null);
		 relocate(nodes,ln);
         repaint();	
        // return;
	 }
}
public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
    for (Entry<T, E> entry : map.entrySet()) {
        if (Objects.equals(value, entry.getValue())) {
            return entry.getKey();
        }
    }
    return null;
}
private boolean hasSelectedNodes(){
	//undoPush();
	Entigrator entigrator=console.getEntigrator(entihome$);
	PickedState<Number> pickedState = vv.getPickedVertexState();
	Set<Number>ns=pickedState.getPicked();
	if(!ns.isEmpty())
		return true;
	else
		return false;
}
private void lens(){
	
	Entigrator entigrator=console.getEntigrator(entihome$);
	PickedState<Number> pickedState = vv.getPickedVertexState();
	Set<Number>ns=pickedState.getPicked();
	if(ns.isEmpty())
		return;
	//Bond bond;
	undoPush();
	ArrayList<String> nkl=new ArrayList<String>();
	for(Number n:ns){
		nkl.add(scope[(int)n]);
	}
	scope=NodeHandler.getScopeExpandedNodeKeys(entigrator, null,nkl.toArray(new String[0]));
	visualize(entigrator,scope,null);
}
}


