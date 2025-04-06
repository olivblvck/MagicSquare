package com.example.magicsquare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

// Main entry point of the app.

// Displays a welcome screen and launches MagicSquareHomeActivity.

public class MainActivity extends AppCompatActivity {

    Button buttonStart; // Reference to the start button in the layout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the layout for this activity
        setContentView(R.layout.activity_main);

        // Connect Java variable to the button defined in XML layout
        buttonStart = findViewById(R.id.buttonStart);

        // Launch MagicSquareHomeActivity when button is clicked
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to go from MainActivity to MagicSquareHomeActivity
                Intent intent = new Intent(MainActivity.this, MagicSquareHomeActivity.class);
                // Start the new activity
                startActivity(intent);
            }
        });
    }
}
