package org.gooru.nucleus.handlers.profiles.processors.repositories;

import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponse;

public interface CourseRepo {

    MessageResponse listCourses();

    MessageResponse fetchSubjectBucketsForCourses();

}
