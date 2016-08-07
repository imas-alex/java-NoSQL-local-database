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
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JFacetOpenItem;
import gdt.jgui.console.JItemPanel;
import gdt.jgui.console.JItemsListPanel;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
/**
 * Display a list of all facets assigned to the entity.
 * @author imasa
 *
 */
public class JEntityFacetPanel extends JItemsListPanel {
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
		       Entigrator entigrator=console.getEntigrator(entihome$);
		       String icon$=Support.readHandlerIcon(entigrator, getClass(), "facet.png");
		       if(icon$!=null)
		          locator.setProperty(Locator.LOCATOR_ICON, icon$);
		    }
		    if(entityKey$!=null)
			       locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
		    if(entityLabel$!=null)
			       locator.setProperty(EntityHandler.ENTITY_LABEL,entityLabel$);
		    if(entityIcon$!=null)
			       locator.setProperty(Locator.LOCATOR_ICON,entityIcon$);
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
		 entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
		 entityIcon$=locator.getProperty(Locator.LOCATOR_ICON);
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
				JFacetOpenItem[] ipa=getFacetOpenItems();
			if(ipa!=null){
		//	System.out.println("JEntityFacetPanel:instantiate:ipa="+ipa.length);	
			for(JFacetOpenItem ip:ipa){
		//		System.out.println("EntityFacetPanel:instantiate:ipa locator="+ip.getLocator());
				  ipl.add(ip);
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
		return "Entity facet panel";
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
	    Entigrator entigrator=console.getEntigrator(entihome$);
	    Sack entity=entigrator.getEntityAtKey(entityKey$);
        Core[]	ca=entity.elementGet("jfacet");
        if(ca==null)
        	return null;
	     JFacetOpenItem openItem;
	     String extension$;
	     Properties itemLocator;
	   //  FacetHandler fh;
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
					     removeFacets();
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
		   ArrayList<JFacetOpenItem>foil=new ArrayList<JFacetOpenItem>();
		   for(JItemPanel aIpa:ipa){
			  if( aIpa.isChecked()){
				  if(((JFacetOpenItem)aIpa).isRemovable())
					 foil.add((JFacetOpenItem)aIpa);
				  else
					  aIpa.setChecked(false);
			  }
		   }
		   JFacetOpenItem[] foia=foil.toArray(new JFacetOpenItem[0]);
		   if(foia.length<1)
			   return;
		   int response = JOptionPane.showConfirmDialog(this, "Delete selected facets ?", "Confirm",
		       JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		   if (response != JOptionPane.YES_OPTION) 
		       return;
		   for(JFacetOpenItem foi:foia)
			 	   foi.removeFacet();
		  console.putContext(instantiate(console,this.locator$), this.locator$);
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
     for(JItemPanel ip:ipa){
    	 if(ip.isChecked())
    		 if(((JFacetOpenItem)ip).isRemovable())
    		     return true;
     }
     return false;
}
}
