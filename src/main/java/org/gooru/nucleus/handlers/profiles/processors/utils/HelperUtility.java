package org.gooru.nucleus.handlers.profiles.processors.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

import org.gooru.nucleus.handlers.profiles.app.components.AppConfiguration;
import org.gooru.nucleus.handlers.profiles.constants.HelperConstants;
import org.gooru.nucleus.handlers.profiles.processors.ProcessorContext;

import io.vertx.core.json.JsonArray;

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
    
    public static String toPostgresArrayString(Collection<String> input) {
        int approxSize = ((input.size() + 1) * 36); // Length of UUID is around 36 chars
        Iterator<String> it = input.iterator();
        if (!it.hasNext()) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder(approxSize);
        sb.append('{');
        for (;;) {
            String s = it.next();
            sb.append('"').append(s).append('"');
            if (!it.hasNext()) {
                return sb.append('}').toString();
            }
            sb.append(',');
        }
    }
    
	public static String toPostgresArrayInt(Collection<Integer> input) {
		Iterator<Integer> it = input.iterator();
		if (!it.hasNext()) {
			return "{}";
		}

		StringBuilder sb = new StringBuilder();
		sb.append('{');
		for (;;) {
			Integer i = it.next();
			sb.append(i);
			if (!it.hasNext()) {
				return sb.append('}').toString();
			}
			sb.append(',');
		}
	}
    
    public static boolean checkPublic(ProcessorContext context) {
        if (!context.userId().equalsIgnoreCase(context.userIdFromURL())) {
            return true;
        }

        JsonArray previewArray = context.request().getJsonArray(HelperConstants.REQ_PARAM_PREVIEW);
        if (previewArray == null || previewArray.isEmpty()) {
            return false;
        }

        String preview = (String) previewArray.getValue(0);
        // Assuming that preview parameter only exists when user want to view his profile as public
        return Boolean.parseBoolean(preview);
    }
    
    public static Integer getLimitFromRequest(ProcessorContext context) {
        AppConfiguration appConfig = AppConfiguration.getInstance();
        try {
            String strLimit = readRequestParam(HelperConstants.REQ_PARAM_LIMIT, context);
            int limitFromRequest = strLimit != null ? Integer.valueOf(strLimit) : appConfig.getDefaultPagesize();
            return limitFromRequest > appConfig.getMaxPagesize() ? appConfig.getDefaultPagesize(): limitFromRequest;
        } catch (NumberFormatException nfe) {
            return appConfig.getDefaultPagesize();
        }
    }
    
    public static Integer getOffsetFromRequest(ProcessorContext context) {
        try {
            String offsetFromRequest = readRequestParam(HelperConstants.REQ_PARAM_OFFSET, context);
            return offsetFromRequest != null ? Integer.valueOf(offsetFromRequest) : HelperConstants.DEFAULT_OFFSET;
        } catch (NumberFormatException nfe) {
            return HelperConstants.DEFAULT_OFFSET;
        }
    }
    
    public static String readRequestParam(String param, ProcessorContext context) {
        JsonArray requestParams = context.request().getJsonArray(param);
        if (requestParams == null || requestParams.isEmpty()) {
            return null;
        }

        String value = requestParams.getString(0);
        return (value != null && !value.isEmpty()) ? value : null;
    }
}
