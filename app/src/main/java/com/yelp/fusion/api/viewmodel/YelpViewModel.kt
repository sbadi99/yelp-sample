package com.yelp.fusion.api.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yelp.fusion.api.model.autocomplete.AutoCompleteResponse
import com.yelp.fusion.api.model.autocomplete.TermsItem
import com.yelp.fusion.api.retrofit.YelpApi
import com.yelp.fusion.api.model.search.BusinessesItem
import com.yelp.fusion.api.model.search.SearchResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import timber.log.Timber

/**
 * The YelpViewModel includes LiveData objects (MVVM approach per Android archectural components)
 * Using RxJava as well in conjuction with LiveData
 */

class YelpViewModel(private val yelpApi: YelpApi) : ViewModel() {
  private val compositeDisposable = CompositeDisposable()

  var yelpLiveDataAutoComplete = MutableLiveData<List<TermsItem?>>()
  var yelpLiveDataSearch = MutableLiveData<List<BusinessesItem?>>()

  internal fun fetchAutoComplete(text: String, lat: Double, lng: Double) {
    val disposable = yelpApi.getAutoComplete(text, lat, lng)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(object : DisposableObserver<Response<AutoCompleteResponse>>() {
          override fun onNext(response: Response<AutoCompleteResponse>) {
            yelpLiveDataAutoComplete.value = response.body()?.terms
          }

          override fun onComplete() {}

          override fun onError(e: Throwable) {
            yelpLiveDataAutoComplete.value = null
            Timber.e("error$e")

          }
        })
    compositeDisposable.add(disposable)
  }

  internal fun fetchBusinessSearch(search: String, lat: Double, lng: Double) {
    val disposable = yelpApi.getBusinessSearch(search, lat, lng)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(object : DisposableObserver<Response<SearchResponse>>() {
          override fun onNext(response: Response<SearchResponse>) {
            yelpLiveDataSearch.value = response.body()?.businesses
          }

          override fun onComplete() {}

          override fun onError(e: Throwable) {
            yelpLiveDataSearch.value = null
            Timber.e("error$e")

          }
        })
    compositeDisposable.add(disposable)
  }

  //Called when the activity is destroyed
  public override fun onCleared() {
    Timber.d("YelpViewModel onCleared()")
    compositeDisposable.dispose()
    super.onCleared()
  }

  class YelpViewModelFactory(private val yelpApi: YelpApi) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(YelpViewModel::class.java)) {
        return YelpViewModel(yelpApi) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
    }
  }
}