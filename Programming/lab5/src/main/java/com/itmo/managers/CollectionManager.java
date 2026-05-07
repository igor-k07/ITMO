package com.itmo.managers;

import com.itmo.models.abstracts.Element;
import com.itmo.util.Status;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;

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
        Status status = Status.ERROR;
        Iterator<T> iterator = this.collection.iterator();
        while (iterator.hasNext()) {
            T route = iterator.next();
            if (route.getId() == id) {
                iterator.remove();
                this.collection.add(newElement);
                status = Status.OK;
                break;
            }
        }
        updateMaxId();
        return status;
    }


    public boolean checkExist(int id) {
        for (Element element : collection) {
            if (element.getId() == id) return true;
        }
        return false;
    }

    public T getById(int id) {
        for (T element : collection) {
        if (element.getId() == id) return element;
        }
        return null;
    }

    public T getByValue(T targetElement) {
        for (T element : collection) {
            if (element.equals(targetElement)) return element;
        }
        return null;
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

        StringBuilder result = new StringBuilder();
        for (T entity: collection) {
            result.append(entity + "\n\n");
        }
        return result.toString();
    }
}
