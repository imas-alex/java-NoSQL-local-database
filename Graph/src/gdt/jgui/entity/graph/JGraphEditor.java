package gdt.jgui.entity.graph;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Stack;
import java.util.logging.Logger;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.apache.commons.codec.binary.Base64;

import gdt.data.entity.BaseHandler;
import gdt.data.entity.EdgeHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.GraphHandler;
import gdt.data.entity.NodeHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Identity;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.entity.JEntityPrimaryMenu;
import gdt.jgui.entity.JReferenceEntry;
import gdt.jgui.entity.bookmark.JBookmarkItem;
import gdt.jgui.entity.bookmark.JBookmarksEditor;
import gdt.jgui.tool.JIconSelector;
import gdt.jgui.tool.JTextEditor;

public class JGraphEditor extends JBookmarksEditor{
	 public JGraphEditor() {
	        super();
	 	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String ACTION_CREATE_GRAPH="action create graph";
	//protected JMenuItem[] mia;
	boolean debug=false;
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

	@Override
	public String newEntity(JMainConsole console, String locator$) {
		try{
			Properties locator=Locator.toProperties(locator$);
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			JTextEditor textEditor=new JTextEditor();
		    String teLocator$=textEditor.getLocator();
		    teLocator$=Locator.append(teLocator$, Entigrator.ENTIHOME,entihome$);
		    teLocator$=Locator.append(teLocator$, JTextEditor.TEXT_TITLE,"New graph");
		    String text$="NewGraph"+Identity.key().substring(0, 4);
		    teLocator$=Locator.append(teLocator$, JTextEditor.TEXT,text$);
		    JGraphEditor ge=new JGraphEditor();
		    String geLocator$=ge.getLocator();
		    geLocator$=Locator.append(geLocator$, Entigrator.ENTIHOME,entihome$);
		    geLocator$=Locator.append(geLocator$, EntityHandler.ENTITY_KEY,entityKey$);
		    geLocator$=Locator.append(geLocator$, BaseHandler.HANDLER_METHOD,"response");
		    geLocator$=Locator.append(geLocator$, JRequester.REQUESTER_ACTION,ACTION_CREATE_GRAPH);
		    String requesterResponseLocator$=Locator.compressText(geLocator$);
		    teLocator$=Locator.append(teLocator$,JRequester.REQUESTER_RESPONSE_LOCATOR,requesterResponseLocator$);
		    JConsoleHandler.execute(console, teLocator$);
		}catch(Exception ee){   
			LOGGER.severe(ee.toString());
		}
		return null;
	}
	@Override
	public void response(JMainConsole console, String locator$) {
		try{
			Properties locator=Locator.toProperties(locator$);
			String action$=locator.getProperty(JRequester.REQUESTER_ACTION);
			if(ACTION_CREATE_GRAPH.equals(action$)){
				String entihome$=locator.getProperty(Entigrator.ENTIHOME);
				String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
				String text$=locator.getProperty(JTextEditor.TEXT);
				Entigrator entigrator=console.getEntigrator(entihome$);  
				Sack graph=entigrator.ent_new("graph", text$);
				graph=entigrator.ent_assignProperty(graph, "graph", graph.getProperty("label"));
				   graph=entigrator.ent_assignProperty(graph, "bookmarks", graph.getProperty("label"));
				   graph.putAttribute(new Core(null,"icon","graph.png"));
				   graph.createElement("fhandler");
				   graph.putElementItem("fhandler", new Core(null,"gdt.data.entity.facet.FieldsHandler",null));
				   graph.putElementItem("fhandler", new Core(null,"gdt.data.entity.facet.BookmarksHandler",null));
				   graph.putElementItem("fhandler", new Core(null,"gdt.data.entity.GraphHandler","_Tm142C8Sgti2iAKlDEcEXT2Kj1E"));
				   graph.createElement("jfacet");
				   graph.putElementItem("jfacet", new Core("gdt.jgui.entity.fields.JFieldsFacetAddItem","gdt.data.entity.facet.FieldsHandler",null));
				   graph.putElementItem("jfacet", new Core("gdt.jgui.entity.bookmark.JBookmarksFacetAddItem","gdt.jgui.entity.bookmark.JBookmarksFacetOpenItem",null));
				   graph.putElementItem("jfacet", new Core(null,"gdt.data.entity.GraphHandler","gdt.jgui.entity.graph.JGraphFacetOpenItem"));
				 
				   entigrator.save(graph);
				   entigrator.saveHandlerIcon(JGraphEditor.class, "graph.png");
				   entityKey$=graph.getKey();
				   JGraphEditor ge=new JGraphEditor();
				   String geLocator$=ge.getLocator();
				   geLocator$=Locator.append(geLocator$, Entigrator.ENTIHOME, entihome$);
				   geLocator$=Locator.append(geLocator$, EntityHandler.ENTITY_KEY, entityKey$);
				   JEntityPrimaryMenu.reindexEntity(console, geLocator$);
				   Stack<String> s=console.getTrack();
				   s.pop();
				   console.setTrack(s);
				   entigrator.store_replace();
				   JConsoleHandler.execute(console, geLocator$);
				   return;
				}
			
			String requesterResponseLocator$=locator.getProperty(JRequester.REQUESTER_RESPONSE_LOCATOR);
	        byte[] ba=Base64.decodeBase64(requesterResponseLocator$); 
			String gm=new String(ba,"UTF-8");
		    Properties bmLocator=Locator.toProperties(gm);
			String entihome$=bmLocator.getProperty(Entigrator.ENTIHOME);
			String entityKey$=bmLocator.getProperty(EntityHandler.ENTITY_KEY);
			String bookmarkKey$=locator.getProperty(BOOKMARK_KEY);
			String text$=locator.getProperty(JTextEditor.TEXT);
			Entigrator entigrator=console.getEntigrator(entihome$);
			entity=entigrator.getEntityAtKey(entityKey$);
			Core bookmark=entity.getElementItem("jbookmark", bookmarkKey$);

			if(JBookmarkItem.ACTION_RENAME.equals(action$)){
			bookmark.type=text$;

			}
			if(JBookmarkItem.ACTION_SET_ICON.equals(action$)){
				String icon$=locator.getProperty(JIconSelector.ICON);
				String bookmarkLocator$=bookmark.value;
				bookmarkLocator$=Locator.append(bookmarkLocator$, Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_ICONS);
				bookmarkLocator$=Locator.append(bookmarkLocator$, Locator.LOCATOR_ICON_FILE,icon$);
				bookmark.value=  bookmarkLocator$;
			}
			entity.putElementItem("jbookmark", bookmark);
			entigrator.save(entity);
			String bmeLocator$=getLocator();
			bmeLocator$=Locator.append(bmeLocator$, Entigrator.ENTIHOME, entihome$);
			bmeLocator$=Locator.append(bmeLocator$, EntityHandler.ENTITY_KEY, entityKey$);
			JConsoleHandler.execute(console, bmeLocator$);
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
		}
	@Override
	public void collectReferences(Entigrator entigrator, String entityKey$, ArrayList<JReferenceEntry> rel) {
		try{
			entity=entigrator.getEntityAtKey(entityKey$);
			Core[] ca=entity.elementGet("jbookmark");
			if(ca!=null){
			String memberKey$;
			ArrayList<String>sl=new ArrayList<String>();
			sl.add(entityKey$);
			sl.add("_v6z8CVgemqMI6Bledpc7F1j0pVY");
			sl.add("_Tm142C8Sgti2iAKlDEcEXT2Kj1E");
			sl.add("_35a4Gr4U9MGmswmMRFtgK2erNo8");
			for(Core c:ca){
				try{
					memberKey$=Locator.getProperty(c.value, EntityHandler.ENTITY_KEY);
					if(memberKey$==null)
						continue;
					if(!sl.contains(memberKey$))
						sl.add(memberKey$);
				}catch(Exception ee){
					Logger.getLogger(getClass().getName()).info(ee.toString());
				}
			}
    		String [] na=NodeHandler.getNetwordNodeKeys(entigrator,sl.toArray(new String[0]));
			if(na!=null)
				for(String n:na)
					if(!sl.contains(n))
						sl.add(n);
			String[] sa=EdgeHandler.getEdgesKeys( entigrator, sl.toArray(new String[0]));
			String[] da;
			if(sa!=null)
				for(String s:sa){
					if(!sl.contains(s))
						sl.add(s);
					da=EdgeHandler.getDetailKeys(entigrator,s, na);
					if(da!=null)
						for(String d:da){
							if(!sl.contains(d))
								sl.add(d);
				}
			}
			for(String s:sl){
				try{
					JReferenceEntry.getReference(entigrator,s, rel);
			}catch(Exception ee){
					Logger.getLogger(getClass().getName()).info(ee.toString());
				}
			}
			}
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		}
	}
	@Override
	public String getEntityType() {
		return "graph";
	}
}
