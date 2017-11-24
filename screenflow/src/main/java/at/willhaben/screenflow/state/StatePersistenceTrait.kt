package at.willhaben.screenflow.state

import android.os.Bundle
import kotlin.reflect.KProperty

/**
 * Created by panmingk on 16/08/2017.
 */
@Suppress("UNCHECKED_CAST")
interface StatePersistenceTrait {

	var stateBundle : Bundle
	val stateVariables : MutableMap<String, StateBundleBinderBase<*>>

	fun gatherState()

	fun saveState(outBundle : Bundle) {
		onSaveState()
		// put all existing values in the outBundle to also maintain states that were not touched at all
		outBundle.putAll(stateBundle)

		// update values which were touched
		for (binder in stateVariables.filterValues { it.touched }.values) {
			val value = binder.value
			outBundle.put(binder.key, value)
		}
	}

	fun restoreState(inBundle : Bundle) {
		stateBundle = inBundle
		for(binder in stateVariables.values) {
			binder.touched = false
		}
	}

	fun hasState() : Boolean {
		return stateBundle.keySet().size > 0
	}

	fun onSaveState()

	/***********************************************************************************************************************************************************
	 * STATE HANDLING HELPERS
	 */
	fun <T> state(defaultValue : T) = StateBundleBinder(this, defaultValue)
	fun <T> lateInitState() = LateInitStateBundleBinder<T>(this)
	fun <T> lazyState(defaultOp : () -> T) = LazyStateBundleBinder(this, defaultOp)

	class LateInitStateBundleBinder<T>(container : StatePersistenceTrait) : StateBundleBinderBase<T>(container) {

		override var value: T
			get(){
				if (internalValue == null) {
					throw UninitializedPropertyAccessException()
				}
				return internalValue as T
			}
			set(value) {
				internalValue = value
			}

		private var internalValue : T? = null
	}

	class StateBundleBinder<T>(container : StatePersistenceTrait, defaultValue : T) : StateBundleBinderBase<T>(container) {
		override var value: T = defaultValue
	}

	class LazyStateBundleBinder<T>(container : StatePersistenceTrait, private val defaultOp : () -> T) : StateBundleBinderBase<T>(container) {

		override var value: T
			get() {
				if (internalValue == null) {
					internalValue = defaultOp.invoke()
				}
				return internalValue as T
			}
			set(value) {
				internalValue = value
			}

		private var internalValue : T? = null
	}

	fun addToStateVariables(stateBinder : StateBundleBinderBase<*>) {
		val key = stateBinder.key
		if (!stateVariables.containsKey(key)) {
			stateVariables.put(key, stateBinder)
		}
	}
}

@Suppress("UNCHECKED_CAST")
abstract class StateBundleBinderBase<T>(private val container : StatePersistenceTrait) {

	abstract var value : T
	var key : String = ""
	var touched = false

	private fun updateKey(newKey : String) {
		if (key != newKey) {
			key = newKey
			if (key.isNotEmpty())
				container.addToStateVariables(this as StateBundleBinderBase<*>)
		}
	}

	operator fun getValue(thisRef: StatePersistenceTrait, property: KProperty<*>): T {
		// we need this even on a get() so Lists are also added to the state, even if their base
		// instance is never modified
		updateKey(property.name)
		return getValue(thisRef)
	}

	private fun getValue(thisRef: StatePersistenceTrait) : T {
		if (!touched && thisRef.stateBundle.containsKey(key)) {
			value = thisRef.stateBundle.get(key) as T
		}
		touched = true
		return value
	}

	operator fun setValue(thisRef: StatePersistenceTrait, property: KProperty<*>, value: T) {
		updateKey(property.name)
		touched = true
		this.value = value
	}
}
