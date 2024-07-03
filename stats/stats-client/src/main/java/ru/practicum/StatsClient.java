package ru.practicum;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.model.HitInput;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StatsClient {

    private final RestTemplate rest = new RestTemplate();
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd%20HH:mm:ss");
    private final String baseUrl;

    public StatsClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public ResponseEntity<HitInput> sendStatsHit(String ip, String app, String uri) {
        var input = new HitInput();
        input.setIp(ip);
        input.setUri(uri);
        input.setApp(app);
        input.setTimestamp(LocalDateTime.now());

        return rest.postForEntity(baseUrl + "/hit", input, HitInput.class);
    }

    //TODO public List<HitOutput> getHits
}
