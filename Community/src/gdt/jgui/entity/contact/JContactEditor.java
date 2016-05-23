package gdt.jgui.entity.contact;
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
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Stack;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import gdt.data.entity.BaseHandler;
import gdt.data.entity.ContactHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Identity;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;

import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JFacetRenderer;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.entity.JEntityDigestDisplay;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.entity.JEntityPrimaryMenu;
import gdt.jgui.entity.JEntityStructurePanel;
import gdt.jgui.entity.JReferenceEntry;
import gdt.jgui.tool.JTextEditor;

import javax.swing.JTextField;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import org.apache.commons.codec.binary.Base64;

public class JContactEditor extends JPanel implements JContext,JFacetRenderer,JRequester{
	public static final String CONTACT_TITLE="contact title";
	public static final String CONTACT_PHONE="contact phone";
	public static final String CONTACT_EMAIL="contact email";
	public static final String ACTION_CREATE_CONTACT="action create contact";
	
	private String entihome$;
	private String entityKey$;
	private JMainConsole console;
	
	private static final long serialVersionUID = 1L;
	private JTextField title;
	private JTextField phone;
	private JTextField email;
	private String requesterResponseLocator$;
	public JContactEditor() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{100, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0};
		setLayout(gridBagLayout);
		JLabel lblTitle = new JLabel("Label");
		
		GridBagConstraints gbc_lblTitle = new GridBagConstraints();
		gbc_lblTitle.insets = new Insets(5, 5, 5, 5);
		gbc_lblTitle.gridx = 0;
		gbc_lblTitle.gridy = 0;
		gbc_lblTitle.anchor=GridBagConstraints.NORTHWEST;
		add(lblTitle, gbc_lblTitle);
		title = new JTextField();

		GridBagConstraints gbc_title = new GridBagConstraints();
		gbc_title.insets = new Insets(5, 0, 5, 5);
		gbc_title.fill = GridBagConstraints.HORIZONTAL;
		gbc_title.gridx = 1;
		gbc_title.gridy = 0;
		add(title, gbc_title);
		title.setColumns(10);
		

		JLabel lblPhone = new JLabel("Phone");
		GridBagConstraints gbc_lblphone = new GridBagConstraints();
		gbc_lblphone.insets = new Insets(5, 5, 5, 5);
		gbc_lblphone.gridx = 0;
		gbc_lblphone.gridy = 1;
		gbc_lblphone.anchor=GridBagConstraints.NORTHWEST;
		add(lblPhone, gbc_lblphone);
		
		phone = new JTextField();
		GridBagConstraints gbc_phone = new GridBagConstraints();
		gbc_phone.insets = new Insets(5, 0, 5, 5);
		gbc_phone.fill = GridBagConstraints.HORIZONTAL;
		gbc_phone.gridx = 1;
		gbc_phone.gridy = 1;
		add(phone, gbc_phone);
		phone.setColumns(10);

		
		
		JLabel lblEmail = new JLabel("Email");
		GridBagConstraints gbc_lblEmail = new GridBagConstraints();
		gbc_lblEmail.insets = new Insets(5, 5, 5, 5);
		gbc_lblEmail.gridx = 0;
		gbc_lblEmail.gridy = 2;
		gbc_lblEmail.anchor=GridBagConstraints.NORTHWEST;
		add(lblEmail, gbc_lblEmail);
		
