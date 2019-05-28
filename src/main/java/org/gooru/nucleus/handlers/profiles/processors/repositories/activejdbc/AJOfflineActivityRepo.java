package org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc;

import org.gooru.nucleus.handlers.profiles.processors.ProcessorContext;
import org.gooru.nucleus.handlers.profiles.processors.repositories.OfflineActivityRepo;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.dbhandlers.DBHandlerBuilder;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.transactions.TransactionExecutor;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponse;

public class AJOfflineActivityRepo implements OfflineActivityRepo {

  private final ProcessorContext context;

  public AJOfflineActivityRepo(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public MessageResponse listOfflineAcitvities() {
    return new TransactionExecutor()
        .executeTransaction(DBHandlerBuilder.buildListOfflineActivitiesHandler(context));
  }

}
