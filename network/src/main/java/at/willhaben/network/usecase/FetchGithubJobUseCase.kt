package at.willhaben.network.usecase

import okhttp3.Request
import com.google.gson.reflect.TypeToken
import at.willhaben.network.model.Job
import okhttp3.HttpUrl
import at.willhaben.whscreenflow.network.usecase.BaseNetworkUseCase
import java.io.Serializable

/**
 * Created by panmingk on 18/08/2017.
 */
class FetchGithubJobUseCase(private val fetchJobFilter : FetchJobFilter) : BaseNetworkUseCase<FetchJobResult>() {

	override fun process(): FetchJobResult {

		val urlBuilder = HttpUrl.parse(BASE_URL)!!.newBuilder()
		val url = urlBuilder.addPathSegment("positions.json")
				.addQueryParameter("description",fetchJobFilter.jobDescription)
				.addQueryParameter("location",fetchJobFilter.jobLocation)
				.addQueryParameter("full_time",fetchJobFilter.onlyFullTime.toString())
				.build()

		val request = Request.Builder()
				.header("Accept", "application/json")
				.url(url)
				.build()

		val response = okHttpClient.newCall(request).execute()

		try {
			return FetchJobResult(fetchJobFilter, gson.fromJson<List<Job>>(response.body()?.string(), object : TypeToken<List<Job>>() {}.type))
		} finally {
			response.body()?.close()
		}
	}

	override fun isCancelable(): Boolean = true
}

class FetchJobFilter(val jobDescription: String, val jobLocation : String, val onlyFullTime: Boolean) : Serializable

class FetchJobResult(val filter : FetchJobFilter, val jobs : List<Job>) : Serializable