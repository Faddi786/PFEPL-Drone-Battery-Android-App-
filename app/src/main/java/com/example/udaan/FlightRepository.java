package com.example.udaan;
import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.Date;
import java.util.List;

public class FlightRepository {

    private FlightDao flightDao;
    private LiveData<List<Flight>> allFlights;

    public FlightRepository(Application application) {
        AppDatabase db = Room.databaseBuilder(application, AppDatabase.class, "flights")
                .allowMainThreadQueries()  // Allow main thread queries for simplicity
                .build();
        flightDao = db.flightDao();
        allFlights = flightDao.getAllFlights();
    }

    public LiveData<List<Flight>> getAllFlights() {
        return allFlights;
    }
    public void insert(Flight flight) {
        flightDao.insert(flight);
    }

    public void update(Flight flight) {
        flightDao.update(flight);
    }

    public List<Flight> getIncompleteFlightsByEmpId(String empId) {
        return flightDao.getIncompleteFlightsByEmpId(empId);
    }

    public void completeFlight(String endVoltage, String flightTime, String flightArea, Date endTime, String empId) {
        flightDao.completeFlight(endVoltage, flightTime, flightArea, endTime, empId);
    }

    public Flight getIncompleteFlightByEmpId(String empId) {
        return flightDao.getIncompleteFlightByEmpId(empId);
    }
    public List<Flight> getAllFlightsList() {
        return flightDao.getAllFlightsList();
    }
}
