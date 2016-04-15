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
 
import java.lang.reflect.Array;
import java.util.Stack;
/**
* A node within B-tree. It contains an array of BValues.
* @author  Alexander Imas
* @version 1.0
* @since   2016-03-11
*/
public class BNode {
    private static final int shouldBeExtended = 0;
    private static final int shouldBeMerged = 1;
    private static final int shouldBeSplit = 2;
    private static final int full = 3;
    private static final int error = -1;
    private static final int between = 0;
    private static final int above = 1;
    private static final int below = 2;
    private static final int equalTop = 3;
    private static final int equalBottom = 4;
    private static final int empty = 5;
    private static final int betweenFinish = 6;
    private static final int betweenNull = 7;
    private static final int betweenFinishNull = 8;
    /**
     * The BTree containing the BNode
     */
    public BTree bTree;
    private final int size = 128;
    private final int mergeLevel = 50;
    private final int splitLevel = 120;
    private final int threshold = 4;
    BNode parent = null;
    final BValue[] values;
    int last_ = 0;
    private final Stack<BValue> s;
    /**
     * Default BNode constructor.
     */
   
    public BNode() {
        values = new BValue[size];
        parent = this;
        s = new Stack<BValue>();
    }
    private BNode(BNode parent, BTree bTree) {
        values = new BValue[size];
        this.parent = parent;
        this.bTree = bTree;
        s = new Stack<BValue>();
    }
/* 
 // Only to test BTree separately.
    public static void main(String[] args) {
        BNode node = new BNode();
        for (int i = 0; i < 100; i++) {
            node.put(String.valueOf(i), i);
        }
    }
*/
  
    private static boolean nameStored(Stack <String>s, String name) {
        if (name == null)
            return true;
        int cnt = s.size();
        for (int i = 0; i < cnt; i++) {
            if (s.get(i) == null)
                continue;
            if (name.compareTo((String) s.get(i)) == 0)
                return true;
        }
        return false;
    }
   
    private static int compare(String arg, String sample) {
        if (arg == null && sample == null)
            return 0;
        if (arg == null && sample != null)
            return 1;
        if (arg != null && sample == null)
            return 2;
        int argLength = arg.length();
        int samLength = sample.length();
        int argChar = 0;
        int samChar = 0;
        for (int i = 0; i < argLength + 1; i++) {
            if (i == argLength && samLength - i > 0)
                return 2;
            if (i == argLength)
                if (samLength == argLength)
                    return 0;
            argChar = (int) arg.charAt(i);
            if (i > sample.length() - 1)
                return 1;
            try {
                samChar = (int) sample.charAt(i);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("BNode:compare:i=" +" arg="+arg+" sample="+sample+" exception="+String.valueOf(i));
            }
            if (argChar > samChar)
                return 1;
            if (argChar < samChar)
                return 2;
        }
        return -1;
    }

    private static int like(String arg, String sample) {
        if (arg == null && sample == null)
            return 0;
        if (arg == null && sample != null)
            return 2;
        if (arg != null && sample == null)
            return 1;
        int argLength = arg.length();
        int samLength = sample.length();
        int argChar = 0;
        int samChar = 0;
        for (int i = 0; i < argLength + 1; i++) {
            if (i == argLength && samLength - i > 0)
                return 1;
            if (i == argLength)
                if (samLength == argLength)
                    return 0;
            argChar = (int) arg.charAt(i);
            if (i > sample.length() - 1)
                return 0;
            try {
                samChar = (int) sample.charAt(i);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("i=" + String.valueOf(i));
                System.out.println("samle=" + sample);
            }
            if (argChar > samChar)
                return 2;
            if (argChar < samChar)
                return 1;
        }
        return -1;
    }

