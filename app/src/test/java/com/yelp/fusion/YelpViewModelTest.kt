package com.evgo.unittestsample

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.yelp.fusion.RxImmediateSchedulerRule
import com.yelp.fusion.api.model.autocomplete.AutoCompleteResponse
import com.yelp.fusion.api.model.autocomplete.TermsItem
import com.yelp.fusion.api.retrofit.YelpApi
import com.yelp.fusion.api.viewmodel.YelpViewModel
import io.reactivex.Observable
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class YelpViewModelTest {

  // A JUnit Test Rule that swaps the background executor used by
  // the Architecture Components with a different one which executes each task synchronously.
  // You can use this rule for your host side tests that use Architecture Components.
  @Rule
  @JvmField
  var rule = InstantTaskExecutorRule()

  // Test rule for making the RxJava to run synchronously in unit test
  companion object {
    @ClassRule
    @JvmField
    val schedulers = RxImmediateSchedulerRule()
  }

  @Mock
  lateinit var yelpApi: YelpApi

  @Mock
  lateinit var observer: Observer<List<TermsItem?>>

  lateinit var yelpViewModel: YelpViewModel


  @Before
  fun setUp() {
    //TODO: setup mock test server?
    // initialize the ViewModed with a mocked api
    yelpViewModel = YelpViewModel(yelpApi)
  }

  @Test
  fun searchTermTest() {
    // mock data
    val searchTerm = "delis"
    val autoCompleteResponse = AutoCompleteResponse()

    // make the api to return mock data
    Mockito.`when`(yelpApi.getAutoComplete(searchTerm,37.786882,-122.399972))
        .thenReturn(Observable.just(Response.success(autoCompleteResponse)))

    // observe on the MutableLiveData with an observer
    yelpViewModel.yelpLiveDataAutoComplete.observeForever(observer)
    yelpViewModel.fetchAutoComplete(searchTerm,37.786882,-122.399972)

    // assert that the term matches
    assert(yelpViewModel.yelpLiveDataAutoComplete.value?.get(0)?.text?.contains(searchTerm, true) == true)
  }

}