package bsuite.weber.security;

import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.View;
import bsuite.weber.model.BsuiteWorkFlow;
import bsuite.weber.relationship.Association;
import bsuite.weber.tools.BSUtil;
import bsuite.weber.tools.BsuiteMain;
import bsuite.weber.tools.JSFUtil;
import lotus.domino.Document;

import com.ibm.xsp.extlib.util.ExtLibUtil;

public class ManualSharing extends BsuiteMain {

	
	public  void sharing(){
		System.out.println("inside sharing method");
		Map viewscope=(Map) JSFUtil.getVariableValue("viewScope");
		Map sessionscope=(Map) JSFUtil.getVariableValue("sessionScope");
		
	//	bsuitepath=BSUtil.getBsuitePath(ExtLibUtil.getCurrentDatabase());
		String dbname=(String)viewscope.get("moduleName");
		
		if(dbname.contains("_")){
			dbname=dbname.replace("_"," ");
		}
		
		
		dbname=dbname.toLowerCase().replace(" ", "")+".nsf";
		try{
			Database entitydb=ExtLibUtil.getCurrentSession().getDatabase("", bsuitepath+dbname);
			System.out.println("Database where the current selected document is from "+dbname);
			String documentId=(String)sessionscope.get("documentId");
			System.out.println("Document Id "+documentId);
			Document doc=entitydb.getDocumentByUNID(documentId);
			System.out.println("Document Id 12 "+doc.getUniversalID());
			//ArrayList users=new ArrayList();
			//ArrayList roles=new ArrayList();
			Vector users=new Vector((ArrayList)viewscope.get("users"));
			Vector roles=new Vector((ArrayList)viewscope.get("roles"));
			//users=(Vector)viewscope.get("users");
			//roles=(Vector)viewscope.get("roles");
		//	System.out.println("users 2 "+users);
			doc.replaceItemValue("shareWithUser", users);
			doc.replaceItemValue("shareWithRoles", roles);
			doc.save(true,false);
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	public boolean sharevisible(){
		System.out.println("Inside shareVisble ");
		Association as=new Association();
		
		String currentuser=as.getFormattedName(this.currentuser, "common");
		System.out.println("Inside shareVisble currentuser "+ this.currentuser);
		Map viewscope=(Map) JSFUtil.getVariableValue("viewScope");
		Map sessionscope=(Map) JSFUtil.getVariableValue("sessionScope");
		//bsuitepath=BSUtil.getBsuitePath(ExtLibUtil.getCurrentDatabase());
		String dbname=(String)viewscope.get("moduleName");
		
		if(dbname.contains("_")){
			dbname=dbname.replace("_"," ");
		}
		
		
		dbname=dbname.toLowerCase().replace(" ", "")+".nsf";
	
		try{
			Database entitydb=ExtLibUtil.getCurrentSession().getDatabase("", bsuitepath+dbname);
			System.out.println("Bsuitepath "+bsuitepath);
			System.out.println("Database inside ManualSharing class "+dbname);
			String documentId=(String)sessionscope.get("documentId");
			System.out.println("Session Scope Document id "+documentId);
			Document doc=entitydb.getDocumentByUNID(documentId);
			String createdBy=doc.getItemValueString("createdBy");
			System.out.println("Current User "+currentuser);
			System.out.println("Created By "+createdBy);
			if(createdBy.equals(currentuser)){
				return true;
			}
	
		
		}catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}
	
	public Vector getFullName(){
		Vector profileNames  = new Vector();
		try{
			Database employeesdb=ExtLibUtil.getCurrentSession().getDatabase("", bsuitepath+"employees.nsf");
			View vie=employeesdb.getView("Employee");
			Document doc=vie.getFirstDocument();
		
			while(doc!=null){
				profileNames.add(doc.getItemValueString("FullName"));
				doc = vie.getNextDocument(doc);
			}
			Association as=new Association();
			String currentuser=as.getFormattedName(this.currentuser, "abr");
			profileNames.remove(currentuser);
			
		}catch (Exception e) {
			// TODO: handle exception
		}
	
		return profileNames;
	}

	public Vector defaultValues(){
		System.out.println("Inside Ddfualtvalues methods");
		Map viewscope=(Map) JSFUtil.getVariableValue("viewScope");
		Map sessionscope=(Map) JSFUtil.getVariableValue("sessionScope");
		bsuitepath=BSUtil.getBsuitePath(ExtLibUtil.getCurrentDatabase());
		String dbname=(String)viewscope.get("moduleName");
		
		if(dbname.contains("_")){
			dbname=dbname.replace("_"," ");
		}
		
		
		dbname=dbname.toLowerCase().replace(" ", "")+".nsf";
		Vector defaultusers=new Vector();
		try{
			Database entitydb=ExtLibUtil.getCurrentSession().getDatabase("", bsuitepath+dbname);

			String documentId=(String)sessionscope.get("documentId");
			
			Document doc=entitydb.getDocumentByUNID(documentId);
			
			defaultusers=doc.getItemValue("shareWithUser");
			
			System.out.println("Inside Ddfualtvalues methods UsersName "+defaultusers);
			
		}catch (Exception e) {
			// TODO: handle exception
		}
		return defaultusers;
	}
	
	public Vector defaultValuesRoles(){
		System.out.println("Inside Ddfualtvalues methods");
		Map viewscope=(Map) JSFUtil.getVariableValue("viewScope");
		Map sessionscope=(Map) JSFUtil.getVariableValue("sessionScope");
		bsuitepath=BSUtil.getBsuitePath(ExtLibUtil.getCurrentDatabase());
		String dbname=(String)viewscope.get("moduleName");
		
		if(dbname.contains("_")){
			dbname=dbname.replace("_"," ");
		}
		
		
		dbname=dbname.toLowerCase().replace(" ", "")+".nsf";
		Vector defaultroles=new Vector();
		try{
			Database entitydb=ExtLibUtil.getCurrentSession().getDatabase("", bsuitepath+dbname);

			String documentId=(String)sessionscope.get("documentId");
			
			Document doc=entitydb.getDocumentByUNID(documentId);
			
			defaultroles=doc.getItemValue("shareWithRoles");
			
			System.out.println("Inside Ddfualtvalues methods UsersName "+defaultroles);
			
		}catch (Exception e) {
			// TODO: handle exception
		}
		return defaultroles;
	}
	
	
	
}