    private void insert(BValue bValue) {
        if (bValue == null)
            return ;
        bValue.parent = this;
        if (last_ == 0) {
            values[0] = bValue;
            last_ = 1;
            return;
        }
        Interval interval = getInterval(bValue.key);
        if (interval == null)
            return ;
        int status = interval.getStatus(bValue.key);
        if (status == betweenFinishNull)
            if (values[interval.top].containsNode()) {
                BNode child = (BNode) values[interval.top].value;
                child.parent = this;
                child.add(bValue);
                return;
            } else {
                values[interval.bottom] = bValue;
                last_ = interval.bottom + 1;
                return;
            }


        if (status == betweenFinish) {
            if (values[interval.top].containsNode()) {
                BNode child = (BNode) values[interval.top].value;
                child.parent = this;
                child.add(bValue);
                return;
            }
            s.clear();
            BValue tmp;
            int cnt = interval.bottom;
            int depth = 0;
            for (int j = cnt; j < last_; j++) {
                tmp = values[j];
                if (tmp == null)
                    break;
                s.push(values[j]);
                depth++;
            }
            last_ = cnt + depth + 1;
            values[cnt] = bValue;
            while (!s.isEmpty()) {
                values[cnt + depth--] = s.pop();
            }
            return ;
        }
        if (status == equalTop)
            if (values[interval.top].containsNode()) {
                BNode child = (BNode) values[interval.top].value;
                child.parent = this;

                child.add(bValue);
                return;
            } else {
                values[interval.top] = bValue;
                return ;
            }
        if (status == equalBottom)
            if (values[interval.bottom].containsNode()) {
                BNode child = (BNode) values[interval.bottom].value;
                child.parent = this;

                return ;

            } else {
                values[interval.bottom] = bValue;
                return ;
            }
        if (status == above) {
            System.arraycopy(values, 0, values, 1, last_);
            values[0] = bValue;
            last_++;
              }


    }

    private void insertLink(BValue bValue) {
        if (bValue == null)
            return ;
        bValue.parent = this;

        if (last_ == 0) {
            values[0] = bValue;
            last_ = 1;
            return;
        }
        Interval interval = getInterval(bValue.key);
        if (interval == null)
            return ;
        int status = interval.getStatus(bValue.key);
        if (status == betweenFinishNull) {
            values[interval.bottom] = bValue;
            last_ = interval.bottom + 1;
            //return interval.bottom;
        }
        if (status == betweenFinish) {
            s.clear();
            BValue tmp;
            int cnt = interval.bottom;
            int depth = 0;
            for (int j = cnt; j < size; j++) {
                tmp = values[j];
                if (tmp == null)
                    break;
                s.push(values[j]);
                depth++;
            }
            last_ = cnt + depth + 1;
            values[cnt] = bValue;
            while (!s.isEmpty()) {
                //     System.out.println(values[cnt+depth-1].key);
                values[cnt + depth--] = s.pop();
            }
            return ;
        }
        if (status == equalTop) {
            values[interval.top] = bValue;
            return ;
        }
        if (status == equalBottom) {
            values[interval.bottom] = bValue;

        }

    }
    
    /**
     * Remove node associated with the key 
     * @param key string.
     * 
     * @return BNode associated with the key
     *    
     */  
    public BNode remove(String key) {
        if (key == null)
            return null;
        if (parent != null)
            parent.cutParent();
        Interval interval = getInterval(key);
        if (interval == null)
            return null;
        int intStatus = interval.getStatus(key);
        switch (intStatus) {
            case 3: {
                // System.out.println("BNode:remove:line 242:key="+key+" case 3");
                if (values[interval.top].containsNode())
                    return ((BNode) values[interval.top].value).remove(key);
                else {
                    String oldKey = values[0].key;
                    System.arraycopy(values, interval.top + 1, values, interval.top, last_ - interval.top);
                    last_--;
                    if (interval.top == 0) {
                        //System.out.println("Parent node:"+parentNode.toString());
                        //  System.out.println("Parent node:"+parentNode.toString());
                        updateParent(oldKey);
                        if (parent != null)
                            parent.remove(key);
                        if (parent != null)
                            parent.remove(key);
                        if (parent != null)
                            return merge();
                    }
                }
            }
            case 4: {
                //System.out.println("BNode:remove:line 242:key="+key+" case 4");
                if (values[interval.bottom] == null)
                    return null;
                if (values[interval.bottom].containsNode())
                    return ((BNode) values[interval.bottom].value).remove(key);
                else {
                   // Object ret = values[interval.bottom].value;
                    System.arraycopy(values, interval.bottom + 1, values, interval.bottom, last_ - interval.bottom);
                    last_--;
                    if (parent != null)
                        parent.remove(key);
                    if (parent != null)
                        return merge();
                }
            }
            case 5: {
                //System.out.println("BNode:remove:line 242:key="+key+" case 5");
                return removeFromParent(key);
            }
            case 6: {
                //  System.out.println("BNode:remove:line 242:key="+key+" case 6");
                if (values[interval.top].containsNode())
                    return ((BNode) values[interval.top].value).remove(key);
                else
                    return null;
            }
            case 8: {
                //System.out.println("BNode:remove:line 242:key="+key+" case 8");
                if (values[interval.top].containsNode()) {
                    //System.out.println();
                    return ((BNode) values[interval.top].value).delete(key);
                } else
                    return null;
            }
        }
        return null;
    }

