var serverVerticle = new function() {

    var vertx = require("vertx");
    var http = require("vertx/http");
    var console = require("vertx/console");
    var example = require("./app.js");
    var eventBus = require("vertx/event_bus");

    this.start = function() {
        console.log("starting deployer server verticle");
        console.log(example.hello());

        var server = http.createHttpServer();

        var routeMatcher = new http.RouteMatcher();

        routeMatcher.get("/information", function(req) {
            eventBus.send("_infochannel", "ping", function(reply) {
                req.response.end(reply);
            });
        });

        server.requestHandler(routeMatcher).listen(8080, 'localhost');

    };

    this.stop = function() {
        console.log("Verticle stopped!");
    }

};

serverVerticle.start();

function vertxStop() {
    serverVerticle.stop();
}