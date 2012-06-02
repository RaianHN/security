package bsuite.weber.model;



import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;

public class Activity extends BsuiteEntity{
 private String a_subject;
 private String display_field;
 @SuppressWarnings("unused")
private final String obid = "activity";

public Activity() {
	super();
	// TODO Auto-generated constructor stub
}

public Activity(Document doc) throws NotesException {
	super(doc);
	
	a_subject= doc.getItemValueString("subject");
	display_field=doc.getItemValueString("display_field");
	
}

public Activity(DocumentCollection dc) {
	super(dc);
	
}
 

public String getSubject() {
	return a_subject;
}

public void setSubject(String a_subject) {
	this.a_subject = a_subject;
}

public String getDisplay_field() {
	return display_field;
}

public void setDisplay_field(String display_field) {
	this.display_field = display_field;
}

}
