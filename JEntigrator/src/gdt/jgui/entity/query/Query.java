package gdt.jgui.entity.query;

import gdt.data.store.Entigrator;

/**
 * Interface for a query.
 * @author imasa
 *
 */
public interface Query {
/**
 * The select entities method.
 * @param entigrator the entigrator.
 * @return array of keys of selected entities.
 */
	public String[] select(Entigrator entigrator);
}
