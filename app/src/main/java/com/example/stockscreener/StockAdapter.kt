package com.example.stockscreener

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class StockAdapter(private val context: Context,
                   private val stockList: List<Stock>,
                   private val onItemClick: (Stock) -> Unit,
                   private val onFavoriteChanged: () -> Unit
) : RecyclerView.Adapter<StockAdapter.ViewHolder>() {

    private var searchStockList: MutableList<Stock> = stockList.toMutableList()
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("StockPrefs", Context.MODE_PRIVATE)

    init {
        searchStockList.addAll(stockList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockAdapter.ViewHolder {
        val card_design = LayoutInflater.from(parent.context).inflate(R.layout.stock_card_design, parent, false)

        return ViewHolder(card_design)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item =searchStockList[position]
        val context = holder.itemView.context
        Glide.with(context).load(item.logo_url).placeholder(R.drawable.ic_launcher_background).into(holder.logo)
        holder.companyName.text = item.name
        holder.stockPrice.text = "${item.stock_price.current_price.currency} ${item.stock_price.current_price.amount}"

        if (item.isFavorite) {
            holder.favoriteIcon.setImageResource(R.drawable.star_icon) // icon berwarna
        } else {
            holder.favoriteIcon.setImageResource(R.drawable.empty_star_icon) // icon grey
        }

        holder.favoriteIcon.setOnClickListener {
            toggleFavoriteIcon(position)
        }

        holder.itemView.setOnClickListener {
            onItemClick(item);
        }
    }

    override fun getItemCount(): Int {
        return searchStockList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val logo: ImageView = itemView.findViewById(R.id.logoCompany)
        val companyName: TextView = itemView.findViewById(R.id.companyName)
        val stockPrice: TextView = itemView.findViewById(R.id.stockPrice)
        val favoriteIcon: ImageView = itemView.findViewById(R.id.favIcon)
    }

    private fun toggleFavoriteIcon(position: Int) {
        if (position < 0 || position >= searchStockList.size) return

        val stock = searchStockList[position]
        stock.isFavorite = !stock.isFavorite
        saveFavoriteStatus(stock)

        onFavoriteChanged()
    }

    private fun saveFavoriteStatus(stock: Stock) {
        with(sharedPreferences.edit()) {
            putBoolean("favorite_${stock.id}", stock.isFavorite)
            apply()
        }
    }

    private fun loadFavorites() {
        stockList.forEach { stock ->
            stock.isFavorite = sharedPreferences.getBoolean("favorite_${stock.id}", false)
        }
    }

    fun updateList(newList: List<Stock>) {
        searchStockList.clear()
        searchStockList.addAll(newList)
        notifyDataSetChanged()
    }

}