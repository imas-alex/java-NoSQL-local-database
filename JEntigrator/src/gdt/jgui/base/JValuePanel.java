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
import java.util.Properties;
import java.util.logging.Logger;

import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JItemPanel;
import gdt.jgui.console.JItemsListPanel;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.entity.JEntityPrimaryMenu;
import gdt.jgui.tool.JTextEditor;

import javax.swing.JMenu;
import javax.swing.JOptionPane;
/**
* This context displays actions 
* to manage property values:
* Add value - to add a property value
* Delete 'property value' - to delete the property value.
* Edit 'property value' - to edit the property value.
* Take off 'property name' - to remove the property from 
* the selected entity.
* Clear values - to remove unused property values.
* The context will be shown by the click on the Value label 
* in the design database context.  
* @author  Alexander Imas
* @version 1.0
* @since   2016-03-11
*/


public class JValuePanel extends JItemsListPanel implements JRequester{
	private static final long serialVersionUID = 1L;
	private Logger LOGGER=Logger.getLogger(JValuePanel.class.getName());
	private static final String ACTION_ADD_VALUE="action add value";
	private static final String ACTION_EDIT_VALUE="action edit value";
	private static final String ACTION_CLEAR_VALUES="action clear values";
	private final static String METHOD_DELETE_VALUE="deleteValue";
	private final static String METHOD_ASSIGN_VALUE="assignValue";
	private final static String METHOD_TAKE_OFF_VALUE="takeOffValue";
	String entihome$;
	String propertyName$;
	String propertyValue$;
	String entityKey$;
	String [] selectedEntities;
	String requesterResponseLocator$;
	String mode$;
	boolean debug=false;
	/**
	 * Get context menu. 
	 * @return context menu..
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
	    locator.setProperty(Locator.LOCATOR_ICON_CONTAINER, Locator.LOCATOR_ICON_CONTAINER_CLASS);
	    locator.setProperty(Locator.LOCATOR_ICON_CLASS,JEntityPrimaryMenu.class.getName());
	    locator.setProperty(Locator.LOCATOR_ICON_FILE, "value.png"); 
	
	    if(entihome$!=null)
	       locator.setProperty(Entigrator.ENTIHOME,entihome$);
	    if(propertyName$!=null)
		       locator.setProperty(JDesignPanel.PROPERTY_NAME,propertyName$);
	    if(propertyValue$!=null)
		       locator.setProperty(JDesignPanel.PROPERTY_VALUE,propertyValue$);
	    if(entityKey$!=null)
		       locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
	    if(selectedEntities!=null)
		       locator.setProperty(EntityHandler.ENTITY_LIST,Locator.toString(selectedEntities));
	    locator.setProperty(Locator.LOCATOR_TITLE, "Value");
	    locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
	    locator.setProperty(BaseHandler.HANDLER_CLASS,JValuePanel.class.getName());
	    
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
//		System.out.println("ValuePanel:instantiate:locator="+Locator.remove(locator$,Locator.LOCATOR_ICON));
		this.console=console;
		Properties locator=Locator.toProperties(locator$);
		entihome$=locator.getProperty(Entigrator.ENTIHOME);
		entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		String list$=locator.getProperty(EntityHandler.ENTITY_LIST);
		if(list$!=null)
			selectedEntities=Locator.toArray(list$);
		propertyName$=locator.getProperty(JDesignPanel.PROPERTY_NAME);
		propertyValue$=locator.getProperty(JDesignPanel.PROPERTY_VALUE);
	//	System.out.println("ValuePanel:instantiate:property name="+propertyName$+" value"+propertyValue$+" entity="+entityKey$);
		mode$=locator.getProperty(JDesignPanel.MODE);
		requesterResponseLocator$=locator.getProperty(JRequester.REQUESTER_RESPONSE_LOCATOR);
		ArrayList<JItemPanel>ipl=new ArrayList<JItemPanel>();
		  String actionLocator$=getAddValueLocator();
		  JItemPanel addValueItem=new JItemPanel(console, actionLocator$);
		  ipl.add(addValueItem);
		  actionLocator$=getDeleteValueLocator();
		  JItemPanel deleteValueItem=new JItemPanel(console, actionLocator$);
		  ipl.add(deleteValueItem);
		  actionLocator$=getEditValueLocator();
		  JItemPanel editValueItem=new JItemPanel(console, actionLocator$);
		  ipl.add(editValueItem);
		  actionLocator$=getAssignValueLocator();
		  if(actionLocator$!=null){
		     JItemPanel assignValueItem=new JItemPanel(console, actionLocator$);
		     ipl.add(assignValueItem);
		  }
		  actionLocator$=getTakeOffValueLocator();
		  if(actionLocator$!=null){
		     JItemPanel assignValueItem=new JItemPanel(console, actionLocator$);
		     ipl.add(assignValueItem);
		  }
		  actionLocator$=getClearValuesLocator();
		  JItemPanel clearValuesItem=new JItemPanel(console, actionLocator$);
		  ipl.add(clearValuesItem);
		putItems(ipl.toArray(new JItemPanel[0]));
		return this;
	}
private String getAddValueLocator(){
	 try{
		 JTextEditor textEditor=new JTextEditor();
			String locator$=textEditor.getLocator();
			locator$=Locator.append(locator$, Locator.LOCATOR_TITLE,"Add value");
			locator$=Locator.append(locator$, Entigrator.ENTIHOME,entihome$);
			locator$=Locator.append(locator$,Locator.LOCATOR_ICON_CONTAINER, Locator.LOCATOR_ICON_CONTAINER_CLASS);
			 locator$=Locator.append(locator$,Locator.LOCATOR_ICON_CLASS,JEntityPrimaryMenu.class.getName());
			locator$=Locator.append(locator$,Locator.LOCATOR_ICON_FILE, "add.png"); 
			
			String responseLocator$=getLocator();
			responseLocator$=Locator.append(responseLocator$, BaseHandler.HANDLER_METHOD, "response");
			responseLocator$=Locator.append(responseLocator$ ,JRequester.REQUESTER_ACTION,ACTION_ADD_VALUE);
			if(requesterResponseLocator$!=null)
	        	responseLocator$=Locator.append(responseLocator$ ,JRequester.REQUESTER_RESPONSE_LOCATOR,requesterResponseLocator$);
			locator$=Locator.append(locator$,JRequester.REQUESTER_RESPONSE_LOCATOR,Locator.compressText(responseLocator$));
			return locator$;
			}catch(Exception ee){
				LOGGER.severe(ee.toString());
				return null;
			}
}
private String getDeleteValueLocator(){
	 try{
			Properties locator=new Properties();
			locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
			locator.setProperty(Entigrator.ENTIHOME,entihome$);
			locator.setProperty(JDesignPanel.PROPERTY_NAME,propertyName$);
			locator.setProperty(JDesignPanel.PROPERTY_VALUE,propertyValue$);
			locator.setProperty(Locator.LOCATOR_TITLE,"Delete '"+propertyValue$+"'");
			locator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
			locator.setProperty(BaseHandler.HANDLER_METHOD,METHOD_DELETE_VALUE);
			locator.setProperty(Locator.LOCATOR_ICON_CONTAINER, Locator.LOCATOR_ICON_CONTAINER_CLASS);
			locator.setProperty(Locator.LOCATOR_ICON_CLASS,JEntityPrimaryMenu.class.getName());
			locator.setProperty(Locator.LOCATOR_ICON_FILE, "delete.png"); 
			
			return Locator.toString(locator);
			}catch(Exception ee){
				LOGGER.severe(ee.toString());
				return null;
			}
}
private String getAssignValueLocator(){
	 try{
		 if(debug)
		System.out.println("JValueLocator:getAssignValueLocator:entity key="+entityKey$);	
	if(entityKey$==null)
		return null;
		Entigrator entigrator=console.getEntigrator(entihome$);
		Sack entity=entigrator.getEntityAtKey(entityKey$);
			
			if(entity.getProperty(propertyName$)!=null)
				return null;
			 String   locator$=getLocator();
			 locator$=Locator.append(locator$, Locator.LOCATOR_TITLE,"Assign '"+propertyName$+":"+propertyValue$+"'");
			 locator$=Locator.append(locator$,Locator.LOCATOR_ICON_CONTAINER, Locator.LOCATOR_ICON_CONTAINER_CLASS);
			 locator$=Locator.append(locator$,Locator.LOCATOR_ICON_CLASS,JEntityPrimaryMenu.class.getName());
			locator$=Locator.append(locator$,Locator.LOCATOR_ICON_FILE, "assign.png"); 
		
			 locator$=Locator.append(locator$,BaseHandler.HANDLER_METHOD,METHOD_ASSIGN_VALUE);
   			return locator$;
		 	}catch(Exception ee){
				LOGGER.severe(ee.toString());
				return null;
			}
}
private String getTakeOffValueLocator(){
	 try{
		 if(debug)
		  System.out.println("JValueLocator:TakeOffValueLocator:entity key="+entityKey$);	
		if(entityKey$==null)
				return null;
		 Entigrator entigrator=console.getEntigrator(entihome$);
			Sack entity=entigrator.getEntityAtKey(entityKey$);
			if(entity.getProperty(propertyName$)==null)
				return null;
			 String   locator$=getLocator();
			 locator$=Locator.append(locator$, Locator.LOCATOR_TITLE,"Take off '"+propertyName$+"'");
			 locator$=Locator.append(locator$,Locator.LOCATOR_ICON_CONTAINER, Locator.LOCATOR_ICON_CONTAINER_CLASS);
			 locator$=Locator.append(locator$,Locator.LOCATOR_ICON_CLASS,JEntityPrimaryMenu.class.getName());
			locator$=Locator.append(locator$,Locator.LOCATOR_ICON_FILE, "takeoff.png"); 
			 locator$=Locator.append(locator$,BaseHandler.HANDLER_METHOD,METHOD_TAKE_OFF_VALUE);
			return locator$;
		 	}catch(Exception ee){
				LOGGER.severe(ee.toString());
				return null;
			}
}
private String getClearValuesLocator(){
	 try{
		String responseLocator$=getLocator();
		responseLocator$=Locator.append(responseLocator$,Locator.LOCATOR_ICON_CONTAINER, Locator.LOCATOR_ICON_CONTAINER_CLASS);
		responseLocator$=Locator.append(responseLocator$,Locator.LOCATOR_ICON_CLASS,JEntityPrimaryMenu.class.getName());
		responseLocator$=Locator.append(responseLocator$,Locator.LOCATOR_ICON_FILE, "broom.png"); 
	
		responseLocator$=Locator.append(responseLocator$,Locator.LOCATOR_TITLE,"Clear values ");
		responseLocator$=Locator.append(responseLocator$, BaseHandler.HANDLER_METHOD, "response");
		responseLocator$=Locator.append(responseLocator$ ,JRequester.REQUESTER_ACTION,ACTION_CLEAR_VALUES);
		return responseLocator$;
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
		return propertyName$;
	}
/**
 * Get context type.
 * @return the type string.
 */	
	@Override
	public String getType() {
		return "Value";
	}
	/**
	 * Complete the context after
	 * remove it from the main console.
	 */	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
	private String getEditValueLocator(){
		 try{
			 JTextEditor textEditor=new JTextEditor();
				String requestLocator$=textEditor.getLocator();
				requestLocator$=Locator.append(requestLocator$, Entigrator.ENTIHOME,entihome$);
				requestLocator$=Locator.append(requestLocator$, JTextEditor.TEXT,propertyValue$);
				requestLocator$=Locator.append(requestLocator$,Locator.LOCATOR_ICON_CONTAINER, Locator.LOCATOR_ICON_CONTAINER_CLASS);
				requestLocator$=Locator.append(requestLocator$,Locator.LOCATOR_ICON_CLASS,JEntityPrimaryMenu.class.getName());
				requestLocator$=Locator.append(requestLocator$,Locator.LOCATOR_ICON_FILE, "broom.png"); 
			
				requestLocator$=Locator.append(requestLocator$,Locator.LOCATOR_TITLE,"Edit '"+propertyValue$+"'");
				String responseLocator$=getLocator();
				responseLocator$=Locator.append(responseLocator$, BaseHandler.HANDLER_METHOD, "response");
				responseLocator$=Locator.append(responseLocator$ ,JRequester.REQUESTER_ACTION,ACTION_EDIT_VALUE);
				if(requesterResponseLocator$!=null)
		        	responseLocator$=Locator.append(responseLocator$ ,JRequester.REQUESTER_RESPONSE_LOCATOR,requesterResponseLocator$);
				requestLocator$=Locator.append(requestLocator$,JRequester.REQUESTER_RESPONSE_LOCATOR,Locator.compressText(responseLocator$));
			    return requestLocator$;
			}catch(Exception ee){
					LOGGER.severe(ee.toString());
					return null;
				}
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
			propertyName$=locator.getProperty(JDesignPanel.PROPERTY_NAME);
//			System.out.println("ValuePanel:response:property name="+propertyName$);
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			Entigrator entigrator=console.getEntigrator(entihome$);
			if(ACTION_ADD_VALUE.equals(action$)){
				String text$=locator.getProperty(JTextEditor.TEXT);
				if(debug)
				  System.out.println("JValuePanel:response:value="+text$);
				if(text$!=null){
					if(entigrator.store_isBusy()){
						JBusyStorage.show(this);
						return;
					}
					entigrator.store_lock();
					entigrator.indx_addPropertyValue(propertyName$, text$);
					entigrator.store_release();
					}
				    JDesignPanel dp=new JDesignPanel();
				    String dpLocator$=dp.getLocator();
				    dpLocator$=Locator.append(dpLocator$, Entigrator.ENTIHOME, entihome$);
				    dpLocator$=Locator.append(dpLocator$, JDesignPanel.PROPERTY_NAME, propertyName$);
				    dpLocator$=Locator.append(dpLocator$, JDesignPanel.PROPERTY_VALUE, text$);
					JConsoleHandler.execute(console, dpLocator$);
				return;
			}
			if(ACTION_EDIT_VALUE.equals(action$)){
				String text$=locator.getProperty(JTextEditor.TEXT);
				propertyValue$=locator.getProperty(JDesignPanel.PROPERTY_VALUE);
	//			System.out.println("ValuePanel:response:replace  property value ="+propertyValue$+" new="+text$);
				if(text$!=null){
					String[] sa=entigrator.indx_listEntities(propertyName$, propertyValue$);
					if(entigrator.store_scopeIsBusy(true, sa)){
						JBusyStorage.show(this);
						return;
					}
					entigrator.store_lock();
					entigrator.prp_editPropertyValue(propertyName$,propertyValue$, text$);
					entigrator.store_release();
				}
				    JDesignPanel dp=new JDesignPanel();
				    String dpLocator$=dp.getLocator();
				    dpLocator$=Locator.append(dpLocator$, Entigrator.ENTIHOME, entihome$);
				    dpLocator$=Locator.append(dpLocator$, JDesignPanel.PROPERTY_NAME, propertyName$);
				    dpLocator$=Locator.append(dpLocator$, JDesignPanel.PROPERTY_VALUE, text$);
				    JConsoleHandler.execute(console, dpLocator$);
				return;
			}
			if(ACTION_CLEAR_VALUES.equals(action$)){
				//System.out.println("ValuePanel:response:action="+action$);
					 int response = JOptionPane.showConfirmDialog(console.getContentPanel(), "Delete unused values ?", "Confirm",
						        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					   if (response == JOptionPane.YES_OPTION) {
						   if(entigrator.store_isBusy()){
								JBusyStorage.show(this);
								return;
							}
						   entigrator.store_lock();
						   entigrator.prp_deleteWrongPropertyEntries(propertyName$);
						   entigrator.store_release();
						   JDesignPanel dp=new JDesignPanel();
						   String dpLocator$=dp.getLocator();
						   dpLocator$=Locator.append(dpLocator$, Entigrator.ENTIHOME, entihome$);
						   dpLocator$=Locator.append(dpLocator$, JDesignPanel.PROPERTY_NAME, propertyName$);
						   JConsoleHandler.execute(console, dpLocator$);
					       return;
				}
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
		try{
			//return "Base ="+console.getEntigrator(entihome$).getBaseName()+" Property="+propertyName$;
			return entihome$;
		}catch(Exception e){
			return null;
		}
	}
	public void assignValue(JMainConsole console,String locator$){
		  try{
			  Properties locator=Locator.toProperties(locator$);
			  String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			  Entigrator entigrator=console.getEntigrator(entihome$);
			  propertyName$=locator.getProperty(JDesignPanel.PROPERTY_NAME);
			  propertyValue$=locator.getProperty(JDesignPanel.PROPERTY_VALUE);
			  
			  entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			  String list$=locator.getProperty(EntityHandler.ENTITY_LIST);
			  if(list$!=null){
				  selectedEntities=Locator.toArray(list$);
			if(debug)	  
			  System.out.println("JValuePanel:assignValue:selected list="+selectedEntities.length);
			  }
			  Sack entity;
			  if(selectedEntities!=null)
			     for(String entity$:selectedEntities){
			    	 if(debug)
			    	 System.out.println("JValuePanel:assignValue:entity="+entity$);
			    	 entity=entigrator.getEntityAtKey(entity$);
			    	 if(entity!=null)
			    		 entigrator.ent_assignProperty(entity, propertyName$, propertyValue$);
			    	 else
			    		 entity=entigrator.getEntityAtKey(entityKey$);
			    	 if(entity!=null)
			    		 entigrator.ent_assignProperty(entity, propertyName$, propertyValue$);
			    	 else
			    		 System.out.println("JValuePanel:assignValue:cannot find entity="+entity$);
			    		 
			     }
			  if(debug)
			  System.out.println("ValuePanel:assignValue.entity key="+entityKey$);
			  JDesignPanel dp=new JDesignPanel();
			  String dpLocator$=dp.getLocator();
			  dpLocator$=Locator.append(dpLocator$, Entigrator.ENTIHOME,entihome$);
			  dpLocator$=Locator.append(dpLocator$, JDesignPanel.PROPERTY_NAME,propertyName$);
			  JConsoleHandler.execute(console,dpLocator$);
		  }catch(Exception e){
			  LOGGER.severe(e.toString());
		  }
		}
	public   void takeOffValue(JMainConsole console,String locator$) {
		try{
		//	System.out.println("JValuePanel:takeOffValue:locator="+locator$);
		    Properties locator=Locator.toProperties(locator$);
		    String propertyName$=locator.getProperty(JDesignPanel.PROPERTY_NAME);
		    String entihome$=locator.getProperty(Entigrator.ENTIHOME);
		    String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			Entigrator entigrator=console.getEntigrator(entihome$);
            if(entigrator.store_scopeIsBusy(true, new String[]{entityKey$})){
            	JBusyStorage.show(this);
            	return;
            }
			Sack entity=entigrator.getEntityAtKey(entityKey$);
			entigrator.store_lock();
			entigrator.ent_takeOffProperty(entity, propertyName$);
			JDesignPanel designPanel=new JDesignPanel();
			String designLocator$=designPanel.getLocator();
			designLocator$=Locator.append(designLocator$,Entigrator.ENTIHOME,entihome$);
		    JConsoleHandler.execute(console, designLocator$);
		    entigrator.store_release();
		}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
		}
		
	}
	@Override
	public void activate() {
		// TODO Auto-generated method stub
		
	}
	public void deleteValue(JMainConsole console,String locator$){
		  try{
			  Properties locator=Locator.toProperties(locator$);
			  String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			  Entigrator entigrator=console.getEntigrator(entihome$);
			  if(entigrator.store_isBusy()){
					JBusyStorage.show(this);
					return;
				}
			  
			  String propertyName$=locator.getProperty(JDesignPanel.PROPERTY_NAME);
			  String propertyValue$=locator.getProperty(JDesignPanel.PROPERTY_VALUE);
			  String[] sa=entigrator.indx_listEntities(propertyName$, propertyValue$);
			  if(sa!=null)
				  for(String s:sa)
					  if(entigrator.ent_isBusy( s)){
						  JBusyStorage.show(this);
							return;
				  }
			  int response = JOptionPane.showConfirmDialog(console.getContentPanel(), "Delete value ?", "Confirm",
				        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			   if (response == JOptionPane.YES_OPTION) {
				   entigrator.store_lock();
				   entigrator.prp_deletePropertyValue(propertyName$,propertyValue$);
				   entigrator.store_release();
				   JDesignPanel dp=new JDesignPanel();
				   String dpLocator$=dp.getLocator();
				   dpLocator$=Locator.append(dpLocator$, Entigrator.ENTIHOME,entihome$);
				   dpLocator$=Locator.append(dpLocator$, JDesignPanel.PROPERTY_NAME,propertyName$);
				   JConsoleHandler.execute(console,dpLocator$);
			   }
			  
		  }catch(Exception e){
			  LOGGER.severe(e.toString());
		  }
		}
}
