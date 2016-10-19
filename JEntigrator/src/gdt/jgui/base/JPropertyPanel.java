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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Logger;
import gdt.data.entity.BaseHandler;
import gdt.data.grain.Locator;
import gdt.data.grain.Support;
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
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import org.apache.commons.codec.binary.Base64;
/**
* This context displays actions 
* to manage property names:
* Add property - to add a property name
* Delete 'property name' - to delete the property name.
* Edit 'property name' - to edit the property name.
* Clear properties - to remove unused property names and values.
* The context will be shown by the click on the Property label 
* in the design database context.  
* @author  Alexander Imas
* @version 1.0
* @since   2016-03-11
*/
public class JPropertyPanel extends JItemsListPanel implements JRequester{
	private static final long serialVersionUID = 1L;
	private Logger LOGGER=Logger.getLogger(JPropertyPanel.class.getName());
	public static final String ACTION_ADD_PROPERTY="action add property";
	public static final String ACTION_EDIT_PROPERTY="action edit property";
	public static final String ACTION_CLEAR_PROPERTIES="action clear properties";
	public final static String METHOD_DELETE_PROPERTY="deleteProperty";
	String entihome$;
	String propertyName$;
	String requesterResponseLocator$;
	
	/**
	 * Get context menu. 
	 * @return context menu..
	 */
	@Override
public JMenu getContextMenu() {
		JMenu menu=new JMenu("Context");
		JMenuItem doneItem = new JMenuItem("Done");
		doneItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(requesterResponseLocator$!=null){
					try{
					   byte[] ba=Base64.decodeBase64(requesterResponseLocator$);
					   String responseLocator$=new String(ba,"UTF-8");
					   JConsoleHandler.execute(console, responseLocator$);
						}catch(Exception ee){
							LOGGER.severe(ee.toString());
						}
				}else
				  console.back();
			}
		} );
		menu.add(doneItem);
		return menu;
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
	    String icon$=Support.readHandlerIcon(entigrator,JEntityPrimaryMenu.class, "property.png");
		if(icon$!=null)
	     locator.setProperty(Locator.LOCATOR_ICON,icon$);
	    }
	    if(propertyName$!=null)
		       locator.setProperty(JDesignPanel.PROPERTY_NAME,propertyName$);
	    locator.setProperty(Locator.LOCATOR_TITLE, "Property");
	    locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
	    locator.setProperty(BaseHandler.HANDLER_CLASS,JPropertyPanel.class.getName());
	   
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
		//System.out.println("BaseNavigator:instantiate:locator="+Locator.remove(locator$,Locator.LOCATOR_ICON));
		this.console=console;
		Properties locator=Locator.toProperties(locator$);
		entihome$=locator.getProperty(Entigrator.ENTIHOME);
		propertyName$=locator.getProperty(JDesignPanel.PROPERTY_NAME);
		requesterResponseLocator$=locator.getProperty(JRequester.REQUESTER_RESPONSE_LOCATOR);
		ArrayList<JItemPanel>ipl=new ArrayList<JItemPanel>();
		  String actionLocator$=getAddPropertyLocator();
		  JItemPanel addPropertyItem=new JItemPanel(console, actionLocator$);
		  ipl.add(addPropertyItem);
		  actionLocator$=getDeletePropertyLocator();
		  JItemPanel deletePropertyItem=new JItemPanel(console, actionLocator$);
		  ipl.add(deletePropertyItem);
		  actionLocator$=getEditPropertyLocator();
		  JItemPanel editPropertyItem=new JItemPanel(console, actionLocator$);
		  ipl.add(editPropertyItem);
		  actionLocator$=getClearPropertiesLocator();
		  JItemPanel clearPropertiesItem=new JItemPanel(console, actionLocator$);
		  ipl.add(clearPropertiesItem);
		putItems(ipl.toArray(new JItemPanel[0]));
		return this;
	}
