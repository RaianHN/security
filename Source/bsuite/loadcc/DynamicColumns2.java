package bsuite.loadcc;
import java.util.Map;
import java.util.Vector;

import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;
import bsuite.utility.Utility;

import com.ibm.xsp.component.UIPanelEx;
import com.ibm.xsp.extlib.component.dojo.grid.UIDojoDataGrid;
import com.ibm.xsp.extlib.component.dojo.grid.UIDojoDataGridColumn;
import com.ibm.xsp.extlib.component.rest.DominoViewJsonService;
import com.ibm.xsp.extlib.component.rest.UIRestService;

public class DynamicColumns2 {
	
	private UIDojoDataGrid dataGrid ;
	private UIRestService rService;
	private UIPanelEx viewPanel;
	
	@SuppressWarnings("unchecked")
	public UIPanelEx getChartView()
	{
		rService = null;//**
		createRestService();//**
		dataGrid = null ;//**
		createDataGrid();
		
		try {
			createColumns(dataGrid);
		} catch (NotesException e) {
			e.printStackTrace();
		}
		
		viewPanel = new UIPanelEx();
		viewPanel.getChildren().add(rService);
		viewPanel.getChildren().add(dataGrid);
		
		return viewPanel;
	}
	
	
	public void createDataGrid()//**
	{
		
		UIDojoDataGrid dataGrid1 = new UIDojoDataGrid();
		dataGrid1.setStoreComponentId("EmbedDiscussionsService");
		dataGrid1.setAutoHeight(10);
		dataGrid1.setId("EmbeddDiscussionView");
		this.dataGrid = dataGrid1;//**
		
	}
	
	
	@SuppressWarnings("unchecked")
	public void createRestService()//**
	{
		
			
			DominoViewJsonService restView = new DominoViewJsonService();
			Map sessionscope = (Map) JSFUtil.getVariableValue("sessionScope");
			restView.setDatabaseName("Test/customer.nsf");
			restView.setViewName("Xview");
			restView.setVar("entryRow");
			restView.setSearch((String) sessionscope.get("searchString"));
			restView.setContentType("application/json");
			restView.setDefaultColumns(true);

			UIRestService restService = new UIRestService();
			restService.setId("EmbedDiscussionsService");
			restService.setService(restView);
			this.rService = restService;//**
		
		
	}
	
	public static void getDeleteColumns(UIDojoDataGrid grid)
	{
		
		try {
			int cnt = grid.getChildCount();
			for (int j=0; j< cnt; ++j)
			{
				
				grid.getChildren().remove(0);
				
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	@SuppressWarnings("unchecked")
	public static void getSaveDoc(String[] value)throws NotesException
	{
		View view = null;
		Document doc = null;
		Session sessionObj = Utility.getCurrentSession();
		String servName = Utility.getCurrentDatabase().getServer();
		
		 try
		 {
			 
			 
				if (value != null)
				{
				 view = sessionObj.getDatabase(servName, "Test/customer.nsf").getView("Testview");
					
					 doc = view.getFirstDocument();


					if(doc != null)
					{
						String viewName = doc.getItemValueString("viewName");
						if (viewName.equalsIgnoreCase("Xview") ) 
						{
							Vector vec = new Vector();
							for (int i=0 ; i< value.length ; ++i)
							{
								vec.add(value[i]);
								
							}
							
							doc.replaceItemValue("sColumns", vec);
							doc.save();
							
						}
						
						
					}
					
					
				}
			 
			 
			 
		 }
		 
		 catch (NotesException e) 
		 {
				e.printStackTrace();
		 }
		 catch(Exception ne){
			ne.printStackTrace();
		 }finally{
			 view.recycle();//**
			 doc.recycle();//**
			 view = null;//**
			 doc = null;//**
		 }
			
	
		
	}
	@SuppressWarnings("unchecked")
	public static void getAddColumns(UIDojoDataGrid grid, String[] columns)
	{
		
		int cnt = grid.getChildCount();
		for (int j=0; j< cnt; ++j)
		{
			
			grid.getChildren().remove(0);
			
			
		}
		
		int len = columns.length;
		for (int i=0; i< len; ++i)
		{
			
			UIDojoDataGridColumn result = new UIDojoDataGridColumn();
			result.setField(columns[i]);
			grid.getChildren().add(result);
			
		}
		
		
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	public  void createColumns(UIDojoDataGrid grid)throws NotesException
	{
		 View view = null;//**
		 Document doc = null;//**
		
		try
		{
			Session sessionObj = Utility.getCurrentSession();
			String servName = Utility.getCurrentDatabase().getServer();
			 
			
			view = sessionObj.getDatabase(servName, "Test/customer.nsf").getView("Testview");
			
			doc = view.getFirstDocument();
			String viewName1 = null;
			Vector cols = null;
			if (doc != null)
			{
				
				viewName1 = doc.getItemValueString("viewName");
				if (viewName1 != null && viewName1.equalsIgnoreCase("Xview"))
				{
					cols = doc.getItemValue("sColumns");
		
				}
		
		
				
			
				if (cols != null)
				{
			
					int cnt = cols.size();
					for (int i=0; i< cnt; ++i)
					{
						UIDojoDataGridColumn result = new UIDojoDataGridColumn();
						result.setField((String) cols.get(i));
						grid.getChildren().add(result);
					}
			 
				}
				
			}

			
			
			
			
		}
		
	
		
		catch (NotesException e) 
		{
			e.printStackTrace();
		}
		finally{
			view.recycle();//**
			doc.recycle();//**
			view = null;//**
			doc = null;//**
		}
		
    
		
		
	}
	


}
