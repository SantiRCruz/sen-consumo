package com.example.prueba

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import com.example.consumokotlinsimple.models.LogIn
import com.example.consumokotlinsimple.models.ResponseLogIn
import com.example.prueba.core.Constants
import com.example.prueba.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.supportActionBar?.hide()

        clicks()


    }

    private fun clicks() {
        binding.btnIniciarSesion.setOnClickListener { validateData() }
    }

    private fun validateData() {
        val results = arrayOf(validateNickName(), validatePassword())
        if (false in results) {
            return
        }
        sendUser()
    }

    private fun validateNickName(): Boolean {
        return if (binding.editText.text.toString().isNullOrEmpty()) {
            binding.editText.error = "Este campo es obligatorio"
            false
        } else {
            binding.editText.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {
        return if (binding.editText2.text.toString().isNullOrEmpty()) {
            binding.editText2.error = "Este campo es obligatorio"
            false
        } else {
            binding.editText2.error = null
            true
        }
    }

    private fun sendUser() {
        val retro = Retrofit.Builder()
            .baseUrl(Constants.URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service: ApiService = retro.create(ApiService::class.java)
        service.postLogin(
            LogIn(
                binding.editText.text.toString(),
                binding.editText2.text.toString()
            )
        ).enqueue(object :
            Callback<ResponseLogIn> {
            override fun onResponse(call: Call<ResponseLogIn>, response: Response<ResponseLogIn>) {
                Log.e("onResponse: ", response.body().toString())
                if (response.isSuccessful) {
                    Constants.actualUser = response.body()
                    val i = Intent(applicationContext, MainActivity::class.java)
                    startActivity(i)
                }else{
                    Snackbar.make(binding.root,"Error en los datos que ingreso",Snackbar.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseLogIn>, t: Throwable) {
                Log.e("onFailure: ", t.toString())
                Snackbar.make(binding.root,"Error de servidor",Snackbar.LENGTH_SHORT).show()
            }
        })
    }
}