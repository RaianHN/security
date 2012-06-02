package bsuite.weber.model;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;

public class DataBmod extends BsuiteEntity {

	private String display_field;
	private String form;
	private String UNID;
	
	private String docdb;

	public DataBmod() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DataBmod(Document doc) throws NotesException {
		super(doc);
		display_field=doc.getItemValueString("display_field");
		form = doc.getItemValueString("Form");
		UNID = doc.getUniversalID();
		docdb = doc.getParentDatabase().getFileName();
	}

	

	public String getDocdb() {
		return docdb;
	}

	

	public String getDisplay_field() {
		return display_field;
	}

	public void setDisplay_field(String display_field) {
		this.display_field = display_field;
	}

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public String getUNID() {
		return UNID;
	}

	public void setUNID(String unid) {
		UNID = unid;
	}

	public DataBmod(DocumentCollection dc) {
		super(dc);
		// TODO Auto-generated constructor stub
	}
	
	public Document getDocument(){
		
		return currentdoc;
		
	}
	
	
}
