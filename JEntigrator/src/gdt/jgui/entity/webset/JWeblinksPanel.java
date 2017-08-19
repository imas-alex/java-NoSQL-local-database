package gdt.jgui.entity.webset;
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
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.logging.Logger;

import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Identity;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JItemPanel;
import gdt.jgui.console.JItemsListPanel;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.console.ReloadDialog;
import gdt.jgui.console.WContext;
import gdt.jgui.entity.JEntitiesPanel;
import gdt.jgui.entity.folder.JFileOpenItem;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.apache.commons.codec.binary.Base64;
/**
 * This class represents a list of web links assigned to the entity.
 * @author imasa
 *
 */
public class JWeblinksPanel extends JItemsListPanel implements WContext {
	private static final long serialVersionUID = 1L;
	/**
	 * The tag of a web link key.
	 */
	public static final String WEB_LINK_KEY="web link key" ;
	/**
	 * The tag of a web link URL.
	 */
	public static final String WEB_LINK_URL="web link URL" ;
	/**
	 * The tag of a web link name.
	 */
	public static final String WEB_LINK_NAME="web link name" ;
	private static final String WEB_LINK_LOGIN="web link login" ;
	private static final String WEB_LINK_PASSWORD="web link password" ;
	/**
	 * Indicates the locator type as a web link.
	 */
	public static final String LOCATOR_TYPE_WEB_LINK="locator type web link";
String entihome$;
String entityKey$;
String entityLabel$;
JMenuItem[] mia;
String requesterResponseLocator$;
String message$;
Sack entity;
boolean debug=false;
boolean ignoreOutdate=false;
/**
 * The default constructor.
 */
public JWeblinksPanel() {
		super();
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
		    locator.setProperty(Locator.LOCATOR_TITLE, getTitle());
		    locator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
	    	locator.setProperty(Locator.LOCATOR_ICON_CLASS,getClass().getName());
	    	locator.setProperty(Locator.LOCATOR_ICON_FILE,"globe.png"); 
		    locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
		    locator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
		    return Locator.toString(locator);
	}
	/**
	 * Create the context.
	 * @param console the main console.
	 * @param locator$ the locator string.
	 * @return the procedure context.
	 */
	@Override
	
public JContext instantiate(JMainConsole console, String locator$) {
	    
		try{
			 this.console=console;
			 this.locator$=locator$;
			 Properties locator=Locator.toProperties(locator$);
			 entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			 entihome$=locator.getProperty(Entigrator.ENTIHOME);
			 entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
			 Entigrator entigrator=console.getEntigrator(entihome$);
			 if(entityLabel$==null)
				 entityLabel$=entigrator.indx_getLabel(entityKey$);
			  entity=entigrator.getEntityAtKey(entityKey$);
    		 JItemPanel[] ipa=getItems(console,entity);
        	 putItems(ipa);
        	return this;
        }catch(Exception e){
        Logger.getLogger(getClass().getName()).severe(e.toString());
        }
        return null;
        }
private boolean hasItemsToPaste(){
	String[] sa=console.clipboard.getContent();
	if(sa==null)
		return false;
	for(String aSa:sa)
	  if(LOCATOR_TYPE_WEB_LINK.equals(Locator.getProperty(aSa, Locator.LOCATOR_TYPE)))
		  return true;
	return false;
}
private String[] getItemsToPaste(){
	String[] sa=console.clipboard.getContent();
	if(sa==null)
		return null;
	ArrayList<String>sl=new ArrayList<String>();
	for(String aSa:sa)
	  if(LOCATOR_TYPE_WEB_LINK.equals(Locator.getProperty(aSa, Locator.LOCATOR_TYPE)))
		  sl.add(aSa);
	return sl.toArray(new String[0]);
}
private JItemPanel[] getItems(JMainConsole console,Sack entity){
	try{
		ArrayList<JWeblinkItem>ipl=new ArrayList<JWeblinkItem>();
		Core[] ca=entity.elementGet("web");
		if(ca!=null){
			ca=Core.sortAtType(ca);
			JWeblinkItem ip;
			String ipLocator$;
			Properties ipLocator;
			String icon$;
			for(Core aCa:ca){
				  try{
					  ipLocator$=getLocator();
					  ipLocator=Locator.toProperties(ipLocator$);	
					  ipLocator.setProperty(Entigrator.ENTIHOME, entihome$);
					  ipLocator.setProperty(EntityHandler.ENTITY_KEY, entityKey$);
					  ipLocator.setProperty(BaseHandler.HANDLER_METHOD,JWeblinkEditor.METHOD_BROWSE_URL);
					  ipLocator.setProperty(WEB_LINK_KEY,aCa.name);
					  if(aCa.type!=null&&!"null".equals(aCa.type))
						  ipLocator.setProperty(WEB_LINK_NAME,aCa.type);
					  if(aCa.value!=null&&!"null".equals(aCa.value))
						  ipLocator.setProperty(WEB_LINK_URL,aCa.value);
					 String title$="Web";
					  if(aCa.type!=null)
						  title$=aCa.type;
					  else
						  if(aCa.value!=null)
							  title$=aCa.value;
					  ipLocator.setProperty(Locator.LOCATOR_TITLE, title$);
					  ipLocator.setProperty(Locator.LOCATOR_TYPE, LOCATOR_TYPE_WEB_LINK);
					  ipLocator.setProperty(Locator.LOCATOR_CHECKABLE, Locator.LOCATOR_TRUE);
					  ipLocator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_ENTITY);
					  ipLocator.setProperty(Locator.LOCATOR_ICON_ELEMENT,"web.icon");
					  ipLocator.setProperty(Locator.LOCATOR_ICON_CORE,aCa.name);
					  ipLocator.setProperty(Locator.LOCATOR_ICON_FIELD,Locator.LOCATOR_ICON_FIELD_VALUE);
					  Core login=entity.getElementItem("web.login", aCa.name);
					  if(login!=null){
						  if(login.type!=null&&!"null".equals(login.type))
							  ipLocator.setProperty(WEB_LINK_LOGIN,login.type);
						  if(login.value!=null&&!"null".equals(login.value))
							  ipLocator.setProperty(WEB_LINK_PASSWORD,login.value);
					  }
					  ipLocator$=Locator.toString(ipLocator);
					  ip=new JWeblinkItem(console,ipLocator$); 
					  ipl.add(ip);	  
					   }catch(Exception ee){
						   Logger.getLogger(JEntitiesPanel.class.getName()).info(ee.toString());
					   }
			}
		}
		Collections.sort(ipl,new ItemPanelComparator());
		return ipl.toArray(new JWeblinkItem[0]);
	}catch(Exception e){
        Logger.getLogger(getClass().getName()).severe(e.toString());
    }
     return null;	
	}
