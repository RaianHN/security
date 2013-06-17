
 window.onload = function(){
 hijackAndPublishPartialRefresh();
//hidestartloader();	  
 }

 /*
 function hidestartloader(){
	 dojo.query(".divstartloader1").forEach(function(node, index, arr){
		  dojo.fadeOut({
		         node:"startloader",
		         duration:600,
		         onEnd: function(){
		             dojo.style("startloader", "display", "none");
		            }
		     }).play();
		 });
   
 }
*/
function hijackAndPublishPartialRefresh(){
 // Hijack the partial refresh
 XSP._inheritedPartialRefresh = XSP._partialRefresh;
 XSP._partialRefresh = function( method, form, refreshId, options ){  
     // Publish init
     dojo.publish( 'partialrefresh-init', [ method, form, refreshId, options ]);
     this._inheritedPartialRefresh( method, form, refreshId, options );
 }
 
 // Publish start, complete and error states 
 dojo.subscribe( 'partialrefresh-init', function( method, form, refreshId, options ){
  
  if( options ){ // Store original event handlers
   var eventOnStart = options.onStart; 
   var eventOnComplete = options.onComplete;
   var eventOnError = options.onError;
  }

  options = options || {};  
  options.onStart = function(){
   dojo.publish( 'partialrefresh-start', [ method, form, refreshId, options ]);
   if( eventOnStart ){
    if( typeof eventOnStart === 'string' ){
     eval( eventOnStart );
    } else {
     eventOnStart();
    }
   }
  };
  
  options.onComplete = function(){
   dojo.publish( 'partialrefresh-complete', [ method, form, refreshId, options ]);
   if( eventOnComplete ){
    if( typeof eventOnComplete === 'string' ){
     eval( eventOnComplete );
    } else {
     eventOnComplete();
    }
   }
  };
  
  options.onError = function(){
   dojo.publish( 'partialrefresh-error', [ method, form, refreshId, options ]);
   if( eventOnError ){
    if( typeof eventOnError === 'string' ){
     eval( eventOnError );
    } else {
     eventOnError();
    }
   }
  };
 });
 /*
 dojo.subscribe( 'partialrefresh-start',  function( method, form, refreshId ){
 //loading()
	 //alert('test')
	 dojo.query(".domfindmebutton5999").forEach(function(node, index, arr){
	  if(dojo.isDescendant(node.id, refreshId )) {
	    var thisDijit = dijit.byId(node.id.replace("button5999", ""));
	      thisDijit.destroyRecursive(false);
	    }
	 });
} );*/

}
//used in search control
multiselectboxcc = {
		"deletevalue" : function (thisvalue, fieldid, valuetoremove, multisep){
	try{
	 var objecttodelete= dojo.byId(thisvalue);
	 objecttodelete.parentNode.removeChild(objecttodelete);
	 var field1 = dojo.query("." +fieldid);
		 var array1 = field1[0].value.split(multisep);
	if (array1.constructor == Array) {
		 //array1.splice(valuetoremove, 1);
		for( y in array1){
			if(array1[y] == valuetoremove){
				array1.splice(y,1)
			}
			
		}
		 field1[0].value = array1.join(multisep);
	}else{
		field1[0].value = "";
	}
	
	XSP.executeOnServer('view:_id1:eventHandler7','view:_id1:_id17:TroubleTicketViewpane');
}catch(err)
  {
  txt="There was an error on this page.\n\n";
  txt+="Error description: " + err.description + "\n\n";
  
  txt+="Click OK to continue.\n\n";
  //alert(txt);
  }

}



}

/*
//Licensed under http://creativecommons.org/licenses/by/3.0/
//Oringinal Code from Jeremy Hodge on the XPages Blog
//http://xpagesblog.com/xpages-blog/2010/4/10/xpages-compatible-dojo-dialog-reusable-component.html

// com.ZetaOne.Widget.Dialog
dojo.provide('com.ZetaOne.widget.Dialog');
dojo.require('dijit.Dialog');
(function(){
		dojo.declare("com.ZetaOne.widget.Dialog", dijit.Dialog, {
				postCreate: function(){
					this.inherited(arguments);
					dojo.query('form', dojo.body())[0].appendChild(this.domNode);
				},
				_setup: function() {
					this.inherited(arguments);
					if (this.domNode.parentNode.nodeName.toLowerCase() == 'body')
						dojo.query('form', dojo.body())[0].appendChild(this.domNode);				
				}				
		})
}());	

*/


