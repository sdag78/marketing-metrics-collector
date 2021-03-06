package info.novatec.metricscollector.github.metrics;

import java.time.LocalDate;
import javax.json.JsonArray;
import javax.json.JsonObject;

import org.springframework.stereotype.Component;

import info.novatec.metricscollector.commons.DailyClicks;
import info.novatec.metricscollector.commons.RestService;
import info.novatec.metricscollector.github.GithubMetricsResult;


/**
 * Github API v3 provides page-visits for the last fortnight. These data will be updated every morning, including
 * 'today' from midnight to mordning. To ensure correct data for the whole day, this method checks the latest entry
 * for its timestamp. If it is 'today' the return value is the entry for 'yesterday' resp. next to the last one.
 */
@Component
public class YesterdaysVisits extends GithubMetric {

    public YesterdaysVisits(RestService restService, GithubMetricsResult metrics) {
        super(restService, metrics);
    }

    @Override
    public void collect() {
        String url = BASE_URL + projectName + "/traffic/views";
        JsonObject visitors = createJsonObject(restService.sendRequest(url).getBody());
        JsonObject visits = getYesterdaysVisits(visitors.getJsonArray("views"));
        String timestamp = visits.getString("timestamp");
        int totalVisits = visits.getInt("count");
        int uniqueVisits = visits.getInt("uniques");
        metrics.setDailyVisits(new DailyClicks(timestamp, totalVisits, uniqueVisits));
    }

    /**
     * Github API v3 provides page-visits for the last fortnight. These data will be updated every morning, including
     * 'today' from midnight to morning. To ensure correct data for the whole day, this method checks the latest entry
     * for its timestamp. If it is 'today' the return value is the entry for 'yesterday' resp. next to the last one.
     *
     * @param visits List of visits-metrics received from github api v3
     * @return The entry for yesterdays visits statistics
     */
    private JsonObject getYesterdaysVisits(JsonArray visits) {
        JsonObject yesterdaysVisits = visits.getJsonObject(visits.size() - 1);
        String timestamp = yesterdaysVisits.getString("timestamp").split("T")[0];

        if (LocalDate.parse(timestamp).toEpochDay() == LocalDate.now().toEpochDay()) {
            yesterdaysVisits = visits.getJsonObject(visits.size() - 2);
        }
        return yesterdaysVisits;
    }
}
