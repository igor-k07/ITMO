package com.itmo.util.asks;

import com.itmo.util.exceptions.InvalidAskException;

// Абстрактный класс для запроса ввода пользовательских данных

public abstract class Ask<T> {
  public abstract T build() throws InvalidAskException;
}

