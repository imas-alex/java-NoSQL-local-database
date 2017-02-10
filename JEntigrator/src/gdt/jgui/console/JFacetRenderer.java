package gdt.jgui.console;
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

import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
import gdt.jgui.entity.JReferenceEntry;
/**
 * This interface defines the facet renderer functionality.  
 */

public interface JFacetRenderer extends JContext {
	public static final String ONLY_ITEM="only item";
	/**
   * Add the renderer's icon bitmap in the form of Base64
   * string to the locator.
   * @param locator$ the origin locator
   * @return the locator with the icon added.
   */
	public String addIconToLocator(String locator$);
	/**
	 * Get facet handler class name associated with the renderer.
	 * @return the facet handler class name.
	 */
  public String getFacetHandler();
  /**
   * Get the entity type associated with the renderer.
   * @return the type string.
   */
  public String getEntityType();
  /**
   * Get the category icon associated with the renderer encoded
   * into Base64 string.
   * @param entigrator the entigrator.
   * @return the icon string.
   */
  public String getCategoryIcon(Entigrator entigrator);
  /**
   * Get category title associated with the given entity type.
   * @return the category string.
   */
  //public String getCategoryIcon(Entigrator entigrator);
  public String getFacetIcon();
  public String getCategoryTitle();
  /**
   * Adapt facet-relevant parameters of the clone after entity cloning. 
   * another entity.
   * @param console the main console.
   * @param locator$ the locator string.
   */
  public void adaptClone(JMainConsole console,String locator$);
  /**
   * Adapt facet-relevant parameters of the entity after renaming. 
   * @param console the main console.
   * @param locator$ the locator string.
   */
  public void adaptRename(JMainConsole console,String locator$);
  /**
   * Add all facet-relevant related entities within the database 
   * to the relations list. 
   * @param entigrator the entigrator.
   * @param entiyKey$ the key of the origin entity.
   * @param sl the list of relations.
   */
  public void collectReferences(Entigrator entigrator,String entiyKey$, ArrayList<JReferenceEntry>sl);
  /**
   * Rebuild the facet-relevant indexes for the entity.
   * @param console the main console.
   * @param entigrator the entigrator.
   * @param entity the entity.
   */
  public void reindex(JMainConsole console,Entigrator entigrator,Sack entity);
  /**
   * Create a new entity having the facet type.
   * @param console the main console. 
   * @param locator$ the locator string.
   * @return the new entity.
   */
  public String newEntity(JMainConsole console,String locator$);
  public String getFacetOpenItem();
}
