package gdt.jgui.entity.edge;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
import javax.swing.JPopupMenu;

import org.apache.commons.codec.binary.Base64;

import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.FacetHandler;
import gdt.data.entity.facet.ExtensionHandler;
import gdt.data.entity.EdgeHandler;
import gdt.data.grain.Core;
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
import gdt.jgui.console.WContext;
import gdt.jgui.console.WUtils;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.entity.bonddetail.JBondDetailFacetOpenItem;
import gdt.jgui.entity.fields.JFieldsFacetOpenItem;
import gdt.jgui.entity.graph.JGraphRenderer;
import gdt.jgui.entity.node.JNodeFacetOpenItem;


public class JEdgeFacetOpenItem extends JFieldsFacetOpenItem implements WContext{
	private static final long serialVersionUID = 1L;
	private static boolean debug=true;
	public static String SORT="sort";
	public static String SORT_TARGET="sort target";
	public static String SORT_SOURCE="sort source";
	public JEdgeFacetOpenItem(){
		super();
	}
	
@Override
public String getLocator(){
	Properties locator=new Properties();
	locator.setProperty(Locator.LOCATOR_TITLE,"Edge");
	locator.setProperty(BaseHandler.HANDLER_CLASS,JEdgeFacetOpenItem.class.getName());
	locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
	locator.setProperty(BaseHandler.HANDLER_METHOD,METHOD_OPEN_FACET);
	locator.setProperty(BaseHandler.HANDLER_LOCATION,EdgeHandler.EXTENSION_KEY);
	locator.setProperty( JContext.CONTEXT_TYPE,"Edge facet");
	locator.setProperty(Locator.LOCATOR_TITLE,"Edge");
	locator.setProperty(FACET_HANDLER_CLASS,EdgeHandler.class.getName());
	if(entityKey$!=null)
		locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
	if(entihome$!=null){
		locator.setProperty(Entigrator.ENTIHOME,entihome$);
		Entigrator entigrator=console.getEntigrator(entihome$);
	 //String icon$=Support.readHandlerIcon(JBondsPanel.class, "edge.png");
		String icon$=ExtensionHandler.loadIcon(entigrator, EdgeHandler.EXTENSION_KEY, "edge.png");
    if(icon$!=null)
    	locator.setProperty(Locator.LOCATOR_ICON,icon$);
    locator.setProperty(Locator.LOCATOR_CHECKABLE,Locator.LOCATOR_TRUE);
	}
    
	return Locator.toString(locator);
}
@Override
public boolean isRemovable() {
	return false;
	}

@Override
public String getFacetName() {
	return "Edge";
}
@Override
public String getFacetIcon(Entigrator entigrator) {
	
	return ExtensionHandler.loadIcon(entigrator,EdgeHandler.EXTENSION_KEY,"edge.png"); 
	
}
@Override
public void removeFacet() {
	
}
@Override
public void openFacet(JMainConsole console,String locator$) {
	try{
		System.out.println("JBondsFacetOpenItem:openFacet:locator="+locator$);
		Properties locator=Locator.toProperties(locator$);
		String entihome$=locator.getProperty(Entigrator.ENTIHOME);
		String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		String responseLocator$=getLocator();
		System.out.println("JBondsFacetOpenItem:openFacet:1");	
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
		
		JBondsPanel bondsPanel=new JBondsPanel();
		String bpLocator$=bondsPanel.getLocator();
		bpLocator$=Locator.append(bpLocator$, Entigrator.ENTIHOME, entihome$);
		bpLocator$=Locator.append(bpLocator$, EntityHandler.ENTITY_KEY, entityKey$);
		bpLocator$=Locator.append(bpLocator$, JRequester.REQUESTER_RESPONSE_LOCATOR, requesterResponseLocator$);
		bpLocator$=Locator.append(bpLocator$, BaseHandler.HANDLER_METHOD,"instantiate");
		JConsoleHandler.execute(console, bpLocator$);
	}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
	}
}
@Override
public String getFacetRenderer() {
	return JBondsPanel.class.getName();
}
@Override
public FacetHandler getFacetHandler() {
	return new EdgeHandler();
}
@Override
public JPopupMenu getPopupMenu(final String digestLocator$) {
	//System.out.println("JFieldsFacetOpenItem:edit:digest locator="+Locator.remove(digestLocator$, Locator.LOCATOR_ICON));
	JPopupMenu menu= super.getPopupMenu(digestLocator$);
	return menu;

}
@Override
public void response(JMainConsole console, String locator$) {
//	System.out.println("JAddressFacetOpenItem:responce:locator="+locator$);
	super.response(console,locator$);

}
@Override
public String getFacetIconName() {
	
	return "edge.png";
}
@Override
public String getWebView(Entigrator entigrator, String locator$) {
	try{
		Properties locator=Locator.toProperties(locator$);
		String webHome$=locator.getProperty(WContext.WEB_HOME);
		String entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
		String webRequester$=locator.getProperty(WContext.WEB_REQUESTER);
		String sort$=locator.getProperty(SORT);
		boolean sortTarget=true;
		if(SORT_SOURCE.equals(sort$))
			sortTarget=false;
		if(debug)
		System.out.println("JFieldsFacetOpenItem:web home="+webHome$+ " web requester="+webRequester$);
		entityKey$=entigrator.indx_keyAtLabel(entityLabel$);
		Sack entity=entigrator.getEntityAtKey(entityKey$);
		String edge$=entity.getProperty("edge");
	    //    Core[]	ca=entity.elementGet("field");
		StringBuffer sb=new StringBuffer();
		sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
		sb.append("<html>");
		sb.append("<head>");
		
		sb.append(WUtils.getMenuBarScript());
		sb.append(WUtils.getMenuBarStyle());
		//sb.append(WUtils.getJquery(entigrator));
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
	    sb.append("<li id=\"source\" onclick=\"sortSource()\"><a href=\"#\">Sort source</a></li>");
	    sb.append("<li id=\"target\" onclick=\"sortTarget()\"><a href=\"#\">Sort target</a></li>");
	    sb.append("<li id=\"graph\" onclick=\"graph()\"><a href=\"#\">Graph</a></li>");
	    sb.append("</ul>");
	    sb.append("</li>");
	  
	    sb.append("</ul>");
	    sb.append("<table><tr><td>Base:</td><td><strong>");
	    sb.append(entigrator.getBaseName());
	    sb.append("</strong></td></tr><tr><td>Entity: </td><td><strong>");
	    sb.append(entityLabel$);
	    sb.append("</strong></td></tr>");
	   
	    sb.append("<tr><td>Facet: </td><td><strong>Edge</strong></td></tr></table>");
	    //sb.append("<table>");
	    sb.append("<table><tr><td><strong>Source</strong></td><td></td><td><strong>Target</strong></td></tr>");
	    sb.append(getItems( webHome$,entigrator,entity,sortTarget));
	    sb.append("</table>");
        sb.append("<script>");
	    sb.append("function onLoad() {");
	    sb.append("initBack(\""+this.getClass().getName()+"\",\""+webRequester$+"\");");
	    sb.append("}");
	    
	    sb.append("function sortSource(){");
	    sb.append(" var locator=\""+locator$+"\";");
    	sb.append(" locator=appendProperty(locator,\"sort\",\"sort source\");");
    	//sb.append("console.log(locator);");
    	sb.append(" var href=\""+webHome$+"?"+WContext.WEB_LOCATOR+"=\"+window.btoa(locator);");
    	sb.append("console.log(href);");
	   sb.append("window.location.assign(href);");
	    sb.append("}");

	    sb.append("function sortTarget(){");
	    sb.append(" var locator=\""+locator$+"\";");
    	sb.append(" locator=appendProperty(locator,\"sort\",\"sort target\");");
    	//sb.append("console.log(locator);");
    	sb.append(" var href=\""+webHome$+"?"+WContext.WEB_LOCATOR+"=\"+window.btoa(locator);");
    	sb.append("console.log(href);");
	   sb.append("window.location.assign(href);");
	   sb.append("}");
	   sb.append("function graph(){");
	   JGraphRenderer gr=new JGraphRenderer();
	   String grLocator$=gr.getLocator();
	   Properties grLocator=Locator.toProperties(grLocator$);
	   grLocator.setProperty(Entigrator.ENTIHOME, entigrator.getEntihome());
	   grLocator.setProperty(WContext.WEB_HOME,webHome$);
	   grLocator.setProperty(EntityHandler.ENTITY_LABEL,entityLabel$);
	   grLocator.setProperty(WContext.WEB_REQUESTER,getClass().getName());
	   grLocator.setProperty(JRequester.REQUESTER_ACTION,JGraphRenderer.ACTION_EDGE);
	   grLocator.setProperty(JBondsPanel.EDGE_LABEL,entityLabel$);
	   ArrayList <String>nl=new ArrayList<String>();
	   Core[] ca=entity.elementGet("bond");
	   if(ca!=null){
		   for (Core c:ca){
			   if(c.type!=null)
				   if(!nl.contains(c.type))
					   nl.add(c.type);
			   if(c.value!=null)
				   if(!nl.contains(c.value))
					   nl.add(c.value);
		   }
	   }
	   ArrayList <String>ll=new ArrayList<String>();
	   for(String n:nl)
		   ll.add(entigrator.indx_getLabel(n));
	   String shownLabels=Locator.toString(ll.toArray(new String[0]));
	   grLocator.setProperty(JGraphRenderer.SHOWN_NODES_LABELS,shownLabels);
	   grLocator$=Locator.toString(grLocator);
	   if(debug)
			System.out.println("JEdgeFacetOpenItem:getWebView:locator="+grLocator$);
	   byte[]ba=grLocator$.getBytes();
	    sb.append(" var locator=\""+Base64.encodeBase64String(ba)+"\";");
    
   	//sb.append("console.log(locator);");
   	   sb.append(" var href=\""+webHome$+"?"+WContext.WEB_LOCATOR+"=\"+locator;");
	   sb.append("window.location.assign(href);");
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
private String getItems(String webHome$,Entigrator entigrator,Sack entity,boolean sortTarget){
	try{
		if(debug)
			System.out.println("JEdgeFacetOpenItem:getItems:node="+entity.getProperty("label"));
		StringBuffer sb=new StringBuffer();	
		Core[] ca=entity.elementGet("bond");
		//String edgeKey$;
		if(ca!=null){
			ArrayList<Core> cl=new ArrayList<Core>(Arrays.asList(ca));
			if(sortTarget){
			BondComparatorByValue bc=new BondComparatorByValue();
			bc.entigrator=entigrator;
			Collections.sort(cl,bc);
			}else{
				BondComparatorByType bc=new BondComparatorByType();
				bc.entigrator=entigrator;
				Collections.sort(cl,bc);
			}
			ca=cl.toArray(new Core[0]);
			//String icon$=ExtensionHandler.loadIcon(entigrator, EdgeHandler.EXTENSION_KEY, "bond.png");
			for(Core aCa:ca){
				  try{
  				   sb.append(getItem( entigrator, entity.getKey(),webHome$, aCa));
			      }catch(Exception ee){
						   Logger.getLogger(JNodeFacetOpenItem.class.getName()).info(ee.toString());
				  }
			}
		}
	return sb.toString();	
	}catch(Exception e){
        Logger.getLogger(JBondsPanel.class.getName()).severe(e.toString());
    }
     return null;	
	}
private static String getItem(Entigrator entigrator,String entityKey$,String webHome$, Core bond){
	try{
	//String iconTerm$="<img src=\"data:image/png;base64,"+WUtils.scaleIcon(icon$)+
	//		  "\" width=\"24\" height=\"24\" alt=\"image\">";
    String outLabel$=entigrator.indx_getLabel(bond.type);
    String inLabel$=entigrator.indx_getLabel(bond.value);
    String edgeLabel$=entigrator.indx_getLabel(entityKey$);
	String outHref$= getEntityReference( entigrator, webHome$, bond.type);
	String inHref$= getEntityReference( entigrator, webHome$, bond.value);
	String edgeHref$= getEntityReference( entigrator, webHome$, entityKey$);
	StringBuffer sb=new StringBuffer();
	
	sb.append("<tr><td><a href=\""+outHref$+"\">"+outLabel$+"</a></td>");
	//sb.append(" -> ");
	//sb.append("<td><a href=\""+edgeHref$+"\">"+edgeLabel$+"</a></td>");
	sb.append("<td>");
	if(hasDetailes(entigrator,bond.name,entityKey$)){
		//sb.append("(Details)");
		String detReference$=getDetailsReference( entigrator, webHome$,entityKey$, entityKey$,bond);
		sb.append("<a href=\""+detReference$+"\">(details)</a>");
	}
	sb.append("-></td>");
	//sb.append(" -> ");
	sb.append("<td><a href=\""+inHref$+"\">"+inLabel$+"</a></td></tr>");

	return sb.toString();
	}catch(Exception e){
		Logger.getLogger(JEdgeFacetOpenItem.class.getName()).info(e.toString());
	}
	return null;	
	
}
private static String getDetailsReference(Entigrator entigrator,String webHome$,String entityKey$,String edgeKey$,Core bond){
	try{
		 Properties foiLocator=new Properties();   
		 foiLocator.setProperty(BaseHandler.HANDLER_CLASS,JBondDetailFacetOpenItem.class.getName());
	     foiLocator.setProperty(Entigrator.ENTIHOME,entigrator.getEntihome());
	     String entityLabel$=entigrator.indx_getLabel(entityKey$);
	    foiLocator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
		foiLocator.setProperty(EntityHandler.ENTITY_LABEL,entityLabel$);
		foiLocator.setProperty(JBondsPanel.EDGE_KEY,edgeKey$);
		foiLocator.setProperty(JBondsPanel.BOND_KEY,bond.name);
		foiLocator.setProperty(Locator.LOCATOR_TITLE,"Details");
		foiLocator.setProperty(WContext.WEB_HOME,webHome$);
		foiLocator.setProperty(WContext.WEB_REQUESTER,JNodeFacetOpenItem.class.getName());
		return webHome$+"?"+WContext.WEB_LOCATOR+"="+Base64.encodeBase64URLSafeString(Locator.toString(foiLocator).getBytes());	 
	}catch(Exception e){
		Logger.getLogger(JNodeFacetOpenItem.class.getName()).info(e.toString());
	}
	return null;
}
private static String getEntityReference(Entigrator entigrator,String webHome$,String entityKey$){
	try{
		 Properties foiLocator=new Properties();   
		 foiLocator.setProperty(BaseHandler.HANDLER_CLASS,JEntityFacetPanel.class.getName());
	     foiLocator.setProperty(Entigrator.ENTIHOME,entigrator.getEntihome());
	     String entityLabel$=entigrator.indx_getLabel(entityKey$);
	    foiLocator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
		foiLocator.setProperty(EntityHandler.ENTITY_LABEL,entityLabel$);
		foiLocator.setProperty(Locator.LOCATOR_TITLE,entityLabel$);
		foiLocator.setProperty(WContext.WEB_HOME,webHome$);
		foiLocator.setProperty(WContext.WEB_REQUESTER,JNodeFacetOpenItem.class.getName());
		return webHome$+"?"+WContext.WEB_LOCATOR+"="+Base64.encodeBase64URLSafeString(Locator.toString(foiLocator).getBytes());	 
	}catch(Exception e){
		Logger.getLogger(JNodeFacetOpenItem.class.getName()).info(e.toString());
	}
	return null;
}
private static boolean hasDetailes(Entigrator entigrator,String bondKey$,String edgeKey$){
	try{
		Sack edge=entigrator.getEntityAtKey(edgeKey$);
		 Core[] ca=edge.elementGet("detail");
		 if(ca==null)
			 return false;
		Core bond=edge.getElementItem("bond", bondKey$);
		for(Core c:ca)
           if(bondKey$.equals(c.type))
        	  return true;
	}catch(Exception e){
		Logger.getLogger(JNodeFacetOpenItem.class.getName()).info(e.toString());
	}
	return false;
}

private static class BondComparatorByValue implements Comparator<Core>{
    public Entigrator entigrator;
	@Override
	public int compare(Core o1, Core o2) {
		try{
    		String l1$=o1.value;
    		String l2$=o2.value;
    		String i1$=entigrator.indx_getLabel(l1$);
    		String i2$=entigrator.indx_getLabel(l2$);
    		if(i1$==null&&i2$==null)
    			return 0;
    		if(i1$==null||"null".equals(i1$)&&i2$!=null)
    			return -1;
    		if(i2$==null||"null".equals(i2$)&&i1$!=null)
    			return 1;	
    		return i1$.compareToIgnoreCase(i2$);
    	}catch(Exception e){
    		return 0;
    	}
	}
}
	private static class BondComparatorByType implements Comparator<Core>{
	    public Entigrator entigrator;
		@Override
		public int compare(Core o1, Core o2) {
			try{
	    		String l1$=o1.type;
	    		String l2$=o2.type;
	    		String i1$=entigrator.indx_getLabel(l1$);
	    		String i2$=entigrator.indx_getLabel(l2$);
	    		if(i1$==null&&i2$==null)
	    			return 0;
	    		if(i1$==null||"null".equals(i1$)&&i2$!=null)
	    			return -1;
	    		if(i2$==null||"null".equals(i2$)&&i1$!=null)
	    			return 1;	
	    		return i1$.compareToIgnoreCase(i2$);
	    	}catch(Exception e){
	    		return 0;
	    	}
		}
	
}
}

