package ru.practicum;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.model.HitInput;
import ru.practicum.model.HitOutput;

public class StatsClient {

    private final RestTemplate rest = new RestTemplate();
    private final String baseUrl;

    public StatsClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public ResponseEntity<HitInput> sendStatsHit(String ip, String app, String uri) {
        var input = new HitInput();
        input.setIp(ip);
        input.setUri(uri);
        input.setApp(app);

        return rest.postForEntity(baseUrl + "/hit", input, HitInput.class);
    }

    public ResponseEntity<HitOutput> getSomeData(String param) {
        // Implement logic to get data from another endpoint
        return rest.getForEntity(baseUrl + "/some-endpoint?param=" + param, HitOutput.class);
    }

    // Add more methods for other endpoints as needed

    // Exception handling example
    public ResponseEntity<String> handleException() {
        try {
            // Code that may throw an exception
        } catch (Exception e) {
            // Log the exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
        return ResponseEntity.ok("No error occurred");
    }
}
