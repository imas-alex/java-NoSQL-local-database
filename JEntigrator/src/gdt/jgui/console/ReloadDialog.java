package gdt.jgui.console;

import java.awt.Component;

import javax.swing.JOptionPane;


public class ReloadDialog {
Component context;	
String title$;
	public ReloadDialog(Component context){
		this.context=context;
	}
	public ReloadDialog(Component context,String title$){
		this.context=context;
		this.title$=title$;
	}
	public int show(){
		if(title$==null)
			title$="Data conflict";
		Object[] options = {"Reload",
                "Replace",
                "Cancel"};
	int n = JOptionPane.showOptionDialog(context,
			"The data in the database is more recent ",
			title$,
		JOptionPane.YES_NO_CANCEL_OPTION,
		JOptionPane.QUESTION_MESSAGE,
		null,
		options,
		options[2]);
	return n;
	}
}
