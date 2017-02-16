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
import java.util.Properties;
import java.util.logging.Logger;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.codec.binary.Base64;

import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.FacetHandler;
import gdt.data.entity.facet.IndexHandler;
import gdt.data.grain.Locator;
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
import gdt.jgui.entity.JEntitiesPanel;
/**
 * This class represents the index facet item in the list
 * of  entity's facets.
 * @author imasa
 *
 */
public class JIndexFacetOpenItem extends JFacetOpenItem implements JRequester,WContext{

	private static final long serialVersionUID = 1L;
	boolean debug=false;
	/**
	 * The default constructor.
	 */
	public JIndexFacetOpenItem(){
			super();
		}
	/**
	 * Get the open facet item locator.
	 * @return the locator string.
	 */
	@Override
	public String getLocator(){
		Properties locator=new Properties();
		locator.setProperty(Locator.LOCATOR_TITLE,"Index");
		locator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
		locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
		locator.setProperty(BaseHandler.HANDLER_METHOD,METHOD_OPEN_FACET);
		locator.setProperty( JContext.CONTEXT_TYPE,"Index facet");
		locator.setProperty(FACET_HANDLER_CLASS,IndexHandler.class.getName());
		if(entityKey$!=null)
			locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
		if(entihome$!=null)
			locator.setProperty(Entigrator.ENTIHOME,entihome$);
			locator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
		locator.setProperty(Locator.LOCATOR_ICON_CLASS,getClass().getName());
		locator.setProperty(Locator.LOCATOR_ICON_FILE,"index.png");
		    if(entihome$!=null)   
	 	locator.setProperty(Locator.LOCATOR_CHECKABLE,Locator.LOCATOR_TRUE);
		return Locator.toString(locator);
	}
	/**
	 * Execute the response locator. No action.
	 * @param console the main console.
	 * @param locator$ the response locator.
	 */
	@Override
	public void response(JMainConsole console, String locator$) {
		// TODO Auto-generated method stub
		
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
		return "Index";
	}
	/**
	 * Get the facet icon as a Base64 string.
	 * @return the facet icon string.
	 */
	@Override
	public String getFacetIcon(Entigrator entigrator) {
		return Support.readHandlerIcon(null,JEntitiesPanel.class, "index.png");
	}
/**
 * No action. 
 * @return null.
 */
	@Override
	public String getFacetRenderer() {
		return  JIndexPanel.class.getName();
	}
	/**
	 * No action. 
	 */
	@Override
	public void removeFacet() {
	}
	/**
	 * Display the index panel.
	 * @param console the main console
	 * @param locator$ the locator string. 
	 */
	@Override
	public void openFacet(JMainConsole console, String locator$) {
		try{
		//	System.out.println("JIndexFacetOpenItem:openFacet:locator="+locator$);
			Properties locator=Locator.toProperties(locator$);
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			
			JIndexPanel indexpanel=new JIndexPanel();
			String ipLocator$=indexpanel.getLocator();
			ipLocator$=Locator.append(ipLocator$, Entigrator.ENTIHOME, entihome$);
			ipLocator$=Locator.append(ipLocator$, EntityHandler.ENTITY_KEY, entityKey$);
			ipLocator$=Locator.append(ipLocator$, BaseHandler.HANDLER_METHOD,"instantiate");
			JConsoleHandler.execute(console, ipLocator$);
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		}
	}
	/**
	 * No action. 
	 * @return null.
	 */
	@Override
	public DefaultMutableTreeNode[] getDigest(Entigrator entigrator,String locator$) {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * Get the facet handler instance.
	 * @return the facet handler instance.	
	 */
	@Override
	public FacetHandler getFacetHandler() {
		return new IndexHandler();
	}
	/**
	 * No action.
	 * @return null.
	 * 
	 */
	@Override
	public JPopupMenu getPopupMenu(String digestLocator$) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getFacetIconName() {
		return "index.png";
	}
	@Override
	public String getWebView(Entigrator entigrator, String locator$) {
		try{
			Properties locator=Locator.toProperties(locator$);
			String webHome$=locator.getProperty(WContext.WEB_HOME);
			String entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
			String webRequester$=locator.getProperty(WContext.WEB_REQUESTER);
			if(debug)
			System.out.println("JIndexFacetOpenItem:locator="+locator$);
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
		    sb.append("<tr><td>Facet: </td><td><strong>Index</strong></td></tr>");
		    sb.append("</table>");
		    sb.append("<div id=\"jstree\">");
		    sb.append("<ul>");
		    sb.append(JIndexPanel.getWebItems(entigrator, locator$));
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

}
