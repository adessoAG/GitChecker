package de.adesso.gitchecker.repositorycheck.adapter;

import de.adesso.gitchecker.repositorycheck.port.driven.ReadConfigFilePort;
import de.adesso.gitchecker.repositorycheck.utils.ExitUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Paths;

@Component
public class ReadConfigFileAdapter implements ReadConfigFilePort {

    @Value("${configfile}")
    private String rulesetFilepath;

    @Override
    public String readContent() {
        return fileContent(getConfigFile());
    }

    private File getConfigFile() {
        File rulesetFile = Paths.get(rulesetFilepath).toFile();
        if (!rulesetFile.exists() || !rulesetFile.isFile() || !rulesetFile.getName().endsWith(".json")) {
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
