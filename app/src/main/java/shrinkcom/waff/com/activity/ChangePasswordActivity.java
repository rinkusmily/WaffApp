package shrinkcom.waff.com.activity;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import android.widget.Toast;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import shrinkcom.waff.com.R;
import shrinkcom.waff.com.databinding.ActivityChangePasswordBinding;
import shrinkcom.waff.com.interfaces.ServerRespondingListener;
import shrinkcom.waff.com.serverconntion.OkHttpRequest;
import shrinkcom.waff.com.util.SessionManager;
import shrinkcom.waff.com.util.ShowMessage;

import java.util.HashMap;
import java.util.regex.Pattern;

public class ChangePasswordActivity extends AppCompatActivity {
    private Activity mContext;

    private static final String PASSWORD_PATTERN =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";

    ActivityChangePasswordBinding activityChangePasswordBinding;

    OkHttpRequest okHttpRequest ;
    SessionManager sessionManager ;

    String specialChars = "~`!@#$%^&*()-_=+\\|[{]};:'\",<.>/?";
    ShowMessage showMessage ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mContext = this;

        activityChangePasswordBinding = DataBindingUtil.setContentView( this , R.layout.activity_change_password);

        activityChangePasswordBinding.setChangePasswordActivity(this);

        showMessage = new ShowMessage(this);
        sessionManager = new SessionManager(mContext);
        okHttpRequest = new OkHttpRequest(mContext);

        activityChangePasswordBinding.toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    public void requestForChangePassword()
    {
        if (applyValidation())
        {

            HashMap<String , Object> param = new HashMap<>();
            param.put("action" , "set_password");
            param.put("user_id" , sessionManager.getUser().getUserId());
            param.put("password" , activityChangePasswordBinding.inputPassword.getText().toString());
            param.put("oldpassword" , activityChangePasswordBinding.inputCurrentPassword.getText().toString());




            okHttpRequest.getResponse(param, new ServerRespondingListener(mContext) {
                @Override
                public void onRespose(@NotNull JSONObject resultData) {



                    try
                    {

                        Toast.makeText(mContext , resultData.getString("message") ,Toast.LENGTH_LONG).show();
                      finish();
                    }
                    catch (Exception e)
                    {
                        showMessage.showDialogMessage(e.getMessage());
                    }

                }
            });
        }
    }
  //action= set_password (user_id ,password )

    public boolean applyValidation()
    {


        if (activityChangePasswordBinding.inputCurrentPassword.getText().toString().length() < 8)
        {
            showMessage.showDialogMessage("Please enter valid current passowrd");

            return false;
        }



        if (activityChangePasswordBinding.inputPassword.getText().toString().length() < 8)
        {
            showMessage.showDialogMessage("Please enter valid new passowrd");

            return false;
        }

        if (activityChangePasswordBinding.inputRepeatPassword.getText().toString().length() < 8)
        {
            showMessage.showDialogMessage("Please enter valid repeat passowrd");

            return false;
        }


        if (!activityChangePasswordBinding.inputPassword.getText().toString().equals(activityChangePasswordBinding.inputRepeatPassword.getText().toString()))
        {
            showMessage.showDialogMessage("Passoword and confirm password should be same");

            return false;
        }



        return true;
    }
}
