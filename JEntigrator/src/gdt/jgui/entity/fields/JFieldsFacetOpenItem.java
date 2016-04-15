package gdt.jgui.entity.fields;
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
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.codec.binary.Base64;

import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.FacetHandler;
import gdt.data.entity.facet.FieldsHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JFacetOpenItem;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.entity.JEntitiesPanel;
import gdt.jgui.entity.JEntityDigestDisplay;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.tool.JTextEditor;
/**
 * This class represents the fields facet item in the list
 * of  entity's facets.
 * @author imasa
 *
 */
public class JFieldsFacetOpenItem extends JFacetOpenItem implements JRequester{
	private static final long serialVersionUID = 1L;
	public static final String LOCATOR_TYPE_FIELD_NAME = "locator type field name";
	public static final String LOCATOR_TYPE_FIELD_VALUE = "locator type field value";
	public static final String FIELD_NAME = "field name";
	public static final String FIELD_VALUE = "field value";
	public static final String NODE_TYPE_FIELD_NAME = "node type field name";
	public static final String NODE_TYPE_FIELD_VALUE = "node type field value";
	public static final String ACTION_DISPLAY_FACETS="action display facets";
	private Logger LOGGER=Logger.getLogger(JFieldsFacetOpenItem.class.getName());
    /**
     * The default constructor.
     * 
     */
	public JFieldsFacetOpenItem(){
		super();
	}
	/**
	 * Get the context locator.
	 * @return the context locator.
	 */
@Override
public String getLocator(){
	Properties locator=new Properties();
	locator.setProperty(Locator.LOCATOR_TITLE,"Fields");
	locator.setProperty(BaseHandler.HANDLER_CLASS,JFieldsFacetOpenItem.class.getName());
	locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
	locator.setProperty(BaseHandler.HANDLER_METHOD,METHOD_OPEN_FACET);
	locator.setProperty( JContext.CONTEXT_TYPE,"Fields facet");
	locator.setProperty(Locator.LOCATOR_TITLE,"Fields");
	locator.setProperty(FACET_HANDLER_CLASS,FieldsHandler.class.getName());
	if(entityKey$!=null)
		locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
	if(entihome$!=null)
		locator.setProperty(Entigrator.ENTIHOME,entihome$);
	 String icon$=Support.readHandlerIcon(JFieldsEditor.class, "fields.png");
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

	//	System.out.println("JFieldsFacetOpenItem:response:FACET locator:"+locator$);
	try{
		Properties locator=Locator.toProperties(locator$);
		String requesterAction$=locator.getProperty(JRequester.REQUESTER_ACTION);
		String text$=locator.getProperty(JTextEditor.TEXT);
		String requesterResponseLocator$=locator.getProperty(JRequester.REQUESTER_RESPONSE_LOCATOR);
		byte[] 	ba=Base64.decodeBase64(requesterResponseLocator$);
		String responseLocator$=new String(ba,"UTF-8");
//		System.out.println("JFieldsFacetItem:response:response locator="+responseLocator$);
		locator=Locator.toProperties(responseLocator$);
		entihome$=locator.getProperty(Entigrator.ENTIHOME);
		entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		if(ACTION_DIGEST_CALL.equals(requesterAction$)){
			 String encodedSelection$=locator.getProperty(JEntityDigestDisplay.SELECTION);
			   ba=Base64.decodeBase64(encodedSelection$);
			   String selection$=new String(ba,"UTF-8");
			   locator=Locator.toProperties(selection$);
			   String fieldName$=locator.getProperty(FIELD_NAME);
			   System.out.println("JFieldsFacetOpenItem:response:SELECTION locator="+selection$);	   
			Entigrator entigrator=console.getEntigrator(entihome$);
			Sack entity=entigrator.getEntityAtKey(entityKey$);
			Core field=entity.getElementItem("field", fieldName$);
			if(field!=null&&text$!=null&&text$.length()>1){
				entity.removeElementItem("field", fieldName$);
				String selectionType$=locator.getProperty(Locator.LOCATOR_TYPE);
				if(FIELD_NAME.equals(selectionType$))
				     field.name=text$;
				if(FIELD_VALUE.equals(selectionType$))
				     field.value=text$;
				entity.putElementItem("field", field);
				entigrator.save(entity);
			}
			JEntityDigestDisplay edd=new JEntityDigestDisplay();
			String eddLocator$=edd.getLocator();
			eddLocator$=Locator.append(eddLocator$, Entigrator.ENTIHOME, entihome$);
			eddLocator$=Locator.append(eddLocator$,  EntityHandler.ENTITY_KEY, Locator.getProperty(responseLocator$,JEntityDigestDisplay.ROOT_ENTITY_KEY ));
			eddLocator$=Locator.append(eddLocator$, JEntityDigestDisplay.SELECTION, Locator.getProperty(responseLocator$,JEntityDigestDisplay.SELECTION ));
			JConsoleHandler.execute(console, eddLocator$);
			return;
		}
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
 * Check if the facet can be removed from the entity.
 * @return true if can be removed false otherwise.
 */
@Override
public boolean isRemovable() {
	try{
		entihome$=Locator.getProperty(locator$, Entigrator.ENTIHOME);
		entityKey$=Locator.getProperty(locator$,EntityHandler.ENTITY_KEY);
		Entigrator entigrator=console.getEntigrator(entihome$);
		 Sack entity =entigrator.getEntityAtKey(entityKey$);
		 if("fields".equals(entity.getProperty("entity")))
			 return false;
		 return true;
	}catch(Exception e){
		LOGGER.severe(e.toString());
	return false;
	}
}
/**
 * Get the facet name.
 * @return the facet name.
 */
@Override
public String getFacetName() {
	return "Fields";
}
/**
 * Get the facet icon as a Base64 string.
 * @return the facet icon string.
 */
@Override
public String getFacetIcon() {
	return Support.readHandlerIcon(JFieldsFacetOpenItem.class, "fields.png");
}
/**
 * Remove the facet from the entity.
 */
@Override
public void removeFacet() {
	try{
		Entigrator entigrator=console.getEntigrator(entihome$);
		 Sack entity =entigrator.getEntityAtKey(entityKey$);
		 if("fields".equals(entity.getProperty("entity")))
			 return ;
		 entity.removeElement("field");
		 entity.removeElementItem("fhandler", FieldsHandler.class.getName());
		 entity.removeElementItem("jfacet", FieldsHandler.class.getName());
		 entigrator.save(entity);
		 entigrator.ent_takeOffProperty(entity, "fields");
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
	
}
/**
 * Display the facet console.
 * @param console the main console.
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

		JFieldsEditor fieldsEditor=new JFieldsEditor();
		String feLocator$=fieldsEditor.getLocator();
		feLocator$=Locator.append(feLocator$, Entigrator.ENTIHOME, entihome$);
		feLocator$=Locator.append(feLocator$, EntityHandler.ENTITY_KEY, entityKey$);
		feLocator$=Locator.append(feLocator$, JRequester.REQUESTER_RESPONSE_LOCATOR, requesterResponseLocator$);
		feLocator$=Locator.append(feLocator$, BaseHandler.HANDLER_METHOD,"instantiate");
		JConsoleHandler.execute(console, feLocator$);
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
}
/**
 * Get the class name of the facet renderer. 
 * @return the JFieldsEditor class name .
 */
@Override
public String getFacetRenderer() {
	return JFieldsEditor.class.getName();
}
/**
 * Get children nodes of the facet node for the digest view.
 * @return the children nodes of the facet node.
 */
@Override
public DefaultMutableTreeNode[] getDigest() {
	try{
		//System.out.println("JFieldsFacetOpenItem:getDigest:locator="+locator$);
		Properties locator=Locator.toProperties(locator$);
		entihome$=locator.getProperty(Entigrator.ENTIHOME);
		entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		Entigrator entigrator=console.getEntigrator(entihome$);
		Sack entity=entigrator.getEntityAtKey(entityKey$);
		Core[]ca=entity.elementGet("field");
		if(ca==null)
			return null;
		DefaultMutableTreeNode nameNode;
		DefaultMutableTreeNode valueNode;
		String locator$=getLocator();
		String nameLocator$;
		String valueLocator$;
		String nameIcon$=icon$=Support.readHandlerIcon(JEntitiesPanel.class, "text.png");
		String valueIcon$=Support.readHandlerIcon(JEntitiesPanel.class, "equal.png");
		ArrayList<DefaultMutableTreeNode>nl=new ArrayList<DefaultMutableTreeNode>();
		for(Core aCa:ca){
			nameNode=new DefaultMutableTreeNode();
			nameLocator$=Locator.append(locator$, Locator.LOCATOR_TITLE,aCa.name);
			nameLocator$=Locator.append(nameLocator$, Locator.LOCATOR_TYPE,FIELD_NAME);
			nameLocator$=Locator.append(nameLocator$,FIELD_NAME,aCa.name);
			nameLocator$=Locator.append(nameLocator$,Locator.LOCATOR_ICON,nameIcon$);
			nameLocator$=Locator.append(nameLocator$,JEntityDigestDisplay.NODE_TYPE,NODE_TYPE_FIELD_NAME);
			if(entihome$!=null)
				nameLocator$=Locator.append(nameLocator$,Entigrator.ENTIHOME,entihome$);
			nameLocator$=Locator.append(nameLocator$,JEntityDigestDisplay.NODE_TYPE,NODE_TYPE_FIELD_VALUE);
			if(entityKey$!=null)
				nameLocator$=Locator.append(nameLocator$,EntityHandler.ENTITY_KEY,entityKey$);
		
			nameNode.setUserObject(nameLocator$);
			valueNode=new DefaultMutableTreeNode();
			valueLocator$=Locator.append(locator$, Locator.LOCATOR_TITLE,aCa.value);
			valueLocator$=Locator.append(valueLocator$,FIELD_NAME,aCa.name);
			valueLocator$=Locator.append(valueLocator$,FIELD_VALUE,aCa.value);
			valueLocator$=Locator.append(valueLocator$, Locator.LOCATOR_TYPE,FIELD_VALUE);
			valueLocator$=Locator.append(valueLocator$,Locator.LOCATOR_ICON,valueIcon$);
			valueLocator$=Locator.append(valueLocator$,JEntityDigestDisplay.NODE_TYPE,NODE_TYPE_FIELD_VALUE);
			if(entihome$!=null)
				valueLocator$=Locator.append(valueLocator$,Entigrator.ENTIHOME,entihome$);
			valueLocator$=Locator.append(valueLocator$,JEntityDigestDisplay.NODE_TYPE,NODE_TYPE_FIELD_VALUE);
			if(entityKey$!=null)
				valueLocator$=Locator.append(valueLocator$,EntityHandler.ENTITY_KEY,entityKey$);
				
			valueNode.setUserObject(valueLocator$);
//			System.out.println("JFieldsFacetOpenItem:getDigest:VALUE locator="+valueLocator$);
			nameNode.add(valueNode);
			nl.add(nameNode);
		}
		return nl.toArray(new DefaultMutableTreeNode[0]);
	}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
	}
	return null;
}
/**
 * Get the facet handler instance.
 * @return the facet handler instance.	
 */
@Override
public FacetHandler getFacetHandler() {
	return new FieldsHandler();
}
/**
 * Get the popup menu for the child node of the facet node 
 * in the digest view.
 * @return the popup menu.	
 */
@Override
public JPopupMenu getPopupMenu(final String digestLocator$) {
	System.out.println("JFieldsFacetOpenItem:getPopupMenu:digest locator="+Locator.remove(digestLocator$, Locator.LOCATOR_ICON));
	JPopupMenu	popup = new JPopupMenu();
	JMenuItem editItem=new JMenuItem("Edit");
	   popup.add(editItem);
	   editItem.setHorizontalTextPosition(JMenuItem.RIGHT);
	   editItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			   try{
				   Properties locator=Locator.toProperties(digestLocator$);
				   String encodedSelection$=locator.getProperty(JEntityDigestDisplay.SELECTION);
				   byte[]ba=Base64.decodeBase64(encodedSelection$);
				   String selection$=new String(ba,"UTF-8");
				   locator=Locator.toProperties(selection$);
				   String entihome$=locator.getProperty(Entigrator.ENTIHOME);
				   String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
				   String nodeType$=locator.getProperty(JEntityDigestDisplay.NODE_TYPE);
				   System.out.println("JFieldsFacetOpenItem:getPopupMenu:node type:"+nodeType$);
				   Entigrator entigrator=console.getEntigrator(entihome$);
			//	   Sack entity=entigrator.getEntityAtKey(entityKey$);
				   if(NODE_TYPE_FIELD_NAME.equals(nodeType$)){
				   String fieldName$=locator.getProperty(Locator.LOCATOR_TITLE);
				   JTextEditor te=new JTextEditor();
				   String teLocator$=te.getLocator();
				   teLocator$=Locator.append(teLocator$, JTextEditor.TEXT, fieldName$);
				   if(entihome$!=null)
					   teLocator$=Locator.append(teLocator$,Entigrator.ENTIHOME,entihome$);
					if(entityKey$!=null)
					   teLocator$=Locator.append(teLocator$,EntityHandler.ENTITY_KEY,entityKey$);
					  
				   String foiLocator$=getLocator();
				   foiLocator$=Locator.append(foiLocator$,BaseHandler.HANDLER_METHOD,METHOD_RESPONSE);
				   foiLocator$=Locator.append(foiLocator$, JRequester.REQUESTER_ACTION,ACTION_DIGEST_CALL);
				   foiLocator$=Locator.append(foiLocator$, FIELD_NAME,fieldName$);
				   foiLocator$=Locator.append(foiLocator$, JRequester.REQUESTER_RESPONSE_LOCATOR, Locator.compressText(digestLocator$));
				   if(entihome$!=null)
				   foiLocator$=Locator.append(foiLocator$,Entigrator.ENTIHOME,entihome$);
				   if(entityKey$!=null)
				   foiLocator$=Locator.append(foiLocator$,EntityHandler.ENTITY_KEY,entityKey$);
				   System.out.println("JFieldsFacetOpenItem:getPopupMenu:name:locator="+foiLocator$);
				   teLocator$=Locator.append(teLocator$, JRequester.REQUESTER_RESPONSE_LOCATOR, Locator.compressText(foiLocator$));
				   JConsoleHandler.execute(console, teLocator$);
				   System.out.println("JFieldsFacetOpenItem:getPopupMenu:teLocator="+teLocator$);
				   return;
				   }
				   if(NODE_TYPE_FIELD_VALUE.equals(nodeType$)){
					   String fieldName$=locator.getProperty(Locator.LOCATOR_TITLE);
					   JTextEditor te=new JTextEditor();
					   String teLocator$=te.getLocator();
					   teLocator$=Locator.append(teLocator$, JTextEditor.TEXT, fieldName$);
					   if(entihome$!=null)
						   teLocator$=Locator.append(teLocator$,Entigrator.ENTIHOME,entihome$);
						if(entityKey$!=null)
						   teLocator$=Locator.append(teLocator$,EntityHandler.ENTITY_KEY,entityKey$);
						
					   String foiLocator$=getLocator();
					   foiLocator$=Locator.append(foiLocator$,BaseHandler.HANDLER_METHOD,METHOD_RESPONSE);
					   foiLocator$=Locator.append(foiLocator$, JRequester.REQUESTER_ACTION,ACTION_DIGEST_CALL);
					   foiLocator$=Locator.append(foiLocator$, FIELD_NAME,fieldName$);
					   foiLocator$=Locator.append(foiLocator$, JRequester.REQUESTER_RESPONSE_LOCATOR, Locator.compressText(digestLocator$));
					   if(entihome$!=null)
						   foiLocator$=Locator.append(foiLocator$,Entigrator.ENTIHOME,entihome$);
						   if(entityKey$!=null)
						   foiLocator$=Locator.append(foiLocator$,EntityHandler.ENTITY_KEY,entityKey$);
						   System.out.println("JFieldsFacetOpenItem:getPopupMenu:value:locator="+foiLocator$);
						 
					   teLocator$=Locator.append(teLocator$, JRequester.REQUESTER_RESPONSE_LOCATOR, Locator.compressText(foiLocator$));
					   JConsoleHandler.execute(console, teLocator$);
					return;  
				   }
				   if(JEntityDigestDisplay.NODE_TYPE_FACET_OWNER.equals(nodeType$)){
					  JEntityDigestDisplay edd=new JEntityDigestDisplay();
					  String eddLocator$=edd.getLocator();
					  eddLocator$=Locator.append(eddLocator$, Entigrator.ENTIHOME, entihome$);
					  eddLocator$=Locator.append(eddLocator$, EntityHandler.ENTITY_KEY, entityKey$);
					  eddLocator$=Locator.append(eddLocator$, JEntityDigestDisplay.SELECTION, encodedSelection$); 
				  	String requesterResponseLocator$=Locator.compressText(eddLocator$);
						JFieldsEditor fieldsEditor=new JFieldsEditor();
						String feLocator$=fieldsEditor.getLocator();
						feLocator$=Locator.append(feLocator$, Entigrator.ENTIHOME, entihome$);
						feLocator$=Locator.append(feLocator$, EntityHandler.ENTITY_KEY, entityKey$);
						feLocator$=Locator.append(feLocator$, JRequester.REQUESTER_RESPONSE_LOCATOR, requesterResponseLocator$);
						feLocator$=Locator.append(feLocator$, BaseHandler.HANDLER_METHOD,"instantiate");
						JConsoleHandler.execute(console, feLocator$);   
				   }
			   }catch(Exception ee){
				   Logger.getLogger(JFieldsFacetOpenItem.class.getName()).info(ee.toString());
			   }
			}
		    });
	return popup;
}
}
