package com.deucate.earnengine.controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deucate.earnengine.R
import kotlinx.android.synthetic.main.card_apps.view.*

class AppsAdapter(private val appNames: ArrayList<String>, private val listener: OnClickListener) :
    RecyclerView.Adapter<AppsViewHolder>() {

    interface OnClickListener {
        fun onClickCard(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppsViewHolder {
        return AppsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.card_apps,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return appNames.size
    }

    override fun onBindViewHolder(holder: AppsViewHolder, position: Int) {
        holder.title.text = appNames[position]

        holder.cardView.setOnClickListener {
            listener.onClickCard(position)
        }
    }

}

class AppsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val cardView = view.cardAppsCardView!!
    val title = view.cardAppsTitle!!
}