    BNode delete(String key) {
        if (key == null)
            return null;
       // BNode curParent = parent;
        if (parent != null)
            parent.cutParent();
        Interval interval = getInterval(key);
        if (interval == null)
            return null;
        int intStatus = interval.getStatus(key);
        switch (intStatus) {
            case 3:
                if (values[interval.top].containsNode())
                    return ((BNode) values[interval.top].value).remove(key);
                else {
                    String oldKey = values[0].key;
                    System.arraycopy(values, interval.top + 1, values, interval.top, last_ - interval.top);
                    last_--;
                    if (interval.top == 0) {
                        //System.out.println("Parent node:"+parentNode.toString());
                        //  System.out.println("Parent node:"+parentNode.toString());
                        updateParent(oldKey);
                        if (parent != null)
                            parent.remove(key);

                        if (parent != null)
                            parent.remove(key);
                        // if(parent!=null)
                        //   parent.cutParent();
                        return parent;
                    }
                }

            case 4:
                if (values[interval.bottom] == null)
                    return null;
                if (values[interval.bottom].containsNode())
                    return ((BNode) values[interval.bottom].value).remove(key);
                else {
                    //Object ret = values[interval.bottom].value;
                    System.arraycopy(values, interval.bottom + 1, values, interval.bottom, last_ - interval.bottom);
                    last_--;
                    if (parent != null)
                        parent.remove(key);
                    return parent;
                }
            case 5:
                return removeFromParent(key);
            case 6:
                if (values[interval.top].containsNode())
                    return ((BNode) values[interval.top].value).remove(key);
                else
                    return null;
            case 8:
                if (values[interval.top].containsNode()) {
                    //System.out.println();
                    return ((BNode) values[interval.top].value).remove(key);
                } else
                    return null;
        }
        return null;
    }
    /**
     * Get  an object associated with the key 
     * @param key string.
     * 
     * @return object associated with the key
     *    
     */  
    public Object getObject(String key) {
        if (key == null)
            return null;
        Interval interval = getInterval(key);
        if (interval == null)
            return null;
        int status = interval.getStatus(key);
        if (status == betweenFinishNull)
            if (values[interval.top].containsNode()) {
                return ((BNode) values[interval.top].value).getObject(key);
            } else {
                return null;
            }
        if (status == betweenFinish)
            if (values[interval.top].containsNode()) {
                return ((BNode) values[interval.top].value).getObject(key);
            } else {
                return null;
            }
        if (status == equalTop)
            if (values[interval.top].containsNode()) {
                BNode node = (BNode) values[interval.top].value;
                return node.getObject(key);
            } else {
                return values[interval.top].value;
            }
        if (status == equalBottom)
            if (values[interval.bottom].containsNode()) {
                return ((BNode) values[interval.bottom].value).getObject(key);
            } else {
                return values[interval.bottom].value;
            }
        return null;
    }


    private Interval getInterval(String key) {
        Interval interval = new Interval(0, size - 1);
        int cnt = 0;
        while (interval.divide(key))
            // if(cnt>10)
            //    System.out.println(String.valueOf(cnt));
            if (cnt++ > 10000)
                return null;
        return interval;
    }

