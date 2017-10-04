package gdt.jgui.entity.users;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import java.util.Collections;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.xml.bind.DatatypeConverter;

import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.UsersHandler;
import gdt.data.entity.facet.ExtensionHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Identity;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;

import gdt.jgui.console.JFacetRenderer;
import gdt.jgui.console.JItemPanel;
import gdt.jgui.console.JItemsListPanel;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.entity.JReferenceEntry;



public class JUsersManager extends JItemsListPanel implements JFacetRenderer,JRequester{
	String entihome$;
	String entityKey$;
	String entityLabel$;
    boolean debug=false;
	String requesterResponseLocator$;
	public JUsersManager() {
		super();
	}

	@Override
	public JMenu getContextMenu() {
		final JMenu menu=new JMenu("Context");
		menu.addMenuListener(new MenuListener(){
			@Override
			public void menuSelected(MenuEvent e) {
//			System.out.println("EntityEditor:getConextMenu:menu selected");
		    menu.removeAll(); 
		    JMenuItem  cancelItem = new JMenuItem("Cancel");
			cancelItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					console.back();
				}
			} );
			menu.add(cancelItem);
			
			JMenuItem newItem = new JMenuItem("New");
			newItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JUserEditor ue=new JUserEditor();
					String ueLocator$=ue.getLocator();
					ueLocator$=Locator.append(ueLocator$, JUserEditor.USER_NAME, "New user"+Identity.key().substring(0,4));
					ueLocator$=Locator.append(ueLocator$, JUserEditor.USER_PASSWORD, "password");
					ueLocator$=Locator.append(ueLocator$, Entigrator.ENTIHOME, entihome$);
					JConsoleHandler.execute(console, ueLocator$);
				   	}
			} );
			menu.add(newItem);
			if(hasSelectedItems()){
			JMenuItem deleteItem = new JMenuItem("Delete");
			deleteItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					delete();
				   	}
			} );
			menu.add(deleteItem);
			}
			//menu.addSeparator();
			
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

	@Override
	public String getLocator() {
		Properties locator=new Properties();
	    locator.setProperty(Locator.LOCATOR_TYPE, JContext.CONTEXT_TYPE);
	    locator.setProperty(JContext.CONTEXT_TYPE,getType());
	    locator.setProperty(Locator.LOCATOR_TITLE, getTitle());
	   if(entihome$!=null)
	      locator.setProperty(Entigrator.ENTIHOME,entihome$);
	   locator.setProperty( Locator.LOCATOR_ICON_CONTAINER, Locator.LOCATOR_ICON_CONTAINER_CLASS);
   	locator.setProperty( Locator.LOCATOR_ICON_CLASS, getClass().getName());
   	locator.setProperty( Locator.LOCATOR_ICON_FILE, "user.png");
	locator.setProperty( Locator.LOCATOR_ICON_LOCATION, UsersHandler.EXTENSION_KEY);
	if(entityKey$!=null)
		      locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
	    locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
	    locator.setProperty(BaseHandler.HANDLER_CLASS,JUsersManager.class.getName());
	   
	    return Locator.toString(locator);
	}

	@Override
	public JContext instantiate(JMainConsole console, String locator$) {
		try{
			if(debug)
			System.out.println("JUsersManager:instantiate:BEGIN");
			 this.console=console;
			 this.locator$=locator$;
			 panel.removeAll();
			 Properties locator=Locator.toProperties(locator$);
			 entihome$=locator.getProperty(Entigrator.ENTIHOME);
			 Entigrator entigrator=console.getEntigrator(entihome$);
 			 entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
 			 Sack users=null;
			 if(entityKey$==null){
				 String[]sa=entigrator.indx_listEntitiesAtPropertyName("users");
				 if(sa!=null){
					 entityKey$=sa[0];
				 }
				 else{
					 users=entigrator.ent_new("users", "users");
					 entigrator.ent_alter(users);
					 entityKey$=users.getKey();
				 }
			 }
			 entityLabel$=entigrator.indx_getLabel(entityKey$);
			 if(users==null)
			 //{
				 users=entigrator.getEntityAtKey(entityKey$);
			 //}else{
			 Core[] ca=users.elementGet("user");
			if(debug)
				System.out.println("JUsersManager:instantiate:ca="+ca.length);
			 if(ca!=null){
	            Core.sortAtName(ca);
            	ArrayList<JItemPanel>ipl=new ArrayList<JItemPanel>();
            	JUserEditor userEditor=new JUserEditor();
            	JItemPanel ip;
            	String userLocator$=userEditor.getLocator();
	            	for(Core aCa:ca){
	            		if(debug)
	        				System.out.println("JUsersManager:instantiate:user="+aCa.name);
	        			
	            		userLocator$=Locator.append(userLocator$, JUserEditor.USER_NAME, aCa.name);
	            		userLocator$=Locator.append(userLocator$, Locator.LOCATOR_TITLE, aCa.name);
	            		userLocator$=Locator.append(userLocator$, Entigrator.ENTIHOME, entihome$);
	            		userLocator$=Locator.append(userLocator$,Locator.LOCATOR_CHECKABLE, Locator.LOCATOR_TRUE);
	            		ip=new JItemPanel(console,userLocator$);
	            		ipl.add(ip);
	            	}
	            	Collections.sort(ipl,new ItemPanelComparator());
	            	putItems(ipl.toArray(new JItemPanel[0]));
	            }else
	            	clearItems();
			 //}
        	return this;
        }catch(Exception e){
        Logger.getLogger(getClass().getName()).severe(e.toString());
        }
        return null;
	}
