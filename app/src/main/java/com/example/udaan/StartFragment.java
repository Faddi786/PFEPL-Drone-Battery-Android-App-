package com.example.udaan;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Date;
import java.util.List;

public class StartFragment extends Fragment {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FlightViewModel flightViewModel;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Location currentLocation;
    private static final int SCAN_DRONE_ID_REQUEST = 1;
    private static final int SCAN_BATTERY_ID_REQUEST = 2;

    private Spinner spinnerProjectName;
    private EditText inputDroneId, inputBatteryId, inputStartVoltage, inputEndVoltage, inputFlightTime, inputFlightArea;
    private Button buttonSubmitStart, buttonScanDroneId, buttonScanBatteryId;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_start_fragment, container, false);

        // Initialize the ViewModel
        flightViewModel = new ViewModelProvider(this).get(FlightViewModel.class);

        // Bind views
        spinnerProjectName = view.findViewById(R.id.spinner_project_name);
        inputDroneId = view.findViewById(R.id.input_drone_id);
        inputBatteryId = view.findViewById(R.id.input_battery_id);
        inputStartVoltage = view.findViewById(R.id.input_start_voltage);
        inputEndVoltage = view.findViewById(R.id.input_end_voltage);
        inputFlightTime = view.findViewById(R.id.input_flight_time);
        inputFlightArea = view.findViewById(R.id.input_flight_area);
        buttonSubmitStart = view.findViewById(R.id.button_submit_start);
        buttonScanDroneId = view.findViewById(R.id.button_scan_drone_id);
        buttonScanBatteryId = view.findViewById(R.id.button_scan_battery_id);
        progressBar = view.findViewById(R.id.progress_bar_start);

        // Add text change listener for start voltage input
        inputStartVoltage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                if (input.isEmpty()) return;
                if (!input.matches("^\\d{0,2}(\\.\\d{0,2})?$")) {
                    s.delete(s.length() - 1, s.length());
                }
            }
        });
        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        // Setup LocationCallback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    currentLocation = location;
                }
            }
        };

        checkLocationPermissions();

        buttonSubmitStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitStartForm();
            }
        });

        buttonScanDroneId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQRCodeScanner(SCAN_DRONE_ID_REQUEST);
            }
        });

        buttonScanBatteryId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQRCodeScanner(SCAN_BATTERY_ID_REQUEST);
            }
        });

