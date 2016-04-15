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
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.entity.JEntitiesPanel;
import javax.swing.JPanel;
import java.awt.Image;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import org.apache.commons.codec.binary.Base64;
import java.util.Properties;
import java.util.logging.Logger;
/**
 * This is an icon selector context.
 * @author imasa
 *
 */
public class JIconSelector extends JPanel implements JContext{
	private static final long serialVersionUID = 1L;
/**
 * The icon tag. 
 */
	public final static String ICON="icon";	
	private String icon$;
private JMainConsole console;
private String entihome$;
private String entityKey$;
private String entityLabel$;
private String requesterResponseLocator$;
private final int largeIcon=64;
private final int smallIcon=24;
private Logger LOGGER=Logger.getLogger(JTextEditor.class.getName());
JScrollPane scrollPane;
JPanel panel ;
/**
 * The default constructor.
 */
public JIconSelector() {
		FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT, 5, 5);
		flowLayout.setAlignOnBaseline(true);
		WrapLayout wrapLayout = new WrapLayout(FlowLayout.LEFT, 5, 5);
		wrapLayout.setAlignOnBaseline(true);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		panel = new JPanel();
		panel.setLayout(wrapLayout);
		scrollPane = new JScrollPane(panel);
		add(scrollPane);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
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
 * No action.
 * @return null.
 */
	@Override
	public JMenu getContextMenu() {
		// TODO Auto-generated method stub
		return null;
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
	    locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
	    if(entihome$!=null)
		       locator.setProperty(Entigrator.ENTIHOME,entihome$);
	    if(entityKey$!=null)
		       locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
	    if(entityLabel$!=null)
		       locator.setProperty(EntityHandler.ENTITY_LABEL,entityLabel$);
	    String icon$=Support.readHandlerIcon(JEntitiesPanel.class,"icon.png" );
	      locator.setProperty(Locator.LOCATOR_ICON,icon$);
	   locator.setProperty(BaseHandler.HANDLER_CLASS,JIconSelector.class.getName());
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
			 panel.removeAll();
			 Properties locator=Locator.toProperties(locator$);
			 entihome$=locator.getProperty(Entigrator.ENTIHOME);
			 entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			 entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
			 requesterResponseLocator$=locator.getProperty(JRequester.REQUESTER_RESPONSE_LOCATOR);
//			 System.out.println("IconSelector:instantiate:locator="+locator$);
			 Entigrator entigrator=console.getEntigrator(entihome$);
			 String icons$=entigrator.ent_getHome(Entigrator.ICONS);
			 File icons=new File(icons$);
			 File[] fa=icons.listFiles();
			 if(fa==null)
				 return this;
			 ImageIcon icon;
			 JLabel label;
			 Image img;
			 for(File aFa:fa){
				  icon = new ImageIcon(aFa.getPath());
				   img = icon.getImage() ;  
				  img = img.getScaledInstance( smallIcon, smallIcon,  java.awt.Image.SCALE_SMOOTH ) ;  
				  icon = new ImageIcon( img );
				  label=new JLabel();
				  label.setIcon(icon);
				  label.setName(aFa.getName());
				  label.addMouseListener(new MouseAdapter() {
				      public void mouseClicked(MouseEvent me) {
				    	  try{
				    	  JLabel label=(JLabel)me.getSource();
				    	  icon$=label.getName();
				          byte[] ba=Base64.decodeBase64(requesterResponseLocator$);
				          String responseLocator$=new String(ba,"UTF-8");
				          responseLocator$=Locator.append(responseLocator$, ICON, icon$);
				          JConsoleHandler.execute(JIconSelector.this.console, responseLocator$); 
				    	  }catch(Exception ee){
									LOGGER.severe(ee.toString());
								}
				      }
				    });
				  
				  panel.add(label);
			 }
			
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
		return "Icon selector";
	}
	/**
	 * Get context type.
	 * @return the context type.
	 */	
	@Override
	public String getType() {
		return "Icon selector";
	}
	/**
	 * No action.
	 */
	@Override
	public void close() {
		// TODO Auto-generated method stub
	}
	/**
	 * Get context subtitle.
	 * @return the context subtitle.
	 */		
	@Override
	public String getSubtitle() {
		return entityLabel$;
	}
}
