package com.example.stockscreener

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.Switch
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
    private lateinit var searchCompanyName: SearchView
    private lateinit var sharedPreferences: android.content.SharedPreferences
    private lateinit var switchFavorite: Switch
    private lateinit var noFavoriteText: TextView
    private lateinit var noDataText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize toggle switch
        switchFavorite = findViewById(R.id.switchFavorites)

        // Initialize textview for no favorite
        noFavoriteText = findViewById(R.id.noFavoriteText)

        // Initialize text for no data found
        noDataText = findViewById(R.id.noDataText)

        // Initialize share preference
        sharedPreferences = getSharedPreferences("StockPrefs", Context.MODE_PRIVATE)

        // Initialize view
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        searchCompanyName = findViewById(R.id.searchCompanyName)

        // Load data dari JSON
        stockList = loadStockDataFromJson()

        // Set adapter
        stockAdapter = StockAdapter(
            context = this,
            stockList = this.stockList,
            onItemClick = { stock ->
                showPopup(stock)
            },
            onFavoriteChanged = {
                filterAndDisplayList()
            }
        )

        recyclerView.adapter = stockAdapter

        searchCompanyName.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterAndDisplayList()
                return true
            }
        })

        // Listener for the favorite switch
        switchFavorite.setOnCheckedChangeListener { _, _ ->
            filterAndDisplayList()
        }

        // Initial list display
        filterAndDisplayList()
    }

    private fun filterAndDisplayList() {
        val query = searchCompanyName.query.toString().lowercase().trim()
        val isFavoritesOnly = switchFavorite.isChecked

        val favoriteStocks = stockList.filter { it.isFavorite }
        val baseList = if (isFavoritesOnly) favoriteStocks else stockList

        val filteredList = if (query.isEmpty()) {
            baseList
        } else {
            baseList.filter { it.name.lowercase().contains(query) }
        }

        stockAdapter.updateList(filteredList)

        when {
            isFavoritesOnly && favoriteStocks.isEmpty() -> {
                recyclerView.visibility = View.GONE
                noDataText.visibility = View.GONE
                noFavoriteText.visibility = View.VISIBLE
            }
            filteredList.isEmpty() -> {
                recyclerView.visibility = View.GONE
                noFavoriteText.visibility = View.GONE
                noDataText.visibility = View.VISIBLE
            }
            else -> {
                recyclerView.visibility = View.VISIBLE
                noDataText.visibility = View.GONE
                noFavoriteText.visibility = View.GONE
            }
        }
    }

    private fun loadStockDataFromJson(): List<Stock> {
        val jsonStr = assets.open("stock.json").bufferedReader().use { it.readText() }
        val gson = Gson()
        val type = object : TypeToken<List<Stock>>() {}.type
        val stocks: List<Stock> = gson.fromJson(jsonStr, type)
        val prefs = getSharedPreferences("StockPrefs", Context.MODE_PRIVATE)
        stocks.forEach { it.isFavorite = prefs.getBoolean("favorite_${it.id}", false) }
        return stocks
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

    // function to change background color base on price change
    private fun backgroundColorChange(priceChange: Double): Int {
        if (priceChange > 0) {
            return Color.parseColor("#A5D6A7")
        } else if (priceChange < 0) {
            return Color.parseColor("#EF9A9A")
        } else {
            return Color.parseColor("#F5F5F5")
        }
    }


}