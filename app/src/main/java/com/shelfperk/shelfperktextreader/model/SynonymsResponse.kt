package com.shelfperk.shelfperktextreader.model

import com.google.gson.annotations.SerializedName


data class SynonymsResponse (

  @SerializedName("word"     ) var word     : String?           = null,
  @SerializedName("synonyms" ) var synonyms : ArrayList<String> = arrayListOf()

)