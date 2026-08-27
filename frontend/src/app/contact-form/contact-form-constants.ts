export const CONTACT_FORM_CONSTANTS = {
  CONTAINER: {
    HEADER: 'Contact us',
    BODY: 'Please fill out the form on this page to get in touch with us.',
  },
  SUBJECT: {
    LABEL: 'Subject',
    PLACEHOLDER: 'Enter a subject',
    ERROR: {
      REQUIRED: 'Subject is required',
      INVALID: 'Invalid input',
    },
  },
  FIRST_NAME: {
    LABEL: 'Given name',
    PLACEHOLDER: 'Enter your given name',
    ERROR: {
      REQUIRED: 'Given name is required',
      INVALID: 'Invalid input',
    },
  },
  LAST_NAME: {
    LABEL: 'Surname',
    PLACEHOLDER: 'Enter your surname',
    ERROR: {
      REQUIRED: 'Surname is required',
      INVALID: 'Invalid input',
    },
  },
  CATEGORY: {
    LABEL: 'Category',
    PLACEHOLDER: 'Select a category',
    ERROR: {
      REQUIRED: 'Category is required',
      INVALID: 'Invalid input',
    },
  },
  EMAIL: {
    LABEL: 'Email',
    PLACEHOLDER: 'Enter your email address',
    ERROR: {
      REQUIRED: 'Email is required',
      INVALID: 'Invalid email format',
    },
  },
  KIND_OF_INSTITUTION: {
    LABEL: 'Kind of institution',
    FUNDER: 'Funder',
    RESEARCH_INSTITUTE: 'Research Institute',
  },
  MESSAGE: {
    LABEL: 'Message',
    PLACEHOLDER: 'Enter your message here',
    ERROR: {
      REQUIRED: 'Message is required',
      MIN_LENGTH: 'Message must be at least 10 characters long',
    },
  },
  SUBMIT_BUTTON: {
    LABEL: 'Submit',
  },
  SUCCESS_MESSAGE: {
    TITLE: 'Thank you for your message!',
    BODY: 'We will get back to you as soon as possible.',
  },
} as const;

export const CONTACT_FORM_CATEGORIES = [
  {
    value: 'registration',
    label: 'Registration',
  },
  {
    value: 'error',
    label: 'Error report',
  },
  {
    value: 'other',
    label: 'Other',
  },
] as const satisfies ContactFormCategory[];
export type ContactFormCategories =
  (typeof CONTACT_FORM_CATEGORIES)[number]['value'];

export type ContactFormCategory = { label: string; value: string };
