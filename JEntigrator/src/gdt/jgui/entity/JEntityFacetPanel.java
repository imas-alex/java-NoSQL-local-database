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
import gdt.data.entity.facet.ExtensionHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.jgui.base.JBaseNavigator;
import gdt.jgui.base.JBasesPanel;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JFacetOpenItem;
import gdt.jgui.console.JFacetRenderer;
import gdt.jgui.console.JItemPanel;
import gdt.jgui.console.JItemsListPanel;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.console.WContext;
import gdt.jgui.console.WUtils;
import gdt.jgui.entity.query.JQueryFacetOpenItem;
import gdt.jgui.entity.view.JViewFacetOpenItem;
import gdt.jgui.tool.JEntityEditor;
/**
 * Display a list of all facets assigned to the entity.
 * @author imasa
 *
 */
public class JEntityFacetPanel extends JItemsListPanel implements WContext {
	public static final String ENTITY_FACET_PANEL="Entity facet panel";
	private static final long serialVersionUID = 1L;
    private Logger LOGGER=Logger.getLogger(JEntityFacetPanel.class.getName());
	private String entihome$;
    private String entityKey$;
    private String entityLabel$;
    private String entityIcon$;
    private String locator$;
    private String requesterResponseLocator$;
    JMenuItem addFacets;
    JMenuItem removeFacets;
    JMenuItem copyFacets;
     static boolean debug=false;
	/**
	 * Get the context locator
	 * @return the context locator.
	 */
    @Override
	public String getLocator() {
		 Properties locator=new Properties();
		    locator.setProperty(Locator.LOCATOR_TYPE, JContext.CONTEXT_TYPE);
		    locator.setProperty(JContext.CONTEXT_TYPE,getType());
		    if(entihome$!=null){
		       locator.setProperty(Entigrator.ENTIHOME,entihome$);
		   
		    }
		    locator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
			locator.setProperty(Locator.LOCATOR_ICON_CLASS,getClass().getName());
			locator.setProperty(Locator.LOCATOR_ICON_FILE,"facet.png");
		    if(entityKey$!=null){
		    	
			       locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
			       if(entihome$!=null){
			       locator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_ICONS);
			       Entigrator entigrator=console.getEntigrator(entihome$);
			       locator.setProperty(Locator.LOCATOR_ICON_FILE,entigrator.ent_getIconAtKey(entityKey$));
			       }
		    }
		    if(entityLabel$!=null)
			       locator.setProperty(EntityHandler.ENTITY_LABEL,entityLabel$);
		    if(requesterResponseLocator$!=null)
			       locator.setProperty(JRequester.REQUESTER_RESPONSE_LOCATOR,requesterResponseLocator$);
 	     
		    locator.setProperty(Locator.LOCATOR_TITLE, getTitle());
		   locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
		   locator.setProperty(BaseHandler.HANDLER_CLASS,JEntityFacetPanel.class.getName());
		   return Locator.toString(locator);
	}
