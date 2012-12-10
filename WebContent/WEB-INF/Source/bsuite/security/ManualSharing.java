package bsuite.security;

import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.View;

import bsuite.relationship.Association;
import bsuite.utility.*;


import lotus.domino.Document;

import bsuite.utility.Utility;

public class ManualSharing {

	String userName;	
	public ManualSharing() {
		try {
			userName = Utility.getCurrentSession().getEffectiveUserName();
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@SuppressWarnings("unchecked")
	public void sharing() {
		Map viewscope = (Map) JSFUtil.getVariableValue("viewScope");
		Map sessionscope = (Map) JSFUtil.getVariableValue("sessionScope");

		String dbname = (String) viewscope.get("moduleName");

		if (dbname.contains("_")) {
			dbname = dbname.replace("_", " ");
		}

		dbname = dbname.toLowerCase().replace(" ", "") + ".nsf";
		try {
			
			Database entitydb = Utility.getDatabase(dbname);
			String documentId = (String) sessionscope.get("documentId");
			Document doc = entitydb.getDocumentByUNID(documentId);

			Vector users = new Vector((ArrayList) viewscope.get("users"));
			Vector roles = new Vector((ArrayList) viewscope.get("roles"));

			doc.replaceItemValue("shareWithUser", users);
			doc.replaceItemValue("shareWithRoles", roles);
			doc.save(true, false);
		} catch (Exception e) {
		}

	}

	@SuppressWarnings("unchecked")
	public boolean sharevisible() {
		Association as = new Association();
		
		String currentuser = as.getFormattedName(userName, "common");
		Map viewscope = (Map) JSFUtil.getVariableValue("viewScope");
		Map sessionscope = (Map) JSFUtil.getVariableValue("sessionScope");
		String dbname = (String) viewscope.get("moduleName");

		if (dbname.contains("_")) {
			dbname = dbname.replace("_", " ");
		}

		dbname = dbname.toLowerCase().replace(" ", "") + ".nsf";

		try {
			
			Database entitydb = Utility.getDatabase(dbname);
			String documentId = (String) sessionscope.get("documentId");
			Document doc = entitydb.getDocumentByUNID(documentId);
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
			
			
			Database employeesdb = Utility.getDatabase("employees.nsf");
			View vie = employeesdb.getView("Employee");
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
		Map viewscope = (Map) JSFUtil.getVariableValue("viewScope");
		Map sessionscope = (Map) JSFUtil.getVariableValue("sessionScope");
		
		
		String dbname = (String) viewscope.get("moduleName");

		if (dbname.contains("_")) {
			dbname = dbname.replace("_", " ");
		}

		dbname = dbname.toLowerCase().replace(" ", "") + ".nsf";
		Vector defaultusers = new Vector();
		try {
			
			Database entitydb = Utility.getDatabase(dbname);
			String documentId = (String) sessionscope.get("documentId");
			Document doc = entitydb.getDocumentByUNID(documentId);
			defaultusers = doc.getItemValue("shareWithUser");
		} catch (Exception e) {
		}
		return defaultusers;
	}

	@SuppressWarnings("unchecked")
	public Vector defaultValuesRoles() {
		Map viewscope = (Map) JSFUtil.getVariableValue("viewScope");
		Map sessionscope = (Map) JSFUtil.getVariableValue("sessionScope");
		
		
		String dbname = (String) viewscope.get("moduleName");

		if (dbname.contains("_")) {
			dbname = dbname.replace("_", " ");
		}

		dbname = dbname.toLowerCase().replace(" ", "") + ".nsf";
		Vector defaultroles = new Vector();
		try {
			Database entitydb = Utility.getDatabase(dbname);

			String documentId = (String) sessionscope.get("documentId");

			Document doc = entitydb.getDocumentByUNID(documentId);

			defaultroles = doc.getItemValue("shareWithRoles");

		} catch (Exception e) {
		}
		return defaultroles;
	}

}
