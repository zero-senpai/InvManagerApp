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

    //Default Constructor for Item class
    public Item(String name, int quantity) {
        this.itemName = name;
        this.quantity = quantity;
    }

    //Getters, Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String name) {
        this.itemName = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
