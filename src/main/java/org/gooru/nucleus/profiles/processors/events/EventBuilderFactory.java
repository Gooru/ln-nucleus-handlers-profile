package org.gooru.nucleus.profiles.processors.events;

import io.vertx.core.json.JsonObject;

public final class EventBuilderFactory {

  private static final String EVENT_NAME = "event.name";
  private static final String EVENT_BODY = "event.body";
  private static final String USER_ID = "user_id";
  private static final String FOLLOW_ON_USER_ID = "follow_on_user_id";
  
  private static final String EVT_PROFILE_FOLLOW = "event.profile.follow";
  private static final String EVT_PROFILE_UNFOLLOW = "event.profile.unfollow";

  private EventBuilderFactory() {
    throw new AssertionError();
  }
  
  public static EventBuilder getFollowProfileEventBuilder(String userId, String followOnUserId) {
    return () -> new JsonObject().put(EVENT_NAME, EVT_PROFILE_FOLLOW).put(EVENT_BODY,
            new JsonObject().put(USER_ID, userId).put(FOLLOW_ON_USER_ID, followOnUserId));
  }
  
  public static EventBuilder getUnfollowProfileEventBuilder(String userId, String followOnUserId) {
    return () -> new JsonObject().put(EVENT_NAME, EVT_PROFILE_UNFOLLOW).put(EVENT_BODY,
            new JsonObject().put(USER_ID, userId).put(FOLLOW_ON_USER_ID, followOnUserId));
  }
}
