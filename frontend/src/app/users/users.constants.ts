export const USER_PERMISSIONS_CONSTANTS = {
  TITLE: 'User Permissions',
  USER_LABEL: 'User',
  ROLES_LABEL: 'Roles',
  AFFILIATION_ID_LABEL: 'Affiliation ID',
  AFFILIATION_ID_PLACEHOLDER: 'Enter Affiliation ID',
  UPDATE_SUCCESS: 'User permissions updated',
  DELETE_SUCCESS: 'User permissions deleted',
  TABLE_TITLE_EXISTING: 'Existing',
  TABLE_TITLE_NEW: 'Create new or change',
  EMAIL_LABEL: 'Email',
  TABLE_COLUMNS: [
    { field: 'email', header: 'Email' },
    { field: 'roles', header: 'Roles' },
    { field: 'affiliationId', header: 'Affiliation ID' },
    { field: 'actions', header: '' },
  ],
  ERRORS: {
    GENERIC_ERROR: 'Failed to update user permissions',
    DELETE_ERROR: 'Failed to delete user permissions',
  },
};

export const USER_ROLE_OPTIONS = [
  { value: 'ADMIN', label: 'Admin' },
  { value: 'FUNDER', label: 'Funder' },
  { value: 'ANNOTATOR', label: 'Annotator' },
  { value: 'EXTERNAL_API_CLIENT', label: 'External API Client' },
];
