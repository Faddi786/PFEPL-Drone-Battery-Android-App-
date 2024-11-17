package com.example.udaan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FlightAdapter extends RecyclerView.Adapter<FlightAdapter.FlightViewHolder> {

    private List<Flight> flights = new ArrayList<>();

    @NonNull
    @Override
    public FlightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_flight, parent, false);
        return new FlightViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlightViewHolder holder, int position) {
        Flight flight = flights.get(position);
        holder.bind(flight);
    }

    @Override
    public int getItemCount() {
        return flights.size();
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
        notifyDataSetChanged();
    }

    class FlightViewHolder extends RecyclerView.ViewHolder {
        // Assuming you have TextViews for displaying flight details
//        private TextView projectNameView;
        private TextView empIdView;

//        private TextView startTimeView;
//        private TextView endTimeView;
        private TextView flightStatusView;

        public FlightViewHolder(@NonNull View itemView) {
            super(itemView);
//            projectNameView = itemView.findViewById(R.id.project_name);
            empIdView = itemView.findViewById(R.id.emp_id);
//            startTimeView = itemView.findViewById(R.id.start_time);
//            endTimeView = itemView.findViewById(R.id.end_time);
            flightStatusView = itemView.findViewById(R.id.flight_status);
        }

        public void bind(Flight flight) {
//            projectNameView.setText(flight.projectName);
            empIdView.setText("Emp_ID: "+flight.empId);
            // Determine flight status
            String status = (flight.startTime != null && flight.endTime != null) ? "Complete" : "Incomplete";
            flightStatusView.setText("Flight Status: " + status);

//            // Check if startTime and endTime are null before calling toString()
//            if (flight.startTime != null) {
//                startTimeView.setText(flight.startTime.toString());
//            } else {
//                startTimeView.setText("Flight Incomplete");
//            }

//            if (flight.endTime != null) {
//                endTimeView.setText(flight.endTime.toString());
//            } else {
//                endTimeView.setText("Flight Incomplete");
//            }
        }
    }
}
