package cloud.khuong.androidhomeskin

import android.app.Activity
import android.content.Intent
import android.graphics.Outline
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Calendar
import kotlin.concurrent.thread

/**
 * Android Home Skin — a port of the LG webOS / Samsung Tizen home skin.
 * Full-screen wallpaper, big clock + weather top-left, one bottom row of
 * square app tiles; a tile's name shows only while it is focused.
 * Can be set as the TV's default home (launcher) app.
 */
class MainActivity : Activity() {

    // ------------------------------------------------------------------ config
    // Front of the row, matched against app labels (lowercase substring).
    private val orderLabels = listOf("youtube", "apple music", "netflix", "apple tv")

    // Weather (Open-Meteo, free, no key). Default: Ho Chi Minh City.
    private val weatherCity = "Ho Chi Minh City"
    private val latitude = 10.8231
    private val longitude = 106.6297

    // ------------------------------------------------------------------ state
    private data class AppEntry(
        val label: String,
        val packageName: String,
        val icon: Drawable,
        val launchIntent: Intent
    )

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var clockView: TextView
    private lateinit var ampmView: TextView
    private lateinit var dateView: TextView
    private lateinit var weatherView: TextView
    private lateinit var rowScroll: HorizontalScrollView
    private lateinit var row: LinearLayout

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    // ------------------------------------------------------------------ lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        clockView = findViewById(R.id.clock)
        ampmView = findViewById(R.id.ampm)
        dateView = findViewById(R.id.dateLine)
        weatherView = findViewById(R.id.weatherLine)
        rowScroll = findViewById(R.id.rowScroll)
        row = findViewById(R.id.row)
        tickClock()
        loadApps()
        fetchWeather()
    }

    override fun onResume() {
        super.onResume()
        tickClock()
        loadApps()
    }

    // ------------------------------------------------------------------ clock
    private val days = arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    private val months = arrayOf(
        "January", "February", "March", "April", "May", "June", "July",
        "August", "September", "October", "November", "December"
    )

    private val clockTask = object : Runnable {
        override fun run() {
            tickClock()
            handler.postDelayed(this, 1000)
        }
    }

    private fun tickClock() {
        val now = Calendar.getInstance()
        val h = now.get(Calendar.HOUR_OF_DAY)
        val mm = String.format("%02d", now.get(Calendar.MINUTE))
        val h12 = if (h % 12 == 0) 12 else h % 12
        clockView.text = "$h12:$mm"
        ampmView.text = if (h >= 12) "PM" else "AM"
        dateView.text =
            "${days[now.get(Calendar.DAY_OF_WEEK) - 1]}, ${now.get(Calendar.DAY_OF_MONTH)} " +
                "${months[now.get(Calendar.MONTH)]} ${now.get(Calendar.YEAR)}"
        handler.removeCallbacks(clockTask)
        handler.postDelayed(clockTask, 1000)
    }

    // ------------------------------------------------------------------ weather
    private val wmo = mapOf(
        0 to "Clear", 1 to "Mainly clear", 2 to "Partly cloudy", 3 to "Overcast",
        45 to "Fog", 48 to "Fog", 51 to "Drizzle", 53 to "Drizzle", 55 to "Drizzle",
        61 to "Rain", 63 to "Rain", 65 to "Heavy rain", 71 to "Snow", 73 to "Snow",
        75 to "Snow", 80 to "Showers", 81 to "Showers", 82 to "Showers",
        95 to "Thunderstorm", 96 to "Thunderstorm", 99 to "Thunderstorm"
    )

    private fun fetchWeather() {
        thread {
            try {
                val url = URL(
                    "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude" +
                        "&current=temperature_2m,weather_code" +
                        "&daily=temperature_2m_max,temperature_2m_min&timezone=auto&forecast_days=1"
                )
                val conn = url.openConnection() as HttpURLConnection
                conn.connectTimeout = 10000
                conn.readTimeout = 10000
                val text = conn.inputStream.bufferedReader().readText()
                conn.disconnect()
                val j = JSONObject(text)
                val cur = j.getJSONObject("current")
                val t = Math.round(cur.getDouble("temperature_2m")).toInt()
                val label = wmo[cur.getInt("weather_code")] ?: ""
                val daily = j.getJSONObject("daily")
                val hi = Math.round(daily.getJSONArray("temperature_2m_max").getDouble(0)).toInt()
                val lo = Math.round(daily.getJSONArray("temperature_2m_min").getDouble(0)).toInt()
                runOnUiThread {
                    weatherView.text = "$weatherCity · $t° $label · H $hi° L $lo°"
                }
            } catch (e: Exception) {
                // Keep the previous line; retried on the next cycle.
            }
        }
        handler.postDelayed({ fetchWeather() }, 30L * 60L * 1000L)
    }

    // ------------------------------------------------------------------ apps
    private fun loadApps() {
        val pm = packageManager
        val byPackage = LinkedHashMap<String, AppEntry>()
        val categories = listOf(Intent.CATEGORY_LEANBACK_LAUNCHER, Intent.CATEGORY_LAUNCHER)
        for (category in categories) {
            val query = Intent(Intent.ACTION_MAIN).addCategory(category)
            for (ri in pm.queryIntentActivities(query, 0)) {
                val pkg = ri.activityInfo.packageName
                if (pkg == packageName || byPackage.containsKey(pkg)) continue
                val launch = pm.getLeanbackLaunchIntentForPackage(pkg)
                    ?: pm.getLaunchIntentForPackage(pkg) ?: continue
                val icon = try {
                    ri.loadIcon(pm)
                } catch (e: Exception) {
                    pm.getDefaultActivityIcon()
                }
                byPackage[pkg] = AppEntry(ri.loadLabel(pm).toString(), pkg, icon, launch)
            }
        }

        val rest = byPackage.values.toMutableList()
        val front = ArrayList<AppEntry>()
        for (want in orderLabels) {
            val hit = rest.firstOrNull { it.label.lowercase().contains(want) }
            if (hit != null) {
                front.add(hit)
                rest.remove(hit)
            }
        }
        rest.sortBy { it.label.lowercase() }
        buildTiles(front + rest)
    }

    private fun buildTiles(apps: List<AppEntry>) {
        val keepFocus = (row.focusedChild as? View)?.tag as? String
        row.removeAllViews()
        var firstTile: View? = null
        apps.forEachIndexed { index, app ->
            val tile = buildTile(app, index == apps.lastIndex)
            row.addView(tile)
            if (firstTile == null) firstTile = tile
            if (app.packageName == keepFocus) firstTile = tile
        }
        val target = firstTile
        row.post { target?.requestFocus() }
    }

    private fun buildTile(app: AppEntry, isLast: Boolean): View {
        val tile = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            isFocusable = true
            isClickable = true
            tag = app.packageName
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { if (!isLast) marginEnd = dp(60) }
        }

        val radius = dp(36).toFloat()
        val card = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(dp(168), dp(168))
            background = GradientDrawable().apply {
                cornerRadius = radius
                setColor(0xFF2C2C2E.toInt())
            }
            clipToOutline = true
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    outline.setRoundRect(0, 0, view.width, view.height, radius)
                }
            }
        }
        val img = ImageView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
            setImageDrawable(app.icon)
        }
        card.addView(img)
        tile.addView(card)

        val name = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(14) }
            text = app.label
            textSize = 20f
            setTextColor(0xFFFFFFFF.toInt())
            maxLines = 1
            visibility = View.INVISIBLE
        }
        tile.addView(name)

        tile.setOnFocusChangeListener { _, hasFocus ->
            card.animate().scaleX(if (hasFocus) 1.16f else 1f)
                .scaleY(if (hasFocus) 1.16f else 1f).setDuration(150).start()
            card.elevation = if (hasFocus) dp(12).toFloat() else 0f
            name.visibility = if (hasFocus) View.VISIBLE else View.INVISIBLE
            if (hasFocus) {
                row.post {
                    val target = (tile.left - dp(240)).coerceAtLeast(0)
                    rowScroll.smoothScrollTo(target, 0)
                }
            }
        }
        tile.setOnClickListener {
            try {
                startActivity(app.launchIntent)
            } catch (e: Exception) {
                Toast.makeText(this, "Could not open ${app.label}", Toast.LENGTH_SHORT).show()
            }
        }
        return tile
    }
}
