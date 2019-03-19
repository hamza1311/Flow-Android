package com.flow.flowforreddit.fragments

import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.flow.flowforreddit.App
import com.flow.flowforreddit.R
import com.flow.flowforreddit.adapters.FrontPageAdapter
import kotlinx.android.synthetic.main.fragment_front_page.*
import net.dean.jraw.models.Submission
import net.dean.jraw.models.SubredditSort
import net.dean.jraw.oauth.AccountHelper
import android.support.v7.widget.DividerItemDecoration
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.flow.flowforreddit.App.Companion.accountHelper
import net.dean.jraw.models.TimePeriod
import java.lang.Exception

class FrontPageFragment : Fragment() {
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: FrontPageAdapter
    private lateinit var accountHelper: AccountHelper
    private val submissions: ArrayList<Submission> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_front_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        accountHelper = App().getAccountHelper()
        layoutManager = LinearLayoutManager(context)
        adapter = FrontPageAdapter(context!!, submissions)

        frontPageFragment_recyclerView.adapter = adapter
        frontPageFragment_recyclerView.layoutManager = layoutManager

        val dividerItemDecoration = DividerItemDecoration(
            frontPageFragment_fab.context,
            layoutManager.orientation
        )
        dividerItemDecoration.setDrawable(
            ContextCompat.getDrawable(
                context!!,
                R.drawable.recycler_view_decoration_spacing
            )!!
        )
        frontPageFragment_recyclerView.addItemDecoration(dividerItemDecoration)

        frontPageFragment_fab.setOnClickListener {
            Snackbar.make(it, "New Post", Snackbar.LENGTH_SHORT).show()
//            TODO: Make a new post
        }
        val sorting = listOf(
            SubredditSort.NEW,
            SubredditSort.BEST,
            SubredditSort.CONTROVERSIAL,
            SubredditSort.HOT,
            SubredditSort.RISING,
            SubredditSort.TOP
        )

        frontPageFragment_sort_spinner.adapter =
                ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, sorting)

        frontPageFragment_sort_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                getPosts(sorting[position])
                GetPostsTask(
                    sort = sorting[position],
                    fragment = this@FrontPageFragment,
                    isSortingChanged = true
                ).execute()
            }
        }

        Toast.makeText(context, "Front Page Fragment", Toast.LENGTH_LONG).show()
    }
/*
    Function no longer being used but still sitting here
    private fun getPosts(sort: SubredditSort, timePeriod: TimePeriod = TimePeriod.HOUR) {
        val thread = Thread(Runnable {
            val reddit = accountHelper.reddit
            val username = reddit.me().username
            Log.d("username", "uuuu: $username")
            if (!sort.requiresTimePeriod)
                reddit.subreddit("teenagers").posts().sorting(sort).limit(10).build().next().forEach {
                    submissions.add(it)
                }
            else
                reddit.subreddit("teenagers").posts().sorting(sort).timePeriod(timePeriod).limit(10).build().next().forEach {
                    submissions.add(it)
                }
        })

        thread.apply {
            start()
            join()
        }
        adapter.notifyDataSetChanged()
    }
*/

    class GetPostsTask(
        private val sort: SubredditSort,
        private val timePeriod: TimePeriod = TimePeriod.HOUR,
        private val fragment: FrontPageFragment,
        private val isSortingChanged: Boolean
    ) :
        AsyncTask<Unit, Unit, Unit>() {
        override fun onPreExecute() {
            super.onPreExecute()
//            TODO: Show a loading indicator
            Log.d("Loading", "uuu start")
        }

        override fun doInBackground(vararg params: Unit?) {
            try {
                val reddit = accountHelper.reddit
                val username = reddit.me().username
                Log.d("username", "uuuu: $username")
                Log.d("isSortingChanged", "uuuu: $isSortingChanged")
                Log.d("requiresTimePeriod", "uuuu: ${sort.requiresTimePeriod}")
                Log.d("sort", "uuuu: ${sort}")
                if (isSortingChanged)
                    fragment.submissions.removeAll(fragment.submissions)
                if (!sort.requiresTimePeriod)
                    reddit.subreddit("teenagers").posts().sorting(sort).limit(10).build().next().forEach {
                        fragment.submissions.add(it)
                    }
                else
                    reddit.subreddit("teenagers").posts().sorting(sort).timePeriod(timePeriod).limit(10).build().next()
                        .forEach {
                            fragment.submissions.add(it)
                        }

            } catch (e: Exception) {
                Log.d("Error", "eee: $e")
            }
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            fragment.adapter.notifyDataSetChanged()
            Log.d("Loading", "uuu done")
        }
    }
}
