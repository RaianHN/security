/**
*	Generic methods
*/

// Helper for inconsistent API
// Wrap around @DbLookup/@DbColumn/@Trim/@Unique calls to have an array returned
function $A( object ){
	try {
		// undefined/null -> empty array
		if( typeof object === 'undefined' || object === null ){ return []; }
		if( typeof object === 'string' ){ return [ object ]; }

		// Collections (Vector/ArrayList/etc) -> convert to Array
		if( typeof object.toArray !== 'undefined' ){
			return object.toArray();
		}

		// Array -> return object unharmed 
		if( object.constructor === Array ){ return object; }  

		// Return array with object as first item
		return [ object ];
	} catch( e ) { Debug.exceptionToPage( e ); }
}

// Sorts an array consisting of Javascript objects
Array.prototype.sortByField = function( fieldName:String, direction:String ){	
	var values = this;	
	if( !fieldName || values.length === 0 ){ return values; }
	direction = direction || 'ascending';
	
	var multiplier = (direction === 'ascending') ? 1 : -1;
	
	var fieldA, fieldB;
	function genericSort( a, b ){
		fieldA = a[fieldName];
		fieldB = b[fieldName];
		
		if( fieldA > fieldB ){ return 1 * multiplier; }
		if( fieldA < fieldB ){ return -1 * multiplier; }
		
		return 0;
	}
	
	values.sort( genericSort );	
	return values;	
}

// Helper methods for parsing/formatting dates
var DateConverter = {
	dateToString: function( date:java.util.Date, pattern:String ){
		try {
			if( !date ){ return ''; }

			var formatter = DateConverter.getFormatter( pattern );
			return formatter.format( date );
		} catch( e ){ Debug.exceptionToPage( e ); }
	},

	stringToDate: function( dateString:String, pattern:String ){
		try {
			if( !dateString ){ return null; }

			var formatter = DateConverter.getFormatter( pattern );
			return formatter.parse( dateString );
		} catch( e ){ Debug.exceptionToPage( e ); }
	},

	getFormatter: function( pattern:String ){
		try {
			var cacheKey = 'dateFormatter' + pattern;
			var dateFormatter = applicationScope[ cacheKey ];
			if( !dateFormatter ){
				dateFormatter = new java.text.SimpleDateFormat( pattern );
				applicationScope[ cacheKey ] = dateFormatter;
			}

			return dateFormatter;
		} catch( e ){ Debug.exceptionToPage( e ); }
	} 
}

// Helper-class for debugging
var Debug = {
	// Send a stack trace of an exception
	exception: function( exception ){
		// If on localhost/public db - throw exception
		if( this.getUserName() === 'Anonymous' ){ throw exception; }
		
		this.message( this.getExceptionString( exception ), 'Exception!' );
	},

	// Add exception to page
	exceptionToPage: function( exception ){  
		this.setPageDebugMessage( 'Exception: ' + this.getExceptionString( exception ) );  
	},
	
	getExceptionString: function( exception ){
		var errorMessage = exception.message;

		if( typeof exception.printStackTrace !== 'undefined' ){
		  var stringWriter = new java.io.StringWriter();
		  exception.printStackTrace( new java.io.PrintWriter( stringWriter ) );
		  errorMessage = stringWriter.toString();
		}

		if( typeof exception === 'com.ibm.jscript.InterpretException' ){
		  errorMessage = exception.getNode().getTraceString() + '\n\n' + errorMessage;
		}
		
		return errorMessage;
	},
	
	getUserName: function(){
		return @Name( '[CN]', @UserName() );
	},

	// Send a message (supports HTML)
	message: function( message, subject ){
		// If on localhost/public db - throw exception
		if( this.getUserName() === 'Anonymous' ){ throw 'Not logged in. Could not send message: ' + message; }

		session.setConvertMime( false );
		var doc:NotesDocument = database.createDocument();
		doc.replaceItemValue( 'Form', 'Memo' );
		doc.replaceItemValue( 'Subject', subject || 'Debug..' );  
		doc.replaceItemValue( 'SendTo', this.getUserName() );

		var body:NotesMIMEEntity = doc.createMIMEEntity();

		var contentStream = session.createStream();
		// Set preferred styling
		contentStream.writeText( '' );

		// Convert linefeeds to <br>s
		contentStream.writeText( message.replace( '\n', '<br />' ) );
		body.setContentFromText( contentStream, 'text/html;charset=ISO-8859-1', 
		lotus.domino.MIMEEntity.ENC_NONE );    
		doc.send();

		session.setConvertMime( true );
	},

	// Add message to page
	messageToPage: function( message ){
		this.setPageDebugMessage( message );
	},

	// Adds message to the bottom of the page in a dynamically created xp:text
	setPageDebugMessage: function( message ){
		var messageControl = getComponent( 'global-debug-messages' );
		if( !messageControl ){
			messageControl = new com.ibm.xsp.component.xp.XspOutputText();
			messageControl.setId( 'global-debug-messages' );
			messageControl.setEscape( false );
			messageControl.setStyleClass( 'xspMessage' );

			var valueBinding = facesContext.getApplication().createValueBinding( '#{requestScope.debugMessages}' );
			messageControl.setValueBinding( 'value', valueBinding );

			view.getChildren().add( messageControl );
		}

		var currentMessages = requestScope.debugMessages;
		if( typeof currentMessages !== 'string' ){ currentMessages = ''; }
		requestScope.put( 'debugMessages', message + '<br />' + currentMessages  );
	} 
}

