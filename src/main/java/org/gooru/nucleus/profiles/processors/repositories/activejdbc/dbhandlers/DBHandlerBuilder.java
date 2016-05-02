package org.gooru.nucleus.profiles.processors.repositories.activejdbc.dbhandlers;

import org.gooru.nucleus.profiles.processors.ProcessorContext;

public final class DBHandlerBuilder {

    private DBHandlerBuilder() {
        throw new AssertionError();
    }

    public static DBHandler buildListCoursesHandler(ProcessorContext context) {
        return new ListCoursesHandler(context);
    }

    public static DBHandler buildListAssessmentsHandler(ProcessorContext context) {
        return new ListAssessmentsHandler(context);
    }

    public static DBHandler buildListResourcesHandler(ProcessorContext context) {
        return new ListResourcesHandler(context);
    }

    public static DBHandler buildListCollectionsHandler(ProcessorContext context) {
        return new ListCollectionsHandler(context);
    }

    public static DBHandler buildListQuestionsHandler(ProcessorContext context) {
        return new ListQuestionsHandler(context);
    }

    public static DBHandler buildListDemographicsHandler(ProcessorContext context) {
        return new GetDemographicsHandler(context);
    }

    public static DBHandler buildFollowHandler(ProcessorContext context) {
        return new FollowHandler(context);
    }

    public static DBHandler buildUnfollowHandler(ProcessorContext context) {
        return new UnfollowHandler(context);
    }

    public static DBHandler buildGetNetworkHandler(ProcessorContext context) {
        return new GetNetworkHandler(context);
    }

    public static DBHandler buildFetchTaxonomyForResourcesHandler(ProcessorContext context) {
        return new FetchTaxonomyForResourcesHandler(context);
    }

    public static DBHandler buildFetchTaxonomyForQuestionsHandler(ProcessorContext context) {
        return new FetchTaxonomyForQuestionsHandler(context);
    }

    public static DBHandler buildFetchSubjectBucketsForCoursesHandler(ProcessorContext context) {
        return new FetchSubjectBucketsForCoursesHandler(context);
    }

    public static DBHandler buildFetchTaxonomyForCollectionsHandler(ProcessorContext context) {
        return new FetchTaxonomyForCollectionsHandler(context);
    }

    public static DBHandler buildFetchTaxonomyForAssessmentsHandler(ProcessorContext context) {
        return new FetchTaxonomyForAssessmentsHandler(context);
    }

}
