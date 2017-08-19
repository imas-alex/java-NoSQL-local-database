package gdt.jgui.entity.fields;
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
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Stack;
import java.util.logging.Logger;

import gdt.data.entity.BaseHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.facet.FieldsHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Identity;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;
import gdt.jgui.console.JFacetOpenItem;
import gdt.jgui.console.JFacetRenderer;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.console.JRequester;
import gdt.jgui.console.ReloadDialog;
import gdt.jgui.entity.JEntityFacetPanel;
import gdt.jgui.entity.JEntityPrimaryMenu;
import gdt.jgui.entity.JReferenceEntry;
import gdt.jgui.tool.JTextEditor;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.BoxLayout;

import org.apache.commons.codec.binary.Base64;
/**
 * This context provides the convenient tool to manage
 * a set of fields assigned to the entity.
 * @author imasa
 */
public class JFieldsEditor extends JPanel implements JFacetRenderer,JRequester
{
	private static final long serialVersionUID = 1L;
	protected Logger LOGGER=Logger.getLogger(JFieldsEditor.class.getName());
	public static final String CELL_FIELD_NAME="cell field name";
	public static final String CELL_FIELD_VALUE="cell field value";
	public static final String CELL_FIELD="cell field";
	public static final String ACTION_COPY_FIELDS="action copy fields";
	public static final String ACTION_CUT_FIELDS="action cut fields";
	public static final String ACTION_NEW_ENTITY="action new entity";
	public static final String LOCATOR_TYPE_FIELD="locator type field";
	//public static final String RELOAD_ENTITY="reload entity";
protected String entihome$;
protected String entityKey$;
protected String entityLabel$;
protected String requesterResponseLocator$;
protected String text$;
protected JTable table;
protected JScrollPane scrollPane;
protected Sack entity;
protected Entigrator entigrator;
protected JMainConsole console;
protected JMenu menu;
protected JMenuItem deleteItemsItem;
protected JMenuItem editCellItem;
protected JMenuItem copyItem;
protected JMenuItem cutItem;
protected JMenuItem pasteItem;
protected JMenuItem doneItem;
protected JMenuItem[] postMenu;
protected String message$;
boolean debug=true;
protected boolean ignoreOutdate=false;
/**
 * The default constructor.
 */
public JFieldsEditor() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		scrollPane = new JScrollPane();
		add(scrollPane);
	}
