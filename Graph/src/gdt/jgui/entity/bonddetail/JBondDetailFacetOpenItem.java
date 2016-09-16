package gdt.jgui.entity.bonddetail;
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
import gdt.data.entity.BaseHandler;
import gdt.data.entity.BondDetailHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.FacetHandler;
import gdt.data.entity.facet.ExtensionHandler;
import gdt.data.entity.EdgeHandler;
import gdt.data.grain.Locator;

import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JFacetOpenItem;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;

import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.entity.edge.JBondsPanel;
import gdt.jgui.entity.fields.JFieldsFacetOpenItem;
/**
 * This class represents the bond detail facet item in the list
 * of  entity's facets.
 * @author imasa
 *
 */

public class JBondDetailFacetOpenItem extends JFieldsFacetOpenItem {
	private static final long serialVersionUID = 1L;
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
public String getFacetIcon() {
	if(console!=null){
	Entigrator entigrator=console.getEntigrator(entihome$);
	return ExtensionHandler.loadIcon(entigrator,EdgeHandler.EXTENSION_KEY,"bond.png");
	}
	return null;
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
}
