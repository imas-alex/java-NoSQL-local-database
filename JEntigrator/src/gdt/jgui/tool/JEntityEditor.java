package gdt.jgui.tool;
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
import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.entity.JEntityPrimaryMenu;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.BoxLayout;
import javax.swing.ListSelectionModel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.apache.commons.codec.binary.Base64;
/**
 * This class is a graphical entity editor. 
 * @author imasa
 *
 */
public class JEntityEditor extends JPanel implements JContext,JRequester{
	private static final long serialVersionUID = 1L;
	private static final String ACTION_RENAME_ELEMENT="action rename element";
	private static final String ACTION_ADD_ELEMENT="action add element";
	private static final String ACTION_EDIT_CELL="action edit cell";
	private static final String CELL_FIELD="cell field";
	private static final String CELL_FIELD_TYPE="cell field type";
	private static final String CELL_FIELD_NAME="cell field name";
	private static final String CELL_FIELD_VALUE="cell field value";
	private static final String CORE_NAME="cell core name";
	private static final String ELEMENT="element";
	private Logger LOGGER=Logger.getLogger(JEntityEditor.class.getName());
	public final static String ENTITY_EDIT="entity edit";
	private JMainConsole console;
	private String entihome$;
	private String entityKey$;
	private String entityLabel$;
	private String requesterAction$;
	private String element$;
	private Core[] content;
	JTabbedPane tabbedPane;
	JMenu menu;
	JMenuItem deleteItemsItem;
	JMenuItem editCellItem;
	JMenuItem copyItem;
	JMenuItem cutItem;
	JMenuItem pasteItem;
	String message$;
	Sack entity;
/**
 * The default consturctor.
 */
	public JEntityEditor() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedPane);
	}
