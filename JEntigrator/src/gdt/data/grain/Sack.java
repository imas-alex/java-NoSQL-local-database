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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
//import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
//import java.io.RandomAccessFile;
import java.io.Writer;
import java.lang.reflect.Array;
//import java.nio.channels.Channels;
//import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Stack;
import java.util.logging.Logger;

//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import gdt.data.btree.BTree;
import gdt.data.store.Entigrator;
/**
* The sack is a basic persistent data structure in the application.
* It serves as a container for entities and index maps.
* @author  Alexander Imas
* @version 1.0
* @since   2016-03-11
*/
public class Sack extends Identity {
    private final BTree attributes;
    private final BTree elements;
    static boolean debug=false;    
    public Sack() {
    super();
        attributes = new BTree();
        elements = new BTree();
    
    }
    /**
   	 * Load a sack from a xml file.
   	 * @param fname$ the path of the file. 
	 * @return a sack.   
   	 */
       public static Sack parseXML(Entigrator entigrator,String fname$) {
    	  if(debug)
    	    System.out.println("Sack:parseXML:fname="+fname$); 
    	  final Logger LOGGER= Logger.getLogger(Sack.class.getName());
    	   try {
            if (fname$ == null||fname$.endsWith("/null")) {
               // LOGGER.severe(":parseXML:file path is null");
                return null;
            }
            long begin=System.currentTimeMillis();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            File file = new File(fname$);
            if(!file.exists()){
            	 LOGGER.severe(":parseXML:not exists file="+fname$);
            	 return null;
            }
            if(file.length()<10){
            	 LOGGER.severe(":parseXML:empty file="+fname$);
            	 file.delete();
                 return null;
            }
          
            int n=0;
            while(n<3){
            
            	if(file.canRead())
            		break;
            	n++;
                Thread.sleep(10);
    	   }
            if(!file.canRead())
        		return null;
           
            FileInputStream is = new FileInputStream(file);
            Document doc = db.parse(is);
            doc.setXmlVersion("1.1");
            Sack ret = new Sack();
            ret.setKey(file.getName());
            NodeList attributes = doc.getElementsByTagName("attribute");
            NodeList elements = doc.getElementsByTagName("element");
            NodeList items;
            String type$;
            String name$;
            String value$;
            String title$;
            Core core;
            for (int i = 0; i < attributes.getLength(); i++) {
                Element attribute = (Element) attributes.item(i);
                type$ = attribute.getAttribute("type");
                name$ = attribute.getAttribute("name");
                value$ = attribute.getAttribute("value");
                if (name$ != null) {
                    core = new Core(type$, name$, value$);
                    ret.putAttribute(core);
                }
            }
            for (int i = 0; i < elements.getLength(); i++) {
                Element element = (Element) elements.item(i);
                title$ = element.getAttribute("title");
                  items = element.getElementsByTagName("item");
                 if (items != null)
                    for (int j = 0; j < items.getLength(); j++) {
                        Element item = (Element) items.item(j);
                        type$ = item.getAttribute("type");
                        name$ = item.getAttribute("name");
                        value$ = item.getAttribute("value");
                        if (name$ != null) {
                            core = new Core(type$, name$, value$);
                            ret.putElementItem(title$, core);
                        }
                    }
            }
            is.close();
           
            return ret;
        } catch (Exception e) {
        	LOGGER.severe("cannot parse,delete file:"+fname$);
        	try{
        		
        		new File(fname$).delete();
        		
        	}catch(Exception ee){
        		LOGGER.severe(ee.toString());
        		
        	}
        	entigrator.removeFileLock(fname$);
        	return null;
        }
    }
      /**
     	 * Insert an attribute
     	 * @param core the attribute 
     	 */
    public void putAttribute(Core core) {
        if (core == null||core.name==null)
            return;
        attributes.put(core.name.trim(), core);
    }
    /**
 	 * Remove the attribute
 	 * @param attr the attribute name. 
 	 */
    public void removeAttribute(String attr) {
        attributes.remove(attr);
    }
    /**
   	 * Get an attribute.
   	 * @param key the name of the attribute. 
	 * @return the attribute.   
   	 */
    public Core getAttribute(String key) {
        if (key == null)
            return null;
        return (Core) attributes.get(key);
    }
    /**
   	 * Get an attribute value.
   	 * @param key the name of the attribute. 
	 * @return the attribute value string.   
   	 */
   public String getAttributeAt(String key) {
        if (key == null)
            return null;
        if (attributes.get(key) == null)
            return null;
        if(((Core) attributes.get(key)).value==null)
        	return null;
        return ((Core) attributes.get(key)).value.trim();
    }

