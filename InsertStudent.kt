package com.manuni.hello_world.api_integration

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.manuni.hello_world.R
import com.manuni.hello_world.api_integration.api.ApiInterface
import com.manuni.hello_world.api_integration.api.RetrofitClient
import com.manuni.hello_world.api_integration.models.SubjectsInfo
import com.manuni.hello_world.databinding.ActivityInsertStudentBinding
import kotlinx.coroutines.launch

class InsertStudent : AppCompatActivity() {
    private lateinit var binding: ActivityInsertStudentBinding
    var selected = "CSE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsertStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cseList = arrayListOf<String>()
        val eeeList = arrayListOf<String>()
        val bbaList = arrayListOf<String>()
        val engList = arrayListOf<String>()

        val departments = listOf("CSE", "EEE", "BBA", "ENG")

        val deptAdapter = ArrayAdapter(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            departments
        )
        binding.deptSpinner.adapter = deptAdapter

        // 🔁 Data fetch only once, outside listener
        var subRes: List<SubjectsInfo> = emptyList()

        lifecycleScope.launch {
            subRes = RetrofitClient.retrofit.getSubjects()
            // Optional: default selection show korar jonno
            handleSubjects(subRes, selected, cseList, eeeList, bbaList, engList)
        }

        binding.deptSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                selected = departments[position]
                Toast.makeText(this@InsertStudent, "$selected selected", Toast.LENGTH_SHORT).show()

                // 🔁 Ekhane call korchi selected value diye
                handleSubjects(subRes, selected, cseList, eeeList, bbaList, engList)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        //button click
        binding.saveBtn.setOnClickListener {
            val selectedSubjects = mutableListOf<String>()
            val checkBoxes = listOf(
                binding.checkbox1,
                binding.checkbox2,
                binding.checkbox3,
                binding.checkbox4,
                binding.checkbox5,
                binding.checkbox6
            )

            for (checkbox in checkBoxes){
                if (checkbox.isChecked){
                    selectedSubjects.add(checkbox.text.toString())
                }
            }


            /**
             * না, এখানে filter ব্যবহার করা যেত না — কারণ আমরা কোনো item বাদ দিচ্ছি না, বরং প্রতিটি item কে রূপান্তর (transform) করছি।
             *
             */

            val selectedIds = selectedSubjects.map { subjectText ->
                subjectText.split("-")[0].trim()
            }

            Toast.makeText(this@InsertStudent,"$selectedIds is here",Toast.LENGTH_SHORT).show()
        }

    }

    private fun handleSubjects(
        subRes: List<SubjectsInfo>,
        selected: String,
        cseList: ArrayList<String>,
        eeeList: ArrayList<String>,
        bbaList: ArrayList<String>,
        engList: ArrayList<String>
    ) {
        // Clear previous lists so data na duplicate hoy
        cseList.clear()
        eeeList.clear()
        bbaList.clear()
        engList.clear()

        subRes.forEach {
            when (selected) {
                "CSE" -> {
                    if (it.subjectName == "Data Structure") cseList.add("101 - Data Structure")
                    if (it.subjectName == "Algorithm") cseList.add("102 - Algorithm")
                    if (it.subjectName == "Operating Systems") cseList.add("103 - Operating Systems")
                }
                "EEE" -> {
                    if (it.subjectName == "Signals and Systems") eeeList.add("201 - Signals and Systems")
                    if (it.subjectName == "Circuit Theory") eeeList.add("202 - Circuit Theory")
                    if (it.subjectName == "Digital Electronics") eeeList.add("203 - Digital Electronics")
                }
                "BBA" -> {
                    if (it.subjectName == "Principles of Manage") bbaList.add("301 - Principles of Manage")
                    if (it.subjectName == "Business Study") bbaList.add("302 - Business Study")
                    if (it.subjectName == "Marketing Fundamenta") bbaList.add("303 - Marketing Fundamenta")
                }
                "ENG" -> {
                    if (it.subjectName == "British Literature") engList.add("401- British Literature")
                    if (it.subjectName == "Fiction") engList.add("402 - Fiction")
                    if (it.subjectName == "Poetry") engList.add("403 - Poetry")
                }
            }
        }



        // Spinner update
//        val subAdapter = when (selected) {
//            "CSE" -> ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, cseList)
//            "EEE" -> ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, eeeList)
//            "BBA" -> ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, bbaList)
//            "ENG" -> ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, engList)
//            else -> null
//        }

        val subList = when (selected) {
            "CSE" -> cseList
            "EEE" -> eeeList
            "BBA" -> bbaList
            "ENG" -> engList
            else -> null
        }

        subList?.let {
            setSubjectsToCheckBox(subList)
            //binding.subjectSpinner.adapter = it
        }
    }


    //একটা subject list (যেমন: ["Math", "Physics", "Chemistry"]) থেকে শুধু যতগুলা subject আছে, ততগুলা checkbox visible থাকুক এবং তাতে সেই subject গুলোর নাম দেখাক। বাকিগুলা hide হয়ে থাকুক।
    private fun setSubjectsToCheckBox(subjects: List<String>){
        val checkboxes = listOf(
            binding.checkbox1,
            binding.checkbox2,
            binding.checkbox3,
            binding.checkbox4,
            binding.checkbox5,
            binding.checkbox6
        )


        /**
         * i	i < subjects.size মানে?	কাজ কী হবে
         * 0	0 < 3 ✅	cb1 = Math, visible
         * 1	1 < 3 ✅	cb2 = Physics, visible
         * 2	2 < 3 ✅	cb3 = Biology, visible
         * 3	3 < 3 ❌	cb4 = GONE
         */

        for (i in checkboxes.indices){
            if (i < subjects.size){
                checkboxes[i].visibility = View.VISIBLE
                checkboxes[i].text = subjects[i]
            }else{
                checkboxes[i].visibility = View.GONE
            }
        }

    }
}
