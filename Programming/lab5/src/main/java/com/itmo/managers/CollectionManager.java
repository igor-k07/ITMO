package com.itmo.managers;

import com.itmo.models.MusicBand;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class CollectionManager {
    private int currentId = 0;
    private Map<Integer, MusicBand> musicBand = new HashMap<>();
    private Collection<MusicBand> collection = new HashSet<MusicBand>();
    private LocalDateTime lastInitTime;
    private LocalDateTime lastsaveTime;
    private final DumpManager dumpManager;

    public CollectionManager(DumpManager dumpManager){
        this.lastsaveTime = null;
        this.lastsaveTime = null;
        this.dumpManager = dumpManager;
    }

    public LocalDateTime getLastInitTime() {
        return lastInitTime;
    }

    public LocalDateTime getLastsaveTime(){
        return lastsaveTime;
    }

    public Collection<MusicBand> getCollection() {
        return collection;
    }

    public int getSizeCollection() {
        return collection.size();
    }

    public MusicBand byId(int id) {
        return collection.stream()
                .filter(band -> band.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public boolean isContain(MusicBand band) {
        return band == null || collection.contains(band);
    }

    public int getFreeId() {
        while (byId(++currentId) != null);
        return currentId;
    }

    public boolean add(MusicBand band) {
        return collection.add(band);
    }


    public boolean remove(int id) {
        return collection.removeIf(band -> band.getId() == id);
    }

    public boolean clearCollection() {
        if (collection.isEmpty()) {
            return false;
        }
        collection.clear();
        return true;
    }

    public boolean init() {
        clearCollection();
        collection = dumpManager.readCollection();

        lastInitTime = LocalDateTime.now();

        boolean hasDuplicateIds = collection.stream()
                .mapToInt(MusicBand::getId)
                .distinct()
                .count() != getSizeCollection();

        if (hasDuplicateIds) {
            clearCollection();
            return false;
        }

        currentId = collection.stream().mapToInt(MusicBand::getId).max().orElse(0);
        return true;
    }

    public void saveCollection() {
        dumpManager.writeCollection(collection);
        lastsaveTime = LocalDateTime.now();
    }


    @Override
    public String toString() {
        return collection.isEmpty() ? "Коллекция пуста" :
                collection.stream()
                        .map(MusicBand::toString)
                        .collect(Collectors.joining("\n"));
    }
}
