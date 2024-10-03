package com.example.emicalculator

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


// Activity responsible for displaying a more in detailed view of the users EMI.
class DetailedViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.detailed_view)

        // Gets the calculated results opf the EMI from the intent of the previous activity.
        var intent = intent
        val monthlyPayment = intent.getStringExtra("EMI")
        val totalInterestPaid = intent.getStringExtra("TOTAL_INTEREST")
        val totalPayment= intent.getStringExtra("TOTAL_PAYMENT")

        val paymentAmountText = findViewById<TextView>(R.id.paymentAmount)
        val interestPaidText = findViewById<TextView>(R.id.payableInterestAmount)
        val totalPaidText = findViewById<TextView>(R.id.totalAmount)

        // Displays the results to the user.
        paymentAmountText.text = monthlyPayment
        interestPaidText.text = totalInterestPaid
        totalPaidText.text = totalPayment

        // Adds functionality to a back button, in case the user would like to make more
        // EMI calculations. It will start the MainActivity (calculations page) again.
        val backBtn = findViewById<Button>(R.id.backBtn)
        backBtn.setOnClickListener{
            intent = Intent(this,CalculatorActivity::class.java)
            startActivity(intent)
        }

    }
}