package de.adesso.gitchecker.repositorycheck.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class BitBucketResources {

  private String serverURL;
  private BitBucketProject project = new BitBucketProject();
  private BitBucketRepository targetRepository = new BitBucketRepository();
  private Map<IssueType, List<Issue>> repositoryIssues = new HashMap<>();

  @JsonProperty("resources")
  private void unpackFromConfig(Map<String, String> resources) {
    targetRepository.setProject(project);
    project.getRepositories().add(targetRepository);
    serverURL = resources.get("serverURL");
    project.setKey(resources.get("project"));
    targetRepository.setSlug(resources.get("targetRepository"));
  }
}
