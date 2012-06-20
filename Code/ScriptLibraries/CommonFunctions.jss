function defineEntity(E_Name, E_List, E_Features){
	var doc = database.createDocument();
	doc.replaceItemValue("Form","Entity")
	doc.replaceItemValue("E_name",E_Name);
	doc.replaceItemValue("E_List",E_List);
	doc.replaceItemValue("E_Features",E_Features);
	doc.save();
	doc.recycle();
	//Changing the profile value for given new entity
	var securityDb:NotesDatabase = getDatabase("Security.nsf");
	var vie:NotesView = securityDb.getView("ProfileView");	

	var profiledoc : NotesDocument= vie.getFirstDocument();
	println("Profile Name passes ",profiledoc.getItemValueString("prof_name"));
	while(profiledoc!=null){
		defaultModulePermission(profiledoc,E_Name);
		defaultFieldPermission(profiledoc,E_Name);
		defaultFeaturePermission(profiledoc,E_Name);
		profiledoc.save();
		profiledoc=vie.getNextDocument(profiledoc);
	}

	
	profiledoc.recycle();
	
}


function defineEmployeeEntity(E_Name, E_List, P_Fields,E_Features){
	//var vectFields:java.util.Vector = P_Fields;
	//vectFields.addAll(E_List);
	var doc = database.createDocument();
	doc.replaceItemValue("Form","Entity")
	doc.replaceItemValue("E_name",E_Name);
	doc.replaceItemValue("E_List",E_List);
	doc.replaceItemValue("E_Features",E_Features);
	doc.save();
	doc.recycle();
}



function getProfiles(){
	var securityDb:NotesDatabase = getDatabase("Security.nsf");
	var vie:NotesView = securityDb.getView("ProfileView");
	var doc:NotesDocument = vie.getFirstDocument();
	var profileNames  = new java.util.Vector();
	while(doc!=null){
		profileNames.add(doc.getItemValueString("prof_name"));
		doc = vie.getNextDocument(doc);
	}
	return profileNames;
	doc.recycle();
}

function defineProfile(ProfileName){
	/*
	var securityDb:NotesDatabase = getDatabase("Security.nsf");
	println("security database"+securityDb.getFileName());
	println("inside dbprofile");
	var doc :NotesDocument = securityDb.createDocument();
	doc.replaceItemValue("Form","permissions")
	doc.replaceItemValue("prof_name",ProfileName);	
	
	defaultModulePermission(doc,"Employee");
	defaultFieldPermission(doc,"Employee");
	defaultFeaturePermission(doc,"Employee");
	doc.save();
	doc.recycle();
	*/
	
	var ob:bsuite.weber.jsonparsing.ProfileCreation=bsuite.weber.jsonparsing.ProfileCreation();
ob.createProfile(ProfileName);
	
}

function defaultModulePermission(profiledoc,moduleName){
	var e_crud=moduleName+":"+"1111111";
	if(moduleName=="Employee"){
		profiledoc.replaceItemValue("E_Crud",e_crud);  //while creating profile for first time
	}else{
		var item: NotesItem = profiledoc.getFirstItem("E_Crud");
	item.appendToTextList(e_crud);	
	}
	


}

function defaultFieldPermission(profiledoc,moduleName){
	println("field permission");
	
	var vie:NotesView = database.getView("All Documents");
	//var doc: NotesDocument=vie.getDocumentByKey("Employee");
	var doc: NotesDocument=vie.getDocumentByKey(moduleName);
	var fieldnames=doc.getItemValue("E_List");	
	var fchoice=new java.util.Vector();
	for(x in fieldnames){
		//fchoice.add(x+":"+"10");	
		fchoice.add(moduleName+":"+x+":"+"10");	
	}
	if(moduleName=="Employee"){
		profiledoc.replaceItemValue("f_choice",fchoice);
	}else{
		var item=NotesItem= profiledoc.getFirstItem("f_choice");
		item.appendToTextList(fchoice);
	}


}

