'use strict';

/**
* @ngdoc directive
* @name loadDataWidget
* @restrict E
* @scope
*
* @description
*
* Loads some data
*
* @usage
* ```html
* <widget load-data-widget="{ variableName: urlOfJSONData }"></widget>
* ```
*/
angular.module('c-3poDashboards')
.directive('loadDataWidget', function($parse, $http, $mdDialog) {
    return {
        require: 'widget',
        restrict: 'A',
        link: function($scope, $element, attrs, widgetCtrl) {
            widgetCtrl.setLoading(true);
            var loadData = $parse(attrs.loadDataWidget)($scope);

            widgetCtrl.setMenu({
                icon: 'icon-more-vert',
                items: [{
                    icon: 'icon-search',
                    title: 'DASHBOARDS.WIDGETS.MENU.DETAILS',
                    click: function($event) {
                        var data = [];
                        angular.forEach(loadData, function(url, variable) {
                            data = $scope[variable];
                        });
                        $mdDialog.show({
                            controller: 'WidgetLoadDataDialogController',
                            templateUrl: 'app/dashboards/widgets/widget-load-data-dialog.tmpl.html',
                            targetEvent: $event,
                            locals: {
                                data: data
                            },
                            clickOutsideToClose: true
                        })
                        .then(function(answer) {
                            $scope.alert = 'You said the information was "' + answer + '".';
                        }, function() {
                            $scope.alert = 'You cancelled the dialog.';
                        });
                    }
                },{
                    icon: 'icon-share',
                    title: 'DASHBOARDS.WIDGETS.MENU.SHARE',
                },{
                    icon: 'icon-print',
                    title: 'DASHBOARDS.WIDGETS.MENU.PRINT',
                }]
            });

            angular.forEach(loadData, function(url, variable) {
                $http.get(url).
                success(function(data) {
                    var header = data.shift();
                    widgetCtrl.setLoading(false);
                    $scope[variable] = {
                        header: header,
                        data: data
                    };
                }).
                error(function() {
                    console.error('Could not load ' + url );
                });
            });
        }
    };
});