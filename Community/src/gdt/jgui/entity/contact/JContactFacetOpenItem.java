package gdt.jgui.entity.contact;
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
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.codec.binary.Base64;

import gdt.data.entity.BaseHandler;
import gdt.data.entity.ContactHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.FacetHandler;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JFacetOpenItem;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.entity.JEntityDigestDisplay;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.tool.JTextEditor;

public class JContactFacetOpenItem extends JFacetOpenItem implements JRequester{

	private static final long serialVersionUID = 1L;
	
	public static final String EXTENSION_KEY="_v6z8CVgemqMI6Bledpc7F1j0pVY";
	private Logger LOGGER=Logger.getLogger(ContactHandler.class.getName());
	String contact$;
	public JContactFacetOpenItem(){
		super();
	}
	public JContactFacetOpenItem(JMainConsole console, String locator$) {
		super(console, locator$);
		Properties locator=Locator.toProperties(locator$);
		entihome$=locator.getProperty(Entigrator.ENTIHOME);
		entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		title$=locator.getProperty(Locator.LOCATOR_TITLE);
		if(title$==null)
			title$="Contact";
		if(icon$==null){
			icon$=Support.readHandlerIcon(JContactFacetOpenItem.class, "contact.png");
		  byte[] ba=Base64.decodeBase64(icon$);
      	  ImageIcon icon = new ImageIcon(ba);
      	  Image image= icon.getImage().getScaledInstance(24, 24, 0);
      	  icon.setImage(image);
      	  title.setIcon(icon); 
		}
		}
	@Override
	public JFacetOpenItem instantiate(JMainConsole console,String locator$){
		try{
			super.instantiate(console, locator$);
			Properties locator=Locator.toProperties(locator$);
			entihome$=locator.getProperty(Entigrator.ENTIHOME);
			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
	        this.locator$=getLocator();
	        this.locator$=Locator.append(this.locator$,BaseHandler.HANDLER_CLASS, getClass().getName());
	        this.locator$=Locator.append(this.locator$,BaseHandler.HANDLER_METHOD, "openFacet");
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
		return this;
	}	
	@Override
	public String getLocator(){
		try{
		JContactEditor editor=new JContactEditor();
		String editorLocator$=editor.getLocator();
		if(entihome$!=null)
			editorLocator$=Locator.append(editorLocator$,Entigrator.ENTIHOME,entihome$);
		if(entityKey$!=null)
			editorLocator$=Locator.append(editorLocator$,EntityHandler.ENTITY_KEY,entityKey$);
		Properties locator=new Properties();
		locator.setProperty(Locator.LOCATOR_TITLE,"Contact");
		locator.setProperty(BaseHandler.HANDLER_CLASS,JContactFacetOpenItem.class.getName());
		locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
		locator.setProperty(BaseHandler.HANDLER_LOCATION,EXTENSION_KEY);
		locator.setProperty(BaseHandler.HANDLER_METHOD,METHOD_OPEN_FACET);
		locator.setProperty( JContext.CONTEXT_TYPE,"Contact facet");
		locator.setProperty(Locator.LOCATOR_TITLE,"Contact");
		if(entityKey$!=null)
			locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
		if(entihome$!=null)
			locator.setProperty(Entigrator.ENTIHOME,entihome$);
		String icon$=Support.readHandlerIcon(JContactFacetOpenItem.class, "contact.png");
		    if(icon$!=null)
		    	locator.setProperty(Locator.LOCATOR_ICON,icon$);
		locator$=Locator.toString(locator);
		 return locator$;
			}catch(Exception e){
				LOGGER.severe(e.toString());
				return null;
			}
	}
	@Override
	public void response(JMainConsole console, String locator$) {
		//System.out.println("JContactFacetItem:response:locator:"+locator$);
		try{
			Properties locator=Locator.toProperties(locator$);
			entihome$=locator.getProperty(Entigrator.ENTIHOME);
			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			String text$=locator.getProperty(JTextEditor.TEXT);
			String requesterAction$=locator.getProperty(JRequester.REQUESTER_ACTION);
	//		System.out.println("JContactFacetItem:response:requester action="+requesterAction$);
			Entigrator entigrator=console.getEntigrator(entihome$);
			Sack entity=entigrator.getEntityAtKey(entityKey$);
			entity=entigrator.ent_assignProperty(entity, "contact", text$);
			if(ACTION_DIGEST_CALL.equals(requesterAction$)){
			//	System.out.println("JContactFacetOpenItem:response:digest call:text:"+text$);
				String requesterResponseLocator$=locator.getProperty(JRequester.REQUESTER_RESPONSE_LOCATOR);
				byte[] ba=Base64.decodeBase64(requesterResponseLocator$);
				String responseLocator$=new String(ba,"UTF-8");
			//	System.out.println("JContactFacetOpenItem:response:response locator="+responseLocator$);
				JEntityDigestDisplay edd=new JEntityDigestDisplay();
				String eddLocator$=edd.getLocator();
				eddLocator$=Locator.append(eddLocator$, Entigrator.ENTIHOME, entihome$);
				eddLocator$=Locator.append(eddLocator$,  EntityHandler.ENTITY_KEY, Locator.getProperty(responseLocator$,JEntityDigestDisplay.ROOT_ENTITY_KEY ));
				eddLocator$=Locator.append(eddLocator$, JEntityDigestDisplay.SELECTION, Locator.getProperty(responseLocator$,JEntityDigestDisplay.SELECTION ));
				JConsoleHandler.execute(console, eddLocator$);
				return;
			}
			JEntityFacetPanel efp=new JEntityFacetPanel(); 
			String efpLocator$=efp.getLocator();
			efpLocator$=Locator.append(efpLocator$,Entigrator.ENTIHOME ,entihome$);
			efpLocator$=Locator.append(efpLocator$,EntityHandler.ENTITY_KEY,entityKey$);
			efpLocator$=Locator.append(efpLocator$,EntityHandler.ENTITY_LABEL,entity.getProperty("label"));
			//System.out.println("JContactFacetOpenItem:response:efpLocator:"+efpLocator$);
			JConsoleHandler.execute(console, efpLocator$);
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
	}
	@Override
	public boolean isRemovable() {
		/*
		try{
			Entigrator entigrator=console.getEntigrator(entihome$);
			 Sack entity =entigrator.getEntityAtKey(entityKey$);
			 if("contact".equals(entity.getProperty("entity")))
				 return false;
			 return true;
		}catch(Exception e){
			LOGGER.severe(e.toString());
		return false;
		}
		*/
		return false;
	}
	@Override
	public String getFacetName() {
		return "Contact";
	}
	@Override
	public String getFacetIcon() {
		return Support.readHandlerIcon(JContactFacetOpenItem.class, "contact.png");
	}
	@Override
	public void removeFacet() {
		try{
		//	System.out.println("JContactFacetOpenItem:removeFacet:BEGIN");
			Entigrator entigrator=console.getEntigrator(entihome$);
			 Sack entity =entigrator.getEntityAtKey(entityKey$);
			 if("contact".equals(entity.getProperty("entity")))
				 return ;
			
			 entity.removeElementItem("fhandler", ContactHandler.class.getName());
			 entity.removeElementItem("jfacet", ContactHandler.class.getName());
			 entigrator.save(entity);
			 entigrator.ent_takeOffProperty(entity, "contact");
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
	}
	@Override
	public void openFacet(JMainConsole console, String locator$) {
		try{
			//System.out.println("JPhoneFacetOpenItem:openFacet:locator="+locator$);
			Properties locator=Locator.toProperties(locator$);
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			String responseLocator$=getLocator();
			
			Properties responseLocator=Locator.toProperties(getLocator());
			responseLocator.setProperty(Entigrator.ENTIHOME, entihome$);
			responseLocator.setProperty(EntityHandler.ENTITY_KEY, entityKey$);
			responseLocator.setProperty(BaseHandler.HANDLER_METHOD, JFacetOpenItem.METHOD_RESPONSE);
			responseLocator$=Locator.toString(responseLocator);
			//System.out.println("JPhoneFacetOpenItem:openFacet:response locator="+responseLocator$);
			String requesterResponseLocator$=Locator.compressText(responseLocator$);

			Entigrator entigrator=console.getEntigrator(entihome$);
			Sack entity=entigrator.getEntityAtKey(entityKey$);
			contact$=entity.getProperty("contact");
		//System.out.println("JContactFacetOpenItem:openFacet:phone="+phone$);	
			JContactEditor contactEditor=new JContactEditor();
			String contactLocator$=contactEditor.getLocator();
			contactLocator$=Locator.append(contactLocator$, Entigrator.ENTIHOME, entihome$);
			contactLocator$=Locator.append(contactLocator$, EntityHandler.ENTITY_KEY, entityKey$);
			contactLocator$=Locator.append(contactLocator$, JRequester.REQUESTER_RESPONSE_LOCATOR, requesterResponseLocator$);
			contactLocator$=Locator.append(contactLocator$, BaseHandler.HANDLER_METHOD,"instantiate");
			contactLocator$=Locator.append(contactLocator$, JTextEditor.TEXT,contact$);
			//System.out.println("JContactFacetOpenItem:openFacet:phone locator="+contactLocator$);
			JConsoleHandler.execute(console, contactLocator$);
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
	}
	@Override
	public String getFacetRenderer() {
		return JContactEditor.class.getName();
	}
	@Override
	public DefaultMutableTreeNode[] getDigest() {
		try{
			Entigrator entigrator=console.getEntigrator(entihome$);
			Sack entity =entigrator.getEntityAtKey(entityKey$);
			contact$=entity.getProperty("contact");
			String locator$=getLocator();
			locator$=Locator.append(locator$, Locator.LOCATOR_TITLE, contact$);
			DefaultMutableTreeNode contactNode=new DefaultMutableTreeNode();
			contactNode.setUserObject(locator$);
			return new DefaultMutableTreeNode[]{contactNode};
				
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		}
		return null;
	}
	@Override
	public FacetHandler getFacetHandler() {
		return new ContactHandler();
	}
	@Override
	public JPopupMenu getPopupMenu(final String digestLocator$) {
		JPopupMenu	popup = new JPopupMenu();
		JMenuItem editItem=new JMenuItem("Edit");
		   popup.add(editItem);
		   editItem.setHorizontalTextPosition(JMenuItem.RIGHT);
		   editItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
				   //System.out.println("JContactFacetOpenItem:edit:digest locator="+digestLocator$);
				   try{
					   Properties locator=Locator.toProperties(digestLocator$);
					   String entihome$=locator.getProperty(Entigrator.ENTIHOME);
					   String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
					   Entigrator entigrator=console.getEntigrator(entihome$);
					   JContactEditor ce=new JContactEditor();
					   String ceLocator$=ce.getLocator();
					   ceLocator$=Locator.append(ceLocator$,Entigrator.ENTIHOME,entihome$);
					   ceLocator$=Locator.append(ceLocator$,EntityHandler.ENTITY_KEY,entityKey$);
					   JConsoleHandler.execute(console, ceLocator$);
					   
				   }catch(Exception ee){
					   Logger.getLogger(JContactFacetOpenItem.class.getName()).info(ee.toString());
				   }
				}
			    });
		return popup;

	}
}