function defaultFeaturePermission(profiledoc,moduleName){
	var vie:NotesView = database.getView("All Documents");
//var doc: NotesDocument=vie.getDocumentByKey("Employee");
var doc: NotesDocument=vie.getDocumentByKey(moduleName);
var features=doc.getItemValue("E_Features");	
var fchoice=new java.util.Vector();
for(x in features){
	fchoice.add(moduleName+":"+x+":"+"1");	
}
if(moduleName=="Employee"){
	profiledoc.replaceItemValue("features",fchoice);
}else{
	var item=NotesItem= profiledoc.getFirstItem("features");
	item.appendToTextList(fchoice);
	
}
}

function defineRole(db1,role_name,role_to,r_description){
var doc: NotesDocument = db1.createDocument();
doc.replaceItemValue("Form","Role")
doc.replaceItemValue("role_name",role_name);
doc.replaceItemValue("role_to",role_to);
doc.replaceItemValue("r_description",r_description);
doc.save();
doc.recycle();
}

function getDatabase(dbName){
	
   	var serv=@Subset(@DbName(),1);
	var dbpath=@LeftBack(@Subset(@DbName(),-1),"/")
	var custdb=dbpath+"\\"+dbName;
	var dbname = new Array(serv,custdb);
    var db: NotesDatabase = session.getDatabase(serv, custdb);
    return db;
}

function getEntities(){
	
	var vie:NotesView = database.getView("All Documents");
	var doc:NotesDocument = vie.getFirstDocument();
	var entityNames  = new java.util.Vector();
	while(doc!=null){
		entityNames.add(doc.getItemValueString("E_name"));
		doc = vie.getNextDocument(doc);
	}
	return entityNames;
	vie.recycle();
	doc.recycle();
}

function getEntityCrud(profileName,entityName){
	var securityDb:NotesDatabase = getDatabase("Security.nsf");
	var vie:NotesView = securityDb.getView("ProfileView");
	var doc:NotesDocument = vie.getDocumentByKey(profileName);
	var entCrud = doc.getItemValue("E_Crud");
	for(x in entCrud){
		var eName = @Left(x,":")
		if(eName==entityName){
			return @Right(x,":")
		}
	}
}

function getFieldVisibility(profileName,entityName){
	
	var securityDb:NotesDatabase = getDatabase("Security.nsf");
var vie:NotesView = securityDb.getView("ProfileView");
var doc:NotesDocument = vie.getDocumentByKey(profileName);
var fields : String=doc.getItemValue("f_choice");
//return fields;
var vect=new java.util.Vector();
for(x in fields){	
	var str=x.split(":");
	var modulename=str[0];
	var fieldnames=str[1];
	var security=str[2];	
	if(entityName==modulename){
		vect.add(fieldnames+":"+security);
	}
	/*
	var fieldname= @Left(x,":");
	println("FieldName ",fieldname)
	var fvisread=@Right(x,":")
	var fieldvisible=fvisread.charAt(0);
	println("Field Vissible ",fieldvisible);
	var fieldreadonly=fvisread.charAt(1);
	println("Field Readonly ",fieldreadonly);*/
	}
return vect;

}

function getFeaturesVisibility(profileName,entityName){
	
	var securityDb:NotesDatabase = getDatabase("Security.nsf");
var vie:NotesView = securityDb.getView("ProfileView");
var doc:NotesDocument = vie.getDocumentByKey(profileName);
var features=doc.getItemValue("features");
//return features;
var vect=new java.util.Vector();
for(x in features){	
	var str=x.split(":");
	var modulename=str[0];
	var featurename=str[1];
	var security=str[2];	
	if(entityName==modulename){
		vect.add(featurename+":"+security);
	}
}
	return vect;
}

function editCrud(profileName,entityName,value){
	println("crud value"+value);
	var securityDb:NotesDatabase = getDatabase("Security.nsf");
	var vie:NotesView = securityDb.getView("ProfileView");
	var doc:NotesDocument = vie.getDocumentByKey(profileName);
	var entCrud = doc.getItemValue("E_Crud");
	println(entCrud);
	var isNew = 1;

	for(x=0;x<entCrud.size();x++){	
	println("Entity Name "+entCrud.get(x));
	var eName=@Left(entCrud.get(x),":");
	if(eName==entityName){
		entCrud.set(x,eName+":"+value);
		isNew=0;
		break;
	}
	}
	/*
	for(x in entCrud){
		println("Entity Name ",x);
		var eName = @Left(x,":")
		if(eName==entityName){
			entCrud.removeElement(x); //edit the existing entity
			entCrud.add(eName+":"+value);
			isNew = 0;
			break;
		}		
	}
	if(isNew==1){
	//	entCrud.add(entityName+":"+value);//create new entity if its not available
	}*/
	doc.replaceItemValue("E_Crud",entCrud);
	doc.save();
	doc.recycle();
	vie.recycle();
	securityDb.recycle();	
}


