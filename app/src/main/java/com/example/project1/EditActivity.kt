package com.example.project1

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.project1.databinding.ActivityEditBinding
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class EditActivity : AppCompatActivity() {

    private lateinit var binding : ActivityEditBinding
    private lateinit var timePicker: TimePicker
    var selectedTime = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = intent
        val selectDate = intent.getStringExtra("selectDate")
        var name = intent.getStringExtra("username")
        binding.dateText.text = selectDate

        //시간 설정
        timePicker = binding.timePicker

        // Set a 24-hour time format
        timePicker.is24HourView.and(true)

        // Set a listener to handle the time selection
        timePicker.setOnTimeChangedListener { view, hourOfDay, minute ->
            // Handle the selected time
            selectedTime = "$hourOfDay:$minute"
        }

        binding.addBtn.setOnClickListener {
            val title = binding.titleText.text.toString()
            val time = selectedTime
            val place = binding.placeText.text.toString()
            val memo = binding.memoText.text.toString()
            val username =  name
            val date =  selectDate
            if (title.isNotEmpty()) {
                val scheduleTask = scheduleAsyncTask(object : scheduleAsyncTask.scheduleCallback {
                    override fun onSignUpResult(result: String) {
                        // Handle the sign-up result
                        Toast.makeText(this@EditActivity, "등록 되었습니다", Toast.LENGTH_SHORT).show()
                        if (result == "successfully") {
                            val intent = Intent(this@EditActivity, HomeActivity::class.java)
                            intent.putExtra("username",username)
                            startActivity(intent)
                            finish() // Optional: Close the current activity if you don't want to go back to it
                        }
                    }
                })

                scheduleTask.execute(title, time, place, memo, username, date)
            } else {
                Toast.makeText(this@EditActivity, "제목을 입력해 주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private class scheduleAsyncTask(private val callback: scheduleCallback) : AsyncTask<String, Void, String>() {

        interface scheduleCallback {
            fun onSignUpResult(result: String)
        }

        override fun doInBackground(vararg params: String?): String {
            val url = URL("http://172.30.1.2:3000/schedule")
            val title = params[0]
            val time = params[1]
            val place = params[2]
            val memo = params[3]
            val username = params[4]
            val date = params[5]
            try {
                val urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.requestMethod = "POST"
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
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                return "successfully"
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
            callback.onSignUpResult(result)

        }
    }
}