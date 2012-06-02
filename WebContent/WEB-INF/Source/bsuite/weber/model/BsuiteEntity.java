package bsuite.weber.model;

import java.util.Vector;

import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;

public class BsuiteEntity extends BsuiteBase {
	private boolean changed;
	public BsuiteEntity() {
		super();
		
	}

	public BsuiteEntity(Document doc) throws NotesException {
		super(doc);
		
	}

	public BsuiteEntity(DocumentCollection dc) {
		super(dc);
	}
	@SuppressWarnings("unchecked")
	protected Vector readValues(String itemName) {
	    Vector retval = null;
	    try {
	        retval = (Vector) currentdoc.getItemValue(itemName);
	    } catch (NotesException ne) {
	        handleException(ne);
	    }
	    return retval;
	}

	protected String readString(String itemName) {
	    String retval = null;
	    try {
	        retval =  currentdoc.getItemValueString(itemName);
	    } catch (NotesException ne) {
	        handleException(ne);
	    }
	    return retval;
	}
	protected double readDouble(String itemName) {
	    double retval=0 ;
	    try {
	        retval =  currentdoc.getItemValueDouble(itemName);
	    } catch (NotesException ne) {
	        handleException(ne);
	    }
	    return retval;
	}
	

	protected int readInteger(String itemName) {
	    int retval=0;
	    try {
	        retval =  currentdoc.getItemValueInteger(itemName);
	    } catch (NotesException ne) {
	        handleException(ne);
	    }
	    return retval;
	}


	protected void writeValue(String itemName, Object value) {
	    try {
	        if (value == null) {
	             currentdoc.removeItem(itemName);
	         }
	        currentdoc.replaceItemValue(itemName, value);
	    } catch (NotesException ne) {
	        handleException(ne);
	    }
	    changed = true;
	}



	public void close() {
	    if (currentdoc == null)
	        return;
	    try {
	        if (changed == true) {
	            currentdoc.save();
	        }
	        currentdoc.recycle();
	        currentdoc = null;
	    } catch (NotesException ne) {
	        handleException(ne);
	    }
	}



	protected boolean delete()// throws DeleteException, InvalidStateException 
	{
	   // this.checkState();

	    boolean ret = false;
	    try {
	        synchronized (this) {
	        ret = currentdoc.remove(true);
	        currentdoc = null;
	    }

	    } catch(NotesException ne) {
	        handleException(ne);
	    }
	    return ret; 
	}

	
}
	
	


