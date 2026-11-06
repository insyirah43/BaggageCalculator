package com.example.baggagecalculator;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

    // Declare UI components for Inputs
    private EditText etPassengerName, etWeight;
    private RadioGroup rgFlightType, rgClass;
    private RadioButton rbDomestic, rbEconomy;
    private Button btnCalculate;

    // Declare UI components for 7 Outputs
    private TextView tvOutName, tvOutFlightType, tvOutTicketClass, tvOutRate, tvExcessWeight, tvExcessCharge, tvTotalPayable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up Toolbar with About & Share menu
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Excess Baggage Fee Calculator");
        }

        // Link UI Input components with XML IDs
        etPassengerName = findViewById(R.id.etPassengerName);
        etWeight = findViewById(R.id.etWeight);
        rgFlightType = findViewById(R.id.rgFlightType);
        rgClass = findViewById(R.id.rgClass);
        rbDomestic = findViewById(R.id.rbDomestic);
        rbEconomy = findViewById(R.id.rbEconomy);
        btnCalculate = findViewById(R.id.btnCalculate);

        // Link UI Output components with XML IDs
        tvOutName = findViewById(R.id.tvOutName);
        tvOutFlightType = findViewById(R.id.tvOutFlightType);
        tvOutTicketClass = findViewById(R.id.tvOutTicketClass);
        tvOutRate = findViewById(R.id.tvOutRate);
        tvExcessWeight = findViewById(R.id.tvExcessWeight);
        tvExcessCharge = findViewById(R.id.tvExcessCharge);
        tvTotalPayable = findViewById(R.id.tvTotalPayable);

        // Set click listener for the calculate button
        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateBaggageFee();
            }
        });
    }

    // Calculate excess baggage fee and tax
    private void calculateBaggageFee() {
        String name = etPassengerName.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();

        // Error validation: Ensure input fields are not empty
        if (TextUtils.isEmpty(name)) {
            etPassengerName.setError("Please enter passenger name");
            Toast.makeText(this, "Please enter passenger name.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(weightStr)) {
            etWeight.setError("Please enter baggage weight");
            Toast.makeText(this, "Please enter baggage weight.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double totalWeight = Double.parseDouble(weightStr);
            String flightType = rbDomestic.isChecked() ? "Domestic" : "International";
            String ticketClass = rbEconomy.isChecked() ? "Economy" : "Business";

            // Determine allowance limit: Economy = 20kg, Business = 40kg
            double weightAllowance = rbEconomy.isChecked() ? 20.0 : 40.0;
            double excessRatePerKg;

            // Fixed excess rate logic based on Flight Type and Ticket Class
            if (rbDomestic.isChecked()) {
                excessRatePerKg = rbEconomy.isChecked() ? 10.0 : 15.0; // Domestic (Eco: RM10, Biz: RM15)
            } else {
                excessRatePerKg = rbEconomy.isChecked() ? 20.0 : 25.0; // International (Eco: RM20, Biz: RM25)
            }

            // 1. Calculate excess weight (weight minus allowance limit)
            double excessWeight = totalWeight - weightAllowance;
            if (excessWeight < 0) {
                excessWeight = 0;
            }

            // 2. Calculate excess baggage charge = excess weight * rate
            double excessCharge = excessWeight * excessRatePerKg;

            // 3. Calculate total payable amount including 8% service tax
            double totalPayable = excessCharge * 1.08;

            // Display outputs
            tvOutName.setText("Passenger Name: " + name);
            tvOutFlightType.setText("Flight Type: " + flightType);
            tvOutTicketClass.setText("Ticket Class: " + ticketClass);
            tvOutRate.setText(String.format("Excess Rate / kg: RM %.2f", excessRatePerKg));
            tvExcessWeight.setText(String.format("Excess Baggage Weight: %.2f kg", excessWeight));
            tvExcessCharge.setText(String.format("Excess Baggage Charge: RM %.2f", excessCharge));
            tvTotalPayable.setText(String.format("Total Amount Payable (inc. 8%% tax): RM %.2f", totalPayable));

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numeric numbers.", Toast.LENGTH_SHORT).show();
        }
    }

    // Toolbar menu for Share and About options
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
            // Open About Activity
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