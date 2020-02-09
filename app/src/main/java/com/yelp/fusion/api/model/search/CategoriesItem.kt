package com.yelp.fusion.api.model.search

import com.google.gson.annotations.SerializedName

data class CategoriesItem(

	@field:SerializedName("alias")
	val alias: String? = null,

	@field:SerializedName("title")
	val title: String? = null
)