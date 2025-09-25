package at.ac.tuwien.refop.adapters.in.rest.constants;

public class FundingEntityMethodPath {

    private FundingEntityMethodPath() {
        throw new IllegalStateException("Utility class");
    }

    public static final String PATH_PARAM_ID = "id";
    public static final String PATH_PARAM_FUNDER_ID = "funderId";

    public static final String QUERY_PARAM_SEARCH_TERM = "term";
    public static final String QUERY_PARAM_FUNDER_ID = "funderId";
    public static final String QUERY_PARAM_STATUS = "status";

    public static final String ADD_ENTITY = "/add";
    public static final String UPDATE_ENTITY = "/update";
    public static final String DELETE_ENTITY_BY_ID_REPLACE_PARAMTER = "/delete/{id}";

    public static final String ENTITY_BY_ID_REPLACE_PARAMETER = "/detail/{id}";
    public static final String ENTITY_BY_ID_APPEND_PARAMETER = "/detail/";
    public static final String ENTITY_UPDATE_SUBSCRIPTIONS = "/{id}/subscriptions";
    public static final String ENTITY_LIST = "/detail/list";
    public static final String ENTITY_LIST_BY_FUNDER_REPLACE_PARAMTER = "detail/list/funder/{funderId}";

    public static final String REFERENCE_BY_ID_REPLACE_PARAMETER = "/ref/{id}";
    public static final String REFERENCE_BY_ID_APPEND_PARAMETER = "/ref/";
    public static final String REFERENCE_LIST = "/ref/list";
    public static final String REFERENCE_LIST_BY_FUNDER_REPLACE_PARAMETER = "/ref/list/funder/{funderId}";
    public static final String REFERENCE_LIST_BY_FUNDER_APPEND_PARAMETER = "/ref/list/funder/";
    public static final String REFERENCE_LIST_SEARCH_ADD_QUERY_PARAM = "/ref/list/search";
}
