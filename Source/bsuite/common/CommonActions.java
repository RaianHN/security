package bsuite.common;

import java.util.Vector;
import java.util.Map;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.RichTextItem;
import bsuite.err.ErrorHandler;
import bsuite.model.*;
import bsuite.utility.*;

import bsuite.utility.Utility;

/**
 *[Thic class contains all the bsuite related common actions]
 * 
 * @author VShashikumar
 *@created Oct 5, 2012
 */
public class CommonActions {

	/**
	 *[used to create an activity document on request which will be a response
	 * to the parent document]
	 * 
	 * @param strType
	 *            Type of activity
	 *@param strSubject
	 *            Subject of activity
	 *@param bodyMessage
	 *            contents in the activity
	 *@param currentdoc
	 *            the parent document
	 */
	public static void createResponse(String strType, String strSubject,
			String bodyMessage, Document currentdoc) {

		try {

			Database db = Utility.getCurrentDatabase();
			Document doc;
			doc = db.createDocument();
			doc.replaceItemValue("Form", "discussion");
			doc.replaceItemValue("DiscussionType", strType);
			doc.replaceItemValue("Subject", strSubject);
			doc.replaceItemValue("FullName", currentdoc
					.getItemValue("DocumentCreator"));
			RichTextItem rti = doc.createRichTextItem("Body");
			rti.addNewLine(2);
			rti.appendText(bodyMessage);
			doc.computeWithForm(true, true);
			doc.makeResponse(currentdoc);
			doc.save();

		} catch (NotesException e) {

			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
		} catch (Exception e) {

			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
		}

	}

	/**
	 *[This is used to notify the selected user(s) after creating a discussion
	 * document (Activity)]
	 * 
	 * @param currentdoc
	 *            , handle to discussion document
	 */
	public static void createDiscussion(Document currentdoc) {
		try {

			String strMessage = "Please click here to open the document";
			Object strTo[] = currentdoc.getItemValue("sendto").toArray();
			Vector v = new Vector();
			String strSender = Utility.getCurrentSession()
					.getEffectiveUserName();
			for (int i = 0; i < strTo.length; i++) {
				v.add(strTo[i]);
			}

			String strSubject = currentdoc.getItemValueString("subject");
			Mail.sendMailWithUNID(strSubject, strMessage, v, strSender,
					currentdoc, false);

		} catch (NotesException e) {

			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
		} catch (Exception e) {

			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
		}

	}

	/**
	 *[This method is used to send the reference of the selected document
	 * through mail]
	 */
	public static void sendReferenceRequest() {

		try {

			Document currentDoc = SessionContext.getDocumentToProcess();

			Map viewScope = SessionContext.getViewScope();

			String subject = viewScope.get("sub1").toString();
			String message = viewScope.get("com1").toString();

			Vector to = new Vector();
			Object s = viewScope.get("sendto");
			String c2 = s.getClass().getName();

			if (c2 == "java.lang.String") {
				to.addElement(s);
			} else {
				to = (Vector) s;
			}

			String sender = Utility.getCurrentSession()
					.getEffectiveUserName();
			Mail.sendMailWithUNID(subject, message, to, sender, currentDoc,
					false);

		} catch (NotesException e) {

			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
		} catch (Exception e) {

			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
		}

	}

	/**
	 *[This method is used to associate a mail document to the selected
	 * document ]
	 */
	public static void associationRequest() {

		Document currentDoc = SessionContext.getDocumentToProcess();
		Document mailDoc = getMailDocumentToLink();

		if (verifyMailDoc(mailDoc)) {
			linkMail(currentDoc, mailDoc);
		}

	}

	/**
	 *[Returns the handle to the selected mail document]
	 * 
	 * @return selected Mail document
	 */
	private static Document getMailDocumentToLink() {

		Object mid = SessionContext.getViewScope().get("maildocid");
		String[] arr = (java.lang.String[]) mid;
		Document ret = null;

		try {

			ret = Mail.getCurrentUserMailDB().getDocumentByID(arr[0]);
		} catch (NotesException e) {

			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
		} catch (Exception e) {

			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
		}

		return ret;
	}

	/**
	 *[Checks whether the selected document is a mail or calendar entry]
	 *@param mail, selected document
	 *@return true if its a mail document
	 */
	private static boolean verifyMailDoc(Document mail) {

		Boolean ret = false;
		String strForm = "";
		try {
			if (mail.hasItem("form")) {
				strForm = mail.getItemValueString("form");
			} else if (mail.hasItem("Form")) {
				strForm = mail.getItemValueString("Form");
			}

			if (strForm.equals("Memo") || strForm.equals("Reply")
					|| strForm.equals("NonDelivery Report")
					|| strForm.equals("")) {
				ret = true;
			} else {

				SessionContext
						.getViewScope()
						.put("errmsg",
								"Please select the Mail to be Linked.   Please do not choose Calendar Entries");
				ret = false;
			}
		}

		catch (NotesException e) {

			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
		} catch (Exception e) {

			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
		}

		return ret;

	}

