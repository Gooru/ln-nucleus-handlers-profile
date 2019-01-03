package org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc;

import org.gooru.nucleus.handlers.profiles.processors.ProcessorContext;
import org.gooru.nucleus.handlers.profiles.processors.repositories.PreferenceRepo;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.dbhandlers.DBHandlerBuilder;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.transactions.TransactionExecutor;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponse;

/**
 * @author szgooru Created On: 01-Feb-2017
 */
public class AJPreferenceRepo implements PreferenceRepo {

  private final ProcessorContext context;

  public AJPreferenceRepo(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public MessageResponse getPrefernce() {
    return new TransactionExecutor()
        .executeTransaction(DBHandlerBuilder.buildGetPreferenceHandler(context));
  }

  @Override
  public MessageResponse updatePreference() {
    return new TransactionExecutor()
        .executeTransaction(DBHandlerBuilder.buildUpdatePreferenceHandler(context));
  }

}
