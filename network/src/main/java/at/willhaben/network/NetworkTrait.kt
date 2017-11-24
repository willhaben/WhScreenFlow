package at.willhaben.network

import at.willhaben.network.storage.NetworkResultStorage
import at.willhaben.network.storage.StorageKey
import at.willhaben.whscreenflow.network.usecase.NetworkUseCase
import kotlin.reflect.KClass

/**
 * Created by mingkangpan on 19/09/2017.
 */
interface NetworkTrait : NetworkListener {

	val networkManager : NetworkManager

	val networkResultStorage : NetworkResultStorage

	fun <R : Any> callNetWorkUseCase(networkUseCase: NetworkUseCase<R>) {
		NetworkManager.processRequest(networkUseCase, this::class)
	}

	fun registerToNetWork() {
		NetworkManager.addNetworkListener(this)
		val results= networkResultStorage.getAvailableResultFromCallerClassAndInvalidate(this::class)
		results.forEach { r ->
			if(r.value.throwable != null) {
				onRelevantNetworkError(r.key.useCaseClass, r.value.throwable!!)
			}else {
				onRelevantNetworkResult(r.key.useCaseClass, r.value.result!!)
			}
			networkResultStorage.invalidateResult(StorageKey(this::class, r.key.useCaseClass))
		}
	}

	fun unregisterFromNetwork() {
		NetworkManager.removeNetworkListener(this)
	}

	fun cancelAllPendingRequests() {
		NetworkManager.cancelAllRequestFromCallerClass(this::class)
	}

	override fun onResult(callerClass: KClass<*>, useCaseClass: KClass<*>, result: Any) {
		if(isRelevantCallerUseCaseCallBack(callerClass)) {
			networkResultStorage.invalidateResult(StorageKey(this::class, useCaseClass))
			onRelevantNetworkResult(useCaseClass, result)
		}
	}

	override fun onError(callerClass: KClass<*>, useCaseClass: KClass<*>, error: Throwable) {
		if(isRelevantCallerUseCaseCallBack(callerClass)) {
			networkResultStorage.invalidateResult(StorageKey(this::class, useCaseClass))
			onRelevantNetworkError(useCaseClass, error)
		}
	}

	fun onRelevantNetworkResult(useCaseClass: KClass<*>, result: Any)

	fun onRelevantNetworkError(useCaseClass: KClass<*>, error: Throwable)

	private fun isRelevantCallerUseCaseCallBack(callerClazz: KClass<*>) : Boolean{
		return this::class == callerClazz
	}
}