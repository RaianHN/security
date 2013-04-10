function defineEntity(E_Name, E_List, E_Features){
	var doc = database.createDocument();
	doc.replaceItemValue("Form","Entity")
	doc.replaceItemValue("E_name",E_Name);
	doc.replaceItemValue("E_List",E_List);
	doc.replaceItemValue("E_Features",E_Features);
	doc.save();
	doc.recycle();
	//Changing the profile value for given new entity
	//var securityDb:NotesDatabase = getDatabase("Security.nsf");
	
	var vie:NotesView = database.getView("ProfileView");	

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
	//var securityDb:NotesDatabase = getDatabase("Security.nsf");
	var vie:NotesView = database.getView("ProfileView");
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
	
	var ob:bsuite.jsonparsing.ProfileCreation=bsuite.jsonparsing.ProfileCreation();
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
	//var securityDb:NotesDatabase = getDatabase("Security.nsf");
	var vie:NotesView = database.getView("ProfileView");
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
	
	//var securityDb:NotesDatabase = getDatabase("Security.nsf");
var vie:NotesView = database.getView("ProfileView");
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
	
//	var securityDb:NotesDatabase = getDatabase("Security.nsf");
var vie:NotesView = database.getView("ProfileView");
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
	//var securityDb:NotesDatabase = getDatabase("Security.nsf");
	var vie:NotesView = database.getView("ProfileView");
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
	var entityCrud = new bsuite.jsonparsing.ProfileEdit;
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
	var entityCrud = new bsuite.jsonparsing.ProfileEdit;
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
	//var securityDb:NotesDatabase = getDatabase("Security.nsf");
	var vie:NotesView = database.getView("ProfileView");
	
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
	var profileName = getComponent("moduleCombo").getValue();
	println("profileName"+profileName);
	var arr = context.getSubmittedValue().split(",");
	var entityCrud = new bsuite.jsonparsing.ProfileEdit;
	println("setting field permission"+typeof(arr)+"submitted type"+typeof(context.getSubmittedValue()));
	
	entityCrud.saveModulePerm(profileName,context.getSubmittedValue());
}

function setEntityPermission(){
	var profileName = getComponent("moduleCombo").getValue();

	println("profileName"+profileName);
	var arr = context.getSubmittedValue().split(",");
	var entityCrud = new bsuite.jsonparsing.ProfileEdit;
	println("setting field permission"+typeof(arr)+"submitted type"+typeof(context.getSubmittedValue()));
	
	entityCrud.saveEntityPerm(profileName,context.getSubmittedValue());
}

function setEntityActionPermission(){
	var profileName = getComponent("moduleCombo").getValue();

	println("profileName"+profileName);
	var arr = context.getSubmittedValue().split(",");
	var entityCrud = new bsuite.jsonparsing.ProfileEdit;
	println("setting field permission"+typeof(arr)+"submitted type"+typeof(context.getSubmittedValue()));
	
	entityCrud.saveEntityActionPerm(profileName,context.getSubmittedValue());
}

function setVieawActionPermisssion(){
	var profileName  = getComponent("moduleCombo").getValue();
	var arr = context.getSubmittedValue().split(",");
	var entityCrud = new bsuite.jsonparsing.ProfileEdit;
	println("setting field permission"+typeof(arr)+"submitted type"+typeof(context.getSubmittedValue()));
	
	println("arr "+arr);
	println("profile name"+profileName);
	println("submitted value"+context.getSubmittedValue());
	entityCrud.saveGroupPerm(profileName,context.getSubmittedValue());
	
}

function setGroupActionPermission(){
	var profileName  = getComponent("moduleCombo").getValue();
	var arr = context.getSubmittedValue().split(",");
	var entityCrud = new bsuite.jsonparsing.ProfileEdit;
	println("setting field permission"+typeof(arr)+"submitted type"+typeof(context.getSubmittedValue()));
	
	println("arr "+arr);
	println("profile name"+profileName);
	println("submitted value"+context.getSubmittedValue());
	entityCrud.saveGroupActionPerm(profileName,context.getSubmittedValue());
	
}

function setFieldPermission(profileName,moduleName,entityName){
	var profileName = getComponent("moduleCombo").getValue();
	var arr = context.getSubmittedValue().split(",");
	var fieldCrud = new bsuite.jsonparsing.ProfileEdit;
	println("setting field permission"+typeof(arr)+"submitted type"+typeof(context.getSubmittedValue()));
	
	fieldCrud.saveFieldPerm(profileName,context.getSubmittedValue());
}

