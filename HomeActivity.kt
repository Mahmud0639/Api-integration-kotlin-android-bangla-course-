package com.manuni.hello_world.api_integration

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.manuni.hello_world.api_integration.adapters.StudentsAdapter
import com.manuni.hello_world.api_integration.api.RetrofitClient
import com.manuni.hello_world.api_integration.models.Students
import com.manuni.hello_world.databinding.ActivityHomeBinding
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var studentsAdapter: StudentsAdapter
    private var studentsList: ArrayList<Students> = arrayListOf()

    companion object {
        var shouldRefresh = false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)



        studentsAdapter = StudentsAdapter(studentsList)
        binding.studentsRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.studentsRV.adapter = studentsAdapter


        studentsAdapter.setOnViewOrEditListener { students ->
            //Toast.makeText(this,"${students.projectTitle} clicked",Toast.LENGTH_SHORT).show()
            val intent = Intent(this, StudentsDetailsActivity::class.java)
            intent.putExtra("STUDENTS",students)
            startActivity(intent)
        }

        binding.addStudentCardView.setOnClickListener {
            startActivity(Intent(this,InsertStudent::class.java))
        }

//        lifecycleScope.launch {
//            val result = RetrofitClient.retrofit.getStudents(1, 10)
//            Log.d("Ret_Data", "onCreate: $result")
//            Toast.makeText(this@HomeActivity,"data is: ${result[0].projectTitle}",Toast.LENGTH_SHORT).show()
//
//            studentsList.clear()
//            studentsList.addAll(result)
//            studentsAdapter.notifyDataSetChanged()
//        }

        loadStudents()

    }

    private fun loadStudents() {
        lifecycleScope.launch {
            try {
                val result = RetrofitClient.retrofit.getStudents(1, 50)
                Log.d("Ret_Data", "onStart: $result")
                studentsList.clear()
                studentsList.addAll(result)
                studentsAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                Toast.makeText(this@HomeActivity, "Failed to load data", Toast.LENGTH_SHORT).show()
                Log.e("API_ERROR", "Error fetching data", e)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (shouldRefresh){ //eta korle jokhon app first time open hoy tokhon jeno onStart method notun kore loadStudents again call na hoy
            loadStudents()
            shouldRefresh = false
        }

    }

}