		email = new JTextField();
		GridBagConstraints gbc_email = new GridBagConstraints();
		gbc_phone.insets = new Insets(5, 0, 5, 5);
		gbc_email.fill = GridBagConstraints.HORIZONTAL;
		gbc_email.gridx = 1;
		gbc_email.gridy = 2;
		add(email, gbc_email);
		email.setColumns(10);
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.weighty = 1.0;
		gbc_panel.insets = new Insets(5, 0, 5, 5);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx =0;
		gbc_panel.gridy = 3;
		add(panel, gbc_panel);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
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
		    JMenuItem  facetsItem = new JMenuItem("Facets");
			facetsItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					save();
					JEntityFacetPanel erm=new JEntityFacetPanel();
					String locator$=erm.getLocator();
					locator$=Locator.append(locator$,Entigrator.ENTIHOME,entihome$);
					locator$=Locator.append(locator$,EntityHandler.ENTITY_KEY,entityKey$);
					JConsoleHandler.execute(console, locator$);  
				}
			} );
			menu.add(facetsItem);
			
			JMenuItem digestItem = new JMenuItem("Digest");
			digestItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					save();
					String locator$=getLocator();
					JEntityDigestDisplay edd=new JEntityDigestDisplay();
					edd.instantiate(console, locator$);
					String eddLocator$=edd.getLocator();
					eddLocator$=Locator.append(eddLocator$, Entigrator.ENTIHOME, entihome$);
					eddLocator$=Locator.append(eddLocator$, EntityHandler.ENTITY_KEY, entityKey$);
					JConsoleHandler.execute(console, eddLocator$);
			
				   	}
			} );
			menu.add(digestItem);
			
			JMenuItem structureItem = new JMenuItem("Structure");
			structureItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					save();
					String locator$=getLocator();
					JEntityStructurePanel esp=new JEntityStructurePanel();
					esp.instantiate(console, locator$);
					String espLocator$=esp.getLocator();
					espLocator$=Locator.append(espLocator$, Entigrator.ENTIHOME, entihome$);
					espLocator$=Locator.append(espLocator$, EntityHandler.ENTITY_KEY, entityKey$);
					JConsoleHandler.execute(console, espLocator$);
			  
				   	}
			} );
			menu.add(structureItem);
			menu.addSeparator();
			JMenuItem  doneItem = new JMenuItem("Done");
			doneItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
						try{
							Entigrator entigrator=console.getEntigrator(entihome$);
							Sack contact=entigrator.getEntityAtKey(entityKey$);
							String entityLabel$=contact.getProperty("label");
							String title$=title.getText();
							if(!entityLabel$.equals(title$)){
								contact=entigrator.ent_assignLabel(contact, title$);
								contact=entigrator.ent_assignProperty(contact,"contact",contact.getProperty("label")); 
							}
							contact=entigrator.ent_assignProperty(contact,"phone", phone.getText());
							contact=entigrator.ent_assignProperty(contact,"email", email.getText());
							if(requesterResponseLocator$!=null){
									byte[] ba=Base64.decodeBase64(requesterResponseLocator$);
									String responseLocator$=new String(ba,"UTF-8");
		//				   		System.out.println("TextEditor:done:response locator="+responseLocator$);
									JConsoleHandler.execute(console, responseLocator$);
							}else
								 console.back();
							}catch(Exception ee){
								Logger.getLogger(JContactEditor.class.getName()).severe(ee.toString());
							}
					
				
				}
			} );
			menu.add(doneItem);
			
			JMenuItem cancelItem = new JMenuItem("Cancel");
			cancelItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					console.back();
				   	}
			} );
			menu.add(cancelItem);
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
	private void save(){
		try{
			Entigrator entigrator=console.getEntigrator(entihome$);
			Sack contact=entigrator.getEntityAtKey(entityKey$);
			String entityLabel$=contact.getProperty("label");
			String title$=title.getText();
			if(!entityLabel$.equals(title$)){
				contact=entigrator.ent_assignLabel(contact, title$);
				contact=entigrator.ent_assignProperty(contact,"contact",contact.getProperty("label")); 
			}
			contact=entigrator.ent_assignProperty(contact,"phone", phone.getText());
			contact=entigrator.ent_assignProperty(contact,"email", email.getText());
			}catch(Exception ee){
				Logger.getLogger(JContactEditor.class.getName()).severe(ee.toString());
			}
	
	}
	@Override
	public String getLocator() {
		Properties locator=new Properties();
	    locator.setProperty(Locator.LOCATOR_TYPE, JContext.CONTEXT_TYPE);
	    locator.setProperty(JContext.CONTEXT_TYPE,getType());
	    locator.setProperty(Locator.LOCATOR_TITLE, "Contact");
	   if(entihome$!=null)
	      locator.setProperty(Entigrator.ENTIHOME,entihome$);
	   if(entityKey$!=null)
		      locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
	    String icon$=Support.readHandlerIcon(JContactEditor.class, "contact.png");
	    if(icon$!=null)
	    	locator.setProperty(Locator.LOCATOR_ICON,icon$);
	    locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
	    locator.setProperty(BaseHandler.HANDLER_CLASS,JContactEditor.class.getName());
	    String title$=title.getText();
	    if(title$!=null)
	    	locator.setProperty(CONTACT_TITLE,title$);
	    String phone$=phone.getText();
	    if(phone$!=null)
	    	locator.setProperty(CONTACT_PHONE,phone$);
	    String email$=email.getText();
	    if(email$!=null)
	    	locator.setProperty(CONTACT_EMAIL,email$);
	    return Locator.toString(locator);
	}

	@Override
	public JContext instantiate(JMainConsole console, String locator$) {
		try{
		this.console=console;
		//System.out.println("DesignPanel:instantiate:locator="+locator$);
		Properties locator=Locator.toProperties(locator$);
		entihome$=locator.getProperty(Entigrator.ENTIHOME);
		entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY,entityKey$);
		Entigrator entigrator=console.getEntigrator(entihome$);
		Sack contact=entigrator.getEntityAtKey(entityKey$);
		title.setText(contact.getProperty("label"));
		phone.setText(contact.getProperty("phone"));
		email.setText(contact.getProperty("email"));
		requesterResponseLocator$=locator.getProperty(JRequester.REQUESTER_RESPONSE_LOCATOR);
		}catch(Exception e){
			Logger.getLogger(JContactEditor.class.getName()).severe(e.toString());
		}
	   return this;
	}

	@Override
	public String getTitle() {
		try{
			Entigrator entigrator=console.getEntigrator(entihome$);
			Sack contact=entigrator.getEntityAtKey(entityKey$);
			return contact.getProperty("label");
			}catch(Exception e){
				Logger.getLogger(JContactEditor.class.getName()).severe(e.toString());
			}	
		return "Contact";
	}

	@Override
	public String getSubtitle() {
		return entihome$;
	}

	@Override
	public String getType() {
		return "Contact";
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getCategoryIcon() {
		return Support.readHandlerIcon(JContactEditor.class, "contact.png");
	}

	@Override
	public String addIconToLocator(String locator$) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFacetHandler() {
		return ContactHandler.class.getName();
	}

	@Override
	public String getEntityType() {
		return "contact";
	}

	@Override
	public String getCategoryTitle() {

		return "Contacts";
	}

	@Override
	public void adaptClone(JMainConsole console, String locator$) {
		  try{
		    	Properties locator=Locator.toProperties(locator$);
		    	entihome$=locator.getProperty(Entigrator.ENTIHOME);
		    	entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		    	Entigrator entigrator=console.getEntigrator(entihome$);
		    	Sack entity=entigrator.getEntityAtKey(entityKey$);
		    	entigrator.ent_assignProperty(entity,"contact",entity.getProperty("label")); 
		    	
		    }catch(Exception e){
		    	Logger.getLogger(JContactEditor.class.getName()).severe(e.toString());
		    }
	}

	@Override
	public void adaptRename(JMainConsole console, String locator$) {
		try{
	    	Properties locator=Locator.toProperties(locator$);
	    	entihome$=locator.getProperty(Entigrator.ENTIHOME);
	    	entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
	    	Entigrator entigrator=console.getEntigrator(entihome$);
	    	Sack entity=entigrator.getEntityAtKey(entityKey$);
	    	entigrator.ent_assignProperty(entity,"contact",entity.getProperty("label")); 
	    	
	    }catch(Exception e){
	    	Logger.getLogger(JContactEditor.class.getName()).severe(e.toString());
	    } 		
	}

	@Override
	public void collectReferences(Entigrator entigrator, String entiyKey$, ArrayList<JReferenceEntry> sl) {
			
	}

	@Override
	public void reindex(JMainConsole console, Entigrator entigrator, Sack entity) {
		 try{
				// System.out.println("JContactEditor:reindex:0:entity="+entity.getProperty("label"));
			    	String fhandler$=ContactHandler.class.getName();
			    	if(entity.getElementItem("fhandler", fhandler$)!=null){
						//System.out.println("JContactEditor:reindex:1:entity="+entity.getProperty("label"));
			    		entity.putElementItem("jfacet", new Core(JContactFacetAddItem.class.getName(),fhandler$,JContactFacetOpenItem.class.getName()));
						entity.putElementItem("fhandler", new Core(null,fhandler$,JContactFacetAddItem.EXTENSION_KEY));
						entigrator.save(entity);
					}
			    }catch(Exception e){
			    	Logger.getLogger(getClass().getName()).severe(e.toString());
			    }
	}

	@Override
	public String newEntity(JMainConsole console, String locator$) {
		try{
			Properties locator=Locator.toProperties(locator$);
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			JTextEditor textEditor=new JTextEditor();
		    String teLocator$=textEditor.getLocator();
		    teLocator$=Locator.append(teLocator$, Entigrator.ENTIHOME,entihome$);
		    teLocator$=Locator.append(teLocator$, JTextEditor.TEXT_TITLE,"New contact");
		    String text$="NewContact"+Identity.key().substring(0, 4);
		    teLocator$=Locator.append(teLocator$, JTextEditor.TEXT,text$);
		    JContactEditor ce=new JContactEditor();
		    String ceLocator$=ce.getLocator();
		    ceLocator$=Locator.append(ceLocator$, Entigrator.ENTIHOME,entihome$);
		    ceLocator$=Locator.append(ceLocator$, EntityHandler.ENTITY_KEY,entityKey$);
		    ceLocator$=Locator.append(ceLocator$, BaseHandler.HANDLER_METHOD,"response");
		    ceLocator$=Locator.append(ceLocator$, JRequester.REQUESTER_ACTION,ACTION_CREATE_CONTACT);
		    String requesterResponseLocator$=Locator.compressText(ceLocator$);
		    teLocator$=Locator.append(teLocator$,JRequester.REQUESTER_RESPONSE_LOCATOR,requesterResponseLocator$);
		    JConsoleHandler.execute(console, teLocator$);
		}catch(Exception ee){   
			Logger.getLogger(getClass().getName()).severe(ee.toString());
			
		}
		return null;
	}

	@Override
	public void response(JMainConsole console, String locator$) {
		System.out.println("JContactEditor.response:locator="+locator$);
		try{
		Properties locator=Locator.toProperties(locator$);
		String action$=locator.getProperty(JRequester.REQUESTER_ACTION);
		
		if(ACTION_CREATE_CONTACT.equals(action$)){
			   String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			   String text$=locator.getProperty(JTextEditor.TEXT);
			   Entigrator entigrator=console.getEntigrator(entihome$);
			   Sack contact=entigrator.ent_new("contact", text$);
			   contact=entigrator.ent_assignProperty(contact, "contact", text$);
			   contact=entigrator.ent_assignProperty(contact, "email", "a@b.com");
			   contact=entigrator.ent_assignProperty(contact, "phone", "123456");
			   contact.putAttribute(new Core(null,"icon","contact.png"));
			   entigrator.save(contact);
			   entigrator.saveHandlerIcon(JContactEditor.class, "contact.png");
			   entityKey$=contact.getKey();
			   JContactEditor ce=new JContactEditor();
			   String ceLocator$=ce.getLocator();
			   ceLocator$=Locator.append(ceLocator$, Entigrator.ENTIHOME, entihome$);
			   ceLocator$=Locator.append(ceLocator$, EntityHandler.ENTITY_KEY, entityKey$);
			   JEntityPrimaryMenu.reindexEntity(console, ceLocator$);
			   Stack<String> s=console.getTrack();
			   s.pop();
			   console.setTrack(s);
			   JConsoleHandler.execute(console, ceLocator$);
			}
		}catch(Exception e){
			Logger.getLogger(JContactEditor.class.getName()).severe(e.toString());
		}
	}
		}	
	
	
