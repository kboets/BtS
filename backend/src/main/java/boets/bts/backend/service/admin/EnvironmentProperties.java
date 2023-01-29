package boets.bts.backend.service.admin;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationProperties(prefix = "environment")
@ConfigurationPropertiesScan
public class EnvironmentProperties {

    private String applicationLog;
    private String serverLog;

    public String getApplicationLog() {
        return applicationLog;
    }

    public void setApplicationLog(String applicationLog) {
        this.applicationLog = applicationLog;
    }

    public String getServerLog() {
        return serverLog;
    }

    public void setServerLog(String serverLog) {
        this.serverLog = serverLog;
    }
}
