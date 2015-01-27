(function(){
    var app = angular.module("generalApp", []);

    app.directive("login", function(){
        return{
          restrict: "E",
           templateUrl: "login.html",
            controller: function($scope){
                $scope.title = "Login";
            }
        };
    });


})();

