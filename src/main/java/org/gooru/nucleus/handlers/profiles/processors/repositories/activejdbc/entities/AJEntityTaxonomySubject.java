
package org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

/**
 * @author szgooru Created On 03-Dec-2018
 */
@Table("taxonomy_subject")
public class AJEntityTaxonomySubject extends Model {
	
	public static final String TABLE = "taxonomy_subject";
	public static final String FETCH_SUBJECT_BY_GUT_AND_FWCODE = "default_taxonomy_subject_id = ? AND standard_framework_id = ?";
}
