package gdt.jgui.tool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;
import javax.swing.BoxLayout;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.apache.commons.codec.binary.Base64;
import gdt.data.entity.BaseHandler;
import gdt.data.grain.Locator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JMainConsole;

public class JLocatorDecryptor extends JPanel implements JContext{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected JMainConsole console;
	
	private JTextField encrypted;
	private JTextField decrypted;
	public JLocatorDecryptor(){
		super();
	}
	@Override
	public JPanel getPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		 encrypted=new JTextField();
		 this.add(encrypted);
		 decrypted=new JTextField();
		 this.add(decrypted);
		return this;
	}

	@Override
	public JMenu getContextMenu() {
		JMenu menu=new JMenu("Context");
		JMenuItem decryptItem = new JMenuItem("Decrypt");
		decryptItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				  String encrypted$=encrypted.getText();
				  if(encrypted$!=null){
					  try{
						  byte[] ba=Base64.decodeBase64(encrypted$); 
							String decrypted$=new String(ba,"UTF-8");
							decrypted.setText(decrypted$);
					  }catch(Exception ee){
						  decrypted.setText(ee.toString());
					  }
				  }
					  
				
			}
		} );
		menu.add(decryptItem);
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
	public String getLocator() {
		Properties locator=new Properties();
		
	    locator.setProperty(Locator.LOCATOR_TYPE, JContext.CONTEXT_TYPE);
	    locator.setProperty(JContext.CONTEXT_TYPE,getType());
	    locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
	    locator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
	   return Locator.toString(locator);
		
	}

	@Override
	public JContext instantiate(JMainConsole console, String locator$) {
		this.console=console;
		return this;
	}

	@Override
	public String getTitle() {
		return "Decrypt locator";
	}

	@Override
	public String getSubtitle() {
	
		return null;
	}

	@Override
	public String getType() {
	
		return "locator decryptor";
	}

	@Override
	public void close() {
	
		
	}

	@Override
	public void activate() {
		
	}

}
