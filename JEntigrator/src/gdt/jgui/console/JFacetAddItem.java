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
import gdt.data.entity.facet.FieldsHandler;
import gdt.data.grain.Locator;
import gdt.data.store.Entigrator;
import gdt.jgui.entity.JEntityPrimaryMenu;

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
	protected boolean debug=false;
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
	@Override
	public String getLocator(){
		//locator$=super.getLocator();
		if(entihome$!=null)
		 locator$=Locator.append(locator$,Entigrator.ENTIHOME, entihome$);
		if(entityKey$!=null)
			 locator$=Locator.append(locator$,EntityHandler.ENTITY_KEY, entityKey$);
		return locator$;
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
			if(debug)
			 System.out.println("JFacetAddItem:instantiate:locator="+locator$);  
			Properties locator=Locator.toProperties(locator$);
			entihome$=locator.getProperty(Entigrator.ENTIHOME);
			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			method$=locator.getProperty(BaseHandler.HANDLER_METHOD);
			extension$=locator.getProperty(BaseHandler.HANDLER_LOCATION);
			addItem$=locator.getProperty(BaseHandler.HANDLER_CLASS);
			title$=locator.getProperty(Locator.LOCATOR_TITLE);
			Entigrator entigrator=console.getEntigrator(entihome$);
			//JFacetAddItem addItem;
			JFacetAddItem fai;
			if(extension$==null)
			    fai=(JFacetAddItem)JConsoleHandler.getHandlerInstance(entigrator, addItem$);
			else
				fai=(JFacetAddItem)JConsoleHandler.getHandlerInstance(entigrator, addItem$,extension$);
			String faiLocator$=fai.getLocator();
//			if(debug)
//			System.out.println("FacetAddItem:instantiate:0:faiLocator="+locator$); 
			//getLocator();
			if(entihome$!=null)
				faiLocator$=Locator.append(locator$, Entigrator.ENTIHOME, entihome$);
			if(entityKey$!=null)
				faiLocator$=Locator.append(locator$, EntityHandler.ENTITY_KEY, entityKey$);
			if(method$!=null)
				faiLocator$=Locator.append(locator$, BaseHandler.HANDLER_METHOD, method$);
			//=(JFacetAddItem)JConsoleHandler.getHandlerInstance(entigrator, addItem$); 
			//locator$=fai.getLocator();
			String foi$=fai.getFacetOpenClass();
			JFacetOpenItem foi=(JFacetOpenItem)JConsoleHandler.getHandlerInstance(entigrator, foi$);
			JFacetRenderer fr=(JFacetRenderer)JConsoleHandler.getHandlerInstance(entigrator, foi.getFacetRenderer());
			String iconFile$=fr.getFacetIcon();
			faiLocator$=Locator.append(faiLocator$,Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
			faiLocator$=Locator.append(faiLocator$,Locator.LOCATOR_ICON_CLASS,foi$);
			faiLocator$=Locator.append(faiLocator$,Locator.LOCATOR_ICON_FILE,iconFile$);
			if(extension$!=null)
				faiLocator$=Locator.append(faiLocator$,Locator.LOCATOR_ICON_LOCATION,extension$);
				
			if(debug)
			System.out.println("JFacetAddItem:instantiate:faiLocator="+faiLocator$);  
			faiLocator$=markAppliedUncheckable(console, faiLocator$);
			//this.locator$=faiLocator$;
			super.instantiate(console, faiLocator$);
		
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
