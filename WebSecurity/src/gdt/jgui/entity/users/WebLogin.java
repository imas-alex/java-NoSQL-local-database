package gdt.jgui.entity.users;

import java.util.Properties;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;

import gdt.data.grain.Locator;
import gdt.data.store.Entigrator;

import gdt.jgui.base.JBasesPanel;
import gdt.jgui.console.WContext;
import gdt.jgui.console.WUtils;
public class WebLogin implements WContext{
	public static String USER="user";
	public static String PASSWORD="password";
	String entihome$;
	boolean debug=false;

	@Override
	public String getWebView(Entigrator entigrator, String locator$) {
		try{
			if(debug)
				System.out.println("JWebLogin:BEGIN:locator="+locator$);
				
			Properties locator=Locator.toProperties(locator$);
			String webHome$=locator.getProperty(WContext.WEB_HOME);
			String webRequester$=locator.getProperty(WContext.WEB_REQUESTER);
			String user$=locator.getProperty(USER);
			String password$=locator.getProperty(PASSWORD);
			if(debug)
			System.out.println("JWebLogin:user="+user$+" password="+password$+" web home="+webHome$+ " web requester="+webRequester$);
			// String icon$=Support.readHandlerIcon(null,JBaseNavigator.class, "base.png");
			StringBuffer sb=new StringBuffer();
			sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
			sb.append("<html>");
			sb.append("<head>");
			//sb.append(WUtils.getJquery(entigrator));
			sb.append(WUtils.getMenuBarScript());
			//sb.append(WUtils.getMenuBarStyle());
		    sb.append("</head>");
		    sb.append("<body onload=\"onLoad()\" >");
		    sb.append("<table><tr><td>Base:</td><td><strong>");
		    sb.append(entigrator.getBaseName());
		    sb.append("</strong></td></tr>");
		    /*
		    sb.append("<tr><td>Context:</td><td><strong>");
		    	    sb.append("Search for label");
		    	    sb.append("</strong></td></tr>");
		    	    */
		    sb.append("</table>");
		    sb.append("<table>");
		    sb.append("<tr>");
		    sb.append("<td>User:</td>");
		    sb.append("<td><input type=\"text\" id=\"honeypot\"  ></td>");
		    sb.append("</tr>");
		    
		    sb.append("<tr>");
		    sb.append("<td>Password:</td>");
		    sb.append("<td><input type=\"password\" id=\"hidden\"  ></td>");
		    sb.append("</tr>");
		    sb.append("<tr>");
		    sb.append("<td></td>");
		    sb.append("<td><input type=\"text\" id=\"login\"  ></td>");
		    sb.append("</tr>");
		    sb.append("<tr><td></td>");
		    sb.append("<td><button onclick=\"submit()\">Submit</button></td>");
		    sb.append("<td>");
		    sb.append("</tr>");
		    int cnt=0;
		   
		   
		    sb.append("</tr></table>");
		    sb.append("<script>");
		    sb.append("document.getElementById(\"login\").style.display='none';");
		    
		    sb.append("function submit() {");
		    sb.append(" var user = document.getElementById(\"honeypot\").value;");
		    sb.append(" var password=document.getElementById(\"hidden\").value;");
		    sb.append(" var locator=\""+locator$+"\";");
		    sb.append("window.localStorage.setItem(\""+WebLogin.USER+"\",user);");
		    sb.append("window.localStorage.setItem(\""+WebLogin.PASSWORD+"\",password);");
		    sb.append("locator=appendProperty(locator,\""+USER+"\",user);");
		    sb.append("locator=appendProperty(locator,\""+PASSWORD+"\",password);");
		    String urlHeader$=webHome$+"?"+WContext.WEB_LOCATOR+"=";
		    sb.append("console.log(locator);");
		    sb.append("var url=\""+urlHeader$+"\"+window.btoa(locator);");
		    sb.append("window.location.assign(url);");
		    sb.append("}");
		      
		    sb.append("function onLoad() {");
		    sb.append("initBack(\""+this.getClass().getName()+"\",\""+webRequester$+"\");");
		    //if(input$!=null)
		    //	sb.append("document.getElementById(\"label\").value=\""+input$+"\";");
		    //sb.append("document.getElementById(\"login\").style.display='none';");   
		    sb.append("}");
		    sb.append("window.localStorage.setItem(\""+this.getClass().getName()+"\",\""+Base64.encodeBase64URLSafeString(locator$.getBytes())+"\");");
		    
	 	    sb.append("</script>");
		    sb.append("</body>");
		    sb.append("</html>");
		    return sb.toString();
		}catch(Exception e){
			Logger.getLogger(JBasesPanel.class.getName()).severe(e.toString());	
		}
		return null;
	}

	@Override
	public String getWebConsole(Entigrator entigrator, String locator$) {
		// TODO Auto-generated method stub
		return null;
	}

}
