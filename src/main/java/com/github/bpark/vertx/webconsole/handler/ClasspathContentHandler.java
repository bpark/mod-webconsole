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

import com.github.bpark.vertx.webconsole.WebConsole;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.apache.commons.io.IOUtils;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.HttpServerResponse;
import org.vertx.java.core.http.impl.MimeMapping;

import java.io.IOException;
import java.io.InputStream;

import static org.vertx.java.core.http.HttpHeaders.CONTENT_LENGTH;
import static org.vertx.java.core.http.HttpHeaders.CONTENT_TYPE;

/**
 * @author bpark.
 */
public class ClasspathContentHandler implements Handler<HttpServerRequest> {

    @Override
    public void handle(HttpServerRequest serverRequest) {

        HttpServerResponse serverResponse = serverRequest.response();

        String path = serverRequest.path();

        InputStream inputStream = WebConsole.class.getResourceAsStream(path);
        if (inputStream != null) {
            try {

                setContentType(serverResponse, path);

                byte[] data = IOUtils.toByteArray(inputStream);
                serverResponse.headers().add(CONTENT_LENGTH, String.valueOf(data.length));
                serverResponse.end(new Buffer(data));
            } catch (IOException e) {
                sendError(serverResponse);
            }
        } else {
            sendNotFound(serverResponse);
        }
    }

    private void setContentType(HttpServerResponse serverResponse, String path) {
        String extension = getExtension(path);
        String contentType = MimeMapping.getMimeTypeForExtension(extension);
        if (contentType != null) {
            serverResponse.headers().add(CONTENT_TYPE, contentType);
        }
    }

    private void sendError(HttpServerResponse serverResponse) {
        serverResponse.setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
        serverResponse.end();
    }

    private void sendNotFound(HttpServerResponse serverResponse) {
        serverResponse.setStatusCode(HttpResponseStatus.NOT_FOUND.code());
        serverResponse.end();
    }

    private String getExtension(String path) {
        String extension = null;
        if (path != null && !path.isEmpty() && path.contains(".") && !path.endsWith(".")) {
            extension = path.substring(path.lastIndexOf(".") + 1);
        }
        return extension;
    }
}
