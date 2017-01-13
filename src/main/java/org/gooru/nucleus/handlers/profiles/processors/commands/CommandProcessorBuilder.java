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
    PROFILE_COURSE_SUBJECTBUCKETS_GET(MessageConstants.MSG_OP_PROFILE_COURSE_SUBJECTBUCKETS_GET) {
        @Override
        public Processor build(ProcessorContext context) {
            return new CourseSubjectBucketsProcessor(context);
        }
    },
    PROFILE_COLLECTION_TAXONOMY_GET(MessageConstants.MSG_OP_PROFILE_COLLECTION_TAXONOMY_GET) {
        @Override
        public Processor build(ProcessorContext context) {
            return new CollectionTaxonomyProcessor(context);
        }
    },
    PROFILE_ASSESSMENT_TAXONOMY_GET(MessageConstants.MSG_OP_PROFILE_ASSESSMENT_TAXONOMY_GET) {
        @Override
        public Processor build(ProcessorContext context) {
            return new AssessmentTaxonomyProcessor(context);
        }
    },
    PROFILE_RESOURCE_TAXONOMY_GET(MessageConstants.MSG_OP_PROFILE_RESOURCE_TAXONOMY_GET) {
        @Override
        public Processor build(ProcessorContext context) {
            return new ResourceTaxonomyProcessor(context);
        }
    },
    PROFILE_QUESTION_TAXONOMY_GET(MessageConstants.MSG_OP_PROFILE_QUESTION_TAXONOMY_GET) {
        @Override
        public Processor build(ProcessorContext context) {
            return new QuestionTaxonomyProcessor(context);
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