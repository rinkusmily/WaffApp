package shrinkcom.waff.com.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import shrinkcom.waff.com.R;
import shrinkcom.waff.com.bean.UserData;
import shrinkcom.waff.com.databinding.ActivityOtpBinding;
import shrinkcom.waff.com.interfaces.ServerRespondingListener;
import shrinkcom.waff.com.serverconntion.OkHttpRequest;
import shrinkcom.waff.com.util.SessionManager;
import shrinkcom.waff.com.util.ShowMessage;

import java.util.HashMap;

public class OTPActivity extends AppCompatActivity
{

    ActivityOtpBinding activityOtpBinding ;

    int action ;
    String mobileNo ;
    Intent intent ;
    UserData userData ;
    OkHttpRequest okHttpRequest ;
    ShowMessage showMessage ;
    Activity activity ;
    SessionManager sessionManager ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        activityOtpBinding = DataBindingUtil.setContentView(this , R.layout.activity_otp);
        activityOtpBinding.setOTPActivity(this);
        okHttpRequest = new OkHttpRequest(this);
        showMessage = new ShowMessage(this);
        activity = this ;
        sessionManager = new SessionManager(activity);
        intent = getIntent();

        if (intent != null)
        {
            action = intent.getIntExtra("action" ,0);
            mobileNo = intent.getStringExtra("mobile_no");

            activityOtpBinding.optMsgTv.setText("We have sent successfully a otp on your registred email address "+mobileNo);

            userData = intent.getParcelableExtra("userData");
        }

        addTextChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (action == 2)
        {
            requestForResentOTP();
        }
    }


    public void addTextChanged()
    {
        activityOtpBinding.firstDegitEdittx.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (activityOtpBinding.firstDegitEdittx.getText().toString().length() ==1 )
                {

                    activityOtpBinding.secondDegitEdittx.requestFocus();
                }

            }
        });



        activityOtpBinding.secondDegitEdittx.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (activityOtpBinding.firstDegitEdittx.getText().toString().length() ==1 )
                {

                    activityOtpBinding.thirdDegitEdittx.requestFocus();
                }

            }
        });



        activityOtpBinding.thirdDegitEdittx.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (activityOtpBinding.firstDegitEdittx.getText().toString().length() ==1 )
                {

                    activityOtpBinding.fourthDegitEdittx.requestFocus();
                }

            }
        });




    }


    public void requestForVerifyOtp()
    {

        String otp = activityOtpBinding.firstDegitEdittx.getText().toString()+
                     activityOtpBinding.secondDegitEdittx.getText().toString()+
                     activityOtpBinding.thirdDegitEdittx.getText().toString()+
                     activityOtpBinding.fourthDegitEdittx.getText().toString();

        if (otp.length() != 4)
        {
            showMessage.showDialogMessage("Please enter 4 degit otp");

            return;
        }

//action= (,otp)
        HashMap<String, Object> param = new HashMap<>();

        if (action  == 0)
        {
            param.put("action" ,"otp_check");
            param.put("email" ,mobileNo);

        }
        else
        {
            param.put("action" ,"verify_otp");
            param.put("user_id" ,userData.getUserId());
        }
        param.put("otp" ,otp);
        Log.e("SENDOTPFROMSERVER",">>"+param);

        okHttpRequest.getResponse(param, new ServerRespondingListener(activity) {
            @Override
            public void onRespose(@NotNull JSONObject resultData) {

                Log.e("resultData" , ""+resultData);
                try
                {
                    if (action == 0)
                    {
                        Gson gson = new Gson();
                        UserData userData = gson.fromJson(resultData.optJSONArray("userData").optString(0) , UserData.class) ;
                        Intent intent = new Intent(OTPActivity.this , ResetPasswordActivity.class);
                        intent.putExtra("action" ,0);
                        intent.putExtra("mobile_no" , mobileNo);
                        intent.putExtra("userData" , userData);
                        startActivity(intent);
                    }
                    else
                    {
                        Intent intent = new Intent(OTPActivity.this , Dashboard.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        sessionManager.saveUser(userData);

                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }


                }
                catch (Exception e)
                {
                    Log.e("excep" , e.getMessage());
                }


            }
        });
    }
//action=resend_otp (user_id, email)

    public void requestForResentOTP()
    {
        HashMap<String , Object> param = new HashMap<>();
        param.put("action" , "resend_otp");
        param.put("user_id" , ""+userData.getUserId());
        param.put("email" , ""+userData.getEmail());
        okHttpRequest.getResponse(param, new ServerRespondingListener(activity) {
            @Override
            public void onRespose( JSONObject resultData) {

                try{


                }
                catch (Exception e)
                {
                    Toast.makeText(getApplicationContext() , e.getMessage(),Toast.LENGTH_LONG).show();
                }

            }
        });
    }

}
