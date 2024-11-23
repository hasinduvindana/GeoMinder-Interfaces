package com.example.geominder_interfaces;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class AddTaskDialogFragment extends DialogFragment {

    public interface TaskCallback {
        void onTaskAdded(String taskName, String description, String date, String time, String location);
    }

    private EditText editTextActivityName, editTextDescription, editTextDate, editTextTime, editTextLocation;
    private Calendar calendar;
    private TaskCallback taskCallback;

    // Default constructor
    public AddTaskDialogFragment() {
    }

    public void setTaskCallback(TaskCallback callback) {
        this.taskCallback = callback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the Places API
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), "AIzaSyDZorhvz4FK1jUDm9H9ojuqUlzsJW62wz4");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_task, container, false);

        editTextActivityName = view.findViewById(R.id.editTextActivityName);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        editTextDate = view.findViewById(R.id.editTextDate);
        editTextTime = view.findViewById(R.id.editTextTime);
        editTextLocation = view.findViewById(R.id.editTextLocation);

        Button btnSubmit = view.findViewById(R.id.btnSubmit);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnClear = view.findViewById(R.id.btnClear);

        calendar = Calendar.getInstance();

        // Handle date selection
        editTextDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (view1, year, month, dayOfMonth) -> editTextDate.setText(year + "-" + (month + 1) + "-" + dayOfMonth),
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        // Handle time selection
        editTextTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                    (view12, hourOfDay, minute) -> editTextTime.setText(String.format("%02d:%02d", hourOfDay, minute)),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true);
            timePickerDialog.show();
        });

        // Handle location selection
        editTextLocation.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(requireContext());
            startActivityForResult(intent, 1001);
        });

        btnSubmit.setOnClickListener(v -> {
            String taskName = editTextActivityName.getText().toString();
            String description = editTextDescription.getText().toString();
            String date = editTextDate.getText().toString();
            String time = editTextTime.getText().toString();
            String location = editTextLocation.getText().toString();

            if (TextUtils.isEmpty(taskName) || TextUtils.isEmpty(description) || TextUtils.isEmpty(date)
                    || TextUtils.isEmpty(time) || TextUtils.isEmpty(location)) {
                Snackbar.make(v, "All fields are required", Snackbar.LENGTH_LONG).show();
            } else {
                if (taskCallback != null) {
                    taskCallback.onTaskAdded(taskName, description, date, time, location);
                }
                dismiss();
            }
        });

        btnCancel.setOnClickListener(v -> dismiss());
        btnClear.setOnClickListener(v -> {
            editTextActivityName.setText("");
            editTextDescription.setText("");
            editTextDate.setText("");
            editTextTime.setText("");
            editTextLocation.setText("");
        });

        getDialog().getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        return view;
    }

     @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                // Get the selected place
                Place place = Autocomplete.getPlaceFromIntent(data);
                editTextLocation.setText(place.getName()); // Set the location name in the text field
            } else if (resultCode == Activity.RESULT_CANCELED && data != null) {
                // Handle errors
                Status status = Autocomplete.getStatusFromIntent(data);
                Snackbar.make(editTextLocation, "Error: " + status.getStatusMessage(), Snackbar.LENGTH_LONG).show();
            }
        }
    }

}
