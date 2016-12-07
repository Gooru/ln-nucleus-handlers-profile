package org.gooru.nucleus.handlers.profiles.bootstrap;

import org.gooru.nucleus.handlers.profiles.bootstrap.shutdown.Finalizer;
import org.gooru.nucleus.handlers.profiles.bootstrap.shutdown.Finalizers;
import org.gooru.nucleus.handlers.profiles.bootstrap.startup.Initializer;
import org.gooru.nucleus.handlers.profiles.bootstrap.startup.Initializers;
import org.gooru.nucleus.handlers.profiles.constants.MessageConstants;
import org.gooru.nucleus.handlers.profiles.constants.MessagebusEndpoints;
import org.gooru.nucleus.handlers.profiles.processors.ProcessorBuilder;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

public class ProfileVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileVerticle.class);

    @Override
    public void start(Future<Void> voidFuture) throws Exception {

        EventBus eb = vertx.eventBus();

        vertx.executeBlocking(blockingFuture -> {
            startApplication();
            blockingFuture.complete();
        }, startApplicationFuture -> {
            if (startApplicationFuture.succeeded()) {
                eb.consumer(MessagebusEndpoints.MBEP_PROFILE, message -> {
                    LOGGER.info("Received message: " + message.body());
                    long startTime = System.currentTimeMillis();
                    vertx.executeBlocking(future -> {
                        MessageResponse result = new ProcessorBuilder(message).build().process();
                        LOGGER.debug("got response :" + result.reply());
                        LOGGER.info("Request processing time:{}ms", (System.currentTimeMillis() - startTime));
                        future.complete(result);
                    }, false, res -> {
                        MessageResponse result = (MessageResponse) res.result();
                        message.reply(result.reply(), result.deliveryOptions());
                        JsonObject eventData = result.event();
                        if (eventData != null) {
                            String sessionToken =
                                ((JsonObject) message.body()).getString(MessageConstants.MSG_HEADER_TOKEN);
                            if (sessionToken != null && !sessionToken.isEmpty()) {
                                eventData.put(MessageConstants.MSG_HEADER_TOKEN, sessionToken);
                            } else {
                                LOGGER.warn("Invalid session token received");
                            }
                            eb.send(MessagebusEndpoints.MBEP_EVENT, eventData);
                        }
                    });
                }).completionHandler(result -> {
                    if (result.succeeded()) {
                        voidFuture.complete();
                        LOGGER.info("Profile end point ready to listen");
                    } else {
                        LOGGER.error("Error registering the profile handler. Halting the Profile machinery");
                        voidFuture.fail(result.cause());
                        Runtime.getRuntime().halt(1);
                    }
                });
            } else {
                voidFuture.fail("Not able to initialize the Profile machinery properly");
            }
        });

    }

    @Override
    public void stop() throws Exception {
        shutDownApplication();
        super.stop();
    }

    private void startApplication() {
        Initializers initializers = new Initializers();
        try {
            for (Initializer initializer : initializers) {
                initializer.initializeComponent(vertx, config());
            }
        } catch (IllegalStateException ie) {
            LOGGER.error("Error initializing application", ie);
            Runtime.getRuntime().halt(1);
        }
    }

    private void shutDownApplication() {
        Finalizers finalizers = new Finalizers();
        for (Finalizer finalizer : finalizers) {
            finalizer.finalizeComponent();
        }

    }
}
