package at.willhaben.network.storage

import java.util.*
import kotlin.reflect.KClass

/**
 * Created by mingkangpan on 26/12/2016.
 */
object NetworkResultStorage {

    private val storageMap = HashMap<StorageKey, NetWorkResult>()

    @Synchronized
    fun storeResult(storageKey: StorageKey, result: NetWorkResult) {
        storageMap.put(storageKey, result)
    }

    @Synchronized
    fun getAvailableResultFromCallerClassAndInvalidate(callerClass: KClass<*>): List<Map.Entry<StorageKey, NetWorkResult>> {
        val results = ArrayList<Map.Entry<StorageKey, NetWorkResult>>()
        val iterator = storageMap.iterator()
        while(iterator.hasNext()) {
            val entry = iterator.next()
            if(entry.key.callerClass == callerClass) {
                results.add(entry)
                iterator.remove()
            }
        }

        return results
    }

    @Synchronized
    fun invalidateAllCallerClassResults(callerClass: KClass<*>) {
        val iterator = storageMap.iterator()
        while(iterator.hasNext()) {
            val entry = iterator.next()
            if(entry.key.callerClass == callerClass) {
                iterator.remove()
            }
        }
    }

    @Synchronized
    fun invalidateResult(storageKey: StorageKey) {
        storageMap.remove(storageKey)
    }
}

data class NetWorkResult(val result : Any?, val throwable: Throwable?)

data class StorageKey(val callerClass: KClass<*>, val useCaseClass: KClass<*>)