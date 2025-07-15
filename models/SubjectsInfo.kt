package com.manuni.hello_world.api_integration.models

import com.google.gson.annotations.SerializedName

data class SubjectsInfo(
    @SerializedName("sub_id"   ) var subjectId   : Int?    = null,
    @SerializedName("subject_name" ) var subjectName : String? = null
)
