package com.yelp.fusion.api.model.search

import com.google.gson.annotations.SerializedName

data class Region(

	@field:SerializedName("center")
	val center: Center? = null
)