function setPermission(profileName,entityName){
	var arr = context.getSubmittedValue().split(",");
	var entityCrud = new bsuite.weber.jsonparsing.ProfileEdit;
	println("setting field permission"+typeof(arr)+"submitted type"+typeof(context.getSubmittedValue()));
	
	entityCrud.saveFieldCrud(profileName, entityName, context.getSubmittedValue());
	/*
	var securityDb:NotesDatabase = getDatabase("Security.nsf");
	var vie:NotesView = securityDb.getView("ProfileView");
	var doc:NotesDocument = vie.getDocumentByKey(profileName);
	var vect = new java.util.Vector();
	var i=0;	
	var fchoice=doc.getItemValue("f_choice");
	for(x=0;x<fchoice.size();x++){			
		var str=fchoice.get(x).split(":");
		var modulename=str[0];
		var fname=str[1];
		var security=str[2];	
		if(modulename==entityName){
			//this array is from clientside			
			for(i=0;i<arr.length;i++)
			{				
				if(arr[i]!="")
				{
					var val=arr[i].split(":");
					var mname=val[0];
					var fieldname=val[1];
					var sec=val[2];
					if(modulename==mname && fieldname==fname)
					{
						fchoice.set(x,arr[i]);
					}
					
				}		
					
			}		
		
		}
		
	}
	doc.replaceItemValue("f_choice",fchoice);	
	doc.save();
	doc.recycle();
	vie.recycle();
	
	*/
}

function setFeaturePermission(profileName,entityName){
	
	var arr = context.getSubmittedValue().split(",");
	var entityCrud = new bsuite.weber.jsonparsing.ProfileEdit;
	entityCrud.saveFeatureCrud(profileName, entityName, context.getSubmittedValue());
	/*
	var securityDb:NotesDatabase = getDatabase("Security.nsf");
	var vie:NotesView = securityDb.getView("ProfileView");
	var doc:NotesDocument = vie.getDocumentByKey(profileName);
	var vect = new java.util.Vector();
	var i=0;
	var featureslist=doc.getItemValue("features");
	for(x=0;x<featureslist.size();x++)
	{			
		var str=featureslist.get(x).split(":");
		var modulename=str[0];
		var fname=str[1];
		var security=str[2];	
		if(modulename==entityName)
		{
			//this array is from clientside			
			for(i=0;i<arr.length;i++)
			{				
				if(arr[i]!="")
				{
					var val=arr[i].split(":");
					var mname=val[0];
					var featurename=val[1];
					var sec=val[2];
					if(modulename==mname && featurename==fname)
					{
						featureslist.set(x,arr[i]);
					}					
				}							
			}			
		}				
	}
	
	doc.replaceItemValue("features",featureslist);	
	doc.save();
	doc.recycle();
	vie.recycle();
	
	*/
}

function createEmployee(personDocid,fieldval){
	var securityDb:NotesDatabase = getDatabase("Employee.nsf");
	var docEmployee:NotesDocument = securityDb.createDocument();
	var db:NotesDatabase = session.getDatabase("","names.nsf");
	var personDoc:NotesDocument = db.getDocumentByID(personDocid);
	docEmployee.replaceItemValue("Form","Employee");
	var vie = database.getView("All Documents");
	var doc: NotesDocument=vie.getDocumentByKey("Employee");
	var fieldnames=doc.getItemValue("E_List");
	doc.getUniversalID()
	for(x in fieldnames){
		if(x=="Name"){
			docEmployee.replaceItemValue(x,personDoc.getItemValueString("FullName"));
		}else{
			docEmployee.replaceItemValue(x,fieldval);
		}
		
	}
	docEmployee.save(true,false);	
	var relid=getRelationNameUnid("IS_A");

	createRelationship(personDoc.getUniversalID(),"names.nsf","Person","Employee.nsf","Employee",docEmployee.getUniversalID(),relid);
	doc.recycle();
	docEmployee.recycle();
}

