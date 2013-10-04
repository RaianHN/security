package bsuite.loadcc;


import java.util.Comparator;

@SuppressWarnings("unchecked")
public class Compare implements Comparator 
{
	
	

		
		
		public int compare(Object cmp1, Object cmp2)

		{
			
			Long Doc1 ;
			Long Doc2 ;
			
			Doc1 = Long.valueOf(((SortData)cmp1).getZindex());
			Doc2 = Long.valueOf(((SortData)cmp2).getZindex());
			
			return (Doc1.compareTo(Doc2));
			 

		}

	

}
