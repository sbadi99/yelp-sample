package com.yelp.fusion.api.retrofit

import com.yelp.fusion.api.model.autocomplete.AutoCompleteResponse
import com.yelp.fusion.api.model.search.SearchResponse
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface YelpApi {
  @GET("v3/autocomplete")
  fun getAutoComplete(@Query("text") text: String, @Query("latitude") lat:Double, @Query("longitude")lng:Double): Observable<Response<AutoCompleteResponse>>

  @GET("v3/businesses/search")
  fun getBusinessSearch(@Query("term") term: String, @Query("latitude") lat:Double, @Query("longitude")lng:Double): Observable<Response<SearchResponse>>
}
