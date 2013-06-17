
package com.bsuite.utility;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.View;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.bsuite.err.ErrorHandler;

/**
 * DbNames is an application scoped bean [This bean is used to get the database
 * names, used in specifying the datasources dbnames in bsuiteui database ex: to
 * bind document datasource - value will be Folder/filename.nsf getEmployees()
 * from this bean will return ex: weberon/employee.nsf, this way we can remove
 * the harcoded database and folder names
 * 
 * 
 * @author JPrakash
 *@created Mar 20, 2013
 */

public class DbNames {

	private String auditTrail;
	private String documents;
	private String employees;
	private String errorCodes;
	private String favorites;
	private String help;
	private String inOut;
	private String policies;
	private String profiles;
	private String reports;
	private String telemarketing;
	private String manageBsuite;
	private String relation;
	
	public String getRelation()
	{
		return relation;
	}

	public void setRelation(String relation)
	{
		this.relation = relation;
	}

	//
	private View profileView;
	private String prodFolder;

	public String getAuditTrail() {
		return auditTrail;
	}

	public void setAuditTrail(String auditTrail) {
		this.auditTrail = auditTrail;
	}

	public String getDocuments() {
		return documents;
	}

	public void setDocuments(String documents) {
		this.documents = documents;
	}

	public String getEmployees() {
		return employees;
	}

	public void setEmployees(String employees) {
		this.employees = employees;
	}

	public String getErrorCodes() {
		return errorCodes;
	}

	public void setErrorCodes(String errorCodes) {
		this.errorCodes = errorCodes;
	}

	public String getFavorites() {
		return favorites;
	}

	public void setFavorites(String favorites) {
		this.favorites = favorites;
	}

	public String getHelp() {
		return help;
	}

	public void setHelp(String help) {
		this.help = help;
	}

	public String getInOut() {
		return inOut;
	}

	public void setInOut(String inOut) {
		this.inOut = inOut;
	}

	public String getPolicies() {
		return policies;
	}

	public void setPolicies(String policies) {
		this.policies = policies;
	}

	public String getProfiles() {
		return profiles;
	}

	public void setProfiles(String profiles) {
		this.profiles = profiles;
	}

	public String getReports() {
		return reports;
	}

	public void setReports(String reports) {
		this.reports = reports;
	}

	public String getTelemarketing() {
		return telemarketing;
	}

	public void setTelemarketing(String telemarketing) {
		this.telemarketing = telemarketing;
	}
	
	

	public String getManageBsuite() {
		return manageBsuite;
	}

	public void setManageBsuite(String manageBsuite) {
		this.manageBsuite = manageBsuite;
	}

	public DbNames() {
		// Get the setup profile from current db, if not found display error
		// Get admntool.nsf, if not found display error
		// Get the view module setup profile to get the filenames, if not found
		// display error
		// Set the file names of all dependent databases

		//Database db = Utility.getCurrentDatabase();
		Database db = ExtLibUtil.getCurrentDatabase();
		String admnFileName = "";
		String admnFilePath = "";

		try {

			
			admnFileName = "admntool.nsf";
			admnFilePath = Utility.getBsuitePath(db);
			
			
			prodFolder = admnFilePath;
			if (admnFileName.equals("")) {
				Utility
						.setErrorString(
								"Manage bsuite's filename has to be provided in the setup profile",
								"true");
				return;
			}

			//Database admntool = Utility.getCurrentSession().getDatabase("",admnFilePath + admnFileName);
			Database admntool = ExtLibUtil.getCurrentSession().getDatabase("",admnFilePath +admnFileName);

			if (admntool == null) {
				Utility.setErrorString(
						"Manage bsuite is not found in this path"
								+ admnFilePath + admnFileName, "true");
				return;
			}
			this.manageBsuite = admnFilePath + admnFileName;
			
			profileView = admntool.getView("moduleprofile");

			if (profileView == null) {
				Utility.setErrorString(
						"Modules By Name view is not found in managebsuite"
								+ admnFilePath + admnFileName, "true");
				return;
			}

			setAllFileNames();// Finally set all the filenames

		} catch (Exception e) {

			Utility.setErrorString(
					"Error in instantiating dbnames bean, check for admntool.nsf in "
							+ admnFilePath + admnFileName, "true");
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
		}
	}

