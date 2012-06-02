package bsuite.weber.model;

import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;

public class BsuiteBase extends BsuiteWorkFlow{

	public BsuiteBase() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BsuiteBase(Document doc) throws NotesException {
		super(doc);
		// TODO Auto-generated constructor stub
	}

	public BsuiteBase(DocumentCollection dc) {
		super(dc);
		// TODO Auto-generated constructor stub
	}

}
