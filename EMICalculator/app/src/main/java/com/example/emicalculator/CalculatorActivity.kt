package com.example.emicalculator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Typeface
import kotlin.math.pow

// This Activity is responsible for displaying and calculating the EMI
class CalculatorActivity : AppCompatActivity() {
    // This Function is responsible for validating that the user has selected an actual
    // item of the spinner class and not just a header.
    private fun spinnerValidation(spinnerArray: List<Spinner>): Boolean{
        val messageBox = findViewById<TextView>(R.id.messageBox)
        var outputText = ""
        var validate = true
        for (spinner in spinnerArray){
            val currentSelection = spinner.selectedItem

            //Responsible for displaying correct error message (in case of multiple Spinners)
            when(currentSelection){
                is Header -> {
                    val firstHeader = spinner.getItemAtPosition(0) as? Header
                    outputText += "*Please ${firstHeader?.header}*\n"
                    validate = false
                }
            }
        }

        //Adds whatever errors were caught to be displayed to the user
        messageBox.text = outputText
        return validate
    }

    // This function is responsible for validating that the user entered a valid integer in fields
    // where an integer is expected.
    private fun integerValidation(): Boolean {
        val mortgage = findViewById<EditText>(R.id.mortgagePrincipalAmountInput).text.toString()
        val interest = findViewById<EditText>(R.id.interestRateInput).text.toString()
        val messageBox = findViewById<TextView>(R.id.messageBox)
        var outputText = "${messageBox.text}"
        var validate = true

        //Try-Catch block for the mortgage amount input
        try{
            mortgage.toDouble()
        } catch (e: NumberFormatException){
            messageBox.setTextColor(Color.RED)
            outputText = "*Invalid Mortgage Amount*\n${outputText}"
            validate = false
        }

        //Try-Catch block for the interest rate input
        try{
            interest.toDouble()
        } catch (e: NumberFormatException){
            messageBox.setTextColor(Color.RED)
            outputText = "*Invalid Interest Rate*\n${outputText}"
            validate = false
        }

        //Adds whatever errors were caught to be displayed to the user
        messageBox.text = outputText
        return validate
    }

    // Is responsible for formatting the error message so it looks like an error
    // and the use can understand that something has gone wrong.
    private fun errorMessageFormatter(){
        val messageBox = findViewById<TextView>(R.id.messageBox)
        messageBox.setTextColor(Color.RED)
        messageBox.textSize = 16f
        messageBox.setTypeface(null, Typeface.NORMAL)
    }

    // This function is responsible for calculating the EMI and displaying it to the user.
    private fun calculateEMI(): Double{
        val messageBox = findViewById<TextView>(R.id.messageBox)
        val mortgageAmount = findViewById<EditText>(R.id.mortgagePrincipalAmountInput)
        val interestRate = findViewById<EditText>(R.id.interestRateInput)
        val amortizationPeriod  =  findViewById<Spinner>(R.id.amortizationSpinner).selectedItem as? Item

        // Parses user input to necessary data formats.
        val principal = mortgageAmount.text.toString().toDouble()
        val interest = interestRate.text.toString().toDouble()
        val period = amortizationPeriod?.itemName?.split("\\s+".toRegex())?.get(0)?.toDouble()?.times(12.00) ?: 0.00

        // Calculates EMI and makes a String to be displayed to the user.
        val monthlyInterest = interest/(12.00 * 100.00)
        val emi = (principal * monthlyInterest * (1 + monthlyInterest).pow(period))/((1 + monthlyInterest).pow(period) - 1.0)
        val outputMessage = "EMI: $${"%,.2f".format(emi)}/monthly"

        // Formats the message to be displayed to user.
        messageBox.textSize = 20f
        messageBox.setTypeface(null, Typeface.BOLD)
        messageBox.setTextColor(Color.BLACK)
        messageBox.text = outputMessage

        return emi
    }

    // This function is responsible for calculating the total interest paid, as well as
    // the total amount paid.
    private fun calculateTotalInterest(emi: Double): DoubleArray {
        val mortgageAmount = findViewById<EditText>(R.id.mortgagePrincipalAmountInput)
        val interestRate = findViewById<EditText>(R.id.interestRateInput)
        val amortizationPeriod  =  findViewById<Spinner>(R.id.amortizationSpinner).selectedItem as? Item

        // Parses user input to necessary data formats.
        val principal = mortgageAmount.text.toString().toDouble()
        val interest = interestRate.text.toString().toDouble()
        val period = amortizationPeriod?.itemName?.split("\\s+".toRegex())?.get(0)?.toInt()?.times(12) ?: 0

        // Calculates total interest paid.
        var remainingPrincipal = principal
        val monthlyInterest = interest/(12.00 * 100.00)
        var totalInterest = 0.00
        for (i in 1..period){
            val interestPaid = remainingPrincipal * monthlyInterest
            totalInterest += interestPaid
            remainingPrincipal -= emi - interestPaid
        }

        // Returns the total interest paid and the total amount paid.
        return doubleArrayOf(totalInterest, (principal + totalInterest))
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.calculator_activity)

        // This creates values to be put into a spinner and populates it with said values.
        // This particular spinner is populated with amortization period options.
        val amortizationSpinner = findViewById<Spinner>(R.id.amortizationSpinner)
        val amortizationPeriods = Array(31){
            when(it) {
                0 -> Header("Select Amortization Period")
                1 -> Item("0.5 years")
                2 -> Item("2 years")
                else -> Item("$it years")
            }
        }

        // This custom adapter is responsible for adapting the needed values properly into the spinner.
        // Headers are styled differently than regular Items hence the need for a custom adapter.
        val customHeaderAdapter = SpinnerHeaderAdapter(this, amortizationPeriods.toList())
        amortizationSpinner.adapter = customHeaderAdapter;

        // Creates a list of all the spinners created to passed for validation.
        // (in this case there is only one but it takes a list so it is scalable when there are multiple spinners).
        val  spinnerList = listOf(amortizationSpinner)

        // Adds functionality to the Calculate button.
        val calculateBtn = findViewById<Button>(R.id.calculateBtn)
        calculateBtn.setOnClickListener {

            // Makes sure that user entered valid data, otherwise an error message will be displayed.
            if (spinnerValidation(spinnerList) and integerValidation()){
                calculateEMI()
            } else {
                errorMessageFormatter()
            }
        }

        // Adds functionality to Detailed View button.
        // This button is responsible for navigating the user to a new activity where they can get
        // more details on their EMI, such as the total amount they paid, as well as the total amount
        // of interest paid.
        val seeMoreBtn = findViewById<Button>(R.id.seeMoreBtn)
        seeMoreBtn.setOnClickListener{

            // Makes sure that user has entered valid data in case they decide to go straight to the Detailed View.
            // If there are any errors the activity will not be started and error messages will be displayed.
            if (spinnerValidation(spinnerList) and integerValidation()){

                // Creates the intent and gets/puts necessary information to be displayed to the user in the intent.
                // It also does a little formatting then finally starts the new activity.
                val intent = Intent(this,DetailedViewActivity::class.java)
                val emi = calculateEMI()
                val totalInterest = calculateTotalInterest(emi)
                intent.putExtra("EMI", "$%.2f".format(emi))
                intent.putExtra("TOTAL_INTEREST", "$%,.2f".format(totalInterest[0]))
                intent.putExtra("TOTAL_PAYMENT", "$%,.2f".format(totalInterest[1]))
                startActivity(intent)
            } else{
                errorMessageFormatter()
            }
        }


    }
}