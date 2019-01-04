package org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc;

import org.gooru.nucleus.handlers.profiles.processors.ProcessorContext;
import org.gooru.nucleus.handlers.profiles.processors.repositories.ResourceRepo;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.dbhandlers.DBHandlerBuilder;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.transactions.TransactionExecutor;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponse;

public class AJResourceRepo implements ResourceRepo {

  private final ProcessorContext context;

  public AJResourceRepo(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public MessageResponse listResources() {
    return new TransactionExecutor()
        .executeTransaction(DBHandlerBuilder.buildListResourcesHandler(context));
  }

}
