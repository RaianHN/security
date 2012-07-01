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

	//clId   view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:modulediv
	//Module computed field id   view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:modulediv:moduleRepeatM:0:ccModuleP:repeatModules:0:moduleM



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
	    moduleComp = document.getElementById(clId + ":moduleRepeatM:" + i + ":ccModuleP:repeatModules:0:moduleM");
	    if (moduleComp != null) {
	        moduleName = moduleComp.innerHTML;	        
	      
	            if (moduleName != "") {
	                             
	               
	                    chkbx = document.getElementById(clId + ":moduleRepeatM:" + i + ":ccModuleP:repeatModules:0:tabVChk");
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

function savePermission(){
	var strBaseId = "view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:";
	var x = document.getElementById(strBaseId+"moduleCombo").selectedIndex;
	var y=document.getElementById(strBaseId+"moduleCombo").options;
	var profileName = y[x].text;
	var moduleName="";
	var k=0;
	var modulePArray = new Array();

	XSP.executeOnServer('view:_id1:eventNModules', "view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:modulediv:table1", 
			{onComplete: function() {
		
		var nModules = document.getElementById("view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:modulediv:inputNModules").value;
		//alert("number of modules"+nModules);
	
		//XSP.executeOnServer('view:_id1:eventNFeatuers', "", "", result);
		for(var i=0;i<nModules;i++){//for each module get the features and entities
			//ModuleNames
			//view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:modulediv:moduleRepeatM:0:ccModuleP:repeatModules:0:moduleM
			//view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:modulediv:moduleRepeatM:1:ccModuleP:repeatModules:0:moduleM
			
			//ModulePermission
			//view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:modulediv:moduleRepeatM:0:ccModuleP:repeatModules:0:tabVChk
			//view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:modulediv:moduleRepeatM:1:ccModuleP:repeatModules:0:tabVChk
			
			
			//FeatureNames
			//view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:modulediv:moduleRepeatM:1:ccModuleP:repeatModules:0:repeatFeature:0:featureName
			//view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:modulediv:moduleRepeatM:2:ccModuleP:repeatModules:0:repeatFeature:0:featureName
			
			//FeaturePermission
			//view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:modulediv:moduleRepeatM:2:ccModuleP:repeatModules:0:repeatFeature:0:featureChk
			moduleName = document.getElementById(strBaseId+"modulediv:moduleRepeatM:"+i+":ccModuleP:repeatModules:0:moduleM").innerHTML;
			modulePerm = document.getElementById(strBaseId+"modulediv:moduleRepeatM:"+i+":ccModuleP:repeatModules:0:tabVChk");
			
			XSP.executeOnServer('view:_id1:eventNFeatures', "view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:modulediv:table1", 
					{onComplete: function() {
							var nFeatures = document.getElementById("view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:modulediv:inputNFeatures").value;
							//alert("nfeatures"+nFeatures);
							var featurePArray = new Array();
						//For each feature in this module
					for(var ftrs=0;ftrs<nFeatures;ftrs++){
						featureName = document.getElementById(strBaseId+"modulediv:moduleRepeatM:"+ftrs+":ccModuleP:repeatModules:0:repeatFeature:0:featureName").innerHTML;
						featurePerm = document.getElementById(strBaseId+"modulediv:moduleRepeatM:"+ftrs+":ccModuleP:repeatModules:0:repeatFeature:0:featureChk");
						
						if(featurePerm!=null){
							if (featurePerm.checked) {
			                	 featurePArray[ftrs] = moduleName+":"+featureName + ":" + "1";
			                	 //alert("fe modulePerm "+moduleName+featureName+" "+1);
			                 } else {
			                	 featurePArray[ftrs] = moduleName+":"+featureName + ":" + "0";
			                	// alert("fe modulePerm "+moduleName+featureName+" "+0);
			              }
						}
						
					}		
					XSP.executeOnServer('view:_id1:eventFeaturePerm', "", "",featurePArray);
					console.log("%o",featurePArray);	
							
					
					}}, profileName+":"+moduleName);
			
			
			
			
			 if (modulePerm != null) {
                 if (modulePerm.checked) {
                	 modulePArray[k++] = moduleName + ":" + "1";
                	 //alert("moduleName modulePerm "+moduleName+" "+1);
                 } else {
                	 modulePArray[k++] = moduleName + ":" + "0";
                	 //alert("moduleName modulePerm "+moduleName+" "+0);
              }
			 }
	
		}
		XSP.executeOnServer('view:_id1:eventModulePerm', "", "",modulePArray);
		console.log("%o",modulePArray);	
		

	}}, profileName);//Get number of modules, inside on complete of getNumber of modules
	
		
}


function saveFeaturePermission(){
	var moduleName = "";
	
	//modulePerm = document.getElementById(strBaseId+"modulediv:moduleRepeatM:"+i+":ccModuleP:repeatModules:0:tabVChk");
	
	XSP.executeOnServer('view:_id1:eventNFeatures', "view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:modulediv:table1", 
			{onComplete: function() {
					var nFeatures = document.getElementById("view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:modulediv:inputNFeatures").value;
					//alert("nfeatures"+nFeatures);
					var featurePArray = new Array();
				//For each feature in this module
					nFeatures = 6;
				var ftrs=0;
			for(ftrs=0;ftrs<nFeatures;ftrs++){
				moduleName = document.getElementById(strBaseId+"modulediv:moduleRepeatM:"+ftrs+":ccModuleP:repeatModules:0:moduleM").innerHTML;
				
				for(var ftrs2=0;ftrs2<nFeatures;ftrs2++){
					featureName = document.getElementById(strBaseId+"modulediv:moduleRepeatM:"+ftrs+":ccModuleP:repeatModules:0:repeatFeature:"+ftrs2+":featureName").innerHTML;
					featurePerm = document.getElementById(strBaseId+"modulediv:moduleRepeatM:"+ftrs+":ccModuleP:repeatModules:0:repeatFeature:"+ftrs2+":featureChk");
					if(featurePerm==null){
						break;
					}
					if(featurePerm!=null){
						if (featurePerm.checked) {
		                	 featurePArray[ftrs] = moduleName+":"+featureName + ":" + "1";
		                	// alert("fe modulePerm "+moduleName+featureName+" "+1);
		                 } else {
		                	 featurePArray[ftrs] = moduleName+":"+featureName + ":" + "0";
		                	// alert("fe modulePerm "+moduleName+featureName+" "+0);
		              }
					}
				}
				
			}		
			XSP.executeOnServer('view:_id1:eventFeaturePerm', "", "",featurePArray);
			console.log("%o",featurePArray);	
					
			
			}}, profileName+":"+moduleName);
	
	
}


function saveModulePermissions(){
	var profileName = getProfileName;
	var modulePArray = new Array();
	var strBaseId = "view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:";
	//Get the profile name	
	var nModulesStr = document.getElementById("view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:modulediv:inputNModules").value
	//Get number of modules
	var nModules = getModuleNumber(nModulesStr);
	var k=0;
	var moduleName = "";
	var modulePerm = null;
	for(var i=0;i<nModules;i++){
		moduleName = document.getElementById(strBaseId+"modulediv:moduleRepeatM:"+i+":ccModuleP:repeatModules:0:moduleM").innerHTML;
		modulePerm = document.getElementById(strBaseId+"modulediv:moduleRepeatM:"+i+":ccModuleP:repeatModules:0:tabVChk");
		
		 if (modulePerm != null) {
             if (modulePerm.checked) {
            	 modulePArray[k++] = moduleName + ":" + "1";
            	// alert("moduleName modulePerm "+moduleName+" "+1);
             } else {
            	 modulePArray[k++] = moduleName + ":" + "0";
            	 //alert("moduleName modulePerm "+moduleName+" "+0);
          }
		 }
		
	}
	XSP.executeOnServer('view:_id1:eventModulePerm', "", "",modulePArray);
	console.log("%o",modulePArray);	
}


function saveFeaturePermissions(){
	var profileName = getProfileName;
	var featurePArray = new Array();
	var strBaseId = "view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:";
	var nModulesStr = document.getElementById("view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:modulediv:inputNModules").value
	//Get number of modules
	var nModules = getModuleNumber(nModulesStr);
	var nFeatures=0;
	var moduleName="";
	var featureName = "";
	var featurePerm = null;
	var ftrs=0;
	for(var m=0;m<nModules;m++){
		moduleName = document.getElementById(strBaseId+"modulediv:moduleRepeatM:"+m+":ccModuleP:repeatModules:0:moduleM").innerHTML;
		nFeatures = getFeatureNumber(moduleName);
		for(var f=0;f<nFeatures;f++){
			featureName = document.getElementById(strBaseId+"modulediv:moduleRepeatM:"+m+":ccModuleP:repeatModules:0:repeatFeature:"+f+":featureName").innerHTML;
			featurePerm = document.getElementById(strBaseId+"modulediv:moduleRepeatM:"+m+":ccModuleP:repeatModules:0:repeatFeature:"+f+":featureChk");
			if(featurePerm==null){
				break;
			}
			if(featurePerm!=null){
				if (featurePerm.checked) {
                	 featurePArray[ftrs++] = moduleName+":"+featureName + ":" + "1";
                //	 alert("fe modulePerm "+moduleName+featureName+" "+1);
                 } else {
                	 featurePArray[ftrs++] = moduleName+":"+featureName + ":" + "0";
                	// alert("fe modulePerm "+moduleName+featureName+" "+0);
              }
			}
		}
	}
	XSP.executeOnServer('view:_id1:eventFeaturePerm', "", "",featurePArray);
	console.log("%o",featurePArray);		
}


function saveEntityPermissions(){
	var profileName = getProfileName;
	var entityPArray = new Array();
	var strBaseId = "view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:";
	var nModulesStr = document.getElementById("view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:modulediv:inputNModules").value
	//Get number of modules
	var nModules = getModuleNumber(nModulesStr);
	var nEntities=0;
	var moduleName="";
	var entityName = "";
	var entityPerm = null;
	var ets=0;
	/*
	 * Ids
	view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:modulediv:moduleRepeatM:1:ccModuleP:repeatModules:0:repeatEntity:1:entityName
view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:modulediv:moduleRepeatM:1:ccModuleP:repeatModules:0:repeatEntity:1:createChk
view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:modulediv:moduleRepeatM:1:ccModuleP:repeatModules:0:repeatEntity:1:readChk
view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:modulediv:moduleRepeatM:1:ccModuleP:repeatModules:0:repeatEntity:1:updateChk
view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:modulediv:moduleRepeatM:1:ccModuleP:repeatModules:0:repeatEntity:1:deleteChk
	*/
	for(var m=0;m<nModules;m++){
		moduleName = document.getElementById(strBaseId+"modulediv:moduleRepeatM:"+m+":ccModuleP:repeatModules:0:moduleM").innerHTML;
		nEntities = getEntityNumber(moduleName);
		var temp="";
		for(var e=0;e<nEntities;e++){
			//view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:modulediv:moduleRepeatM:0:ccModuleP:repeatModules:0:repeatEntity:0:entityName
			//alert("mdoule"+moduleName+" "+m+" "+e);
			//alert(strBaseId+"modulediv:moduleRepeatM:"+m+":ccModuleP:repeatModules:0:repeatFeature:"+e+":entityName");
			entityName = document.getElementById(strBaseId+"modulediv:moduleRepeatM:"+m+":ccModuleP:repeatModules:0:repeatEntity:"+e+":entityName").innerHTML;
			entityC = document.getElementById(strBaseId+"modulediv:moduleRepeatM:"+m+":ccModuleP:repeatModules:0:repeatEntity:"+e+":createChk");
			entityR= document.getElementById(strBaseId+"modulediv:moduleRepeatM:"+m+":ccModuleP:repeatModules:0:repeatEntity:"+e+":readChk");
			entityU= document.getElementById(strBaseId+"modulediv:moduleRepeatM:"+m+":ccModuleP:repeatModules:0:repeatEntity:"+e+":updateChk");
			entityD= document.getElementById(strBaseId+"modulediv:moduleRepeatM:"+m+":ccModuleP:repeatModules:0:repeatEntity:"+e+":deleteChk");
			
		
					
					 if (entityC.checked) {
					        temp = entityName + ":"+"1";
					    } else {
					        temp = entityName + ":"+"0";
					    }
					    
					    if (entityR.checked) {
					        temp = temp  + "1";
					    } else {
					        temp = temp + "0";
					    }
					    
					     if (entityU.checked) {
					        temp = temp  + "1";
					    } else {
					        temp = temp + "0";
					    }
					    
					     if (entityD.checked) {
					        temp = temp  + "1";
					    } else {
					        temp = temp + "0";
					    }
					
					
                	 entityPArray[ets++] = moduleName+":"+temp;
               
			
		}
	}
	XSP.executeOnServer('view:_id1:eventEntityPerm', "", "",entityPArray);
	console.log("%o",entityPArray);		
}

function saveFieldPermissions(){
	var profileName = getProfileName;
	var fieldPArray = new Array();
	var strBaseId = "view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:";
	var nModulesStr = document.getElementById("view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:modulediv:inputNModules").value
	//Get number of modules
	var nModules = getModuleNumber(nModulesStr);
	var nEntities = 0;
	var moduleName="";
	var entityName = "";
	var nFields = 0;
	var ets=0;
	/*
	 * Ids
	view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:modulediv:moduleRepeatM:1:ccModuleP:repeatModules:0:repeatEntity:1:entityName
view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:modulediv:moduleRepeatM:1:ccModuleP:repeatModules:0:repeatEntity:1:createChk
view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:modulediv:moduleRepeatM:1:ccModuleP:repeatModules:0:repeatEntity:1:readChk
view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:modulediv:moduleRepeatM:1:ccModuleP:repeatModules:0:repeatEntity:1:updateChk
view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:modulediv:moduleRepeatM:1:ccModuleP:repeatModules:0:repeatEntity:1:deleteChk
	*/
	for(var m=0;m<nModules;m++){
		moduleName = document.getElementById(strBaseId+"modulediv:moduleRepeatM:"+m+":ccModuleP:repeatModules:0:moduleM").innerHTML;
		nEntities = getEntityNumber(moduleName);
		var temp="";
		for(var e=0;e<nEntities;e++){
			entityName = document.getElementById(strBaseId+"modulediv:moduleRepeatM:"+m+":ccModuleP:repeatModules:0:repeatEntity:"+e+":entityName").innerHTML;
			nFields = getFieldNumber(moduleName,entityName);
				var fieldName = "";
				var fieldV = "";
				var fieldU = "";
				for(f=0;f<nFields;f++){
					fieldName = document.getElementById(strBaseId+"modulediv:moduleRepeatM:"+m+":ccModuleP:repeatModules:0:repeatEntity:0:repeatFields:"+f+":computedField1").innerHTML;
					fieldV = document.getElementById(strBaseId+"modulediv:moduleRepeatM:"+m+":ccModuleP:repeatModules:0:repeatEntity:0:repeatFields:"+f+":fieldVChk");
					fieldU = document.getElementById(strBaseId+"modulediv:moduleRepeatM:"+m+":ccModuleP:repeatModules:0:repeatEntity:0:repeatFields:"+f+":fieldUChk");
			
				
				
					 	if (fieldV.checked) {
					        temp = entityName+":"+fieldName + ":"+"1";
					    } else {
					        temp =entityName+":"+fieldName + ":"+"0";
					    }
					    
					    if (fieldU.checked) {
					        temp = temp  + "0";
					    } else {
					        temp = temp + "1";
					    }

                	 fieldPArray[ets++] = moduleName+":"+temp;
				}
			
		}
	}
	XSP.executeOnServer('view:_id1:eventFieldPerm', "", "",fieldPArray);
	console.log("%o",fieldPArray);		
}


function savePermissions(){
	saveModulePermissions();
	saveFeaturePermissions();
	saveEntityPermissions();
	saveFieldPermissions();
	
}




function getModuleNumber(strValue){
	//alert(strValue);
	var nModules = strValue.split(":");
	if(nModules!=null){
		return nModules[1];
	}
	return null;
}
function getProfileName(){
	var strBaseId = "view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:";
	var x = document.getElementById(strBaseId+"moduleCombo").selectedIndex;
	var y=document.getElementById(strBaseId+"moduleCombo").options;
	return y[x].text;
}

function getFeatureNumber(moduleName){
	var ele = document.getElementById("view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:modulediv:inputNFeatures");
	if(ele==null){
		alert("no features found for"+moduleName);
		return 0;
	}
	var fArray = new Array();
	var features = new Array();
	fArray = ele.value.split(",");
	for(var i=0;i<fArray.length;i++){
		features = fArray[i].split(":");
		if(features[0]==moduleName){
			return features[1];
		}
		
	}
	return 0;
}

function getEntityNumber(moduleName){
	var ele = document.getElementById("view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:modulediv:inputNEntities");
	if(ele==null){
		alert("no features found for"+moduleName);
		return 0;
	}
	var eArray = new Array();
	var entities = new Array();
	eArray = ele.value.split(",");
	
	for(var i=0;i<eArray.length;i++){
		entities = eArray[i].split(":");
		if(entities[0]==moduleName){
			return entities[1];
		}
		
	}
	return 0;
}
 

function getCheckedValue(ele){
	
}

function getFieldNumber(moduleName,entityName){
	var ele = document.getElementById("view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:modulediv:inputNFields");
	if(ele==null){
		alert("no features found for"+moduleName);
		return 0;
	}
	var fArray = new Array();
	var flds = new Array();
	fArray = ele.value.split(",");
	
	for(var i=0;i<fArray.length;i++){
		flds = fArray[i].split(":");
		if(flds[0]==moduleName+"+"+entityName){
			return flds[1];
		}
	}
	return 0;
}


