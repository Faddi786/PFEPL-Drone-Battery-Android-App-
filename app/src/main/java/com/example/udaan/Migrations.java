package com.example.udaan;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class Migrations {

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Add the new column to the Flight table
            database.execSQL("ALTER TABLE flight ADD COLUMN start_location TEXT");
        }
    };
}

