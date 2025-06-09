package com.example.stockscreener

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var stockAdapter: StockAdapter
    private lateinit var stockList: List<Stock>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize view
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Load data dari JSON
        stockList = loadStockDataFromJson()

        // Set adapter
        stockAdapter = StockAdapter(stockList) { stock ->
            showPopup(stock)
        }
        recyclerView.adapter = stockAdapter
    }

    private fun loadStockDataFromJson(): List<Stock> {
        val jsonStr = assets.open("stock.json").bufferedReader().use { it.readText() }
        val gson = Gson()
        val type = object : TypeToken<List<Stock>>() {}.type
        return gson.fromJson(jsonStr, type)
    }

    private fun showPopup(stock: Stock) {
        val view = layoutInflater.inflate(R.layout.popup_stock_detail, null)
        val companyName: TextView = view.findViewById(R.id.popupCompanyName)
        val symbol: TextView = view.findViewById(R.id.popupSymbol)
        val price: TextView = view.findViewById(R.id.popupPrice)
        val change: TextView = view.findViewById(R.id.popupChange)
        val popupBackground = view.findViewById<View>(R.id.popup_detail)
        val bgColor = backgroundColorChange(stock.stock_price.price_change)

        companyName.text = stock.name
        symbol.text = "(${stock.symbol})"
        price.text = "${stock.stock_price.current_price.currency} ${stock.stock_price.current_price.amount}"
        change.text = "${stock.stock_price.price_change} (${stock.stock_price.percentage_change}%)"

        Glide.with(this ).load(stock.logo_url).placeholder(R.drawable.ic_launcher_background).into(view.findViewById(R.id.popupLogo))

        popupBackground.setBackgroundColor(bgColor)

        AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(true)
            .show()
    }

    private fun backgroundColorChange(priceChange: Double): Int {
        if (priceChange > 0) {
            return Color.parseColor("#A5D6A7")
        } else if (priceChange < 0) {
            return Color.parseColor("#EF9A9A")
        } else {
            // Tiada perubahan, kelabu
            return Color.parseColor("#F5F5F5")
        }
    }


}