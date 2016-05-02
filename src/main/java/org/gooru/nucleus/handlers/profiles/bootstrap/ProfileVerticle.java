package org.gooru.nucleus.handlers.profiles.bootstrap;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

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

public class ProfileVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileVerticle.class);

    @Override
    public void start(Future<Void> voidFuture) throws Exception {

        vertx.executeBlocking(blockingFuture -> {
            startApplication();
            blockingFuture.complete();
        } , future -> {
            if (future.succeeded()) {
                voidFuture.complete();
            } else {
                voidFuture.fail("Not able to initialize the Profile machinery properly");
            }
        });

        EventBus eb = vertx.eventBus();

        eb.consumer(MessagebusEndpoints.MBEP_PROFILE, message -> {

            LOGGER.debug("Received message: " + message.body());

            vertx.executeBlocking(future -> {
                MessageResponse result = new ProcessorBuilder(message).build().process();
                LOGGER.info("got response :" + result.reply());
                future.complete(result);
            } , res -> {
                MessageResponse result = (MessageResponse) res.result();
                message.reply(result.reply(), result.deliveryOptions());

                JsonObject eventData = result.event();
                if (eventData != null) {
                    String sessionToken = ((JsonObject) message.body()).getString(MessageConstants.MSG_HEADER_TOKEN);
                    if (sessionToken != null && !sessionToken.isEmpty()) {
                        eventData.put(MessageConstants.MSG_HEADER_TOKEN, sessionToken);
                    } else {
                        LOGGER.warn("Invalid session token received");
                    }
                    eb.publish(MessagebusEndpoints.MBEP_EVENT, eventData);
                }
            });

        }).completionHandler(result -> {
            if (result.succeeded()) {
                LOGGER.info("Profile end point ready to listen");
            } else {
                LOGGER.error("Error registering the profile handler. Halting the Profile machinery");
                Runtime.getRuntime().halt(1);
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
