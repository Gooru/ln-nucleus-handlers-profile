package org.gooru.nucleus.handlers.profiles.processors.commands;

import org.gooru.nucleus.handlers.profiles.processors.ProcessorContext;
import org.gooru.nucleus.handlers.profiles.processors.repositories.RepoBuilder;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponse;

/**
 * @author szgooru
 * Created On: 01-Feb-2017
 */
public class PreferenceGetProcessor extends AbstractCommandProcessor {

    protected PreferenceGetProcessor(ProcessorContext context) {
        super(context);
    }

    @Override
    protected void setDeprecatedVersions() {
        //NOOP
    }

    @Override
    protected MessageResponse processCommand() {
        return RepoBuilder.buildPreferenceRepo(context).getPrefernce();
    }

}