/**
 * Create the facet panel.
 * @param console the main console
 * @param locator$ the locator
 * return the instance of the facet console. 
 */
	@Override
	public JContext instantiate(JMainConsole console, String locator$) {
	//	System.out.println("JEntityFacetPanel:instantiate:BEGIN");
		this.console=console;
		this.locator$=locator$;
		 Properties locator=Locator.toProperties(locator$);
		 entihome$=locator.getProperty(Entigrator.ENTIHOME);
		 entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		 Entigrator entigrator=console.getEntigrator(entihome$);
		 entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
		 entityIcon$=JConsoleHandler.getIcon(entigrator,locator$);
		 if(Locator.LOCATOR_TRUE.equals(locator.getProperty(JFacetRenderer.ONLY_ITEM)))
			 return this;
		 requesterResponseLocator$=locator.getProperty(JRequester.REQUESTER_RESPONSE_LOCATOR);
//		 System.out.println("EntityFacetPanel:instantiate:locator="+Locator.remove(locator$,Locator.LOCATOR_ICON));
		 ArrayList<JItemPanel>ipl=new ArrayList<JItemPanel>();
		JEntityPrimaryMenu primaryMenu=new JEntityPrimaryMenu();
			if(primaryMenu.instantiate(console, locator$)==null){
				LOGGER.severe("cannot instantitate primary menu");
				return null;
			}
			JItemPanel designItem=new JItemPanel(console, primaryMenu.getLocator());
			ipl.add(designItem);
			String[]sa=listFacetOpenItems();
			if(sa!=null){
			JItemPanel ip;	
		//	System.out.println("JEntityFacetPanel:instantiate:ipa="+ipa.length);	
			for(String s:sa){
		//		System.out.println("EntityFacetPanel:instantiate:ipa locator="+ip.getLocator());
				  ipl.add(new JItemPanel(console,s));
			}
			Collections.sort(ipl,new ItemPanelComparator());
			}
			JItemPanel[]ipla=ipl.toArray(new JItemPanel[0]);
		//	System.out.println("EntityFacetPanel:instantiate:ipla="+ipla.length);	
			putItems(ipla);
		 return this;
	}
/**
 *Get the title
 *@return the title.
 */
	@Override
	public String getTitle() {
		try{
			if(entityLabel$!=null)
				return entityLabel$;
			entityLabel$= console.getEntigrator(entihome$).indx_getLabel(entityKey$);
			if(entityLabel$!=null)
				return entityLabel$;	
		return "No label";
			}catch(Exception e ){
				return "No label";
			}
	}
	/**
	 *Get the type
	 *@return the type.
	 */
	@Override
	public String getType() {
		return ENTITY_FACET_PANEL;
	}
	/**
	 * Complete the context.
	 * No action.
	 */
@Override
	public void close() {
	}
