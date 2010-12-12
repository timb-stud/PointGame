
var ctx;
var url = "PointGame.do";
var playGround = {
    w:0,
    h:0
};
var player = {
    w:20,
    h:20,
    x:0,
    y:0,
    draw: function(){
        ctx.beginPath();
        ctx.rect(this.x, this.y, this.w, this.h);
        ctx.closePath();
        ctx.fill();
    }
}

jQuery(document).ready(function(){
    init();
})

function init(){
    var data = "";
    $.get(url, data, requestHandler, "text");
    var canvas = document.getElementById("pointGameCanvas");
    ctx = canvas.getContext("2d");
    playGround.w = canvas.width;
    playGround.h = canvas.height;
    $("#pointGameCanvas").click(onClick);
    //setInterval(mainLoop, 100);
}

function onClick(evt){
    var x = evt.pageX - this.offsetLeft;
    var y = evt.pageY - this.offsetTop;
    $("#status").html(x + ", " + y);
    player.x = x - player.w/2;
    player.y = y - player.w/2;
    var data="px=" + player.x + "&py=" + player.y;
    $.post(url, data, requestHandler, "json");
}

function mainLoop(){
    ctx.clearRect(0,0, playGround.w, playGround.h);
    player.draw();
    $("#status").html(player.x + ", " + player.y);
}

function requestHandler(data){
    alert(data);
    //player.x = data.px;
    //player.y = data.py;
}

