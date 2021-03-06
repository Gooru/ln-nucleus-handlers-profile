package org.gooru.nucleus.handlers.profiles.processors.repositories;

import org.gooru.nucleus.handlers.profiles.processors.ProcessorContext;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.AJRepoBuilder;

public final class RepoBuilder {

  private RepoBuilder() {
    throw new AssertionError();
  }

  public static CourseRepo buildCourseRepo(ProcessorContext context) {
    return AJRepoBuilder.buildCourseRepo(context);
  }

  public static CollectionRepo buildCollectionRepo(ProcessorContext context) {
    return AJRepoBuilder.buildCollectionRepo(context);
  }

  public static AssessmentRepo buildAssessmentRepo(ProcessorContext context) {
    return AJRepoBuilder.buildAssessmentRepo(context);
  }

  public static ResourceRepo buildResourceRepo(ProcessorContext context) {
    return AJRepoBuilder.buildResourceRepo(context);
  }

  public static QuestionRepo buildQuestionRepo(ProcessorContext context) {
    return AJRepoBuilder.buildQuestionRepo(context);
  }

  public static ProfileRepo buildProfileRepo(ProcessorContext context) {
    return AJRepoBuilder.buildProfileRepo(context);
  }

  public static PreferenceRepo buildPreferenceRepo(ProcessorContext context) {
    return AJRepoBuilder.buildPreferenceRepo(context);
  }

  public static RubricRepo buildRubricRepo(ProcessorContext context) {
    return AJRepoBuilder.buildRubricRepo(context);
  }

  public static UserStateRepo buildUserStateRepo(ProcessorContext context) {
    return AJRepoBuilder.buildUserStateRepo(context);
  }

  public static OfflineActivityRepo buildOfflineActivityRepo(ProcessorContext context) {
    return AJRepoBuilder.buildOfflineActivityRepo(context);
  }

}
