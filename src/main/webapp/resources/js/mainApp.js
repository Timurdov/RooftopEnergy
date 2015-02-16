var generalAppConfig = {
    /* When set to false a query parameter is used to pass on the auth token.
     * This might be desirable if headers don't work correctly in some
     * environments and is still secure when using https. */
    useAuthTokenHeader: true
};


var app = angular.module("generalApp", ['ngRoute', 'ngCookies', 'generalApp.services']).config(
        [ '$routeProvider', '$locationProvider', '$httpProvider', function($routeProvider, $locationProvider, $httpProvider) {


            $routeProvider.when('/login', {
                templateUrl: 'login.html',
                controller: LoginController
            });

            $routeProvider.otherwise({
                templateUrl: 'pages/home.html',
                controller: HomeController
            });

            $locationProvider.hashPrefix('!');

            /* Register error provider that shows message on failed requests or redirects to login page on
             * unauthenticated requests */
            $httpProvider.interceptors.push(function ($q, $rootScope, $location) {
                    return {
                        'responseError': function(rejection) {
                            var status = rejection.status;
                            var config = rejection.config;
                            var method = config.method;
                            var url = config.url;
                            if (status == 401) {
                                $location.path( "/login" );
                                $rootScope.loginFail(true);
                            } else {
                                $rootScope.error = method + " on " + url + " failed with status " + status;
                            }

                            return $q.reject(rejection);
                        }
                    };
                }
            );

            /* Registers auth token interceptor, auth token is either passed by header or by query parameter
             * as soon as there is an authenticated user */
            $httpProvider.interceptors.push(function ($q, $rootScope, $location) {
                    return {
                        'request': function(config) {
                            var isRestCall = config.url.indexOf('rest') == 0;
                            if (isRestCall && angular.isDefined($rootScope.authToken)) {
                                var authToken = $rootScope.authToken;
                                if (generalAppConfig.useAuthTokenHeader) {
                                    config.headers['X-Auth-Token'] = authToken;
                                } else {
                                    config.url = config.url + "?token=" + authToken;
                                }
                            }
                            return config || $q.when(config);
                        }
                    };
                }
            );

        } ]

    ).run(function($rootScope, $location, $cookieStore, UserService) {

            /* Reset error when a new view is loaded */
            $rootScope.$on('$viewContentLoaded', function() {
                delete $rootScope.error;
            });

            $rootScope.hasRole = function(role) {

                if ($rootScope.user === undefined) {
                    return false;
                }

                if ($rootScope.user.roles[role] === undefined) {
                    return false;
                }

                return $rootScope.user.roles[role];
            };

            $rootScope.logout = function() {
                delete $rootScope.user;
                delete $rootScope.authToken;
                $cookieStore.remove('authToken');
                $location.path("/login");
            };

            /* Try getting valid user from cookie or go to login page */
            var originalPath = $location.path();
            $location.path("/login");
            var authToken = $cookieStore.get('authToken');
            if (authToken !== undefined) {
                $rootScope.authToken = authToken;
                UserService.get(function(user) {
                    $rootScope.user = user;
                    $location.path(originalPath);
                });
            }

            $rootScope.initialized = true;

            $rootScope.styleSheet = {
                login_styleCSS : true,
                homeCSS : true,
                productionCSS : true,
                consumptionCSS : true,
                changePasswordCSS : true,
                pagesCSS : true,
                ecoDataCSS : true,
                scoreCSS : true,
                comparingCSS : true,
                weatherCSS : true,
                settingsCSS : true
            };

            $rootScope.switchOffCSS = function(){
                for(var index in $rootScope.styleSheet){
                    if ($rootScope.styleSheet.hasOwnProperty(index)) {
                        $rootScope.styleSheet[index] = true;
                    }
                }
            };
        });

function LoginController($scope, $rootScope, $location, $cookieStore, UserService) {

    //$scope.rememberMe = false;
    $rootScope.switchOffCSS();
    $rootScope.styleSheet.login_styleCSS = false;

    $rootScope.loginFail = function(showAlert){
        $scope.loginAlert = showAlert;
    };
    $rootScope.loginFail(false);

    $scope.login = function() {
        UserService.authenticate($.param({username: $scope.username, password: $scope.password}), function(authenticationResult) {
            var authToken = authenticationResult.token;
            $rootScope.authToken = authToken;
            if ($scope.rememberMe) {
                $cookieStore.put('authToken', authToken);
            }
            UserService.get(function(user) {
                $rootScope.user = user;
                console.log(user);
                $location.path("/");
            });
        });

    };
};

function HomeController($scope, $rootScope, $location, $cookieStore, UserSettingsDataService){

    $rootScope.switchOffCSS();
    $rootScope.styleSheet.productionCSS = false;
    $rootScope.styleSheet.pagesCSS = false;
    $rootScope.styleSheet.ecoDataCSS = false;

    UserSettingsDataService.getUserDescription(function(info){
        $rootScope.userInfo = info;
        console.log("GET user Data - " + info.userName + ", " + info.company);
        $scope.greeting = "Hello " + info.userName + "!, from company: " + info.company;
    })


}

var services = angular.module('generalApp.services', ['ngResource']);

services.factory('UserService', function($resource) {

    return $resource('rest/user/:action', {},
        {
            authenticate: {
                method: 'POST',
                params: {'action' : 'authenticate'},
                headers : {'Content-Type': 'application/x-www-form-urlencoded'}
            }
        }
    );
});

services.factory('UserSettingsDataService', function($resource) {
    return $resource('rest/boxData/:action', {},
        {
            saveNewUserInfo: {
                method: 'POST',
                params: {'action' : 'saveNewUserInfo'}
            },
            getUserDescription: {
                method: 'GET',
                params: {'action': 'getUserDescription'}
            },
            changePassword: {
                method: 'POST',
                params: {'action' : 'changePassword'}
            }
        }
    )
});

    /*app.directive("login", function(){
        return{
          restrict: "E",
           templateUrl: "login.html",
            controller: function($scope, $rootScope, $location, *//*$cookieStore,*//* UserService){
                $scope.title = "Login";

                $scope.rememberMe = false;

                $scope.login = function() {
                    UserService.authenticate($.param({username: $scope.username, password: $scope.password}), function(authenticationResult) {
                        var authToken = authenticationResult.token;
                        $rootScope.authToken = authToken;
                        if ($scope.rememberMe) {
                            $cookieStore.put('authToken', authToken);
                        }
                        UserService.get(function(user) {
                            $rootScope.user = user;
                            $location.path("/");
                        });
                    });
                };
            }
        };
    });*/





