package shrinkcom.waff.com.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import shrinkcom.waff.com.R;
import shrinkcom.waff.com.bean.UserData;
import shrinkcom.waff.com.databinding.ActivityForgotBinding;
import shrinkcom.waff.com.interfaces.ServerRespondingListener;
import shrinkcom.waff.com.serverconntion.OkHttpRequest;
import shrinkcom.waff.com.util.ShowMessage;
import shrinkcom.waff.com.util.Validation;

import java.util.HashMap;

public class ForgotActivity extends AppCompatActivity implements View.OnClickListener {
    private Button send_email;
    private Activity mContext;
    ActivityForgotBinding activityForgotBinding ;
    OkHttpRequest okHttpRequest ;
    String emailAddress ;
    ShowMessage showMessage ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        activityForgotBinding = DataBindingUtil.setContentView(this ,R.layout.activity_forgot );
        activityForgotBinding.setForgotActivity(this);
        mContext = this;
        showMessage = new ShowMessage(mContext);
        okHttpRequest = new OkHttpRequest(this);

        initViews();
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    public void requestForForgotPassword()
    {
        emailAddress = activityForgotBinding.mobileNoEdittv.getText().toString();

        if (TextUtils.isEmpty(emailAddress))
        {
            showMessage.showDialogMessage("Please enter email address");
        }

        if (!Validation.isValidEmail(emailAddress))
        {
            showMessage.showDialogMessage("Please enter valid email address");

        }

        HashMap<String , Object> param = new HashMap<>();
        param.put("action" , "forget_password");
        param.put("email" , emailAddress);

        okHttpRequest.getResponse(param, new ServerRespondingListener(mContext) {
            @Override
            public void onRespose(@NotNull JSONObject resultData) {

                Gson gson = new Gson();
                UserData userData = gson.fromJson(resultData.optJSONArray("userData").optString(0) , UserData.class) ;
                Intent intent = new Intent(getApplicationContext() , OTPActivity.class);
                intent.putExtra("action" ,0);
                intent.putExtra("mobile_no" , activityForgotBinding.mobileNoEdittv.getText().toString());
                intent.putExtra("userData" , userData);
                startActivity(intent);

            }
        });
    }

    private void initViews() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_email:

                break;
        }
    }
}
