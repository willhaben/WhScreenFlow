package at.willhaben.screenflow

import android.app.Activity
import android.os.Bundle
import android.widget.FrameLayout

/**
 * Created by panmingk on 16/08/2017.
 */
interface ScreenFlow {

	val activity: Activity
	val backStack: ScreenFlowBackStack
	var currentScreen: Screen?
	val contentArea: FrameLayout
	var isBetweenOnResumeAndOnPause: Boolean

	fun goToScreen(screenState: ScreenState, saveBackStack: Boolean = false, goBackArguments: Bundle? = null) {
		val newScreen = screenState.clazz.getConstructor(ScreenFlow::class.java).newInstance(this)
		val state = screenState.state
		if (state != null) {
			newScreen.restoreState(state)
		}
		if (goBackArguments != null)
			newScreen.initArguments = goBackArguments

		goToScreen(newScreen, saveBackStack)
	}

	fun goToScreen(screen: Screen, saveBackStack: Boolean = false) {
		val nowScreen = currentScreen
		if(nowScreen != null) {
			if (saveBackStack) {
				backStack.saveScreen(nowScreen)
			}
			nowScreen.onPause()
			nowScreen.onScreenBecomesIrrelevant()
		}

		screen.inflate(contentArea)
		val newLayout = screen.view
		contentArea.removeAllViews()
		contentArea.addView(newLayout)

		this.currentScreen = screen
		if (isBetweenOnResumeAndOnPause) {
			screen.onResume()
		}
	}

	fun goToPreviousScreen(doTag: Boolean = true, goBackArguments: Bundle? = null): Boolean {
		backStack.popPrevScreenState()?.let {
			goToScreen(it)
			return true
		}
		return false
	}

	fun onBackButtonPressed(goBackArguments: Bundle? = null) {
		if (currentScreen?.handleBackButton() == true) {
			return
		} else if (!goToPreviousScreen(goBackArguments = goBackArguments)) {
			exit()
		}
	}

	fun exit()
}