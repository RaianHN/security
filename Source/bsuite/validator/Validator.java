package bsuite.validator;

import bsuite.utility.JSFUtil;

import com.ibm.xsp.component.xp.XspInputTextarea;

/**
 *Validator is used in validation before creation of a model object, contains
 * all common validation functions, ex: to check if a document is selected or
 * not in a view OR Single document selection check
 * 
 * @author JPrakash
 *@created Sep 25, 2012
 */

public class Validator {

	/**
	 *This method is used to check if the rich text field has some content or
	 * not.
	 * 
	 * @param compId
	 *            component id of the rich text field
	 *@return true if has some content, false if has no content
	 */
	public boolean validateRichTextField(String compId) {
			String content = null;
			
			XspInputTextarea rtField = (XspInputTextarea) JSFUtil.findComponent(compId);

			content = rtField.getValueAsString();
			content = content.replace("<p dir=ltr>", "");//Remove all paragraph and new line tags and get only the content
			content = content.replace("</p>", "");
			content = content.replace("<p>", "");
			content = content.replace("<br />", "");
			content = content.replace("<br>", "");
			content = content.replace(" ", "");
			content = content.trim();

			if ((content.equals("")) || (content == null)) {
				return false;
			} else {
				return true;
			}

	}
}
