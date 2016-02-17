package org.gooru.nucleus.profiles.processors.repositories;

import org.gooru.nucleus.profiles.processors.ProcessorContext;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.AJRepoBuilder;

public class RepoBuilder {

  public CourseRepo buildCourseRepo(ProcessorContext context) {
    return new AJRepoBuilder().buildCourseRepo(context);
  }

  public CollectionRepo buildCollectionRepo(ProcessorContext context) {
    return new AJRepoBuilder().buildCollectionRepo(context);
  }

  public AssessmentRepo buildAssessmentRepo(ProcessorContext context) {
    return new AJRepoBuilder().buildAssessmentRepo(context);
  }

  public ResourceRepo buildResourceRepo(ProcessorContext context) {
    return new AJRepoBuilder().buildResourceRepo(context);
  }

  public QuestionRepo buildQuestionRepo(ProcessorContext context) {
    return new AJRepoBuilder().buildQuestionRepo(context);
  }

  public ProfileRepo buildProfileRepo(ProcessorContext context) {
    return new AJRepoBuilder().buildProfileRepo(context);
  }

}
