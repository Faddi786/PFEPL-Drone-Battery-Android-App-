package com.example.udaan;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

@Dao
public interface FlightDao {

    @Insert
    void insert(Flight flight);
    @Query("DELETE FROM flight")
    void deleteAllFlights();
    @Update
    void update(Flight flight);

    @Query("SELECT * FROM flight WHERE emp_id = :empId AND end_voltage IS NULL")
    List<Flight> getIncompleteFlightsByEmpId(String empId);

    @Query("UPDATE flight SET end_voltage = :endVoltage, flight_time = :flightTime, flight_area = :flightArea, end_time = :endTime WHERE emp_id = :empId AND end_voltage IS NULL")
    void completeFlight(String endVoltage, String flightTime, String flightArea, Date endTime, String empId);

    @Query("SELECT * FROM flight WHERE emp_id = :empId AND end_voltage IS NULL LIMIT 1")
    Flight getIncompleteFlightByEmpId(String empId);
    @Query("SELECT * FROM flight")
    LiveData<List<Flight>> getAllFlights();

    // Add this method
    @Query("SELECT * FROM flight")
    List<Flight> getAllFlightsList();

}
