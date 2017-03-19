package gdt.jgui.entity.procedure;

import gdt.data.store.Entigrator;
import gdt.jgui.console.JMainConsole;
/**
 * Interface for a procedure.
 * @author imasa
 *
 */
public interface Procedure {
/**
 * This method runs the procedure.	
 * @param console the main console
 * @param entihome$ the database directory.
 * @param dividerLocation the current position of the divider on the context panel.
 */
public void  run(JMainConsole console,String entihome$,Integer dividerLocation);
public void  run(Entigrator entigrator,Integer dividerLocation);
}
