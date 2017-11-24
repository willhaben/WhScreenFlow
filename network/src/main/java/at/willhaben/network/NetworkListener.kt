package at.willhaben.network

import kotlin.reflect.KClass

/**
 * Created by mingkangpan on 26/12/2016.
 */
interface NetworkListener{
    fun onResult(callerClass: KClass<*>, useCaseClass: KClass<*>, result : Any)
    fun onError(callerClass: KClass<*>, useCaseClass: KClass<*>, error: Throwable)
}