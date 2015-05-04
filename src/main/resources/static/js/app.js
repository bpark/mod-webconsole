'use strict';

angular.module("VertxConsoleModule", [
    "VertxConsoleModule.deployments",
    "VertxConsoleModule.bus"
])

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

            $http.post("/deploy", data).success(function(response) {
                $rootScope.$broadcast("SUCCESS_CHANNEL", "Deployment successful");
                $scope.deployments.splice(0, $scope.deployments.length);
                response.forEach(function(item) {
                    $scope.deployments.push(item);
                });
                waitingDialog.hide();
            }).error(function(response) {
                $rootScope.$broadcast("ERROR_CHANNEL", response.cause);
                $("#errorDialog").modal("show");
                waitingDialog.hide();
            });

        };

        $scope.$watch('$viewContentLoaded', function() {
            jsonEditor.init("jsoneditor");
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

    .controller("TabCtrl", function($scope) {
        $scope.$watch('$viewContentLoaded', function() {
            $("#navigation-ul").find('a').click(function (e) {
                e.preventDefault();
                $(this).tab('show');
            })
        });
    })

    .service("jsonEditor", function() {

        var editor;

        var init = function(elementid) {
            var container = document.getElementById(elementid);

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

    .service("timeService", function() {

        var formattedDate = function() {
            var date = new Date();
            return date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate() + " " +  date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();
        };

        return {
            formattedDate: formattedDate
        }
    })

;