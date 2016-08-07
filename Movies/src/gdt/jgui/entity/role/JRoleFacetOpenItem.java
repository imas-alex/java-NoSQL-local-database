package gdt.jgui.entity.role;
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

import javax.swing.JPopupMenu;
import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.FacetHandler;
import gdt.data.entity.RoleHandler;
import gdt.data.entity.facet.ExtensionHandler;
import gdt.data.grain.Locator;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JFacetOpenItem;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.entity.fields.JFieldsEditor;
import gdt.jgui.entity.fields.JFieldsFacetOpenItem;

public class JRoleFacetOpenItem extends JFieldsFacetOpenItem{
	private static final long serialVersionUID = 1L;
	public JRoleFacetOpenItem(){
		super();
	}
	@Override
	public String getLocator(){
		Properties locator=new Properties();
		locator.setProperty(Locator.LOCATOR_TITLE,"Role");
		locator.setProperty(BaseHandler.HANDLER_CLASS,JRoleFacetOpenItem.class.getName());
		locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
		locator.setProperty(BaseHandler.HANDLER_METHOD,METHOD_OPEN_FACET);
		locator.setProperty(BaseHandler.HANDLER_LOCATION,RoleHandler.EXTENSION_KEY);
		locator.setProperty( JContext.CONTEXT_TYPE,"Role facet");
		locator.setProperty(Locator.LOCATOR_TITLE,"Role");
		locator.setProperty(FACET_HANDLER_CLASS,RoleHandler.class.getName());
		if(entityKey$!=null)
			locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
		if(entihome$!=null){
			locator.setProperty(Entigrator.ENTIHOME,entihome$);
			locator.setProperty(Locator.LOCATOR_CHECKABLE,Locator.LOCATOR_TRUE);
			Entigrator entigrator=console.getEntigrator(entihome$);
			String icon$=ExtensionHandler.loadIcon(entigrator,RoleHandler.EXTENSION_KEY, "role.png");
		    if(icon$!=null)
		    	locator.setProperty(Locator.LOCATOR_ICON,icon$);
		}
		return Locator.toString(locator);
	}

@Override
public boolean isRemovable() {
	return false;
	}

@Override
public String getFacetName() {
	return "role";
}
@Override
public String getFacetIcon() {
	//return Support.readHandlerIcon(JBankFacetOpenItem.class, "bank.png");
	if(entihome$!=null){
		Entigrator entigrator=console.getEntigrator(entihome$);
		return ExtensionHandler.loadIcon(entigrator,RoleHandler.EXTENSION_KEY, "role.png");
	}
	return null;
}
@Override
public void removeFacet() {
	
}
@Override
public void openFacet(JMainConsole console,String locator$) {
	try{
	//	System.out.println("JBankFacetOpenItem:openFacet:locator="+locator$);
		Properties locator=Locator.toProperties(locator$);
		String entihome$=locator.getProperty(Entigrator.ENTIHOME);
		String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		String responseLocator$=getLocator();
		Properties responseLocator=Locator.toProperties(responseLocator$);
		responseLocator.setProperty(Entigrator.ENTIHOME, entihome$);
		responseLocator.setProperty(EntityHandler.ENTITY_KEY, entityKey$);
		responseLocator.setProperty(BaseHandler.HANDLER_METHOD, JFacetOpenItem.METHOD_RESPONSE);
		//
		JEntityFacetPanel efp=new JEntityFacetPanel();
		String efpLocator$=efp.getLocator();
		efpLocator$=Locator.append(efpLocator$, Entigrator.ENTIHOME, entihome$);
		efpLocator$=Locator.append(efpLocator$, EntityHandler.ENTITY_KEY, entityKey$);
		efpLocator$=Locator.append(efpLocator$, JRequester.REQUESTER_ACTION, ACTION_DISPLAY_FACETS);
		responseLocator.setProperty(JRequester.REQUESTER_RESPONSE_LOCATOR, Locator.compressText(efpLocator$));
		//
		responseLocator$=Locator.toString(responseLocator);
		String requesterResponseLocator$=Locator.compressText(responseLocator$);

		JRoleEditor roleEditor=new JRoleEditor();
		String mvLocator$=roleEditor.getLocator();
		mvLocator$=Locator.append(mvLocator$, Entigrator.ENTIHOME, entihome$);
		mvLocator$=Locator.append(mvLocator$, EntityHandler.ENTITY_KEY, entityKey$);
		mvLocator$=Locator.append(mvLocator$, JRequester.REQUESTER_RESPONSE_LOCATOR, requesterResponseLocator$);
		mvLocator$=Locator.append(mvLocator$, BaseHandler.HANDLER_METHOD,"instantiate");
		JConsoleHandler.execute(console, mvLocator$);
	}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
	}
}
@Override
public String getFacetRenderer() {
	return JRoleEditor.class.getName();
}
@Override
public FacetHandler getFacetHandler() {
	return new RoleHandler();
}

@Override
public JPopupMenu getPopupMenu(final String digestLocator$) {
return super.getPopupMenu(digestLocator$);
	
}


@Override
public void response(JMainConsole console, String locator$) {
//	System.out.println("JBankFacetOpenItem:responce:locator="+locator$);
	super.response(console,locator$);
}

}