	private String getFileName(String dbTitle) {
		Document doc = null;
		String fileName = "";
		try {
			doc = profileView.getFirstDocument();
			while (doc != null) {
				if (doc.getItemValueString("ModuleTitle").equals(dbTitle)) {
					break;
				}
				doc = profileView.getNextDocument(doc);
			}

		} catch (Exception e) {

			Utility.setErrorString("Error in getting module document for"
					+ dbTitle, "true");

			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
		}
		if (doc == null) {
			Utility.setErrorString(
					"Module document is not found for" + dbTitle, "true");
			return "";
		} else {
			try {
				fileName = doc.getItemValueString("ModuleName");
			} catch (Exception e) {

				ErrorHandler erh = new ErrorHandler();
				erh.createErrorDocument(e);
			}
		}
		return prodFolder + fileName;
	}

	/**
	 *This method will be called in the constructor to set all the filenames
	 * when this bean is created
	 * 
	 * @return
	 */
	private boolean setAllFileNames() {

		this.auditTrail = getFileName("Audit Trail");// To add additional
														// dependency pls look
														// into Modules by name
														// view of managebsuite
		this.documents = getFileName("Documents");
		this.employees = getFileName("Employees");
		this.errorCodes = getFileName("Error Codes");
		this.favorites = getFileName("Favorites");
		this.help = getFileName("Help");
		this.inOut = getFileName("In Out");
		this.policies = getFileName("Policies");
		this.profiles = getFileName("Profiles");
		this.reports = getFileName("Reports");
		this.telemarketing = getFileName("Telemarketing");
		this.relation = getFileName("Relation");

		return true;

	}

	public String getDbPath(String dbTitle) {

		if (dbTitle.equalsIgnoreCase("auditTrail")) {
			return this.auditTrail;
		} else if (dbTitle.equalsIgnoreCase("documents")) {
			return this.documents;
		} else if (dbTitle.equalsIgnoreCase("employees")) {
			return this.employees;
		} else if (dbTitle.equalsIgnoreCase("errorCodes")) {
			return this.errorCodes;
		} else if (dbTitle.equalsIgnoreCase("favorites")) {
			return this.favorites;
		} else if (dbTitle.equalsIgnoreCase("help")) {
			return this.help;
		} else if (dbTitle.equalsIgnoreCase("inOut")) {
			return this.inOut;
		} else if (dbTitle.equalsIgnoreCase("policies")) {
			return this.policies;
		} else if (dbTitle.equalsIgnoreCase("profiles")) {
			return this.profiles;
		} else if (dbTitle.equalsIgnoreCase("reports")) {
			return this.reports;
		} else if (dbTitle.equalsIgnoreCase("telemarketing")) {
			return this.telemarketing;
		}else if (dbTitle.equalsIgnoreCase("ManageBSUITE")) {
			return this.manageBsuite;
		}else if(dbTitle.equalsIgnoreCase("Relation")){
			return this.relation;
		}
		

		return null;
	}

	/**
	 *This method will return only the filename part without the folder path
	 *@param dbTitle
	 *@return
	 */
	public String getDbFileName(String dbTitle){
		
		if (dbTitle.equalsIgnoreCase("auditTrail")) {
			return this.auditTrail.split("/")[1];
		} else if (dbTitle.equalsIgnoreCase("documents")) {
			return this.documents.split("/")[1];
		} else if (dbTitle.equalsIgnoreCase("employees")) {
			return this.employees.split("/")[1];
		} else if (dbTitle.equalsIgnoreCase("errorCodes")) {
			return this.errorCodes.split("/")[1];
		} else if (dbTitle.equalsIgnoreCase("favorites")) {
			return this.favorites.split("/")[1];
		} else if (dbTitle.equalsIgnoreCase("help")) {
			return this.help.split("/")[1];
		} else if (dbTitle.equalsIgnoreCase("inOut")) {
			return this.inOut.split("/")[1];
		} else if (dbTitle.equalsIgnoreCase("policies")) {
			return this.policies.split("/")[1];
		} else if (dbTitle.equalsIgnoreCase("profiles")) {
			return this.profiles.split("/")[1];
		} else if (dbTitle.equalsIgnoreCase("reports")) {
			return this.reports.split("/")[1];
		} else if (dbTitle.equalsIgnoreCase("telemarketing")) {
			return this.telemarketing.split("/")[1];
		}else if (dbTitle.equalsIgnoreCase("ManageBSUITE")) {
			return this.manageBsuite.split("/")[1];
		}else if(dbTitle.equalsIgnoreCase("Relation")){
			return this.relation.split("/")[1];
		}
		return null;
	}
}
