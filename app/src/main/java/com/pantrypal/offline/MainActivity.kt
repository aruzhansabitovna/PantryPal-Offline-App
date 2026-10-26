package com.student.pantrypal

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.*
import org.json.JSONArray
import java.util.Locale

class MainActivity : Activity() {
    private val green = Color.rgb(47, 107, 79)
    private val cream = Color.rgb(247, 244, 237)
    private val dark = Color.rgb(34, 49, 42)
    private lateinit var list: LinearLayout
    private lateinit var count: TextView
    private lateinit var input: EditText
    private lateinit var recipes: JSONArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recipes = JSONArray(assets.open("recipes.json").bufferedReader().use { it.readText() })
        val scroll = ScrollView(this)
        scroll.setBackgroundColor(cream)
        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(22), dp(18), dp(30))
        }
        scroll.addView(page)
        page.addView(text("🍳 PantryPal", 30, green, true))
        page.addView(text("Cook with what you already have.", 18, dark, true))
        page.addView(text("50 recipes • Works offline", 14, Color.DKGRAY))
        page.addView(text("What ingredients do you have?", 19, dark, true).apply { setPadding(0,dp(22),0,dp(5)) })
        input = EditText(this).apply {
            hint = "e.g. chicken, tomato, rice"
            setSingleLine(false)
            minLines = 2
            setTextColor(dark)
            setBackgroundColor(Color.WHITE)
            setPadding(dp(12),dp(10),dp(12),dp(10))
        }
        page.addView(input, LinearLayout.LayoutParams(-1, -2))
        val actions = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val search = Button(this).apply { text = "Find recipes"; setOnClickListener { render() } }
        val clear = Button(this).apply { text = "Show all"; setOnClickListener { input.setText(""); render() } }
        actions.addView(search, LinearLayout.LayoutParams(0,-2,1f))
        actions.addView(clear, LinearLayout.LayoutParams(0,-2,1f))
        page.addView(actions)
        count = text("", 17, green, true).apply { setPadding(0,dp(16),0,dp(8)) }
        page.addView(count)
        list = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        page.addView(list)
        setContentView(scroll)
        render()
    }

    private fun render() {
        val user = input.text.toString().split(",").map { it.trim().lowercase(Locale.ROOT) }.filter { it.isNotEmpty() }
        data class Match(val index: Int, val score: Int, val matches: Int)
        val matches = (0 until recipes.length()).map { i ->
            val r = recipes.getJSONObject(i)
            val a = r.getJSONArray("ingredients")
            val ingredients = (0 until a.length()).map { a.getString(it).lowercase(Locale.ROOT) }
            val matching = ingredients.count { ingredient -> user.any { u -> ingredient.contains(u) || u.contains(ingredient) } }
            val enteredMatches = user.count { u -> ingredients.any { ingredient -> ingredient.contains(u) || u.contains(ingredient) } }
            Match(i, if (ingredients.isEmpty()) 0 else matching * 100 / ingredients.size, enteredMatches)
        }.filter { user.isEmpty() || it.matches > 0 }
            .sortedWith(compareByDescending<Match> { it.score }.thenByDescending { it.matches })
        list.removeAllViews()
        count.text = if (user.isEmpty()) "All ${matches.size} recipes" else "${matches.size} suggested recipes"
        if (matches.isEmpty()) {
            list.addView(text("No matches. Try egg, tomato, chicken or rice.", 16, dark))
        }
        matches.forEach { match ->
            val recipe = recipes.getJSONObject(match.index)
            val name = recipe.getString("name")
            val ingredients = recipe.getJSONArray("ingredients")
            val ingredientText = (0 until ingredients.length()).joinToString(", ") { ingredients.getString(it) }
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundColor(Color.WHITE)
                setPadding(dp(16),dp(14),dp(16),dp(14))
            }
            card.addView(text(name, 19, green, true))
            card.addView(text("${recipe.getString("category")}  •  ${recipe.getInt("time")} min" + if (user.isNotEmpty()) "  •  ${match.score}% match" else "", 13, Color.DKGRAY))
            card.addView(text(ingredientText, 14, dark))
            card.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle(name)
                    .setMessage("Category: ${recipe.getString("category")}\nTime: ${recipe.getInt("time")} minutes\n\nIngredients:\n$ingredientText\n\nPreparation instructions were not included in the original Task 3 dataset.")
                    .setPositiveButton("Close", null)
                    .show()
            }
            list.addView(card, LinearLayout.LayoutParams(-1,-2).apply { bottomMargin = dp(10) })
        }
    }

    private fun text(s: String, size: Int, color: Int, bold: Boolean = false): TextView = TextView(this).apply {
        text = s
        textSize = size.toFloat()
        setTextColor(color)
        if (bold) setTypeface(null, Typeface.BOLD)
        setPadding(0, dp(4), 0, dp(4))
    }
    private fun dp(n: Int): Int = (n * resources.displayMetrics.density).toInt()
}