XSP.executeOnServer = function () {
	//alert("inside execute on server function")
	// must supply event handler id or we're outta here....
	if (!arguments[0])
		return false;

	// the ID of the event handler we want to execute
	var functionName = arguments[0];
	//alert("event "+functionName);
	// OPTIONAL - The Client Side ID that you want to partial refresh after executing the event handler
	var refreshId = (arguments[1]) ? arguments[1] : "@none";
	var form = (arguments[1]) ? this.findForm(arguments[1]) : dojo.query('form')[0];
//alert("refresh id"+refreshId);
	// catch all in case dojo element has moved object outside of form...
	if (!form)
		form = dojo.query('form')[0];

	// OPTIONAL - Options object contianing onStart, onComplete and onError functions for the call to the
	// handler and subsequent partial refresh
	var options = (arguments[2]) ? arguments[2] : {};

	// OPTIONAL - Value to submit in $$xspsubmitvalue. can be retrieved using context.getSubmittedValue()
	var submitValue = (arguments[3]) ? arguments[3] : '';

	//alert("event "+functionName+" svalue "+submitValue);
	// Set the ID in $$xspsubmitid of the event handler to execute
	dojo.query('[name="$$xspsubmitid"]')[0].value = functionName;
	dojo.query('[name="$$xspsubmitvalue"]')[0].value = submitValue;
	this._partialRefresh("post", form, refreshId, options);
}

//Setting resizer handle

function reinitialize(mov, head, panel)
{
 	var dnd = new dojo.dnd.move.boxConstrainedMoveable(mov, {box: {l: 0, t: 0, w: 3000, h: 3000}, handle: head, skip: "true", within: true});
 
//	var dnd = new dojo.dnd.Moveable(dojo.byId(mov),{handle: head , skip: "true"});
    
    var handle = new dojox.layout.ResizeHandle({targetId:panel}).placeAt(panel);

	var header = dojo.byId(head);
	header.onmouseup = onMouseUp1;

}

//called when jumping to desired portlet
function jumplink(id){
	
	var com = document.getElementById(id);
	if (com != null){ 
	dojox.fx.smoothScroll({ node:dojo.byId(id), win:window, duration:400}).play();
	var toggle = dojo.byId("view:_id1:_id17:Toggle");//"view:_id1:_id17:Toggle"
		if (toggle.getAttribute("value") == "1"){
		var con = document.getElementById(id+"moveable");
  			var style = con.style;
			var idarr = id.split(":");
			var len = idarr.length;
			var id1 = idarr[len-1];
		
			var props = new Array();
    		props[0] = style.left;
     		props[1] = style.top;
     		props[2] = id1+"moveable" ;
     		props[3] = zIndex;
     
  			con.style.zIndex = props[3];     
  			 
		
			XSP.executeOnServer('view:_id1:eventHandler3',"", "", props);		
		
			setTimeout(function(){
		
			XSP.executeOnServer('view:_id1:eventHandler4',"", "", ++zIndex);
		
			}, 300);
		// var portletstyle=com.parentNode.style.cssText; 
		// com.parentNode.style.cssText=portletstyle+ "z-index:"+zIndex;
		// XSP.executeOnServer("view:_id1:eventHandler4","", "", ++zIndex);
		}
		setFocusComponent(com);
	}
}


// To set focus on a portlet
function setFocusComponent(com){
var cnt = 0 ; 
		setInterval(function(){if (cnt == 0 || cnt%2 ==0){
					com.style.border='2px solid #0000FF';
					}else if (cnt%2 != 0){
					com.style.border='2px dashed #0000FF';} 
				if (cnt == 3){ com.style.border=''; return;}
				 cnt++;  },400)  ;
}
