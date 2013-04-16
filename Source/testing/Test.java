package testing;

import bsuite.security.Security;


public class Test {
public boolean test(){
	System.out.println("eeb"+System.getProperty("java.io.tmpdir"));
	
	Security se = new Security();
	//return se.isMyDoc();
	return false;
}
}
