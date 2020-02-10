package com.yelp.fusion.ui


import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Bundle
import android.provider.BaseColumns
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.SearchView
import androidx.cursoradapter.widget.CursorAdapter
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yelp.fusion.R

import com.yelp.fusion.api.model.autocomplete.TermsItem
import com.yelp.fusion.api.model.search.BusinessesItem
import com.yelp.fusion.api.retrofit.Network
import com.yelp.fusion.api.viewmodel.YelpViewModel
import com.yelp.fusion.api.viewmodel.YelpViewModel.YelpViewModelFactory
import com.yelp.fusion.ui.adapter.YelpSearchRecyclerViewDataAdapter
import com.yelp.fusion.utils.ViewUtils.Companion.toggleProgressIndicator
import kotlinx.android.synthetic.main.activity_main.empty_results
import kotlinx.android.synthetic.main.activity_main.main_progress
import kotlinx.android.synthetic.main.activity_main.search_recyclerview
import timber.log.Timber

/**
 * This Activity provides the core Yelp Search UI.
 * Utilizes search icon in Android toolbar.
 * Also uses native Android SearchView widget
 */
class YelpSearchActivity : LocationActivity() {

  private var isAutoComplete: Boolean = false
  private var suggestions: ArrayList<String>? = arrayListOf()
  private var searchRecyclerView: RecyclerView? = null
  private var searchAdapterSearch: YelpSearchRecyclerViewDataAdapter? = null
  private var searchQuery: String? = null

  private var autoCompletetObserver: Observer<List<TermsItem?>>? = null
  private var businessSearchtObserver: Observer<List<BusinessesItem?>>? = null
  private var yelpViewModel: YelpViewModel? = null

  companion object {
    private val TAG = YelpSearchActivity::class.java.simpleName
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    when {
      hasLocationPermission() -> {
        startLocationUpdates()
      }
    }
    setupUi()

    //initializing the search view model & live data observer
    initSearchViewModel()
    setUpLiveDataObservers()

    //intro message on first launch
    empty_results.text = getString(R.string.intro_message)
    empty_results.visibility = View.VISIBLE
  }

  /**
   * Setting up LiveData Observer which observes all the search live data updates
   * This also populates and updates the UI accordingly,
   * when new searches are conducted by the user
   */
  private fun setUpLiveDataObservers() {
    autoCompletetObserver = Observer {
      if (it == null) {
        Timber.e("no auto complete search results fetched")
      }
      it?.let { terms ->
        when {
          terms.isEmpty() -> {
            Timber.d("auto complete search results are empty")
          }
          else -> {
            terms.let { termsList ->
              toggleProgressIndicator(main_progress)
              empty_results.visibility = View.GONE
              val autoCompleteTerms = arrayListOf<TermsItem?>()
              autoCompleteTerms.addAll(termsList)

              autoCompleteTerms.forEach {
                it?.text?.let { text -> suggestions?.add(text) }
              }
              Timber.i("success api reponse: $autoCompleteTerms")

            }
          }
        }
      }
    }

    businessSearchtObserver = Observer {
      if (it == null) {
        toggleProgressIndicator(main_progress)
        Timber.e("no search results fetched")
        resetUi()
        toggleProgressIndicator(main_progress)
        empty_results.text = getString(R.string.empty_results)
        empty_results.visibility = View.VISIBLE

      }
      it?.let { terms ->
        when {
          terms.isEmpty() -> {
            Timber.e("search results are empty")
            resetUi()
            toggleProgressIndicator(main_progress)
            empty_results.text = getString(R.string.empty_results)
            empty_results.visibility = View.VISIBLE
          }
          else -> {
            terms.let { businessItems ->

              toggleProgressIndicator(main_progress)
              val items = arrayListOf<BusinessesItem?>()
              empty_results.visibility = View.GONE
              items.addAll(businessItems)
              populateSearchUi(items)
              Timber.i("success api reponse: $businessItems")

            }
          }
        }
      }
    }

    yelpViewModel?.yelpLiveDataAutoComplete?.observe(this,
        autoCompletetObserver as Observer<List<TermsItem?>>)

    yelpViewModel?.yelpLiveDataSearch?.observe(this,
        businessSearchtObserver as Observer<List<BusinessesItem?>>)
  }

  /**
   * Setting up the UI with the searchRecyclerView (search list)
   */
  private fun setupUi() {
    searchRecyclerView = search_recyclerview
    // Create the grid layout manager with 2 columns.
    val gridLayoutManager = GridLayoutManager(this, 2)
    searchRecyclerView?.layoutManager = gridLayoutManager

  }

