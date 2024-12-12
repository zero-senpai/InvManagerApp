package com.zybooks.jakebinvmanager.data.database;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class Migrations {

    //Migration version 2 to 3
    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Create a new table with the updated schema
            database.execSQL("CREATE TABLE items_new ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + "itemName TEXT, "
                    + "quantity INTEGER NOT NULL, "
                    + "serialNumber INTEGER NOT NULL, " // Updated to INTEGER
                    + "price REAL NOT NULL, "
                    + "photo TEXT)");

            // Copy the data from the old table to the new table
            database.execSQL("INSERT INTO items_new (id, itemName, quantity, serialNumber, price, photo) "
                    + "SELECT id, itemName, quantity, CAST(serialNumber AS INTEGER), price, photo FROM items");

            // Drop the old table
            database.execSQL("DROP TABLE items");

            // Rename the new table to match the original table name
            database.execSQL("ALTER TABLE items_new RENAME TO items");
        }
    };


    // Migration from version 1 to version 2
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // First, fix any issue with the photo column.
            // Ensure the `photo` column has the correct default value
            // and any discrepancies are resolved.
            database.execSQL("PRAGMA foreign_keys=OFF;");

            // Handle the missing column (photo) and set a default value
            // Ensure the column's default value is handled correctly (with a single quote for 'undefined')
            database.execSQL("ALTER TABLE items ADD COLUMN photo TEXT DEFAULT 'undefined'");

            // Turn foreign keys back on
            database.execSQL("PRAGMA foreign_keys=ON;");
        }
    };
}
