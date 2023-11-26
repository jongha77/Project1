package com.example.project1

import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.project1.databinding.ActivitySignUpBinding
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class SignUpActivity : AppCompatActivity() {
    private var mBinding: ActivitySignUpBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSignUp.setOnClickListener {
            val username = binding.editTextUsername.text.toString()
            val password = binding.editTextPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                val signUpTask = SignUpAsyncTask(object : SignUpAsyncTask.SignUpCallback {
                    override fun onSignUpResult(result: String) {
                        // Handle the sign-up result
                        Toast.makeText(this@SignUpActivity, result, Toast.LENGTH_SHORT).show()
                    }
                })

                signUpTask.execute(username, password)
            } else {
                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private class SignUpAsyncTask(private val callback: SignUpCallback) : AsyncTask<String, Void, String>() {

        interface SignUpCallback {
            fun onSignUpResult(result: String)
        }

        override fun doInBackground(vararg params: String?): String {
            if (params.isNullOrEmpty() || params.size < 2) {
                return "Error: Missing username or password."
            }

            val url = URL("http://172.30.1.2:3000/signup")
            val username = params[0]
            val password = params[1]

            try {
                val urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.requestMethod = "POST"
                urlConnection.setRequestProperty("Content-Type", "application/json")
                urlConnection.doOutput = true

                val outputStream = DataOutputStream(urlConnection.outputStream)
                val data = "{\"username\":\"$username\",\"password\":\"$password\"}".toByteArray(Charsets.UTF_8)
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
                return "User created successfully"
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