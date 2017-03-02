package gdt.jgui.entity.graph;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
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
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.codec.binary.Base64;

import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.FacetHandler;
import gdt.data.entity.GraphHandler;
import gdt.data.entity.facet.ExtensionHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
import gdt.jgui.base.JBaseNavigator;
import gdt.jgui.base.JBasesPanel;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JFacetOpenItem;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.console.WContext;
import gdt.jgui.console.WUtils;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.entity.fields.JFieldsFacetOpenItem;
import gdt.jgui.tool.JEntityEditor;
/**
 * This class represents the graph facet item in the list
 * of  entity's facets.
 * @author imasa
 *
 */


public class JGraphFacetOpenItem extends JFieldsFacetOpenItem implements WContext{
	private static final long serialVersionUID = 1L;
	private static final boolean debug=false;
	private static String ACTION_ENTITY="action entity";
	private static String ACTION_RELATIONS="action relations";
	private static String ACTION_EXPAND="action expand";
	private static String SELECTED_NODE_LABEL="selected node label";
	private static String SHOW_NODES="show nodes";
	private static String NODE_LABELS="node labels";
	private static String GRAPH_MODE_ORIGIN="graph mode origin";
	private static String GRAPH_MODE_RELATIONS="graph mode relations";
	private static String GRAPH_MODE_EXPAND="graph mode expand";
	
/**
 * Default constructor
 */
	public JGraphFacetOpenItem(){
		super();
	}
	/**
	 * Get the context locator.
	 * @return the context locator.
	 */		
@Override
public String getLocator(){
	Properties locator=new Properties();
	locator.setProperty(Locator.LOCATOR_TITLE,"Graph");
	locator.setProperty(BaseHandler.HANDLER_CLASS,JGraphFacetOpenItem.class.getName());
	locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
	locator.setProperty(BaseHandler.HANDLER_METHOD,METHOD_OPEN_FACET);
	locator.setProperty(BaseHandler.HANDLER_LOCATION,GraphHandler.EXTENSION_KEY);
	locator.setProperty( JContext.CONTEXT_TYPE,"Graph facet");
	locator.setProperty(Locator.LOCATOR_TITLE,"Graph");
	locator.setProperty(FACET_HANDLER_CLASS,GraphHandler.class.getName());
	if(entityKey$!=null)
		locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
	if(entihome$!=null){
		locator.setProperty(Entigrator.ENTIHOME,entihome$);
	//	Entigrator entigrator=console.getEntigrator(entihome$);
	// icon$=ExtensionHandler.loadIcon(entigrator, GraphHandler.EXTENSION_KEY,"graph.png");
	//if(icon$!=null)
	 //   	locator.setProperty(Locator.LOCATOR_ICON,icon$);
	}
    if(entihome$!=null){   
 	locator.setProperty(Locator.LOCATOR_CHECKABLE,Locator.LOCATOR_TRUE);
	    }
    locator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
	locator.setProperty(Locator.LOCATOR_ICON_CLASS,getClass().getName());
	locator.setProperty(Locator.LOCATOR_ICON_FILE,"graph.png");
	return Locator.toString(locator);
}
/**
 * Check if the facet can be removed from the entity.
 * @return false.
 */
@Override
public boolean isRemovable() {
	return false;
	}
/**
 * Get the facet name.
 * @return the facet name.
 */
@Override
public String getFacetName() {
	return "Graph";
}
/**
 * Get the facet icon as a Base64 string.
 * @return the facet icon string.
 */
@Override
public String getFacetIcon(Entigrator entigrator) {
	return ExtensionHandler.loadIcon(entigrator,GraphHandler.EXTENSION_KEY,"graph.png"); 

}
/**
 * Remove the facet from the entity.
 * No action.
 */
@Override
public void removeFacet() {
	
}
/**
 * Display the facet console.
 * @param console the main console.
 * @param locator$ the locator string.
 */
@Override
public void openFacet(JMainConsole console,String locator$) {
	try{
	  
		//	System.out.println("JAddressFacetOpenItem:openFacet:locator="+locator$);
		Properties locator=Locator.toProperties(locator$);
		String entihome$=locator.getProperty(Entigrator.ENTIHOME);
		String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		String responseLocator$=getLocator();
		Properties responseLocator=Locator.toProperties(responseLocator$);
		responseLocator.setProperty(Entigrator.ENTIHOME, entihome$);
		responseLocator.setProperty(EntityHandler.ENTITY_KEY, entityKey$);
		responseLocator.setProperty(BaseHandler.HANDLER_METHOD, JFacetOpenItem.METHOD_RESPONSE);
		//
		JEntityFacetPanel efp=new JEntityFacetPanel();
		String efpLocator$=efp.getLocator();
		efpLocator$=Locator.append(efpLocator$, Entigrator.ENTIHOME, entihome$);
		efpLocator$=Locator.append(efpLocator$, EntityHandler.ENTITY_KEY, entityKey$);
		efpLocator$=Locator.append(efpLocator$, JRequester.REQUESTER_ACTION, ACTION_DISPLAY_FACETS);
		responseLocator.setProperty(JRequester.REQUESTER_RESPONSE_LOCATOR, Locator.compressText(efpLocator$));
		//
		responseLocator$=Locator.toString(responseLocator);
		String requesterResponseLocator$=Locator.compressText(responseLocator$);
		JGraphEditor graphEditor =new JGraphEditor();
		String vsLocator$=graphEditor.getLocator();
		vsLocator$=Locator.append(vsLocator$, Entigrator.ENTIHOME, entihome$);
		vsLocator$=Locator.append(vsLocator$, EntityHandler.ENTITY_KEY, entityKey$);
		vsLocator$=Locator.append(vsLocator$, JRequester.REQUESTER_RESPONSE_LOCATOR, requesterResponseLocator$);
		vsLocator$=Locator.append(vsLocator$, BaseHandler.HANDLER_METHOD,"instantiate");
		JConsoleHandler.execute(console, vsLocator$);
/*
		JGraphViewSelector viewSelector =new JGraphViewSelector();
		String vsLocator$=viewSelector.getLocator();
		vsLocator$=Locator.append(vsLocator$, Entigrator.ENTIHOME, entihome$);
		vsLocator$=Locator.append(vsLocator$, EntityHandler.ENTITY_KEY, entityKey$);
		vsLocator$=Locator.append(vsLocator$, JRequester.REQUESTER_RESPONSE_LOCATOR, requesterResponseLocator$);
		vsLocator$=Locator.append(vsLocator$, BaseHandler.HANDLER_METHOD,"instantiate");
		JConsoleHandler.execute(console, vsLocator$);
*/
	}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
	}
}
/**
 * Get the class name of the facet renderer. 
 * @return the JGraphViewSelector class name .
 */
