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
			System.out.println("in get assoprofile");
			Database reldb = session.getDatabase("", bsuitepath
					+ "relation.nsf");
			String relname = "HAS_A";
			View relview = reldb.getView("CategoryRelation");
			Document reldoc = relview.getDocumentByKey(relname);
			String reldocunid = reldoc.getUniversalID();
			System.out.println("Relation UNID "+reldocunid);
			// Person unid
			// Changing the canonical form to abr form so that it will lookup in
			// the $VIMPeople view
			String abbrname = getFormattedName(currentuser, "abr");
			System.out.println("Current user Abbr Name "+abbrname);
			System.out.println("in get assoprofile1");
			Document persondoc = getPerson(abbrname);
			System.out.println("in get assoprofile2");
			String persondocunid = persondoc.getUniversalID();
			System.out.println("PersonDOc UNID "+persondocunid);
			System.out.println("in get assoprofile3");
			String lookupkey = JSFUtil.getlookupkey(reldocunid, persondocunid);
			System.out.println("in get assoprofile4");
			Vector tmp = new Vector();
			tmp.add(lookupkey);
			String targetunid = JSFUtil.DBLookupString("relation",
					"SourceRelation", tmp, 4);
			System.out.println("in get assoprofile5");
			Database securitydb = session.getDatabase("", bsuitepath
					+ "Security.nsf");
			System.out.println("in get assoprofile6");
			if(targetunid==null){
				return null;
			}
			Document profiledoc = securitydb.getDocumentByUNID(targetunid);
			System.out.println("in get assoprofile2");
			return profiledoc;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public  String getAssociatedRoleName(String currentuser){
		try {
			System.out.println("get ass rolename1 ");
			Database reldb = session.getDatabase("", bsuitepath
					+ "relation.nsf");
			String relname = "HAS_ROLE";
			View relview = reldb.getView("CategoryRelation");
			Document reldoc = relview.getDocumentByKey(relname);
			String reldocunid = reldoc.getUniversalID();
			System.out.println("Relation UNID "+reldocunid);

			// Person unid
			// Changing the canonical form to abr form so that it will lookup in
			// the $VIMPeople view
			String abbrname = getFormattedName(currentuser, "abr");
			Document persondoc = getPerson(abbrname);
			String persondocunid = persondoc.getUniversalID();
			System.out.println("get ass rolename2 ");
			String lookupkey = JSFUtil.getlookupkey(reldocunid, persondocunid);
			Vector tmp = new Vector();
			tmp.add(lookupkey);
			String targetunid = JSFUtil.DBLookupString("relation",
					"SourceRelation", tmp, 4);
			Database securitydb = session.getDatabase("", bsuitepath
					+ "Security.nsf");
			if(targetunid==null){
				return null;
			}
			Document roledoc = securitydb.getDocumentByUNID(targetunid);
			System.out.println("get ass rolename3 ");
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
	public String getEntityUnid(String entityName){
		
			try {
				Database entitiesdb = session.getDatabase("", bsuitepath+"bentity.nsf");				
				View allView = entitiesdb.getView("Entities");				
				Document entityDoc = allView.getDocumentByKey(entityName);
				return entityDoc.getUniversalID();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		
	}
	
	public String getParentEntity(String entityName){	
		String entityUnid = getEntityUnid(entityName);
		String relDocUnid = getRelationDocUnid("IS_A");
		String lookupkey = JSFUtil.getlookupkey(relDocUnid, entityUnid);
		Vector tmp = new Vector();
		tmp.add(lookupkey);
		String targetunid = JSFUtil.DBLookupString("relation","SourceRelation", tmp, 4);
		//Not complete
		
		return "";
	}
	
	private String getRelationDocUnid(String relName){
		
		try{
			Database reldb = session.getDatabase("", bsuitepath
					+ "relation.nsf");
			
			View relview = reldb.getView("CategoryRelation");
			Document reldoc = relview.getDocumentByKey(relName);
			return reldoc.getUniversalID();	
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return "";
	}

}
