package com.deucate.earnengine

import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import kotlinx.android.synthetic.main.activity_apps.*
import kotlinx.android.synthetic.main.app_bar_apps.*
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.deucate.earnengine.model.ImportantData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.alert_update_details.view.*
import java.text.SimpleDateFormat
import java.util.*

class AppsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var importantValueDB: DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apps)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        importantValueDB = FirebaseFirestore.getInstance().collection("Apps")
            .document(intent.getStringExtra("AppName")).collection(getString(R.string.important))
            .document(getString(R.string.value))

        getData()

        //adding watchers to all edit text
        addTextWatcher(mainBannerID, "BannerID")
        addTextWatcher(mainImpressionPoint, "ImpressionPoint")
        addTextWatcher(mainInterstitialID, "InterstitialAdID")
        addTextWatcher(mainPointsPerRupee, "PointsPerRupee")

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.fabAddHome -> {
                createAlertForUpdate()
            }
            R.id.fabAddOverview -> {
                val intent = Intent(this, WebActivity::class.java)
                intent.putExtra(
                    "URL",
                    "https://console.firebase.google.com/u/0/project/earntobank-8835b/overview"
                )
                startActivity(intent)
            }
            R.id.fabAddTelegram -> {
                createAlertForUpdate(false)
            }
            R.id.fabAddUsers -> {
                val intent = Intent(this, WebActivity::class.java)
                intent.putExtra(
                    "URL",
                    "https://console.firebase.google.com/u/0/project/earntobank-8835b/authentication/users"
                )
                startActivity(intent)
            }
            R.id.fabAddWithdrawal -> {
                startActivity(Intent(this, HistoryActivity::class.java))
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }


    @SuppressLint("InflateParams", "SimpleDateFormat")
    private fun createAlertForUpdate(isHome: Boolean = true) {
        val view = LayoutInflater.from(this).inflate(R.layout.alert_update_details, null, false)

        val alertDialog = AlertDialog.Builder(this).setTitle("Fill detail").setView(view)
            .setPositiveButton("OK") { _, _ ->
                val title = view.alertUpdateTitle.text.toString()
                val link = view.alertUpdateLink.text.toString()
                val time = SimpleDateFormat("dd MMM yyyy").format(Timestamp.now().toDate())

                val data = HashMap<String, Any>()
                data["Title"] = title
                data["Link"] = link

                val collectionID = if (isHome) {
                    data["PostDate"] = time
                    "Home"
                } else {
                    data["Time"] = time
                    "Telegram"
                }

                FirebaseFirestore.getInstance().collection(collectionID).add(data)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this, "Data added successfully", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            AlertDialog.Builder(this@AppsActivity)
                                .setMessage(it.exception!!.localizedMessage).setTitle("Error")
                                .show()
                        }
                    }
            }


        alertDialog.show()
    }

    private fun addTextWatcher(editText: EditText, key: String) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val data = HashMap<String, Any>()

                if (key.contains("Point")) {
                    var point = s!!
                    if (point == "") {
                        point = "1"
                    }
                    editText.inputType = InputType.TYPE_CLASS_NUMBER

                    data[key] = point.toString().toLong()
                } else {
                    data[key] = s.toString()
                }

                importantValueDB.update(data).addOnCompleteListener {
                    if (!it.isSuccessful) {
                        AlertDialog.Builder(this@AppsActivity)
                            .setMessage(it.exception!!.localizedMessage).setTitle("Error").show()
                    }
                }
            }
        })
    }


    private fun updateEditText(data: ImportantData) {
        mainBannerID.text = SpannableStringBuilder(data.BannerAdID)
        mainInterstitialID.text = SpannableStringBuilder(data.InterstitialAdID)
        mainImpressionPoint.text = SpannableStringBuilder(data.ImpressionPoint.toString())
        mainPointsPerRupee.text = SpannableStringBuilder(data.PointsPerRupee.toString())
    }

    private fun getData() {
        importantValueDB.get().addOnCompleteListener {
            if (it.isComplete) {
                val result = it.result!!
                if (result.exists()) {
                    updateEditText(
                        ImportantData(
                            BannerAdID = result.getString("BannerAdID")!!,
                            InterstitialAdID = result.getString("InterstitialAdID")!!,
                            ImpressionPoint = result.getLong("ImpressionPoint")!!,
                            PointsPerRupee = result.getLong("PointsPerRupee")!!
                        )
                    )
                } else {
                    AlertDialog.Builder(this)
                        .setMessage("Probably database error contact developer of this app.").show()
                }
            } else {
                AlertDialog.Builder(this).setTitle("Error")
                    .setMessage(it.exception!!.localizedMessage).show()
            }
        }
    }
}
