package com.example.project1

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.project1.databinding.ActivityUpDateBinding
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class UpDateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpDateBinding
    private lateinit var timePicker: TimePicker
    var selectedTime = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpDateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val selectDate = intent.getStringExtra("selectDate")
        val name = intent.getStringExtra("username")
        val title = intent.getStringExtra("title")
        val time = intent.getStringExtra("time")
        val place = intent.getStringExtra("place")
        val memo = intent.getStringExtra("memo")
        val id_schedule = intent.getIntExtra("id_schedule", 0)

        //시간 설정
        timePicker = binding.timePicker

        // Set a 24-hour time format
        timePicker.is24HourView.and(true)

        // 받아온 시간값 설정
        val timeParts = time!!.split(":")
        val hours = timeParts[0].toInt()
        val minutes = timeParts[1].toInt()
        timePicker.hour = hours
        timePicker.minute = minutes
        // Set a listener to handle the time selection
        timePicker.setOnTimeChangedListener { view, hourOfDay, minute ->
            // Handle the selected time
            selectedTime = "$hourOfDay:$minute"
        }

        binding.dateText.text = selectDate
        binding.titleText.setText(title)
        binding.placeText.setText(place)
        binding.memoText.setText(memo)

        binding.UpDateBtn.setOnClickListener {
            // Call the updateSchedule method when the update button is clicked
            updateSchedule(id_schedule, binding.titleText.text.toString(), selectedTime, binding.placeText.text.toString(), binding.memoText.text.toString(), name!!, selectDate!!)
            val intent = Intent(this@UpDateActivity, HomeActivity::class.java)
            Toast.makeText(this@UpDateActivity, "수정 완료!", Toast.LENGTH_SHORT).show()
            intent.putExtra("username", name)
            startActivity(intent)
        }

        binding.DeleteBtn.setOnClickListener {
            // Call the deleteSchedule method when the delete button is clicked
            deleteSchedule(id_schedule)
            val intent = Intent(this@UpDateActivity, HomeActivity::class.java)
            Toast.makeText(this@UpDateActivity, "삭제 완료!", Toast.LENGTH_SHORT).show()
            intent.putExtra("username", name)
            startActivity(intent)
        }
    }
    private fun updateSchedule(id_schedule: Int, title: String, time: String, place: String, memo: String, username: String, date: String) {
        // Create an instance of updateAsyncTask and execute it
        val updateTask = UpdateAsyncTask(object : UpdateAsyncTask.UpdateCallback {
            override fun onUpdateResult(result: String) {
                // Handle the result of the update operation
                // You can display a toast message or update the UI accordingly
                // For simplicity, we'll just log the result to the console
                println(result)
            }
        })

        // Execute the AsyncTask with the parameters for the update
        updateTask.execute(id_schedule.toString(), title, time, place, memo, username, date)
    }

    private fun deleteSchedule(id_schedule: Int) {
        // Create an instance of deleteAsyncTask and execute it
        val deleteTask = DeleteAsyncTask(object : DeleteAsyncTask.DeleteCallback {
            override fun onDeleteResult(result: String) {
                // Handle the result of the delete operation
                // You can display a toast message or update the UI accordingly
                // For simplicity, we'll just log the result to the console
                println(result)
            }
        })

        // Execute the AsyncTask with the parameter for the delete
        deleteTask.execute(id_schedule.toString())
    }
    private class UpdateAsyncTask(private val callback: UpdateCallback) : AsyncTask<String, Void, String>() {

        interface UpdateCallback {
            fun onUpdateResult(result: String)
        }

        override fun doInBackground(vararg params: String?): String {
            val url = URL("http://172.30.1.2:3000/schedule/${params[0]}")
            val title = params[1]
            val time = params[2]
            val place = params[3]
            val memo = params[4]
            val username = params[5]
            val date = params[6]

            try {
                val urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.requestMethod = "PUT"
                urlConnection.setRequestProperty("Content-Type", "application/json")
                urlConnection.doOutput = true

                val outputStream = DataOutputStream(urlConnection.outputStream)
                val data = "{\"title\":\"$title\",\"time\":\"$time\",\"place\":\"$place\",\"memo\":\"$memo\",\"username\":\"$username\",\"date\":\"$date\"}".toByteArray(Charsets.UTF_8)
                outputStream.write(data)
                outputStream.flush()
                outputStream.close()

                return getResponse(urlConnection)
            } catch (e: Exception) {
                return "Error: ${e.message}"
            }
        }

        private fun getResponse(connection: HttpURLConnection): String {
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return "Successfully updated"
            } else {
                val inputStreamReader = InputStreamReader(connection.errorStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                val stringBuilder = StringBuilder()
                var line: String?

                while (bufferedReader.readLine().also { line = it } != null) {
                    stringBuilder.append(line).append("\n")
                }

                return stringBuilder.toString()
            }
        }

        override fun onPostExecute(result: String) {
            callback.onUpdateResult(result)
        }
    }

    private class DeleteAsyncTask(private val callback: DeleteCallback) : AsyncTask<String, Void, String>() {

        interface DeleteCallback {
            fun onDeleteResult(result: String)
        }

        override fun doInBackground(vararg params: String?): String {
            val url = URL("http://172.30.1.2:3000/schedule/${params[0]}")

            try {
                val urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.requestMethod = "DELETE"
                urlConnection.setRequestProperty("Content-Type", "application/json")

                return getResponse(urlConnection)
            } catch (e: Exception) {
                return "Error: ${e.message}"
            }
        }

        private fun getResponse(connection: HttpURLConnection): String {
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return "Successfully deleted"
            } else {
                val inputStreamReader = InputStreamReader(connection.errorStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                val stringBuilder = StringBuilder()
                var line: String?

                while (bufferedReader.readLine().also { line = it } != null) {
                    stringBuilder.append(line).append("\n")
                }

                return stringBuilder.toString()
            }
        }

        override fun onPostExecute(result: String) {
            callback.onDeleteResult(result)
        }
    }
}