//$(function(){
//$('#logForm').submit(function(e){
//
//    var login = $('#login1').val();
//    var password = $('#password1').val();
//    if((login != 'rooftop') || (password != 'energy')){
//        e.preventDefault();
//        $('#wrongInf').text("Wrong login or password").css({'color':'#ee0000','font-size':'20px'});
//        $('.inPut').css({'border-color':'#ff0000'});
//           } else {
//        e.preventDefault();
//        window.location = "loggedPage.html";
//    }


   //var str = form.serialize();
   //$.ajax({
   //     url:'http://localhost:8080/loggedPage.html',
   //     type: 'POST',
   //     data: str
   // })
   //
   //     .done(function(msg){
   //         if(msg === "OK"){
   //             e.preventDefault();
   //             $('#wrongInf').text("Wrong login or password").css({'color':'#ee0000','font-size':'20px'});
   //             $('#login').css({'border-color':'#ff0000'});
   //             $('#password').css({'border-color':'#ff0000'});
   //         }
   //     });
//});
//});


$(function(){
    //$('#logForm').submit(function(e){
    //    var name = $('#login1').val();
    //    var password = $('#password1').val();
    //
    //    $.ajax({
    //        type: 'post',
    //        url: '/rest/user/authenticate',
    //        crossDomain: true,
    //        data: {'username': name, 'password': password},
    //        error: function (data) {
    //            $('#wrongInf').html(data.responseText).css({'color':'#ff0000'});
    //        },
    //        statusCode: {
    //            // HTTP 307 - redirect
    //            307: function (data) {
    //                document.location.href = data.responseText;
    //                $('#wrongInf').html("");
    //
    //            }
    //        }
    //    });
    //    return false;
    //});
   var loginState = "data";
        var query = window.location.search.substring(1);
        var vars = query.split('&');
        for (var i = 0; i < vars.length; i++) {
            var pair = vars[i].split('=');
            if (decodeURIComponent(pair[0]) == loginState) {
                var wrongData = "<p>Incorrect login or password!</p>";
                $("#wrongInf").html(wrongData).css({'visibility':'visible'});
                //$("#login1, #password1").css({'border':'1px solid #ff0000'});
            }
        }



});


