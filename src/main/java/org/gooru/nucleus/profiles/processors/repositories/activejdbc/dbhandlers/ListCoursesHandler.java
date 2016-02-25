package org.gooru.nucleus.profiles.processors.repositories.activejdbc.dbhandlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.gooru.nucleus.profiles.constants.HelperConstants;
import org.gooru.nucleus.profiles.processors.ProcessorContext;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.dbauth.AuthorizerBuilder;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.entities.AJEntityCourse;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.entities.AJTaxonomySubject;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.gooru.nucleus.profiles.processors.responses.ExecutionResult;
import org.gooru.nucleus.profiles.processors.responses.ExecutionResult.ExecutionStatus;
import org.gooru.nucleus.profiles.processors.responses.MessageResponse;
import org.gooru.nucleus.profiles.processors.responses.MessageResponseFactory;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ListCoursesHandler implements DBHandler {

  private final ProcessorContext context;
  private boolean isPublic = false;
  private String standardFramework;
  private static final Logger LOGGER = LoggerFactory.getLogger(ListCoursesHandler.class);

  public ListCoursesHandler(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public ExecutionResult<MessageResponse> checkSanity() {
    if (context.userIdFromURL() == null || context.userIdFromURL().isEmpty()) {
      LOGGER.warn("Invalid user id");
      return new ExecutionResult<MessageResponse>(MessageResponseFactory.createInvalidRequestResponse("Invalid user id"), ExecutionStatus.FAILED);
    }

    // TODO: Not sure how to handle the use case when standard framework is null
    // or not exists in prefs. May need to revisit later
    standardFramework = context.prefs().getString(HelperConstants.PREFS_SFCODE);

    // identify whether the request is for public or owner
    isPublic = checkPublic();
    
    LOGGER.debug("checkSanity() OK");
    return new ExecutionResult<>(null, ExecutionStatus.CONTINUE_PROCESSING);
  }

  @Override
  public ExecutionResult<MessageResponse> validateRequest() {
    return AuthorizerBuilder.buildUserAuthorizer(context).authorize(null);
  }

  @Override
  public ExecutionResult<MessageResponse> executeRequest() {
    
    LazyList<AJEntityCourse> courseList;
    if(isPublic) {
      LOGGER.debug("getting list of courses for public view");  
      courseList = AJEntityCourse.findBySQL(AJEntityCourse.SELECT_COURSES_FOR_PUBLIC, context.userIdFromURL());
    } else {
      LOGGER.debug("getting list of courses for owner view"); 
      courseList = AJEntityCourse.findBySQL(AJEntityCourse.SELECT_COURSES_FOR_OWNER, context.userIdFromURL());
    }
    
    Map<String, List<AJEntityCourse>> bucketedCourses = new HashMap<>();
    for (AJEntityCourse ajEntityCourse : courseList) {
      JsonArray taxonomy = new JsonArray(ajEntityCourse.getString(AJEntityCourse.TAXONOMY));
      if(!taxonomy.isEmpty()) {
        for (int i = 0; i < taxonomy.size(); i++) {
          String taxonomyCode = taxonomy.getString(i);
          StringTokenizer tokenizer = new StringTokenizer(taxonomyCode, HelperConstants.TAXONOMY_SEPARATOR);
          String subjectId = tokenizer.nextToken();
          AJTaxonomySubject ajTaxonomySubject = AJTaxonomySubject.first(AJTaxonomySubject.SELECT_TX_SUBJECT, subjectId, standardFramework);
          if (ajTaxonomySubject != null) {
            String code = ajTaxonomySubject.getString(AJTaxonomySubject.CODE);
            
            if (bucketedCourses.containsKey(code)) {
              bucketedCourses.get(code).add(ajEntityCourse);
            } else {
              List<AJEntityCourse> tempList = new ArrayList<>();
              tempList.add(ajEntityCourse);
              bucketedCourses.put(code, tempList);
            }
          }
        }
      }
    }
    
    JsonObject responseBody = new JsonObject();
    Set<String> keys = bucketedCourses.keySet();
    for (String key : keys) {
      responseBody.put(key, new JsonArray(new JsonFormatterBuilder().buildSimpleJsonFormatter(false, AJEntityCourse.COURSE_LIST).toJson(bucketedCourses.get(key))));
    }
    
    //responseBody.put("courses", new JsonArray(new JsonFormatterBuilder().buildSimpleJsonFormatter(false, AJEntityCourse.COURSE_LIST).toJson(courseList)));
    return new ExecutionResult<>(MessageResponseFactory.createGetResponse(responseBody), ExecutionStatus.SUCCESSFUL);
  }

  @Override
  public boolean handlerReadOnly() {
    return true;
  }

  private boolean checkPublic() {
    if (!context.userId().equalsIgnoreCase(context.userIdFromURL())) {
      return true;
    } 
    
    JsonArray previewArray = context.request().getJsonArray(HelperConstants.REQ_PARAM_PREVIEW);
    if (previewArray == null || previewArray.isEmpty()) {
      return false;
    }

    String preview = (String) previewArray.getValue(0);
    // Assuming that preview parameter only exists when user want to view his
    // profile as public
    if (Boolean.parseBoolean(preview)) {
      return true;
    } else {
      return false;
    }
  }
}
