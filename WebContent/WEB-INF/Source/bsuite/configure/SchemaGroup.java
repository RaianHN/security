package bsuite.configure;

import java.util.ArrayList;

public class SchemaGroup {
	private String GroupName;
	private ArrayList<Object> GroupEntries;//Can contain feature or entity

	public String getGroupName() {
		return GroupName;
	}

	public void setGroupName(String groupName) {
		GroupName = groupName;
	}

	public ArrayList<Object> getGroupEntries() {
		return GroupEntries;
	}

	public void setGroupEntries(ArrayList<Object> groupEntries) {
		GroupEntries = groupEntries;
	}

}
