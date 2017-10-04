package gdt.jgui.entity.users;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.logging.Logger;

import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.UsersHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JItemPanel;
import gdt.jgui.console.JItemsListPanel;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JItemsListPanel.ItemPanelComparator;
import gdt.jgui.entity.group.JGroupEditor;

public class JGroupsList extends JItemsListPanel{
	String entihome$;
	
	boolean debug=false;
	public JGroupsList() {
		super();
	}
	@Override
	public String getLocator() {
		Properties locator=new Properties();
	    locator.setProperty(Locator.LOCATOR_TYPE, JContext.CONTEXT_TYPE);
	    locator.setProperty(JContext.CONTEXT_TYPE,getType());
	    if(entihome$!=null)
	    	locator.setProperty(Entigrator.ENTIHOME,entihome$);
	    locator.setProperty(Locator.LOCATOR_TITLE, "Groups");
	    locator.setProperty( Locator.LOCATOR_ICON_CONTAINER, Locator.LOCATOR_ICON_CONTAINER_CLASS);
     	locator.setProperty( Locator.LOCATOR_ICON_CLASS, getClass().getName());
    	locator.setProperty( Locator.LOCATOR_ICON_FILE, "group.png");
   	    locator.setProperty( Locator.LOCATOR_ICON_LOCATION, UsersHandler.EXTENSION_KEY);
   	    locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
	    locator.setProperty(BaseHandler.HANDLER_CLASS,JGroupsList.class.getName());
	  
   	    return Locator.toString(locator);
	}

	@Override
	public String getTitle() {
		
		return "Groups";
	}

	@Override
	public String getSubtitle() {
		// TODO Auto-generated method stub
		return entihome$;
	}

	@Override
	public String getType() {
		return "group";
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void activate() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public JContext instantiate(JMainConsole console, String locator$) {
		try{
		this.console=console;
		if(debug)
		System.out.println("JGroupsList:instantiate:locator="+locator$);
		Properties locator=Locator.toProperties(locator$);
		entihome$=locator.getProperty(Entigrator.ENTIHOME);
		Entigrator entigrator=console.getEntigrator(entihome$);
		String[] sa=entigrator.indx_listEntities("entity", "group");
		JGroupEditor ge=new JGroupEditor();
		String geLocator$=ge.getLocator();
		geLocator$=Locator.append(geLocator$, Entigrator.ENTIHOME,entihome$ );
		JItemPanel ip;
		if(sa!=null){
			ArrayList<JItemPanel>gl=new ArrayList<JItemPanel>();
			for(String s:sa){
				geLocator$=Locator.append(geLocator$, EntityHandler.ENTITY_KEY, s);
				geLocator$=Locator.append(geLocator$, Locator.LOCATOR_TITLE, entigrator.indx_getLabel(s));
				ip=new JItemPanel(console,geLocator$);
				gl.add(ip);
			}
			 Collections.sort(gl,new ItemPanelComparator());
			 putItems(gl.toArray(new JItemPanel[0]));
		}
			//
		}catch(Exception e){
			Logger.getLogger(JUserEditor.class.getName()).severe(e.toString());
		    console.back();
		}
	   return this;
	}
}
