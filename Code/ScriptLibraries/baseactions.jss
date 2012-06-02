function storeSelectedViewids(){
	println("setting ids");
	var x= getComponent('viewPanel1');
	var y=x.getSelectedIds();
	
	viewScope.put('select',y);
	for(i=0;i<y.length;i++){	println(y[i]);}
	var l= y.length
	println("length"+l)
	if(l == 0 ){
		viewScope.errsms="Please select a document";
		viewScope.errval="true";
		println("no selected documents");
	}else if(l > 1){
		viewScope.errsms="Please select only one document";
		viewScope.errval="true";
		println("Please select only one document");
	}else{
		viewScope.errsms="";
		viewScope.errval="false";
		println(" selected documents");
		viewScope.put('seldocids',y);
		println("under seldocids12" + seldocids);
	}
}

function storeSelectedViewids1()
{
	println("setting ids11111");
	var x= getComponent('viewPanel1');
	var y=x.getSelectedIds();
	
	viewScope.put('select',y);
	for(i=0;i<y.length;i++){	println(y[i]);}
	var l= y.length
	println("length"+l)
	viewScope.errsms="";
	viewScope.errval="false";
	println(" selected documents");
	viewScope.put('seldocids',y);
	println("under seldocids" + seldocids);
}
function storeViewids(){
	println("setting ids");
	var x= getComponent('viewPanel1');
	var y=x.getSelectedIds();
	
	for(i=0;i<y.length;i++){	println(y[i]);}
	var l= y.length
	println("length"+l)
	if(l == 0 ){
		viewScope.errsms="Please select a document";
		viewScope.errval="true";
		println("no selected documents");
	}else{
		viewScope.errsms="";
		viewScope.errval="false";
		println(" selected documents");
		viewScope.put('seldocids',y);
	}
}
//---------------------------------------------------------------------
function storeSelectedMailid(){
	var mailView= getComponent('viewMail');
	var link=mailView.getSelectedIds();
	if(link.length == 0 ){
	viewScope.errmsg="Please select a document";
		
}else if(link.length  > 1){
	viewScope.errmsg="Please select only one document";
}else{
	print("setting maidocids")
	viewScope.errmsg=""
	viewScope.put('maildocid',link);
}	
}
//-------------------------------------------------------------------------------
function storeSubmittedRequest(){
	println(context.getSubmittedValue());
	viewScope.pp3choice= context.getSubmittedValue();
	println(viewScope.pp3choice);
	viewScope.put("action",pp3choice);
	
}

//-------------------------------------------------------
function getmaildb(){
	var mailfilename;
	var mailserver;
	// setting the server copy of the NAMES.nsf
	var ladb:NotesDatabase = session.getDatabase(database.getServer(), "names.nsf");	
	var  destview:NotesView =ladb.getView("($Users)");
	var person:NotesDocument = destview.getDocumentByKey(@Name("[CN]",@UserName()),true);


		if ( person != null ) {

			mailfilename = person.getItemValueString("mailfile");	
						mailserver = person.getItemValueString("mailserver");		
		}																	
		else{
			code_g = "B093" // Person document not available - UNLIKELY
			println("B093");
			//message_g = getMessageString(code_g)
		//viewScope.err	
			return null;
		}
																											
		if( mailfilename  == "") {
			code_g = "B094"; //Mail file name not mentioned
			println("B094");
			//message_g = getMessageString(code_g);
			//viewScope.err	
			return null;
		}		else if(mailserver == ""){
			code_g = "B095" ; //Mail server not mentioned.  NO assumption made here that always it will be the current server
			println( "B095");
			//message_g = getMessageString(code_g);
			//viewScope.err	
			return null;
		}	
			//mailserver=@Name("[ABB]",mailserver);														
//			println(mailserver+"!!"+mailfilename);
			return	mailserver+"!!"+mailfilename	;			

}


function checkmailviewerr(){
	return viewScope.get('errmsg');
}
function sendDocumentReference(){
	var contrl= new bsuite.controller.CommonController();
	contrl.sendReferenceRequest();
}
//-----------------------------------------------------------------
function associateMailtoDocument(){	

		storeSelectedMailid();
		var err=viewScope.get('errmsg');
		if(err==""){
				var contrl= new bsuite.controller.CommonbaseController();
				contrl.associationRequest();
		}
}
//-----------------------------------------------------------------
function subscribe(){
	var contrl= new bsuite.controller.CommonbaseController();
	contrl.subscriptionRequest();
}
//---------------------------------------------------------------------
function unsubscribe(){
	var contrl= new bsuite.controller.CommonbaseController();
	contrl.unsubscriptionRequest();
}
//-----------------------------------------------------------------------
function createactivity(){
}
//--------------------------------------------------------------------
function s_delete(){

}
//-----------------------------------------------------------------------------------
function getfavdb(){
	var db = @Word(@Implode(@DbName(), ";"), ";", 2);
	var dbpath = @LeftBack(db,12);
	var fdb = dbpath + "favorite.nsf";
	var serv = @Word(@Implode(@DbName(), ";"), ";", 1);
	var favdb:NotesDatabase=session.getDatabase(serv,fdb);
	if(favdb.isOpen()){
		viewScope.errsms="";
		viewScope.errval="false";
		return "true";
	}	else{
		/*
		code_g = "B061"
		message_g = getMessageString(code_g) + " favorite.nsf"
		Msgbox message_g,,"Message # " + code_g
	*/
		viewScope.errsms="Please contact your Administrator because the following Module is not available : favorite.nsf";
		viewScope.errval="true";
		return "false";
	}

}