    private int add(BValue bValue) {
        if (bValue == null)
            return -1;
        insert(bValue);
        if (getStatus() == shouldBeSplit)
            split();
        return 0;
    }

    private BNode split() {
        if (last_ < splitLevel)
            return this;
//root
        if (parent == null) {
            BNode newParent = new BNode(null, bTree);
            parent = newParent;
            bTree.root = parent;
            newParent.parent = null;
            //boolean leaf = false;

        }
        BValue link = new BValue(values[0].key, this);
        link.noLeaf();
        parent.insertLink(link);
        BNode node = new BNode(parent, bTree);
        for (int i = mergeLevel + threshold; i < last_; i++) {
            if (values[i].containsNode())
                ((BNode) values[i].value).parent = node;
            node.add(values[i]);
            values[i] = null;
        }
        last_ = mergeLevel + threshold;
        link = new BValue(node.values[0].key, node);
        link.noLeaf();
        parent.insertLink(link);

        if (parent.getStatus() == shouldBeSplit)
            return parent.split();
        return parent;
    }
    /**
     * Put  an object associated with the key into the BNode
     * @param key string
     * @param value object
     * 
     * @return  int 0 if successful, -1 if failed
     *    
     */  
    public int put(String key, Object value) {
        BValue bValue = new BValue(key, value);
        return add(bValue);

    }

    private int getStatus() {
        if (last_ < mergeLevel)
            return shouldBeMerged;
        if (last_ <= splitLevel)
            return shouldBeExtended;
        if (last_ > splitLevel)
            return shouldBeSplit;
        if (last_ > size - 2)
            return full;
        return error;

    }
    /**
     * @return the key of the first value
     */  
    public String toString() {
        if (values[0] == null)
            return null;
        return values[0].key;
    }

   private  int getIndex() {
        if (parent == null)
            return -1;
        Interval interval = parent.getInterval(values[0].key);
        int intStat = interval.getStatus(values[0].key);
        if (intStat == equalTop)
            return interval.top;
        if (intStat == equalBottom)
            return interval.bottom;
        return -1;
    }
    /**
     * Check if the node contains the key
     * @param key Key string.
     * 
     * @return true if the node contains the key
     *    
     */  
    public boolean containsKey(String key) {
        Interval interval = getInterval(key);
        int intStat = interval.getStatus(key);
        switch (intStat) {
            case above:
                return false;
            case below:
                return false;
            case equalTop:
                return true;
            case equalBottom:
                return true;
            case empty:
                return false;
            case betweenFinish:
                return values[interval.top].containsNode() && ((BNode) values[interval.top].value).containsKey(key);
            case betweenNull:
                return values[interval.top].containsNode() && ((BNode) values[interval.top].value).containsKey(key);
            case betweenFinishNull:
                return values[interval.top].containsNode() && ((BNode) values[interval.top].value).containsKey(key);
            
        }
        return false;
    }

    private BNode merge() {
        if (parent == null)
            return null;
        if (parent == this)
            return null;
        if (last_ > mergeLevel)
            return null;
        if (parent.last_ + last_ + threshold > splitLevel)
            return null;
        if (last_ > -1) {
            //noinspection ConstantConditions
            if (values == null || values[last_] == null || values[last_].key == null || values[last_].value == null)
                return null;
            //System.out.println("Last:"+values[last_].toString());
            String key$ = values[last_].key;
            Object value = values[last_].value;
            values[last_] = null;
            last_--;
            //System.out.println("Key="+values[0].key +" Value:"+values[0].toString());
            bTree.put(key$, value);
            if (last_ > -1)
                return merge();
            else return null;
        }
        return null;
    }

