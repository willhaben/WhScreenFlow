package at.willhaben.whscreenflow.network.usecase

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * Created by panmingk on 18/08/2017.
 */
interface NetworkUseCase<out R> {
	fun process() : R
	fun isCancelable() : Boolean
}

abstract class BaseNetworkUseCase<out R>(open val iBoundOnCallerClientLifeCycle: Boolean = true) : NetworkUseCase<R>{

	protected val okHttpClient : OkHttpClient by lazy {
		val logging = HttpLoggingInterceptor()
		logging.level = HttpLoggingInterceptor.Level.BODY

		val builder = OkHttpClient.Builder()
				.addInterceptor(logging)
				.readTimeout(60, TimeUnit.SECONDS)
				.connectTimeout(60, TimeUnit.SECONDS)

		builder.build()
	}

	protected val gson = Gson()


	protected inline fun <reified T : Any> parseResponse(response: Response): T {
		try {
			return gson.fromJson(response.body()?.string(), T::class.java)
		} finally {
			response.body()?.close()
		}
	}

	companion object {
		const val BASE_URL = "https://jobs.github.com/"
	}
}
