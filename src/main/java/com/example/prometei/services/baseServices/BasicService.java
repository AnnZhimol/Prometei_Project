package com.example.prometei.services.baseServices;

import java.util.List;

public interface BasicService<T> {
    void add(T entity);
    void delete(T entity);
    List<T> getAll();
    void deleteAll();
    void edit(Long id, T entity);
    T getById(Long id);
}