    Stack<String> listAttributes() {
        return attributes.keys();
    }
    /**
   	 * Get all attributes.
   
	 * @return the array of attributes.   
   	 */
    public Core[] attributesGet() {
        Stack<String> s = attributes.keys();
        if (s == null)
            return null;
        Stack<Core> ss = new Stack<Core>();
        String name;
        while (!s.isEmpty()) {
            name = s.pop().toString();
            if (name == null)
                continue;
            ss.push(getAttribute(name));
        }
        int cnt = ss.size();
        if (cnt < 1)
            return null;
        Core[] ret = new Core[cnt];
        for (int i = 0; i < cnt; i++)
            ret[i] = (Core) ss.pop();
        return ret;
    }
    /**
   	 * List names of all cores in the element.
   	 * @param element$ the name of the element. 
	 * @return the sorted array of names.   
   	 */
    public String[] elementList(String element$) {
        if (element$ == null)
            return null;
        Core[] cores = elementGet(element$);
        if (cores == null)
            return null;
        int cnt = Array.getLength(cores);
        String[] ret = new String[cnt];
        for (int i = 0; i < cnt; i++)
            ret[i] = cores[i].name;
        Support.sortStrings(ret);
        return ret;
    }
    /**
   	 * List names of all cores in the element.
   	 * @param element$ the name of the element. 
	 * @return the no-sorted array of names.   
   	 */
    public String[] elementListNoSorted(String element$) {
        if (element$ == null)
            return null;
        Core[] cores = elementGet(element$);
        if (cores == null)
            return null;
        int cnt = Array.getLength(cores);
        String[] ret = new String[cnt];
        for (int i = 0; i < cnt; i++)
            ret[i] = cores[i].name;
        return ret;
    }
    /**
   	 * Get all cores from the element.
   	 * @param element$ the name of the element. 
	 * @return the array of cores.   
   	 */
    public Core[] elementGet(String element$) {
        return enumerateElement(element$);
    }
    /**
   	 * Replace element with the array of cores.
   	 * @param element$ the name of the element
   	 * @param ca array of cores. 
   	 */
    public void elementReplace(String element$, Core[] ca) {
        removeElement(element$);
        if (element$ == null)
            return;
        createElement(element$);
        if (ca == null)
            return;
        for (Core aCa : ca) putElementItem(element$, aCa);
    }

    
   private Core[] enumerateElement(String element) {
        if (element == null)
            return null;
       Stack<String> s = listElement(element);
        if (s == null)
            return null;
        Stack<Core> ss = new Stack<Core>();
        String name;
        while (!s.isEmpty()) {
            name = s.pop().toString();
            if (name == null)
                continue;
            ss.push(getElementItem(element, name));
        }
        int cnt = ss.size();
        if (cnt < 1)
            return null;
        Core[] ret = new Core[cnt];
        for (int i = 0; i < cnt; i++)
            ret[i] = (Core) ss.pop();
        return ret;
    }
   /**
  	 * List names of cores in the element having the given type.
  	 * @param element$ the name of the element
  	 * @param type$ the type. 
	 * @return the no-sorted array of names.   
  	 */

