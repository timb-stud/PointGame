/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

var xmlHttp = getXmlHttp();

function getXmlHttp(){
    try {
        // Mozilla, Opera, Safari sowie Internet Explorer (ab v7)
        xmlHttp = new XMLHttpRequest();
    } catch(e) {
        try {
            // MS Internet Explorer (ab v6)
            xmlHttp  = new ActiveXObject("Microsoft.XMLHTTP");
        } catch(e) {
            try {
                // MS Internet Explorer (ab v5)
                xmlHttp  = new ActiveXObject("Msxml2.XMLHTTP");
            } catch(e) {
                xmlHttp  = null;
            }
        }
    }
    return xmlHttp;
}

function sendRequest(){
    if (xmlHttp) {
        xmlHttp.open('POST', 'PointGame.do', true);
        xmlHttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        xmlHttp.onreadystatechange = handleRequest;
        xmlHttp.send("px=100&py=200");
    }
}

function handleRequest(){
    if(xmlHttp.readyState == 4){
        var resp = xmlHttp.responseText;
        var px = resp.substr(3, 3);
        alert(px);
    }
}


function jqHandler(data){
    alert(data.px + " | " + data.py);
}

function jqTest(){
    var url = "PointGame.do";
    var data="px=100&py=200";
    $.getJSON(url, data, jqHandler);
}