package com.deucate.earnengine

import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import kotlinx.android.synthetic.main.activity_apps.*
import kotlinx.android.synthetic.main.app_bar_apps.*
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.deucate.earnengine.views.AddHomeFragment
import com.deucate.earnengine.views.HomeFragment
import com.deucate.earnengine.views.TelegramFragment
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.alert_update_details.view.*
import java.text.SimpleDateFormat
import java.util.*

class AppsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val currentFragment = MutableLiveData<Fragment>()
    private var fragmentID = 8080

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

        currentFragment.observe(this, androidx.lifecycle.Observer {
            it.let { fragment ->
                if (fragment.id != fragmentID) {
                    supportFragmentManager.beginTransaction().replace(R.id.containerApps, fragment)
                        .commit()
                }
            }
        })

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
            R.id.fabHome -> {
                val fragment = HomeFragment()
                fragment.appName = intent.getStringExtra("AppName")
                currentFragment.value = fragment
            }
            R.id.fabAddHome -> {
                val fragment = AddHomeFragment()
                fragment.appName = intent.getStringExtra("AppName")
                currentFragment.value = fragment
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
                val fragment = TelegramFragment()
                fragment.appName = intent.getStringExtra("AppName")
                currentFragment.value = fragment
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


    companion object {

        @SuppressLint("InflateParams", "SimpleDateFormat")
        fun createAlertForUpdate(isHome: Boolean = true, context: Context, appName: String) {
            val view =
                LayoutInflater.from(context).inflate(R.layout.alert_update_details, null, false)

            val alertDialog = AlertDialog.Builder(context).setTitle("Fill detail").setView(view)
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

                    FirebaseFirestore.getInstance().collection("Apps").document(appName)
                        .collection(collectionID).add(data)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(
                                    context,
                                    "Data added successfully",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            } else {
                                AlertDialog.Builder(context)
                                    .setMessage(it.exception!!.localizedMessage).setTitle("Error")
                                    .show()
                            }
                        }
                }
            alertDialog.show()
        }
    }
}
