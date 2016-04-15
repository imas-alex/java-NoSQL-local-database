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
import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.FacetHandler;
import gdt.data.entity.facet.IndexHandler;
import gdt.data.grain.Locator;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JFacetOpenItem;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.entity.JEntitiesPanel;
/**
 * This class represents the index facet item in the list
 * of  entity's facets.
 * @author imasa
 *
 */
public class JIndexFacetOpenItem extends JFacetOpenItem implements JRequester{

	private static final long serialVersionUID = 1L;
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
		 String icon$=Support.readHandlerIcon(JEntitiesPanel.class, "index.png");
		    if(icon$!=null)
		    	locator.setProperty(Locator.LOCATOR_ICON,icon$);
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
	public String getFacetIcon() {
		return Support.readHandlerIcon(JEntitiesPanel.class, "index.png");
	}
/**
 * No action. 
 * @return null.
 */
	@Override
	public String getFacetRenderer() {
		// TODO Auto-generated method stub
		return null;
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
	public DefaultMutableTreeNode[] getDigest() {
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

}
