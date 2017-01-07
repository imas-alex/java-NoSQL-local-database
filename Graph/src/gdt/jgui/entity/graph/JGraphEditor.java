package gdt.jgui.entity.graph;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import gdt.jgui.entity.bookmark.JBookmarksEditor;

public class JGraphEditor extends JBookmarksEditor{
	@Override
	public JMenu getContextMenu() {
		menu=super.getContextMenu();
		menu.addSeparator();	
		JMenuItem graphItem = new JMenuItem("Graph");
			graphItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					//copy();
				}
			} );
			menu.add(graphItem);
		return menu;
	}
}
