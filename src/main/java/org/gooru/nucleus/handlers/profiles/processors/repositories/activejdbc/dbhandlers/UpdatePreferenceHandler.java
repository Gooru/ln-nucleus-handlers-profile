package org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.dbhandlers;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.gooru.nucleus.handlers.profiles.processors.ProcessorContext;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities.AJEntityGooruLanguage;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities.AJEntityTaxonomySubject;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities.AJEntityUserPreference;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities.AJEntityUsers;
import org.gooru.nucleus.handlers.profiles.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.profiles.processors.responses.ExecutionResult.ExecutionStatus;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponseFactory;
import org.gooru.nucleus.handlers.profiles.processors.utils.HelperUtility;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.Errors;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * @author szgooru Created On: 01-Feb-2017
 */
public class UpdatePreferenceHandler implements DBHandler {

	private final ProcessorContext context;
	private static final Logger LOGGER = LoggerFactory.getLogger(UpdatePreferenceHandler.class);

	private static final String REQ_KEY_PREFERENCE_SETTING = "preference_settings";
	private static final String REQ_KEY_STANDARD_PREF = "standard_preference";
	private static final String REQ_KEY_LANGUAGE_PREF = "language_preference";

	private AJEntityUserPreference userPreference;

	public UpdatePreferenceHandler(ProcessorContext context) {
		this.context = context;
	}

	@Override
	public ExecutionResult<MessageResponse> checkSanity() {
		// TODO: validation of the request JSON

		LOGGER.debug("checkSanity() OK");
		return new ExecutionResult<>(null, ExecutionStatus.CONTINUE_PROCESSING);
	}

	@Override
	public ExecutionResult<MessageResponse> validateRequest() {
		LazyList<AJEntityUsers> users = AJEntityUsers.findBySQL(AJEntityUsers.VALIDATE_USER, context.userId());
		if (users == null || users.isEmpty()) {
			LOGGER.warn("user not found in database");
			return new ExecutionResult<>(MessageResponseFactory.createNotFoundResponse("user not found in database"),
					ExecutionStatus.FAILED);
		}

		this.userPreference = AJEntityUserPreference.findById(UUID.fromString(context.userId()));
		return validateRequestPayload();
	}

	@Override
	public ExecutionResult<MessageResponse> executeRequest() {

		if (this.userPreference != null) {
			return doUpdate();
		}

		LOGGER.debug("no existing user prefernece found, creating new");
		userPreference = new AJEntityUserPreference();
		userPreference.setUserId(context.userId());
		userPreference.setPreferenceSettings(context.request().toString());

		if (!userPreference.insert()) {
			LOGGER.error("error while inserting user preference settings");
			return new ExecutionResult<>(
					MessageResponseFactory.createInternalErrorResponse("Error while saving user preference settings"),
					ExecutionResult.ExecutionStatus.FAILED);
		}

		LOGGER.debug("user preference settings stored successfully");
		return new ExecutionResult<>(MessageResponseFactory.createNoContentResponse(),
				ExecutionResult.ExecutionStatus.SUCCESSFUL);
	}

	@Override
	public boolean handlerReadOnly() {
		return false;
	}

	private ExecutionResult<MessageResponse> doUpdate() {
		this.userPreference.setPreferenceSettings(context.request().toString());

		if (!this.userPreference.save()) {
			LOGGER.error("error while updating user preference settings");
			return new ExecutionResult<>(
					MessageResponseFactory.createInternalErrorResponse("Error while saving user preference settings"),
					ExecutionResult.ExecutionStatus.FAILED);
		}

		LOGGER.debug("user preference settings updated successfully");
		return new ExecutionResult<>(MessageResponseFactory.createNoContentResponse(),
				ExecutionResult.ExecutionStatus.SUCCESSFUL);

	}

	private ExecutionResult<MessageResponse> validateRequestPayload() {
		try {
			JsonObject preferenceSettings = this.context.request().getJsonObject(REQ_KEY_PREFERENCE_SETTING, null);
			if (preferenceSettings == null || preferenceSettings.isEmpty()) {
				return new ExecutionResult<>(null, ExecutionStatus.CONTINUE_PROCESSING);
			}

			JsonObject standardPreferences = preferenceSettings.getJsonObject(REQ_KEY_STANDARD_PREF, null);
			if (standardPreferences != null && !standardPreferences.isEmpty()) {
				for (String subject : standardPreferences.fieldNames()) {
					Long count = Base.count(AJEntityTaxonomySubject.TABLE,
							AJEntityTaxonomySubject.FETCH_SUBJECT_BY_GUT_AND_FWCODE, subject,
							standardPreferences.getString(subject));
					if (count < 1) {
						LOGGER.warn("invalid subject preference provided '{}' and framework '{}'", subject,
								standardPreferences.getString(subject));
						return new ExecutionResult<>(MessageResponseFactory.createInvalidRequestResponse(
								"Invalid subject preference provided"), ExecutionResult.ExecutionStatus.FAILED);
					}
				}
			}

			JsonArray languagePreference = preferenceSettings.getJsonArray(REQ_KEY_LANGUAGE_PREF, null);

			if (languagePreference != null && !languagePreference.isEmpty()) {
				Set<Integer> languageIds = new HashSet<>();
				languagePreference.forEach(langId -> {
					languageIds.add(Integer.valueOf(langId.toString()));
				});
				
				if (languagePreference.size() != languageIds.size()) {
					LOGGER.warn("non unique language preferences provided, aborting");
					return new ExecutionResult<>(
							MessageResponseFactory.createInvalidRequestResponse("non unique language preference provided"),
							ExecutionResult.ExecutionStatus.FAILED);
				}
 
				Long count = Base.count(AJEntityGooruLanguage.TABLE, AJEntityGooruLanguage.FETCH_LANGUAGES_BY_IDS,
						HelperUtility.toPostgresArrayInt(languageIds));
				if (count != languageIds.size()) {
					LOGGER.warn("invalid language preferences provided, aborting");
					return new ExecutionResult<>(
							MessageResponseFactory.createInvalidRequestResponse("Invalid language preference provided"),
							ExecutionResult.ExecutionStatus.FAILED);
				}
			}
			return new ExecutionResult<>(null, ExecutionStatus.CONTINUE_PROCESSING);
		} catch (Throwable t) {
			LOGGER.error("unable to validate request", t);
			return new ExecutionResult<>(
					MessageResponseFactory.createInternalErrorResponse("unable to validate request"),
					ExecutionResult.ExecutionStatus.FAILED);
		}
	}

}
