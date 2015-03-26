package com.github.bpark.vertx.vdeployer;

import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Container;
import org.vertx.java.platform.PlatformLocator;
import org.vertx.java.platform.PlatformManager;
import org.vertx.java.platform.Verticle;
import org.vertx.java.platform.impl.DefaultContainer;
import org.vertx.java.platform.impl.DefaultPlatformManager;
import org.vertx.java.platform.impl.Deployment;
import org.vertx.java.platform.impl.PlatformManagerInternal;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

/**
 * @author ks..
 */
public class InformationVerticle extends Verticle {

    @Override
    public void start() {
        super.start();
        PlatformManager platformManager = PlatformLocator.factory.createPlatformManager();
        getVertx().eventBus().registerHandler("_infochannel", (Message<JsonObject> message) -> {
            Map<String, Integer> instances = platformManager.listInstances();
            container.logger().info("got info request");

            final JsonObject answer = new JsonObject();
            getDeployments().forEach((id, deployment) -> {
                answer.putString(id, deployment.main);
            });

            //instances.forEach((i, j) -> container.logger().info(i + " - " + j));
            //instances.forEach(answer::putNumber);

            getDeployments().forEach((i, j) -> {
                container.logger().info(i + " - " + j.main + " - " + (j.modID != null ? j.modID.getName(): "null"));
            });

            message.reply(answer);
        });
        container.logger().info("Deployed InformationVerticle");
    }


    protected final PlatformManagerInternal getManager() {
        try {
            Container container = getContainer();
            Field f = DefaultContainer.class.getDeclaredField("mgr");
            f.setAccessible(true);
            return (PlatformManagerInternal) f.get(container);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected final Map<String, Deployment> getDeployments() {
        try {
            PlatformManagerInternal mgr = getManager();
            Field d = DefaultPlatformManager.class.getDeclaredField("deployments");
            d.setAccessible(true);
            return Collections.unmodifiableMap((Map<String, Deployment>) d.get(mgr));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
