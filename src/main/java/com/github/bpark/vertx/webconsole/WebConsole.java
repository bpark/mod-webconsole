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

package com.github.bpark.vertx.webconsole;

import com.github.bpark.vertx.webconsole.handler.*;
import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author bpark.
 */
public class WebConsole extends BusModBase {

    private static final String ROUTE_VERSION = "/version";
    private static final String ROUTE_RESOURCES = "/resources";
    private static final String ROUTE_REMOVE = "/remove";
    private static final String ROUTE_DEPLOY = "/deploy";
    private static final String ROUTE_CHANNELS = "/channels";

    private static final String CONFIG_HOST = "host";
    private static final String DEFAULT_HOST = "localhost";

    private static final String CONFIG_PORT = "port";
    private static final int DEFAULT_PORT = 8990;


    private static final String TEXT = "Der geplante Pipeline-Deal zwischen Griechenland und Russland sorgt international für Aufmerksamkeit Nach Informationen von SPIEGEL ONLINE könnte das Geschäft schon am Dienstag festgezurrt werden und Athen bis zu fünf Milliarden Euro einbringen";

    private Random random = new Random();

    @Override
    public void start() {
        super.start();

        InformationProvider informationProvider = new InformationProvider(this.container);

        HttpServer server = vertx.createHttpServer();
        RouteMatcher routeMatcher = new RouteMatcher();

        routeMatcher.get(ROUTE_VERSION, new VersionHandler());
        routeMatcher.get(ROUTE_RESOURCES, new ListDeploymentsHandler(informationProvider));
        routeMatcher.post(ROUTE_REMOVE, new UndeployHandler(informationProvider, container));
        routeMatcher.post(ROUTE_DEPLOY, new DeployHandler(informationProvider, container));
        routeMatcher.post(ROUTE_CHANNELS, new ChannelHandler(eb));
        routeMatcher.noMatch(new ClasspathContentHandler());

        server.requestHandler(routeMatcher);

        String host = getOptionalStringConfig(CONFIG_HOST, DEFAULT_HOST);
        int port = getOptionalIntConfig(CONFIG_PORT, DEFAULT_PORT);


        // set in and outbound permitted addresses
        JsonObject config = new JsonObject().putString("prefix", "/bridge");
        JsonArray inboundPermitted = new JsonArray();
        inboundPermitted.add(new JsonObject().putString("address", "msg.client"));

        JsonArray outboundPermitted = new JsonArray();
        outboundPermitted.add(new JsonObject().putString("address", "msg.server"));
        outboundPermitted.add(new JsonObject().putString("address", "msg.client"));

        vertx.createSockJSServer(server).bridge(config, inboundPermitted, outboundPermitted);

        server.listen(port, host);

        long timerID = vertx.setPeriodic(1000, timerID1 -> {
            String[] words = TEXT.split(" ");
            List<String> phrase = new ArrayList<>();
            for (int i=0; i<random.nextInt(800) + 2; i++) {
                phrase.add(words[random.nextInt(words.length)]);
            }
            eb.publish("phrase", String.join(" ", phrase.toArray(new String[phrase.size()])));
            logger.info("Sending text");
        });
    }

}
