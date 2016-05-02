package org.gooru.nucleus.handlers.profiles.processors.repositories;

import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponse;

public interface CollectionRepo {

    MessageResponse listCollections();

    MessageResponse fetchTaxonomyForCollections();

}
