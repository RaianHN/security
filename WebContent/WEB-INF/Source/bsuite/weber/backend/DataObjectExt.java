package bsuite.weber.backend;

import java.io.Serializable;

import com.ibm.xsp.model.DataObject;

public interface DataObjectExt extends DataObject,Serializable{
	/**
	 * Stores the data in the database
	 */
	public void store();

	public void reset();

	public void delete();
}
