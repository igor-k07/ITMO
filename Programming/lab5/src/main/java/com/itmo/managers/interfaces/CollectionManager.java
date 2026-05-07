package com.itmo.managers.interfaces;

import com.itmo.models.Element;
import com.itmo.util.Status;

import java.time.LocalDateTime;
import java.util.Collection;

// Абстрактный класс для работы с элементами коллекции

public interface CollectionManager<T extends Element> {
    Collection<T> getCollection();
    int getCollectionSize();
    String getCollectionType();
    Status clearCollection();
    Status saveCollection(FileManager fileManager);
    Status addToCollection(T element);
    Status removeFromCollection(T element);
    Status updateById(int id, T newElement);
    boolean checkExist(int id);
    T getById(int id);
    T getByValue(T targetElement);
    LocalDateTime getLastInitTime();
    LocalDateTime getLastSaveTime();
}


