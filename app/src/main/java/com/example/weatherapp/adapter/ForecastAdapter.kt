package com.example.weatherapp.adapter

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapp.databinding.ForecastViewholderBinding
import com.example.weatherapp.model.ForecastResponseApi
import java.util.Calendar
import java.util.TimeZone

class ForecastAdapter : RecyclerView.Adapter<ForecastAdapter.ViewHolder>() {
    private lateinit var binding: ForecastViewholderBinding
    var timeZone = java.util.TimeZone.getDefault()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ForecastViewholderBinding.inflate(inflater, parent, false)
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ForecastAdapter.ViewHolder, position: Int) {
//        val customTimeZone = TimeZone.getTimeZone("GMT+09:30")

        val date =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(differ.currentList[position].dtTxt.toString())
        //  val date = Date() // Convert milliseconds to Date object
        // val calendar = Calendar.getInstance(TimeZone.getTimeZone(timezone)) // Create calendar with specified timezone
        //calendar.time = date // Set calendar time to the Date object

        // Set hour, minute, and second to 0 (to get the date at 00:00 on that day)
        //        calendar.set(Calendar.HOUR_OF_DAY, 0)
        /* calendar.set(Calendar.MINUTE, 0)
         calendar.set(Calendar.SECOND, 0)
         calendar.set(Calendar.MILLISECOND, 0)*/

        // Format the date with desired format
        //  val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        binding = ForecastViewholderBinding.bind(holder.itemView)
        val calendar = Calendar.getInstance(timeZone)
        calendar.time = date


        val dayOfWeekName = when (calendar.get(Calendar.DAY_OF_WEEK)) {
            1 -> "Sun"
            2 -> "Mon"
            3 -> "Tue"
            4 -> "Wed"
            5 -> "Thu"
            6 -> "Fri"
            7 -> "Sat"
            else -> "-"
        }

        binding.nameDayTxt.text = dayOfWeekName
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val amPm = if (hour < 12) "am" else "pm"
        val hour12 = calendar.get(Calendar.HOUR)
        binding.weaTxt.text = when (differ.currentList[position].weather?.get(0)?.icon.toString()) {
            "01d", "01n" -> "sun"
            "02d", "02n" -> "cloudy_sunny"
            "03d", "03n" -> "cloudy_sunny"
            "04d", "04n" -> "cloudy"
            "09d", "09n" -> "rainy"
            "10d", "10n" -> "rainy"
            "11d", "11n" -> "storm"
            "13d", "13n" -> "snow"
            "50d", "50n" -> "wind"
            else -> "sunny"
        }
        binding.hourTxt.text = hour12.toString() + amPm
        binding.tempTxt.text =
            differ.currentList[position].main?.temp?.let { Math.round(it).toString() + "°" }
        val icon = when (differ.currentList[position].weather?.get(0)?.icon.toString()) {
            "01d", "01n" -> "sun"
            "02d", "02n" -> "cloudy_sunny"
            "03d", "03n" -> "cloudy_sunny"
            "04d", "04n" -> "cloudy"
            "09d", "09n" -> "rainy"
            "10d", "10n" -> "rainy"
            "11d", "11n" -> "storm"
            "13d", "13n" -> "snow"
            "50d", "50n" -> "wind"
            else -> "sunny"
        }
        val drawableResourceId: Int = binding.root.resources.getIdentifier(
            icon,
            "drawable", binding.root.context.packageName
        )
        Glide.with(binding.root.context)
            .load(drawableResourceId)
            .into(binding.pic)


    }

    fun setTimeZone(timeZone: String){
        this.timeZone = TimeZone.getTimeZone("GMT$timeZone")
        notifyDataSetChanged()
    }
    inner class ViewHolder : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount() = differ.currentList.size


    private val differCallback = object : DiffUtil.ItemCallback<ForecastResponseApi.data>() {
        override fun areItemsTheSame(
            oldItem: ForecastResponseApi.data,
            newItem: ForecastResponseApi.data
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: ForecastResponseApi.data,
            newItem: ForecastResponseApi.data
        ): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, differCallback)
}