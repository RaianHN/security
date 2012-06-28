package bsuite.weber.jsonparsing;

import java.util.ArrayList;
import java.util.Vector;

import org.codehaus.jackson.map.ObjectMapper;

public class EntitySchema {
	
	public static void createEntity(String eName,Vector fieldNames,Vector featureNames){
		
	Entity entity=new Entity();	
	Field field=new Field();
	Feature feature=new Feature();
	
	ArrayList flds = new ArrayList(); 
	ArrayList ftrs = new ArrayList();
	  
	for(int i=0;i<fieldNames.size();i++){		  
		  field.setFieldName((String)fieldNames.get(i));
		  flds.add(field);
	  }
	 
	  for(int i=0;i<featureNames.size();i++){		  
		  feature.setFeatureName((String)featureNames.get(i));
		  ftrs.add(feature);
	  }
	//to set the entityname
	  entity.setEntityName(eName);
	  entity.setFields(flds);
	 // entity.setFeatures(ftrs);
	  
	  ObjectMapper mapper=new ObjectMapper();
	  String val="";
	  try{
		val = mapper.writeValueAsString(entity);
	  }catch (Exception e) {
		// TODO: handle exception
	}
	 
	  
	  System.out.println("Json String "+ val);
	  
	  
	
	}
	
	
}

