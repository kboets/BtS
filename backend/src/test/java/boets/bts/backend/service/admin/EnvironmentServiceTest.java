package boets.bts.backend.service.admin;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EnvironmentServiceTest {
    @InjectMocks
    private EnvironmentService environmentService;
    @Mock
    private EnvironmentProperties environmentProperties;

    @Test(expected = Test.None.class)
    public void cleanUpApplicationLogs_shouldNotThrowErrors() {
        when(environmentProperties.getApplicationLog()).thenReturn("C:\\tmp\\logs\\develop");
        environmentService.cleanUpApplicationLogs();
    }

    @Test(expected = Test.None.class)
    public void cleanUpServerLogs_shouldNotThrowErrors() {
        when(environmentProperties.getServerLog()).thenReturn("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\logs");
        environmentService.cleanUpServerLogs();
    }
}