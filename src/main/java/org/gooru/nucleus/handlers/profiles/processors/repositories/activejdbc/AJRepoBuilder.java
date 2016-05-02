package org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc;

import org.gooru.nucleus.handlers.profiles.processors.ProcessorContext;
import org.gooru.nucleus.handlers.profiles.processors.repositories.AssessmentRepo;
import org.gooru.nucleus.handlers.profiles.processors.repositories.CollectionRepo;
import org.gooru.nucleus.handlers.profiles.processors.repositories.CourseRepo;
import org.gooru.nucleus.handlers.profiles.processors.repositories.ProfileRepo;
import org.gooru.nucleus.handlers.profiles.processors.repositories.QuestionRepo;
import org.gooru.nucleus.handlers.profiles.processors.repositories.ResourceRepo;

public final class AJRepoBuilder {

    private AJRepoBuilder() {
        throw new AssertionError();
    }

    public static CourseRepo buildCourseRepo(ProcessorContext context) {
        return new AJCourseRepo(context);
    }

    public static CollectionRepo buildCollectionRepo(ProcessorContext context) {
        return new AJCollectionRepo(context);
    }

    public static AssessmentRepo buildAssessmentRepo(ProcessorContext context) {
        return new AJAssessmentRepo(context);
    }

    public static ResourceRepo buildResourceRepo(ProcessorContext context) {
        return new AJResourceRepo(context);
    }

    public static QuestionRepo buildQuestionRepo(ProcessorContext context) {
        return new AJQuestionRepo(context);
    }

    public static ProfileRepo buildProfileRepo(ProcessorContext context) {
        return new AJProfileRepo(context);
    }
}
