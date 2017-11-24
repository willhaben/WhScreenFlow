package at.willhaben.whscreenflow.screens

import android.text.Html
import android.text.method.LinkMovementMethod
import com.squareup.picasso.Picasso
import at.willhaben.network.model.Job
import at.willhaben.screenflow.Screen
import at.willhaben.screenflow.ScreenFlow
import kotlinx.android.synthetic.main.screen_job.view.*
import at.willhaben.whscreenflow.R
import android.content.Intent
import android.net.Uri


/**
 * Created by panmingk on 14/11/2017.
 */
class GithubJobScreen(screenFlow: ScreenFlow) : Screen(screenFlow, R.layout.screen_job) {

    var job by lateInitState<Job>()

    override fun afterInflate() {
        view.tvJobTypeLocation.text = "${job.type} / ${job.location}"
        view.tvJobTitle.text = job.title
        view.tvJobDescription.text = Html.fromHtml(job.description)

        view.tvJobCompanyName.text = job.company

        if(!job.companyLogo.isNullOrBlank()) {
            Picasso.with(activity).load(job.companyLogo).into(view.imageViewJobCompanyLogo)
        }

        if(!job.companyUrl.isNullOrBlank()) {
            view.tvJobUrl.text = job.companyUrl
            view.tvJobUrl.setOnClickListener {
                activity.startActivity(Intent(Intent.ACTION_VIEW).also { it.data = Uri.parse(job.companyUrl) })
            }
        }

        view.tvJobApply.movementMethod = LinkMovementMethod.getInstance()
        view.tvJobApply.text = Html.fromHtml(job.howToApply)
    }
}