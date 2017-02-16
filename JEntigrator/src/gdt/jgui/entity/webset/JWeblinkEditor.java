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

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Stack;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JMenu;
import javax.swing.JPanel;

import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.facet.WebsetHandler;
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
import gdt.jgui.console.ReloadDialog;
import gdt.jgui.entity.JEntitiesPanel;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.entity.JEntityPrimaryMenu;
import gdt.jgui.entity.JReferenceEntry;
import gdt.jgui.tool.JIconSelector;
import gdt.jgui.tool.JTextEditor;
import gdt.jgui.tool.JTextEncrypter;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JLabel;

import org.apache.commons.codec.binary.Base64;

import javax.swing.JTextField;

import java.awt.Insets;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
/**
 * This class represents the web link editor context.
 * @author imasa
 *
 */
public class JWeblinkEditor extends JPanel implements JFacetRenderer,JRequester,ClipboardOwner {
	private static final long serialVersionUID = 1L;
	private static final String ACTION_NEW_ENTITY="action new entity";
	private static final String ACTION_SET_ICON="action set icon";
	private static final String ACTION_ENCODE_PASSWORD="action encode password";
	public static final String METHOD_BROWSE_URL="browseUrl";
	String entihome$;
	String entityKey$;
	String entityLabel$;
	String webLinkKey$;
	String requesterResponseLocator$;
	JMainConsole console;
	String locator$;
	
	private JTextField nameField;
	private JTextField addressField;
	private JTextField loginField;
	private JTextField passwordField;
	private JLabel iconIcon;
	private GridBagConstraints c;
	private GridBagConstraints c_0;
	private GridBagConstraints c_1;
	private GridBagConstraints c_2;
	private GridBagConstraints c_3;
	private GridBagConstraints c_4;
	private GridBagConstraints c_5;
	private GridBagConstraints c_6;
	private GridBagConstraints c_7;
	private GridBagConstraints c_8;
	private GridBagConstraints c_9;
	JPopupMenu iconMenu;
	String message$;
	Sack entity;
	boolean debug=false;
	boolean ignoreOutdate=false;
	/**
	 * The default constructor.
	 */
	public JWeblinkEditor() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
		setLayout(gridBagLayout);
		
