package com.popularmovies2.munish.popularmovies2;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Munish on 3/26/2016.
 */
public class Utility
{
    public static String getPreferedRequest(Context context){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        return pref.getString(context.getString(R.string.pref_request_key),
                context.getString(R.string.pref_request_default));

    }

}
