package bsuite.jsonparsing;

 /**This class is for Features in the json String, for feature permissions and is stored in profile document, contains getter
  * and setters for feature and its permission.
  *@author JPrakash
  *@created Oct 9, 2013
 */
public class Feature{
	private String featureName;
	private String visible;

	public String getFeatureName() {
		return featureName;
	}

	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}
	
	
	public String getVisible() {
		return visible;
	}
	public void setVisible(String visible) {
		this.visible = visible;
	}
	
}
