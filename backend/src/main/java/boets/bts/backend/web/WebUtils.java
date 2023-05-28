package boets.bts.backend.web;

import okhttp3.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

public class WebUtils {

    private static final String API = "api";
    private static String BASE_URL = "https://api-football-v1.p.rapidapi.com/v2/";


    public static Optional<String> readJsonFileFromApi(String fileName) {
        try {
            File jsonFile = ResourceUtils.getFile(createFullPathName(fileName));
            String fileAsString = new String(Files.readAllBytes(jsonFile.toPath()));
            return Optional.of(fileAsString);
        } catch (IOException io) {
            return Optional.empty();
        }
    }

    public static Optional<String> readJsonFileFromApi(String fileName, int season) {
        try {
            File jsonFile = ResourceUtils.getFile(createFullPathName(fileName, season));
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

    private static String createFullPathName(String fileName, int season) {
        StringBuilder builder = new StringBuilder();
        builder.append("classpath:");
        builder.append(API);
        builder.append("/");
        builder.append(season);
        builder.append("/");
        builder.append(fileName);
        return builder.toString();
    }

    public static synchronized String buildUrl(String... vars) {
        StringBuilder builder = new StringBuilder();
        builder.append(BASE_URL);
        for(String arg: vars) {
            builder.append(arg);
            builder.append("/");
        }
        return builder.toString();
    }


    public static synchronized int getCurrentSeason() {
        LocalDate now = LocalDate.now();
        if(now.getMonthValue() < Month.JUNE.getValue()) {
            return now.getYear() - 1;
        }
        return  now.getYear();
    }

    public static synchronized boolean isWeekend() {
        LocalDate now = LocalDate.now();
        DayOfWeek today = now.getDayOfWeek();
        return (today.equals(DayOfWeek.FRIDAY)
                || today.equals(DayOfWeek.SATURDAY)
                || today.equals(DayOfWeek.SUNDAY));
    }

}
