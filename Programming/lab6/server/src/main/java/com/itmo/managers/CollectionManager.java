package com.itmo.managers;

import com.itmo.models.abstracts.Element;
import com.itmo.util.Status;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

// Менеджер для работы с коллекцией типа HashSet

public class CollectionManager<T extends Element> {
    private Collection<T> collection = new HashSet<>();
    private LocalDateTime lastInitTime;
    private LocalDateTime lastSaveTime = LocalDateTime.now();
    private int maxId;

    public CollectionManager(Collection<T> collection) {
        this.collection = new HashSet<>();
        if (collection != null) this.collection.addAll(collection);
        this.lastInitTime = LocalDateTime.now();
        updateMaxId();
    }


    public Status saveCollection(DumpManager dumpManager){
        try {
            dumpManager.writeCollectionToFile(collection);
            this.lastSaveTime = LocalDateTime.now();
            return Status.OK;
        } catch (Exception e) {
            return Status.ERROR;
        }
    }

    private void updateMaxId() {
        maxId = collection
            .stream().filter(Objects::nonNull)
            .map(T::getId)
            .mapToInt(Integer::intValue).max().orElse(0);
    }


    public Status clearCollection() {
        try {
            collection.clear();
            return Status.OK;
        } catch (Exception e) {
            return Status.ERROR;
        }

    }

    public Status removeFromCollection(T element) {
        try {
            collection.remove(element);
            updateMaxId();
            return Status.OK;
        } catch (Exception e) {
            return Status.ERROR;
        }

    }

    public Status addToCollection(T element) {
        try {
            element.setId(maxId+1);
            collection.add(element);
            updateMaxId();
            return Status.OK;
        } catch (Exception e) {
            return Status.ERROR;
        }
    }

    public Status updateById(int id, T newElement) {
        newElement.setId(id);
        Optional<T> target = collection.stream()
            .filter(element -> element.getId() == id)
            .findFirst();

        if (target.isPresent()) {
            collection.remove(target.get());
            collection.add(newElement);
            updateMaxId();
            return Status.OK;
        }
        updateMaxId();
        return Status.ERROR;
    }


    public boolean checkExist(int id) {
        return collection.stream().anyMatch(element -> element.getId() == id);
    }

    public T getById(int id) {
        return collection.stream()
            .filter(element -> element.getId() == id)
            .findFirst()
            .orElse(null);
    }

    public T getByValue(T targetElement) {
        return collection.stream()
            .filter(element -> element.equals(targetElement))
            .findFirst()
            .orElse(null);
    }

    public Collection<T> getCollection() {
        return collection;
    }

    public String getCollectionType() {
        return collection.getClass().getName();
    }

    public int getCollectionSize() {
        return collection.size();
    }

    public LocalDateTime getLastInitTime() {
        return this.lastInitTime;
    }

    public LocalDateTime getLastSaveTime() {
        return this.lastSaveTime;
    }

    @Override
    public String toString() {
        if (collection.isEmpty()) {
            return "Коллекция пуста";
        }
        return collection.stream()
            .map(Object::toString)
            .collect(Collectors.joining("\n\n"));
    }
}
