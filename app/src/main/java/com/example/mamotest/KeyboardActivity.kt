package com.example.mamotest

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.davidmiguel.numberkeyboard.NumberKeyboard
import com.davidmiguel.numberkeyboard.NumberKeyboardListener


class KeyboardActivity : AppCompatActivity(), NumberKeyboardListener {

    private lateinit var amountEditText: TextView
    private var amountText: String = ""
    private var amount: Double = 0.00

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keyboard)
        amountEditText = findViewById(R.id.amount)
        val numberKeyboard = findViewById<NumberKeyboard>(R.id.numberKeyboard)
        numberKeyboard.setListener(this)
    }

    override fun onNumberClicked(number: Int) {
        if (amountText.isEmpty() && number == 0) {
            return
        }
        updateAmount(amountText + number)
    }

    override fun onLeftAuxButtonClicked() {
        // decimal button
        if (!hasDecimal(amountText)) {
            amountText = if (amountText.isEmpty()) "0.00" else "$amountText."
            showAmount(amountText)
        }
    }

    override fun onRightAuxButtonClicked() {
        // Delete button
        if (amountText.isEmpty()) {
            return
        }
        var newAmountText: String
        if (amountText.length <= 1) {
            newAmountText = ""
        } else {
            newAmountText = amountText.substring(0, amountText.length - 1)
            if (newAmountText[newAmountText.length - 1] == ',') {
                newAmountText = newAmountText.substring(0, newAmountText.length - 1)
            }
            if ("0" == newAmountText) {
                newAmountText = ""
            }
        }
        updateAmount(newAmountText)
    }

    /**
     * Update new entered amount if it is valid.
     */
    private fun updateAmount(newAmountText: String) {
        val newAmount = if (newAmountText.isEmpty()) 0.00 else java.lang.Double.parseDouble(newAmountText.replace(",".toRegex(), "."))
        if (newAmount in 0.00..MAX_ALLOWED_AMOUNT
                && getNumDecimals(newAmountText) <= MAX_ALLOWED_DECIMALS) {
            amountText = newAmountText
            amount = newAmount
            showAmount(amountText)
        }
    }

    /**
     * Shows amount in UI.
     */
    private fun showAmount(amount: String) {
        var integer: String= "0.00"

        if (amount.isEmpty()){
            amountEditText.text ="AED "+integer
            amountEditText.setTextColor(ContextCompat.getColor(this, R.color.gray));
        }else{
            amountEditText.setTextColor(ContextCompat.getColor(this, R.color.black));
            val builder = SpannableStringBuilder()

            val decimal: String
            var start: Int = 0

            if (amount.indexOf('.') >= 0) {
                integer = amount.substring(0, amount.indexOf('.'))
                if(getNumDecimals(amount)==0){
                    decimal = amount.substring(amount.indexOf('.'), amount.length)+"00"
                    start=1
                }else if(getNumDecimals(amount)==1){
                    start=2
                    decimal = amount.substring(amount.indexOf('.'), amount.length)+"0"
                }else{
                    start=3
                    decimal = amount.substring(amount.indexOf('.'), amount.length)
                }
            } else {
                integer = amount
                decimal = ".00"

            }
            if (integer.length > 3) {
                val tmp = StringBuilder(integer)
                var i = integer.length - 3
                while (i > 0) {
                    tmp.insert(i, ",")
                    i -= 3
                }
                integer = tmp.toString()
            }

            val redSpannable = SpannableString("AED "+integer)
            redSpannable.setSpan(ForegroundColorSpan(Color.BLACK), 0, ("AED "+integer).length, 0)
            builder.append(redSpannable)

            val whiteSpannable = SpannableString(decimal)
            whiteSpannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.gray)), start, (decimal).length, 0)
            builder.append(whiteSpannable)

            amountEditText.setText(builder, TextView.BufferType.SPANNABLE);
        }
    }

    /**
     * Checks whether the string has a decimal.
     */
    private fun hasDecimal(text: String): Boolean {
        for (element in text) {
            if (element == '.') {
                return true
            }
        }
        return false
    }

    /**
     * Calculate the number of decimals of the string.
     */
    private fun getNumDecimals(num: String): Int {

        return if (!hasDecimal(num)) {
            0
        } else num.substring(num.indexOf('.') + 1, num.length).length
    }


    companion object {
        private const val MAX_ALLOWED_AMOUNT = 99999999.99
        private const val MAX_ALLOWED_DECIMALS = 2
    }
}
