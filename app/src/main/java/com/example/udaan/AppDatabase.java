package com.example.udaan;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.util.Date;

@Database(entities = {Flight.class, Employee.class, Drone.class, Battery.class}, version = 2, exportSchema = false)
@TypeConverters({AppDatabase.DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract FlightDao flightDao();
    public abstract EmployeeDao employeeDao(); // Add this line
    public abstract DroneDao droneDao();     // Add this line
    public abstract BatteryDao batteryDao();  // Add this line

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "flights")
                    .fallbackToDestructiveMigration() // This will clear the database if the version is updated
                    .build();
        }
        return instance;
    }

    public static class DateConverter {

        @TypeConverter
        public static Date toDate(Long timestamp) {
            return timestamp == null ? null : new Date(timestamp);
        }

        @TypeConverter
        public static Long toTimestamp(Date date) {
            return date == null ? null : date.getTime();
        }
    }
}
