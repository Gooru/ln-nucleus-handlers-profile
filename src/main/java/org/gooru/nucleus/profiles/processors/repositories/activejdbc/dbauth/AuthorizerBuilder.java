package org.gooru.nucleus.profiles.processors.repositories.activejdbc.dbauth;

import org.gooru.nucleus.profiles.processors.ProcessorContext;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.entities.AJEntityUserIdentity;

public final class AuthorizerBuilder {

  private AuthorizerBuilder() {
    throw new AssertionError();
  }

  public static Authorizer<AJEntityUserIdentity> buildUserAuthorizer(ProcessorContext context) {
    return new UserAuthorizer(context);
  }

}
