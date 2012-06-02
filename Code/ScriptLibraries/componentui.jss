function addrow( dataTable, labelString, valueBindingString ){
	// Create column
	var row:com.ibm.xsp.component.xp.XspTableRow = new com.ibm.xsp.component.xp.XspTableRow();
	dataTable.getChildren().add(column);
	
	// Add header to Name Column
	var header:com.ibm.xsp.component.xp.XspOutputLabel = new com.ibm.xsp.component.xp.XspOutputLabel();
	header.setValue( headerString );
	column.setHeader(header);
	
	// Add body to Name Column
	var columnData:com.ibm.xsp.component.xp.XspOutputText = new 
com.ibm.xsp.component.xp.XspOutputText();
	var valueBinding = application.createValueBinding( valueBindingString );
	columnData.setValueBinding( 'value', valueBinding );
	column.getChildren().add( columnData );
	
	return column;
}