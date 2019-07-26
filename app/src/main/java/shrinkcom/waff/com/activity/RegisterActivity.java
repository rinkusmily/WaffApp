package shrinkcom.waff.com.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.TextUtils;
import android.widget.Toast;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import shrinkcom.waff.com.R;
import shrinkcom.waff.com.bean.UserData;
import shrinkcom.waff.com.databinding.ActivityRegisterBinding;
import shrinkcom.waff.com.interfaces.ServerRespondingListener;
import shrinkcom.waff.com.serverconntion.OkHttpRequest;
import shrinkcom.waff.com.util.ShowMessage;
import shrinkcom.waff.com.util.Validation;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding activityRegisterBinding;

    String inputName;
    String inputEmail;
    String inputPassword;
    String inputMobile;
    boolean termCondiCheckbox;
    ShowMessage showMessage;
    OkHttpRequest okHttpRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityRegisterBinding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        activityRegisterBinding.setRegisterActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showMessage = new ShowMessage(this);
        okHttpRequest = new OkHttpRequest(this);
    }

    public void requestForRegisterNewUser() {
        inputName = activityRegisterBinding.inputName.getText().toString();
        inputEmail = activityRegisterBinding.inputEmail.getText().toString();
        inputPassword = activityRegisterBinding.inputPassword.getText().toString();
        inputMobile = activityRegisterBinding.inputMobile.getText().toString();
        termCondiCheckbox = activityRegisterBinding.termCondiCheckbox.isChecked();


        if (applyValidation()) {
            HashMap<String, Object> param = new HashMap<>();
            param.put("action", "register");
            param.put("username", inputName);
            param.put("email", inputEmail);
            param.put("phone", inputMobile);
            param.put("password", inputPassword);

            okHttpRequest.getResponse(param, new ServerRespondingListener(this) {
                @Override
                public void onRespose(@NotNull JSONObject jsonObject) {

                    try {
                        Gson gson = new Gson();
                        UserData userData = gson.fromJson(jsonObject.getJSONArray("userData").getString(0), UserData.class);


                        Intent intent = new Intent(getApplicationContext() , OTPActivity.class);
                        intent.putExtra("action" ,1);
                        intent.putExtra("mobile_no" , inputEmail);
                        intent.putExtra("userData", userData);
                        startActivity(intent);

                    } catch (Exception e) {

                    }

                }
            });


        }


    }


    public boolean applyValidation() {

        if (TextUtils.isEmpty(inputName)) {
            showMessage.showDialogMessage("Please enter user name");

            return false;
        }

        if (TextUtils.isEmpty(inputEmail)) {
            showMessage.showDialogMessage("Please enter email address");

            return false;
        }

        if (!Validation.isValidEmail(inputEmail)) {
            showMessage.showDialogMessage("Please enter valid email address");

            return false;
        }

        if (TextUtils.isEmpty(inputPassword)) {
            showMessage.showDialogMessage("Please enter password");

            return false;
        }

        if (inputPassword.length() < 8) {
            showMessage.showDialogMessage("Password length should be atleast 8 characters");

            return false;
        }

        if (TextUtils.isEmpty(inputMobile)) {
            showMessage.showDialogMessage("Please enter mobile no");

            return false;
        }

        if (inputMobile.length() < 6) {
            showMessage.showDialogMessage("Mobile no should be atleast 6 characters");

            return false;
        }

        if (!termCondiCheckbox) {
            showMessage.showDialogMessage("Please checked term and condition checkbox");

            return false;
        }

        return true;
    }
}
