package gdt.jgui.entity.phone;
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

import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.PhoneHandler;
import gdt.data.entity.facet.ExtensionHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Identity;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
import gdt.jgui.console.*;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.entity.JReferenceEntry;
import gdt.jgui.entity.address.JAddressFacetOpenItem;
import gdt.jgui.tool.JTextEditor;
public class JPhoneEditor extends JTextEditor implements JFacetRenderer,JRequester{
String entityKey$;
String entityLabel$;
String entihome$;
	private static final long serialVersionUID = 1L;
	public static final String ACTION_CREATE_PHONE="action create phone";
	public static final String ACTION_EDIT_PHONE="action edit phone";
	@Override
	public String getLocator() {
		String locator$=super.getLocator();
		if(entityKey$!=null)
			locator$=Locator.append(locator$, EntityHandler.ENTITY_KEY, entityKey$);
		if(entityLabel$!=null){
			locator$=Locator.append(locator$, EntityHandler.ENTITY_LABEL, entityLabel$);
			locator$=Locator.append(locator$, "subtitle", entityLabel$);
		}
		if(entihome$!=null)
			locator$=Locator.append(locator$, Entigrator.ENTIHOME, entihome$);
		if(text$!=null)
			locator$=Locator.append(locator$, JTextEditor.TEXT, text$);
		locator$=Locator.append(locator$, BaseHandler.HANDLER_CLASS, getClass().getName());
		locator$=Locator.append(locator$, BaseHandler.HANDLER_METHOD, "instantiate");
		
		//System.out.println("JPhoneEditor.getLocator:locator="+locator$);
				return locator$;
	}

	@Override
	public JContext instantiate(JMainConsole console, String locator$) {
			try{
				//System.out.println("JMovieEditor.instantiate:begin");
					this.console=console;
					Properties locator=Locator.toProperties(locator$);
					entihome$=locator.getProperty(Entigrator.ENTIHOME);
					
					entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
					if(entityKey$!=null)
						return super.instantiate(console, locator$);
					else
						return this;
				}catch(Exception e){
					Logger.getLogger(getClass().getName()).severe(e.toString());
				}
				return this;
		}
	
	

	@Override
	public String getTitle() {
		
		return "Phone";
	}

	@Override
	public String getSubtitle() {
		return entityLabel$;	
	}

