
var vertx = require('vertx')
var console = require('vertx/console')

var bootVerticle = new function() {

    this.start = function () {
        console.log("Verticle started");
        vertx.eventBus.registerHandler('ping-address', function(message, replier) {
            replier('pong!');
            console.log('Sent back pong JavaScript!')
        });

    };

    this.stop = function() {
        console.log("Verticle stopped!");
    }
};

/*
var config = {
    foo: "wibble",
    bar: false
} */

//container.deployVerticle("foo.ChildVerticle", config);  // Deploy a Java verticle

bootVerticle.start();

function vertxStop() {
    bootVerticle.stop();
}