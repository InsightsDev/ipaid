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
	.otherwise({
		redirectTo: '/home'
	})
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
	// $scope.userName = "jack";
	$scope.image = $scope.image;
	$scope.bill = null;
	$scope.displayDetails = function (){
		appService.getSearchResult.then(function(data){
			$scope.serachData = data.data.users;
		})
	}
	$scope.upload = function (){
		console.log($scope.bill);
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