	/**
	 *[Creates a document by copying all the contents of the mail document and makes it as a response to the selected document]
	 *@param currentDoc selected document
	 *@param mailDoc selected mail document
	 */
	private static void linkMail(Document currentDoc, Document mailDoc) {

		try {

			Database db = Utility.getCurrentDatabase();
			Document linkdoc = db.createDocument();
			linkdoc.replaceItemValue("obid", "linkedmail");
			mailDoc.copyAllItems(linkdoc, true);
			linkdoc.replaceItemValue("Form", "linkedmail");

			if (currentDoc.hasItem("FullName")) {
				linkdoc.replaceItemValue("FullName", currentDoc
						.getItemValueString("FullName"));
			}

			linkdoc.replaceItemValue("bsuiteStatus", "0"); // mark it inactive
															// so that it cannot
															// be edited
			linkdoc.replaceItemValue("ByOption ", "1");// Linked Mail, 0 for
														// sent mails
			linkdoc.replaceItemValue("ModuleName", db.getFileName());
			linkdoc.replaceItemValue("bstSync", "0");
			linkdoc.computeWithForm(true, true);
			linkdoc.makeResponse(currentDoc);
			linkdoc.save();
			linkdoc.recycle();

		}

		catch (NotesException e) {
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);		}

		catch (Exception e1) {
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e1);		}

	}

	/**
	 *[Creates a P-request to delete the selected document]
	 */
	public static void deleteRequest() {

		Vector docUNIDs = new Vector();
		String requestType = "39";

		docUNIDs = getUNIDSAndMarkDelete();
		BsuiteRequest.createRequest(requestType, "1", docUNIDs, "", 0, null);
		SessionContext.getViewScope().put("errsms",
				"A BSUITE-P request #" + requestType + " has been generated.");
		SessionContext.getViewScope().put("errval", "true");

		// TODO need to create audit doc

	}

	/**
	 *[Returns the unids of all the selected documents for deletion]
	 *@return vector containing unids
	 */
	private static Vector getUNIDSAndMarkDelete() {

		String unid = "";
		Vector unids = new Vector();
		try {
			Object ids = SessionContext.getViewScope().get("seldocids");
			String[] arr = (java.lang.String[]) ids;
			for (String id : arr) {
				Document selecteddoc = Utility.getCurrentDatabase()
						.getDocumentByID(id);
				selecteddoc.replaceItemValue("MarkDeletion", "1");
				selecteddoc.save();
				unid = selecteddoc.getUniversalID();
				unids.addElement(unid);
			}

		}

		catch (NotesException e) {
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);		}

		catch (Exception e1) {
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e1);		}

		return unids;
	}

	/**
	 *[Creates a p-request to archive the selected documents]
	 */
	public static void archiveRequest() {

		try {

			Vector docUNIDs = new Vector();
			String requestType = "38";

			if (!(checkModuleForArchive())) {

				return;
			}

			docUNIDs = getUNIDSAndMarkDelete();
			BsuiteRequest
					.createRequest(requestType, "1", docUNIDs, "", 0, null);
			SessionContext.getViewScope().put(
					"errsms",
					"A BSUITE-P request #" + requestType
							+ " has been generated.");
			SessionContext.getViewScope().put("errval", "true");

			// TODO need to create audit doc

		}

		catch (Exception e1) {
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e1);		}

	}

	/**
	 *[Checks if module is available for archiving]
	 *@return true if module is available for archiving
	 */
	private static boolean checkModuleForArchive() {
		boolean checkarchive = true;
		Document module = null;

		try {

			Database mbdb = Utility.getDatabase("admntool.nsf");
			module = Utility.getModuleProfile(mbdb, Utility
					.getCurrentDatabase().getFileName());

			if (module == null) {
				checkarchive = false;
				SessionContext
						.getViewScope()
						.put(
								"errsms",
								"There is no Module Profile available for this Module.  Please contact system Administrator.");
				SessionContext.getViewScope().put("errval", "true");

			}

			else if (module.getItemValueString("ModuleArchive") != "1") {
				checkarchive = false;
				SessionContext
						.getViewScope()
						.put(
								"errsms",
								"This Module is not configured to Archive Documents.  Please contact system Administrator.");
				SessionContext.getViewScope().put("errval", "true");

			}

		} catch (NotesException e) {
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);		}

		catch (Exception e1) {
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e1);		}

		return checkarchive;
	}

	

	/**
	 *[Request to initialize bookmark]
	 */
	public static void initBookmarkRequest() {
		Bookmark.initialize();

	}

	/**
	 *[Request to remove bookmark]
	 */
	public static void removeBookmarkRequest() {
		Bookmark.removeBookmark();
	}

	/**
	 *[Request to view bookmark]
	 */
	public static void viewBookmarkRequest() {
		Bookmark.viewBookmark();

	}

	/**
	 *[Request to edit bookmark]
	 */
	public static void editBookmarkRequest() {
		Bookmark.editBookmark();
	}

	/**
	 *[Request to create bookmark]
	 */
	public static void createBookmarkRequest() {
		Bookmark.createBookmark();

	}

	/**
	 *[Request to update bookmark]
	 */
	public static void updateBookmarkRequest() {
		Bookmark.updateBookmark();
	}

	/**
	 *[Request to get contextual help]
	 */
	public static void helpRequest() {

		Help.getContextHelpForView();

	}

	public static void generateURLRequest() {
		
		Extranet.generateURL();
		
	}

	public static void initGenerateURLRequest() {
		System.out.print("inside initGenerateURLRequest");
		Extranet.initGenerateURL();
		
	}

}
