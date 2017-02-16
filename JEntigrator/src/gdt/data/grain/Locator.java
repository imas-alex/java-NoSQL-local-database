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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;
/**
* Numerous classes within the JEntigrator use a locator as a parameter
* or a returned value. The locator is a formatted string containing 
* multiple name/value pairs separated by delimiters.
* 
* @author  Alexander Imas
* @version 1.0
* @since   2016-03-11
*/
public class Locator {
	/**
	 * Separate name and its value
	 */
	public static final String VALUE_DELIMITER = "=";
	/**
	 * Separate items within an array
	 */
    public static final String ARRAY_DELIMITER ="_;A_";
    /**
	 * Separates name/value pairs
	 */
    public static final String NAME_DELIMITER ="_;N_";
    /**
   	 * Separates groups
   	 */
    public static final String GROUP_DELIMITER ="_;G_"; 
    /**
   	 * Locator's title
   	 */
    public static final String LOCATOR_TITLE="title";
    /**
   	 * Locator's icon encoded into Base64 string
   	 */
 //   public static final String LOCATOR_ICON="icon";
    //public static final String LOCATOR_ICON_TYPE="icon type";
    public static final String LOCATOR_ICON_CONTAINER="icon container";
    public static final String LOCATOR_ICON_CONTAINER_ICONS="icons folder";
    public static final String LOCATOR_ICON_CONTAINER_ENTITY="entity container";
    public static final String LOCATOR_ICON_CONTAINER_CLASS="icon class container";
    public static final String LOCATOR_ICON_LOCATION="icon class location";
    public static final String LOCATOR_ICON_ELEMENT="icon element";
    public static final String LOCATOR_ICON_CORE="icon core";
    public static final String LOCATOR_ICON_FIELD="icon field";
    public static final String LOCATOR_ICON_FIELD_VALUE="icon field value";
    public static final String LOCATOR_ICON_ENTITY_KEY="icon entity key";
    public static final String LOCATOR_ICON_FIELD_TYPE="icon field type";
    public static final String LOCATOR_ICON_FILE="icon file";
    public static final String LOCATOR_ICON_CLASS="icon class";
    public static final String LOCATOR_ICON_CLASS_LOCATION="icon class location";
    
    /**
   	 * The type of the locator
   	 */
    public static final String LOCATOR_TYPE="type";
    /**
   	 * Instruction for the handler.
   	 */
    public static final String LOCATOR_SCOPE="scope";
    /**
   	 * Shows if the	locator must be checkable item in the list.  
   	 */
    public static final String LOCATOR_CHECKABLE="locator checkable";
    /**
   	 * Shows if the	locator must be checked item in the list.  
   	 */
    public static final String LOCATOR_CHECKED="locator checked";
    /**
   	 * The boolean true representation  
   	 */
    public static final String LOCATOR_TRUE="locator true";
    /**
   	 * The boolean false representation  
   	 */
    public static final String LOCATOR_FALSE="locator false";
    public static final String SESSION_STORAGE = "session storage";
   
