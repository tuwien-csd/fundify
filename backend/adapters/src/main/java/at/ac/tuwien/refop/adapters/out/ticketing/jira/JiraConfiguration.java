package at.ac.tuwien.refop.adapters.out.ticketing.jira;

import at.ac.tuwien.refop.adapters.common.jira.ApiClient;
import io.smallrye.common.annotation.Identifier;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Produces;
import java.util.Base64;
import java.util.List;
import org.eclipse.microprofile.config.inject.ConfigProperty;

public class JiraConfiguration {
  private final static String AUTHORIZATION_HEADER = "Authorization";

  @ConfigProperty(name = "fundify.ticketing.jira.base-url")
  String jiraBaseUrl;

  @ConfigProperty(name = "fundify.ticketing.jira.auth.user-email")
  String userEmail;

  @ConfigProperty(name = "fundify.ticketing.jira.auth.api-token")
  String apiToken;

  @ConfigProperty(name = "fundify.ticketing.jira.ticket-properties.project-id")
  String jiraProjectId;

  @ConfigProperty(name = "fundify.ticketing.jira.ticket-properties.issue-type-id")
  String jiraIssueTypeId;

  @ConfigProperty(name = "fundify.ticketing.jira.ticket-properties.labels")
  List<String> jiraLabels;

  @Produces
  @ApplicationScoped
  @Identifier("jiraApiClient")
  ApiClient getApiClient() {
    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath(jiraBaseUrl);
    apiClient.addDefaultHeader(AUTHORIZATION_HEADER, this.getAuthorizationHeaderValue());

    return apiClient;
  }

  @Produces
  @ApplicationScoped
  JiraTicketBaseProperties getJiraTicketBaseProperties() {
    return new JiraTicketBaseProperties(
        jiraProjectId,
        jiraIssueTypeId,
        jiraLabels
    );
  }



  /**
   * Generates the Authorization header value for Jira API requests.
   * Refer to the <a href="https://developer.atlassian.com/cloud/jira/platform/basic-auth-for-rest-apis/">Jira documentation</a> for more details.
   * @return
   */
  private String getAuthorizationHeaderValue() {
    final String userWithToken = userEmail + ":" + apiToken;
    return "Basic " + Base64.getEncoder().encodeToString(userWithToken.getBytes());
  }

}