//---------------------------------------------------------------------
function loadCC(Name:String){
	//get panel control as paretn of custom control to be included
	 var objParent = getComponent("view");
	 
	 var parentchild = objParent.getChildren();
	 var childcount =objParent.getChildCount();
	 println(childcount);
	 if (childcount ==2){
	 	println("child one");
		parentchild.remove(1);
	 }
	 //page name of custom control to include
	 var strPageName = "/"+Name;
	 
	 //create new UIIncludeComposite()
	 var objControl = new com.ibm.xsp.component.UIIncludeComposite();
	 
	 //set the pagename to the UIIncludeComposite object
	 objControl.setPageName(strPageName);
	 
	 //Ensure a uniqueid for this control
	 objControl.setId("new"+@Unique());
	 
	 //Create a builder object from Xpages Extension Library
	 var objBuilder = com.ibm.xsp.extlib.builder.ControlBuilder();
	
	//to use the builder you need ControlImpl objects.
	//Because the builder can only work with those objects
	var classControlImpl = new objBuilder.getClass().getDeclaredClasses()[1];
	
	//create a ControlImpl object  of the parent panel
	var objImplParent = new classControlImpl(objParent);
	
	//create a ControlImpl object  of the custom control
	var objImplControl = new classControlImpl(objControl);
	
	 //add  the new custom control to the parent
	 
	 objImplParent.addChild(objImplControl);
	 
	 //build the updated parent control
	 objBuilder.buildControl(facesContext,objImplParent,false);
}
	function saveactivity()
	{
	
//var richtext = getComponent("inputRichText1");	
//var plainText= @Abstract("[TRYFIT]", 100, "", "Body");
//var plainText= bsuite.helper.BsuiteUtil().removechar(plainText,"\n");
//plainText=@LeftBack(@RightBack(plainText, 1),1);
//document1.setValue("Subject", plainText);
//document1.replaceItemValue("bsuiteStatus", "0");
//document1.setValue("BsuiteTitle", plainText);
//println("Seldocid"+ seldocids);
var currentdb=session.getCurrentDatabase();
var serv=currentdb.getServer();
var dbpath=currentdb.getFilePath();
var pathname=@LeftBack(dbpath,12);
var tadb : NotesDatabase=session.getDatabase(serv, pathname + "employee.nsf");
println(tadb);
println(pathname);

	
	
	
//println(seldocids[0]);
var databasename=tadb.getFileName()

	
var doc:NotesDocument=tadb.getDocumentByID(seldocids[0]);
var unid=doc.getUniversalID();
println(unid);
document1.setValue("Unid",unid);
//document1.setValue("DatabaseName",databasename);
//document1.setValue("Createdate",@Created());
//document1.setValue("DocumentCreator",@UserName());
	
/*
var x= getComponent('viewPanel1');
	var y=x.getSelectedIds();
	println("Inside save and close");
	//println(x);
	println(y);
	
	for(i=0;i<y.length;i++){	println(y[i]);}
	var l= y.length
	println("length"+l)
	if(l == 0 ){
		viewScope.errsms="Please select a document";
		viewScope.errval="true";
		println("no selected documents");

	}
	else{
		println(y);
		document1.setValue("Unid", y);
	}
	*/
	}
	function CreateAssocation()
	{
	println("setting ids");
	var x= getComponent('dataView1');
	var y=x.getSelectedIds();
	var z=getComponent('dataView2');
	var s=z.getSelectedIds();
	viewScope.put('select',y);	
	viewScope.put('seldocids',y);
	viewScope.put('seldocids2',s);
}
function CreateDocAssociation()
{
	var x=viewScope.get("select")
print(x);
var currentdb=session.getCurrentDatabase();
var serv=currentdb.getServer();
var dbpath=currentdb.getFilePath();
var pathname=@LeftBack(dbpath,17);
var tadb : NotesDatabase=session.getDatabase(serv, pathname + "user.nsf");
println("database"+tadb);
println("pathname"+pathname);




var taac:NotesDatabase=session.getDatabase(serv, pathname + "activity.nsf");
var taar:NotesDatabase=session.getDatabase(serv,pathname+"relation.nsf")
//var subject=viewScope.get("subject");

println("sel: "+ select);

if(select!="")
	{
	
var doc:NotesDocument=tadb.getDocumentByID(seldocids[0]);
var doc1:NotesDocument=taac.getDocumentByID(seldocids2[0]);
var unid=doc.getUniversalID();
var unid1=doc1.getUniversalID();
println("user unid" + unid);
println("activity unid"+ unid1);

var fullname=doc.getItemValueString("display_field");
var fullname1=doc1.getItemValueString("display_field");
println("hi");
viewScope.put('unids',unid);


println("for testing unids"+ unid);
var docrel:NotesDocument=taar.createDocument();
docrel.replaceItemValue("form","association");
docrel.replaceItemValue("sourceid",unid);
docrel.replaceItemValue("sourcedb",tadb.getFileName().toString());
docrel.replaceItemValue("src_data", fullname);
docrel.replaceItemValue("targetdb",taac.getFileName().toString());
docrel.replaceItemValue("trg_data", fullname1);
docrel.replaceItemValue("targetid",unid1);

docrel.computeWithForm(true,false);
docrel.save(true,false);

println("end of code");
	}
}