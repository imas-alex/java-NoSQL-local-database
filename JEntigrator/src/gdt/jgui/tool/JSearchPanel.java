package gdt.jgui.tool;
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
import gdt.data.store.Entigrator;
import gdt.jgui.base.JBaseNavigator;
import gdt.jgui.base.JBasesPanel;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.WContext;
import gdt.jgui.console.WUtils;
import gdt.jgui.entity.JEntitiesPanel;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.entity.JEntityPrimaryMenu;

import javax.swing.ComboBoxModel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.apache.commons.codec.binary.Base64;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.logging.Logger;
/**
 * This class is the search panel. It searches entities by the label. 
 * @author imasa
 *
 */
public class JSearchPanel extends JPanel implements JContext,WContext {
private static final long serialVersionUID = 1L;
private static String INPUT="input";
String entihome$;
JMainConsole console;
AutocompleteJComboBox comboBox;
JMenuItem openItem;
JMenuItem listItem;
static boolean debug=false;
/**
 * The default constructor.
 */
	public JSearchPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{200};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0};
		setLayout(gridBagLayout);
	}
	/**
	 * Get the panel to insert into the main console.
	 * @return the panel.
	 */
		@Override
		public JPanel getPanel() {
			return this;
		}
		/**
		 * Get the context locator.
		 * @return the context locator.
		 */
		@Override
		public String getLocator() {
			Properties locator=new Properties();
		    locator.setProperty(Locator.LOCATOR_TYPE, JContext.CONTEXT_TYPE);
		    locator.setProperty(Locator.LOCATOR_TITLE, getTitle());
		    locator.setProperty(JContext.CONTEXT_TYPE,getType());
		    if(entihome$!=null)
			       locator.setProperty(Entigrator.ENTIHOME,entihome$);
		   locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
		   locator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
		   locator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
		   	locator.setProperty(Locator.LOCATOR_ICON_CLASS,JEntityPrimaryMenu.class.getName());
		   	locator.setProperty(Locator.LOCATOR_ICON_FILE,"search.png"); 
		   return Locator.toString(locator);
		}
		@Override
		/**
		 * Create the context.
		 * @param console the main console.
		 * @param locator$ the locator string.
		 * @return the procedure context.
		 */		
public JContext instantiate(JMainConsole console, String locator$) {
			this.console=console;
			 Properties locator=Locator.toProperties(locator$);
			 entihome$=locator.getProperty(Entigrator.ENTIHOME);
			 try{
				 removeAll(); 
				 Entigrator entigrator=console.getEntigrator(entihome$);
				 entigrator.store_refresh();
				 String[] labels=entigrator.indx_listAllLabels();
				 String[] files=new File(entigrator.getEntihome()+"/"+Entigrator.ENTITY_BASE+"/data/").list();
				 ArrayList<String>sl=new ArrayList<String>();
				 for(String s:labels)
					 sl.add(s);
				 for(String s:files)
					 sl.add(s);
				 
				 
		//		 System.out.println("SearchPanel:instantiate.labels="+labels.length);
				 comboBox = new AutocompleteJComboBox(sl.toArray(new String[0]));
					GridBagConstraints gbc_comboBox = new GridBagConstraints();
					gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
					gbc_comboBox.insets = new Insets(5, 5, 5, 5);
					gbc_comboBox.gridx = 0;
					gbc_comboBox.gridy = 0;
					gbc_comboBox.anchor=GridBagConstraints.FIRST_LINE_START;
					add(comboBox, gbc_comboBox);
					
					JPanel panel = new JPanel();
					GridBagConstraints gbc_panel = new GridBagConstraints();
					gbc_panel.insets = new Insets(0, 0, 5, 5);
					gbc_panel.fill = GridBagConstraints.BOTH;
					gbc_panel.gridx = 0;
					gbc_panel.gridy = 1;
					add(panel, gbc_panel); 
			 }catch(Exception e){
				 Logger.getLogger(getClass().getName()).severe(e.toString());
			 }
		 return this;
		}
		/**
		 * Get context title.
		 * @return the context title.
		 */	
		@Override
		public String getTitle() {
			return "Search for label";
		}
		/**
		 * Get context subtitle.
		 * @return the context subtitle.
		 */	
		@Override
		public String getSubtitle() {
			try{
			  return console.getEntigrator(entihome$).getBaseName();	
			}catch(Exception e){
			return null;
			}
		}
		/**
		 * Get context type.
		 * @return the context type.
		 */	
		@Override
		public String getType() {
			return "Search panel";
		}
		/**
		 * No action.
		 */
		@Override
		public void close() {
			// TODO Auto-generated method stub
			
		}
/**
 * Get context menu.
 * @return the context menu. 
 */
		@Override
		
