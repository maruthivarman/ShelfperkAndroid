package com.shelfperk.shelfperktextreader.model

import com.google.gson.annotations.SerializedName


data class ThesaurusResponse (

  @SerializedName("word"     ) var word     : String?           = null,
  @SerializedName("similarTo" ) var similarTo : ArrayList<String> = arrayListOf()

)