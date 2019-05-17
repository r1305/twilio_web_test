$(function(){
    $("#btn_send").on("click",function(){
        //openWsp();
        openWsp();
    });
});

function openWsp(){
    var win = window.open("https://wa.me/14155238886", '_blank');
    win.focus();
}

function sendMessage()
{
    var msg = $("#msg_input").val();
    var phone = $("#phone_input").val();
    if(phone==""){
        alert("Ingrese un destinatario");
        return;
    }
    
    if(msg==""){
        alert("Ingrese un mensaje");
        return;
    }
    
    
    
    var xhr = $.ajax({
        url:"SendMessage",
        method:"GET",
        data:{msg:msg,phone:phone}
    });
    
    xhr.done(function(data){
        var json = JSON.parse(data);
        var str_response = "To: "+json.to+"<br/>\n\
                            Message: "+json.message+"<br/>\n\
                            SId: "+json.sid+"<br/>\n\
                            ----------------<br/>";
        $("#response").append(str_response);
    });
}


