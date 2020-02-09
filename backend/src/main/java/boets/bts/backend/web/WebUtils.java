package boets.bts.backend.web;

import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

public class WebUtils {

    private static final String API = "api";

    public static Optional<String> readJsonFileFromApi(String fileName) {
        try {
            File jsonFile = ResourceUtils.getFile(createFullPathName(fileName));
            String fileAsString = new String(Files.readAllBytes(jsonFile.toPath()));
            return Optional.of(fileAsString);
        } catch (IOException io) {
            return Optional.empty();
        }
    }

    private static String createFullPathName(String fileName) {
        StringBuilder builder = new StringBuilder();
        builder.append("classpath:");
        builder.append(API);
        builder.append("/");
        builder.append(fileName);
        return builder.toString();
    }
}
