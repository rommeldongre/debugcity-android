package com.greylabs.sumod.dbct10;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Sumod on 25-Nov-15.
 */
public class PrefManager {

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared pref file name
    private static final String PREF_NAME = "DebugCity";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";

    public static final String LOGIN_SESSION_CODE = "login_session_code";

    public final int EMAIL_LOGIN_SESSION = 0;

    public final int GOOGLE_LOGIN_SESSION = 1;

    public final int FB_LOGIN_SESSION = 2;



    // Constructor
    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(String email, int loginSessionCode) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);

        //Storing login session code
        editor.putInt(LOGIN_SESSION_CODE, loginSessionCode);

        // commit changes
        editor.commit();
    }

    public void putLoginSessionCode(int code){
        editor.putInt(LOGIN_SESSION_CODE, code);
    }

    public int getLoginSessionCode(){
        return pref.getInt(LOGIN_SESSION_CODE, -1);
    }


    public String getEmail() {
        return pref.getString(KEY_EMAIL, null);
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void logout() {
        editor.clear();
        editor.commit();
    }



}