function setFeaturePermission(){
	var profileName = getComponent("moduleCombo").getValue();
	
	
	println("profileName"+profileName);

	var arr = context.getSubmittedValue().split(",");
	var featureCrud = new bsuite.jsonparsing.ProfileEdit;
	println("setting feature permission"+typeof(arr)+"submitted type"+typeof(context.getSubmittedValue()));
	
	//featureCrud.saveFeaturePerm(profileName,moduleName,entityName,context.getSubmittedValue());
	featureCrud.saveFeaturePerm(profileName,context.getSubmittedValue());
}


function loadViewEntity(moduleName,entityName){

	println("Inside loadViewEntity");
	var tabentity:java.util.ArrayList=sessionScope.moduleentity;
	println("TabEntity ",tabentity);	
	var i;
	
		for(i=0;i<tabentity.size();i++)
		{
			var mname=tabentity.get(i).split(":");
			if(mname[0]==moduleName)
				{
					tabentity.remove(tabentity.get(i));
				}
		}
	var temp=moduleName+":"+entityName;
	tabentity.add(temp);
	
	println("SessionScope moduleEntity ",sessionScope.moduleentity);
	println("ModuleName ",moduleName);
	
	//In order to show the Delete button in the view
	var entityDelete = new bsuite.jsonparsing.ProfileEdit;
	var result=entityDelete.checkEntityDelete(moduleName,entityName);
	if(result){
		println("Result is true");
		viewScope.entityDelete=true;
	}else{
		println("Result is false");
		viewScope.entityDelete=false;
	}
	
	var component = getComponent('EntityPanel'+moduleName); 
	var s = facesContext;
	var c1="/cc_EntityView.xsp"; 
	var id="entityViewPanel";
	
	bsuite.weberon.DynamicCC.removePreview(component);
	bsuite.weberon.DynamicCC.loadCC(s, component, c1, id);	
	
}

function loadReadEntity(moduleName){
	var component = getComponent('EntityName'+moduleName); 
	var s = facesContext;
	var c1="/ccReadEntity.xsp"; 
	var id="readEntityPanel";
	bsuite.weberon.DynamicCC.loadCC(s, component, c1, id);	
	
}



function loadCreateEntity(moduleName,entityName){
	wrkspc.resetBean();
	//Session scoped var declare in beforePageLoad of MainPage
	var tabentity:java.util.ArrayList=sessionScope.moduleentity;
	var i=0;
	if(tabentity!=null)
	{
		for(i=0;i<tabentity.size();i++)
		{
			var mname=tabentity.get(i).split(":");
			if(mname[0]==moduleName)
			{
				tabentity.remove(tabentity.get(i));
			}
		}
		var temp=moduleName+":"+entityName;
		tabentity.add(temp);
	}

	println("SessionScope moduleEntity ",sessionScope.moduleentity);
	println("viewsco create entity"+viewScope.entityName);
	println("modulename"+moduleName);
	var component = getComponent('EntityPanel'+moduleName);	
	var s = facesContext;
	var c1="/cc_entityForm.xsp"; 
	var id="entityPanel";
	sessionScope.employeeRegister = viewScope.entityName;
	viewScope.moduleName = moduleName;
	viewScope.entityName = entityName;
	println("viewsco"+viewScope.entityName);
	println("modulename"+moduleName);
	bsuite.weberon.DynamicCC.removePreview(component);
	bsuite.weberon.DynamicCC.loadCC(s, component, c1, id);	
	
}



/*
function loadViewEntity(moduleName,entityName){

	println("Inside loadViewEntity");
	var tabentity:java.util.ArrayList=sessionScope.moduleentity;
	println("TabEntity ",tabentity);	
	var i;
	
		for(i=0;i<tabentity.size();i++)
		{
			var mname=tabentity.get(i).split(":");
			if(mname[0]==moduleName)
				{
					tabentity.remove(tabentity.get(i));
				}
		}
	var temp=moduleName+":"+entityName;
	tabentity.add(temp);
	
	println("SessionScope moduleEntity ",sessionScope.moduleentity);
	println("ModuleName ",moduleName);
	var component = getComponent('EntityPanel'+moduleName); 
	var s = facesContext;
	var c1="/cc_EntityView.xsp"; 
	var id="entityViewPanel";
	
	bsuite.weberon.DynamicCC.removePreview(component);
	bsuite.weberon.DynamicCC.loadCC(s, component, c1, id);	
	
}
*/


