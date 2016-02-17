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
    return new TransactionExecutor().executeTransaction(new DBHandlerBuilder().buildListDemographicsHandler(context));
  }

}
