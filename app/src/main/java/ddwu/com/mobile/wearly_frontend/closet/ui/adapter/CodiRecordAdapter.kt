package ddwu.com.mobile.wearly_frontend.closet.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.data.CodiRecord

//코디기록 연결
class CodiRecordAdapter(private val items: List<CodiRecord>) :
    RecyclerView.Adapter<CodiRecordAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateTv: TextView = view.findViewById(R.id.item_date_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_codi_record, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dateTv.text = items[position].date

        //Glide로 코디 이미지 표현

    }

    override fun getItemCount() = items.size
}