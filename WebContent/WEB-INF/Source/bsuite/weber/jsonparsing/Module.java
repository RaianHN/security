package bsuite.weber.jsonparsing;

import java.util.ArrayList;

public class Module {

	private String moduleName;
	private String tabvis;
	private ArrayList<Entity> entities;
	
	
	
	public ArrayList<Entity> getEntities() {
		return entities;
	}
	public void setEntities(ArrayList<Entity> entities) {
		this.entities = entities;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	public String getTabvis() {
		return tabvis;
	}
	public void setTabvis(String tabvis) {
		this.tabvis = tabvis;
	}
	
	
}
