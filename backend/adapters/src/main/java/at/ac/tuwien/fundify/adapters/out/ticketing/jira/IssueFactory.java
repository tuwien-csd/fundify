package at.ac.tuwien.fundify.adapters.out.ticketing.jira;

import at.ac.tuwien.fundify.adapters.common.jira.model.Comment;
import at.ac.tuwien.fundify.adapters.common.jira.model.IssueUpdateDetails;
import at.ac.tuwien.fundify.domain.ticketing.AppendableTicketCreate;
import at.ac.tuwien.fundify.domain.ticketing.TicketCreate;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class IssueFactory {

  private final JiraTicketBaseProperties baseProps;
  private static final String PROP_TYPE = "type";
  private static final int PROP_VERSION = 1;
  private static final String CONTENT_TYPE_TEXT = "text";
  private static final String FIELD_ID = "id";
  private static final String FIELD_FIELDS = "fields";
  private static final String FIELD_VERSION = "version";
  private static final String FIELD_LABEL = "labels";
  private static final String FIELD_PROJECT = "project";
  private static final String FIELD_DESCRIPTION = "description";
  private static final String FIELD_CONTENT = "content";
  private static final String FIELD_SUMMARY = "summary";
  private static final String FIELD_ISSUE_TYPE = "issuetype";


  public IssueUpdateDetails createIssuePayload(TicketCreate createInput) {
    IssueUpdateDetails issuePayload = new IssueUpdateDetails();

    Map<String, Object> fields = new HashMap<>();

    // Description field as Atlassian Document Format
    Map<String, Object> description = getDescription(createInput);
    fields.put(FIELD_DESCRIPTION, description);

    // Issue type
    Map<String, String> issueType = new HashMap<>();
    issueType.put(FIELD_ID, this.baseProps.getIssueTypeId());
    fields.put(FIELD_ISSUE_TYPE, issueType);

    // Labels
    List<String> labels = this.baseProps.getLabels();
    if (createInput.category() != null) {
      // Add category as label
      labels.add(createInput.category());
    }
    fields.put(FIELD_LABEL, labels);

    // Project
    Map<String, String> project = new HashMap<>();
    project.put(FIELD_ID, this.baseProps.getProjectId());
    fields.put(FIELD_PROJECT, project);

    // Summary
    fields.put(FIELD_SUMMARY, createInput.subject());

    issuePayload.put(FIELD_FIELDS, fields);

    return issuePayload;
  }

  public IssueUpdateDetails createIssuePayload(AppendableTicketCreate input) {
    IssueUpdateDetails issuePayload = new IssueUpdateDetails();

    Map<String, Object> fields = new HashMap<>();

    // Description field as Atlassian Document Format
    Map<String, Object> description = new HashMap<>();
    description.put(PROP_TYPE, "doc");
    description.put(FIELD_VERSION, PROP_VERSION);

    List<Map<String, Object>> content = new ArrayList<>();

    // Message section
    if (!isBlank(input.description())) {
      content.add(paragraph(boldText("Description:")));
      content.add(paragraph(text(input.description())));
      content.add(paragraph(boldText("Additional Text:")));
      for (String text : input.additionalText()) {
        if (!isBlank(text)) {
          content.add(paragraph(text(text)));
        }
      }
    }

    description.put(FIELD_CONTENT, content);
    fields.put(FIELD_DESCRIPTION, description);



    // Summary
    fields.put(FIELD_SUMMARY, input.ticketKey());

    // Issue type
    Map<String, String> issueType = new HashMap<>();
    issueType.put(FIELD_ID, this.baseProps.getIssueTypeId());
    fields.put(FIELD_ISSUE_TYPE, issueType);

    // Project
    Map<String, String> project = new HashMap<>();
    project.put(FIELD_ID, this.baseProps.getProjectId());
    fields.put(FIELD_PROJECT, project);

    issuePayload.put(FIELD_FIELDS, fields);

    return issuePayload;
  }

  public Comment createCommentPayload(List<String> texts) {
    Map<String, Object> body = new HashMap<>();
    body.put(PROP_TYPE, "doc");
    body.put(FIELD_VERSION, PROP_VERSION);

    List<Map<String, Object>> content = new ArrayList<>();

    for (String text : texts) {
      if (!isBlank(text)) {
        content.add(paragraph(text(text)));
      }
    }

    body.put(FIELD_CONTENT, content);
    Comment comment = new Comment();
    comment.put("body", body); // For some unknown reason, using comment.setBody(body) does not work.
    return comment;
  }

  /**
   * Represents the main description of the Jira ticket.
   */
  private static Map<String, Object> getDescription(TicketCreate input) {
    Map<String, Object> doc = new HashMap<>();
    doc.put(PROP_TYPE, "doc");
    doc.put(FIELD_VERSION, PROP_VERSION);

    List<Map<String, Object>> content = new ArrayList<>();

    // Heading: "Ticket Details"
    content.add(heading(3, "Ticket Details"));

    // Bullet list with key/value pairs (skip blanks)
    List<Map<String, Object>> listItems = new ArrayList<>();
    addListItemIfPresent(listItems, "Name", input.name());
    addListItemIfPresent(listItems, "Email", input.email());
    addListItemIfPresent(listItems, "Category", input.category());
    addListItemIfPresent(listItems, "Kind of institution", input.kindOfInstitution());

    if (!listItems.isEmpty()) {
      Map<String, Object> bulletList = new HashMap<>();
      bulletList.put(PROP_TYPE, "bulletList");
      bulletList.put(FIELD_CONTENT, listItems);
      content.add(bulletList);
    }

    // Message section
    if (!isBlank(input.message())) {
      content.add(paragraph(boldText("Message:")));
      content.add(paragraph(text(input.message())));
    }

    doc.put(FIELD_CONTENT, content);
    return doc;
  }

  private static Map<String, Object> heading(int level, String text) {
    Map<String, Object> heading = new HashMap<>();
    heading.put(PROP_TYPE, "heading");

    Map<String, Object> attrs = new HashMap<>();
    attrs.put("level", level);
    heading.put("attrs", attrs);

    List<Map<String, Object>> content = new ArrayList<>();
    content.add(text(text));
    heading.put(FIELD_CONTENT, content);

    return heading;
  }

  @SafeVarargs
  private static Map<String, Object> paragraph(Map<String, Object>... nodes) {
    Map<String, Object> p = new HashMap<>();
    p.put(PROP_TYPE, "paragraph");
    List<Map<String, Object>> content = new ArrayList<>();
    Collections.addAll(content, nodes);
    p.put(FIELD_CONTENT, content);
    return p;
  }

  private static Map<String, Object> listItem(Map<String, Object> paragraph) {
    Map<String, Object> item = new HashMap<>();
    item.put(PROP_TYPE, "listItem");
    List<Map<String, Object>> content = new ArrayList<>();
    content.add(paragraph);
    item.put(FIELD_CONTENT, content);
    return item;
  }

  private static void addListItemIfPresent(List<Map<String, Object>> listItems, String label, String value) {
    if (isBlank(value)) {
      return;
    }
    Map<String, Object> p = paragraph(boldText(label + ": "), text(value));
    listItems.add(listItem(p));
  }

  private static Map<String, Object> text(String value) {
    Map<String, Object> node = new HashMap<>();
    node.put(PROP_TYPE, CONTENT_TYPE_TEXT);
    node.put(CONTENT_TYPE_TEXT, value);
    return node;
  }

  private static Map<String, Object> boldText(String value) {
    Map<String, Object> node = new HashMap<>();
    node.put(PROP_TYPE, CONTENT_TYPE_TEXT);
    node.put(CONTENT_TYPE_TEXT, value);
    List<Map<String, Object>> marks = new ArrayList<>();
    Map<String, Object> strong = new HashMap<>();
    strong.put(PROP_TYPE, "strong");
    marks.add(strong);
    node.put("marks", marks);
    return node;
  }

  private static boolean isBlank(String s) {
    return s == null || s.isBlank();
  }
}