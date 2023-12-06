package com.example.project1

import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.project1.databinding.FragmentCalenderBinding
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class CalenderFragment : Fragment() {
    private var mBinding: FragmentCalenderBinding? = null
    private val binding get() = mBinding!!
    private lateinit var viewModel: ScheduleViewModel
    private lateinit var scheduleAdapter: ScheduleCalenderAdapter

    private lateinit var username: String
    private lateinit var dayList: ArrayList<Date>
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(ScheduleViewModel::class.java)

        mBinding = FragmentCalenderBinding.inflate(inflater, container, false)
        username = arguments?.getString("username") ?: ""
        dayList = dayInMonthArray()
        val selectedDate = CalendarUtil.selectedDate.time
        val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate)
        // Initialize the adapter
        scheduleAdapter = ScheduleCalenderAdapter(emptyList(),username,formattedDate)
        binding.scheduleView.adapter = scheduleAdapter

        loadData(username, getCurrentFormattedDate())

        viewModel.schedules.observe(viewLifecycleOwner, Observer {
            onDataLoaded(it)
        })

        setMonthView()

        binding.preBtn.setOnClickListener {
            CalendarUtil.selectedDate.add(Calendar.MONTH, -1)
            setMonthView()
        }

        binding.nextBtn.setOnClickListener {
            CalendarUtil.selectedDate.add(Calendar.MONTH, 1)
            setMonthView()
        }

        binding.editBtn.setOnClickListener {
            val selectedDate = CalendarUtil.selectedDate.time
            val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate)
            activity?.let {
                val intent = Intent(context, EditActivity::class.java)
                intent.putExtra("username", username)
                intent.putExtra("selectDate", formattedDate)
                startActivity(intent)
            }
        }
        return binding.root
    }

    private fun getCurrentFormattedDate(): String {
        val selectedDate = CalendarUtil.selectedDate.time
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate)
    }

    private fun onDataLoaded(schedules: List<Schedule>) {
        scheduleAdapter.updateData(schedules)
    }

    private fun setMonthView() {
        binding.monthYearText.text = monthYearFromDate(CalendarUtil.selectedDate)
        val adapter = CalendarAdapter(dayList)
        adapter.setOnDateSelectedListener { selectedDate ->
            // Load data from the server based on the selected date
            val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate)
            scheduleAdapter.clearSelectedDate()
            loadData(username, formattedDate)
        }
        binding.recyclerView.layoutManager = GridLayoutManager(context, 7)
        binding.recyclerView.adapter = adapter
    }

    private fun monthYearFromDate(calendar: Calendar): String {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        return "$month 월 $year"
    }

    private fun dayInMonthArray(): ArrayList<Date> {
        val dayList = ArrayList<Date>()
        val monthCalendar = CalendarUtil.selectedDate.clone() as Calendar
        monthCalendar[Calendar.DAY_OF_MONTH] = 1
        val firstDayOfMonth = monthCalendar[Calendar.DAY_OF_WEEK] - 1
        monthCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth)

        while (dayList.size < 42) {
            dayList.add(monthCalendar.time)
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return dayList
    }

    private fun loadData(username: String?, formattedDate: String?) {
        val calenderSchedulesTask = CalenderSchedulesTask(object : CalenderSchedulesTask.OnTaskCompleted {
            override fun onTaskCompleted(result: List<Schedule>) {
                if (result.isNotEmpty()) {
                    viewModel.setSchedules(result)
                    scheduleAdapter.updateData(result)
                }
            }
        })
        calenderSchedulesTask.execute(username, formattedDate)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }
}

class CalenderSchedulesTask(private val listener: OnTaskCompleted) : AsyncTask<String, Void, String>() {

    interface OnTaskCompleted {
        fun onTaskCompleted(result: List<Schedule>)
    }

    override fun doInBackground(vararg params: String): String {
        val username = params[0]
        val date = params[1]
        val url = URL("http://172.30.1.2:3000/schedules/$username/$date")

        try {
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            val bufferedReader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = StringBuilder()

            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                response.append(line)
            }

            bufferedReader.close()
            connection.disconnect()

            return response.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            return "Error: ${e.message}"
        }
    }

    override fun onPostExecute(result: String) {
        try {
            val schedulesListType = object : TypeToken<List<Schedule>>() {}.type
            val schedulesList = Gson().fromJson<List<Schedule>>(result, schedulesListType)
            listener.onTaskCompleted(schedulesList)
        } catch (e: JsonSyntaxException) {
            Log.e("FetchSchedulesTask", "Error parsing JSON: $result")
            listener.onTaskCompleted(emptyList())
        }
    }
}