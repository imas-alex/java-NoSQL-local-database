package gdt.data.entity;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;
import java.util.logging.Logger;

import gdt.data.entity.facet.FieldsHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
import gdt.jgui.console.WContext;
import gdt.jgui.entity.group.JGroupEditor;
import gdt.jgui.entity.users.JUserEditor;

public class GroupHandler extends FieldsHandler{
public static boolean debug=false;
	@Override
	public boolean isApplied(Entigrator entigrator, String locator$) {
		try{
			Properties locator=Locator.toProperties(locator$);
     		entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			Sack entity=entigrator.getEntityAtKey(entityKey$);
			if(!"group".equals(entity.getProperty("entity")))
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
		return "Group";
	
	}

	@Override
	public String getType() {
		
		return "group";
	}

	@Override
	public String getLocation() {
			return UsersHandler.EXTENSION_KEY;
	}

	@Override
	public String getClassName() {
		return GroupHandler.class.getName();
	}
	private void adaptLabel(Entigrator entigrator){
		 try{
			Sack entity=entigrator.getEntityAtKey(entityKey$);
			entityLabel$= entity.getProperty("label");
			entity=entigrator.ent_assignProperty(entity, "fields",entityLabel$);
			entity=entigrator.ent_assignProperty(entity, "group",entityLabel$);
			entigrator.ent_alter(entity);
		 }catch(Exception e){
	    	Logger.getLogger(getClass().getName()).severe(e.toString());
		    }
	}
	@Override
	public void adaptClone(Entigrator entigrator) {
		 adaptLabel(entigrator);
		
	}

	@Override
	public void adaptRename(Entigrator entigrator) {
		 adaptLabel(entigrator);
		
	}

	@Override
	public void completeMigration(Entigrator entigrator) {
		// TODO Auto-generated method stub
		
	}
	public static boolean denyRequest(Entigrator entigrator,String locator$){
		try{
			
			if(debug)
				System.out.println("GroupHandler:denyRequest: locator="+locator$);
			Properties locator=Locator.toProperties(locator$);
			String user$=locator.getProperty("user");
			String groupKey$=locator.getProperty(JGroupEditor.GROUP_KEY);
			if(debug)
				System.out.println("GroupHandler:denyRequest: user="+user$+" group="+groupKey$);
			
			Sack group=entigrator.getEntityAtKey(groupKey$);
		    if(group.getElementItem("user", user$)==null){
		    	if(debug)
					System.out.println("GroupHandler:denyRequest:no users in group="+groupKey$);
			
			   return true;
		    }
		 Core[]ca=group.elementGet("restriction");
	    	if(debug)
				System.out.println("GroupHandler:denyRequest:ca="+ca.length);

		 if(ca!=null){
			 ClassLoader parentLoader = WContext.class.getClassLoader();
			 String entihome$=locator.getProperty(Entigrator.ENTIHOME);	
			 File securityJar=new File(entihome$+"/"+UsersHandler.EXTENSION_KEY+"/security.jar");
	    		URL[] urls = { new URL( "jar:file:" + securityJar.getPath()+"!/")};
	    		URLClassLoader cl = new URLClassLoader(urls,parentLoader);
		    	Class<?> cls = cl.loadClass("gdt.data.entity.RestrictionHandler");
		    	cl.close();
		    	
		    	Method method = cls.getMethod("denyRequest", Entigrator.class,String.class);
		    
			 if(debug)
					System.out.println("GroupHandler:denyRequest:ca="+ca.length);

			 for(Core c:ca){
				 if(debug)
						System.out.println("GroupHandler:denyRequest:restriction="+c.name);
				 locator$=Locator.append(locator$, "restriction key", c.value);
				 boolean denied=(boolean)method.invoke(null, entigrator,locator$);
				if(debug)
						System.out.println("UsersHandler:denyRequest:denied="+denied);
				 if(!denied)
					 return false;
				 //if(!RestrictionHandler.denyRequest(entigrator, c.value, locator$))
				//	 return false;
			 }
		 }
		}catch(Exception e){
			Logger.getLogger(GroupHandler.class.getName()).severe(e.toString());
		}
		return true;
	}
}
