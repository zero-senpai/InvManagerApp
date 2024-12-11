package com.zybooks.jakebinvmanager.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Class: Item
 * Description: Class uses the Room library to populate the table items with each object's properties
 **/
@Entity(tableName = "items")
public class Item {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String itemName;
    private int quantity;
    private double serialNumber;  // Updated to double
    private double price;         // Already double
    private String photo;

    // Default constructor
    public Item() {}

    // Constructor with new fields (including quantity and updated serialNumber as double)
    public Item(String itemName, double serialNumber, double price, int quantity, String photo) {
        this.itemName = itemName;
        this.serialNumber = serialNumber;
        this.price = price;
        this.quantity = quantity;
        this.photo = photo;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(double serialNumber) {
        this.serialNumber = serialNumber;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
