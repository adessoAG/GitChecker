package de.adesso.gitchecker.repositorycheck.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Commit {

  private String id;
  private String message;
  private LocalDate timestamp;
  private String authorName;
  @JsonProperty("parents")
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private ArrayList<Commit> parentCommits = new ArrayList<>();
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private List<Commit> childCommits = new ArrayList<>();
  @EqualsAndHashCode.Exclude
  private Branch creatorBranch;

  public boolean isBranchPointCommit() {
    return childCommits.size() > 1;
  }

  public boolean isMergeCommit() {
    return parentCommits.size() > 1;
  }

  @JsonProperty("author")
  private void unpackAuthorName(Map<String, Object> author) {
    this.authorName = (String) (author.containsKey("displayName") ? author.get("displayName") : author.get("name"));
  }

  @JsonProperty("committerTimestamp")
  private void extractTimestamp(long millis) {
    Instant instant = Instant.ofEpochMilli(millis);
    timestamp = instant.atZone(ZoneId.systemDefault()).toLocalDate();
  }
}
