package at.willhaben.whscreenflow.screens

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import at.willhaben.network.model.Job
import at.willhaben.network.usecase.FetchJobResult
import at.willhaben.screenflow.Screen
import at.willhaben.screenflow.ScreenFlow
import kotlinx.android.synthetic.main.job_list_item.view.*
import kotlinx.android.synthetic.main.screen_joblist.view.*
import at.willhaben.whscreenflow.R
import at.willhaben.whscreenflow.withSpan

/**
 * Created by panmingk on 14/11/2017.
 */
class GithubJobListScreen(screenFlow: ScreenFlow) : Screen(screenFlow, R.layout.screen_joblist) {

    var fetchJobResult by lateInitState<FetchJobResult>()

    override fun afterInflate() {
        val size = fetchJobResult.jobs.size
        view.toolbarScreenGithubJobList.title = "$size ${if(size == 1) "job" else "jobs"} found"

        val recyclerView = view.listViewJobList
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity).apply { orientation = LinearLayoutManager.VERTICAL }
        val adapter = JobListAdapter(activity, fetchJobResult.jobs)
        recyclerView.adapter = adapter

        adapter.onItemChooseListener = { job ->
            val jobScreen = GithubJobScreen(screenFlow)
            jobScreen.job = job
            screenFlow.goToScreen(jobScreen, true)
        }
    }

    class JobListAdapter(private val context : Context, private val jobs: List<Job>) : RecyclerView.Adapter<JobListAdapter.RepoHolder>() {

        var onItemChooseListener : ((job : Job) -> Unit)? = null

        override fun onBindViewHolder(holder: RepoHolder, position: Int) {
            val job = jobs[position]
            holder.itemView.titleJobListItem.text = job.title
            holder.itemView.tvJobListItemCompanyAndType.text = SpannableStringBuilder("${job.company} - ")
                    .withSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.md_green_700))) { append(job.type) }
            holder.itemView.tvJobListItemLocation.text = job.location

            if(!job.companyLogo.isNullOrEmpty()) {
                Picasso.with(context).load(job.companyLogo).into(holder.itemView.imageJobListItem)
            }

            holder.itemView.setOnClickListener {
                onItemChooseListener?.invoke(job)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.job_list_item, parent, false)
            return RepoHolder(view)
        }

        override fun getItemCount(): Int = jobs.size

        class RepoHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
    }

}