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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
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
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.jgui.base.JBaseNavigator;
import gdt.jgui.base.JBasesPanel;
import gdt.jgui.base.JCategoryPanel;
import gdt.jgui.base.JDesignPanel;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JFacetRenderer;
import gdt.jgui.console.JItemPanel;
import gdt.jgui.console.JItemsListPanel;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.ReloadDialog;
import gdt.jgui.console.WContext;
import gdt.jgui.console.WUtils;
/**
 * Displays the list of entities.
 * @author imasa
 *
 */

public class JEntitiesPanel extends JItemsListPanel implements WContext {
	private static final long serialVersionUID = 1L;
	
	
	public static final String SELECTED="selected";
	private Logger LOGGER=Logger.getLogger(JEntitiesPanel.class.getName());
	protected String entihome$;
	protected String list$;
	protected String entityKey$;
	protected JMenuItem pasteItem;
	protected JMenuItem copyItem;
	protected JMenuItem reindexItem;
	protected JMenuItem deleteItem;
	protected JMenuItem archiveItem;
	protected JMenuItem removeComponentsItem; 
	protected JMenuItem removeContainersItem; 
	protected String requesterResponseLocator$;
	protected String containerKey$;
	protected String componentKey$;
	protected String saveId$;
	static boolean debug=false; 
	boolean ignoreOutdate=false;

/**
 * The default constructor.
 */
    public JEntitiesPanel (){
        super();
       
    }
    
