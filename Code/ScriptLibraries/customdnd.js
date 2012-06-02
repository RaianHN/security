XSP.addOnLoad(initDndTables);

function initDndTables(){
	initDndTable(mainTableId);
	initDndTable1(sourceControl);
	//initDndTable(mainTableId2);
}
function initDndTable(tid) {
console.log("tableid:"+tid);
	//setUNIDS(tid,false,true,3);
	var tVar = new dojo.dnd.Source(tid,{copyOnly:true, selfAccept:false});
	dojo.parser.parse();
	dojo.connect(tVar,"onDndDrop", movedata);
}

function initDndTable1(tid){

	var tVar = new dojo.dnd.Source(tid);
	dojo.parser.parse();
	dojo.connect(tVar,"onDndDrop", movedata);

}

//XSP._inheritedPartialRefresh = XSP._partialRefresh;
//XSP._partialRefresh = function(method,form,refreshId,options){
//    dojo.publish("/XSP/partialRefresh", new Array(method,form,refreshId,options));
 //   this._inheritedPartialRefresh(method,form,refreshId,options);
//}
//dojo.subscribe("/XSP/partialRefresh", null, function(method,form,refreshId,options) {
//	if ((refreshId == mainTableId) ){	
//	    if (options.onComplete)
//	        options._inheritedOnComplete = options.onComplete;
	    
//	    options.onComplete = function() {
//	        initDndTable(refreshId);
//	        if (this.inheritedOnComplete)
//	            this.inheritedOnComplete();
//	    }
//	}
//});

function setUNIDS(tableID,hasTitle,hasTH,UNIDRow){
	table1 = dojo.byId(tableID);
	var childrenIndex = 0;
	if (hasTitle) childrenIndex++;
	if (hasTH) childrenIndex++;	
	var tRows = table1.children[childrenIndex].children;
	//walk rows
	for (var i=0;i<tRows.length;i++){
		UNID = tRows[i].children[UNIDRow - 1].children[0].value;
		tRows[i].id = UNID;
	}
}//setUNIDs

function movedata(source, nodes, copy){
	var man = dojo.dnd.manager();
	var tar = dojo.dnd.manager().target;	 
	var t = this;
	if(dojo.dnd.manager().target !== this){ return; }
	targetName = dojo.dnd.manager().target.node.id;
	console.log("targetname"+targetName);
		console.log("nodes:length"+nodes.length);
	var rowArr ;
	console.log("sourcename"+nodes[0].children[0].children[0].id);
	for (i=0;i<nodes.length;i++)
	{
		
		//rowArr = (nodes[i].id).split("-");
		rowArr = nodes[i].children[1].children[0].innerHTML
		var com = document.getElementById(targetName);
		console.log("nodes"+nodes[0].id);
		console.log("nodes"+nodes[0].children[1].children[0].innerHTML);
		com.value =nodes[i].children[1].children[0].innerHTML;
	}
	dojo.byId(nodes[0].children[0].children[0].id).click()
	
	/*tNameArr = targetName.split(":");
	targetName = tNameArr[tNameArr.length - 1];
	var docsArr = new Array();
	for (i=0;i<nodes.length;i++){
		docsArr.push(nodes[i].id);
	}
	var obj = {};
	obj.table = targetName;
	obj.docs = docsArr;	
	var tableDataJSON = dojo.toJson(obj);
	var stashField = document.getElementById(hiddenFldId);
	stashField.value = tableDataJSON;*/
	//var bttn = document.getElementById(button_id);
	 XSP.partialRefreshPost(computePanelId, {
        onStart: null,
        onComplete:XSP.partialRefreshPost(refreshpanelId, {
        onStart: null,
        onComplete:  XSP.partialRefreshPost(sourceControl, {
        onStart: null,
        onComplete: null,
        onError: null                        
   	 	}),
        onError: null                        
    }),
        onError: null                        
    })
     
   
}//movedata