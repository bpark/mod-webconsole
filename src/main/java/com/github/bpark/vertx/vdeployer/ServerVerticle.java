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

package com.github.bpark.vertx.vdeployer;

import com.github.bpark.vertx.vdeployer.handler.ClasspathContentHandler;
import com.github.bpark.vertx.vdeployer.handler.DeployHandler;
import com.github.bpark.vertx.vdeployer.handler.ListDeploymentsHandler;
import com.github.bpark.vertx.vdeployer.handler.UndeployHandler;
import com.github.bpark.vertx.vdeployer.handler.VersionHandler;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.platform.Verticle;

/**
 * @author bpark.
 */
public class ServerVerticle extends Verticle {

    @Override
    public void start() {
        super.start();

        InformationProvider informationProvider = new InformationProvider(this.container);

        HttpServer server = vertx.createHttpServer();
        RouteMatcher routeMatcher = new RouteMatcher();

        routeMatcher.get("/version", new VersionHandler());
        routeMatcher.get("/resources", new ListDeploymentsHandler(informationProvider));
        routeMatcher.post("/remove", new UndeployHandler(informationProvider, container));
        routeMatcher.post("/deploy", new DeployHandler(informationProvider, container));
        routeMatcher.noMatch(new ClasspathContentHandler());

        server.requestHandler(routeMatcher);
        server.listen(8990, "localhost");
    }

}
