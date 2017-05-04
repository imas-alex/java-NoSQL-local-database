package gdt.jgui.base;
import gdt.data.entity.ArchiveHandler;
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
import gdt.jgui.console.JRequester;
import gdt.jgui.console.WContext;
import gdt.jgui.console.WUtils;
import gdt.jgui.entity.JEntityPrimaryMenu;
import gdt.jgui.entity.procedure.JProcedurePanel;
import gdt.jgui.entity.query.JQueryPanel;
import gdt.jgui.tool.JTextEditor;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.BoxLayout;

import javax.swing.JMenu;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;


/**
* This context displays all databases located in
* the given directory.
* @author  Alexander Imas
* @version 1.0
* @since   2016-03-11
*/
public class JBasesPanel extends JItemsListPanel implements JRequester,WContext{
	public static final String BASES_LIST="bases list";
private static final long serialVersionUID = 1L;
private Logger LOGGER=Logger.getLogger(JBasesPanel.class.getName());
private String entiroot$;
public static boolean debug=false;
/**
 * Default constructor
 *  
 */
public JBasesPanel(){
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
}
/**
 * Get context menu. 
 * @return null.
 */	
	@Override
	public JMenu getContextMenu() {
		return null;
	}
	/**
	 * Get context locator. 
	 * @return the locator.
	 */	
	@Override
	public String getLocator() {
	    Properties locator=new Properties();
	    locator.setProperty(Locator.LOCATOR_TYPE, JContext.CONTEXT_TYPE);
	    locator.setProperty(JContext.CONTEXT_TYPE,getType());
	    locator.setProperty(BaseHandler.ENTIROOT,entiroot$);
	    File file = new File(entiroot$);
	    locator.setProperty(Locator.LOCATOR_TITLE, file.getPath());
	    //String icon$=Support.readHandlerIcon(null,JBasesPanel.class, "bases.png");
	    /*
	    if(icon$!=null)
	    	locator.setProperty(Locator.LOCATOR_ICON,icon$);
	    	*/
	    locator.setProperty(Locator.LOCATOR_ICON_CONTAINER, Locator.LOCATOR_ICON_CONTAINER_CLASS);
	    locator.setProperty(Locator.LOCATOR_ICON_CLASS,getClass().getName());
	    locator.setProperty(Locator.LOCATOR_ICON_FILE, "bases.png"); 
	    locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
	    locator.setProperty(BaseHandler.HANDLER_CLASS,JBasesPanel.class.getName());
	    return Locator.toString(locator);
	}
	/**
	 * Create the context.
	 *  @param console the main application console
	 *  @param locator$ the locator string.
	 * @return the context.
	 */		
	@Override
	public JContext instantiate(JMainConsole console, String locator$) {
        try{
        	if(debug)
        	System.out.println("JBasesPanel:instantiate:locator:"+locator$);
        	Properties locator=Locator.toProperties(locator$);
        	entiroot$=locator.getProperty(BaseHandler.ENTIROOT);
        	JConsoleHandler jch=new JConsoleHandler ();
        	JItemPanel[] ipl=jch.listBases(console, entiroot$);
        	putItems(ipl);
        	return this;
        }catch(Exception e){
        LOGGER.severe(e.toString());
        return null;
        }
	}
	/**
	 * Get context title.
	 * @return the title string.
	 */
	@Override
	public String getTitle() {
		try{
		File entiroot=new File(entiroot$);
		return entiroot.getName();
		}catch(Exception e){
		return "Entigrator";
		}
	}
	/**
	 * Get context type.
	 * @return the type string.
	 */
	@Override
	public String getType() {
		return "Bases";
	}
	/**
	 * Complete the context after
	 * remove it from the main console.
	 */	
	@Override
	public void close() {
		// TODO Auto-generated method stub
	}
	/**
	 * Get context subtitle.
	 * @return the subtitle string.
	 */
	@Override
	public String getSubtitle() {
		return entiroot$;
	}
	/**
	 * Response on menu action
	 * @param console main console
	 * @param locator$ the locator string.
	 */	
	@Override
	public void response(JMainConsole console, String locator$) {
	try{
		if(debug)
		  System.out.println("JBasesPanel:response:locator="+locator$);
		Properties locator=Locator.toProperties(locator$);
		String text$=locator.getProperty(JTextEditor.TEXT);
		String entiroot$=locator.getProperty(BaseHandler.ENTIROOT);
	    BaseHandler.createBlankDatabase(entiroot$+"/"+text$);
	    if(debug)
	     System.out.println("JBasesPanel:response:database created");
	    Entigrator entigrator=new Entigrator(new String[]{entiroot$+"/"+text$});
	    entigrator.indx_reindex(null);
	    if(debug)
	    System.out.println("JBasesPanel:response:index rebuilt");
			String [] sa=entigrator.indx_listEntities();
			Sack entity;
			String entityLocator$;
			for(String s:sa){
				entity=entigrator.getEntityAtKey(s);
				if(entity==null)
					continue;
				if(!"extension".equals(entity.getProperty("entity")))
					continue;
				entityLocator$=EntityHandler.getEntityLocator(entigrator, entity);
				JEntityPrimaryMenu.reindexEntity(console,entityLocator$);
			}
		if(debug)	
			 System.out.println("JBasesPanel:response:entities reindexed");
		    console.putEntigrator(entigrator);
		    refreshIconsFolder(console,entigrator.getEntihome());
		    refreshAllEntitiesQuery(console,entigrator.getEntihome());
		    refreshListProcedure(console,entigrator.getEntihome());
		 if(debug)   
		    System.out.println("JBasesPanel:response:refreshed queries and procedures");
		    JBaseNavigator jbn=new JBaseNavigator();
		    String jbnLocator$=jbn.getLocator();
		    jbnLocator$=Locator.append( jbnLocator$, Entigrator.ENTIHOME,entigrator.getEntihome());
		   JConsoleHandler.execute(console, jbnLocator$); 
		    
	}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
	}
	}
	 static void refreshListProcedure(JMainConsole console,String entihome$){
		try{
			InputStream is=JProcedurePanel.class.getResourceAsStream("list.tar");
	        TarArchiveInputStream tis = new TarArchiveInputStream(is);
			ArchiveHandler.extractEntitiesFromTar(entihome$,tis);
		    Entigrator entigrator=console.getEntigrator(entihome$);
		    Sack procedure=entigrator.getEntityAtKey(JProcedurePanel.PROCEDURE_LIST_KEY);
		    String procedureLocator$=EntityHandler.getEntityLocator(entigrator, procedure);
		    JEntityPrimaryMenu.reindexEntity(console, procedureLocator$);
		}catch(Exception e){
			Logger.getLogger(JQueryPanel.class.getName()).severe(e.toString());	
		}
	}
	static void refreshAllEntitiesQuery(JMainConsole console,String entihome$){
		/*	
		try{
				InputStream is=JQueryPanel.class.getResourceAsStream("query.tar");
		        TarArchiveInputStream tis = new TarArchiveInputStream(is);
				ArchiveHandler.extractEntitiesFromTar(entihome$,tis);
			    Entigrator entigrator=console.getEntigrator(entihome$);
			    Sack query=entigrator.getEntityAtKey(JQueryPanel.QUERY_ALL_ENTITIES_KEY);
			    String queryLocator$=EntityHandler.getEntityLocator(entigrator, query);
			    JEntityPrimaryMenu.reindexEntity(console, queryLocator$);
			}catch(Exception e){
				Logger.getLogger(JQueryPanel.class.getName()).severe(e.toString());	
			}
			*/
		}
	static void refreshIconsFolder(JMainConsole console,String entihome$){
		try{
			InputStream is=BaseHandler.class.getResourceAsStream("icons.tar");
	        TarArchiveInputStream tis = new TarArchiveInputStream(is);
			ArchiveHandler.extractEntitiesFromTar(entihome$,tis);
		    Entigrator entigrator=console.getEntigrator(entihome$);
		    Sack icons=entigrator.getEntityAtKey(Entigrator.ICONS);
		    String iconsLocator$=EntityHandler.getEntityLocator(entigrator, icons);
		    JEntityPrimaryMenu.reindexEntity(console, iconsLocator$);
		}catch(Exception e){
			Logger.getLogger(JQueryPanel.class.getName()).severe(e.toString());	
		}
	}
	@Override
	public void activate() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getWebView(Entigrator entigrator,String locator$) {
		try{
			Properties locator=Locator.toProperties(locator$);
			String basesList$=locator.getProperty(WContext.BASES);
			String[] sa=Locator.toArray(basesList$);
			String webHome$=locator.getProperty(WContext.WEB_HOME);
		    String webRequester$=this.getClass().getName();
		    if(debug)
				System.out.println("JBasesPanel:web home="+webHome$);
			 String icon$=Support.readHandlerIcon(null,JBaseNavigator.class, "base.png");
			
			StringBuffer sb=new StringBuffer();
			sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
			sb.append("<html>");
			sb.append("<head>");
			sb.append(WUtils.getMenuBarScript());
			sb.append(WUtils.getMenuBarStyle());
		    sb.append("</head>");
		    sb.append("<body onload=\"onLoad()\" >");
		    
		    sb.append("<h3>Bases</h3>");
		    sb.append("<script>");
		    if(debug)
		    sb.append("console.log(window.location.href);");
	        sb.append("window.localStorage.setItem(\"back."+JBaseNavigator.class.getName()+"\",\""+this.getClass().getName()+"\");");
	        sb.append("window.localStorage.setItem(\""+this.getClass().getName()+"\",\""+Base64.encodeBase64URLSafeString(locator$.getBytes())+"\");");
	        if(debug)
	        sb.append("console.log(window.localStorage.getItem(\""+this.getClass().getName()+"\"));");
			sb.append("function onLoad() {");
		    sb.append("initBack(\""+this.getClass().getName()+"\",\""+webRequester$+"\");");
		    sb.append("}");
	        sb.append("</script>");
		    if(sa!=null)
				for(String s:sa){
					if(debug)
					System.out.println("JBasesPanel:s="+s);
					sb.append(getItem(icon$, webHome$,s,locator$)+"<br>");
				}
		    
	       
		    
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
				return null;
	}
	private String getItem(String icon$, String url$,String entihome$,String locator$ ){
          Properties itemLocator=new Properties();
          itemLocator.setProperty(WContext.WEB_HOME, url$);
          //itemLocator.setProperty(WContext.WEB_REQUESTER, JBasesPanel.class.getName());
          locator$=Locator.append(locator$,Entigrator.ENTIHOME, entihome$);
          itemLocator.setProperty(WContext.WEB_REQUESTER, this.getClass().getName());
          itemLocator.setProperty(Entigrator.ENTIHOME,entihome$);
          itemLocator.setProperty(BaseHandler.HANDLER_CLASS,JBaseNavigator.class.getName());
          String itemLocator$=Locator.toString(itemLocator);
          File entihome = new File(entihome$);
          String title$= entihome.getName();
		  String iconTerm$="<img src=\"data:image/png;base64,"+icon$+
				  "\" width=\"24\" height=\"24\" alt=\""+title$+"\">";
		  return iconTerm$+"<a href=\""+url$+"?"+WContext.WEB_LOCATOR+"="+Base64.encodeBase64URLSafeString(itemLocator$.getBytes())+"\" >"+" "+title$+"</a>";
	  }
}
