package com.github.bpark.vertx.vdeployer;

import com.github.bpark.vertx.vdeployer.handler.ClasspathContentHandler;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;
import org.vertx.java.platform.impl.Deployment;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ks..
 */
public class ServerVerticle extends Verticle {

    private InformationProvider informationProvider;

    @Override
    public void start() {
        super.start();

        informationProvider = new InformationProvider(this.container);

        HttpServer server = vertx.createHttpServer();
        RouteMatcher routeMatcher = new RouteMatcher();

        routeMatcher.get("/version", event -> event.response().end("1.0"));

        routeMatcher.get("/deployments", event -> event.response().end(listDeployments().toString()));

        routeMatcher.post("/undeploy", event -> event.dataHandler(data -> {
            JsonObject object = new JsonObject(data.toString());
            container.undeployVerticle(object.getString("id"));
            event.response().end(listDeployments().toString());
        }));

        routeMatcher.post("/deploy", event -> event.dataHandler(data -> {
            //JsonObject object = new JsonObject(data.toString());
            event.response().end(listDeployments().toString());
        }));

        routeMatcher.noMatch(new ClasspathContentHandler());
        server.requestHandler(routeMatcher);
        server.listen(9990, "localhost");
    }

    private JsonArray listDeployments() {
        Map<String, Deployment> deployments = informationProvider.getDeployments();
        JsonArray deploymentObject = new JsonArray();
        final AtomicInteger index = new AtomicInteger(0);
        deployments.forEach((key, value) -> {
            JsonObject deployment = new JsonObject();
            deployment.putNumber("index", index.incrementAndGet());
            deployment.putString("key", key);
            deployment.putString("name", value.name);
            deployment.putString("main", value.main);
            deployment.putNumber("instances", value.instances);
            deployment.putBoolean("autoRedeploy", value.autoRedeploy);
            deploymentObject.addObject(deployment);
        });
        return deploymentObject;
    }

}
