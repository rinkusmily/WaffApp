package shrinkcom.waff.com.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import android.widget.Toast;
import com.facebook.*;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import shrinkcom.waff.com.R;
import shrinkcom.waff.com.bean.UserData;
import shrinkcom.waff.com.databinding.ActivityLoginBinding;
import shrinkcom.waff.com.interfaces.ServerRespondingListener;
import shrinkcom.waff.com.serverconntion.OkHttpRequest;
import shrinkcom.waff.com.util.SessionManager;
import shrinkcom.waff.com.util.Validation;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private Activity mContext;

    private String password, emailAddress;
    private OkHttpRequest okHttpRequest;

    ActivityLoginBinding activityLoginBinding;

    LoginManager loginManager;
    CallbackManager callbackManager;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    SessionManager sessionManager ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        mContext = this;
        activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        activityLoginBinding.setLoginActivity(this);
        okHttpRequest = new OkHttpRequest(mContext);
        sessionManager = new SessionManager(this);
        initViews();
    }

    private void initViews() {

        activityLoginBinding.forgotPassword.setOnClickListener(this::onClick);
        activityLoginBinding.btnLogin.setOnClickListener(this::onClick);
        activityLoginBinding.tvSignUp.setOnClickListener(this::onClick);
        loginManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();
        loginManager.registerCallback(callbackManager, callback);
        ArrayList<String> list = new ArrayList<String>();
        list.add("user_friends");


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //   LoginManager.getInstance().logInWithPublishPermissions(this, Arrays.asList("publish_actions"));


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            callbackManager.onActivityResult(requestCode, resultCode, data);

        }

        if (resultCode == RESULT_OK && requestCode == 1000) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forgot_password:
                startActivity(new Intent(mContext, ForgotActivity.class));
                break;

            case R.id.btn_login:
                List<String> errorList = new ArrayList<String>();
                password = activityLoginBinding.inputPassword.getText().toString().trim();
                if (!isValid(password, errorList)) {
                    activityLoginBinding.inputPassword.setError("The password entered here  is invalid");
                    for (String error : errorList) {
                        activityLoginBinding.inputPassword.setError(error);
                    }
                } else {
                    requestForLogin();
                }
                break;

            case R.id.tv_sign_up:
                startActivity(new Intent(mContext, RegisterActivity.class));
                break;
        }
    }

    public boolean isValid(String passwordhere, List<String> errorList) {

        Pattern specailCharPatten = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Pattern UpperCasePatten = Pattern.compile("[A-Z ]");
        Pattern lowerCasePatten = Pattern.compile("[a-z ]");
        Pattern digitCasePatten = Pattern.compile("[0-9 ]");

        emailAddress = activityLoginBinding.inputName.getText().toString();
        errorList.clear();

        boolean flag = true;


        if (TextUtils.isEmpty(emailAddress)) {
            activityLoginBinding.inputName.setError("Please enter email address");


            return false;
        }

        if (!Validation.isValidEmail(emailAddress)) {
            activityLoginBinding.inputName.setError("Please enter valid email address");


            return false;
        }


       /* if (!passwordhere.equals(confirmhere)) {
            errorList.add("password and confirm password does not match");
            flag=false;
        }*/
        if (passwordhere.length() < 8) {
            errorList.add("Password length must have atleast 8 character !!");
            flag = false;
        }
       /* if (!specailCharPatten.matcher(passwordhere).find()) {
            errorList.add("Password must have atleast on" +
                    "e special character !!");
            flag = false;
        }
        if (!UpperCasePatten.matcher(passwordhere).find()) {
            errorList.add("Password must have atleast one uppercase character !!");
            flag = false;
        }
        if (!lowerCasePatten.matcher(passwordhere).find()) {
            errorList.add("Password must have atleast one lowercase character !!");
            flag = false;
        }
        if (!digitCasePatten.matcher(passwordhere).find()) {
            errorList.add("Password must have atleast one digit character !!");
            flag = false;
        }*/

        return flag;

    }

    public void requestForLogin() {
        HashMap<String, Object> param = new HashMap<>();
        param.put("action", "login");
        param.put("email", emailAddress);
        param.put("password", password);
        okHttpRequest.getResponse(param, new ServerRespondingListener(mContext) {
            @Override
            public void onRespose(@NotNull JSONObject resultData) {


                try {
                    Gson gson = new Gson();
                    UserData userData = gson.fromJson(resultData.getJSONArray("userData").getString(0), UserData.class);

                    if (userData.getVerifyStatus().equals("0")) {
                        Intent intent = new Intent(getApplicationContext(), OTPActivity.class);
                        intent.putExtra("action", 2);
                        intent.putExtra("mobile_no", emailAddress);
                        intent.putExtra("userData", userData);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                        intent.putExtra("action", 1);
                        intent.putExtra("mobile_no", emailAddress);
                        intent.putExtra("userData", userData);
                        sessionManager.saveUser(userData);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(intent);
                    }


                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });

    }


//action=   (, , phone, )

    public void requestForLoginforSocial(String userName, String emailAddress, String loginBy) {
        HashMap<String, Object> param = new HashMap<>();
        param.put("action", "sociallogin");
        param.put("email", emailAddress);
        param.put("user_name", userName);
        param.put("login_by", loginBy);

        okHttpRequest.getResponse(param, new ServerRespondingListener(mContext) {
            @Override
            public void onRespose(@NotNull JSONObject resultData) {


                try {
                    Gson gson = new Gson();
                    UserData userData = gson.fromJson(resultData.getJSONArray("userData").getString(0), UserData.class);

                    if (userData.getVerifyStatus().equals("0")) {
                        Intent intent = new Intent(getApplicationContext(), OTPActivity.class);
                        intent.putExtra("action", 2);
                        intent.putExtra("mobile_no", emailAddress);
                        intent.putExtra("userData", userData);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                        intent.putExtra("action", 1);
                        sessionManager.saveUser(userData);
                        intent.putExtra("mobile_no", emailAddress);
                        intent.putExtra("userData", userData);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }


                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {

            Profile profile = Profile.getCurrentProfile();


            getDataFaceBook(loginResult.getAccessToken());

        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

        }
    };


    public void getDataFaceBook(AccessToken token) {
        GraphRequest request = GraphRequest.newMeRequest(
                token,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v("LoginActivity", response.toString());

                        // Application code
                        try {
                            Log.d("tttttt", object.getString("id"));

                            String fnm = object.getString("first_name");
                            String lnm = object.getString("last_name");
                            String mail = object.getString("email");
                            String fid = object.getString("id");
                           // Toast.makeText(getApplicationContext(), fnm + " " + lnm + mail, Toast.LENGTH_LONG).show();


                            requestForLoginforSocial(fnm + " " + lnm, mail, "Facebook");

                           /* String birthday="";
                            if(object.has("birthday")){
                                birthday = object.getString("birthday"); // 01/31/1980 format
                            }


                            String gender = object.getString("gender");

                           // tvdetails.setText("Name: "+fnm+" "+lnm+" \n"+"Email: "+mail+" \n"+"Gender: "+gender+" \n"+"ID: "+fid+" \n"+"Birth Date: "+birthday);
                           // aQuery.id(ivpic).image("https://graph.facebook.com/" + fid + "/picture?type=large");
                            //https://graph.facebook.com/143990709444026/picture?type=large
      */
                            Log.d("aswwww", "https://graph.facebook.com/" + fid + "/picture?type=large");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, first_name, last_name, email, gender, birthday, location,link");
        request.setParameters(parameters);
        request.executeAsync();

    }

    public void faceBooLoginRequest() {

        LoginManager.getInstance().logOut();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("user_photos", "email", "public_profile", "user_posts"));

    }

    public void googleLoginRequest() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, 1000);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    // handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Wait...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("sdfhk", "handleSignInResult:" + result.isSuccess());



        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            Log.e("sdfhk", "display name: " + acct.getDisplayName());

            String personName = acct.getDisplayName();
            String email = acct.getEmail();

            Log.e("sdfhk", "Name: " + personName + ", email: " + email
                    + ", Image: ");

           // Toast.makeText(getApplicationContext() , "Email "+acct.getEmail() , Toast.LENGTH_LONG ).show();

            requestForLoginforSocial(personName, email, "Google");

            signOut();

            /*txtName.setText(personName);
            txtEmail.setText(email);
            Glide.with(getApplicationContext()).load(personPhotoUrl)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgProfilePic);*/


        }
    }

    private void signOut() {
        try {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                        }
                    });
        } catch (Exception e) {

        }

    }
}
