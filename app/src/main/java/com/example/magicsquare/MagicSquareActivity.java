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
    private GridLayout gridLayout;   // GridLayout with game board
    private TextView textMessage;   // Message displayed under the grid
    private int[][] solution = new int[3][3];   // 3x3 solution matrix
    private View[][] cells = new View[3][3]; // References to Views (EditTexts or TextViews) in grid
    private static final int SIZE = 3;  // Size of the grid (3x3)
    private int level; // number of empty cells (difficulty level)
    private boolean isCorrect = false; // is solution correct flag
    private Set<Integer> emptyPositions = new HashSet<>();  // Positions (0 to 8) of cells that should be left empty

    // Sums for each row and column
    private int[] rowSums = new int[3];
    private int[] colSums = new int[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Binding activity to XML layout file
        setContentView(R.layout.activity_magic_square);

        // Get view references
        gridLayout = findViewById(R.id.gridLayout);
        textMessage = findViewById(R.id.textMessage);
        Button buttonCheck = findViewById(R.id.buttonCheck);
        Button buttonExit = findViewById(R.id.buttonExit);
        Button buttonRules = findViewById(R.id.buttonRules);

        // Get difficulty level from intent
        level = getIntent().getIntExtra(MagicSquareHomeActivity.EXTRA_LEVEL, 3);

        if (savedInstanceState == null) {
            // Only generate a new board if this is the first creation
            generateMagicSquare();
            populateGrid();
        }
        // else: grid and data will be restored in onRestoreInstanceState()

        // Check button: validate user input
        buttonCheck.setOnClickListener(v -> checkSolution());

        // Exit button: return to previous screen with result
        buttonExit.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            String result = isCorrect ? "Success" : "Failed";
            resultIntent.putExtra(MagicSquareHomeActivity.EXTRA_RESULT, result);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        // Rules button: open Wikipedia link
        buttonRules.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://en.wikipedia.org/wiki/Magic_square"));
            startActivity(browserIntent);
        });
    }

    // Method generates unique numbers 1–9 and stores them in the solution array
    // Also calculates row and column sums and randomly selects empty positions
    private void generateMagicSquare() {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= 9; i++) numbers.add(i);
        Collections.shuffle(numbers); // shuffle numbers

        // Fill the 3x3 solution array with the shuffled numbers
        int index = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                solution[i][j] = numbers.get(index++);
            }
        }
        // Calculate row and column sums
        for (int i = 0; i < SIZE; i++) rowSums[i] = 0;
        for (int j = 0; j < SIZE; j++) colSums[j] = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                rowSums[i] += solution[i][j];
                colSums[j] += solution[i][j];
            }
        }

        // Randomly select positions to be left empty (based on difficulty)
        emptyPositions.clear();
        Random random = new Random();
        while (emptyPositions.size() < level) {
            emptyPositions.add(random.nextInt(9));
        }

    }

    // Display the grid and corresponding row/column sums
    private void populateGrid() {
        gridLayout.removeAllViews(); // Clear previous views to reset layout

        // Fill grid cells with numbers or input fields
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                int cellIndex = i * SIZE + j;
                View cell;

                // Create EditText if it's an empty position, otherwise TextView
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

            // "=" sign before each row sum
            TextView eq = new TextView(this);
            eq.setText("=");
            eq.setGravity(Gravity.CENTER);
            GridLayout.LayoutParams eqParams = new GridLayout.LayoutParams();
            eqParams.rowSpec = GridLayout.spec(i);
            eqParams.columnSpec = GridLayout.spec(SIZE); // column 3
            eqParams.width = 40;
            eqParams.height = 120;
            gridLayout.addView(eq, eqParams);

            // Row sum value
            TextView rowSum = new TextView(this);
            rowSum.setText(String.valueOf(rowSums[i]));
            rowSum.setTypeface(null, Typeface.BOLD);
            rowSum.setTextColor(Color.parseColor("#63519F"));
            rowSum.setGravity(Gravity.CENTER);
            GridLayout.LayoutParams rowParams = new GridLayout.LayoutParams();
            rowParams.rowSpec = GridLayout.spec(i);
            rowParams.columnSpec = GridLayout.spec(SIZE + 1); // column 4
            rowParams.setMargins(4, 4, 4, 4);
            rowParams.width = 120;
            rowParams.height = 120;
            gridLayout.addView(rowSum, rowParams);
        }

        for (int j = 0; j < SIZE; j++) {
            // Add "=" signs above each column sum
            TextView eq = new TextView(this);
            eq.setText("=");
            eq.setGravity(Gravity.CENTER);
            GridLayout.LayoutParams eqParams = new GridLayout.LayoutParams();
            eqParams.rowSpec = GridLayout.spec(SIZE); // row 3
            eqParams.columnSpec = GridLayout.spec(j);
            eqParams.width = 120;
            eqParams.height = 40;
            gridLayout.addView(eq, eqParams);

            // Display column sums
            TextView colSum = new TextView(this);
            colSum.setText(String.valueOf(colSums[j]));
            colSum.setTypeface(null, Typeface.BOLD);
            colSum.setTextColor(Color.parseColor("#63519F"));
            colSum.setGravity(Gravity.CENTER);
            GridLayout.LayoutParams colParams = new GridLayout.LayoutParams();
            colParams.rowSpec = GridLayout.spec(SIZE + 1); // row 4
            colParams.columnSpec = GridLayout.spec(j);
            colParams.setMargins(4, 4, 4, 4);
            colParams.width = 120;
            colParams.height = 120;
            gridLayout.addView(colSum, colParams);
        }

    }


    // Checks the user's solution
    private void checkSolution() {
        Set<Integer> usedNumbers = new HashSet<>();
        int[][] userBoard = new int[SIZE][SIZE];

        // Read input values from each cell
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

                // Validate range and uniqueness
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

        // If all checks passed
        textMessage.setText("Congratulations! You solved it correctly.");
        isCorrect = true;

    }

    // Save board state during orientation change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Store the generated solution (3x3 matrix flattened into a list)
        ArrayList<Integer> flatSolution = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                flatSolution.add(solution[i][j]);
            }
        }
        outState.putIntegerArrayList("solution", flatSolution);

        // Store the current level (number of empty cells)
        outState.putInt("level", level);
        outState.putIntegerArrayList("emptyPositions", new ArrayList<>(emptyPositions));


        // Store user input values from EditTexts; null for TextViews
        ArrayList<String> userInputs = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                View v = cells[i][j];
                if (v instanceof EditText) {
                    userInputs.add(((EditText) v).getText().toString());
                } else {
                    userInputs.add(null); // cell was a TextView, not editable
                }
            }
        }
        outState.putStringArrayList("userInputs", userInputs);
    }

    // Restore board state after orientation change
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore the flattened 3x3 solution matrix
        ArrayList<Integer> flatSolution = savedInstanceState.getIntegerArrayList("solution");
        level = savedInstanceState.getInt("level", 3); // default to 3 if missing
        ArrayList<Integer> restored = savedInstanceState.getIntegerArrayList("emptyPositions");
        emptyPositions = new HashSet<>(restored);

        int index = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                solution[i][j] = flatSolution.get(index++);
            }
        }

        // Recalculate sums
        for (int i = 0; i < SIZE; i++) rowSums[i] = 0;
        for (int j = 0; j < SIZE; j++) colSums[j] = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                rowSums[i] += solution[i][j];
                colSums[j] += solution[i][j];
            }
        }

        // Recreate the grid with the correct level and positions
        populateGrid();

        // Restore user inputs into corresponding EditText fields
        ArrayList<String> userInputs = savedInstanceState.getStringArrayList("userInputs");
        index = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                View v = cells[i][j];
                String input = userInputs.get(index++);
                if (v instanceof EditText && input != null) {
                    ((EditText) v).setText(input);
                }
            }
        }
    }


}