    public String[] listItemsAtType(String element$, String type$) {
        if (type$ == null)
            return null;
        if (element$ == null)
            return null;
        Stack<String> s = listElement(element$);
        if (s == null)
            return null;
        int cnt = s.size();
        if (cnt < 1)
            return null;
        Stack<String> out = new Stack<String>();
        Core core;
        while (!s.isEmpty()) {
            core = getElementItem(element$, s.pop().toString());
            if (core.type.equals(type$))
                out.push(core.name);
        }
        cnt = out.size();
        if (cnt < 1)
            return null;
        String[] ret = new String[cnt];
        for (int i = 0; i < cnt; i++)
            ret[i] = out.pop().toString();
        return ret;
    }
    /**
   	 * List names of all cores in the element.
	 * @return the no-sorted array of names.   
   	 */
   public String[] elementsList() {
        Stack<String> s = elements.keys();
        int cnt = s.size();
        String[] ret = new String[cnt];
        for (int i = 0; i < cnt; i++)
            ret[i] = s.pop().toString();
        Support.sortStrings(ret);
        return ret;
    }
   /**
  	 * No sorted list names of all cores in the element.
	 * @return the no-sorted array of names.   
  	 */
    public String[] elementsListNoSorted() {
        Stack<String> s = listElements();
        String[] ret = new String[s.size()];
        int cnt = s.size();
        for (int i = 0; i < cnt; i++) {
            ret[i] = s.pop().toString();
        }
        return ret;
    }
    Stack <String>listElements() {
        return elements.keys();
    }
    /**
  	 * Check if the sack has the element
	 * @param element$ the name of element
	 * @return true if contains , false if not.   
  	 */
    public boolean existsElement(String element$) {
        if(element$==null)
        	return false;
    	return elements.containsKey(element$.trim());
    }
    /**
  	 * Find  first core having the given value and return its name.
  	 * @param element$ the name of the element
  	 * @param value$ the value. 
	 * @return the name of the core.   
  	 */
    public String getElementItemAtValue(String element$, String value$) {
        if (!existsElement(element$))
            return null;
        if (value$ == null)
            value$ = "";
        BTree bTree = (BTree) elements.get(element$);
        Stack<String> keys = bTree.keys();
        Object key ;
        Object value ;
        while (!keys.isEmpty()) {
            key = keys.pop();
            if (key == null)
                continue;
            value = bTree.get(key.toString());
            if (value == null)
                continue;
            if (value$.equals(((Core) value).value))
                return key.toString();
        }
        return null;
    }
  
    private Stack<String> listElement(String element) {
        BTree bTree = (BTree) elements.get(element);
        if (bTree == null)
            return null;
        Stack<String> s = bTree.keys();
        if (s == null)
            return null;
        Stack <String>ret = new Stack<String>();
        while (!s.isEmpty())
            ret.push(s.pop());
        return ret;

    }
    /**
  	 * Remove all cores from the element
  	 * @param element$ the name of the element
  	 */  
    public void clearElement(String element$) {
        if (element$ == null)
            return;
        Stack <String>s = listElement(element$);
        if (s == null)
            return;
        while (!s.isEmpty())
            removeElementItem(element$, s.pop().toString());
    }
    /**
  	 * Create an element
  	 * @param element$ the name of the element
  	 */  
    public void createElement(String element$) {
        if(element$==null)
        	return;
    	elements.put(element$.trim(), new BTree());
    }
    /**
  	 * Remove the element
  	 * @param element$ the name of the element
  	 */  
    public void removeElement(String element$) {
        elements.remove(element$);
    }
    /**
  	 * Put a core into the element
  	 * @param element$ the name of the element
  	 * @param core new core
  	 */  
    public void putElementItem(String element$, Core core) {
        if (element$ == null || core == null||core.name==null)
            return;
        BTree bTree = (BTree) elements.get(element$.trim());
        if (bTree == null) {
            elements.put(element$.trim(), new BTree());
            bTree = (BTree) elements.get(element$.trim());
            if (bTree == null)
                return;

        }
        bTree.put(core.name.trim(), core);
    }
    /**
  	 * Get the core from the element by name
  	 * @param element$ the name of the element
  	 * @param item$ the name of the core
  	 * @return the core having the given name.
  	 */  
    public Core getElementItem(String element$, String item$) {
        if (element$ == null || item$ == null)
            return null;
        BTree bTree = (BTree) elements.get(element$);
        if (bTree == null)
            return null;
        return (Core) bTree.get(item$);
    }
    /**
  	 * Get the value of the core from the element by its name.
  	 * @param element$ the name of the element
  	 * @param item$ the name of the core
  	 * @return the value of the core having the given name.
  	 */  
    public String getElementItemAt(String element$, String item$) {
        if (element$ == null)
            return null;
        if (item$ == null)
            item$ = "";
        BTree bTree = (BTree) elements.get(element$);
        if (bTree == null)
            return null;
        Core core = (Core) bTree.get(item$);
        if (core == null)
            return null;
        if (core.value == null)
            return null;
        
        return core.value.trim();
    }
    /**
  	 * Remove the core from the element by its name.
  	 * @param element$ the name of the element
  	 * @param item$ the name of the core
  	 */  
    public void removeElementItem(String element$, String item$) {
        if (element$ == null || item$ == null) {
            return;
        }
        BTree bTree = (BTree) elements.get(element$);
        if (bTree == null) {
            return;
        }
        bTree.remove(item$);
    }
    /**
  	 * Print out the sack. For debugging only.
  	 */  
    public void print() {
//    	final Logger LOGGER= Logger.getLogger(Sack.class.getName());
    	Granule[] ga = pour();
        if (ga == null) {
            return;
        }
        int cnt = ga.length;
        if (cnt < 1) {
            System.out.println("Empty sack");
            return;
        }
        
        for (Granule aGa : ga) 
        	//LOGGER.info(aGa.toString());
        	System.out.println(aGa.toString());
    }

