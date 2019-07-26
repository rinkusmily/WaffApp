package shrinkcom.waff.com.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.mapbox.api.directions.v5.MapboxDirections;
import shrinkcom.waff.com.R;
import shrinkcom.waff.com.bean.UserData;
import shrinkcom.waff.com.util.SessionManager;
import shrinkcom.waff.com.util.SqliteDB;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;


public class SplashActivity extends AppCompatActivity {
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    SessionManager sessionManager ;
    SqliteDB sqliteDB ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sessionManager = new SessionManager(SplashActivity.this);
        sqliteDB = new SqliteDB(this);

        sessionManager = new SessionManager(this);

        EditText hdhdh = findViewById(R.id.hdhdh);
        hdhdh.setVisibility(View.GONE);
        try {
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            android.content.res.Configuration conf = res.getConfiguration();

            if (!TextUtils.isEmpty(sessionManager.getLanguage())) {
                if (sessionManager.getLanguage().equalsIgnoreCase("english")) {
                    conf.setLocale(new Locale("en"));

                } else {
                    conf.setLocale(new Locale("fr"));

                }
            } else {
                String defaultDeviceLanguage = Locale.getDefault().getDisplayLanguage();
                sessionManager.setLanguage("french");
                conf.setLocale(new Locale("fr"));
            }


            res.updateConfiguration(conf, dm);




        }
            catch (Exception e) {


            }

        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("shrinkcom.waff.com", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
                hdhdh.setVisibility(View.GONE);
            }
        } catch (PackageManager.NameNotFoundException e1) {
        } catch (NoSuchAlgorithmException e) {
        } catch (Exception e) {
        }


        /*
         * Showing splash_screen with a timer. This will be useful when you
         * want to show case your app logo / company
         */
        new Handler().postDelayed(() -> {

            try {

                UserData userData = sessionManager.getUser();


                if (!TextUtils.isEmpty(userData.getEmail()))
                {
                    Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                    sessionManager.saveUser(userData);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
                else
                {
                    sessionManager.clear();
                    sqliteDB.clearTable();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

            }
            catch (Exception e)
            {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }

            finish();
        }, SPLASH_TIME_OUT);

    }




}
