require.config({
	baseUrl : "app/js", // By default will load all scripts from
	// WebContent/app/js
	"paths" : {
		'jquery' : 'libs/jquery-1.11.2.min',
		'datatables' : 'libs/jquery.dataTables.min',
		'jquery.easing.min' : 'libs/jquery.easing.min',
		'dataTablesBootstrap' : 'libs/dataTables.bootstrap',
		'classie' : 'libs/classie',
		'cbpAnimatedHeader' : 'libs/cbpAnimatedHeader.min',
		'angular' : 'libs/angular',
		'jquery.bootstrap' : 'libs/bootstrap.min',
		'agency' : 'thirdparty/agency',
		'angular-route' : 'libs/angular-route.min',
		'app' : 'app',
		'sdDialog' : 'directives/sdDialog',
		'fileModel' : 'directives/fileModel',
		'sdDataTable' : 'directives/sdDataTable'
	},
	shim : {
		'angular' : {
			"deps" : [ 'jquery' ],
			exports : 'angular'
		},
		'datatables': {
		    deps: ['jquery']
		},
		'jquery.easing.min' : ['jquery'],
		'classie' : ['jquery'],
		'cbpAnimatedHeader' : ['jquery'], 
		'jquery.bootstrap' : [ 'jquery' ],
		'dataTablesBootstrap' : [ 'jquery.bootstrap'],
		'agency' : [ 'jquery','classie','cbpAnimatedHeader','jquery.easing.min' ],
		'angular-route' : {
			'deps' : [ 'angular' ]
		},
		'sdDialog' : [ 'jquery.bootstrap', 'app' ],
		'fileModel' : [ 'jquery.bootstrap', 'app' ],
		'sdDataTable' : [ 'jquery.bootstrap', 'app' ]
	}
})

require([ 'jquery','jquery.bootstrap','datatables','dataTablesBootstrap', 'app', 'agency',
		'services/MusicApplicationService', 'sdDialog', 'fileModel','sdDataTable',
		'controller/MusicApplicationController' ], function(jquery,bootstrap,datatables,dataTablesBootstrap,
		app) {

	app.init();
})