private JFacetOpenItem[] getFacetOpenItems(){
	try{
		
		ArrayList<JFacetOpenItem>foil=new ArrayList<JFacetOpenItem>();
		Properties locator=Locator.toProperties(locator$);
		entihome$=locator.getProperty(Entigrator.ENTIHOME);
		entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		//System.out.println("EntityFacetPanel:getFacetOpenItems:entity key="+entityKey$); 
		Entigrator entigrator=console.getEntigrator(entihome$);
	    Sack entity=entigrator.getEntityAtKey(entityKey$);
        Core[]	ca=entity.elementGet("jfacet");
        if(ca==null)
        	return null;
	     JFacetOpenItem openItem;
	     String extension$;
	     Properties itemLocator;
	    for(Core aCa:ca){
			try{
				itemLocator=new Properties();
			    itemLocator.setProperty(Entigrator.ENTIHOME, entihome$);
			    itemLocator.setProperty(EntityHandler.ENTITY_KEY, entityKey$);
				itemLocator.setProperty(JFacetOpenItem.FACET_HANDLER_CLASS, aCa.name);
				extension$=entity.getElementItemAt("fhandler", aCa.name);
				if(extension$!=null)
				  itemLocator.setProperty(BaseHandler.HANDLER_LOCATION,extension$);
		     	itemLocator.setProperty(BaseHandler.HANDLER_CLASS, aCa.value);
     	//System.out.println("EntityFacetPanel:getFacetOpenItems:handler class="+aCa.value);
		     	String itemLocator$= Locator.toString(itemLocator);
		     	openItem=JFacetOpenItem.getFacetOpenItemInstance(console,itemLocator$);
		     	
			    if(openItem!=null){
			    //	System.out.println("EntityFacetPanel:getFacetOpenItems:open item="+openItem.getFacetName());    	
		     	   foil.add(openItem);
			    }
			  //  else
			   // 	System.out.println("EntityFacetPanel:getFacetOpenItems:cannot get open item for the class="+aCa.value);
			}catch(Exception ee){
				LOGGER.info("ee:"+ee.toString());
			}
		}
	  //  System.out.println("EntityFacetPanel:getFacetOpenItems:FINISH");
     return foil.toArray(new JFacetOpenItem[0]);	
	}catch(Exception e){
		LOGGER.info("e:"+e.toString());
		return null;
	}
}
private String[] listFacetOpenItems(){
	try{
		
		ArrayList<String>foil=new ArrayList<String>();
		Properties locator=Locator.toProperties(locator$);
		entihome$=locator.getProperty(Entigrator.ENTIHOME);
		entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		//System.out.println("EntityFacetPanel:getFacetOpenItems:entity key="+entityKey$); 
		Entigrator entigrator=console.getEntigrator(entihome$);
	    Sack entity=entigrator.getEntityAtKey(entityKey$);
        Core[]	ca=entity.elementGet("jfacet");
        if(ca==null)
        	return null;
	     JFacetOpenItem openItem;
	     String extension$;
	     Properties itemLocator;
	     String itemLocator$;
	    for(Core aCa:ca){
			try{
				openItem=(JFacetOpenItem )JConsoleHandler.getHandlerInstance(entigrator,aCa.value);
				
				if(debug)
					System.out.println("JEntityFacetPanel:listFacetOpenItems:handler="+aCa.value+"  locator="+openItem.getLocator());
				itemLocator=Locator.toProperties(openItem.getLocator());
			    itemLocator.setProperty(Entigrator.ENTIHOME, entihome$);
			    itemLocator.setProperty(EntityHandler.ENTITY_KEY, entityKey$);
			    itemLocator$= Locator.toString(itemLocator);
			    openItem.instantiate(entigrator, itemLocator$);
     	        if(openItem.isRemovable())
     	        	itemLocator.setProperty(Locator.LOCATOR_CHECKABLE,Locator.LOCATOR_TRUE);
			    //System.out.println("EntityFacetPanel:getFacetOpenItems:handler class="+aCa.value);
		     	 
		     	if(debug)
					System.out.println("JEntityFacetPanel:listFacetOpenItems:item locator="+itemLocator$);
				
		     	foil.add(itemLocator$);
			  //  else
			   // 	System.out.println("EntityFacetPanel:getFacetOpenItems:cannot get open item for the class="+aCa.value);
			}catch(Exception ee){
				LOGGER.info("ee:"+ee.toString());
			}
		}
	  //  System.out.println("EntityFacetPanel:getFacetOpenItems:FINISH");
     return foil.toArray(new String[0]);	
	}catch(Exception e){
		LOGGER.info("e:"+e.toString());
		return null;
	}
}
/**
 *Get the context menu.
 *@return the context menu.
 */
