package com.example.project1

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar
import java.util.Date

class CalendarAdapter(private val dayList: ArrayList<Date>) :
    RecyclerView.Adapter<CalendarAdapter.ItemViewHolder>() {

    private var selectedDate: Date? = null

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayText: TextView = itemView.findViewById(R.id.dayText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.calendar_item, parent, false)

        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val monthDate = dayList[holder.adapterPosition]
        val dateCalendar = Calendar.getInstance().apply {
            time = monthDate
        }
        val dayNo = dateCalendar.get(Calendar.DAY_OF_MONTH)

        holder.dayText.text = dayNo.toString()

        val todayCalendar = Calendar.getInstance()

        if (dateCalendar.time == selectedDate) {
            // Highlight the selected date
            holder.dayText.setTextColor(Color.WHITE)
            holder.itemView.setBackgroundColor(Color.GREEN)
        } else if (
            dateCalendar.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR) &&
            dateCalendar.get(Calendar.MONTH) == todayCalendar.get(Calendar.MONTH) &&
            dateCalendar.get(Calendar.DAY_OF_MONTH) == todayCalendar.get(Calendar.DAY_OF_MONTH)
        ) {
            // Highlight today's date
            holder.dayText.setTextColor(Color.BLACK)
            holder.itemView.setBackgroundColor(Color.LTGRAY)
        } else {
            // Default styling for other dates
            holder.dayText.setTextColor(
                if ((position + 1) % 7 == 0) Color.BLUE
                else if (position == 0 || position % 7 == 0) Color.RED
                else Color.parseColor("#B4B4B4")
            )
            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
        }

        holder.itemView.setOnClickListener {
            selectedDate = dateCalendar.time
            notifyDataSetChanged()

            val iYear = dateCalendar.get(Calendar.YEAR)
            val iMonth = dateCalendar.get(Calendar.MONTH) + 1
            val iDay = dateCalendar.get(Calendar.DAY_OF_MONTH)

            val yearMonDay = "$iYear 년 $iMonth 월 $iDay 일"
            Toast.makeText(holder.itemView.context, yearMonDay, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return dayList.size
    }
}