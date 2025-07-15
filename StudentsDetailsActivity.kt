package com.manuni.hello_world.api_integration

import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.manuni.hello_world.R
import com.manuni.hello_world.api_integration.api.ApiEndPoints
import com.manuni.hello_world.api_integration.models.Students
import com.manuni.hello_world.databinding.ActivityStudentsDetailsBinding
import com.squareup.picasso.Picasso

class StudentsDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStudentsDetailsBinding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStudentsDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // val studentData = intent.getParcelableExtra<Students>("STUDENTS")
        //val studentData = intent.getParcelableExtra("STUDENTS") as Students?
        val stu = intent.getParcelableExtra("STUDENTS", Students::class.java)



        //eta edit text er khetre setText use korte hobe
        binding.projectTitle.setText(stu?.projectTitle)

        binding.proDescription.setText(stu?.projectDesc)

        binding.userNameET.setText(stu?.name)
        binding.userEmailET.setText(stu?.email)
        binding.userMobileET.setText(stu?.phone)



        val subjects = arrayListOf<String>()


        stu?.subjects?.forEach {
            subjects.add(
                "${it.subjectId} - ${it.subjectName}"
            )
        }

        binding.motivationTxt.text =
            "Hi, ${stu?.name}. Your department is ${stu?.departments?.deptName} and taken subjects are ${subjects}"

        val subAdapter = ArrayAdapter(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            subjects
        )

    //    binding.subjectSpinner.adapter = subAdapter


        //try/catch shortcut ctrl+alt+T
        try {
            Picasso.get().load(ApiEndPoints.BASE_URL + "ourapi" + stu?.imageUrl)
                .placeholder(R.drawable.avatar).into(binding.profilePic)
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }
}