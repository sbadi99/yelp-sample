package com.yelp.fusion.api.model.search

import com.google.gson.annotations.SerializedName

data class Coordinates(

	@field:SerializedName("latitude")
	val latitude: Double? = null,

	@field:SerializedName("longitude")
	val longitude: Double? = null
)