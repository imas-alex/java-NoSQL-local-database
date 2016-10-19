package gdt.jgui.base;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.logging.Logger;

import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Identity;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JItemPanel;
import gdt.jgui.console.JItemsListPanel;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.console.ReloadDialog;
import gdt.jgui.entity.JEntitiesPanel;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.entity.JEntityPrimaryMenu;
import javax.swing.JMenu;
/**
* This context displays actions 
* to manage container/component relations :
* Exclude - to exclude component from the container
* List components - to list all components of the container
* Facets - display the facets context for the container.
* The context will be shown by the click on the Container label 
* in the design database context if the list of containers is not empty.  
* @author  Alexander Imas
* @version 1.0
* @since   2016-03-11
*/
public class JContainerPanel extends JItemsListPanel implements JRequester{
	private static final long serialVersionUID = 1L;
	private Logger LOGGER=Logger.getLogger(JContainerPanel.class.getName());
	private static final String ACTION_INCLUDE_COMPONENT="action include component";
	private static final String ACTION_EXCLUDE_COMPONENT="action exclude component";
	private static final String ACTION_FACETS="action facets";
	private static final String ACTION_LIST_COMPONENTS="action list containers";
	
	String entihome$;
	String propertyName$;
	String propertyValue$;
	String containerLabel$;
	String containersList$;
	String entitiesList$;
	String entityLabel$;
	String entityKey$;
	String [] selectedEntities;
	String requesterResponseLocator$;
	String mode$;
	/**
	 * Get context menu. 
	 * @return null.
	 */	
	@Override
	public JMenu getContextMenu() {
		return null;
	}
	/**
	 * Get context locator. 
	 * @return the locator.
	 */	
	@Override
	public String getLocator() {
		Properties locator=new Properties();
	    locator.setProperty(Locator.LOCATOR_TYPE, JContext.CONTEXT_TYPE);
	    locator.setProperty(JContext.CONTEXT_TYPE,getType());
	    if(entihome$!=null){
	       locator.setProperty(Entigrator.ENTIHOME,entihome$);
	       Entigrator entigrator=console.getEntigrator(entihome$);
	       String icon$=Support.readHandlerIcon(entigrator,JEntityPrimaryMenu.class, "box.png");
			 locator.setProperty(Locator.LOCATOR_ICON,icon$);
	    }
	    if(propertyName$!=null)
		       locator.setProperty(JDesignPanel.PROPERTY_NAME,propertyName$);
	    if(propertyValue$!=null)
		       locator.setProperty(JDesignPanel.PROPERTY_VALUE,propertyValue$);
	    if(entityKey$!=null)
		       locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
	    if(selectedEntities!=null)
		       locator.setProperty(EntityHandler.ENTITY_LIST,Locator.toString(selectedEntities));
	    if(containerLabel$!=null)
	    	 locator.setProperty(JDesignPanel.CONTAINER_LABEL,containerLabel$);  
	    if(containersList$!=null)
	    	 locator.setProperty(JDesignPanel.CONTAINERS_LIST,containersList$);  
	    if(mode$!=null)
	    	 locator.setProperty(JDesignPanel.MODE,mode$);  
	    locator.setProperty(Locator.LOCATOR_TITLE, "Container");
	    locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
	    locator.setProperty(BaseHandler.HANDLER_CLASS,JContainerPanel.class.getName());
	   
	    return Locator.toString(locator);
	}
	/**
	 * Create the context.
	 *  @param console the main application console
	 *  @param locator$ the locator string.
	 * @return the context.
	 */	
	@Override
	public JContext instantiate(JMainConsole console, String locator$) {
		//System.out.println("JContainerPanel:instantiate:locator="+Locator.remove(locator$,Locator.LOCATOR_ICON));
		this.console=console;
		Properties locator=Locator.toProperties(locator$);
		entihome$=locator.getProperty(Entigrator.ENTIHOME);
		entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
		containerLabel$=locator.getProperty(JDesignPanel.CONTAINER_LABEL);
		containersList$=locator.getProperty(JDesignPanel.CONTAINERS_LIST);
		String list$=locator.getProperty(EntityHandler.ENTITY_LIST);
		if(list$!=null)
			selectedEntities=Locator.toArray(list$);
		propertyName$=locator.getProperty(JDesignPanel.PROPERTY_NAME);
		propertyValue$=locator.getProperty(JDesignPanel.PROPERTY_VALUE);
	//	System.out.println("ValuePanel:instantiate:property name="+propertyName$+" value"+propertyValue$+" entity="+entityKey$);
		mode$=locator.getProperty(JDesignPanel.MODE);
		requesterResponseLocator$=locator.getProperty(JRequester.REQUESTER_RESPONSE_LOCATOR);
		ArrayList<JItemPanel>ipl=new ArrayList<JItemPanel>();
		String actionLocator$=getIncludeLocator();
   	  	if(actionLocator$!=null){
		JItemPanel includeItem=new JItemPanel(console, actionLocator$);
		  ipl.add(includeItem);
   	  	}
		  actionLocator$=getExcludeLocator();
		if(actionLocator$!=null){ 
		  JItemPanel excludeItem=new JItemPanel(console, actionLocator$);
		  ipl.add(excludeItem);
		  }
		  actionLocator$=getListComponentsLocator();
		 if(actionLocator$!=null){
		  JItemPanel listContainersItem=new JItemPanel(console, actionLocator$);
		  ipl.add(listContainersItem);
		  }
		 actionLocator$=getFacetsLocator();
		 if(actionLocator$!=null){
			  JItemPanel facetsItem=new JItemPanel(console, actionLocator$);
			  ipl.add(facetsItem);
			  }
		putItems(ipl.toArray(new JItemPanel[0]));
		return this;
	}

private String getIncludeLocator(){
	 try{
		    Entigrator entigrator=console.getEntigrator(entihome$);
		    String containerKey$=entigrator.indx_keyAtLabel(containerLabel$);
		    Sack container=entigrator.getEntityAtKey(containerKey$);
		    Sack component=entigrator.getEntityAtKey(entityKey$);
		    if(entigrator.col_isComponentDown(container, component))
		    	return null;
		 	String locator$=getLocator();
            locator$=Locator.append(locator$, Locator.LOCATOR_TITLE,"Include '"+component.getProperty("label")+"'");
            locator$=Locator.append(locator$,BaseHandler.HANDLER_METHOD,"response"); 
            locator$=Locator.append(locator$,JRequester.REQUESTER_ACTION,ACTION_INCLUDE_COMPONENT);
			String icon$=Support.readHandlerIcon(entigrator,JEntityPrimaryMenu.class, "include.png");
			if(icon$!=null)
			locator$=Locator.append(locator$,Locator.LOCATOR_ICON,icon$);
			return locator$;
			}catch(Exception ee){
				LOGGER.severe(ee.toString());
				return null;
			}
}
private String getExcludeLocator(){
	 try{
		   Entigrator entigrator=console.getEntigrator(entihome$);
		    String containerKey$=entigrator.indx_keyAtLabel(containerLabel$);
		    Sack container=entigrator.getEntityAtKey(containerKey$);
		    Sack component=entigrator.getEntityAtKey(entityKey$);
		    if(!entigrator.col_isComponentDown(container, component))
		    	return null;
		 	String locator$=getLocator();
           locator$=Locator.append(locator$, Locator.LOCATOR_TITLE,"Exclude '"+component.getProperty("label")+"'");
           locator$=Locator.append(locator$,BaseHandler.HANDLER_METHOD,"response"); 
           locator$=Locator.append(locator$,JRequester.REQUESTER_ACTION,ACTION_EXCLUDE_COMPONENT);
           String icon$=Support.readHandlerIcon(entigrator,JEntityPrimaryMenu.class, "exclude.png");
           //String icon$=Support.readHandlerIcon(JEntityPrimaryMenu.class, "exclude.png");
           if(icon$!=null)
			locator$=Locator.append(locator$,Locator.LOCATOR_ICON,icon$);
			return locator$;
			}catch(Exception ee){
				LOGGER.severe(ee.toString());
				return null;
			}
}
private String getListComponentsLocator(){
	 try{
		 Entigrator entigrator=console.getEntigrator(entihome$);
		    String containerKey$=entigrator.indx_keyAtLabel(containerLabel$);
		    Sack container=entigrator.getEntityAtKey(containerKey$);
		   String[] sa=entigrator.ent_listComponents(container);
		    if(sa==null||sa.length<1)
		    	return null;
		 String locator$=getLocator();
        locator$=Locator.append(locator$, Locator.LOCATOR_TITLE,"List components");
        locator$=Locator.append(locator$,BaseHandler.HANDLER_METHOD,"response"); 
        locator$=Locator.append(locator$,JRequester.REQUESTER_ACTION,ACTION_LIST_COMPONENTS);
		String icon$=Support.readHandlerIcon(entigrator,JEntityPrimaryMenu.class, "entities.png");
		if(icon$!=null)
		locator$=Locator.append(locator$,Locator.LOCATOR_ICON,icon$);
		return locator$;
			}catch(Exception ee){
				LOGGER.severe(ee.toString());
				return null;
			}
}	 
private String getFacetsLocator(){
	 try{
		 Entigrator entigrator=console.getEntigrator(entihome$);
		 String locator$=getLocator();
       locator$=Locator.append(locator$, Locator.LOCATOR_TITLE,"Facets");
       locator$=Locator.append(locator$,BaseHandler.HANDLER_METHOD,"response"); 
       locator$=Locator.append(locator$,JRequester.REQUESTER_ACTION,ACTION_FACETS);
		String icon$=Support.readHandlerIcon(entigrator,JEntityPrimaryMenu.class, "facet.png");
		if(icon$!=null)
		locator$=Locator.append(locator$,Locator.LOCATOR_ICON,icon$);
		return locator$;
			}catch(Exception ee){
				LOGGER.severe(ee.toString());
				return null;
			}
}		
/**
 * Get context title.
 * @return the title string.
 */	
	@Override
	public String getTitle() {
		return containerLabel$;
	}
	/**
	 * Get context type.
	 * @return the type string.
	 */	
	@Override
	public String getType() {
		return "Container";
	}
	/**
	 * Complete the context after
	 * remove it from the main console.
	 */	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Response on menu action
	 * @param console main console
	 * @param locator$ the locator string.
	 */	
	
