package bsuite.jsonparsing;

 /***This class is for Features in the json String, for feature permissions and is stored in profile document, contains getter
  * and setters for feature and its permission, not in use currently
  *@author JPrakash
  *@created Oct 9, 2013
 */
public class FeaturePerm {

	private String featureName;
	private String visible;
	public String getFeatureName() {
		return featureName;
	}
	public void setFeaturename(String featurename) {
		this.featureName = featurename;
	}
	public String getVisible() {
		return visible;
	}
	public void setVisible(String visible) {
		this.visible = visible;
	}
	
	
	
}

