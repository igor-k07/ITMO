package com.itmo.managers;

import com.itmo.models.abstracts.Element;
import com.itmo.util.Status;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Менеджер коллекции.
 * 
 * Синхронизация: Collections.synchronizedSet() — потокобезопасная обёртка над HashSet.
 * - Простые операции (add, remove, size) — synchronizedSet обеспечивает потокобезопасность сам.
 * - Итерация и составные операции — требуют явного synchronized(collection) блока
 *   (согласно документации Collections.synchronizedSet).
 */
public class CollectionManager<T extends Element> {
    private final Set<T> collection;
    private LocalDateTime lastInitTime;
    private LocalDateTime lastSaveTime = LocalDateTime.now();

    public CollectionManager(Collection<T> collection) {
        HashSet<T> initial = new HashSet<>();
        if (collection != null) initial.addAll(collection);
        // Collections.synchronizedSet — потокобезопасная обёртка.
        // Все вызовы add/remove/size/contains автоматически синхронизированы.
        this.collection = Collections.synchronizedSet(initial);
        this.lastInitTime = LocalDateTime.now();
    }

    // ===== Простые операции — synchronizedSet обеспечивает потокобезопасность =====

    public Status addToCollection(T element) {
        try {
            collection.add(element);
            return Status.OK;
        } catch (Exception e) {
            return Status.ERROR;
        }
    }

    public Status removeFromCollection(T element) {
        try {
            collection.remove(element);
            return Status.OK;
        } catch (Exception e) {
            return Status.ERROR;
        }
    }

    public int getCollectionSize() {
        return collection.size();
    }

    // ===== Операции с итерацией — требуют synchronized(collection) =====

    public Status updateById(int id, T newElement) {
        synchronized (collection) {
            newElement.setId(id);
            Optional<T> target = collection.stream()
                .filter(element -> element.getId() == id)
                .findFirst();
            if (target.isPresent()) {
                collection.remove(target.get());
                collection.add(newElement);
                return Status.OK;
            }
        }
        return Status.ERROR;
    }

    public Status clearByOwner(Long ownerId) {
        synchronized (collection) {
            collection.removeIf(element -> ownerId.equals(
                ((com.itmo.models.MusicBand) element).getOwnerId()));
            return Status.OK;
        }
    }

    public boolean checkExist(int id) {
        synchronized (collection) {
            return collection.stream().anyMatch(element -> element.getId() == id);
        }
    }

    public T getById(int id) {
        synchronized (collection) {
            return collection.stream()
                .filter(element -> element.getId() == id)
                .findFirst()
                .orElse(null);
        }
    }

    public T getByValue(T targetElement) {
        synchronized (collection) {
            return collection.stream()
                .filter(element -> element.equals(targetElement))
                .findFirst()
                .orElse(null);
        }
    }

    /**
     * Возвращает копию коллекции для безопасного чтения.
     * synchronized(collection) гарантирует, что копия создаётся атомарно.
     */
    public List<T> getCollectionCopy() {
        synchronized (collection) {
            return new ArrayList<>(collection);
        }
    }

    public Set<T> getCollection() {
        return collection;
    }

    public String getCollectionType() {
        return "HashSet (synchronizedSet)";
    }

    public LocalDateTime getLastInitTime() {
        return this.lastInitTime;
    }

    public LocalDateTime getLastSaveTime() {
        return this.lastSaveTime;
    }

    @Override
    public String toString() {
        synchronized (collection) {
            if (collection.isEmpty()) {
                return "Коллекция пуста";
            }
            return collection.stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n\n"));
        }
    }
}
