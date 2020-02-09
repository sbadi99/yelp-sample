package com.yelp.fusion.api.model.autocomplete

import com.google.gson.annotations.SerializedName

data class TermsItem(

	@field:SerializedName("text")
	val text: String? = null
)