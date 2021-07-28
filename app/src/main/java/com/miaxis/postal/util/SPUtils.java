package com.miaxis.postal.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;
import java.util.Set;

/**
 * @author Admin
 * @version $
 * @des
 * @updateAuthor $
 * @updateDes
 */
public class SPUtils {

    private SharedPreferences.Editor mEditor;
    private SharedPreferences mClientAccount;

    private SPUtils() {
    }

    private static class SPUtilsHolder {
        @SuppressLint("StaticFieldLeak")
        private static SPUtils spUtils = new SPUtils();
    }

    @SuppressLint("CommitPrefEdits")
    public SPUtils init(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context can not be null");
        }
        mClientAccount = context.getSharedPreferences("Postal_Data", Context.MODE_PRIVATE);
        mEditor = mClientAccount.edit();
        return this;
    }

    public synchronized static SPUtils getInstance() {
        return SPUtilsHolder.spUtils;
    }

    public boolean write(String key, String content) {
        if (mEditor == null) {
            return false;
        }
        mEditor.putString(key, content);
        return mEditor.commit();
    }

    public boolean write(String key, float value) {
        if (mEditor == null) {
            return false;
        }
        mEditor.putFloat(key, value);
        return mEditor.commit();
    }

    public boolean write(String key, int value) {
        if (mEditor == null) {
            return false;
        }
        mEditor.putInt(key, value);
        return mEditor.commit();
    }

    public boolean write(String key, Set<String> value) {
        if (mEditor == null) {
            return false;
        }
        mEditor.putStringSet(key, value);
        return mEditor.commit();
    }

    public boolean write(Map<String, String> map) {
        if (mEditor == null) {
            return false;
        }
        if (map == null || map.isEmpty()) {
            return false;
        }
        Set<Map.Entry<String, String>> entries = map.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            mEditor.putString(entry.getKey(), entry.getValue());
        }
        return mEditor.commit();
    }

    public float read(String key, float def) {
        if (mClientAccount == null) {
            return def;
        }
        return mClientAccount.getFloat(key, def);
    }

    public int read(String key, int def) {
        if (mClientAccount == null) {
            return def;
        }
        try {
            return mClientAccount.getInt(key, def);
        } catch (Exception e) {
            e.printStackTrace();
            mEditor.remove(key);
        }
        return def;
    }

    public String read(String key, String def) {
        if (mClientAccount == null) {
            return def;
        }
        return mClientAccount.getString(key, def);
    }

    public Set<String> read(String key, Set<String> def) {
        if (mClientAccount == null) {
            return def;
        }
        return mClientAccount.getStringSet(key, def);
    }


    public boolean remove(String key) {
        if (mEditor != null) {
            mEditor.remove(key);
            return true;
        }
        return false;
    }

}
