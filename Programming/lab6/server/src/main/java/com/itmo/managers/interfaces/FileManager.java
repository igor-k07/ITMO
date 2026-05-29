package com.itmo.managers.interfaces;

import com.itmo.models.Element;
import com.itmo.util.exceptions.CollectionLoadException;
import com.itmo.util.exceptions.CollectionWriteException;

import java.util.Collection;

// Абстрактный класс для сохранения и загрузки коллекции из файла

public interface FileManager {
    Collection<Element> readCollectionFromFile() throws CollectionLoadException;
    void writeCollectionToFile(Collection<? extends Element> collection) throws CollectionWriteException;
}


