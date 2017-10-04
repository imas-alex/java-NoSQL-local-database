package gdt.jgui.entity.users;
import java.awt.BorderLayout;
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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.UsersHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JItemPanel;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JItemsListPanel.ItemPanelComparator;
import gdt.jgui.entity.group.JGroupEditor;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.xml.bind.DatatypeConverter;
public class JUserEditor extends JPanel implements JContext{
	public static final String USER_NAME="Name";
	public static final String USER_PASSWORD="Password";
	public static final String USER_KEY="Key";
	public static final String ACTION_CREATE_USER="action create user";
	
	private String entihome$;
	private String entityKey$;
	private String user$;
	private String key$;
	private JMainConsole console;
	
	private static final long serialVersionUID = 1L;
	private JTextField name;
	private JTextField password;
	private JTextField key;
	private JPanel groupsPanel;
	private JScrollPane scrollPane;
	//private String requesterResponseLocator$;
	boolean debug=false;
	public JUserEditor() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{100, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0};
		setLayout(gridBagLayout);
		JLabel lblTitle = new JLabel("Name");
		
		GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.insets = new Insets(5, 5, 5, 5);
		gbc_lblName.gridx = 0;
		gbc_lblName.gridy = 0;
		gbc_lblName.anchor=GridBagConstraints.NORTHWEST;
		add(lblTitle, gbc_lblName);
		name = new JTextField();

		GridBagConstraints gbc_name = new GridBagConstraints();
		gbc_name.insets = new Insets(5, 0, 5, 5);
		gbc_name.fill = GridBagConstraints.HORIZONTAL;
		gbc_name.gridx = 1;
		gbc_name.gridy = 0;
		add(name, gbc_name);
		name.setColumns(10);
		

		JLabel lblPassword = new JLabel("Password");
		GridBagConstraints gbc_lblpassword = new GridBagConstraints();
		gbc_lblpassword.insets = new Insets(5, 5, 5, 5);
		gbc_lblpassword.gridx = 0;
		gbc_lblpassword.gridy = 1;
		gbc_lblpassword.anchor=GridBagConstraints.NORTHWEST;
		add(lblPassword, gbc_lblpassword);
		
		password= new JTextField();
		GridBagConstraints gbc_phone = new GridBagConstraints();
		gbc_phone.insets = new Insets(5, 0, 5, 5);
		gbc_phone.fill = GridBagConstraints.HORIZONTAL;
		gbc_phone.gridx = 1;
		gbc_phone.gridy = 1;
		add(password, gbc_phone);
		password.setColumns(10);

		
		
		JLabel lblKey = new JLabel("Key");
		GridBagConstraints gbc_lblKey = new GridBagConstraints();
		gbc_lblKey.insets = new Insets(5, 5, 5, 5);
		gbc_lblKey.gridx = 0;
		gbc_lblKey.gridy = 2;
		gbc_lblKey.anchor=GridBagConstraints.NORTHWEST;
		add(lblKey, gbc_lblKey);
		
