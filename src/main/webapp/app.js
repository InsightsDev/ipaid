var app = angular.module('app', ['ngRoute']);
app.config(['$routeProvider', function ($routeProvider) {
	$routeProvider
	.when('/login', {
		templateUrl: 'login/login.html',
		controller: 'loginController'
	})
	.when('/home', {
		templateUrl: 'home/home.html',
		controller: 'homeController'
	})
	.when('/userProfile', {
		templateUrl: 'userProfile/userProfile.html',
		controller: 'userProfileController'
	})
	.when('/email', {
		templateUrl: 'email/email.html',
		controller: 'emailController'
	})
	.otherwise({
		redirectTo: '/home'
	})
}])
.directive('fileModel', ['$parse', function ($parse) {
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            var model = $parse(attrs.fileModel);
            var modelSetter = model.assign;

            element.bind('change', function(){
                scope.$apply(function(){
                	modelSetter(scope, element[0].files[0]);
                });
            });
        }
    };
}])
.service('appService', ['$http', function ($http){
	this.getUser = $http({
	  					method: 'GET',
	  					url: 'user/'
					});
	this.getCity = $http({
					   	method: 'GET',
					   	url: 'Jsons/city.json'
					});
	this.getSearchResult = function(productName) {
					return $http({
						method: 'GET',
						url: 'purchase/fetchProductCost',
						params: {productName: productName}
					});
	}
	 this.uploadFileToUrl = function(file, userName, uploadUrl){
	        var fd = new FormData();
	        fd.append('file', file);
	        fd.append('user', userName);
	        $http.post(uploadUrl, fd, {
	            transformRequest: angular.identity,
	            headers: {'Content-Type': undefined}
	        })
	        .success(function(){
	        })
	        .error(function(){
	        });
	    }
}])
.controller('loginController',['$scope', 'appService', function ($scope, appService){
	appService.getUser.then(function(data){

		$scope.users = data.data.users;
		console.log($scope.users);
	})
	$scope.validateUser = function (){
		var userId = $scope.userId;
		var password = $scope.password;
		var user = {"userName": userId, "password": password};
		var users =  $scope.users
		for(var i=0; i<users.length; i++){
			if(users[i].userName == userId && users[i].password == password){
				console.log("success");
				window.location.href = '#home';
				$rootScope.currentUser = users[i];
				break;
			}else{
				$scope.userId = undefined;
				$scope.password = undefined;
			}
		}

	}
}])
.controller('homeController',['$scope', 'appService', function ($scope, appService){
	$scope.image = $scope.image;
	$scope.bill = null;
	$scope.userName = "testuser1@test.com";
	$scope.displayDetails = function (){
		productName = $scope.productName
		appService.getSearchResult(productName).then(function(data){
			$scope.serachData = data.data.users;
		})
	}
	$scope.upload = function (element){
		var file = element.files[0];
		var userName = $scope.userName;
		var uploadUrl = 'purchase/upload';
		appService.uploadFileToUrl(file, userName, uploadUrl);
	}
	$scope.clearData = function () {
		$scope.serachData = null;
	}
	
	appService.getUser.then(function(data) {
		console.log(data.data);
		$scope.user = data.data;
	});
	
}])
.controller('userProfileController',['$scope', 'appService', function ($scope, appService) {
	
	appService.getUser.then(function(data) {
		
		$scope.user = data.data;
		$scope.purchases=data.data.purchases;
		console.log(data.data.purchases);
	});
	
}])
.controller('emailController',['$scope', 'appService', function ($scope, appService){
	$scope.userName = "testuser1@test.com";
	$scope.clearData = function () {
		$scope.serachData = null;
	}
	$scope.clickUpload = function(){
	    angular.element('#bill').trigger('click');
	}
	$scope.email = function () {
		  $location.path('#/email');
		};
}])