package bsuite.jsonparsing;

import java.util.ArrayList;

/**
 * This class is used to maintain the group permission of a profile, group
 * visible and list of entries and its permissions
 * 
 * @author JPrakash
 *@created Feb 20, 2013
 */
public class GroupPermission {
	
	private String name;
	private String visible;
	private ArrayList<GroupEntry> entries;//Can contain feature or entity
	
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
	public ArrayList<GroupEntry> getEntries() {
		return entries;
	}
	public void setEntries(ArrayList<GroupEntry> entries) {
		this.entries = entries;
	}

	
	
}