function loadEditEntity(moduleName,entityName){
	wrkspc.resetBean();
	println("viewsco create entity"+viewScope.entityName);
	println("modulename"+moduleName);
	var component=getComponent("readPanel"+moduleName);
	var s = facesContext;
	var c1="/cc_EditEntity.xsp"; 
	var id="entityEditPanel1";
	sessionScope.employeeRegister = viewScope.entityName;
	viewScope.moduleName = moduleName;
	viewScope.entityName = entityName;
	println("viewsco"+viewScope.entityName);
	println("modulename"+moduleName);
	println("Component ",component)
	bsuite.weberon.DynamicCC.removePreview(component);
	bsuite.weberon.DynamicCC.loadCC(s, component, c1, id);	
	
}


function loadTestControl(componentid,cc,ccid,moduleName,entityName){
	var component = getComponent(componentid); 
	var s = facesContext;
	var c1=cc; 
	var id=ccid;
	println("inside oncomplete");
	println("component "+component);
	component2 = getComponent(ccid);
	if(component2==null){
		viewScope.moduleName = moduleName;
		viewScope.entityName = entityName;
		bsuite.weberon.DynamicCC.loadCC(s, component, c1, ccid);
	}
}
function getModuleNames(){
	var arr:java.util.Vector = new java.util.Vector();
arr.add("Module1");
arr.add("Module2");
arr.add("Module3");
arr.add("Module4");
arr.add("Module5");
viewScope.moduleNames = arr;
return arr;
}

function getFeaturePerm(moduleName){
	var featurePerm:java.util.HashMap = new  java.util.HashMap();
	var arr:java.util.Vector = new java.util.Vector();
	arr.add("feature1:1");
	arr.add("feature2:1");
	arr.add("feature3:0");
	arr.add("feature4:1");
	arr.add("feature5:1");
	featurePerm.put("Module1",arr);
	featurePerm.put("Module2",arr);
	featurePerm.put("Module3",arr);
	featurePerm.put("Module4",arr);
	featurePerm.put("Module5",arr);
	
	
	return featurePerm.get(moduleName);	
	
}
function ifChecked(permission){
	if(permission!=null){
		var chk = @Right(permission,":")
		if(chk){
			if(chk=="1"){
				return true;
			}else{
				return false;
			}
		}
	}
	
}
function getChkBoxid(moduleName,featurePerm){
	var moduleName = moduleName;
	var featureName = @Left(featurePerm,":");
	if((moduleName!=null)&&(featureName!=null)){
	return "chkbx"+moduleName+featureName;
	}
}

function crateFeaturePermArr() {
    var featurePerm: java.util.Vector = new java.util.Vector();
    var arr: java.util.Vector = new java.util.Vector();
    arr.add("feature1:1");
    arr.add("feature2:1");
    arr.add("feature3:0");
    arr.add("feature4:1");
    arr.add("feature5:1");
    featurePerm.add(arr);
    featurePerm.add(arr);
    featurePerm.add(arr);
    featurePerm.add(arr);
    featurePerm.add(arr);
    var moduleRepeatId = "moduleRepeat";
    var featureRepeatId = "featureRepeat";
    var chkBxId = "chk";
    var result: java.util.Vector = new java.util.Vector();
	var strChkBxId = "moduleRepeat:0:featureRepeat:0:chk";
	var chkBox = null;
    for (var x = 0; x < featurePerm.size(); x++) {
        var arr: java.util.Vector = featurePerm.elementAt(x);
        for (var s = 0; s < arr.size(); s++) {
        	strChkBxId = "moduleRepeat:"+x+":featureRepeat:"+s+":chk";
        	chkBox = strChkBxId+getComponent(strChkBxId);
        	if(chkBox!=null){
        		result.add(chkBox.isChecked());
        	}
			
        }
    }
    viewScope.result = result;
}

function createModulePermArr(){
	
}

function getNumberOfModules(profileName){
	println("in getn");
	profileName = context.getSubmittedValue();
	println("in getn"+profileName);
	var moduleN = new bsuite.jsonparsing.ProfileEdit().getNumberOfMOdules(profileName);
	getComponent("inputNModules").setValue(moduleN);
}
function getNumberOfFeatures(){
	println("in getn features");
	params = context.getSubmittedValue().split(":");//will hold profileName,moduleName
	profileName = params[0];
	moduleName = params[1];
	println("in getn "+profileName+" "+moduleName);
	var featuresN = new bsuite.jsonparsing.ProfileEdit().getNumberOfFeatures(profileName,moduleName);
	getComponent("inputNFeatures").setValue(featuresN);
	println("in get features "+profileName+" "+moduleName+" "+featuresN);
	
}
function getNumberOfEntities(profileName, moduleName){
	println("in getn entities");
	params = context.getSubmittedValue().split(":");//will hold profileName,moduleName
	profileName = params[0];
	moduleName = params[1];
	println("in getn "+profileName+" "+moduleName);
	var entitiesN = new bsuite.jsonparsing.ProfileEdit().getNumberOfEntities(profileName, moduleName);
	getComponent("inputNEntities").setValue(entitiesN);
	println("in get entities "+profileName+" "+moduleName+" "+entitiesN);
}
function getNumberOfFields(profileName, moduleName, entityName){
	println("in getn");
	params = context.getSubmittedValue().split(":");//will hold profileName,moduleName, entityName
	profileName = params[0];
	moduleName = params[1];
	entityName = params[2];
	println("in getn "+profileName+" "+moduleName);
	var fieldsN = new bsuite.jsonparsing.ProfileEdit().getNumberOfFields(profileName, moduleName, entityName);
	getComponent("inputNFields").setValue(fieldsN);
	
}


