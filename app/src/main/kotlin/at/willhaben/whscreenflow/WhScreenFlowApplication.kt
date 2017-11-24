package at.willhaben.whscreenflow

import android.app.Application
import at.willhaben.network.Network

/**
 * Created by panmingk on 20/09/2017.
 */
class WhScreenFlowApplication : Application() {

	override fun onCreate() {
		super.onCreate()
		Network.setUp()
	}
}