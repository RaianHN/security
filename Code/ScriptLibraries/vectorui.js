    dojo.addOnLoad(init);
     XSP.addOnLoad( hijackAndPublishPartialRefresh);
   surface ="view:_id1:container"
   var drawing;
    function init() {
    alert("onload");
     var node = dojo.byId(surface);
   drawing  = dojox.gfx.createSurface(node,700,800);
		
	//	var graph = new NodeGraph(drawing);
        //Create our surface.
          
        drawing.createRect({
            width: 100,
            height: 100,
            x: 50,
            y: 50
        }).setFill("blue").setStroke("black");

      //  dojo.connect(dijit.byId("button2"), "onClick", function() {
       //     var def = dojox.gfx.utils.toSvg(drawing);
       //     def.addCallback(function(svg) {
       //         dojo.byId("svg").innerHTML = svg;
      //      });
       //     def.addErrback(function(err) {
        //        alert(err);
         //   });
      //  });
    }
    
    
    //add circle
    function addcircle(){
 		drawing.createCircle({ cx: 500, cy: 900, r:50 }).setFill("blue").setStroke("black");
    }
    //add line
    function addline(){
    console.log("adding line")
    	drawing.createLine({ x1: 100, y1: 50, x2:250, y2:90 }).setFill("blue").setStroke("black");;
 		//drawing.createCircle({ cx: 100, cy: 100, r:50 });
    }
    
    //Set the init function to run when dojo loading and page parsing has completed.
function hijackAndPublishPartialRefresh(){
 // Hijack the partial refresh
 XSP._inheritedPartialRefresh = XSP._partialRefresh;
 XSP._partialRefresh = function( method, form, refreshId, options ){  
     // Publish init
     dojo.publish( 'partialrefresh-init', [ method, form, refreshId, options ]);
     dojo.publish("/XSP/partialRefresh", new Array(method,form,refreshId,options));
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
 
/*dojo.subscribe( 'partialrefresh-start',  function( method, form, refreshId ){
 //loading()
	//alert(refreshId)
	 dojo.query(".domfindmebutton5999").forEach(function(node, index, arr){
	  if(dojo.isDescendant(node.id, refreshId )) {
	    var thisDijit = dijit.byId(node.id.replace("button5999", ""));
     thisDijit.destroyRecursive(false);
	    }
	 });
	  
});*/


dojo.subscribe("/XSP/partialRefresh", null, function(method,form,refreshId,options) {

	if ((refreshId == surface)  ){	
	alert('test1')
	    if (options.onComplete)
	        options._inheritedOnComplete = options.onComplete;
	    
    options.onComplete = function() {
	       // init();
	        if (this.inheritedOnComplete)
	            this.inheritedOnComplete();
	    }
	}
	/*else if ((refreshId == sourceControl)){	
	alert('test2')
	    if (options.onComplete)
	        options._inheritedOnComplete = options.onComplete;
	    
	    options.onComplete = function() {
	        initDndTable1(refreshId);
	        alert('test3')
	        if (this.inheritedOnComplete)
	            this.inheritedOnComplete();
	    }
	}else{
	alert('test4'+refreshId);
	 if (options.onComplete)
	        options._inheritedOnComplete = options.onComplete;
	    
	    options.onComplete = function() {
	        initDndTable1(refreshId);
	        alert('test3')
	        if (this.inheritedOnComplete)
	            this.inheritedOnComplete();
	            }
	}*/
});
}

