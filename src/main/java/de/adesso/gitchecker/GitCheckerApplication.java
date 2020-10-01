package de.adesso.gitchecker;

import de.adesso.gitchecker.repositorycheck.utils.ExitUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GitCheckerApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(GitCheckerApplication.class);
        app.setWebApplicationType(WebApplicationType.NONE);

        try {
            System.exit(
                    SpringApplication.exit(
                            app.run(args),
                            ExitUtils::getExitCode));
        } catch (Exception e) {}
    }
}
