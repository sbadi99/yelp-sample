package com.yelp.fusion.api.model.autocomplete

import com.google.gson.annotations.SerializedName

data class AutoCompleteResponse(

	@field:SerializedName("terms")
	val terms: List<TermsItem?>? = null,

	@field:SerializedName("categories")
	val categories: List<CategoriesItem?>? = null,

	@field:SerializedName("businesses")
	val businesses: List<BusinessesItem?>? = null
)