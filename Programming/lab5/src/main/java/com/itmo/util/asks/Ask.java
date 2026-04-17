package com.itmo.util.forms;

import com.itmo.util.exceptions.InvalidFormException;


/**
 * Абстрактный класс формы для ввода пользовательских данных.
 * @param <T> создаваемый объект
 * @author Septyq
 */
public abstract class Ask<T> {
  public abstract T build() throws InvalidFormException;
}