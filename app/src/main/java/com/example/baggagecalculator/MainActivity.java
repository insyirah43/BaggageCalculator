package com.example.baggagecalculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    // Declare UI components
    private EditText etWeight, etRate;
    private RadioGroup rgClass;
    private RadioButton rbEconomy;
    private TextView tvExcessWeight, tvExcessCharge, tvTotalPayable;
    private Button btnCalculate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set up about menu
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Excess Baggage Fee Estimator");
        }

        // Link UI components with their XML IDs
        etWeight = findViewById(R.id.etWeight);
        etRate = findViewById(R.id.etRate);
        rgClass = findViewById(R.id.rgClass);
        rbEconomy = findViewById(R.id.rbEconomy);
        tvExcessWeight = findViewById(R.id.tvExcessWeight);
        tvExcessCharge = findViewById(R.id.tvExcessCharge);
        tvTotalPayable = findViewById(R.id.tvTotalPayable);
        btnCalculate = findViewById(R.id.btnCalculate);

        // Set click listener for the calculate button
        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateBaggageFee();
            }
        });
    }

    // calculate excess baggage fee and tax
    private void calculateBaggageFee() {
        String weightStr = etWeight.getText().toString().trim();
        String rateStr = etRate.getText().toString().trim();

        // Error validation: Ensure input fields are not empty
        if (weightStr.isEmpty() || rateStr.isEmpty()) {
            Toast.makeText(this, "Please enter both baggage weight and rate charge.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double weight = Double.parseDouble(weightStr);
            double rate = Double.parseDouble(rateStr);

            // Determine allowance limit: Economy = 20kg, Business = 40kg
            double xLimit = rbEconomy.isChecked() ? 20.0 : 40.0;

            // 1. Calculate excess weight (weight minus allowance limit)
            double excessWeight = weight - xLimit;
            if (excessWeight < 0) {
                excessWeight = 0;
            }

            // 2. Calculate excess baggage charge = excess weight * rate
            double excessCharge = excessWeight * rate;

            // 3. Calculate total payable amount including 8% service tax
            double totalPayable = excessCharge + (excessCharge * 0.08);

            // Display clear and formatted results on the TextView outputs
            tvExcessWeight.setText(String.format("Excess Baggage Weight: %.2f kg", excessWeight));
            tvExcessCharge.setText(String.format("Excess Baggage Charge: RM %.2f", excessCharge));
            tvTotalPayable.setText(String.format("Total Amount Payable (inc. 8%% tax): RM %.2f", totalPayable));

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numeric numbers.", Toast.LENGTH_SHORT).show();
        }
    }

    // Toolbar menu inflation for Share and About options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // Handle clicks on toolbar menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about) {
            // Open About
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_share) {
            // Share application link with other users
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareMessage = "Check out my Excess Baggage Fee Estimator app here: https://github.com/insyirah43/BaggageCalculator.git";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}