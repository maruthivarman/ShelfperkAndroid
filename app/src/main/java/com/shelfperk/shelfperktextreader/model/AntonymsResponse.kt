package com.shelfperk.shelfperktextreader.model

import com.google.gson.annotations.SerializedName


data class AntonymsResponse (

  @SerializedName("word"     ) var word     : String?           = null,
  @SerializedName("antonyms" ) var antonyms : ArrayList<String> = arrayListOf()

)