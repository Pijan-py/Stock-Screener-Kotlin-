package com.example.stockscreener

import android.content.ClipData.Item
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class StockAdapter(private val stockList: List<Stock>) : RecyclerView.Adapter<StockAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockAdapter.ViewHolder {
        val card_design = LayoutInflater.from(parent.context).inflate(R.layout.stock_card_design, parent, false)

        return ViewHolder(card_design)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = stockList[position]
        val context = holder.itemView.context
        Glide.with(context).load(item.logo_url).placeholder(R.drawable.ic_launcher_background).into(holder.logo)
        holder.companyName.text = item.name
        holder.stockPrice.text = "${item.stock_price.current_price.currency} ${item.stock_price.current_price.amount}"
    }

    override fun getItemCount(): Int {
        return stockList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val logo: ImageView = itemView.findViewById(R.id.logoCompany)
        val companyName: TextView = itemView.findViewById(R.id.companyName)
        val stockPrice: TextView = itemView.findViewById(R.id.stockPrice)
    }
}