public JMenu getContextMenu() {
			final JMenu menu=new JMenu("Context");
			menu.addMenuListener(new MenuListener(){
				@Override
				public void menuSelected(MenuEvent e) {
//				System.out.println("EntityEditor:getConextMenu:menu selected");
				if(openItem!=null) 
				     menu.remove(openItem);
				if(listItem!=null)
				   menu.remove(listItem);
				ComboBoxModel<String> model=comboBox.getModel();
				if(model.getSize()>0){
				  openItem = new JMenuItem("Open");
				openItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						  JEntityFacetPanel ep=new JEntityFacetPanel();
						  String locator$=ep.getLocator();
						   locator$=Locator.append(locator$, Entigrator.ENTIHOME, entihome$);
						   String item$=(String)comboBox.getSelectedItem();
						   Entigrator entigrator=console.getEntigrator(entihome$);
						   String entityKey$=item$;
						   File file= new File(entigrator.getEntihome()+"/"+Entigrator.ENTITY_BASE+"/data/"+item$);
						   if(!file.exists())
						   entityKey$=entigrator.indx_keyAtLabel(item$);
						   locator$=Locator.append(locator$, EntityHandler.ENTITY_KEY, entityKey$);
						   JConsoleHandler.execute(console, locator$);
					}
				} );
				menu.add(openItem);
				listItem = new JMenuItem("List");
				listItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
					   JEntitiesPanel ep=new JEntitiesPanel();
					   String locator$=ep.getLocator();
					   locator$=Locator.append(locator$, Entigrator.ENTIHOME, entihome$);
					   ComboBoxModel<String> model=comboBox.getModel();
					   ArrayList<String>sl=new ArrayList<String>();
					   int cnt=model.getSize();
					   for(int i=1;i<cnt;i++)
						   sl.add(model.getElementAt(i));
					   String[] sa=sl.toArray(new String[0]);
					   String sa$=Locator.toString(sa);
					   locator$=Locator.append(locator$, EntityHandler.ENTITY_LIST, sa$);
					   JConsoleHandler.execute(console, locator$);
					   	}
				} );
				menu.add(listItem);
					}
					}
				
				@Override
				public void menuDeselected(MenuEvent e) {
				}
	    		@Override
				public void menuCanceled(MenuEvent e) {
				}
			});
			JMenuItem doneItem = new JMenuItem("Done");
			doneItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
						  console.back();
				}
			} );
			menu.add(doneItem);
			return menu;
		}
@Override
public void activate() {
	JConsoleHandler.execute(console, getLocator());
}
@Override
public String getWebView(Entigrator entigrator, String locator$) {
	try{
		if(debug)
			System.out.println("JSearchPanel:BEGIN:locator="+locator$);
			
		Properties locator=Locator.toProperties(locator$);
		String webHome$=locator.getProperty(WContext.WEB_HOME);
		String webRequester$=locator.getProperty(WContext.WEB_REQUESTER);
		String input$=locator.getProperty(INPUT);
		if(debug)
		System.out.println("JSearchPanel:input="+input$+" web home="+webHome$+ " web requester="+webRequester$);
		// String icon$=Support.readHandlerIcon(null,JBaseNavigator.class, "base.png");
		StringBuffer sb=new StringBuffer();
		sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
		sb.append("<html>");
		sb.append("<head>");
		sb.append(WUtils.getJquery(entigrator));
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
	    sb.append("<tr><td>Context:</td><td><strong>");
	    	    sb.append("Search for label");
	    	    sb.append("</strong></td></tr>");
	    sb.append("</table>");
	    sb.append("<table>");
	    sb.append("<tr>");
	    sb.append("<td>Label:</td>");
	    sb.append("<td><input type=\"text\" id=\"label\"  onchange=\"updateSelector()\" ></td>");
	    sb.append("</tr>");
	   
	    int cnt=0;
	   
	    if(input$!=null){
	    	String[] la=entigrator.indx_listAllLabels();
	    	ArrayList<String>sl=new ArrayList<String>();
	    	for(String s:la){
	    		if(s.toLowerCase().contains(input$.toLowerCase()))
	    			sl.add(s);
	    	}
	    	Collections.sort(sl);
	    	cnt=sl.size();
	    	sb.append("<tr>");
	    	if(cnt==1)
	    	 sb.append("<td> <button onclick=\"openEntity()\">Open:</button> </td>");
	    	else
	    		sb.append("<td></td>");
	    	if(cnt>0){
	    	sb.append("<td><select id=\"selector\" size=\""+1+"\" onchange=\"openEntity()\">");
	    	for(String s:sl){
	    		if(debug)
	    			System.out.println("JSearchPanel:option=="+s);
	    		s=s.replaceAll("\"", "&quot;");
            	s=s.replaceAll("'", "&#39;");
	    	sb.append("<option value=\""+s+"\">"+s+"</option>");
	    	}
	    	sb.append("</select></td>");
	    	}
	    	else
	    		 sb.append("<td></td>");
	    }
	    sb.append("</tr></table>");
	    sb.append("<script>");
	    sb.append("function updateSelector() {");
	    sb.append(" var input = document.getElementById(\"label\").value;");
	    sb.append(" var locator=\""+locator$+"\";");
	    sb.append("locator=appendProperty(locator,\""+INPUT+"\",input);");
	    String urlHeader$=webHome$+"?"+WContext.WEB_LOCATOR+"=";
	    sb.append("var url=\""+urlHeader$+"\"+window.btoa(locator);");
	    sb.append("window.location.assign(url);");
	    sb.append("}");
	    
	    sb.append("function openEntity() {");
	    sb.append("var locator =\""+locator$+"\";");
	    sb.append("var entityLabel = document.getElementById(\"selector\").value;");
	    sb.append("locator=appendProperty(locator,\""+EntityHandler.ENTITY_LABEL+"\",entityLabel);");
	    sb.append("locator=appendProperty(locator,\""+BaseHandler.HANDLER_CLASS+"\",\""+JEntityFacetPanel.class.getName()+"\");");
	    sb.append("locator=appendProperty(locator,\""+WContext.WEB_REQUESTER+"\",\""+this.getClass().getName()+"\");");
	   
	    sb.append("var url=\""+urlHeader$+"\"+window.btoa(locator);");
	    sb.append("window.localStorage.setItem(\"back."+JEntityFacetPanel.class.getName()+"\",\""+this.getClass().getName()+"\");");
	    sb.append("window.location.assign(url);");
	    sb.append("}");
	    
	    //sb.append("<script>");
      
	    
	    sb.append("function onLoad() {");
	    sb.append("initBack(\""+this.getClass().getName()+"\",\""+webRequester$+"\");");
	    if(input$!=null)
	    	sb.append("document.getElementById(\"label\").value=\""+input$+"\";");
	        
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
}
