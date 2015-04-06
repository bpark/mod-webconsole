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

import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Container;
import org.vertx.java.platform.impl.DefaultContainer;
import org.vertx.java.platform.impl.DefaultPlatformManager;
import org.vertx.java.platform.impl.Deployment;
import org.vertx.java.platform.impl.PlatformManagerInternal;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author bpark.
 */
public class InformationProvider {

    private Container container;

    public InformationProvider(Container container) {
        this.container = container;
    }

    public JsonArray listDeployments() {
        Map<String, Deployment> deployments = getDeployments();
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
