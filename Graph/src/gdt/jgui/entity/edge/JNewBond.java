package gdt.jgui.entity.edge;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import java.util.Properties;
import java.util.Stack;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import gdt.data.entity.BaseHandler;
import gdt.data.entity.BondDetailHandler;
import gdt.data.entity.EntityHandler;
import gdt.data.entity.NodeHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Identity;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JContext;

import gdt.jgui.console.JMainConsole;
import gdt.jgui.entity.bonddetail.JBondDetailFacetOpenItem;
import gdt.jgui.entity.node.JNodeFacetAddItem;
import gdt.jgui.entity.node.JNodeFacetOpenItem;
import gdt.jgui.tool.AutocompleteJComboBox;


public class JNewBond extends JPanel implements JContext{
	private static final long serialVersionUID = 1L;
	String entihome$;
	JMainConsole console;
	String entityLabel$;
	String entityKey$;
	AutocompleteJComboBox nodeSelector;
	JTextField out=new JTextField();
	JTextField in=new JTextField();
	boolean debug=false;
	ArrayList<Core>bl=new ArrayList<Core>();;
	Stack<Core[]>undo=new Stack<Core[]>();
	JTextArea log;
	public JNewBond() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{100, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0};
		setLayout(gridBagLayout);
		
	}
	@Override
	public void activate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	
		@Override
		public JMenu getContextMenu() {
			JMenu menu=new JMenu("Context");
			
			JMenuItem undoItem = new JMenuItem("Undo");
			undoItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
                    if(undo.size()>0){
                    	bl = new ArrayList<Core>(Arrays.asList(undo.pop()));
                        display();
                    }
				}
			} );
			menu.add(undoItem);
			
			JMenuItem clearItem = new JMenuItem("Clear");
			clearItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
				    undo.clear();
				    bl.clear();
				    display();
				}
			} );
			menu.add(clearItem);
			JMenuItem doneItem = new JMenuItem("Done");
			doneItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					commit();
					JBondsPanel bp=new JBondsPanel();
					String bpLocator$=bp.getLocator();
					bpLocator$=Locator.append(bpLocator$, Entigrator.ENTIHOME,entihome$);
					bpLocator$=Locator.append(bpLocator$, EntityHandler.ENTITY_KEY,entityKey$);
					JConsoleHandler.execute(console, bpLocator$);
					//console.back();
				/*  
					try{
					  Entigrator entigrator=console.getEntigrator(entihome$);
					  Sack edge=entigrator.getEntityAtKey(entityKey$);
					  Core[] bonds=edge.elementGet("bond");
					  
					  
				  }catch(Exception ee){
					  Logger.getLogger(getClass().getName()).severe(ee.toString());
				  }
				  */
				}
			} );
			menu.add(doneItem);
		   return menu;
	}

	@Override
	public String getLocator() {
		Properties locator=new Properties();
	    locator.setProperty(Locator.LOCATOR_TYPE, JContext.CONTEXT_TYPE);
	    locator.setProperty(Locator.LOCATOR_TITLE, getTitle());
	    locator.setProperty(JContext.CONTEXT_TYPE,getType());
	    if(entihome$!=null)
		       locator.setProperty(Entigrator.ENTIHOME,entihome$);
	   locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
	   locator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
	   locator.setProperty( Locator.LOCATOR_ICON_CONTAINER, Locator.LOCATOR_ICON_CONTAINER_CLASS);
		locator.setProperty( Locator.LOCATOR_ICON_CLASS, getClass().getName());
		locator.setProperty( Locator.LOCATOR_ICON_FILE, "bonds.png");
		locator.setProperty( Locator.LOCATOR_ICON_CLASS_LOCATION,BondDetailHandler.EXTENSION_KEY);
	   
	   return Locator.toString(locator);
	}

	@Override
	public JPanel getPanel() {
		return this;
	}

	@Override
	public String getSubtitle() {

		return entityLabel$;
	}

	@Override
	public String getTitle() {
		
		return "Add bonds";
	}

	@Override
	public String getType() {
		return "add bonds";
	}

	@Override
	public JContext instantiate(JMainConsole console, String locator$) {
		if(debug)
			System.out.println("JNewBond:instantiate:locator="+locator$);
		this.console=console;
		 Properties locator=Locator.toProperties(locator$);
		 entihome$=locator.getProperty(Entigrator.ENTIHOME);
		 entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
		 Entigrator entigrator=console.getEntigrator(entihome$);
		 entityLabel$=entigrator.indx_getLabel(entityKey$);
		 try{
			 removeAll(); 
			 //Entigrator entigrator=console.getEntigrator(entihome$);
			 //entigrator.store_refresh();
			 String[] sa=entigrator.indx_listEntitiesAtPropertyName("node");
			 ArrayList<String>sl=new ArrayList<String>();
			 String[] labels=new String[]{};
			 String label$;
			 if(sa!=null&&sa.length>0){
				 for(String s:sa){
				 label$=entigrator.indx_getLabel(s);
				 if(label$!=null&&!sl.contains(label$))
					 sl.add(label$);
				 }
				 Collections.sort(sl);
				 labels=sl.toArray(new String[0]);
			 }
			 nodeSelector = new AutocompleteJComboBox(labels);
			 	GridBagConstraints gbc_nodeLabel = new GridBagConstraints();
				gbc_nodeLabel.insets = new Insets(5, 5, 5, 5);
				gbc_nodeLabel.gridx = 0;
				gbc_nodeLabel.gridy = 0;
				gbc_nodeLabel.anchor=GridBagConstraints.FIRST_LINE_START;
				JLabel nodeLabel=new JLabel("Node");
				add(nodeLabel, gbc_nodeLabel);
				GridBagConstraints gbc_comboBox = new GridBagConstraints();
				gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
				gbc_comboBox.insets = new Insets(0, 0, 5, 5);
				gbc_comboBox.gridx = 1;
				gbc_comboBox.gridy = 0;
				gbc_comboBox.anchor=GridBagConstraints.FIRST_LINE_START;
				add(nodeSelector, gbc_comboBox);
				
				GridBagConstraints gbc_outButton = new GridBagConstraints();
				gbc_outButton.insets = new Insets(5, 5, 5, 5);
				gbc_outButton.gridx = 0;
				gbc_outButton.gridy = 1;
				gbc_outButton.anchor=GridBagConstraints.FIRST_LINE_START;
				JButton outButton=new JButton("Out");
				add(outButton, gbc_outButton);
				outButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
					     out.setText((String)nodeSelector.getSelectedItem());
					}
				});
				GridBagConstraints gbc_outField = new GridBagConstraints();
				//gbc_outField.fill = GridBagConstraints.HORIZONTAL;
				gbc_outField.insets = new Insets(0, 0, 5, 5);
				gbc_outField.weighty = 0.0;
				gbc_outField.gridx = 1;
				gbc_outField.gridy = 1;
				gbc_outField.anchor=GridBagConstraints.FIRST_LINE_START;
				gbc_outField.fill = GridBagConstraints.BOTH;
				out=new JTextField();
				add(out, gbc_outField);
				
				GridBagConstraints gbc_inButton = new GridBagConstraints();
				gbc_inButton.insets = new Insets(5, 5, 5, 5);
				gbc_inButton.gridx = 0;
				gbc_inButton.gridy = 2;
				gbc_inButton.anchor=GridBagConstraints.FIRST_LINE_START;
				JButton inButton=new JButton("In");
				add(inButton, gbc_inButton);
				inButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
					     in.setText((String)nodeSelector.getSelectedItem());
					}
				});
				GridBagConstraints gbc_inField = new GridBagConstraints();
				//gbc_outField.fill = GridBagConstraints.HORIZONTAL;
				gbc_inField.insets = new Insets(0, 0, 5, 5);
				gbc_inField.weighty = 0.0;
				gbc_inField.gridx = 1;
				gbc_inField.gridy = 2;
				gbc_inField.anchor=GridBagConstraints.FIRST_LINE_START;
				gbc_inField.fill = GridBagConstraints.BOTH;
				in=new JTextField();
				add(in, gbc_inField);
				
				GridBagConstraints gbc_addButton = new GridBagConstraints();
				gbc_addButton.insets = new Insets(5, 5, 5, 5);
				gbc_addButton.gridx = 0;
				gbc_addButton.gridy = 3;
				gbc_addButton.anchor=GridBagConstraints.FIRST_LINE_START;
				JButton addButton=new JButton("Add");
				add(addButton, gbc_addButton);
				addButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						if(out.getText()==null|in.getText()==null)
							return;
					if(bl!=null)
						undo.push(bl.toArray(new Core[0]));
					Core bond= new Core(out.getText(),Identity.key(),in.getText());
					 if(!contains(bond,bl))
						 bl.add(bond);
					 display();
					}
				});
				
				log = new JTextArea();
				GridBagConstraints gbc_panel = new GridBagConstraints();
				gbc_panel.weighty = 1.0;
				gbc_panel.weightx = 0.0;
				gbc_panel.gridwidth=2;
				gbc_panel.insets = new Insets(0, 0, 0, 5);
				gbc_panel.fill = GridBagConstraints.BOTH;
				gbc_panel.gridx =0;
				gbc_panel.gridy = 4;
				add(log, gbc_panel); 
		 }catch(Exception e){
			 Logger.getLogger(getClass().getName()).severe(e.toString());
		 }
	 return this;
	}
	private boolean contains(Core candidate, ArrayList<Core>cl){
		try{
			for(Core c:cl)
				if(c.type.equals(candidate.type)&&c.value.equals(candidate.value))
					return true;
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		}
		return false;
	}
	private void display(){
		log.setText(null);
		StringBuffer sb=new StringBuffer();
		if(bl==null||bl.size()<1)
			return;
		for(Core c:bl)
			sb.append(c.type+" > "+c.value+"\n");
		log.setText(sb.toString());
	}
	private void commit(){
		if(bl.size()<1)
			return;
		try{
		String inKey$;
		String outKey$;
		Entigrator entigrator=console.getEntigrator(entihome$);
		Sack edge=entigrator.getEntityAtKey(entityKey$);
		Core[] ba=edge.elementGet("bond");
		Sack outNode;
		Sack inNode;
		Core bond;
		boolean skip=false;
		for( Core c:bl){
			if(c.type==null||c.value==null)
				continue;
			outKey$=entigrator.indx_keyAtLabel(c.type);
			if(outKey$==null)
				continue;
			inKey$=entigrator.indx_keyAtLabel(c.value);
			if(inKey$==null)
				continue;
			skip=false;
			if(ba!=null){
				for(Core b:ba){
					if(outKey$.equals(b.type)&&inKey$.equals(b.value)){
						skip=true;
						break;
					}
				}
			}else
				edge.createElement("bond");
			if(skip)
				continue;
			
			outNode=entigrator.getEntityAtKey(outKey$);
			inNode=entigrator.getEntityAtKey(inKey$);
			if(inNode==null||outNode==null){
				System.out.println("JNewBond:commit:broken bond in="+inKey$+" out="+outKey$);
				continue;
			}
			bond=new Core(outKey$,c.name,inKey$);
			edge.putElementItem("bond",bond);
			
			if(!outNode.existsElement("bond"))
				outNode.createElement("bond");
			outNode.putElementItem("bond", bond);
			if(!outNode.existsElement("edge"))
				outNode.createElement("edge");
			outNode.putElementItem("edge", new Core(null,bond.name,entityKey$));
			if(!inNode.existsElement("bond"))
				inNode.createElement("bond");
			inNode.putElementItem("bond", bond);
			if(!inNode.existsElement("edge"))
				inNode.createElement("edge");
			inNode.putElementItem("edge", new Core(null,bond.name,entityKey$));
			
			if(!outNode.existsElement("fhandler"))
				outNode.createElement("fhandler");
			outNode.putElementItem("fhandler", new Core(null, BondDetailHandler.class.getName(),BondDetailHandler.EXTENSION_KEY));
			if(!inNode.existsElement("fhandler"))
				inNode.createElement("fhandler");
			inNode.putElementItem("fhandler", new Core(null, BondDetailHandler.class.getName(),BondDetailHandler.EXTENSION_KEY));

			if(!outNode.existsElement("jfacet"))
				outNode.createElement("jfacet");
			outNode.putElementItem("jfacet", new Core(null, BondDetailHandler.class.getName(),JBondDetailFacetOpenItem.class.getName()));
			if(!inNode.existsElement("jfacet"))
				inNode.createElement("jfacet");
			inNode.putElementItem("jfacet", new Core(null,BondDetailHandler.class.getName(),JBondDetailFacetOpenItem.class.getName()));
			
			entigrator.replace(outNode);
			entigrator.replace(inNode);
		}
		entigrator.replace(edge);
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
		}
		
	}
	/*
	private static class BondComparator implements Comparator<Core>{
	    @Override
	    public int compare(Core o1, Core o2) {
	    	try{
	    		String l1$=o1.type;
	    		String l2$=o2.type;
	    		return l1$.compareToIgnoreCase(l2$);
	    	}catch(Exception e){
	    		return 0;
	    	}
	    }
		
	}
	*/
}
