package com.deucate.earnengine.controller

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deucate.earnengine.R
import com.deucate.earnengine.model.Home
import kotlinx.android.synthetic.main.card_home.view.*

class MainAdapter(
    private val data: ArrayList<Home>,
    private val listener: OnClickHomeCard
) :
    RecyclerView.Adapter<HomeViewHolder>() {

    private val colors =
        arrayOf("#F44336", "#E91E63", "#9C27B0", "#3F51B5", "#4CAF50", "#FF5722", "#795548")
    private var currentColor = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(
            LayoutInflater.from(parent.context!!).inflate(
                R.layout.card_home,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val home = data[position]

        holder.cardView.setCardBackgroundColor(Color.parseColor(colors[currentColor]))
        currentColor += 1
        if (currentColor >= colors.size) {
            currentColor = 0
        }

        holder.titleTv.text = home.Title
        holder.dateTv.text = home.Time
        holder.linkTv.text = home.Link

        holder.deleteBtn.setOnClickListener {
            listener.onClickRemove(home.ID,position)
        }

    }

    interface OnClickHomeCard {
        fun onClickRemove(id: String, position: Int)
    }

}

class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val deleteBtn = view.cardHomeDelete!!
    val cardView = view.cardHomeCardView!!
    val titleTv = view.cardHomeTitle!!
    val dateTv = view.cardHomeDate!!
    val linkTv = view.cardHomeLink!!
}