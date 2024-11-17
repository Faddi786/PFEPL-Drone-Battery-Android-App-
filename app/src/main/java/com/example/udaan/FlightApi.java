package com.example.udaan;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface FlightApi {
    @POST("upload_flights.php")
    Call<Void> uploadFlights(@Body List<Flight> flights);
}