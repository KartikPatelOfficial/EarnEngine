package com.deucate.earnengine.views


import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.deucate.earnengine.R
import com.deucate.earnengine.model.ImportantData
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_home.view.*
import java.util.HashMap

class HomeFragment : Fragment() {

    lateinit var appName:String

    private lateinit var importantValueDB: DocumentReference
    private lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_home, container, false)

        importantValueDB = FirebaseFirestore.getInstance().collection("Apps")
            .document(appName).collection(getString(R.string.important))
            .document(getString(R.string.value))

        getData()

        //adding watchers to all edit text
        addTextWatcher(rootView.mainBannerID, "BannerID")
        addTextWatcher(rootView.mainImpressionPoint, "ImpressionPoint")
        addTextWatcher(rootView.mainInterstitialID, "InterstitialAdID")
        addTextWatcher(rootView.mainPointsPerRupee, "PointsPerRupee")

        return rootView
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
                        AlertDialog.Builder(context!!)
                            .setMessage(it.exception!!.localizedMessage).setTitle("Error").show()
                    }
                }
            }
        })
    }


    private fun updateEditText(data: ImportantData) {
        rootView.mainBannerID.text = SpannableStringBuilder(data.BannerAdID)
        rootView.mainInterstitialID.text = SpannableStringBuilder(data.InterstitialAdID)
        rootView.mainImpressionPoint.text = SpannableStringBuilder(data.ImpressionPoint.toString())
        rootView.mainPointsPerRupee.text = SpannableStringBuilder(data.PointsPerRupee.toString())
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
                    AlertDialog.Builder(context!!)
                        .setMessage("Probably database error contact developer of this app.").show()
                }
            } else {
                AlertDialog.Builder(context!!).setTitle("Error")
                    .setMessage(it.exception!!.localizedMessage).show()
            }
        }
    }


}
