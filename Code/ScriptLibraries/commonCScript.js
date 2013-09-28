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


function saveModulePermissions(id){
	var profileName = getProfileName(id);
	var modulePArray = new Array();
	
	var strBaseId = id;
	
	var nModulesStr = document.getElementById(strBaseId+"modulediv:inputNModules").value
	//Get number of modules
	var nModules = getModuleNumber(nModulesStr);
	//alert("nmodules"+nModules);
	var k=0;
	var moduleName = "";
	var modulePerm = null;
	for(var i=0;i<nModules;i++){
		//alert("i"+i);
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
	XSP.executeOnServer('view:_id1:eventModulePerm', "", 
			""
			
			,modulePArray);
	console.log("%o",modulePArray);	
}


function saveFeaturePermissions(id){
	var profileName = getProfileName;
	var featurePArray = new Array();
	//var strBaseId = "view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:";
	//var strBaseId = "view:_id1:_id2:MainTabContainer:djProfile:_id76:";
	var strBaseId = id;
	var nModulesStr = document.getElementById(strBaseId+"modulediv:inputNModules").value
	//Get number of modules
	var nModules = getModuleNumber(nModulesStr);
	var nFeatures=0;
	var moduleName="";
	var featureName = "";
	var featurePerm = null;
	var ftrs=0;
	for(var m=0;m<nModules;m++){
		moduleName = document.getElementById(strBaseId+"modulediv:moduleRepeatM:"+m+":ccModuleP:repeatModules:0:moduleM").innerHTML;
		nFeatures = getFeatureNumber(moduleName,strBaseId);
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
	XSP.executeOnServer('view:_id1:eventFeaturePerm', "", 
			""
	,featurePArray);
	console.log("%o",featurePArray);		
}

function saveFeaturePermissionsUI(id){
	var profileName = getProfileName;
	var featurePArray = new Array();
	
	var strBaseId = id;
	var nFeatures = document.getElementById(strBaseId+"ccPerm:singleFN").value
	//Get number of modules
	var moduleName=document.getElementById(id+"ccPerm:moduleName").value;
	var featureName = "";
	var featurePerm = null;
	var ftrs=0;
		for(var f=0;f<nFeatures;f++){
			featureName = document.getElementById(strBaseId+"ccPerm:ccViewPerm:ccViewAction:repeatF:"+f+":computedField1").innerHTML;
			featurePerm = document.getElementById(strBaseId+"ccPerm:ccViewPerm:ccViewAction:repeatF:"+f+":checkBox1");
			if(featurePerm==null){
				break;
			}
			if(featurePerm!=null){
				if (featurePerm.checked) {
                	 featurePArray[ftrs++] = moduleName+":"+featureName + ":" + "1";
                 } else {
                	 featurePArray[ftrs++] = moduleName+":"+featureName + ":" + "0";
              }
			}
		}
	
	XSP.executeOnServer('view:_id1:eventFeaturePerm', "", 
			""
	,featurePArray);
	console.log("%o",featurePArray);		
	
}
function saveEntityPermissions(id){
	var profileName = getProfileName;
	var entityPArray = new Array();

	
	var strBaseId = id;
	var nModulesStr = document.getElementById(strBaseId+"modulediv:inputNModules").value
	//Get number of modules
	var nModules = getModuleNumber(nModulesStr);
	var nEntities=0;
	var moduleName="";
	var entityName = "";
	var entityPerm = null;
	var ets=0;

	for(var m=0;m<nModules;m++){
		moduleName = document.getElementById(strBaseId+"modulediv:moduleRepeatM:"+m+":ccModuleP:repeatModules:0:moduleM").innerHTML;
		nEntities = getEntityNumber(moduleName,strBaseId);
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
	XSP.executeOnServer('view:_id1:eventEntityPerm', "", 
			""
			,entityPArray);
	console.log("%o",entityPArray);		
}

function saveGroupPermissions(id){
	var profileName = getProfileName;
	var groupPArray = new Array();

	

	//view:_id1:_id205:dync:profile_page:ccPerm:ccViewPerm:ccViewAction:repeat1:0:checkBox2
	//view:_id1:_id205:dync:profile_page:ccPerm:ccViewPerm:ccViewAction:repeat1:1:checkBox2
	//view:_id1:_id205:dync:profile_page:ccPerm:ccViewPerm:ccViewAction:repeat1:2:checkBox2
	
	//view:_id1:_id205:dync:profile_page:ccPerm:ccViewPerm:ccViewAction:repeat1:0:computedField2
	//view:_id1:_id205:dync:profile_page:ccPerm:ccViewPerm:ccViewAction:repeat1:1:computedField2
	//view:_id1:_id205:dync:profile_page:ccPerm:inputNGroups
	var strBaseId = id;
	var nGroupStr = document.getElementById(strBaseId+"ccPerm:inputNGroups").value
	//Get number of modules
	//var nGroups = getGroupNumber(nGroupStr);
	var nGroups = nGroupStr;
	var groupV;
	var moduleName="";
	var groupName = "";
	var groupPerm = null;
	var grs=0;
	var groupNameEle = null;


		moduleName = document.getElementById(id+"ccPerm:moduleName").value;
		
		var temp="";
		for(var e=0;e<nGroups;e++){
		
			groupNameEle = document.getElementById(strBaseId+"ccPerm:ccViewPerm:ccViewAction:repeat1:"+e+":computedField2");
			
			if(groupNameEle==null){
				
				break;
			}
			groupName = groupNameEle.innerHTML;
			//groupName = document.getElementById(strBaseId+"modulediv:moduleRepeatM:"+m+":ccModuleP:repeatModules:0:repeatEntity:"+e+":entityName").innerHTML;
			//groupV = document.getElementById(strBaseId+"modulediv:moduleRepeatM:"+m+":ccModuleP:repeatModules:0:repeatEntity:"+e+":createChk");
			groupV = document.getElementById(strBaseId+"ccPerm:ccViewPerm:ccViewAction:repeat1:"+e+":checkBox2");
		
					
					 if (groupV.checked) {
					        temp = groupName + ":"+"1";
					    } else {
					        temp = groupName + ":"+"0";
					    }
					    
					   
					
					
                	 groupPArray[grs++] = moduleName+":"+temp;
               
			
		}
	
	XSP.executeOnServer('view:_id1:eventGroupPerm', "", 
			""
			,groupPArray);
	console.log("%o",groupPArray);		
}


function saveFieldPermissions(id){
	var profileName = getProfileName;
	var fieldPArray = new Array();
	//var strBaseId = "view:_id1:_id2:MainTabContainer:Profile:modulePermdiv:";
	//var strBaseId = "view:_id1:_id2:MainTabContainer:djProfile:_id76:";
	var strBaseId = id;
	var nModulesStr = document.getElementById(strBaseId+"modulediv:inputNModules").value
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
		nEntities = getEntityNumber(moduleName,strBaseId);
		var temp="";
		for(var e=0;e<nEntities;e++){
			entityName = document.getElementById(strBaseId+"modulediv:moduleRepeatM:"+m+":ccModuleP:repeatModules:0:repeatEntity:"+e+":entityName").innerHTML;
			nFields = getFieldNumber(moduleName,entityName,strBaseId);
				var fieldName = "";
				var fieldV = "";
				var fieldU = "";
				for(f=0;f<nFields;f++){
					fieldName = document.getElementById(strBaseId+"modulediv:moduleRepeatM:"+m+":ccModuleP:repeatModules:0:repeatEntity:"+e+":repeatFields:"+f+":computedField1").innerHTML;
					fieldV = document.getElementById(strBaseId+"modulediv:moduleRepeatM:"+m+":ccModuleP:repeatModules:0:repeatEntity:"+e+":repeatFields:"+f+":fieldVChk");
					fieldU = document.getElementById(strBaseId+"modulediv:moduleRepeatM:"+m+":ccModuleP:repeatModules:0:repeatEntity:"+e+":repeatFields:"+f+":fieldUChk");
			
				
				
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
	XSP.executeOnServer('view:_id1:eventFieldPerm', "",
			{onComplete: function() { hideWait()}, onStart:function(){showWait()}}
			,fieldPArray);
	console.log("%o",fieldPArray);		
}

function saveGroupActionPerm(id){
	var profileName = getProfileName;
	var actionPArray = new Array();

	var strBaseId = id;
	
	//Get number of modules

	var nGroups = 0;
	var nActions = 0;
	
	var groupName = "";
	var moduleName = "";
	var ets=0;

	moduleName = document.getElementById(id+"ccPerm:moduleName").value;
	
		nGroups = document.getElementById(strBaseId+"ccPerm:inputNGroups").value
		var temp="";
		for(var e=0;e<nGroups;e++){
			//view:_id1:_id205:dync:profile_page:ccPerm:ccViewPerm:ccViewAction:repeat1:0:computedField2
			var groupNameEle = document.getElementById(strBaseId+"ccPerm:ccViewPerm:ccViewAction:repeat1:"+e+":computedField2");
			if(groupNameEle==null){
				
				break;
			}
			groupName = groupNameEle.innerHTML;
			
			nActions = getActionNumber(moduleName,groupName,strBaseId);
				var actionName = "";
				var actionV = "";
				
				for(f=0;f<nActions;f++){
					//
					//view:_id1:_id205:dync:profile_page:ccPerm:ccViewPerm:ccViewAction:repeat1:0:repeat2:0:computedField3
					//view:_id1:_id205:dync:profile_page:ccPerm:ccViewPerm:ccViewAction:repeat1:0:repeat2:1:computedField3
					actionName = document.getElementById(strBaseId+"ccPerm:ccViewPerm:ccViewAction:repeat1:"+e+":repeat2:"+f+":computedField3").innerHTML;
					actionV = document.getElementById(strBaseId+"ccPerm:ccViewPerm:ccViewAction:repeat1:"+e+":repeat2:"+f+":checkBox3");
					
			
				
				
					 	if (actionV.checked) {
					        temp = groupName+":"+actionName + ":"+"1";
					    } else {
					        temp =groupName+":"+actionName + ":"+"0";
					    }
					    
					   

                	 actionPArray[ets++] = moduleName+":"+temp;
				}
			
		}
	
	XSP.executeOnServer('view:_id1:eventGroupActionPerm', "",
			{onComplete: function() { hideWait()}, onStart:function(){showWait()}}
			,actionPArray);
	console.log("%o", actionPArray);		
		
}


function getActionNumber(moduleName,groupName,strBaseId){
	var ele = document.getElementById(strBaseId+"ccPerm:inputNActions");
	if(ele==null){
		alert("no view actions found for"+moduleName);
		return 0;
	}
	var eArray = new Array();
	var entities = new Array();
	eArray = ele.value.split(",");
	
	for(var i=0;i<eArray.length;i++){
		entities = eArray[i].split(":");
		if(entities[0]==moduleName+"+"+groupName){
			return entities[1];
		}
	}
	return 0;
}

function savePermissions(id){
	var newid = id.split("modulediv");
	//alert(newid[0]);
	saveModulePermissions(newid[0]);
	saveFeaturePermissions(newid[0]);
	saveEntityPermissions(newid[0]);
	saveFieldPermissions(newid[0]);
	
}




function getModuleNumber(strValue){
	//alert(strValue);
	var nModules = strValue.split(":");
	if(nModules!=null){
		return nModules[1];
	}
	return null;
}
function getProfileName(id){
	//var strBaseId = "view:_id1:_id2:MainTabContainer:djProfile:_id76:";
	var strBaseId = id;
	var x = document.getElementById(strBaseId+"moduleCombo").selectedIndex;
	var y=document.getElementById(strBaseId+"moduleCombo").options;
	return y[x].text;
}

function getProfileNameUI(id){
	//view:_id1:_id205:dync:profile_page:_id206:moduleCombo
	//view:_id1:_id205:dync:profile_page:_id206:button1
	
	var strBaseId = id;
	var x = document.getElementById(strBaseId+"moduleCombo").selectedIndex;
	var y=document.getElementById(strBaseId+"moduleCombo").options;
	return y[x].text;
}
function getSelectedValue(element){
	//Returns the value selected in the checkbox
	var x = element.selectedIndex;
	var y = element.options;
	return y[x].text;
}

function getFeatureNumber(moduleName,id){
	var ele = document.getElementById(id+"modulediv:inputNFeatures");
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

function getEntityNumber(moduleName,id){
	//var ele = document.getElementById("view:_id1:_id2:MainTabContainer:djProfile:_id76:modulediv:inputNEntities");
	var ele = document.getElementById(id+"modulediv:inputNEntities");
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

function getFieldNumber(moduleName,entityName,id){
	//var ele = document.getElementById("view:_id1:_id2:MainTabContainer:djProfile:_id76:modulediv:inputNFields");
	var ele = document.getElementById(id+"modulediv:inputNFields");
	
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

function savePermissionsUI(id){
	//New function to save the permissions
	//var newid = id.split("buttonSaveProfile");
	var newid = id.split("ccPerm");
	
	saveModulePermissionsUI(newid[0]);
	saveGroupPermissions(newid[0]);
	saveGroupActionPerm(newid[0]);
	saveFeaturePermissionsUI(newid[0])
	saveEntityPermissionsUI(newid[0]);
	saveFieldPermissionsUI(newid[0]);
	
}

function saveModulePermissionsUI(id){

	//view:_id1:_id205:dync:profile_page:ccPerm:moduleName
	//view:_id1:_id205:dync:profile_page:ccPerm:checkBox1
	//view:_id1:_id205:dync:profile_page:_id206:moduleCombo
	//view:_id1:_id205:dync:profile_page:saveProfile:moduleCombo
	//view:_id1:_id205:dync:profile_page:ccPerm:ccModuleP:checkBox2
	
	var moduleText = document.getElementById(id+"ccPerm:moduleName");
	var moduleName = moduleText.value;
	
	var profileName = getProfileNameUI(id+"saveProfile:");
	var modulePArray = new Array();
	
	var strBaseId = id;
	
	if(moduleName==null || moduleName==""){
		alert("Module name is null");
		return;
	}
	
	
	
	var modulePerm = null;
	
		modulePerm = document.getElementById(strBaseId+"ccPerm:ccModuleP:checkBoxM1");
	

	if(modulePerm==null){
		alert("Error: module permission not found")
		return;
	}
		//modulePerm = document.getElementById(strBaseId+"ccPerm:checkBox1");
		
		 if (modulePerm != null) {
             if (modulePerm.checked) {
            	 modulePArray[0] = moduleName + ":" + "1";
            	// alert("moduleName modulePerm "+moduleName+" "+1);
             } else {
            	 modulePArray[0] = moduleName + ":" + "0";
            	 //alert("moduleName modulePerm "+moduleName+" "+0);
          }
		 
		
	}
	XSP.executeOnServer('view:_id1:eventModulePerm', "", 
			""
			
			,modulePArray);
	console.log("%o",modulePArray);	
}

function saveGroupPermissionsUI(id){
	
}

function saveEntityPermissionsUI(id){
	var profileName = getProfileName;
	var entityPArray = new Array();

	var strBaseId = id;
	
	//Get number of modules

	
	var nActions = 0;
	
	var entityName = "";
	var moduleName = "";
	var ets=0;

	moduleName = document.getElementById(id+"ccPerm:moduleName").value;
	
		nActions = document.getElementById(strBaseId+"ccPerm:ccRecordPerm:inputNEActions").value
		var temp="";
			var entityNameEle = document.getElementById(strBaseId+"ccPerm:ccRecordPerm:comboBox1");
			var entityName = getSelectedValue(entityNameEle);
			
				var actionName = "";
				var actionV = "";
				var entityV = document.getElementById(strBaseId+"ccPerm:ccRecordPerm:checkBox1");
				if(entityV==null){
					return ;
				}
				if(entityV.checked){
					entityV = "1";
				}else{
					entityV = "0";
				}
				
				for(var f=0;f<nActions;f++){
//view:_id1:_id205:dync:profile_page:ccPerm:ccRecordPerm:repeat1:0:label5
					actionName = document.getElementById(strBaseId+"ccPerm:ccRecordPerm:repeat1:"+f+":label5").innerHTML;
					actionV = document.getElementById(strBaseId+"ccPerm:ccRecordPerm:repeat1:"+f+":checkBox4");
					
			
				
				
					 	if (actionV.checked) {
					        temp = entityName+":"+entityV+":"+actionName + ":"+"1";
					    } else {
					        temp = entityName+":"+entityV+":"+actionName + ":"+"0";
					    }
					    
					   

                	 entityPArray[ets++] = moduleName+":"+temp;
				}
			
		
	
	XSP.executeOnServer('view:_id1:eventEntityActionPerm', "",
			{onComplete: function() { hideWait()}, onStart:function(){showWait()}}
			,entityPArray);
	console.log("%o", entityPArray);		
}

function saveFieldPermissionsUI(id){
	var profileName = getProfileName;
	var fieldPArray = new Array();
	var strBaseId = id;
	//Get number of modules
	//view:_id1:_id205:dync:profile_page:ccPerm:ccFieldPerm:inputNFields
	var moduleName="";
	var entityName = "";
	var nFields = 0;
	var ets=0;

		var moduleText = document.getElementById(id+"ccPerm:moduleName");
		var moduleName = moduleText.value;
		
		var entityEle = document.getElementById(strBaseId+"ccPerm:ccFieldPerm:frecordType");
		var entityName = getSelectedValue(entityEle);
		
		var fieldsNele = document.getElementById(strBaseId+"ccPerm:ccFieldPerm:ccFieldTable:inputNFields");
		if(fieldsNele==null){
			return;
		}
		nFields = fieldsNele.value
		
		
		
		var temp="";
					
			
				var fieldName = "";
				var fieldV = "";
				var fieldU = "";
				for(f=0;f<nFields;f++){
					fieldName = document.getElementById(strBaseId+"ccPerm:ccFieldPerm:ccFieldTable:repeatField:"+f+":label5").innerHTML;
					fieldV = document.getElementById(strBaseId+"ccPerm:ccFieldPerm:ccFieldTable:repeatField:"+f+":checkBox4");
					fieldU = document.getElementById(strBaseId+"ccPerm:ccFieldPerm:ccFieldTable:repeatField:"+f+":fieldUChk");
			
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
			
			
	XSP.executeOnServer('view:_id1:eventFieldPerm', "",
			{onComplete: function() { hideWait()}, onStart:function(){showWait()}}
			,fieldPArray);
	console.log("%o",fieldPArray);		
}

function isError(){
	
	var formError = dojo.query('.lotusFormError');
	var globalError = dojo.query('.lotusError');
	var editError = dojo.query('.lotusInfo');
	if(formError!="" || globalError!="" || editError!=""){
		return true;
	}else{
		return false;
	}
	
	
}
