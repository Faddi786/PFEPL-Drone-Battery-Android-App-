package com.example.udaan;
import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.Date;
import java.util.List;

public class FlightViewModel extends AndroidViewModel {

    private FlightRepository repository;
    private LiveData<List<Flight>> allFlights;
    public FlightViewModel(@NonNull Application application) {
        super(application);
        repository = new FlightRepository(application);
        allFlights = repository.getAllFlights();
    }
    public LiveData<List<Flight>> getAllFlights() {
        return allFlights;
    }
    public void insert(Flight flight) {
        repository.insert(flight);
    }

    public void update(Flight flight) {
        repository.update(flight);
    }

//    public List<Flight> getIncompleteFlightsByEmpId(String empId) {
//        return repository.getIncompleteFlightsByEmpId(empId);
//    }

//    public void completeFlight(String endVoltage, String flightTime, String flightArea, Date endTime, String empId) {
//        List<Flight> incompleteFlights = repository.getIncompleteFlightsByEmpId(empId);
//        if (incompleteFlights != null && !incompleteFlights.isEmpty()) {
//            // Do not update if there are incomplete flights
//            return;
//        }
//
//        // Proceed with updating the flight data
//        repository.completeFlight(endVoltage, flightTime, flightArea, endTime, empId);
//    }

//    public Flight getIncompleteFlightByEmpId(String empId) {
//        return repository.getIncompleteFlightByEmpId(empId);
//    }

    // New method to check for incomplete journeys
//    public boolean hasIncompleteJourneys() {
//        List<Flight> incompleteFlights = repository.getAllFlightsList();
//        for (Flight flight : incompleteFlights) {
//            if (flight.endVoltage == null || flight.flightTime == null || flight.flightArea == null || flight.endTime == null) {
//                return true; // There is at least one incomplete journey
//            }
//        }
//        return false; // All journeys are complete
//    }


}
