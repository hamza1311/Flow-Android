package com.flow.flowforreddit.fragments

import android.os.Bundle
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
            frontPageFragment_recyclerView.context,
            layoutManager.orientation
        )
        dividerItemDecoration.setDrawable(
            ContextCompat.getDrawable(
                context!!,
                R.drawable.recycler_view_decoration_spacing
            )!!
        )
        frontPageFragment_recyclerView.addItemDecoration(dividerItemDecoration)


        Toast.makeText(context, "Front Page Fragment", Toast.LENGTH_LONG).show()
        getPosts()
    }

    fun getPosts() {
//        TODO: Change r/teenagers to frontpage and allow user to select the sorting
//        TODO: Probably should put this in an Async task and display a loading indicator
//        TODO: Basically needs to fix the way posts are fetched
        val thread = Thread(Runnable {
            val reddit = accountHelper.reddit
            val username = reddit.me().username
            Log.d("username", "uuuu: $username")

            reddit.subreddit("teenagers").posts()
                .sorting(SubredditSort.NEW)
                .limit(10)
                .build()
                .next()
                .forEach {
                    Log.d("title", "ttt: ${it.title}")
                    submissions.add(it)
                }
        })

        thread.apply {
            start()
            join()
        }
        adapter.notifyDataSetChanged()
    }
}
