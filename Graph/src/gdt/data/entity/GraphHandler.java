package gdt.data.entity;

import java.util.Properties;
import java.util.logging.Logger;

import gdt.data.entity.facet.FieldsHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;

public class GraphHandler extends FieldsHandler{
	private Logger LOGGER=Logger.getLogger(GraphHandler.class.getName());
	public static final String EXTENSION_KEY="_Tm142C8Sgti2iAKlDEcEXT2Kj1E";	
	String entihome$;
	String entityKey$;
	public final static String GRAPH="graph";
	public GraphHandler(){
		super();
	}
	@Override
	public boolean isApplied(Entigrator entigrator, String locator$) {
		try{
//		System.out.println("GraphHandler:isApplied:locator="+locator$);
			Properties locator=Locator.toProperties(locator$);
			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			boolean result=false;
			Sack entity=entigrator.getEntityAtKey(entityKey$);
//			System.out.println("GraphHandler:isApplied:entity="+entity.getProperty("label"));
			String graph$=entity.getProperty("graph");
			if(graph$!=null&&!Locator.LOCATOR_FALSE.equals(graph$)){
			   if(entity.getElementItem("fhandler", GraphHandler.class.getName())==null){	
					if(!entity.existsElement("fhandler"))
						entity.createElement("fhandler");
					entity.putElementItem("fhandler", new Core(null, GraphHandler.class.getName(),null));
					entigrator.save(entity);
				}
	            result=true;
			}
			return result;
		}catch(Exception e){
		LOGGER.severe(e.toString());
		return false;
		}
	}
	public String getTitle() {
		return "Graph";
	}
	public String getType() {
		return "graph";
	}
	private void adaptLabel(Entigrator entigrator){
		 try{
				Sack entity=entigrator.getEntityAtKey(entityKey$);
				entigrator.ent_assignProperty(entity, "graph", entityLabel$);
		    }catch(Exception e){
		    	
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
public String getClassName() {
	return  GraphHandler.class.getName();
}

}
