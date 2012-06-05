package bsuite.weber.action;
/**
 * Interface of an action that is called from an XPage
 * 
 * @author 
 */
public interface IPageAction {
	/**
	 * Implement this method with the code that should be executed when
	 * calling the action via #{javascript:actions["actionName"].execute(args)}
	 * 
	 * @param args argument list
	 * @return optional result
	 */
	public Object execute(String arg);

	/**
	 * Second execute method without the need to pass any arguments
	 * 
	 * @return optional result
	 */
	public Object execute();
}
