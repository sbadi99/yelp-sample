package com.yelp.fusion.api.model.search

import com.google.gson.annotations.SerializedName
data class Location(

	@field:SerializedName("country")
	val country: String? = null,

	@field:SerializedName("address3")
	val address3: String? = null,

	@field:SerializedName("address2")
	val address2: String? = null,

	@field:SerializedName("city")
	val city: String? = null,

	@field:SerializedName("address1")
	val address1: String? = null,

	@field:SerializedName("display_address")
	val displayAddress: List<String?>? = null,

	@field:SerializedName("state")
	val state: String? = null,

	@field:SerializedName("zip_code")
	val zipCode: String? = null
)