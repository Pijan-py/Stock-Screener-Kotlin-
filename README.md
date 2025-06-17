# Stock-Screener-Kotlin-
Pijan-py/Stock-Screener-Kotlin-

Setup Instruction : 
1. Create Git Repository 
2. Create Android project -> Empty View Activity with kotlin
3. Commit project into Git Hub : Git add . -> git commit -> git push

App Structure :
app/
├── manifests/
│   └── AndroidManifest.xml

├── kotlin+java/
│   └── com.example.stockscreener/
│       ├── MainActivity.kt           # Main activity handling UI logic
│       ├── Stock.kt                  # Data model class
│       ├── StockAdapter.kt           # RecyclerView adapter
│
├── assets/
│   └── stock.json                    # stock data

├── res/
│   ├── drawable/ 
│   │   ├── empty_star_icon.png      # favorite icon if not save as favorite
│   │   ├── star_icon.png			   # favorite icon if save as favorite
│   │   ├── stock_card_border.xml    
│   │   ├── stock_background.xml     
│   │   ├── popup_background.xml     
│   │   └── rounded_searchview.xml   # change SearchView shape into rounded
│
│   ├── layout/
│   │   ├── activity_main.xml         # Main layout with RecyclerView & search
│   │   ├── stock_card_design.xml     # Layout for stock card
│   │   └── popup_stock_detail.xml    # Popup layout for stock detail


Trade-offs or future improvement :
- Integrated backend API for real time stock data
- Improve on UI design, animation
- Add live graph data for stock for more interactive 
- Using database for store current data 
- Add more filter such as price, percentage(increase or decrease)