function setAccessTypePermission(){
	//var profileName = getComponent("moduleCombo").getValue();
var profileName="Admin";
	println("profileName"+profileName);
	var arr = context.getSubmittedValue().split(",");
	var entityCrud = new bsuite.jsonparsing.ProfileEdit;
	println("setting field permission"+typeof(arr)+"submitted type"+typeof(context.getSubmittedValue()));
	
	var securityDb:NotesDatabase = getDatabase("Security.nsf");
	var vie:NotesView = database.getView("ProfileView");
	var doc:NotesDocument = vie.getFirstDocument();
	
	while(doc!=null){
		var pname=doc.getItemValueString("prof_name");
		entityCrud.saveAccessTypePerm(pname,context.getSubmittedValue());
		doc=vie.getNextDocument(doc);
	}

	//entityCrud.saveAccessTypePerm("Standard",context.getSubmittedValue());
}
function setProfileNumbers(){
	var profileName = getComponent("moduleCombo").getValue();
	//--Select Profile--
	if(profileName=="--Select Profile--"){
		return ;
	}
	
	var profileEdit = new bsuite.jsonparsing.ProfileEdit();
	var strModuleN = profileEdit.getModuleN(profileName);
	var strFeatureN = profileEdit.getFeatureN(profileName);
	var strEntityN = profileEdit.getEntityN(profileName);
	var strFieldN  = profileEdit.getFieldN(profileName);
	
	
	getComponent("inputNModules").setValue(strModuleN);
	getComponent("inputNFeatures").setValue(strFeatureN);
	getComponent("inputNEntities").setValue(strEntityN);
	getComponent("inputNFields").setValue(strFieldN);
	
	
	
}
function updateAddSchema(){
	var schemaObj = new bsuite.configure.Deploy();
	schemaObj.updateAllProfiles();

}

function updateDeleteSchema(){
	var profileObj = new bsuite.jsonparsing.ProfileEdit();
	profileObj.removeUpdate();
}
function loadProfile(){
	
	println("in loadProfile");	
	var profileName = getComponent("moduleCombo").getValue();
	viewScope.profileName = profileName;
	var moduleName = context.getSubmittedValue();
	viewScope.moduleName = moduleName;
	
	println("submitted value "+context.getSubmittedValue());
	
	if(moduleName == null || profileName == null){
		return;
		}
	println("in loadProfile 1");
	var profileObj = new bsuite.jsonparsing.ProfileEdit();
	var perm = profileObj.getModulePermission(profileName,moduleName);
	
	println("in loadProfile 2");
	
	
	var val = @Right(perm,":");

	println("in loadProfile 3");
	
	if(val.charAt(0)=="1"){

		viewScope.modulePerm = true;
	}
	else{

		viewScope.modulePerm = false;
	}
	
	println("in loadProfile 4");
	var strGroupN = profileObj.getNumberOfGroups(profileName, moduleName);
	println("in loadProfile 5");
	var strActionN = profileObj.getActionN(profileName, moduleName);
	println("in loadProfile 6");
	
	

	var component = getComponent("tabContent"); 
	var s = facesContext;
	var c1="/ccUIPermissions.xsp"; 
	var id="ccPerm";
	bsuite.weberon.DynamicCC.removePreview(component);
	bsuite.weberon.DynamicCC.loadCC(s, component, c1, id);	
	
	getComponent("moduleName").setValue(moduleName);
	getComponent("inputNGroups").setValue(strGroupN);
	getComponent("inputNActions").setValue(strActionN);
	
}

function setFieldNumber(strHiddenFId){
	var profileName = viewScope.profileName;
	var moduleName = viewScope.moduleName;
	var entityName = getComponent("").getValue();
	
	if(prfileName=null || mdouleName=null || mdouleName =null){
		return ;
	}
	
	var profileObj = new bsuite.jsonparsing.ProfileEdit();
	var fieldN = profileObj.getFieldN(prfileName,mdouleName,mdouleName)
	
}