@Override
public String getFacetRenderer() {
	//return JGraphViewSelector.class.getName();
	return JGraphEditor.class.getName();
}
/**
 * Get the facet handler instance.
 * @return the facet handler instance.	
 */
@Override
public FacetHandler getFacetHandler() {
	return new GraphHandler();
}
/**
 * Get the popup menu for the child node of the facet node 
 * in the digest view.
 * @return the popup menu.	
 */
@Override
public JPopupMenu getPopupMenu(final String digestLocator$) {
	//System.out.println("JFieldsFacetOpenItem:edit:digest locator="+Locator.remove(digestLocator$, Locator.LOCATOR_ICON));
	return super.getPopupMenu(digestLocator$);
	//return null;

}
/**
 * Response on call from the other context.
 *	@param console main console
 *  @param locator$ action's locator 
 */
@Override
public void response(JMainConsole console, String locator$) {
//	System.out.println("JAddressFacetOpenItem:responce:locator="+locator$);
	super.response(console,locator$);

}

@Override
public DefaultMutableTreeNode[] getDigest(Entigrator entigrator,String entityKey$) {
	// TODO Auto-generated method stub
	return null;
}
@Override
public String getFacetIconName() {
	// TODO Auto-generated method stub
	return "graph.png";
}
@Override
public String getWebView(Entigrator entigrator, String locator$) {
      try{
    	 if(debug)
    		 System.out.println("JGraphFacetOpenItem:getWebView:locator="+locator$);
    	  Properties locator=Locator.toProperties(locator$);
    	  String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
    	  String entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
    	  String webHome$=locator.getProperty(WContext.WEB_HOME);
    	  String webRequester$=locator.getProperty(WContext.WEB_REQUESTER);
    	  if(entityKey$==null&&entityLabel$!=null)
    		  entityKey$=entigrator.indx_keyAtLabel(entityLabel$);
    	  if(entityLabel$==null&&entityKey$!=null)
    		  entityLabel$=entigrator.indx_getLabel(entityKey$);
    	  if(debug)
     		 System.out.println("JGraphFacetOpenItem:getWebView:graph key="+entityKey$+" label="+entityLabel$);
     	
    	  Sack graph=entigrator.getEntityAtKey(entityKey$);
    	  ArrayList<String>ll=new ArrayList<String>();
    	  Core[] ca=graph.elementGet("jbookmark");
    	  if(ca!=null)
    		  for(Core c:ca)
    			  ll.add(c.type);
    	  String[] sa=ll.toArray(new String[0]);
    	  if(debug)
      		 System.out.println("JGraphFacetOpenItem:getWebView:sa="+sa.length);
      	
    	  String nodes$=Locator.toString(sa);
    	  if(debug)
       		 System.out.println("JGraphFacetOpenItem:getWebView:nodes="+nodes$);
       	
    	  JWebGraph gr=new JWebGraph();
    	  String grLocator$=gr.getLocator();
    	  Properties grLocator=Locator.toProperties(grLocator$);
    	  grLocator.setProperty(WContext.WEB_HOME,webHome$);
    	  grLocator.setProperty(Entigrator.ENTIHOME,entigrator.getEntihome());
    	  grLocator.setProperty(WContext.WEB_REQUESTER,webRequester$);
    	  grLocator.setProperty(BaseHandler.HANDLER_CLASS,JGraphRenderer.class.getName());
    	  grLocator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
    	  grLocator.setProperty(EntityHandler.ENTITY_LABEL,entityLabel$);
    	  grLocator.setProperty(JGraphRenderer.SHOWN_NODES_LABELS,nodes$);
    	  grLocator.setProperty(JRequester.REQUESTER_ACTION,JGraphRenderer.ACTION_SCOPE_RELATIONS);
    	  if(debug)
     		 System.out.println("JGraphFacetOpenItem:getWebView:graph locator="+Locator.toString(grLocator));
     	 
    	  return gr.getWebView(entigrator, Locator.toString(grLocator));
      }catch(Exception e){
    	  
      }
      return null;
}
/*
public String getWebViewOld(Entigrator entigrator, String locator$) {
	try{
		Properties locator=Locator.toProperties(locator$);
		String webHome$=locator.getProperty(WContext.WEB_HOME);
		String entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
		String webRequester$=locator.getProperty(WContext.WEB_REQUESTER);
		String action$=locator.getProperty(JRequester.REQUESTER_ACTION);
		if(debug)
			System.out.println("JGraphFacetOpenItem:web home="+webHome$+ " locator="+locator$);
			
		if(ACTION_ENTITY.equals(action$)){
			String nodeLabel$=locator.getProperty(SELECTED_NODE_LABEL);
			if(debug)
				System.out.println("JGraphFacetOpenItem:selected node="+nodeLabel$);
			if(nodeLabel$!=null){

			String nodeKey$=entigrator.indx_keyAtLabel(nodeLabel$);   
		    Properties foiLocator=new Properties();
		    foiLocator.setProperty(WContext.WEB_HOME,webHome$);
		    foiLocator.setProperty(WContext.WEB_REQUESTER,webRequester$);
		   	foiLocator.setProperty(BaseHandler.HANDLER_CLASS,JEntityFacetPanel.class.getName());
	    	foiLocator.setProperty(Entigrator.ENTIHOME,entigrator.getEntihome());
	    	foiLocator.setProperty(EntityHandler.ENTITY_KEY,nodeKey$);
			foiLocator.setProperty(EntityHandler.ENTITY_LABEL,nodeLabel$);
			JEntityFacetPanel efp=new JEntityFacetPanel();
			 return efp.getWebView(entigrator, Locator.toString(foiLocator));
			}
		}
		entityKey$=entigrator.indx_keyAtLabel(entityLabel$);
		    Sack entity=entigrator.getEntityAtKey(entityKey$);
	    //    Core[]	ca=entity.elementGet("field");
		StringBuffer sb=new StringBuffer();
		sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
		sb.append("<html>");
		sb.append("<head>");
		
		sb.append(WUtils.getMenuBarScript());
		sb.append(WUtils.getMenuBarStyle());
		
		sb.append("<script>");
		sb.append(WUtils.getSegment(entigrator,"vis","vis.min.js"));
		sb.append(WUtils.getSegment(entigrator,"vis","vis-network.min.js"));
		sb.append(WUtils.getSegment(entigrator,"context.menu","js"));
		sb.append("</script>");
		sb.append("<style>");
		sb.append(WUtils.getSegment(entigrator,"vis","vis.min.css"));
		sb.append(WUtils.getSegment(entigrator,"context.menu","css"));
		sb.append("</style>");
	    sb.append("</head>");
	    sb.append("<body onload=\"onLoad()\" >");
	    sb.append("<ul class=\"menu_list\">");
	    sb.append("<li class=\"menu_item\"><a id=\"back\">Back</a></li>");
	    sb.append("<li class=\"menu_item\"><a href=\""+webHome$+"\">Home</a></li>");
	    String navLocator$=Locator.append(locator$, BaseHandler.HANDLER_CLASS, JBaseNavigator.class.getName());
	    navLocator$=Locator.append(navLocator$, Entigrator.ENTIHOME, entigrator.getEntihome());
	    String navUrl$=webHome$+"?"+WContext.WEB_LOCATOR+"="+Base64.encodeBase64URLSafeString(navLocator$.getBytes());
	    sb.append("<li class=\"menu_item\"><a href=\""+navUrl$+"\">Base</a></li>");
	    sb.append("</ul>");
	    sb.append("<table><tr><td>Base:</td><td><strong>");
	    sb.append(entigrator.getBaseName());
	    sb.append("</strong></td></tr><tr><td>Entity: </td><td><strong>");
	    sb.append(entityLabel$);
	    sb.append("</strong></td></tr>");
	   
	    sb.append("<tr><td>Facet: </td><td><strong>Graph</strong></td></tr></table>");
	    sb.append("<pre id=\"eventSpan\"></pre>");
	    sb.append("<div id=\"panel\"></div>");
	    sb.append("<ul id=\"nodeMenu\" class=\"dropdown-content\">");
	    sb.append("<li id=\"entity\" onclick=\"entity()\"><a href=\"#\">Entity</a></li>");
	    sb.append("<li id=\"relations\" onclick=\"relations()\"><a href=\"#\">Relations</a></li>");
	    sb.append("<li id=\"expand\" onclick=\"expand()\"><a href=\"#\">Expand</a></li>");
	    sb.append("</ul>");
	    boolean network=true;
	    if(ACTION_RELATIONS.equals(action$)){
	    	String nodeLabel$=locator.getProperty(SELECTED_NODE_LABEL);
			if(debug)
				System.out.println("JGraphFacetOpenItem:relations:selected node="+nodeLabel$);
			if(nodeLabel$!=null){
				// sb.append(getRelations(entigrator,entityKey$,nodeLabel$));
				sb.append(getGraph(getRelationsSet(entigrator, entityKey$, nodeLabel$)));
				 network=false;
			}
	    }
	    if(ACTION_EXPAND.equals(action$)){
	    	String nodeLabel$=locator.getProperty(SELECTED_NODE_LABEL);
	    	String nodeLabels$=locator.getProperty(NODE_LABELS);
	    	if(debug)
				System.out.println("JGraphFacetOpenItem:relations:selected selection="+nodeLabel$+" nodes="+nodeLabels$);
		
				// sb.append(getRelations(entigrator,entityKey$,nodeLabel$));
				sb.append(getGraph(getExpansionSet(entigrator, entityKey$, nodeLabel$,nodeLabels$)));
				 network=false;
	    }
	    if(network)
	      sb.append(getGraph(getOriginSet(entigrator, entityKey$)));
        sb.append("<script>");
        sb.append("var selectedNodeLabel='';");
        sb.append("var selection=new Array();"); 		
	    sb.append("function onLoad() {");
	    sb.append("initBack(\""+this.getClass().getName()+"\",\""+webRequester$+"\");");
	    sb.append("}");
	    
	    sb.append("function entity(){");
	    locator$=Locator.append(locator$, JRequester.REQUESTER_ACTION, ACTION_ENTITY);
	    sb.append(" var locator=\""+locator$+"\";");
    	sb.append(" locator=appendProperty(locator,\""+SELECTED_NODE_LABEL+"\",selectedNodeLabel);");
    	sb.append(" locator=appendProperty(locator,\""+WContext.WEB_REQUESTER+"\",\""+getClass().getName()+"\");");
    	//sb.append("console.log(locator);");
    	sb.append(" var href=\""+webHome$+"?"+WContext.WEB_LOCATOR+"=\"+window.btoa(locator);");
	   
    	sb.append("window.location.assign(href);");
	    sb.append("}");

	    sb.append("function relations(){");
	    locator$=Locator.append(locator$, JRequester.REQUESTER_ACTION, ACTION_RELATIONS);
	    sb.append(" var locator=\""+locator$+"\";");
    	sb.append(" locator=appendProperty(locator,\""+SELECTED_NODE_LABEL+"\",selectedNodeLabel);");
    	sb.append(" locator=appendProperty(locator,\""+WContext.WEB_REQUESTER+"\",\""+getClass().getName()+"\");");
    	//sb.append("console.log(locator);");
    	sb.append(" var href=\""+webHome$+"?"+WContext.WEB_LOCATOR+"=\"+window.btoa(locator);");
    	sb.append("window.location.assign(href);");
	    sb.append("}");
	    
	    sb.append("function expand(){");
	    locator$=Locator.append(locator$, JRequester.REQUESTER_ACTION, ACTION_EXPAND);
	    sb.append(" var locator=\""+locator$+"\";");
	    sb.append(" var nl=new Array();");
	  //  sb.append("for(var i=0;i<nodes.length;i++)");
	   // sb.append("nl.push(nodes[i].label);");
	    sb.append(" var nodeLabels=selection.join(\"_;A_\");"); 
    	sb.append(" locator=appendProperty(locator,\""+SELECTED_NODE_LABEL+"\",selectedNodeLabel);");
    	sb.append(" locator=appendProperty(locator,\""+NODE_LABELS+"\",nodeLabels);");
    	sb.append(" locator=appendProperty(locator,\""+WContext.WEB_REQUESTER+"\",\""+getClass().getName()+"\");");
    	sb.append("console.log(nodeLabels);");
    	sb.append(" var href=\""+webHome$+"?"+WContext.WEB_LOCATOR+"=\"+window.btoa(locator);");
    	sb.append("window.location.assign(href);");
	    sb.append("}");

    sb.append("window.localStorage.setItem(\""+this.getClass().getName()+"\",\""+Base64.encodeBase64URLSafeString(locator$.getBytes())+"\");");
	 	    sb.append("</script>");
	    sb.append("</body>");
	    sb.append("</html>");
	    return sb.toString();
        
	}catch(Exception e){
		Logger.getLogger(JGraphFacetOpenItem.class.getName()).severe(e.toString());	
	}
	return null;
}
*/
private static String getGraph( String dataSet$){
	try{
	        StringBuffer sb=new StringBuffer();	
			sb.append("<script type=\"text/javascript\">");
			sb.append("var nl = new Array();");
			sb.append(dataSet$);
		  // create a network
						sb.append(" var heights = window.innerHeight;");
						sb.append(" document.getElementById(\"panel\").style.height = heights -50 + \"px\";");
						sb.append(" var container = document.getElementById('panel');");
						
						sb.append(" var data = {");
						sb.append("nodes: nodes,");
						sb.append("edges: edges");
						sb.append("};");
						sb.append("var options = {interaction:{hover:true}};");
						sb.append("var network = new vis.Network(container, data, options);");
						sb.append("network.on(\"startStabilizing\", function (params) {");
						sb.append(" document.getElementById('eventSpan').innerHTML = '<h3>Starting Stabilization</h3>';");
						   // console.log("started")
						sb.append("});");
						sb.append(" network.on(\"stabilized\", function (params) {");
						sb.append("document.getElementById('eventSpan').innerHTML = '<h3>Stabilized!('+params.iterations+' iterations)</h3>';");

						  //  console.log("stabilized!", params);
						sb.append(" });");
						sb.append(" network.on(\"selectNode\", function (params) {");
						sb.append("console.log('selectNode Event:', params);");
						sb.append(" selectedNodeLabel=nodes.get(params.nodes[0]).label;");
						sb.append(" selection=nl;"); 
						sb.append("console.log('selectNode Event:', nl);");
								//+ "params.nodes[0].label;");
						sb.append("var menu= document.getElementById(\"nodeMenu\");");
						sb.append("menu.style.display = 'inline-block';"); 
						sb.append("menu.style.position = \"absolute\";");
						sb.append("menu.style.left = params.pointer.DOM.x+'px';");
						sb.append("menu.style.top = params.pointer.DOM.y+'px';");
						sb.append(" });");
						sb.append(" network.on(\"deselectNode\", function (params) {");
						sb.append("console.log('selectNode Event:', params);");
						sb.append("var menu= document.getElementById(\"nodeMenu\");");
						sb.append("menu.style.display = 'none';"); 
						sb.append(" });");
						sb.append("network.on(\"stabilized\", function(params) {");
						   // console.log('stabilized='+params);
						sb.append("if(!nodes){");
						sb.append("window.localStorage.setItem(\"nodes\",nodes);");
						sb.append("}");
						sb.append("if(!edges){");
						sb.append("window.localStorage.setItem(\"edges\",edges);");
						sb.append("}});");
						sb.append("</script>");
						return sb.toString();
	
	}catch(Exception e){
		Logger.getLogger(JGraphFacetOpenItem.class.getName()).severe(e.toString());	
	}
	return null;
	
	
	
}
private static String getRelationsSet(Entigrator entigrator, String graphKey$,String nodeLabel$){
	try{
		 Sack graph=entigrator.getEntityAtKey(graphKey$);
		 String nodeKey$=entigrator.indx_keyAtLabel(nodeLabel$);
		 Core[] ca=graph.elementGet("bond");
		 if(ca==null)
			 return null;
		ArrayList<String>nbl=new ArrayList<String>();
		ArrayList<String>bkl=new ArrayList<String>();
		nbl.add(nodeKey$);
		for(Core c:ca){
			if(c.value.equals(nodeKey$))
				if(!nbl.contains(c.type)){
					nbl.add(c.type);
					bkl.add(c.name);
				}
	    	if(c.type.equals(nodeKey$))
				if(!nbl.contains(c.value)){
					nbl.add(c.value);
					bkl.add(c.name);
				}
			}	
	   StringBuffer sb=new StringBuffer();
    		String title$;
			String icon$;
			Core node;
		//sb.append("var nl = new Array();");	
			for(String s:nbl){
				node=graph.getElementItem("node", s);
				title$=node.value;
				sb.append("nl.push('"+title$+"');");
			}
		sb.append("var nodes = new vis.DataSet([");
			for(String s:nbl){
				node=graph.getElementItem("node", s);
				title$=node.value;
				icon$=entigrator.readIconFromIcons(node.type);
				sb.append("{id: "+graph.getElementItemAt("vertex", s)+", label: '"+title$+"',title: '"+title$+"',image: \"data:image/png;base64,"+icon$+"\" ,shape :'image' },");
			}
			sb.setLength(sb.length() - 1);
		sb.append("]);");
		sb.append("var edges = new vis.DataSet([");
			Core[] ea=graph.elementGet("edge");
						String [] sa;
			for(Core c:ea){
				    for (String s:bkl)
				    	if(s.equals(c.name))
						    try{
							sa=c.type.split("\\+");
						    if(debug)
								System.out.println("JgraphFacetOpenItem:getNetwork:add edge="+"{from: "+sa[0]+", to: "+sa[1]+"},");
						
							sb.append("{from: "+sa[0]+", to: "+sa[1]+"},");
						    }catch(Exception ee){
						    	Logger.getLogger(JGraphFacetOpenItem.class.getName()).info(ee.toString());	
						    }
						}
						sb.setLength(sb.length() - 1);
						sb.append("]);");
		return sb.toString();				
	}catch(Exception e){
		Logger.getLogger(JGraphFacetOpenItem.class.getName()).severe(e.toString());	
	}
	return null;
	}
