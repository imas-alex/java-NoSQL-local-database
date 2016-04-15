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
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;
/**
* Represents non-specific object having unique identifier.
* An identity structure has two string fields: 
* key - the unique identifier
* path - the path of the file where the identity is stored.
* The path can be null. 
*/
public class Identity {
    String path;
    String key;
    public Identity() {
        key = newKey();
    }
    public Identity(String key, String path) {
        this.key = key;
        this.path = path;
    }
    /**
 	 * Generate a unique key  
 	 * @return key string.
 	 */	
    public static String key() {
        return new Identity().newKey();
    }
    /**
 	 * Get a path  
 	 * @return path string.
 	 */	
    public String getPath() {
        return path;
    }
    /**
 	 * Set a path  
 	 *  @param path the path string. 
 	 */	
    public void setPath(String path) {
        this.path = path;
    }
    /**
 	 * Get a key  
 	 * @return key string.
 	 */	
    public String getKey() {
        return key;
    }
    /**
 	 * Set a key  
 	 *  @param key the key string. 
 	 */	
    public void setKey(String key) {
        this.key = key;
    }

    private String newKey() {
        String raw$ = new Object().hashCode() + String.valueOf((System.currentTimeMillis()));
        String pid$ = System.getProperty("JVM_PID");
        if (pid$ != null)
            raw$ = raw$ + pid$;
        java.security.MessageDigest md;
        try {
            md = java.security.MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e.toString());
            return null;
        }
        md.update(raw$.getBytes());
        byte[] digest = md.digest();
        String out$ = DatatypeConverter.printBase64Binary(digest);
        out$ = out$.replaceAll("/", "_S");
        out$ = out$.replace('+', '_');
        out$ = out$.replace('=', ' ').trim();
        return "_" + out$;
    }
}
