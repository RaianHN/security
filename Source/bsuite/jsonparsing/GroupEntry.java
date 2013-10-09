package bsuite.jsonparsing;

 /**Used to maintain the group entries inside a menu(group), contains getter and setters for groupentry and its permissions
  *@author JPrakash
  *@created Oct 9, 2013
 */
public class GroupEntry {
	private String type;//entity of feature
	private String name;//name of the entry
	private String visible;//1 or 0 

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVisible() {
		return visible;
	}

	public void setVisible(String visible) {
		this.visible = visible;
	}

}
