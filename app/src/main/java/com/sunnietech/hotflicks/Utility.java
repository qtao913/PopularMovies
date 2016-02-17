package com.sunnietech.hotflicks;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by qlzh727 on 1/1/16.
 */
public class Utility {
    public static String getPreferredMovieSorting(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.sorting_preference_key),
                context.getString(R.string.most_popular_value));
    }

    public static String fetchRawJson(Uri uri){
        final String LOG_TAG = Utility.class.getSimpleName() + " fetch Row Json";
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String movieJsonStr = null;
        try {
            URL url = new URL(uri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                return null;
            }
            movieJsonStr = buffer.toString();
        } catch (IOException e) {
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {

                }
            }
        }
        return movieJsonStr;
    }

//    public static String fetchJson(Uri uri) {
//        try {
//            URL url = new URL(uri.toString());
//            InputStream is = url.openStream();
//            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
//            StringBuilder sb = new StringBuilder();
//            int cp;
//            while ((cp = rd.read()) != -1) {
//                sb.append((char) cp);
//            }
//            return sb.toString();
//        } catch (IOException e) {
//            return null;
//        }
//    }

    public static String numberConvert(String num) {
        final double BILLION = 1000000000.0;
        final double MILLION = 1000000.0;
        long number = Long.parseLong(num);
        StringBuilder sb = new StringBuilder();
        if (number >= BILLION) {
            double n = Utility.round(number / BILLION, 2);
            sb.append(Double.toString(n));
            sb.append("\nBillion");
            return sb.toString();
        } else if (number >= MILLION) {
            double n = Utility.round(number / MILLION, 2);
            sb.append(Double.toString(n));
            sb.append("\nMillion");
            return sb.toString();
        }
        return Long.toString(number);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
