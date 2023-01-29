package boets.bts.backend;

import boets.bts.backend.service.admin.EnvironmentProperties;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableConfigurationProperties(EnvironmentProperties.class)
@SpringBootApplication
public class BackendApplication  {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}


}
