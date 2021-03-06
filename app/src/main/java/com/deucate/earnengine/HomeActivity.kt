package com.deucate.earnengine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.deucate.earnengine.controller.AppsAdapter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val apps = ArrayList<String>()

    private lateinit var adapter: AppsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        adapter = AppsAdapter(apps, object : AppsAdapter.OnClickListener {
            override fun onClickCard(position: Int) {
                val intent = Intent(this@HomeActivity, AppsActivity::class.java)
                intent.putExtra("AppName", apps[position])
                startActivity(intent)
            }
        })

        homeRecyclerView.adapter = adapter
        homeRecyclerView.layoutManager = LinearLayoutManager(this)

        db.collection("Apps").get().addOnCompleteListener {
            if (it.isSuccessful) {
                val result = it.result!!
                if (!result.isEmpty) {
                    for (title in result) {
                        apps.add(title.id)
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    notifyNoAppsFound("Data not found create from scratch.")
                }
            } else {
                notifyNoAppsFound(it.exception!!.localizedMessage!!)
            }
        }
    }

    private fun notifyNoAppsFound(localizedMessage: String?) {
        AlertDialog.Builder(this).setTitle("Error").setMessage(localizedMessage).show()
    }

}
