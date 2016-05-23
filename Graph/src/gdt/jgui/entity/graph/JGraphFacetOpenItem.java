package gdt.jgui.entity.graph;
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
import gdt.data.entity.EdgeHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.FacetHandler;
import gdt.data.entity.GraphHandler;
import gdt.data.entity.NodeHandler;
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
import gdt.jgui.entity.fields.JFieldsFacetOpenItem;


public class JGraphFacetOpenItem extends JFieldsFacetOpenItem {
	private static final long serialVersionUID = 1L;
	public JGraphFacetOpenItem(){
		super();
	}
	
@Override
public String getLocator(){
	Properties locator=new Properties();
	locator.setProperty(Locator.LOCATOR_TITLE,"Graph");
	locator.setProperty(BaseHandler.HANDLER_CLASS,JGraphFacetOpenItem.class.getName());
	locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
	locator.setProperty(BaseHandler.HANDLER_METHOD,METHOD_OPEN_FACET);
	locator.setProperty(BaseHandler.HANDLER_LOCATION,GraphHandler.EXTENSION_KEY);
	locator.setProperty( JContext.CONTEXT_TYPE,"Graph facet");
	locator.setProperty(Locator.LOCATOR_TITLE,"Graph");
	locator.setProperty(FACET_HANDLER_CLASS,GraphHandler.class.getName());
	if(entityKey$!=null)
		locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
	if(entihome$!=null){
		locator.setProperty(Entigrator.ENTIHOME,entihome$);
		Entigrator entigrator=console.getEntigrator(entihome$);
	 icon$=ExtensionHandler.loadIcon(entigrator, GraphHandler.EXTENSION_KEY,"graph.png");
	if(icon$!=null)
	    	locator.setProperty(Locator.LOCATOR_ICON,icon$);
	}
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
	return "Graph";
}
@Override
public String getFacetIcon() {
	Entigrator entigrator=console.getEntigrator(entihome$);
	return ExtensionHandler.loadIcon(entigrator,GraphHandler.EXTENSION_KEY,"graph.png"); 

}
@Override
public void removeFacet() {
	
}
@Override
public void openFacet(JMainConsole console,String locator$) {
	try{
	//	System.out.println("JAddressFacetOpenItem:openFacet:locator="+locator$);
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

		JGraphRenderer graphRenderer =new JGraphRenderer();
		String grLocator$=graphRenderer.getLocator();
		grLocator$=Locator.append(grLocator$, Entigrator.ENTIHOME, entihome$);
		grLocator$=Locator.append(grLocator$, EntityHandler.ENTITY_KEY, entityKey$);
		grLocator$=Locator.append(grLocator$, JRequester.REQUESTER_RESPONSE_LOCATOR, requesterResponseLocator$);
		grLocator$=Locator.append(grLocator$, BaseHandler.HANDLER_METHOD,"instantiate");
		JConsoleHandler.execute(console, grLocator$);
	}catch(Exception e){
		Logger.getLogger(getClass().getName()).severe(e.toString());
	}
}
@Override
public String getFacetRenderer() {
	return JGraphRenderer.class.getName();
}
@Override
public FacetHandler getFacetHandler() {
	return new GraphHandler();
}
@Override
public JPopupMenu getPopupMenu(final String digestLocator$) {
	//System.out.println("JFieldsFacetOpenItem:edit:digest locator="+Locator.remove(digestLocator$, Locator.LOCATOR_ICON));
	return super.getPopupMenu(digestLocator$);

}
@Override
public void response(JMainConsole console, String locator$) {
//	System.out.println("JAddressFacetOpenItem:responce:locator="+locator$);
	super.response(console,locator$);

}
}
