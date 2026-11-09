/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.smartschool.permit.tubespbo.repository;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Doni354
 */
public interface CrudRepository<T> {
    List<T> getAll();
    T getById(String id);
    String create(T entity);
    void update(String id, Map<String, Object> data);
    void delete(String id);
}
