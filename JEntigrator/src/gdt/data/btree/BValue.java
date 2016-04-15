
package gdt.data.btree;
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
/**
 *  A structure to keep key/value pair within the BNode
 * @author  Alexander Imas
 * @version 1.0
 * @since   2016-03-11
 */
public class BValue {
	/**
	 * The key (string) 
	 */
    public String key;
    /**
	 * The value (object) 
	 */
    public Object value;
    /**
	 * The BNode containing this BValue 
	 */
    public BNode parent;
    private boolean leaf = true;
    /**
     * Default BValue constructor.
     * @param key string.
     * @param value object.
     *
     */  
    public BValue(String key, Object value) {
        this.key = key;
        this.value = value;
    }
    /**
     * Check if this instance is a container 
     *@return true if this instance of BValue contains sub nodes and false otherwise  
     *    
     */
    public boolean containsNode() {
        return !leaf;
    }
    /**
 
     *@return key  
     *    
     */
    public String toString() {
        return key;
    }
    /**
     *Set this instance to be a container.  
     */
    public void noLeaf() {
        leaf = false;
    }
}
