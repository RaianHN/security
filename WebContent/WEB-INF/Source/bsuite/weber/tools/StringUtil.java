package bsuite.weber.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public class StringUtil {

	/**
	 * The method compares two strings. It is supported that one of the strings
	 * or both are null.
	 * 
	 * @param str1 string 1
	 * @param str2 string 2
	 * @return <code>true</code>, if the strings are equal
	 */
	public static boolean isEqual(String str1, String str2) {
		if (str1==null) {
			return str2==null;
		}
		else {
			return str1.equals(str2);
		}
	}
	public static String removechar(String s,String c){
		StringTokenizer st = new StringTokenizer(s, c);
		String key=null;
		
		while(st.hasMoreTokens()) {
			key = st.nextToken();
			
		if(!(key.equals(null))){
			s =key;
		}
		}
		 
		return key;
	}
	
	public static String replacechar(String s,char sub,char repl){
		
		s.replace(sub, repl);
		
		return s;
	}
	
	public static String removeFirst(String input)
	{
	    return input.substring(1);
	}
	
	public static String[] mergeStringArrays(String array1[], String array2[]) {  
		 if (array1 == null || array1.length == 0)  
		  return array2;  
		 if (array2 == null || array2.length == 0)  
		  return array1;  
		 List<String> array1List = Arrays.asList(array1);  //a
		 List<String> array2List = Arrays.asList(array2);  //def
		 List<String> result = new ArrayList<String>(array1List);    
		 List<String> tmp = new ArrayList<String>(array1List);  
		 tmp.retainAll(array2List);  
		 result.removeAll(tmp);  
		 result.addAll(0, array2List);    
		 return ((String[]) result.toArray(new String[result.size()]));  
		} 
	
	public static String Propercase(String string){
        String result = "";
        for (int i = 0; i < string.length(); i++){
            String next = string.substring(i, i + 1);
            if (i == 0){
                result += next.toUpperCase();
            } else {
                result += next.toLowerCase();
            }
        }
        return result;
    }
	
}

