package com.example.project1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ScheduleAdapter(private val schedules: List<Schedule>) :
    RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val schedule = schedules[position]
        holder.bind(schedule)
    }

    override fun getItemCount(): Int {
        return schedules.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        private val placeTextView: TextView = itemView.findViewById(R.id.placeTextView)
        private val memoTextView: TextView = itemView.findViewById(R.id.memoTextView)

        fun bind(schedule: Schedule) {
            titleTextView.text = schedule.title
            timeTextView.text = "Time: ${schedule.time}"
            placeTextView.text = "Place: ${schedule.place}"
            memoTextView.text = "Memo: ${schedule.memo}"
            // You can bind other data as needed
        }
    }
}