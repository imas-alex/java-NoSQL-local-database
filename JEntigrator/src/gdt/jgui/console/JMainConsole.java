package gdt.jgui.console;

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
import gdt.data.store.Entigrator;
import gdt.jgui.base.JBaseNavigator;
import gdt.jgui.base.JBasesPanel;
import gdt.jgui.tool.JLocatorDecryptor;
import gdt.jgui.tool.JTextEditor;
import java.awt.Component;
import java.awt.EventQueue;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JMenu;
import java.awt.BorderLayout;
import java.awt.event.WindowEvent;

import javax.swing.JLabel;
import javax.swing.border.BevelBorder;
import java.awt.Color;
/**
 * This is the main application console.
 * @author imasa
 *
 */
public class JMainConsole {
    public final static String ACTION_NEW_BASE="action new base";
	private JFrame frmEntigrator;
	private Hashtable<String,Entigrator>entigrators;
	private Stack<String> track;
	private final Action openWorkspace = new OpenWorkspace();
	private final Action showTrack = new ShowTrack();
	private final Action backTrack = new BackTrack();
	private final Action clearTrack = new ClearTrack();
	private final Action showClipboard = new ShowClipboard();
	private final Action showRecent = new ShowRecent();
	private final Action baseNavigator = new BaseNavigator();
	private JMenu contextMenu;
	private JMenuBar menuBar;
	JLabel subtitle;
	JMenuItem File;
	JMenuItem BaseNavigator;
	JMenu trackMenu;
	
