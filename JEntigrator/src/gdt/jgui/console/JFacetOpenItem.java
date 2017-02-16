package gdt.jgui.console;
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
import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.FacetHandler;
import gdt.data.entity.facet.ExtensionHandler;
import gdt.data.grain.Locator;
import gdt.data.store.Entigrator;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
/**
 *  This class represents a facet assigned to the entity.
 *  The click on the facet item displays the facet console.  
 *   
 */
public abstract class JFacetOpenItem extends JItemPanel {
	private static final long serialVersionUID = 1L;
	public static final String FACET_HANDLER_CLASS="facet handler class";
	public static final String METHOD_OPEN_FACET="openFacet";
	public static final String METHOD_RESPONSE="response";
	public static final String DO_NOT_OPEN="do not open";
	public static final String ACTION_DIGEST_CALL="action digest call";
	public String entihome$;
	public String entityKey$;
	static boolean debug=false;
	/**
	 * The constructor.
	 * @param console the main console.
	 * @param locator$ the facet open item locator.
	 */
	public JFacetOpenItem(JMainConsole console, String locator$){
		super(console, locator$);
		instantiate(console,locator$);
	}
	/**
	 * The default constructor.
	 */
	public JFacetOpenItem(){
		super();
	}
/**
 * Create a facet open item.
 * @param console the main console
 * @param locator$ the facet locator string.
 * @return the facet open item.
 */
public static JFacetOpenItem getFacetOpenItemInstance(JMainConsole console,String locator$){
		try{
		if(debug)
			System.out.println("JFacetOpenItem:getFacetOpenItemInstance:locator="+locator$);
			
		Properties locator=Locator.toProperties(locator$);
		String entihome$=locator.getProperty(Entigrator.ENTIHOME);
		String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		String extension$=locator.getProperty(BaseHandler.HANDLER_LOCATION);
		if(debug)
		System.out.println("JFacetOpenItem:getFacetOpenItemInstance:extension="+extension$);
		String handler$=locator.getProperty(BaseHandler.HANDLER_CLASS);
		if(debug)
		System.out.println("JFacetItem:getFacetOpenItemInstance:handler="+handler$+" extension="+extension$);
		Entigrator entigrator=console.getEntigrator(entihome$);
		JFacetOpenItem facetOpenItem;
		if(extension$==null||"null".equals(extension$))
			 facetOpenItem=(JFacetOpenItem)Class.forName(handler$).newInstance(); 
		else
			facetOpenItem= (JFacetOpenItem)ExtensionHandler.loadHandlerInstance(entigrator, extension$, handler$);
		Properties  foiLocator=Locator.toProperties(facetOpenItem.getLocator());
		foiLocator.setProperty(Entigrator.ENTIHOME, entihome$);
		foiLocator.setProperty(EntityHandler.ENTITY_KEY, entityKey$);
		if(extension$!=null){
		   foiLocator.setProperty(BaseHandler.HANDLER_LOCATION, extension$);
		  
		}
  		
		String iconFile$=facetOpenItem.getFacetIconName();
		foiLocator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
		foiLocator.setProperty(Locator.LOCATOR_ICON_CLASS,handler$);
		foiLocator.setProperty(Locator.LOCATOR_ICON_FILE,iconFile$);
		if(debug)
			System.out.println("JFacetOpenItem:getFacetOpenItemInstance:foi locator="+Locator.toString(foiLocator));
		
		facetOpenItem.instantiate(console, Locator.toString(foiLocator));
		 return facetOpenItem;
		}catch(Exception e){
			Logger.getLogger(JFacetAddItem.class.getName()).severe(e.toString());
			return null;
		}
	
	}
/**
 * Check if the facet is removable.
 * @return true if removable false otherwise.
 */
public abstract boolean isRemovable();
/**
 * Get facet name.
 * @return the facet name.
 */
public abstract String getFacetName();
/**
 * Get facet icon.
 * @param entigrator the entigrator.
 * @return the icon bitmap encoded as Base64 string.
 */
public abstract String getFacetIcon(Entigrator entigrator);
/**
 * Get facet icon name.

 * @return the icon file name.
 */
public abstract String getFacetIconName();
/**
 * Get facet renderer class name.
 * @return the class name of the facet renderer.
 */
public abstract String getFacetRenderer();
/**
 * Remove facet from the entity.
 * 
 */
public abstract void removeFacet();
/**
 * Open the facet.
 * @param console the main console.
 * @param locator$ the facet locator string.
 */
public abstract void openFacet(JMainConsole console,String locator$);
/**
 * Get a facet node for the digest view.
 * @param entigrator the entigrator.
 * @param entityKey$ the entity key.
 * @return the facet node.
 */
public abstract DefaultMutableTreeNode[] getDigest(Entigrator entigrator,String entityKey$);
/**
 * Get facet handler .
 * @return the facet handler .
 */
public abstract FacetHandler getFacetHandler();
/**
 * Get popup menu for the facet node in the digest view.
 * @param digestLocator$ the locator string.
 * @return the popup menu.
 */
public abstract JPopupMenu getPopupMenu(String digestLocator$);
}
