package com.deucate.earnengine.controller

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deucate.earnengine.R
import com.deucate.earnengine.model.History
import kotlinx.android.synthetic.main.card_history.view.*

class HistoryAdapter(
    private val histories: ArrayList<History>,
    private val listener: OnClickListener
) :
    RecyclerView.Adapter<HistoryViewHolder>() {

    interface OnClickListener {
        fun onClickCard(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.card_history,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return histories.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = histories[position]

        holder.cardView.setOnClickListener {
            listener.onClickCard(position)
        }

        holder.mobileNumber.text = history.mobileNumber
        holder.amount.text = "₹ ${history.Amount}"

        holder.status.text = if (history.status) {
            "Done"
        } else {
            "pending"
        }
    }

}

class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val cardView = view.historyCard!!
    val mobileNumber = view.historyMobileNumber!!
    val amount = view.historyAmount!!
    val status = view.historyStatus!!
}