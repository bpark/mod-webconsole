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

import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

/**
 * @author bpark.
 */
public class DeployerVerticle extends Verticle {

    @Override
    public void start() {
        super.start();

        JsonObject config = new JsonObject();
        config.putString("web_root", "C:/Dateien/Devel/java/projects/vertx/vertx-vdeployer/src/main/resources/static");
        config.putString("index_page", "index.html");
        config.putString("host", "localhost");
        config.putNumber("port", 8088);

        container.deployVerticle("com.github.bpark.vertx.vdeployer.ServerVerticle");
        container.deployModule("io.vertx~mod-web-server~2.0.0-final", config);
    }
}
