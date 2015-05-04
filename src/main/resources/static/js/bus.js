'use strict';

angular.module('VertxConsoleModule.bus', [])

    .controller("BusCtrl", function($scope, $location) {

        var eventBus;

        $scope.connectType = "CONNECT";

        $scope.connect = function() {
            eventBus.send("msg.connect", {
                channelId: $scope.channelId,
                type: "connect"
            });
            $scope.connectType = "DISCONNECT";
        };

        $scope.send = function() {
            eventBus.send("msg.send", {
                channelId: $scope.channelId,
                messageId: JSON.parse($scope.messageId)
            });
            $scope.connect();
            $scope.connectType = "DISCONNECT";
        };

        $scope.disconnect = function () {
            eventBus.send("msg.connect", {
                channelId: $scope.channelId,
                type: "disconnect"
            });
            $scope.connectType = "CONNECT";
        };

        $scope.$watch('$viewContentLoaded', function() {

            // get the eventbus
            $location.path("bridge");
            var bridgeUrl = $location.protocol() + '://'+ $location.host() +':'+  '8990/bridge';
            eventBus = new vertx.EventBus(bridgeUrl);

            // when the eventbus is ready, register a listener
            eventBus.onopen = function() {

                // register to address
                eventBus.registerHandler('msg.receive', function(message) {
                    $scope.message = message;
                    $scope.$apply();
                });
            }
        });

    })

;