  /**
   * Get context menu.
   * @return the context menu.
   * 
   */
	@Override
	public JMenu getContextMenu() {
		menu=super.getContextMenu();
		mia=null;
		 int cnt=menu.getItemCount();
		 if(cnt>0){
			 mia=new JMenuItem[cnt];
			for(int i=0;i<cnt;i++) 
			 mia[i]=menu.getItem(i);
		 }
		menu.addMenuListener(new MenuListener(){
			@Override
			public void menuSelected(MenuEvent e) {
				try{
				menu.removeAll();
				}catch(Exception ee){
					System.out.println("JEntitiesPanel:getConextMenu:"+ee.toString());
				}
				if(mia!=null)
					 for(JMenuItem mi:mia)
					try{
			  			 if(mi!=null) 
			  			 menu.add(mi);
			  			}catch(Exception ee){
			  				 System.out.println("JEntitiesPanel:getConextMenu:"+ee.toString());
			  			}
			Properties locator=Locator.toProperties(locator$);
				if(locator.getProperty(EntityHandler.ENTITY_CONTAINER)!=null){
				if(JEntityPrimaryMenu.hasToPaste(console, locator$)){
					pasteItem = new JMenuItem("Paste components");
					pasteItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							pasteComponents();
						}
					} );
					menu.add(pasteItem);
				}
				if(hasSelectedItems()){
				  if(containerKey$!=null){	
					removeComponentsItem = new JMenuItem("Remove components");
					removeComponentsItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							removeComponents();
						}
					} );
					menu.add(removeComponentsItem);
				  }
				}
				}
				if(locator.getProperty(EntityHandler.ENTITY_COMPONENT)!=null){
					 if(componentKey$!=null&&JEntitiesPanel.this.hasSelectedItems()){	
							removeContainersItem = new JMenuItem("Remove containers");
							removeContainersItem.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									removeContainers();
								}
							} );
							menu.add(removeContainersItem);
						  }
				}
				if(hasSelectedItems()){
					menu.addSeparator();
					copyItem = new JMenuItem("Copy");
					copyItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							JItemPanel[] ipa=JEntitiesPanel.this.getItems();
							ArrayList<String>sl=new ArrayList<String>();
							for(JItemPanel ip:ipa)
								if(ip.isChecked())
									sl.add(ip.getLocator());
							String[]sa=sl.toArray(new String[0]);
							console.clipboard.clear();
							for(String aSa:sa)
								console.clipboard.putString(aSa);
						}
					} );
					menu.add(copyItem);
					reindexItem = new JMenuItem("Reindex");
					reindexItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							JItemPanel[] ipa=JEntitiesPanel.this.getItems();
							for(JItemPanel ip:ipa)
								if(ip.isChecked()){
								    JEntityPrimaryMenu.reindexEntity(console, ip.getLocator());
								}
						}
					} );
					menu.add(reindexItem);
					archiveItem = new JMenuItem("Archive");
					archiveItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							JItemPanel[] ipa=JEntitiesPanel.this.getItems();
							Entigrator entigrator=console.getEntigrator(entihome$);
							Properties locator;
							ArrayList<String>sl=new ArrayList<String>();
							String entityKey$;
							for(JItemPanel ip:ipa){
								if(ip.isChecked()){
								    locator=Locator.toProperties(ip.getLocator());
								    entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
								    sl.add(entityKey$);
								}
							}
							String[] sa=sl.toArray(new String[0]);
							
							String[] ea=JReferenceEntry.getCoalition(console, entigrator, sa);
							if(debug){
							if(ea==null)
								System.out.println("JEntitiesPanel:archive:ea null");
							else
							System.out.println("JEntitiesPanel:archive:ea="+ea.length);
							}
							JArchivePanel archivePanel=new JArchivePanel();
				        	String apLocator$=archivePanel.getLocator();
				        	//locator$=getLocator();
			        	  apLocator$=Locator.append(apLocator$,Entigrator.ENTIHOME,entihome$);
						  apLocator$=Locator.append(apLocator$, EntityHandler.ENTITY_LIST,Locator.toString(ea));
					      //String icon$=Support.readHandlerIcon(null,JEntityPrimaryMenu.class, "archive.png");
					      //apLocator$=Locator.append(apLocator$, Locator.LOCATOR_ICON,icon$);
						  apLocator$=Locator.append(apLocator$,Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
						  apLocator$=Locator.append(apLocator$,Locator.LOCATOR_ICON_CLASS,JEntityPrimaryMenu.class.getName());
						  apLocator$=Locator.append(apLocator$,Locator.LOCATOR_ICON_FILE,"archive.png.png");
					
						  JConsoleHandler.execute(console, apLocator$);
							}
					} );
					menu.add(archiveItem);
					menu.addSeparator();
					deleteItem = new JMenuItem("Delete");
					deleteItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							//System.out.println("JEntitiesPanel:delete:0");
							int response = JOptionPane.showConfirmDialog(console.getContentPanel(), "Delete ?", "Confirm",
							        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						   if (response == JOptionPane.YES_OPTION) {
							  // System.out.println("JEntitiesPanel:delete:1");
							   JItemPanel[] ipa=JEntitiesPanel.this.getItems();
							Entigrator entigrator=console.getEntigrator(entihome$);
							String iLocator$;
							Properties iLocator;
							String iEntityKey$;
							String iEntityLabel$;
							Sack iEntity;
							ArrayList<String>sl=new ArrayList<String>();
							for(JItemPanel ip:ipa){
								iLocator$=ip.getLocator();
								
								iLocator=Locator.toProperties(iLocator$);
							//	System.out.println("JEntitiesPanel:delete title="+iLocator.getProperty(Locator.LOCATOR_TITLE));
								iEntityLabel$=iLocator.getProperty(EntityHandler.ENTITY_LABEL);
								if(ip.isChecked()){
								//	System.out.println("JEntitiesPanel:delete label="+iEntityLabel$);
									iEntity=entigrator.ent_getAtLabel(iEntityLabel$);
									
									//iEntityKey$=iLocator.getProperty(EntityHandler.ENTITY_KEY);
									//	iEntity=entigrator.getEntityAtKey(iEntityKey$);
									if(iEntity!=null)
										entigrator.deleteEntity(iEntity);
									
								}
								else{
									sl.add(iEntityLabel$);
								}
							}
							String[] sa=sl.toArray(new String[0]);
                            if(sa!=null&&sa.length>0){							
							String sa$=Locator.toString(sa);
							locator$=Locator.append(locator$, EntityHandler.ENTITY_LIST, sa$);
                            }
							JConsoleHandler.execute(console, locator$);
						}
						}
					} );
					menu.add(deleteItem);
				  }
			}
			@Override
			public void menuDeselected(MenuEvent e) {
			}
			@Override
			public void menuCanceled(MenuEvent e) {
			}	
		});
		menu.addSeparator();
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
 * Get the context locator.
 *  @return the context locator.
 */
	@Override
	public String getLocator() {
		 Properties locator=new Properties();
		    locator.setProperty(Locator.LOCATOR_TYPE, JContext.CONTEXT_TYPE);
		    locator.setProperty(JContext.CONTEXT_TYPE,getType());
		    locator.setProperty(JItemsListPanel.POSITION,String.valueOf(getPosition()));
		    if(entihome$!=null)
		       locator.setProperty(Entigrator.ENTIHOME,entihome$);
		    if(list$!=null)
			       locator.setProperty(EntityHandler.ENTITY_LIST,list$);
		    if(entityKey$!=null)
			       locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
		    locator.setProperty(JItemsListPanel.POSITION,String.valueOf(getPosition()));
		   // String icon$=Support.readHandlerIcon(null,JEntitiesPanel.class, "entities.png");
		    locator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
			locator.setProperty(Locator.LOCATOR_ICON_CLASS,getClass().getName());
			//locator.setProperty(Locator.LOCATOR_ICON_FILE,"bookmark.png");
		    if(containerKey$!=null){
			       locator.setProperty(EntityHandler.ENTITY_CONTAINER,containerKey$);
			       //icon$=Support.readHandlerIcon(null,JEntitiesPanel.class, "clip.png");
			       locator.setProperty(Locator.LOCATOR_ICON_FILE,"clip.png");
		    }
		    if(componentKey$!=null){
			       locator.setProperty(EntityHandler.ENTITY_COMPONENT,componentKey$);
			       //icon$=Support.readHandlerIcon(null,JEntitiesPanel.class, "box.png");
			       locator.setProperty(Locator.LOCATOR_ICON_FILE,"box.png");
		    }
		    
    	    locator.setProperty(Locator.LOCATOR_TITLE, getTitle());
		    //if(icon$!=null)
		    //	locator.setProperty(Locator.LOCATOR_ICON,icon$);
		    locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
		    locator.setProperty(BaseHandler.HANDLER_CLASS,JEntitiesPanel.class.getName());
			if(list$!=null)
		    locator.setProperty(EntityHandler.ENTITY_LIST, list$);
		    return Locator.toString(locator);
	}
