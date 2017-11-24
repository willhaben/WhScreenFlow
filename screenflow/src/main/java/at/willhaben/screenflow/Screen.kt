package at.willhaben.screenflow

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import at.willhaben.screenflow.state.StateBundleBinderBase
import at.willhaben.screenflow.state.StatePersistenceTrait

/**
 * Created by panmingk on 16/08/2017.
 */
abstract class Screen(protected val screenFlow: ScreenFlow, private val layoutId : Int) : StatePersistenceTrait {

	override var stateBundle: Bundle = Bundle()
	override val stateVariables: MutableMap<String, StateBundleBinderBase<*>> = HashMap()

	var initArguments : Bundle? = null

	lateinit var view : View
		private set

	val activity = screenFlow.activity
	private var viewState : SparseArray<Parcelable> by state(SparseArray())

	fun inflate(parent : ViewGroup) {
		view = LayoutInflater.from(activity).inflate(layoutId, parent, false)
		beforeRestoreViewState()
		view.restoreHierarchyState(viewState)
		afterInflate()
	}

	override fun onSaveState() {
		view.saveHierarchyState(viewState)
	}

	open fun beforeRestoreViewState() {

	}

	protected abstract fun afterInflate()

	open fun onResume() {

	}

	open fun onPause() {

	}

	open fun onScreenBecomesIrrelevant() {

	}

	open fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

	}

	open fun handleBackButton(): Boolean = false

	override fun gatherState() { }
}
