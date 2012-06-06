(function(){
 var oldPartialRefreshGet = XSP.partialRefreshGet;
 XSP.partialRefreshGet = function( targetId, options ){
  // Convert to array
  var argsArray = Array.prototype.slice.apply( arguments );
  
  if( argsArray.length > 1 ){ argsArray[1] = argsArray[1] || {}; }  
  if( argsArray.length === 1 ){ argsArray.push( {} ); }
  
  oldPartialRefreshGet.apply( XSP, argsArray );
 };
 
 var oldPartialRefreshPost = XSP.partialRefreshPost;
 XSP.partialRefreshPost = function( targetId, options ){
  // Convert to array
  var argsArray = Array.prototype.slice.apply( arguments );
  
  if( argsArray.length > 1 ){ argsArray[1] = argsArray[1] || {}; }  
  if( argsArray.length === 1 ){ argsArray.push( {} ); }
  
  oldPartialRefreshPost.apply( XSP, argsArray );
 };
})();