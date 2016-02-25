package org.gooru.nucleus.profiles.processors.repositories.activejdbc.entities;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("taxonomy_subject")
public class AJTaxonomySubject extends Model {

  public static final String CODE = "code";
  public static final String TITLE = "title";
  public static final String DEFAULT_SUBJECT_ID = "default_subject_id";
  
  public static final String SELECT_TX_SUBJECT = "default_subject_id = ? AND standard_framework_id = ?";
  
  public static final String SELECT_DEFAULT_CODE_FOR_SEARCH = "id = ? AND standard_framework_id = ?";
}