/**
 * Get the context menu.
 * @return the context menu.
 */
@Override
public JMenu getContextMenu() {
menu=super.getContextMenu();
int cnt=menu.getItemCount();
mia=new JMenuItem[cnt];
for (int i=0;i<cnt;i++)
	mia[i]=menu.getItem(i);
menu.addMenuListener(new MenuListener(){
	@Override
	public void menuSelected(MenuEvent e) {
	//System.out.println("WeblinkPanel:getConextMenu:menu selected");
	 menu.removeAll();
	 if(mia!=null){
		 for(JMenuItem mi:mia)
			 menu.add(mi);
	 menu.addSeparator();
	 }
	 if(hasSelectedItems()){
	JMenuItem deleteItem = new JMenuItem("Delete");
	 deleteItem.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			 
			int response = JOptionPane.showConfirmDialog(console.getContentPanel(), "Delete ?", "Confirm",
				        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			   if (response == JOptionPane.YES_OPTION) {
				  String[] sa=JWeblinksPanel.this.listSelectedItems();
				  if(sa==null)
					  return;
				  String webLinkKey$;
				  Entigrator entigrator=console.getEntigrator(entihome$);
				  Sack entity=entigrator.getEntityAtKey(entityKey$);
				  for(String aSa:sa){
					  webLinkKey$=Locator.getProperty(aSa, WEB_LINK_KEY);
					  if(webLinkKey$==null)
						  continue;
                   entity.removeElementItem("web", webLinkKey$);
                   entity.removeElementItem("web.login", webLinkKey$);
                   entity.removeElementItem("web.icon", webLinkKey$);
				  }
                   entigrator.ent_replace(entity);  
                   
				   JConsoleHandler.execute(console,locator$);
			   }
			   }
	});
	menu.add(deleteItem);
	JMenuItem copyItem = new JMenuItem("Copy");
	copyItem.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JItemPanel[] ipa=JWeblinksPanel.this.getItems();
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
	 }
	JMenuItem newItem = new JMenuItem("New");
	newItem.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(debug)
			System.out.println("WeblinksPanel:new:"+locator$);
			Entigrator entigrator=console.getEntigrator(entihome$);	
			Sack entity=entigrator.getEntityAtKey(entityKey$);
			if(!entity.existsElement("web"))
					entity.createElement("web");
			String webLinkKey$=Identity.key();
			entity.putElementItem("web", new Core("Google",webLinkKey$,"http://www.google.com"));
			if(!entity.existsElement("web.icon"))
				entity.createElement("web.icon");
			String icon$=Support.readHandlerIcon(null,JEntitiesPanel.class, "globe.png");
			entity.putElementItem("web.icon", new Core(null,webLinkKey$,icon$));
			entigrator.ent_replace(entity);
			JWeblinkEditor wle=new JWeblinkEditor();
			String wleLocator$=wle.getLocator();
			wleLocator$=Locator.append(wleLocator$, Entigrator.ENTIHOME, entihome$);
			wleLocator$=Locator.append(wleLocator$,EntityHandler.ENTITY_KEY,entityKey$);
			wleLocator$=Locator.append(wleLocator$,WEB_LINK_KEY,webLinkKey$);
			String requesterResponseLocator$=Locator.compressText(getLocator());
			wleLocator$=Locator.append(wleLocator$,JRequester.REQUESTER_RESPONSE_LOCATOR,requesterResponseLocator$);
			JConsoleHandler.execute(console, wleLocator$);
			
		}
	} );
	menu.add(newItem);
	if(hasItemsToPaste()){
		JMenuItem pasteItem = new JMenuItem("Paste");
		pasteItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				 String[] sa=getItemsToPaste();
				 Entigrator entigrator=console.getEntigrator(entihome$);
				 Sack entity=entigrator.getEntityAtKey(entityKey$);
				 if(!entity.existsElement("web"))
					 entity.createElement("web");
				 if(!entity.existsElement("web.icon"))
					 entity.createElement("web.icon");
				 if(!entity.existsElement("web.login"))
					 entity.createElement("web.login");
				 Properties itemLocator;
				 String webLinkKey$;
				 String webLinkUrl$;
				 String webLinkName$;
				 String webLinkIcon$;
				 String webLinkLogin$;
				 String webLinkPassword$;
				 for(String aSa:sa){
					 itemLocator=Locator.toProperties(aSa);
					 webLinkKey$=itemLocator.getProperty(WEB_LINK_KEY);
					 webLinkUrl$=itemLocator.getProperty(WEB_LINK_URL);
					 webLinkName$=itemLocator.getProperty(WEB_LINK_NAME);
					 webLinkIcon$=JConsoleHandler.getIcon(entigrator, aSa);
					 webLinkLogin$=itemLocator.getProperty(WEB_LINK_LOGIN);
					 webLinkPassword$=itemLocator.getProperty(WEB_LINK_PASSWORD);
					 if(webLinkKey$==null||webLinkUrl$==null)
						 continue;
					 entity.putElementItem("web", new Core(webLinkName$,webLinkKey$,webLinkUrl$));
					 if(webLinkLogin$!=null||webLinkPassword$!=null)
						 entity.putElementItem("web.login", new Core(webLinkLogin$,webLinkKey$,webLinkPassword$));
					 if(webLinkIcon$!=null)
						 entity.putElementItem("web.icon", new Core(null,webLinkKey$,webLinkIcon$));
				 }
				 entigrator.ent_replace(entity);
				 JConsoleHandler.execute(console, getLocator());
			}
		} );
		menu.add(pasteItem);
	}
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
						Logger.getLogger(JWeblinksPanel.class.getName()).severe(ee.toString());
					}
				}else{
					Entigrator entigrator=console.getEntigrator(entihome$);
					entigrator.ent_replace(entity);
					console.back();
				}
		}
	} );
	menu.add(doneItem);
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
/**
 * Get context title.
 * @return the context title.
 */	
