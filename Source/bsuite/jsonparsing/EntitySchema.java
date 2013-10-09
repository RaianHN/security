package bsuite.jsonparsing;

import java.util.ArrayList;
import java.util.Vector;

import org.codehaus.jackson.map.ObjectMapper;

public class EntitySchema {
	
	@SuppressWarnings("unchecked")
	public static void createEntity(String eName,Vector fieldNames,Vector actionNames){
		
	Entity entity=new Entity();	
	Field field=new Field();
	EntityAction action=new EntityAction();
	
	ArrayList flds = new ArrayList(); 
	ArrayList actions = new ArrayList();
	  
	for(int i=0;i<fieldNames.size();i++){		  
		  field.setFieldName((String)fieldNames.get(i));
		  flds.add(field);
	  }
	 
	  for(int i=0;i<actionNames.size();i++){		  
		  action.setActionName((String)actionNames.get(i));
		  actions.add(action);
	  }
	//to set the entityname
	  entity.setEntityName(eName);
	  entity.setFields(flds);
	 entity.setActions(actions);
	  
	  ObjectMapper mapper=new ObjectMapper();
	  @SuppressWarnings("unused")
	String val="";
	  try{
		val = mapper.writeValueAsString(entity);
	  }catch (Exception e) {
		e.printStackTrace();
	}
	 
	  
	  //
	  
	  
	
	}
	
	
}

