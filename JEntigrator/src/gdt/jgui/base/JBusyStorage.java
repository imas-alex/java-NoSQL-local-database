package gdt.jgui.base;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
public class JBusyStorage {
    public static void show(JComponent component){
    	JOptionPane.showMessageDialog(component, "The storage is busy. Try later.");
    }
}
