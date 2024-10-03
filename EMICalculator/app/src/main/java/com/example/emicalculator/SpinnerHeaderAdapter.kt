package com.example.emicalculator
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

// Defines two data classes to differentiate between Spinner Headers and Spinner Items.
data class Header(val header: String)
data class Item(val itemName: String)

// Constructor of the custom spinner.
class SpinnerHeaderAdapter(
    context: Context,
    private val items: List<Any>
) : ArrayAdapter<Any>(context, 0, items) {

    // Overrides the default getView function of ArrayAdapter class to implement the logic of the custom one.
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    // Overrides the default getDropDownView function of ArrayAdapter class to implement the logic of the custom one.
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    // this function is responsible for displaying the headers and items of the spinner appropriately
    // headers get .
    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = getItem(position)
        val view: View
        val defaultText = "N/A"

        // This is responsible for displaying the actual text within the Header and or Item data classes
        // that the custom array adapter accepts as elements.
        when (item) {
            is Header -> {
                view = LayoutInflater.from(context).inflate(R.layout.spinner_header, parent, false)
                val textView = view.findViewById<TextView>(R.id.spinnerHeader)
                textView.text = item.header
            }
            is Item -> {
                view = LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false)
                val textView = view.findViewById<TextView>(R.id.spinnerItem)
                textView.text = item.itemName
            } else -> {
                view = LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false)
                val textView = view.findViewById<TextView>(R.id.spinnerItem)
                textView.text = defaultText
            }
        }

        // Returns the modified View
        return view
    }
}
