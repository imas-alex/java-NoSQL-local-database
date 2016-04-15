package gdt.jgui.entity;
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

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.apache.commons.codec.binary.Base64;

import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.FacetHandler;
import gdt.data.entity.facet.ExtensionHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JFacetOpenItem;
import gdt.jgui.console.JFacetRenderer;
import gdt.jgui.console.JItemPanel;
import gdt.jgui.console.JItemsListPanel;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.tool.JEntityEditor;
import gdt.jgui.tool.JIconSelector;
import gdt.jgui.tool.JTextEditor;
/**
 * Displays common entity actions.
 * @author imasa
 *
 */
public class JEntityPrimaryMenu extends JItemsListPanel implements JRequester{
	private static final long serialVersionUID = 1L;
	private String entihome$;
    private String entityKey$;
    private String entityLabel$;
    String requesterResponseLocator$;
    private Logger LOGGER=Logger.getLogger(JEntityPrimaryMenu.class.getName());
    public final static String ACTION_RENAME="action rename";
    public final static String ACTION_ARCHIVE="action arvhive";
    public final static String ACTION_SET_ICON="action set icon";
    public final static String ACTION_COPY="action copy";
    public final static String ACTION_CLONE="action clone";
    public final static String ORIGIN_KEY="origin key";
    private JMenu menu;
    private JMenuItem[] mia;
/**
 * Get context menu.
 * @return the context menu.
 */
    @Override
	
public JMenu getContextMenu() {
		menu=new JMenu("Context");
		menu.addMenuListener(new MenuListener(){
			@Override
			public void menuSelected(MenuEvent e) {
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
				if(hasToPaste()){
				JMenuItem pasteItem = new JMenuItem("Paste components");
				pasteItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						pasteComponents();
					}
				} );
				menu.add(pasteItem);
				}
			}

