define([ 'angular','angular-route'], function(angular) {
	'use strict';

	var app = angular.module("soundMix",['ngRoute']);
	app.init = function() {
		angular.bootstrap(document, [ 'soundMix' ]);
	}

	return app;
})