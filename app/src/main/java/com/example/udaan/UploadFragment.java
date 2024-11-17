package com.example.udaan;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class UploadFragment extends Fragment {

    private FlightViewModel flightViewModel;
    private FlightAdapter adapter;
    private FlightDao flightDao;
    private Button uploadButton;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_upload_fragment, container, false);

        // Initialize FlightDao
        flightDao = AppDatabase.getInstance(getContext()).flightDao();

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FlightAdapter();
        recyclerView.setAdapter(adapter);
        uploadButton = view.findViewById(R.id.upload_button);
        progressBar = view.findViewById(R.id.progress_bar);
        uploadButton.setVisibility(View.GONE); // Hide the upload button

        flightViewModel = new ViewModelProvider(this).get(FlightViewModel.class);

        // Check for incomplete journeys and update UI
        flightViewModel.getAllFlights().observe(getViewLifecycleOwner(), new Observer<List<Flight>>() {
            @Override
            public void onChanged(List<Flight> flights) {
                adapter.setFlights(flights);
                adapter.notifyDataSetChanged();
                updateUploadButtonVisibility(flights);

            }
        });

        return view;
    }

    private void updateUploadButtonVisibility(List<Flight> flights) {
        if (flights.isEmpty()) {
            // If the flights list is empty, hide the upload button
            uploadButton.setVisibility(View.GONE);
        } else if (flightViewModel.hasIncompleteJourneys()) {
            // If there are incomplete journeys, hide the upload button
            Toast.makeText(getActivity(), "Incomplete journeys detected. Upload disabled.", Toast.LENGTH_LONG).show();
            uploadButton.setVisibility(View.GONE);
        } else {
            // Show the upload button if there are flights and no incomplete journeys
            uploadButton.setVisibility(View.VISIBLE);
            uploadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadData(flights);
                }
            });
        }
    }

    private void uploadData(List<Flight> flights) {
        if (flights != null && !flights.isEmpty()) {
            String baseUrl = "https://9b9a-203-123-38-102.ngrok-free.app/myProjects/Parinda/"; // Use 10.0.2.2 to access localhost from an Android emulator
            FlightApi flightApi = RetrofitClient.getClient(baseUrl).create(FlightApi.class);
            uploadButton.setEnabled(false); // Disable the upload button
            progressBar.setVisibility(View.VISIBLE); // Show the ProgressBar
            new UploadTask(flightApi, getContext(), flightDao, uploadButton, progressBar).execute(flights);
        }
    }

    private class UploadTask extends AsyncTask<List<Flight>, Void, UploadResult> {
        private FlightApi flightApi;
        private Context context;
        private FlightDao flightDao;
        private WeakReference<Button> uploadButtonRef;
        private WeakReference<ProgressBar> progressBarRef;

        public UploadTask(FlightApi flightApi, Context context, FlightDao flightDao, Button uploadButton, ProgressBar progressBar) {
            this.flightApi = flightApi;
            this.context = context;
            this.flightDao = flightDao;
            this.uploadButtonRef = new WeakReference<>(uploadButton); // Store reference to upload button
            this.progressBarRef = new WeakReference<>(progressBar); // Store reference to progress bar
        }

        @Override
        protected UploadResult doInBackground(List<Flight>... lists) {
            List<Flight> flights = lists[0];
            Call<Void> call = flightApi.uploadFlights(flights);
            try {
                Response<Void> response = call.execute();
                if (response.isSuccessful()) {
                    if (flightDao != null) {
                        flightDao.deleteAllFlights();
                        return new UploadResult(true, null);
                    } else {
                        return new UploadResult(false, "FlightDao is null");
                    }
                } else {
                    String errorMessage = response.errorBody().string();
                    return new UploadResult(false, errorMessage);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return new UploadResult(false, e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(UploadResult result) {
            Button uploadButton = uploadButtonRef.get();
            ProgressBar progressBar = progressBarRef.get();

            if (uploadButton != null) {
                uploadButton.setEnabled(true); // Re-enable the upload button
            }

            if (progressBar != null) {
                progressBar.setVisibility(View.GONE); // Hide the ProgressBar
            }

            if (result.isSuccess()) {
                showToast("Upload successful");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (flightDao != null) {
                                flightDao.deleteAllFlights();
                                List<Flight> flightsAfterDeletion = flightDao.getAllFlightsList();
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (flightsAfterDeletion.isEmpty()) {
//                                            showToast("Database is empty after deletion");
                                        } else {
//                                            showToast("Database still contains flights after deletion");
                                        }
                                        adapter.setFlights(new ArrayList<>()); // Clear adapter dataset
                                        adapter.notifyDataSetChanged(); // Notify adapter of dataset change
                                        if (uploadButton != null) {
                                            uploadButton.setVisibility(View.GONE); // Hide the button if it was visible
                                        }
                                    }
                                });
                            } else {
                                showToast("FlightDao is null during deletion");
                            }
                        } catch (Exception e) {
                            Log.e("UploadTask", "Error deleting flights from database", e);
//                            showToast("Error deleting flights from database: " + e.getMessage());
                            showToast("Check Internet Connection and try again");
                        }
                    }
                }).start();
            } else {
//                showToast("Upload failed: " + result.getErrorMessage());
                showToast("Check Internet Connection and try again");
            }
        }

        private void showToast(String message) {
            // Display Toast messages on the UI thread
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private String convertFlightsToString(List<Flight> flights) {
        StringBuilder sb = new StringBuilder();
        for (Flight flight : flights) {
            sb.append("Project Name: ").append(flight.projectName).append(" (String)\n");
            sb.append("Employee ID: ").append(flight.empId).append(" (String)\n");
            sb.append("Drone ID: ").append(flight.droneId).append(" (String)\n");
            sb.append("Battery ID: ").append(flight.batteryId).append(" (String)\n");
            sb.append("Start Voltage: ").append(flight.startVoltage).append(" (String)\n");
            sb.append("Start Time: ").append(flight.startTime).append(" (Date)\n");
            sb.append("End Voltage: ").append(flight.endVoltage).append(" (String)\n");
            sb.append("Flight Time: ").append(flight.flightTime).append(" (String)\n");
            sb.append("Flight Area: ").append(flight.flightArea).append(" (String)\n");
            sb.append("End Time: ").append(flight.endTime).append(" (Date)\n");
            sb.append("\n");
        }
        return sb.toString();
    }

    private void showToast(String message) {
        // Display Toast messages on the UI thread
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }
}