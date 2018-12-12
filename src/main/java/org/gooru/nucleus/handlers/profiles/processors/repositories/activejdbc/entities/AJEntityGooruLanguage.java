
package org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

/**
 * @author szgooru Created On 03-Dec-2018
 */
@Table("gooru_language")
public class AJEntityGooruLanguage extends Model {
	
	public static final String TABLE = "gooru_language";
	public static final String FETCH_LANGUAGES_BY_IDS = "id = ANY(?::int[])";
}
