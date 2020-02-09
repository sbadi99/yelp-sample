package com.yelp.fusion.utils

import android.view.View

/**
 * ViewUtility for generic view helpers
 */

class ViewUtils{

  companion object{
    /**
     * toggle progress bar visibility
     * @param view the view
     * @param show to show the view or not
     */
    fun toggleProgressIndicator(view: View?,show: Boolean = false) {
      when {
        show -> view?.visibility = View.VISIBLE
        else -> view?.visibility = View.GONE
      }
    }
  }
}
