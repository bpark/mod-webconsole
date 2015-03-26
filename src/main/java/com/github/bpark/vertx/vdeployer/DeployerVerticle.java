package com.github.bpark.vertx.vdeployer;

import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

/**
 * @author ks..
 */
public class DeployerVerticle extends Verticle {

    @Override
    public void start() {
        super.start();

        JsonObject config = new JsonObject();
        config.putString("web_root", "C:/Dateien/Devel/java/projects/vertx/vertx-vdeployer/src/main/resources/static");
        config.putString("index_page", "index.html");
        config.putString("host", "localhost");
        config.putNumber("port", 8081);

        container.deployVerticle("com.github.bpark.vertx.vdeployer.ServerVerticle");
        container.deployModule("io.vertx~mod-web-server~2.0.0-final", config);
    }
}
