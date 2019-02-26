package com.deucate.earnengine.views


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.deucate.earnengine.AppsActivity
import com.deucate.earnengine.R
import kotlinx.android.synthetic.main.fragment_telegram.view.*

class TelegramFragment : Fragment() {

    lateinit var appName:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_telegram, container, false)

        val recyclerView = rootView.recyclerViewTelegram
        recyclerView.layoutManager = LinearLayoutManager(activity)

        rootView.fabTelegram.setOnClickListener {
            AppsActivity.createAlertForUpdate(isHome = false, context = context!!,appName = appName)
        }

        return rootView
    }


}