	Hashtable <String,String>recents;
	String entihome$;
	public String saveId$;
	public String outdatedTreatment$;
	public JClipboard clipboard=new JClipboard();
	static boolean debug=false;
	public Map<String,String>cache;
	/**

   * This is the main method which displays the main application console.
   * @param args Unused.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					final JMainConsole console = new JMainConsole();
					console.frmEntigrator.setVisible(true);
					console.entigrators=new Hashtable<String,Entigrator>();
					console.setTrack(new Stack<String>());
					JTrackPanel.restoreTrack(console);
					JClipboard.restore(console);
					JRecentPanel.restore(console);
					console.frmEntigrator.addWindowListener(new java.awt.event.WindowAdapter() {
					    public void windowClosing(WindowEvent winEvt) {
					      //  System.out.println("MainConsole:exit:user home="+System.getProperty("user.home") );
					        
					    	JTrackPanel.storeTrack(console);
					        JClipboard.store(console);
					        JRecentPanel.store(console);
					        System.exit(0);
					    }
					    public void windowActivated(WindowEvent e) {
					    	try{
					    		if(debug)
							    	System.out.println("MainConsole:activated");   	    	
					    		int cnt=console.frmEntigrator.getContentPane().getComponentCount();
					    	if(cnt>0){
					    	
					    	JContext current=(JContext)console.frmEntigrator.getContentPane().getComponent(0);
					    	
					    	if(current!=null)
					    		if(debug)
							    	System.out.println("MainConsole:activated:context="+current.getTitle());   	    	
					    		  	
					    	    current.activate();
					    	}
					    	}catch(Exception ee){
					    		Logger.getLogger(getClass().getName()).severe(ee.toString());
					    	}
					    }
					    public void windowDeactivated(WindowEvent e){
					    	if(debug)
					    	System.out.println("MainConsole:deactivated.save id="+console.saveId$);  
					    }
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * The default constructor.
	 */
	public JMainConsole() {
		initialize();
	}
	/**
	 * Set track stack which contains previous shown locators.
	 * @param track the track stack.
	 */
    public void setTrack(Stack<String> track){
    	this.track=track;
    }
    /**
     * Get track stack which contains previous shown locators. 
     * @return the track stack.
     */
    public Stack<String> getTrack(){
    	if(track==null)
    		track=new Stack<String>();
    	return track;
    }
    public Hashtable<String,String> getRecents(){
    	return recents;
    }
	private void initialize() {
		
		frmEntigrator = new JFrame();
		frmEntigrator.setTitle("Entigrator");
		frmEntigrator.setBounds(100, 100, 450, 300);
		frmEntigrator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		subtitle = new JLabel("Subtitle");
		subtitle.setAlignmentX(Component.RIGHT_ALIGNMENT);
		subtitle.setLabelFor(subtitle);
		subtitle.setBorder(new BevelBorder(BevelBorder.RAISED));
		subtitle.setBackground(Color.WHITE);
		subtitle.setForeground(Color.BLACK);
		menuBar = new JMenuBar();
		frmEntigrator.setJMenuBar(menuBar);
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		File = new JMenuItem("Bases");
		File.setAction(openWorkspace);
		fileMenu.add(File);
		recents=new Hashtable<String,String>();
		cache=new HashMap<String,String>();
		JMenuItem newBase = new JMenuItem("New base");
		newBase.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser(); 
			    chooser.setCurrentDirectory(new java.io.File(System.getProperty("user.home")));
			    chooser.setDialogTitle("Select workspace");
			    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			    chooser.setAcceptAllFileFilterUsed(false);
			    if (chooser.showOpenDialog(frmEntigrator) == JFileChooser.APPROVE_OPTION) { 
			    	String entiroot$=chooser.getSelectedFile().getPath();
			    	Properties locator= new Properties();
			    	locator.setProperty(BaseHandler.ENTIROOT,entiroot$);
			    	locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
			    	locator.setProperty(BaseHandler.HANDLER_CLASS,JBasesPanel.class.getName());
			    	locator.setProperty(Entigrator.ENTIHOME,entiroot$+"/New_base");
			    	locator.setProperty(BaseHandler.HANDLER_METHOD,"response");
			    	String locator$=Locator.toString(locator);
			    	JTextEditor textEditor=new JTextEditor();
					String editorLocator$=textEditor.getLocator();
					Properties editorLocator=Locator.toProperties(editorLocator$);
					editorLocator.setProperty(JRequester.REQUESTER_RESPONSE_LOCATOR, Locator.compressText(locator$));
					editorLocator.setProperty(JTextEditor.TEXT,"New_base");
					editorLocator.setProperty(Entigrator.ENTIHOME,entiroot$+"/New_base");
					String teLocator$=Locator.toString(editorLocator);
			    	JConsoleHandler.execute(JMainConsole.this, teLocator$);
				
			    }
			}
		} );
		fileMenu.add(newBase);
		trackMenu = new JMenu("Track");
		menuBar.add(trackMenu);
		JMenuItem backItem = new JMenuItem("Back");
		backItem.setAction(backTrack);
		trackMenu.add(backItem);
		trackMenu.addSeparator();
		JMenuItem showItem = new JMenuItem("Show");
		showItem.setAction(showTrack);
		trackMenu.add(showItem);
		JMenuItem clipboardItem = new JMenuItem("Clipboard");
		clipboardItem.setAction(showClipboard);
		trackMenu.add(clipboardItem);
		JMenuItem recentItem = new JMenuItem("Recent");
		recentItem.setAction(showRecent);
		trackMenu.add(recentItem);
		JMenuItem navigatorItem = new JMenuItem("Base navigator");
		navigatorItem.setAction(baseNavigator);
		trackMenu.add(navigatorItem);
		trackMenu.addSeparator();
		JMenuItem clearItem = new JMenuItem("Clear");
		clearItem.setAction(clearTrack);
		trackMenu.add(clearItem);
		trackMenu.addSeparator();
		JMenuItem decryptItem = new JMenuItem("Decrypt locator");
		decryptItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				   JLocatorDecryptor ld=new JLocatorDecryptor();
				  // JConsoleHandler.execute(JMainConsole.this, ld.getLocator());
				   JMainConsole.this.putContext(ld,ld.getLocator());
				  }
		} );
		
		trackMenu.add(decryptItem);
	}
	
/**
 * Put entigrator into the cache
 * @param entigrator theentigrator.
 */
	public void putEntigrator(Entigrator entigrator){
	if(entigrators==null)
		entigrators=new Hashtable<String,Entigrator>();
	entigrators.put(entigrator.getEntihome(),entigrator);
	
}
	
	/**
	 * Get entigrator.
	 * @param entihome$ the database directory
	 * @return the entigrator.
	 */
