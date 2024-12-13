package com.zybooks.jakebinvmanager.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.zybooks.jakebinvmanager.data.model.Item;

import java.util.List;

@Dao
public interface ItemDao {

    // Insert a new item into the database
    @Insert
    void insertItem(Item item);

    // Update an existing item in the database
    @Update
    void updateItem(Item item);

    // Delete an item from the database
    @Delete
    void deleteItem(Item item);

    // Query to get all items from the database
    @Query("SELECT * FROM items")
    List<Item> getAllItems();

    // Query to get a specific item by its ID
    @Query("SELECT * FROM items WHERE id = :itemId")
    Item getItemById(int itemId);

    @Query("DELETE FROM items WHERE id = :id")
    void deleteItemById(long id);
}

