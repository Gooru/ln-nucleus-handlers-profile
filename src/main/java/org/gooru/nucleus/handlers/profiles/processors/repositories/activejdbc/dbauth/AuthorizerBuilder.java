package org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.dbauth;

import org.gooru.nucleus.handlers.profiles.processors.ProcessorContext;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities.AJEntityUsers;

public final class AuthorizerBuilder {

    private AuthorizerBuilder() {
        throw new AssertionError();
    }

    public static Authorizer<AJEntityUsers> buildUserAuthorizer(ProcessorContext context) {
        return new UserAuthorizer(context);
    }

}
