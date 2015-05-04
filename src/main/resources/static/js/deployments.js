'use strict';

angular.module('VertxConsoleModule.deployments', [])

    .controller("DeploymentCtrl", function($scope, $rootScope, $http) {

        var selected;

        $http.get('/resources').then(function(deploymentResponse) {
            $scope.deployments = deploymentResponse.data;
            $scope.selected = $scope.deployments[0];
        });

        $scope.setSelected = function() {
            $scope.selected = this.deployment;
            selected = $scope.selected;
        };

        $scope.doUndeploy = function() {
            waitingDialog.show("Undeploying...");
            $http.post("/remove", {id: selected.name}).success(function(response) {
                $scope.deployments.splice(selected.index - 1, 1);
                $rootScope.$broadcast("SUCCESS_CHANNEL", "Removed " + $scope.selected.main);
                $scope.selected = $scope.deployments[0];
                waitingDialog.hide();
            }).error(function(response) {
                $rootScope.$broadcast("ERROR_CHANNEL", response.cause);
                waitingDialog.hide();
            });
        };

    })

;