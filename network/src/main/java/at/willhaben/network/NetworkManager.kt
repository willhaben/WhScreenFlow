package at.willhaben.network

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import at.willhaben.network.storage.NetWorkResult
import at.willhaben.network.storage.NetworkResultStorage
import at.willhaben.network.storage.StorageKey
import at.willhaben.whscreenflow.network.usecase.NetworkUseCase
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * Created by panmingk on 23/12/2016.
 */
object NetworkManager {

    private val networkListeners = Collections.synchronizedCollection(ArrayList<NetworkListener>())
    private val runningNetWorkUseCasesMap = ConcurrentHashMap<RunningUseCaseKey, RunningNetworkUseCase>()

    fun addNetworkListener(networkListener: NetworkListener) {
        if (networkListeners.contains(networkListener))
            return

        networkListeners.add(networkListener)
    }

    fun removeNetworkListener(networkListener: NetworkListener) {
        networkListeners.remove(networkListener)
    }

    fun <R : Any> processRequest(networkUseCase: NetworkUseCase<R>, callerClass: KClass<*>) {
        val observable = Observable.create<R> { e ->
                        val result = networkUseCase.process()
                        e.onNext(result)
                        e.onComplete()
                }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

        val runningUseCaseKey = RunningUseCaseKey(callerClass, UUID.randomUUID())
        val disposable = observable.subscribe({ r ->
            val runningNetWorkUseCase = runningNetWorkUseCasesMap[runningUseCaseKey]!!
            if (runningNetWorkUseCase.storeResult) {
                NetworkResultStorage.storeResult(StorageKey(callerClass, networkUseCase::class), NetWorkResult(r, null))
            }

            runningNetWorkUseCasesMap.remove(runningUseCaseKey)
            saveIterateThroughListeners { it.onResult(callerClass, networkUseCase::class, r) }
        }, { e ->
			NetworkResultStorage.storeResult(StorageKey(callerClass, networkUseCase::class), NetWorkResult(null, e as Exception))
            saveIterateThroughListeners { it.onError(callerClass, networkUseCase::class, e) }
        })

        runningNetWorkUseCasesMap.put(runningUseCaseKey, RunningNetworkUseCase(networkUseCase.isCancelable(), disposable))
    }

    private fun saveIterateThroughListeners(func: (listener: NetworkListener) -> Unit) {
        val copy = ArrayList(networkListeners)
        copy.forEach { l ->
            if (networkListeners.contains(l))
                func.invoke(l)
        }
    }

    fun cancelAllRequestFromCallerClass(callerClass: KClass<*>) {
        runningNetWorkUseCasesMap.keys.forEach { k ->
            if (callerClass == k.callerClass) {
                val runningNetWorkUseCase = runningNetWorkUseCasesMap[k]!!
                if (runningNetWorkUseCase.isCancelable) {
                    runningNetWorkUseCase.disposable.dispose()
                } else {
                    runningNetWorkUseCase.storeResult = false
                }
            }
        }
    }
}