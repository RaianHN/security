package bsuite.weber.model;

import lotus.domino.Document;
import lotus.domino.NotesException;

public class User extends BsuiteEntity {
	private String u_firstname;
	private String u_middlename;
	private String u_lastname;
	private String u_email;
	private String u_contact;
	private String u_location;
	private final String obid = "user";
	private String display_field;
	private String U_UNID;
	//private String OWNER;
	public User(){}
	public User(String UNId){
		super();
		this.setU_UNID(UNId);
		try {
			Document d1=currentdb.getDocumentByUNID(U_UNID);
			this.currentdoc= d1;
			initdoc();
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		
	}

		
	
	public User(Document doc) throws NotesException{
		super(doc);
		initdoc();
	}
	private void initdoc() {
		try {
			this.u_firstname=currentdoc.getItemValueString("firstname");
			this.u_middlename = currentdoc.getItemValueString("middlename");
			this.u_lastname = currentdoc.getItemValueString("lastname");
			this.u_email = currentdoc.getItemValueString("email");
			this.u_contact = currentdoc.getItemValueString("contact");
			this.u_location = currentdoc.getItemValueString("location");
			this.U_UNID= currentdoc.getUniversalID();
			//this.OWNER = currentdoc.getItemValueString(arg0)
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public User(String firstname, String middlename, String lastname,
			String email, String contact, String location) {

		super();
		this.u_firstname = firstname;
		this.u_middlename = middlename;
		this.u_lastname = lastname;
		this.u_email = email;
		this.u_contact = contact;
		this.u_location = location;
	}

	
	public String getU_firstname() {
		return u_firstname;
	}
	public void setU_firstname(String u_firstname) {
		this.u_firstname = u_firstname;
	}
	public String getU_middlename() {
		return u_middlename;
	}
	public void setU_middlename(String u_middlename) {
		this.u_middlename = u_middlename;
	}
	public String getU_lastname() {
		return u_lastname;
	}
	public void setU_lastname(String u_lastname) {
		this.u_lastname = u_lastname;
	}
	public String getU_email() {
		return u_email;
	}
	public void setU_email(String u_email) {
		this.u_email = u_email;
	}
	public String getU_contact() {
		return u_contact;
	}
	public void setU_contact(String u_contact) {
		this.u_contact = u_contact;
	}
	public String getU_phone() {
		return u_location;
	}
	public void setU_phone(String u_phone) {
		this.u_location = u_phone;
	}
	public String getU_location() {
		return u_location;
	}
	public void setU_location(String u_location) {
		this.u_location = u_location;
	}
	public String getDisplay_field() {
		return display_field;
	}
	public void setDisplay_field(String display_field) {
		this.display_field = display_field;
	}
	public String getObid() {
		return obid;
	}
	public String getU_UNID() {
		return U_UNID;
	}
	public void setU_UNID(String u_unid) {
		U_UNID = u_unid;
	}
}
