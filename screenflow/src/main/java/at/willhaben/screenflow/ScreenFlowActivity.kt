package at.willhaben.screenflow

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import android.widget.FrameLayout
import at.willhaben.screenflow.state.CompressedBundle

abstract class ScreenFlowActivity : AppCompatActivity(), ScreenFlow {

	override val activity: Activity
		get() = this

	override var currentScreen: Screen? = null
	override val contentArea: FrameLayout by lazy { provideContentFrameForScreenFlow() }
	override var isBetweenOnResumeAndOnPause: Boolean = false
	override lateinit var backStack: ScreenFlowBackStack

	private lateinit var screenFlowState : ScreenFlowState

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(getLayoutId())

		val bundle = savedInstanceState?.getParcelable<CompressedBundle>(COMPRESSED_STATE)?.bundle
		screenFlowState = if (bundle != null && bundle.containsKey(SCREEN_FLOW_STATE)) {
			bundle.getParcelable(SCREEN_FLOW_STATE)
		} else {
            ScreenFlowState(getInitialScreenClass(), Bundle(), ScreenFlowBackStack())
		}
		backStack = screenFlowState.backStack

		val newScreen = screenFlowState.currentScreenClass.getConstructor(ScreenFlow::class.java).newInstance(this)
		if (savedInstanceState != null) {
			newScreen.restoreState(screenFlowState.currentScreenState)
		} else {
			setDataForInitialScreen(newScreen)
		}
		goToScreen(newScreen)
	}

	abstract fun getInitialScreenClass(): Class<out Screen>

	abstract fun setDataForInitialScreen(screen: Screen)

	override fun onSaveInstanceState(outState: Bundle) {
		val nowScreen = currentScreen

		if(nowScreen != null) {
			val uncompressedBundle = Bundle()
			screenFlowState.currentScreenClass = nowScreen::class.java
			nowScreen.gatherState()
			nowScreen.saveState(screenFlowState.currentScreenState)
			uncompressedBundle.putParcelable(SCREEN_FLOW_STATE, screenFlowState)
			outState.putParcelable(COMPRESSED_STATE, CompressedBundle(uncompressedBundle))
		}

		super.onSaveInstanceState(outState)
	}

	abstract fun getLayoutId(): Int

	abstract fun provideContentFrameForScreenFlow(): FrameLayout

	override fun onResume() {
		super.onResume()
		currentScreen?.onResume()
		isBetweenOnResumeAndOnPause = true
	}

	override fun onPause() {
		super.onPause()
		currentScreen?.onPause()
		isBetweenOnResumeAndOnPause = false
	}

	override fun onStop() {
		super.onStop()

		if(!isChangingConfigurations) {
			currentScreen?.onScreenBecomesIrrelevant()
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		currentScreen?.onActivityResult(requestCode, resultCode, data)
	}

	override fun onBackPressed() {
		onBackButtonPressed()
	}

	companion object {
		private const val SCREEN_FLOW_STATE = "SCREEN_FLOW_STATE"
		private const val COMPRESSED_STATE = "COMPRESSED_STATE"
	}

	data class ScreenFlowState(var currentScreenClass: Class<out Screen>, val currentScreenState: Bundle, val backStack: ScreenFlowBackStack) : Parcelable {
		companion object {
			@JvmField val CREATOR: Parcelable.Creator<ScreenFlowState> = object : Parcelable.Creator<ScreenFlowState> {
				override fun createFromParcel(source: Parcel): ScreenFlowState = ScreenFlowState(source)
				override fun newArray(size: Int): Array<ScreenFlowState?> = arrayOfNulls(size)
			}
		}

		constructor(source: Parcel) : this(source.readSerializable() as Class<out Screen>, source.readParcelable<Bundle>(Bundle::class.java.classLoader), source.readParcelable<ScreenFlowBackStack>(ScreenFlowBackStack::class.java.classLoader))

		override fun describeContents() = 0

		override fun writeToParcel(dest: Parcel?, flags: Int) {
			dest?.writeSerializable(currentScreenClass)
			dest?.writeParcelable(currentScreenState, 0)
			dest?.writeParcelable(backStack, 0)
		}
	}
}