		key = new JTextField();
		key.setEditable(false);
		GridBagConstraints gbc_key = new GridBagConstraints();
		gbc_key.insets = new Insets(5, 0, 5, 5);
		gbc_key.fill = GridBagConstraints.HORIZONTAL;
		gbc_key.gridx = 1;
		gbc_key.gridy = 2;
		add(key, gbc_key);
		//key.setColumns(10);
		
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.weighty = 1.0;
		gbc_panel.insets = new Insets(5, 0, 0, 5);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx =0;
		gbc_panel.gridy = 3;
		gbc_panel.gridwidth = 2;
		add(panel, gbc_panel);
		//add(panel);
		scrollPane = new JScrollPane();
		TitledBorder title= BorderFactory.createTitledBorder("Groups");
		 scrollPane.setBorder(title);
		panel.setLayout(new BorderLayout());
		panel.add(scrollPane, BorderLayout.CENTER);
		//panel.add(scrollPane);
		groupsPanel = new JPanel();
		groupsPanel.setLayout(new BoxLayout(groupsPanel, BoxLayout.Y_AXIS));
		scrollPane.getViewport().add(groupsPanel);
	}

	@Override
	public JPanel getPanel() {
		return this;
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
			
			JMenuItem doneItem = new JMenuItem("Done");
			doneItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					done();
				 
				   	}
			} );
			menu.add(doneItem);
			menu.addSeparator();
			JMenuItem groupsItem = new JMenuItem("Groups");
			groupsItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					showGroups();
				   	}
			} );
			menu.add(groupsItem);
			//
			if(hasSelectedItems()){
		    	JMenuItem deleteItem = new JMenuItem("Remove from selected groups");
			    deleteItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try{
							int response = JOptionPane.showConfirmDialog(console.getContentPanel(), "Remove from groups ?", "Confirm",
							        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						   if (response == JOptionPane.YES_OPTION) {
							  String[] sa=listSelectedItems();
							  if(sa==null)
								  return;
							  Entigrator entigrator=console.getEntigrator(entihome$);
							  Sack group;
							 // String name$=name.getText();
							  for(String s:sa){
			                      group=entigrator.getEntityAtKey(Locator.getProperty(s,EntityHandler.ENTITY_KEY));
			                      if(group!=null)
			                    	  group.removeElementItem("user", user$);
			                      entigrator.ent_alter(group);
							  }
							  done();
						   }
						}catch(Exception ee){
							Logger.getLogger(getClass().getName()).severe(e.toString());
						}
					}
				} );
				menu.add(deleteItem);
		    }
			//
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
	private void showGroups(){
		 JGroupsList gl=new JGroupsList();
		 String glLocator$=gl.getLocator(); 
		 glLocator$=Locator.append(glLocator$, Entigrator.ENTIHOME, entihome$);
    	if(debug)
    		System.out.println("JUserEditor:showGroups:locator="+glLocator$);
		JConsoleHandler.execute(console, glLocator$);
	}
	private void done(){
		try{
			Entigrator entigrator=console.getEntigrator(entihome$);
			Sack users=entigrator.getEntityAtKey(entigrator.indx_keyAtLabel("users"));
			if(!users.existsElement("user"))
				users.createElement("user");
			String password$=password.getText();
			String name$=name.getText();
			if(name$.length()<1){
				JOptionPane.showMessageDialog(this, "Empty user name");
				return;
			}
			
			int strength=calculatePasswordStrength(password$);
			if(strength<2){
				 JOptionPane.showMessageDialog(this, "Too weak password");
				 return;
			}
			if(strength<6){
				int response = JOptionPane.showConfirmDialog(console.getContentPanel(), "Weak password", "Confirm",
				        JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(response==JOptionPane.CANCEL_OPTION)
				return;
			}
			if(user$!=null){
				Core user=users.getElementItem("user", user$);
				if(user!=null){
				 users.removeElementItem("user", user.name); 	
				 user.name=name$;
				 if(debug)
						System.out.println("JUserEditor:done:user="+name$+" password="+password$ );
				 strength=calculatePasswordStrength(password$);
					if(strength<2){
						 JOptionPane.showMessageDialog(this, "Too weak password");
						 return;
					}
					if(strength<6){
						int response = JOptionPane.showConfirmDialog(console.getContentPanel(), "Weak password", "Confirm",
						        JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
						if(response==JOptionPane.CANCEL_OPTION)
						return;
					}
				 java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-1");
				 md.update(password$.getBytes());
			     byte[] digest = md.digest();
			     key$= DatatypeConverter.printBase64Binary(digest);
			     user.value =key$;
			     user.type=password$;
				 users.putElementItem("user", user);
				 if(debug)
						System.out.println("JUserEditor:done:user="+user.name+" password="+user.type+" key="+user.value );
				
				 entigrator.ent_alter(users);
				 updateGroups(user$,name$);
				 //console.back();
				 JUsersManager um=new JUsersManager();
				 String umLocator$=um.getLocator();
				 umLocator$=Locator.append(umLocator$, Entigrator.ENTIHOME, entihome$);
				 JConsoleHandler.execute(console, umLocator$);
				return;
				}
			}
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-1");
	        md.update(password$.getBytes());
	        byte[] digest = md.digest();
	        key$ = DatatypeConverter.printBase64Binary(digest);
	        users.putElementItem("user", new Core(password$,name$,key$ ));
	        entigrator.ent_alter(users);
	        console.back();
			}catch(Exception ee){
				Logger.getLogger(JUserEditor.class.getName()).severe(ee.toString());
			}
	
	}
private void updateGroups(String user$,String name$){
	try{
		Entigrator entigrator=console.getEntigrator(entihome$);
		String[] sa=entigrator.indx_listEntities("entity", "group");
		if(sa!=null){
			Sack group;
			Core user;
			for(String s:sa){
				try{
				group=entigrator.getEntityAtKey(s);
				user=group.getElementItem("user", user$);
				if(user!=null){
					user.name=name$;
					user.value=key$;
					group.putElementItem("user", user);
					entigrator.ent_alter(group);
				}
				}catch(Exception ee){}
			}
		}
	}catch(Exception ee){
		Logger.getLogger(JUserEditor.class.getName()).severe(ee.toString());
	}
}
 private static int calculatePasswordStrength(String password){
	        
	        //total score of password
	        int iPasswordScore = 0;
	        
	        if( password.length() < 8 )
	            return 0;
	        else if( password.length() >= 10 )
	            iPasswordScore += 2;
	        else 
	            iPasswordScore += 1;
	        if( password.matches("(?=.*[0-9]).*") )
	            iPasswordScore += 2;
	        if( password.matches("(?=.*[a-z]).*") )
	            iPasswordScore += 2;
	        if( password.matches("(?=.*[A-Z]).*") )
	            iPasswordScore += 2;    
	        if( password.matches("(?=.*[~!@#$%^&*()_-]).*") )
	            iPasswordScore += 2;
	        return iPasswordScore;
	    }
	@Override
	public String getLocator() {
		Properties locator=new Properties();
	    locator.setProperty(Locator.LOCATOR_TYPE, JContext.CONTEXT_TYPE);
	    locator.setProperty(JContext.CONTEXT_TYPE,getType());
	    locator.setProperty(Locator.LOCATOR_TITLE, "User");
	   if(entihome$!=null)
	      locator.setProperty(Entigrator.ENTIHOME,entihome$);
	   locator.setProperty( Locator.LOCATOR_ICON_CONTAINER, Locator.LOCATOR_ICON_CONTAINER_CLASS);
   	locator.setProperty( Locator.LOCATOR_ICON_CLASS, getClass().getName());
   	locator.setProperty( Locator.LOCATOR_ICON_FILE, "user.png");
   	locator.setProperty( Locator.LOCATOR_ICON_LOCATION, UsersHandler.EXTENSION_KEY);
    if(entityKey$!=null)
		      locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
	    locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
	    locator.setProperty(BaseHandler.HANDLER_CLASS,JUserEditor.class.getName());
	    String title$=user$;
	    if(title$!=null)
	    	 locator.setProperty(Locator.LOCATOR_TITLE, user$);
	    if(user$!=null)
	    locator.setProperty(USER_NAME,user$);
	    if(key$!=null)
	    locator.setProperty(USER_KEY,key$);
	    return Locator.toString(locator);
	}

	@Override
	public JContext instantiate(JMainConsole console, String locator$) {
		try{
		this.console=console;
		if(debug)
		System.out.println("JUserEditor:instantiate:locator="+locator$);
		Properties locator=Locator.toProperties(locator$);
		entihome$=locator.getProperty(Entigrator.ENTIHOME);
		entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY,entityKey$);
		Entigrator entigrator=console.getEntigrator(entihome$);
		Sack users=entigrator.getEntityAtKey(entigrator.indx_keyAtLabel("users"));
		if(users==null)
			console.back();
		user$=locator.getProperty(USER_NAME);
		name.setText(user$);
		String password$="";
		String key$="";
		if(debug)
			System.out.println("JUserEditor:instantiate:user="+user$);
		Core user=users.getElementItem("user", user$);
		if(user!=null){
			password$=user.type;
			key$=user.value;
			}else
				if(debug)
			   System.out.println("JUserEditor:instantiate:cannot get user="+user$);
		password.setText(password$);
		key.setText(key$);
		//
		String[]ga=UsersHandler.listGroups(entigrator, user$);
		if(ga!=null){
		 JGroupEditor groupEditor=new JGroupEditor();
		 String itemLocator$=groupEditor.getLocator();
		 itemLocator$=Locator.append(itemLocator$, Entigrator.ENTIHOME, entihome$);
 		 itemLocator$=Locator.append(itemLocator$,Locator.LOCATOR_CHECKABLE, Locator.LOCATOR_TRUE);
       	 ArrayList<String>gl=new ArrayList<String>();
 		 String groupName$;
 		JItemPanel ip;
 		ArrayList<JItemPanel>ipl=new ArrayList<JItemPanel>();
 		for(String g:ga){
 			     if(gl.contains(g))
 			    	 continue;
 			     gl.add(g);
 			     groupName$=entigrator.indx_getLabel(g);
        		 itemLocator$=Locator.append(itemLocator$, EntityHandler.ENTITY_KEY, g);
        		 itemLocator$=Locator.append(itemLocator$, Locator.LOCATOR_TITLE, groupName$);
        		 ip=new JItemPanel(console,itemLocator$);
        		 ipl.add(ip);
        	}
        	Collections.sort(ipl,new ItemPanelComparator());
        	groupsPanel.removeAll();
    			 for(JItemPanel aIpl:ipl){
    				 groupsPanel.add(aIpl);
    			 }
		 }
	
		//
		}catch(Exception e){
			Logger.getLogger(JUserEditor.class.getName()).severe(e.toString());
		    console.back();
		}
	   return this;
	}

	@Override
	public String getTitle() {
		
		return user$;
	}
	@Override
	public String getSubtitle() {
		return entihome$;
	}

	@Override
	public String getType() {
		return "User";
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void activate() {
		// TODO Auto-generated method stub
		
	}
	private JItemPanel[] getItems(){
		 
		  try{
			  int cnt=groupsPanel.getComponentCount();
			  JItemPanel[] ipa=new JItemPanel[cnt];
			  for(int i=0;i<cnt;i++)
				  ipa[i]=(JItemPanel)groupsPanel.getComponent(i);
			  return ipa;
		  }catch(Exception e){
			  Logger.getLogger(getClass().getName()).severe(e.toString());
			  return null;
		  }
		 
	 }
	private boolean hasSelectedItems(){

		   JItemPanel[] ipa=getItems();
		   if(ipa==null)
			   return false;
		   for(int i=0;i<ipa.length;i++)
			   if(ipa[i].isChecked())
			      return true;
		   return false;
	}
	protected String[] listSelectedItems(){
		   JItemPanel[] ipa=getItems();
		   if(ipa==null)
			   return null;
		   ArrayList<String>sl=new ArrayList<String>();
		   for(int i=0;i<ipa.length;i++)
			   if(ipa[i].isChecked())
			      sl.add(ipa[i].getLocator());
		   return sl.toArray(new String[0]);
	}
	
	
		}	
	
	
