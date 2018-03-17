package ru.icc.cells.tabbypdf.utils;

import java.util.*;

public class Measurer {
    private final Map<String, List<Long>> timesMap = new LinkedHashMap<>();

    public void start(String eventName) {
        timesMap
                .computeIfAbsent(eventName, key -> new ArrayList<>())
                .add(System.nanoTime());
    }

    public long end(String eventName) {
        List<Long> times = timesMap.get(eventName);
        long measuredTime = System.nanoTime() - times.get(times.size() - 1);
        times.set(times.size() - 1, measuredTime);

        return measuredTime;
    }

    public double getAverageFor(String eventName) {
        return timesMap
                .getOrDefault(eventName, Collections.emptyList())
                .stream()
                .mapToLong(value -> value)
                .average()
                .orElse(0.0);
    }

    public long getSummaryFor(String eventName) {
        return timesMap
                .getOrDefault(eventName, Collections.emptyList())
                .stream()
                .mapToLong(value -> value)
                .sum();
    }

    public long getSummaryForEventSuffix(String eventSuffix) {
        return timesMap.keySet()
                .stream()
                .filter(event -> event.endsWith(eventSuffix))
                .map(timesMap::get)
                .flatMap(List::stream)
                .mapToLong(value -> value)
                .sum();
    }

    public void printStatistics() {
        timesMap.keySet().forEach(
                event -> System.out.println(event + " lasted " + getSummaryFor(event) / 1000000.0 + " ms")
        );
    }
}
