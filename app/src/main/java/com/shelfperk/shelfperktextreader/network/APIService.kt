package com.shelfperk.shelfperktextreader.network

import com.shelfperk.shelfperktextreader.model.AntonymsResponse
import com.shelfperk.shelfperktextreader.model.NewsResponse
import com.shelfperk.shelfperktextreader.model.SynonymsResponse
import com.shelfperk.shelfperktextreader.model.ThesaurusResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface APIService {

    @Headers(
        "X-RapidAPI-Key: 3doF2PJGahmsha4q4RbS3sETcEj2p1jpnzqjsnmPU7A9rB1xXP",
        "X-RapidAPI-Host: wordsapiv1.p.rapidapi.com",
    )
    @GET("/words/{keyword}/synonyms")
    fun fetchSynonymsResult(@Path(value = "keyword", encoded = true) keyword: String?): Call<SynonymsResponse>

    @Headers(
        "X-RapidAPI-Key: 3doF2PJGahmsha4q4RbS3sETcEj2p1jpnzqjsnmPU7A9rB1xXP",
        "X-RapidAPI-Host: wordsapiv1.p.rapidapi.com",
    )
    @GET("/words/{keyword}/antonyms")
    fun fetchAntonymsResult(@Path(value = "keyword", encoded = true) keyword: String?): Call<AntonymsResponse>

    @Headers(
        "X-RapidAPI-Key: 3doF2PJGahmsha4q4RbS3sETcEj2p1jpnzqjsnmPU7A9rB1xXP",
        "X-RapidAPI-Host: wordsapiv1.p.rapidapi.com",
    )
    @GET("/words/{keyword}/similarTo")
    fun fetchThesaurusResult(@Path(value = "keyword", encoded = true) keyword: String?): Call<ThesaurusResponse>

    @GET("v2/everything")
    fun getNews(
        @Query("q")
        searchQuery: String,
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey:String = "5db32ae52f9e411eb351c6b5453a2500"
    ): Call<NewsResponse>
}