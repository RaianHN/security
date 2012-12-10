package bsuite.loadcc;

public class SortData {
	
	private String index;
	private String zIndex;
	
	

	public SortData(String str1, String str2)
	{
		setIndex(str1.trim());
		setZindex(str2.trim());
		
	}



	public void setIndex(String index) {
		this.index = index;
	}



	public String getIndex() {
		return index;
	}



	public void setZindex(String zIndex) {
		this.zIndex = zIndex;
	}



	public String getZindex() {
		return zIndex;
	}

}
