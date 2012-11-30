package com.bsuite.model;

import java.util.Map;
import java.util.Vector;

import com.bsuite.security.UserAccess;
import com.bsuite.utility.SessionContext;
import com.bsuite.utility.Utility;
import com.ibm.xsp.extlib.util.ExtLibUtil;

import lotus.domino.Database;
import lotus.domino.DateTime;
import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.NotesException;
import com.bsuite.common.*;
import com.bsuite.err.ErrorHandler;

 /**
  *[Class containing 'bookmark common action' related business logic ]
  *@author VShashikumar
  *@created Oct 12, 2012
 */
public class Bookmark {
	/**
	 *[To set the required scope variables for processing bookmar related requests]
	 */
	@SuppressWarnings("unchecked")
	public static void initialize()
	{

		try
		{
			Map viewScope = SessionContext.getViewScope();

			if (UserAccess.hasModuleRole("[Maintenance]", "favorite.nsf") == true
					|| UserAccess.hasModuleRole("[SuperUser]", "favorite.nsf") == true
					|| UserAccess.hasModuleRole("[BranchHead]", "favorite.nsf") == true
					|| UserAccess.hasModuleRole("[Authors]", "favorite.nsf") == true
					|| UserAccess.hasModuleRole("[Telemarketer]",
							"favorite.nsf") == true)
			{
				viewScope.put("roleCheck", "1");
			}
			else
			{
				viewScope.put("roleCheck", "0");
			}

			String bookmarkType = (String) viewScope.get("pp3choice");
			if (bookmarkType.equals("intranet"))
			{
				Document pdoc = SessionContext.getDocumentToProcess();
				viewScope
						.put("bookmarktitle", pdoc.getItemValue("bsuiteTitle"));

			} 
			else
			{
				viewScope.put("bookmarktitle", "");
			}
			viewScope.put("groups", "");
			viewScope.put("type", "0");
			viewScope.put("folder", "");
			viewScope.put("subject", "");
			viewScope.put("bookmarkurl", "");
			viewScope.put("editfavdocid", "");

		} 
		catch (NotesException e)
		{
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);		} 
		catch (Exception e)
		{
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);		}
	}

	/**
	 *[Removes the selected bookmark]
	 */
	@SuppressWarnings("unchecked")
	public static void removeBookmark() {
		String[] ids = (String[]) SessionContext.getViewScope().get("bmIds");
		Map viewScope = SessionContext.getViewScope();
		try {
			String err = "";
			String roleCheck;
			if (UserAccess.hasModuleRole("[Maintenance]", "favorite.nsf") == true
					|| UserAccess.hasModuleRole("[SuperUser]", "favorite.nsf") == true
					|| UserAccess.hasModuleRole("[BranchHead]", "favorite.nsf") == true) {
				roleCheck = "1";
			} else {
				roleCheck = "0";
			}
			for (String id : ids) {
				Document favorite = Utility.getDatabase("favorite.nsf").getDocumentByID(id);
				String author = favorite.getItemValueString("AuthorEmployees");
				String category = favorite.getItemValueString("Categories");
				String type = favorite.getItemValueString("Type");
				
				if (!(author.equals(ExtLibUtil.getCurrentSession().getEffectiveUserName()))) {
					if (roleCheck.equals("0")) {
						err = err + "You cannot delete bookmark titled "
								+ favorite.getItemValueString("Title") + ". ";
						viewScope.put("errsms", err);
						viewScope.put("errval", "true");
						viewScope.put("errmsg1", err);
						return;
					} else if (roleCheck.equals("1")) {
						favorite.replaceItemValue("bsuitestatus", "3");
						favorite.save(true, false);
						if (category.equals("1") && type.equals("2")) {
							createRequestForDocuments("88", "1", favorite
									.getItemValueString("WebsiteURL"), "", 1,
									favorite.getItemValue("Groups"), favorite
											.getItemValueString("ModuleName"),
									favorite.getItemValueString("ModuleTitle"));
							viewScope
									.put(
											"errsms",
											"A BSUITE-P request #88 has been generated. Once it is completed this favorite is available for the group");
							viewScope.put("errval", "true");

						}

					}

				} else {
					favorite.replaceItemValue("bsuitestatus", "3");
					favorite.save(true, false);
					if (roleCheck.equals("1") && type.equals("2")
							&& category.equals("1")) {
						createRequestForDocuments("88", "1", favorite
								.getItemValueString("WebsiteURL"), "", 1,
								favorite.getItemValue("Groups"), favorite
										.getItemValueString("ModuleName"),
								favorite.getItemValueString("ModuleTitle"));
						viewScope
								.put(
										"errsms",
										"A BSUITE-P request #88 has been generated. Once it is completed this favorite is available for the group");
						viewScope.put("errval", "true");

					}
				}

			}

		} catch (NotesException e) {
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);			
		} catch (Exception e) {
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);			
		}

	}
	/**
	*[Creates request document in manage bsuite]
	
	 *@param requestType, String [ determines the action to be taken ] - This is the number entered in string format that identifies the BSUITE-P request number. This parameter value will be used by 'processBsuitePrequests' subroutine in 'BsuitePLibrary' Script Library to trigger the respective action.
	 *@param selectionType,As String [ 0 - selected document only, 1 - dynamically selected documents ]
	 *@param selectedID As Variant [ UNID of the document/s selected ] - You may also pass a single UNID as string. If no documents are selected then pass the empty "" string
	 *@param searchFormula [ formula can be passed as  the document selection criteria ] - It is a SQL query that will be passed to it. If no query is required then just pass the empty "" string
	 *@param eventType As Integer [ False - immediate request , True - scheduled request] - Immediate requests get processed immediately in a span of 5-10 mins. Scheduled requests get processed ONLY ONCE IN A DAY at 9:00PM.
	 *@param varNewOption As Variant [ any additonal information ] - This parameter can be used effectively to pass any additional information that this BSUITE-P request requires to get a job done. For example, in a BSUITE-P request to delete a user, to varNewOption we can pass the new subsitute that might be required in the place of deleted user. If there is no additional information to be passed then just pass the empty "" string.
	
	 *@param filename, module name
	 *@param title, module title
	
	 */
	private static void createRequestForDocuments( String requestType, String selectionType, String selectedID, String searchFormula,	int eventType, Vector varNewOption,String filename,String title){
		try{
			Document request;
			String strDays=null;
			String currentUser = ExtLibUtil.getCurrentSession().getEffectiveUserName();
			
			Database  mbdb; //bsuite-p database
			mbdb = Utility.getDatabase("admntool.nsf");
			if(mbdb.isOpen())
			{
				request = mbdb.createDocument();
				request.replaceItemValue("form","requests");
				request.replaceItemValue("ModuleName", filename); 
				request.replaceItemValue("ModuleTitle", title);
				request.replaceItemValue("RequestType",requestType.substring(0,2));
				request.replaceItemValue("selectionType", selectionType);		
				request.replaceItemValue("SelectedDocID", selectedID);
				request.replaceItemValue("SearchFormula", searchFormula);
				if(eventType==0)
				{
					request.replaceItemValue("eventType", 0);

				}
				else
				{
					request.replaceItemValue("eventType", 1);
				}
				strDays=Utility.getBsuiteProfile(ExtLibUtil.getCurrentDatabase()).getItemValueString("ExpiryDays");
				if(strDays.equals(""))  {
					strDays="90";

					if(!(strDays.equals("0") ))// 'if set to 0, there is no  Expiry Date
					{
						int intDays;
						intDays = Integer.parseInt(strDays);
						DateTime dt1 = ExtLibUtil.getCurrentSession().createDateTime("Today");
						dt1.adjustDay(intDays);
						request.replaceItemValue("ExpiryDate", dt1.getDateOnly());

					}
					request.replaceItemValue("bsuiteStatus","0");
					request.replaceItemValue("NewOption",varNewOption);
					// HIDDEN FIELDS
					request.replaceItemValue("AuthorEmployees",currentUser);
					request.replaceItemValue("DocumentCreator",currentUser);
					request.computeWithForm(true, true);
					request.replaceItemValue("BsuiteTitle", request.getItemValueString("Request"));// 'Request field is computed

					Item item = request.getFirstItem("AuthorEmployees");
					item.setAuthors(true);
					request.replaceItemValue("AuthorEmployees",currentUser);

					request.save(true, false);
				}

			}

		}
		catch(Exception e){
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);		}
	}

	/**
	 *[To open the selected bookmark]
	 */
	@SuppressWarnings("unchecked")
	public static void viewBookmark() {
		Map viewScope = SessionContext.getViewScope();
		Database favDb = Utility.getDatabase("favorite.nsf");
		String[] ids = (String[]) SessionContext.getViewScope().get("bmIds");
		try {
			
			Document currentdoc = favDb.getDocumentByID(ids[0]);
			String category = currentdoc.getItemValueString("Categories");
			String favURL = currentdoc.getItemValueString("websiteURL");
			String link = "";
			String url = "";
			viewScope.put("noteid", currentdoc.getNoteID());
			

			if (category.equals("1")) { // intranet doc.....
				String module = currentdoc.getItemValueString("ModuleName");
				viewScope.put("docID", favURL);// used to take unid of the
												// document to open.
				viewScope.put("pp3choice", "Intranet");
			
				url = ExtLibUtil.getCurrentSession().getHttpURL();

				url = url.substring(0, url.length() - 11);
				link = url + "/" + Utility.getBsuitePath(favDb) + module + "/0/" + favURL;

			

			} else if (category.equals("0")) {
				link = favURL;
			}

			viewScope.put("bookmarkURL", link);
			viewScope.put("Category", category);// added by ranjeet to create
												// tab for intranet.

		} catch (NotesException e) {

			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);			
		} catch (Exception e) {

			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);		}

	}

	/**
	 *[Edits the selected bookmark]
	 */
	@SuppressWarnings("unchecked")
	public static void editBookmark() {
		Map viewScope = SessionContext.getViewScope();
		Database favDb = Utility.getDatabase("favorite.nsf");
		String[] ids = (String[]) SessionContext.getViewScope().get("bmIds");
		try {
			Document currentdoc = favDb.getDocumentByID(ids[0]);
			String err = "";
			String roleCheck;
			if (UserAccess.hasModuleRole("[Maintenance]", "favorite.nsf") == true
					|| UserAccess.hasModuleRole("[SuperUser]", "favorite.nsf") == true
					|| UserAccess.hasModuleRole("[BranchHead]", "favorite.nsf") == true) {
				roleCheck = "1";
			} else {
				roleCheck = "0";
			}

			String author = currentdoc.getItemValueString("AuthorEmployees");

			if (!(author.equals(ExtLibUtil.getCurrentSession()
					.getEffectiveUserName()))) {
				if (roleCheck.equals("0")) {
					err = err + "You cannot edit bookmark titled "
							+ currentdoc.getItemValueString("Title") + ". ";
					viewScope.put("errsms", err);
					viewScope.put("errval", "true");
					return;
				}
			}

			if (UserAccess.hasModuleRole("[Maintenance]", "favorite.nsf") == true
					|| UserAccess.hasModuleRole("[SuperUser]", "favorite.nsf") == true
					|| UserAccess.hasModuleRole("[BranchHead]", "favorite.nsf") == true
					|| UserAccess.hasModuleRole("[Telemarketer]",
							"favorite.nsf") == true) {
				viewScope.put("roleCheck", "1");
			} else {
				viewScope.put("roleCheck", "0");
			}
			viewScope.put("bookmarktitle", currentdoc
					.getItemValueString("Title"));
			viewScope.put("folder", currentdoc
					.getItemValueString("BookmarkFolder"));
			viewScope.put("subject", currentdoc.getItemValueString("Subject"));
			viewScope.put("type", currentdoc.getItemValueString("Type"));
			viewScope.put("groups", currentdoc.getItemValue("Groups"));

			String url = currentdoc.getItemValueString("WebsiteURL");
			url = url.substring(0, 7);
			if (url.equals("http://")) {
				viewScope.put("pp3choice", "webpage");
				viewScope.put("bookmarkurl", currentdoc
						.getItemValueString("WebsiteURL"));
			} else {
				viewScope.put("pp3choice", "intranet");
			}
			viewScope.put("editfavdocid", currentdoc.getNoteID());

		} catch (NotesException e) {
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);			
		} catch (Exception e) {
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);			
		}

	}

	/**
	 *[To update the selected bookmark]
	 */
	public static void updateBookmark() {
		Map viewScope = SessionContext.getViewScope();
		Database favDb = Utility.getDatabase("favorite.nsf");
		String id = (String) viewScope.get("editfavdocid");

		String folder = (String) viewScope.get("folder");
		String title = getTitle();
		String subject = (String) viewScope.get("subject");
		Vector Groups= (Vector) viewScope.get("groups");
		String roleCheck = (String) viewScope.get("roleCheck");
		try {
			Document currentdoc = favDb.getDocumentByID(id);

			String strCategory;
			String cat = (String) viewScope.get("pp3choice");
			if (cat.equals("webpage")) {
				strCategory = "0";
			} else {
				strCategory = "1";
			}

			saveFavorite(currentdoc, strCategory);

			if (strCategory.equals("1") && roleCheck.equals("1")) {
				createRequestForFavorites("80", "1", currentdoc
						.getUniversalID(), "", 1, Groups, "favorite.nsf",
						"Favorites");
				createRequestForDocuments("99", "1", currentdoc
						.getItemValueString("WebsiteURL"), "", 1, currentdoc
						.getItemValue("Groups"), currentdoc
						.getItemValueString("ModuleName"), currentdoc
						.getItemValueString("ModuleTitle"));
				viewScope
						.put(
								"errsms",
								"A BSUITE-P request #99 has been generated. Once it is completed this favorite is available for the group");
				viewScope.put("errval", "true");

			}

		} catch (NotesException e) {
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);			
		} catch (Exception e) {
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);			
		}

	}
	
	/**
	 *[to get the bookmark title from the view scope]
	 *@return title as string
	 */
	private static String getTitle() {
		String st;
		Map viewScope = SessionContext.getViewScope();
		Vector c1 = new Vector();
		Object s = viewScope.get("bookmarktitle");// sendto;
		String c2 = s.getClass().getName();
		System.out.print(c2);
		if (c2 == "java.lang.String") {
			st = (String) s;
		} else {
			c1 = (Vector) s;
			st = c1.toString();
		}
		return st;
	}
	
	/**
	 *[Saves the favorite document when a  bookmark is saved]
	 *@param favorite
	 *@param strCategory
	 */
	private static void saveFavorite(Document favorite, String strCategory)
	{
		try
		{
			Map viewScope = SessionContext.getViewScope();
			String Type = (String) viewScope.get("type");
			String folder = (String) viewScope.get("folder");
			String subject = (String) viewScope.get("subject");

			Vector Groups = null;
			String title = getTitle();

			if (Type.equals("0"))
			{
				Groups = null;
				favorite.replaceItemValue("Status", "1"); // 'Make this field
															// set to "1" so
															// that it is
															// available in user
															// views
			} 
			else
			{
				Groups = (Vector) viewScope.get("groups");

			}
			String roleCheck = (String) viewScope.get("roleCheck");

			if (roleCheck.equals("1") && Type.equals("1"))
			{ // 'NO READERS SHOULD BE ENFORCED
				favorite.replaceItemValue("ReaderRole", ""); // Make this field
																// NULL so that
																// anyone can
																// read this
				favorite.replaceItemValue("ReaderGroups", ""); // Make this
																// field NULL so
																// that anyone
																// can read this
			}

			if (roleCheck.equals("1") && strCategory.equals("0")
					&& Type.equals("2"))
			{// ' READERS SHOULD BE ENFORCED in favorite document in favorite
				// databse
				favorite.replaceItemValue("ReaderRole", "[Readers]");
				favorite.replaceItemValue("ReaderGroups", ""); // Make this
																// field NULL
				favorite.replaceItemValue("Status", "1"); // 'Make this field
															// set to "1" so
															// that it is
															// available in user
															// views

				for (Object x : Groups)
				{
					Vector item = (Vector) favorite
							.getFirstItem("ReaderGroups");
					if (!(item.contains(x)))
					{
						item.addElement(x);
					}
				}

			}

			favorite.replaceItemValue("Title", title);
			favorite.replaceItemValue("BsuiteTitle", title);

			if (folder == "" || folder.isEmpty() || folder.equals(" "))
			{
				folder = "BSUITE";
			}
			favorite.replaceItemValue("BookmarkFolder", folder);
			favorite.replaceItemValue("Subject", subject);
			favorite.replaceItemValue("Type", Type);
			favorite.replaceItemValue("Groups", Groups);
			favorite.replaceItemValue("NewFolder", folder);
			if (strCategory.equals("1"))
			{

				String edit = (String) viewScope.get("editfavdocid");
				if (edit.equals(""))
				{
					// TODO what is entity
					favorite.replaceItemValue("WebsiteURL", SessionContext.getDocumentToProcess().getUniversalID());
				}

			}
			else
			{
				String URL = (String) viewScope.get("bookmarkurl");
				String prefix = URL.substring(0, 7);
				if (!(prefix.equals("http://")))
				{
					URL = "http://" + URL;

				}
				favorite.replaceItemValue("WebsiteURL", URL);
			}

			Utility.setLastModified(favorite);
			favorite.save(true, false);
			System.out.print("inside save favorite");
		}
		catch (NotesException e)
		{

			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);		} 
		catch (Exception e)
		{

			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);		}

	}
	
	/**
	 *[Creates request document in manage bsuite]
	 *@param requestType, String [ determines the action to be taken ] - This is the number entered in string format that identifies the BSUITE-P request number. This parameter value will be used by 'processBsuitePrequests' subroutine in 'BsuitePLibrary' Script Library to trigger the respective action.
	 *@param selectionType,As String [ 0 - selected document only, 1 - dynamically selected documents ]
	 *@param selectedID As Variant [ UNID of the document/s selected ] - You may also pass a single UNID as string. If no documents are selected then pass the empty "" string
	 *@param searchFormula [ formula can be passed as  the document selection criteria ] - It is a SQL query that will be passed to it. If no query is required then just pass the empty "" string
	 *@param eventType As Integer [ False - immediate request , True - scheduled request] - Immediate requests get processed immediately in a span of 5-10 mins. Scheduled requests get processed ONLY ONCE IN A DAY at 9:00PM.
	 *@param varNewOption As Variant [ any additonal information ] - This parameter can be used effectively to pass any additional information that this BSUITE-P request requires to get a job done. For example, in a BSUITE-P request to delete a user, to varNewOption we can pass the new subsitute that might be required in the place of deleted user. If there is no additional information to be passed then just pass the empty "" string.
	
	 *@param filename, module name
	 *@param title, module title
	 */
	public static void createRequestForFavorites(String requestType,String selectionType,String selectedID,String searchFormula,int eventType, Vector varNewOption,String filename,String title){

		try
		{
			Document request;
			String strDays=null;
			String currentuser = ExtLibUtil.getCurrentSession().getEffectiveUserName();
			Database  mbdb; //bsuite-p database
			mbdb = Utility.getDatabase("admntool.nsf");
			if(mbdb.isOpen())
			{
				request = mbdb.createDocument();
				request.replaceItemValue("form","requests");
				request.replaceItemValue("ModuleName", filename); 
				request.replaceItemValue("ModuleTitle", title);
				request.replaceItemValue("RequestType",requestType.substring(0,2));
				request.replaceItemValue("selectionType", selectionType);		
				request.replaceItemValue("SelectedDocID", selectedID);
				request.replaceItemValue("SearchFormula", searchFormula);

				if(eventType==0)
				{
					request.replaceItemValue("EventType", eventType);

				}
				else
				{
					request.replaceItemValue("EventType", eventType);
				}


				strDays=Utility.getBsuiteProfile(ExtLibUtil.getCurrentDatabase()).getItemValueString("ExpiryDays");;
				if(strDays.equals(""))
				{
					strDays="90";

					if(!(strDays.equals("0") ))// 'if set to 0, there is no  Expiry Date
					{
						int intDays;
						intDays = Integer.parseInt(strDays);
						DateTime dt1 = ExtLibUtil.getCurrentSession().createDateTime("Today");
						dt1.adjustDay(intDays);
						request.replaceItemValue("ExpiryDate", dt1.getDateOnly());

					}
					request.replaceItemValue("bsuiteStatus","0");
					request.replaceItemValue("NewOption",varNewOption);
					// HIDDEN FIELDS
					request.replaceItemValue("AuthorEmployees",currentuser);
					request.replaceItemValue("DocumentCreator",currentuser);
					request.computeWithForm(true, true);
					request.replaceItemValue("BsuiteTitle", request.getItemValueString("Request"));// 'Request field is computed

					Item item = request.getFirstItem("AuthorEmployees");
					item.setAuthors(true);
					request.replaceItemValue("AuthorEmployees",currentuser);

					request.save(true, false);
				}

			}

		}


		catch(Exception e)
		{
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);		}
	}

	
	
	public static void createBookmark()
	{
		
		Map viewScope = SessionContext.getViewScope();
		String bookmarkType = (String) viewScope.get("pp3choice");
		try
		{
			if (bookmarkType.equals("intranet"))
			{
				Document currentDoc = SessionContext.getDocumentToProcess();
				createIntranet(currentDoc); //  INTRANET
			} 
			else
			{
				createWebpage(); //  INTERNET
			}
		} 
		catch (Exception e)
		{
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);			
		}

	}
	
	
	
	

	private static void createWebpage()
	{

		try
		{

			String strCategory = "0";
			Database currentdb = ExtLibUtil.getCurrentDatabase();
			String currentUser = ExtLibUtil.getCurrentSession().getEffectiveUserName();
			Map viewScope = SessionContext.getViewScope();
			Database favdb = Utility.getDatabase("favorite.nsf");
			Document favorite = favdb.createDocument();

			favorite.replaceItemValue("form", "bookmark");
			favorite.replaceItemValue("obid", "bookmark");
			favorite.replaceItemValue("$ConflictAction", "1");
			favorite.replaceItemValue("Categories", strCategory);
			favorite.replaceItemValue("ModuleName", currentdb.getFileName());
			favorite.replaceItemValue("ModuleTitle", currentdb.getTitle());
			favorite.replaceItemValue("AuthorEmployees", currentUser);
			favorite.computeWithForm(true, true);
			Item autitem = favorite.getFirstItem("AuthorEmployees");
			autitem.setAuthors(true);
			favorite.replaceItemValue("AuthorEmployees", currentUser);

			saveFavorite(favorite, strCategory);

			
			
		} 
		catch (NotesException e)
		{

			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
		} 
		catch (Exception e)
		{

			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
			}

	}

	
	

	
	

	private static void createIntranet(Document currentDoc)
	{
		
		
		try 
		{
			
			
			String strCategory = "1";
			Database currentdb = ExtLibUtil.getCurrentDatabase();
			String currentUser = ExtLibUtil.getCurrentSession().getEffectiveUserName();
			Map viewScope = SessionContext.getViewScope();
			String roleCheck = (String) viewScope.get("roleCheck");
			Vector Groups = new Vector();
			Groups.add(viewScope.get("groups"));
				
			String Type = (String) viewScope.get("type");
			Database favdb = Utility.getDatabase("favorite.nsf");
			Document favorite = favdb.createDocument();
			
			favorite.replaceItemValue("form", "bookmark");
			favorite.replaceItemValue("obid", "bookmark");
			favorite.replaceItemValue("$ConflictAction", "1");
			favorite.replaceItemValue("Categories", strCategory); 
			favorite.replaceItemValue("ModuleName", currentdb.getFileName());
			favorite.replaceItemValue("ModuleTitle", currentdb.getTitle());
			favorite.replaceItemValue("AuthorEmployees", currentUser);
			favorite.computeWithForm(true,true);
			Item autitem = favorite.getFirstItem("AuthorEmployees");
			autitem.setAuthors(true);
			favorite.replaceItemValue("AuthorEmployees",currentUser);
			
			System.out.print("inside createintranet before save");
			saveFavorite(favorite,strCategory);
			System.out.print("inside createintranet after save");
			
			if (strCategory.equals("1")&& roleCheck.equals("1")&& Type.equals("2"))
			{ 
				Vector docunid=new Vector();
				docunid.addElement(currentDoc.getUniversalID());
				createRequestForFavorites( "80", "1", favorite.getUniversalID(), "", 1, Groups,"favorite.nsf","Favorites");

				BsuiteRequest.createRequest( "99", "1", docunid, "", 1, Groups) ;
				viewScope.put("errsms", "A BSUITE-P request #99 has been generated. Once it is completed this favorite is available for the group");
				viewScope.put("errval", "true");


			}
		} 
		catch (NotesException e) 
		{

			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);			
		}
		catch (Exception e) {

			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);			
		}
		
		
		
	}
}
