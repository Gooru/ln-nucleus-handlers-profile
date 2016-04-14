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

    public DBHandler buildGetNetworkHandler(ProcessorContext context) {
        return new GetNetworkHandler(context);
    }

    public DBHandler buildFetchTaxonomyForResourcesHandler(ProcessorContext context) {
        return new FetchTaxonomyForResourcesHandler(context);
    }

    public DBHandler buildFetchTaxonomyForQuestionsHandler(ProcessorContext context) {
        return new FetchTaxonomyForQuestionsHandler(context);
    }

    public DBHandler buildFetchSubjectBucketsForCoursesHandler(ProcessorContext context) {
        return new FetchSubjectBucketsForCoursesHandler(context);
    }

    public DBHandler buildFetchTaxonomyForCollectionsHandler(ProcessorContext context) {
        return new FetchTaxonomyForCollectionsHandler(context);
    }

    public DBHandler buildFetchTaxonomyForAssessmentsHandler(ProcessorContext context) {
        return new FetchTaxonomyForAssessmentsHandler(context);
    }

}
