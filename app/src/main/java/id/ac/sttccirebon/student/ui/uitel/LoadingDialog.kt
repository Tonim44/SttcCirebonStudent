package id.ac.sttccirebon.student.ui.uitel

import android.app.Activity
import android.app.AlertDialog
import id.ac.sttccirebon.student.R

class LoadingDialog(val mActivity: Activity) {

    private lateinit var isdialog:AlertDialog

    fun startLoading(){

        val infalter = mActivity.layoutInflater
        val dialogView = infalter.inflate(R.layout.loading_item,null)

        val bulider = AlertDialog.Builder(mActivity)
        bulider.setView(dialogView)
        bulider.setCancelable(false)
        isdialog = bulider.create()
        isdialog.show()
    }

    fun isDismiss(){
        isdialog.dismiss()
    }

}