package gdt.data.entity.facet;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.table.DefaultTableModel;

import gdt.data.entity.EntityHandler;
import gdt.data.entity.FacetHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
import gdt.jgui.console.JMainConsole;
import gdt.jgui.entity.view.View;
import java.net.URL;
import java.net.URLClassLoader;
/**
* Contains methods to process a view entity.
* @author  Alexander Imas
* @version 1.0
* @since   2016-03-11
*/
public class ViewHandler extends FacetHandler {
	private Logger LOGGER=Logger.getLogger(getClass().getName());
	/**
	 * Check if the view handler is applied to the entity  
	 *  @param entigrator entigrator instance
	 *  @param locator$ entity's locator 
	 * @return true if applied false otherwise.
	 */	
	@Override
	public boolean isApplied(Entigrator entigrator, String locator$) {
		try{
			Properties locator=Locator.toProperties(locator$);
     		entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			boolean result=false;
			Sack entity=entigrator.getEntityAtKey(entityKey$);
	    	String view$=entity.getProperty("view");
				if(view$!=null&&!Locator.LOCATOR_FALSE.equals(view$)){
				    if(entity.getElementItem("fhandler", getClass().getName())==null){	
						if(!entity.existsElement("fhandler"))
							entity.createElement("fhandler");
							entity.putElementItem("fhandler", new Core(null,getClass().getName(),null));
							entigrator.ent_alter(entity);
					}
	            result=true;
				}
			return result;
		}catch(Exception e){
			LOGGER.severe(e.toString());
			return false;
			}
	}
	 /**
     * Get title of the view handler.  
     * @return the title of the query handler..
     */	
	@Override
	public String getTitle() {
		return "View";
	}
	 /**
     * Get type of the query handler.  
     * @return the type of the query handler..
     */	
	@Override
	public String getType() {
		return "view";
	}
	 /**
     * Get class name of the view handler.  
     * @return the class name of the view handler..
     */	
	@Override
	public String getClassName() {
		return ViewHandler.class.getName();
	}
	private void adaptLabel(Entigrator entigrator){
		 try{
				Sack entity=entigrator.getEntityAtKey(entityKey$);
				entigrator.ent_assignProperty(entity, "view", entityLabel$);
		    }catch(Exception e){
		    	
		    }
	}
	/**
	* Adapt the clone of the entity.  
	*/
	@Override
	public void adaptClone(Entigrator entigrator) {
		adaptLabel(entigrator);
	}
	/**
	 * Adapt the the entity after rename.   
	 */	
	@Override
	public void adaptRename(Entigrator entigrator) {
		adaptLabel(entigrator);
	}
	@Override
	public void completeMigration(Entigrator entigrator) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getLocation() {
		// TODO Auto-generated method stub
		return null;
	}
   public static void performView(Entigrator entigrator, String viewKey$){
	   try{
		   //System.out.println("Viewhandler:performView:veiw="+viewKey$);
		   File viewHome=new File(entigrator.getEntihome()+"/"+viewKey$);
			URL url = viewHome.toURI().toURL();
		    URL[] urls = new URL[]{url};
		    ClassLoader parentLoader = JMainConsole.class.getClassLoader();
		    URLClassLoader cl = new URLClassLoader(urls,parentLoader);
		    Class<?> cls = cl.loadClass(viewKey$);
		    View view=(View)cls.newInstance();
		    Method method = view.getClass().getDeclaredMethod("select",Entigrator.class);
	 	    method.invoke(view,entigrator);
	   }catch(Exception e){
		   Logger.getLogger(ViewHandler.class.getName()).severe(e.toString());
	   }
   }
}
