package org.gooru.nucleus.handlers.profiles.processors.commands;

import org.gooru.nucleus.handlers.profiles.processors.ProcessorContext;
import org.gooru.nucleus.handlers.profiles.processors.repositories.RepoBuilder;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponse;

/**
 * @author ashish on 2/1/17.
 */
class AssessmentListProcessor extends AbstractCommandProcessor {

  public AssessmentListProcessor(ProcessorContext context) {
    super(context);
  }

  @Override
  protected void setDeprecatedVersions() {

  }

  @Override
  protected MessageResponse processCommand() {
    return RepoBuilder.buildAssessmentRepo(context).listAssessments();
  }
}
