package org.gooru.nucleus.handlers.profiles.processors.repositories;

import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponse;

/**
 * @author szgooru
 * Created On: 01-Feb-2017
 */
public interface PreferenceRepo {
    
    MessageResponse getPrefernce();
    
    MessageResponse updatePreference();
}
