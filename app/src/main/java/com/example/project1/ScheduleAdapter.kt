package com.example.project1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ScheduleAdapter(private val schedules: List<Schedule>) :
    RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    // Keep track of expanded state for each item
    private val expandedItems = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val schedule = schedules[position]

        // Set click listener to handle item expansion/collapse
        holder.itemView.setOnClickListener {
            if (expandedItems.contains(position)) {
                expandedItems.remove(position)
            } else {
                expandedItems.add(position)
            }
            notifyItemChanged(position)
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
        private val placeTextView: TextView = itemView.findViewById(R.id.placeTextView)
        private val memoTextView: TextView = itemView.findViewById(R.id.memoTextView)

        fun bind(schedule: Schedule, isExpanded: Boolean) {
            titleTextView.text = schedule.title
            timeTextView.text = "Time: ${schedule.time}"
            placeTextView.text = "Place: ${schedule.place}"
            memoTextView.text = "Memo: ${schedule.memo}"

            // Set visibility based on expanded state
            memoTextView.visibility = if (isExpanded) View.VISIBLE else View.GONE
            // You can handle other views visibility here as needed
        }
    }
}