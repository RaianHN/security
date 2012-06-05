package bsuite.weber.action;

import bsuite.weber.action.IPageAction;

/**
 * Base implementation of a page action
 * 
 * @author Karsten Lehmann
 */
public abstract class AbstractPageAction implements IPageAction {

	public Object execute() {
		//redirection of method call if no arguments have been provided
		return execute(null);
	}
}
