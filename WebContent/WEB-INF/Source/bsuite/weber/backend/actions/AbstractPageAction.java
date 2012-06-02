package bsuite.weber.backend.actions;

import bsuite.weber.backend.IPageAction;

public abstract class AbstractPageAction implements IPageAction  {

	public Object execute() {
		//redirection of method call if no arguments have been provided
		return execute(null);
	}
}
