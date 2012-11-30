package com.bsuite.controller;

import lotus.domino.*;
import com.bsuite.common.*;

 /**
  *[Controller class for all the common actions, controls the request for all the common actions]
  *@author VShashikumar
  *@created Oct 12, 2012
 */
public class CommonActionController extends Controller {

	private Document currentDoc;

	public CommonActionController() {

		currentDoc = null;

	}

	public CommonActionController(Document doc) {

		currentDoc = doc;

	}

	/**
	 *[Processes the request based on the action by invoking appropriate methods of model classes]
	 *@param action, request number
	 */
	public void runAction(int action) {

		switch (action) {

		case 1:
			CommonActions.createDiscussion(currentDoc);
			break;
		case 2:
			CommonActions.sendReferenceRequest();
			break;
		case 3:
			CommonActions.associationRequest();
			break;
		case 4:
			CommonActions.deleteRequest();
			break;
		case 5:
			CommonActions.archiveRequest();
			break;
		case 6:
			CommonActions.initGenerateURLRequest();
			break;
		case 7:
			CommonActions.generateURLRequest();
			break;
		case 8:
			CommonActions.initBookmarkRequest();
			break;
		case 9:
			CommonActions.removeBookmarkRequest();
			break;
		case 10:
			CommonActions.viewBookmarkRequest();
			break;
		case 11:
			CommonActions.editBookmarkRequest();
			break;
		case 12:
			CommonActions.updateBookmarkRequest();
			break;
		case 13:
			CommonActions.createBookmarkRequest();
			break;
		case 14:
			CommonActions.helpRequest();
			break;
		}

	}

}
