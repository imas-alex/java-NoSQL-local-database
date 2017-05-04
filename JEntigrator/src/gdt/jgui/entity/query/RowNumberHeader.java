package gdt.jgui.entity.query;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.LookAndFeel;
import javax.swing.table.AbstractTableModel;

public class RowNumberHeader extends JTable {

	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected JTable mainTable;

	  public RowNumberHeader(JTable table) {
	    super();
	    mainTable = table;
	    setModel(new RowNumberTableModel());
	    setPreferredScrollableViewportSize(getMinimumSize());
	    setRowSelectionAllowed(false);
	    JComponent renderer = (JComponent) getDefaultRenderer(Object.class);
	    LookAndFeel.installColorsAndFont(renderer, "TableHeader.background",
	        "TableHeader.foreground", "TableHeader.font");
	    LookAndFeel.installBorder(this, "TableHeader.cellBorder");
	  }

	  public int getRowHeight(int row) {
	    return mainTable.getRowHeight();
	  }

	  class RowNumberTableModel extends AbstractTableModel {

	    public int getRowCount() {
	      return mainTable.getModel().getRowCount();
	    }

	    public int getColumnCount() {
	      return 1;
	    }

	    public Object getValueAt(int row, int column) {
	      return new Integer(row + 1);
	    }

	  }
}