	@Override
	public String getType() {
			return "phone";
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String addIconToLocator(String locator$) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFacetHandler() {
		return PhoneHandler.class.getName();
	}

	@Override
	public String getEntityType() {
		return "phone";
	}

	@Override
	public String getCategoryIcon(Entigrator entigrator) {
		   return ExtensionHandler.loadIcon(entigrator,PhoneHandler.EXTENSION_KEY, "phone.png");
	}
	
	@Override
	public String getCategoryTitle() {
		return "Phones";
	}

	@Override
	public void adaptClone(JMainConsole console, String locator$) {
	}

	@Override
	public void adaptRename(JMainConsole console, String locator$) {
	}

	@Override
	public void reindex(JMainConsole console, Entigrator entigrator, Sack entity) {
		 try{
			// System.out.println("JPhoneEditor:reindex:0:entity="+entity.getProperty("label"));
		    	String fhandler$=PhoneHandler.class.getName();
		    	if(entity.getElementItem("fhandler", fhandler$)!=null){
					//System.out.println("JPhoneEditor:reindex:1:entity="+entity.getProperty("label"));
		    		entity.putElementItem("jfacet", new Core(JPhoneFacetAddItem.class.getName(),fhandler$,JPhoneFacetOpenItem.class.getName()));
					entity.putElementItem("fhandler", new Core(null,fhandler$,PhoneHandler.EXTENSION_KEY));
					entigrator.save(entity);
				}
		    }catch(Exception e){
		    	Logger.getLogger(getClass().getName()).severe(e.toString());
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
		    teLocator$=Locator.append(teLocator$, JTextEditor.TEXT_TITLE,"Title of phone");
		    String text$="NewPhone"+Identity.key().substring(0, 4);
		    teLocator$=Locator.append(teLocator$, JTextEditor.TEXT,text$);
		    JPhoneEditor fp=new JPhoneEditor();
		    String fpLocator$=fp.getLocator();
		    fpLocator$=Locator.append(fpLocator$, Entigrator.ENTIHOME,entihome$);
		    fpLocator$=Locator.append(fpLocator$, EntityHandler.ENTITY_KEY,entityKey$);
		    fpLocator$=Locator.append(fpLocator$, BaseHandler.HANDLER_METHOD,"response");
		    fpLocator$=Locator.append(fpLocator$, JRequester.REQUESTER_ACTION,ACTION_CREATE_PHONE);
		    String requesterResponseLocator$=Locator.compressText(fpLocator$);
		    teLocator$=Locator.append(teLocator$,JRequester.REQUESTER_RESPONSE_LOCATOR,requesterResponseLocator$);
		    teLocator$=Locator.append(teLocator$,Entigrator.ENTIHOME,entihome$);
		    JConsoleHandler.execute(console, teLocator$);
		}catch(Exception ee){   
			Logger.getLogger(getClass().getName()).severe(ee.toString());
			
		}
		return null;
	}

	@Override
	public void response(JMainConsole console, String locator$) {
		//System.out.println("JPhoneEditor:response:locator="+locator$);
		Properties locator=Locator.toProperties(locator$);
		String action$=locator.getProperty(JRequester.REQUESTER_ACTION);
		
		if(ACTION_CREATE_PHONE.equals(action$)){
			   String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			   String text$=locator.getProperty(JTextEditor.TEXT);
			   Entigrator entigrator=console.getEntigrator(entihome$);
			   Sack phone=entigrator.ent_new("phone", text$);
			   phone=entigrator.ent_assignProperty(phone, "phone", "123456");
			   phone.putAttribute(new Core(null,"icon","phone.png"));
			   phone.createElement("jfacet");
			   phone.putElementItem("jfacet", new Core("gdt.jgui.entity.phone.JPhoneFacetAddItem","gdt.data.entity.PhoneHandler","gdt.jgui.entity.phone.JPhoneFacetOpenItem"));
			   phone.createElement("fhandler");
			   phone.putElementItem("fhandler", new Core(null,"gdt.data.entity.PhoneHandler",PhoneHandler.EXTENSION_KEY));
			   entigrator.replace(phone);
			   entigrator.saveHandlerIcon(JPhoneEditor.class, "phone.png");
			   entityKey$=phone.getKey();
			   JTextEditor textEditor=new JTextEditor();
			    String teLocator$=textEditor.getLocator();
			    teLocator$=Locator.append(teLocator$, Entigrator.ENTIHOME,entihome$);
			    teLocator$=Locator.append(teLocator$, JTextEditor.TEXT_TITLE,"Phone number");
			    text$="123";
			    teLocator$=Locator.append(teLocator$, JTextEditor.TEXT,text$);
			    JPhoneEditor pe=new JPhoneEditor();
			    String peLocator$=pe.getLocator();
			    peLocator$=Locator.append(peLocator$, Entigrator.ENTIHOME,entihome$);
			    peLocator$=Locator.append(peLocator$, EntityHandler.ENTITY_KEY,entityKey$);
			    peLocator$=Locator.append(peLocator$, BaseHandler.HANDLER_METHOD,"response");
			    peLocator$=Locator.append(peLocator$, JRequester.REQUESTER_ACTION,ACTION_EDIT_PHONE);
			    String requesterResponseLocator$=Locator.compressText(peLocator$);
			    teLocator$=Locator.append(teLocator$,JRequester.REQUESTER_RESPONSE_LOCATOR,requesterResponseLocator$);
			    JConsoleHandler.execute(console, teLocator$);
			   /*
			   JPhoneEditor pe=new JPhoneEditor();
			   String peLocator$=pe.getLocator();
			   peLocator$=Locator.append(peLocator$, Entigrator.ENTIHOME, entihome$);
			   peLocator$=Locator.append(peLocator$, EntityHandler.ENTITY_KEY, entityKey$);
			   JEntityPrimaryMenu.reindexEntity(console, peLocator$);
			   Stack<String> s=console.getTrack();
			   s.pop();
			   console.setTrack(s);
			   JConsoleHandler.execute(console, peLocator$);
			   */
			}
		if(ACTION_EDIT_PHONE.equals(action$)){
			   entihome$=locator.getProperty(Entigrator.ENTIHOME);
			   entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			   String text$=locator.getProperty(JTextEditor.TEXT);
			   Entigrator entigrator=console.getEntigrator(entihome$);
			   Sack phone=entigrator.ent_getAtKey(entityKey$);
			   phone=entigrator.ent_assignProperty(phone, "phone", text$);
			   entigrator.replace(phone);
			   JEntityFacetPanel efp=new JEntityFacetPanel();
			   String efpLocator$=efp.getLocator();
			   efpLocator$=Locator.append(efpLocator$, Entigrator.ENTIHOME, entihome$);
			   efpLocator$=Locator.append(efpLocator$, EntityHandler.ENTITY_KEY, entityKey$);
			   JConsoleHandler.execute(console, efpLocator$);
			  	}
		
	}

	@Override
	public void collectReferences(Entigrator entigrator, String entityKey$, ArrayList<JReferenceEntry> rel)
	{

	}
	@Override
	public String getFacetOpenItem() {
		// TODO Auto-generated method stub
		return JPhoneFacetOpenItem.class.getName();
	}
	@Override
	public String getFacetIcon() {
		
		return "phone.png";
	}
}
