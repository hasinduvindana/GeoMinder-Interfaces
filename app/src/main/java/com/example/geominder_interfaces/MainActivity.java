package com.example.geominder_interfaces;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> tasks;
    private TaskAdapter adapter; // Custom adapter for ListView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listViewTasks = findViewById(R.id.listViewTasks);
        FloatingActionButton fabAddTask = findViewById(R.id.fabAddTask);

        tasks = new ArrayList<>();

        // Initialize custom adapter
        adapter = new TaskAdapter(this, tasks);
        listViewTasks.setAdapter(adapter);

        // Add a new task with FAB click
        fabAddTask.setOnClickListener(v -> {
            AddTaskDialogFragment dialog = new AddTaskDialogFragment();
            dialog.setTaskCallback((taskName, description, date, time, location) -> {
                String taskDetails = "Task: " + taskName + "\nDescription: " + description +
                        "\nDate: " + date + "\nTime: " + time + "\nLocation: " + location;
                tasks.add(taskDetails);
                adapter.notifyDataSetChanged();
            });
            dialog.show(getSupportFragmentManager(), "AddTaskDialog");
        });

        // Handle list item clicks for edit/delete
        listViewTasks.setOnItemClickListener((parent, view, position, id) -> showEditDeleteDialog(position));
    }

    // Method to show edit/delete dialog
    private void showEditDeleteDialog(int position) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.edit_task);

        EditText editTextTitle = dialog.findViewById(R.id.editTextTaskTitle);
        EditText editTextDescription = dialog.findViewById(R.id.editTextTaskDescription);
        Button buttonSave = dialog.findViewById(R.id.buttonSaveTask);
        Button buttonDelete = dialog.findViewById(R.id.buttonDeleteTask);

        // Split task details into title and description (if structured that way)
        String[] taskDetails = tasks.get(position).split("\n", 2);
        String taskTitle = taskDetails[0].replace("Task: ", "");
        String taskDescription = taskDetails.length > 1 ? taskDetails[1].replace("Description: ", "") : "";

        // Pre-fill existing details
        editTextTitle.setText(taskTitle);
        editTextDescription.setText(taskDescription);

        // Save button logic
        buttonSave.setOnClickListener(v -> {
            String updatedTitle = editTextTitle.getText().toString().trim();
            String updatedDescription = editTextDescription.getText().toString().trim();

            if (!updatedTitle.isEmpty()) {
                String updatedTask = "Task: " + updatedTitle + "\nDescription: " + updatedDescription;
                tasks.set(position, updatedTask);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            } else {
                Toast.makeText(MainActivity.this, "Task title cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        // Delete button logic
        buttonDelete.setOnClickListener(v -> {
            tasks.remove(position);
            adapter.notifyDataSetChanged();
            dialog.dismiss();
        });

        dialog.show();
    }
}
