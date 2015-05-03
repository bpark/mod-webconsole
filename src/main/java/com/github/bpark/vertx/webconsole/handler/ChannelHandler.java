package com.github.bpark.vertx.webconsole.handler;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonObject;

/**
 * @author ks..
 */
public class ChannelHandler implements Handler<HttpServerRequest> {

    private EventBus eventBus;


    public ChannelHandler(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void handle(HttpServerRequest event) {
        event.bodyHandler(data -> {
            JsonObject messageObject = new JsonObject(data.toString());
            eventBus.registerHandler(messageObject.getString("channelId"), message -> eventBus.send("msg.client", message.body()));
        });
        event.response().end();
    }
}
