package gdt.jgui.entity.view;

import java.util.Properties;
import javax.swing.table.DefaultTableModel;
import gdt.data.store.Entigrator;
/**
 * Interface for a query.
 * @author imasa
 *
 */
public interface View {
/**
 * The select entities method.
 * @param entigrator the entigrator.
 * @return default table model.
 */
	public DefaultTableModel select(Entigrator entigrator);
	public String getColumnType(String columnName$);
}
