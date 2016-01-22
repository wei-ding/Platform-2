/*
 * Copyright 2015 Clinical Personalized Pragmatic Predictions of Outcomes.
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

package org.clinical3po.backendservices.server.handler;

import org.clinical3po.backendservices.server.Clinical3POServer;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.*;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by w.ding on 8/7/14.
 */
public class WebSocketHandler implements WebSocketConnectionCallback {
    static final Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);

    public WebSocketHandler() {
    }

    @Override
    public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {
        logger.info("Connecting to websocket server.");
        // Validate if access token is available. Do I need to validate if the request comes from
        // one of configured hosts?


        String originHeader = exchange.getRequestHeader("Origin");
        boolean allowedOriginHeader = (originHeader == null ||
                Clinical3POServer.WEBSOCKET_ALLOWED_ORIGIN_HEADER.matcher(originHeader).matches());

        if (!allowedOriginHeader) {
            logger.info(channel.toString() + " disconnected due to invalid origin header: " + originHeader);
            exchange.close();
        }
        else {
            logger.info("Valid origin header, setting up connection.");

            channel.getReceiveSetter().set(new AbstractReceiveListener() {
                @Override
                protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) {
                    final String messageData = message.getData();
                    for(WebSocketChannel session : channel.getPeerConnections()) {
                        WebSockets.sendText(messageData, session, null);
                    }
                }

                @Override
                protected void onError(WebSocketChannel webSocketChannel, Throwable error) {
                    logger.error("Server error:", error);
                }

                @Override
                protected void onClose(WebSocketChannel clientChannel, StreamSourceFrameChannel streamSourceChannel) throws IOException {
                    logger.info(clientChannel.toString() + " disconnected");
                }
            });
            channel.resumeReceives();
        }
    }
}
