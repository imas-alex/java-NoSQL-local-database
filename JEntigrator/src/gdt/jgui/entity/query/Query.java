package gdt.jgui.entity.query;

import gdt.jgui.console.JMainConsole;
/**
 * Interface for a query.
 * @author imasa
 *
 */
public interface Query {
/**
 * The select entities method.
 * @param console the main console.
 * @param entihome$ the database directory.
 * @return array of keys of selected entities.
 */
	public String[] select(JMainConsole console,String entihome$);
}
