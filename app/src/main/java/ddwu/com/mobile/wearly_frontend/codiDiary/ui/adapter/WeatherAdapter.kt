package ddwu.com.mobile.wearly_frontend.codiDiary.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ddwu.com.mobile.wearly_frontend.codiDiary.data.WeaklyWeatherData // 💡 데이터 클래스 임포트
import ddwu.com.mobile.wearly_frontend.databinding.ItemCalendarWeatherBinding

class WeatherAdapter : RecyclerView.Adapter<WeatherAdapter.WeeklyViewHolder>() {

    private var items = listOf<WeaklyWeatherData>()

    fun submitList(list: List<WeaklyWeatherData>) {
        items = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeeklyViewHolder {
        val binding = ItemCalendarWeatherBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return WeeklyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeeklyViewHolder, position: Int) {
        val item = items[position]

        val iconRes = when (item.weatherIcon) {
            0 -> ddwu.com.mobile.wearly_frontend.R.drawable.ic_weather_sunny
            1 -> ddwu.com.mobile.wearly_frontend.R.drawable.ic_weather_cloudy
            2 -> ddwu.com.mobile.wearly_frontend.R.drawable.ic_weather_rainy
            3 -> ddwu.com.mobile.wearly_frontend.R.drawable.ic_weather_snowy
            else -> ddwu.com.mobile.wearly_frontend.R.drawable.ic_weather_sunny
        }

        holder.binding.itemCalendarWeatherDateTv.text = item.date
        holder.binding.itemCalendarWeatherIv.setImageResource(iconRes)
        holder.binding.itemCalendarWeatherTempTv.text = item.temperature
    }

    override fun getItemCount() = items.size

    class WeeklyViewHolder(val binding: ItemCalendarWeatherBinding) :
        RecyclerView.ViewHolder(binding.root)
}