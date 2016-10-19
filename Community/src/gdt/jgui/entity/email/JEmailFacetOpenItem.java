package gdt.jgui.entity.email;
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
import java.util.Properties;
import java.util.logging.Logger;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.codec.binary.Base64;

import gdt.data.entity.BaseHandler;
import gdt.data.entity.EmailHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.FacetHandler;
import gdt.data.entity.facet.ExtensionHandler;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JFacetOpenItem;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.entity.JEntityDigestDisplay;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.tool.JTextEditor;

public class JEmailFacetOpenItem extends JFacetOpenItem implements JRequester{

	private static final long serialVersionUID = 1L;
	
	//public static final String EXTENSION_KEY="_v6z8CVgemqMI6Bledpc7F1j0pVY";
	private Logger LOGGER=Logger.getLogger(EmailHandler.class.getName());
	String email$;
	boolean debug=false;
	public JEmailFacetOpenItem(){
		super();
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
			if(debug)	
				System.out.println("JEmailFacetOpenItem:getLocator:BEGIN");
			
		JEmailEditor editor=new JEmailEditor();
		String editorLocator$=editor.getLocator();
		if(entihome$!=null)
			editorLocator$=Locator.append(editorLocator$,Entigrator.ENTIHOME,entihome$);
		if(entityKey$!=null)
			editorLocator$=Locator.append(editorLocator$,EntityHandler.ENTITY_KEY,entityKey$);
		Properties locator=new Properties();
		locator.setProperty(Locator.LOCATOR_TITLE,"Email");
		locator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
		locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
		locator.setProperty(BaseHandler.HANDLER_LOCATION,EmailHandler.EXTENSION_KEY);
		locator.setProperty(BaseHandler.HANDLER_METHOD,METHOD_OPEN_FACET);
		locator.setProperty( JContext.CONTEXT_TYPE,"Email facet");
		locator.setProperty(Locator.LOCATOR_TITLE,"Email");
		if(entityKey$!=null)
			locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
		if(entihome$!=null){
			locator.setProperty(Entigrator.ENTIHOME,entihome$);
			locator.setProperty(Locator.LOCATOR_CHECKABLE,Locator.LOCATOR_TRUE);
			if(debug)	
				System.out.println("JEmailFacetOpenItem:getLocator:1");
			Entigrator entigrator=console.getEntigrator(entihome$);
			String icon$=ExtensionHandler.loadIcon(entigrator,EmailHandler.EXTENSION_KEY, "email.png");
		    if(icon$!=null)
		    	locator.setProperty(Locator.LOCATOR_ICON,icon$);
		}
		locator$=Locator.toString(locator);
		if(debug)	
			System.out.println("JEmailFacetOpenItem:getLocator:locator="+locator$);
		 return locator$;
			}catch(Exception e){
				LOGGER.severe(e.toString());
				return null;
			}
	}
	@Override
	public void response(JMainConsole console, String locator$) {
		//System.out.println("JEmailFacetItem:response:locator:"+locator$);
		try{
			Properties locator=Locator.toProperties(locator$);
			entihome$=locator.getProperty(Entigrator.ENTIHOME);
			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			String text$=locator.getProperty(JTextEditor.TEXT);
			String requesterAction$=locator.getProperty(JRequester.REQUESTER_ACTION);
	//		System.out.println("JEmailFacetItem:response:requester action="+requesterAction$);
			Entigrator entigrator=console.getEntigrator(entihome$);
			Sack entity=entigrator.getEntityAtKey(entityKey$);
			entity=entigrator.ent_assignProperty(entity, "email", text$);
			if(ACTION_DIGEST_CALL.equals(requesterAction$)){
			//	System.out.println("JEmailFacetOpenItem:response:digest call:text:"+text$);
				String requesterResponseLocator$=locator.getProperty(JRequester.REQUESTER_RESPONSE_LOCATOR);
				byte[] ba=Base64.decodeBase64(requesterResponseLocator$);
				String responseLocator$=new String(ba,"UTF-8");
			//	System.out.println("JEmailFacetOpenItem:response:response locator="+responseLocator$);
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
			//System.out.println("JEmailFacetOpenItem:response:efpLocator:"+efpLocator$);
			
			JConsoleHandler.execute(console, efpLocator$);
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
	}
	@Override
	public boolean isRemovable() {
		try{
			entihome$=Locator.getProperty(locator$, Entigrator.ENTIHOME);
			entityKey$=Locator.getProperty(locator$,EntityHandler.ENTITY_KEY);
			Entigrator entigrator=console.getEntigrator(entihome$);
			 Sack entity =entigrator.getEntityAtKey(entityKey$);
			 if("email".equals(entity.getProperty("entity")))
				 return false;
			 return true;
		}catch(Exception e){
			LOGGER.severe(e.toString());
		return false;
		}
	}
	@Override
	public String getFacetName() {
		return "Email";
	}
	@Override
	public String getFacetIcon() {
		if(entihome$!=null){
			Entigrator entigrator=console.getEntigrator(entihome$);
			return ExtensionHandler.loadIcon(entigrator,EmailHandler.EXTENSION_KEY, "email.png");
		}
		return null;
	}
	@Override
	public void removeFacet() {
		try{
		//	System.out.println("JEmailFacetOpenItem:removeFacet:BEGIN");
			Entigrator entigrator=console.getEntigrator(entihome$);
			 Sack entity =entigrator.getEntityAtKey(entityKey$);
			 if("email".equals(entity.getProperty("entity")))
				 return ;
			
			 entity.removeElementItem("fhandler", EmailHandler.class.getName());
			 entity.removeElementItem("jfacet", EmailHandler.class.getName());
			 entigrator.save(entity);
			 entigrator.ent_takeOffProperty(entity, "email");
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
		
		
	}
	@Override
	public void openFacet(JMainConsole console, String locator$) {
		try{
		if(debug)	
			System.out.println("JEmailFacetOpenItem:openFacet:locator="+locator$);
		this.console=console;	
		Properties locator=Locator.toProperties(locator$);
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			String responseLocator$=getLocator();
			
			Properties responseLocator=Locator.toProperties(getLocator());
			responseLocator.setProperty(Entigrator.ENTIHOME, entihome$);
			responseLocator.setProperty(EntityHandler.ENTITY_KEY, entityKey$);
			responseLocator.setProperty(BaseHandler.HANDLER_METHOD, JFacetOpenItem.METHOD_RESPONSE);
			responseLocator$=Locator.toString(responseLocator);
		if(debug)	
			System.out.println("JEmailFacetOpenItem:openFacet:response locator="+responseLocator$);
			String requesterResponseLocator$=Locator.compressText(responseLocator$);

			Entigrator entigrator=console.getEntigrator(entihome$);
			Sack entity=entigrator.getEntityAtKey(entityKey$);
			email$=entity.getProperty("email");
		if(debug)	
		   System.out.println("JEmailFacetOpenItem:openFacet:email="+email$);	
			JEmailEditor emailEditor=new JEmailEditor();
			String emailLocator$=emailEditor.getLocator();
			emailLocator$=Locator.append(emailLocator$, Entigrator.ENTIHOME, entihome$);
			emailLocator$=Locator.append(emailLocator$, EntityHandler.ENTITY_KEY, entityKey$);
			emailLocator$=Locator.append(emailLocator$, JRequester.REQUESTER_RESPONSE_LOCATOR, requesterResponseLocator$);
			emailLocator$=Locator.append(emailLocator$, BaseHandler.HANDLER_METHOD,"instantiate");
			emailLocator$=Locator.append(emailLocator$, JTextEditor.TEXT,email$);
			//System.out.println("JEmailFacetOpenItem:openFacet:email locator="+emailLocator$);
			JConsoleHandler.execute(console, emailLocator$);
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
	}
	@Override
	public String getFacetRenderer() {
		return JEmailEditor.class.getName();
	}
	@Override
	public DefaultMutableTreeNode[] getDigest() {
		try{
			Entigrator entigrator=console.getEntigrator(entihome$);
			Sack entity =entigrator.getEntityAtKey(entityKey$);
			String email$=entity.getProperty("email");
			String locator$=getLocator();
			locator$=Locator.append(locator$, Locator.LOCATOR_TITLE, email$);
			DefaultMutableTreeNode emailNode=new DefaultMutableTreeNode();
			emailNode.setUserObject(locator$);
			return new DefaultMutableTreeNode[]{emailNode};
				
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		}
		return null;
	}
	@Override
	public FacetHandler getFacetHandler() {
		return new EmailHandler();
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
				   //System.out.println("JEmailFacetOpenItem:edit:digest locator="+digestLocator$);
				   try{
					   Properties locator=Locator.toProperties(digestLocator$);
					   String entihome$=locator.getProperty(Entigrator.ENTIHOME);
					   String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
					   Entigrator entigrator=console.getEntigrator(entihome$);
					   Sack entity=entigrator.getEntityAtKey(entityKey$);
					   String email$=entity.getProperty("email");
					   JTextEditor te=new JTextEditor();
					   String teLocator$=te.getLocator();
					   teLocator$=Locator.append(teLocator$, JTextEditor.TEXT, email$);
					   String foiLocator$=getLocator();
					   foiLocator$=Locator.append(foiLocator$,BaseHandler.HANDLER_METHOD,METHOD_RESPONSE);
					   foiLocator$=Locator.append(foiLocator$, JRequester.REQUESTER_ACTION,ACTION_DIGEST_CALL);
					   foiLocator$=Locator.append(foiLocator$, JRequester.REQUESTER_RESPONSE_LOCATOR, Locator.compressText(digestLocator$));
					   teLocator$=Locator.append(teLocator$, JRequester.REQUESTER_RESPONSE_LOCATOR, Locator.compressText(foiLocator$));
					   //System.out.println("JEmailFacetOpenItem:edit:text editor="+teLocator$);
					   JConsoleHandler.execute(console, teLocator$);
				   }catch(Exception ee){
					   Logger.getLogger(JEmailFacetOpenItem.class.getName()).info(ee.toString());
				   }
				}
			    });
		return popup;

	}
}
