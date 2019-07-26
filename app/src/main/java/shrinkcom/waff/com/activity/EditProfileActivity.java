package shrinkcom.waff.com.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import shrinkcom.waff.com.R;
import shrinkcom.waff.com.adapter.SpinnerItemAdapter;
import shrinkcom.waff.com.bean.UserData;
import shrinkcom.waff.com.databinding.ActivityEditProfileBinding;
import shrinkcom.waff.com.interfaces.RecycleViewItemClickListner;
import shrinkcom.waff.com.interfaces.ServerRespondingListener;
import shrinkcom.waff.com.serverconntion.OkHttpRequest;
import shrinkcom.waff.com.util.CircleBitmapTranslation;
import shrinkcom.waff.com.util.SessionManager;
import shrinkcom.waff.com.util.ShowMessage;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener  {
    private Activity mContext;
    private String userChoosenTask;
    private int REQUEST_CAMERA = 0;
    private int SELECT_FILE = 1;
    UserData userData ;
    SessionManager sessionManager ;
    ShowMessage showMessage;
    OkHttpRequest okHttpRequest ;
    SpinnerItemAdapter spinnerItemAdapter ;
    File userProfileFile ;
    ArrayList<String> spinnerArrayList ;


    ActivityEditProfileBinding activityEditProfileBinding ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        sessionManager = new SessionManager(mContext);
        activityEditProfileBinding = DataBindingUtil.setContentView(mContext , R.layout.activity_edit_profile);
        activityEditProfileBinding.setEditProfileActivity(this);
        activityEditProfileBinding.toolbar.setNavigationOnClickListener(v -> finish());

        activityEditProfileBinding.btnLogin.setOnClickListener(this);

        okHttpRequest = new OkHttpRequest(mContext);
//
        spinnerArrayList = new ArrayList<>();
        spinnerArrayList.add("Mountain");
        spinnerArrayList.add("Biking");
        spinnerArrayList.add("Walking");
        spinnerArrayList.add("Nordic");
        spinnerArrayList.add("Running");
        spinnerArrayList.add("Walking");
        spinnerArrayList.add("Hiking");


        spinnerItemAdapter = new SpinnerItemAdapter(this);
        spinnerItemAdapter.inislizedRecycleItemClickListener(recycleViewItemClickListner , spinnerArrayList ,0 );
        activityEditProfileBinding.actionSpiinner.setAdapter(spinnerItemAdapter);

        userData =  sessionManager.getUser();


        try
        {
            Log.e("userimage" ,sessionManager.getUser().getImage());

            String url = "https://shrinkcom.com/waff/"+ sessionManager.getUser().getImage();
            requestOptions =   new RequestOptions();
            requestOptions = requestOptions.transform(new CircleBitmapTranslation(this));
            requestOptions = requestOptions.placeholder(R.drawable.user);
            requestOptions = requestOptions.error(R.drawable.user);
            Glide.with(this)
                    .setDefaultRequestOptions(requestOptions).asBitmap()
                    .load(url)
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {

                            return false;
                        }
                    })
                    .into(activityEditProfileBinding.imageUserPick) ;
        }
        catch (Exception e)
        {


        }
    }
    RequestOptions requestOptions ;
    @Override
    protected void onResume() {
        super.onResume();
        activityEditProfileBinding.imageUserSet.setOnClickListener(this);
        showMessage = new ShowMessage(this);

        try
        {
            activityEditProfileBinding.inputEmail.setText(userData.getEmail());
            activityEditProfileBinding.inputUsernameProf.setText(userData.getUsername());
            activityEditProfileBinding.inputNickname.setText(userData.getUsername());
            activityEditProfileBinding.inputMobile.setText(userData.getMobile());







            activityEditProfileBinding.inputEmail.setEnabled(false);

        }
        catch (Exception e)
        {

        }

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageUserSet:
                selectImage();
                break;

            case R.id.btn_login :

                selectImage();

                break;

        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(mContext);
                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        userProfileFile = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            userProfileFile.createNewFile();
            fo = new FileOutputStream(userProfileFile);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Glide.with(this)
                .setDefaultRequestOptions(requestOptions).asBitmap()
                .load(userProfileFile)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap bitmap, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {

                       // activityEditProfileBinding.imageUserPick.setImageBitmap(bitmap);

                        return false;
                    }
                })
                .into(activityEditProfileBinding.imageUserPick) ;    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {



        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                userProfileFile = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");

                bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

                FileOutputStream fo;
                try {
                    userProfileFile.createNewFile();
                    fo = new FileOutputStream(userProfileFile);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        requestOptions = requestOptions.transform(new CircleBitmapTranslation(this));
        requestOptions = requestOptions.placeholder(R.drawable.user);
        requestOptions = requestOptions.error(R.drawable.user);

        Glide.with(this)
                .setDefaultRequestOptions(requestOptions).asBitmap()
                .load(userProfileFile)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {

                        return false;
                    }
                }).into(activityEditProfileBinding.imageUserPick) ;
    }

   RecycleViewItemClickListner recycleViewItemClickListner = new RecycleViewItemClickListner() {
       @Override
       public void onItemClick(int pos, int status) {

       }
   };

    public static class Utility {
        public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public static boolean checkPermission(final Context context) {
            int currentAPIVersion = Build.VERSION.SDK_INT;
            if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                        alertBuilder.setCancelable(true);
                        alertBuilder.setTitle("Permission necessary");
                        alertBuilder.setMessage("External storage permission is necessary");
                        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                            }
                        });
                        AlertDialog alert = alertBuilder.create();
                        alert.show();
                    } else {
                        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        }
    }