var JSON = {
	// Makes a by-value copy of the object
	copy: function( object ){
		try {
			// Faster way to copy arrays
			if( object && typeof object.concat === 'function' ){ return object.concat(); }

			return this.parse( this.stringify( object ) );
		} catch( e ){ Debug.exceptionToPage( e ); }
	},
	
	// Parses JSON to JS object
	parse: function( JSON ){
		try {
			return fromJson( '{"values":' + JSON + '}' ).values;   
		} catch( e ){ Debug.exceptionToPage( e ); }  
	},
	
	// Parses an array of JSON string to an array of JS objects. Includes parsing of dates
	// in format ¤¤yyyy-MM-dd
	parseWithDates: function( valuesJson ){
		try {
			if( !valuesJson ){ return []; }
			valuesJson = $A( valuesJson );
			
			var values = JSON.parse( '[' + valuesJson.join(',') + ']' );
			
			// Get date keys
			var dateKeys = [];
			var obj = values[0];
			for( key in obj ){
				var objValue = obj[ key ];
				if( objValue === null || ( typeof objValue === 'string' && objValue.indexOf( '¤¤' ) > -1 ) ){
					dateKeys.push( key );
				}
			}			
			
			// Convert dates
			if( dateKeys.length > 0 ){
				for( var i = 0; i < values.length; i++ ){
					var obj = values[i];
					for( var j = 0; j < dateKeys.length; j++ ){
						var key = dateKeys[j];
						obj[key] = DateConverter.stringToDate( obj[key], '¤¤yyyy-MM-dd' );			
					}
				}			
			}
			
			return values;
		} catch( e ){ Debug.exceptionToPage( e ); }
	},

	// Converts object to JSON string
	stringify: function( object ){
		try {
			return toJson( object );
		} catch( e ){ Debug.exceptionToPage( e ); }
	}
}