  /**
   * Note: launch mode of activity is 'single-top' to prevent multiple nested activity instances from launching,
   * so onNewIntent will fire here instead on the normal onCreate (since activity laucnch mode is 'single-top'
   * This method handles the search query flow in the native Android search view in the toolbar
   * @param the intent passed
   */
  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    // Verify the action and get the query
    when (Intent.ACTION_SEARCH) {
      intent?.action -> {
        intent.getStringExtra(SearchManager.QUERY)?.also { searchQuery ->
          if (searchQuery.isNotEmpty()) {
            this.searchQuery = searchQuery
            makeBusinessApiCall(searchQuery)
          }
        }
      }
    }
  }

  /**
   * Populating the searchRecyclerView adapter with the search result data
   */
  private fun populateSearchUi(
      businessesItem: ArrayList<BusinessesItem?>) {
    searchAdapterSearch = YelpSearchRecyclerViewDataAdapter(businessesItem)
    searchRecyclerView?.adapter = searchAdapterSearch
  }

  /**
   * Creating the search menu icon on the toolbar & relevant search wire-up logic
   */
  override fun onCreateOptionsMenu(menu: Menu?): Boolean {

    val inflater = menuInflater
    inflater.inflate(R.menu.search_menu, menu)

    // Get the SearchView and set the searchable configuration
    val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
    var searchView = menu?.findItem(R.id.search)?.actionView as SearchView
    searchView.apply {
      // Assumes current activity is the searchable activity
      setSearchableInfo(searchManager.getSearchableInfo(componentName))
      setIconifiedByDefault(false) // Do not iconify the widget; expand it by default
      searchView.findViewById<AutoCompleteTextView>(
          R.id.search_src_text).threshold = 1

      val from = arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1)
      val to = intArrayOf(R.id.item_label)
      val cursorAdapter = SimpleCursorAdapter(context,
          R.layout.search_item, null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER)

      searchView.suggestionsAdapter = cursorAdapter

      searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
          hideKeyboard(currentFocus ?: View(this@YelpSearchActivity))
          return false
        }

        override fun onQueryTextChange(query: String?): Boolean {
          isAutoComplete = true

          //has location permission
          if(hasLocationPermission()){
            currentLocation?.longitude?.let {lng->
              currentLocation?.latitude?.let { lat ->
                //fetch auto complete data from yelp here
                yelpViewModel?.fetchAutoComplete(query.toString(), lat, lng)
              }
            }
          }
          else{
            //check location permission
            checkLocationPermission()
          }

          //add suggestions to the cursor adapter
          val cursor = MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1))
          query?.let {
            suggestions?.forEachIndexed { index, suggestion ->
              if (suggestion.contains(query, true))
                cursor.addRow(arrayOf(index, suggestion))
            }
          }
          cursorAdapter.changeCursor(cursor)
          return true
        }
      })

      searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
        override fun onSuggestionSelect(position: Int): Boolean {
          return false
        }

        /**
         * The suggestion click listener
         */
        override fun onSuggestionClick(position: Int): Boolean {
          hideKeyboard(currentFocus ?: View(this@YelpSearchActivity))
          val cursor = searchView.suggestionsAdapter.getItem(position) as Cursor
          val selection = cursor.getString(
              cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1))
          searchView.setQuery(selection, false)

          this@YelpSearchActivity.searchQuery = selection
          makeBusinessApiCall(selection)
          return true
        }

      })
    }
    return true
  }

  /**
   * Make yelp search call to get buiness data
   * @param the search query
   */
  private fun makeBusinessApiCall(searchQuery: String) {
    when {
      hasLocationPermission() -> {
        currentLocation?.longitude?.let { lng ->
          currentLocation?.latitude?.let { lat ->
            toggleProgressIndicator(main_progress, true)
            yelpViewModel?.fetchBusinessSearch(searchQuery, lat, lng)
          }
        }
      }
      else -> {
        checkLocationPermission()
      }
    }
  }


  /**
   * Resetting the UI here
   */
  private fun resetUi() {
    searchAdapterSearch?.itemList?.clear()
    searchRecyclerView?.recycledViewPool?.clear();
    searchRecyclerView?.adapter?.notifyDataSetChanged()
  }

  /**
   * Initializing the Search ViewModel
   */
  private fun initSearchViewModel() {
    val viewModelFactory = YelpViewModelFactory(Network.getYelpApi(this@YelpSearchActivity))
    yelpViewModel = ViewModelProviders.of(this, viewModelFactory).get(YelpViewModel::class.java)
  }


  /**
   * Location permission is granted by user,
   * fetch users latest location update
   * Also trigger corresponding auto complete or business search call depending on the user flow
   */
  override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    super.onPermissionsGranted(requestCode, perms)
    startLocationUpdates()

    when {
      isAutoComplete -> {
        currentLocation?.longitude?.let {lng->
          currentLocation?.latitude?.let { lat ->
            yelpViewModel?.fetchAutoComplete(this@YelpSearchActivity.searchQuery.toString(), lat, lng)
            isAutoComplete = false
          }
        }
      }
    }
    currentLocation?.longitude?.let {lng->
      currentLocation?.latitude?.let { lat ->
        yelpViewModel?.fetchBusinessSearch(this@YelpSearchActivity.searchQuery.toString(), lat, lng)
      }
    }

  }

  /**
   * Hide the keyboard
   * @param the view
   */
  fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
  }

}
