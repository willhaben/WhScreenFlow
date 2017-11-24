package at.willhaben.whscreenflow

import android.widget.FrameLayout
import at.willhaben.screenflow.Screen
import at.willhaben.screenflow.ScreenFlowActivity
import kotlinx.android.synthetic.main.screenflow.*
import at.willhaben.whscreenflow.screens.GithubJobSearchScreen

/**
 * Created by panmingk on 18/08/2017.
 */
class MainActivity : ScreenFlowActivity() {

	override fun getLayoutId(): Int = R.layout.screenflow

	override fun provideContentFrameForScreenFlow(): FrameLayout = screenflow_content

	override fun getInitialScreenClass(): Class<out Screen> = GithubJobSearchScreen::class.java

	override fun setDataForInitialScreen(screen: Screen) {

	}

	override fun exit() {
		finish()
	}
}