@Override
	public JMenu getContextMenu() {
	   menu=super.getContextMenu();
	   menu.addSeparator();
	   JMenuItem showStructure = new JMenuItem("Structure");
	   showStructure.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String locator$=getLocator();
				JEntityStructurePanel esp=new JEntityStructurePanel();
				esp.instantiate(console, locator$);
				String espLocator$=esp.getLocator();
				espLocator$=Locator.append(espLocator$, Entigrator.ENTIHOME, entihome$);
				espLocator$=Locator.append(espLocator$, EntityHandler.ENTITY_KEY, entityKey$);
				JConsoleHandler.execute(console, espLocator$);
		      }
		} );
		menu.add(showStructure);
		JMenuItem showDigest = new JMenuItem("Digest");
		   showDigest.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String locator$=getLocator();
					JEntityDigestDisplay edd=new JEntityDigestDisplay();
					edd.instantiate(console, locator$);
					String eddLocator$=edd.getLocator();
					eddLocator$=Locator.append(eddLocator$, Entigrator.ENTIHOME, entihome$);
					eddLocator$=Locator.append(eddLocator$, EntityHandler.ENTITY_KEY, entityKey$);
					JConsoleHandler.execute(console, eddLocator$);
			      }
			} );
			menu.add(showDigest);
			menu.addSeparator();
			 addFacets = new JMenuItem("Add facets");
			   addFacets.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						String locator$=getLocator();
						JEntityAddFacets addFacets=new JEntityAddFacets();
						addFacets.instantiate(console, locator$);
						String facetSelector$=addFacets.getLocator();
						facetSelector$=Locator.append(facetSelector$, Entigrator.ENTIHOME, entihome$);
						facetSelector$=Locator.append(facetSelector$, EntityHandler.ENTITY_KEY, entityKey$);
						JConsoleHandler.execute(console, facetSelector$);
				      }
				} );
				menu.add(addFacets);
				JMenuItem doneItem = new JMenuItem("Done");
				   doneItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							String locator$=getLocator();
							String requesterResponseLocator$=Locator.getProperty(locator$,JRequester.REQUESTER_RESPONSE_LOCATOR );
							if(requesterResponseLocator$==null)
							    console.back();
					       else{
					    	 try{
					    	   byte[] ba=Base64.decodeBase64(requesterResponseLocator$);
					    	  String responseLocator$=new String(ba,"UTF-8");
//    				    	  System.out.println("EntityfacetPanel:done:response locator="+responseLocator$);
					    	  JConsoleHandler.execute(console,responseLocator$);
					    	 }catch(Exception ee){
					    		 LOGGER.info(ee.toString());
					    	 }
					       }
				   }
					} );
					menu.add(doneItem);	
	   menu.addMenuListener(new MenuListener(){
			@Override
			public void menuSelected(MenuEvent e) {
			//System.out.println("EntityEditor:getConextMenu:menu selected");
			if(removeFacets!=null) 
			menu.remove(removeFacets);
			if(copyFacets!=null) 
				menu.remove(copyFacets);
			if(hasSelectedItems()){
			copyFacets = new JMenuItem("Copy facets");
			   copyFacets.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
				     copyFacets();
					}
				} );
				menu.add(copyFacets);
			}
			if(hasSelectedRemovableFacets()){
				   removeFacets = new JMenuItem("Remove facets");
				   removeFacets.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							int response = JOptionPane.showConfirmDialog(console.getContentPanel(), "Delete ?", "Confirm",
							        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						   if (response == JOptionPane.YES_OPTION){ 
							   removeFacets();	
							   JEntityFacetPanel efp=new JEntityFacetPanel();
								String efpLocator$=efp.getLocator();
								efpLocator$=Locator.append(efpLocator$, Entigrator.ENTIHOME,entihome$);
								efpLocator$=Locator.append(efpLocator$, EntityHandler.ENTITY_KEY,entityKey$);
								JConsoleHandler.execute(console, efpLocator$);
							
						   }
							
						}
					} );
					menu.add(removeFacets);
			}
			}
			@Override
			public void menuDeselected(MenuEvent e) {
			}
   		@Override
			public void menuCanceled(MenuEvent e) {
			}
		});
		return menu;
	}
