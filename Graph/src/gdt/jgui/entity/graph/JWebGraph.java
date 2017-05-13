package gdt.jgui.entity.graph;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.JMenu;
import javax.swing.JPanel;

import org.apache.commons.codec.binary.Base64;

import gdt.data.entity.BaseHandler;
import gdt.data.entity.Bond;
import gdt.data.entity.EdgeHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.GraphHandler;
import gdt.data.entity.NodeHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
import gdt.data.store.StoreAdapter;
import gdt.jgui.base.JBaseNavigator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JFacetRenderer;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.console.WContext;
import gdt.jgui.console.WUtils;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.entity.bonddetail.JBondDetailFacetOpenItem;
import gdt.jgui.entity.edge.JBondsPanel;

public class JWebGraph implements WContext {
static boolean debug=false;
//private JMainConsole console;
private String entihome$;
private String entityKey$;
private String entityLabel$;
public JWebGraph(){
	
}
	@Override
	public String getWebConsole(Entigrator entigrator , String locator$) {
		return null;
	}

	//@Override
	public  String getWebView(Entigrator entigrator, String locator$) {
		try{
			//boolean initSelector=false;
			Properties locator=Locator.toProperties(locator$);
			String webHome$=locator.getProperty(WContext.WEB_HOME);
			String entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
			String edgeLabel$=locator.getProperty(JBondsPanel.EDGE_LABEL);
			String webRequester$=locator.getProperty(WContext.WEB_REQUESTER);
			String action$=locator.getProperty(JRequester.REQUESTER_ACTION);
			String filteredNodeLabels$=null;
			String[] sa=null;
			String shownLabels$=locator.getProperty(JGraphRenderer.SHOWN_NODES_LABELS);
			String nodeLabel$=locator.getProperty(JGraphRenderer.SELECTED_NODE_LABEL);
			if(shownLabels$!=null)
			 sa=Locator.toArray(shownLabels$);
			else
				sa=new String[]{entityLabel$}; 
			 
			if(debug)
				System.out.println("JWebGraph:web home="+webHome$+ " locator="+locator$);
				
			if(JGraphRenderer.ACTION_ENTITY.equals(action$)){
				//String nodeLabel$=locator.getProperty(SELECTED_NODE_LABEL);
				if(debug)
					System.out.println("JWebGraph:selected node="+nodeLabel$);
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
			
			String entityKey$=entigrator.indx_keyAtLabel(entityLabel$);
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
		   // sb.append("<li class=\"menu_item\"><a href=\""+navUrl$+"\">Base</a></li>");
		    sb.append("<li class=\"dropdown\">");
		    sb.append("<a href=\"javascript:void(0)\" class=\"dropbtn\">Context</a>");
		    sb.append("<ul class=\"dropdown-content\">");
		    sb.append("<li id=\"scope\" onclick=\"graphScope()\"><a href=\"#\">Scope</a></li>");
		    sb.append("<li id=\"network\" onclick=\"graphNetwork()\"><a href=\"#\">Network</a></li>");
		    sb.append("</ul>");
		    sb.append("</li>");
		    sb.append("<li class=\"menu_item\"><a href=\""+WContext.ABOUT+"\">About</a></li>");
		    sb.append("</ul>");
		    sb.append("<table><tr><td>Base:</td><td><strong>");
		    sb.append(entigrator.getBaseName());
		    sb.append("</strong></td></tr><tr><td>Entity: </td><td><strong>");
		    sb.append(entityLabel$);
		    sb.append("</strong></td></tr>");
		    sb.append("<tr><td>Facet: </td><td><strong>Node</strong></td></tr>");
		    sb.append("<tr><td>Context: </td><td><strong>Graph</strong></td></tr></table>");
		   
		    sb.append("<table>");
		   
		   //initSelector=true;
		    
		    if(JGraphRenderer.ACTION_EDGE.equals(action$)&&shownLabels$!=null){
		    	sa=EdgeHandler.filterNodesAtEdge(entigrator, sa, edgeLabel$);
		    	filteredNodeLabels$=Locator.toString(sa);
		    }
		    sb.append("<tr><td><button onclick=\"showNodeMenu()\">Node</button></td>");
		    sb.append("<td><select id=\"nselector\" size=\"1\" onchange=\"selectNode()\">");
		    if(sa!=null)
		    for(String s:sa){
		    		//System.out.println("JDesignPanel:getWebView:property name="+propertyName$+" candidate="+s);
		    		s=s.replaceAll("\"", "&quot;");
	            	s=s.replaceAll("'", "&#39;");
	    		    sb.append("<option value=\""+s+"\">"+s+"</option>");
		    		}
		    sb.append("</select>");
		    sb.append("</td>");
		    if(!JGraphRenderer.ACTION_EDGE.equals(action$)){
		    sb.append("<td><button onclick=\"showEdge()\">Edge</button></td>");
			    sb.append("<td><select id=\"eselector\" size=\"1\" onchange=\"selectEdge()\">");
			    ArrayList<String>el=new ArrayList<String>();
		        Sack node;
				Core[] ca;
				String eLabel$;
				for(String n:sa){
					node=entigrator.ent_getAtLabel(n);
					if(node==null)
						continue;
					ca=node.elementGet("edge");
					if(ca==null)
						continue;
					for(Core c:ca){
						eLabel$=entigrator.indx_getLabel(c.value);
						if(eLabel$!=null){	
						if(!el.contains(eLabel$))
							el.add(eLabel$);
						}
					}
					Collections.sort(el);
				}
		        for(String s:el){
		        	s=s.replaceAll("\"", "&quot;");
	            	s=s.replaceAll("'", "&#39;");
		            sb.append("<option value=\""+s+"\">"+s+"</option>");
	    		}
			    sb.append("</select>");
			    sb.append("</td>");
			    sb.append("<td>");
			    sb.append("<input type=\"checkbox\" id=\"newTab\">New tab");
			    sb.append("</td>");
		    }
			    sb.append("</tr>");
		    sb.append("</table>");
		    sb.append("<pre id=\"eventSpan\"></pre>");
		    sb.append("<div id=\"panel\"></div>");
		   // sb.append("<div id=\"pin\"></div>");
		    sb.append("<ul id=\"nodeMenu\" class=\"dropdown-content\">");
		    sb.append("<li id=\"entity\" onclick=\"entity()\"><a href=\"#\">Entity</a></li>");
		    sb.append("<li id=\"relations\" onclick=\"relations()\"><a href=\"#\">Relations</a></li>");
		    sb.append("<li id=\"expand\" onclick=\"expand()\"><a href=\"#\">Expand</a></li>");
		    sb.append("<li id=\"nw\" onclick=\"nw()\"><a href=\"#\">Network</a></li>");
		    sb.append("</ul>");
		    sb.append("<ul id=\"edgeMenu\" class=\"dropdown-content\">");
		    sb.append("<li id=\"details\" onclick=\"details()\"><a href=\"#\">Details</a></li>");
		    sb.append("</ul>");
		    if(debug)
				System.out.println("JWebGraph:getWebView:5");
		    if(action$==null)
		    	action$=JGraphRenderer.ACTION_RELATIONS;
		    if(JGraphRenderer.ACTION_RELATIONS.equals(action$)){
		    	//String nodeLabel$=locator.getProperty(SELECTED_NODE_LABEL);
				if(debug)
					System.out.println("JWebGraph:relations:selected node="+nodeLabel$);
				if(nodeLabel$!=null){
					//nodeLabel$=nodeLabel$.replaceAll("\"", "&quot;");
					//nodeLabel$=nodeLabel$.replaceAll("'", "&#39;");
					// sb.append(getRelations(entigrator,entityKey$,nodeLabel$));
					sb.append(getGraph(getRelations(entigrator,  nodeLabel$)));
					//sb.append(getGraph(getScopeExpansion(entigrator,  nodeLabel$,shownLabels$)));
				}
		    }
		    if(JGraphRenderer.ACTION_NETWORK_RELATIONS.equals(action$)){
		    	//String nodeLabel$=locator.getProperty(SELECTED_NODE_LABEL);
		    	
				if(debug)
					System.out.println("JWebGraph:network relations:selected node="+nodeLabel$ +" shown labels="+shownLabels$);
				//nodeLabel$=nodeLabel$.replaceAll("\"", "&quot;");
				//nodeLabel$=nodeLabel$.replaceAll("'", "&#39;");
					// sb.append(getRelations(entigrator,entityKey$,nodeLabel$));
					sb.append(getGraph(getNetworkRelations(entigrator,  nodeLabel$,shownLabels$)));
					 
				
		    }
		    if(JGraphRenderer.ACTION_SCOPE_RELATIONS.equals(action$)){
		    	//String nodeLabel$=locator.getProperty(SELECTED_NODE_LABEL);
				if(debug)
					System.out.println("JWebGraph:scope relations:selected node="+nodeLabel$+" schown labels="+shownLabels$);
				//nodeLabel$=nodeLabel$.replaceAll("\"", "&quot;");
				//nodeLabel$=nodeLabel$.replaceAll("'", "&#39;");	
				sb.append(getGraph(getScopeRelations(entigrator, nodeLabel$,shownLabels$)));
					 
				
		    }
		    if(JGraphRenderer.ACTION_EXPAND.equals(action$)){
		    	//String nodeLabel$=locator.getProperty(SELECTED_NODE_LABEL);
		    	//shownLabels$=locator.getProperty(SHOWN_NODES_LABELS);
		    	if(debug)
					System.out.println("JWebGraph:expansion:selected selection="+nodeLabel$+" nodes="+shownLabels$);
					// sb.append(getRelations(entigrator,entityKey$,nodeLabel$));
		    	//nodeLabel$=nodeLabel$.replaceAll("\"", "&quot;");
				//nodeLabel$=nodeLabel$.replaceAll("'", "&#39;");
					sb.append(getGraph(getExpansion(entigrator, nodeLabel$,shownLabels$)));
					 
		    }
		    if(JGraphRenderer.ACTION_NETWORK.equals(action$)){
		    	//String nodeLabel$=locator.getProperty(SELECTED_NODE_LABEL);
		    	// shownLabels$=locator.getProperty(SHOWN_NODES_LABELS);
		    	if(debug)
					System.out.println("JWebGraph:network:selected selection="+nodeLabel$+" nodes="+shownLabels$);
					// sb.append(getRelations(entigrator,entityKey$,nodeLabel$));
		    	//nodeLabel$=nodeLabel$.replaceAll("\"", "&quot;");
				//nodeLabel$=nodeLabel$.replaceAll("'", "&#39;");	
		    	sb.append(getGraph(getNetwork(entigrator, nodeLabel$,shownLabels$)));
					 
		    }
		    if(JGraphRenderer.ACTION_EDGE.equals(action$)){
		    	//String nodeLabel$=locator.getProperty(SELECTED_NODE_LABEL);
		    	
		    	if(debug)
					System.out.println("JWebGraph:edge:label="+edgeLabel$+" selected selection="+nodeLabel$+" nodes="+shownLabels$);
					// sb.append(getRelations(entigrator,entityKey$,nodeLabel$));
		    	//edgeLabel$=edgeLabel$.replaceAll("\"", "&quot;");
		    	//edgeLabel$=edgeLabel$.replaceAll("'", "&#39;");
					sb.append(getGraph(getEdge(entigrator, edgeLabel$,filteredNodeLabels$)));
					 
		    }
	    	

	        sb.append("<script>");
	        sb.append("var selectedNodeLabel='';");
	        sb.append("var selection=new Array();");
	        sb.append(" var shownNodesLabels;");
	        sb.append(" var bondKey;");
	        sb.append(" var edgeLabel;");
	        sb.append(" var network;");
	       // sb.append("var na=new Array();"); 
	       // sb.append("var ea=new Array();"); 
	/*
	        sb.append("function onLoad() {");
		    sb.append("initBack(\""+this.getClass().getName()+"\",\""+webRequester$+"\");");
		    sb.append("}");
		*/    
		    sb.append("function entity(){");
		    locator$=Locator.append(locator$, JRequester.REQUESTER_ACTION, JGraphRenderer.ACTION_ENTITY);
		    locator$=locator$.replaceAll("\"", "&quot;");
		    locator$=locator$.replaceAll("'", "&#39;");
		    sb.append(" var locator=\""+locator$+"\";");
	    	sb.append(" locator=appendProperty(locator,\""+JGraphRenderer.SELECTED_NODE_LABEL+"\",selectedNodeLabel);");
	    	sb.append(" locator=appendProperty(locator,\""+WContext.WEB_REQUESTER+"\",\""+getClass().getName()+"\");");
	    	sb.append(" locator=appendProperty(locator,\""+EntityHandler.ENTITY_LABEL+"\",selectedNodeLabel);");
	    	sb.append(" locator=appendProperty(locator,\""+BaseHandler.HANDLER_CLASS+"\",\""+ JEntityFacetPanel.class.getName()+"\");");
	    	sb.append(" locator=appendProperty(locator,\""+Entigrator.ENTIHOME+"\",\""+ entigrator.getEntihome()+"\");");
	     // 	sb.append("console.log(locator);");
	    	sb.append(" var href=\""+webHome$+"?"+WContext.WEB_LOCATOR+"=\"+window.btoa(locator);");
	    //	sb.append("console.log(href);");
	    	sb.append("window.location.assign(href);");
		    sb.append("}");

		    sb.append("function relations(){");
		    sb.append(" var locator=\""+locator$+"\";");
	    	sb.append(" locator=appendProperty(locator,\""+JGraphRenderer.SELECTED_NODE_LABEL+"\",selectedNodeLabel);");
	    	sb.append(" locator=appendProperty(locator,\""+EntityHandler.ENTITY_LABEL+"\",selectedNodeLabel);");
	    	sb.append(" locator=appendProperty(locator,\""+WContext.WEB_REQUESTER+"\",\""+getClass().getName()+"\");");
	    	sb.append(" locator=appendProperty(locator,\""+JRequester.REQUESTER_ACTION+"\",\""+JGraphRenderer.ACTION_RELATIONS+"\");");
	    	sb.append(" locator=appendProperty(locator,\""+BaseHandler.HANDLER_CLASS+"\",\""+ getClass().getName()+"\");");
	    	//sb.append("console.log(locator);");
	    	sb.append(" var href=\""+webHome$+"?"+WContext.WEB_LOCATOR+"=\"+window.btoa(locator);");
	    	sb.append("window.location.assign(href);");
		    sb.append("}");
		    
		    sb.append("function expand(){");
		    sb.append(" var locator=\""+locator$+"\";");
		    locator$=Locator.append(locator$, JRequester.REQUESTER_ACTION, JGraphRenderer.ACTION_EXPAND);
	    	sb.append(" locator=appendProperty(locator,\""+JGraphRenderer.SELECTED_NODE_LABEL+"\",selectedNodeLabel);");
	    	sb.append(" locator=appendProperty(locator,\""+JGraphRenderer.SHOWN_NODES_LABELS+"\",shownNodesLabels);");
	    	sb.append(" locator=appendProperty(locator,\""+WContext.WEB_REQUESTER+"\",\""+getClass().getName()+"\");");
	    	sb.append(" locator=appendProperty(locator,\""+JRequester.REQUESTER_ACTION+"\",\""+JGraphRenderer.ACTION_EXPAND+"\");");
	    	sb.append(" locator=appendProperty(locator,\""+BaseHandler.HANDLER_CLASS+"\",\""+ getClass().getName()+"\");");
	    	//sb.append("console.log(nodeLabels);");
	    	sb.append(" var href=\""+webHome$+"?"+WContext.WEB_LOCATOR+"=\"+window.btoa(locator);");
	    	sb.append("window.location.assign(href);");
		    sb.append("}");
		    
		    sb.append("function nw(){");
		    sb.append(" var locator=\""+locator$+"\";");
		    locator$=Locator.append(locator$, JRequester.REQUESTER_ACTION, JGraphRenderer.ACTION_EXPAND);
	    	sb.append(" locator=appendProperty(locator,\""+JGraphRenderer.SELECTED_NODE_LABEL+"\",selectedNodeLabel);");
	    	sb.append(" locator=appendProperty(locator,\""+JGraphRenderer.SHOWN_NODES_LABELS+"\",shownNodesLabels);");
	  //  	sb.append(" locator=appendProperty(locator,\""+WContext.WEB_REQUESTER+"\",\""+getClass().getName()+"\");");
	    	sb.append(" locator=appendProperty(locator,\""+JRequester.REQUESTER_ACTION+"\",\""+JGraphRenderer.ACTION_NETWORK+"\");");
	    	sb.append(" locator=appendProperty(locator,\""+BaseHandler.HANDLER_CLASS+"\",\""+ getClass().getName()+"\");");
	    	//sb.append("console.log(nodeLabels);");
	    	sb.append(" var href=\""+webHome$+"?"+WContext.WEB_LOCATOR+"=\"+window.btoa(locator);");
	    	sb.append("window.location.assign(href);");
		    sb.append("}");
		    sb.append("function details(){");
		    JBondDetailFacetOpenItem bdi=new JBondDetailFacetOpenItem();
		    String bdiLocator$=bdi.getLocator();
		    Properties bdiLocator=Locator.toProperties(bdiLocator$);
		     bdiLocator.setProperty(WContext.WEB_HOME, webHome$);
		    bdiLocator.setProperty(Entigrator.ENTIHOME, entigrator.getEntihome());
		    bdiLocator.setProperty(BaseHandler.HANDLER_CLASS,JBondDetailFacetOpenItem.class.getName());
		    sb.append(" var locator=\""+Locator.toString(bdiLocator)+"\";");
	    	sb.append(" locator=appendProperty(locator,\""+JBondsPanel.EDGE_LABEL+"\",edgeLabel);");
	    	sb.append(" locator=appendProperty(locator,\""+JBondsPanel.BOND_KEY+"\",bondKey);");
	    	sb.append("console.log('locator='+locator);");
	    	sb.append(" var href=\""+webHome$+"?"+WContext.WEB_LOCATOR+"=\"+window.btoa(locator);");
	    	sb.append(" var win = window.open(href, '_blank');");
	    	sb.append(" win.focus();");
	    	//sb.append("window.location.assign(href);");
		    sb.append("}");

		    sb.append("function showNodeMenu(){");
		    sb.append("var selector = document.getElementById(\"nselector\");");
		    sb.append("selectedNodeLabel = selector.options[selector.selectedIndex].text;");
		    sb.append("var menu= document.getElementById(\"nodeMenu\");");
			sb.append("menu.style.display = 'inline-block';"); 
			sb.append("menu.style.position = \"absolute\";");
			sb.append("var nodePosition = network.getPositions([selectedNodeLabel]);");
			sb.append("var nodeXY = network.canvasToDOM({x: nodePosition[selectedNodeLabel].x, y: nodePosition[selectedNodeLabel].y});");
			sb.append("menu.style.left =nodeXY.x+'px';");
			sb.append("menu.style.top =nodeXY.y+'px';");
		   sb.append("}");
		    
		    sb.append("function selectNode(){");
		    sb.append("var menu= document.getElementById(\"nodeMenu\");");
			sb.append("menu.style.display = 'none';"); 
		    sb.append("}");
		    sb.append("function showEdge(){");
		    sb.append("var selector = document.getElementById(\"eselector\");");
		    sb.append("edgeLabel = selector.options[selector.selectedIndex].text;");
		    sb.append(" var locator=\""+locator$+"\";");
		    locator$=Locator.append(locator$, JRequester.REQUESTER_ACTION, JGraphRenderer.ACTION_EDGE);
	    	//sb.append(" locator=appendProperty(locator,\""+SELECTED_NODE_LABEL+"\",selectedNodeLabel);");
	    	sb.append(" locator=appendProperty(locator,\""+JGraphRenderer.SHOWN_NODES_LABELS+"\",shownNodesLabels);");
	    	sb.append(" locator=appendProperty(locator,\""+WContext.WEB_REQUESTER+"\",\""+getClass().getName()+"\");");
	    	sb.append(" locator=appendProperty(locator,\""+JRequester.REQUESTER_ACTION+"\",\""+JGraphRenderer.ACTION_EDGE+"\");");
	    	sb.append(" locator=appendProperty(locator,\""+BaseHandler.HANDLER_CLASS+"\",\""+ getClass().getName()+"\");");
	    	sb.append(" locator=appendProperty(locator,\""+JBondsPanel.EDGE_LABEL+"\",edgeLabel);");
	    	//sb.append("console.log(nodeLabels);");
	    	sb.append(" var href=\""+webHome$+"?"+WContext.WEB_LOCATOR+"=\"+window.btoa(locator);");
	    	sb.append("if(document.getElementById(\"newTab\").checked){");
	    	sb.append(" window.open(href, '_blank');");
	    	sb.append(" }else{");
	    	sb.append("window.location.assign(href);}");
		    sb.append("}");
	         
		    sb.append("function graphScope(){");
		    sb.append(" var locator=\""+locator$+"\";");
	    	sb.append(" locator=appendProperty(locator,\""+JGraphRenderer.SELECTED_NODE_LABEL+"\",selectedNodeLabel);");
	    	sb.append(" locator=appendProperty(locator,\""+JGraphRenderer.SHOWN_NODES_LABELS+"\",shownNodesLabels);");
	    	sb.append(" locator=appendProperty(locator,\""+EntityHandler.ENTITY_LABEL+"\",selectedNodeLabel);");
	    	sb.append(" locator=appendProperty(locator,\""+WContext.WEB_REQUESTER+"\",\""+getClass().getName()+"\");");
	    	sb.append(" locator=appendProperty(locator,\""+JRequester.REQUESTER_ACTION+"\",\""+JGraphRenderer.ACTION_SCOPE_RELATIONS+"\");");
	    	sb.append(" locator=appendProperty(locator,\""+BaseHandler.HANDLER_CLASS+"\",\""+ getClass().getName()+"\");");
	    	//sb.append("console.log(locator);");
	    	sb.append(" var href=\""+webHome$+"?"+WContext.WEB_LOCATOR+"=\"+window.btoa(locator);");
	    	sb.append("window.location.assign(href);");

		    sb.append("}");
		    
		    sb.append("function graphNetwork(){");
		    sb.append(" var locator=\""+locator$+"\";");
	    	sb.append(" locator=appendProperty(locator,\""+JGraphRenderer.SELECTED_NODE_LABEL+"\",selectedNodeLabel);");
	    	sb.append(" locator=appendProperty(locator,\""+JGraphRenderer.SHOWN_NODES_LABELS+"\",shownNodesLabels);");
	    	sb.append(" locator=appendProperty(locator,\""+EntityHandler.ENTITY_LABEL+"\",selectedNodeLabel);");
	    	sb.append(" locator=appendProperty(locator,\""+WContext.WEB_REQUESTER+"\",\""+getClass().getName()+"\");");
	    	sb.append(" locator=appendProperty(locator,\""+JRequester.REQUESTER_ACTION+"\",\""+JGraphRenderer.ACTION_NETWORK_RELATIONS+"\");");
	    	sb.append(" locator=appendProperty(locator,\""+BaseHandler.HANDLER_CLASS+"\",\""+ getClass().getName()+"\");");
	    	//sb.append("console.log(locator);");
	    	sb.append(" var href=\""+webHome$+"?"+WContext.WEB_LOCATOR+"=\"+window.btoa(locator);");
	    	sb.append("window.location.assign(href);");
		
		    sb.append("}");
	    sb.append("window.localStorage.setItem(\""+this.getClass().getName()+"\",\""+Base64.encodeBase64URLSafeString(locator$.getBytes())+"\");");
		 	    sb.append("</script>");
		    sb.append("</body>");
		    sb.append("</html>");
		    return sb.toString();
	        
		}catch(Exception e){
			Logger.getLogger(JWebGraph.class.getName()).severe(e.toString());	
		}
		return null;
	}
	private static String getGraph( String dataSet$){
		try{
		        StringBuffer sb=new StringBuffer();	
				sb.append("<script type=\"text/javascript\">");
				//sb.append("var nl = new Array();");
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
							sb.append("network = new vis.Network(container, data, options);");
							sb.append("network.on(\"startStabilizing\", function (params) {");
							sb.append(" document.getElementById('eventSpan').innerHTML = '<h3>Starting Stabilization</h3>';");
							   // console.log("started")
							sb.append("});");
							sb.append(" network.on(\"stabilized\", function (params) {");
							
							sb.append("document.getElementById('eventSpan').innerHTML = '<h3>Stabilized! Iterations('+params.iterations+') Nodes ('+nodes.length+') Edges ('+ edges.length+') </h3>'; ");
									
									// "Nodes ('+nodes.length+') Edges ('+ edges.length+')</h3>')';");

							  //  console.log("stabilized!", params);
							sb.append(" });");
							sb.append(" network.on(\"click\", function (params) {");
							sb.append("console.log('on click Event:', params);");
							//sb.append("selectedNodeLabel=nodes.get(params.nodes[0]).label;");
							sb.append("var menu= document.getElementById(\"nodeMenu\");");
							
							sb.append(" });");
							sb.append(" network.on(\"selectNode\", function (params) {");
							sb.append("console.log('selectNode Event:', params);");
							sb.append("selectedNodeLabel=nodes.get(params.nodes[0]).label;");
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
							sb.append("network.on(\"selectEdge\", function (params) {");
							sb.append("console.log('selectEdge Event:', params);");
							sb.append("console.log(edges.get(params.edges[0]).id);");
							sb.append("bondKey=edges.get(params.edges[0]).id;");
							sb.append("edgeLabel=edges.get(params.edges[0]).label;");
							sb.append("var menu= document.getElementById(\"edgeMenu\");");
							sb.append("var cnt=params.nodes.length;");
							sb.append(" if(cnt<1)");
							sb.append("menu.style.display = 'inline-block';");
							sb.append(" else ");
							sb.append("menu.style.display = 'none';");
							sb.append("menu.style.position = \"absolute\";");
							sb.append("menu.style.left = params.pointer.DOM.x+'px';");
							sb.append("menu.style.top = params.pointer.DOM.y+'px';");
							sb.append("});");
							sb.append("network.on(\"deselectEdge\", function (params) {");
							sb.append("console.log('deselectEdge Event:', params);");
							sb.append("var menu= document.getElementById(\"edgeMenu\");");
							sb.append("menu.style.display = 'none';"); 
							sb.append("});");
							sb.append("</script>");
							return sb.toString();
		
		}catch(Exception e){
			Logger.getLogger(JGraphFacetOpenItem.class.getName()).severe(e.toString());	
		}
		return null;
		
	}
	private static String getEdge(Entigrator entigrator, String edgeLabel$,String shownNodeslabels$){
		try{
			 
			String[] scope=new String[0];
			 
			 String[]sna=Locator.toArray(shownNodeslabels$);
			 scope=new String[sna.length];
			 for(int i=0;i<sna.length;i++)
				 scope[i]=entigrator.indx_keyAtLabel(sna[i]);
			 
			if(debug)
					 System.out.println("JWebGraph:getEdge:scope"+scope.length+" edge label="+edgeLabel$);
			 return getDatasets(entigrator,scope,edgeLabel$);

		}catch(Exception e){
			Logger.getLogger(JWebGraph.class.getName()).severe(e.toString());	
		}
		return null;
		}
	private static String getDatasets(Entigrator entigrator,String[] scope){
		try{
			 Bond[] ba=NodeHandler.getScopeBonds(entigrator, scope);
			 if(debug)
				 System.out.println("JWebGraph:getDatasets:bonds="+ba.length);
			 StringBuffer sb=new StringBuffer();
			 String outIcon$;
			 String inIcon$;
			 String outNodeLabel$;
			 String inNodeLabel$;
			 String edgeLabel$;
			 
			 ArrayList<String>nl=new ArrayList<String>();
			 ArrayList<String>el=new ArrayList<String>();
			 ArrayList<String>dl=new ArrayList<String>();
			 //ArrayList<String>bl=new ArrayList<String>();
			 for(Bond b:ba){
				 	outIcon$=getNodeIcon(entigrator,b.outNodeKey$);
					inIcon$=getNodeIcon(entigrator,b.inNodeKey$);
					outNodeLabel$=entigrator.indx_getLabel(b.outNodeKey$);
					if(outNodeLabel$==null)
						continue;
					inNodeLabel$=entigrator.indx_getLabel(b.inNodeKey$);
					if(inNodeLabel$==null)
						continue;
					edgeLabel$=entigrator.indx_getLabel(b.edgeKey$);
					
//					if(debug)
		//				 System.out.println("JGraphRenderer:getExpansion:out="+outNodeLabel$+ "in="+inNodelabel$);
					if(!dl.contains(outNodeLabel$)){
					nl.add("{id: '"+outNodeLabel$+"',label: '"+outNodeLabel$+"', title: '"+outNodeLabel$+"',image: \"data:image/png;base64,"+outIcon$+"\" ,shape :'image' },");
					dl.add(outNodeLabel$);
					}
					if(!dl.contains(inNodeLabel$)){
						nl.add("{id: '"+inNodeLabel$+"',label: '"+inNodeLabel$+"', title: '"+inNodeLabel$+"',image: \"data:image/png;base64,"+inIcon$+"\" ,shape :'image' },");
						dl.add(inNodeLabel$);
						}
					 el.add("{id: '"+b.bondKey$+"',from: '"+outNodeLabel$+"', to: '"+inNodeLabel$+"',label: '"+edgeLabel$+"', font: {align: 'top'}},");
			 }
			 //
			 
			 sb.append(" var selector = document.getElementById(\"nselector\");");
			 sb.append(" if (selector!=null){");
			 sb.append(" if (selector.options != null) 	selector.options.length = 0;");	
			 sb.append(" var option;");
			 Collections.sort(dl);
			 for(String l:dl){
			 sb.append(" option = document.createElement(\"option\");");
			 sb.append(" option.text = \""+l+"\";");
			 sb.append(" selector.add(option);");
			 }
			 sb.append(" };");
			 //
			 sb.append(" var nodes = new vis.DataSet([");
			 for(String s:nl)
				 sb.append(s);
			 sb.setLength(sb.length() - 1);
			 sb.append("]);");
			 String labels$=Locator.toString(dl.toArray(new String[0]));
			 sb.append(" shownNodesLabels=\""+labels$+"\";");
	   		sb.append("console.log('labels='+shownNodesLabels);");
			 sb.append(" var edges = new vis.DataSet([");
			 for(String s:el){
				 sb.append(s);
			 }
			 sb.setLength(sb.length() - 1);
			 sb.append("]);");
			return sb.toString();
		}catch(Exception e){
			Logger.getLogger(JWebGraph.class.getName()).severe(e.toString());	
		}
		return null;
		}
	private static String getDatasets(Entigrator entigrator,String[] scope,String edgeLabel$){
		try{
	       if(debug)
	    	   System.out.println("JWebGraph:getDatasets:scope:"+scope.length+" edge label="+edgeLabel$);
			String[] fn=scope;
					//EdgeHandler.filterNodesAtEdge(entigrator, scope, edgeLabel$);  
			Bond[] ba=NodeHandler.getScopeBonds(entigrator, scope); 
			 StringBuffer sb=new StringBuffer();
			 String outIcon$;
			 String inIcon$;
			 String outNodeLabel$;
			 String inNodeLabel$;
			 String edgeKey$=entigrator.indx_keyAtLabel(edgeLabel$);
			 
			 ArrayList<String>nl=new ArrayList<String>();
			 ArrayList<String>el=new ArrayList<String>();
			 ArrayList<String>dl=new ArrayList<String>();
			 //ArrayList<String>bl=new ArrayList<String>();
			 for(Bond b:ba){
				    if(!edgeKey$.equals(b.edgeKey$))
				    	continue;
				 	outIcon$=JGraphRenderer.getNodeIcon(entigrator,b.outNodeKey$);
					inIcon$=JGraphRenderer.getNodeIcon(entigrator,b.inNodeKey$);
					outNodeLabel$=entigrator.indx_getLabel(b.outNodeKey$);
					if(outNodeLabel$==null)
						continue;
					inNodeLabel$=entigrator.indx_getLabel(b.inNodeKey$);
					if(inNodeLabel$==null)
						continue;
					edgeLabel$=entigrator.indx_getLabel(b.edgeKey$);
					
//					if(debug)
		//				 System.out.println("JGraphRenderer:getExpansion:out="+outNodeLabel$+ "in="+inNodelabel$);
					if(!dl.contains(outNodeLabel$)){
					nl.add("{id: '"+outNodeLabel$+"',label: '"+outNodeLabel$+"', title: '"+outNodeLabel$+"',image: \"data:image/png;base64,"+outIcon$+"\" ,shape :'image' },");
					dl.add(outNodeLabel$);
					}
					if(!dl.contains(inNodeLabel$)){
						nl.add("{id: '"+inNodeLabel$+"',label: '"+inNodeLabel$+"', title: '"+inNodeLabel$+"',image: \"data:image/png;base64,"+inIcon$+"\" ,shape :'image' },");
						dl.add(inNodeLabel$);
						}
					 el.add("{id: '"+b.bondKey$+"',from: '"+outNodeLabel$+"', to: '"+inNodeLabel$+"',label: '"+edgeLabel$+"', font: {align: 'top'}},");
			 }
			 //
			 
			 sb.append(" var selector = document.getElementById(\"nselector\");");
			 sb.append(" if (selector!=null){");
			 sb.append(" if (selector.options != null) 	selector.options.length = 0;");	
			 sb.append(" var option;");
			 Collections.sort(dl);
			 for(String l:dl){
			 sb.append(" option = document.createElement(\"option\");");
			 sb.append(" option.text = \""+l+"\";");
			 sb.append(" selector.add(option);");
			 }
			 sb.append(" };");
			 //
			 sb.append(" var nodes = new vis.DataSet([");
			 for(String s:nl)
				 sb.append(s);
			 sb.setLength(sb.length() - 1);
			 sb.append("]);");
			 String labels$=Locator.toString(dl.toArray(new String[0]));
			 sb.append(" shownNodesLabels=\""+labels$+"\";");
	   		sb.append("console.log('labels='+shownNodesLabels);");
			 sb.append(" var edges = new vis.DataSet([");
			 for(String s:el)
				 sb.append(s);
			 sb.setLength(sb.length() - 1);
			 sb.append("]);");
			return sb.toString();
		}catch(Exception e){
			Logger.getLogger(JWebGraph.class.getName()).severe(e.toString());	
		}
		return null;
		}
	private static String getExpansion(Entigrator entigrator, String nodeLabel$,String shownNodeslabels$){
		try{
			
			 String nodeKey$=entigrator.indx_keyAtLabel(nodeLabel$);
			 String[] scope=new String[0];
			 if(shownNodeslabels$!=null){
			 String[]sna=Locator.toArray(shownNodeslabels$);
			 scope=new String[sna.length];
			 for(int i=0;i<sna.length;i++)
				 scope[i]=entigrator.indx_keyAtLabel(sna[i]);
			 }
				if(debug)
					 System.out.println("JWebGraph:getExpansion:scope="+scope.length);

			 String[] eka=NodeHandler.getExpandedNodeKeys(entigrator, nodeKey$, scope);
			 return getDatasets(entigrator,eka);

		}catch(Exception e){
			Logger.getLogger(JWebGraph.class.getName()).severe(e.toString());	
		}
		return null;
		}
	private static String getNetwork(Entigrator entigrator, String nodeLabel$,String shownNodeslabels$){
		try{
			String nodeKey$=null;
			if(nodeLabel$!=null)
			  nodeKey$=entigrator.indx_keyAtLabel(nodeLabel$);
			 String[] scope=new String[0];
			 if(shownNodeslabels$!=null){
			 String[]sna=Locator.toArray(shownNodeslabels$);
			 scope=new String[sna.length];
			 for(int i=0;i<sna.length;i++)
				 scope[i]=entigrator.indx_keyAtLabel(sna[i]);
			 }
				if(debug)
					 System.out.println("JWebGraph:getNetwork:scope="+scope.length);

			 String[] eka=NodeHandler.getNetwordNodeKeys(entigrator, nodeKey$, scope);
			 return getDatasets(entigrator,eka);

		}catch(Exception e){
			Logger.getLogger(JWebGraph.class.getName()).severe(e.toString());	
		}
		return null;
		}
	/*
	private static String getScopeExpansion(Entigrator entigrator, String nodeLabel$,String shownNodeslabels$){
		try{
			
			 String nodeKey$=entigrator.indx_keyAtLabel(nodeLabel$);
			 String[] scope=new String[0];
			 if(shownNodeslabels$!=null){
			 String[]sna=Locator.toArray(shownNodeslabels$);
			 scope=new String[sna.length];
			 for(int i=0;i<sna.length;i++)
				 scope[i]=entigrator.indx_keyAtLabel(sna[i]);
			 }
				if(debug)
					 System.out.println("JGraphRenderer:getScopeExpansion:scope"+scope.length);

			 String[] eka=NodeHandler.getScopeExpandedNodeKeys(entigrator, nodeKey$, scope);
			 return getDatasets(entigrator,eka);

		}catch(Exception e){
			Logger.getLogger(JGraphRenderer.class.getName()).severe(e.toString());	
		}
		return null;
		}
	*/
	private static String getNetworkRelations(Entigrator entigrator, String nodeLabel$,String shownLabels$){
		try{
			ArrayList<String>nkl=new ArrayList<String>();
			String nk$=null;
			if(nodeLabel$!=null){
			   nk$=entigrator.indx_keyAtLabel(nodeLabel$);
			if(nk$!=null)
				nkl.add(nk$);
			}
			if(shownLabels$!=null){
			String[] sna=Locator.toArray(shownLabels$);	
			for(String s:sna){
				nk$=entigrator.indx_keyAtLabel(s);
				if(nk$!=null&&!nkl.contains(nk$))
					nkl.add(nk$);
			}
			}
			if(nkl.size()<1)
				return null;
			String[] nka=NodeHandler.getNetwordNodeKeys(entigrator, nkl.toArray(new String[0]));
			 return getDatasets(entigrator,nka);	
			// return getDatasets(entigrator,nkl.toArray(new String[0]));	
		}catch(Exception e){
			Logger.getLogger(JWebGraph.class.getName()).severe(e.toString());	
		}
		return null;
		}
	private static String getRelations(Entigrator entigrator, String nodeLabel$){
		try{
			 
			 String nodeKey$=entigrator.indx_keyAtLabel(nodeLabel$);
			 String[] eka=NodeHandler.getRelatedNodeKeys(entigrator, nodeKey$);
			 return getDatasets(entigrator,eka);			
		}catch(Exception e){
			Logger.getLogger(JWebGraph.class.getName()).severe(e.toString());	
		}
		return null;
		}
	private static String getScopeRelations(Entigrator entigrator, String nodeLabel$,String shownLabels$){
		try{
			ArrayList<String>nkl=new ArrayList<String>();
			String nodeKey$=entigrator.indx_keyAtLabel(nodeLabel$);
			if(nodeKey$!=null)
				nkl.add(nodeKey$);
			if(shownLabels$!=null){
			String[] sna=Locator.toArray(shownLabels$);
			String nk$;
			for(String s:sna){
				nk$=entigrator.indx_keyAtLabel(s);
				if(nk$!=null&&!nkl.contains(nk$))
					nkl.add(nk$);
			}
			}
			String[] nka=NodeHandler.getScopeExpandedNodeKeys(entigrator,nodeKey$, nkl.toArray(new String[0]));
			 return getDatasets(entigrator,nka);			
		}catch(Exception e){
			Logger.getLogger(JWebGraph.class.getName()).severe(e.toString());	
		}
		return null;
		}
	//@Override
	public String getLocator() {
		try{
			Properties locator=new Properties();
			locator.setProperty(BaseHandler.HANDLER_CLASS,JWebGraph.class.getName());
			locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
		//	locator.setProperty( JContext.CONTEXT_TYPE,getType());
		//	locator.setProperty(Locator.LOCATOR_TITLE,getTitle());
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
	/*
	@Override
	public void activate() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public JMenu getContextMenu() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public JPanel getPanel() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getSubtitle() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getTitle() {
		
		return "Web graph";
	}
	@Override
	public String getType() {

		return "web graph";
	}
	*/
/*
	@Override
	public JContext instantiate(JMainConsole console, String locator$) {

		Properties locator=Locator.toProperties(locator$);
		entihome$=locator.getProperty(Entigrator.ENTIHOME);
		entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		String action$=locator.getProperty(JRequester.REQUESTER_ACTION);
		Entigrator entigrator=console.getEntigrator(entihome$);
        Sack graphEntity=entigrator.getEntityAtKey(entityKey$);
        entityLabel$=graphEntity.getProperty("label");
		return this;
	}
*/
private static String getNodeIcon(Entigrator entigrator,String nodeKey$){
try{
	//Core [] ca=entigrator.indx_getMarks(new String[]{nodeKey$});
	String header$=entigrator.getEntihome()+"/"+StoreAdapter.HEADERS+"/"+nodeKey$;
    if(!new File(header$).exists())
    	return null;
	Sack header=Sack.parseXML(entigrator,header$);
    String nodeLabel$=header.getElementItem("key", nodeKey$).type;
   // if(debug)
   // System.out.println("JGraphRenderer:getNodeIcon:header="+header$+" key="+nodeKey$+" label="+nodeLabel$);
    String iconFile$=header.getElementItem("label", nodeLabel$).type;
	return entigrator.readIconFromIcons(iconFile$);
}catch(Exception e){
	Logger.getLogger(JWebGraph.class.getName()).info(e.toString());	
}
return null;
}
}