		String icon$=Support.readHandlerIcon(null,JEntitiesPanel.class, "globe.png");
		 byte[] ba=Base64.decodeBase64(icon$);
     	ImageIcon icon = new ImageIcon(ba);
     	Image image= icon.getImage().getScaledInstance(24, 24, 0);
     	icon.setImage(image);
		JLabel iconLabel = new JLabel("Icon");
		c= new GridBagConstraints();
		c.insets = new Insets(5,5, 5, 5);
		c.anchor=GridBagConstraints.FIRST_LINE_START;
		c.weighty=0;
		c.gridx = 0;
		c.gridy = 0;
		add(iconLabel, c);
		iconLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				showIconMenu(e);
			}   
		});
		iconIcon = new JLabel();
		iconIcon.setIcon(icon);
		c_0= new GridBagConstraints();
		c_0.anchor = GridBagConstraints.WEST;
		c_0.insets = new Insets(0,5, 5, 0);
		c.anchor = GridBagConstraints.WEST;
		c_0.gridx = 1;
		c_0.gridy = 0;
		add(iconIcon, c_0);
		iconIcon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				showIconMenu(e);
			}   
		});
		JLabel lblName = new JLabel("Name");
		c_1= new GridBagConstraints();
		c_1.insets = new Insets(5, 5, 5, 5);
		c_1.fill = GridBagConstraints.HORIZONTAL;
		c_1.gridx = 0;
		c_1.gridy = 1;
		add(lblName, c_1); 
		
		nameField = new JTextField();
		c_2= new GridBagConstraints();
		c_2.insets = new Insets(0, 5, 5, 0);
		c_2.fill = GridBagConstraints.HORIZONTAL;
     	c_2.gridx = 1;
     	c_2.gridy = 1;
     	add(nameField, c_2); 
     	
     	JLabel lblUrl = new JLabel("Address");
		c_3= new GridBagConstraints();
		c_3.insets = new Insets(5, 5, 5, 5);
		c_3.fill = GridBagConstraints.HORIZONTAL;
		c_3.gridx = 0;
		c_3.gridy = 2;
		add(lblUrl, c_3); 
		
		addressField = new JTextField();
		c_4= new GridBagConstraints();
		c_4.insets = new Insets(0, 5, 5, 0);
		c_4.fill = GridBagConstraints.HORIZONTAL;
     	c_4.gridx = 1;
     	c_4.gridy = 2;
     	add(addressField, c_4);
     	
     	JLabel lblLogin = new JLabel("Login");
		c_5= new GridBagConstraints();
		c_5.insets = new Insets(5, 5, 5, 5);
		c_5.fill = GridBagConstraints.HORIZONTAL;
		c_5.gridx = 0;
		c_5.gridy = 3;
		add(lblLogin, c_5); 
		lblLogin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				showLoginMenu(e);
					}   
		});
		loginField = new JTextField();
		c_6= new GridBagConstraints();
		c_6.insets = new Insets(0, 5, 5, 0);
		c_6.fill = GridBagConstraints.HORIZONTAL;
     	c_6.gridx = 1;
     	c_6.gridy = 3;
     	add(loginField, c_6);
     	
     	JLabel lblPassword = new JLabel("Password");
		c_7= new GridBagConstraints();
		c_7.insets = new Insets(5, 5, 5, 5);
		c_7.fill = GridBagConstraints.HORIZONTAL;
		c_7.gridx = 0;
		c_7.gridy = 4;
		add(lblPassword, c_7); 
		lblPassword.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				showPasswordMenu(e);
					}   
		});
		
		passwordField = new JTextField();
		c_8= new GridBagConstraints();
		c_8.insets = new Insets(0, 5, 5, 0);
		c_8.fill = GridBagConstraints.HORIZONTAL;
     	c_8.gridx = 1;
     	c_8.gridy = 4;
     	add(passwordField, c_8);
     	
     	 JPanel bottom=new JPanel();
     	c_9= new GridBagConstraints();
     	c_9.weighty=1;
		c_9.fill = GridBagConstraints.VERTICAL;
     	c_9.gridx = 0;
     	c_9.gridy = 5;
     	add(bottom, c_9);
	}
private void showIconMenu(MouseEvent e){
try{
	iconMenu=new JPopupMenu();
	JMenuItem loadItem=new JMenuItem("Load");
	iconMenu.add(loadItem);
	   loadItem.setHorizontalTextPosition(JMenuItem.RIGHT);
	   loadItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
		   try{
		       String favicon$="http://www.google.com/s2/favicons?domain="+addressField.getText();
	            URL url = new URL(favicon$);
	            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	            connection.setDoInput(true);
	            connection.connect();
	            InputStream input = connection.getInputStream();
	            ImageIcon icon = new ImageIcon(ImageIO.read(input));
	             int type = BufferedImage.TYPE_INT_RGB;
	             BufferedImage out = new BufferedImage(24, 24, type);
	             Color background=JWeblinkEditor.this.getBackground();
	             Graphics2D g2 = out.createGraphics();
	             g2.setBackground(background);
	             g2.clearRect(0, 0, 24, 24);
	             Image image=icon.getImage();
	             g2.drawImage(image, 4, 4, null);
	             g2.dispose();
	             icon=new ImageIcon(out);
	             iconIcon.setIcon(icon);
	            input.close();
			   }catch(Exception ee){
				   Logger.getLogger(getClass().getName()).info(ee.toString());
			   }
			}});
	   JMenuItem setItem=new JMenuItem("Set");
		iconMenu.add(setItem);
		   setItem.setHorizontalTextPosition(JMenuItem.RIGHT);
		   setItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
				   System.out.println("WeblinkEditor:set icon");
				   JIconSelector is=new JIconSelector();
				   String isLocator$=is.getLocator();
				   if(entihome$!=null)
						isLocator$=Locator.append(isLocator$,Entigrator.ENTIHOME,entihome$);
					if(entityKey$!=null)
						isLocator$=Locator.append(isLocator$,EntityHandler.ENTITY_KEY,entityKey$);

				   String responseLocator$=getLocator();
				   responseLocator$=Locator.append(responseLocator$,JRequester.REQUESTER_ACTION, ACTION_SET_ICON);
				   responseLocator$=Locator.append(responseLocator$,BaseHandler.HANDLER_METHOD, "response");
				   isLocator$=Locator.append(isLocator$,JRequester.REQUESTER_RESPONSE_LOCATOR, Locator.compressText(responseLocator$));
				   JConsoleHandler.execute(console,isLocator$);
				}});  
		   iconMenu.show(e.getComponent(), e.getX(), e.getY());
}catch(Exception ee){
	Logger.getLogger(getClass().getName()).severe(ee.toString());
}
}   

