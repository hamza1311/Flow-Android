package com.flow.flowforreddit.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.flow.flowforreddit.App
import com.flow.flowforreddit.R

class FrontPageFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_front_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Toast.makeText(context, "Front Page Fragment", Toast.LENGTH_LONG).show()
    }

    override fun onStart() {
        super.onStart()
        Thread(Runnable {
            val reddit = App().getAccountHelper().reddit
            val username = reddit.me().username
            Log.d("username", "uuuu: $username")
        }).start()
    }
/*
    fun getFrontPage(limit: Int, sorting: SubredditSort, timePeriod: TimePeriod) : Listing<Submission>? {
        var list: Listing<Submission>? = null
        val frontPageThread = Thread(Runnable {
            list = redditClient.frontPage()
                .limit(limit)
                .timePeriod(timePeriod)
                .sorting(sorting)
                .build()
                .next()
        })
        frontPageThread.start()
        frontPageThread.join()
        return list
    }
*/
    /* fun userlessMode() {
         val app = App()
         val thread = Thread(Runnable {
             val list = app.getAccountHelper().switchToUserless()
                 .subreddit("teenagers")
                 .posts()
                 .limit(10).sorting(SubredditSort.NEW)
                 .build()
                 .next()
             list.forEach {
                 Log.d("title:", "ttt: ${it.title}")
             }
         })
         thread.start()
     }*/
}
