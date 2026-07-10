export const PROGRAMS_CONSTANTS = {
  SEARCH_FIELD_LABEL: 'Search...',
  SEARCH_FIELD_PLACEHOLDER: 'Search by id, name or funder',
  TABLE_COLUMNS: [
    { header: 'Acronym', field: 'acronym' },
    { header: 'Name', field: 'name' },
    { header: 'Website', field: 'website' },
    { header: 'Funder', field: 'funder' },
    { header: 'Status', field: 'status' },
  ],
  ADD_BUTTON_LABEL: 'new',
};

export const PROGRAM_DETAILS_CONSTANTS = {
  TITLE: 'Program Details',
  TABLE_HEADERS: ['label', 'value'],
  NO_DATA_MESSAGE: 'No Data available',
  APPlICATION_LANGUAGE_LABEL: 'Application Language',
  RIS_ID_LABEL: 'RIS Synergy ID',
  RIS_ID_PLACEHOLDER: 'Enter RIS Synergy ID',
  NAME_LABEL: 'Name',
  NAME_PLACEHOLDER: 'Enter Name for Program',
  ACRONYM_LABEL: 'Acronym',
  ACRONYM_PLACEHOLDER: 'Enter Acronym for Program',
  PROGRAM_TRACK_LABEL: 'Program Track',
  PROGRAM_TRACK_PLACEHOLDER: 'Enter Program Track',
  TARGET_GROUPS_LABEL: 'Target Groups',
  CAREER_STAGES_LABEL: 'Career Stages',
  PROGRAM_DATE_RANGE_LABEL: 'Program Start/End Date',
  PROGRAM_DATE_RANGE_PLACEHOLDER: 'Enter Program Start/End Date',
  SUBJECTS_LABEL: 'Subjects',
  SUBJECT_SEARCH_LABEL: 'Search...',
  SUBJECT_SEARCH_PLACEHOLDER: 'Search by code or title',
  DESCRIPTION_LABEL: 'Description',
  DESCRIPTION_PLACEHOLDER: 'Enter Description for Program',
  CHARACTERISTICS_LABEL: 'Characteristics',
  FUNDING_SCHEMES_LABEL: 'Funding Scheme',
  LEGAL_TYPE_LABEL: 'Legal Type',
  WEBSITE_LABEL: 'Website',
  WEBSITE_PLACEHOLDER: 'URl for Website',
  FUNDER_LABEL: 'Funder',
  PLACEHOLDER: ' - ',
  PUBLISH_DISABLED_TOOLTIP: 'Program is not valid',
  ERRORS: {
    FETCH_ALL_ERROR:
      'An unexpected error occurred when retrieving all programs.',
    VERSION_HISTORY_ERROR:
      'An unexpected error occurred when retrieving the version history.',
  },
  MESSAGES: {
    CREATE: {
      SUCCESS: 'A new program has been successfully created.',
      ERROR: 'An unexpected error occurred when creating the program.',
    },
    UPDATE: {
      SUCCESS: 'The program has been successfully updated.',
      ERROR: 'An unexpected error occurred while updating the program.',
    },
    DELETE: {
      SUCCESS: 'The program has been successfully deleted.',
      ERROR: 'An unexpected error occurred when deleting the program.',
    },
  },
};
