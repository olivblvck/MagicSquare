package com.example.magicsquare;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.graphics.Typeface;


import java.util.*;

public class MagicSquareActivity extends AppCompatActivity {

    // GridLayout with game board
    private GridLayout gridLayout;
    // Message displayed under the grid
    private TextView textMessage;
    // 3x3 solution matrix (random permutation of 1–9)
    private int[][] solution = new int[3][3];
    // References to Views (EditTexts or TextViews) in grid
    private View[][] cells = new View[3][3];
    // Row and column sum arrays
    private int[] rowSums = new int[3];
    private int[] colSums = new int[3];

    private static final int SIZE = 3; // board dimension
    private int level; // number of empty cells

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magic_square);

        // Get view references
        gridLayout = findViewById(R.id.gridLayout);
        textMessage = findViewById(R.id.textMessage);
        Button buttonCheck = findViewById(R.id.buttonCheck);
        Button buttonExit = findViewById(R.id.buttonExit);
        Button buttonRules = findViewById(R.id.buttonRules);

        // Get difficulty level from intent
        level = getIntent().getIntExtra(MagicSquareHomeActivity.EXTRA_LEVEL, 3);
        generateMagicSquare();     // generate random 3x3 matrix
        populateGrid(level);       // fill grid with inputs/texts and sums

        // Button actions
        buttonCheck.setOnClickListener(v -> checkSolution());

        buttonExit.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(MagicSquareHomeActivity.EXTRA_RESULT, "Success"); // domyślnie
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        buttonRules.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://en.wikipedia.org/wiki/Magic_square"));
            startActivity(browserIntent);
        });
    }

    // Generates a randomized 3x3 square with digits 1-9 (no duplicates)
    private void generateMagicSquare() {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= 9; i++) numbers.add(i);
        Collections.shuffle(numbers);

        // Fill solution matrix
        int index = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                solution[i][j] = numbers.get(index++);
            }
        }
        // Calculate row and column sums
        for (int i = 0; i < SIZE; i++) {
            rowSums[i] = 0;
        }
        for (int j = 0; j < SIZE; j++) {
            colSums[j] = 0;
        }
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                rowSums[i] += solution[i][j];
                colSums[j] += solution[i][j];
            }
        }

    }

    // Populates the GridLayout with cells and sum indicators
    private void populateGrid(int level) {
        gridLayout.removeAllViews();
        Set<Integer> emptyPositions = new HashSet<>();
        Random random = new Random();
        while (emptyPositions.size() < level) {
            emptyPositions.add(random.nextInt(9)); // 0..8
        }

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                int cellIndex = i * SIZE + j;
                View cell;

                if (emptyPositions.contains(cellIndex)) {
                    EditText edit = new EditText(this);
                    edit.setEms(2);
                    edit.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
                    cell = edit;
                } else {
                    TextView text = new TextView(this);
                    text.setText(String.valueOf(solution[i][j]));
                    text.setGravity(Gravity.CENTER);
                    cell = text;
                }

                cells[i][j] = cell;
                GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                param.setMargins(4, 4, 4, 4);
                param.width = 120;
                param.height = 120;
                param.rowSpec = GridLayout.spec(i);
                param.columnSpec = GridLayout.spec(j);
                gridLayout.addView(cell, param);
            }

            //row sum
            TextView rowSum = new TextView(this);
            rowSum.setText(String.valueOf(rowSums[i]));
            rowSum.setTypeface(null, Typeface.BOLD);
            rowSum.setTextColor(Color.parseColor("#63519F"));
            rowSum.setGravity(Gravity.CENTER);
            GridLayout.LayoutParams rowParams = new GridLayout.LayoutParams();
            rowParams.rowSpec = GridLayout.spec(i);
            rowParams.columnSpec = GridLayout.spec(SIZE); // kolumna 3
            rowParams.setMargins(4, 4, 4, 4);
            rowParams.width = 120;
            rowParams.height = 120;
            gridLayout.addView(rowSum, rowParams);
        }

        //  Add column sums below each column
        for (int j = 0; j < SIZE; j++) {
            TextView colSum = new TextView(this);
            colSum.setText(String.valueOf(colSums[j]));
            colSum.setTypeface(null, Typeface.BOLD);
            colSum.setTextColor(Color.parseColor("#63519F"));
            colSum.setGravity(Gravity.CENTER);
            GridLayout.LayoutParams colParams = new GridLayout.LayoutParams();
            colParams.rowSpec = GridLayout.spec(SIZE); // row 3
            colParams.columnSpec = GridLayout.spec(j);
            colParams.setMargins(4, 4, 4, 4);
            colParams.width = 120;
            colParams.height = 120;
            gridLayout.addView(colSum, colParams);
        }

        // Bottom-right corner empty cell
        TextView corner = new TextView(this);
        GridLayout.LayoutParams cornerParams = new GridLayout.LayoutParams();
        cornerParams.rowSpec = GridLayout.spec(SIZE); // 3
        cornerParams.columnSpec = GridLayout.spec(SIZE); // 3
        cornerParams.width = 120;
        cornerParams.height = 120;
        gridLayout.addView(corner, cornerParams);
    }

    // Checks the user's solution
    private void checkSolution() {
        Set<Integer> usedNumbers = new HashSet<>();
        int[][] userBoard = new int[SIZE][SIZE];

        // Read values from grid
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                View cell = cells[i][j];
                int value;
                if (cell instanceof EditText) {
                    String txt = ((EditText) cell).getText().toString().trim();
                    if (txt.isEmpty()) {
                        textMessage.setText("Please fill in all empty cells.");
                        return;
                    }
                    value = Integer.parseInt(txt);
                } else {
                    value = Integer.parseInt(((TextView) cell).getText().toString());
                }
                if (value < 1 || value > 9 || usedNumbers.contains(value)) {
                    textMessage.setText("Only numbers 1–9 without duplicates are allowed.");
                    return;
                }
                usedNumbers.add(value);
                userBoard[i][j] = value;
            }
        }

        // Verify row sums
        for (int i = 0; i < SIZE; i++) {
            int sum = 0;
            for (int j = 0; j < SIZE; j++) sum += userBoard[i][j];
            if (sum != rowSums[i]) {
                textMessage.setText("Wrong sum in row " + (i + 1) + ".");
                return;
            }
        }
        // Verify column sums
        for (int j = 0; j < SIZE; j++) {
            int sum = 0;
            for (int i = 0; i < SIZE; i++) sum += userBoard[i][j];
            if (sum != colSums[j]) {
                textMessage.setText("Wrong sum in column " + (j + 1) + " .");
                return;
            }
        }

        textMessage.setText("Congratulations! You solved it correctly.");
    }
}
