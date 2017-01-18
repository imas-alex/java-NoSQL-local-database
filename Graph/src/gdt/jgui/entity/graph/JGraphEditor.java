package gdt.jgui.entity.graph;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import gdt.data.entity.EntityHandler;
import gdt.data.entity.GraphHandler;
import gdt.data.entity.NodeHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Identity;
import gdt.data.grain.Locator;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.entity.bookmark.JBookmarksEditor;

public class JGraphEditor extends JBookmarksEditor{
	 public JGraphEditor() {
	        super();
	 	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//protected JMenuItem[] mia;
	boolean debug=true;
	@Override
	public JMenu getContextMenu() {
		menu=super.getContextMenu();
		ArrayList <JMenuItem>mil=new ArrayList<JMenuItem>();
		JMenuItem graphItem = new JMenuItem("Graph");
		graphItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			   JGraphRenderer gr=new JGraphRenderer();
			   String grLocator$=gr.getLocator();
			   Properties grLocator=Locator.toProperties(grLocator$);
			   if(debug)
				   System.out.println("JGraphEditor:graph menu item:entity key="+entityKey$+" entihome="+entihome$);
			   grLocator.setProperty(Entigrator.ENTIHOME,entihome$);
			   grLocator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
			   JConsoleHandler.execute(console, Locator.toString(grLocator));
			  // grLocator.setProperty(JRequester.REQUESTER_ACTION,JGraphRenderer.ACTION_SHOW_VIEW);
			}
		} );
		mil.add(graphItem);
		 int cnt=menu.getItemCount();
		 if(cnt>0){
			 mia=new JMenuItem[cnt];
			for(int i=0;i<cnt;i++) 
				mil.add(menu.getItem(i));
		 }	
		mia=mil.toArray(new JMenuItem[0]); 
		menu.addMenuListener(new MenuListener(){
				@Override
				public void menuSelected(MenuEvent e) {
					if(debug)
				System.out.println("JGraphEditor:getConextMenu:event source="+e.getSource().getClass().getName());
				menu=(JMenu)e.getSource();
					menu.removeAll();
				if(mia!=null){
					for(JMenuItem mi:mia)
						menu.add(mi);
				}
				if(hasSelectedItems()){
					menu.addSeparator();	
				JMenuItem copyItem = new JMenuItem("Copy");
					copyItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							copy();
						}
					} );
					menu.add(copyItem);
				 JMenuItem deleteItem = new JMenuItem("Delete");
					deleteItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							try{
								int response = JOptionPane.showConfirmDialog(console.getContentPanel(), "Delete ?", "Confirm",
								        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
							   if (response == JOptionPane.YES_OPTION) {
								  String[] sa=JGraphEditor.this.listSelectedItems();
								  if(sa==null)
									  return;
								  String bookmarkKey$;
								  Entigrator entigrator=console.getEntigrator(entihome$);
								  entity=entigrator.getEntityAtKey(entityKey$);
								  for(String aSa:sa){
									  bookmarkKey$=Locator.getProperty(aSa, BOOKMARK_KEY);
									  if(bookmarkKey$==null)
										  continue;
				                   entity.removeElementItem("jbookmark", bookmarkKey$);
								  }
				                   
								  //entigrator.save(entity);
								  entigrator.replace(entity);
								  JConsoleHandler.execute(console,getLocator());
							   }
							}catch(Exception ee){
								LOGGER.severe(ee.toString());
							}
						}
					} );
					menu.add(deleteItem);
				}
				if(hasToPaste()){
					JMenuItem pasteItem = new JMenuItem("Paste");
					pasteItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							paste();
						}
					} );
					menu.add(pasteItem);
				}
				if(debug)
					System.out.println("JGraphEditor:getContextMenu:add graph item");
				
				
				menu.addSeparator();
				
				
				JMenuItem doneItem = new JMenuItem("Done");
				doneItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if(debug)
							System.out.println("JGraphEditor:getContextMenu:done item");
						
						Entigrator entigrator=console.getEntigrator(entihome$);
						//entigrator.save(entity);
						entigrator.replace(entity);
						console.back();
					}
				} );
				menu.add(doneItem);
				
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
@Override
	public JContext instantiate(JMainConsole console, String locator$) {
		return super.instantiate(console, locator$);
	}
	@Override
	public String getTitle() {
			return "Graph";
	}
	@Override
	public String getCategoryTitle() {
		return "Graphs";
	}
	@Override
	public String getFacetIcon() {
		
		return "graph.png";
	}
	@Override
	public String getLocator() {
		String locator$=super.getLocator();
		locator$=Locator.append(locator$, Locator.LOCATOR_ICON_CLASS_LOCATION, GraphHandler.EXTENSION_KEY);
		locator$=Locator.append(locator$, Locator.LOCATOR_ICON_FILE, "graph.png");
		return locator$;
	}
	@Override
	protected boolean hasToPaste(){
		String[] sa=console.clipboard.getContent();
		if(sa==null||sa.length<1){
			return false;
		}
		NodeHandler nh=new NodeHandler();
		for(String s:sa)
			if(nh.isApplied(console.getEntigrator(entihome$),s))
					return true;
	return false;
	}
	@Override
	protected void paste(){
	    try{
	    	String[] sa=console.clipboard.getContent();
	    	if(sa==null||sa.length<1)
	    		return;
	    	ArrayList<Core>cl=new ArrayList<Core>();
	    	Entigrator entigrator=console.getEntigrator(entihome$);
	    	entity=entigrator.getEntityAtKey(entityKey$);
	    	Core[] ca=entity.elementGet("jbookmark");
	    	if(ca==null){
	    		entity.createElement("jbookmark");
	    	}else
	    	for(Core aCa:ca)
	    		cl.add(aCa);
	    	String title$;
	    	String bookmarkKey$;
	    	locator$=getLocator();
	    	String requesterResponseLocator$=Locator.compressText(locator$);
	    	NodeHandler nh=new NodeHandler();
	    	for(String aSa:sa){
	    		if(!nh.isApplied(entigrator, aSa))
	    			continue;
	    		title$=Locator.getProperty(aSa, Locator.LOCATOR_TITLE);
	    		if(title$==null)
	    			continue;
	    		bookmarkKey$=Locator.getProperty(aSa, BOOKMARK_KEY);
	    		if(bookmarkKey$==null){
	    			bookmarkKey$=Identity.key();
	    			aSa=Locator.append(aSa, BOOKMARK_KEY, bookmarkKey$);
	    			aSa=Locator.append(aSa,JRequester.REQUESTER_RESPONSE_LOCATOR, requesterResponseLocator$);
	    			aSa=Locator.append(aSa, Locator.LOCATOR_CHECKABLE, Locator.LOCATOR_TRUE);
	    		}
	    		cl.add( new Core(title$,bookmarkKey$,aSa));
	    	}
	    	ca=cl.toArray(new Core[0]);
	    	entity.elementReplace("jbookmark", ca);
	    	entigrator.replace(entity);
	    	JConsoleHandler.execute(console, getLocator());
	    }catch(Exception e){
	    	LOGGER.severe(e.toString());
	    }
	}
}
