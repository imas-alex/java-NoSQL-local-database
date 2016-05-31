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
import gdt.data.grain.Locator;
import gdt.data.grain.Support;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.entity.JEntitiesPanel;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JTextArea;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;

import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.border.TitledBorder;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.wb.swing.FocusTraversalOnArray;
/**
 * This class encrypt/decrypt text using the master password.
 * @author imasa
 *
 */
public class JTextEncrypter extends JPanel implements JContext{
	private static final long serialVersionUID = 1L;
	private JPasswordField passwordField;
	private JTextArea textArea;
	protected String text$;
	protected String title$="Text encoder";
	protected String subtitle$;
	protected JMainConsole console;
	private String requesterResponseLocator$;
	private JMenu menu;
	/**
	 * The default constructor.
	 */
	public JTextEncrypter() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Master password", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		JCheckBox chckbxNewCheckBox = new JCheckBox("Show");
		chckbxNewCheckBox.setHorizontalTextPosition(SwingConstants.LEFT);
		chckbxNewCheckBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() != ItemEvent.SELECTED) {
		           passwordField.setEchoChar('*');
		        } else {
		             passwordField.setEchoChar((char) 0);
		        }
			}
		});
		panel.add(chckbxNewCheckBox);
		panel.setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{chckbxNewCheckBox, passwordField}));
		passwordField = new JPasswordField();
		passwordField.setMaximumSize(
			    new Dimension(Integer.MAX_VALUE,
			    		passwordField.getPreferredSize().height));
		panel.add(passwordField);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Text", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		textArea = new JTextArea();
		textArea.setColumns(1);
		panel_1.add(textArea);
		
	}
	/**
	 * Get context menu.
	 * @return the context menu. 
	 */
	@Override
	public JMenu getContextMenu() {
		menu=new JMenu("Context");
		menu.addMenuListener(new MenuListener(){
			@Override
			public void menuSelected(MenuEvent e) {
				menu.removeAll();
				JMenuItem doneItem = new JMenuItem("Done");
				doneItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if(requesterResponseLocator$!=null){
							try{
							   byte[] ba=Base64.decodeBase64(requesterResponseLocator$);
							   String responseLocator$=new String(ba,"UTF-8");
							   text$=textArea.getText();
							   responseLocator$=Locator.append(responseLocator$, JTextEditor.TEXT, text$);
							   //System.out.println("TextEditor:done:response locator="+responseLocator$);
							   JConsoleHandler.execute(console, responseLocator$);
								}catch(Exception ee){
									Logger.getLogger(JTextEncrypter.class.getName()).severe(ee.toString());
								}
						}else
						  console.back();
						
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
				char[] master=passwordField.getPassword();
//				System.out.println("Textencoder:context menu:master="+master.length);
				if(master.length>6){
				menu.addSeparator();
				JMenuItem encryptItem = new JMenuItem("Encrypt");
				encryptItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						char[] master=passwordField.getPassword();
						DesEncrypter desEncrypter = new DesEncrypter(master);
						String encrypted$ = desEncrypter.encrypt(textArea.getText());
                       textArea.setText(encrypted$);
					}
				} );
				
				menu.add(encryptItem);
				JMenuItem decryptItem = new JMenuItem("Decrypt");
				decryptItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						char[] master=passwordField.getPassword();
						DesEncrypter desEncrypter = new DesEncrypter(master);
						String decrypted$ = desEncrypter.decrypt(textArea.getText());
						 if(decrypted$!=null&&decrypted$.length()>0)
						textArea.setText(decrypted$);
					}
				} );
				menu.add(decryptItem);
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
	    locator.setProperty(JContext.CONTEXT_TYPE,getType());
	    text$=textArea.getText();
	    if(text$!=null){
	    	locator.setProperty(JTextEditor.TEXT,text$);
	    }
	    
	    if( requesterResponseLocator$!=null)
	    	locator.setProperty(JRequester.REQUESTER_RESPONSE_LOCATOR, requesterResponseLocator$);
	   locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
	   locator.setProperty(BaseHandler.HANDLER_CLASS,JTextEncrypter.class.getName());
	   String icon$=Support.readHandlerIcon(null,JEntitiesPanel.class, "lock.png");
       locator.setProperty(Locator.LOCATOR_ICON,icon$);
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
		this.console=console;
		try{
		Properties locator=Locator.toProperties(locator$);
		text$=locator.getProperty(JTextEditor.TEXT);
		subtitle$=locator.getProperty(JTextEditor.SUBTITLE);
		title$=locator.getProperty(JTextEditor.TEXT_TITLE);
    	 requesterResponseLocator$=locator.getProperty(JRequester.REQUESTER_RESPONSE_LOCATOR);
		textArea.setText(text$);
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		}
		 return this;
	}
	@Override
	public String getTitle() {
		return title$;
	}
	/**
	 * Get context subtitle.
	 * @return the context subtitle.
	 */	
	@Override
	public String getSubtitle() {
			return subtitle$;
	}
	/**
	 * Get context type.
	 * @return the context type.
	 */	
	@Override
	public String getType() {
		return "Text encoder";
	}
	/**
	 * No action.
	 */
	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
	 static class DesEncrypter {
        Cipher ecipher;
        Cipher dcipher;
        byte[] salt = {
                (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
                (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03
        };
        int iterationCount = 19;
        public DesEncrypter(char[] master) {
            try {
                KeySpec keySpec = new PBEKeySpec(master, salt, iterationCount);
                SecretKey key = SecretKeyFactory.getInstance(
                        "PBEWithMD5AndDES").generateSecret(keySpec);
                ecipher = Cipher.getInstance(key.getAlgorithm());
                dcipher = Cipher.getInstance(key.getAlgorithm());
                AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
                ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
                dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
            } catch (java.security.InvalidAlgorithmParameterException e) {
            } catch (java.security.spec.InvalidKeySpecException e) {
            } catch (javax.crypto.NoSuchPaddingException e) {
            } catch (java.security.NoSuchAlgorithmException e) {
            } catch (java.security.InvalidKeyException e) {
            }
        }

        public String encrypt(String str) {
            try {
                byte[] utf8 = str.getBytes("UTF8");
                byte[] enc = ecipher.doFinal(utf8);
                return Base64.encodeBase64String(enc);
            } catch (javax.crypto.BadPaddingException e) {
            } catch (IllegalBlockSizeException e) {
            } catch (java.io.IOException e) {
            }
            return null;
        }

        public String decrypt(String str) {
            try {
                byte[] dec =Base64.decodeBase64(str); 
                byte[] utf8 = dcipher.doFinal(dec);
                return new String(utf8, "UTF8");
            } catch (javax.crypto.BadPaddingException e) {
            } catch (IllegalBlockSizeException e) {
            } catch (java.io.IOException e) {
            }
            return null;
        }
    }
}
