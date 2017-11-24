package at.willhaben.whscreenflow

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.View
import android.view.ViewGroup

/**
 * Created by mingkangpan on 19/09/2017.
 */
fun getDensityPixels(context: Context, dp: Int): Int {
	val displayMetrics = context.resources.displayMetrics
	return Math.round(dp * displayMetrics.density)
}

inline fun <reified T : View>ViewGroup.getAllViews() : List<T>{
	return (0..childCount)
			.map { getChildAt(it) }
			.filterIsInstance<T>()
}

inline fun SpannableStringBuilder.withSpan(span: Any, action: SpannableStringBuilder.() -> Unit): SpannableStringBuilder {
	val from = length
	action()
	setSpan(span, from, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
	return this
}