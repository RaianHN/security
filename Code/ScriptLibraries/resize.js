dojo.subscribe("/dojo/resize/stop", function(inst){
	 tid = inst.targetDomNode.id ;
	var x = tid.split(":");
	var length =  x.length;	
	var tid1 =  x[length-1];	
 
	 setTimeout(function() { 

	   var tarea = document.getElementById(tid);	  
	   var height = tarea.style.height;
	   var width = tarea.style.width;
	   
	// alert(tarea.parentNode.parentNode.parentNode.parentNode.id);
	   var refreshId = tarea.parentNode.parentNode.parentNode.parentNode.parentNode.id ;	  	 
	  	 var props = new Array();
         props[0] =  tid1 ; 
     	 props[1] = height ; 
     	 props[2] = width ;      
     //	alert(refreshId);
     	 XSP.executeOnServer('view:_id1:eventHandler12', refreshId , {}, props);			
  			

	}, 300);


});

function onMouseUp1(e)
{

  var tid = e.target.id;
//alert(tid);
  var id = tid.split(":")[3].split("header")[0]+"moveable";
 
  var arr = tid.split(":");
  var len = arr.length;
  if ((arr[len-1] == "image1") || (arr[len-1] == "image2") || (arr[len-1] == "image4"))
  {
  	return;
  }
  var con = dojo.byId("view:_id1:_id17:"+id);
  var style = con.style;

			
		var props = new Array();
    	props[0] = style.left;
     	props[1] = style.top;
     	props[2] = id ;
     	props[3] = zIndex;
     
  		con.style.zIndex = props[3];        
		
		//XSP.executeOnServer('view:_id1:eventHandler3',"view:_id1:_id18:"+id, "", props);
		XSP.executeOnServer('view:_id1:eventHandler3',"", "", props);		
		setTimeout(function(){
		
		XSP.executeOnServer('view:_id1:eventHandler4',"", "", ++zIndex);
		
		}, 300);
}

// needs to be made generic---------
function discussionscroll(){
	var comp = document.getElementById('view:_id1:_id17:Ticketpane:include1:discussionPanel' );
if (comp != null){ 
	 dojox.fx.smoothScroll({ node:dojo.byId('view:_id1:_id17:Ticketpane:include1:discussionPanel'), win:window, duration:1000}).play(); 
	}
	}