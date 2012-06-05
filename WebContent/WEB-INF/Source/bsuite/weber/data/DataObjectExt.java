package bsuite.weber.data;

import java.io.Serializable;

import com.ibm.xsp.model.DataObject;

/**
 * Extension of {@link DataObject} to add a {@link #store()} method
 * 
 * @author Karsten Lehmann
 */
public interface DataObjectExt extends DataObject, Serializable {

	/**
	 * Stores the data in the database
	 */
	public void store();
	
}
