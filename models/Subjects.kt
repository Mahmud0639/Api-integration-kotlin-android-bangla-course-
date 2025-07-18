package com.manuni.hello_world.api_integration.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Subjects (

    @SerializedName("subject_id"   ) var subjectId   : Int?    = null,
    @SerializedName("subject_name" ) var subjectName : String? = null

):Parcelable