    private Granule[] pour() {
        Stack<Granule> s = new Stack<Granule>();
        Stack<String> attrKeys = attributes.keys();
        Core core ;
        Granule grain;
        while (!attrKeys.isEmpty()) {
            core = (Core) attributes.get((String) attrKeys.pop());
            if (core == null)
                continue;
            grain = new Granule(getKey(), "attribute", core);
            s.push(grain);
        }
        Stack<String> elKeys = elements.keys();
        BTree bTree;
        Stack<String> itemKeys;
        String element;
        while (!elKeys.isEmpty()) {
            element = (String) elKeys.pop();
            bTree = (BTree) elements.get(element);
            if (bTree == null)
                continue;
            itemKeys = bTree.keys();
            while (!itemKeys.isEmpty()) {
                core = (Core) bTree.get((String) itemKeys.pop());
                if (core == null)
                    continue;
                grain = new Granule(getKey(), element, core);
                s.push(grain);
            }
        }
        int cnt = s.size();
        if (cnt < 1)
            return null;
        Granule[] ret = new Granule[cnt];
        for (int i = 0; i < cnt; i++)
            ret[i] = (Granule) s.pop();
        return ret;
    }
    /**
  	 * Overrides the toString method.
  	 * @return the key of the sack.
  	 */  
    public String toString() {
        return getKey();
    }
    /**
  	 * Write the sack into the xml file. 
  	 * @param fname$ path of the file.
  	 * @return true if success, false otherwise.
  	 */  
public synchronized boolean saveXML(String fname$){
	final Logger LOGGER= Logger.getLogger(Sack.class.getName());   
	if (fname$ == null){
		LOGGER.severe(":saveXML: fname is null");
		return false;
	}
    try{
        String out;
        File file = new File(fname$);
        if (!file.exists()) {
            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();
            if (!file.createNewFile()) {
                //System.out.println("Sack:saveXML:cannot create file=" + fname);
            	LOGGER.severe(":saveXML: cannot create file=" + fname$);
            	return false;
            }
        }
       
        FileOutputStream fos = new FileOutputStream(fname$, false);
        FileLock fl = fos.getChannel().tryLock();
        if (fl == null) {
            int cnt = 0;
            fos.close();
            while (fl == null)
                try {
                    Thread.sleep(100);
                    cnt++;
                    if (cnt > 10) {
                    	LOGGER.severe(":saveXML: cannot save sack=" + getKey());
                    	return false;
                    }
                    fos = new FileOutputStream(fname$, false);
                    fl = fos.getChannel().tryLock();
                } catch (Exception e) {
                	LOGGER.severe(":saveXML:" + e.toString());
                }
        }
        fl.release();
        fos.close();
        fos = new FileOutputStream(fname$, false);
        Writer writer = new OutputStreamWriter(fos, "UTF-8");
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
        writer.write(10);
        out = "<sack path=\"" + path + "\" key=\"" + key + "\">";
        writer.write(10);
        writer.write(out);
        writer.write(10);
        Stack<String> s = new Stack<String>();
        Stack<String> buf = attributes.keys();
        while (!buf.isEmpty())
            s.push(buf.pop());
        writer.write("<attributes>");
        writer.write(10);
        String key$;
        Core core;
        while (!s.isEmpty()) {
            key$ = s.pop().toString();
            core = getAttribute(key$);
            out = "<attribute type=\"" + Support.translate(core.type) + "\" name=\"" + Support.translate(core.name) + "\" value=\"" + Support.translate(core.value) + "\" />";
            writer.write(out);
            writer.write(10);
        }
        writer.write("</attributes>");
        writer.write(10);
        writer.write("<elements>");
        writer.write(10);
        buf = elements.keys();
        while (!buf.isEmpty())
            s.push(buf.pop());
        BTree element ;
        Stack<String> items = new Stack<String>();
        while (!s.isEmpty()) {
            key$ = s.pop().toString();
            element = (BTree) elements.get(key$);
            buf = element.keys();
            while (!buf.isEmpty())
                items.push(buf.pop());
            out = "<element title=\"" + Support.translate(key$) + "\" >";
            writer.write(out);
            writer.write(10);
            while (!items.isEmpty()) {
                key$ = items.pop().toString();
                core = (Core) element.get(key$);
                if (core == null)
                    continue;
                out = "<item type=\"" + Support.translate(core.type) + "\" name=\"" + Support.translate(core.name) + "\" value=\"" + Support.translate(core.value) + "\" />";
                writer.write(out);
                writer.write(10);
            }
            writer.write("</element>");
            writer.write(10);
        }
        writer.write("</elements>");
        writer.write(10);
        writer.write("</sack>");
        writer.write(10);
        writer.close();
        return true;
    }catch(Exception e){
    	LOGGER.severe(":saveXML:"+e.toString());
    	return false;
    }
    }
/**
	 * Write the sack into the output stream. 
	 * @param os output stream.
	 * @return true if success, false otherwise.
	 * @throws java.io.IOException write failed.
	 */  
    public boolean writeXML(OutputStream os) throws IOException {
    	final Logger LOGGER= Logger.getLogger(Sack.class.getName()); 
    	if (os == null) {
    		LOGGER.severe(":saveXML::writeXML: parameter 'OutputStream' is null");	
            return false;
        }
        os.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>".getBytes());
        String out = "<sack path=\"" + path + "\" key=\"" + key + "\">";
        os.write(10);
        os.write(out.getBytes());
        os.write(10);
        Stack<String> s = new Stack<String>();
        Stack<String> buf = attributes.keys();
        while (!buf.isEmpty())
            s.push(buf.pop());
        os.write("<attributes>".getBytes());
        os.write(10);
        String key$;
        Core core;
        while (!s.isEmpty()) {
            key$ = s.pop().toString();
            core = getAttribute(key$);
            out = "<attribute type=\"" + Support.translate(core.type) + "\" name=\"" + Support.translate(core.name) + "\" value=\"" + Support.translate(core.value) + "\" />";
            os.write(out.getBytes());
            os.write(10);
        }
        os.write("</attributes>".getBytes());
        os.write(10);
        os.write("<elements>".getBytes());
        os.write(10);
        buf = elements.keys();
        while (!buf.isEmpty())
            s.push(buf.pop());
        BTree element ;
        Stack<String> items = new Stack<String>();
        while (!s.isEmpty()) {
            key$ = s.pop().toString();
            element = (BTree) elements.get(key$);
            buf = element.keys();
            while (!buf.isEmpty())
                items.push(buf.pop());
            out = "<element title=\"" + key$ + "\" >";
            os.write(out.getBytes());
            os.write(10);
            while (!items.isEmpty()) {
                key$ = items.pop().toString();
                core = (Core) element.get(key$);
                if (core == null)
                    continue;
                out = "<item type=\"" + Support.translate(core.type) + "\" name=\"" + Support.translate(core.name) + "\" value=\"" + Support.translate(core.value) + "\" />";
                os.write(out.getBytes());
                os.write(10);
            }
            os.write("</element>".getBytes());
            os.write(10);
        }
        os.write("</elements>".getBytes());
        os.write(10);
        os.write("</sack>".getBytes());
        os.write(10);
        return true;
    }
    /**
	 * Get the property value. 
	 * @param property$ property name.
	 * @return property value.
	 */  
    public String getProperty(String property$) {
        if (property$ == null)
            return null;
        if("label".equals(property$))
        	return getElementItemAt("property", getKey());
        Core[] ca = elementGet("property");
        if (ca == null)
            return null;
        for (Core aCa : ca)
            if (property$.equals(aCa.type))
                return aCa.value;
        return null;
    }
}
