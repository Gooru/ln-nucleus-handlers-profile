package org.gooru.nucleus.profiles.processors.utils;

import java.util.UUID;

public final class HelperUtility {

    private HelperUtility() {
        throw new AssertionError();
    }

    public static boolean validateUUID(String id) {
        try {
            UUID.fromString(id);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
