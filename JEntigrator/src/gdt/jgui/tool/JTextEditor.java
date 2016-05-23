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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;
import java.util.logging.Logger;
import gdt.data.entity.BaseHandler;
import gdt.data.grain.Locator;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.entity.JEntitiesPanel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JEditorPane;
import javax.swing.BoxLayout;

import org.apache.commons.codec.binary.Base64;

public class JTextEditor extends JPanel implements JContext,JRequester{
private static final long serialVersionUID = 1L;
/**
 * The text value tag. 
 */
public final static String TEXT="text";	
/**
 * The subtitle tag. 
 */
public final static String SUBTITLE="subtitle";	
/**
 * The text title tag. 
 */
public final static String TEXT_TITLE="text title";	
/**
 * Indicates that the text is the Base64 encoded string. 
 */
public final static String IS_BASE64="is base64";	
private final static String ACTION_ENCODE_TEXT="action encode text";	
private JEditorPane editorPane;
protected String text$;
String entihome$;
protected String textTitle$="Text editor";
protected String subtitle$;
protected JMainConsole console;
private String requesterResponseLocator$;
private boolean  base64=false;
private Logger LOGGER=Logger.getLogger(JTextEditor.class.getName());
/**
 * The default constructor.
 */
public JTextEditor() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		editorPane = new JEditorPane();
		add(editorPane);
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
	 * Get context menu.
	 * @return the context menu. 
	 */
	@Override
	public JMenu getContextMenu() {
		JMenu menu=new JMenu("Context");
		JMenuItem doneItem = new JMenuItem("Done");
		doneItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//System.out.println("TextEditor:done:text="+editorPane.getText());
				if(requesterResponseLocator$!=null){
					try{
					   byte[] ba=Base64.decodeBase64(requesterResponseLocator$);
					   String responseLocator$=new String(ba,"UTF-8");
					   text$=editorPane.getText();
					   if(base64)
						   text$=Locator.compressText(text$);
					   responseLocator$=Locator.append(responseLocator$, TEXT, text$);
					   System.out.println("TextEditor:done:response locator="+Locator.remove(responseLocator$, Locator.LOCATOR_ICON));
					   JConsoleHandler.execute(console, responseLocator$);
						}catch(Exception ee){
							LOGGER.severe(ee.toString());
						}
				}else{
					 //System.out.println("TextEditor:done:requester locator is null");
				  console.back();
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
		menu.addSeparator();
		JMenuItem encryptItem = new JMenuItem("Encrypt/Decrypt");
		encryptItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				   JTextEncrypter ten=new JTextEncrypter();
					 String tenLocator$=ten.getLocator();
					 tenLocator$=Locator.append(tenLocator$,Entigrator.ENTIHOME, entihome$);
					 tenLocator$=Locator.append(tenLocator$,JTextEditor.TEXT, editorPane.getText());
					 String tedLocator$=getLocator();
					 tedLocator$=Locator.append(tedLocator$, BaseHandler.HANDLER_METHOD,"response");
					 tedLocator$=Locator.append(tedLocator$, JRequester.REQUESTER_ACTION,ACTION_ENCODE_TEXT);
					 tenLocator$=Locator.append(tenLocator$,JRequester.REQUESTER_RESPONSE_LOCATOR,Locator.compressText(tedLocator$));
					 JConsoleHandler.execute(console, tenLocator$);
			}
		} );
		menu.add(encryptItem);
		return menu;
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
	    text$=editorPane.getText();
	    if(text$!=null){
	        if(base64){
	        	text$=Locator.compressText(text$);
	        	locator.setProperty(IS_BASE64, Locator.LOCATOR_TRUE);
	        }
	    	locator.setProperty(TEXT,text$);
	    }
	    if(entihome$!=null)
	    	locator.setProperty(Entigrator.ENTIHOME, entihome$);
	    if( requesterResponseLocator$!=null)
	    	locator.setProperty(JRequester.REQUESTER_RESPONSE_LOCATOR, requesterResponseLocator$);
	   locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
	   locator.setProperty(BaseHandler.HANDLER_CLASS,JTextEditor.class.getName());
	   String icon$=Support.readHandlerIcon(JEntitiesPanel.class, "edit.png");
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
//		System.out.println("TextEditor:instantiate:locator="+locator$);
		this.console=console;
		try{
		Properties locator=Locator.toProperties(locator$);
		text$=locator.getProperty(TEXT);
		subtitle$=locator.getProperty(SUBTITLE);
		textTitle$=locator.getProperty(TEXT_TITLE);
		entihome$=locator.getProperty(Entigrator.ENTIHOME);
		if(Locator.LOCATOR_TRUE.equals(locator.getProperty(IS_BASE64))){
				byte[] ba=Base64.decodeBase64(text$);
				text$ = new String(ba, "UTF-8");
				base64=true;
		}
		 requesterResponseLocator$=locator.getProperty(JRequester.REQUESTER_RESPONSE_LOCATOR);
		editorPane.setText(text$);
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
		 return this;
	}
	/**
	 * Get context title.
	 * @return the context title.
	 */	
	@Override
	public String getTitle() {
		if(textTitle$==null)
		   return "Text editor";
		else
			 return textTitle$;
	}
	/**
	 * Get context type.
	 * @return the context type.
	 */	
	@Override
	public String getType() {
		return "Text editor";
	}
	/**
	 * No action.
	 */
	@Override
	public void close() {
		
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
	 * No action.
	 */
	@Override
	public void response(JMainConsole console, String locator$) {
	//	System.out.println("TextEditor:response:locator="+locator$);
		
	}

}
