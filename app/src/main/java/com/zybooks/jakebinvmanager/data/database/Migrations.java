package com.zybooks.jakebinvmanager.data.database;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class Migrations {

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
