package bsuite.jsonparsing;

import java.util.ArrayList;

 /**This is the profile json object, which will hold the profile and its permissions as a json object, contains getters and setters for
  * its modules and its name
  *@author JPrakash
  *@created Oct 9, 2013
 */
public class ProfileJson {

	private String profName;
	private ArrayList<Module> modules;
	

	public String getProfName() {
		return profName;
	}
	public void setProfName(String profName) {
		this.profName = profName;
	}
	public ArrayList<Module> getModules() {
		return modules;
	}
	public void setModules(ArrayList<Module> modules) {
		this.modules = modules;
	}
	
	
	
}
