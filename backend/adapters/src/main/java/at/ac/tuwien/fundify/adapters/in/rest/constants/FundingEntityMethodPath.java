package at.ac.tuwien.fundify.adapters.in.rest.constants;

public class FundingEntityMethodPath {

    private FundingEntityMethodPath() {
        throw new IllegalStateException("Utility class");
    }

    public static final String PATH_PARAM_ID = "id";
    public static final String QUERY_PARAM_STATUS = "status";

    public static final String BY_ID = "/{id}";
    public static final String ENTITY_UPDATE_SUBSCRIPTIONS = "/{id}/subscriptions";
    public static final String ENTITY_VERSIONS = "/{id}/versions";

    public static final String REFERENCE_LIST = "/refs";
    public static final String REFERENCE_BY_ID = "/refs/{id}";
    public static final String REFERENCE_SEARCH = "/refs/search";
}
