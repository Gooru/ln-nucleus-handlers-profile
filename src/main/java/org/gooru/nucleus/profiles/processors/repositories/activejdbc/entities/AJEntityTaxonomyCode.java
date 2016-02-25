package org.gooru.nucleus.profiles.processors.repositories.activejdbc.entities;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("taxonomy_code")
public class AJEntityTaxonomyCode extends Model {

  public static final String ID = "id";
  public static final String DEFAULT_CODE_ID = "default_code_id";
  
  public static final String SELECT_DEFAULT_CODE_FOR_SEARCH = "id = ? AND standard_framework_id = ?";
}
