package com.github.bpark.vertx.webconsole.communication;

import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

/**
 * @author ks..
 */
public class SockJsConfig {

    private JsonObject config;

    private JsonArray inbound;

    private JsonArray outbound;


    public SockJsConfig(JsonObject config, JsonArray inbound, JsonArray outbound) {
        this.config = config;
        this.inbound = inbound;
        this.outbound = outbound;
    }

    public JsonObject getConfig() {
        return config;
    }

    public JsonArray getInbound() {
        return inbound;
    }

    public JsonArray getOutbound() {
        return outbound;
    }
}
