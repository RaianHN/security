package bsuite.jsonparsing;

 /**Used to define the entity action permission in the entity object in profile document, contains getters and setters
  * for action and its permission
  *@author JPrakash
  *@created Oct 9, 2013
 */
public class EntityAction {
	private String actionName;
	private String visible;

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public String getVisible() {
		return visible;
	}

	public void setVisible(String visible) {
		this.visible = visible;
	}

}
