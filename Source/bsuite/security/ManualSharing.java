package bsuite.security;

import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import com.bsuite.utility.JSFUtil;
import com.bsuite.utility.Utility;
import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.View;

import bsuite.relationship.Association;



import lotus.domino.Document;



public class ManualSharing {

	String userName;
	Document doc;
	public ManualSharing(String database, String unid) {
		Database db = Utility.getDatabase(database);
		userName = Utility.getEffectiveUserName(1);
		try
		{
			doc = db.getDocumentByUNID(unid);
		}
		catch (NotesException e)
		{
			e.printStackTrace();
		}
	}
	@SuppressWarnings("unchecked")
	public void sharing() {
		Map viewscope  = (Map)JSFUtil.getVariableValue("viewScope");
		
		
		try {
			
			Vector users = new Vector((ArrayList) viewscope.get("users"));
			Vector roles = new Vector((ArrayList) viewscope.get("roles"));

			doc.replaceItemValue("shareWithUser", users);
			doc.replaceItemValue("shareWithRoles", roles);
			doc.save(true, false);
		} catch (Exception e) {
		}

	}

	public boolean sharevisible() {
		Association as = new Association();		
		String currentuser = as.getFormattedName(userName, "common");
		try {
	
			String createdBy = doc.getItemValueString("createdBy");
			if (createdBy.equals(currentuser)) {
				return true;
			}

		} catch (Exception e) {
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public Vector getFullName() {
		Vector profileNames = new Vector();
		try {
			
			
			Database employeesdb = Utility.getDatabase("Employees");
			View vie = employeesdb.getView("employeeprofile");
			Document doc = vie.getFirstDocument();

			while (doc != null) {
				profileNames.add(doc.getItemValueString("FullName"));
				doc = vie.getNextDocument(doc);
			}
			Association as = new Association();
			String currentuser = as.getFormattedName(userName, "abr");
			profileNames.remove(currentuser);

		} catch (Exception e) {
		}

		return profileNames;
	}

	@SuppressWarnings("unchecked")
	public Vector defaultValues() {

		Vector defaultusers = new Vector();
		try {

			defaultusers = doc.getItemValue("shareWithUser");
		} catch (Exception e) {
		}
		return defaultusers;
	}

	@SuppressWarnings("unchecked")
	public Vector defaultValuesRoles() {		
		Vector defaultroles = new Vector();
		try {
			defaultroles = doc.getItemValue("shareWithRoles");

		} catch (Exception e) {
		}
		return defaultroles;
	}

}
