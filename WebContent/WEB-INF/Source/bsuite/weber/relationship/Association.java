package bsuite.weber.relationship;

import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.Name;
import lotus.domino.View;
import bsuite.weber.model.BsuiteWorkFlow;
import bsuite.weber.tools.JSFUtil;

public class Association extends BsuiteWorkFlow {
	
	public  Document getAssociatedProfile(String currentuser) {
		try {

			Database reldb = session.getDatabase("", bsuitepath
					+ "relation.nsf");
			String relname = "HAS_A";
			View relview = reldb.getView("CategoryRelation");
			Document reldoc = relview.getDocumentByKey(relname);
			String reldocunid = reldoc.getUniversalID();

			// Person unid
			// Changing the canonical form to abr form so that it will lookup in
			// the $VIMPeople view
			String abbrname = getFormattedName(currentuser, "abr");
			Document persondoc = getPerson(abbrname);
			String persondocunid = persondoc.getUniversalID();

			String lookupkey = JSFUtil.getlookupkey(reldocunid, persondocunid);
			Vector tmp = new Vector();
			tmp.add(lookupkey);
			String targetunid = JSFUtil.DBLookupString("relation",
					"SourceRelation", tmp, 4);
			Database securitydb = session.getDatabase("", bsuitepath
					+ "Security.nsf");
			Document profiledoc = securitydb.getDocumentByUNID(targetunid);

			return profiledoc;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public  String getAssociatedRoleName(String currentuser){
		try {

			Database reldb = session.getDatabase("", bsuitepath
					+ "relation.nsf");
			String relname = "HAS_ROLE";
			View relview = reldb.getView("CategoryRelation");
			Document reldoc = relview.getDocumentByKey(relname);
			String reldocunid = reldoc.getUniversalID();

			// Person unid
			// Changing the canonical form to abr form so that it will lookup in
			// the $VIMPeople view
			String abbrname = getFormattedName(currentuser, "abr");
			Document persondoc = getPerson(abbrname);
			String persondocunid = persondoc.getUniversalID();

			String lookupkey = JSFUtil.getlookupkey(reldocunid, persondocunid);
			Vector tmp = new Vector();
			tmp.add(lookupkey);
			String targetunid = JSFUtil.DBLookupString("relation",
					"SourceRelation", tmp, 4);
			Database securitydb = session.getDatabase("", bsuitepath
					+ "Security.nsf");
			Document roledoc = securitydb.getDocumentByUNID(targetunid);

			return roledoc.getItemValueString("role_name");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
	
	
	public Document getPerson(String username) {
		try {

			Database namesdb = session.getDatabase("", "names.nsf");
			View peopleview = namesdb.getView("($VIMPeople)");
			Document userdoc = peopleview.getDocumentByKey(username);
			return userdoc;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public String getFormattedName(String currentuser, String param) {
		try {			

			Name user = session.createName(currentuser);
			if (param.equals("abr")) {
				return user.getAbbreviated();
			}

			else if (param.equals("canonical")) {
				return user.getCanonical();
			} else if (param.equals("common")) {
				return user.getCommon();
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

}
