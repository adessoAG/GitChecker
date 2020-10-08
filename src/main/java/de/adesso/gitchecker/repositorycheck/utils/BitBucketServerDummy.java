package de.adesso.gitchecker.repositorycheck.utils;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Paths;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Component
public class BitBucketServerDummy {

    @Value("${wiremock.project}")
    private String projectFilePath;
    @Value("${wiremock.projects}")
    private String projectsFilePath;
    @Value("${wiremock.repo}")
    private String repositoryFilePath;
    @Value("${wiremock.repos}")
    private String repositoriesFilePath;
    @Value("${wiremock.branches}")
    private String branchFilePath;
    @Value("${wiremock.commits}")
    private String commitFilePath;
    @Value("${wiremock.commits.distinct}")
    private String distinctCommitFilePath;
    @Value("${wiremock.pullRequests}")
    private String pullRequestFilePath;

    private WireMockServer mockServer;

    public void init() {
        mockServer = new WireMockServer(8081);
        mockServer.start();
        stubProject();
        stubRepositories();
        stubBranches();
        stubCommits();
        stubPullRequests();
    }

    public void kill() {
        mockServer.stop();
    }

    private void stubProject() {
        mockServer.stubFor(
                get(urlPathEqualTo("/rest/api/1.0/projects/PROJECTKEY"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(fileContent(getFile(projectFilePath))))
        );
        mockServer.stubFor(
                get(urlPathEqualTo("/rest/api/1.0/projects/"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(fileContent(getFile(projectsFilePath))))
        );
    }

    private void stubRepositories() {
        mockServer.stubFor(
                get(urlPathEqualTo("/rest/api/1.0/projects/PROJECTKEY/repos/"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(fileContent(getFile(repositoriesFilePath))))
        );
        mockServer.stubFor(
                get(urlPathEqualTo("/rest/api/1.0/projects/PROJECTKEY/repos/backend"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(fileContent(getFile(repositoryFilePath))))
        );
    }

    private void stubBranches() {
        mockServer.stubFor(
                get(urlPathEqualTo("/rest/api/1.0/projects/PROJECTKEY/repos/backend/branches/"))
                .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(fileContent(getFile(branchFilePath))))
        );
    }

    private void stubCommits() {
        mockServer.stubFor(
                get(urlPathEqualTo("/rest/api/1.0/projects/PROJECTKEY/repos/backend/commits/"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(fileContent(getFile(commitFilePath))))
        );
        mockServer.stubFor(
                get(urlPathEqualTo("/rest/api/1.0/projects/PROJECTKEY/repos/backend/compare/commits/"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(fileContent(getFile(distinctCommitFilePath))))
        );
    }

    private void stubPullRequests() {
        mockServer.stubFor(
                get(urlPathEqualTo("/rest/api/1.0/projects/PROJECTKEY/repos/backend/pull-requests/"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(fileContent(getFile(pullRequestFilePath))))
        );
    }

    private File getFile(String filepath) {
        File rulesetFile = Paths.get(filepath).toFile();
        if (!rulesetFile.exists() || !rulesetFile.isFile()) {
            ExitUtils.configFileNotFound();
        }
        return rulesetFile;
    }

    private String fileContent(File file) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            new BufferedReader(new FileReader(file))
                    .lines()
                    .forEachOrdered(stringBuilder::append);
        } catch (FileNotFoundException e) {
            ExitUtils.configFileNotFound();
        }
        return stringBuilder.toString().trim();
    }
}
