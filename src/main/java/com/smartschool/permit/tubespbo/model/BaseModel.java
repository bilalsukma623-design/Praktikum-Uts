/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartschool.permit.tubespbo.model;

import java.util.Map;

/**
 *
 * @author Doni354
 */
public abstract class BaseModel {
    protected String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public abstract Map<String, Object> toMap();
    public abstract void fromMap(Map<String, Object> map);
}
