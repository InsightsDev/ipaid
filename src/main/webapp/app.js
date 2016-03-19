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
	.otherwise({
		redirectTo: '/login'
	})
}])
.service('appService', ['$http', function ($http){
	this.getUser = $http({
	  					method: 'GET',
	  					url: 'Jsons/user.json'
					});
	this.getCity = $http({
					   	method: 'GET',
					   	url: 'Jsons/city.json'
					});
	this.getSearchResult = $http({
						method: 'GET',
						url: 'Jsons/search.json'
					});
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
	//$scope.userName = "jack";
	$scope.displayDetails = function (){
		appService.getSearchResult.then(function(data){
			$scope.serachData = data.data.users;
		})
	}
	$scope.clearData = function () {
		$scope.serachData = null;
	}
}])