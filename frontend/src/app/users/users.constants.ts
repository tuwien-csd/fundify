export const USER_PERMISSIONS_CONSTANTS = {
  TITLE: 'User Permissions',
  USER_LABEL: 'User',
  ROLES_LABEL: 'Roles',
  AFFILIATION_ID_LABEL: 'Affiliation ID',
  AFFILIATION_ID_PLACEHOLDER: 'Enter Affiliation ID',
  UPDATE_SUCCESS: 'User permissions updated',
  ERRORS: {
    GENERIC_ERROR: 'Failed to update user permissions',
  },
};

export const USER_ROLE_OPTIONS = [
  { value: 'ADMIN', label: 'Admin' },
  { value: 'FUNDER', label: 'Funder' },
  { value: 'ANNOTATOR', label: 'Annotator' },
  { value: 'EXTERNAL_API_CLIENT', label: 'External API Client' },
];
