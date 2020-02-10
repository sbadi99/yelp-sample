package com.yelp.fusion.ui.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.yelp.fusion.R

/**
 * Content ViewHolder containing search results item elements
 */

class YelpRecyclerViewItemHolder(itemView: View?) : ViewHolder(
    itemView!!) {
  var businessName: TextView? = null
  var businessImage: ImageView? = null

  init {
    when {
      itemView != null -> {
        businessName = itemView.findViewById<View>(
            R.id.card_view_image_title) as TextView
        businessImage = itemView.findViewById<View>(
            R.id.card_view_image) as ImageView
      }
    }
  }
}