private void showLoginMenu(MouseEvent e){
try{
	JPopupMenu logonMenu=new JPopupMenu();
	JMenuItem copyItem=new JMenuItem("Copy");
	logonMenu.add(copyItem);
	   copyItem.setHorizontalTextPosition(JMenuItem.RIGHT);
	   copyItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
		   try{
			   StringSelection stringSelection = new StringSelection(loginField.getText());
			    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			   clipboard.setContents(stringSelection, JWeblinkEditor.this);
		   }catch(Exception ee){
				   Logger.getLogger(getClass().getName()).info(ee.toString());
			   }
			}});
	      logonMenu.show(e.getComponent(), e.getX(), e.getY());
}catch(Exception ee){
	Logger.getLogger(getClass().getName()).severe(ee.toString());
}
}   
private void showPasswordMenu(MouseEvent e){
try{
	JPopupMenu	passwordMenu=new JPopupMenu();
	JMenuItem copyItem=new JMenuItem("Copy");
	passwordMenu.add(copyItem);
	   copyItem.setHorizontalTextPosition(JMenuItem.RIGHT);
	   copyItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
		   try{
			   StringSelection stringSelection = new StringSelection(passwordField.getText());
			    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			   clipboard.setContents(stringSelection, JWeblinkEditor.this);
		   }catch(Exception ee){
				   Logger.getLogger(getClass().getName()).info(ee.toString());
			   }
			}});
	   JMenuItem encodeItem=new JMenuItem("Encrypt/decrypt");
		passwordMenu.add(encodeItem);
		   encodeItem.setHorizontalTextPosition(JMenuItem.RIGHT);
		   encodeItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
			   try{
				save();
				   JTextEncrypter te=new JTextEncrypter();
				 String teLocator$=te.getLocator();
				 teLocator$=Locator.append(teLocator$,Entigrator.ENTIHOME, entihome$);
				 teLocator$=Locator.append(teLocator$,JTextEditor.TEXT, passwordField.getText());
				 teLocator$=Locator.append(teLocator$,JTextEditor.TEXT_TITLE, nameField.getText());
				 String weLocator$=JWeblinkEditor.this.getLocator();
				 weLocator$=Locator.append(weLocator$, BaseHandler.HANDLER_METHOD,"response");
				 weLocator$=Locator.append(weLocator$, JRequester.REQUESTER_ACTION,ACTION_ENCODE_PASSWORD);
				 teLocator$=Locator.append(teLocator$,JRequester.REQUESTER_RESPONSE_LOCATOR,Locator.compressText(weLocator$));
				 JConsoleHandler.execute(console, teLocator$);
			   }catch(Exception ee){
					   Logger.getLogger(getClass().getName()).info(ee.toString());
				   }
				}});
	      passwordMenu.show(e.getComponent(), e.getX(), e.getY());
}catch(Exception ee){
	Logger.getLogger(getClass().getName()).severe(ee.toString());
}
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
 * Get the context menu.
 * @return the context menu.
 */
	@Override
	public JMenu getContextMenu() {
		JMenu menu=new JMenu("Context");
		JMenuItem doneItem = new JMenuItem("Done");
		doneItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
				if(requesterResponseLocator$!=null){
					try{
					   byte[] ba=Base64.decodeBase64(requesterResponseLocator$);
					   String responseLocator$=new String(ba,"UTF-8");
      				   JConsoleHandler.execute(console, responseLocator$);
						}catch(Exception ee){
							Logger.getLogger(getClass().getName()).severe(ee.toString());
						}
				}else{
					Entigrator entigrator=console.getEntigrator(entihome$);
					entigrator.replace(entity);	
				  console.back();
				}
				
			}
		} );
		menu.add(doneItem);
		JMenuItem cancelItem = new JMenuItem("Cancel");
		cancelItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					browseUrl(console,locator$);
					}catch(Exception ee){
						Logger.getLogger(getClass().getName()).info(ee.toString());
					}
			}
		} );
		menu.add(cancelItem);
		menu.addSeparator();
		JMenuItem browseItem = new JMenuItem("Browse");
		browseItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					Desktop.getDesktop().browse(new URI(addressField.getText()));
				}catch(Exception ee){
					Logger.getLogger(JWeblinkEditor.class.getName()).info(ee.toString());
				}
			}
		} );
		menu.add(browseItem);
		return menu;
	}