private void delete(){
	try{
		int response = JOptionPane.showConfirmDialog(console.getContentPanel(), "Delete selected users ?", "Confirm",
		        JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		if(response==JOptionPane.CANCEL_OPTION)
		return;
		 Entigrator entigrator=console.getEntigrator(entihome$);
		 Sack users=entigrator.getEntityAtKey(entityKey$);
		String[] sa=listSelectedItems();
		String user$;
		for(String s:sa){
			user$=Locator.getProperty(s, JUserEditor.USER_NAME);
			users.removeElementItem("user", user$);
		}
		entigrator.ent_alter(users);
		JConsoleHandler.execute(console, getLocator());
	 }catch(Exception e){
        Logger.getLogger(getClass().getName()).severe(e.toString());
        }
}
	@Override
	public String getTitle() {
		return "Users";
	}

	@Override
	public String getSubtitle() {
		try{
			  File file = new File(entihome$);
			  return file.getName();
			}catch(Exception e){
				return null;
			}
	}

	@Override
	public String getType() {
		return "Users";
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void activate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void response(JMainConsole console, String locator$) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String addIconToLocator(String locator$) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFacetHandler() {
		return UsersHandler.class.getName();
	}

	@Override
	public String getEntityType() {
		return "users";
	}

	@Override
	public String getCategoryIcon(Entigrator entigrator) {
		return ExtensionHandler.loadIcon(entigrator, UsersHandler.EXTENSION_KEY, "user.png");
	}

	@Override
	public String getFacetIcon() {
		return "user.png";
	}

	@Override
	public String getCategoryTitle() {
		return "Users";
	}

	@Override
	public void adaptClone(JMainConsole console, String locator$) {
		
	}

	@Override
	public void adaptRename(JMainConsole console, String locator$) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void collectReferences(Entigrator entigrator, String entiyKey$, ArrayList<JReferenceEntry> sl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reindex(JMainConsole console, Entigrator entigrator, Sack entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String newEntity(JMainConsole console, String locator$) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFacetOpenItem() {
		//return JUsersManagerFacetOpenItem.class.getName();
		return null;
	}
public static boolean isAuthorized(gdt.data.store.Entigrator entigrator,java.lang.String locator$){
	try{
		Properties locator=Locator.toProperties(locator$);
		String user$=locator.getProperty(WebLogin.USER);
		String password$=locator.getProperty(WebLogin.PASSWORD);
		Sack users=entigrator.getEntityAtKey(entigrator.indx_keyAtLabel("users"));
		Core user =users.getElementItem("user", user$);
		 if(password$!=null&&user!=null){
		java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-1");
	    md.update(password$.getBytes());
	    byte[] digest = md.digest();
	    String key$ = DatatypeConverter.printBase64Binary(digest);
	    if(user.value.equals(key$))
	    	return true;
		 }
	}catch(Exception e){
		Logger.getLogger(JUsersManager.class.getName()).severe(e.toString());
	}
	return false;
}
}