private String getAddPropertyLocator(){
	 try{
			JTextEditor textEditor=new JTextEditor();
			String locator$=textEditor.getLocator();
			locator$=Locator.append(locator$, Locator.LOCATOR_TITLE,"Add property");
			locator$=Locator.append(locator$, Entigrator.ENTIHOME,entihome$);
		    Entigrator entigrator=console.getEntigrator(entihome$);
			String icon$=Support.readHandlerIcon(entigrator,JEntityPrimaryMenu.class, "add.png");
			locator$=Locator.append(locator$, Locator.LOCATOR_ICON,icon$);
			String responseLocator$=getLocator();
			responseLocator$=Locator.append(responseLocator$, BaseHandler.HANDLER_METHOD, "response");
			responseLocator$=Locator.append(responseLocator$ ,JRequester.REQUESTER_ACTION,ACTION_ADD_PROPERTY);
			if(requesterResponseLocator$!=null)
	        	responseLocator$=Locator.append(responseLocator$ ,JRequester.REQUESTER_RESPONSE_LOCATOR,requesterResponseLocator$);
			locator$=Locator.append(locator$,JRequester.REQUESTER_RESPONSE_LOCATOR,Locator.compressText(responseLocator$));
			return locator$;
			}catch(Exception ee){
				LOGGER.severe(ee.toString());
				return null;
			}
}
private String getDeletePropertyLocator(){
	 try{
			Properties locator=new Properties();
			locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
			locator.setProperty(Entigrator.ENTIHOME,entihome$);
			locator.setProperty(JDesignPanel.PROPERTY_NAME,propertyName$);
			locator.setProperty(Locator.LOCATOR_TITLE,"Delete '"+propertyName$+"'");
			locator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
			locator.setProperty(BaseHandler.HANDLER_METHOD,METHOD_DELETE_PROPERTY);
			 Entigrator entigrator=console.getEntigrator(entihome$);
			String icon$=Support.readHandlerIcon(entigrator,JEntityPrimaryMenu.class, "delete.png");
			locator.setProperty(Locator.LOCATOR_ICON,icon$);
			return Locator.toString(locator);
			}catch(Exception ee){
				LOGGER.severe(ee.toString());
				return null;
			}
}
private String getEditPropertyLocator(){
	 try{
		JTextEditor textEditor=new JTextEditor();
		String requestlocator$=textEditor.getLocator();
		requestlocator$=Locator.append(requestlocator$, Entigrator.ENTIHOME,entihome$);
		requestlocator$=Locator.append(requestlocator$, JTextEditor.TEXT,propertyName$);
		 Entigrator entigrator=console.getEntigrator(entihome$);
		String icon$=Support.readHandlerIcon(entigrator,JEntityPrimaryMenu.class, "edit.png");
		requestlocator$=Locator.append(requestlocator$,Locator.LOCATOR_ICON,icon$);
		requestlocator$=Locator.append(requestlocator$,Locator.LOCATOR_TITLE,"Edit '"+propertyName$+"'");
		String responseLocator$=getLocator();
		responseLocator$=Locator.append(responseLocator$, BaseHandler.HANDLER_METHOD, "response");
		responseLocator$=Locator.append(responseLocator$ ,JRequester.REQUESTER_ACTION,ACTION_EDIT_PROPERTY);
		if(requesterResponseLocator$!=null)
        	responseLocator$=Locator.append(responseLocator$ ,JRequester.REQUESTER_RESPONSE_LOCATOR,requesterResponseLocator$);
		requestlocator$=Locator.append(requestlocator$,JRequester.REQUESTER_RESPONSE_LOCATOR,Locator.compressText(responseLocator$));
		return requestlocator$;
			}catch(Exception ee){
				LOGGER.severe(ee.toString());
				return null;
			}
}
private String getClearPropertiesLocator(){
	 try{
		String responseLocator$=getLocator();
		 Entigrator entigrator=console.getEntigrator(entihome$);
		String icon$=Support.readHandlerIcon(entigrator,JEntityPrimaryMenu.class, "broom.png");
		responseLocator$=Locator.append(responseLocator$,Locator.LOCATOR_ICON,icon$);
		responseLocator$=Locator.append(responseLocator$,Locator.LOCATOR_TITLE,"Clear properties ");
		responseLocator$=Locator.append(responseLocator$, BaseHandler.HANDLER_METHOD, "response");
		responseLocator$=Locator.append(responseLocator$ ,JRequester.REQUESTER_ACTION,ACTION_CLEAR_PROPERTIES);
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
		return "Property";
	}
	/**
	 * Get context type.
	 * @return the type string.
	 */	
	@Override
	public String getType() {
		return "Property";
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
			//System.out.println("PropertyPanel:response:locator="+locator$);
			Properties locator=Locator.toProperties(locator$);
			String action$=locator.getProperty(JRequester.REQUESTER_ACTION);
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			Entigrator entigrator=console.getEntigrator(entihome$);
			if(ACTION_ADD_PROPERTY.equals(action$)){
				String text$=locator.getProperty(JTextEditor.TEXT);
			//	System.out.println("PropertyPanel:response:property="+text$);
				if(text$!=null)
					entigrator.indx_addPropertyName(text$);
				    JDesignPanel dp=new JDesignPanel();
				    String dpLocator$=dp.getLocator();
				    dpLocator$=Locator.append(dpLocator$, Entigrator.ENTIHOME, entihome$);
				    dpLocator$=Locator.append(dpLocator$, JDesignPanel.PROPERTY_NAME, text$);
					JConsoleHandler.execute(console, dpLocator$);
				return;
			}
			if(ACTION_EDIT_PROPERTY.equals(action$)){
				String text$=locator.getProperty(JTextEditor.TEXT);
				String propertyName$=locator.getProperty(JDesignPanel.PROPERTY_NAME);
			//	System.out.println("PropertyPanel:response:set  property name ="+propertyName$+" new="+text$);
				if(text$!=null)
					entigrator.prp_editPropertyName(propertyName$, text$);
					
				    JDesignPanel dp=new JDesignPanel();
				    String dpLocator$=dp.getLocator();
				    dpLocator$=Locator.append(dpLocator$, Entigrator.ENTIHOME, entihome$);
				    dpLocator$=Locator.append(dpLocator$, JDesignPanel.PROPERTY_NAME, text$);
				    JConsoleHandler.execute(console, dpLocator$);
				return;
			}
			if(ACTION_CLEAR_PROPERTIES.equals(action$)){
			//System.out.println("PropertyPanel:response:action="+action$);
				 int response = JOptionPane.showConfirmDialog(console.getContentPanel(), "Delete unused properties ?", "Confirm",
					        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				   if (response == JOptionPane.YES_OPTION) {
					   entigrator.prp_deleteWrongEntries();
					   JDesignPanel dp=new JDesignPanel();
					   String dpLocator$=dp.getLocator();
					   dpLocator$=Locator.append(dpLocator$, Entigrator.ENTIHOME, entihome$);
					   JConsoleHandler.execute(console, dpLocator$);
				       return;
			}
			}
			}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());	
			}
	}
	/**
	 * Get context subtitle.
	 * @return the subtitle string.
	 */	
	@Override
	public String getSubtitle() {
		try{
			return console.getEntigrator(entihome$).getBaseName();
		}catch(Exception e){
			return null;
		}
	}
	public void deleteProperty(JMainConsole console,String locator$){
		  try{
			  Properties locator=Locator.toProperties(locator$);
			  String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			  Entigrator entigrator=console.getEntigrator(entihome$);
			  String propertyName$=locator.getProperty(JDesignPanel.PROPERTY_NAME);
			  int response = JOptionPane.showConfirmDialog(console.getContentPanel(), "Delete property ?", "Confirm",
				        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			   if (response == JOptionPane.YES_OPTION) {
				   entigrator.prp_deletePropertyName(propertyName$);
				   JDesignPanel dp=new JDesignPanel();
				   String dpLocator$=dp.getLocator();
				   dpLocator$=Locator.append(dpLocator$, Entigrator.ENTIHOME,entihome$);
				   JConsoleHandler.execute(console,dpLocator$);
			   }
			  
		  }catch(Exception e){
			  LOGGER.severe(e.toString());
		  }
		}
	@Override
	public void activate() {
		// TODO Auto-generated method stub
		
	}
	
	
}
