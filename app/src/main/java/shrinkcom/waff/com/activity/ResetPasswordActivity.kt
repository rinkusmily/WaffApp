package shrinkcom.waff.com.activity

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import org.json.JSONObject
import shrinkcom.waff.com.R
import shrinkcom.waff.com.bean.UserData
import shrinkcom.waff.com.databinding.ResetPasswordLayoutBinding
import shrinkcom.waff.com.interfaces.ServerRespondingListener
import shrinkcom.waff.com.serverconntion.OkhttpClientKotlin
import shrinkcom.waff.com.util.ShowMessage
import kotlin.collections.HashMap


class ResetPasswordActivity : AppCompatActivity() {

    lateinit var userData: UserData
    var newPassword = "";
    var confirmPassword = ""
    lateinit var showMessage: ShowMessage
    var activity: Activity? = null
    lateinit var okHttpRequest: OkhttpClientKotlin

    internal var action: Int = 0
    lateinit var mobileNo: String

    lateinit var resetPasswordLayoutBinding: ResetPasswordLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resetPasswordLayoutBinding = DataBindingUtil.setContentView(this, R.layout.reset_password_layout)
        resetPasswordLayoutBinding.resetPasswordActivity = this
        activity = this
        showMessage = ShowMessage(activity!!);
        okHttpRequest = OkhttpClientKotlin(activity!!)


        if (intent != null) {
            action = intent.getIntExtra("action", 0)
            mobileNo = intent.getStringExtra("mobile_no")
            userData = intent.getParcelableExtra("userData")



        }

    }

    override fun onResume() {
        super.onResume()
    }

    public fun applyValidation(): Boolean {
        newPassword = resetPasswordLayoutBinding.inputPassword.text.toString()
        confirmPassword = resetPasswordLayoutBinding.inputRepeatPassword.text.toString()


        if (newPassword.length < 8) {
            showMessage.showDialogMessage("New password should be atleast 8 characters.")
            return false;
        }


        if (confirmPassword.length < 8) {
            showMessage.showDialogMessage("Repeat password should be atleast 8 characters.")
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            showMessage.showDialogMessage("New password and repate password  should be same.")

        }

        return true
    }
//=  ( , )
     fun requestForResetPassword() {


        if (applyValidation())
        {
            val  param:HashMap<String? , Any?> = HashMap<String? , Any?>()

            param.put("action" , "set_password");
            param.put("user_id" , userData.userId);
            param.put("newpassword" , newPassword);

            param.put("password" , confirmPassword);


            okHttpRequest.getResponse(param ,object : ServerRespondingListener(this!!.activity!!)
            {
                override fun onRespose(resultData: JSONObject) {

                    Log.e("resultData" ,""+resultData);


                    val intent1 = Intent(this@ResetPasswordActivity, LoginActivity::class.java);
                    intent1.flags =   Intent.FLAG_ACTIVITY_CLEAR_TASK or  Intent.FLAG_ACTIVITY_NEW_TASK

                    startActivity(intent1)
                }

            });

        }


    }
}