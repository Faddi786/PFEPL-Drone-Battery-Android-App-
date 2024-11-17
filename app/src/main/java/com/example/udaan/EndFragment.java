package com.example.udaan;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Date;

public class EndFragment extends Fragment {
    private static final int SCAN_EMP_ID_REQUEST = 1;
    private FlightViewModel flightViewModel;
    private EditText inputEmpId, inputEndVoltage, inputFlightTime, inputFlightArea;
    private Button buttonScanEmpId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_end_fragment, container, false);

        flightViewModel = new ViewModelProvider(this).get(FlightViewModel.class);

        inputEmpId = view.findViewById(R.id.input_emp_id);
        inputEndVoltage = view.findViewById(R.id.input_end_voltage);
        inputFlightTime = view.findViewById(R.id.input_flight_time);
        inputFlightArea = view.findViewById(R.id.input_flight_area);
        buttonScanEmpId = view.findViewById(R.id.button_scan_emp_id);
        Button buttonSubmitEnd = view.findViewById(R.id.button_submit_end);

        setFlightAreaFilter(inputFlightArea);
        setEndVoltageFilter(inputEndVoltage);

        buttonScanEmpId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQRCodeScanner(SCAN_EMP_ID_REQUEST);
            }
        });

        buttonSubmitEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String flightArea = inputFlightArea.getText().toString();
                String endVoltage = inputEndVoltage.getText().toString();

                if (isValidFlightArea(flightArea) && isValidEndVoltage(endVoltage)) {
                    submitEndForm();
                } else {
                    Toast.makeText(getContext(), "End Voltage before decimal 2 and after decimal 2 digit", Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    private void startQRCodeScanner(int requestCode) {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(EndFragment.this);
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
                if (requestCode == SCAN_EMP_ID_REQUEST) {
                    inputEmpId.setText(result.getContents());
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    private boolean isValidFlightArea(String input) {
        return input.matches("^\\d{1,2}(\\.\\d{1,3})?$");
    }

    private boolean isValidEndVoltage(String input) {
        return input.matches("^\\d{1,2}\\.\\d{2}$");
    }

    private void submitEndForm() {
        String empId = inputEmpId.getText().toString().trim();
        String endVoltage = inputEndVoltage.getText().toString().trim();
        String flightTime = inputFlightTime.getText().toString().trim();
        String flightArea = inputFlightArea.getText().toString().trim();
        Date endTime = new Date();

        if (empId.isEmpty()) {
            showToast("Employee ID is required");
            return;
        }
        if (endVoltage.isEmpty()) {
            showToast("End Voltage is required");
            return;
        }
        if (flightTime.isEmpty()) {
            showToast("Flight Time is required");
            return;
        }
        if (flightArea.isEmpty()) {
            showToast("Flight Area is required");
            return;
        }

        Flight incompleteFlight = flightViewModel.getIncompleteFlightByEmpId(empId);
        if (incompleteFlight == null) {
            showToast("Journey not started");
            return;
        }

        flightViewModel.completeFlight(endVoltage, flightTime, flightArea, endTime, empId);
        clearInputFields();
        showToast("Updated Successfully");
    }

    private void showToast(String message) {
        getActivity().runOnUiThread(() -> Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show());
    }

    private void clearInputFields() {
        inputEmpId.setText("");
        inputEndVoltage.setText("");
        inputFlightTime.setText("");
        inputFlightArea.setText("");
    }



    private void setFlightAreaFilter(EditText editText) {
        editText.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String newInput = dest.subSequence(0, dstart).toString() + source.toString() + dest.subSequence(dend, dest.length()).toString();
                if (newInput.matches("^\\d{0,2}(\\.\\d{0,3})?$")) {
                    return null; // Accept the input
                }
                return ""; // Reject the input
            }
        }});
    }


    private void setEndVoltageFilter(EditText editText) {
        editText.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String newInput = dest.subSequence(0, dstart).toString() + source.toString() + dest.subSequence(dend, dest.length()).toString();
                if (newInput.matches("^\\d{0,2}(\\.\\d{0,2})?$")) {
                    return null; // Accept the input
                }
                return ""; // Reject the input
            }
        }});
    }
}
