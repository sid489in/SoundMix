define([ 'angular' ], function(angular) {

	var soundMix = angular.module('soundMix');

	soundMix.directive('sdDialog', function() {
		return {
			restrict : 'E',
			scope : true,
			replace : true,
			transclude : true,
			templateUrl : 'app/js/directives/dialog.html',
			link : function postLink(scope, elem, attrs) {
				scope.title = attrs.title;

				scope.$watch(attrs.visible, function(val) {
					if (val == true) {
						$(elem).modal('show');
					} else {
						$(elem).modal('hide');
					}
				});
				
				elem.on('hidden.bs.modal', function () {
				    scope.parent.closeMusicDialog();
				})

			}
		}
	});
});