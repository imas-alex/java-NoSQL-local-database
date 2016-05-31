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
/**
 * This renderer has the same functionality as the 
 * folder panel. 
 * @author imasa
 *
 */

public class JExtensionRenderer extends gdt.jgui.entity.folder.JFolderPanel{
	private static final long serialVersionUID = 1L;
/**
 * 	Get the context locator.
 * @return the context locator string 
 */
	@Override
	public String getLocator() {
		try{
			Properties locator=new Properties();
			locator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
			locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
			 locator.setProperty( JContext.CONTEXT_TYPE,getType());
			locator.setProperty(Locator.LOCATOR_TITLE,getTitle());
			if(entityLabel$!=null)
				locator.setProperty(EntityHandler.ENTITY_LABEL,entityLabel$);
			if(entityKey$!=null)
				locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
			if(entihome$!=null)
				locator.setProperty(Entigrator.ENTIHOME,entihome$);
			 String icon$=Support.readHandlerIcon(null,getClass(), "facet.png");
			    if(icon$!=null)
			    	locator.setProperty(Locator.LOCATOR_ICON,icon$);
			return Locator.toString(locator);
			}catch(Exception e){
	        LOGGER.severe(e.toString());
	        return null;
			}
	}
/**
 * Get the facet handler class name.
 * @return the facet handler class name. 
 */
	@Override
	public String getFacetHandler() {
		return ExtensionHandler.class.getName();
	}
	/**
	 * Get the entity type.
	 * @return the entity type. 
	 */
	@Override
	public String getEntityType() {
		return "extension";
	}
	/**
	 * Get the category icon as a Base64 string.
	 * @return the icon string.
	 */
	@Override
	public String getCategoryIcon() {
		
		return Support.readHandlerIcon(null,getClass(), "facet.png");
	}
	/**
	 * Get the category title.
	 * @return the category title. 
	 */
	@Override
	public String getCategoryTitle() {
	
		return "Extensions";
	}
	/**
	 * Create  a new entity. No action.
	 */
	@Override
	public String newEntity(JMainConsole console, String locator$) {
	 		return null;
	}
}