private static String getExpansionSet(Entigrator entigrator, String graphKey$,String nodeLabel$,String nodeLabels$){
	try{
		 Sack graph=entigrator.getEntityAtKey(graphKey$);
		 String nodeKey$=entigrator.indx_keyAtLabel(nodeLabel$);
		 Core[] ca=graph.elementGet("bond");
		 
		 if(ca==null)
			 return null;
		ArrayList<String>nbl=new ArrayList<String>();
		ArrayList<String>bkl=new ArrayList<String>();
		nbl.add(nodeKey$);
		for(Core c:ca){
			if(c.value.equals(nodeKey$))
				if(!nbl.contains(c.type)){
					nbl.add(c.type);
					bkl.add(c.name);
				}
	    	if(c.type.equals(nodeKey$))
				if(!nbl.contains(c.value)){
					nbl.add(c.value);
					bkl.add(c.name);
				}
			}	
	   StringBuffer sb=new StringBuffer();
    		String title$;
			String icon$;
			Core node;
		//sb.append("var nl = new Array();");	
			for(String s:nbl){
				node=graph.getElementItem("node", s);
				title$=node.value;
				sb.append("nl.push('"+title$+"');");
			}
			if(nodeLabels$!=null){
				String[] sa=Locator.toArray(nodeLabels$);
				if(sa!=null)
					for(String s:sa){
						if(debug)
							System.out.println("JgraphFacetOpenItem:getExpansionSet:add node label="+s);
						nbl.add(entigrator.indx_keyAtLabel(s));
					}
			}
		sb.append("var nodes = new vis.DataSet([");
		ArrayList<String>ids=new ArrayList<String>();	
		String id$;
		for(String s:nbl){
				try{
				id$=graph.getElementItemAt("vertex", s);
				if(ids.contains(id$))
					continue;
				ids.add(id$);
				node=graph.getElementItem("node", s);
				title$=node.value;
				icon$=entigrator.readIconFromIcons(node.type);
				sb.append("{id: "+graph.getElementItemAt("vertex", s)+", label: '"+title$+"',title: '"+title$+"',image: \"data:image/png;base64,"+icon$+"\" ,shape :'image' },");
				}catch(Exception ee){
					System.out.println("JgraphFacetOpenItem:getExpansionSet:"+ee.toString());
				}
			}
			sb.setLength(sb.length() - 1);
			
		sb.append("]);");
		if(debug)
			System.out.println("JgraphFacetOpenItem:getExpansionSet:nodes="+nbl.size());
	
		sb.append("var edges = new vis.DataSet([");
			Core[] ea=graph.elementGet("edge");
						String [] sa;
			for(Core c:ea){
				    for (String s:bkl)
				    	if(s.equals(c.name))
						    try{
							sa=c.type.split("\\+");
						    if(debug)
								System.out.println("JgraphFacetOpenItem:getNetwork:add edge="+"{from: "+sa[0]+", to: "+sa[1]+"},");
						
							sb.append("{from: "+sa[0]+", to: "+sa[1]+"},");
						    }catch(Exception ee){
						    	Logger.getLogger(JGraphFacetOpenItem.class.getName()).info(ee.toString());	
						    }
						}
						sb.setLength(sb.length() - 1);
						sb.append("]);");
		return sb.toString();				
	}catch(Exception e){
		Logger.getLogger(JGraphFacetOpenItem.class.getName()).severe(e.toString());	
	}
	return null;
	}
