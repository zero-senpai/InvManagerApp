package com.zybooks.jakebinvmanager.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.zybooks.jakebinvmanager.data.dao.ItemDao;
import com.zybooks.jakebinvmanager.data.dao.UserDao;
import com.zybooks.jakebinvmanager.data.model.Item;
import com.zybooks.jakebinvmanager.data.model.User;

@Database(entities = {Item.class, User.class}, version = 3) // Updated version
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract ItemDao itemDao();
    public abstract UserDao userDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "inventory_database")
                            .addMigrations(Migrations.MIGRATION_2_3) // Apply migration
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
