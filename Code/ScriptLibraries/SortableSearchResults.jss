/*
	Warning!!
	The following code is poorly documented and should not .
	It also contains poorly thought out variable names.
	
	The alternative to this mess is me not sharing this technology/technique with the world.
	I like to share, but I also like to be lazy.. :)
	
	Sincerely
	Tommy Valand/DontPanic
*/
var SearchDemo = {	
	beforeRenderResponse: function(){
		try {
			var startTime = new Date();			
			var searchMethod = viewScope.selectedDataSource;
			
			switch( searchMethod ){
				case 'json':
					JSONSearch.execute();
					viewScope.put( 'loadTime', new Date() - startTime );
					break;
				
				case 'doc_col':
				default:
					var query = '[Form=Person] AND ' + viewScope.query;
					TemplateSearch.execute({
						fieldDefinitions: [{	
							fieldName: 'firstName',
							formula: 'FirstName',
							dataType: 'string'
						},{
							fieldName: 'lastName',
							formula: 'LastName',
							dataType: 'string'
						},{
							fieldName: 'email',
							formula: 'InternetAddress',
							dataType: 'string'
						},{
							fieldName: 'emailLength',
							formula: '@Length(InternetAddress)',
							dataType: 'number'
						},{
							fieldName: 'officeCountry',
							formula: 'OfficeCountry',
							dataType: 'string'
						},{
							fieldName: 'modified',
							formula: '@Modified',
							dataType: 'date'
						}],
						query: query,
						sortDirection: viewScope.sortDirection,
						sortField: viewScope.sortField
					});
					
					viewScope.put( 'loadTime', new Date() - startTime );
					break;
			}			
		} catch( e ){ Debug.exceptionToPage( e ); }
	},
	searchMethodChanged: function(){
		try {
			viewScope.remove( 'jsonValues' );
			viewScope.remove( 'sortField' );
			viewScope.remove( 'sortDirection' );
		} catch( e ){ Debug.exceptionToPage( e ); }
	}
}

/*
	Simple democode for sorting a ViewEntryCollection 
*/
var ViewEntrySearch = {
	execute: function(){
		try {
			var startTime = new Date();
			
			importPackage( java.util );
	
			var queryString = viewScope.query;
			if( !queryString ){ return null; }
			
			// Updates FTIndex if changes since last FTIndex
			FullText.updateFTIndexIfContentChanged( database, query );
					
			var sortColumn = viewScope.sortField || 0;
			
			var peopleView:NotesView = database.getView( '_People' );
			peopleView.FTSearch( queryString );
			
			var entryMap:TreeMap = new TreeMap();			
			var unsortedEntries:NotesViewEntryCollection = peopleView.getAllEntries();
			
			var sortValue, unsortedEntry:NotesViewEntry = unsortedEntries.getFirstEntry();					
			while( unsortedEntry !== null ){
				sortValue = unsortedEntry.getColumnValues()[ sortColumn ];
				if( sortValue ){
					// Value to sort by + a unique identifier (key has to be unique in TreeMap)
					entryMap.put( sortValue + ' ' + unsortedEntry.getNoteID() , unsortedEntry );
				}
						
				unsortedEntry = unsortedEntries.getNextEntry( unsortedEntry );
			}
			
			var sortedEntries:Collection = new ArrayList( entryMap.values() );
			viewScope.put( 'numValues', sortedEntries.size() );
			
			var sortDirection = viewScope.sortDirection;
			if( sortDirection === 'descending' ){
				Collections.reverse( sortedEntries );				
			}
			
			viewScope.put( 'loadTime', new Date() - startTime );
			return sortedEntries;
		} catch( e ){ Debug.exceptionToPage( e ); }
	}
}

// Simple democode for working with JSON-strings from a view
var JSONSearch = {
	execute: function(){
		try {
			var query = viewScope.query;		
			var jsonValues = viewScope.jsonValues;
			
			if( !jsonValues || ScopeHelper.variableChanged( 'query' ) ){
				// Updates FTIndex if changes since last FTIndex
				FullText.updateFTIndexIfContentChanged( database, query );
				
				var jsonValues = Lookup.searchColumnValues( {
					viewName: 'PeopleJSON',
					query: query,
					evalValues: true
				} );
				
				viewScope.put( 'numValues', jsonValues.length );
			}
			
			jsonValues.sortByField( viewScope.sortField, viewScope.sortDirection );	
			viewScope.put( 'jsonValues', jsonValues );
		} catch( e ){ Debug.exceptionToPage( e ); }
	}
}

// Toggle sort-order by field name
function toggleSortOrder( dataTableId, fieldName ){
	getComponent( dataTableId ).setFirst( 0 );
	var currentSortField = viewScope.sortField;
	var currentSortDirection = viewScope.sortDirection;
	
	var newSortDirection;
	if( currentSortField === fieldName ){
		newSortDirection = ( currentSortDirection === 'ascending' ) ? 'descending' : 'ascending';
	} else {
		newSortDirection = 'ascending';			
	}
	
	viewScope.put( 'sortField', fieldName );
	viewScope.put( 'sortDirection', newSortDirection );
}

