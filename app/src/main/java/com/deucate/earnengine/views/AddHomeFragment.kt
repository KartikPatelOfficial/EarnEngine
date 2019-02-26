package com.deucate.earnengine.views


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.deucate.earnengine.AppsActivity
import com.deucate.earnengine.R
import kotlinx.android.synthetic.main.fragment_add_home.view.*

class AddHomeFragment : Fragment() {

    lateinit var appName: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_add_home, container, false)

        val recyclerView = rootView.recyclerViewAddHome
        recyclerView.layoutManager = LinearLayoutManager(activity)

        rootView.fabAddHome.setOnClickListener {
            AppsActivity.createAlertForUpdate(
                isHome = true,
                context = context!!,
                appName = appName
            )
        }
        return rootView
    }


}