function createRelationship(srcunid,sourcedb,src_data,targetdb,trg_data,targetid,relationid){
	var reladb:NotesDatabase = getDatabase("relation.nsf");
	var docrel:NotesDocument=reladb.createDocument();
	docrel.replaceItemValue("form","association");
	docrel.replaceItemValue("sourceid", srcunid);
	docrel.replaceItemValue("sourcedb",sourcedb);
	docrel.replaceItemValue("src_data", src_data);
	docrel.replaceItemValue("targetdb",targetdb);
	docrel.replaceItemValue("trg_data",trg_data);
	docrel.replaceItemValue("targetid",targetid);

	docrel.replaceItemValue("relationid",relationid);
	docrel.save(true,false);
}

function getRelationNameUnid(relationName){
	var relationDb:NotesDatabase = getDatabase("Relation.nsf");
		var relview=relationDb.getView("CategoryRelation")
		var reldoc:NotesDocument= relview.getDocumentByKey(relationName);
		return reldoc.getUniversalID();
	}

function createProfileResponse(entityName){
	var profiles = getProfiles();
	var securityDb:NotesDatabase = getDatabase("Security.nsf");
	var vie:NotesView = securityDb.getView("ProfileView");
	
	for(x in profiles){
		var docProf:NotesDocument = vie.getDocumentByKey(x);
		var doc:NotesDocument = securityDb.createDocument();		
		doc.replaceItemValue("Form","permissions")
		doc.replaceItemValue("entityName",entityName);	
		defaultModulePermission(doc);
		defaultFieldPermission(doc);
		defaultFeaturePermission(doc);
		doc.makeResponse(docProf);
		doc.save();
		doc.recycle()
		
	}
}
function setModulePermission(profileName){
	var arr = context.getSubmittedValue().split(",");
	var entityCrud = new bsuite.weber.jsonparsing.ProfileEdit;
	println("setting field permission"+typeof(arr)+"submitted type"+typeof(context.getSubmittedValue()));
	
	entityCrud.saveModulePerm(profileName,context.getSubmittedValue());
}

function setEntityPermission(profileName,moduleName){
	var arr = context.getSubmittedValue().split(",");
	var entityCrud = new bsuite.weber.jsonparsing.ProfileEdit;
	println("setting field permission"+typeof(arr)+"submitted type"+typeof(context.getSubmittedValue()));
	
	entityCrud.saveEntityPerm(profileName,moduleName,context.getSubmittedValue());
}
function setFieldPermission(profileName,moduleName,entityName){
	var arr = context.getSubmittedValue().split(",");
	var fieldCrud = new bsuite.weber.jsonparsing.ProfileEdit;
	println("setting field permission"+typeof(arr)+"submitted type"+typeof(context.getSubmittedValue()));
	
	fieldCrud.saveFieldPerm(profileName,moduleName,entityName,context.getSubmittedValue());
}

function setFeaturePermission(profileName,moduleName,entityName){
	var arr = context.getSubmittedValue().split(",");
	var featureCrud = new bsuite.weber.jsonparsing.ProfileEdit;
	println("setting feature permission"+typeof(arr)+"submitted type"+typeof(context.getSubmittedValue()));
	
	featureCrud.saveFeaturePerm(profileName,moduleName,entityName,context.getSubmittedValue());
}


function loadCreateEntity(moduleName){
	var component = getComponent('cEntityPanel'+moduleName); 
	var s = facesContext;
	var c1="/cc_entityForm.xsp"; 
	var id="entityPanel";
	sessionScope.employeeRegister = viewScope.entityName;
	println("viewsco"+viewScope.entityName);
	println("modulename"+moduleName);
	com.weberon.DynamicCC.loadCC(s, component, c1, id);	
}

function loadViewEntity(moduleName){
	var component = getComponent('cEntityPanel'+moduleName); 
	var s = facesContext;
	var c1="/cc_EntityView.xsp"; 
	var id="entityViewPanel";
	com.weberon.DynamicCC.loadCC(s, component, c1, id);	
}

function loadReadEntity(){
	var component = getComponent('readPanel'); 
	var s = facesContext;
	var c1="/ccReadEntity.xsp"; 
	var id="readEntityPanel";
	com.weberon.DynamicCC.loadCC(s, component, c1, id);	
	
}
