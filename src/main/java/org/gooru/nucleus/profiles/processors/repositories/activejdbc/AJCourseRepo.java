package org.gooru.nucleus.profiles.processors.repositories.activejdbc;

import org.gooru.nucleus.profiles.processors.ProcessorContext;
import org.gooru.nucleus.profiles.processors.repositories.CourseRepo;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.dbhandlers.DBHandlerBuilder;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.transactions.TransactionExecutor;
import org.gooru.nucleus.profiles.processors.responses.MessageResponse;

public class AJCourseRepo implements CourseRepo {

    private final ProcessorContext context;

    public AJCourseRepo(ProcessorContext context) {
        this.context = context;
    }

    @Override
    public MessageResponse listCourses() {
        return new TransactionExecutor().executeTransaction(new DBHandlerBuilder().buildListCoursesHandler(context));
    }

    @Override
    public MessageResponse fetchSubjectBucketsForCourses() {
        return new TransactionExecutor()
            .executeTransaction(new DBHandlerBuilder().buildFetchSubjectBucketsForCoursesHandler(context));
    }

}
