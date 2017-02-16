package gdt.data.grain;
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Base64;

import gdt.data.entity.facet.ExtensionHandler;
import gdt.data.store.Entigrator;
/**
* The class collects general-purpose methods. 
* @author  Alexander Imas
* @version 1.0
* @since   2016-03-11
*/

public class Support {
	static boolean debug=false;
    private static boolean itemExists(String name, Vector<String> vec) {
        if (name == null)
            return false;
        if (vec == null)
            return false;
        int cnt = vec.size();
        for (int i = 0; i < cnt; i++) {
            if (name.compareTo((String) vec.get(i)) == 0)
                return true;
        }
        return false;
    }
    /**
   	 * Add a string into the vector.
   	 * @param name$ the string
   	 * @param vec vector of strings. 
	 * @return 0 if success,-1 otherwise.   
   	 */
    public static int addItem(String name$, Vector<String> vec) {
        if (name$ == null)
            return -1;
        if (vec == null)
            return -1;
        if (Support.itemExists(name$, vec))
            return 0;
        vec.addElement(name$);
        return 0;
    }
    /**
   	 * Sort array of strings
   	 * @param sa string array 
   	 */
    public static void sortStrings(String[] sa) {
        String s;
        boolean greater = false;
        for (int i = sa.length; --i >= 0; ) {
            boolean flipped = false;
            for (int j = 0; j < i; j++) {
                greater = false;
                if (sa[j] == null)
                    sa[j] = "null";
                if (sa[j + 1] == null)
                    sa[j + 1] = "null";
                if (sa[j].equals("null")) {
                    greater = false;
                    continue;
                }
                if (sa[j + 1].equals("null")) {
                    greater = true;
                    continue;
                }
                if (sa[j].compareToIgnoreCase(sa[j + 1]) > 0)
                    greater = true;
                if (greater) {
                    s = sa[j];
                    sa[j] = sa[j + 1];
                    sa[j + 1] = s;
                    flipped = true;
                }
            }
            if (!flipped)
                return;
        }
    }
    private static String transamp(String string$) {
        if (string$ == null)
            return null;
        return  string$.replaceAll("&", "&amp;");
    }
    /**
   	 * Replace special characters in XML string. 
   	 * @param string$ original string
   	 * @return converted string. 
   	 */ 
    public static String translate(String string$) {
        if (string$ == null)
            return null;
        String ret$ = transamp(string$);
        ret$ = ret$.replaceAll(">", "&gt;");
        ret$ = ret$.replaceAll("<", "&lt;");
        ret$ = ret$.replaceAll("\"", "&quot;");
        ret$ = ret$.replaceAll("\n", "&#xA;");
        ret$ = ret$.replaceAll("'", "&apos;");
 
        return ret$;
    }
    /**
   	 * Get value from the hash table by key string  
   	 * @param keyName key string
   	 * @param tab hash table.
   	 * @return found value or null. 
   	 */ 
  public static Object getValue(String keyName, Hashtable <String,?>tab) {
        if (keyName == null)
            return null;
        if (tab == null)
            return null;
        Enumeration<String> en = tab.keys();
        String curKey = null;
        while (en.hasMoreElements()) {
            curKey = (String) en.nextElement();
            if (keyName.compareTo(curKey) == 0)
                return tab.get(curKey);
        }
        return null;
    }
  /**
 	 * Remove the key and the associated value from the 
 	 * hash table. 
 	 * @param keyName key string
 	 * @param tab hash table.
 	 */ 
    public static void removeKey(String keyName, Hashtable<String,?> tab) {
        if (keyName == null)
            return;
        if (tab == null)
            return;
        Enumeration<String> en = tab.keys();
        String curKey = null;
        while (en.hasMoreElements()) {
            curKey = (String) en.nextElement();
            if (keyName.compareTo(curKey) == 0) {
                tab.remove(curKey);
                break;
            }
        }

    }
    /**
 	 * Get class resource as input stream. 
 	 * @param handler the class
 	 * @param resource$ the resource name.
 	 * @return input stream.
 	 */ 
    public static InputStream getClassResource(Class<?> handler,String resource$){
    	try {
    		InputStream is=handler.getResourceAsStream(resource$);
        	if(is!=null){
        		if(debug)
        		  System.out.println("Support:getClassResource:resource stream="+is.toString());
        		return is;
        	}
        	else{
        	if(debug)
        		  System.out.println("Support:getClassResource:cannot get embedded resource stream for handler="+handler.getName());            		
        	ClassLoader classLoader=handler.getClassLoader();
        		is=classLoader.getResourceAsStream(resource$);
    		if(is!=null)
    		 if(debug)
        		  System.out.println("Support:getClassResource:resourse stream="+is.toString());
    		else
    			if(debug)
    			System.out.println("Support:getClassResource:cannot get resource stream");
        	String handler$=handler.getName();
        	if(debug)
        		System.out.println("Support:getClassResource:class="+handler$);
    		String handlerName$=handler.getSimpleName();
    		if(debug)
    			System.out.println("Support:getClassResource:class name="+handlerName$);
    		String handlerPath$=handler$.replace(".", "/");
    		if(debug)
    			System.out.println("Support:getClassResource:class path="+handlerPath$);
    		String resourcePath$="src/"+handlerPath$.replace(handlerName$, resource$);
    		if(debug)
    			System.out.println("Support:getClassResource:resource path="+resourcePath$);
    		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            URL resourceUrl= classloader.getResource(resourcePath$);
            if(resourceUrl!=null){
            	if(debug)
            		System.out.println("Support:getClassResource:resource URL="+resourceUrl.toString());
                return resourceUrl.openStream();
            }
            else{
            	if(debug)
            		System.out.println("Support:getClassResource:cannot get resource URL");            	
            }
        	}
    	} catch (Exception e) {
 			Logger.getLogger(gdt.data.grain.Support.class.getName()).severe(e.toString());
 		}
 		return null;
    }
    /**
 	 * Get an icon encoded as Base64 string located as file in the class path. 
 	 * @param handler the class
 	 * @param iconResource$ the name of the icon file.
 	 * @return input stream.
 	 */ 
   
