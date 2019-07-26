package shrinkcom.waff.com.util

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.R
import shrinkcom.waff.com.interfaces.DialogBoxButtonListner
import android.R.string.cancel
import android.R.string.cancel






class ShowMessage
{
    lateinit var activity: Activity
    lateinit var builder: AlertDialog.Builder
    lateinit var alertDialog: AlertDialog

    public constructor(activity: Activity)
    {
        this.activity = activity ;
    }

     fun  showDialogMessage(message: String)
     {

         builder = AlertDialog.Builder(activity);
         alertDialog = builder.create();
         alertDialog.setMessage(message)
         alertDialog.setTitle("Alert!")
         alertDialog.setButton("OK", object :DialogInterface.OnClickListener
         {
             override fun onClick(dialog: DialogInterface, which: Int)
             {
                 dialog.dismiss()
             }

         })

         alertDialog.show()
     }


    fun  showDialogMessage(message: String , dialogBoxButtonListner: DialogBoxButtonListner)
    {

        builder = AlertDialog.Builder(activity);
        alertDialog = builder.create();
        alertDialog.setMessage(message)
        alertDialog.setTitle("Alert!")
        alertDialog.setCancelable(false)
        alertDialog.setButton("OK", object :DialogInterface.OnClickListener
        {
            override fun onClick(dialog: DialogInterface, which: Int)
            {
                dialog.dismiss()
                dialogBoxButtonListner.onYesButtonClick(dialog)
            }

        })

        alertDialog.show()
    }

    public fun  showOptionalDailogBox( title:String,    msg:String,   dialogBoxButtonListner: DialogBoxButtonListner)
    {
        val alertDialog = AlertDialog.Builder(activity)
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(msg);

        // Setting Icon to Dialog

        // Setting Positive "Yes" Button

        alertDialog.setPositiveButton(activity.getString(R.string.yes) , object : DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {

                  dialog?.cancel();
                dialogBoxButtonListner.onYesButtonClick(dialog);
            }

        })

        alertDialog.setNegativeButton(activity.getString(R.string.no) , object : DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {

  dialog?.cancel();
                dialogBoxButtonListner.onNoButtonClick(dialog);
            }

        })

        // Showing Alert Message
        alertDialog.show();

    }
}