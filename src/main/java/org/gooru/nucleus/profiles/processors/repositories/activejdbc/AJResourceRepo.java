package org.gooru.nucleus.profiles.processors.repositories.activejdbc;

import org.gooru.nucleus.profiles.processors.ProcessorContext;
import org.gooru.nucleus.profiles.processors.repositories.ResourceRepo;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.dbhandlers.DBHandlerBuilder;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.transactions.TransactionExecutor;
import org.gooru.nucleus.profiles.processors.responses.MessageResponse;

public class AJResourceRepo implements ResourceRepo {

  private final ProcessorContext context;

  public AJResourceRepo(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public MessageResponse listResources() {
    return new TransactionExecutor().executeTransaction(new DBHandlerBuilder().buildListResourcesHandler(context));
  }

}
