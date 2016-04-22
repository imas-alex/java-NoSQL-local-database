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
import gdt.data.grain.Locator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.logging.Logger;
/**
 * This class contains methods to support
 * the application's clipboard.
 */
public class JClipboard {
	private static final String CLIP="clip";
	private ArrayList<String>sl=new ArrayList<String>();
	/**
	 * Get  clipboard content.
	 * @return clipboard content as a string array.
	 * 
	 */	
public String[] getContent() {
    int cnt = sl.size();
    if (cnt < 1)
        return null;
    return sl.toArray(new String[0]);
}
/**
 * Put a string into the clipboard.
 * @param s$ the string to put.
 */
public void putString(String s$) {
        if(sl.contains(s$))
        	return;
        sl.add(s$);
}
/**
 * Remove all items from the clipboard.
 * 
 */
public void clear() {
    sl.clear();
}
/**
 * Store clipboard on the disk. 
 * @param console the main console.
 */
public static void store(JMainConsole console){
	try{
		
		File home=new File(System.getProperty("user.home")+"/.entigrator");
		if(!home.exists())
			home.mkdir();
		File clipFile=new File(home,CLIP);
		if(clipFile.exists())
			clipFile.delete();
		String[] sa=console.clipboard.getContent();
		if(sa==null)
			return;
		System.out.println("Clipboard:store:sa="+sa.length);
	 FileOutputStream fos = new FileOutputStream(clipFile);
     OutputStreamWriter osw = new OutputStreamWriter(fos);
     for(String aSa:sa)
            osw.write(aSa+Locator.GROUP_DELIMITER);
     osw.close();
	}catch(Exception e){
		Logger.getLogger(JClipboard.class.getName()).severe(e.toString());
	}
}
/**
 * Restore clipboard from the disk. 
 * @param console the main console.
 * 
 */
public static void restore(JMainConsole console){
	try{
		console.clipboard.clear();
		File home=new File(System.getProperty("user.home")+"/.entigrator");
		if(!home.exists())
			home.mkdir();
		File clipFile=new File(home,CLIP);
		if(!clipFile.exists())
			return;
		FileInputStream fis = new FileInputStream(clipFile);
        InputStreamReader inp = new InputStreamReader(fis, "UTF-8");
        BufferedReader rd = new BufferedReader(inp);
		String locator$;
		 while((locator$=rd.readLine())!=null)
			 console.clipboard.putString(locator$);
		rd.close();
		fis.close();
	}catch(Exception e){
		Logger.getLogger(JClipboard.class.getName()).severe(e.toString());
	}
}
}