public Entigrator getEntigrator(String entihome$){
	try{
	Entigrator entigrator= entigrators.get(entihome$);
	if(entigrator==null){
		entigrator =new Entigrator(new String[]{entihome$});

		if(entigrator!=null){
			putEntigrator(entigrator);
		}
		}
	return entigrator;
	}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
		return null;
	}
}
private void clearContextMenu(){
	int cnt=menuBar.getComponentCount();
	ArrayList<JComponent>cl=new ArrayList<JComponent>();
	for(int i=0;i<cnt;i++){
		String menuTitle$=((JMenu)menuBar.getComponent(i)).getText();
        if("Context".equals(menuTitle$))
         	cl.add((JComponent)menuBar.getComponent(i));	
	}
	JComponent[] ca=cl.toArray(new JComponent[0]);
	if(ca!=null)
		for(JComponent jc:ca)
			menuBar.remove(jc);
}
/**
 * Put the context into the main console.
 * @param context the context.
 * @param locator$ the context locator.
 */
public void putContext(JContext context,String locator$){
	if(debug)
	System.out.println("JMainConsole:putContext::context="+context.getClass().getName()+" locator="+locator$);
	try{
	int cnt=frmEntigrator.getContentPane().getComponentCount();
	if(cnt>0){
	JContext current=(JContext)frmEntigrator.getContentPane().getComponent(0);
	if(current!=null)
	    current.close();
	}
	}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
	}
	entihome$=Locator.getProperty(locator$, Entigrator.ENTIHOME);
	
	if(entihome$!=null)
	    getEntigrator(entihome$);
	frmEntigrator.getContentPane().removeAll();
	
	context.instantiate(this, locator$);
	if(debug)
	System.out.println("JMainConsole:putContext:1");
	frmEntigrator.getContentPane().add(context.getPanel(),BorderLayout.CENTER );
	if(debug)
	System.out.println("JMainConsole:putContext:2");
	frmEntigrator.setTitle(context.getTitle());
	
	String ctxLocator$=context.getLocator();
	if(ctxLocator$!=null)
	        JTrackPanel.putMember(this,ctxLocator$ );
	try{
	contextMenu=context.getContextMenu();
	
	if(contextMenu!=null){
		clearContextMenu();
		menuBar.add(contextMenu);
		contextMenu.setVisible(true);
	}else
		clearContextMenu();	
	}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe("cannot get context menu for context="+context.getClass().getName()+".error:"+ e.toString()); 	
	}
	
	String subtitle$=context.getSubtitle();
	if(subtitle$!=null){
	subtitle.setText(subtitle$);
	frmEntigrator.getContentPane().add(subtitle,BorderLayout.SOUTH );
	}
		
	menuBar.revalidate();
	menuBar.repaint();
		
	frmEntigrator.getContentPane().revalidate();
	frmEntigrator.getContentPane().repaint();
	
}

/**
 * Get the content panel of the main frame.
 * @return the content panel.
 */
public Component getContentPanel(){
	 return	frmEntigrator.getContentPane();
	}

