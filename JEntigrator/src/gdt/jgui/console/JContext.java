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
import javax.swing.JMenu;
import javax.swing.JPanel;
/**
 *  This interface defines a context.
 * 
 */
public interface JContext {
/** 
 * Indicates the context type.
 * 
 */
	public static final String CONTEXT_TYPE="context type";
/**
 * Get the panel to put into the main console.
 * @return the context panel.
 */
	public abstract JPanel getPanel();
	/**
	 * Get the context menu to put into the main console.
	 * @return the context menu.
	 */	
	public abstract JMenu getContextMenu();
	/**
	 * Get context locator. 
	 * @return the locator.
	 */	
	public abstract String getLocator();
	/**
	 * Create the context.
	 *  @param console the main application console
	 *  @param locator$ the locator string.
	 * @return the context.
	 */	
    public abstract JContext instantiate(JMainConsole console,String locator$);
    /**
     * Get context title.
     * @return the title string.
     */	
    public abstract String getTitle();
    /**
     * Get context subtitle.
     * @return the subtitle string.
     */	
    public abstract String getSubtitle();
    /**
     * Get context type.
     * @return the type string.
     */	
    public abstract String getType();
    /**
	 * Complete the context after
	 * remove it from the main console.
	 */	
    public abstract void close();
}