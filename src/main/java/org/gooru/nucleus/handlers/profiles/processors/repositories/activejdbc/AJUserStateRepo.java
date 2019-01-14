package org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc;

import org.gooru.nucleus.handlers.profiles.processors.ProcessorContext;
import org.gooru.nucleus.handlers.profiles.processors.repositories.UserStateRepo;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.dbhandlers.DBHandlerBuilder;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.transactions.TransactionExecutor;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponse;

/**
 * @author szgooru Created On: 08-Dec-2017
 */
public class AJUserStateRepo implements UserStateRepo {

  private final ProcessorContext context;

  public AJUserStateRepo(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public MessageResponse updateUserState() {
    return new TransactionExecutor()
        .executeTransaction(DBHandlerBuilder.buildUpdateUserStateHandler(context));
  }

}
