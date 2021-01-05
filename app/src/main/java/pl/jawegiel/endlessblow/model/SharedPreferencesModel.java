package pl.jawegiel.endlessblow.model;

import android.content.Context;
import android.content.SharedPreferences;

import pl.jawegiel.endlessblow.interfaces.CredentialsContract;

public class SharedPreferencesModel implements CredentialsContract.Model.SharedPreferencesModel {

    public static final String USER_LOGIN = "userLogin";
    public static final String USER_PASSWORD = "userPassword";
    public static final String CB_SAVE_CREDENTIALS = "cbSaveCredentials";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    public SharedPreferencesModel(Context context) {
        pref = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    @Override
    public void saveSomeCredentials(String nameLogin, String passLogin) {
        editor.putString(USER_LOGIN, nameLogin); // Storing string
        editor.putString(USER_PASSWORD, passLogin); // Storing string
        editor.putBoolean(CB_SAVE_CREDENTIALS, true);
        editor.apply();
    }

    @Override
    public void saveEmptyCredentials() {
        editor.putString(USER_LOGIN, ""); // Storing string
        editor.putString(USER_PASSWORD, ""); // Storing string
        editor.putBoolean(CB_SAVE_CREDENTIALS, false);
        editor.apply();
    }

    @Override
    public String getNameCredential() {
        return pref.getString(USER_LOGIN, "");
    }

    @Override
    public String getPassCredential() {
        return pref.getString(USER_PASSWORD, "");
    }

    @Override
    public boolean getCbCredential() {
        return pref.getBoolean(CB_SAVE_CREDENTIALS, false);
    }
}