/**
 * Create the entities panel.
 * @param console the main console.
 * @param locator$ the locator string.
 * @return an instance of the entities panel.
 */
	@Override
	public JContext instantiate(JMainConsole console, String locator$) {
		try{
			 this.console=console;
			 this.locator$=locator$;
			 Properties locator=Locator.toProperties(locator$);
			 list$=locator.getProperty(EntityHandler.ENTITY_LIST);
			if(debug)
			 System.out.println("JEntitiesPanel:instantiate:list="+list$);
			 entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			 entihome$=locator.getProperty(Entigrator.ENTIHOME);
        	 containerKey$=locator.getProperty(EntityHandler.ENTITY_CONTAINER);
        	 componentKey$=locator.getProperty(EntityHandler.ENTITY_COMPONENT);
        	  Entigrator entigrator=console.getEntigrator(entihome$);
        	 saveId$=entigrator.store_saveId();
        	
        	
			 JItemPanel[] ipl= listEntitiesAtList( console,locator$);
        	 putItems(ipl);
        	 try{
        		 pos=Integer.parseInt(locator.getProperty(POSITION));
        		if(debug)
        		 System.out.println("JEntitiesPanel:instantiate:pos="+pos);
        		 select(pos);
        	 }catch(Exception e){
        		 LOGGER.info(e.toString());
        	 }
        	 if(debug)
        	 System.out.println("JEntitiesPanel:instantiate:save id="+saveId$);
        	return this;
        }catch(Exception e){
        
        LOGGER.severe(e.toString());
        }
        return null;
        }
	@Override