// Helper-class for searches/lookups
var Lookup = {
	getColumnValues: function( notesView:NotesView, columnIndex:int, evalValues:boolean ){
		try {
			var entryCol:NotesViewEntryCollection = notesView.getAllEntries();
			return this.getColumnValuesFromEntryCol( entryCol, columnIndex, evalValues );
		} catch( e ){ Debug.exceptionToPage( e ); }		
	},	
	getColumnValuesByKey: function( notesView:NotesView, key:String, columnIndex:int, exactMatch:boolean, evalValues:boolean ){
		try {
			exactMatch = exactMatch || true;
					
			var entryCol:NotesViewEntryCollection = notesView.getAllEntriesByKey( key, exactMatch );
			return this.getColumnValuesFromEntryCol( entryCol, columnIndex, evalValues );
		} catch( e ){ Debug.exceptionToPage( e ); }		
	},
	
	// Do a search, and fetch the results from a specific view column
	searchColumnValues: function( options ){
		try {
			var query = options.query;
			if( !query ){ return []; }
									
			// Configvalues for the search
			var columnIndex = options.columnIndex || 0; // Column with values
			var evalValues = options.evalValues; // Convert string in column to JSON
							
			// Fetch the values from the view
			var lookupView:NotesView = database.getView( options.viewName );						
			lookupView.FTSearch( query );	
			var entryCol:NotesViewEntryCollection = lookupView.getAllEntries();
			return this.getColumnValuesFromEntryCol( entryCol, columnIndex, evalValues );
		} catch( e ){ Debug.exceptionToPage( e ); }
	},
	
	// Helper function to extract the column values from an entry-collection
	getColumnValuesFromEntryCol: function( entryCol:NotesViewEntryCollection, columnIndex:int, evalValues:boolean ){
		try {
			var entry:NotesViewEntry = entryCol.getFirstEntry();	
			var columnValues = [];	
			while( entry !== null ){
				columnValues.push( entry.getColumnValues()[columnIndex]);						
				entry = entryCol.getNextEntry();
			}
			
			// Converts the array of strings to an array of Javascript objects
			if( evalValues ){ 
				return JSON.parseWithDates( columnValues );				
			} else {
				return columnValues;
			}
		} catch( e ){ Debug.exceptionToPage( e ); }
	}
}

var ScopeHelper = {
	// Test if a scoped variable has changed
	variableChanged: function( scopedVariableName ){
		try {
			var scopedVariable = viewScope.get( scopedVariableName );
			var prevScopedVariable = viewScope.get( 'prev-' + scopedVariableName );
			
			var variableChanged = ( !prevScopedVariable || scopedVariable !== prevScopedVariable );
			viewScope.put( 'prev-' + scopedVariableName, scopedVariable );
			
			return variableChanged;
		} catch( e ){ Debug.exceptionToPage( e ); }
	}
}

// FTSearch -> JS Object Array based on field definitions 
var TemplateSearch = {	
	// Templates to build the value-parts from
	"template-string": "'\"' + @ReplaceSubstring( $formula ; '\"' ; '&#x22;' ) + '\"'",
	"template-multi-string": "'[\"' + @Implode( $formula ; '\",\"' ) + '\"]'",
	"template-number": "@ReplaceSubstring( @Text( @ToNumber( $formula ) ) ; ',' ; '.' )",
	"template-multi-number": "'[' + @Implode( @Text( @ToNumber( $formula ) ) ; ',' ) + ']'",
	"template-date": "@If( $formula='' ; 'null' ; '\"¤¤' + @Implode( @Text( @Year( $formula ) : @Month( $formula ) : @Day( $formula ) ) ; '-' ) + '\"' )",
	"template-multi-date":"@If( $formula='' ; 'null' ; '[\"¤¤' + @Implode( @Transform( $formula ; 'date' ;"
		+ "@Implode( @Text( @Year( date ) : @Month( date ) : @Day( date ) ) ; '-' ) ); '\",\"¤¤' ) + '\"]' )",
	
	// Helper-method to build a string that can be evaluated in formula-language against a document
	buildJSONFormulaTemplate: function( fieldDefinitions ){
		try {
			var formulaTemplateItems = [];
			for( var i=0; i < fieldDefinitions.length; i++ ){
				var field = fieldDefinitions[i];
				if( field.formula ){							
					var valuePart = this['template-' + field.dataType].replace( /\$formula/g, field.formula );
					formulaTemplateItems.push( "'\"" + field.fieldName + "\":' + " + valuePart );
				}
			}
			// Legger til noteId og unid
			formulaTemplateItems.push( "'\"noteId\":\"' + @RightBack( @NoteId ; 5 ) + '\"'" )
			formulaTemplateItems.push( "'\"unid\":\"' + @Text( @DocumentUniqueId ) + '\"'" );
			
			return "'{' + " + formulaTemplateItems.join( " + ',' + " ) + " + '}'";
		} catch( e ){ Debug.exceptionToPage( e ); }				
	},
	
	/*
		query: The FTSearch-query
		fields: an array of objects describing the template fields with fieldName, formulaString, dataType
			dataType: 	string, multi-string, number, multi-number, date, multi-date
	*/
	execute: function( options ){	
		try {
			var query = options.query;
			if( !query ){ return []; }
			
			var fieldDefinitions = options.fieldDefinitions;			
			var sortField = options.sortField;
			var sortDirection = options.sortDirection || 'ascending';
			
			var jsonValues = viewScope.jsonValues;
						
			if( !jsonValues || ScopeHelper.variableChanged( 'query' ) ){
				// Updates FTIndex if changes since last FTIndex
				FullText.updateFTIndexIfContentChanged( database, query );
				
				var resultCol:NotesDocumentCollection = database.FTSearch( query );
				var jsonFormulaTemplate = this.buildJSONFormulaTemplate( fieldDefinitions );
				
				var jsonItems = [];
				var resultDoc:NotesDocument = resultCol.getFirstDocument();
				while( resultDoc !== null ){
					if( resultDoc.isValid() && resultDoc.getSize() > 0 ){
						jsonItems.push( session.evaluate( jsonFormulaTemplate, resultDoc )[0] );
					}
					
					resultDoc = resultCol.getNextDocument();
				}		
				
				if( jsonItems.length === 0 ){ return; }
				
				jsonValues = JSON.parseWithDates( jsonItems );				
				viewScope.put( 'numValues', jsonValues.length );
				
			}
			
			if( sortField && sortDirection ){
				jsonValues.sortByField( sortField, sortDirection );
			}
			viewScope.put( 'jsonValues', jsonValues );
		} catch( e ){ Debug.exceptionToPage( e ); }
	}
}

