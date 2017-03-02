package gdt.data.entity;
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

import java.util.Properties;
import java.util.logging.Logger;

import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
import gdt.data.entity.FacetHandler;
public class ContactHandler extends FacetHandler{
	public static final String EXTENSION_KEY="_v6z8CVgemqMI6Bledpc7F1j0pVY";	
private Logger LOGGER=Logger.getLogger(ContactHandler.class.getName());
String entihome$;
String entityKey$;
boolean debug=false;
public final static String CONTACT="contact";

	@Override
	public String instantiate( String locator$) {
		try{
	     	Properties locator=Locator.toProperties(locator$);
		entihome$=locator.getProperty(Entigrator.ENTIHOME);
		entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		if(entityKey$!=null)
		   locator$=Locator.append(locator$,EntityHandler.ENTITY_KEY,entityKey$);
		if(entihome$!=null)
			locator$=Locator.append(locator$,Entigrator.ENTIHOME,entihome$);
        locator$=Locator.append(locator$,BaseHandler.HANDLER_CLASS,ContactHandler.class.getName());
        locator$=Locator.append(locator$,BaseHandler.HANDLER_SCOPE,BaseHandler.BASE_SCOPE);
        locator$=Locator.append(locator$,Locator.LOCATOR_TITLE,CONTACT);
        return locator$;
		}catch(Exception e){
        LOGGER.severe(e.toString());
		locator$=Locator.append(locator$, METHOD_STATUS, METHOD_STATUS_FAILED);
		}
		return null;
	}
	@Override
	public boolean isApplied(Entigrator entigrator, String locator$) {
		try{
//		System.out.println("ContactHandler:isApplied:locator="+locator$);
			if(debug	){
		    	 System.out.println("ContactHandler:isApplied:locator="+locator$);
		    	 if(entigrator==null)
		    		 System.out.println("ContactHandler:isApplied:entigrator is NULL");
			}
			
			Properties locator=Locator.toProperties(locator$);
			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			boolean result=false;
			Sack entity=entigrator.getEntityAtKey(entityKey$);
//			System.out.println("ContactHandler:isApplied:entity="+entity.getProperty("label"));
			String contact$=entity.getProperty("contact");
			if(contact$!=null&&!Locator.LOCATOR_FALSE.equals(contact$)){
			   if(entity.getElementItem("fhandler", ContactHandler.class.getName())==null){	
					if(!entity.existsElement("fhandler"))
						entity.createElement("fhandler");
					entity.putElementItem("fhandler", new Core(null,ContactHandler.class.getName(),null));
					entigrator.save(entity);
				}
	            result=true;
			}
			return result;
		}catch(Exception e){
		LOGGER.severe(e.toString());
		return false;
		}
	}

	
	public String getTitle() {
		return "Contact";
	}

	public String getType() {
		return "contact";
	}
	private void adaptLabel(Entigrator entigrator){
		 try{
				Sack entity=entigrator.getEntityAtKey(entityKey$);
				entigrator.ent_assignProperty(entity, "contact", entityLabel$);
		    }catch(Exception e){
		    	
		    }
	}
	@Override
	public void adaptClone(Entigrator entigrator) {
	   adaptLabel(entigrator);
	}

	@Override
	public void adaptRename(Entigrator entigrator) {
		adaptLabel(entigrator);
	}

@Override
public String getClassName() {
	return ContactHandler.class.getName();
}
@Override
public void completeMigration(Entigrator entigrator) {
	// TODO Auto-generated method stub
	
}
}