    private BNode updateParent(String oldKey) {
        if (parent == null || parent == this)
            return null;
        // System.out.println("Update key:"+toString());
        Interval interval = parent.getInterval(oldKey);
        if (interval == null)
            return null;
        if (parent.values[interval.top] == null)
            return null;
        if (values[0] == null)
            return null;
        if (interval.getStatus(oldKey) == 3) {
            parent.values[interval.top].key = values[0].key;
            parent.values[interval.top].value = this;
            return parent.updateParent(oldKey);
        }
        if (interval.getStatus(oldKey) == 4) {
            parent.values[interval.bottom].key = values[0].key;
            parent.values[interval.bottom].value = this;
            return parent.updateParent(oldKey);
        }

        return null;
    }
    private BNode removeFromParent(String oldKey) {
        if (parent == null || parent == this)
            return null;
        //  System.out.println("Update key:"+toString());
        Interval interval = parent.getInterval(oldKey);
        if (interval == null)
            return null;
        if (parent.values[interval.top] == null)
            return null;
        int index = -1;
        if (interval.getStatus(oldKey) == 3)
            index = interval.top;
        if (interval.getStatus(oldKey) == 4)
            index = interval.bottom;
        if (index == -1)
            return null;
//    bTree.removeJnode(parent.values[index]);
        System.arraycopy(parent.values, index + 1, parent.values, index, parent.last_ - index);
        return null;
    }

    void cutParent() {
        if (parent == null)
            return;
        BNode grandParent = parent.parent;
        if (grandParent == null)
            return;
        while (parent.last_ == 1) {
            int index = parent.getIndex();
            if (index == -1)
                return;
            grandParent.values[index].value = this;
            parent = grandParent;
            grandParent = parent.parent;
            if (grandParent == null)
                return;
        }

    }
    /**
     * Check if the string already stored in the stack.
     * @param s The stack collecting names.
     * @param name  The name to check.
     * @return boolean true if the stack already contains the name. 
     */   
    private class Interval {
        int top = 0;
        public Interval(int top, int bottom) {
            this.top = top;
            this.bottom = bottom;
        }        int bottom = size - 1;

       int getStatus(String key) {
            if (key == null)
                return error;
            if (values[top] == null)
                return empty;

            int topStatus = compare(key, values[top].key);

            if (topStatus == 0)
                return equalTop;
            if (topStatus == 2)
                return above;
            if (values[bottom] == null && bottom < top + 2)
                return betweenFinishNull;
            if (values[bottom] == null)
                return betweenNull;
            int bottomStatus = compare(key, values[bottom].key);
            if (bottomStatus == 0)
                return equalBottom;
            if (bottomStatus == 1)
                return below;
            if (bottomStatus == 2) {
                if (bottom - top == 1)
                    return betweenFinish;
                else
                    return between;
            }
            return error;
        }

        int getStatusLike(String key) {
            if (key == null)
                return error;
            if (values[top] == null)
                return empty;

            int topStatus = like(values[top].key, key);

            if (topStatus == 0)
                return equalTop;
            if (topStatus == 2)
                return above;
            if (values[bottom] == null && bottom < top + 2)
                return betweenFinishNull;
            if (values[bottom] == null)
                return betweenNull;
            int bottomStatus = like(values[bottom].key, key);
            if (bottomStatus == 0)
                return equalBottom;
            if (bottomStatus == 1)
                return below;
            if (bottomStatus == 2) {
                if (bottom - top == 1)
                    return betweenFinish;
                else
                    return between;
            }
            return error;
        }

        public boolean divide(String key) {
            if (top >= bottom - 1)
                return false;
            int middle = (bottom - top) / 2 + top;
            if (middle == top && bottom > top + 1)
                middle++;
            if (middle == bottom && top < bottom - 1)
                middle--;
            Interval test = new Interval(top, middle);
            int testStatus = test.getStatus(key);
            if (testStatus == between) {
                bottom = middle;
                return true;
            }
            if (testStatus == betweenFinish) {
                bottom = middle;
                return false;
            }
            if (testStatus == betweenFinishNull) {
                bottom = middle;
                return false;
            }
            if (testStatus == betweenNull) {
                bottom = middle;
                return true;
            }

            if (testStatus == equalBottom) {
                bottom = middle;
                return false;
            }
            if (testStatus == equalTop) {
                bottom = middle;
                return false;
            }
            if (testStatus == below) {
                top = test.bottom;
                return true;
            }
            return false;
        }


    }
}