package gdt.jgui.entity.folder;

import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;

import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.FacetHandler;
import gdt.data.entity.facet.FolderHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Identity;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JFacetAddItem;
import gdt.jgui.console.JFacetOpenItem;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.entity.JEntitiesPanel;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.entity.fields.JFieldsFacetOpenItem;
import gdt.jgui.tool.JTextEditor;
/**
 * This class represents the folder facet in the list
 * of available facets to add.
 * @author imasa
 *
 */
public class JFolderFacetAddItem extends JFacetAddItem{
	private static final long serialVersionUID = 1L;
	 private Logger LOGGER=Logger.getLogger(JFolderFacetAddItem.class.getName());
	    String entityLabel$;
/**
 * The default constructor.
 */
	    public JFolderFacetAddItem(){
			super();
		}
	    /**
		 * Get the add facet item locator.
		 * @return the locator string.
		 */	    
	    @Override
	    public String getLocator(){
	    	Properties locator=new Properties();
	    	locator.setProperty(Locator.LOCATOR_TITLE,"Folder");
	    	locator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
	    	locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
	    	locator.setProperty(BaseHandler.HANDLER_METHOD,METHOD_ADD_COMPONENT);
	    	locator.setProperty( JContext.CONTEXT_TYPE,"Folder add ");
	    	locator.setProperty(Locator.LOCATOR_TITLE,"Folder");
	    	locator.setProperty(JFacetOpenItem.FACET_HANDLER_CLASS,FolderHandler.class.getName());
	    	if(entityKey$!=null)
	    		locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
	    	if(entihome$!=null)
	    		locator.setProperty(Entigrator.ENTIHOME,entihome$);
	    	 icon$=Support.readHandlerIcon(getClass(), "folder.png");
	    	if(icon$!=null)
	    	    	locator.setProperty(Locator.LOCATOR_ICON,icon$);
	    	 locator$=Locator.toString(locator);
	    	locator.setProperty(Locator.LOCATOR_CHECKABLE,Locator.LOCATOR_TRUE);
	    	 return Locator.toString(locator);
	    }	  
	    /**
		 * Execute the response locator.
		 * @param console the main console.
		 * @param locator$ the locator string.
		 */
	    @Override
	public void response(JMainConsole console, String locator$) {
		try{
			Properties locator=Locator.toProperties(locator$);
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			String mode$=locator.getProperty(JFacetAddItem.ADD_MODE);
			if(JFacetAddItem.ADD_MODE_COMPONENT.equals(mode$)){
				String componentLabel$=locator.getProperty(JTextEditor.TEXT);
				Entigrator entigrator=console.getEntigrator(entihome$);
				Sack component=entigrator.ent_new("folder", componentLabel$);
				component=entigrator.ent_assignProperty(component, "phone", "123456");
				component.createElement("fhandler");
				component.putElementItem("fhandler", new Core(null,FolderHandler.class.getName(),null));
				component.createElement("jfacet");
				component.putElementItem("jfacet", new Core(getClass().getName(),FolderHandler.class.getName(),JFolderFacetOpenItem.class.getName()));
			    File folder=new File(entigrator.getEntihome()+"/"+component.getKey());
			    folder.mkdir();
				entigrator.save(component);
				entigrator.saveHandlerIcon(getClass(), "folder.png");
				Sack container=entigrator.getEntityAtKey(entityKey$);
				entigrator.col_addComponent(container, component);
				JEntityFacetPanel efp=new JEntityFacetPanel();
				String efpLocator$=efp.getLocator();
				efpLocator$=Locator.append(efpLocator$,Entigrator.ENTIHOME,entihome$);
				efpLocator$=Locator.append(efpLocator$,EntityHandler.ENTITY_KEY,component.getKey());
			    JConsoleHandler.execute(console, efpLocator$);	
			}
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
		
	}
		/**
		 * Add facet to the entity.
		 * @param console the main console.
		 * @param locator$ the locator string.
		 */
	@Override
	public void addFacet(JMainConsole console, String locator$) {
		try{
			//   System.out.println("JFieldsFacetAddItem:addFacet:locator:"+locator$);
			   Properties locator=Locator.toProperties(locator$);
			   String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			   String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			   Entigrator entigrator=console.getEntigrator(entihome$);
			   Sack entity=entigrator.getEntityAtKey(entityKey$);
			   if(!entity.existsElement("fhandler"))
				   entity.createElement("fhandler");
			   entity.putElementItem("fhandler", new Core(null,FolderHandler.class.getName(),null)); 
			   if(!entity.existsElement("jfacet"))
				   entity.createElement("jfacet");
			   entity.putElementItem("jfacet", new Core(JFolderFacetAddItem.class.getName(),FolderHandler.class.getName(),JFolderFacetOpenItem.class.getName()));
			   entigrator.save(entity);
			   entity=entigrator.ent_assignProperty(entity, "folder", entity.getProperty("label"));
			}catch(Exception e){
				  LOGGER.severe(e.toString());
			  }
		
	}
	/**
	 * Create an entity of the facet type 
	 * and add it as a component to the entity.
	 * @param console the main console.
	 * @param locator$ the locator string.
	 */
	@Override
	public void addComponent(JMainConsole console, String locator$) {
		try{
			Properties locator=Locator.toProperties(locator$);
		    String entihome$=locator.getProperty(Entigrator.ENTIHOME);
		    String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		    Entigrator entigrator=console.getEntigrator(entihome$);
		    String label$=entigrator.indx_getLabel(entityKey$);
		    
			JTextEditor textEditor=new JTextEditor();
		    String editorLocator$=textEditor.getLocator();
		   // editorLocator$=Locator.append(editorLocator$, Requester.REQUESTER_CLASS, getClass().getName());
		    editorLocator$=Locator.append(editorLocator$, JTextEditor.TEXT, label$+".folder."+Identity.key().substring(0,4));
		   // editorLocator$=Locator.append(editorLocator$,Locator.LOCATOR_DATA,Locator.compressText(locator$));
		    editorLocator$=Locator.append(editorLocator$,Locator.LOCATOR_TITLE,"Component label");
		    editorLocator$=Locator.append(editorLocator$,JTextEditor.TEXT_TITLE,"Add folder component");
		 //   String icon$=Support.readHandlerIcon(EntitiesPanel.class, "folder.png");
		 //   editorLocator$=Locator.append(editorLocator$,Locator.LOCATOR_ICON,icon$);
		    String responseLocator$=getLocator();
		    responseLocator$=Locator.append(responseLocator$, BaseHandler.HANDLER_METHOD, "response");
		    responseLocator$=Locator.append(responseLocator$, Entigrator.ENTIHOME, entihome$);
		    responseLocator$=Locator.append(responseLocator$, EntityHandler.ENTITY_KEY, entityKey$);
		    responseLocator$=Locator.append(responseLocator$, ADD_MODE, ADD_MODE_COMPONENT);
		    String requesterResponseLocator$=Locator.compressText(responseLocator$);
		    editorLocator$=Locator.append(editorLocator$, JRequester.REQUESTER_RESPONSE_LOCATOR, requesterResponseLocator$);
		    JConsoleHandler.execute(console, editorLocator$);
			
		}catch(Exception e){
			  LOGGER.severe(e.toString());
		  }
		
	}
	/**
	 * Get facet handler instance.
	 * @return the facet handler instance.
	 * 
	 */
	@Override
	public FacetHandler getFacetHandler() {
		return new FolderHandler();
	}

	/**
	 * Check if the facet is already assigned and set the
	 * corresponding tag in the locator.
	 * @param console the main console
	 * @param locator$ the origin locator string. 
	 * @return the locator string.
	 * 
	 */
	@Override
	public String markAppliedUncheckable(JMainConsole console, String locator$) {
		try{
			//System.out.println("JFieldsFacetAddItem: markAppliedUncheckable:locator="+locator$);
			Properties locator=Locator.toProperties(locator$);	
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			Entigrator entigrator=console.getEntigrator(entihome$);
			Sack entity=entigrator.getEntityAtKey(entityKey$);
			if(entity==null)
				return null;
			boolean isApplied=false;
			if(entity.getProperty("folder")!=null){
				Core fhandler=entity.getElementItem("fhandler",FolderHandler.class.getName());
				if(fhandler!=null){
						Core jfacet=entity.getElementItem("jfacet", FolderHandler.class.getName());
						if(jfacet!=null){
							if( JFolderFacetOpenItem.class.getName().equals(jfacet.value)
									&& JFolderFacetAddItem.class.getName().equals(jfacet.type)){
								isApplied=true;	
							}
						}
				}
			}
			if(isApplied)
				locator$=Locator.append(locator$, Locator.LOCATOR_CHECKABLE, Locator.LOCATOR_FALSE);
			else
				locator$=Locator.append(locator$, Locator.LOCATOR_CHECKABLE, Locator.LOCATOR_TRUE);
			this.locator$=locator$;
			return locator$;
			}catch(Exception e){
			LOGGER.info(e.toString());
			}	
			this.locator$=locator$;
			return locator$;
	}
	@Override
	public String getIconResource() {
		return "folder.png";
	}
	@Override
	public String getFacetOpenClass() {
		return JFolderFacetOpenItem.class.getName();
	}
	@Override
	public String getFacetAddClass() {
		return JFolderFacetAddItem.class.getName();
	}
}
