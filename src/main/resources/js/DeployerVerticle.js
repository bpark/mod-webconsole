var bootVerticle = new function() {

    var vertx = require("vertx");
    var console = require("vertx/console");
    var container = require("vertx/container");

    this.start = function () {
        console.log("Verticle started");

        var config = {
            web_root: "C:/Dateien/Devel/java/projects/vertx/vertx-vdeployer/src/main/resources/static",
            index_page: "index.html",
            host: "localhost",
            port: 8081
        };

        container.deployVerticle("js/ServerVerticle.js");
        container.deployModule("io.vertx~mod-web-server~2.0.0-final", config);
        container.deployVerticle("com.github.bpark.vertx.vdeployer.InformationVerticle");


        vertx.eventBus.registerHandler("ping-address", function(message, replier) {
            replier('pong!');
            console.log('Sent back pong JavaScript!')
        });

    };

    this.stop = function() {
        console.log("Verticle stopped!");
    }
};

bootVerticle.start();

function vertxStop() {
    bootVerticle.stop();
}