private void showElement(Sack entity,String element$){
	try{
//		System.out.println("EntityEditor:showElement:"+element$);
	Core[] ca=null;
	if("attributes".equals(element$))
		ca=entity.attributesGet();
	else
	   ca=entity.elementGet(element$);
	  final JTable table = new JTable();
	  DefaultTableModel model=new DefaultTableModel(
			  null
			  ,
				new String[] {
					"type", "name", "value"
				}
			);
	  table.setModel(model);
	  table.getTableHeader().setDefaultRenderer(new SimpleHeaderRenderer());
	  table.getTableHeader().addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		        int col = table.columnAtPoint(e.getPoint());
		        String name = table.getColumnName(col);
//		        System.out.println("Column index selected " + col + " " + name);
		        sort(name);
		    }
		});
	  JScrollPane scrollPane = new JScrollPane();
    	tabbedPane.add(element$,scrollPane);
		scrollPane.add(table);
		scrollPane.setViewportView(table); 
	if(ca!=null)	
	  for(Core aCa:ca){
		  model.addRow(new String[]{aCa.type,aCa.name,aCa.value});
	  }
	}catch(Exception e ){
		LOGGER.severe(e.toString());
	}
}
private Core[] getContent(boolean selected){
	try{
		JScrollPane scrollPane=(JScrollPane)tabbedPane.getSelectedComponent();
		JTable table=(JTable)scrollPane.getViewport().getView();
		DefaultTableModel model=(DefaultTableModel)table.getModel();
		ListSelectionModel listModel=table.getSelectionModel();
		int cnt=model.getRowCount();
		if(cnt<1)
			return null;
		ArrayList<Core>cl=new ArrayList<Core>();
		for(int i=0;i<cnt;i++){
			if(selected)
				if(!listModel.isSelectedIndex(i))
					continue;
			cl.add(new Core((String)model.getValueAt(i, 0),(String)model.getValueAt(i, 1),(String)model.getValueAt(i, 2)));
		}
        return cl.toArray(new Core[0]);		
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
	return null;
}
private void replaceTable( Core[] ca){
	try{
		JScrollPane scrollPane=(JScrollPane)tabbedPane.getSelectedComponent();
		JTable table=(JTable)scrollPane.getViewport().getView();
		DefaultTableModel model=(DefaultTableModel)table.getModel();
		while(model.getRowCount()>0)
			model.removeRow(0);
		int cnt=ca.length;
		if(cnt<1)
			return;
		for(int i=0;i<cnt;i++)
			model.addRow(new String[]{ca[i].type,ca[i].name,ca[i].value});
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
}
private void pasteTable( Core[] ca){
	try{
		JScrollPane scrollPane=(JScrollPane)tabbedPane.getSelectedComponent();
		JTable table=(JTable)scrollPane.getViewport().getView();
		DefaultTableModel model=(DefaultTableModel)table.getModel();
		int cnt=ca.length;
		if(cnt<1)
			return;
		for(int i=0;i<cnt;i++)
			model.addRow(new String[]{ca[i].type,ca[i].name,ca[i].value});
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
}
private void cutTable( Core[] ca){
	try{
		if(ca==null)
			return;
		int cnt=ca.length;
		if(cnt<1)
			return;
		Core[] tca=getContent(false);
		Core[]ca1=Core.subtract(tca, ca);
		replaceTable(ca1);
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
}
private void sort(String header$){
	try{
//		System.out.println("EntityEditor.sort:header="+header$);
		Core[] ca=getContent(false);
		if("type".equals(header$))
			ca=Core.sortAtType(ca);
		else if ("name".equals(header$))
			ca=Core.sortAtName(ca);
		else if ("value".equals(header$))
			ca=Core.sortAtValue(ca);
		replaceTable(ca);
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
}
/**
 * Get the panel to insert into the main console.
 * @return the panel.
 */
	@Override
	public JPanel getPanel() {
		return this;
	}
	/**
	 * Get the context menu.
	 * @return the context menu.
	 */
	@Override
	public JMenu getContextMenu() {
		menu=new JMenu("Context");
		menu.addMenuListener(new MenuListener(){
			@Override
			public void menuSelected(MenuEvent e) {
//			System.out.println("EntityEditor:getConextMenu:menu selected");
	
			if(editCellItem!=null) 
			menu.remove(editCellItem);
			if(deleteItemsItem!=null)
			   menu.remove(deleteItemsItem);
			if(copyItem!=null)
				   menu.remove(copyItem);
			if(pasteItem!=null)
				   menu.remove(pasteItem);
			if(cutItem!=null)
				   menu.remove(cutItem);
			if(hasEditingCell()){
				//menu.addSeparator();
				editCellItem = new JMenuItem("Edit item");
				editCellItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						String locator$=getEditCellLocator();
						if(locator$!=null)
						   JConsoleHandler.execute(console, locator$);
					}
				} );
				menu.add(editCellItem);
				}
			if(hasSelectedRows()){
			    deleteItemsItem = new JMenuItem("Delete items");
			    deleteItemsItem.addActionListener(new ActionListener() {
				   @Override
				   public void actionPerformed(ActionEvent e) {
					   int response = JOptionPane.showConfirmDialog(JEntityEditor.this, "Delete selected items ?", "Confirm",
						        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					   if (response == JOptionPane.YES_OPTION) {
							   deleteRows();
						    } 
				   }
			      } );
			menu.add(deleteItemsItem);
			copyItem = new JMenuItem("Copy");
			copyItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
				   element$=null;
				   content=getContent(true);
				}
			} );
			menu.add(copyItem);
			cutItem = new JMenuItem("Cut");
			cutItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int i=tabbedPane.getSelectedIndex();
					element$=tabbedPane.getTitleAt(i);
					   content=getContent(true);
				}
			} );
			menu.add(cutItem);
			}
			if(content!=null){
				pasteItem = new JMenuItem("Paste");
				pasteItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						pasteTable(content);
						int j=tabbedPane.getSelectedIndex();
						if(element$!=null){
							int cnt=tabbedPane.getComponentCount();
							for(int i=0;i<cnt;i++){
								if(element$.equals(tabbedPane.getTitleAt(i))){
									tabbedPane.setSelectedIndex(i);
									cutTable(content);
									tabbedPane.setSelectedIndex(j);
								}
							}
						}
					}
				} );
				menu.add(pasteItem);
				}
			}
			@Override
			public void menuDeselected(MenuEvent e) {
			}
    		@Override
			public void menuCanceled(MenuEvent e) {
			}
		});
		
		JMenuItem doneItem = new JMenuItem("Done");
		doneItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
				console.back();
				
			}
		} );
		menu.add(doneItem);
		JMenuItem cancelItem = new JMenuItem("Cancel");
		cancelItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
				console.back();
			}
		} );
		menu.add(cancelItem);
        menu.addSeparator();		
		JMenuItem addItemItem = new JMenuItem("Add item");
		addItemItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addRow();
			}
		} );
		menu.add(addItemItem);
		JMenuItem addElementItem = new JMenuItem("Add element");
		addElementItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String element$="new element";
				//addElement(element$);
				String locator$=getRenameElementLocator(element$);
				JConsoleHandler.execute(console, locator$);
			}
		} );
		menu.add(addElementItem);
		JMenuItem deleteElementItem = new JMenuItem("Delete element");
		deleteElementItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				 int response = JOptionPane.showConfirmDialog(JEntityEditor.this, "Delete element ?", "Confirm",
					        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				   if (response == JOptionPane.YES_OPTION) {
						   tabbedPane.remove(tabbedPane.getSelectedComponent());
					    } 
			}
		} );
		menu.add(deleteElementItem);
		JMenuItem renameElementItem = new JMenuItem("Rename element");
		renameElementItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int i=tabbedPane.getSelectedIndex();
				String locator$=getRenameElementLocator(tabbedPane.getTitleAt(i));
				JConsoleHandler.execute(console, locator$);
			}
		} );
		menu.add(renameElementItem);
		menu.addSeparator();
		if(hasEditingCell()){
		editCellItem = new JMenuItem("Edit item");
		editCellItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String locator$=getEditCellLocator();
				if(locator$!=null)
				   JConsoleHandler.execute(console, locator$);
			}
		} );
		menu.add(editCellItem);
		}
		return menu;
	}
	/**
	 * Get the context locator.
	 * @return the context locator.
	 */
	@Override
	public String getLocator() {
		Properties locator=new Properties();
	    locator.setProperty(Locator.LOCATOR_TYPE, JContext.CONTEXT_TYPE);
	    locator.setProperty(Locator.LOCATOR_TITLE, getTitle());
	    locator.setProperty(JContext.CONTEXT_TYPE,getType());
	    if(entihome$!=null)
		       locator.setProperty(Entigrator.ENTIHOME,entihome$);
	    if(entityKey$!=null)
		       locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
	    if(entityLabel$!=null)
		       locator.setProperty(EntityHandler.ENTITY_LABEL,entityLabel$);
	    if(requesterAction$!=null)
		       locator.setProperty(JRequester.REQUESTER_ACTION,requesterAction$);
	   locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
	   locator.setProperty(BaseHandler.HANDLER_CLASS,JEntityEditor.class.getName());
	   locator.setProperty(BaseHandler.HANDLER_METHOD,"response");
	   String icon$=Support.readHandlerIcon(null,JEntityPrimaryMenu.class, "edit.png");
	   locator.setProperty( Locator.LOCATOR_ICON,icon$);
	   return Locator.toString(locator);
	}
	/**
	 * Create the context.
	 * @param console the main console.
	 * @param locator$ the locator string.
	 * @return the procedure context.
	 */
	@Override
	public JContext instantiate(JMainConsole console, String locator$) {
		this.console=console;
			 Properties locator=Locator.toProperties(locator$);
			 entihome$=locator.getProperty(Entigrator.ENTIHOME);
			 entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			 Entigrator entigrator=console.getEntigrator(entihome$);
			 entity=entigrator.getEntityAtKey(entityKey$);
			 if(!entigrator.lock_set(entity)){
					
			  message$=entigrator.lock_message(entity);
		  }
			 entityLabel$=locator.getProperty(EntityHandler.ENTITY_LABEL);
			 requesterAction$=locator.getProperty(JRequester.REQUESTER_ACTION);
			 element$=locator.getProperty(ELEMENT);
//			 System.out.println("EntityEditor:instantiate:locator="+locator$);
			 refresh();
		 return this;
	}
