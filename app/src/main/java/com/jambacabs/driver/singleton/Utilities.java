package com.jambacabs.driver.singleton;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.jambacabs.driver.JambaCabApplication;
import com.jambacabs.driver.R;
import com.jambacabs.driver.font.CustomEditTextView;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.jambacabs.driver.singleton.Constants.FIREBASE_ANALYTICS_OPEN_SCREEN;
import static com.jambacabs.driver.singleton.Constants.FIREBASE_ANALYTICS_SCREEN_NAME;

public class Utilities {
    private Utilities() {

    }

    public static boolean isInternetAvailable(Context context) {
        boolean isNetAvailable = false;
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
//                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                NetworkInfo info = connectivity.getActiveNetworkInfo();

                if (info != null) {
                    String name = info.getTypeName();
                    String subName = info.getSubtypeName();
                    if (name.equals("WIFI")) {
                        if (info.getState() == NetworkInfo.State.CONNECTED) {
                            isNetAvailable = true;
                        }
                    } else if (name.equals("MOBILE")) {
                        if (subName.equals("")) {
                            if (info.getState() == NetworkInfo.State.CONNECTED) {
                                isNetAvailable = true;
                            }
                        } else {
                            if (info.getState() == NetworkInfo.State.CONNECTED) {
                                isNetAvailable = true;
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return isNetAvailable;
    }

    /**
     * Method to hide keyboard start
     */
    public static void hideSoftKeyboard(Activity activity) {

        InputMethodManager inputMethodManager = (InputMethodManager) activity
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        if (inputMethodManager.isAcceptingText()) {
            try {
                inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(activity
                        .getCurrentFocus()).getWindowToken(), 0);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static Boolean changeEditTextLineColor(Activity activity, String tag, EditText editText) {
        boolean is_validated;
        if (tag.equals("show")) {
            is_validated = false;
            editText.getBackground().mutate().setColorFilter(ContextCompat.getColor(activity,R.color.holo_red_color), PorterDuff.Mode.SRC_ATOP);
        } else {
            is_validated = true;
            editText.getBackground().mutate().setColorFilter(ContextCompat.getColor(activity,R.color.black), PorterDuff.Mode.SRC_ATOP);
        }
        return is_validated;
    }


    public static void showSoftKeyboard(Activity activity, CustomEditTextView editText) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void showSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }


    public static void hideStatusBar(Activity activity) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public static void showStatusBar(Activity activity) {
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public static void startShakeAnimation(Activity context, View view) {
        Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
        view.startAnimation(shake);
    }

    public static void startBubleAnimation(Activity context, View view) {
        Animation buble = AnimationUtils.loadAnimation(context, R.anim.bubble_animation);
        view.startAnimation(buble);
    }

    public static void startBlinkAnimation(Activity context, View view) {
        Animation blink = AnimationUtils.loadAnimation(context, R.anim.blink);
        view.startAnimation(blink);
    }

    public static void startSlideUpAnimation(Activity context, View view) {
        Animation slide_up = AnimationUtils.loadAnimation(context, R.anim.slide_up);
        view.startAnimation(slide_up);
    }

    public static void startSlideDownAnimation(Activity context, View view) {
        Animation slide_down = AnimationUtils.loadAnimation(context, R.anim.slide_down);
        view.startAnimation(slide_down);
    }

    public static void showMessage(View view, String msg) {
        try {
            Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);

            final View snack_view = snackbar.getView();
            final TextView tv = snack_view.findViewById(com.google.android.material.R.id.snackbar_text);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimension(R.dimen.fifteendp));
            snackbar.show();
        } catch (Exception e) {
            Log.v("Exception", "Exception" + e.toString());
        }
    }

    public static void multilineShowMessage(View view, String msg) {
        try {
            Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);

            final View snack_view = snackbar.getView();
            final TextView tv = snack_view.findViewById(com.google.android.material.R.id.snackbar_text);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimension(R.dimen.fifteendp));
            tv.setMaxLines(5);
            snackbar.show();
        } catch (Exception e) {
            Log.v("Exception", "Exception" + e.toString());
        }
    }

    public static void setFirebaseAnalytics(Activity activity) {
        Bundle bundle = new Bundle();
        bundle.putString(FIREBASE_ANALYTICS_SCREEN_NAME, activity.getClass().getSimpleName());
        JambaCabApplication.getmFirebaseAnalytics().logEvent(FIREBASE_ANALYTICS_OPEN_SCREEN, bundle);
    }

    public static String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        return DateFormat.format("dd/MM/yyyy", cal).toString();
    }

    public static String getDatesDifferent(String currentDate, String documentDate) {
        try {

            Date date1;
            Date date2;

            SimpleDateFormat dates = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            //Setting dates
            date1 = dates.parse(currentDate);
            date2 = dates.parse(documentDate);

            //Comparing dates
            long difference = 0;
            if (date1 != null && date2 != null)
            {
                difference = Math.abs(date1.getTime() - date2.getTime());
            }
            long differenceDates = difference / (24 * 60 * 60 * 1000);

            //Convert long to String
            return Long.toString(differenceDates);

            //Log.e("HERE","HERE: " + dayDifference);

        } catch (Exception exception) {
            Log.e("DIDN'T WORK", "exception " + exception);
            return null;
        }
    }

    public static String getCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return df.format(c);
    }

    public static List<Date> getDates(String dateString1, String dateString2) {
        ArrayList<Date> dates = new ArrayList<Date>();
        java.text.DateFormat df1 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1.parse(dateString1);
            date2 = df1.parse(dateString2);
        } catch (ParseException e) {
           Log.v("parseError", "ParseError"+e.getLocalizedMessage());
        }

        Calendar cal1 = Calendar.getInstance();
        if (date1 != null) {
            cal1.setTime(date1);
        }


        Calendar cal2 = Calendar.getInstance();
        if (date2 != null) {
            cal2.setTime(date2);
        }

        while (!cal1.after(cal2)) {
            dates.add(cal1.getTime());
            cal1.add(Calendar.DATE, 1);
        }
        return dates;
    }

    public static String getEndTripDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        return DateFormat.format("dd/MM/yyyy", cal).toString();

    }

    public static String getEndTripTime(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        return DateFormat.format("hh:mm a", cal).toString();
    }

    public static String getTransactionDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        return DateFormat.format("EEE, MMM, dd HH:mm", cal).toString();

    }

    public static String getEarningDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        return DateFormat.format("dd/MM", cal).toString();
    }

    public static String getRealPathFromURI (Context context,Uri contentUri) {
        String path = null;
        String[] proj = { MediaStore.MediaColumns.DATA };
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            path = cursor.getString(column_index);
            cursor.close();
        }
        return path;
    }

    public static String getRideTime(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        return DateFormat.format("hh:mm a", cal).toString();
    }

    public static String getFullTripDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        return DateFormat.format("EEE, MMM dd, hh:mm a", cal).toString();
    }

    public static String getURL(Context context, String val)
    {
        String apiKey = new UserSession(context).getAPI();
        if (apiKey.equals(""))
        {
            apiKey = context.getResources().getString(R.string.map_api_key);
        }
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        return "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + val + "&key=" + apiKey + "&sessiontoken=" + token +"&components=country:ind&language=en";
    }
}
