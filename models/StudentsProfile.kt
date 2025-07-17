package com.manuni.hello_world.api_integration.models

import com.google.gson.annotations.SerializedName


data class StudentsProfile (

    @SerializedName("id"            ) var id           : String? = null,
    @SerializedName("project_title" ) var projectTitle : String? = null,
    @SerializedName("project_desc"  ) var projectDesc  : String? = null,
    @SerializedName("photoUrl"      ) var photoUrl     : String? = null

)
