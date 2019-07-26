package shrinkcom.waff.com.util;

import android.content.Context;
import android.content.SharedPreferences;

public class WaffPref {

    private static WaffPref _instance = null;
    private static SharedPreferences _sPrefs,_sPrefs2 = null;
    private static SharedPreferences.Editor _editor = null;

    private static final String WaffPref = "WaffApp";
    private static final String WaffPref2 = "WaffApps";

    public static final String PREFS_USER_ID = "user_id";
    public static final String PREFS_IS_LOGGEDIN = "is_logged_in";
    public static final String PREFS_IS_LOGGEDIN_VIA_FB = "is_logged_in_via_fb";

    public static final String PREFS_LOGING_VIA = "logged_in_via";
    public static final String PREFS_FULL_NAME = "full_name";
    public static final String PREFS_FIRST_NAME = "first_name";
    public static final String PREFS_LAST_NAME = "last_name";
    public static final String PREFS_USER_TYPE = "user_type";
    public static final String PREFS_USER_NAME = "user_name";
    public static final String PREFS_USER_EMAIL = "email";
    public static final String PREFS_USER_PASSWORD= "password";
    public static final String PREFS_ORIGIN= "Origin";
    public static final String PREFS_DESTINATION= "Destination";

    public WaffPref() {
    }

    public WaffPref(Context context) {
        _sPrefs = context.getSharedPreferences(WaffPref,
                Context.MODE_PRIVATE);

        _sPrefs2 = context.getSharedPreferences(WaffPref2,
                Context.MODE_PRIVATE);
    }

    public static WaffPref getInstance(Context context) {
        if (_instance == null) {
            _instance = new WaffPref(context);
        }
        return _instance;
    }

    public String readPrefs(String pref_name) {
        return _sPrefs.getString(pref_name, "");
    }

    public String writePrefs(String pref_name, String pref_val) {
        _editor = _sPrefs.edit();
        _editor.putString(pref_name, pref_val);
        _editor.commit();
        return pref_name;
    }

    public void clearPrefs() {
        _editor = _sPrefs.edit();
        _editor.clear();
        _editor.commit();
    }

    public boolean readBooleanPrefs(String pref_name) {
        return _sPrefs.getBoolean(pref_name, false);
    }

    public void writeBooleanPrefs(String pref_name, boolean pref_val) {
        _editor = _sPrefs.edit();
        _editor.putBoolean(pref_name, pref_val);
        _editor.commit();
    }

    public String readDefaultLangPrefs(String pref_name) {
        return _sPrefs.getString(pref_name, "");
    }

    public void writeDefaultLangPrefs(String pref_name) {
        _editor = _sPrefs.edit();
        _editor.putString(pref_name, pref_name);
        _editor.commit();
    }

    public String readLatLngPrefs(String pref_name) {
        return _sPrefs.getString(pref_name, "0.0");
    }

    public void writeLatLngPrefs(String pref_name, String pref_val) {
        _editor = _sPrefs.edit();
        _editor.putString(pref_name, pref_val);
        _editor.commit();
    }

    public String readBackupPrefs(String pref_name) {
        return _sPrefs2.getString(pref_name, "");
    }

    public void writeBackupPrefs(String pref_name, String pref_val) {
        SharedPreferences.Editor _editor = _sPrefs2.edit();
        _editor.putString(pref_name, pref_val);
        _editor.commit();
    }


}
