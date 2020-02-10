package com.yelp.fusion.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.squareup.picasso.Picasso
import com.yelp.fusion.api.model.search.BusinessesItem
import com.yelp.fusion.R

/**
 * The YelpSearchRecyclerViewDataAdapter item adapter class extends RecyclerView.Adapter
 */

class YelpSearchRecyclerViewDataAdapter(
    var itemList: ArrayList<BusinessesItem?>) : Adapter<YelpRecyclerViewItemHolder>() {
  override fun onCreateViewHolder(parent: ViewGroup,
      viewType: Int): YelpRecyclerViewItemHolder { // Get LayoutInflater object.
    val layoutInflater = LayoutInflater.from(parent.context)
    // Inflate the RecyclerView item layout xml.
    val itemView = layoutInflater.inflate(R.layout.activity_card_view_item,
        parent, false)
    val titleView = itemView.findViewById<View>(
        R.id.card_view_image_title) as TextView
    val imageView = itemView.findViewById<View>(
        R.id.card_view_image) as ImageView
    // When click the image.
    imageView.setOnClickListener {
      val title = titleView.text.toString()
    }
    // Create and return our custom Recycler View Item Holder object.
    return YelpRecyclerViewItemHolder(itemView)
  }

  override fun onBindViewHolder(holder: YelpRecyclerViewItemHolder,
      position: Int) {
      val item = itemList[position]
      holder.businessName?.text = item?.name
      Picasso.get().load(item?.imageUrl).into(holder.businessImage)}

  override fun getItemCount(): Int {
    var ret = 0
    ret = itemList.size
    return ret
  }

}