//        buttonScanEmpId.setOnClickListener(new View.OnClickListener() { // Add this click listener
//            @Override
//            public void onClick(View v) {
//                startQRCodeScanner(SCAN_EMP_ID_REQUEST);
//            }
//        });


        checkLocationPermissions();

        buttonSubmitStart.setOnClickListener(v -> submitStartForm());
        buttonScanDroneId.setOnClickListener(v -> startQRCodeScanner(SCAN_DRONE_ID_REQUEST));
        buttonScanBatteryId.setOnClickListener(v -> startQRCodeScanner(SCAN_BATTERY_ID_REQUEST));

        // Check if we are in Start or End form
        checkFormStatus();

        return view;
    }



    private void checkFormStatus() {
        String empId = ""; // Replace with the actual empId from context or other means
        List<Flight> existingFlights = flightViewModel.getFlightsByEmpId(empId); // Adjust as needed

        if (existingFlights != null && !existingFlights.isEmpty()) {
            Flight existingFlight = existingFlights.get(0);
            // Populate fields with existing data
            spinnerProjectName.setSelection(getProjectIndex(existingFlight.projectName));
            inputDroneId.setText(existingFlight.droneId);
            inputBatteryId.setText(existingFlight.batteryId);
            inputStartVoltage.setText(existingFlight.startVoltage);
            inputEndVoltage.setText(existingFlight.endVoltage);
            inputFlightTime.setText(existingFlight.flightTime);
            inputFlightArea.setText(existingFlight.flightArea);

            // Disable the start form fields and enable end form fields
            enableEndFormFields(true);
            enableStartFormFields(false);
        } else {
            // Enable only start form fields
            enableStartFormFields(true);
            enableEndFormFields(false);
        }
    }


    public void onRefreshButtonClick(View view) {
        // Call the function to refresh or update the form or data
        refreshForm();
    }

    private void refreshForm() {
        // Add logic to refresh data, e.g., reloading data from the database or resetting the form fields
        // For example:
        // Toast.makeText(this, "Refreshing data...", Toast.LENGTH_SHORT).show();
        // Update your form fields or data here as needed
    }


    private void enableStartFormFields(boolean enable) {
        inputDroneId.setEnabled(enable);
        inputBatteryId.setEnabled(enable);
        inputStartVoltage.setEnabled(enable);
    }

    private void enableEndFormFields(boolean enable) {
        inputEndVoltage.setEnabled(enable);
        inputFlightTime.setEnabled(enable);
        inputFlightArea.setEnabled(enable);
    }


    private void startQRCodeScanner(int requestCode) {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(StartFragment.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan a QR code");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.setRequestCode(requestCode);
        integrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                if (requestCode == SCAN_DRONE_ID_REQUEST) {
                    inputDroneId.setText(result.getContents());
                } else if (requestCode == SCAN_BATTERY_ID_REQUEST) {
                    inputBatteryId.setText(result.getContents());
                } else if (requestCode == SCAN_EMP_ID_REQUEST) { // Add this condition
                    inputEmpId.setText(result.getContents());
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            checkGPSEnabled();
        }
    }

    private void checkGPSEnabled() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (isGPSEnabled) {
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(location -> {
                            if (location != null) {
                                currentLocation = location;
                                startLocationUpdates();
                            } else {
                                requestLocationUpdates();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getActivity(), "Failed to get location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                promptForGPSSettings();
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void promptForGPSSettings() {
        new android.app.AlertDialog.Builder(getActivity())
                .setTitle("GPS Required")
                .setMessage("GPS is required for this app. Please enable GPS.")
                .setPositiveButton("Settings", (dialog, which) -> {
                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    Toast.makeText(getActivity(), "GPS is required for this app", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .show();
    }

    private void requestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            try {
                LocationRequest locationRequest = LocationRequest.create();
                locationRequest.setInterval(10000);
                locationRequest.setFastestInterval(5000);
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
                        .addOnSuccessListener(aVoid -> {
                            // Successfully requested location updates
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getActivity(), "Failed to request location updates: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } catch (SecurityException e) {
                Toast.makeText(getActivity(), "Location permission is required to request location updates: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkGPSEnabled();
            } else {
                Toast.makeText(getActivity(), "Location permission is required", Toast.LENGTH_SHORT).show();
                new android.app.AlertDialog.Builder(getActivity())
                        .setTitle("Permission Required")
                        .setMessage("Location permission is required to use this feature. Please grant permission.")
                        .setPositiveButton("Settings", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                            startActivity(intent);
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            Toast.makeText(getActivity(), "Location permission is required", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        })
                        .show();
            }
        }
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(5000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void retryLocationAndSubmitForm(Flight flight, int retryCount) {
        buttonSubmitStart.setEnabled(false); // Disable the Submit button
        progressBar.setVisibility(View.VISIBLE); // Show the ProgressBar

        if (retryCount <= 0) {
            flight.startLocation = "Unknown";
            flightViewModel.insert(flight);
            clearForm();
            Toast.makeText(getActivity(), "Form submitted with unknown location", Toast.LENGTH_LONG).show();
            buttonSubmitStart.setEnabled(true); // Re-enable the Submit button
            progressBar.setVisibility(View.GONE); // Hide the ProgressBar
            return;
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (currentLocation != null) {
                flight.startLocation = currentLocation.getLatitude() + "," + currentLocation.getLongitude();
                Toast.makeText(getActivity(), "Current Location: " + currentLocation.getLatitude() + "," + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                flightViewModel.insert(flight);
                clearForm();
                Toast.makeText(getActivity(), "Form submitted successfully", Toast.LENGTH_LONG).show();
                buttonSubmitStart.setEnabled(true); // Re-enable the Submit button
                progressBar.setVisibility(View.GONE); // Hide the ProgressBar
            } else {
                retryLocationAndSubmitForm(flight, retryCount - 1);
            }
        }, 5000); // Retry after 5 seconds
    }

    private void submitStartForm() {
        String empId = inputEmpId.getText().toString().trim();
        String droneId = inputDroneId.getText().toString().trim();
        String batteryId = inputBatteryId.getText().toString().trim();
        String startVoltage = inputStartVoltage.getText().toString();
        String projectName = spinnerProjectName.getSelectedItem().toString();

        if (empId.isEmpty() || projectName.isEmpty() || droneId.isEmpty() || batteryId.isEmpty() || startVoltage.isEmpty()) {
            Toast.makeText(getActivity(), "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Flight> incompleteFlights = flightViewModel.getIncompleteFlightsByEmpId(empId);
        if (incompleteFlights != null && !incompleteFlights.isEmpty()) {
            Toast.makeText(getActivity(), "You have incomplete flights", Toast.LENGTH_LONG).show();
            return;
        }

        Flight flight = new Flight();
        flight.projectName = projectName;
        flight.empId = empId;
        flight.droneId = droneId;
        flight.batteryId = batteryId;
        flight.startVoltage = startVoltage;
        flight.startTime = new Date();

        if (currentLocation != null) {
            flight.startLocation = currentLocation.getLatitude() + "," + currentLocation.getLongitude();
            Toast.makeText(getActivity(), "Current Location: " + currentLocation.getLatitude() + "," + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
            flightViewModel.insert(flight);
            clearForm();
            Toast.makeText(getActivity(), "Form submitted successfully", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), "Location is unknown. Retrying...", Toast.LENGTH_LONG).show();
            retryLocationAndSubmitForm(flight, 3); // Retry 3 times
        }
    }

    private void clearForm() {
        inputEmpId.setText("");
        inputDroneId.setText("");
        inputBatteryId.setText("");
        inputStartVoltage.setText("");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
}
