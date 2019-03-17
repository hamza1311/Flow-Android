package com.flow.flowforreddit.adapters

import android.content.Context
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.flow.flowforreddit.R
import kotlinx.android.synthetic.main.list_front_page.view.*
import net.dean.jraw.models.Submission

class FrontPageAdapter(private val context: Context, private val submissions: ArrayList<Submission>) :
    RecyclerView.Adapter<FrontPageAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_front_page, null)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return submissions.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bindViews(submissions[position])
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var subredditName: TextView
        private lateinit var username: TextView
        private lateinit var title: TextView
        private lateinit var text: TextView
        private lateinit var upvoteButton:ImageButton
        private lateinit var downvoteButton:ImageButton
        private lateinit var commentsButton:ImageView
        private lateinit var shareButton:ImageView

        fun bindViews(submission: Submission) {
            subredditName = itemView.listFrontRow_subredditName_textView
            username = itemView.listFrontRow_username_textView
            title = itemView.listFrontRow_title_textView
            text = itemView.listFrontRow_text_textView
            upvoteButton = itemView.listFrontRow_upvote_imageButton
            downvoteButton = itemView.listFrontRow_downvote_imageButton
            commentsButton = itemView.listFrontRow_comments_button
            shareButton = itemView.listFrontRow_share_button

            subredditName.text = "r/${submission.subreddit}"
            username.text = "u/${submission.author}"
            title.text = submission.title
            text.text = submission.selfText

            upvoteButton.setOnClickListener {
//                TODO: Upvote
                Snackbar.make(it, "Gave upd00t", Snackbar.LENGTH_SHORT).show()
            }

            downvoteButton.setOnClickListener {
//                TODO: Downvote
                Snackbar.make(it, "Gave upd00t'nt", Snackbar.LENGTH_SHORT).show()
            }

            commentsButton.setOnClickListener {
//                TODO: Start next activity
                Snackbar.make(it, "Starting Activity", Snackbar.LENGTH_SHORT).show()
            }

            shareButton.setOnClickListener {
//                TODO: share
                Snackbar.make(it, "share", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}