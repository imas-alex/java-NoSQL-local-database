package gdt.jgui.entity;
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
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import gdt.data.entity.ArchiveHandler;
import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
import gdt.jgui.base.JBaseNavigator;
import gdt.jgui.base.ProgressDialog;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JItemPanel;
import gdt.jgui.console.JItemsListPanel;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.tool.JTextEditor;
/**
 * This class displays items to select the type
 * of an archive file.
 * @author imasa
 *
 */
public class JArchivePanel extends  JItemsListPanel implements JRequester{
	private static final long serialVersionUID = 1L;
	private Logger LOGGER=Logger.getLogger(getClass().getName());
	public static final String ARCHIVE_PANEL="Archive panel";
	public static final String ACTION_TAR="action tar";
	
	private static final int ARCHIVE_MODE_DAB_TAR=1;
	private static final int ARCHIVE_MODE_DAB_TGZ=3;
	private static final int ARCHIVE_MODE_DAB_ZIP=5;
	private static final int ARCHIVE_MODE_ENTITIES_TAR=2;
	private static final int ARCHIVE_MODE_ENTITIES_TGZ=4;
	private static final int ARCHIVE_MODE_ENTITIES_ZIP=6;
	String entihome$;
    String list$;
    String entityKey$;
    String entityLabel$;
    String archiveContent$;
    String archiveLocator$;
    Entigrator entigrator;
    ArchiveHandler archiveHandler;
    int archiveMode=0;
	/**
	 * The default constructor.
	 */
    public JArchivePanel() {
       super();
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
	    if(entihome$!=null){
	       locator.setProperty(Entigrator.ENTIHOME,entihome$);
	       locator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
			locator.setProperty(Locator.LOCATOR_ICON_CLASS,JEntitiesPanel.class.getName());
			locator.setProperty(Locator.LOCATOR_ICON_FILE,"archive.png");
	    }
	    if(list$!=null)
		       locator.setProperty(EntityHandler.ENTITY_LIST,list$);
	    if(entityKey$!=null)
		       locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
	    if(entityLabel$!=null)
		       locator.setProperty(EntityHandler.ENTITY_LABEL,entityLabel$);
	    if(archiveContent$!=null)
		       locator.setProperty(ArchiveHandler.ARCHIVE_CONTENT,archiveContent$);
	    locator.setProperty(Locator.LOCATOR_TITLE, getTitle());

	    locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
	    locator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
		if(list$!=null)
			locator.setProperty(EntityHandler.ENTITY_LIST, list$);
	    return Locator.toString(locator);
	}
	/**
	 * Create an instance of the archive panel.
	 * @param console the main console
	 * @param locator$ the locator string.
	 */
	@Override
	public JContext instantiate(JMainConsole console, String locator$) {
	    
		try{
	//		System.out.println("JArchivePanel:instantiate:locator="+locator$);
			 this.console=console;
			 Properties locator=Locator.toProperties(locator$);
			 list$=locator.getProperty(EntityHandler.ENTITY_LIST);
			 entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			 entihome$=locator.getProperty(Entigrator.ENTIHOME);
        	 entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
        	 archiveContent$=locator.getProperty(ArchiveHandler.ARCHIVE_CONTENT);
        	 if(entityLabel$==null&&entityKey$!=null){
        		 Entigrator entigrator=console.getEntigrator(entihome$);
        		 entityLabel$=entigrator.indx_getLabel(entityKey$);
        	 }
        	 ArrayList<JItemPanel>ipl=new ArrayList<JItemPanel>();
        	 String actionLocator$=getTarLocator();
        	 JItemPanel tarItem=new JItemPanel(console, actionLocator$);
   		  	 ipl.add(tarItem);
    		
   		  	 actionLocator$=getTgzLocator();
    		 JItemPanel tgzItem=new JItemPanel(console, actionLocator$);
		  	 ipl.add(tgzItem);
		  	 
   		  	 actionLocator$=getZipLocator();
    		 JItemPanel zipItem=new JItemPanel(console, actionLocator$);
		  	 ipl.add(zipItem);
		  	 putItems(ipl.toArray(new JItemPanel[0]));
        	return this;
        }catch(Exception e){
        
        LOGGER.severe(e.toString());
        }
        return null;
        }
private String getTarLocator() {
		try{
			Properties tarLocator=new Properties();
			tarLocator.setProperty(Locator.LOCATOR_TITLE,"Tar");
			  tarLocator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
				tarLocator.setProperty(Locator.LOCATOR_ICON_CLASS,JEntityPrimaryMenu.class.getName());
				tarLocator.setProperty(Locator.LOCATOR_ICON_FILE,"tar.png");
		
			tarLocator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
			tarLocator.setProperty(BaseHandler.HANDLER_METHOD,"response");
			tarLocator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
			tarLocator.setProperty(ArchiveHandler.ARCHIVE_TYPE,ArchiveHandler.ARCHIVE_TYPE_TAR);
			if(archiveContent$!=null)
				tarLocator.setProperty(ArchiveHandler.ARCHIVE_CONTENT,archiveContent$);
			if(entihome$!=null){
				
				tarLocator.setProperty(Entigrator.ENTIHOME,entihome$);
			
				
			}
			if(entityKey$!=null)
				tarLocator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
			if(entityLabel$!=null)
				tarLocator.setProperty(EntityHandler.ENTITY_LABEL,entityLabel$);
			if(list$!=null)
				tarLocator.setProperty(EntityHandler.ENTITY_LIST,list$);
			String tarLocator$=Locator.toString(tarLocator);
			JTextEditor textEditor=new JTextEditor();
			String editorLocator$=textEditor.getLocator();
			Properties editorLocator=Locator.toProperties(editorLocator$);
			editorLocator.setProperty(Entigrator.ENTIHOME, entihome$);
			if(ArchiveHandler.ARCHIVE_CONTENT_DATABASE.equals(archiveContent$)){
			   Entigrator entigrator=console.getEntigrator(entihome$);
			   String database$=entigrator.getBaseName();
			   editorLocator.setProperty(JTextEditor.TEXT, database$+".tar");
			}else{
			  if(entityKey$!=null)
				editorLocator.setProperty(JTextEditor.TEXT, entityKey$+".tar\n"+entityLabel$+".tar");
			  else
				  editorLocator.setProperty(JTextEditor.TEXT,"archive.tar");  
     		}
			editorLocator.setProperty(JRequester.REQUESTER_RESPONSE_LOCATOR, Locator.compressText(tarLocator$));
			editorLocator.setProperty(Locator.LOCATOR_TITLE,"Tar");
			//editorLocator.setProperty(Locator.LOCATOR_ICON,icon$);
			editorLocator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
			editorLocator.setProperty(Locator.LOCATOR_ICON_CLASS,JEntityPrimaryMenu.class.getName());
			editorLocator.setProperty(Locator.LOCATOR_ICON_FILE,"tar.png");
			String teLocator$=Locator.toString(editorLocator);
			return teLocator$;
			}catch(Exception ee){
				LOGGER.severe(ee.toString());
				return null;
			}
	}	
	private String getTgzLocator() {
		try{
			Properties tgzLocator=new Properties();
			tgzLocator.setProperty(Locator.LOCATOR_TITLE,"Tgz");
			tgzLocator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
			tgzLocator.setProperty(Locator.LOCATOR_ICON_CLASS,JEntityPrimaryMenu.class.getName());
			tgzLocator.setProperty(Locator.LOCATOR_ICON_FILE,"tgz.png");
	
			//tgzLocator.setProperty(Locator.LOCATOR_ICON,icon$);
			tgzLocator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
			tgzLocator.setProperty(BaseHandler.HANDLER_METHOD,"response");
			tgzLocator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
			tgzLocator.setProperty(ArchiveHandler.ARCHIVE_TYPE,ArchiveHandler.ARCHIVE_TYPE_TGZ);
			if(entihome$!=null)
				tgzLocator.setProperty(Entigrator.ENTIHOME,entihome$);
			if(entityKey$!=null)
				tgzLocator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
			if(entityLabel$!=null)
				tgzLocator.setProperty(EntityHandler.ENTITY_LABEL,entityLabel$);
			if(archiveContent$!=null)
				tgzLocator.setProperty(ArchiveHandler.ARCHIVE_CONTENT,archiveContent$);
			if(list$!=null)
				tgzLocator.setProperty(EntityHandler.ENTITY_LIST,list$);
			String tgzLocator$=Locator.toString(tgzLocator);
			JTextEditor textEditor=new JTextEditor();
			String editorLocator$=textEditor.getLocator();
			Properties editorLocator=Locator.toProperties(editorLocator$);
			editorLocator.setProperty(Entigrator.ENTIHOME, entihome$);
			if(ArchiveHandler.ARCHIVE_CONTENT_DATABASE.equals(archiveContent$)){
			   Entigrator entigrator=console.getEntigrator(entihome$);
			   String database$=entigrator.getBaseName();
			   editorLocator.setProperty(JTextEditor.TEXT, database$+".tgz");
			}else{
				  if(entityKey$!=null)
						editorLocator.setProperty(JTextEditor.TEXT, entityKey$+".tgz\n"+entityLabel$+".tgz");
					  else
						  editorLocator.setProperty(JTextEditor.TEXT,"archive.tgz");  
     		}
			editorLocator.setProperty(JRequester.REQUESTER_RESPONSE_LOCATOR, Locator.compressText(tgzLocator$));
			editorLocator.setProperty(Locator.LOCATOR_TITLE,"Tgz");
			editorLocator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
			editorLocator.setProperty(Locator.LOCATOR_ICON_CLASS,JEntityPrimaryMenu.class.getName());
			editorLocator.setProperty(Locator.LOCATOR_ICON_FILE,"tgz.png");
			String teLocator$=Locator.toString(editorLocator);
			return teLocator$;
			}catch(Exception ee){
				LOGGER.severe(ee.toString());
				return null;
			}
	}	
	private String getZipLocator() {
		try{
			Properties zipLocator=new Properties();
			zipLocator.setProperty(Locator.LOCATOR_TITLE,"Zip");
			zipLocator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
			zipLocator.setProperty(Locator.LOCATOR_ICON_CLASS,JEntityPrimaryMenu.class.getName());
			zipLocator.setProperty(Locator.LOCATOR_ICON_FILE,"zip.png");
	
			zipLocator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
			zipLocator.setProperty(BaseHandler.HANDLER_METHOD,"response");
			zipLocator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
			zipLocator.setProperty(ArchiveHandler.ARCHIVE_TYPE,ArchiveHandler.ARCHIVE_TYPE_ZIP);
			
			if(entihome$!=null)
				zipLocator.setProperty(Entigrator.ENTIHOME,entihome$);
			if(entityKey$!=null)
				zipLocator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
			if(entityLabel$!=null)
				zipLocator.setProperty(EntityHandler.ENTITY_LABEL,entityLabel$);
			if(archiveContent$!=null)
				zipLocator.setProperty(ArchiveHandler.ARCHIVE_CONTENT,archiveContent$);
			if(list$!=null)
				zipLocator.setProperty(EntityHandler.ENTITY_LIST,list$);
			String zipLocator$=Locator.toString(zipLocator);
			JTextEditor textEditor=new JTextEditor();
			String editorLocator$=textEditor.getLocator();
			Properties editorLocator=Locator.toProperties(editorLocator$);
			editorLocator.setProperty(Entigrator.ENTIHOME, entihome$);
			if(ArchiveHandler.ARCHIVE_CONTENT_DATABASE.equals(archiveContent$)){
			   Entigrator entigrator=console.getEntigrator(entihome$);
			   String database$=entigrator.getBaseName();
			   editorLocator.setProperty(JTextEditor.TEXT, database$+".zip");
			}else{
				  if(entityKey$!=null)
						editorLocator.setProperty(JTextEditor.TEXT, entityKey$+".zip\n"+entityLabel$+".zip");
					  else
						  editorLocator.setProperty(JTextEditor.TEXT,"archive.zip");  
     		}
			editorLocator.setProperty(JRequester.REQUESTER_RESPONSE_LOCATOR, Locator.compressText(zipLocator$));
			editorLocator.setProperty(Locator.LOCATOR_TITLE,"Zip");
			editorLocator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
			editorLocator.setProperty(Locator.LOCATOR_ICON_CLASS,JEntityPrimaryMenu.class.getName());
			editorLocator.setProperty(Locator.LOCATOR_ICON_FILE,"zip.png");
	
			String teLocator$=Locator.toString(editorLocator);
			return teLocator$;
			}catch(Exception ee){
				LOGGER.severe(ee.toString());
				return null;
			}
	}	
	/**
	 * Get the context title.
	 * @return the context title.
	 * 
	 */
	@Override
	public String getTitle() {
		return "Archive";
	}
	/**
	 * Get the context subtitle.
	 * @return the context subtitle.
	 * 
	 */
	@Override
	public String getSubtitle() {
		return entityLabel$;
	}
	/**
	 * Get the context type.
	 * @return the context type.
	 * 
	 */
	@Override
	public String getType() {
		return "Archive";
	}
	/**
	 * Complete the context. No action.
	 */
	@Override
	public void close() {
		// TODO Auto-generated method stub
	}
	private String addComponents(String entitiesList$,Entigrator entigrator){
		try{
			ArrayList <String>sl=new ArrayList<String>();
			String[] sa=Locator.toArray(entitiesList$);
			Sack entity;
			Core[] ca; 
			for(String s:sa){
				if(!sl.contains(s))
					sl.add(s);
				entity=entigrator.getEntityAtKey(s);
				if(entity!=null){
					ca=entity.elementGet("component");
					if(ca!=null)
					   for(Core c:ca){
						  if(c.value!=null)
							  if(!sl.contains(c.value))
								  sl.add(c.value);
					}
				}
			}
			sa=sl.toArray(new String[0]);
			return Locator.toString(sa);
		}catch(Exception e){
			Logger.getLogger(JArchivePanel.class.getName()).severe(e.toString());
		}
		return entitiesList$;
	}
	/**
	 * Execute the response action.
	 * @param console the main console.
	 * @param locator$ the response locator.
	 */
	@Override
	public void response(JMainConsole console, String locator$) {
//	System.out.println("JArchivePanel:response:locator="+locator$);
	Properties locator=Locator.toProperties(locator$);
	String text$=locator.getProperty(JTextEditor.TEXT);
    archiveContent$=locator.getProperty(ArchiveHandler.ARCHIVE_CONTENT);
	String[] sa=text$.split("\n");	
	String fileName$=sa[0];	
	JFileChooser chooser = new JFileChooser(); 
    chooser.setCurrentDirectory(new java.io.File(System.getProperty("user.home")));
    chooser.setDialogTitle(fileName$);
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    chooser.setAcceptAllFileFilterUsed(false);
    if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
    	String entihome$=locator.getProperty(Entigrator.ENTIHOME);
    	entigrator=console.getEntigrator(entihome$);
        String file$=chooser.getSelectedFile().getPath()+"/"+fileName$;	
//    	System.out.println("ArchivePanel:response:archive to "+file$);
    	String archiveType$=locator.getProperty(ArchiveHandler.ARCHIVE_TYPE);
    	archiveHandler=new ArchiveHandler();
		archiveLocator$=archiveHandler.getLocator();
		Properties archiveLocator=Locator.toProperties(archiveLocator$);
		archiveLocator.setProperty(ArchiveHandler.ARCHIVE_FILE, file$);
		String entityList$=locator.getProperty(EntityHandler.ENTITY_LIST);
	//	System.out.println("ArchivePanel:response:entities list:"+entityList$);
		  if(!ArchiveHandler.ARCHIVE_CONTENT_DATABASE.equals(archiveContent$)){
		if(entityList$!=null){
			entityList$=addComponents(entityList$, entigrator);
			archiveLocator.setProperty(EntityHandler.ENTITY_LIST,entityList$);
		}
		else{
			String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			String[]ea=JReferenceEntry.getCoalition(console, entigrator,new String[]{entityKey$} );
			//String[]ea=new String[]{entityKey$};
			entityList$=Locator.toString(ea);
			archiveLocator.setProperty(EntityHandler.ENTITY_LIST,entityList$);
		}
		  }
		if(ArchiveHandler.ARCHIVE_TYPE_TAR.equals(archiveType$)){
//    		System.out.println("ArchivePanel:response:archive tar:locator="+locator$);
    		archiveLocator.setProperty(ArchiveHandler.ARCHIVE_TYPE, ArchiveHandler.ARCHIVE_TYPE_TAR);
    		archiveLocator$=Locator.toString(archiveLocator); 
    	   if(ArchiveHandler.ARCHIVE_CONTENT_DATABASE.equals(archiveContent$)){
    		   //archiveHandler.compressDatabaseToTar(entigrator, archiveLocator$);
    		   archiveMode=ARCHIVE_MODE_DAB_TAR;   
    	   }else{
    		 //archiveHandler.compressEntitiesToTar(entigrator, archiveLocator$);
    		   archiveMode=ARCHIVE_MODE_ENTITIES_TAR;
    	   }
    	}
    	if(ArchiveHandler.ARCHIVE_TYPE_TGZ.equals(archiveType$)){
  // 		System.out.println("ArchivePanel:response:archive tgz:locator="+locator$);
    		archiveLocator.setProperty(ArchiveHandler.ARCHIVE_TYPE, ArchiveHandler.ARCHIVE_TYPE_TGZ);
    		archiveLocator$=Locator.toString(archiveLocator); 
    		if(ArchiveHandler.ARCHIVE_CONTENT_DATABASE.equals(archiveContent$)){
     		   //archiveHandler.compressDatabaseToTgz(entigrator, archiveLocator$);
     		  archiveMode=ARCHIVE_MODE_DAB_TGZ;   
    		}
     	   	else{
    		   //archiveHandler.compressEntitiesToTgz(entigrator, archiveLocator$);
     	   	archiveMode=ARCHIVE_MODE_ENTITIES_TGZ;
     	   	}
    	}
    	if(ArchiveHandler.ARCHIVE_TYPE_ZIP.equals(archiveType$)){
    	//	System.out.println("ArchivePanel:response:archive zip:locator="+locator$);
    		archiveLocator.setProperty(ArchiveHandler.ARCHIVE_TYPE, ArchiveHandler.ARCHIVE_TYPE_ZIP);
    		archiveLocator$=Locator.toString(archiveLocator); 
    		if(ArchiveHandler.ARCHIVE_CONTENT_DATABASE.equals(archiveContent$)){
      		   //archiveHandler.compressDatabaseToZip(entigrator, archiveLocator$);
    			archiveMode=ARCHIVE_MODE_DAB_ZIP;
    		}
      	   	else{
    		   //archiveHandler.compressEntitiesToZip(entigrator, archiveLocator$);
      	   	archiveMode=ARCHIVE_MODE_ENTITIES_ZIP;
      	   	}
    		
    	}
    	ProgressDialog pd=new ProgressDialog(console.getFrame(),Export,"Export");
		pd.setLocationRelativeTo(JArchivePanel.this);
		pd.setVisible(true);
  }
    else {
    	Logger.getLogger(JMainConsole.class.getName()).info(" no selection");
      }
    console.back();
	}
Runnable Export =new Runnable(){
	public void run(){
		switch(archiveMode){
		case ARCHIVE_MODE_DAB_TAR:{
			archiveHandler.compressDatabaseToTar(entigrator, archiveLocator$);
			return;
		}
		case ARCHIVE_MODE_ENTITIES_TAR:{
			archiveHandler.compressEntitiesToTar(entigrator, archiveLocator$);
			return;
		}
		case ARCHIVE_MODE_DAB_TGZ:{
			archiveHandler.compressDatabaseToTgz(entigrator, archiveLocator$);
			return;
		}
		case ARCHIVE_MODE_ENTITIES_TGZ:{
			archiveHandler.compressEntitiesToTgz(entigrator, archiveLocator$);
			return;
		}
		case ARCHIVE_MODE_DAB_ZIP:{
			archiveHandler.compressDatabaseToZip(entigrator, archiveLocator$);
			return;
		}
		case ARCHIVE_MODE_ENTITIES_ZIP:
			archiveHandler.compressEntitiesToZip(entigrator, archiveLocator$);	
		}
	}
};
	@Override
	public void activate() {
		// TODO Auto-generated method stub
		
	}
	
	
}
