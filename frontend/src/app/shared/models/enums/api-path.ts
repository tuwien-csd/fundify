export enum ApiPath {
  ADD_ENTITY = '/add',
  UPDATE_ENTITY = '/update',
  DELETE_ENTITY = '/delete/',
  ENTITY_BY_ID_APPEND_PARAMETER = '/detail/', // append entity id
  ENTITY_LIST = '/detail/list',
  ENTITY_BY_ID = '/by-id',
  ENTITY_LIST_BY_FUNDER_APPEND_PARAMETER = '/detail/list/funder/', // append funder id
  REFERENCE_BY_ID_APPEND_PARAMETER = '/ref/', // append entity id
  REFERENCE_LIST = '/ref/list',
  REFERENCE_LIST_BY_FUNDER_APPEND_PARAMETER = '/ref/list/by-funder/', // append funder id
  REFERENCE_LIST_SEARCH_ADD_QUERY_PARAM = '/ref/list/search',
  QUERY_PARAM_SEARCH_TERM = 'term',
  QUERY_PARAM_FUNDER_ID = 'funderId',
  QUERY_PARAM_STATUS = 'status',
}
