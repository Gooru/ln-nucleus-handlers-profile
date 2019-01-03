package org.gooru.nucleus.handlers.profiles.processors.repositories;

import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponse;

public interface ProfileRepo {

  MessageResponse listDemographics();

  MessageResponse follow();

  MessageResponse unfollow();

  MessageResponse getNetwork();

  MessageResponse searchProfile();

}
