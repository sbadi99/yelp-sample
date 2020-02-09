package com.yelp.fusion.api.model.autocomplete

import com.google.gson.annotations.SerializedName

data class BusinessesItem(

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("name")
	val name: String? = null
)