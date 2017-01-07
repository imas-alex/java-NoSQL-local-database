package gdt.jgui.entity.bonddetail;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
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
import java.util.Properties;
import java.util.logging.Logger;
import javax.swing.JPopupMenu;

import org.apache.commons.codec.binary.Base64;

import gdt.data.entity.BaseHandler;
import gdt.data.entity.BondDetailHandler;
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
import gdt.jgui.base.JCategoryPanel;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JFacetOpenItem;
import gdt.jgui.console.JFacetRenderer;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.console.WContext;
import gdt.jgui.console.WUtils;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.entity.edge.JBondsPanel;
import gdt.jgui.entity.fields.JFieldsFacetOpenItem;
import gdt.jgui.entity.node.JNodeFacetOpenItem;
/**
 * This class represents the bond detail facet item in the list
 * of  entity's facets.
 * @author imasa
 *
 */

public class JBondDetailFacetOpenItem extends JFieldsFacetOpenItem  implements WContext {
	private static final long serialVersionUID = 1L;
	public static boolean debug=true;
	/**
     * The default constructor.
     * 
     */
	public JBondDetailFacetOpenItem(){
		super();
	}
	/**
	 * Get the context locator.
	 * @return the context locator.
	 */	
@Override
public String getLocator(){
	Properties locator=new Properties();
	locator.setProperty(Locator.LOCATOR_TITLE,"Bonds");
	locator.setProperty(BaseHandler.HANDLER_CLASS,JBondDetailFacetOpenItem.class.getName());
	locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
	locator.setProperty(BaseHandler.HANDLER_METHOD,METHOD_OPEN_FACET);
	locator.setProperty(BaseHandler.HANDLER_LOCATION,EdgeHandler.EXTENSION_KEY);
	locator.setProperty( JContext.CONTEXT_TYPE,"Bonds facet");
	locator.setProperty(Locator.LOCATOR_TITLE,"Bonds");
	locator.setProperty(FACET_HANDLER_CLASS,BondDetailHandler.class.getName());
	if(entityKey$!=null)
		locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
	if(entihome$!=null){
		locator.setProperty(Entigrator.ENTIHOME,entihome$);
		Entigrator entigrator=console.getEntigrator(entihome$);
	 //String icon$=Support.readHandlerIcon(JBondsPanel.class, "edge.png");
		String icon$=ExtensionHandler.loadIcon(entigrator, EdgeHandler.EXTENSION_KEY, "bond.png");
    if(icon$!=null)
    	locator.setProperty(Locator.LOCATOR_ICON,icon$);
    locator.setProperty(Locator.LOCATOR_CHECKABLE,Locator.LOCATOR_TRUE);
	}
    
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
	return "Bonds";
}
/**
 * Get the facet icon as a Base64 string.
 * @return the facet icon string.
 */
@Override
public String getFacetIcon(Entigrator entigrator) {
	
	return ExtensionHandler.loadIcon(entigrator,EdgeHandler.EXTENSION_KEY,"bond.png");
	
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
	//	System.out.println("JBondDetailFacetOpenItem:openFacet:locator="+locator$);
		Properties locator=Locator.toProperties(locator$);
		String entihome$=locator.getProperty(Entigrator.ENTIHOME);
		String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		JBondsPanel bondsPanel=new JBondsPanel();
		String bpLocator$=bondsPanel.getLocator();
		bpLocator$=Locator.append(bpLocator$, Entigrator.ENTIHOME, entihome$);
		bpLocator$=Locator.append(bpLocator$, EntityHandler.ENTITY_KEY, entityKey$);
		bpLocator$=Locator.append(bpLocator$, JFacetOpenItem.FACET_HANDLER_CLASS, BondDetailHandler.class.getName());
		//bpLocator$=Locator.append(bpLocator$, BaseHandler.HANDLER_METHOD,"instantiate");
		JConsoleHandler.execute(console, bpLocator$);
	}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
	}
}
/**
 * Get the class name of the facet renderer. 
 * @return the JBondsPanel class name .
 */
