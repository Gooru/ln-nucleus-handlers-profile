package org.gooru.nucleus.profiles.processors.repositories.activejdbc.dbhandlers;

import org.gooru.nucleus.profiles.processors.ProcessorContext;

public class DBHandlerBuilder {

  public DBHandler buildListCoursesHandler(ProcessorContext context) {
    return new ListCoursesHandler(context);
  }

  public DBHandler buildListAssessmentsHandler(ProcessorContext context) {
    return new ListAssessmentsHandler(context);
  }

  public DBHandler buildListResourcesHandler(ProcessorContext context) {
    return new ListResourcesHandler(context);
  }

  public DBHandler buildListCollectionsHandler(ProcessorContext context) {
    return new ListCollectionsHandler(context);
  }

  public DBHandler buildListQuestionsHandler(ProcessorContext context) {
    return new ListQuestionsHandler(context);
  }

  public DBHandler buildListDemographicsHandler(ProcessorContext context) {
    return new GetDemographicsHandler(context);
  }

  public DBHandler buildFollowHandler(ProcessorContext context) {
    return new FollowHandler(context);
  }

  public DBHandler buildUnfollowHandler(ProcessorContext context) {
    return new UnfollowHandler(context);
  }

  public DBHandler buildListFollowersHandler(ProcessorContext context) {
    return new ListFollowersHandler(context);
  }

  public DBHandler buildListFollowingsHandler(ProcessorContext context) {
    return new ListFollowingsHandler(context);
  }

}
