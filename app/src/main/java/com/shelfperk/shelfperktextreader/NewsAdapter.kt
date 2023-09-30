package com.shelfperk.shelfperktextreader

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shelfperk.shelfperktextreader.model.Articles
import com.shelfperk.shelfperktextreader.model.NewsResponse

class NewsAdapter(newsResponse: NewsResponse?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var articles: List<Articles>? = null

    init {
        articles = newsResponse?.articles
    }

    private class NewsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titleTextView: TextView? = null

        init {
            init(itemView)
        }

        private fun init(view: View) {
            titleTextView = view.findViewById(R.id.titleTextView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.article_item, parent, false)
        return NewsHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val thisHolder = holder as NewsHolder
        thisHolder.titleTextView!!.text = articles!![position].title
    }

    override fun getItemCount(): Int {
        return articles?.size ?: 0
    }
}