@Override
	public String getTitle() {
		
		if(message$==null)
		return "Web links";
		else
			return "Web links"+message$;
		
	}
/**
 * Get context subtitle.
 * @return the context subtitle.
 */
	@Override
	public String getSubtitle() {
		return entityLabel$;
	}
	/**
	 * Get context type.
	 * @return the context type.
	 */
	@Override
	public String getType() {
		return "webset";
	}
	/**
	 * No action.
	 */
	@Override
	public void close() {
	}
	/**
	 * Open URL in the system browser. 
	 * @param console the main console
	 * @param locator$ the locator string
	 */
	public void browseUrl(JMainConsole console,String locator$){
		try{
			try{
				Properties locator=Locator.toProperties(locator$);
				String url$=locator.getProperty(JWeblinksPanel.WEB_LINK_URL);
//				System.out.println("weblinkEditor:browseUrl:url="+url$);
				Desktop.getDesktop().browse(new URI(url$));
				}catch(Exception ee){
					Logger.getLogger(JFileOpenItem.class.getName()).info(ee.toString());
				}
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		}
	}
	@Override
	public void activate() {
		if(debug)
			System.out.println("JWeblinksPanel:activate:begin");
		if(ignoreOutdate){
			ignoreOutdate=false;
			return;
		}
		Entigrator entigrator=console.getEntigrator(entihome$);
		if(entity==null)
			return;
		if(!entigrator.ent_entIsObsolete(entity)){
			System.out.println("JWeblinksPanel:activate:up to date");
			return;
		}
		int n=new ReloadDialog(this).show();
		if(2==n){
			ignoreOutdate=true;
			return;
		}
		if(1==n){
			entigrator.ent_replace(entity);
			
		}
		if(0==n){
			 JConsoleHandler.execute(console, getLocator());
			}
		
		
	}
	@Override
	public String getWebView(Entigrator entigrator, String locator$) {
		try{
			if(debug)
				System.out.println("JWeblinksPanel:locator="+locator$);
			StringBuffer sb=new StringBuffer();
			sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
			sb.append("<html>");
			Properties locator=Locator.toProperties(locator$);
			String url$=locator.getProperty(JWeblinksPanel.WEB_LINK_URL);
			if(debug)
					System.out.println("JWeblinksPanel:url="+url$);
				sb.append("<body>");
				sb.append("<script>");
				sb.append("window.location.assign(\""+url$+"\");");
				sb.append("</script>");
				sb.append("</body>");
				sb.append("</html>");
				return sb.toString();
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		}
		return null;	
	}
	@Override
	public String getWebConsole(Entigrator entigrator, String locator$) {
		// TODO Auto-generated method stub
		return null;
	}
}
