package net.sorted.exchange.web;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class OrderSnapshotCache {
    private final Map<String, ClientOrderSnapshot> instrumentIdToSnapshot = new ConcurrentHashMap<>(100, 0.75f, 4);

    public void setSnapshot(String instrumentId, ClientOrderSnapshot snapshot) {
        instrumentIdToSnapshot.put(instrumentId, snapshot);
    }

    public Optional<ClientOrderSnapshot> getSnapshot(String instrumentId) {
        return Optional.ofNullable(instrumentIdToSnapshot.get(instrumentId));
    }
}
