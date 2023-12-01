package com.example.project1

import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project1.databinding.FragmentHomeBinding
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class HomeFragment : Fragment() {
    private var mBinding: FragmentHomeBinding? = null
    private val binding get() = mBinding!!
    private lateinit var viewModel: ScheduleViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(ScheduleViewModel::class.java)

        mBinding = FragmentHomeBinding.inflate(inflater, container, false)

        val username = arguments?.getString("username")
        val date = getCurrentDateUsingLocalDate()

        val fetchSchedulesTask = FetchSchedulesTask(object : FetchSchedulesTask.OnTaskCompleted {
            override fun onTaskCompleted(result: List<Schedule>) {
                if (result.isNotEmpty()) {
                    viewModel.setSchedules(result)
                    // Move RecyclerView initialization and adapter setting here
                    initRecyclerView(result)
                }
            }
        })
        fetchSchedulesTask.execute(username, date)

        // Observe changes in the ViewModel and update the UI accordingly
        viewModel.schedules.observe(viewLifecycleOwner, Observer {
            onDataLoaded(it)
        })

        return binding.root
    }

    private fun onDataLoaded(schedules: List<Schedule>) {
        // Update your UI or perform any additional operations based on the loaded data
    }

    private fun initRecyclerView(schedules: List<Schedule>) {
        // Initialize RecyclerView and adapter
        val recyclerView: RecyclerView = binding.recyclerView
        val adapter = ScheduleAdapter(schedules)

        // Set the layout manager and adapter to the RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }
}

class FetchSchedulesTask(private val listener: OnTaskCompleted) :
    AsyncTask<String, Void, String>() {

    interface OnTaskCompleted {
        fun onTaskCompleted(result: List<Schedule>)
    }

    override fun doInBackground(vararg params: String): String {
        val username = params[0]
        val date = params[1]

        // URL of your server endpoint with the username parameter
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
            // Try to parse the response as a JSON array
            val schedulesListType = object : TypeToken<List<Schedule>>() {}.type
            val schedulesList = Gson().fromJson<List<Schedule>>(result, schedulesListType)

            listener.onTaskCompleted(schedulesList)
        } catch (e: JsonSyntaxException) {
            // If parsing as a JSON array fails, handle the error
            Log.e("FetchSchedulesTask", "Error parsing JSON: $result")
            listener.onTaskCompleted(emptyList())
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getCurrentDateUsingLocalDate(): String {
    val currentDate = LocalDate.now()
    val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
    return currentDate.format(dateFormat)
}