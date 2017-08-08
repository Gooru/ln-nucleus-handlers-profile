package org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc;

import org.gooru.nucleus.handlers.profiles.processors.ProcessorContext;
import org.gooru.nucleus.handlers.profiles.processors.repositories.RubricRepo;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.dbhandlers.DBHandlerBuilder;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.transactions.TransactionExecutor;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponse;

/**
 * @author szgooru
 * Created On: 01-Mar-2017
 */
public class AJRubricRepo implements RubricRepo {

    private final ProcessorContext context;
    
    public AJRubricRepo(ProcessorContext context) {
        this.context = context;
    }

    @Override
    public MessageResponse listRubrics() {
        return new TransactionExecutor().executeTransaction(DBHandlerBuilder.buildListRubricsHandler(context));
    }

}
