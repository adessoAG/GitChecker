package de.adesso.gitchecker.repositorycheck.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExitUtils {

    public static ApplicationContext context = null;
    private static int exitCode = 0;

    public static int getExitCode() {
        return exitCode;
    }

    public static void configFileNotFound() {
        System.err.println("Configuration file not found.");
        exitCode = 1;
    }

    public static void rulesetNotParsed() {
        System.err.println("Ruleset could not be parsed.");
        exitCode = 2;
    }

    public static void rulesetInvalid(String reason) {
        System.err.println("Ruleset invalid: " + reason);
        exitCode = 3;
    }

    public static void resourcesNotParsed() {
        System.err.println("Resources could not be parsed.");
        exitCode = 4;
    }

    public static void projectNotFetched() {
        System.err.println("Project could not be fetched.");
        exitCode = 5;
        exitApplication();
    }

    public static void repositoryNotFetched() {
        System.err.println("Repository could not be fetched.");
        exitCode = 6;
        exitApplication();
    }

    public static void branchesNotFetched() {
        System.err.println("Branches could not be fetched.");
        exitCode = 7;
        exitApplication();
    }

    public static void pullRequestNotFetched() {
        System.err.println("Pull requests could not be fetched.");
        exitCode = 8;
        exitApplication();
    }

    public static void commitsNotFetched() {
        System.err.println("Commits could not be fetched.");
        exitCode = 9;
        exitApplication();
    }

    public static void issuesDiscovered() {
        exitCode = 10;
    }

    public static void authenticationFailed() {
        System.err.println("Could not authorize at server.");
        exitCode = 11;
        exitApplication();
    }

    private static void exitApplication() {
        System.exit(SpringApplication.exit(context, () -> exitCode));
    }
}