    /**
   	 * Convert properties into the locator string.
   	 * @param props properties 
	 * @return locator string.   
   	 */
    public static String toString(Properties props) {
        if (props == null)
            return null;
       try{
        StringBuffer sb = new StringBuffer();
        Enumeration<?> keys = props.keys();
        if(keys==null)
        	return null;
        String name$;
        String value$;
        while (keys.hasMoreElements()) {
            name$ = (String) keys.nextElement();
            value$ = props.getProperty(name$);
            if (value$ != null)
                sb.append(name$ + VALUE_DELIMITER + value$ + NAME_DELIMITER);
        }
        String locator$ = sb.toString();
        locator$.substring(0, locator$.length() - NAME_DELIMITER.length());
        return sb.toString();
       }catch(Exception e){
    	   Logger.getLogger(Locator.class.getName()).severe(":compress:"+e.toString());
    	   return null;
       }
    }
    /**
   	 * Get property value from the locator.
   	 * @param locator$ the locator string
   	 * @param property$ the name of the property 
	 * @return property value.   
   	 */
    public static String getProperty(String locator$,String property$){
    	if(locator$==null||property$==null)
    		return null;
    	Properties props=toProperties( locator$);
    	if(props==null)
    		return null;
    	return props.getProperty(property$);
    }
    /**
   	 * Convert the locator string into the Properties object.
   	 * @param locator$ the locator string. 
	 * @return the Properties object.   
   	 */
    public static Properties toProperties(String locator$) {
       // System.out.println("Locator:toProperties:locator="+locator$);
    	if (locator$ == null){
             return null;
        }
        Properties props = new Properties();
        String[] sa = locator$.split(NAME_DELIMITER);
        if (sa == null){
        	Logger.getLogger(Locator.class.getName()).severe(":toProperties:cannot split fields");
            return null;
        }
        String[] na;
        for (int i = 0; i < sa.length; i++) {
            try {
            	
                na = sa[i].split(VALUE_DELIMITER);
                if (na == null || na.length < 2)
                    continue;
                props.setProperty(na[0], na[1]);
            } catch (Exception e) {
            	Logger.getLogger(Locator.class.getName()).severe(":toProperties:"+e.toString());
            }
        }
        if (props.isEmpty()){
        	Logger.getLogger(Locator.class.getName()).severe(":toProperties:empty");
        	return null;
        }
        return props;
    }
    /**
   	 * Convert the array of strings into the formatted string
   	 * @param sa the array of strings. 
	 * @return the formatted string.   
   	 */
    public static String toString( String[] sa) {
        if(sa==null)
        	return null;
        StringBuffer list = new StringBuffer();
        for (int i = 0; i < sa.length; i++)
            list.append(sa[i] + ARRAY_DELIMITER);
        String list$ = list.toString();
        list$ = list$.substring(0, list$.length() - ARRAY_DELIMITER.length());
        return list$;
    }
    /**
   	 * Convert  the formatted string into the array of strings
   	 * @param list$ the formatted string. 
	 * @return  the array of strings.   
   	 */
    public static String[] toArray(String list$){
    	if(list$==null)
    		return null;
    	if(list$.indexOf(ARRAY_DELIMITER)<0)
    		return new String[]{list$};
    	return list$.split(ARRAY_DELIMITER);
    }
    /**
   	 * Append the name/value pair to the locator. If the name
   	 * already exists its value  will be replaced. 
   	 * @param locator$ the locator string
   	 * @param name$ the name
   	 * @param value$ the value$ 
	 * @return  the locator string.   
   	 */
    public static String append(String locator$, String name$, String value$) {
        if (name$ == null || value$ == null)
            return locator$;
        Properties locator =toProperties(locator$);
        if (locator == null)
            locator = new Properties();
        locator.setProperty(name$, value$);
           return toString(locator);
    }
    /**
   	 * Remove the name/value pair from the locator. 
   	 * @param locator$ the locator string
   	 * @param name$ the name. 
	 * @return  the locator string.   
   	 */
    public static String remove(String locator$,String name$) {
        if (name$ == null )
            return locator$;
        Properties locator =toProperties(locator$);
        if (locator == null)
        	return locator$;
        Enumeration<?> en=locator.keys();
        String key$;
        while(en.hasMoreElements()){
        	key$=(String)en.nextElement();
        	if(name$.equals(key$)){
        		locator.remove(key$);
        		break;
        		}
        }
        return toString(locator);
    }
    /**
   	 * Append the name/value pairs from the other locator. If the name
   	 * already exists it will be ignored. 
   	 * @param locator$ the target locator string
   	 * @param locator2$ the second locator string. 
	 * @return  the result locator string.   
   	 */
    public static String merge(String locator$,String locator2$) {
        try{
        Properties locator =toProperties(locator2$);
        if (locator == null)
        	return locator$;
        Enumeration<?> en=locator.keys();
        String key$;
        String value$;
        while(en.hasMoreElements()){
        	key$=(String)en.nextElement();
        	value$=locator.getProperty(key$);
        	locator$=Locator.append(locator$, key$, value$);
        }
        return locator$;
        }catch(Exception e){
        	Logger.getLogger(Locator.class.getName()).severe(e.toString());
        	return null;
        }
    }
    /**
   	 * Encode a string into the Base64 string.
   	 *  
   	 * @param text$ the original string. 
	 * @return  the encoded string.   
   	 */
    public static String compressText(String text$){
    	try{
    		
    		byte[] ba = text$.getBytes(); 
    		return Base64.encodeBase64String(ba);
    				
		}catch(Exception e){
		Logger.getLogger(Locator.class.getName()).severe(e.toString());
		return null;
		}
    }
    /**
   	 * Sort locators by title.
   	 *  
   	 * @param list the array of locators 
	 *   
   	 */
 public static void sort(ArrayList<String> list){
    	Collections.sort(list, new LocatorComparator());
    }
 
