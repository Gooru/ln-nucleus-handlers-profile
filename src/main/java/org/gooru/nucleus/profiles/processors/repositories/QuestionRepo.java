package org.gooru.nucleus.profiles.processors.repositories;

import org.gooru.nucleus.profiles.processors.responses.MessageResponse;

public interface QuestionRepo {

    MessageResponse listQuestions();

    MessageResponse fetchTaxonomyForQuestions();

}
