package com.zybooks.jakebinvmanager.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.zybooks.jakebinvmanager.data.dao.ItemDao;
import com.zybooks.jakebinvmanager.data.model.Item;

@Database(entities = {Item.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // Define the DAO for accessing the items table
    public abstract ItemDao itemDao();

    // Singleton instance of the database
    private static AppDatabase INSTANCE;

    // Get the database instance (if it doesn't exist, create it)
    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "inventory_database")
                    .fallbackToDestructiveMigration()  // Handle schema migrations
                    .build();
        }
        return INSTANCE;
    }
}
