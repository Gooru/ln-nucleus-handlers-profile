package org.gooru.nucleus.profiles.processors.repositories.activejdbc;

import org.gooru.nucleus.profiles.processors.ProcessorContext;
import org.gooru.nucleus.profiles.processors.repositories.AssessmentRepo;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.dbhandlers.DBHandlerBuilder;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.transactions.TransactionExecutor;
import org.gooru.nucleus.profiles.processors.responses.MessageResponse;

public class AJAssessmentRepo implements AssessmentRepo {

  private final ProcessorContext context;

  public AJAssessmentRepo(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public MessageResponse listAssessments() {
    return new TransactionExecutor().executeTransaction(new DBHandlerBuilder().buildListAssessmentsHandler(context));
  }

}
