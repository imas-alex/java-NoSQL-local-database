package gdt.jgui.entity;
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
import java.util.Properties;
import java.util.Stack;
import java.util.logging.Logger;

import gdt.data.entity.EntityHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JFacetRenderer;
import gdt.jgui.console.JMainConsole;
/**
 * This class represents an entity within the referenced entities list. 
 * @author imasa
 *
 */
public class JReferenceEntry extends Core{
	private static final String LOCATOR_TYPE_REFERENCE="locator type reference";
	private static final String FACET_CLASS_NAME="facet class name";
	private static final String BREAK_PROCEEDING="break proceeding";
	public static final String ORIGIN_ENTIHOME="origin_entihome";
	static boolean debug=false;
    public JReferenceEntry(Entigrator entigrator,String entityKey$,String facetClassName$){
 	   type=entigrator.getEntihome();
 	   name=entityKey$;
 	   Properties locator=new Properties();
 	   locator.setProperty(Locator.LOCATOR_TYPE, LOCATOR_TYPE_REFERENCE);
 	   locator.setProperty(Locator.LOCATOR_TITLE, entityKey$);
 	   locator.setProperty(FACET_CLASS_NAME, facetClassName$);
 	  locator.setProperty(Entigrator.ENTIHOME, entigrator.getEntihome());
 	 locator.setProperty(EntityHandler.ENTITY_KEY, entityKey$);
 	   locator.setProperty(facetClassName$,Locator.LOCATOR_TRUE);
 	   value=Locator.toString(locator);
 	   Sack entity=entigrator.getEntityAtKey(entityKey$);
 	   if(entity==null)
 		  value=Locator.append(value, BREAK_PROCEEDING,Locator.LOCATOR_TRUE);
    }
 /**
  * Get the reference entry for the entity
  * @param entigrator the entigrator.
  * @param entityKey$ the key of entity
  * @param rel the referenced entities list.
  * @return the reference entry.
  */
  
    public static  JReferenceEntry getReference(Entigrator entigrator,String entityKey$, ArrayList< JReferenceEntry>rel){
	   try{  
		//   System.out.println("JReferenceEntry:getReference:entity="+entityKey$);
		   if(entityKey$==null)
			   return null;
		   for(JReferenceEntry re : rel){
	            if(entityKey$.equals(re.name)){
	      //      	System.out.println("JReferenceEntry:getReference:value="+re.value);
	            	return re;
	            }
	        }
 		   JReferenceEntry  re=new JReferenceEntry(entigrator,entityKey$,JEntityPrimaryMenu.class.getName());
           putReference(re, rel);	      
       	   return re;
	   }catch(Exception e){
		   Logger.getLogger(JReferenceEntry.class.getName()).severe(e.toString());   
	    }
	   return null;
   }
  /**
   * Put the reference entry into the list.
   * @param jre the reference entry.
   * @param rel the referenced entities list.
   */
    public static  void putReference(JReferenceEntry jre, ArrayList< JReferenceEntry>rel){
	   try{     
	   for(JReferenceEntry re : rel){
	            if(jre.name.equals(re.name)){
	            	rel.remove(re);
	            	break;
	            }
	        }
	   rel.add(jre);
	   }catch(Exception e){
		   Logger.getLogger(JReferenceEntry.class.getName()).severe(e.toString());   
	    }
	   }
 /**  
  * Get all bound entities.
  * @param console the main console.
  * @param entigrator the entigrator.
  * @param sa the array of key of origin entities.
  * @return an array of all bound entities.
  */
  
   public static String[] getCoalition(JMainConsole console,Entigrator entigrator,String [] sa){
	   ArrayList< JReferenceEntry>rel= collectReferences(console, entigrator, sa);
	   ArrayList< String>sl=new ArrayList< String>();
	   String entityKey$;
	   for(JReferenceEntry re:rel){
		   entityKey$=Locator.getProperty(re.value, EntityHandler.ENTITY_KEY);
		   if(entityKey$!=null)
			   sl.add(entityKey$);
	   }
	   return sl.toArray(new String[0]);
	} 
   /**
    * Get all bound entities.
     @param console the main console.
  * @param entigrator the entigrator.
  * @param sa the array of key of origin entities.
  * @return a list of all bound entities.
    */
   public  static  ArrayList< JReferenceEntry> collectReferences(JMainConsole console, Entigrator entigrator,String[] sa){
	   ArrayList< JReferenceEntry>rel=new ArrayList< JReferenceEntry>();
	   try{
			for(String s:sa){
				try{
				if(debug)
				System.out.println("JReferenceEntry:collectReferences:entity key="+s);
				getReference(entigrator, s, rel);
				}catch(Exception ee){
					if(debug)
						System.out.println("JReferenceEntry:collectReferences:ee="+ee.toString());
				}
						
			}
			
	
			boolean done;
			Stack <JReferenceEntry> res=new Stack<JReferenceEntry>();
			JReferenceEntry jre;
			int cnt=rel.size();
			do{
				  for(JReferenceEntry re:rel)
                   res.push(re);
			  while(!res.isEmpty()){
				  jre=res.pop();
				  jre.processEntity(console, entigrator, rel);
			  }
			  if(cnt<rel.size()){
				  cnt=rel.size();
				  done=false;
			  }else
				  done=true;
			 }while(!done);
			
		   }catch(Exception e){
			   Logger.getLogger(JReferenceEntry.class.getName()).severe((e.toString()));
		   }   
	   return rel;
   }
   
   private  void processEntity(JMainConsole console, Entigrator entigrator, ArrayList< JReferenceEntry>rel){
	   try{
//		   System.out.println("JReferenceEntry:processEntity:entity key="+name+" value="+value);
		  Sack entity=entigrator.getEntityAtKey(name);
		  String[] sa=entity.elementList("fhandler");
		  String extensionKey$;
		  if(sa!=null){
			  Properties locator =Locator.toProperties(value); 
			  for(String s:sa){
				if(locator.getProperty(s)==null){
					processFacet(entigrator,s,rel);
					extensionKey$=entity.getElementItemAt("fhandler", s);
				//	System.out.println("JReferenceEntry:processEntity:extension key="+extensionKey$);
					if(extensionKey$!=null&&!"null".equals(extensionKey$))
						getReference(entigrator, extensionKey$, rel).processEntity(console, entigrator, rel);					}
				}
			  }
		  
		  Core[] ca=entity.elementGet("component");
			if(ca!=null){
				for(Core c:ca)
					getReference(entigrator, c.value, rel);
			}
		 
	   }catch(Exception e){
		   Logger.getLogger(JReferenceEntry.class.getName()).severe((e.toString()));
		   //Locator.append(value, BREAK_PROCEEDING, Locator.LOCATOR_TRUE);
	   } 
	   Locator.append(value, BREAK_PROCEEDING, Locator.LOCATOR_TRUE);  
	   putReference(this, rel);
   }
   private  void processFacet(Entigrator entigrator,String facetHandler$, ArrayList< JReferenceEntry>rel){
	   try{
		//   System.out.println("JReferenceEntry:processFacet:entity key="+name+" facet="+facetHandler$+" entihome="+entigrator.getEntihome());
		   JFacetRenderer facetRenderer=JConsoleHandler.getFacetRenderer(entigrator, facetHandler$);
		   facetRenderer.collectReferences(entigrator, name,rel);
	   }catch(Exception e){
		   Logger.getLogger(JReferenceEntry.class.getName()).severe((e.toString()));
	   }
	   Locator.append(value, facetHandler$,Locator.LOCATOR_TRUE);
   }
}
