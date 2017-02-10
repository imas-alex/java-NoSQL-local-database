package gdt.jgui.entity.extension;


import java.util.Properties;

import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.facet.ExtensionHandler;
import gdt.data.grain.Locator;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.entity.folder.JFolderFacetOpenItem;
/**
 * This class represents the extension facet item in the list
 * of  entity's facets.
 * @author imasa
 *
 */

public class JExtensionFacetOpenItem extends JFolderFacetOpenItem{
	private static final long serialVersionUID = 1L;
	/**
	 * The default constructor.
	 */
	public JExtensionFacetOpenItem(){
		super();
	}
	/**
	 * Get the facet name.
	 * @return the facet name.
	 */
	@Override
	public String getFacetName() {
		return "Extension";
	}
	/**
	 * Get the facet icon as a Base64 string.
	 * @return the facet icon string.
	 */
	@Override
	public String getFacetIcon(Entigrator entigrator) {
		return Support.readHandlerIcon(null,getClass(), "facet.png");
	}
	@Override
	public String getFacetIconName() {
		return "file.png";
	}
	/**
	 * Get the facet renderer class name.
	 * @return the facet renderer class name.
	 */
	@Override
	public String getFacetRenderer() {
		return JExtensionRenderer.class.getName();
	}
	/**
	 * Get the context locator.
	 * @return the context locator.
	 */
	@Override
	public String getLocator(){
		Properties locator=new Properties();
		locator.setProperty(Locator.LOCATOR_TITLE,"Extension");
		locator.setProperty(BaseHandler.HANDLER_CLASS,JExtensionFacetOpenItem.class.getName());
		locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
		locator.setProperty(BaseHandler.HANDLER_METHOD,METHOD_OPEN_FACET);
		locator.setProperty( JContext.CONTEXT_TYPE,"Extension facet");
		locator.setProperty(Locator.LOCATOR_TITLE,"Extension");
		locator.setProperty(FACET_HANDLER_CLASS,ExtensionHandler.class.getName());
		if(entityKey$!=null)
			locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
		if(entihome$!=null)
			locator.setProperty(Entigrator.ENTIHOME,entihome$);
		// String icon$=Support.readHandlerIcon(null,JExtensionRenderer.class, "facet.png");
		 //   if(icon$!=null)
		  //  	locator.setProperty(Locator.LOCATOR_ICON,icon$);
	 	locator.setProperty(Locator.LOCATOR_CHECKABLE,Locator.LOCATOR_FALSE);
	 	locator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
		locator.setProperty(Locator.LOCATOR_ICON_CLASS,getClass().getName());
		locator.setProperty(Locator.LOCATOR_ICON_FILE,"extension.png");   
		return Locator.toString(locator);
	}
	/**
	 * Remove the facet from the entity. No action.
	 */
	@Override
	public void removeFacet() {
		
	}
	/**
	 * Open the entity home folder.
	 * @param console the main console.
	 * @param locator$ the locator string.
	 */
	@Override
	public void openFacet(JMainConsole console, String locator$) {
		super.openFacet(console, locator$);
	}
	
}