private void save(){
	try{
		Entigrator entigrator=console.getEntigrator(entihome$);
		Sack webset=entigrator.getEntityAtKey(entityKey$);
		webset.putElementItem("web",new Core(nameField.getText(),webLinkKey$,addressField.getText()));
        String login$=loginField.getText();
        String password$=passwordField.getText();
        if(login$!=null||password$!=null)
        	webset.putElementItem("web.login",new Core(login$,webLinkKey$,password$));
        ImageIcon imageIcon = (ImageIcon)iconIcon.getIcon();
        if(imageIcon!=null){
        BufferedImage bi = (BufferedImage)imageIcon.getImage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, "png", baos);
        byte[] res=baos.toByteArray(); 
        String icon$ = Base64.encodeBase64String(res);
        webset.putElementItem("web.icon",new Core(null,webLinkKey$,icon$));
       }
       entigrator.save(webset);
	}catch(Exception e){
       Logger.getLogger(getClass().getName()).severe(e.toString());		
	}
	
}
/**
 * Get the context locator.
 * @return the context locator.
 */
@Override
	public String getLocator() {
		try{
			Properties locator=new Properties();
			locator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
			locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
			 locator.setProperty( JContext.CONTEXT_TYPE,getType());
			String title$=null;
			 if(nameField!=null)
			 if(getTitle()!=null)
				 title$=getTitle();
			 if(title$==null)
				 title$="Web address";
			locator.setProperty(Locator.LOCATOR_TITLE,title$);	
			if(entityLabel$!=null){
				locator.setProperty(EntityHandler.ENTITY_LABEL,entityLabel$);
			}
			if(entityKey$!=null)
				locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
			if(entihome$!=null)
				locator.setProperty(Entigrator.ENTIHOME,entihome$);
			if(webLinkKey$!=null){
				locator.setProperty(JWeblinksPanel.WEB_LINK_KEY,webLinkKey$);
			locator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_ENTITY);
			locator.setProperty(Locator.LOCATOR_ICON_ELEMENT,"web.icon");
			locator.setProperty(Locator.LOCATOR_ICON_CORE,webLinkKey$);
			locator.setProperty(Locator.LOCATOR_ICON_FIELD,Locator.LOCATOR_ICON_FIELD_VALUE);
			}else{
				locator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
				locator.setProperty(Locator.LOCATOR_ICON_CLASS,getClass().getName());
				locator.setProperty(Locator.LOCATOR_ICON_FILE,"globe.png");
			}
				return Locator.toString(locator);
			}catch(Exception e){
	        Logger.getLogger(getClass().getName()).severe(e.toString());
	        return null;
			}

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
			if(debug)
			System.out.println("WeblinkEditor.instantiate:locator="+locator$);
			this.console=console;
			Properties locator=Locator.toProperties(locator$);
			entihome$=locator.getProperty(Entigrator.ENTIHOME);
			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			webLinkKey$=locator.getProperty(JWeblinksPanel.WEB_LINK_KEY);
			
			Entigrator entigrator=console.getEntigrator(entihome$);
			entityLabel$=entigrator.indx_getLabel(entityKey$);
			 if(Locator.LOCATOR_TRUE.equals(locator.getProperty(JFacetRenderer.ONLY_ITEM)))
				 return this;
			entity=entigrator.getEntityAtKey(entityKey$);
           // if(!entigrator.lock_set(entity))
			//	  message$=entigrator.lock_message(entity);
			  
            Core address=entity.getElementItem("web", webLinkKey$);
            addressField.setText(address.value);
            nameField.setText(address.type);
            Core login=entity.getElementItem("web.login", webLinkKey$);
            if(login!=null){
            loginField.setText(login.type);
			passwordField.setText(login.value);
            }
            Core iconCore=entity.getElementItem("web.icon", webLinkKey$);
            if(iconCore!=null&&iconCore.value!=null){
            try{	String icon$=iconCore.value;
       		 byte[] ba=Base64.decodeBase64(icon$);
            	ImageIcon icon = new ImageIcon(ba);
            	Image image= icon.getImage().getScaledInstance(24, 24, 0);
            	icon.setImage(image);
            	iconIcon.setIcon(icon);
            }catch(Exception ee){}
            }
			requesterResponseLocator$=locator.getProperty(JRequester.REQUESTER_RESPONSE_LOCATOR);
			}catch(Exception e){
	        Logger.getLogger(getClass().getName()).severe(e.toString());
		}
		return this;
	}
