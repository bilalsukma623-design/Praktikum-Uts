/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartschool.permit.tubespbo.model.enums;

/**
 *
 * @author Doni354
 */
public enum UserRole {
    SUPER_ADMIN,
    ADMIN_PIKET;

    public boolean isSuperAdmin() {
        return this == SUPER_ADMIN;
    }
}
