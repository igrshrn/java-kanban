package tracker.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import tracker.util.adapters.DurationTypeAdapter;
import tracker.util.adapters.LocalDateTimeTypeAdapter;

import java.time.Duration;
import java.time.LocalDateTime;

public class GsonProvider {
    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();
    }
}
