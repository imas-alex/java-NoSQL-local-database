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
import gdt.data.entity.FacetHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
import gdt.jgui.base.JBaseNavigator;
import gdt.jgui.base.JBasesPanel;
import gdt.jgui.base.JBusyStorage;
import gdt.jgui.base.JCategoryPanel;
import gdt.jgui.base.JDesignPanel;
import gdt.jgui.base.ProgressDialog;
import gdt.jgui.base.ProgressDisplay;
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
	public static final String PAGE_BEGIN_COUNT="page begin count";
	public static final String ENCODED_LABEL="encoded label";
	public static final int PAGE_SIZE=100;
	private Logger LOGGER=Logger.getLogger(JEntitiesPanel.class.getName());
	protected String entihome$;
	protected String list$;
	protected String entityKey$;
	protected JMenuItem pasteItem;
	protected JMenuItem copyItem;
	protected JMenuItem reindexItem;
	protected JMenuItem deleteItem;
	protected JMenuItem archiveItem;
	protected JMenuItem releaseItem;
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
							Entigrator entigrator=console.getEntigrator(entihome$);
							//if(entigrator.store_scopeIsBusy(true, sa))
							JItemPanel[] ipa=JEntitiesPanel.this.getItems();
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
							if(entigrator.store_scopeIsBusy(true, sa)){
								JBusyStorage.show(JEntitiesPanel.this);
								return;
							}
							entigrator.store_lock();
							for(JItemPanel ip:ipa)
								if(ip.isChecked()){
								    JEntityPrimaryMenu.reindexEntity(console, ip.getLocator());
								}
							entigrator.store_releaseScope(sa);
							
						}
					} );
					menu.add(reindexItem);
					archiveItem = new JMenuItem("Export");
					archiveItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							ProgressDialog pd=new ProgressDialog(console.getFrame(),Export,"Prepare ..");
							pd.setLocationRelativeTo(JEntitiesPanel.this);
							pd.setVisible(true);
							/*
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
			        	  apLocator$=Locator.append(apLocator$,Entigrator.ENTIHOME,entihome$);
						  apLocator$=Locator.append(apLocator$, EntityHandler.ENTITY_LIST,Locator.toString(ea));
						  apLocator$=Locator.append(apLocator$,Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
						  apLocator$=Locator.append(apLocator$,Locator.LOCATOR_ICON_CLASS,JEntityPrimaryMenu.class.getName());
						  apLocator$=Locator.append(apLocator$,Locator.LOCATOR_ICON_FILE,"archive.png.png");
					
						  JConsoleHandler.execute(console, apLocator$);
						  */
							}
					} );
					menu.add(archiveItem);
					//
					releaseItem = new JMenuItem("Force release");
					releaseItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							Entigrator entigrator=console.getEntigrator(entihome$);
							//if(entigrator.store_scopeIsBusy(true, sa))
							JItemPanel[] ipa=JEntitiesPanel.this.getItems();
							Properties locator;
							ArrayList<String>sl=new ArrayList<String>();
							String entityKey$;
							for(JItemPanel ip:ipa){
								if(ip.isChecked()){
								    locator=Locator.toProperties(ip.getLocator());
								    entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
								    entigrator.ent_release(entityKey$);
								}
							}
							entigrator.store_release();
						}
					} );
					menu.add(releaseItem);
					//
					menu.addSeparator();
					deleteItem = new JMenuItem("Delete");
					deleteItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							int response = JOptionPane.showConfirmDialog(console.getContentPanel(), "Delete ?", "Confirm",
							        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						   if (response != JOptionPane.YES_OPTION) 
							   return;
							
							ProgressDisplay pd=new ProgressDisplay(console,Delete,"Wait for delete..");	
				    	pd.setLocationRelativeTo(JEntitiesPanel.this);
				    	pd.setVisible(true);
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
	//
	
	private void deleteEntities(Entigrator entigrator){
		JItemPanel[] ipa=JEntitiesPanel.this.getItems();
		
		String iLocator$;
		Properties iLocator;
		String iEntityKey$;
		String iEntityLabel$;
		Sack iEntity;
		ArrayList<String>sl=new ArrayList<String>();
		entigrator.setBulkMode(true);
		console.clipboard.resetProgress(ipa.length);
	    console.clipboard.setProgressMessage("Delete entities..");
		long i=0;
		for(JItemPanel ip:ipa){
			console.clipboard.setProgress(i++);
			iLocator$=ip.getLocator();
			iEntityKey$=Locator.getProperty(iLocator$,EntityHandler.ENTITY_KEY);
			iEntityLabel$=entigrator.indx_getLabel(iEntityKey$);
			if(ip.isChecked()){
			//System.out.println("JEntitiesPanel:delete label="+iEntityLabel$);
				iEntity=entigrator.getEntityAtKey(iEntityKey$);
				if(iEntity!=null)
					entigrator.deleteEntity(iEntity);
			}
			else{
				//System.out.println("JEntitiesPanel:cannot get entity  label="+iEntityLabel$);
				sl.add(iEntityKey$);
			}
		}
		entigrator.setBulkMode(false);
		String[] sa=sl.toArray(new String[0]);
        if(sa!=null&&sa.length>0){							
		String sa$=Locator.toString(sa);
		locator$=Locator.append(locator$, EntityHandler.ENTITY_LIST, sa$);
        }
		JConsoleHandler.execute(console, locator$);
	}
	Runnable Delete=new Runnable(){
		public void run(){
			//System.out.println("Entigrator:Reindex:thread="+Thread.currentThread().hashCode());
			if(console.clipboard.getProgress()>0){
				console.clipboard.setProgress(0);
				return;
			}
			Entigrator entigrator=console.getEntigrator(entihome$);
			deleteEntities(entigrator);
		}};
	//
	Runnable Export =new Runnable(){
		public void run(){
		   export();	
		}
	};
	private void export(){
		JItemPanel[] ipa=JEntitiesPanel.this.getItems();
		if(ipa==null||ipa.length<1)
			return;
		Entigrator entigrator=console.getEntigrator(entihome$);
		Properties locator;
		ArrayList<String>sl=new ArrayList<String>();
		String entityKey$;
		long i=0;
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
	  apLocator$=Locator.append(apLocator$,Entigrator.ENTIHOME,entihome$);
	  apLocator$=Locator.append(apLocator$, EntityHandler.ENTITY_LIST,Locator.toString(ea));
	  apLocator$=Locator.append(apLocator$,Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
	  apLocator$=Locator.append(apLocator$,Locator.LOCATOR_ICON_CLASS,JEntityPrimaryMenu.class.getName());
	  apLocator$=Locator.append(apLocator$,Locator.LOCATOR_ICON_FILE,"archive.png.png");

	  JConsoleHandler.execute(console, apLocator$);
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
			       locator.setProperty(Locator.LOCATOR_ICON_FILE,"clip.png");
		    }
		    if(componentKey$!=null){
			       locator.setProperty(EntityHandler.ENTITY_COMPONENT,componentKey$);
			       locator.setProperty(Locator.LOCATOR_ICON_FILE,"box.png");
		    }
		    
    	    locator.setProperty(Locator.LOCATOR_TITLE, getTitle());
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
        	// saveId$=entigrator.store_saveId();
        	
        	
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
			   String entityKey$;
			   String entityLocator$;
			   String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			   Entigrator entigrator=console.getEntigrator(entihome$);
			   //
			   ArrayList <String>sl=new ArrayList<String>();
			   for(String aLa:la){
				 
				if(debug)
					   System.out.println("JEntitiesPanel: listEntitiesAtLabelList:aLa="+aLa);	   
				      entityLocator$=EntityHandler.getEntityLocatorAtLabel(entigrator, aLa);
				   if(entityLocator$==null){
				       entityLocator$=EntityHandler.getEntityLocatorAtKey(entigrator, aLa);
				       entityKey$=aLa;
				   }else
					   entityKey$=entigrator.indx_keyAtLabel(aLa);
				  if(!sl.contains(entityKey$))
					  sl.add(entityKey$);
				   
				   }
		String[]  sa=sl.toArray(new String[0]);
			   if(sa.length<1)
				   return null;
 		   ArrayList<JItemPanel>ipl=new ArrayList<JItemPanel>();
		   JItemPanel itemPanel;
	       String emLocator$;
	       Core [] ca=entigrator.indx_getMarks(sa);
	       if(debug)
				System.out.println("JEntitiesPanel: listEntitiesAtLabelList:ca="+ca.length);
				
	       JEntityFacetPanel em;
	       Properties emLocator;
		   String iconFile$;
		   FacetHandler[] fha=BaseHandler.listAllHandlers(entigrator);
	       for(Core c:ca){
			   try{
				   if(debug)
						System.out.println("JCategoryPanel:listCategoryMembers:c type="+c.type+" name="+c.name+" value="+c.value);
			   em=new JEntityFacetPanel();
			   emLocator$=em.getLocator();
			   emLocator=Locator.toProperties(emLocator$);
			   emLocator.setProperty(Entigrator.ENTIHOME , entihome$);
			   emLocator.setProperty(EntityHandler.ENTITY_KEY ,c.name);
			   emLocator.setProperty(Locator.LOCATOR_CHECKABLE, Locator.LOCATOR_TRUE);
			   emLocator.setProperty(Locator.LOCATOR_TITLE, c.value);
			   emLocator.setProperty(BaseHandler.HANDLER_SCOPE, JConsoleHandler.CONSOLE_SCOPE);
			   iconFile$=entigrator.ent_getIconAtKey(c.name);
			   if(iconFile$!=null&&!"sack.gif".equals(iconFile$)&&!"null".equals(iconFile$)){
				   emLocator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_ICONS);
				   emLocator.setProperty(Locator.LOCATOR_ICON_FILE,iconFile$);
				   }else{
						String type$=entigrator.getEntityType(c.name);
						boolean found=false;	
						
				    	   for(FacetHandler fh:fha){
				    		if(type$.equals(fh.getType())){
				    			 JFacetRenderer facetRenderer=JConsoleHandler.getFacetRenderer(entigrator, fh);
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
				   Logger.getLogger(JCategoryPanel.class.getName()).info(ee.toString());
			   }
		   }
		   Collections.sort(ipl,new ItemPanelComparator());
		   return ipl.toArray(new JItemPanel[0]);
					   
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
			String pageBeginCount$=locator.getProperty(PAGE_BEGIN_COUNT);
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
		    sb.append("<li class=\"menu_item\"><a href=\""+WContext.ABOUT+"\">About</a></li>");
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
		    ArrayList<String>page=new ArrayList<String>();
		    String[] ia;
		    if(debug)
		    	   System.out.println("JEntitiesPanel:getWebView:selected keys="+sa.length);
		    
		    if(sa!=null){
		    //
		       sa=entigrator.indx_sortKeysAtlabel(sa);
		       if(debug)
		    	   System.out.println("JEntitiesPanel:getWebView:sorted keys="+sa.length);
		       int pageBegin=0;
		       if(pageBeginCount$!=null)
		    		try{ pageBegin=Integer.parseInt(pageBeginCount$);}catch(Exception e){}
		       int pageEnd=pageBegin+PAGE_SIZE;
		      // page.clear();
		       if(pageEnd<sa.length){
		    	   for(int i=pageBegin;i<pageEnd;i++)
		    		   page.add(sa[i]);
		    	   if(debug)
			    	   System.out.println("JEntitiesPanel:getWebView:page="+page.size());
			     	   
		    	   ia=listWebItems(entigrator,webHome$,page.toArray(new String[0]));
		    	   if(debug)
			    	   System.out.println("JEntitiesPanel:getWebView:items="+ia.length);
			     	   
		    	
		    	   for(String it:ia)
		    		   sb.append(it+"<br>");
		    	   locator$=Locator.append(locator$, PAGE_BEGIN_COUNT, String.valueOf(pageEnd));
		    		String moreItem$="<a href=\""+webHome$+"?"+WContext.WEB_LOCATOR+"="+Base64.encodeBase64URLSafeString(locator$.getBytes())+"\" ><strong>Next page </strong></a>";
		    		sb.append(moreItem$);
		       }else{
		    	   for(int i=0;i<sa.length;i++){
		    		   page.add(sa[i]);
		    	   }
		    	   ia=listWebItems(entigrator,webHome$,page.toArray(new String[0]));
		    	   for(String it:ia)
		    		   sb.append(it+"<br>");
		       }
		    	   
		       
		    	
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

	public  static String[] listWebItems(Entigrator entigrator,String webHome$,String[] sa){
		try{
			
	    Properties foiLocator=new Properties();   
	   	foiLocator.setProperty(BaseHandler.HANDLER_CLASS,JEntityFacetPanel.class.getName());
    	foiLocator.setProperty(Entigrator.ENTIHOME,entigrator.getEntihome());
    	
	    ArrayList<String>tl=new ArrayList<String>();
	    String entityLocator$;
	    String entityIcon$;
	    String foiItem$;
	    String entityLabel$;
		   for(String s:sa){
			   try{
				   entityLocator$=EntityHandler.getEntityLocatorAtKey(entigrator, s);
				   entityLabel$=Locator.getProperty(entityLocator$,EntityHandler.ENTITY_LABEL); 
				   entityIcon$=JConsoleHandler.getIcon(entigrator, entityLocator$);
				   entityLocator$=Locator.append(entityLocator$,BaseHandler.HANDLER_CLASS, JEntityFacetPanel.class.getName());
				   entityLocator$=Locator.append(entityLocator$,Entigrator.ENTIHOME, entigrator.getEntihome());
				   entityLocator$=Locator.append(entityLocator$,ENCODED_LABEL, Base64.encodeBase64URLSafeString(entityLabel$.getBytes()));
				   foiItem$=getItem(entityIcon$, webHome$,entityLabel$,entityLocator$);
				   tl.add(foiItem$);
			   }catch(Exception ee){
				   Logger.getLogger(JCategoryPanel.class.getName()).info(ee.toString());
			   }
		   }
		   return tl.toArray(new String[0]);
		}catch(Exception e) {
        	Logger.getLogger(JEntitiesPanel.class.getName()).severe(e.toString());
            return null;
        }
	}
	private static String getItem(String icon$, String url$, String title$,String foiLocator$){
		if(debug)
				System.out.println("JEntitiesPanel:getItem: locator="+foiLocator$);
	    
		String iconTerm$="<img src=\"data:image/png;base64,"+WUtils.scaleIcon(icon$)+
				  "\" width=\"24\" height=\"24\" alt=\""+title$+"\">";
		foiLocator$=Locator.append(foiLocator$,WContext.WEB_HOME, url$);
		foiLocator$=Locator.append(foiLocator$,WContext.WEB_REQUESTER, JCategoryPanel.class.getName());
		  return iconTerm$+"<a href=\""+url$+"?"+WContext.WEB_LOCATOR+"="+Base64.encodeBase64URLSafeString(foiLocator$.getBytes())+"\" >"+" "+title$+"</a>";
	}	
	
}
