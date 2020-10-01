package de.adesso.gitchecker.repositorycheck.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PullRequest {

  private Integer id;
  private String title;
  private String description;
  @JsonProperty("open")
  private Boolean isOpen;
  private String authorName;
  private List<String> reviewerNames = new ArrayList<>();
  private Commit from;
  private Commit to;

  @SuppressWarnings("unchecked")
  @JsonProperty(value = "reviewers")
  private void unpackReviewerNames(List<Map<String, Object>> reviewers) {
    reviewers.forEach(
        reviewer -> {
            Map<String, Object> user = (Map<String, Object>) reviewer.get("user");
            reviewerNames.add((String) user.get("name"));
        });
  }

  @SuppressWarnings("unchecked")
  @JsonProperty("author")
  private void unpackAuthorName(Map<String, Object> author) {
    Map<String, Object> user = (Map<String, Object>) author.get("user");
    authorName = (String) user.get("displayName");
  }

  @JsonProperty("fromRef")
  private void unpackFromCommit(Map<String, Object> ref) {
    from = new Commit();
    from.setId((String) ref.get("latestCommit"));
  }

  @JsonProperty("toRef")
  private void unpackToCommit(Map<String, Object> ref) {
    to = new Commit();
    to.setId((String) ref.get("latestCommit"));
  }
}
