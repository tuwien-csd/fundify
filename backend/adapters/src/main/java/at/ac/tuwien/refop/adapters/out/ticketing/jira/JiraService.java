package at.ac.tuwien.refop.adapters.out.ticketing.jira;

import at.ac.tuwien.refop.adapters.common.jira.ApiClient;
import at.ac.tuwien.refop.adapters.common.jira.ApiException;
import at.ac.tuwien.refop.adapters.common.jira.api.IssueCommentsApi;
import at.ac.tuwien.refop.adapters.common.jira.api.IssueSearchApi;
import at.ac.tuwien.refop.adapters.common.jira.api.IssuesApi;
import at.ac.tuwien.refop.adapters.common.jira.model.IssueBean;
import at.ac.tuwien.refop.application.port.out.ticketing.TicketingService;
import at.ac.tuwien.refop.domain.common.exceptions.ExternalApiErrorException;
import at.ac.tuwien.refop.domain.common.exceptions.FundifyException;
import at.ac.tuwien.refop.domain.ticketing.AppendableTicketCreate;
import at.ac.tuwien.refop.domain.ticketing.TicketCreate;
import io.smallrye.common.annotation.Identifier;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.extern.jbosslog.JBossLog;

@ApplicationScoped
@JBossLog
public class JiraService implements TicketingService {

  public JiraService(@Identifier("jiraApiClient") ApiClient jiraApiClient,
      IssueFactory issueFactory, JiraTicketBaseProperties baseProps) {
    this.issuesApi = new IssuesApi(jiraApiClient);
    this.issueSearchApi = new IssueSearchApi(jiraApiClient);
    this.issueCommentsApi = new IssueCommentsApi(jiraApiClient);
    this.issueFactory = issueFactory;
    this.jiraBaseProps = baseProps;
  }

  private final IssueFactory issueFactory;
  private final IssuesApi issuesApi;
  private final IssueSearchApi issueSearchApi;
  private final IssueCommentsApi issueCommentsApi;
  private final JiraTicketBaseProperties jiraBaseProps;
  private static final String SUMMARY_FILTER_TEMPLATE = "summary ~ \"%s\" AND project = %s AND statusCategory != Done ORDER BY created ASC";

  public void createTicket(TicketCreate createInput) throws FundifyException {
    log.info("Creating a Jira issue...");
    var issue = issueFactory.createIssuePayload(createInput);
    try {
      var created = this.issuesApi.createIssue(issue, false);
      log.info("Created Jira issue: " + created);
    } catch (ApiException e) {
      throw new ExternalApiErrorException(e.getMessage(), e.getCode());
    }
  }

  @Override
  public void createOrAppend(AppendableTicketCreate input) {
    log.infof("Creating or appending a Jira issue for ticket key %s...", input.ticketKey());
    try {
      String jqlQuery = String.format(SUMMARY_FILTER_TEMPLATE, input.ticketKey(),
          jiraBaseProps.getProjectId());

      List<IssueBean> searchResults = issueSearchApi.searchAndReconsileIssuesUsingJql(jqlQuery,
              null, null, null, null,
              null, null, null, null)
          .getIssues();

      Optional<String> matchingIssueId = Optional.ofNullable(searchResults)
          .stream()
          .flatMap(Collection::stream)
          .findFirst()
          .map(IssueBean::getId);

      if (matchingIssueId.isEmpty()) {
        log.infof("No existing ticket found with key %s and JQL query '%s'. Creating it...",
            input.ticketKey(), jqlQuery);

        var createPayload = issueFactory.createIssuePayload(input);
        var createdIssue = issuesApi.createIssue(createPayload, false);

        log.infof("Created Jira issue with key %s", createdIssue.getId());
      } else {
        log.infof(
            "Found existing ticket matching key '%s' in jira issue id '%s', appending to it as comment...",
            input.ticketKey(), matchingIssueId.get());

        var payload = issueFactory.createCommentPayload(input.additionalText());
        var createdComment = issueCommentsApi.addComment(matchingIssueId.get(), payload, null);

        log.infof("Successfully appended comment with id '%s' to issue id '%s'",
            createdComment.getId(), matchingIssueId.get());
      }
    } catch (ApiException e) {
      throw new RuntimeException(e);
    }

  }


}
