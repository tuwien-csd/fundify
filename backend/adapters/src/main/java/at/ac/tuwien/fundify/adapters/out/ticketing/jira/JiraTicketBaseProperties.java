package at.ac.tuwien.fundify.adapters.out.ticketing.jira;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JiraTicketBaseProperties {

  private String projectId;
  private String issueTypeId;
  private List<String> labels;

}
