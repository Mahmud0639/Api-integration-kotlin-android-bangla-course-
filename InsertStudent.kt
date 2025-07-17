package com.manuni.hello_world.api_integration

import android.app.Activity
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.dhaval2404.imagepicker.ImagePicker
import com.manuni.hello_world.R
import com.manuni.hello_world.api_integration.api.ProgressTracker
import com.manuni.hello_world.api_integration.api.RetrofitClient
import com.manuni.hello_world.api_integration.models.ResultResponse
import com.manuni.hello_world.api_integration.models.SubjectsInfo
import com.manuni.hello_world.databinding.ActivityInsertStudentBinding
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.io.File

class InsertStudent : AppCompatActivity() {
    private lateinit var binding: ActivityInsertStudentBinding
    var selected = "CSE"

    private var mProfileUri:String? = null

    @RequiresApi(Build.VERSION_CODES.O)
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

        //image pick
        binding.pickImage.setOnClickListener{
            ImagePicker.with(this@InsertStudent)
                .crop()
                .compress(1024)
                .maxResultSize(512, 512)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }

        var proTitle = ""
        var proDesc = ""
        var userName = ""
        var userEmail = ""
        var userPhone = ""
        var credits = ""

        //button click
        binding.saveBtn.setOnClickListener {

            //for image validation
            if (mProfileUri == null){
                return@setOnClickListener
            }

            if (binding.projectTitle.text.isNullOrEmpty()){
                binding.projectTitle.error = "Field can't be empty."
                return@setOnClickListener
            }else{
                proTitle = binding.projectTitle.text.toString().trim()
                binding.projectTitle.error = null
            }

            if (binding.proDescription.text.isNullOrEmpty()){
                binding.proDescription.error = "Field can't be empty."
                return@setOnClickListener
            }else{
                proDesc = binding.proDescription.text.toString().trim()
                binding.projectTitle.error = null
            }

            if (binding.userNameET.text.isNullOrEmpty()){
                binding.userNameET.error = "Field can't be empty."
                return@setOnClickListener
            }else{
                userName = binding.userNameET.text.toString().trim()
                binding.projectTitle.error = null
            }

            if (binding.userEmailET.text.isNullOrEmpty()){
                binding.userEmailET.error = "Field can't be empty."
                return@setOnClickListener
            }else{
                userEmail = binding.userEmailET.text.toString().trim()
                binding.projectTitle.error = null
            }

            if (binding.userMobileET.text.isNullOrEmpty()){
                binding.userMobileET.error = "Field can't be empty."
                return@setOnClickListener
            }else{
                userPhone = binding.userMobileET.text.toString().trim()
                binding.projectTitle.error = null
            }

            //for checkbox
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
                subjectText.split("-")[0].trim().toInt()
            }

            Toast.makeText(this@InsertStudent,"$selectedIds is here",Toast.LENGTH_SHORT).show()


            val sSubjects = checkBoxes.filter { it.isChecked }.map { it.text.toString() }

