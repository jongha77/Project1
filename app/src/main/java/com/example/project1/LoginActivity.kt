package com.example.project1

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.project1.databinding.ActivityLoginBinding
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL


class LoginActivity : AppCompatActivity() {

    private var mBinding: ActivityLoginBinding? = null
    private val binding get() = mBinding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.buttonLogin.setOnClickListener {
            val username = binding.editTextUsername.text.toString()
            val password = binding.editTextPassword.text.toString()
            // Execute AsyncTask to perform the login in the background
            LoginTask().execute(username, password)
        }
    }

    // AsyncTask to perform the login in the background
    private inner class LoginTask : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String): String {
            val username = params[0]
            val password = params[1]

            val url = URL("http://172.30.1.2:3000/login")
            val connection = url.openConnection() as HttpURLConnection

            try {
                // Configure connection for POST request
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                // Prepare the request body
                val requestBody = """{"username": "$username", "password": "$password"}"""
                val outputStream: OutputStream = connection.outputStream
                outputStream.write(requestBody.toByteArray())
                outputStream.flush()

                // Get the response from the server
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    reader.close()

                    return response.toString()
                } else {
                    return "Error: ${connection.responseCode}"
                }

            } finally {
                connection.disconnect()
            }
        }

        override fun onPostExecute(result: String) {
            // Process the result on the UI thread
            // In a real app, you'd want to handle different cases (success, failure) appropriately
            // 관리자 사용자 구분
            if (result.contains("Login successful")) {
                if (binding.editTextUsername.text.toString().equals("admin")) {
                    Toast.makeText(this@LoginActivity, "관리자 로그인 성공", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
                    startActivity(intent)
                    finish()
                } else{
                    Toast.makeText(this@LoginActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    intent.putExtra("username",binding.editTextUsername.text.toString())
                    startActivity(intent)
                    finish()
                }
            } else {
                // 로그인 실패
                Toast.makeText(this@LoginActivity, "잘못된 정보 입니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}