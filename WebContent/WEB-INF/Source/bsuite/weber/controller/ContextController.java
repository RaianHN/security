package bsuite.weber.controller;

import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.View;
import bsuite.weber.tools.BSUtil;
import bsuite.weber.tools.JSFUtil;

import com.ibm.xsp.extlib.util.ExtLibUtil;

public class ContextController {
	
	private static String beanid;
	@SuppressWarnings("unchecked")
	public static String getAssociatedTrgString(String relation){//for manager
			
		String c=null;
		
		try {							
			Vector tmp = new Vector();
			tmp.add(relation);//"MANAGER_TO"
			String x = getlookupkey(tmp);
			if(x ==null){
				
				return c;
			}
			tmp.clear();
			tmp.add(x);
			c=(String) JSFUtil.DBLookupString(beanid, "TargetRelation", tmp, 1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return c;
	}
	@SuppressWarnings("unchecked")
	public static String getAssociatedSrcString(String relation){//for manager
		
		String c=null;				
		Vector tmp = new Vector();
		tmp.add(relation);//"MANAGER_TO"

		String x = getlookupkey(tmp);
		if(x ==null){
			
			return c;
		}
		tmp.clear();
		tmp.add(x);
		c=(String) JSFUtil.DBLookupString(beanid, "SourceRelation", tmp, 1);

		return c;
	}
	//For reportee Relation is always forward 
	
	@SuppressWarnings("unchecked")
	public static Vector getAssociatedSrcVector(String relation){
	System.out.println("start");
		Vector c=  new Vector();	
		Vector tmp = new Vector();
		tmp.add(relation);//"MANAGER_TO"
		System.out.println("start");
		String x = getlookupkey(tmp);
		if(x ==null){
			c= null;
			return c;
		}
		tmp.clear();
		tmp.add(x);
		System.out.println("startvector");
		c=(Vector)JSFUtil.DBLookupVector(beanid, "SourceRelation", tmp, 1);
		for(Object l:c.toArray()){
			System.out.println(l.toString());
		}
		return  c;
		
	}
	
	@SuppressWarnings("unchecked")
	public static Vector getAssociatedTrgVector(String relation){
		
		Vector c=  new Vector();		
	
		Vector tmp = new Vector();
		tmp.add(relation);//"MANAGER_TO"
		String x = getlookupkey(tmp);
		if(x ==null){
			c= null;
			return c;
		}
		tmp.clear();
		tmp.add(x);
		c=(Vector)JSFUtil.DBLookupVector(beanid, "TargetRelation", tmp, 1);
		
		return  c;
		
	}
	@SuppressWarnings("unchecked")
	private static String getlookupkey(Vector tmp){
		String  y;
		String reluid;
		try {
			Database rel = ExtLibUtil.getCurrentSession().getDatabase("", BSUtil.getBsuitePath(ExtLibUtil.getCurrentDatabase())+"relation.nsf");
			View vrel = rel.getView("CategoryRelation");
			Document reldoc1 = vrel.getDocumentByKey(tmp);
			reluid = reldoc1.getUniversalID();
			if(reluid==null){System.out.println("reluid is null");return null;}
			//beanid = (String)JSFUtil.getSessionComponent("Beanid");
				
				  y = (String) JSFUtil.getSessionComponent("docrelId");
				  if(reluid==null){System.out.println("reluid is null");return null;}
			return(y+"|"+reluid);
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;		
		
	}
	

	
}
