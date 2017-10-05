

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;

import gdt.data.entity.BaseHandler;
import gdt.data.grain.Locator;
import gdt.data.store.Entigrator;
import gdt.jgui.base.JBasesPanel;
import gdt.jgui.console.JConsoleHandler;
import gdt.jgui.console.JItemsListPanel;
import gdt.jgui.console.WContext;

/**
 * Servlet implementation class ’entry'. Provides web access to the JEntigrator database management system. 
 */
@WebServlet(name = "entry", description = "The app servlet", urlPatterns = { "/" })
public class entry extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final String PAGE_METHOD="page method";
	private final String PAGE_METHOD_DOWNLOAD="page method download";
	private final String PAGE_METHOD_OPEN="page method open";
	private static final String FILE_PATH="file path";
	private static final String SECURITY_KEY="__ZZMb_JuVLoqq1Jgk8AMsuK2oZ0";
	private Hashtable<String,Entigrator>entigrators;  
	boolean debug=false;
    /**
     Default constructor.
     */
    public entry() {
        super();
        entigrators=new Hashtable<String,Entigrator>();
    }
    private Entigrator getEntigrator(String entihome$){
    	try{
    	Entigrator entigrator= entigrators.get(entihome$);
    	if(entigrator==null){
    		entigrator =new Entigrator(new String[]{entihome$});
    		if(entigrator!=null){
    			putEntigrator(entigrator);
    		}
    		}
    	return entigrator;
    	}catch(Exception e){
    		Logger.getLogger(getClass().getName()).severe(e.toString());
    		return null;
    	}
    }
	private void putEntigrator(Entigrator entigrator){
	if(entigrators==null)
		entigrators=new Hashtable<String,Entigrator>();
	entigrators.put(entigrator.getEntihome(),entigrator);
	
}
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 response.setContentType("text/html; charset=UTF-8");
		 String response$=request.getParameter(WContext.WEB_LOCATOR);
		 if(debug)
		 System.out.println("entry:response ="+response$);
		 FileReader fr=new FileReader(new File(getServletContext().getRealPath("/bases")));
	     BufferedReader reader =new BufferedReader(fr);
	     ArrayList<String> al=new ArrayList<String>();
	     String line$;
	     while ((line$ = reader.readLine()) != null) {
	         al.add(line$);
	         if(debug)
	         System.out.println("entry:base="+line$);
	     }
	     reader.close();
	     String[] bases=al.toArray(new String[0]);
	     String bases$=Locator.toString(bases);
	     if(response$==null){
	    	 Properties locator=new Properties();
	    	 locator.setProperty(BaseHandler.HANDLER_CLASS, JBasesPanel.class.getName());
	    	 locator.setProperty(WContext.WEB_HOME,request.getRequestURL().toString());
	    	 locator.setProperty(BaseHandler.HANDLER_METHOD, "getWebView");
	    	 locator.setProperty(WContext.BASES,bases$);
	    	 JBasesPanel bp=new JBasesPanel();
	    	 PrintWriter out = response.getWriter();
	    	 out.println(bp.getWebView(null,Locator.toString(locator)));
	    	 
	    	 return;
	     }else{
	    	 
	    	 String locator$=null;	     
	    	try{
	    	 byte[] ba= Base64.decodeBase64(response$);
	    	 locator$=new String(ba,"UTF-8");
	    	if(debug)
	    	 System.out.println("entry:response locator="+locator$);
	    	}catch(Exception e){
	    		if(debug)
	    		 System.out.println("entry:"+e.toString());
	    		 Properties locator=new Properties();
		    	 locator.setProperty(BaseHandler.HANDLER_CLASS, JBasesPanel.class.getName());
		    	 locator.setProperty(WContext.WEB_HOME,request.getRequestURL().toString());
		    	 locator.setProperty(BaseHandler.HANDLER_METHOD, "getWebView");
		    	 locator.setProperty(WContext.BASES,bases$);
		    	 JBasesPanel bp=new JBasesPanel();
		    	 PrintWriter out = response.getWriter();
		    	 out.println(bp.getWebView(null,Locator.toString(locator)));
		    	 return;
	    	}
	     Properties locator=Locator.toProperties(locator$);
	    	 ///
	    	 String entihome$=locator.getProperty(Entigrator.ENTIHOME);
	    	 if(debug)
	    	 System.out.println("entry:entihome="+entihome$); 
	    	 Entigrator entigrator=getEntigrator(entihome$);
	    	 
	    	 
	   if(entigrator.ent_existsAtKey(SECURITY_KEY)){
		   //
		  String user$=locator.getProperty("user");
		  String password$=locator.getProperty("password");
		  HttpSession session=request.getSession();
		  if(password$!=null&&user$!=null){
			  session.setAttribute("user", user$);
			  session.setAttribute("password", password$);
		  }
		  Enumeration<String> es=session.getAttributeNames();
		 while(es.hasMoreElements()){
			  if(debug)
			    	 System.out.println("entry:session attribute="+es.nextElement());
			  user$=(String)session.getAttribute("user");
			  password$=(String)session.getAttribute("password");
			  locator$=Locator.append(locator$, "user", user$);
			  locator$=Locator.append(locator$, "password", password$);
		 }
		  //
		   boolean isAuthorized=false;
		   URLClassLoader cl;
		   PrintWriter out = response.getWriter();
		   try{
		   ClassLoader parentLoader = JItemsListPanel.class.getClassLoader();
 	    	File securityJar=new File(entihome$+"/"+SECURITY_KEY+"/security.jar");
    		URL[] urls = { new URL( "jar:file:" + securityJar.getPath()+"!/")};
	    	cl = new URLClassLoader(urls,parentLoader);
        	Class<?> cls = cl.loadClass("gdt.jgui.entity.users.JUsersManager");
	    	Method method = cls.getMethod("isAuthorized", entigrator.getClass(),locator$.getClass() );
	    	isAuthorized=(boolean) method.invoke(null,entigrator,locator$);
	    	if(debug)
	    	    	 System.out.println("entry:security:is authorized="+isAuthorized);  
	    	 cl.close();
	    	}catch(Exception ee){
	    		 if(debug)
	    	    	 System.out.println("entry:security:"+ee.toString());
	    		 out.print(ee.toString());
	    		 return;
	    	}
	    	if(!isAuthorized){
	    		
	    			try{
	    				   
	    				    ClassLoader parentLoader = WContext.class.getClassLoader();
	    		 	    	File securityJar=new File(entihome$+"/"+SECURITY_KEY+"/security.jar");
	    		    		URL[] urls = { new URL( "jar:file:" + securityJar.getPath()+"!/")};
	    			    	 cl = new URLClassLoader(urls,parentLoader);
	    		        	
	    			    	Class<?> cls = cl.loadClass("gdt.jgui.entity.users.WebLogin");
	    		        	Object obj=cls.newInstance();
	    		        	Method method = obj.getClass().getDeclaredMethod("getWebView",entigrator.getClass(),locator$.getClass());
	    		        	String login$=(String) method.invoke(obj,entigrator,locator$);
	    		        	
	    		        	if(debug)
   			    	    	 System.out.println("entry:security:is authorized="+isAuthorized);  
	    		        	cl.close();
	    		        	out.print(login$);
	    			    	}catch(Exception ee){
	    			    		 if(debug)
	    			    	    	 System.out.println("entry:security:"+ee.toString());
	    			    		 out.print(ee.toString());
	    			    	}
	    			//
	    			return;
	    		}else{
	    			if(debug)
		    	    	 System.out.println("entry:security:authorized request");
	    			try{
	    			ClassLoader parentLoader = WContext.class.getClassLoader();
 		 	    	File securityJar=new File(entihome$+"/"+SECURITY_KEY+"/security.jar");
 		    		URL[] urls = { new URL( "jar:file:" + securityJar.getPath()+"!/")};
 			    	 cl = new URLClassLoader(urls,parentLoader);
 		        	
 			    	Class<?> cls = cl.loadClass("gdt.data.entity.UsersHandler");
 			    	cl.close();
 			    	
 			    	Method method = cls.getMethod("denyRequest", Entigrator.class,String.class);
 			    	boolean denied=(boolean)method.invoke(null, entigrator,locator$);
 			    	if(debug)
		    	    	 System.out.println("entry:security:denied="+denied);
 			    	if(denied){
 			    		out.print(accessDenied());
 			    		return;
 			    	}
	    			}catch(Exception ee){
	    				System.out.println("entry:security:authorized:"+ee.toString());
	    				out.print(ee.toString());
	    				return;
	    			}
	    		}
	    	
	    	 }else{
	    		 if(debug)
	    	    	 System.out.println("entry:security:no security");
	    		 
	    	 } 
	    	
	    		
	    	 ///
	    	 String pageMethod$=locator.getProperty(PAGE_METHOD);
    		 if(PAGE_METHOD_OPEN.equals(pageMethod$)){
    			 String filePath$=locator.getProperty(FILE_PATH);
    			 if(debug)
    			 System.out.println("entry:file path="+filePath$);
    			 ServletContext cntx= request.getServletContext();
    		      String mime = cntx.getMimeType(filePath$);
    		      if (mime == null) {
    		        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    		        return;
    		      }

    		      response.setContentType(mime);
    		      File file = new File(filePath$);
    		      response.setContentLength((int)file.length());

    		      FileInputStream in = new FileInputStream(file);
    		      OutputStream os = response.getOutputStream();

    		      // Copy the contents of the file to the output stream
    		       byte[] buf = new byte[1024];
    		       int count = 0;
    		       while ((count = in.read(buf)) >= 0) {
    		         os.write(buf, 0, count);
    		      }
    		    os.close();
    		    in.close();
    			 return;
    		 }
    		 if(PAGE_METHOD_DOWNLOAD.equals(pageMethod$)){
    			 String filePath$=locator.getProperty(FILE_PATH);
    			 if(debug)
    			 System.out.println("entry:file path="+filePath$);
    			 ServletContext cntx= request.getServletContext();
    		      String mime = cntx.getMimeType(filePath$);
    		      if (mime == null) {        
    		            // set to binary type if MIME mapping not found
    		            mime = "application/octet-stream";
    		        }

    		      response.setContentType(mime);
    		      File file = new File(filePath$);
    		      response.setContentLength((int)file.length());
    		      String headerKey = "Content-Disposition";
    		      String headerValue = String.format("attachment; filename=\"%s\"", file.getName());
    		      response.setHeader(headerKey, headerValue);
    		      FileInputStream in = new FileInputStream(file);
    		      OutputStream os = response.getOutputStream();

    		      // Copy the contents of the file to the output stream
    		       byte[] buf = new byte[1024];
    		       int count = 0;
    		       while ((count = in.read(buf)) >= 0) {
    		         os.write(buf, 0, count);
    		      }
    		    os.close();
    		    in.close();
    			 return;
    		 }
	    	 String wContext$=locator.getProperty(BaseHandler.HANDLER_CLASS);
	    	 if(JBasesPanel.class.getName().equals(wContext$)){
	    		 locator=new Properties();
		    	 locator.setProperty(BaseHandler.HANDLER_CLASS, JBasesPanel.class.getName());
		    	 locator.setProperty(WContext.WEB_HOME,request.getRequestURL().toString());
		    	 locator.setProperty(BaseHandler.HANDLER_METHOD, "getWebView");
		    	 locator.setProperty(WContext.BASES,bases$);
		    	 JBasesPanel bp=new JBasesPanel();
		    	 PrintWriter out = response.getWriter();
		    	 out.println(bp.getWebView(null,Locator.toString(locator)));
		    	 return;
	    	 }
	    	 //if(debug)
	    	 //System.out.println("entry:web context="+wContext$);
	    	
	    	 System.out.println("---------------------"); 
	    	try{
	    	 WContext wContext=(WContext)JConsoleHandler.getHandlerInstance(entigrator, wContext$);
	    	 PrintWriter out = response.getWriter(); 
	    	 if( wContext!=null){
	    		 if(debug)
	    	    	 System.out.println("entry:web context="+wContext$);
	    	         out.println(wContext.getWebView(entigrator,locator$));
	    	 }else{
	    		 out.println("Cannot instantiate web context="+wContext$);	 
	    		 
	    	 }
	    	}catch(Exception e ){
	    		if(debug)
	    			 System.out.println("entry:Cannot instantiate web context="+wContext$);
	    	
	    		PrintWriter out = response.getWriter();
	    		out.println(e.toString());
	    	}
	    	
	    	 return;
	    	 }
	   
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	private String accessDenied(){
		StringBuffer sb=new StringBuffer();
		sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
		sb.append("<html>");
		sb.append("<body>");
		sb.append("<strong>Access denied.</strong>");
	    sb.append("</body>");
	    sb.append("</html>");
	    return sb.toString();
	}
}