            if (sSubjects.isEmpty()){
                Toast.makeText(this, "At least one subject should be taken.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (binding.creditET.text.isNullOrEmpty()){
                binding.creditET.error = "Field can't be empty."
                return@setOnClickListener
            }else{
                credits = binding.creditET.text.toString().trim()

                binding.projectTitle.error = null
            }

            Toast.makeText(this@InsertStudent,"$mProfileUri-$proTitle-$proDesc-$userName-$userEmail-$userPhone-$selected-$selectedIds-$credits",Toast.LENGTH_SHORT).show()


            saveInfo(userName,userEmail,userPhone,credits.toInt(),selected,selectedIds.toTypedArray(),proTitle,proDesc,mProfileUri!!, success = {
                Toast.makeText(this@InsertStudent,"${it.Result}",Toast.LENGTH_SHORT).show()
            }, saveSuccess = {
                Toast.makeText(this@InsertStudent,"${it.Result}",Toast.LENGTH_SHORT).show()
            })


        }

    }

    //save data to database
    private fun saveInfo(mName: String,mEmail: String,mPhone: String,totCredit: Int,mDept: String,mSubjects: Array<Int>,proTitle: String, proDesc: String, mProfile:String?, success: (ResultResponse) -> Unit,saveSuccess: (ResultResponse)->Unit){

        binding.progressBar.visibility = View.VISIBLE
        binding.progressValue.visibility = View.VISIBLE

        //students_profile

        val mapData = HashMap<String,Any>().apply {
            put("name",mName)
            put("email",mEmail)
            put("phone",mPhone)
            put("total_credits",totCredit)
            put("dept_name",mDept)
            put("subjects",mSubjects)
        }

        val formData = MultipartBody.Builder().apply {
            setType(MultipartBody.FORM)//kon type er input..form naki raw

            addFormDataPart("title",proTitle)
            addFormDataPart("description",proDesc)
            addFormDataPart("profile_id", "")

            val profileFile = mProfile?.let {
                File(it)
            }



            if (profileFile != null){
                addPart(MultipartBody.Part.createFormData("my_file",profileFile.name,ProgressTracker(profileFile,object : ProgressTracker.UploadCallBack{
                    override fun onProgressUpdate(percentage: Int) {
                        runOnUiThread {
                            binding.progressBar.progress = percentage
                            binding.progressValue.text = "$percentage%"
                        }

                    }

                    override fun onError() {
                        runOnUiThread {

                        }
                    }

                    override fun onFinish() {
                        runOnUiThread {
                            binding.progressBar.visibility = View.GONE
                            binding.progressValue.visibility = View.GONE

                            binding.projectTitle.text.clear()
                            binding.proDescription.text.clear()
                            binding.userNameET.text.clear()
                            binding.userEmailET.text.clear()
                            binding.userMobileET.text.clear()

                            val checkBoxes = listOf(
                                binding.checkbox1,
                                binding.checkbox2,
                                binding.checkbox3,
                                binding.checkbox4,
                                binding.checkbox5,
                                binding.checkbox6

                            )
                            for (cb in checkBoxes) {
                                cb.isChecked = false
                            }

                            binding.creditET.text.clear()

                            mProfileUri = null
                            binding.profilePic.setImageResource(R.drawable.avatar)

                        }
                    }
                })))
            }

        }.build()




        lifecycleScope.launch {
            try {

                val parts = formData.parts()
                Log.d("DEBUG", "Multipart parts count: ${parts.size}")
                parts.forEachIndexed { index, part ->
                    Log.d("DEBUG", "Part $index: $part")
                }



                val saveResponse = RetrofitClient.retrofit.saveStudents(mapData)
                Log.d("DEBUG", "After saveStudents: $saveResponse")

                saveSuccess(saveResponse)

                Toast.makeText(this@InsertStudent,"Call here",Toast.LENGTH_SHORT).show()
                Log.d("DEBUG", "Before uploadFile")
                val uploadResponse = RetrofitClient.retrofit.uploadFile(formData.parts())
                Log.d("DEBUG", "After uploadFile: $uploadResponse")


           success(uploadResponse)

                HomeActivity.shouldRefresh = true

            } catch (e: Exception) {
                Log.e("UPLOAD", "Error in upload or save: ${e.message}", e)
            }
        }
    }



    //for image
    @RequiresApi(Build.VERSION_CODES.O)
    private val startForProfileImageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val resultCode = result.resultCode
        val data = result.data

        if (resultCode == Activity.RESULT_OK) {
            // Image Uri will not be null for RESULT_OK
            val fileUri = data?.data
            //this below code portion was not given in the above same function for that we faced error
            if (fileUri != null) {
                mProfileUri = getRealPathFromUri(fileUri)
                Toast.makeText(this, "Image path: $mProfileUri", Toast.LENGTH_SHORT).show()
                Log.d("IMAGE_PATH", "Storage path: $mProfileUri")
                if (mProfileUri != null) {
                    binding.profilePic.setImageURI(fileUri)
                } else {
                    Toast.makeText(this, "Failed to get image path", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getRealPathFromUri(contentUri: Uri): String? {
        var result: String? = null
        val cursor = contentResolver.query(contentUri, null, null, null, null)
        if (cursor == null) {
            result = contentUri.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }

        // Debug the result
        Log.d("FileUploadActivity", "Real Path: $result")
        return result
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