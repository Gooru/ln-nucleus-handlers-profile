package org.gooru.nucleus.handlers.profiles.constants;

public final class MessagebusEndpoints {
    /*
     * Any change here in end points should be done in the gateway side as well,
     * as both sender and receiver should be in sync
     */
    public static final String MBEP_PROFILE = "org.gooru.nucleus.message.bus.profile";
    public static final String MBEP_EVENT = "org.gooru.nucleus.message.bus.publisher.event";

    private MessagebusEndpoints() {
        throw new AssertionError();
    }
}
