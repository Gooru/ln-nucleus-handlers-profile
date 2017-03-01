package org.gooru.nucleus.handlers.profiles.processors.commands;

import org.gooru.nucleus.handlers.profiles.processors.ProcessorContext;
import org.gooru.nucleus.handlers.profiles.processors.repositories.RepoBuilder;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponse;

/**
 * @author szgooru
 * Created On: 01-Mar-2017
 */
public class RubricListProcessor extends AbstractCommandProcessor {

    protected RubricListProcessor(ProcessorContext context) {
        super(context);
    }

    @Override
    protected void setDeprecatedVersions() {
        // NOOP
    }

    @Override
    protected MessageResponse processCommand() {
        return RepoBuilder.buildRubricRepo(context).listRubrics();
    }

}
