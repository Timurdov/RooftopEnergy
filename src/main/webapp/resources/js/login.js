$(function(){
   var loginState = "login";
        var query = window.location.search.substring(1);
        var vars = query.split('&');
        for (var i = 0; i < vars.length; i++) {
            var pair = vars[i].split('=');
            if (decodeURIComponent(pair[0]) == loginState) {
                var wrongData = "<p>Incorrect login or password!</p>";
                $("#wrongInf").html(wrongData).css({'visibility':'visible'});
            }
        }
});


