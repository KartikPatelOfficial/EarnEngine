package com.deucate.earnengine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.deucate.earnengine.controller.HistoryAdapter
import com.deucate.earnengine.model.History
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : AppCompatActivity() {

    private lateinit var db: DocumentReference

    private val histories = ArrayList<History>()
    private lateinit var adapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val appName = intent.getStringExtra("AppName")
        db = FirebaseFirestore.getInstance().collection("Apps")
            .document(appName)

        adapter = HistoryAdapter(
            histories = histories,
            listener = object : HistoryAdapter.OnClickListener {
                override fun onClickCard(position: Int) {


                    val data = HashMap<String, Any>()
                    data["Status"] = !histories[position].status

                    db.collection(getString(R.string.withdrawel)).document(histories[position].id)
                        .update(data).addOnCompleteListener {
                        if (it.isSuccessful) {
                            histories[position].status = !histories[position].status
                            adapter.notifyItemChanged(position)
                        } else {
                            AlertDialog.Builder(this@HistoryActivity).setTitle("Error")
                                .setMessage(it.exception!!.localizedMessage).show()
                        }
                    }
                }
            }
        )

        val recyclerView = historyRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        db.collection(getString(R.string.withdrawel)).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val result = it.result!!
                if (!result.isEmpty) {
                    for (history in result) {
                        histories.add(
                            History(
                                id = history.id,
                                mobileNumber = history.getString("MobileNumber")!!,
                                status = history.getBoolean("Status")!!,
                                Amount = history.getLong("Amount")!!
                            )
                        )
                    }
                    adapter.notifyDataSetChanged()
                }
            } else {
                AlertDialog.Builder(this).setTitle("Error")
                    .setMessage(it.exception!!.localizedMessage).show()
            }
        }

    }
}
