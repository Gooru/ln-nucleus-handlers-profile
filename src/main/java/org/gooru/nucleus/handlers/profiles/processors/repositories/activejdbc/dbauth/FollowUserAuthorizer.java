package org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.dbauth;

import org.gooru.nucleus.handlers.profiles.processors.ProcessorContext;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities.AJEntityUsers;
import org.gooru.nucleus.handlers.profiles.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponseFactory;
import org.gooru.nucleus.libs.tenant.TenantTree;
import org.gooru.nucleus.libs.tenant.TenantTreeBuilder;
import org.gooru.nucleus.libs.tenant.users.UserTenantAuthorization;
import org.gooru.nucleus.libs.tenant.users.UserTenantAuthorizationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ashish on 19/1/17.
 */
class FollowUserAuthorizer implements Authorizer<AJEntityUsers> {

    private final ProcessorContext context;
    private static final Logger LOGGER = LoggerFactory.getLogger(FollowUserAuthorizer.class);

    public FollowUserAuthorizer(ProcessorContext context) {
        this.context = context;
    }

    @Override
    public ExecutionResult<MessageResponse> authorize(AJEntityUsers model) {
        TenantTree loggedInUserTenantTree = TenantTreeBuilder.build(context.tenant(), context.tenantRoot());
        TenantTree followOnUserTenantTree = TenantTreeBuilder.build(model.getTenant(), model.getTenantRoot());

        UserTenantAuthorization authorization =
            UserTenantAuthorizationBuilder.build(loggedInUserTenantTree, followOnUserTenantTree);
        if (authorization.canFollow()) {
            return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);
        }
        LOGGER
            .info("User follow on tenancy auth check failed. Logged user: '{}', follow on user: '{}'", context.userId(),
                model.getId());
        return new ExecutionResult<>(MessageResponseFactory.createForbiddenResponse("User being followed not found"),
            ExecutionResult.ExecutionStatus.FAILED);
    }
}
