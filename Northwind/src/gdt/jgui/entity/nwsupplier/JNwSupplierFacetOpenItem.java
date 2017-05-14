package gdt.jgui.entity.nwsupplier;
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
import javax.swing.tree.DefaultMutableTreeNode;
import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.FacetHandler;
import gdt.data.entity.NwSupplierHandler;
import gdt.data.entity.facet.ExtensionHandler;
import gdt.data.grain.Locator;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JFacetOpenItem;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.entity.fields.JFieldsFacetOpenItem;


public class JNwSupplierFacetOpenItem extends JFieldsFacetOpenItem  {
	private static final long serialVersionUID = 1L;
	public JNwSupplierFacetOpenItem(){
		super();
	}
	
@Override
public String getLocator(){
	Properties locator=new Properties();
	locator.setProperty(Locator.LOCATOR_TITLE,"Nw supplier");
	locator.setProperty(BaseHandler.HANDLER_CLASS,JNwSupplierFacetOpenItem.class.getName());
	locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
	locator.setProperty(BaseHandler.HANDLER_METHOD,METHOD_OPEN_FACET);
	locator.setProperty(BaseHandler.HANDLER_LOCATION,NwSupplierHandler.EXTENSION_KEY);
	locator.setProperty( JContext.CONTEXT_TYPE,"Nw supplier facet");
	locator.setProperty(Locator.LOCATOR_TITLE,"Nw supplier");
	locator.setProperty(FACET_HANDLER_CLASS,NwSupplierHandler.class.getName());
	if(entityKey$!=null)
		locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
	if(entihome$!=null)
		locator.setProperty(Entigrator.ENTIHOME,entihome$);
	locator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
	locator.setProperty(Locator.LOCATOR_ICON_CLASS,getClass().getName());
	locator.setProperty(Locator.LOCATOR_ICON_FILE,"nwSupplier.png");
	locator.setProperty(Locator.LOCATOR_ICON_LOCATION,NwSupplierHandler.EXTENSION_KEY);
    if(entihome$!=null){   
 	locator.setProperty(Locator.LOCATOR_CHECKABLE,Locator.LOCATOR_TRUE);
	    }
	return Locator.toString(locator);
}
@Override
public boolean isRemovable() {
	return false;
	}

@Override
public String getFacetName() {
	return "NW supplier";
}
@Override
public String getFacetIcon(Entigrator entigrator) {
	if(entihome$!=null)
		 entigrator=console.getEntigrator(entihome$);
		return ExtensionHandler.loadIcon(entigrator,NwSupplierHandler.EXTENSION_KEY, "nwSupplier.png");
}
@Override
public String getFacetIconName() {
	return "nwSupplier.png";
}
@Override
public void removeFacet() {
	
}
@Override
public void openFacet(JMainConsole console,String locator$) {
	try{
	//	System.out.println("JAddressFacetOpenItem:openFacet:locator="+locator$);
		this.console=console;
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

		JNwSupplierEditor addressEditor=new JNwSupplierEditor();
		String aeLocator$=addressEditor.getLocator();
		aeLocator$=Locator.append(aeLocator$, Entigrator.ENTIHOME, entihome$);
		aeLocator$=Locator.append(aeLocator$, EntityHandler.ENTITY_KEY, entityKey$);
		aeLocator$=Locator.append(aeLocator$, JRequester.REQUESTER_RESPONSE_LOCATOR, requesterResponseLocator$);
		aeLocator$=Locator.append(aeLocator$, BaseHandler.HANDLER_METHOD,"instantiate");
		JConsoleHandler.execute(console, aeLocator$);
	}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
	}
}
@Override
public String getFacetRenderer() {
	return JNwSupplierEditor.class.getName();
}
@Override
public FacetHandler getFacetHandler() {
	return new NwSupplierHandler();
}
@Override
public JPopupMenu getPopupMenu(final String digestLocator$) {
	return super.getPopupMenu(digestLocator$);

}
@Override
public void response(JMainConsole console, String locator$) {
	super.response(console,locator$);

}

@Override
public DefaultMutableTreeNode[] getDigest(Entigrator entigrator,String locator$) {
	return null;
}

}
