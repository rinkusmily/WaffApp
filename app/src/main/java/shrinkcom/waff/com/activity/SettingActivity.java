package shrinkcom.waff.com.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import shrinkcom.waff.com.R;
import shrinkcom.waff.com.util.SessionManager;

import java.util.ArrayList;
import java.util.Locale;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout ll_change_pass, linear_change_edit_prof, linear_genral, layout_helpcenter, linear_map, linear_privecy, linear_feedback, linearWatch, layout_about, linear_alert;
    private LinearLayout linear_sound_voice, linearNotifi, linear_socialnew , chooseLanguageLayout;
    private Context mContext;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mContext = this;
        initviews();
    }
    SessionManager sessionManager ;

    private void initviews() {
        toolbar =  findViewById(R.id.toolbar);
        linear_socialnew =  findViewById(R.id.linear_socialnew);
        linearNotifi = findViewById(R.id.linearNotifi);
        linear_sound_voice =  findViewById(R.id.linear_sound_voice);
        linear_alert =  findViewById(R.id.linear_alert);
        layout_about = findViewById(R.id.layout_about);
        linear_feedback = findViewById(R.id.linear_feedback);
        linear_privecy =  findViewById(R.id.linear_privecy);
        linear_map =  findViewById(R.id.linear_map);
        layout_helpcenter =  findViewById(R.id.layout_helpcenter);
        linear_genral =  findViewById(R.id.linear_genral);
        linear_change_edit_prof =  findViewById(R.id.linear_change_edit_prof);
        ll_change_pass =  findViewById(R.id.ll_change_pass);
        chooseLanguageLayout = findViewById(R.id.choose_language_layout);


        toolbar.setNavigationOnClickListener(v -> finish());
        linear_change_edit_prof.setOnClickListener(this);
        ll_change_pass.setOnClickListener(this);
        linear_genral.setOnClickListener(this);
        layout_helpcenter.setOnClickListener(this);
        linear_map.setOnClickListener(this);
        linear_privecy.setOnClickListener(this);
        linear_feedback.setOnClickListener(this);
        linear_sound_voice.setOnClickListener(this);
        layout_about.setOnClickListener(this);
        linear_alert.setOnClickListener(this);
        linearNotifi.setOnClickListener(this);
        linear_socialnew.setOnClickListener(this);
        chooseLanguageLayout.setOnClickListener(this);



        sessionManager = new SessionManager(this);

    }

    //https://shrinkcom.com/waff/help-center.php
    //

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.layout_about:


                Intent intent = new Intent(mContext, WebviewActivity.class);
                intent.putExtra("mToolbar", "About");
                intent.putExtra("link", "https://shrinkcom.com/waff/about-us.php");
                startActivity(intent);

                break;
            case R.id.linear_feedback:
                startActivity(new Intent(mContext, WebviewActivity.class).putExtra("mToolbar", "Feedback"));
                break;
            case R.id.linear_privecy:


                //


                intent = new Intent(mContext, WebviewActivity.class);

                intent.putExtra("mToolbar", "Privacy Setting");

                intent.putExtra("link", "https://shrinkcom.com/waff/privacy-policy.php");


                startActivity(intent);
                break;
            case R.id.layout_helpcenter:

                intent = new Intent(mContext, WebviewActivity.class);

                intent.putExtra("mToolbar", "Help Center");

                intent.putExtra("link", "https://shrinkcom.com/waff/privacy-policy.php");
                startActivity(intent);


                break;


            case R.id.linear_change_edit_prof:
                startActivity(new Intent(mContext, EditProfileActivity.class));
                break;
            case R.id.ll_change_pass:
                startActivity(new Intent(mContext, ChangePasswordActivity.class));
                break;



            case R.id.choose_language_layout:

                changeLungage();
                break;
        }
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
