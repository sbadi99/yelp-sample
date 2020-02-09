package com.yelp.fusion.api.model.autocomplete

import com.google.gson.annotations.SerializedName

data class CategoriesItem(

	@field:SerializedName("alias")
	val alias: String? = null,

	@field:SerializedName("title")
	val title: String? = null
)