package bsuite.configure;

 /**Used to define the field of an entity to update in the modue object of module document, contains getter and
  * setters for field name and field type
  *@author JPrakash
  *@created Oct 9, 2013
 */
public class Field {
	private String fieldName;
	private String fieldType;
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
}
