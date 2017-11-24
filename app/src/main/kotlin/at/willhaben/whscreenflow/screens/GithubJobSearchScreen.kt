package at.willhaben.whscreenflow.screens

import android.view.View
import android.widget.Toast
import at.willhaben.network.usecase.FetchGithubJobUseCase
import at.willhaben.network.usecase.FetchJobFilter
import at.willhaben.network.usecase.FetchJobResult
import at.willhaben.screenflow.ScreenFlow
import kotlinx.android.synthetic.main.screen_jobsearch.view.*
import at.willhaben.whscreenflow.R
import kotlin.reflect.KClass

/**
 * Created by panmingk on 14/11/2017.
 */
class GithubJobSearchScreen(screenFlow: ScreenFlow) : NetworkScreen(screenFlow, R.layout.screen_jobsearch) {

    private var isLoading by state(false)

    override fun afterInflate() {
        val searchButton = view.btnGithubJobSearch

        renderLoadingState()
        searchButton.setOnClickListener {
            isLoading = true
            renderLoadingState()
            callNetWorkUseCase(FetchGithubJobUseCase(parseFetchJobFilter()))
        }
    }

    private fun parseFetchJobFilter() : FetchJobFilter {
        return FetchJobFilter(
                view.etGithubJobSearchDescription.editableText.toString(),
                view.etGithubJobSearchLocation.editableText.toString(),
                view.checkboxScreenGithubJobSearch.isChecked
        )
    }

    private fun renderLoadingState() {
        view.btnGithubJobSearch.visibility = if(isLoading) View.GONE else View.VISIBLE
        view.progressBarGithubJobSearch.visibility = if(isLoading) View.VISIBLE else View.GONE
    }

    override fun onRelevantNetworkResult(useCaseClass: KClass<*>, result: Any) {
        isLoading = false
        renderLoadingState()

        if(useCaseClass == FetchGithubJobUseCase::class) {
            result as FetchJobResult

            if(result.jobs.isEmpty()) {
                onNothingFoundOrError()
            }else {
                val jobListScreen = GithubJobListScreen(screenFlow)
                jobListScreen.fetchJobResult = result
                screenFlow.goToScreen(jobListScreen, true)
            }
        }
    }

    override fun onRelevantNetworkError(useCaseClass: KClass<*>, error: Throwable) {
        isLoading = false
        renderLoadingState()
        onNothingFoundOrError()
    }

    private fun onNothingFoundOrError() = Toast.makeText(activity, "No jobs found.", Toast.LENGTH_SHORT).show()
}