package com.bsuite.controller; 
import com.bsuite.model.Policy;
import lotus.domino.*;

 /**
  *[PolicyContoller is application specific controller where all module specific workflow actions gets processed, this class will be the entry point for policy's workflow actions]
  *@author VShashikumar
  *@created Oct 5, 2012
 */
public class PolicyController extends Controller
{
	
	
	private Policy policy;
	
	
	
	public PolicyController()
	{
		
		
		
	}
	
	
	/**
	 * @param doc initialize policy document
	 */
	public PolicyController(Document doc)
	{
		
		policy = new Policy(doc);
		
	}
	
	
	
	/**
	 *[Based on the request number appropriate methods are called from Policy model class]
	 *@param action the type of request, ex:1 for SaveAsDraft
	 */
	public void runPolicy(int action)
	{
		
		switch (action) {
			case 1:policy.saveAsDraft();
					break;
			case 2:policy.sendForReview();
					break;
			case 3:policy.approve();
					break;
			case 4:policy.deny();
					break;
			case 5:policy.publish();
					break;
			case 6:policy.signPolicy();
		
			
		}
		
		
	}

}
