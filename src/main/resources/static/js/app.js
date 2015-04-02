'use strict';

angular.module("VertxConsoleModule", [])

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
            $http.post("/remove", {id: selected.name}).success(function(response) {
                $scope.deployments.splice(selected.index - 1, 1);
                $rootScope.$broadcast("SUCCESS_CHANNEL", "Removed " + $scope.selected.main);
                $scope.selected = $scope.deployments[0];
            }).error(function(response) {
                $rootScope.$broadcast("ERROR_CHANNEL", response.cause);
            });
        };

    })

    .controller("ModuleDialogCtrl", function($scope, $rootScope, $http, jsonEditor) {

        $scope.doAddModule = function() {

            $("#addModuleDialog").modal("hide");
            waitingDialog.show("Deploying...");

            var data = {
                type: "module",
                groupId: $scope.groupId,
                artifactId: $scope.artifactId,
                version: $scope.version,
                instances: $scope.instances,
                config: jsonEditor.getJson()
            };

            $http.post("/deploy", data).success(function(deploymentResponse) {
                waitingDialog.hide();
                $rootScope.$broadcast("SUCCESS_CHANNEL", "Deployment successful.");
            }).error(function(deploymentResponse) {
                waitingDialog.hide();
                $rootScope.$broadcast("ERROR_CHANNEL", deploymentResponse.cause);
                $("#errorDialog").modal("show");
            });

        };

        $scope.$watch('$viewContentLoaded', function() {
            jsonEditor.init();
        });

    })

    .controller("ErrorDialogCtrl", function($scope) {

        $scope.$on("ERROR_CHANNEL", function(event, args) {
            $scope.cause = args;
        })

    })

    .controller("MessageCtrl", function($scope) {

        $scope.messageType = "NONE";

        $scope.$on("ERROR_CHANNEL", function(event, args) {
            $scope.messageType = "ERROR";
            $scope.message = args;
        });

        $scope.$on("SUCCESS_CHANNEL", function(event, args) {
            $scope.messageType = "SUCCESS";
            $scope.message = args;
        })
    })

    .service("jsonEditor", function() {

        var editor;

        var init = function() {
            var container = document.getElementById('jsoneditor');

            var options = {
                "mode": "code",
                "indentation": 3,
                error: function (err) {
                    alert(err.toString());
                }
            };

            editor = new JSONEditor(container, options, {});
            return editor;
        };

        var getJson = function() {
            return editor.get();
        };

        return {
            init: init,
            getJson: getJson
        };
    })

;