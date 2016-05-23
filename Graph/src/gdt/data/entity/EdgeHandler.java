package gdt.data.entity;

import java.util.Properties;
import java.util.logging.Logger;

import gdt.data.entity.facet.FieldsHandler;
import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;

public class EdgeHandler extends FieldsHandler{
	private Logger LOGGER=Logger.getLogger(EdgeHandler.class.getName());
	public static final String EXTENSION_KEY="_Tm142C8Sgti2iAKlDEcEXT2Kj1E";
	String entihome$;
	String entityKey$;
	public final static String EDGE="edge";
	public EdgeHandler(){
		super();
	}
	@Override
	public boolean isApplied(Entigrator entigrator, String locator$) {
		try{
//		System.out.println("AddressHandler:isApplied:locator="+locator$);
			Properties locator=Locator.toProperties(locator$);
			entityKey$=locator.getProperty(EntityHandler.ENTITY_KEY);
			boolean result=false;
			Sack entity=entigrator.getEntityAtKey(entityKey$);
//			System.out.println("PhoneHandler:isApplied:entity="+entity.getProperty("label"));
			String edge$=entity.getProperty("edge");
			if(edge$!=null&&!Locator.LOCATOR_FALSE.equals(edge$)){
			   if(entity.getElementItem("fhandler", EdgeHandler.class.getName())==null){	
					if(!entity.existsElement("fhandler"))
						entity.createElement("fhandler");
					entity.putElementItem("fhandler", new Core(null, EdgeHandler.class.getName(),null));
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
		return "Edge";
	}
	public String getType() {
		return "edge";
	}
	private void adaptLabel(Entigrator entigrator){
		 try{
				Sack entity=entigrator.getEntityAtKey(entityKey$);
				entigrator.ent_assignProperty(entity, "edge", entityLabel$);
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
	return  EdgeHandler.class.getName();
}
}