    public static String readHandlerIcon(Entigrator entigrator,Class<?> handler,String iconResource$) {
 		try {
 		if(debug)	
 			System.out.println("Support:readHandlerIcon:handler="+handler.getName()+" icon="+iconResource$);
 			//InputStream is=handler.getResourceAsStream(icon$);
 			InputStream is= getClassResource(handler,iconResource$);
 			if(is!=null){
 			ByteArrayOutputStream bos = new ByteArrayOutputStream();
 	            byte[] b = new byte[1024];
 	            int bytesRead = 0;
 	            while ((bytesRead = is.read(b)) != -1) {
 	               bos.write(b, 0, bytesRead);
 	            }
 	            byte[] ba = bos.toByteArray();
 	            is.close();
 	           return Base64.encodeBase64String(ba);
 			}else
 			{
 				if(entigrator==null){
 					if(debug)
 					System.out.println("Support:readHandlerIcon:entigrator is null");
 				return null;
 				}
 				String [] sa=entigrator.indx_listEntities("entity", "extension");
 				if(sa!=null){
 				String icon$=null;
 				for(String s:sa){
 				   icon$=ExtensionHandler.loadIcon(entigrator, s, iconResource$);
 				   if(icon$!=null)
 					   return icon$;
 				}
 				}
 			}
 		} catch (Exception e) {
 			Logger.getLogger(gdt.data.grain.Support.class.getName()).severe(e.toString());
 		}
 		return null;
 	}
    /**
 	 * Copy the icon file from the class path into the target directory. 
 	 * @param handler the class
 	 * @param icon$ the name of the icon file.
 	 *  @param directory$ the icons directory path .
 	 */    
    public static void addHandlerIcon(Class<?> handler,String icon$,String directory$) {
 		try {
 			
 			File iconFile=new File(directory$+"/"+icon$);
 			if(iconFile.exists())
 				return;
 			iconFile.createNewFile();
 			InputStream is=handler.getResourceAsStream(icon$);
 			FileOutputStream fos=new FileOutputStream(iconFile);
 	            byte[] b = new byte[1024];
 	            int bytesRead = 0;
 	            while ((bytesRead = is.read(b)) != -1) {
 	               fos.write(b, 0, bytesRead);
 	            }
 	            is.close();
 	            fos.close();
 		} catch (Exception e) {
 			Logger.getLogger(gdt.data.grain.Support.class.getName()).severe(e.toString());
 		}
 	}
    /**
 	 * Intersect two string arrays 
 	 * @param list1 first array
 	 * @param list2 second array
 	 * @return the result string array.
 	 */  
    public static String[] intersect(String[] list1, String[] list2) {
        if (list2 == null || list1 == null) {
            return null;
        }
        Stack<String> s1 = new Stack<String>();
        Stack<String> s2 = new Stack<String>();
        for (String aList2 : list2) s2.push(aList2);
        String line$ ;
        boolean found ;
        String member$ = null;
        while (!s2.isEmpty()) {
            try {
                found = false;
                line$ = s2.pop().toString();
                if (line$ == null)
                    continue;
                for (String aList1 : list1) {
                    member$ = aList1;
                    if (line$.equals(member$)) {
                        found = true;
                        break;
                    }
                }
                if (found)
             Support.addItem(member$, s1);
                       
            } catch (Exception e) {
                Logger.getLogger(Support.class.getName()).info("intersect:"+e.toString());
            }
        }
        return s1.toArray(new String[0]); 
        
    }
}

