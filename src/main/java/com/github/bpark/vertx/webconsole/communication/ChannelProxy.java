package com.github.bpark.vertx.webconsole.communication;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

/**
 * @author ks..
 */
public class ChannelProxy {

    private String connectedChannel;

    public SockJsConfig createSockJsConfig() {
        // set in and outbound permitted addresses
        JsonObject config = new JsonObject().putString("prefix", "/bridge");
        JsonArray inboundPermitted = new JsonArray();
        inboundPermitted.add(new JsonObject().putString("address", "msg.receive"));
        inboundPermitted.add(new JsonObject().putString("address", "msg.send"));
        inboundPermitted.add(new JsonObject().putString("address", "msg.connect"));

        JsonArray outboundPermitted = new JsonArray();
        outboundPermitted.add(new JsonObject().putString("address", "msg.receive"));
        outboundPermitted.add(new JsonObject().putString("address", "msg.send"));
        outboundPermitted.add(new JsonObject().putString("address", "msg.connect"));

        return new SockJsConfig(config, inboundPermitted, outboundPermitted);
    }

    public void initComChannel(EventBus eventBus) {
        eventBus.registerHandler("msg.send", new Handler<Message<JsonObject>>() {

            @Override
            public void handle(Message<JsonObject> message) {
                JsonObject messageObject = message.body();
                String channelId = messageObject.getString("channelId");
                JsonObject messageId = messageObject.getObject("messageId");
                eventBus.send(channelId, messageId);
            }
        });

        ProxyReceiveHandler proxyReceiveHandler = new ProxyReceiveHandler(eventBus);

        eventBus.registerHandler("msg.connect", new Handler<Message<JsonObject>>() {
            @Override
            public void handle(Message<JsonObject> message) {
                JsonObject messageObject = message.body();
                String channelId = messageObject.getString("channelId");
                String type = messageObject.getString("type");
                System.out.println("connected channel: " + connectedChannel);
                if (connectedChannel != null) {
                    System.out.println("unregistering");
                    eventBus.unregisterHandler(connectedChannel, proxyReceiveHandler);
                    connectedChannel = null;
                }
                if ("connect".equals(type)) {
                    connectedChannel = channelId;
                    eventBus.registerHandler(channelId, proxyReceiveHandler);
                }
            }
        });

    }

    private class ProxyReceiveHandler implements Handler<Message> {

        private EventBus eventBus;

        public ProxyReceiveHandler(EventBus eventBus) {
            this.eventBus = eventBus;
        }

        @Override
        public void handle(Message message) {
            eventBus.send("msg.receive", message.body());
        }
    }
}
