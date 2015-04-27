require.config({
	baseUrl : "app/js", // By default will load all scripts from
	// WebContent/app/js
	"paths" : {
		'jquery' : 'libs/jquery-1.11.2.min',
		'jqueryTable' : 'libs/jquery.dataTables.min',
		'jquery.easing.min' : 'libs/jquery.easing.min',
		'dataTablesBootstrap' : 'libs/dataTables.bootstrap',
		'classie' : 'libs/classie',
		'cbpAnimatedHeader' : 'libs/cbpAnimatedHeader.min',
		'angular' : 'libs/angular',
		'bootstrap' : 'libs/bootstrap.min',
		'agency' : 'thirdparty/agency',
		'angular-route' : 'libs/angular-route.min',
		'app' : 'app',
		'sdDialog' : 'directives/sdDialog',
		'fileModel' : 'directives/fileModel'
	},
	shim : {
		'angular' : {
			"deps" : [ 'jquery' ],
			exports : 'angular'
		},
		'jqueryTable' : [ 'jquery' ],
		'jquery.easing.min' : ['jquery'],
		'classie' : ['jquery'],
		'cbpAnimatedHeader' : ['jquery'], 
		'bootstrap' : [ 'jquery' ],
		'dataTablesBootstrap' : [ 'bootstrap' ],
		'agency' : [ 'jquery','classie','cbpAnimatedHeader','jquery.easing.min' ],
		'angular-route' : {
			'deps' : [ 'angular' ]
		},
		'sdDialog' : [ 'bootstrap', 'app' ],
		'fileModel' : [ 'bootstrap', 'app' ]
	}
})

require([ 'jquery', 'jqueryTable', 'app', 'agency',
		'services/MusicApplicationService', 'sdDialog', 'fileModel',
		'controller/MusicApplicationController' ], function(jquery,
		jqueryTable, app) {

	app.init();
})
