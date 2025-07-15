package com.manuni.hello_world.api_integration.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Departments (

    @SerializedName("dept_id"   ) var deptId   : Int?    = null,
    @SerializedName("dept_name" ) var deptName : String? = null

):Parcelable
