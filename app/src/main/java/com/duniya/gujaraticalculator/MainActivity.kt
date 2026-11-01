package com.duniya.gujaraticalculator

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class MainActivity : Activity() {
    private lateinit var expressionView: TextView
    private lateinit var resultView: TextView
    private var expression = ""
    private var justEvaluated = false

    private val gujaratiDigits = "૦૧૨૩૪૫૬૭૮૯"
    private val westernDigits = "0123456789"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(17, 24, 39)
        window.navigationBarColor = Color.rgb(17, 24, 39)
        window.decorView.systemUiVisibility = 0
        buildScreen()
        refresh()
    }

    private fun buildScreen() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(26), dp(20), dp(18))
            background = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(Color.rgb(17, 24, 39), Color.rgb(31, 41, 55), Color.rgb(49, 46, 129))
            )
        }

        val header = TextView(this).apply {
            text = "ગુજરાતી કેલ્ક્યુલેટર"
            setTextColor(Color.WHITE)
            textSize = 23f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }
        root.addView(header, LinearLayout.LayoutParams(-1, dp(48)))

        val subtitle = TextView(this).apply {
            text = "સરળ • ઝડપી • સ્માર્ટ"
            setTextColor(Color.rgb(196, 181, 253))
            textSize = 13f
            gravity = Gravity.CENTER
        }
        root.addView(subtitle, LinearLayout.LayoutParams(-1, dp(28)))

        val display = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.BOTTOM or Gravity.END
            setPadding(dp(20), dp(18), dp(20), dp(18))
            background = rounded(Color.argb(42, 255, 255, 255), dp(24))
        }
        val displayParams = LinearLayout.LayoutParams(-1, 0, 1.0f).apply {
            topMargin = dp(18)
            bottomMargin = dp(20)
        }
        expressionView = TextView(this).apply {
            text = ""
            setTextColor(Color.rgb(203, 213, 225))
            textSize = 22f
            gravity = Gravity.END
            maxLines = 2
        }
        resultView = TextView(this).apply {
            text = "૦"
            setTextColor(Color.WHITE)
            textSize = 48f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.END
            maxLines = 1
        }
        display.addView(expressionView, LinearLayout.LayoutParams(-1, dp(58)))
        display.addView(resultView, LinearLayout.LayoutParams(-1, dp(76)))
        root.addView(display, displayParams)

        val keys = arrayOf(
            arrayOf("AC", "⌫", "%", "÷"),
            arrayOf("૭", "૮", "૯", "×"),
            arrayOf("૪", "૫", "૬", "−"),
            arrayOf("૧", "૨", "૩", "+"),
            arrayOf("±", "૦", ".", "=")
        )
        val grid = GridLayout(this).apply {
            columnCount = 4
            rowCount = 5
            useDefaultMargins = false
        }
        val gap = dp(9)
        keys.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, label ->
                val button = Button(this).apply {
                    text = label
                    textSize = if (label == "AC" || label == "⌫") 17f else 23f
                    isAllCaps = false
                    typeface = Typeface.DEFAULT_BOLD
                    setTextColor(
                        when (label) {
                            "=" -> Color.WHITE
                            "÷", "×", "−", "+", "%" -> Color.rgb(221, 214, 254)
                            "AC", "⌫", "±" -> Color.rgb(248, 250, 252)
                            else -> Color.WHITE
                        }
                    )
                    background = rounded(
                        when (label) {
                            "=" -> Color.rgb(124, 58, 237)
                            "÷", "×", "−", "+", "%" -> Color.argb(210, 91, 65, 160)
                            "AC", "⌫", "±" -> Color.argb(95, 255, 255, 255)
                            else -> Color.argb(48, 255, 255, 255)
                        }, dp(18)
                    )
                    stateListAnimator = null
                    setOnClickListener { onKey(label) }
                }
                val params = GridLayout.LayoutParams(
                    GridLayout.spec(rowIndex, 1, 1f),
                    GridLayout.spec(colIndex, 1, 1f)
                ).apply {
                    width = 0
                    height = dp(66)
                    setMargins(gap / 2, gap / 2, gap / 2, gap / 2)
                }
                grid.addView(button, params)
            }
        }
        root.addView(grid, LinearLayout.LayoutParams(-1, dp(380)))
        setContentView(root)
    }

    private fun onKey(key: String) {
        when (key) {
            "AC" -> { expression = ""; justEvaluated = false }
            "⌫" -> {
                if (expression.isNotEmpty()) expression = expression.dropLast(1)
                justEvaluated = false
            }
            "=" -> {
                try {
                    val value = Parser(expression).parse()
                    expression = format(value)
                    justEvaluated = true
                } catch (_: Exception) {
                    resultView.text = "ભૂલ"
                    return
                }
            }
            "±" -> {
                if (expression.isNotEmpty()) {
                    val value = try { Parser(expression).parse() } catch (_: Exception) { return }
                    expression = format(-value)
                    justEvaluated = true
                }
            }
            else -> {
                if (justEvaluated && (key.firstOrNull()?.isDigit() == true || key == ".")) expression = ""
                justEvaluated = false
                val token = when (key) {
                    "÷" -> "/"
                    "×" -> "*"
                    "−" -> "-"
                    else -> if (key.length == 1 && key[0] in gujaratiDigits) {
                        westernDigits[gujaratiDigits.indexOf(key[0])].toString()
                    } else key
                }
                if (key == "." && currentNumberHasDot()) return
                expression += token
            }
        }
        refresh()
    }

    private fun currentNumberHasDot(): Boolean {
        val part = expression.takeLastWhile { it.isDigit() || it == '.' }
        return part.contains('.')
    }

    private fun refresh() {
        expressionView.text = toGujarati(expression.replace("*", "×").replace("/", "÷").replace("-", "−"))
        if (expression.isEmpty()) {
            resultView.text = "૦"
        } else if (!justEvaluated) {
            val preview = try { format(Parser(expression).parse()) } catch (_: Exception) { "" }
            resultView.text = if (preview.isEmpty()) " " else toGujarati(preview)
        } else {
            resultView.text = toGujarati(expression)
        }
    }

    private fun toGujarati(value: String): String = buildString {
        value.forEach { ch ->
            append(if (ch in westernDigits) gujaratiDigits[westernDigits.indexOf(ch)] else ch)
        }
    }

    private fun format(value: Double): String {
        if (!value.isFinite()) throw IllegalArgumentException("Not finite")
        val clean = if (kotlin.math.abs(value) < 1e-10) 0.0 else value
        val df = DecimalFormat("0.##########", DecimalFormatSymbols(Locale.US))
        return df.format(clean)
    }

    private fun rounded(color: Int, radius: Int): GradientDrawable =
        GradientDrawable().apply {
            setColor(color)
            cornerRadius = radius.toFloat()
        }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private class Parser(private val input: String) {
        private var pos = 0
        fun parse(): Double {
            if (input.isBlank()) throw IllegalArgumentException()
            val result = expression()
            if (pos != input.length) throw IllegalArgumentException()
            return result
        }
        private fun expression(): Double {
            var value = term()
            while (pos < input.length && (input[pos] == '+' || input[pos] == '-')) {
                val op = input[pos++]
                val rhs = term()
                value = if (op == '+') value + rhs else value - rhs
            }
            return value
        }
        private fun term(): Double {
            var value = factor()
            while (pos < input.length && (input[pos] == '*' || input[pos] == '/' || input[pos] == '%')) {
                val op = input[pos++]
                val rhs = factor()
                value = when (op) {
                    '*' -> value * rhs
                    '/' -> value / rhs
                    else -> value % rhs
                }
                if (!value.isFinite()) throw IllegalArgumentException()
            }
            return value
        }
        private fun factor(): Double {
            if (pos < input.length && (input[pos] == '+' || input[pos] == '-')) {
                val sign = input[pos++]
                val v = factor()
                return if (sign == '-') -v else v
            }
            val start = pos
            var dots = 0
            while (pos < input.length && (input[pos].isDigit() || input[pos] == '.')) {
                if (input[pos] == '.') dots++
                if (dots > 1) throw IllegalArgumentException()
                pos++
            }
            if (start == pos) throw IllegalArgumentException()
            return input.substring(start, pos).toDouble()
        }
    }
}
