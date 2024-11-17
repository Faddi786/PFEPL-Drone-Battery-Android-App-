package com.example.udaan;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class UploadTask extends AsyncTask<List<Flight>, Void, Void> {

    private FlightApi flightApi;

    public UploadTask(FlightApi flightApi) {
        this.flightApi = flightApi;
    }

    @Override
    protected Void doInBackground(List<Flight>... lists) {
        List<Flight> flights = lists[0];
        Call<Void> call = flightApi.uploadFlights(flights);
        try {
            Response<Void> response = call.execute();
            if (response.isSuccessful()) {
                // Handle successful response

            } else {
                // Handle unsuccessful response
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

