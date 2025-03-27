package tracker.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class IntervalManager {
    private final Map<LocalDateTime, Boolean> intervalMap = new HashMap<>();
    private static final Duration INTERVAL_DURATION = Duration.ofMinutes(15);

    public IntervalManager() {
        LocalDateTime start = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        for (int i = 0; i < 2 * 24 * 4; i++) {
            intervalMap.put(start.plusMinutes(i * 15), true);
        }
    }

    // Проверка, свободны ли интервалы для новой задачи
    public boolean isIntervalFree(LocalDateTime startTime, Duration duration) {
        LocalDateTime endTime = startTime.plus(duration);
        LocalDateTime current = startTime;

        while (current.isBefore(endTime)) {
            if (intervalMap.getOrDefault(current, true)) {
                return false;
            }
            current = current.plus(INTERVAL_DURATION);
        }
        return true;
    }

    // Отметка интервалов как занятых
    public void markIntervalsAsBusy(LocalDateTime startTime, Duration duration) {
        LocalDateTime endTime = startTime.plus(duration);
        LocalDateTime current = startTime;
        while (current.isBefore(endTime)) {
            intervalMap.put(current, false);
            current = current.plus(INTERVAL_DURATION);
        }
    }
}
