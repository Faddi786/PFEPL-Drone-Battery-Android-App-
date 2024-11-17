package com.example.udaan;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.TypeConverters;

import java.util.Date;

@Entity(tableName = "flight")
@TypeConverters(Converters.class)
public class Flight {

    @PrimaryKey(autoGenerate = true)
    public int form_id;

    @ColumnInfo(name = "project_name")
    public String projectName;

    @ColumnInfo(name = "emp_id")
    public String empId;

    @ColumnInfo(name = "drone_id")
    public String droneId;

    @ColumnInfo(name = "battery_id")
    public String batteryId;

    @ColumnInfo(name = "start_voltage")
    public String startVoltage;

    @ColumnInfo(name = "start_time")
    public Date startTime;

    @ColumnInfo(name = "start_location") // New column
    public String startLocation;

    @ColumnInfo(name = "end_voltage")
    public String endVoltage;

    @ColumnInfo(name = "flight_time")
    public String flightTime;

    @ColumnInfo(name = "flight_area")
    public String flightArea;

    @ColumnInfo(name = "end_time")
    public Date endTime;
}
