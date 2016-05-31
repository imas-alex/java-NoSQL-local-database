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
import java.util.Collections;
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
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JItemPanel;
import gdt.jgui.console.JItemsListPanel;
import gdt.jgui.console.JMainConsole;
/**
 * Displays the list of entities.
 * @author imasa
 *
 */

public class JEntitiesPanel extends JItemsListPanel{
	private static final long serialVersionUID = 1L;
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
 //   protected JMenuItem[] mia;
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
				menu.removeAll();
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
					 if(componentKey$!=null){	
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
							System.out.println("JEntitiesPanel:archive:1");
							String[] ea=JReferenceEntry.getCoalition(console, entigrator, sa);
							if(ea==null)
								System.out.println("JEntitiesPanel:archive:ea null");
							else
							System.out.println("JEntitiesPanel:archive:ea="+ea.length);
							JArchivePanel archivePanel=new JArchivePanel();
				        	String apLocator$=archivePanel.getLocator();
				        	//locator$=getLocator();
			        	  apLocator$=Locator.append(apLocator$,Entigrator.ENTIHOME,entihome$);
						  apLocator$=Locator.append(apLocator$, EntityHandler.ENTITY_LIST,Locator.toString(ea));
					      String icon$=Support.readHandlerIcon(null,JEntityPrimaryMenu.class, "archive.png");
					      apLocator$=Locator.append(apLocator$, Locator.LOCATOR_ICON,icon$);
						  JConsoleHandler.execute(console, apLocator$);
							}
					} );
					menu.add(archiveItem);
					menu.addSeparator();
					deleteItem = new JMenuItem("Delete");
					deleteItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							int response = JOptionPane.showConfirmDialog(console.getContentPanel(), "Delete ?", "Confirm",
							        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						   if (response == JOptionPane.YES_OPTION) {
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
								iEntityLabel$=iLocator.getProperty(EntityHandler.ENTITY_LABEL);
								if(ip.isChecked()){
									iEntityKey$=iLocator.getProperty(EntityHandler.ENTITY_KEY);
										iEntity=entigrator.getEntityAtKey(iEntityKey$);
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
		    if(entihome$!=null)
		       locator.setProperty(Entigrator.ENTIHOME,entihome$);
		    if(list$!=null)
			       locator.setProperty(EntityHandler.ENTITY_LIST,list$);
		    if(entityKey$!=null)
			       locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
		    String icon$=Support.readHandlerIcon(null,JEntitiesPanel.class, "entities.png");
		    if(containerKey$!=null){
			       locator.setProperty(EntityHandler.ENTITY_CONTAINER,containerKey$);
			       icon$=Support.readHandlerIcon(null,JEntitiesPanel.class, "clip.png");
		    }
		    if(componentKey$!=null){
			       locator.setProperty(EntityHandler.ENTITY_COMPONENT,componentKey$);
			       icon$=Support.readHandlerIcon(null,JEntitiesPanel.class, "box.png");
		    }
    	    locator.setProperty(Locator.LOCATOR_TITLE, getTitle());
		    if(icon$!=null)
		    	locator.setProperty(Locator.LOCATOR_ICON,icon$);
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
			 entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			 entihome$=locator.getProperty(Entigrator.ENTIHOME);
        	 containerKey$=locator.getProperty(EntityHandler.ENTITY_CONTAINER);
        	 componentKey$=locator.getProperty(EntityHandler.ENTITY_COMPONENT);
			 JItemPanel[] ipl= listEntitiesAtLabelList( console,locator$);
        	 putItems(ipl);
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
	public static JItemPanel[] listEntitiesAtLabelList(JMainConsole console,String locator$){
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
		   for(String aLa:la){
			   try{
			   entityLocator$=EntityHandler.getEntityLocator(entigrator, aLa);
			   entityLocator$=Locator.append(entityLocator$,Entigrator.ENTIHOME , entihome$);
			   entityLocator$=Locator.append(entityLocator$,Locator.LOCATOR_CHECKABLE , Locator.LOCATOR_TRUE);
			   JEntityFacetPanel em=new JEntityFacetPanel();
			   em.instantiate(console, entityLocator$);
			   String emLocator$=em.getLocator();
			   emLocator$=Locator.append(emLocator$,Locator.LOCATOR_CHECKABLE, Locator.LOCATOR_TRUE);
			   itemPanel=new JItemPanel(console,emLocator$);
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
		// TODO Auto-generated method stub
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
}
