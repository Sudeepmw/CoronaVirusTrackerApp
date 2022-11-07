package com.skv.coronavirustracker.Service;

import com.skv.coronavirustracker.Model.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class CoronaVirusDataService {

    private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_US.csv";
    private List<LocationStats> allStats = new ArrayList<>();

    public List<LocationStats> getAllStats() {
        return allStats;
    }

    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    public void fetchCovidData() throws IOException, InterruptedException {
        List<LocationStats> newStats = new ArrayList<>();
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(VIRUS_DATA_URL))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        StringReader csvResponse = new StringReader(response.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvResponse);
        for (CSVRecord record : records) {
            LocationStats locationStats = new LocationStats();
            locationStats.setCity(record.get("Admin2"));
            locationStats.setProvince(record.get("Province_State"));
            locationStats.setCountry_region(record.get("Country_Region"));
            locationStats.setLat(record.get("Lat"));
            locationStats.setLatestTotalCases(Integer.parseInt(record.get(record.size() - 1)));
            int prev = Integer.parseInt(record.get(record.size() - 4));
            int latest = Integer.parseInt(record.get(record.size() - 1));
            locationStats.setDiffFromPrevDay(latest - prev);
            newStats.add(locationStats);
        }
        this.allStats = newStats;
    }


}
