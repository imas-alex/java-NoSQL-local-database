package gdt.data.extension;
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
import java.net.URLDecoder;
import java.util.logging.Logger;
import gdt.data.entity.facet.ExtensionMain;
import gdt.data.grain.Core;
import gdt.data.grain.Sack;
import gdt.data.store.*;
public class Main implements ExtensionMain{
	 public static final String EXTENSION_KEY="_v6z8CVgemqMI6Bledpc7F1j0pVY";
	 private static final String EXTENSION_LABEL="community";
	 private static final String EXTENSION_JAR="community.jar";
	static boolean debug =false;
	 public void main(String[] args) {
      
		final String[] sa=args;
        if(sa!=null)
       javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              try{
           	//System.out.println("Community:main");
            	  
               String entihome$=sa[0];
               if(debug)
               System.out.println("Community:main.entihome="+entihome$);
               Entigrator entigrator=new Entigrator(new String[]{entihome$});
           //    System.out.println(entigrator.getEntihome());
               makeExtension(entigrator);
               String folder$=entigrator.ent_getHome(EXTENSION_KEY);
               File folder=new File(folder$);
               if(!folder.exists())
            	   folder.mkdir();
               String path$ = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
             // System.out.println("Community:main.path="+path$);
              String jar$ = URLDecoder.decode(path$, "UTF-8");
              jar$=jar$.replace("file:", "");
              jar$=jar$.replace("!/", "");
              File jar=new File(jar$);
           //    System.out.println("Community:main:jar="+jar.getPath());
               File target=new File(folder$+"/"+EXTENSION_JAR);
               if(!target.exists())
            	   target.createNewFile();
               FileExpert.copyFile(jar, target);
        
                }catch(Exception e ){
            	  Logger.getLogger(Main.class.getName()).severe(e.toString());
              }
            }
        });
    }
	private static Sack makeExtension(Entigrator entigrator){
		 Sack extension=entigrator.getEntityAtKey(EXTENSION_KEY);
         if(extension!=null)
        	 entigrator.deleteEntity(extension);
        	  extension=new Sack();
              extension.putAttribute(new Core("String", "residence.base",Entigrator.ENTITY_BASE));
              extension.putAttribute(new Core("String", "alias", EXTENSION_LABEL));
              extension.setKey(EXTENSION_KEY);
              String path$=entigrator.getEntihome()+"/"+Entigrator.ENTITY_BASE+"/data/"+EXTENSION_KEY;
              extension.setPath(path$);
        	  if(!extension.saveXML(path$)){
        		 if(debug)
        		  System.out.println("Main:makeExtension:cannot save extension="+entigrator.getEntihome()+"/"+Entigrator.ENTITY_BASE+"/data/"+EXTENSION_KEY) ;
        		  return null;
        	  }
        	  entigrator.ent_reindex(extension);
              extension=entigrator.ent_assignLabel(extension, EXTENSION_LABEL);
              extension=entigrator.ent_assignProperty(extension, "entity", "extension");
              extension=entigrator.ent_assignProperty(extension, "extension",EXTENSION_LABEL);
         
         if(!extension.existsElement("fhandler"))
        	 extension.createElement("fhandler");
         else
        	 extension.clearElement("fhandler");
         extension.putElementItem("fhandler", new Core(null,"gdt.data.entity.facet.ExtensionHandler",null));
         extension.putElementItem("jfacet", new Core(null,"gdt.data.entity.facet.FieldsHandler",null));
         if(!extension.existsElement("jfacet"))
        	 extension.createElement("jfacet");
         else
        	 extension.clearElement("jfacet");
         extension.putElementItem("jfacet", new Core(null,"gdt.data.entity.facet.ExtensionHandler","gdt.jgui.entity.extension.JExtensionFacetOpenItem"));
         extension.putElementItem("jfacet", new Core("gdt.jgui.entity.fields.JFieldsFacetAddItem","gdt.data.entity.facet.FieldsHandler","gdt.jgui.entity.fields.JFieldsFacetOpenItem"));
         if(!extension.existsElement("field"))
        	 extension.createElement("field");
         else
        	 extension.clearElement("field");
         extension.putElementItem("field", new Core(null,"lib","community.jar"));
         extension.putElementItem("field", new Core(null,"res","community.tar"));
         entigrator.ent_assignProperty(extension, "fields",EXTENSION_LABEL );
         if(!extension.existsElement("content.fhandler"))
        	 extension.createElement("content.fhandler");
         else
        	 extension.clearElement("content.fhandler");
         extension.putElementItem("content.fhandler", new Core(null,"gdt.data.entity.PhoneHandler",EXTENSION_KEY));
         extension.putElementItem("content.fhandler", new Core(null,"gdt.data.entity.EmailHandler",EXTENSION_KEY));
         extension.putElementItem("content.fhandler", new Core(null,"gdt.data.entity.PersonHandler",EXTENSION_KEY));
         extension.putElementItem("content.fhandler", new Core(null,"gdt.data.entity.AddressHandler",EXTENSION_KEY));
         extension.putElementItem("content.fhandler", new Core(null,"gdt.data.entity.BankHandler",EXTENSION_KEY));
         extension.putElementItem("content.fhandler", new Core(null,"gdt.data.entity.ContactHandler",EXTENSION_KEY));
         if(!extension.existsElement("content.jfacet"))
        	 extension.createElement("content.jfacet");
         else
        	 extension.clearElement("content.jfacet");
         extension.putElementItem("content.jfacet", new Core("gdt.jgui.entity.phone.JPhoneFacetAddItem","gdt.data.entity.PhoneHandler","gdt.jgui.entity.phone.JPhoneFacetOpenItem"));
         extension.putElementItem("content.jfacet", new Core("gdt.jgui.entity.email.JEmailFacetAddItem","gdt.data.entity.EmailHandler","gdt.jgui.entity.email.JEmailFacetOpenItem"));
         extension.putElementItem("content.jfacet", new Core("gdt.jgui.entity.person.JPersonFacetAddItem","gdt.data.entity.PersonHandler","gdt.jgui.entity.person.JPersonFacetOpenItem"));
         extension.putElementItem("content.jfacet", new Core("gdt.jgui.entity.address.JAddressFacetAddItem","gdt.data.entity.AddressHandler","gdt.jgui.entity.address.JAddressFacetOpenItem"));
         extension.putElementItem("content.jfacet", new Core("gdt.jgui.entity.bank.JBankFacetAddItem","gdt.data.entity.BankHandler","gdt.jgui.entity.bank.JBankFacetOpenItem"));
         extension.putElementItem("content.jfacet", new Core("gdt.jgui.entity.contact.JContactFacetAddItem","gdt.data.entity.ContactHandler","gdt.jgui.entity.contact.JContactFacetOpenItem"));
         if(!extension.existsElement("content.jrenderer"))
        	 extension.createElement("content.jrenderer");
         else
        	 extension.clearElement("content.jrenderer");
         extension.putElementItem("content.jrenderer", new Core(null,"gdt.data.entity.PhoneHandler","gdt.jgui.entity.phone.JPhoneEditor"));
         extension.putElementItem("content.jrenderer", new Core(null,"gdt.data.entity.EmailHandler","gdt.jgui.entity.email.JEmailEditor"));
         extension.putElementItem("content.jrenderer", new Core(null,"gdt.data.entity.PersonHandler","gdt.jgui.entity.person.JPersonEditor"));
         extension.putElementItem("content.jrenderer", new Core(null,"gdt.data.entity.AddressHandler","gdt.jgui.entity.address.JAddressEditor"));
         extension.putElementItem("content.jrenderer", new Core(null,"gdt.data.entity.BankHandler","gdt.jgui.entity.bank.JBankEditor"));
         extension.putElementItem("content.jrenderer", new Core(null,"gdt.data.contact.ContactHandler","gdt.jgui.entity.contact.JContactEditor"));
         if(!extension.existsElement("content.jrenderer"))
        	 extension.createElement("content.super");
         else
        	 extension.clearElement("content.super");
         extension.putElementItem("content.super", new Core(null,"gdt.jgui.entity.phone.JPhoneFacetOpenItem","gdt.jgui.console.JFacetOpenItem"));
         extension.putElementItem("content.super", new Core(null,"gdt.jgui.entity.phone.JPhoneFacetAddItem","gdt.jgui.console.JFacetAddItem"));
         extension.putElementItem("content.super", new Core(null,"gdt.jgui.entity.person.JPersonFacetOpenItem","gdt.jgui.console.JFacetOpenItem"));
         extension.putElementItem("content.super", new Core(null,"gdt.jgui.entity.person.JPersonFacetAddItem","gdt.jgui.console.JFacetAddItem"));
         extension.putElementItem("content.super", new Core(null,"gdt.jgui.entity.email.JEmailFacetOpenItem","gdt.jgui.console.JFacetOpenItem"));
         extension.putElementItem("content.super", new Core(null,"gdt.jgui.entity.email.JEmailFacetAddItem","gdt.jgui.console.JFacetAddItem"));
         extension.putElementItem("content.super", new Core(null,"gdt.jgui.entity.contact.JContactFacetOpenItem","gdt.jgui.console.JFacetOpenItem"));
         extension.putElementItem("content.super", new Core(null,"gdt.jgui.entity.contact.JContactFacetAddItem","gdt.jgui.console.JFacetAddItem"));
         extension.putElementItem("content.super", new Core(null,"gdt.jgui.entity.bank.JBankFacetOpenItem","gdt.jgui.console.JFacetOpenItem"));
         extension.putElementItem("content.super", new Core(null,"gdt.jgui.entity.bank.JBankFacetAddItem","gdt.jgui.console.JFacetAddItem"));
         extension.putElementItem("content.super", new Core(null,"gdt.jgui.entity.address.JAddressFacetOpenItem","gdt.jgui.console.JFacetOpenItem"));
         extension.putElementItem("content.super", new Core(null,"gdt.jgui.entity.address.JAddressFacetAddItem","gdt.jgui.console.JFacetAddItem"));
                  return extension;
	}
}