private void copyFacets(){
	try{
		console.clipboard.clear();
		String[] sa=listSelectedItems();
		if(sa!=null)
			for(String aSa:sa)
				console.clipboard.putString(aSa);
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
}
private void  removeFacets(){
	try{
	//	System.out.println("EntityFacetPanel:removeFacets:BEGIN");
		JItemPanel[] ipa=getItems();
		   if(ipa==null)
			   return;
		   String foiLocator$;
		   Properties foiLocator;
		   JFacetOpenItem foi; 
		   for(JItemPanel aIpa:ipa){
			  if( aIpa.isChecked()){
				try{
				  foiLocator$=aIpa.getLocator();
				  foiLocator=Locator.toProperties(foiLocator$);
				  foiLocator.setProperty(Entigrator.ENTIHOME,entihome$);
				  foiLocator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
				  foi=JFacetOpenItem.getFacetOpenItemInstance(console, Locator.toString(foiLocator));
						  if(foi.isRemovable()){
					        foi.removeFacet();
						  }
				}catch(Exception ee){
					System.out.println("JEntityFacetPanel:removeFacets:"+ee.toString());
				}
				  
			  }
		   }
	}catch(Exception ee){
		LOGGER.severe(ee.toString());
	}    
}
/**
 *Get the subtitle
 *@return the subtitle .
 */
@Override
public String getSubtitle() {
	try{
	return console.getEntigrator(entihome$).getBaseName();
	}catch(Exception e ){
		return null;
	}
}
private boolean hasSelectedRemovableFacets(){
     if(!hasSelectedItems())
    	 return false;
     JItemPanel[] ipa=getItems();
     if(ipa==null)
    	 return false;
     String locator$;
     for(JItemPanel ip:ipa){
    	 if(ip.isChecked()){
    		 locator$=ip.getLocator();
    		 if(locator$!=null&&Locator.LOCATOR_TRUE.equals(Locator.getProperty(locator$,Locator.LOCATOR_CHECKABLE)))
    			 return true;
    	 }
     }
     return false;
}
@Override
public void activate() {
	// TODO Auto-generated method stub
	
}
@Override
public String getWebView(Entigrator entigrator,String locator$) {
	try{
		if(debug)
			System.out.println("JEntityFacetPanel:BEGIN:locator="+locator$);
			
		Properties locator=Locator.toProperties(locator$);
		String basesList$=locator.getProperty(WContext.BASES);
		String[] sa=Locator.toArray(basesList$);
		String webHome$=locator.getProperty(WContext.WEB_HOME);
	
		String entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
		String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		/*
		String action$=locator.getProperty(JRequester.REQUESTER_ACTION);
		if(JViewFacetOpenItem.ACTION_OPEN_ID.equals(action$)){
			try{
				String idName$=JViewFacetOpenItem.ID_NAME;
				String idValue$=JViewFacetOpenItem.ID_VALUE;
				Sack id2key=entigrator.getEntityAtKey(entigrator.indx_keyAtLabel("id2key"));
				entityKey$=id2key.getElementItemAt(idName$, idValue$);
			}catch(Exception ee){}
		}
		*/
		if(entityKey$==null)
			entityKey$=entigrator.indx_keyAtLabel(entityLabel$);
		if(entityLabel$==null)
			entityLabel$=entigrator.indx_getLabel(entityKey$);
		String webRequester$=locator.getProperty(WContext.WEB_REQUESTER);
		
		if(debug)
		System.out.println("JEntityFacetPanel:web home="+webHome$+ " web requester="+webRequester$);
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
	    sb.append("</strong></td></tr><tr><td>Entity: </td><td><strong>");
	    sb.append(entityLabel$);
	    sb.append("</strong></td></tr>");
	    sb.append("<tr><td>Context:</td><td><strong>Facets");
	    sb.append("</strong></td></tr>");
	    sb.append("</table>");
	    Sack entity=entigrator.getEntityAtKey(entityKey$);
        Core[]	ca=entity.elementGet("jfacet");
        
        if(ca!=null){
        	sb.append("<script>");
        	String foiClass$;
        	String foiExtension$;
        	String foiTitle$;
        	
        	JFacetOpenItem facetOpenItem;
        	String foiIcon$;
        	Properties foiLocator=new Properties();
        	foiLocator.setProperty(WContext.WEB_HOME, webHome$);
    	    foiLocator.setProperty(Entigrator.ENTIHOME, entigrator.getEntihome());
            Hashtable<String,String> tab=new Hashtable<String,String>();
            ArrayList <String>sl=new ArrayList<String>();
            foiLocator.setProperty(BaseHandler.HANDLER_CLASS,JEntityEditor.class.getName());
            foiLocator.setProperty(EntityHandler.ENTITY_LABEL,entityLabel$);
            foiLocator.setProperty(WContext.WEB_REQUESTER,JEntityFacetPanel.class.getName());
            
            foiIcon$=Support.readHandlerIcon(null,JEntityPrimaryMenu.class, "entity.png");
            foiTitle$="Entity";
            String foiItem$=getItem(foiIcon$, webHome$,foiTitle$,Locator.toString(foiLocator));
            sl.add(foiTitle$);
            tab.put(foiTitle$,foiItem$ );
            for(Core c:ca){
        		try{
        		foiClass$=c.value;
        		sb.append("window.localStorage.setItem(\"back."+foiClass$+"\",\""+this.getClass().getName()+"\");");
        		foiExtension$=entity.getElementItemAt("fhandler", c.name);
        		if(debug)
        		System.out.println("JEntityFacetPanel:getWebView: foi class="+foiClass$+ " foi extension="+foiExtension$);
        		foiLocator.setProperty(BaseHandler.HANDLER_CLASS,foiClass$);
        		if(JQueryFacetOpenItem.class.getName().equals(foiClass$))
        			sb.append("window.localStorage.setItem('query_requester',\""+webRequester$+"\");");
        	    if(foiExtension$==null||"null".equals(foiExtension$)){
       			facetOpenItem=(JFacetOpenItem)Class.forName(foiClass$).newInstance();
       			foiLocator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
       			foiLocator.setProperty(Locator.LOCATOR_ICON_CLASS,foiClass$);
       			foiLocator.setProperty(Locator.LOCATOR_ICON_FILE,facetOpenItem.getFacetIconName());

        	}
       		else{
       			Object o=ExtensionHandler.loadHandlerInstance(entigrator, foiExtension$, foiClass$);
       			if(debug)
       			System.out.println("JEntityFacetPanel:getWebView: o="+o.getClass().getName());
       			facetOpenItem= (JFacetOpenItem)ExtensionHandler.loadHandlerInstance(entigrator, foiExtension$, foiClass$);
       			foiLocator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
       			foiLocator.setProperty(Locator.LOCATOR_ICON_CLASS,foiClass$);
       			foiLocator.setProperty(Locator.LOCATOR_ICON_FILE,facetOpenItem.getFacetIconName());
       			foiLocator.setProperty(BaseHandler.HANDLER_LOCATION,foiExtension$);
       		}
        	foiLocator.setProperty(WEB_REQUESTER,JEntityFacetPanel.class.getName());
 			foiTitle$=facetOpenItem.getFacetName();
 			if(debug)
 			System.out.println("JEntityFacetPanel:getWebView: foi title="+foiTitle$+" icon="+foiIcon$);
 			foiIcon$=JConsoleHandler.getIcon(entigrator,Locator.toString(foiLocator));
 			foiItem$=getItem(foiIcon$, webHome$,foiTitle$,Locator.toString(foiLocator));
 			sl.add(foiTitle$);
 			tab.put(foiTitle$, foiItem$);
        	  }catch(Exception ee){
        		  System.out.println("JEntityFacetPanel:getWebView:"+ee.toString());
        	  }
        	}
            sb.append("</script>");
            Collections.sort(sl);
            for(String s:sl)
            	sb.append(tab.get(s)+"<br>");
            	
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
public String getWebConsole(Entigrator entigrator,String locator$) {
	// TODO Auto-generated method stub
	return null;
}
private String getItem(String icon$, String url$, String title$,String locator$){
	  String iconTerm$="<img src=\"data:image/png;base64,"+icon$+
			  "\" width=\"24\" height=\"24\" alt=\""+title$+"\">";
	  return iconTerm$+"<a href=\""+url$+"?"+WContext.WEB_LOCATOR+"="+Base64.encodeBase64URLSafeString(locator$.getBytes())+"\" >"+" "+title$+"</a>";
}
}
