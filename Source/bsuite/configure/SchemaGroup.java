package bsuite.configure;

import java.util.ArrayList;

public class SchemaGroup {
	//This class will be used in defining the schema of the profiles.
	private String GroupName;
	private ArrayList<String> GroupEntries;//Can contain feature or entity

	public String getGroupName() {
		return GroupName;
	}

	public void setGroupName(String groupName) {
		GroupName = groupName;
	}

	public ArrayList<String> getGroupEntries() {
		return GroupEntries;
	}

	public void setGroupEntries(ArrayList<String> groupEntries) {
		GroupEntries = groupEntries;
	}


	

}
