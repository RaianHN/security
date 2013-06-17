package bsuite.jsonparsing;

import java.util.ArrayList;

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