private static String getOriginSet(Entigrator entigrator, String graphKey$){
	try{
		if(debug)
			System.out.println("JgraphFacetOpenItem:getoriginSet:graph key="+graphKey$);
		StringBuffer sb=new StringBuffer();
		Sack graph=entigrator.getEntityAtKey(graphKey$);
		
		sb.append("var nodes = new vis.DataSet([");
		Core[] va=graph.elementGet("vertex");
		if(debug)
			System.out.println("JgraphFacetOpenItem:getOriginSet:va="+va.length);
		String title$;
		String icon$;
		Core node;
		for(Core c:va){
			node=graph.getElementItem("node", c.name);
			title$=node.value;
			icon$=entigrator.readIconFromIcons(node.type);
			//if(debug)
			//	System.out.println("{id: "+c.value+", label: '"+title$+"',title: '"+title$+"',image: 'data:image/png;base64,"+icon$+"', shape : 'image' }");
			sb.append("{id: "+c.value+", label: '"+title$+"',title: '"+title$+"',image: \"data:image/png;base64,"+icon$+"\" ,shape :'image' },");
		}
		sb.setLength(sb.length() - 1);
		sb.append("]);");
		sb.append("var edges = new vis.DataSet([");
		Core[] ea=graph.elementGet("edge");
		String [] sa;
		
		for(Core c:ea){
		    try{
			sa=c.type.split("\\+");
		    //if(debug)
			//	System.out.println("JgraphFacetOpenItem:getOriginSet:add edge="+"{from: "+sa[0]+", to: "+sa[1]+"},");
		
			sb.append("{from: "+sa[0]+", to: "+sa[1]+"},");
		    }catch(Exception ee){
		    	Logger.getLogger(JGraphFacetOpenItem.class.getName()).info(ee.toString());	
		    }
		}
		sb.setLength(sb.length() - 1);
		sb.append("]);");
		
		return sb.toString();
	}catch(Exception e){
		Logger.getLogger(JGraphFacetOpenItem.class.getName()).severe(e.toString());	
	}
	return null;
	}

}
