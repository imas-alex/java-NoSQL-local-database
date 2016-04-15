package gdt.jgui.entity.webset;
import java.awt.Desktop;
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
import java.net.URI;
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
import gdt.data.entity.facet.WebsetHandler;
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
import gdt.jgui.entity.folder.JFileOpenItem;
/**
 * This class represents the webset facet item in the list
 * of  entity's facets.
 * @author imasa
 *
 */
public class JWebsetFacetOpenItem extends JFacetOpenItem implements JRequester {
	private static final long serialVersionUID = 1L;
	private static final String NODE_TYPE_WEB_NAME = "node type web name";
	private static final String NODE_TYPE_WEB_ADDRESS = "node type web address";
	private static final String LOCATOR_TYPE_WEB_ADDRESS = "locator type web address";
/**
 * The default constructor.
 */
	public JWebsetFacetOpenItem(){
			super();
		}
	/**
	 * Execute the response locator.
	 * @param console the main console.
	 * @param locator$ the response locator.
	 */	
	@Override
	public void response(JMainConsole console, String locator$) {
//		System.out.println("JWebsetFacetItem:response:FACET locator:"+locator$);
		try{
			Properties locator=Locator.toProperties(locator$);
			String requesterAction$=locator.getProperty(JRequester.REQUESTER_ACTION);
			String requesterResponseLocator$=locator.getProperty(JRequester.REQUESTER_RESPONSE_LOCATOR);
			byte[] 	ba=Base64.decodeBase64(requesterResponseLocator$);
			String responseLocator$=new String(ba,"UTF-8");
	//		System.out.println("JWebsetFacetItem:response:response locator="+responseLocator$);
			locator=Locator.toProperties(responseLocator$);
			entihome$=locator.getProperty(Entigrator.ENTIHOME);
			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			if(ACTION_DIGEST_CALL.equals(requesterAction$)){
				 String encodedSelection$=locator.getProperty(JEntityDigestDisplay.SELECTION);
				   ba=Base64.decodeBase64(encodedSelection$);
				   String selection$=new String(ba,"UTF-8");
				   locator=Locator.toProperties(selection$);
				
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
			Logger.getLogger(getClass().getName()).severe(e.toString());
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
			 if("webset".equals(entity.getProperty("entity")))
				 return false;
			 return true;
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		return false;
		}
	}
	/**
	 * Get the facet name.
	 * @return the facet name.
	 */
	@Override
	public String getFacetName() {
		return "Web links";
	}
	/**
	 * Get the facet icon as a Base64 string.
	 * @return the facet icon string.
	 */
	@Override
	public String getFacetIcon() {
		return Support.readHandlerIcon(JEntitiesPanel.class, "globe.png");
	}
	/**
	 * Get the context locator.
	 * @return the context locator.
	 */
	@Override
	public String getLocator(){
		Properties locator=new Properties();
		locator.setProperty(Locator.LOCATOR_TITLE,"Web links");
		locator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
		locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
		locator.setProperty(BaseHandler.HANDLER_METHOD,METHOD_OPEN_FACET);
		locator.setProperty( JContext.CONTEXT_TYPE,"Web links facet");
		locator.setProperty(Locator.LOCATOR_TITLE,"Web links");
		locator.setProperty(FACET_HANDLER_CLASS,WebsetHandler.class.getName());
		if(entityKey$!=null)
			locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
		if(entihome$!=null)
			locator.setProperty(Entigrator.ENTIHOME,entihome$);
		 String icon$=Support.readHandlerIcon(JEntitiesPanel.class, "globe.png");
		    if(icon$!=null)
		    	locator.setProperty(Locator.LOCATOR_ICON,icon$);
		    if(entihome$!=null)   
	 	locator.setProperty(Locator.LOCATOR_CHECKABLE,Locator.LOCATOR_TRUE);
		return Locator.toString(locator);
	}
	/**
	 * No action.
	 */
	@Override
	
	public String getFacetRenderer() {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * Remove the facet from the entity.
	 */
	@Override
	public void removeFacet() {
		try{
		    Entigrator entigrator=console.getEntigrator(entihome$);
			Sack entity=entigrator.getEntityAtKey(entityKey$);
			entity.removeElementItem("fhandler", WebsetHandler.class.getName());
			 entity.removeElementItem("jfacet", WebsetHandler.class.getName());
			 entity.removeElement("web");
			 entity.removeElement("web.icon");
			 entity.removeElement("web.login");
			entigrator.save(entity);
			entigrator.ent_takeOffProperty(entity, "webset");
		}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
		}
		
	}
	/**
	 * Display the facet context.
	 * @param console the main console.
	 * @param locator$ the locator string.
	 */
	@Override
	public void openFacet(JMainConsole console, String locator$) {
		try{
			Properties locator=Locator.toProperties(locator$);
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			
			JWeblinksPanel weblinksPanel=new JWeblinksPanel();
			String wlLocator$=weblinksPanel.getLocator();
			wlLocator$=Locator.append(wlLocator$, Entigrator.ENTIHOME, entihome$);
			wlLocator$=Locator.append(wlLocator$, EntityHandler.ENTITY_KEY, entityKey$);
			wlLocator$=Locator.append(wlLocator$, BaseHandler.HANDLER_METHOD,"instantiate");
			JConsoleHandler.execute(console, wlLocator$);
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		}
	}

	/**
	 * Get children nodes of the facet node for the digest view.
	 * @return the children nodes of the facet node.
	 */
	@Override
	public DefaultMutableTreeNode[] getDigest() {
		try{
	//		System.out.println("JWebsetFacetOpenItem:getDigest:locator="+locator$);
			Properties locator=Locator.toProperties(locator$);
			entihome$=locator.getProperty(Entigrator.ENTIHOME);
			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			Entigrator entigrator=console.getEntigrator(entihome$);
			Sack entity=entigrator.getEntityAtKey(entityKey$);
			Core[]ca=entity.elementGet("web");
			if(ca==null)
				return null;
			DefaultMutableTreeNode nameNode;
			DefaultMutableTreeNode valueNode;
			String locator$=getLocator();
			String nameLocator$;
			String valueLocator$;
			String nameIcon$;//=icon$=Support.readHandlerIcon(JEntitiesPanel.class, "text.png");
			String valueIcon$=Support.readHandlerIcon(JEntitiesPanel.class, "equal.png");
			ArrayList<DefaultMutableTreeNode>nl=new ArrayList<DefaultMutableTreeNode>();
			for(Core aCa:ca){
				nameNode=new DefaultMutableTreeNode();
				nameLocator$=Locator.append(locator$, Locator.LOCATOR_TITLE,aCa.type);
				nameLocator$=Locator.append(nameLocator$, Locator.LOCATOR_TYPE,JWeblinksPanel.WEB_LINK_NAME);
				nameLocator$=Locator.append(nameLocator$,JWeblinksPanel.WEB_LINK_NAME,aCa.type);
				nameLocator$=Locator.append(nameLocator$,JWeblinksPanel.WEB_LINK_KEY,aCa.name);
				nameIcon$=entity.getElementItemAt("web.icon", aCa.name);
				if(nameIcon$!=null)
				 nameLocator$=Locator.append(nameLocator$,Locator.LOCATOR_ICON,nameIcon$);
				nameLocator$=Locator.append(nameLocator$,JEntityDigestDisplay.NODE_TYPE,NODE_TYPE_WEB_NAME);
				if(entihome$!=null)
					nameLocator$=Locator.append(nameLocator$,Entigrator.ENTIHOME,entihome$);
				nameLocator$=Locator.append(nameLocator$,JEntityDigestDisplay.NODE_TYPE,NODE_TYPE_WEB_ADDRESS);
				if(entityKey$!=null)
					nameLocator$=Locator.append(nameLocator$,EntityHandler.ENTITY_KEY,entityKey$);
			
				nameNode.setUserObject(nameLocator$);
				valueNode=new DefaultMutableTreeNode();
				valueLocator$=Locator.append(locator$, Locator.LOCATOR_TITLE,aCa.value);
				valueLocator$=Locator.append(valueLocator$,JWeblinksPanel.WEB_LINK_NAME,aCa.type);
				valueLocator$=Locator.append(valueLocator$,JWeblinksPanel.WEB_LINK_URL,aCa.value);
				valueLocator$=Locator.append(valueLocator$,JWeblinksPanel.WEB_LINK_KEY,aCa.name);
				valueLocator$=Locator.append(valueLocator$, Locator.LOCATOR_TYPE,LOCATOR_TYPE_WEB_ADDRESS);
				valueLocator$=Locator.append(valueLocator$,Locator.LOCATOR_ICON,valueIcon$);
				valueLocator$=Locator.append(valueLocator$,JEntityDigestDisplay.NODE_TYPE,NODE_TYPE_WEB_ADDRESS);
				if(entihome$!=null)
					valueLocator$=Locator.append(valueLocator$,Entigrator.ENTIHOME,entihome$);
				valueLocator$=Locator.append(valueLocator$,JEntityDigestDisplay.NODE_TYPE,NODE_TYPE_WEB_ADDRESS);
				if(entityKey$!=null)
					valueLocator$=Locator.append(valueLocator$,EntityHandler.ENTITY_KEY,entityKey$);
					
				valueNode.setUserObject(valueLocator$);
//				System.out.println("JFieldsFacetOpenItem:getDigest:VALUE locator="+valueLocator$);
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
		return new WebsetHandler();
	}
	/**
	 * Get the popup menu for the child node of the facet node 
	 * in the digest view.
	 * @return the popup menu.	
	 */
	@Override
	public JPopupMenu getPopupMenu(final String digestLocator$) {
		JPopupMenu	popup = new JPopupMenu();
		JMenuItem openItem=new JMenuItem("Open");
		   popup.add(openItem);
		   openItem.setHorizontalTextPosition(JMenuItem.RIGHT);
		   openItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
				   try{
				//	  System.out.println("JWebsetFacetOpenItem:open:digest locator="+digestLocator$); 
					   Properties locator=Locator.toProperties(digestLocator$);
					   String encodedSelection$=locator.getProperty(JEntityDigestDisplay.SELECTION);
					   byte[]ba=Base64.decodeBase64(encodedSelection$);
					   String selection$=new String(ba,"UTF-8");
				//	   System.out.println("JWebsetFacetOpenItem:open:selection="+selection$);
					   locator=Locator.toProperties(selection$);
					   String entihome$=locator.getProperty(Entigrator.ENTIHOME);
					   String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
					   String type$=locator.getProperty(Locator.LOCATOR_TYPE);
					   if(LOCATOR_TYPE_WEB_ADDRESS.equals(type$)){
						   try{
								String url$=locator.getProperty(JWeblinksPanel.WEB_LINK_URL);
//								System.out.println("weblinkEditor:browseUrl:url="+url$);
								Desktop.getDesktop().browse(new URI(url$));
								}catch(Exception ee){
									Logger.getLogger(JFileOpenItem.class.getName()).info(ee.toString());
								}
						   return;
					   }
					   if(JEntityDigestDisplay.LOCATOR_FACET_COMPONENT.equals(type$)){
						  // String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
						   JWeblinkEditor we=new JWeblinkEditor();
						   String weLocator$=we.getLocator();
						   weLocator$=Locator.append(weLocator$, Entigrator.ENTIHOME, entihome$);
						   weLocator$=Locator.append(weLocator$, EntityHandler.ENTITY_KEY, entityKey$);
						   //JConsoleHandler.execute(console,beLocator$);
						   return;
					   }
					   String weblinkKey$=locator.getProperty(JWeblinksPanel.WEB_LINK_KEY);
					   JWeblinkEditor we=new JWeblinkEditor();
					   String weLocator$=we.getLocator();
					   weLocator$=Locator.append(weLocator$, Entigrator.ENTIHOME, entihome$);
					   weLocator$=Locator.append(weLocator$, EntityHandler.ENTITY_KEY, entityKey$);
					   weLocator$=Locator.append(weLocator$,JWeblinksPanel.WEB_LINK_KEY,weblinkKey$);
					   /*
					   Entigrator entigrator=console.getEntigrator(entihome$);
					   String componentKey$=locator.getProperty(JEntityDigestDisplay.COMPONENT_KEY);
					   Sack entity=entigrator.getEntityAtKey(componentKey$);
					   Core weblink=entity.getElementItem("web", weblinkKey$);
					   */
//					   System.out.println("JBookmarkFacetOpenItem:open:selection="+selection$);
					   JConsoleHandler.execute(console, weLocator$);
				   }catch(Exception ee){
					   Logger.getLogger(JWebsetFacetOpenItem.class.getName()).info(ee.toString());
				   }
				}
			    });
		return popup;
	}

}
