package ru.practicum;

import org.springframework.http.ResponseEntity;
import ru.practicum.model.HitInput;
import org.springframework.web.client.RestTemplate;

public class StatsClient {

    private final RestTemplate rest = new RestTemplate();

    public ResponseEntity<HitInput> sendStatsHit(String ip, String app, String uri) {
        var input = new HitInput();
        input.setIp(ip);
        input.setUri(uri);
        input.setApp(app);

        return rest.postForEntity("http://localhost:9090/hit", input, HitInput.class);
    }
}
