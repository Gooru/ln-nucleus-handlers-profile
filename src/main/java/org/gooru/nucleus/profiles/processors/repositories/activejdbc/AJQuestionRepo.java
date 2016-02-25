package org.gooru.nucleus.profiles.processors.repositories.activejdbc;

import org.gooru.nucleus.profiles.processors.ProcessorContext;
import org.gooru.nucleus.profiles.processors.repositories.QuestionRepo;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.dbhandlers.DBHandlerBuilder;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.transactions.TransactionExecutor;
import org.gooru.nucleus.profiles.processors.responses.MessageResponse;

public class AJQuestionRepo implements QuestionRepo {

  private final ProcessorContext context;

  public AJQuestionRepo(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public MessageResponse listQuestions() {
    return new TransactionExecutor().executeTransaction(new DBHandlerBuilder().buildListQuestionsHandler(context));
  }

  @Override
  public MessageResponse searchQuestions() {
    return new TransactionExecutor().executeTransaction(new DBHandlerBuilder().buildSearchQuestionsHandler(context));
  }

}
