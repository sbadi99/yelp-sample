package com.yelp.fusion.api.model.search

import com.google.gson.annotations.SerializedName

data class SearchResponse(

	@field:SerializedName("total")
	val total: Int? = null,

	@field:SerializedName("region")
	val region: Region? = null,

	@field:SerializedName("businesses")
	val businesses: List<BusinessesItem?>? = null
)