package org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc;

import org.gooru.nucleus.handlers.profiles.processors.ProcessorContext;
import org.gooru.nucleus.handlers.profiles.processors.repositories.CourseRepo;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.dbhandlers.DBHandlerBuilder;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.transactions.TransactionExecutor;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponse;

public class AJCourseRepo implements CourseRepo {

  private final ProcessorContext context;

  public AJCourseRepo(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public MessageResponse listCourses() {
    return new TransactionExecutor()
        .executeTransaction(DBHandlerBuilder.buildListCoursesHandler(context));
  }

}
