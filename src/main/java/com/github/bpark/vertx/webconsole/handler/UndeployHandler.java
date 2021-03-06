/*
 * Copyright 2015 Burt Parkers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.bpark.vertx.webconsole.handler;

import com.github.bpark.vertx.webconsole.InformationProvider;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Container;

public class UndeployHandler implements Handler<HttpServerRequest> {

    private InformationProvider informationProvider;

    private Container container;

    public UndeployHandler(InformationProvider informationProvider, Container container) {
        this.informationProvider = informationProvider;
        this.container = container;
    }

    @Override
    public void handle(HttpServerRequest event) {
        try {
            event.bodyHandler(body -> {
                JsonObject object = new JsonObject(body.toString());
                container.undeployVerticle(object.getString("id"), done -> {
                    event.response().end(informationProvider.listDeployments().toString());
                });
            });
        } catch (Exception e) {
            event.response().setStatusCode(500);
            JsonObject message = new JsonObject();
            message.putString("cause", e.getMessage());
            event.response().end(message.toString());
        }
    }
}
