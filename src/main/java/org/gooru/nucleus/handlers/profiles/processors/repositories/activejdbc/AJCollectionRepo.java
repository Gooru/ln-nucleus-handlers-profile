package org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc;

import org.gooru.nucleus.handlers.profiles.processors.ProcessorContext;
import org.gooru.nucleus.handlers.profiles.processors.repositories.CollectionRepo;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.dbhandlers.DBHandlerBuilder;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.transactions.TransactionExecutor;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponse;

public class AJCollectionRepo implements CollectionRepo {

    private final ProcessorContext context;

    public AJCollectionRepo(ProcessorContext context) {
        this.context = context;
    }

    @Override
    public MessageResponse listCollections() {
        return new TransactionExecutor()
            .executeTransaction(DBHandlerBuilder.buildListCollectionsHandler(context));
    }

}
