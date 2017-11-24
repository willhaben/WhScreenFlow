package at.willhaben.screenflow

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import java.util.*

/**
 * Created by panmingk on 16/08/2017.
 */
class ScreenState : Parcelable {

	var clazz : Class<out Screen> = Screen::class.java
	var state: Bundle? = null

	override fun writeToParcel(dest: Parcel?, flags: Int) {
		dest?.writeSerializable(clazz)
		dest?.writeBundle(state)
	}

	override fun describeContents(): Int = 0

	constructor()

	constructor(clazz : Class<out Screen>, state: Bundle) {
		this.clazz = clazz
		this.state = state
	}

	private constructor(parcelIn: Parcel) {
		clazz = parcelIn.readSerializable() as Class<out Screen>
		state = parcelIn.readBundle(clazz.classLoader)
	}

	companion object {
		@JvmField val CREATOR: Parcelable.Creator<ScreenState> = object : Parcelable.Creator<ScreenState> {
			override fun createFromParcel(parcelIn: Parcel): ScreenState {
				return ScreenState(parcelIn)
			}

			override fun newArray(size: Int): Array<ScreenState?> {
				return arrayOfNulls(size)
			}
		}
	}
}

class ScreenFlowBackStack : Parcelable {

	constructor() {
		screenStack = ArrayList()
	}

	private val screenStack: ArrayList<ScreenState>

	fun saveScreen(screen: Screen) {
		val state = Bundle()
		screen.saveState(state)
		screenStack.add(ScreenState(screen::class.java, state))
	}

	fun hasPrevScreenState(): Boolean = screenStack.size > 0

	fun popPrevScreenState(): ScreenState? {
		if (hasPrevScreenState()) {
			return screenStack.removeAt(screenStack.size - 1)
		}

		return null
	}

	fun clear() {
		screenStack.clear()
	}

	companion object {
		@JvmField val CREATOR: Parcelable.Creator<ScreenFlowBackStack> = object : Parcelable.Creator<ScreenFlowBackStack> {
			override fun createFromParcel(source: Parcel): ScreenFlowBackStack = ScreenFlowBackStack(source)
			override fun newArray(size: Int): Array<ScreenFlowBackStack?> = arrayOfNulls(size)
		}
	}

	private constructor(source: Parcel) {
		screenStack = Arrays.asList(source.readArray(javaClass.classLoader)) as ArrayList<ScreenState>
	}

	override fun describeContents() = 0

	override fun writeToParcel(dest: Parcel, flags: Int) {
		dest.writeArray(screenStack.toArray())
	}
}
