/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartschool.permit.tubespbo.app;
import com.google.cloud.firestore.Firestore;
/**
 *
 * @author Doni354
 */
public class MainApp {
    public static void main(String[] args) {
        System.out.println("Trying to connect to Firebase...");
        
        Firestore db = FirestoreConnection.getInstance().getDb();
        
        if (db != null) {
            System.out.println("Connected to Firebase");
        } else {
            System.out.println("Connection Failed");
        }
    }
}