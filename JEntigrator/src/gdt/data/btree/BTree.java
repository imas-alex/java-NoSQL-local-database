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
import java.util.Stack;
/**
 *  B-tree container of BNodes
 * @author  Alexander Imas
 * @version 1.0
 * @since   2016-03-11
 */
public class BTree {
    protected BNode root;
    /**
     * Default BTree constructor.
     */  
    public BTree() {
        root = new BNode();
        root.parent = null;
        root.bTree = this;
    }
    /**
     * Put  an object associated with the key into the BTree
     * @param key string
     * @param value object
     * 
     * @return  int 0 if successful, -1 if failed
     *    
     */  
    public int put(String key, Object value) {
        return root.put(key, value);
    }
    /**
     * Get  an object associated with the key 
     * @param key string.
     * 
     * @return object associated with the key
     *    
     */  
    public Object get(String key) {
        return root.getObject(key);
    }
    /**
     * Remove  an object associated with the key 
     * @param key string.
     * 
     * @return object associated with the key
     *    
     */      
    public Object remove(String key) {
    	Object ret=root.getObject(key);
        root.remove(key);
        return ret;
    }
    /**
     * @return all keys as a stack
     *    
     */      
    public Stack<String> keys() {
        Stack <String>ret = new Stack<String>();
        pushKeys(ret, root);
        return ret;
    }
    /**
     * Check if the BTree contains the key
     * @param key Key string.
     * 
     * @return true if the node contains the key and false otherwise
     *    
     */  
    public boolean containsKey(String key) {
        return root.containsKey(key);
    }

    private void pushKeys(Stack<String> s, BNode bNode) {
        if (s == null || bNode == null)
            return ;
        if (bNode.last_ == 0)
            return ;
        for (int i = 0; i < bNode.last_; i++) {
            if (bNode.values[i] == null)
                continue;
            if (bNode.values[i].containsNode())
                pushKeys(s, ((BNode) bNode.values[i].value));
            else
                s.push(bNode.values[i].key);
        }
    }

}
