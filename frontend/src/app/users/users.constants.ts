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
    INVALID_EMAIL: 'Please select an existing user or enter a valid email',
    REGISTRATION_DELETE_ERROR: 'Failed to remove the registration request',
  },
};

export const REGISTRATION_REQUESTS_CONSTANTS = {
  TABLE_TITLE: 'Registration requests',
  EMPTY: 'No pending registration requests',
  NAME_LABEL: 'Name',
  EMAIL_LABEL: 'Email',
  INSTITUTION_LABEL: 'Institution',
  MESSAGE_LABEL: 'Message',
  CREATED_AT_LABEL: 'Received',
  CREATE_USER_LABEL: 'Create user',
  REJECT_LABEL: 'Reject',
  TABLE_COLUMNS: [
    { field: 'name', header: 'Name' },
    { field: 'email', header: 'Email' },
    { field: 'kindOfInstitution', header: 'Institution' },
    { field: 'message', header: 'Message' },
    { field: 'createdAt', header: 'Received' },
    { field: 'actions', header: '' },
  ],
};

export const USER_ROLE_OPTIONS = [
  { value: 'ADMIN', label: 'Admin' },
  { value: 'FUNDER', label: 'Funder' },
  { value: 'ANNOTATOR', label: 'Annotator' },
  { value: 'EXTERNAL_API_CLIENT', label: 'External API Client' },
];
