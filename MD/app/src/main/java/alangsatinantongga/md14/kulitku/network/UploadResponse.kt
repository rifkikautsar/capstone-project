package alangsatinantongga.md14.kulitku.network

import com.google.gson.annotations.SerializedName

data class UploadResponse(

	@field:SerializedName("code")
	val code: Int,

	@field:SerializedName("data")
	val data: Data
)

data class Data(

	@field:SerializedName("result")
	val result: Result,

	@field:SerializedName("message")
	val message: String
)

data class Result(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("class")
	val jsonMemberClass: String
)
