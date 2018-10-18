package at.willhaben.whscreenflow.screens

import androidx.annotation.CallSuper
import at.willhaben.network.NetworkManager
import at.willhaben.network.NetworkTrait
import at.willhaben.network.storage.NetworkResultStorage
import at.willhaben.screenflow.Screen
import at.willhaben.screenflow.ScreenFlow

/**
 * Created by panmingk on 14/11/2017.
 */
abstract class NetworkScreen(screenFlow: ScreenFlow, layoutId : Int) : Screen(screenFlow, layoutId), NetworkTrait {

    override val networkManager: NetworkManager = NetworkManager

    override val networkResultStorage: NetworkResultStorage = NetworkResultStorage

    @CallSuper
    override fun onResume() {
        registerToNetWork()
    }

    @CallSuper
    override fun onPause() {
        unregisterFromNetwork()
    }

    @CallSuper
    override fun onScreenBecomesIrrelevant() {
        cancelAllPendingRequests()
    }
}