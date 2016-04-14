package org.gooru.nucleus.profiles.processors.repositories.activejdbc;

import org.gooru.nucleus.profiles.processors.ProcessorContext;
import org.gooru.nucleus.profiles.processors.repositories.ProfileRepo;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.dbhandlers.DBHandlerBuilder;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.transactions.TransactionExecutor;
import org.gooru.nucleus.profiles.processors.responses.MessageResponse;

public class AJProfileRepo implements ProfileRepo {

    private final ProcessorContext context;

    public AJProfileRepo(ProcessorContext context) {
        this.context = context;
    }

    @Override
    public MessageResponse listDemographics() {
        return new TransactionExecutor()
            .executeTransaction(new DBHandlerBuilder().buildListDemographicsHandler(context));
    }

    @Override
    public MessageResponse follow() {
        return new TransactionExecutor().executeTransaction(new DBHandlerBuilder().buildFollowHandler(context));
    }

    @Override
    public MessageResponse unfollow() {
        return new TransactionExecutor().executeTransaction(new DBHandlerBuilder().buildUnfollowHandler(context));
    }

    @Override
    public MessageResponse getNetwork() {
        return new TransactionExecutor().executeTransaction(new DBHandlerBuilder().buildGetNetworkHandler(context));
    }

}
