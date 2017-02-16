package gdt.jgui.entity.query;
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
import gdt.data.entity.facet.QueryHandler;
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
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.entity.JEntityPrimaryMenu;
import gdt.jgui.entity.fields.JFieldsFacetOpenItem;

/**
 * This class represents the query facet item in the list
 * of  entity's facets.
 * @author imasa
 *
 */
public class JQueryFacetOpenItem extends JFacetOpenItem implements JRequester,WContext{
	private static final long serialVersionUID = 1L;
	private static final String ACTION_DISPLAY_FACETS="action display facets";
	private Logger LOGGER=Logger.getLogger(JQueryFacetOpenItem.class.getName());

    boolean debug=false;
	/**
     * The default constructor.
     */
	public JQueryFacetOpenItem(){
		super();
	}
	/**
	 * Get the context locator.
	 * @return the context locator.
	 */   
 @Override
public String getLocator(){
	Properties locator=new Properties();
	locator.setProperty(Locator.LOCATOR_TITLE,"Query");
	locator.setProperty(BaseHandler.HANDLER_CLASS,JQueryFacetOpenItem.class.getName());
	locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
	locator.setProperty(BaseHandler.HANDLER_METHOD,METHOD_OPEN_FACET);
	locator.setProperty( JContext.CONTEXT_TYPE,"Query facet");
	locator.setProperty(Locator.LOCATOR_TITLE,"Query");
	locator.setProperty(FACET_HANDLER_CLASS,QueryHandler.class.getName());
	if(entityKey$!=null)
		locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
	if(entihome$!=null)
		locator.setProperty(Entigrator.ENTIHOME,entihome$);
	locator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
	locator.setProperty(Locator.LOCATOR_ICON_CLASS,getClass().getName());
	locator.setProperty(Locator.LOCATOR_ICON_FILE,"query.png");
    if(entihome$!=null){   
 	locator.setProperty(Locator.LOCATOR_CHECKABLE,Locator.LOCATOR_TRUE);
	    }
	return Locator.toString(locator);
}
 /**
  * Execute the response locator.
  * @param console the main console.
  * @param locator$ the response locator.
  */
@Override
public void response(JMainConsole console, String locator$) {
//	System.out.println("JQueryFacetOpenItem:response:FACET locator:"+locator$);
	try{
		Properties locator=Locator.toProperties(locator$);
		String requesterResponseLocator$=locator.getProperty(JRequester.REQUESTER_RESPONSE_LOCATOR);
		byte[] 	ba=Base64.decodeBase64(requesterResponseLocator$);
		String responseLocator$=new String(ba,"UTF-8");
//		System.out.println("JFieldsFacetItem:response:response locator="+responseLocator$);
		locator=Locator.toProperties(responseLocator$);
		entihome$=locator.getProperty(Entigrator.ENTIHOME);
		entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		
			entihome$=locator.getProperty(Entigrator.ENTIHOME);
			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		JEntityFacetPanel efp=new JEntityFacetPanel();
		String efpLocator$=efp.getLocator();
		 efpLocator$=Locator.append(efpLocator$, Entigrator.ENTIHOME, entihome$);
		 efpLocator$=Locator.append(efpLocator$, EntityHandler.ENTITY_KEY, entityKey$);
		 JConsoleHandler.execute(console, efpLocator$);
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
}
/**
 * Return false. The facet cannot be removed.
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
	return "Query";
}
/**
 * Get the facet icon as a Base64 string.
 * @return the facet icon string.
 */
@Override
public String getFacetIcon(Entigrator entigrator) {
	return Support.readHandlerIcon(null,JEntityPrimaryMenu.class, "query.png");
}

@Override
public void openFacet(JMainConsole console,String locator$) {
	try{
//		System.out.println("JFieldsFacetOpenItem:openFacet:locator="+locator$);
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

		JQueryPanel queryPanel=new JQueryPanel();
		String qpLocator$=queryPanel.getLocator();
		qpLocator$=Locator.append(qpLocator$, Entigrator.ENTIHOME, entihome$);
		qpLocator$=Locator.append(qpLocator$, EntityHandler.ENTITY_KEY, entityKey$);
		qpLocator$=Locator.append(qpLocator$, JRequester.REQUESTER_RESPONSE_LOCATOR, requesterResponseLocator$);
		qpLocator$=Locator.append(qpLocator$, BaseHandler.HANDLER_METHOD,"instantiate");
		JConsoleHandler.execute(console, qpLocator$);
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
}
/**
 * Get the facet renderer class name.
 * @return the facet renderer class name.
 */

@Override
public String getFacetRenderer() {
	return JQueryPanel.class.getName();
}
/**
 * No action.
 */

@Override
public DefaultMutableTreeNode[] getDigest(Entigrator entigrator,String locator$) {
	JFieldsFacetOpenItem foi=new JFieldsFacetOpenItem();
	return foi.getDigest(entigrator,locator$);
}

/**
 * Get the facet handler instance.
 * @return the facet handler instance.
 */

@Override
public FacetHandler getFacetHandler() {
	return new QueryHandler();
}
/**
 * No action.
 */
@Override
public JPopupMenu getPopupMenu(final String digestLocator$) {

	return null;
}
/**
 * No action.
 */
@Override
public void removeFacet() {
	
}
@Override
public String getFacetIconName() {
	return  "query.png";
}
@Override
public String getWebView(Entigrator entigrator, String locator$) {
	try{
		Properties locator=Locator.toProperties(locator$);
		String webHome$=locator.getProperty(WContext.WEB_HOME);
		String entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
		String webRequester$=locator.getProperty(WContext.WEB_REQUESTER);
		if(debug)
		System.out.println("JQueryFacetOpenItem:locator="+locator$);
		entityKey$=entigrator.indx_keyAtLabel(entityLabel$);
		StringBuffer sb=new StringBuffer();
		sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
		sb.append("<html>");
		sb.append("<head>");
		sb.append(WUtils.getMenuBarScript());
		sb.append(WUtils.getMenuBarStyle());
		sb.append("</head>");
	    sb.append("<body onload=\"onLoad()\" >");
	    sb.append("<ul class=\"menu_list\">");
	    sb.append("<li class=\"menu_item\"><a id=\"back\">Back</a></li>");
	    sb.append("<li class=\"menu_item\"><a href=\""+webHome$+"\">Home</a></li>");
	    String navLocator$=Locator.append(locator$, BaseHandler.HANDLER_CLASS, JBaseNavigator.class.getName());
	    navLocator$=Locator.append(navLocator$, Entigrator.ENTIHOME, entigrator.getEntihome());
	    String navUrl$=webHome$+"?"+WContext.WEB_LOCATOR+"="+Base64.encodeBase64URLSafeString(navLocator$.getBytes());
	    sb.append("<li class=\"menu_item\"><a href=\""+navUrl$+"\">Base</a></li>");
	    sb.append("<li class=\"menu_item\"><a href=\""+WContext.ABOUT+"\">About</a></li>");
	    sb.append("</ul>");
	    sb.append("<table><tr><td>Base:</td><td><strong>");
	    sb.append(entigrator.getBaseName());
	    sb.append("</strong></td></tr><tr><td>Entity: </td><td><strong>");
	    sb.append(entityLabel$);
	    sb.append("</strong></td></tr>");
	    sb.append("<tr><td>Facet: </td><td><strong>Query</strong></td></tr>");
	    sb.append("</table>");
	    sb.append(JQueryPanel.getViewItems(entigrator, locator$));
	    sb.append("<script>");
	    sb.append("function onLoad() {");
	    sb.append(" var parameter=window.localStorage.getItem(\"query_requester\");");
	    sb.append("console.log('parameter='+parameter);");
	    sb.append("if(parameter==null) "); 
	    sb.append("initBack(\""+this.getClass().getName()+"\",\""+webRequester$+"\");");
	    sb.append("else {");
	    sb.append("initBack(\""+this.getClass().getName()+"\",parameter);");
	   
	    sb.append("}");
	    sb.append("}");
	    sb.append("function labelClick(label) {");
	    sb.append("console.log(label);");
	    Properties foiLocator=new Properties(); 
	    foiLocator.setProperty(BaseHandler.HANDLER_CLASS,JEntityFacetPanel.class.getName());
    	foiLocator.setProperty(Entigrator.ENTIHOME,entigrator.getEntihome());
    	foiLocator.setProperty(WContext.WEB_HOME, webHome$);
    	foiLocator.setProperty(WContext.WEB_REQUESTER, this.getClass().getName());
     	sb.append(" var locator=\""+Locator.toString(foiLocator)+"\";");
    	sb.append(" locator=appendProperty(locator,\""+EntityHandler.ENTITY_LABEL+"\",label);");
    	sb.append("console.log(locator);");
    	sb.append(" var href=\""+webHome$+"?"+WContext.WEB_LOCATOR+"=\"+window.btoa(locator);");
    	sb.append("console.log(href);");
    	sb.append("window.localStorage.setItem(\"back."+JEntityFacetPanel.class.getName()+"\",\""+this.getClass().getName()+"\");");
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
@Override
public String getWebConsole(Entigrator entigrator, String locator$) {
	// TODO Auto-generated method stub
	return null;
}
}
