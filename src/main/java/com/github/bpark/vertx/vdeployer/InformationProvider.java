package com.github.bpark.vertx.vdeployer;

import org.vertx.java.platform.Container;
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
public class InformationProvider {

    private Container container;

    public InformationProvider(Container container) {
        this.container = container;
    }

    protected final PlatformManagerInternal getManager() {
        try {
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
