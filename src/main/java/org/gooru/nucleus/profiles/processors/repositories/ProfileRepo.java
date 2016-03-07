package org.gooru.nucleus.profiles.processors.repositories;

import org.gooru.nucleus.profiles.processors.responses.MessageResponse;

public interface ProfileRepo {

  MessageResponse listDemographics();

  MessageResponse follow();

  MessageResponse unfollow();

  MessageResponse getNetwork();

}
