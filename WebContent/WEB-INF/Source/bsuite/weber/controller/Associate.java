package bsuite.weber.controller;

import java.util.Map;

import javax.faces.context.FacesContext;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;

import bsuite.weber.tools.BSUtil;

import com.ibm.xsp.extlib.util.ExtLibUtil;

public class Associate {

	private Session session;
	private FacesContext context;
	@SuppressWarnings("unchecked")
	private Map viewscope;
	private Database currentdb;
	@SuppressWarnings({ "unused", "unchecked" })
	private Map requestscope;
	private String sourcedb;
	private String source_id;
	private String src_disp;
	private String trgdb;
	private String trgid;
	private String trg_disp;
	private String bsuitepath;
	private Database reldb;
	@SuppressWarnings("unused")
	private String relationid;


	public Associate() {
		init();
	}
	
	
	@SuppressWarnings("unchecked")
	private void init() {
		 session = ExtLibUtil.getCurrentSession();
		 context = FacesContext.getCurrentInstance();
		viewscope = (Map) context.getApplication().getVariableResolver().resolveVariable(context, "viewScope");
		currentdb= ExtLibUtil.getCurrentDatabase();
		 requestscope=(Map) context.getApplication().getVariableResolver().resolveVariable(context, "requestScope");
		 bsuitepath=BSUtil.getBsuitePath(currentdb);
		 try {
			reldb = session.getDatabase("", bsuitepath+"relation.nsf");
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	public void createAssociation(){
		if(getparameters()){
			
			createdoc();
			}
		
	}


	


	private void createdoc() {
		
		try {
			Document doc1= reldb.createDocument();
			doc1.replaceItemValue(sourcedb, sourcedb);
			doc1.replaceItemValue("", source_id);
			doc1.replaceItemValue("", src_disp);
			doc1.replaceItemValue("", trgdb);
			doc1.replaceItemValue("", trgid);
			doc1.replaceItemValue("", trg_disp);
			
			doc1.replaceItemValue("","");
			
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private boolean getparameters() {
		
		
		
		sourcedb=(String) this.viewscope.get("");
		if(sourcedb.equals(null)){return false;}
		source_id=(String) this.viewscope.get("");
		if(source_id.equals(null)){return false;}
		src_disp = (String) this.viewscope.get("");
		if(src_disp.equals(null)){return false;}
		trgdb= (String)this.viewscope.get("");
		if(trgdb.equals(null)){return false;}
		trgid = (String)this.viewscope.get("");
		if(trgid.equals(null)){return false;}
		trg_disp = (String)this.viewscope.get("");
		if(trg_disp.equals(null)){return false;}
		
		relationid = (String)this.viewscope.get(""); 
		
		return true;
	}
}