private void refresh(){
	try{
		Entigrator entigrator=console.getEntigrator(entihome$);
		entity=entigrator.getEntityAtKey(entityKey$);
		showElement(entity,"attributes");
		String[]sa=entity.elementsList();
		Support.sortStrings(sa);
		for(String aSa:sa)
			showElement(entity,aSa);
		if(element$!=null)
			selectElement(element$);
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
}
/**
 * Get context title.
 * @return the context title.
 */		
@Override
	public String getTitle() {
	
		if(entityLabel$!=null){
			if(message$==null)
				return entityLabel$;
			else
				return entityLabel$+message$;
			
		}
		return "Entity editor";
	}
/**
 * Get context type.
 * @return the context type.
 */	
	@Override
	public String getType() {
		return "Entity editor";
	}
	private void save(){
		try{
			int eCnt=tabbedPane.getComponentCount();
			Sack candidate=new Sack();
			String element$;
			JScrollPane scrollPane;
			JTable   table;
			int rCnt;
			Core row;
			TableModel model;
			for(int i=0;i<eCnt;i++){
				element$=tabbedPane.getTitleAt(i);
				candidate.createElement(element$);
				scrollPane=(JScrollPane)tabbedPane.getComponentAt(i);
				table=(JTable)scrollPane.getViewport().getView();
				rCnt=table.getRowCount();
				model=table.getModel();
				for(int j=0;j<rCnt;j++){
					row=new Core((String)model.getValueAt(j,0),(String)model.getValueAt(j,1),(String)model.getValueAt(j,2));
					if("attributes".equals(element$))
						candidate.putAttribute(row);
					else
					    candidate.putElementItem(element$, row);
				}
			}
			candidate.setKey(entityKey$);
			candidate.saveXML(entihome$+"/"+Entigrator.ENTITY_BASE+"/data/"+entityKey$);
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
	}
	private void addRow(){
		try{
			JScrollPane scrollPane=(JScrollPane)tabbedPane.getSelectedComponent();
			JTable table=(JTable)scrollPane.getViewport().getView();
			DefaultTableModel model=(DefaultTableModel)table.getModel();
			model.addRow(new String[]{null,"Name"+String.valueOf(model.getRowCount()),"Value"});
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
	}
	private boolean hasSelectedRows(){
		try{
			JScrollPane scrollPane=(JScrollPane)tabbedPane.getSelectedComponent();
			JTable table=(JTable)scrollPane.getViewport().getView();
			int[] i=table.getSelectedRows();
		    if(i.length>0)
		    	return true;
		    return false;
		}catch(Exception e){
			LOGGER.severe(e.toString());
			return false;
		}
	}
	private boolean hasEditingCell(){
		try{
			JScrollPane scrollPane=(JScrollPane)tabbedPane.getSelectedComponent();
			JTable table=(JTable)scrollPane.getViewport().getView();
//			System.out.println("Entityeditor:hasEditingCell:x="+table.getEditingRow()+" y="+table.getEditingColumn());
			if(table.getEditingColumn()>-1&&table.getEditingRow()>-1)
				return true;
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
		return false;
	}
	private void selectElement(String title$){
		int cnt=tabbedPane.getComponentCount();
		for(int i=0;i<cnt;i++)
		     if(title$.equals(tabbedPane.getTitleAt(i))){
		    	 tabbedPane.setSelectedIndex(i);
		    	 return;
		     }
	}
	private void deleteRows(){
		try{
			JScrollPane scrollPane=(JScrollPane)tabbedPane.getSelectedComponent();
			JTable table=(JTable)scrollPane.getViewport().getView();
			DefaultTableModel tableModel=(DefaultTableModel)table.getModel();
			ListSelectionModel listModel=table.getSelectionModel();
			int rCnt=table.getRowCount();
			ArrayList<Integer>srl=new ArrayList<Integer>();
			for(int i=0;i<rCnt;i++)
				if(listModel.isSelectedIndex(i)){
					srl.add(new Integer(i));
				}
			Integer[] sra=srl.toArray(new Integer[0]);
			ArrayList<Core> ol=new ArrayList<Core>();
			Core row;
			boolean skip;
			for(int i=0;i<rCnt;i++){
				skip=false;
				for(int aSra:sra){
					if(i==aSra){
						skip=true;
						break;
					}
				}
				if(!skip){
					row =new Core((String)tableModel.getValueAt(i, 0),(String)tableModel.getValueAt(i, 1),(String)tableModel.getValueAt(i, 2));
					ol.add(row);
				}
			}
			Core[] ra=ol.toArray(new Core[0]);
			while(tableModel.getRowCount()>0)
				tableModel.removeRow(0);
			for(Core aRa:ra){
				 tableModel.addRow(new String[]{aRa.type,aRa.name,aRa.value});
			}
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
	}
	private String getRenameElementLocator(String element$) {
		        JTextEditor textEditor=new JTextEditor();
		        String teLocator$=textEditor.getLocator();
		        teLocator$= Locator.merge(getLocator(),teLocator$);
		        teLocator$=Locator.append(teLocator$ ,JTextEditor.TEXT,element$);
		        teLocator$=Locator.append(teLocator$ ,ELEMENT,element$);
		        teLocator$=Locator.append(teLocator$ ,JRequester.REQUESTER_ACTION,ACTION_RENAME_ELEMENT);
				teLocator$=Locator.append(teLocator$, Locator.LOCATOR_TITLE,"Rename element");
				teLocator$=Locator.append(teLocator$,BaseHandler.HANDLER_CLASS,JTextEditor.class.getName());
				String responseLocator$=getLocator();
				responseLocator$=Locator.append(responseLocator$, BaseHandler.HANDLER_METHOD, "response");
				responseLocator$=Locator.append(responseLocator$, JRequester.REQUESTER_ACTION, ACTION_ADD_ELEMENT);
				teLocator$=Locator.append(teLocator$,JRequester.REQUESTER_RESPONSE_LOCATOR,Locator.compressText(responseLocator$));
				return teLocator$;
			}
	private String getEditCellLocator() {
        try{
		save();
        JTextEditor textEditor=new JTextEditor();
        String locator$=textEditor.getLocator();
        locator$= Locator.merge(getLocator(),locator$);
        JScrollPane scrollPane=(JScrollPane)tabbedPane.getSelectedComponent();
		JTable table=(JTable)scrollPane.getViewport().getView();
		int i=tabbedPane.getSelectedIndex();
		String element$=tabbedPane.getTitleAt(i);
		int x=table.getEditingRow();
		int y=table.getEditingColumn();
		String cellField$=CELL_FIELD_TYPE;
		if(y==1)
			cellField$=CELL_FIELD_NAME;
		else if(y==2)
		   cellField$=CELL_FIELD_VALUE;
		TableModel model=table.getModel();
		String text$=(String)model.getValueAt(x, y);
		text$=Locator.compressText(text$);
		locator$=Locator.append(locator$ ,JTextEditor.IS_BASE64,Locator.LOCATOR_TRUE);
		String coreName$=(String)model.getValueAt(x, 1);
        locator$=Locator.append(locator$ ,JTextEditor.TEXT,text$);
        locator$=Locator.append(locator$ ,ELEMENT,element$);
        locator$=Locator.append(locator$ ,CELL_FIELD,cellField$);
        locator$=Locator.append(locator$ ,CORE_NAME,coreName$);
        locator$=Locator.append(locator$ ,JRequester.REQUESTER_ACTION,ACTION_EDIT_CELL);
		locator$=Locator.append(locator$, Locator.LOCATOR_TITLE,"Edit item");
		locator$=Locator.append(locator$,BaseHandler.HANDLER_CLASS,JTextEditor.class.getName());
		 return locator$;
        }catch(Exception e){
        	LOGGER.severe(e.toString());
        	return null;
        }
	}
	/**
	 * Execute the response locator.
	 * @param console the main console.
	 * @param locator$ the response locator.
	 * 
	 */
	@Override
public void response(JMainConsole console, String locator$) {
//	System.out.println("EntityEditor:response:"+Locator.remove(locator$,Locator.LOCATOR_ICON ));
	try{
		Properties locator=Locator.toProperties(locator$);
		String action$=locator.getProperty(JRequester.REQUESTER_ACTION);
		String text$=locator.getProperty(JTextEditor.TEXT);
		if(ACTION_ADD_ELEMENT.equals(action$)){
				  Entigrator entigrator=console.getEntigrator(entihome$);
				  entity=entigrator.getEntityAtKey(entityKey$);
				  if(entity.existsElement(text$))
					  return;
				  entity.createElement(text$);
				  entity.putElementItem(text$, new Core(null,"item",null));
				  entigrator.save(entity);
				  locator$=getLocator();
				  locator$=Locator.remove(locator$, BaseHandler.HANDLER_METHOD);
				  locator$=Locator.remove(locator$, JRequester.REQUESTER_ACTION);
				  JConsoleHandler.execute(console, locator$);
				 return;
		}
		if(ACTION_RENAME_ELEMENT.equals(action$)){
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			Entigrator entigrator=console.getEntigrator(entihome$);
			String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			String element$=locator.getProperty(JTextEditor.TEXT);
			String oldElement$=locator.getProperty(ELEMENT);
			entity=entigrator.getEntityAtKey(entityKey$);
			entity.createElement(element$);
			entity.elementReplace(element$, entity.elementGet(oldElement$));
			entity.removeElement(oldElement$);
			entigrator.save(entity);
			locator$=Locator.append(locator$, Locator.LOCATOR_TITLE,"Edit");
			locator$=Locator.append(locator$,EntityHandler.ENTITY_ACTION,JEntityEditor.ENTITY_EDIT);
			locator$=Locator.append(locator$,BaseHandler.HANDLER_CLASS,JEntityEditor.class.getName());
			locator$=Locator.append(locator$,ELEMENT,element$);
			JConsoleHandler.execute(console, locator$);
		}
		if(ACTION_EDIT_CELL.equals(action$)){
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			Entigrator entigrator=console.getEntigrator(entihome$);
			String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			entity=entigrator.getEntityAtKey(entityKey$);
			//System.out.println("EntityEditor:response:entity="+entity.getProperty("label"));
			String element$=locator.getProperty(ELEMENT);
			text$=locator.getProperty(JTextEditor.TEXT);
			System.out.println("EntityEditor:response:text="+text$);
			if(Locator.LOCATOR_TRUE.equals(locator.getProperty(JTextEditor.IS_BASE64))){
				byte[] ba=Base64.decodeBase64(text$);
				text$ = new String(ba, "UTF-8");
	//			System.out.println("EntityEditor:response:decoded text="+text$);
			}
				
			String cellField$=locator.getProperty(CELL_FIELD);
			String core$=locator.getProperty(CORE_NAME);
			Core core;
			if("attributes".equals(element$))
				core=entity.getAttribute(core$);
			else	
			 core=entity.getElementItem(element$, core$);
			if(core==null)
		//	System.out.println("EntityEditor:response:cannot find core="+core$);
			if(CELL_FIELD_TYPE.equals(cellField$))
				core.type=text$;
			else if(CELL_FIELD_NAME.equals(cellField$))
				core.name=text$;
			else if (CELL_FIELD_VALUE.equals(cellField$))
				core.value=text$;
			if("attributes".equals(element$))
				entity.putAttribute(core);
			else	
				entity.putElementItem(element$, core);
			entigrator.save(entity);
			System.out.println("EntityEditor:response:entity saved");
			locator$=Locator.append(locator$, Locator.LOCATOR_TITLE,"Edit");
			locator$=Locator.append(locator$,EntityHandler.ENTITY_ACTION,JEntityEditor.ENTITY_EDIT);
			locator$=Locator.append(locator$,BaseHandler.HANDLER_CLASS,JEntityEditor.class.getName());
			locator$=Locator.append(locator$,ELEMENT,element$);
			JConsoleHandler.execute(console, locator$);
		}
		
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
}
	private class SimpleHeaderRenderer extends JLabel implements TableCellRenderer {
		private static final long serialVersionUID = 1L;
		public SimpleHeaderRenderer() {
	        setFont(new Font("Consolas", Font.BOLD, 14));
	        setForeground(Color.BLUE);
	        setBorder(BorderFactory.createEtchedBorder());
	        setHorizontalAlignment( JLabel.CENTER );
	    }
	    @Override
	    public Component getTableCellRendererComponent(JTable table, Object value,
	            boolean isSelected, boolean hasFocus, int row, int column) {
	        setText(value.toString());
	        return this;
	    }
	}
	/**
	 * No action.
	 */
	@Override
	public void close() {
		Entigrator entigrator=console.getEntigrator(entihome$);
		//entity=entigrator.getEntityAtKey(entityKey$);
		if(!entigrator.lock_release(entity))
			JOptionPane.showMessageDialog(this, Entigrator.LOCK_CLOSE_MESSAGE);
		
	}
	/**
	 * Get context subtitle.
	 * @return the context subtitle.
	 */		
	@Override
	public String getSubtitle() {
		// TODO Auto-generated method stub
		return entityKey$;
	}
}
