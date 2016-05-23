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
/**
 *  This class represents an item in the list of facets 
 *  available for adding to an entity. 
 */
public abstract class JFacetAddItem extends JItemPanel implements JRequester{
	private static final long serialVersionUID = 1L;
/**
 * Indicates  adding facet as component.	
 */
	public static final String METHOD_ADD_COMPONENT="addComponent";
	/**
	 * Indicates  adding facet to the entity.	
	 */
	public static final String METHOD_ADD_FACET="addFacet";
	/**
	 * Tag of  adding mode.	
	 */
	public static final String ADD_MODE="add mode";
	protected String entihome$;
	protected String entityKey$;
	protected String method$;
	protected String extension$;
	protected String addItem$;
	/**
	 * Set mode to add facet as component.	
	 */
	public static final String ADD_MODE_COMPONENT="add mode component";
	/**
	 * Default constructor.	
	 */
	public JFacetAddItem(){
		super();
	}
	/**
	 * Create the facet add item
	 *  @param console the main application console
     * @param locator$ the requester string.
	 * @return the JFacetAddItem.
	 */	
	@Override
	public JFacetAddItem instantiate(JMainConsole console,String locator$){
		try{
			
			System.out.println("JFacetAddItem:instantiate:locator="+locator$);  
			Properties locator=Locator.toProperties(locator$);
			entihome$=locator.getProperty(Entigrator.ENTIHOME);
			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			method$=locator.getProperty(BaseHandler.HANDLER_METHOD);
			extension$=locator.getProperty(BaseHandler.HANDLER_LOCATION);
			addItem$=locator.getProperty(BaseHandler.HANDLER_CLASS);
			Entigrator entigrator=console.getEntigrator(entihome$);
			JFacetAddItem addItem;
			if(extension$==null)
			    addItem=(JFacetAddItem)JConsoleHandler.getHandlerInstance(entigrator, addItem$);
			else
				addItem=(JFacetAddItem)JConsoleHandler.getHandlerInstance(entigrator, addItem$,extension$);
			locator$=addItem.getLocator();
			System.out.println("FacetAddItem:instantiate:0:faiLocator="+locator$); 
			//getLocator();
			if(entihome$!=null)
				locator$=Locator.append(locator$, Entigrator.ENTIHOME, entihome$);
			if(entityKey$!=null)
				locator$=Locator.append(locator$, EntityHandler.ENTITY_KEY, entityKey$);
			if(method$!=null)
				locator$=Locator.append(locator$, BaseHandler.HANDLER_METHOD, method$);
			
			if(extension$!=null){
				locator$=Locator.append(locator$, BaseHandler.HANDLER_LOCATION,extension$);
				String iconResource$=addItem.getIconResource();
				if(iconResource$!=null)
					 icon$=ExtensionHandler.loadIcon(entigrator, extension$,iconResource$);
				if(icon$!=null)
					locator$=Locator.append(locator$,Locator.LOCATOR_ICON,icon$);
			}
			
			System.out.println("FacetAddItem:instantiate:1:faiLocator="+locator$);  
			locator$=markAppliedUncheckable(console, locator$);
			super.instantiate(console, locator$);
		
		}catch(Exception e){
			Logger.getLogger(JFacetAddItem.class.getName()).severe(e.toString());
		}
		return this;
	}		
public abstract void addFacet(JMainConsole console, String locator$);
public abstract void addComponent(JMainConsole console, String locator$);
public abstract FacetHandler getFacetHandler();
public abstract String getIconResource();
public abstract String getFacetOpenClass();
public abstract String getFacetAddClass();
public String markAppliedUncheckable(JMainConsole console, String locator$){
	try{
	FacetHandler fh=getFacetHandler();
	Properties locator=Locator.toProperties(locator$);	
	String entihome$=locator.getProperty(Entigrator.ENTIHOME);
	Entigrator entigrator=console.getEntigrator(entihome$);
	if(fh.isApplied(entigrator, locator$))
		locator$=Locator.append(locator$, Locator.LOCATOR_CHECKABLE, Locator.LOCATOR_FALSE);
	else
		locator$=Locator.append(locator$, Locator.LOCATOR_CHECKABLE, Locator.LOCATOR_TRUE);
	
	}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
	}
	return locator$;
}
}
