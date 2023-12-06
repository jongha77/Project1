package com.example.project1

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView

class ScheduleCalenderAdapter(private var schedules: List<Schedule>, var username :String,var formattedDate :String) :
    RecyclerView.Adapter<ScheduleCalenderAdapter.ViewHolder>() {

    // Keep track of expanded state for each item
    private val expandedItems = mutableSetOf<Int>()

    fun updateData(newList: List<Schedule>) {
        schedules = newList
        notifyDataSetChanged()
    }

    fun clearSelectedDate() {
        schedules = emptyList() // 값 초기화
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_schedule, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val schedule = schedules[position]

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val nextIntent = Intent(context, UpDateActivity::class.java)
            nextIntent.putExtra("username", username)
            nextIntent.putExtra("selectDate", formattedDate)
            nextIntent.putExtra("id_schedule", schedule.id_schedule)
            nextIntent.putExtra("title", schedule.title)
            nextIntent.putExtra("time", schedule.time)
            nextIntent.putExtra("place", schedule.place)
            nextIntent.putExtra("memo", schedule.memo)
            context.startActivity(nextIntent)
            // Note: finish() is not applicable here, as it's typically used in activities
        }

        // Bind data and update visibility based on expanded state
        holder.bind(schedule, expandedItems.contains(position))
    }

    override fun getItemCount(): Int {
        return schedules.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)

        fun bind(schedule: Schedule, isExpanded: Boolean) {
            titleTextView.text = schedule.title
            timeTextView.text = "Time: ${schedule.time}"
        }
    }
}