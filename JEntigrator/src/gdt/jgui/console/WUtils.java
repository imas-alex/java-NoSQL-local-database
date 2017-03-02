package gdt.jgui.console;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.commons.codec.binary.Base64;

import gdt.data.grain.Sack;
import gdt.data.grain.Support;
import gdt.data.store.Entigrator;
import gdt.jgui.entity.JEntitiesPanel;

public class WUtils {
static boolean debug=false;	

public static String getMenuBarStyle(){
	return "<style>	ul.menu_list {list-style-type: none; margin: 0;padding: 0; overflow: hidden; background-color: #333;	}	li.menu_item {float: left;} li a, .dropbtn { display: inline-block;color: white; text-align: center;padding: 14px 16px; text-decoration: none;}li a:hover, .dropdown:hover .dropbtn { background-color: red;}	li.dropdown {display: inline-block;} .dropdown-content { display: none; position: absolute; background-color: #f9f9f9; min-width: 160px; box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2);}.dropdown-content a { color: black;padding: 12px 16px; text-decoration: none; display: block; text-align: left;}	.dropdown-content a:hover {background-color: #f1f1f1} .show {display:block;} .dropdown:hover .dropdown-content {display: block;}</style>";
}
public static String getJquery(Entigrator entigrator){
	StringBuffer sb=new StringBuffer();	
	sb.append("<script>");
	sb.append(getSegment(entigrator,"jquery","js"));
	sb.append("</script>");
	return sb.toString();
	
}
public static String getStringBuffer(){
	StringBuffer sb=new StringBuffer();	
	sb.append("<script>");
	sb.append("function StringBuffer() {");
	sb.append("this.__strings__ = new Array;");
	sb.append("}");

	sb.append("StringBuffer.prototype.append = function (str) {");
	sb.append("this.__strings__.push(str);");
	sb.append("};");

	sb.append("StringBuffer.prototype.toString = function () {");
	sb.append("return this.__strings__.join(\"\");");
	sb.append("};");
	sb.append("</script>");
	sb.append("return sb.toString();");
	return sb.toString();
}
public static String getArrayToString(){
	StringBuffer sb=new StringBuffer();	
	sb.append("<script>");
	sb.append("function arrayToString(params) {");
	sb.append("return params.join(\"_;A_\");");
	sb.append("}");
	return sb.toString();
}
public static String getSegment(Entigrator entigrator,String folder$,String segment$){
	try{
	StringBuffer sb=new StringBuffer();	

	Sack folder=entigrator.getEntityAtKey(entigrator.indx_keyAtLabel(folder$));
	String file$=folder.getElementItemAt("field",segment$);
	File file=new File(entigrator.getEntihome()+"/"+folder.getKey()+"/"+file$);
	FileReader fr=new FileReader(file);
	BufferedReader reader =new BufferedReader(fr);
	String line$;
    while ((line$ = reader.readLine()) != null) {
    	sb.append(line$);
     }
    reader.close();
	return sb.toString();
	}catch(Exception e){
		Logger.getLogger(WUtils.class.getName()).severe(e.toString());
	}
	return null;
}

public static String getJstree(Entigrator entigrator){
	try{
	StringBuffer sb=new StringBuffer();	
		
	Sack jstree=entigrator.getEntityAtKey(entigrator.indx_keyAtLabel("jstree"));
	String jsTree$=jstree.getElementItemAt("field", "js");
	String jsCss$=jstree.getElementItemAt("field", "css");
	String theme$=jstree.getElementItemAt("field", "theme");
	File jsTree=new File(entigrator.getEntihome()+"/"+jstree.getKey()+"/"+jsTree$);
	FileReader fr=new FileReader(jsTree);
	BufferedReader   reader =new BufferedReader(fr);
	String line$;
	sb.append("<script>");
	while ((line$ = reader.readLine()) != null) {
    	sb.append(line$);
         //System.out.println("Page:base="+line$);
     }
    reader.close();
    sb.append("</script>");
	FileInputStream fis=new FileInputStream(new File(entigrator.getEntihome()+"/"+jstree.getKey()+"/"+theme$));
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
	            byte[] b = new byte[1024];
	            int bytesRead = 0;
	            while ((bytesRead = fis.read(b)) != -1) {
	               bos.write(b, 0, bytesRead);
	            }
	            byte[] ba = bos.toByteArray();
	            fis.close();
	            bos.close();
    String themeIcon$=Base64.encodeBase64String(ba);       
   String throbber$=jstree.getElementItemAt("field", "throbber");
	fis=new FileInputStream(new File(entigrator.getEntihome()+"/"+jstree.getKey()+"/"+throbber$));
		bos = new ByteArrayOutputStream();
	    b = new byte[1024];
	    bytesRead = 0;
	            while ((bytesRead = fis.read(b)) != -1) {
	               bos.write(b, 0, bytesRead);
	            }
	    ba = bos.toByteArray();
	            fis.close();
	            bos.close();
	    String throbberIcon$=Base64.encodeBase64String(ba);  
		File jsCss=new File(entigrator.getEntihome()+"/"+jstree.getKey()+"/"+jsCss$);
			fr=new FileReader(jsCss);
		reader =new BufferedReader(fr);
		sb.append("<style>");	
			    while ((line$ = reader.readLine()) != null) {
			    	if(line$.contains("32px.png")){
			          //System.out.println("JIndexFacetOpenItem:getWebView:style="+line$);
			          line$=line$.replace("url(\"32px.png\")", "url(\"data:image/png;base64,"+themeIcon$+"\");");
			          //System.out.println("JIndexFacetOpenItem:getWebView:after="+line$);
			    	}
			    	if(line$.contains("throbber.gif")){
				        //  System.out.println("JIndexFacetOpenItem:getWebView:style="+line$);
				          line$=line$.replace("url(\"throbber.gif\")", "url(\"data:image/gif;base64,"+throbberIcon$+"\");");
				        //  System.out.println("JIndexFacetOpenItem:getWebView:after="+line$);
				    	}
			    	sb.append(line$);
			     }
			    reader.close();
				sb.append("</style>");
				return sb.toString();
	}catch(Exception e){
		Logger.getLogger(WUtils.class.getName()).severe(e.toString());
	}
	return null;
}
public static String getMenuBarScript(){
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
	 sb.append("function showBack(requester$) {");
	 sb.append("var locator=window.localStorage.getItem(requester$);"); 
	 if(debug)
		 sb.append("console.log('showBack:requester ='+requester$+' locator='+locator);");
	 sb.append("if (!locator){"); 
	 sb.append("document.getElementById(\"back\").setAttribute(\"href\",window.location.href );");
	 sb.append("}");
	 sb.append("else{");
	 sb.append(" var res = document.URL.split(\"?web_locator=\");");
	 sb.append(" var backUrl=res[0]+\"?web_locator=\"+locator;");
	 if(debug)
		 sb.append("console.log('showBack:back url='+backUrl);");
	 sb.append("document.getElementById(\"back\").setAttribute(\"href\",backUrl);");
	 sb.append("}");
	 sb.append("}");
	 sb.append("function initBack(handler$,requester$) {");
	 if(debug)
		 sb.append("console.log('web requester='+requester$);");
	 sb.append("if(handler$!=requester$){");
	 if(debug)
	    sb.append("console.log('get handler at web requester='+requester$);");
	 sb.append("var backHandler=requester$;");
	 if(debug)   
	    sb.append("console.log('got back handler='+backHandler);");
	 sb.append("}else{");
	 sb.append("backHandler=window.localStorage.getItem(\"back.\"+handler$);");
	 sb.append("}");
	 if(debug)   
	    sb.append("console.log('back handler='+backHandler);");
	
	 sb.append("var locator=window.localStorage.getItem(requester$);");
	 if(debug)
		 sb.append("console.log('initBack:requester ='+requester$+' locator='+locator);");
	 sb.append("if (!locator){"); 
	 sb.append("document.getElementById(\"back\").setAttribute(\"href\",window.location.href );");
	 sb.append("}");
	 sb.append("else{");
	 sb.append(" var res = document.URL.split(\"?web_locator=\");");
	 sb.append(" var backUrl=res[0]+\"?web_locator=\"+locator;");
	 if(debug)
		 sb.append("console.log('showBack:back url='+backUrl);");
	 sb.append("document.getElementById(\"back\").setAttribute(\"href\",backUrl);");
	 sb.append("}");
	 sb.append("}");
	 sb.append("</script>");
	 return sb.toString();
}
public static String scaleIcon(String icon$){
	try{ 
	if(icon$==null)
		icon$=Support.readHandlerIcon(null,JEntitiesPanel.class, "icon.png");
		byte[] ba=Base64.decodeBase64(icon$);
		BufferedImage origin=ImageIO.read(new ByteArrayInputStream(ba));
	  BufferedImage bi = new BufferedImage(24, 24, BufferedImage.TYPE_INT_RGB);
      Graphics2D g2d = (Graphics2D)bi.createGraphics();
      g2d.setColor(Color.WHITE);
      g2d.fillRect(0, 0, 24, 24);
      g2d.drawImage(origin, 0, 0, 24,24, null);
   	  ByteArrayOutputStream baos=new ByteArrayOutputStream();
	  ImageIO.write(bi, "png", baos );
	  g2d.dispose();
	  ba=baos.toByteArray();
//	  System.out.println("WUtils:scaleIcon:encoded="+Base64.encodeBase64String(ba));
	  return Base64.encodeBase64String(ba);
	}catch(Exception e){
		Logger.getLogger(WUtils.class.getName()).severe(e.toString());
	}
	return null;
}
}
