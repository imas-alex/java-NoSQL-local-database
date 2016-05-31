package gdt.jgui.entity.procedure;
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
import gdt.data.entity.facet.ProcedureHandler;
import gdt.data.grain.Locator;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JFacetOpenItem;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.tool.JTextEditor;
/**
 * This class represents the procedure facet item in the list
 * of  entity's facets.
 * @author imasa
 *
 */

public class JProcedureFacetOpenItem extends JFacetOpenItem implements JRequester{
	private static final long serialVersionUID = 1L;
	public static final String ACTION_DISPLAY_FACETS="action display facets";
	private Logger LOGGER=Logger.getLogger(JProcedureFacetOpenItem.class.getName());
    public JProcedureFacetOpenItem(){
		super();
	}
    /**
	 * Get the context locator.
	 * @return the context locator.
	 */   
@Override
public String getLocator(){
	Properties locator=new Properties();
	locator.setProperty(Locator.LOCATOR_TITLE,"Procedure");
	locator.setProperty(BaseHandler.HANDLER_CLASS,JProcedureFacetOpenItem.class.getName());
	locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
	locator.setProperty(BaseHandler.HANDLER_METHOD,METHOD_OPEN_FACET);
	locator.setProperty( JContext.CONTEXT_TYPE,"Procedure facet");
	locator.setProperty(Locator.LOCATOR_TITLE,"Procedure");
	locator.setProperty(FACET_HANDLER_CLASS,ProcedureHandler.class.getName());
	if(entityKey$!=null)
		locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
	if(entihome$!=null)
		locator.setProperty(Entigrator.ENTIHOME,entihome$);
	 String icon$=Support.readHandlerIcon(null,getClass(), "procedure.png");
    if(icon$!=null)
    	locator.setProperty(Locator.LOCATOR_ICON,icon$);
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
//	System.out.println("JProcedureFacetItem:response:FACET locator:"+locator$);
	try{
		Properties locator=Locator.toProperties(locator$);
		String requesterResponseLocator$=locator.getProperty(JRequester.REQUESTER_RESPONSE_LOCATOR);
		byte[] 	ba=Base64.decodeBase64(requesterResponseLocator$);
		String responseLocator$=new String(ba,"UTF-8");
//		System.out.println("JProcedureFacetItem:response:response locator="+responseLocator$);
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
	return "Procedure";
}
/**
 * Get the facet icon as a Base64 string.
 * @return the facet icon string.
 */
@Override
public String getFacetIcon() {
	return Support.readHandlerIcon(null,getClass(), "procedure.png");
}
/**
 * No action.
 */
@Override
public void removeFacet() {
	
}
/**
 * Display the procedure context. 
 * @param console the main console
 * @param locator$ the locator string. 
 */
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

		JProcedurePanel procedurePanel=new JProcedurePanel();
		String ppLocator$=procedurePanel.getLocator();
		ppLocator$=Locator.append(ppLocator$, Entigrator.ENTIHOME, entihome$);
		ppLocator$=Locator.append(ppLocator$, EntityHandler.ENTITY_KEY, entityKey$);
		ppLocator$=Locator.append(ppLocator$, JRequester.REQUESTER_RESPONSE_LOCATOR, requesterResponseLocator$);
		ppLocator$=Locator.append(ppLocator$, BaseHandler.HANDLER_METHOD,"instantiate");
		JConsoleHandler.execute(console, ppLocator$);
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
	return JProcedurePanel.class.getName();
}
/**
 * No action
 * @return  null.
 */
@Override
public DefaultMutableTreeNode[] getDigest() {
	return null;
}
/**
 * Get the facet handler instance.
 * @return the facet handler instance.
 */
@Override
public FacetHandler getFacetHandler() {
	return new ProcedureHandler();
}
/**
 * No action.
 * @return null.
 */
@Override
public JPopupMenu getPopupMenu(final String digestLocator$) {
return null;
}
}
