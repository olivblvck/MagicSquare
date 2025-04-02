package com.example.magicsquare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Main entry point of the app.
 * Displays a welcome screen and launches MagicSquareHomeActivity.
 */
public class MainActivity extends AppCompatActivity {

    Button buttonStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize start button
        buttonStart = findViewById(R.id.buttonStart);

        // Launch MagicSquareHomeActivity when button is clicked
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MagicSquareHomeActivity.class);
                startActivity(intent);
            }
        });
    }
}
