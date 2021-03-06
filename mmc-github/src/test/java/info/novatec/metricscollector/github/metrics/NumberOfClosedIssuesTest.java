package info.novatec.metricscollector.github.metrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import info.novatec.metricscollector.commons.RestService;
import info.novatec.metricscollector.github.GithubMetricsResult;
import info.novatec.metricscollector.github.TestConfig;
import info.novatec.metricscollector.github.data.DataProvider;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class NumberOfClosedIssuesTest {

    @MockBean
    private RestService restService;

    @Autowired
    private GithubMetricsResult metrics;

    private DataProvider data = new DataProvider();

    @Test
    public void collectTest() {
        String mockedResponseBody = "[{},{},{}]";
        ResponseEntity<String> mockedResponse = mock(ResponseEntity.class);

        when(restService.sendRequest(data.getRestURL() + "/issues/events")).thenReturn(mockedResponse);
        when(mockedResponse.getBody()).thenReturn(mockedResponseBody);

        NumberOfClosedIssues numberOfClosedIssues = new NumberOfClosedIssues(restService, metrics);
        numberOfClosedIssues.setProjectName(data.NON_EXISTING_PROJECT);
        numberOfClosedIssues.collect();
        assertThat(metrics.getClosedIssues()).isEqualTo(3);
    }
}
