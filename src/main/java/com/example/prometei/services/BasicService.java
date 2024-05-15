package com.example.prometei.services;

import java.util.List;

public interface BasicService<T> {
    void add(T entity);
    void delete(T entity);
    List<T> getAll();
    void deleteAll();
    void edit(T entity);
    T getById(Long id);
}
