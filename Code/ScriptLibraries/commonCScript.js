function getFeaturePermission(clId,arrNumberOfFeatures){
	//This function is used to get the checked values for features from the client and create an arrow with the module name, feature name and permission
		
	//clId: The base client id of the page ex:view:_id1:_id2

	//var size = "#{javascript:viewScope.moduleCount}";
	//var arrCnt = "#{javascript:viewScope.arrCount}"
	
	arrCnt = arrNumberOfFeatures;//at each index, the number specifies the number of features available for this module

	if(arrCnt.charAt(0)=="["){
	arrCnt = arrCnt.replace("[",""); //Remove []
	}
	if(arrCnt.charAt(arrCnt.length-1)=="]"){
	arrCnt = arrCnt.replace("]","");
	}

	var arr = new Array();
	arr = arrCnt.split(",");
	var size = arr.length;

	if (size == 0) {
	    alert("No modules to define features");
	}

	var i = 0;
	var moduleComp = null;
	var moduleName = "";
	var featureName = "";
	var j = 0;
	var k=0;
	var chkbx = null;
	var editArray = new Array();
	
	for (i = 0; i < size; i++) {
	    moduleComp = document.getElementById(clId + ":moduleRepeat:" + i + ":moduleName");
	    if (moduleComp != null) {
	        moduleName = moduleComp.innerHTML;	        
	        for (j = 0; j < arr[i]; j++) {
	            if (moduleName != "") {
	                featureName = document.getElementById(clId + ":moduleRepeat:" + i + ":featureRepeat:" + j + ":computedField2").innerHTML;               
	                if (featureName != null) {
	                    chkbx = document.getElementById(clId + ":moduleRepeat:" + i + ":featureRepeat:" + j + ":chk");
	                    if (chkbx != null) {
	                        if (chkbx.checked) {
	                            editArray[k++] = moduleName + ":" + featureName + ":1";
	                        } else {
	                            editArray[k++] = moduleName + ":" + featureName + ":0";
	                        }
	                    }
	                }

	            }
	        }

	    }
	}
	console.log("%o",editArray);

}

function getModulePermission(clId,numberOfModules){
	//This function is used to get the checked values for module permission from the client and create an arrow with the module name and tab visibility 
		
	//clId: The base client id of the page ex:view:_id1:_id2

	



	var size = numberOfModules;

	if (size == 0) {
	    alert("No modules to define permission");
	}

	var i = 0;
	var moduleComp = null;
	var moduleName = "";

	var chkbx = null;
	var editArray = new Array();
	
	for (i = 0; i < size; i++) {
	    moduleComp = document.getElementById(clId + ":moduleRepeatM:" + i + ":moduleNameM");
	    if (moduleComp != null) {
	        moduleName = moduleComp.innerHTML;	        
	      
	            if (moduleName != "") {
	                             
	               
	                    chkbx = document.getElementById(clId + ":moduleRepeatM:" + i + ":chk");
	                    if (chkbx != null) {
	                        if (chkbx.checked) {
	                            editArray[k++] = moduleName + ":" + "1";
	                        } else {
	                            editArray[k++] = moduleName + ":" + "0";
	                        }
	                    
	                }

	            }
	      

	    }
	}
	console.log("%o",editArray);

}

var tabs = dijit.byId("view:_id1:_id2:MainTabContainer");
dojo.connect(tabs,"_transition", function(newPage, oldPage){
    console.log("I was showing: ", oldPage || "nothing");
    console.log("I am now showing: ", newPage);
});