//=   (,,,)

    public void requestForUpdateProfile()
    {

        if (applyValidation())
        {
            HashMap<String , Object> param = new HashMap<>();
            param.put("action" , "changeUserDetails");
            param.put("userId" , sessionManager.getUser().getUserId());
            param.put("username" , activityEditProfileBinding.inputUsernameProf.getText().toString());
            param.put("email" , sessionManager.getUser().getEmail());
            param.put("phone" , activityEditProfileBinding.inputMobile.getText().toString());

            param.put("outside_action" , activityEditProfileBinding.actionSpiinner.getSelectedItem().toString());



            if (userProfileFile != null)
            {
                param.put("profile" , userProfileFile);

            }



            okHttpRequest.getResponse(param, new ServerRespondingListener(mContext) {
                @Override
                public void onRespose(@NotNull JSONObject resultData) {


                    try
                    {
                        Gson gson = new Gson();
                        UserData userData = gson.fromJson(resultData.getJSONArray("userData").getString(0), UserData.class);
                        sessionManager.saveUser(userData);

                        Toast.makeText(mContext , resultData.getString("message") ,Toast.LENGTH_LONG).show();
                             finish();
                    }
                    catch (Exception e)
                    {
                        Log.e("excep" , ""+e.getMessage());

                    }

                }
            });
        }




    }


    public boolean applyValidation()
    {


        if (TextUtils.isEmpty(activityEditProfileBinding.inputUsernameProf.getText().toString()))
        {
            showMessage.showDialogMessage("Please enter user name");

            return false;
        }


        if (TextUtils.isEmpty(activityEditProfileBinding.inputNickname.getText().toString()))
        {
            showMessage.showDialogMessage("Please enter nick name");

            return false;
        }









        if (TextUtils.isEmpty(activityEditProfileBinding.inputMobile.getText().toString())) {
            showMessage.showDialogMessage("Please enter mobile no");

            return false;
        }

        if (activityEditProfileBinding.inputMobile.getText().toString().length() < 6)
        {
            showMessage.showDialogMessage("Mobile no should be atleast 6 characters");

            return false;
        }


        return true ;
    }



    public void changeLungage()
    {
        showDialogForChangeLanguage();
    }



    ArrayList<String> languageList;
    int pos  = 0 ;
    public void showDialogForChangeLanguage()
    {
        languageList = new ArrayList<String>();
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.choose_language));
        String[] list = new String[]{getString(R.string.engleish), getString(R.string.french)};


        if (TextUtils.isEmpty( sessionManager.getLanguage())|| sessionManager.getLanguage().equalsIgnoreCase("english"))
        {
            pos =  0 ;

        }
        else
        {
            pos =  1 ;

        }

        builder.setSingleChoiceItems(list, pos, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                pos = which ;
            }
        });

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int pos1) {

                if (pos == 0)
                {
                    sessionManager.setLanguage("english");
                }
                else
                {
                    sessionManager.setLanguage("french");

                }

                try {
                    Resources res = getResources();
                    DisplayMetrics dm = res.getDisplayMetrics();
                    android.content.res.Configuration conf = res.getConfiguration();

                    if (!TextUtils.isEmpty(sessionManager.getLanguage()))
                    {
                        if (sessionManager.getLanguage().equalsIgnoreCase("english"))
                        {
                            conf.setLocale(new Locale("en"));

                        }
                        else
                        {
                            conf.setLocale(new Locale("fr"));

                        }
                    }
                    else
                    {
                        String defaultDeviceLanguage = Locale.getDefault().getDisplayLanguage();
                        if (defaultDeviceLanguage.equalsIgnoreCase("français"))
                        {
                            conf.setLocale(new Locale("fr"));
                        }
                        else
                        {
                            conf.setLocale(new Locale("en"));

                        }
                    }


                    res.updateConfiguration(conf, dm);

                    Intent intent = new Intent(getApplicationContext() , SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                } catch (Exception e) {

                }



                dialog.cancel();

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

}
