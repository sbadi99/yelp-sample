package com.yelp.fusion.api.retrofit

import android.content.Context
import com.yelp.fusion.BuildConfig
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import retrofit2.Retrofit
import retrofit2.Retrofit.Builder
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit.SECONDS

/**
 * This class handles setup and creating of retrofit api client and related logic
 * Also passing Yelp Fusion API_KEY in headers per Yelp documentation
 */
object Network {

  const val BASE_URL = "https://api.yelp.com/"
  const val API_KEY = "Bearer YGTA18YoS9IhHLFOc0-1zLRZscnZeA_nyzsfOj-9xZOY_ufJ-gpx9aTyIvefOc45iCFLoW6kF5zwdfiC-g16C7fyl5ia-mjFNZMpGRnO8CKfmIrxqAd4ybklI14_XnYx"
  const val API_HEADER = "Authorization"


  fun getYelpApi(context: Context): YelpApi {
    return createRetrofitClient(context)
  }


  private fun createCache(context: Context?): Cache? {
    var cache: Cache? = null
    try {
      val cacheSize = 10 * 1024 * 1024 // 10 MB
      val httpCacheDirectory = File(context?.getCacheDir(), "http-cache")
      cache = Cache(httpCacheDirectory, cacheSize.toLong())
    } catch (e: Exception) {
      Timber.e("Failed to create create Cache!" + e)
    }
    return cache
  }


  /**
   * Creates the retrofit client
   * @param the context passed
   */
  private fun createRetrofitClient(context: Context?): YelpApi {
    val builder = OkHttpClient().newBuilder()
        .cache(createCache(context))

    builder.readTimeout(10, SECONDS)
    builder.connectTimeout(5, SECONDS)

    if (BuildConfig.DEBUG) {
      val interceptor = HttpLoggingInterceptor()
      interceptor.setLevel(BODY)
      builder.addInterceptor(interceptor)
    }
    builder.addInterceptor(Interceptor { chain: Chain ->
      val request: Request = chain.request().newBuilder().addHeader(API_HEADER, API_KEY).build()
      chain.proceed(request)
    })

    val client = builder.build()
    val retrofit: Retrofit = Builder().baseUrl(BASE_URL).client(
        client).addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(
        RxJava2CallAdapterFactory.create()).build()

    return retrofit.create(YelpApi::class.java)
  }

}