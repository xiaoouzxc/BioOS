/**
 * 
 */
// 选取要修改背景图的元素
var element = $("body"); // selector为需要修改背景图的元素的选择器
 
// 设置新的背景图URL






$(document).ready(function(){
   
    var initialBackground ="";
    
    // 将初始背景图应用到目标元素上
    $("body").css("background-image", initialBackground);
});
 
// 当需要切换背景图时，调用该函数并传入新的背景图路径

function change(){
	var selector=document.getElementById("imageurl")
   var redUrl=selector.value;
   console.log(redUrl);
    $("body").css("background-image", redUrl);
}