			@Override
			public void menuDeselected(MenuEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void menuCanceled(MenuEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
			
		return menu;
	}
	/**
	 * Get the context locator.
	 * @return the context locator.
	 */
    @Override
	public String getLocator() {
		Properties locator=new Properties();
	    locator.setProperty(Locator.LOCATOR_TYPE, JContext.CONTEXT_TYPE);
	    locator.setProperty(JContext.CONTEXT_TYPE,getType());
	    if(entihome$!=null)
	       locator.setProperty(Entigrator.ENTIHOME,entihome$);
	    if(entityKey$!=null)
		       locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
	    if(entityLabel$!=null)
		       locator.setProperty(EntityHandler.ENTITY_LABEL,entityLabel$);
	    String icon$=Support.readHandlerIcon(JEntityPrimaryMenu.class, "entity.png");
	    if(icon$!=null)
	    	locator.setProperty(Locator.LOCATOR_ICON,icon$);
	    if(entityLabel$!=null)
	    locator.setProperty(Locator.LOCATOR_TITLE, getTitle());
	   locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
	   locator.setProperty(BaseHandler.HANDLER_CLASS,JEntityPrimaryMenu.class.getName());
	//  System.out.println("EntityPrimaryMenul:getLocator:locator="+Locator.toString(locator));
	   return Locator.toString(locator);
	}
/**
 * Create the primary menu panel.
 * @param console the main console
 * @param locator$ the locator string.
 */
	@Override
	public JContext instantiate(JMainConsole console, String locator$) {
//		System.out.println("EntityPrimaryMenul:instantiate:locator:"+Locator.remove(locator$, Locator.LOCATOR_ICON));
		try{
		this.console=console;
		 Properties locator=Locator.toProperties(locator$);
		 entihome$=locator.getProperty(Entigrator.ENTIHOME);
		 entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		 entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
		 requesterResponseLocator$=locator.getProperty(JRequester.REQUESTER_RESPONSE_LOCATOR);
		 Entigrator entigrator=console.getEntigrator(entihome$);
		 Sack entity=entigrator.getEntityAtKey(entityKey$);
		 if(entity==null)
			 return null;
		 ArrayList<JItemPanel>ipl=new ArrayList<JItemPanel>();
		 String actionLocator$=getRenameLocator();
		 JItemPanel renameItem=new JItemPanel(console, actionLocator$);
		 ipl.add(renameItem);
		 actionLocator$=getSetIconLocator();
		 JItemPanel setIconItem=new JItemPanel(console, actionLocator$);
		 ipl.add(setIconItem);
		 actionLocator$=getViewLocator();
		 JItemPanel viewItem=new JItemPanel(console, actionLocator$);
		 ipl.add(viewItem);
		 actionLocator$=getEntityEditorLocator();
		 JItemPanel editItem=new JItemPanel(console, actionLocator$);
		 ipl.add(editItem);
		 actionLocator$=getCopyLocator();
		 JItemPanel copyItem=new JItemPanel(console, actionLocator$);
		 ipl.add(copyItem);
		  actionLocator$=getCloneLocator();
		  JItemPanel cloneItem=new JItemPanel(console, actionLocator$);
		  ipl.add(cloneItem);
		  
		  actionLocator$=getDeleteLocator();
		  JItemPanel deleteItem=new JItemPanel(console, actionLocator$);
		  ipl.add(deleteItem);
		  
		  actionLocator$=getArchiveLocator();
		  JItemPanel archiveItem=new JItemPanel(console, actionLocator$);
		  ipl.add(archiveItem);
		
		  actionLocator$=getReindexLocator();
		  JItemPanel reindexItem=new JItemPanel(console, actionLocator$);
		  ipl.add(reindexItem);
		  if(hasComponents()){
		  actionLocator$=getComponentsLocator();
		  JItemPanel componentsItem=new JItemPanel(console, actionLocator$);
		  ipl.add(componentsItem);
		  }
		  if(hasContainers()){
			  actionLocator$=getContainersLocator();
			  JItemPanel containersItem=new JItemPanel(console, actionLocator$);
			  ipl.add(containersItem);
			  }
			putItems(ipl.toArray(new JItemPanel[0]));
		//	System.out.println("EntityPrimaryMenul:instantiate:END");
		 return this;
		}catch(Exception e){
			LOGGER.severe(e.toString());
			return null;
		}
	}
	/**
	 *Get the title
	 *@return the title .
	 */
	@Override
	public String getTitle() {
		return "Entity services";
	}
	/**
	 *Get the context type
	 *@return the context type .
	 */
	@Override
	public String getType() {
		return "Entity primary menu";
	}

private String getRenameLocator() {
	// System.out.println("EntityPrimaryMenul:getRenameLocator:BEGIN");
	        try{
	        	if(entityLabel$==null&&entihome$!=null){
	    			 Entigrator entigrator=console.getEntigrator(entihome$);
	    			Sack entity=entigrator.getEntityAtKey(entityKey$);
	    			entityLabel$=entity.getProperty("label");
	    			}
	        	JTextEditor textEditor=new JTextEditor();
				String locator$=textEditor.getLocator();
				locator$=Locator.append(locator$, JTextEditor.TEXT, entityLabel$);
				locator$=Locator.append(locator$, Locator.LOCATOR_TITLE, "Rename");
				String icon$=Support.readHandlerIcon(JEntityPrimaryMenu.class, "refresh.png");
				locator$=Locator.append(locator$, Locator.LOCATOR_ICON,icon$);
			
				String responseLocator$=getLocator();
				responseLocator$=Locator.append(responseLocator$, JRequester.REQUESTER_ACTION, ACTION_RENAME);
				responseLocator$=Locator.append(responseLocator$, BaseHandler.HANDLER_METHOD,"response");
				String requesterResponseLocator$=Locator.compressText(responseLocator$);
				locator$=Locator.append(locator$, JRequester.REQUESTER_RESPONSE_LOCATOR, requesterResponseLocator$);
				return locator$;
			}catch(Exception ee){
				LOGGER.severe(ee.toString());
				return null;
			}
		}
private String getArchiveLocator() {
		        try{
	        	if(entityLabel$==null&&entihome$!=null){
	    			 Entigrator entigrator=console.getEntigrator(entihome$);
	    			Sack entity=entigrator.getEntityAtKey(entityKey$);
	    			entityLabel$=entity.getProperty("label");
	    			}
	        	JArchivePanel archivePanel=new JArchivePanel();
	        	String locator$=archivePanel.getLocator();
	        	if(entihome$!=null)
	        	locator$=Locator.append(locator$,Entigrator.ENTIHOME,entihome$);
	        	if(entityKey$!=null)
		        	locator$=Locator.append(locator$,EntityHandler.ENTITY_KEY,entityKey$);
	        	if(entityLabel$!=null)
		        	locator$=Locator.append(locator$,EntityHandler.ENTITY_LABEL,entityLabel$);
	        	String icon$=Support.readHandlerIcon(JEntityPrimaryMenu.class, "archive.png");
				locator$=Locator.append(locator$, Locator.LOCATOR_ICON,icon$);
				//String responseLocator$=getLocator();
				String responseLocator$=locator$;
				responseLocator$=Locator.append(responseLocator$, JRequester.REQUESTER_ACTION, ACTION_ARCHIVE);
				responseLocator$=Locator.append(responseLocator$, BaseHandler.HANDLER_METHOD,"response");
				String requesterResponseLocator$=Locator.compressText(responseLocator$);
				locator$=Locator.append(locator$, JRequester.REQUESTER_RESPONSE_LOCATOR, requesterResponseLocator$);
				return locator$;
			}catch(Exception ee){
				LOGGER.severe(ee.toString());
				return null;
			}
		}
private String getSetIconLocator() {
	try{
	if(entityLabel$==null&&entihome$!=null){
	Entigrator entigrator=console.getEntigrator(entihome$);
	Sack entity=entigrator.getEntityAtKey(entityKey$);
	entityLabel$=entity.getProperty("label");
	}
	JIconSelector iconSelector=new JIconSelector();
    String isLocator$=iconSelector.getLocator();
	isLocator$=Locator.append(isLocator$ ,BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
	if(entihome$!=null)
	isLocator$=Locator.append(isLocator$,Entigrator.ENTIHOME,entihome$);
	if(entityKey$!=null)
	isLocator$=Locator.append(isLocator$,EntityHandler.ENTITY_KEY,entityKey$);
	if(entityLabel$!=null)
	isLocator$=Locator.append(isLocator$,EntityHandler.ENTITY_LABEL,entityLabel$);
	String responseLocator$=getLocator();
	responseLocator$=Locator.append(responseLocator$ ,JRequester.REQUESTER_ACTION,ACTION_SET_ICON);
	responseLocator$=Locator.append(responseLocator$ ,BaseHandler.HANDLER_METHOD,"response");
	isLocator$=Locator.append(isLocator$, Locator.LOCATOR_TITLE,"Set icon");
	isLocator$=Locator.append(isLocator$ ,JRequester.REQUESTER_RESPONSE_LOCATOR,Locator.compressText(responseLocator$));
	return isLocator$;
	}catch(Exception ee){
		LOGGER.severe(ee.toString());
		return null;
	}
}
private String getEntityEditorLocator() {
	try{
	if(entityLabel$==null&&entihome$!=null){
	Entigrator entigrator=console.getEntigrator(entihome$);
	Sack entity=entigrator.getEntityAtKey(entityKey$);
	entityLabel$=entity.getProperty("label");
	}
	JEntityEditor editor=new JEntityEditor();
	String locator$=editor.getLocator();
	locator$=Locator.append(locator$ ,BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
	if(entihome$!=null)
	locator$=Locator.append(locator$,Entigrator.ENTIHOME,entihome$);
	if(entityKey$!=null)
	locator$=Locator.append(locator$,EntityHandler.ENTITY_KEY,entityKey$);
	if(entityLabel$!=null)
	locator$=Locator.append(locator$,EntityHandler.ENTITY_LABEL,entityLabel$);
	locator$=Locator.append(locator$, Locator.LOCATOR_TITLE,"Edit");
	locator$=Locator.append(locator$,EntityHandler.ENTITY_ACTION,JEntityEditor.ENTITY_EDIT);
	locator$=Locator.append(locator$,BaseHandler.HANDLER_CLASS,JEntityEditor.class.getName());
	 String icon$=Support.readHandlerIcon(JEntityPrimaryMenu.class, "edit.png");
	locator$=Locator.append(locator$, Locator.LOCATOR_ICON,icon$);
	return locator$;
	}catch(Exception ee){
		LOGGER.severe(ee.toString());
		return null;
	}
}
private String getCopyLocator() {
	try{
		Entigrator entigrator=console.getEntigrator(entihome$);
		String entityLocator$=EntityHandler.getEntityLocatorAtKey(entigrator, entityKey$);
		entityLocator$=Locator.append(entityLocator$,BaseHandler.HANDLER_CLASS,getClass().getName());
		entityLocator$=Locator.append(entityLocator$,BaseHandler.HANDLER_METHOD,"response");
		entityLocator$=Locator.append(entityLocator$,BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
		entityLocator$=Locator.append(entityLocator$,JRequester.REQUESTER_ACTION,ACTION_COPY);
		String icon$=Support.readHandlerIcon(JEntityPrimaryMenu.class, "copy.png");
		entityLocator$= Locator.append(entityLocator$,Locator.LOCATOR_ICON,icon$);
		entityLocator$=Locator.append(entityLocator$,Locator.LOCATOR_TITLE,"Copy");
		return entityLocator$;
		}catch(Exception ee){
			LOGGER.severe(ee.toString());
			return null;
		}
	
}
private String getCloneLocator() {
	try{
		Entigrator entigrator=console.getEntigrator(entihome$);
		String entityLocator$=EntityHandler.getEntityLocatorAtKey(entigrator, entityKey$);
		entityLocator$=Locator.append(entityLocator$,BaseHandler.HANDLER_CLASS,getClass().getName());
		entityLocator$=Locator.append(entityLocator$,BaseHandler.HANDLER_METHOD,"response");
		entityLocator$=Locator.append(entityLocator$,BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
		entityLocator$=Locator.append(entityLocator$,JRequester.REQUESTER_ACTION,ACTION_CLONE);
		String icon$=Support.readHandlerIcon(JEntityPrimaryMenu.class, "clone.png");
		entityLocator$= Locator.append(entityLocator$,Locator.LOCATOR_ICON,icon$);
		entityLocator$=Locator.append(entityLocator$,Locator.LOCATOR_TITLE,"Clone");
		return entityLocator$;
		
	}catch(Exception ee){
			LOGGER.severe(ee.toString());
			return null;
		}
	
}
private String getDeleteLocator() {
	try{
		Properties locator=new Properties();
		locator.setProperty(Entigrator.ENTIHOME, entihome$);
		locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
		locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
		locator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
		locator.setProperty( BaseHandler.HANDLER_METHOD, "deleteEntity");
		 String icon$=Support.readHandlerIcon(JEntityPrimaryMenu.class, "delete.png");
		 locator.setProperty(Locator.LOCATOR_ICON,icon$);
		 locator.setProperty(Locator.LOCATOR_TITLE,"Delete");
		return Locator.toString(locator);
		}catch(Exception ee){
			LOGGER.severe(ee.toString());
			return null;
		}
	
}

private String getReindexLocator() {
	try{
		//System.out.println("EntityPrimaryMenul:getReindexLocator:BEGIN");
		Properties locator=new Properties();
		locator.setProperty(Entigrator.ENTIHOME, entihome$);
		locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
		locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
		locator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
		locator.setProperty( BaseHandler.HANDLER_METHOD, "reindexEntity");
		 String icon$=Support.readHandlerIcon(JEntityPrimaryMenu.class, "wrench.png");
		 locator.setProperty(Locator.LOCATOR_ICON,icon$);
		 locator.setProperty(Locator.LOCATOR_TITLE,"Reindex");
		return Locator.toString(locator);
		}catch(Exception ee){
			LOGGER.severe(ee.toString());
			return null;
		}
	
}
private String getViewLocator() {
	 //System.out.println("EntityPrimaryMenul:getViewLocator:BEGIN");
	        try{
	        	JEntitiesPanel ep=new JEntitiesPanel();
	        	String locator$=ep.getLocator();
	        	locator$=Locator.append(locator$, Entigrator.ENTIHOME, entihome$);
	        	locator$=Locator.append(locator$, EntityHandler.ENTITY_LIST, entityLabel$);
	        	 String icon$=Support.readHandlerIcon(JEntityPrimaryMenu.class, "eye.png");
	        	locator$=Locator.append(locator$, Locator.LOCATOR_ICON,icon$);
	        	locator$=Locator.append(locator$, Locator.LOCATOR_TITLE,"View");
	        	return locator$;
	        }catch(Exception ee){
	    		LOGGER.severe(ee.toString());
	    		return null;
	    	}
		}
private String getComponentsLocator() {
	try{
		Properties locator=new Properties();
		locator.setProperty(Entigrator.ENTIHOME, entihome$);
		locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
		locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
		locator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
		locator.setProperty( BaseHandler.HANDLER_METHOD, "showComponents");
		 String icon$=Support.readHandlerIcon(JEntityPrimaryMenu.class, "clip.png");
		 locator.setProperty(Locator.LOCATOR_ICON,icon$);
		 locator.setProperty(Locator.LOCATOR_TITLE,"Show components");
		return Locator.toString(locator);
		}catch(Exception ee){
			LOGGER.severe(ee.toString());
			return null;
		}
}
private String getContainersLocator() {
	try{
		Properties locator=new Properties();
		locator.setProperty(Entigrator.ENTIHOME, entihome$);
		locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
		locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
		locator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
		locator.setProperty( BaseHandler.HANDLER_METHOD, "showContainers");
		 String icon$=Support.readHandlerIcon(JEntityPrimaryMenu.class, "box.png");
		 locator.setProperty(Locator.LOCATOR_ICON,icon$);
		 locator.setProperty(Locator.LOCATOR_TITLE,"Show containers");
		return Locator.toString(locator);
		}catch(Exception ee){
			LOGGER.severe(ee.toString());
			return null;
		}
}
private String adaptLocator(String locator$){
	if(locator$==null)
		return null;
	String aLocator$=Locator.append(locator$,Locator.LOCATOR_TYPE , JContext.CONTEXT_TYPE);
	aLocator$=Locator.remove(aLocator$,EntityHandler.ENTITY_ACTION);
	aLocator$=Locator.remove(aLocator$,JRequester.REQUESTER_ACTION);
	aLocator$=Locator.append(aLocator$,JContext.CONTEXT_TYPE , getType());
	aLocator$=Locator.append(aLocator$,JContext.CONTEXT_TYPE , getType());
	String icon$=Support.readHandlerIcon(JEntityPrimaryMenu.class, "entity.png");
    aLocator$=Locator.append(aLocator$,Locator.LOCATOR_ICON,icon$);
    aLocator$=Locator.append(aLocator$,Locator.LOCATOR_TITLE, getTitle());
    aLocator$=Locator.append(aLocator$,BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
    aLocator$=Locator.append(aLocator$,BaseHandler.HANDLER_CLASS,JEntityPrimaryMenu.class.getName());
    return aLocator$;
}
private void cloneEntity(JMainConsole console,String locator$){
	  try{
		  Properties locator=Locator.toProperties(locator$);
		  entihome$=locator.getProperty(Entigrator.ENTIHOME);
		  Entigrator entigrator=console.getEntigrator(entihome$);
		  entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		  //System.out.println("EntityPrimaryMenu:clone entity="+entityKey$);
		  Sack origin=entigrator.getEntityAtKey(entityKey$);
		  Sack clone= entigrator.ent_clone(origin);
     	  //clone.print();
		  Core[] ca=clone.elementGet("fhandler");
     	  
         JFacetOpenItem foi;
         JFacetRenderer fr;
         String rendererClass$;
         FacetHandler fah;
         locator$=Locator.append(locator$, EntityHandler.ENTITY_KEY, clone.getKey());
         locator$=Locator.append(locator$, ORIGIN_KEY, entityKey$);
         if(ca!=null)
         	for(Core fh:ca){
         		try{
         		System.out.println("EntityPrimaryMenu:adapt clone:handler="+fh.name+" renderer="+fh.value);	
         		rendererClass$=clone.getElementItemAt("jfacet", fh.name);
         		if(rendererClass$!=null){
//         			System.out.println("EntityPrimaryMenu:adapt clone:handler="+fh.name+" renderer="+rendererClass$);		
         		foi=(JFacetOpenItem)JConsoleHandler.getHandlerInstance(entigrator, rendererClass$);
         		String fr$=foi.getFacetRenderer();
         		fr=(JFacetRenderer)JConsoleHandler.getHandlerInstance(entigrator, fr$);
         		fr.adaptClone(console, locator$);
         		}else{
         		fah=(FacetHandler)JConsoleHandler.getHandlerInstance(entigrator, fh.name);
         		fah.instantiate(locator$);
         		fah.adaptClone(entigrator);
         		}
         		}catch(Exception ee){
         			Logger.getLogger(ExtensionHandler.class.getName()).info(ee.toString());
         		}
         	}
     	  JEntityFacetPanel fp=new JEntityFacetPanel();
			String fpLocator$=fp.getLocator();
			fpLocator$=Locator.append(fpLocator$,Entigrator.ENTIHOME,entihome$);
			fpLocator$=Locator.append(fpLocator$,EntityHandler.ENTITY_KEY,clone.getKey());
			fpLocator$=Locator.append(fpLocator$,EntityHandler.ENTITY_LABEL,clone.getProperty("label"));
			JConsoleHandler.execute(console, fpLocator$);
	  }catch(Exception e){
		  LOGGER.severe(e.toString());
	  }
	}
/**
 * Rebuild entity index.
 * @param console the main console.
 * @param locator$ the locator string.
 */
public static void reindexEntity(JMainConsole console,String locator$){
	  try{
		  Properties locator=Locator.toProperties(locator$);
		  String entihome$=locator.getProperty(Entigrator.ENTIHOME);
		  Entigrator entigrator=console.getEntigrator(entihome$);
		  String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		  Sack entity=entigrator.getEntityAtKey(entityKey$);
		  entity=entigrator.ent_reindex(entity);
		  FacetHandler[] fha=BaseHandler.listAllHandlers(entigrator);
	//	  System.out.println("EntityPrimaryMenu:reindexEntity:fha="+fha.length);
		  Core[] ca;
		  JFacetRenderer facetRenderer;
			for(FacetHandler fh:fha){
				try{
				fh.instantiate(locator$);
				if(fh.isApplied(entigrator, locator$)){
		        	entity=entigrator.get(entity);
		        	ca=entity.elementGet("fhandler");
		   		    if(ca!=null){
		   			  for(Core aCa:ca){
		  				  facetRenderer=JConsoleHandler.getFacetRenderer(entigrator, aCa.name);
		   				  if(facetRenderer!=null){
		   					  facetRenderer.reindex(console,entigrator ,entity);
		   				  }
		   				  //else
		   				//	System.out.println("EntityPrimaryMenu:reindexEntity:cannot get renderer="+aCa.value);
		   			  }
		   		  }
		        }
			}catch(Exception ee){
				Logger.getLogger(JEntityPrimaryMenu.class.getName()).info(ee.toString());
			}
			}
			entigrator.save(entity);
	  }catch(Exception e){
		  Logger.getLogger(JEntityPrimaryMenu.class.getName()).severe(e.toString());
	  }
	}
/**
 * Display a list of components
 * @param console the main console.
 * @param locator$ the locator string.
 */
public static void showComponents(JMainConsole console,String locator$){
	  try{
		  Properties locator=Locator.toProperties(locator$);
		  String entihome$=locator.getProperty(Entigrator.ENTIHOME);
		  Entigrator entigrator=console.getEntigrator(entihome$);
		  String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		  Sack entity=entigrator.getEntityAtKey(entityKey$);
		  String list$=null;
		  String[] sa=entigrator.ent_listComponents(entity);
		 if(sa!=null){
		  String[] la=entigrator.indx_getLabels(sa);
      	 list$=Locator.toString(la);
		 }
      	JEntitiesPanel ep=new JEntitiesPanel();
      	String epLocator$=ep.getLocator();
      	epLocator$=Locator.append(epLocator$, Entigrator.ENTIHOME, entihome$);
      	epLocator$=Locator.append(epLocator$, EntityHandler.ENTITY_KEY, entityKey$);
      	epLocator$=Locator.append(epLocator$, EntityHandler.ENTITY_CONTAINER, entityKey$);
      	if(list$!=null)
      	 epLocator$=Locator.append(epLocator$, EntityHandler.ENTITY_LIST, list$);
      	JConsoleHandler.execute(console, epLocator$);
	  }catch(Exception e){
		  Logger.getLogger(JEntityPrimaryMenu.class.getName()).severe(e.toString());
	  }
	} 
private boolean hasComponents(){
	 try{
		 Entigrator entigrator=console.getEntigrator(entihome$);
		  Sack entity=entigrator.getEntityAtKey(entityKey$);
		  String[] sa=entigrator.ent_listComponents(entity);
		  if(sa!=null&&sa.length>0)
			  return true;
		  else
			  return false;
	 }catch(Exception e){
		  LOGGER.severe(e.toString());
		  return false;
	  }
}
/**
 * Display a list of containers
 * @param console the main console.
 * @param locator$ the locator string.
 */
public static void showContainers(JMainConsole console,String locator$){
	  try{
		  Properties locator=Locator.toProperties(locator$);
		  String entihome$=locator.getProperty(Entigrator.ENTIHOME);
		  Entigrator entigrator=console.getEntigrator(entihome$);
		  String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		  Sack entity=entigrator.getEntityAtKey(entityKey$);
		  String list$=null;
		  String[] sa=entigrator.ent_listContainers(entity);
		 if(sa!=null){
		  String[] la=entigrator.indx_getLabels(sa);
    	 list$=Locator.toString(la);
		 }
    	JEntitiesPanel ep=new JEntitiesPanel();
    	String epLocator$=ep.getLocator();
    	epLocator$=Locator.append(epLocator$, Entigrator.ENTIHOME, entihome$);
    	epLocator$=Locator.append(epLocator$, EntityHandler.ENTITY_KEY, entityKey$);
    	epLocator$=Locator.append(epLocator$, EntityHandler.ENTITY_COMPONENT, entityKey$);
    	if(list$!=null)
    	 epLocator$=Locator.append(epLocator$, EntityHandler.ENTITY_LIST, list$);
    	JConsoleHandler.execute(console, epLocator$);
	  }catch(Exception e){
		  Logger.getLogger(JEntityPrimaryMenu.class.getName()).severe(e.toString());
	  }
	} 
private boolean hasContainers(){
	 try{
		 Entigrator entigrator=console.getEntigrator(entihome$);
		  Sack entity=entigrator.getEntityAtKey(entityKey$);
		  String[] sa=entigrator.ent_listContainers(entity);
		  if(sa!=null&&sa.length>0)
			  return true;
		  else
			  return false;
					  
	 }catch(Exception e){
		  LOGGER.severe(e.toString());
		  return false;
	  }
}
/**
 * Check if there are entities in the clipboard to paste.
 * @param console the main console.
 * @param locator$ the locator string.
 * @return true if there are entities in the clipboard false otherwise. 
 */
public  static boolean hasToPaste(JMainConsole console,String locator$){
	try{
		Properties locator=Locator.toProperties(locator$);
		String entihome$=locator.getProperty(Entigrator.ENTIHOME);
		String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		String[]sa=console.clipboard.getContent();
		 if(sa==null||sa.length<1)
		    return false;
		 Entigrator entigrator=console.getEntigrator(entihome$);
		 Sack entity=entigrator.getEntityAtKey(entityKey$);
		 for(String aSa:sa){
			 try{
				 locator=Locator.toProperties(aSa);
				 if(!EntityHandler.ENTITY_TYPE.equals(locator.getProperty(Locator.LOCATOR_TYPE)))
					 continue;
				 if(!locator.getProperty(Entigrator.ENTIHOME).equals(entihome$))
					 continue;
				 String candidateKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
				 if(candidateKey$.equals(entityKey$))
					 continue;
				 Sack candidate=entigrator.getEntityAtKey(candidateKey$);
				 if(candidate==null)
					 continue;
			     if(entigrator.col_isComponentDown(entity, candidate))
			    	 continue;
				 return true;
			 }catch(Exception ee){
				 Logger.getLogger(JEntityPrimaryMenu.class.getName()).info(ee.toString());
			 }
		 }
		return false;
	}catch(Exception e){
		Logger.getLogger(JEntityPrimaryMenu.class.getName()).severe(e.toString());
		return false;
	}
}
private boolean hasToPaste(){
	return hasToPaste(console,getLocator());
}
private void pasteComponents(){
	String locator$= pasteComponents(console,getLocator());
	showComponents(console, locator$);
	
}
/**
 * Add entities from the clipboard as components. 
 * @param console the main console.
 * @param locator$ the locator string.
 * @return the container key.
 */
public static String pasteComponents(JMainConsole console,String locator$){
	try{
		Properties locator=Locator.toProperties(locator$);
		String entihome$=locator.getProperty(Entigrator.ENTIHOME);
		String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		String[]sa=console.clipboard.getContent();
		 if(sa==null||sa.length<1)
		    return locator$;
		 Entigrator entigrator=console.getEntigrator(entihome$);
		 Sack entity=entigrator.getEntityAtKey(entityKey$);
		 Sack candidate;
		 for(String aSa:sa){
			 try{
				 locator=Locator.toProperties(aSa);
				 if(!EntityHandler.ENTITY_TYPE.equals(locator.getProperty(Locator.LOCATOR_TYPE)))
					 continue;
				 if(!locator.getProperty(Entigrator.ENTIHOME).equals(entihome$))
					 continue;
				 String candidateKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
				 if(candidateKey$.equals(entityKey$))
					 continue;
				 candidate=entigrator.getEntityAtKey(candidateKey$);
				 if(candidate==null)
					 continue;
			     if(entigrator.col_isComponentDown(entity, candidate))
			    	 continue;
				 entigrator.col_addComponent(entity, candidate);
			 }catch(Exception ee){
				// System.out.println("EntityPrimaryMenu:hasToPaste:"+ee.toString());
				Logger.getLogger(JEntityPrimaryMenu.class.getName()).info(ee.toString());
			 }
		 }
		 
		 locator$=Locator.append(locator$,EntityHandler.ENTITY_CONTAINER, entityKey$);
	 }catch(Exception e){
		 Logger.getLogger(JEntityPrimaryMenu.class.getName()).severe(e.toString());
	  }
	return locator$;
}
/**
 * Execute the response locator.
 * @param the main console
 * @param locator$ the response locator.
 */
@Override
public void response(JMainConsole console, String locator$) {
//	System.out.println("JEntityPrimaryMenu:response:locator="+locator$);
	try{
		Properties locator=Locator.toProperties(locator$);
		String action$=locator.getProperty(JRequester.REQUESTER_ACTION);
		String entihome$=locator.getProperty(Entigrator.ENTIHOME);
		String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		Entigrator entigrator=console.getEntigrator(entihome$);
		Sack entity=entigrator.getEntityAtKey(entityKey$);
	//	System.out.println("EntityPrimaryMenu:response:action="+action$);
		if(ACTION_RENAME.equals(action$)){
			String text$=locator.getProperty(JTextEditor.TEXT);
	//		System.out.println("EntityPrimaryMenu:response:rename="+text$);
			if(text$!=null){
				entity=entigrator.ent_assignLabel(entity, text$);
				entityLabel$=entity.getProperty("label");
		//		System.out.println("EntityPrimaryMenu:response:label="+entityLabel$);
				locator$=Locator.append(locator$, EntityHandler.ENTITY_LABEL,entityLabel$);
			     adaptRename(entigrator,entity);
				JEntityPrimaryMenu pm=new JEntityPrimaryMenu();
			    pm.instantiate(console,locator$);
				JConsoleHandler.execute(console, pm.getLocator());
			}
			return;
		}
		if(ACTION_SET_ICON.equals(action$)){
			String icon$=locator.getProperty(JIconSelector.ICON);
		//	System.out.println("EntityPrimaryMenu:response:icon="+icon$);
			if(icon$!=null){
				entity.putAttribute(new Core(null,"icon",icon$));
				entigrator.save(entity);
			    String aLocator$=		adaptLocator(locator$);
				JConsoleHandler.execute(console, aLocator$);
				return;
			}
		}
			if(ACTION_ARCHIVE.equals(action$)){
				String text$=locator.getProperty(JTextEditor.TEXT);
//				System.out.println("EntityPrimaryMenu:response:arhive="+text$);
				if(text$!=null){
					String [] sa=JReferenceEntry.getCoalition(console, entigrator, new String[]{entityKey$});
							//getCoalition(console, entityLocator$);
					if(sa==null)
						System.out.println("EntityPrimaryMenu:response:archive:sa is null");
					else
						System.out.println("EntityPrimaryMenu:response:archive:sa="+sa.length);
					JArchivePanel jap=new JArchivePanel();
					String japLocator$=jap.getLocator();
					japLocator$=Locator.append(japLocator$, Entigrator.ENTIHOME, entihome$);
					japLocator$=Locator.append(japLocator$, EntityHandler.ENTITY_LIST,Locator.toString(sa));
					JConsoleHandler.execute(console, japLocator$);
				}
				return;
			}
			if(ACTION_COPY.equals(action$)){
				console.clipboard.clear();
				JEntityFacetPanel em=new JEntityFacetPanel();
				String emLocator$=em.getLocator();
				emLocator$=Locator.append(emLocator$, Entigrator.ENTIHOME, entihome$);
				emLocator$=Locator.append(emLocator$, EntityHandler.ENTITY_KEY,entityKey$);
				String entityLabel$=entigrator.indx_getLabel(entityKey$);
				emLocator$=Locator.append(emLocator$,Locator.LOCATOR_TITLE,entityLabel$);
				String icon$=entigrator.getEntityIcon(entityKey$);
				if(icon$!=null){
						String picture$=entigrator.readIconFromIcons(icon$);
						if(picture$!=null)
							emLocator$=Locator.append(emLocator$,Locator.LOCATOR_ICON,picture$);
				}
				console.clipboard.putString(emLocator$);
				return;
			}
			if(ACTION_CLONE.equals(action$)){
				System.out.println("EntityPrimaryMenu:response:action="+action$);
				String entityLocator$=EntityHandler.getEntityLocatorAtKey(entigrator,entityKey$);
				cloneEntity(console, entityLocator$);
				return;
			}
			return;
		
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
}
/**
 * Complete the context. No action.
 */
@Override
public void close() {
	// TODO Auto-generated method stub
	
}
/**
 * Get context subtitle.
 * @return the context subtitle.
 */
@Override
public String getSubtitle() {
	return entityLabel$;
}

private void adaptRename(Entigrator entigrator,Sack entity){
	try{
//		System.out.println("JEntityPrimaryMenu:adaptRename:BEGIN");
		String[]	sa=entity.elementListNoSorted("fhandler");
//		System.out.println("JEntityPrimaryMenu:adaptRename:1");
		locator$=getLocator();
        if(sa==null)
        	return;
	     JFacetRenderer facetRenderer;
	   	for(String s:sa){
			try{
				facetRenderer=JConsoleHandler.getFacetRenderer(entigrator, s);
				if(facetRenderer!=null)
			    	facetRenderer.adaptRename(console, locator$);
//		     	System.out.println("EntityFacetPanel:getFacetOpenItems:handler class="+aCa.value);
			}catch(Exception ee){
				Logger.getLogger(JEntityPrimaryMenu.class.getName()).info(s+":"+ee.toString());
			}
		}
     	
	}catch(Exception e){
		Logger.getLogger(JEntityPrimaryMenu.class.getName()).severe("e:"+e.toString());
	}
}
}