/**
 * Get the context title.
 * @return the context title.	
 */
public String getTitle() {
		String title$= "Entities list";
		if(locator$!=null)
		try{
		Properties locator=Locator.toProperties(locator$);
		if(locator.getProperty(EntityHandler.ENTITY_CONTAINER)!=null)
			title$= "Components list";
		if(locator.getProperty(EntityHandler.ENTITY_COMPONENT)!=null)
			title$= "Containers list";
		}catch(Exception e){
			LOGGER.info(e.toString());
		}
		return title$;
	}
	/**
	 * Get the context type.
	 * @return the context type.	
	 */
	@Override
	public String getType() {
		return "Entities";
	}
	public static JItemPanel[] listEntitiesAtList(JMainConsole console,String locator$){
		try{
			   Properties locator=Locator.toProperties(locator$);
			   String list$=locator.getProperty(EntityHandler.ENTITY_LIST);
			   String[]la=Locator.toArray(list$);
			  
			   if(la==null){
				   Logger.getLogger(JEntitiesPanel.class.getName()).info("empty list");
				   return null;
			   }
			   
			   String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			   Entigrator entigrator=console.getEntigrator(entihome$);
			    ArrayList<JItemPanel>ipl=new ArrayList<JItemPanel>();
			   JItemPanel itemPanel;
			   String entityLocator$;
			   int i=0;
			   String entityKey$;
		   for(String aLa:la){
			   try{
			if(debug)
				   System.out.println("JEntitiesPanel: listEntitiesAtLabelList:aLa="+aLa);	   
			      entityLocator$=EntityHandler.getEntityLocatorAtLabel(entigrator, aLa);
			   if(entityLocator$==null){
			       entityLocator$=EntityHandler.getEntityLocatorAtKey(entigrator, aLa);
			       entityKey$=aLa;
			   }else
				   entityKey$=entigrator.indx_keyAtLabel(aLa);
		  if(debug)   
			   System.out.println("JEntitiesPanel: listEntitiesAtLabelList:locator="+entityLocator$);	
			   entityLocator$=Locator.append(entityLocator$,Entigrator.ENTIHOME , entihome$);
			   entityLocator$=Locator.append(entityLocator$,Locator.LOCATOR_CHECKABLE , Locator.LOCATOR_TRUE);
			   JEntityFacetPanel em=new JEntityFacetPanel();
			   entityLocator$=Locator.append(entityLocator$, JFacetRenderer.ONLY_ITEM, Locator.LOCATOR_TRUE);
			   em.instantiate(console, entityLocator$);
			   String emLocator$=em.getLocator();
			   Properties emLocator=Locator.toProperties(emLocator$);
			   emLocator.setProperty(Locator.LOCATOR_CHECKABLE, Locator.LOCATOR_TRUE);
			   emLocator.setProperty(POSITION, String.valueOf(i++));
			   String iconFile$=entigrator.ent_getIconAtKey(entityKey$);
			   if(iconFile$!=null&&!"sack.gif".equals(iconFile$)&&!"null".equals(iconFile$)){
			   emLocator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_ICONS);
			   emLocator.setProperty(Locator.LOCATOR_ICON_FILE,iconFile$);
			   }else{
					String type$=entigrator.getEntityType(entityKey$);
					if(debug)   
						   System.out.println("JEntitiesPanel: listEntitiesAtLabelList:entity type="+type$);	
					boolean found=false;	
					FacetHandler[] fha=BaseHandler.listAllHandlers(entigrator);
			    	   for(FacetHandler fh:fha){
			    		if(debug)   
							   System.out.println("JEntitiesPanel: listEntitiesAtLabelList:handler type="+fh.getType());	
						
			    		if(type$.equals(fh.getType())){
			    			 JFacetRenderer facetRenderer=JConsoleHandler.getFacetRenderer(entigrator, fh.getClass().getName());
			    			 emLocator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
			    			 emLocator.setProperty(Locator.LOCATOR_ICON_CLASS,facetRenderer.getClass().getName());
			    			 emLocator.setProperty(Locator.LOCATOR_ICON_FILE,facetRenderer.getFacetIcon());
			    			 found=true;
			    			 break;
			    		}
			    	   }
			    	   if(!found){
			    		 emLocator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
			    		 emLocator.setProperty(Locator.LOCATOR_ICON_CLASS,JEntitiesPanel.class.getName());
			    		 emLocator.setProperty(Locator.LOCATOR_ICON_FILE,"facet.png");
			    		
			    	   }
			   }
			   itemPanel=new JItemPanel(console,Locator.toString(emLocator));
			   ipl.add(itemPanel);
			   }catch(Exception ee){
				   Logger.getLogger(JEntitiesPanel.class.getName()).info(ee.toString());
			   }
		   }
		   Collections.sort(ipl,new ItemPanelComparator());
		   JItemPanel[] ipa=ipl.toArray(new JItemPanel[0]);
		   return ipa;
		}catch(Exception e) {
        	Logger.getLogger(JEntitiesPanel.class.getName()).severe(e.toString());
            return null;
        }
	}
	/**
	 * Complete the context. No action.
	 */
	@Override
	public void close() {
		console.getTrack().pop();
	      console.getTrack().push(getLocator());
	}
	/**
	 * Get the context subtitle.
	 * @return the context subtitle.	
	 */
	@Override
	public String getSubtitle() {
		String subtitle$=null;
		try{
			  Entigrator entigrator=console.getEntigrator(entihome$);
			  subtitle$=entigrator.getBaseName();
			  if(entityKey$!=null){
				  subtitle$=entigrator.indx_getLabel(entityKey$);
			  }
			}catch(Exception e){
			}
		return subtitle$;
	}
	private void pasteComponents(){
		String locator$= JEntityPrimaryMenu.pasteComponents(console,getLocator());
		JEntityPrimaryMenu.showComponents(console, locator$);
	}
	private void removeComponents(){
		String[] sa=listSelectedItems();
		if(sa==null|sa.length<1)
			return;
		try{
			  Entigrator entigrator=console.getEntigrator(entihome$);
			  Sack container=entigrator.getEntityAtKey(entityKey$);
			  Properties locator;
			  String componentKey$;
			  Sack component;
			  for(String aSa:sa){
				 try{ 
				 locator=Locator.toProperties(aSa);
				 locator=Locator.toProperties(aSa);
				 componentKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
				 component=entigrator.getEntityAtKey(componentKey$);
				 if(component!=null){
					 container=entigrator.col_breakRelation(container, component);
				 }
				 }catch(Exception ee){
					 LOGGER.info(ee.toString()); 
				 }
			  }
			 JEntityPrimaryMenu.showComponents(console, locator$);
			}catch(Exception e){
			LOGGER.severe(e.toString());
			}
	}
	private void removeContainers(){
		String[] sa=listSelectedItems();
		if(sa==null|sa.length<1)
			return;
		try{
			  Entigrator entigrator=console.getEntigrator(entihome$);
			  Sack component=entigrator.getEntityAtKey(entityKey$);
			  Properties locator;
			  Sack container;
			  for(String aSa:sa){
				 try{ 
				 locator=Locator.toProperties(aSa);
				 locator=Locator.toProperties(aSa);
				 containerKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
				 container=entigrator.getEntityAtKey(containerKey$);
				 if(component!=null){
					 container=entigrator.col_breakRelation(container, component);
				 }
				 }catch(Exception ee){
					 LOGGER.info(ee.toString()); 
				 }
			  }
			 JEntityPrimaryMenu.showContainers(console, locator$);
			  
			}catch(Exception e){
			LOGGER.severe(e.toString());
			}
	}

	@Override
	public void activate() {
		if(debug)
			System.out.println("JEntitiesPanel:activate:begin");
		if(ignoreOutdate){
			ignoreOutdate=false;
			return;
		}
		Entigrator entigrator=console.getEntigrator(entihome$);
		
		if(!entigrator.store_outdated()){
			System.out.println("JEntitiesPanel:activate:up to date");
			return;
		}
		int n=new ReloadDialog(this).show();
		if(2==n){
			//cancel
			ignoreOutdate=true;
			return;
		}
		if(1==n){
			//replace
			entigrator.store_replace();//JConsoleHandler.execute(console, getLocator());
		}
		if(0==n){
			//reload
			entigrator.store_reload();
			 JConsoleHandler.execute(console, getLocator());
			}
		
		
	}

	@Override
	public String getWebView(Entigrator entigrator, String locator$) {
		try{
			if(debug)
				System.out.println("JEntitesPanel:BEGIN:locator="+locator$);
				
			Properties locator=Locator.toProperties(locator$);
			String webHome$=locator.getProperty(WContext.WEB_HOME);
			String webRequester$=locator.getProperty(WContext.WEB_REQUESTER);
			String mode$=locator.getProperty(JDesignPanel.MODE);
			String propertyName$=locator.getProperty(JDesignPanel.PROPERTY_NAME);
			String propertyValue$=locator.getProperty(JDesignPanel.PROPERTY_VALUE);
			if(debug)
			System.out.println("JEntitiesPanel:web home="+webHome$+ " web requester="+webRequester$);
			// String icon$=Support.readHandlerIcon(null,JBaseNavigator.class, "base.png");
			StringBuffer sb=new StringBuffer();
			sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
			sb.append("<html>");
			sb.append("<head>");
			sb.append(WUtils.getMenuBarScript());
			sb.append(WUtils.getMenuBarStyle());
		    sb.append("</head>");
		    sb.append("<body onload=\"onLoad()\" >");
		    sb.append("<ul class=\"menu_list\">");
		    sb.append("<li class=\"menu_item\"><a id=\"back\">Back</a></li>");
		    sb.append("<li class=\"menu_item\"><a href=\""+webHome$+"\">Home</a></li>");
		    String navLocator$=Locator.append(locator$, BaseHandler.HANDLER_CLASS, JBaseNavigator.class.getName());
		    navLocator$=Locator.append(navLocator$, Entigrator.ENTIHOME, entigrator.getEntihome());
		    String navUrl$=webHome$+"?"+WContext.WEB_LOCATOR+"="+Base64.encodeBase64URLSafeString(navLocator$.getBytes());
		    sb.append("<li class=\"menu_item\"><a href=\""+navUrl$+"\">Base</a></li>");
		    sb.append("<li class=\"menu_item\"><a href=\""+webHome$.replace("entry", WContext.ABOUT)+"\">About</a></li>");
		    sb.append("</ul>");
		    sb.append("<table><tr><td>Base:</td><td><strong>");
		    sb.append(entigrator.getBaseName());
		    sb.append("</strong></td></tr>");
		    sb.append("<tr><td>Property:</td><td><strong>");
		    	    sb.append(propertyName$);
		    	    sb.append("</strong></td></tr>");
		    sb.append("</table>");
		    String [] sa=null;
		    if(JDesignPanel.PROPERTY_MODE.equals(mode$))
		    	sa=entigrator.indx_listEntitiesAtPropertyName(propertyName$);
		    if(JDesignPanel.VALUE_MODE.equals(mode$))
		    	sa=entigrator.indx_listEntities(propertyName$,propertyValue$);
		    if(sa!=null){
		    String[] ia=listWebItems(entigrator,webHome$,sa);
		    if(ia!=null)
		    	for(String i:ia)
		    	sb.append(i+"<br>");
		    }	  
	        sb.append("<script>");
	      
		    
		    sb.append("function onLoad() {");
		    sb.append("initBack(\""+this.getClass().getName()+"\",\""+webRequester$+"\");");
		    sb.append("}");
		    sb.append("window.localStorage.setItem(\""+this.getClass().getName()+"\",\""+Base64.encodeBase64URLSafeString(locator$.getBytes())+"\");");
		    
	 	    sb.append("</script>");
		    sb.append("</body>");
		    sb.append("</html>");
		    return sb.toString();
		}catch(Exception e){
			Logger.getLogger(JBasesPanel.class.getName()).severe(e.toString());	
		}
		return null;
	}

	@Override
	public String getWebConsole(Entigrator entigrator, String locator$) {
		// TODO Auto-generated method stub
		return null;
	}

	private  static String[] listWebItems(Entigrator entigrator,String webHome$,String[] sa){
		try{
			
	    Properties foiLocator=new Properties();   
	   	foiLocator.setProperty(BaseHandler.HANDLER_CLASS,JEntityFacetPanel.class.getName());
    	foiLocator.setProperty(Entigrator.ENTIHOME,entigrator.getEntihome());
    	
	    ArrayList<String>tl=new ArrayList<String>();
	    String entityLocator$;
	    String entityIcon$;
	    String foiItem$;
	    String entityLabel$;
	    Hashtable<String,String> tab=new Hashtable<String,String>();
		   for(String s:sa){
			   try{
				   //if(debug)
				//	   		System.out.println("JEntitiesPanelPanel:listCategoryMembers:c type="+c.type+" name="+c.name+" value="+c.value);
				   entityLocator$=EntityHandler.getEntityLocatorAtKey(entigrator, s);
				   entityLabel$=Locator.getProperty(entityLocator$,EntityHandler.ENTITY_LABEL); 
				   entityIcon$=JConsoleHandler.getIcon(entigrator, entityLocator$);
				   entityLocator$=Locator.append(entityLocator$,BaseHandler.HANDLER_CLASS, JEntityFacetPanel.class.getName());
				   entityLocator$=Locator.append(entityLocator$,Entigrator.ENTIHOME, entigrator.getEntihome());
				   foiItem$=getItem(entityIcon$, webHome$,entityLabel$,entityLocator$);
                   tl.add(entityLabel$);
                   tab.put(entityLabel$,foiItem$);
			   }catch(Exception ee){
				   Logger.getLogger(JCategoryPanel.class.getName()).info(ee.toString());
			   }
		   }
		   Collections.sort(tl);
		   ArrayList<String>sl=new ArrayList<String>();
		   for(String s:tl)
			   sl.add(tab.get(s));
		   return sl.toArray(new String[0]);
		}catch(Exception e) {
        	Logger.getLogger(JEntitiesPanel.class.getName()).severe(e.toString());
            return null;
        }
	}
	private static String getItem(String icon$, String url$, String title$,String foiLocator$){
		if(debug)
				System.out.println("JBookmarksFacetOpenItem:getItem: locator="+foiLocator$);
	    
		String iconTerm$="<img src=\"data:image/png;base64,"+WUtils.scaleIcon(icon$)+
				  "\" width=\"24\" height=\"24\" alt=\""+title$+"\">";
		foiLocator$=Locator.append(foiLocator$,WContext.WEB_HOME, url$);
		foiLocator$=Locator.append(foiLocator$,WContext.WEB_REQUESTER, JCategoryPanel.class.getName());
		  return iconTerm$+"<a href=\""+url$+"?"+WContext.WEB_LOCATOR+"="+Base64.encodeBase64URLSafeString(foiLocator$.getBytes())+"\" >"+" "+title$+"</a>";
	}	
	
}
