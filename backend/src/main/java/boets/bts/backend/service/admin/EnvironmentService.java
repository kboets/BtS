package boets.bts.backend.service.admin;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class EnvironmentService {

    private final EnvironmentProperties environmentProperties;
    private final DateTimeFormatter applicationLogDateMapper = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final DateTimeFormatter serverLogDateMapper = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final Logger logger = LoggerFactory.getLogger(EnvironmentService.class);


    public EnvironmentService(EnvironmentProperties environmentProperties) {
        this.environmentProperties = environmentProperties;
    }


    /**
     * Runs at startup, and each hour after.
     */
    @Scheduled(fixedRate = 3600000)
    protected void scheduleCleanUp() {
        logger.info("Start schedule cleanup");
        this.cleanUpApplicationLogs();
        this.cleanUpServerLogs();
    }

    protected void cleanUpApplicationLogs() {
        Path appLogDirectory = Paths.get(environmentProperties.getApplicationLog());
        try {
            List<String> fileNames = listFilesUsingFilesList(appLogDirectory);
            for (String applicationLog: fileNames) {
                if (isApplicationLogOlderAsThreeDays(applicationLog)) {
                    String fileNameToDelete = environmentProperties.getApplicationLog() +"\\" + applicationLog;
                    Path logFilePath = Paths.get(fileNameToDelete);
                    Files.delete(logFilePath);
                }
            }
        } catch (IOException ioException) {
            logger.error("Could not clean up logs {} ", ioException.getCause().getMessage());
        }
    }

    protected void cleanUpServerLogs() {
        Path appLogDirectory = Paths.get(environmentProperties.getServerLog());
        List<String> fileNamesToBeDeleted = new ArrayList<>();
        try {
            List<String> fileNames = listFilesUsingFilesList(appLogDirectory);
            for (String serverLog: fileNames) {
                if (isServerLogOlderAsThreeDays(serverLog)) {
                    fileNamesToBeDeleted.add(serverLog);
                }
            }

            for (String fileNameToBeDeleted: fileNamesToBeDeleted) {
                this.deleteFile(fileNameToBeDeleted);
            }
        } catch (IOException ioException) {
            logger.error("Could not clean up logs ", ioException);
        }
    }

    private void deleteFile(String fileNameToBeDeleted) {
        String fileNameToDelete = environmentProperties.getServerLog() +"\\" + fileNameToBeDeleted;
        Path logFilePath = Paths.get(fileNameToDelete);
        try {
            if (logFilePath.toFile().exists()) {
                Files.delete(logFilePath);
            }
        } catch (IOException ioException) {
            logger.warn("File could not be deleted {} ", fileNameToBeDeleted);
        }

    }

    private List<String> listFilesUsingFilesList(Path dir) throws IOException {
        try (Stream<Path> stream = Files.list(dir)) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        }
    }

    private boolean isApplicationLogOlderAsThreeDays(String fileName) {
        if (StringUtils.contains(fileName, "-")) {
            String log1 = StringUtils.substringAfter(fileName, "-");
            String dateOfFile = StringUtils.substringBeforeLast(log1, "-");
            LocalDate todayMinusThree = LocalDate.now().minusDays(3);
            return LocalDate.parse(dateOfFile, applicationLogDateMapper).isBefore(todayMinusThree);

        }
        return false;
    }

    private boolean isServerLogOlderAsThreeDays(String fileName) {
        String dateOfFile = StringUtils.substringBetween(fileName, ".");
        if (dateOfFile != null) {
            LocalDate todayMinusThree = LocalDate.now().minusDays(3);
            return LocalDate.parse(dateOfFile, serverLogDateMapper).isBefore(todayMinusThree);
        }
        return false;
    }
}