    public static class LocatorComparator implements Comparator<String>{
    	/**
    	 * Compare locators by title
    	 * @param l1$ first locator
    	 * @param l2$ second locator 
    	 *@return  result of titles comparison   
    	 *     
    	 */
    	@Override
	    public int compare(String l1$, String l2$) {
	    	try{
	    		Properties locator=Locator.toProperties(l1$);
	    		String t1$=locator.getProperty(Locator.LOCATOR_TITLE);
	    		locator=Locator.toProperties(l2$);
	    		String t2$=locator.getProperty(Locator.LOCATOR_TITLE);
	    		return t1$.compareToIgnoreCase(t2$);
	    	}catch(Exception e){
	    		System.out.println("Locator:compare:"+e.toString());
	    		return 0;
	    		
	    	}
	    }
	}
 public static String getScript(){
	 StringBuffer sb=new StringBuffer();
	 sb.append("<script >");
	 sb.append("var NAME_DELIMITER=\"_;N_\";");
	 sb.append("var VALUE_DELIMITER=\"=\";");
	 sb.append("var ARRAY_DELIMITER=\"_;A_\";");
	 sb.append("function appendProperty(locator$,name$,value$){");
	 sb.append("var properties=locator$.split(NAME_DELIMITER);");
	 sb.append("var result=[];");
	 sb.append("var nv=[];");	
	 sb.append("for (i = 0; i < properties.length; i++){");
	 sb.append("if(properties[i]==null||properties[i].length<3)");
	 sb.append("continue;");
	 sb.append("nv=properties[i].split(VALUE_DELIMITER);");
	 sb.append("if(nv[0]!=name$){");
	 sb.append("result.push(nv.join(VALUE_DELIMITER));"); 
	 sb.append("}");
	 sb.append("}");
	 sb.append("nv=[name$,value$];"); 
	 sb.append("result.push(nv.join(VALUE_DELIMITER));");
	 sb.append("return result.join(NAME_DELIMITER);");
	 sb.append("};");
	 sb.append("function getProperty(locator$,name$){");
	 sb.append("var properties=locator$.split(NAME_DELIMITER);");
	 sb.append("var nv=[];");	
	 sb.append("for (i = 0; i < properties.length; i++){");
	 sb.append("if(properties[i]==null||properties[i].length<3)");
	 sb.append("continue;");
	 sb.append("nv=properties[i].split(VALUE_DELIMITER);");
	 sb.append("if(nv[0]==name$){");
	 sb.append("return nv[1];");
	 sb.append("}");
	 sb.append("}");
	 sb.append("return null;");
	 sb.append("};");
	 sb.append("function removeProperty(locator$,name$,value$){");
	 sb.append("var properties=locator$.split(NAME_DELIMITER);");
	 sb.append("var result=[];");
	 sb.append("var nv=[];");	
	 sb.append("for (i = 0; i < properties.length; i++){");
	 sb.append("if(properties[i]==null||properties[i].length<3)");
	 sb.append("continue;");
	 sb.append("nv=properties[i].split(VALUE_DELIMITER);");
	 sb.append("if(nv[0]!=name$){");
	 sb.append("result.push(nv.join(VALUE_DELIMITER));"); 
	 sb.append("}");
	 sb.append("}");
	 sb.append("return result.join(NAME_DELIMITER);");
	 sb.append("};");
	 //session storage
	 sb.append("function appendSessionStorage(locator$){");
	 sb.append("var properties=locator$.split(NAME_DELIMITER);");
	 sb.append("var result=[];");
	 sb.append("var nv=[];");	
	/*
	 sb.append("for (i = 0; i < properties.length; i++){");
	 sb.append("if(properties[i]==null||properties[i].length<3)");
	 sb.append("continue;");
	 sb.append("nv=properties[i].split(VALUE_DELIMITER);");
	 sb.append("result.push(nv.join(VALUE_DELIMITER));"); 
	 sb.append("}");
	 */
	 sb.append("for(var i=0, len=localStorage.length; i<len; i++) {"); 
	 sb.append(" var key = localStorage.key(i);");
	 sb.append(" var value = localStorage[key];");
	 sb.append(" nv=[key,value]");
	 sb.append("result.push(nv.join(VALUE_DELIMITER));"); 
	 sb.append("console.log(key + \" => \" + value);");
	 sb.append("}");
	 sb.append("}");
	 sb.append("return appendProperty(locator,"+SESSION_STORAGE+",btoa(result));");
	 sb.append("};");
	 
	 sb.append("</script>");
	 return sb.toString();
 }
}
