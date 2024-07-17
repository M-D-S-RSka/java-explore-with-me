package ru.practicum;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.model.HitInput;
import ru.practicum.model.HitOutput;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class StatsClient {

    private final RestTemplate rest = new RestTemplate();
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd%20HH:mm:ss");
    private final Gson gson = new Gson();

    public ResponseEntity<HitInput> sendStatsHit(String ip, String app, String uri) {
        var input = new HitInput();
        input.setIp(ip);
        input.setUri(uri);
        input.setApp(app);
        input.setTimestamp(LocalDateTime.now());

        return rest.postForEntity("http://localhost:9090/hit", input, HitInput.class);
    }

    public List<HitOutput> getHits(LocalDateTime startTime,
                                   LocalDateTime endTime,
                                   List<String> uri,
                                   boolean unique) {
        var uris = new StringBuilder();
        uri.forEach(it -> {
            uris.append(String.format("&uris=%s", it));
        });
        try {
            var url = new URL(String.format("http://localhost:9090/stats?start=%s&end=%s%s&unique=%s", startTime.format(dtf), endTime.format(dtf), uris, unique));
            var con = url.openConnection();
            var baos = new ByteArrayOutputStream();
            try (var is = con.getInputStream()) {
                is.transferTo(baos);
            }
            var str = baos.toString(StandardCharsets.UTF_8);
            var listType = new TypeToken<ArrayList<HitOutput>>() {
            }.getType();
            return gson.<ArrayList<HitOutput>>fromJson(str, listType);
        } catch (IOException e) {
            throw new RuntimeException("something wrong");
        }
    }
}