private class OpenWorkspace extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public OpenWorkspace() {
			putValue(NAME, "Open workspace");
			putValue(SHORT_DESCRIPTION, "List bases in directory");
		}
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser(); 
			    chooser.setCurrentDirectory(new java.io.File(System.getProperty("user.home")));
			    chooser.setDialogTitle("Select databases");
			    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			    chooser.setAcceptAllFileFilterUsed(false);
			    if (chooser.showOpenDialog(frmEntigrator) == JFileChooser.APPROVE_OPTION) { 
			    	
			    	String entiroot$=chooser.getSelectedFile().getPath();
			    	Properties locator= new Properties();
			    	locator.setProperty(BaseHandler.ENTIROOT,entiroot$);
			    	locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
			    	locator.setProperty(BaseHandler.HANDLER_CLASS,JBasesPanel.class.getName());
			    	locator.setProperty(Locator.LOCATOR_TITLE,entiroot$);
			    	String locator$=Locator.toString(locator);
			    	JBasesPanel bp=new JBasesPanel();
			    	bp.instantiate(JMainConsole.this, locator$);
			    	putContext(bp, bp.getLocator());
			    	
		      }
			    else {
			    	Logger.getLogger(JMainConsole.class.getName()).info(" no selection");
			      }
			     }
		}
	private class ShowTrack extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public ShowTrack() {
			putValue(NAME, "Show track");
			putValue(SHORT_DESCRIPTION, "Display track panel");
		}
		public void actionPerformed(ActionEvent e) {
			try{
				if(track.isEmpty())
					return;
				JTrackPanel trackPanel=new JTrackPanel();
				String locator$=trackPanel.getLocator();
//				System.out.println("MainConsole:showTrack:locator="+locator$);
				JConsoleHandler.execute(JMainConsole.this, locator$);
			}catch(Exception ee) {
			    	Logger.getLogger(JMainConsole.class.getName()).severe(ee.toString());
			    }
			     }
		}
	private class ShowClipboard extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public ShowClipboard() {
			putValue(NAME, "Show clipboard");
			putValue(SHORT_DESCRIPTION, "Display clipboard content");
		}
		public void actionPerformed(ActionEvent e) {
			try{
				JClipboardPanel clipboardPanel=new JClipboardPanel();
				JConsoleHandler.execute(JMainConsole.this, clipboardPanel.getLocator());
			}catch(Exception ee) {
			    	Logger.getLogger(JMainConsole.class.getName()).severe(ee.toString());
			    }
			     }
		}
/**
 * Display the previous context.	
 */
	public void back(){
		if(track.size()>0)
			  JTrackPanel.popMember(JMainConsole.this);
	    	String locator$=JTrackPanel.popMember(JMainConsole.this);
	   	if(locator$!=null){
	    	    JConsoleHandler.execute(JMainConsole.this, locator$);
	    	}
	}
	public JFrame getFrame(){
		return frmEntigrator;
		
	}
	private class BackTrack extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public BackTrack() {
			putValue(NAME, "Back");
			putValue(SHORT_DESCRIPTION, "Previous screen");
		}
		public void actionPerformed(ActionEvent e) {
			try{
				if(track.size()>0)
				  JTrackPanel.popMember(JMainConsole.this);
		    	String locator$=JTrackPanel.popMember(JMainConsole.this);
		    	//System.out.println("JMainConsole:BackTrack:locator="+Locator.remove(locator$, Locator.LOCATOR_ICON));
		    	if(locator$!=null){
		    	    JConsoleHandler.execute(JMainConsole.this, locator$);
		    	}
			}catch(Exception ee) {
			    	Logger.getLogger(JMainConsole.class.getName()).severe(ee.toString());
			    }
			     }
		}
	private class ClearTrack extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public ClearTrack() {
			putValue(NAME, "Clear");
			putValue(SHORT_DESCRIPTION, "Clear track");
		}
		public void actionPerformed(ActionEvent e) {
			JTrackPanel.clearMembers(JMainConsole.this);
			frmEntigrator.getContentPane().removeAll();	
			frmEntigrator.revalidate();
			frmEntigrator.repaint();
		}
		}
	private class ShowRecent extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public ShowRecent() {
			putValue(NAME, "Show recent");
			putValue(SHORT_DESCRIPTION, "Display recent content");
		}
		public void actionPerformed(ActionEvent e) {
			try{
				JRecentPanel recentPanel=new JRecentPanel();
				JConsoleHandler.execute(JMainConsole.this, recentPanel.getLocator());
			}catch(Exception ee) {
			    	Logger.getLogger(JMainConsole.class.getName()).severe(ee.toString());
			    }
			     }
		}
	private class BaseNavigator extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public BaseNavigator() {
			putValue(NAME, "Base navigator");
			putValue(SHORT_DESCRIPTION, "Display base navigator");
		}
		public void actionPerformed(ActionEvent e) {
			try{
				if(entihome$==null)
					return;
				JBaseNavigator navigatorPanel=new JBaseNavigator();
				String np$=navigatorPanel.getLocator();
				np$=Locator.append(np$, Entigrator.ENTIHOME, entihome$);
				JConsoleHandler.execute(JMainConsole.this, np$);
			}catch(Exception ee) {
			    	Logger.getLogger(JMainConsole.class.getName()).severe(ee.toString());
			    }
			     }
		}
	}

