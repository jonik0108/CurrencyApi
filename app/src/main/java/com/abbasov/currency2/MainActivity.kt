package com.abbasov.currency2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.abbasov.currency2.adapter.RvAdapter
import com.abbasov.currency2.models.User
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray

class MainActivity : AppCompatActivity() {
    lateinit var requestQueue: RequestQueue
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestQueue = Volley.newRequestQueue(this)
        loadArrayList()
    }
    fun loadArrayList(){
        val url="http://cbu.uz/uzc/arkhiv-kursov-valyut/json/"
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            object : Response.Listener<JSONArray> {
                override fun onResponse(response: JSONArray?) {
                    val list=ArrayList<User>()
                    val gsonString = response.toString()
                    val type = object : TypeToken<ArrayList<User>>(){}.type
                    list.addAll(Gson().fromJson(gsonString,type))
                    val userAdapter= RvAdapter(list)
                    rv.adapter=userAdapter


                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    Toast.makeText(
                        this@MainActivity,
                        "Eror\n ${error?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
        requestQueue.add(jsonArrayRequest)
    }
}