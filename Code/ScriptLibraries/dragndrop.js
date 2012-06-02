XSP.addOnLoad(initDndTables);



function initDndTables(){
	initDndTable(mainTableId);
	initDndTable1(sourceControl);
	initDndTable1(targetControl);
}

function initDndTable(tid) {
	setUNIDS(tid,true,true,2);
	var tVar = new dojo.dnd.Source(tid, {copyOnly:true, selfAccept:false});
	dojo.parser.parse();


}


function initDndTable1(tid){

	var tVar = new dojo.dnd.Source(tid);
	dojo.parser.parse();
	dojo.connect(tVar,"onDndDrop", movedata);

}



XSP._inheritedPartialRefresh = XSP._partialRefresh;
XSP._partialRefresh = function(method,form,refreshId,options){
    dojo.publish("/XSP/partialRefresh", new Array(method,form,refreshId,options));
    this._inheritedPartialRefresh(method,form,refreshId,options);
}
dojo.subscribe("/XSP/partialRefresh", null, function(method,form,refreshId,options) {
	if ((refreshId == mainTableId)){	
	    if (options.onComplete)
	        options._inheritedOnComplete = options.onComplete;
	    
	    options.onComplete = function() {
	        initDndTable(refreshId);
	        if (this.inheritedOnComplete)
	            this.inheritedOnComplete();
	    }
	}
	
	else if ((refreshId == sourceControl)|| (refreshId == targetControl)){	
	    if (options.onComplete)
	        options._inheritedOnComplete = options.onComplete;
	    
	    options.onComplete = function() {
	        initDndTable1(refreshId);
	        if (this.inheritedOnComplete)
	            this.inheritedOnComplete();
	    }
	}
});




function setUNIDS(tableID,hasTitle,hasTH,UNIDRow)
{
	table1 = dojo.byId(tableID);
	var childrenIndex = 0;
	if (hasTitle) childrenIndex++;
	if (hasTH) childrenIndex++;	
	var tRows = table1.children[childrenIndex].children;
	//walk rows
	for (var i=0;i<tRows.length;i++){
		UNID = (tRows[i].children[0].children[0].innerHTML) + "-" + (tRows[i].children[UNIDRow - 1].children[0].value);
		tRows[i].id = UNID;
	}
}//setUNIDs







function movedata(source, nodes, copy)

{


	var man = dojo.dnd.manager();
	var tar = dojo.dnd.manager().target;	 
	var t = this;
	if(dojo.dnd.manager().target !== this){ return; }
	targetName = dojo.dnd.manager().target.node.id;


	
	var rowArr ;
	
	for (i=0;i<nodes.length;i++)
	{
		
		rowArr = (nodes[i].id).split("-");
		var com = document.getElementById(targetName);
		com.value = rowArr[0] ;
	}
	
	
  
    if (targetName == sourceControl)
    {
    	var com = document.getElementById(sourceUnid);
		com.value = rowArr[1] ;
		
		
    	XSP.partialRefreshPost(sourceControl, {
        onStart: null,
        onComplete: null,
        onError: null                        
   	 	})
   	 	
    
    }
    else if (targetName == targetControl)
    {
    	var com = document.getElementById(targetUnid);
		com.value = rowArr[1] ;
		
		
    	XSP.partialRefreshPost(targetControl, {
        onStart: null,
        onComplete: null,
        onError: null                        
    	})
    
  
    }

	
}//movedata






