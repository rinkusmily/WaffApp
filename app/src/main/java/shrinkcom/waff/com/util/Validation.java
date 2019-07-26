package shrinkcom.waff.com.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import shrinkcom.waff.com.R;

import java.io.File;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class Validation
{
    public static void showServerError(Context ctx) {
        try {
            if (ctx != null) {
                String message = ctx.getResources().getString(
                        R.string.server_error_message);
                Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showNetworkError(Context ctx) {
        try {
            if (ctx != null) {
                String message = ctx.getResources().getString(
                        R.string.network_error_message);
                Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideLoading(ProgressDialog progress) {
        try {
            if (progress != null) {

                if (progress.isShowing()) {
                    progress.dismiss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showDownloading(ProgressBar progress) {
        if (progress != null) {
            progress.setVisibility(View.VISIBLE);
        }
    }

    public static void hideDownloading(ProgressBar progress) {
        if (progress != null) {
            progress.setVisibility(View.GONE);
        }
    }

    public static boolean isNetworkAvailable(Context context) {

        NetworkInfo localNetworkInfo = ((ConnectivityManager) context
                .getSystemService("connectivity")).getActiveNetworkInfo();

        return (localNetworkInfo != null) && (localNetworkInfo.isConnected());
    }

    public static String getDeviceId(Context context) {

        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        String build_serial = Build.SERIAL;
        return build_serial;
    }

    public boolean isPasswordcontaintSpecilaChar(String password)
    {
        Pattern specailCharPatten = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);

        if (specailCharPatten.matcher(password).find()) {

            return true ;
        }

        return false ;
    }


    public static boolean isStringNullOrBlank(String str) {
        if (str == null) {
            return true;
        } else if (str.equals("null") || str.equals("")) {
            return true;
        }
        return false;
    }

    public static void showToast(String message, Context ctx) {
        try {
            if (ctx != null)
                Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target)
                && Patterns.EMAIL_ADDRESS.matcher(target)
                .matches();
    }

    public static void printToLog(String message, Context context) {
        Log.i(context.getResources().getString(
                R.string.app_name) + " App Log ", message);
    }

    public static void hideKeyBoard(Context ct, View v) {
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) ct
                    .getSystemService(Service.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public static void hideKey(Context ct, View v) {
        // Check if no view has focus:
        InputMethodManager imm = (InputMethodManager)
                ct.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null){
            imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }



    public static String getDevicePath(Context context) {

        File directory = new File(Environment.getExternalStorageDirectory(),
                context.getResources().getString(
                        R.string.app_name));
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String path = Environment.getExternalStorageDirectory()
                + File.separator + context.getResources().getString(
                R.string.app_name) + File.separator;

        return path;
    }

    public static String getMd5Key(String device_id, String device_type, long timestamp) {
        //  sequence of md5 ($device_id,$device_type,$timestamp,$secretkey)
        String sercuretytoken = "masterkey" + device_id + device_type + timestamp;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sercuretytoken.getBytes());
            byte byteData[] = md.digest();
            //convert the byte to hex format method 1
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            Log.e("Digest(in hex format)= ", "" + sb.toString());
            //convert the byte to hex format method 2
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                String hex = Integer.toHexString(0xff & byteData[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            Log.e("Digest(in hex format)= ", "" + hexString.toString());

            return hexString.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static long getTimeStamp() {
        return System.currentTimeMillis();
    }

    public static boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    public static int getDeviceWidth(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int width=display.getWidth();
        return width;
    }


    public static int getDeviceHeight(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int width=display.getHeight();
        return width;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


    public static String formattedDateFromString(String inputFormat, String outputFormat, String inputDate) {
        if (inputFormat.equals("")) { // if inputFormat = "", set a default
            // input format.
            inputFormat = "dd-MM-yyyy";
        }
        if (outputFormat.equals("")) {
            outputFormat = "EEEE d MMMM  yyyy";
            // if inputFormat =
            // "", set a default
            // output format.
        }
        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, java.util.Locale.getDefault());

        // You can set a different Locale, This example set a locale of Country
        // Mexico.
        // SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, new
        // Locale("es", "MX"));
        // SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, new
        // Locale("es", "MX"));

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);
        } catch (Exception e) {
            Log.e("formattedDateFromString", "Exception in formateDateFromstring(): " + e.getMessage());
        }
        return outputDate;

    }



    public static int compareDates(Calendar firstDate , Calendar SecondDate)
    {
        if (firstDate.get(Calendar.YEAR) > SecondDate.get(Calendar.YEAR))
        {
            return 1 ;
        }

        else
        {
            if (firstDate.get(Calendar.YEAR) == SecondDate.get(Calendar.YEAR))
            {
                if (firstDate.get(Calendar.MONTH) > SecondDate.get(Calendar.MONTH))
                {
                    return 1 ;
                }
                else
                {

                    if (firstDate.get(Calendar.MONTH) == SecondDate.get(Calendar.MONTH))

                    {
                        Log.e("firser " ,firstDate.get(Calendar.MONTH)+" "+SecondDate.get(Calendar.MONTH));

                        if (firstDate.get(Calendar.DATE) > SecondDate.get(Calendar.DATE))
                        {
                            return 1 ;
                        }
                        else
                        {
                            if (firstDate.get(Calendar.DATE) == SecondDate.get(Calendar.DATE))
                            {
                                return 0 ;
                            }
                        }
                    }

                }


            }
        }


        return -1;
    }
}
