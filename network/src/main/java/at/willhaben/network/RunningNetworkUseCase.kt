package at.willhaben.network

import io.reactivex.disposables.Disposable
import java.util.*
import kotlin.reflect.KClass

/**
 * Created by panmingk on 17/01/2017.
 */
data class RunningNetworkUseCase(val isCancelable : Boolean, val disposable: Disposable, var storeResult : Boolean = true)

data class RunningUseCaseKey(val callerClass: KClass<*>, val uuid : UUID)