/**
 * Add icon string to the locator.
 * @param locator$ the origin locator.
 * @return the locator.
 */
@Override
	public String addIconToLocator(String locator$) {
	    	return locator$;
	}
/**
 * Get facet handler class name.
 * @return the facet handler class name.
 */
	@Override
	public String getFacetHandler() {
	return  WebsetHandler.class.getName();
	}
	/**
	 * Get the type of the entity for the facet.
	 * @return the entity type.
	 */
	@Override
	public String getEntityType() {
		return "webset";
	}
	/**
	 * Get facet icon as a Base64 string. 
	 * @return the icon string.
	 */
	@Override
	public String getCategoryIcon(Entigrator entigrator) {
		return Support.readHandlerIcon(null,JEntitiesPanel.class, "globe.png");
	}
	/**
	 * Get category title for entities having the facet type.
	 * @return the category title.
	 */

	@Override
	public String getCategoryTitle() {
         return "Web links";
	}
	/**
	 * Adapt cloned entity.
	 * @param console the main console.
	 * @param locator$ the locator string.
	 */
	@Override
	public void adaptClone(JMainConsole console, String locator$) {
		try{
//			System.out.println("WebsetEditor:adaptClone:locator="+locator$);
			Properties locator=Locator.toProperties(locator$);
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			Entigrator entigrator=console.getEntigrator(entihome$);
			Sack entity=entigrator.getEntityAtKey(entityKey$);
			String entityLocator$=EntityHandler.getEntityLocator(entigrator, entity);
			WebsetHandler websetHandler=new WebsetHandler();
			websetHandler.instantiate(entityLocator$);
			websetHandler.adaptClone(entigrator);
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		}
	}
	/**
	 * Adapt renamed entity.
	 * @param console the main console.
	 * @param locator$ the locator string.
	 */
	@Override
	public void adaptRename(JMainConsole console, String locator$) {
		try{
			System.out.println("WeblinkEditor:adaptRename:locator="+locator$);
			Properties locator=Locator.toProperties(locator$);
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			Entigrator entigrator=console.getEntigrator(entihome$);
			Sack entity=entigrator.getEntityAtKey(entityKey$);
			String entityLocator$=EntityHandler.getEntityLocator(entigrator, entity);
			WebsetHandler websetHandler=new WebsetHandler();
			websetHandler.instantiate(entityLocator$);
			websetHandler.adaptRename(entigrator);
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		}
		
	}
	/**
	 * Rebuild entity's facet related parameters.
	 * @param console the main console
	 * @param entigrator the entigrator.
	 * @param entity the entity.
	 */
	@Override
	public void reindex(JMainConsole console, Entigrator entigrator, Sack entity) {
		 try{
		    	String websetHandler$=WebsetHandler.class.getName();
		    	if(entity.getElementItem("fhandler", websetHandler$)!=null){
					entity.putElementItem("jfacet", new Core(JWebsetFacetAddItem.class.getName(),websetHandler$,JWebsetFacetOpenItem.class.getName()));
					entigrator.save(entity);
				}
		    }catch(Exception e){
		    	Logger.getLogger(getClass().getName()).severe(e.toString());
		    }
	}
	/**
	* Create a new entity of the facet type.
	 * @param console the main console.
	 * @param locator$ the locator string.
	 * @return the new entity key.
	 */
	@Override
	public String newEntity(JMainConsole console, String locator$) {
		JTextEditor textEditor=new JTextEditor();
	    String editorLocator$=textEditor.getLocator();
	    editorLocator$=Locator.append(editorLocator$, JTextEditor.TEXT, "Web links"+Identity.key().substring(0,4));
	    editorLocator$=Locator.append(editorLocator$,Locator.LOCATOR_TITLE,"Web links entity");
	   
	   // String icon$=Support.readHandlerIcon(null,JEntitiesPanel.class, "globe.png");
	   // editorLocator$=Locator.append(editorLocator$,Locator.LOCATOR_ICON,icon$);
	    JWeblinkEditor fe=new JWeblinkEditor();
	    String feLocator$=fe.getLocator();
	    Properties responseLocator=Locator.toProperties(feLocator$);
	    entihome$=Locator.getProperty(locator$,Entigrator.ENTIHOME );
	    if(entihome$!=null){
	      responseLocator.setProperty(Entigrator.ENTIHOME,entihome$);
	    editorLocator$=Locator.append(editorLocator$,Entigrator.ENTIHOME,entihome$);
	    }
	    responseLocator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
		responseLocator.setProperty(BaseHandler.HANDLER_METHOD,"response");
		responseLocator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
		responseLocator.setProperty(BaseHandler.HANDLER_METHOD,"response");
		responseLocator.setProperty(JRequester.REQUESTER_ACTION,ACTION_NEW_ENTITY);
		responseLocator.setProperty(Locator.LOCATOR_TITLE,"Web links");
		 String responseLocator$=Locator.toString(responseLocator);
		String requesterResponseLocator$=Locator.compressText(responseLocator$);
		
		editorLocator$=Locator.append(editorLocator$,JRequester.REQUESTER_RESPONSE_LOCATOR,requesterResponseLocator$);
		JConsoleHandler.execute(console,editorLocator$); 
		return editorLocator$;
	}
	/**
	 * Execute the response locator.
	 * @param console the main console.
	 * @param locator$ the response locator.
	 * 
	 */
	@Override
	public void response(JMainConsole console, String locator$) {
//	System.out.println("WeblinksEditor:response:"+Locator.remove(locator$,Locator.LOCATOR_ICON ));
		
		try{
			Properties locator=Locator.toProperties(locator$);
			String action$=locator.getProperty(JRequester.REQUESTER_ACTION);
			entihome$=locator.getProperty(Entigrator.ENTIHOME);
			Entigrator entigrator=console.getEntigrator(entihome$);
			String text$=locator.getProperty(JTextEditor.TEXT);
			if(ACTION_NEW_ENTITY.equals(action$)){
				Sack newEntity=entigrator.ent_new("webset", text$);
				newEntity.createElement("fhandler");
				newEntity.putElementItem("fhandler", new Core(null,WebsetHandler.class.getName(),null));
				newEntity.putAttribute(new Core (null,"icon","globe.png"));
				entigrator.save(newEntity);
				String icons$=entihome$+"/"+Entigrator.ICONS;
				Support.addHandlerIcon(JEntitiesPanel.class, "globe.png", icons$);
				newEntity=entigrator.ent_reindex(newEntity);
				reindex(console, entigrator, newEntity);
				JEntityFacetPanel efp=new JEntityFacetPanel(); 
				String efpLocator$=efp.getLocator();
				efpLocator$=Locator.append(efpLocator$,Locator.LOCATOR_TITLE,newEntity.getProperty("label"));
				efpLocator$=Locator.append(efpLocator$, Entigrator.ENTIHOME, entihome$);
				efpLocator$=Locator.append(efpLocator$, EntityHandler.ENTITY_KEY, newEntity.getKey());
				efpLocator$=Locator.append(efpLocator$, EntityHandler.ENTITY_LABEL, newEntity.getProperty("label"));
				JEntityPrimaryMenu.reindexEntity(console, efpLocator$);
				Stack<String> s=console.getTrack();
				s.pop();
				console.setTrack(s);
				entigrator.store_replace();
				JConsoleHandler.execute(console, efpLocator$);
				return;
			}
			if(ACTION_SET_ICON.equals(action$)){
				//System.out.println("WeblinkEditor:response:set icon:locator="+locator$);
				try{
					String iconFile$=locator.getProperty(JIconSelector.ICON);
	//				System.out.println("WeblinkEditor:response:set icon="+iconFile$);
		             Sack entity=entigrator.getEntityAtKey(entityKey$);
		             entity.putElementItem("web.icon", new Core(null,webLinkKey$,iconFile$));
		             entigrator.save(entity);
		             locator$=Locator.remove(locator$,BaseHandler.HANDLER_METHOD);
		             locator$=Locator.remove(locator$,JRequester.REQUESTER_ACTION);
		             JConsoleHandler.execute(console, locator$);
		             
				   }catch(Exception ee){
					   Logger.getLogger(getClass().getName()).info(ee.toString());
				   }
				return;
			}
			if(ACTION_ENCODE_PASSWORD.equals(action$)){
				//System.out.println("WeblinkEditor:response:set icon:locator="+locator$);
				try{
				     Sack entity=entigrator.getEntityAtKey(entityKey$);
		             Core login=entity.getElementItem("web.login",webLinkKey$ );
		             if(login==null){
		            	 if(!entity.existsElement("web.login"))
		            		 entity.createElement("web.login");
		            	     login=new Core(null,webLinkKey$,text$);
		             }else
		            	 login.value=text$;
				     entity.putElementItem("web.login",login);
		             entigrator.save(entity);
		             locator$=Locator.remove(locator$,BaseHandler.HANDLER_METHOD);
		             locator$=Locator.remove(locator$,JRequester.REQUESTER_ACTION);
		             JConsoleHandler.execute(console, locator$);
				   }catch(Exception ee){
					   Logger.getLogger(getClass().getName()).info(ee.toString());
				   }
				return;
			}
	 }catch(Exception e){
	    	Logger.getLogger(getClass().getName()).severe(e.toString());
	    }
	}
