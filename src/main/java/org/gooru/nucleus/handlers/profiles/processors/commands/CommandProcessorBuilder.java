package org.gooru.nucleus.handlers.profiles.processors.commands;

import java.util.HashMap;
import java.util.Map;
import org.gooru.nucleus.handlers.profiles.constants.MessageConstants;
import org.gooru.nucleus.handlers.profiles.processors.Processor;
import org.gooru.nucleus.handlers.profiles.processors.ProcessorContext;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ashish on 2/1/17.
 */
public enum CommandProcessorBuilder {

    DEFAULT("default") {
        private final Logger LOGGER = LoggerFactory.getLogger(CommandProcessorBuilder.class);

        @Override
        public Processor build(ProcessorContext context) {
            return () -> {
                LOGGER.error("Invalid operation type passed in, not able to handle");
                return MessageResponseFactory.createInvalidRequestResponse("Invalid operation");
            };
        }
    },
    PROFILE_COURSE_LIST(MessageConstants.MSG_OP_PROFILE_COURSE_LIST) {
        @Override
        public Processor build(ProcessorContext context) {
            return new CourseListProcessor(context);
        }
    },
    PROFILE_COLLECTION_LIST(MessageConstants.MSG_OP_PROFILE_COLLECTION_LIST) {
        @Override
        public Processor build(ProcessorContext context) {
            return new CollectionListProcessor(context);
        }
    },
    PROFILE_ASSESSMENT_LIST(MessageConstants.MSG_OP_PROFILE_ASSESSMENT_LIST) {
        @Override
        public Processor build(ProcessorContext context) {
            return new AssessmentListProcessor(context);
        }
    },
    PROFILE_RESOURCE_LIST(MessageConstants.MSG_OP_PROFILE_RESOURCE_LIST) {
        @Override
        public Processor build(ProcessorContext context) {
            return new ResourceListProcessor(context);
        }
    },
    PROFILE_QUESTION_LIST(MessageConstants.MSG_OP_PROFILE_QUESTION_LIST) {
        @Override
        public Processor build(ProcessorContext context) {
            return new QuestionListProcessor(context);
        }
    },
    PROFILE_RUBRIC_LIST(MessageConstants.MSG_OP_PROFILE_RUBRIC_LIST) {
        @Override
        public Processor build(ProcessorContext context) {
            return new RubricListProcessor(context);
        }
    },
    ROFILE_DEMOGRAPHICS_GET(MessageConstants.MSG_OP_PROFILE_DEMOGRAPHICS_GET) {
        @Override
        public Processor build(ProcessorContext context) {
            return new DemographicsProcessor(context);
        }
    },
    PROFILE_FOLLOW(MessageConstants.MSG_OP_PROFILE_FOLLOW) {
        @Override
        public Processor build(ProcessorContext context) {
            return new FollowUserProcessor(context);
        }
    },
    PROFILE_UNFOLLOW(MessageConstants.MSG_OP_PROFILE_UNFOLLOW) {
        @Override
        public Processor build(ProcessorContext context) {
            return new UnFollowUserProcessor(context);
        }
    },
    PROFILE_NETWORK_GET(MessageConstants.MSG_OP_PROFILE_NETWORK_GET) {
        @Override
        public Processor build(ProcessorContext context) {
            return new NetworkProcessor(context);
        }
    },
    PROFILE_SEARCH(MessageConstants.MSG_OP_PROFILE_SEARCH) {
        @Override
        public Processor build(ProcessorContext context) {
            return new SearchProfileProcessor(context);
        }
    },
    PROFILE_PREFERENCE_GET(MessageConstants.MSG_OP_PROFILE_PREFERENCE_GET) {
        @Override
        public Processor build(ProcessorContext context) {
            return new PreferenceGetProcessor(context);
        }
    },
    PROFILE_PREFERENCE_UPDATE(MessageConstants.MSG_OP_PROFILE_PREFERENCE_UPDATE) {
        @Override
        public Processor build(ProcessorContext context) {
            return new PreferenceUpdateProcessor(context);
        }
    },
    PROFILE_STATE_UPDATE(MessageConstants.MSG_OP_PROFILE_STATE_UPDATE) {
        @Override
        public Processor build(ProcessorContext context) {
            return new UserStateUpdateProcessor(context);
        }

    },
    PROFILE_OFFLINE_ACTIVITY_LIST(MessageConstants.MSG_OP_PROFILE_OFFLINE_ACTIVITES_LIST) {
        @Override
        public Processor build(ProcessorContext context) {
            return new OfflineActivitiesListProcessor(context);
        }

    };

    private String name;

    CommandProcessorBuilder(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    private static final Map<String, CommandProcessorBuilder> LOOKUP = new HashMap<>();

    static {
        for (CommandProcessorBuilder builder : values()) {
            LOOKUP.put(builder.getName(), builder);
        }
    }

    public static CommandProcessorBuilder lookupBuilder(String name) {
        CommandProcessorBuilder builder = LOOKUP.get(name);
        if (builder == null) {
            return DEFAULT;
        }
        return builder;
    }

    public abstract Processor build(ProcessorContext context);
}