@Override
public String getFacetRenderer() {
	return JBondsPanel.class.getName();
}
/**
 * Get the facet handler instance.
 * @return the facet handler instance.	
 */
@Override
public FacetHandler getFacetHandler() {
	return new EdgeHandler();
}
/**
 * Get the popup menu for the child node of the facet node 
 * in the digest view.
 * @return the popup menu.	
 */
@Override
public JPopupMenu getPopupMenu(final String digestLocator$) {
	//System.out.println("JFieldsFacetOpenItem:edit:digest locator="+Locator.remove(digestLocator$, Locator.LOCATOR_ICON));
	JPopupMenu menu= super.getPopupMenu(digestLocator$);
	return menu;

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
public String getFacetIconName() {
	// TODO Auto-generated method stub
	return "detail.png";
}
@Override
public String getWebView(Entigrator entigrator, String locator$) {
	try{
		if(debug)
			System.out.println("JBondDetaiFacetOpenitem:getWebView:locator="+locator$);
		
		Properties locator=Locator.toProperties(locator$);
		String webHome$=locator.getProperty(WContext.WEB_HOME);
		String entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
		String webRequester$=locator.getProperty(WContext.WEB_REQUESTER);
		
		//Sack entity=entigrator.getEntityAtKey(entityKey$);
		String edgeKey$=locator.getProperty(JBondsPanel.EDGE_KEY);
		String edgeLabel$=locator.getProperty(JBondsPanel.EDGE_LABEL);
		if(edgeKey$==null&&edgeLabel$!=null)
			edgeKey$=entigrator.indx_keyAtLabel(edgeLabel$);
		if(edgeKey$==null){
			if(debug)
				System.out.println("JBondDetaiFacetOpenitem:getWebView:bonds mode");
			
			JNodeFacetOpenItem nfoi=new JNodeFacetOpenItem();
			return nfoi.getWebView(entigrator, locator$);
		}
		entityKey$=entigrator.indx_keyAtLabel(entityLabel$);
		String bondKey$=locator.getProperty(JBondsPanel.BOND_KEY);
		if(edgeLabel$==null)
		  edgeLabel$=entigrator.indx_getLabel(edgeKey$);
	    Sack edge=entigrator.getEntityAtKey(edgeKey$);
	    Core bond=edge.getElementItem("bond", bondKey$);
	    String sourceLabel$=entigrator.indx_getLabel(bond.type);
	    String targetLabel$=entigrator.indx_getLabel(bond.value);
		//    Core[]	ca=entity.elementGet("field");
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
	    sb.append("</ul>");
	    sb.append("<table><tr><td>Base:</td><td><strong>");
	    sb.append(entigrator.getBaseName());
	    sb.append("</strong></td></tr><tr><td>Source: </td><td><strong>");
	    sb.append(sourceLabel$);
	    sb.append("</strong></td></tr><tr><td>Target: </td><td><strong>");
	    sb.append(targetLabel$);
	    sb.append("</strong></td></tr>");
	    sb.append("<tr><td>Relation: </td><td><strong>"+edgeLabel$+"</strong></td></tr>");
	    sb.append("<tr><td>Context: </td><td><strong>Details</strong></td></tr></table>");
	     String [] sa=listWebItems( entigrator, webHome$,locator$, bondKey$, edgeKey$);
	     if(sa!=null)
	    	 for(String s:sa)
	    		 sb.append(s+"<br>");
        sb.append("<script>");
	    sb.append("function onLoad() {");
	    sb.append("initBack(\""+this.getClass().getName()+"\",\""+webRequester$+"\");");
	    sb.append("}");
	    sb.append("window.localStorage.setItem(\""+this.getClass().getName()+"\",\""+Base64.encodeBase64URLSafeString(locator$.getBytes())+"\");");
	 	    sb.append("</script>");
	    sb.append("</body>");
	    sb.append("</html>");
	    return sb.toString();
        
	}catch(Exception e){
		Logger.getLogger(JBondDetailFacetOpenItem.class.getName()).severe(e.toString());	
	}
	return null;
}
private static String[] listDetailes(Entigrator entigrator,String bondKey$,String edgeKey$){
	try{
		Sack edge=entigrator.getEntityAtKey(edgeKey$);
		 Core[] ca=edge.elementGet("detail");
		 if(ca==null)
			 return null;
		Core bond=edge.getElementItem("bond", bondKey$);
		ArrayList<String>sl=new ArrayList<String>();
		String detailLabel$;
		for(Core c:ca)
           try{
			if(bondKey$.equals(c.type)){
			  detailLabel$=entigrator.indx_getLabel(c.value);
			  if(detailLabel$!=null)
        	    sl.add(detailLabel$);
			}
           }catch(Exception ee){
        	   Logger.getLogger(JBondDetailFacetOpenItem.class.getName()).info(ee.toString());	
           }
		Collections.sort(sl);
		return sl.toArray(new String[0]);
	}catch(Exception e){
		Logger.getLogger(JNodeFacetOpenItem.class.getName()).info(e.toString());
	}
	return null;
}
private static String getItem(String icon$, String url$, String title$,String foiLocator$){
	if(debug)
			System.out.println("JBondDetailFacetOpenItem:getItem: locator="+foiLocator$);
    
	String iconTerm$="<img src=\"data:image/png;base64,"+WUtils.scaleIcon(icon$)+
			  "\" width=\"24\" height=\"24\" alt=\""+title$+"\">";
	foiLocator$=Locator.remove(foiLocator$, Locator.LOCATOR_ICON);
	foiLocator$=Locator.append(foiLocator$,WContext.WEB_HOME, url$);
	foiLocator$=Locator.append(foiLocator$,WContext.WEB_REQUESTER, JBondDetailFacetOpenItem.class.getName());
	  return iconTerm$+"<a href=\""+url$+"?"+WContext.WEB_LOCATOR+"="+Base64.encodeBase64URLSafeString(foiLocator$.getBytes())+"\" >"+" "+title$+"</a>";
}
private  static String[] listWebItems(Entigrator entigrator,String webHome$,String locator$,String bondKey$,String edgeKey$){
	try{
		if(debug)
		System.out.println("JBondDetailFacetOpenItem:listWebItems:locator="+locator$);
	String[] sa= listDetailes( entigrator, bondKey$, edgeKey$);   
    Properties foiLocator=new Properties();   
   	foiLocator.setProperty(BaseHandler.HANDLER_CLASS,JEntityFacetPanel.class.getName());
	foiLocator.setProperty(Entigrator.ENTIHOME,entigrator.getEntihome());
	//foiLocator.setProperty(EntityHandler.ENTITY_LABEL,foiTitle$);
	String itemIcon$;
    String itemTitle$;
    String itemKey$;
    String foiItem$;
      ArrayList<String>sl=new ArrayList<String>();
    Sack item;
	   for(String s:sa){
		   try{
			   if(debug)
				   		System.out.println("JBondDetailFacetOpenItem:listWebItems:s0"+s);
			   itemKey$=entigrator.indx_keyAtLabel(s);
			   item=entigrator.getEntityAtKey(itemKey$);
			   itemIcon$=entigrator.getEntityIcon(item);
					   //entigrator.readIconFromIcons(itemKey$);
			   itemTitle$=s;
			   foiLocator.setProperty(EntityHandler.ENTITY_KEY,itemKey$);
			   foiLocator.setProperty(EntityHandler.ENTITY_LABEL,s);
			   foiLocator.setProperty(Locator.LOCATOR_TITLE,s);
			   foiItem$=getItem(itemIcon$, webHome$,itemTitle$,Locator.toString(foiLocator));
			   sl.add(getItem(itemIcon$, webHome$, s,Locator.toString(foiLocator))); 
		   }catch(Exception ee){
			   Logger.getLogger(JBondDetailFacetOpenItem.class.getName()).info(ee.toString());
		   }
	   }
	   return sl.toArray(new String[0]);
				   
	}catch(Exception e) {
    	Logger.getLogger(JBondDetailFacetOpenItem.class.getName()).severe(e.toString());
        return null;
    }
}

}
