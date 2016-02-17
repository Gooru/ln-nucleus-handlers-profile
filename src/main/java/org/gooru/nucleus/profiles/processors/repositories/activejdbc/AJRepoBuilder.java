package org.gooru.nucleus.profiles.processors.repositories.activejdbc;

import org.gooru.nucleus.profiles.processors.ProcessorContext;
import org.gooru.nucleus.profiles.processors.repositories.AssessmentRepo;
import org.gooru.nucleus.profiles.processors.repositories.CollectionRepo;
import org.gooru.nucleus.profiles.processors.repositories.CourseRepo;
import org.gooru.nucleus.profiles.processors.repositories.ProfileRepo;
import org.gooru.nucleus.profiles.processors.repositories.QuestionRepo;
import org.gooru.nucleus.profiles.processors.repositories.ResourceRepo;

public class AJRepoBuilder {

  public CourseRepo buildCourseRepo(ProcessorContext context) {
    return new AJCourseRepo(context);
  }

  public CollectionRepo buildCollectionRepo(ProcessorContext context) {
    return new AJCollectionRepo(context);
  }

  public AssessmentRepo buildAssessmentRepo(ProcessorContext context) {
    return new AJAssessmentRepo(context);
  }

  public ResourceRepo buildResourceRepo(ProcessorContext context) {
    return new AJResourceRepo(context);
  }

  public QuestionRepo buildQuestionRepo(ProcessorContext context) {
    return new AJQuestionRepo(context);
  }
  
  public ProfileRepo buildProfileRepo(ProcessorContext context) {
    return new AJProfileRepo(context);
  }
}
