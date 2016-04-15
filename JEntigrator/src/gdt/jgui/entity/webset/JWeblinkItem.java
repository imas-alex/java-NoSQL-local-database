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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import gdt.data.entity.EntityHandler;
import gdt.data.grain.Locator;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JItemPanel;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
/**
 * This class represents a web site item within webset context.
 * @author imasa
 *
 */
public class JWeblinkItem extends JItemPanel{
	private static final long serialVersionUID = 1L;
	/**
	 * The constructor.
	 * @param console the main console.
	 * @param locator$ the locator string.
	 */
	public JWeblinkItem(final JMainConsole console,final String locator$){
		super(console,locator$);
		popup = new JPopupMenu();
		 JMenuItem openItem=new JMenuItem("Browse");
		   popup.add(openItem);
		   openItem.setHorizontalTextPosition(JMenuItem.RIGHT);
		   openItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try{
					JWeblinkEditor wle=new JWeblinkEditor();
					wle.browseUrl(console,locator$);
					}catch(Exception ee){
						Logger.getLogger(getClass().getName()).info(ee.toString());
					}
				}
			    });
		   JMenuItem editItem=new JMenuItem("Edit");
		   popup.add(editItem);
		   editItem.setHorizontalTextPosition(JMenuItem.RIGHT);
		   editItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try{
					Properties locator=Locator.toProperties(JWeblinkItem.this.locator$);
					String entihome$=locator.getProperty(Entigrator.ENTIHOME);
				    String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
				    String webLinkKey$=locator.getProperty(JWeblinksPanel.WEB_LINK_KEY);
					JWeblinkEditor wle=new JWeblinkEditor();
					String wle$=wle.getLocator();
					Properties wleLocator=Locator.toProperties(wle$);
					if(entihome$!=null)
					wleLocator.setProperty(Entigrator.ENTIHOME,entihome$);
					if(entityKey$!=null)
					wleLocator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
					if(webLinkKey$!=null)
						wleLocator.setProperty(JWeblinksPanel.WEB_LINK_KEY,webLinkKey$);
					JWeblinksPanel wlp=new JWeblinksPanel();
					String wlpLocator$=wlp.getLocator();
					wlpLocator$=Locator.append(wlpLocator$, Entigrator.ENTIHOME,entihome$);
					wlpLocator$=Locator.append(wlpLocator$, EntityHandler.ENTITY_KEY,entityKey$);
					String responseLocator$=wlpLocator$;
					String requesterResponseLocator$=Locator.compressText(responseLocator$);
					wleLocator.setProperty(JRequester.REQUESTER_RESPONSE_LOCATOR,requesterResponseLocator$);
					String wleLocator$=Locator.toString(wleLocator);
					JConsoleHandler.execute(console, wleLocator$);
					}catch(Exception ee){
						Logger.getLogger(getClass().getName()).info(ee.toString());
					}
				}
			    });
	}
}
