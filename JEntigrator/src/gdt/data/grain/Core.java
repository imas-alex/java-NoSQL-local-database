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
/**
* One of the most basic data structures in the application 
* that serves as a component of elements. 
* This data structure contains three string fields:type,name and value.
* 
* @author  Alexander Imas
* @version 1.0
* @since   2016-03-11
*/
public class Core {
    public String type = "string";
    public String name;
    public String value;
    public Core() {
    }
    public Core(String type, String name, String value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }
    /**
	 * Sort array of cores by type 
	 *  @param ca array of cores 
	 * @return sorted array of cores
	 */	
    
    public static Core[] sortAtType(Core[] ca) {
        if (ca == null)
            return null;
        Core c;
        boolean greater;
        for (int i = ca.length; --i >= 0; ) {
            boolean flipped = false;
            for (int j = 0; j < i; j++) {
                greater = false;
                if (ca[j].type == null)
                    ca[j].type = "null";
                if (ca[j + 1].type == null)
                    ca[j + 1].type = "null";
                if (ca[j].type.equals("null")) {
                    //greater = false;
                    continue;
                }
                if (ca[j + 1].type.equals("null")) {
                    //greater = true;
                    continue;
                }
                if (ca[j].type.compareToIgnoreCase(ca[j + 1].type) > 0)
                    greater = true;
                if (greater) {
                    c = ca[j];
                    ca[j] = ca[j + 1];
                    ca[j + 1] = c;
                    flipped = true;
                }
            }
            if (!flipped)
                return ca;
        }
        return null;
    }
    /**
	 * Sort array of cores by value 
	 *  @param ca array of cores 
	 * @return sorted array of cores
	 */	
 public static Core[] sortAtValue(Core[] ca) {
        if (ca == null)
            return null;
        Core c;
        boolean greater;
        for (int i = ca.length; --i >= 0; ) {
            boolean flipped = false;
            for (int j = 0; j < i; j++) {
                greater = false;
                if (ca[j].value == null)
                    ca[j].value = "null";
                if (ca[j + 1].value == null)
                    ca[j + 1].value = "null";
                if (ca[j].value.equals("null")) {
                    //greater = false;
                    continue;
                }
                if (ca[j + 1].value.equals("null")) {
                    //greater = true;
                    continue;
                }
                if (ca[j].value.compareToIgnoreCase(ca[j + 1].value) > 0)
                    greater = true;
                if (greater) {
                    c = ca[j];
                    ca[j] = ca[j + 1];
                    ca[j + 1] = c;
                    flipped = true;
                }
            }
            if (!flipped)
                return ca;
        }
        return null;
    }
 /**
	 * Sort array of cores by name 
	 *  @param ca array of cores 
	 * @return sorted array of cores
	 */	
    public static Core[] sortAtName(Core[] ca) {
        if (ca == null)
            return null;
        Core c;
        boolean greater;
        for (int i = ca.length; --i >= 0; ) {
            boolean flipped = false;
            for (int j = 0; j < i; j++) {
                greater = false;
                if (ca[j].name.compareToIgnoreCase(ca[j + 1].name) > 0)
                    greater = true;
                if (greater) {
                    c = ca[j];
                    ca[j] = ca[j + 1];
                    ca[j + 1] = c;
                    flipped = true;
                }
            }
            if (!flipped)
                return ca;
        }
        return null;
    }
    /**
   	 * Select cores from the first array which have no 
   	 * matching cores in the second array. One core matches to another
   	 * if they have the same name. 
   	 * 
   	 *  @param ca1 first array of cores
   	 *  @param ca2 second array of cores 
   	 * @return the difference array.
   	 * 
   	 */
   	     	
    public static Core[] subtract(Core[] ca1,Core[] ca2){
    	if(ca1==null)
    		return null;
    	if(ca2==null)
    		return ca1;
    	ArrayList<Core>cl=new ArrayList<Core>();
    	boolean found;
    	for(Core aCa1:ca1){
    		found=false;
    		for(Core aCa2:ca2)
    			 if(aCa1.name.equals(aCa2.name)){
    			    	found=true;
    			    	break;
    			   }
    		if(!found)
    			cl.add(aCa1);
    	}
    	 return cl.toArray(new Core[0]);
    }
}