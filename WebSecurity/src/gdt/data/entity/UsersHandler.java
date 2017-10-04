package gdt.data.entity;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Logger;

import gdt.data.grain.Core;
import gdt.data.grain.Identity;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
import gdt.jgui.console.WContext;
import gdt.jgui.entity.group.JGroupEditor;
import gdt.jgui.entity.users.JUserEditor;

public class UsersHandler extends FacetHandler{
	public static final String EXTENSION_KEY="__ZZMb_JuVLoqq1Jgk8AMsuK2oZ0";
	public static boolean debug=false;
	public static boolean isAuthorized (Entigrator entigrator,String locator$){
		if(entigrator==null||locator$==null)
			return false;
		try{
			Properties locator=Locator.toProperties(locator$);
			String user$=locator.getProperty("user");
			String password$=locator.getProperty("password");
			if(password$==null)
				return false;
			Sack users=entigrator.getEntityAtKey(entigrator.indx_keyAtLabel("users"));
			String passwordKey$=users.getElementItemAt("user", user$);
			MessageDigest    md = java.security.MessageDigest.getInstance("SHA-1");
			md.update(password$.getBytes());
	        byte[] digest = md.digest();
	        byte[] key=passwordKey$.getBytes();
	        return Arrays.equals(digest, key);
		}catch(Exception ee){
			Logger.getLogger(UsersHandler.class.getName()).severe(ee.toString());
		}
		return false;
	}
	public static Core encryptPassword (String password$){
		
		try{
			if(password$==null){
				 Random random = new Random();
				 password$=random.ints(48,122)
	                .filter(i-> (i<57 || i>65) && (i <90 || i>97))
	                .mapToObj(i -> (char) i)
	                .limit(9)
	                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
	                .toString();
			}
			MessageDigest    md = java.security.MessageDigest.getInstance("SHA-1");
			md.update(password$.getBytes());
	        byte[] digest = md.digest();
	        String encrypted$=new String(digest, "UTF-8");
	        return new Core(password$,Identity.key(),encrypted$);
		}catch(Exception ee){
			Logger.getLogger(UsersHandler.class.getName()).severe(ee.toString());
		}
		return null;
	}
	@Override
	public boolean isApplied(Entigrator entigrator, String locator$) {
		try{
			Properties locator=Locator.toProperties(locator$);
     		entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			Sack entity=entigrator.getEntityAtKey(entityKey$);
			if(!"users".equals(entity.getProperty("entity")))
				return false;
   		    if(entity.getElementItem("fhandler", getClass().getName())==null){	
						if(!entity.existsElement("fhandler"))
							entity.createElement("fhandler");
							entity.putElementItem("fhandler", new Core(null,getClass().getName(),null));
							entigrator.ent_alter(entity);
					}
			return true;
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).severe(e.toString());
			return false;
			}
	}
	@Override
	public String getTitle() {
		return "Users";
	}
	@Override
	public String getType() {
		return "users";
	}
	@Override
	public String getLocation() {
				return EXTENSION_KEY;
	}
	@Override
	public String getClassName() {
		return UsersHandler.class.getName();
	}
	@Override
	public void adaptClone(Entigrator entigrator) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void adaptRename(Entigrator entigrator) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void completeMigration(Entigrator entigrator) {
		// TODO Auto-generated method stub
		
	}
	public static boolean denyRequest(Entigrator entigrator,String locator$){
		try{
			if(debug)
			System.out.println("UsersHandler:denyRequest:locator="+locator$);
			Properties locator=Locator.toProperties(locator$);
			String user$=locator.getProperty(JUserEditor.USER_NAME);
			String entihome$=locator.getProperty(Entigrator.ENTIHOME);
			String[]ga=entigrator.indx_listEntities("entity", "group");
			ClassLoader parentLoader = WContext.class.getClassLoader();
	 	    	File securityJar=new File(entihome$+"/"+EXTENSION_KEY+"/security.jar");
	    		URL[] urls = { new URL( "jar:file:" + securityJar.getPath()+"!/")};
	    		URLClassLoader cl = new URLClassLoader(urls,parentLoader);
		    	Class<?> cls = cl.loadClass("gdt.data.entity.GroupHandler");
		    	cl.close();
		    	
		    	Method method = cls.getMethod("denyRequest", Entigrator.class,String.class);
		    	//if((boolean)method.invoke(null, entigrator,locator$))
		    
			boolean denied;
			for(String g:ga){
				if(debug)
					System.out.println("UsersHandler:denyRequest:group="+g);
				denied=true;
				try{
				locator$=Locator.append(locator$, JGroupEditor.GROUP_KEY, g);	
				denied=(boolean)method.invoke(null, entigrator,locator$);
						//GroupHandler.denyRequest(entigrator, user$, g,locator$);
				}catch(Exception ee){
					System.out.println("UsersHandler:denyRequest:"+ee);
				}
				if(debug)
					System.out.println("UsersHandler:denyRequest:denied="+denied);
				if(!denied)
					return false;
			//	if(!GroupHandler.denyRequest(entigrator, user$, g,locator$))
			//		return false;
			}
		}catch(Exception e){
			Logger.getLogger(UsersHandler.class.getName()).severe(e.toString());
		}
		return true;
	}
	public static String[] listGroups(Entigrator entigrator,String user$){
		try{
			String[] sa=entigrator.indx_listEntities("entity", "group");
			Sack group;
			if(sa!=null){
				ArrayList<String>gl=new ArrayList<String>();
				for(String s:sa){
					group=entigrator.getEntityAtKey(s);
					if(group!=null&&group.getElementItem("user", user$)!=null)
						if(!gl.contains(s))
						gl.add(s);
				}
				return gl.toArray(new String[0]);
			}
		}catch(Exception e){
			Logger.getLogger(UsersHandler.class.getName()).severe(e.toString());
		}
		return null;
	}
}
