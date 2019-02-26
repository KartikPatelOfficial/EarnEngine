package com.deucate.earnengine.views


import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.deucate.earnengine.AppsActivity
import com.deucate.earnengine.R
import com.deucate.earnengine.controller.MainAdapter
import com.deucate.earnengine.model.Home
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_telegram.view.*

class TelegramFragment : Fragment() {

    lateinit var appName: String

    private val data = ArrayList<Home>()
    private lateinit var adapter: MainAdapter

    private lateinit var db: CollectionReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_telegram, container, false)

        db = FirebaseFirestore.getInstance().collection("Apps").document(appName)
            .collection("Telegram")

        adapter = MainAdapter(data, object : MainAdapter.OnClickHomeCard {
            override fun onClickRemove(id: String, position: Int) {
                db.document(id).delete().addOnCompleteListener {
                    adapter.notifyItemRemoved(position)
                    data.removeAt(position)
                }
            }
        })

        val recyclerView = rootView.recyclerViewTelegram
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter

        db.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val result = it.result!!
                if (!result.isEmpty) {
                    for (doc in result) {
                        data.add(
                            Home(
                                ID = doc.id,
                                Title = doc.getString("Title")!!,
                                Time = doc.getString("Time")!!,
                                Link = doc.getString("Link")!!
                            )
                        )
                    }
                    adapter.notifyDataSetChanged()
                }
            } else {
                AlertDialog.Builder(activity).setTitle("Error")
                    .setMessage(it.exception!!.localizedMessage).show()
            }
        }

        rootView.fabTelegram.setOnClickListener {
            AppsActivity.createAlertForUpdate(
                isHome = false,
                context = context!!,
                appName = appName
            )
        }

        return rootView
    }


}