/**
 * Open URL in the default browse.
 * @param console the main console.
 * @param locator$ the locator string.
 */
	public void browseUrl(JMainConsole console,String locator$){
		try{
			Properties locator=Locator.toProperties(locator$);
			String url$=locator.getProperty(JWeblinksPanel.WEB_LINK_URL);
//			System.out.println("weblinkEditor:browseUrl:url="+url$);
			Desktop.getDesktop().browse(new URI(url$));
			}catch(Exception ee){
				Logger.getLogger(getClass().getName()).info(ee.toString());
			}
	
}
	/**
	 * Get context title.
	 * @return the context title.
	 */	
@Override
public String getTitle() {
	try{
		return nameField.getText();
	}catch(Exception e){
		return null;
	}
	
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
	return "Web link editor";
}
/**
 * Complete context. No action.
 */
@Override
public void close() {

}
/**
 * No action.
 */
@Override
public void lostOwnership(Clipboard clipboard, Transferable contents) {
}
/**
 * No action.
 */
@Override
public void collectReferences(Entigrator entigrator, String entiyKey$, ArrayList<JReferenceEntry> sl) {

}
@Override
public void activate() {
	if(debug)
		System.out.println("JWeblinkEditor:activate:begin");
	Entigrator entigrator=console.getEntigrator(entihome$);
	if(entity==null)
		return;
	if(ignoreOutdate){
		ignoreOutdate=false;
		return;
	}
	if(!entigrator.ent_outdated(entity)){
		System.out.println("JWeblinkEditor:activate:up to date");
		return;
	}
	int n=new ReloadDialog(this).show();
	if(2==n){
		ignoreOutdate=true;
		return;
	}
	if(1==n){
		entigrator.save(entity);
		
	}
	if(0==n){
		 JConsoleHandler.execute(console, getLocator());
		}
	
	
}
@Override
public String getFacetOpenItem() {
	// TODO Auto-generated method stub
	return JWebsetFacetOpenItem.class.getName();
}
@Override
public String getFacetIcon() {
	// TODO Auto-generated method stub
	return "globe.png";
}
}
	