	@Override
	public void response(JMainConsole console, String locator$) {
		try{
//			System.out.println("ValuePanel:response:locator="+locator$);
			Properties locator=Locator.toProperties(locator$);
			String action$=locator.getProperty(JRequester.REQUESTER_ACTION);
			containerLabel$=locator.getProperty(JDesignPanel.CONTAINER_LABEL);
			
//			System.out.println("ValuePanel:response:property name="+propertyName$);
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			Entigrator entigrator=console.getEntigrator(entihome$);
			String containerKey$=entigrator.indx_keyAtLabel(containerLabel$);
			Sack container=entigrator.getEntityAtKey(containerKey$);
			if(ACTION_INCLUDE_COMPONENT.equals(action$)){
				     entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
				     Sack component=entigrator.getEntityAtKey(entityKey$);
				     entigrator.col_addComponent(container, component);
				    JDesignPanel dp=new JDesignPanel();
				    String dpLocator$=dp.getLocator();
				    dpLocator$=Locator.append(dpLocator$, Entigrator.ENTIHOME, entihome$);
				    dpLocator$=Locator.append(dpLocator$, JDesignPanel.PROPERTY_NAME, propertyName$);
				    dpLocator$=Locator.append(dpLocator$, JDesignPanel.PROPERTY_VALUE, propertyValue$);
				    dpLocator$=Locator.append(dpLocator$, JDesignPanel.CONTAINER_LABEL, containerLabel$);
				    dpLocator$=Locator.append(dpLocator$, JDesignPanel.CONTAINERS_LIST, containersList$);
				    dpLocator$=Locator.append(dpLocator$, JDesignPanel.MODE, mode$);
				    System.out.println("JContainerPanel:response:locator="+Locator.remove(dpLocator$,Locator.LOCATOR_ICON));
					
				    JConsoleHandler.execute(console, dpLocator$);
				    
				return;
			}
			if(ACTION_EXCLUDE_COMPONENT.equals(action$)){
				    entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			        Sack component=entigrator.getEntityAtKey(entityKey$);
			        entigrator.col_breakRelation(container, component);
				    JDesignPanel dp=new JDesignPanel();
				    String dpLocator$=dp.getLocator();
				    dpLocator$=Locator.append(dpLocator$, Entigrator.ENTIHOME, entihome$);
				    dpLocator$=Locator.append(dpLocator$, JDesignPanel.PROPERTY_NAME, propertyName$);
				    dpLocator$=Locator.append(dpLocator$, JDesignPanel.PROPERTY_VALUE, propertyValue$);
				    dpLocator$=Locator.append(dpLocator$, JDesignPanel.CONTAINER_LABEL, containerLabel$);
				    dpLocator$=Locator.append(dpLocator$, JDesignPanel.CONTAINERS_LIST, containersList$);
				    dpLocator$=Locator.append(dpLocator$, JDesignPanel.MODE, mode$);
				    JConsoleHandler.execute(console, dpLocator$);
				return;
			}
			if(ACTION_LIST_COMPONENTS.equals(action$)){
				//System.out.println("ValuePanel:response:action="+action$);
						   String[] sa=entigrator.ent_listComponents(container);
						   if(sa==null||sa.length<1)
							   return;
						   ArrayList<String>sl=new ArrayList<String>();
						   String label$;
						   for(String s:sa){
							   label$=entigrator.indx_getLabel(s);
							   if(label$!=null)
								   sl.add(label$);
						   }
						  Collections.sort(sl);	   
						   entitiesList$=Locator.toString(sl.toArray(new String[0]));
						   JEntitiesPanel jep=new JEntitiesPanel();
						   String jepLocator$=jep.getLocator();
						   jepLocator$=Locator.append(jepLocator$, Entigrator.ENTIHOME, entihome$);
						   jepLocator$=Locator.append(jepLocator$,EntityHandler.ENTITY_LIST,entitiesList$);
						   jepLocator$=Locator.append(jepLocator$,EntityHandler.ENTITY_KEY,entityKey$);
						   JConsoleHandler.execute(console, jepLocator$);
					       return;
				}
			if(ACTION_FACETS.equals(action$)){
				//System.out.println("ValuePanel:response:action="+action$);
						   JEntityFacetPanel jfp=new JEntityFacetPanel();
						   String jfpLocator$=jfp.getLocator();
						   jfpLocator$=Locator.append(jfpLocator$, Entigrator.ENTIHOME, entihome$);
						   jfpLocator$=Locator.append(jfpLocator$,EntityHandler.ENTITY_KEY,entityKey$);
						   JConsoleHandler.execute(console, jfpLocator$);
					       return;
				}
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
		
	}
	/**
	 * Get context subtitle.
	 * @return the subtitle string.
	 */		
	@Override
	public String getSubtitle() {
			return entihome$;
	}
	@Override
	public void activate() {
		
	}

}
