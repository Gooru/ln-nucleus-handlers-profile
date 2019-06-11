package org.gooru.nucleus.handlers.profiles.processors.commands;

import org.gooru.nucleus.handlers.profiles.processors.Processor;
import org.gooru.nucleus.handlers.profiles.processors.ProcessorContext;
import org.gooru.nucleus.handlers.profiles.processors.repositories.RepoBuilder;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponse;


public class OfflineActivitiesListProcessor extends AbstractCommandProcessor implements Processor {

  protected OfflineActivitiesListProcessor(ProcessorContext context) {
    super(context);
  }

  @Override
  protected void setDeprecatedVersions() {
    // NOOP
  }

  @Override
  protected MessageResponse processCommand() {
    return RepoBuilder.buildOfflineActivityRepo(context).listOfflineAcitvities();
  }

}
