package gdt.jgui.console;
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
import gdt.data.grain.Locator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Stack;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JMenu;
/**
 * This class displays the list of previously shown contexts.
 * 
 */

public class JTrackPanel extends JItemsListPanel {
	
	private static final long serialVersionUID = 1L;
	private final static String TRACK="track";
	private Stack<String>track;
	private Logger LOGGER=Logger.getLogger(JTrackPanel.class.getName());
	/**
	 * The constructor.
	 * @param console the main console.
	 */
	public JTrackPanel(JMainConsole console){
		super();
		this.track=console.getTrack();
		JItemPanel[] ipl=listMembers(console);
		putItems(ipl);
	
	}
	/**
	 * The default constructor.
	 */
	public JTrackPanel(){
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}
	/**
	 * Get context menu.
	 * @return null.
	 */
	@Override
	public JMenu getContextMenu() {
		return null;
	}
	/**
	 * Get context locator.
	 * @return the track panel locator.
	 */
	@Override
	public String getLocator() {
		 Properties locator=new Properties();
		    locator.setProperty(Locator.LOCATOR_TYPE, JContext.CONTEXT_TYPE);
		    locator.setProperty(Locator.LOCATOR_TITLE,TRACK);
		    locator.setProperty(BaseHandler.HANDLER_SCOPE,JConsoleHandler.CONSOLE_SCOPE);
		    locator.setProperty(BaseHandler.HANDLER_CLASS,JTrackPanel.class.getName());
			return Locator.toString(locator);
	}
/**
 * Create the track panel.
 * @param console the main console.
 * @param locator$ the track panel locator.
 * @return the  context.
 */
	@Override
	public JContext instantiate(JMainConsole console, String locator$) {
		try{
        	JItemPanel[] ipl=listMembers(console);
        	putItems(ipl);
        	return this;
        }catch(Exception e){
        LOGGER.severe(e.toString());
        return null;
        }
	}
private JItemPanel[] listMembers(JMainConsole console){
	try{
		   track=console.getTrack();
		   if(track.isEmpty())
			   return null;
		   //System.out.println("TrackPanel:listMembers:track="+track.size());
		   ArrayList<JItemPanel>ipl=new ArrayList<JItemPanel>();
		   JItemPanel itemPanel;
		   
		   Object[]oa=track.toArray();
		   String[] sa=Arrays.asList(oa).toArray(new String[oa.length]);
		   for(int i=sa.length-1;i>-1;i--){
			   //System.out.println("TrackPanel:listMembers:aSa="+sa[i]);
			   itemPanel=new JItemPanel(console,sa[i]);
			   ipl.add(itemPanel);
		   }
		   return ipl.toArray(new JItemPanel[0]);
		}catch(Exception e) {
     	LOGGER.severe(e.toString());
         return null;
     }
}
/**
 * Pop the previous context on the top of the track stack. 
 * @param console the main console.
 * @return the top context locator.
 */
public static String popMember(JMainConsole console){
	Stack <String>track=console.getTrack();
	if(track==null)
		return null;
	String locator$=track.pop();
	console.setTrack(track);
	return locator$;
}
/**
 * Clear the track stack.
 * @param console the main console.
 */
public static void clearMembers(JMainConsole console){
	Stack <String>track=console.getTrack();
	if(track==null)
		return ;
	track.clear();
	console.setTrack(track);
	
}
/**
 * Add context locator on the top of the track stack.
 * @param console the main console.
 * @param locator$ the context locator.
 */
public static void putMember(JMainConsole console,String locator$){
//	System.out.println("TrackPanel:putMember:locator:"+Locator.remove(locator$, Locator.LOCATOR_ICON));
	Stack <String>track=console.getTrack();
	if(track==null)
		track=new Stack<String>();
	if(track.isEmpty()){
		track.push(locator$);
		console.setTrack(track);
		return;
	}
	
	 Properties locator=Locator.toProperties(locator$);
	 String title$=locator.getProperty(Locator.LOCATOR_TITLE);
	 if("track".equalsIgnoreCase(title$))
		 return;
	 String type$=locator.getProperty(JContext.CONTEXT_TYPE);
//	 System.out.println("TrackPanel:putMember:title="+title$+" type="+type$);
	 Stack<String> s = new Stack<String>();
     s.push(locator$);
     String candidate$;
     String mTitle$;
	 String mType$;
     while (!track.isEmpty()) {
         candidate$ = track.pop();
         locator=Locator.toProperties(candidate$);
         mTitle$=locator.getProperty(Locator.LOCATOR_TITLE);
		 mType$=locator.getProperty(JContext.CONTEXT_TYPE);
		//System.out.println("TrackPanel:putMember:mTitle="+mTitle$+" mType="+mType$);
		if(mTitle$==null||mType$==null){
			System.out.println("TrackPanel:putMember:candidate="+candidate$);
			continue;
		}
         if (mTitle$.equals(title$)&&mType$.equals(type$)){
             s.clear();
         }
         s.push(candidate$);
     }
       track.clear();
      
     while (!s.isEmpty())
     	 track.push(s.pop());
     console.setTrack(track);
}
/**
 * Get the context title.
 * @return the context title.
 */
@Override
public String getTitle() {
	return "Track";
}
/**
 * Get the context type.
 * @return the context type.
 */
@Override
public String getType() {
	return "Track";
}
/**
 * Store the track stack on the disk.
 * @param console the main console.
 */
public static void storeTrack(JMainConsole console){
	try{
		File home=new File(System.getProperty("user.home")+"/.entigrator");
		if(!home.exists())
			home.mkdir();
		File trackFile=new File(home,TRACK);
		Stack <String>track=console.getTrack();
		if(track==null||track.isEmpty()){
			if(trackFile.exists())
				trackFile.delete();
			return;
		}
//		System.out.println("TrackPanel:saveTrack:size="+track.size());
		String[] sa=track.toArray(new String[0]);
		 FileOutputStream fos = new FileOutputStream(trackFile);
         OutputStreamWriter osw = new OutputStreamWriter(fos);
         for(String aSa:sa)
             osw.write(aSa+Locator.GROUP_DELIMITER);
         osw.close();
	}catch(Exception e){
		Logger.getLogger(JTrackPanel.class.getName()).severe(e.toString());
	}
}
/**
 * Restore the track stack from the disk.
 * @param console the main console.
 */
public static void restoreTrack(JMainConsole console){
	try{
		File home=new File(System.getProperty("user.home")+"/.entigrator");
		if(!home.exists())
			home.mkdir();
		File trackFile=new File(home,TRACK);
		if(!trackFile.exists())
			return;
		FileInputStream fis = new FileInputStream(trackFile);
        InputStreamReader inp = new InputStreamReader(fis, "UTF-8");
        BufferedReader rd = new BufferedReader(inp);
		String track$=rd.readLine();
		String[] sa=track$.split(Locator.GROUP_DELIMITER);
		
		Stack <String>track=new Stack<String>();
		for(String aSa:sa)
			if(aSa!=null&&aSa.length()>0)
				track.push(aSa);
		String locator$=track.peek();
		
		JConsoleHandler.execute(console, locator$);
		console.setTrack(track);
		rd.close();
		fis.close();
		
	}catch(Exception e){
		Logger.getLogger(JTrackPanel.class.getName()).severe(e.toString());
	}
}
/**
 * Complete the context.
 * No action.
 */
@Override
public void close() {
	// TODO Auto-generated method stub
	
}
/**
 * Get the context subtitle.
 * @return null.
 */
@Override
public String getSubtitle() {
	// TODO Auto-generated method stub
	return null;
}
}
