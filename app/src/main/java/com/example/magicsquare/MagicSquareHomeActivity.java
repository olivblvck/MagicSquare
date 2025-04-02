package com.example.magicsquare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Second screen where the user selects difficulty level.
 * Launches the MagicSquareActivity with selected level and receives result.
 */
public class MagicSquareHomeActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_GAME = 1;
    public static final String EXTRA_LEVEL = "LEVEL";
    public static final String EXTRA_RESULT = "RESULT";

    EditText editLevel;
    Button buttonStartGame;
    TextView textResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magic_square_home);

        editLevel = findViewById(R.id.editLevel);
        buttonStartGame = findViewById(R.id.buttonStartGame);
        textResult = findViewById(R.id.textResult);

        // Start the game when the button is clicked
        buttonStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String levelStr = editLevel.getText().toString().trim();

                if (levelStr.isEmpty()) {
                    Toast.makeText(MagicSquareHomeActivity.this, "Please enter a level between 1 and 9", Toast.LENGTH_SHORT).show();
                    return;
                }

                int level = Integer.parseInt(levelStr);
                if (level < 1 || level > 9) {
                    Toast.makeText(MagicSquareHomeActivity.this, "Level must be between 1 and 9", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Hide keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(editLevel.getWindowToken(), 0);
                }

                // Start MagicSquareActivity
                Intent intent = new Intent(MagicSquareHomeActivity.this, MagicSquareActivity.class);
                intent.putExtra(EXTRA_LEVEL, level);
                startActivityForResult(intent, REQUEST_CODE_GAME);
            }
        });
    }

    // Receive result from the game activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_GAME && resultCode == RESULT_OK && data != null) {
            String result = data.getStringExtra(EXTRA_RESULT);
            textResult.setText("Last game result: " + result);
        }
    }
}
