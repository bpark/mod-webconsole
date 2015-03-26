'use strict';

angular.module("VertxConsoleModule", [])
    .controller("DeploymentCtrl", function($scope, $http) {

        var selected;

        $http.get('/deployments').then(function(deploymentResponse) {
            $scope.deployments = deploymentResponse.data;
            $scope.selected = $scope.deployments[0];
        });

        $scope.setSelected = function() {
            $scope.selected = this.deployment;
            selected = $scope.selected;
            //alert($scope.selected.main);
        };

        $scope.doUndeploy = function() {
            $http.post("/undeploy", {id: selected.name}).then(function(undeploymentResponse) {
                $scope.deployments.splice(selected.index - 1, 1);
                $scope.selected = $scope.deployments[0];
            });
        };

        $scope.doRedeploy = function() {
            $http.post("/redeploy", {id: selected.name}).then(function(redeploymentResponse) {
            });
        };
    })

    .controller("ModuleDialogCtrl", function($scope, $http) {

        $scope.doAddModule = function() {

            var data = {
                type: "module",
                groupId: $scope.groupId,
                artifactId: $scope.artifactId,
                version: $scope.version,
                instances: $scope.instances
            };

            $http.post("/deploy", data).then(function(deployResponse) {

            });
        };
    })

;