/**
 * Get the panel to insert into the main console.
 * @return this panel.
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
//			System.out.println("FieldsEditor:getConextMenu:menu selected");
			
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
				editCellItem = new JMenuItem("Edit item");
				editCellItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try{
						JTextEditor textEditor=new JTextEditor();
						String locator$=textEditor.getLocator();
						locator$=Locator.append(locator$, Entigrator.ENTIHOME, entihome$);
						locator$=Locator.append(locator$, EntityHandler.ENTITY_KEY, entityKey$);
						String responseLocator$=getEditCellLocator();
						text$=Locator.getProperty(responseLocator$, JTextEditor.TEXT);
						locator$=Locator.append(locator$, JTextEditor.TEXT, text$);
//						System.out.println("FieldsEditor:edit cell:text="+text$);
		                locator$=Locator.append(locator$,JRequester.REQUESTER_RESPONSE_LOCATOR, Locator.compressText(responseLocator$));				
   					   JConsoleHandler.execute(console, locator$);
						}catch(Exception ee){
							LOGGER.severe(ee.toString());
						}
					}
				} );
				menu.add(editCellItem);
				}
			if(hasSelectedRows()){
			    deleteItemsItem = new JMenuItem("Delete items");
			    deleteItemsItem.addActionListener(new ActionListener() {
				   @Override
				   public void actionPerformed(ActionEvent e) {
					   int response = JOptionPane.showConfirmDialog(JFieldsEditor.this, "Delete selected fields ?", "Confirm",
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
					copy(false);
				}
			} );
			menu.add(copyItem);
			cutItem = new JMenuItem("Cut");
			cutItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
				   copy(true);
				}
			} );
			menu.add(cutItem);
			}
			if(hasFieldsToPaste()){
				pasteItem = new JMenuItem("Paste");
				pasteItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try{
						String[] sa=console.clipboard.getContent();
						Properties locator;
						String type$;
						String sourceEntityKey$;
						Sack sourceEntity;
						String sourceEntihome$;
						Entigrator sourceEntigrator;
						for(String aSa:sa){
							//System.out.println("FieldsEditor:paste:"+aSa);
							locator=Locator.toProperties(aSa);
							type$=locator.getProperty(Locator.LOCATOR_TYPE);
							if(LOCATOR_TYPE_FIELD.equals(type$)){
								String action$=locator.getProperty(JRequester.REQUESTER_ACTION);
								String name$=locator.getProperty(CELL_FIELD_NAME);
								String value$=locator.getProperty(CELL_FIELD_VALUE);
								if(name$!=null){
								if(ACTION_COPY_FIELDS.equals(action$))
									entity.putElementItem("field", new Core(null,name$,value$));
								if(ACTION_CUT_FIELDS.equals(action$)){
									entity.putElementItem("field", new Core(null,name$,value$));	
									sourceEntihome$=locator.getProperty(Entigrator.ENTIHOME);
									sourceEntigrator=console.getEntigrator(sourceEntihome$);
									sourceEntityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
									sourceEntity=sourceEntigrator.getEntityAtKey(sourceEntityKey$);
									sourceEntity.removeElementItem("field", name$);
									sourceEntigrator.ent_replace(sourceEntity);
								}
							}
						}
						}
						entigrator.ent_replace(entity);
						Core[] ca=entity.elementGet("field");
						replaceTable(ca);
						}catch(Exception ee){
							LOGGER.info(ee.toString());
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
		doneItem = new JMenuItem("Done");
		doneItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
				if(requesterResponseLocator$!=null){
					try{
					   
						byte[] ba=Base64.decodeBase64(requesterResponseLocator$);
					   String responseLocator$=new String(ba,"UTF-8");
					   responseLocator$=Locator.append(responseLocator$, Entigrator.ENTIHOME, entihome$);
					   responseLocator$=Locator.append(responseLocator$, EntityHandler.ENTITY_KEY, entityKey$);
					//  System.out.println("FieldsEditor:done.response locator="+responseLocator$);
					   JConsoleHandler.execute(console, responseLocator$);
						}catch(Exception ee){
							LOGGER.severe(ee.toString());
						}
				}else
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
		if(postMenu!=null){
			for(JMenuItem jmi:postMenu)
				menu.add(jmi);
		}
		return menu;
		}
/**
 * Get the context locator.
 * @return the locator string.
 */
	@Override
	public String getLocator() {
		try{
			Properties locator=new Properties();
			locator.setProperty(BaseHandler.HANDLER_CLASS,JFieldsEditor.class.getName());
			locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
			 locator.setProperty( JContext.CONTEXT_TYPE,getType());
			locator.setProperty(Locator.LOCATOR_TITLE,getTitle());
			if(entityLabel$!=null){
				locator.setProperty(EntityHandler.ENTITY_LABEL,entityLabel$);
			}
			if(entityKey$!=null)
				locator.setProperty(EntityHandler.ENTITY_KEY,entityKey$);
			if(entihome$!=null)
				locator.setProperty(Entigrator.ENTIHOME,entihome$);
			locator.setProperty(Locator.LOCATOR_ICON_CONTAINER,Locator.LOCATOR_ICON_CONTAINER_CLASS);
			locator.setProperty(Locator.LOCATOR_ICON_CLASS,getClass().getName());
			locator.setProperty(Locator.LOCATOR_ICON_FILE,"fields.png");   
			return Locator.toString(locator);
			}catch(Exception e){
	        LOGGER.severe(e.toString());
	        return null;
			}
	}
	/**
	 * Create a new facet renderer.
	 * @param console the main console.
	 * @param locator$ the locator string.
	 * @return the fields editor.
	 */
	@Override
	public JFacetRenderer instantiate(JMainConsole console, String locator$) {
		try{
			if(debug)
				System.out.println("FieldsEditor.instantiate:locator="+locator$);
			this.console=console;
			Properties locator=Locator.toProperties(locator$);
			entihome$=locator.getProperty(Entigrator.ENTIHOME);
			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			if(entityKey$==null){
				return null;
			}
			
			requesterResponseLocator$=locator.getProperty(JRequester.REQUESTER_RESPONSE_LOCATOR);
			table = new JTable();
			  DefaultTableModel model=new DefaultTableModel(
					  null
					  ,
						new String[] {
							"Name", "Value"
						}
					);
			  entigrator=console.getEntigrator(entihome$);
			  entityLabel$=entigrator.indx_getLabel(entityKey$);
				if(Locator.LOCATOR_TRUE.equals(locator.getProperty(JFacetRenderer.ONLY_ITEM)))
					 return this;
			    entity=entigrator.ent_getAtKey(entityKey$);
			  entityLabel$=entity.getProperty("label");
			  Core[] ca=entity.elementGet("field");
			  if(ca!=null)
				  for(Core aCa:ca){
					  model.addRow(new String[]{aCa.name,aCa.value});
					 
				  }
			  table.setModel(model);
			  scrollPane.setViewportView(table);
			  table.getTableHeader().setDefaultRenderer(new SimpleHeaderRenderer());
			  table.getTableHeader().addMouseListener(new MouseAdapter() {
				    @Override
				    public void mouseClicked(MouseEvent e) {
				        int col = table.columnAtPoint(e.getPoint());
				        String name = table.getColumnName(col);
				       // System.out.println("Column index selected " + col + " " + name);
				        sort(name);
				    }
				});
		}catch(Exception e){
	        LOGGER.severe(e.toString());
			
			}
		
		return this;
		
	}
	protected void sort(String header$){
		try{
//			System.out.println("EntityEditor.sort:header="+header$);
			Core[] ca=entity.elementGet("field");
		 if ("Name".equals(header$))
				ca=Core.sortAtName(ca);
			else if ("Value".equals(header$))
				ca=Core.sortAtValue(ca);
			replaceTable(ca);
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
	}
	protected void replaceTable( Core[] ca){
		try{
			table=(JTable)scrollPane.getViewport().getView();
			DefaultTableModel model=(DefaultTableModel)table.getModel();
			while(model.getRowCount()>0)
				model.removeRow(0);
			int cnt=ca.length;
			if(cnt<1)
				return;
			for(int i=0;i<cnt;i++)
				model.addRow(new String[]{ca[i].name,ca[i].value});
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
	}
/**
 * Get the context title.
 * @return the title of the context.
 */
	@Override
	public String getTitle() {
			return "Fields";
	
	}
	/**
	 * Get the context type.
	 * @return the type of the context.
	 */
	@Override
	public String getType() {
		return "Fields editor";
	}
/**
 * Complete the context. No action.
 */
	@Override
	public void close() {
		activate();	
	}
	protected boolean hasEditingCell(){
		try{
			
			JTable table=(JTable)scrollPane.getViewport().getView();
			//System.out.println("Entityeditor:hasEditingCell:x="+table.getEditingRow()+" y="+table.getEditingColumn());
			if(table.getEditingColumn()>-1&&table.getEditingRow()>-1)
				return true;
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
		return false;
	}
	protected String getEditCellLocator() {
        try{
		save();
        String locator$=getLocator();
       if(entihome$!=null)
        locator$=Locator.append(locator$ ,Entigrator.ENTIHOME,entihome$);
       if(entityKey$!=null)
           locator$=Locator.append(locator$ ,EntityHandler.ENTITY_KEY,entityKey$);
		JTable table=(JTable)scrollPane.getViewport().getView();
		int x=table.getEditingRow();
		int y=table.getEditingColumn();
		String cellField$=CELL_FIELD_NAME;
		if(y==1)
			cellField$=CELL_FIELD_VALUE;
		TableModel model=table.getModel();
		text$=(String)model.getValueAt(x, y);
		String fieldName$=(String)model.getValueAt(x, 0);
        locator$=Locator.append(locator$ ,JTextEditor.TEXT,text$);
        locator$=Locator.append(locator$ ,CELL_FIELD,cellField$);
        locator$=Locator.append(locator$ ,CELL_FIELD_NAME,fieldName$);
        if(requesterResponseLocator$!=null)
        	locator$=Locator.append(locator$ ,JRequester.REQUESTER_RESPONSE_LOCATOR,requesterResponseLocator$);
         locator$=Locator.append(locator$,BaseHandler.HANDLER_METHOD,JFacetOpenItem.METHOD_RESPONSE);
		// System.out.println("FieldsEditor:getEditCellLocator:END:"+locator$);
		 return locator$;
        }catch(Exception e){
        	LOGGER.severe(e.toString());
        	return null;
        }
	}
	protected void save(){
		try{
			int rCnt;
			Core row;
			TableModel model;
			table=(JTable)scrollPane.getViewport().getView();
			rCnt=table.getRowCount();
			model=table.getModel();
			entity.clearElement("field");
				for(int j=0;j<rCnt;j++){
					row=new Core(null,(String)model.getValueAt(j,0),(String)model.getValueAt(j,1));
					entity.putElementItem("field", row);
				}
		//	System.out.println("JFieldsEditor:save:finish");
			//entity.print();
				entity.putAttribute(new Core(null,Entigrator.TIMESTAMP,String.valueOf(System.currentTimeMillis())));	
				entity.putAttribute(new Core(null,Entigrator.SAVE_ID,Identity.key()));
				entigrator.ent_replace(entity);
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
	}
	protected boolean hasSelectedRows(){
		try{
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
	protected void addRow(){
		try{
			JTable table=(JTable)scrollPane.getViewport().getView();
			DefaultTableModel model=(DefaultTableModel)table.getModel();
			model.addRow(new String[]{"Name"+String.valueOf(model.getRowCount()),"Value"});
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
	}
	protected void deleteRows(){
		try{
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
					row =new Core(null,(String)tableModel.getValueAt(i, 0),(String)tableModel.getValueAt(i, 1));
					ol.add(row);
				}
			}
			Core[] ra=ol.toArray(new Core[0]);
			while(tableModel.getRowCount()>0)
				tableModel.removeRow(0);
			for(Core aRa:ra){
				 tableModel.addRow(new String[]{aRa.name,aRa.value});
			}
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
	}
	protected void copy(boolean cut){
		try{
		JTable table=(JTable)scrollPane.getViewport().getView();
		DefaultTableModel model=(DefaultTableModel)table.getModel();
		ListSelectionModel listModel=table.getSelectionModel();
		int rCnt=table.getRowCount();
		ArrayList<Integer>srl=new ArrayList<Integer>();
		for(int i=0;i<rCnt;i++)
			if(listModel.isSelectedIndex(i)){
				srl.add(new Integer(i));
			}
		Integer[] sra=srl.toArray(new Integer[0]);
		ArrayList<String> sl=new ArrayList<String>();
		Properties locator=new Properties();
		locator.setProperty(Locator.LOCATOR_TYPE, LOCATOR_TYPE_FIELD);
		if(cut)
		  locator.setProperty(JRequester.REQUESTER_ACTION, ACTION_CUT_FIELDS);
		else
		  locator.setProperty(JRequester.REQUESTER_ACTION, ACTION_COPY_FIELDS);
		locator.setProperty(Entigrator.ENTIHOME, entihome$);
		locator.setProperty(EntityHandler.ENTITY_KEY, entityKey$);
		String locator$;
		console.clipboard.clear();
		for(int i=0;i<rCnt;i++){
			for(int aSra:sra){
				if(i==aSra){
					locator.setProperty(CELL_FIELD_NAME, (String)model.getValueAt(i, 0));
					locator.setProperty(Locator.LOCATOR_TITLE, (String)model.getValueAt(i, 0));
					locator.setProperty(CELL_FIELD_VALUE, (String)model.getValueAt(i, 1));
				    locator$=Locator.toString(locator);
//				    System.out.println("FieldsEditor:copy:locator="+locator$);
				    console.clipboard.putString(locator$);
				}
			}
		}
	}catch(Exception e){
		LOGGER.severe(e.toString());
	}
	}
	protected boolean hasFieldsToPaste(){
		try{
			String[]sa= console.clipboard.getContent();
			
			if(sa!=null){
				Properties locator;
				String type$;
				String action$;
				for(String aSa:sa){
					locator=Locator.toProperties(aSa);
					type$=locator.getProperty(Locator.LOCATOR_TYPE);
					if(LOCATOR_TYPE_FIELD.equals(type$)){
						action$=locator.getProperty(JRequester.REQUESTER_ACTION);
						if(ACTION_COPY_FIELDS.equals(action$)
								||ACTION_CUT_FIELDS.equals(action$))
							return true;
					}
				}
			}
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
		return false;
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
	 * Execute the response locator.
	 * @param console the main console.
	 * @param locator$ the response locator.
	 */
	@Override
	public void response(JMainConsole console, String locator$) {
		//System.out.println("FieldsEditor:response:"+Locator.remove(locator$,Locator.LOCATOR_ICON ));
		try{
			Properties locator=Locator.toProperties(locator$);
			String action$=locator.getProperty(JRequester.REQUESTER_ACTION);
			entihome$=locator.getProperty(Entigrator.ENTIHOME);
			Entigrator entigrator=console.getEntigrator(entihome$);
			String text$=locator.getProperty(JTextEditor.TEXT);
			if(ACTION_NEW_ENTITY.equals(action$)){
				if(debug)
				System.out.println("JFieldsEditor:response:new entity");
				Sack newEntity=entigrator.ent_new("fields", text$);
				
				newEntity.createElement("field");
				newEntity.putElementItem("field", new Core(null,"Name","Value"));
				newEntity.createElement("fhandler");
				newEntity.putElementItem("fhandler", new Core(null,FieldsHandler.class.getName(),null));
				newEntity.putAttribute(new Core (null,"icon","fields.png"));
				//newEntity.print();
				System.out.println("JFieldsEditor:response:1");
				entigrator.ent_replace(newEntity);
					
				String icons$=entihome$+"/"+Entigrator.ICONS;
				Support.addHandlerIcon(getClass(), "fields.png", icons$);
				System.out.println("JFieldsEditor:response:2");
				newEntity=entigrator.ent_reindex(newEntity);
				//newEntity.print();
				JEntityFacetPanel efp=new JEntityFacetPanel(); 
				String efpLocator$=efp.getLocator();
				efpLocator$=Locator.append(efpLocator$,Locator.LOCATOR_TITLE,newEntity.getProperty("label"));
				efpLocator$=Locator.append(efpLocator$, Entigrator.ENTIHOME, entihome$);
				efpLocator$=Locator.append(efpLocator$, EntityHandler.ENTITY_KEY, newEntity.getKey());
				efpLocator$=Locator.append(efpLocator$, EntityHandler.ENTITY_LABEL, newEntity.getProperty("label"));
				JEntityPrimaryMenu.reindexEntity(console, efpLocator$);
				
				Stack<String> s=console.getTrack();
				s.pop();
				console.setTrack(s);
				JConsoleHandler.execute(console, efpLocator$);
				//entigrator.store_replace();
				return;
			}
				String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
				Sack entity=entigrator.getEntityAtKey(entityKey$);
				String cellField$=locator.getProperty(CELL_FIELD);
				String name$=locator.getProperty(CELL_FIELD_NAME);
				Core core=entity.getElementItem("field", name$);
				if(CELL_FIELD_NAME.equals(cellField$))
					core.name=text$;
				else if (CELL_FIELD_VALUE.equals(cellField$))
					core.value=text$;
//				System.out.println("FieldsEditor:response:name="+core.name+" value="+core.value);
				entity.putElementItem("field", core);
				System.out.println("JFieldsEditor:response:3");
				entigrator.ent_replace(entity);
				String feLocator$=getLocator();
				feLocator$=Locator.append(locator$, Entigrator.ENTIHOME, entihome$);
				feLocator$=Locator.append(locator$, EntityHandler.ENTITY_KEY, entityKey$);
				feLocator$=Locator.remove(feLocator$, BaseHandler.HANDLER_METHOD);
				JConsoleHandler.execute(console, feLocator$);
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
	}
/**
 * Add renderer's icon to the locator.
 * @param locator$ the origin locator.
 * @return the locator with the icon added.
 */
	@Override
	public String addIconToLocator(String locator$) {
	    	return locator$;
	}
	/**
	 * Get facet handler class name.
	 * @return facet handler class name.
	 */
	@Override
	public String getFacetHandler() {
		return FieldsHandler.class.getName();
	}
/**
 * Get context subtitle.
 * @return the context subtitle.
 */
	@Override
	public String getSubtitle() {
			return entityLabel$;
	}
	/**
	 * Adapt cloned entity.
	 * @param console the main console.
	 * @param locator$ the locator string.
	 * 
	 */
	@Override
	public void adaptClone(JMainConsole console, String locator$) {
		try{
//			System.out.println("FieldsEditor:adaptClone:locator="+locator$);
			Properties locator=Locator.toProperties(locator$);
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			Entigrator entigrator=console.getEntigrator(entihome$);
			Sack entity=entigrator.getEntityAtKey(entityKey$);
			String entityLocator$=EntityHandler.getEntityLocator(entigrator, entity);
			FieldsHandler fieldsHandler=new FieldsHandler();
			fieldsHandler.instantiate(entityLocator$);
			fieldsHandler.adaptClone(entigrator);
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
	}
	/**
	 * Adapt renamed entity.
	 * @param console the main console.
	 * @param locator$ the locator string.
	 * 
	 */
	@Override
	public void adaptRename(JMainConsole console, String locator$) {
		try{
			if(debug)
			System.out.println("JFieldsEditor:adaptRename:locator="+locator$);
			Properties locator=Locator.toProperties(locator$);
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			String entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			Entigrator entigrator=console.getEntigrator(entihome$);
			Sack entity=entigrator.ent_getAtKey(entityKey$);
			String entityLocator$=EntityHandler.getEntityLocator(entigrator, entity);
			FieldsHandler fieldsHandler=new FieldsHandler();
			fieldsHandler.instantiate(entityLocator$);
			fieldsHandler.adaptRename(entigrator);
		}catch(Exception e){
			LOGGER.severe(e.toString());
		}
	}
	/**
	 * Rebuild entity parameters and indexes.
	 * @param console the main console.
	 * @param entigrator the entigrator.
	 * @param entity the entity.
	 */
	@Override
	public void reindex(JMainConsole console, Entigrator entigrator, Sack entity) {
	    try{
	    //	System.out.println("JFieldsEditor:reindex:entity="+entity.getProperty("label"));
	    	
	    	String fhandler$=FieldsHandler.class.getName();
	    	if(entity.getElementItem("fhandler", fhandler$)!=null){
				entity.putElementItem("jfacet", new Core(JFieldsFacetAddItem.class.getName(),fhandler$,JFieldsFacetOpenItem.class.getName()));
				entigrator.ent_replace(entity);
			}
	    	String entityLocator$=EntityHandler.getEntityLocator(entigrator, entity);
			FieldsHandler fieldsHandler=new FieldsHandler();
			fieldsHandler.instantiate(entityLocator$);
			fieldsHandler.adaptRename(entigrator);
	    	adaptRename(console, entityLocator$);
	    }catch(Exception e){
	    	LOGGER.severe(e.toString());
	    }
	}
	/**
	 * Get entity type.
	 * @return the type of the entity.
	 */
	@Override
	public String getEntityType() {
		return "fields";
	}
	/**
	 * Get category icon as a Base64 string.
	 * @return the category icon string. 
	 */
	@Override
	public String getCategoryIcon(Entigrator entigrator) {
		return Support.readHandlerIcon(null,getClass(), "fields.png");
	}
	/**
	 * Get category title.
	 * @return the category title.
	 */
	@Override
	public String getCategoryTitle() {
		return "Fields";
	}
	/**
	 * Create a new entity of the facet type.
	 * @param console the main console.
	 * @param locator$ the locator string.
	 * @return the key of the new entity.
	 */
	@Override
	public String newEntity(JMainConsole console, String locator$) {
		JTextEditor textEditor=new JTextEditor();
	    String editorLocator$=textEditor.getLocator();
	    editorLocator$=Locator.append(editorLocator$, JTextEditor.TEXT, "Fields"+Identity.key().substring(0,4));
	    editorLocator$=Locator.append(editorLocator$,Locator.LOCATOR_TITLE,"Fields entity");
	    entihome$=Locator.getProperty(locator$,Entigrator.ENTIHOME );
	    if(entihome$!=null)
	    	editorLocator$=Locator.append(editorLocator$,Entigrator.ENTIHOME,entihome$);
	    //String icon$=Support.readHandlerIcon(null,getClass(), "fields.png");
	    //editorLocator$=Locator.append(editorLocator$,Locator.LOCATOR_ICON,icon$);
	    JFieldsEditor fe=new JFieldsEditor();
	    String feLocator$=fe.getLocator();
	    Properties responseLocator=Locator.toProperties(feLocator$);
	    if(entihome$!=null)
	      responseLocator.setProperty(Entigrator.ENTIHOME,entihome$);
	      responseLocator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
		responseLocator.setProperty(BaseHandler.HANDLER_METHOD,"response");
		responseLocator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
		responseLocator.setProperty(BaseHandler.HANDLER_METHOD,"response");
		responseLocator.setProperty(JRequester.REQUESTER_ACTION,ACTION_NEW_ENTITY);
		responseLocator.setProperty(Locator.LOCATOR_TITLE,"Fields");
		 String responseLocator$=Locator.toString(responseLocator);
    	//System.out.println("FieldsEditor:newEntity:responseLocator:=:"+responseLocator$);
		String requesterResponseLocator$=Locator.compressText(responseLocator$);
		editorLocator$=Locator.append(editorLocator$,JRequester.REQUESTER_RESPONSE_LOCATOR,requesterResponseLocator$);
		JConsoleHandler.execute(console,editorLocator$); 
		return editorLocator$;
	}
	/**
	 * Add referenced entities. No action.
	 */
	@Override
	public void collectReferences(Entigrator entigrator, String entiyKey$, ArrayList<JReferenceEntry> sl) {
		// TODO Auto-generated method stub
	}
	protected String getField(String fieldName$){
	try{
		return entity.getElementItemAt("field", fieldName$);
	}catch(Exception e){
		LOGGER.severe(e.toString());
		return null;
	}
	}
	@Override
	public void activate() {
		if(debug)
			System.out.println("JFieldsEditor:activate:locator="+getLocator());
		if(entity==null)
			return;
		
		if(ignoreOutdate){
			ignoreOutdate=false;
			return;
		}
		System.out.println("JFieldsEditor:activate:outdated treatment="+console.outdatedTreatment$);
		if(console.outdatedTreatment$!=null){
			if(JContext.OUTDATED_CANCEL.equals(console.outdatedTreatment$)){
				console.outdatedTreatment$=null;
				return;
			}
			if(JContext.OUTDATED_RELOAD.equals(console.outdatedTreatment$)){
				entity=entigrator.ent_reload(entityKey$);
				instantiate(console,getLocator());
				console.outdatedTreatment$=null;
				return;
			}
			if(JContext.OUTDATED_REPLACE.equals(console.outdatedTreatment$)){
				entity.putAttribute(new Core(null,Entigrator.SAVE_ID,Identity.key()));
				save();
				console.outdatedTreatment$=null;
				return;
			}
		}
		if(!entigrator.ent_entIsObsolete(entity))
			return;
		int n=new ReloadDialog(this).show();
		if(2==n){
			ignoreOutdate=true;
		
			return;
		}
		if(1==n){
			entity.putAttribute(new Core(null,Entigrator.SAVE_ID,Identity.key()));
			save();
			
		}
		if(0==n){
				entity=entigrator.ent_reload(entityKey$);
				instantiate(console,getLocator());
			}
	
	}
	@Override
	public String getFacetOpenItem() {
		return JFieldsFacetOpenItem.class.getName();
	}
	@Override
	public String getFacetIcon() {
		
		return "fields.png";
	}
		
}