// Helper code for dealing with FullText indexes
var FullText = {
	// Checks for any change in content vs last update of FTIndex
	contentChangedSinceLastFTIndex: function( db:NotesDatabase ){
		try {
			var lastFTIndexed:NotesDateTime = db.getLastFTIndexed();
			
			if( lastFTIndexed === null ){ // FTIndex missing -> create 
				db.createFTIndex( 0, true );
				return false;
			}
			
			var lastFTIndexedDate = lastFTIndexed.toJavaDate();
			var lastModifiedDate = db.getLastModified().toJavaDate(); 
			
			// First test (least expensive) - FTIndex up to date -> exit
			if( lastModifiedDate.compareTo( lastFTIndexedDate ) <= 0  ){
				return false;
			}
			
			// Adjust FTIndexed-date backwards, just in case
			var lastFTIndexedAdjusted:NotesDateTime = session.createDateTime( lastFTIndexedDate );
			lastFTIndexedAdjusted.adjustSecond( -5 );
			
			// Query by form if possible to make the search more efficient
			//var query = '';
			//if( forms.length > 0 ){
				//query = 'Form="' + forms.join( '":"' ) + '"';
			//}			
			//var col:NotesDocumentCollection = db.search( query, lastFTIndexedAdjusted );
			//return ( col.getCount() > 0 );						
		} catch( e ){ Debug.exceptionToPage( e ); }
	},
	updateFTIndexIfContentChanged: function( db:NotesDatabase ){
		try {	
			println("Inside fulltext");
			//if( !query ){ throw new java.lang.Exception( 'No query' ); }
			if( !db.isFTIndexed() ){
				Debug.messageToPage( 'No FTIndex - trying to create..' );
				println("Creating index");
				db.createFTIndex( 0, false );			
				println("Index created");
				return;
			}
						
			// Extract any form information to make the query more efficient
			//var forms = [];
			//if( query.indexOf( 'Form' ) > -1 ){
			//	forms = this.extractFormnamesFromQuery( query );
			//}			 
			
			var contentChanged = this.contentChangedSinceLastFTIndex( db);
			
			if( contentChanged ){
				db.updateFTIndex( false );
			}
		} catch( e ){ Debug.exceptionToPage( e ); }
	},
	extractFormnamesFromQuery: function( query ){
		try {
			return query.match( /\[form=[\(\)\,\w]+\]/ig ).join(',').replace( /[\[\]\(\)]|form=/ig, '' ).split( ',' );
		} catch( e ){ Debug.exceptionToPage( e ); }
	}
}