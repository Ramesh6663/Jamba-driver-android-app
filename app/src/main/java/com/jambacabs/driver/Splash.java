package com.jambacabs.driver;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnFailureListener;
import com.jambacabs.driver.singleton.UserSession;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import java.util.Objects;

import timber.log.Timber;

import static com.jambacabs.driver.singleton.Constants.MOVE_SCREEN_DOCUMENTS;
import static com.jambacabs.driver.singleton.Constants.MOVE_SCREEN_VEHICLE;
import static com.jambacabs.driver.singleton.Constants.MOVE_SCREEN_VEHICLE_DOC;


public class Splash extends AppCompatActivity {

    private static int APP_UPDATE_CODE = 1010;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.black));

        generateToken();

        String tag_from = new UserSession(Splash.this).getTagFrom();
        if (!tag_from.equals("")) {
           pageNavigation();
        }else
        {
            checkAppUpdate();
        }
    }

    private void checkAppUpdate()
    {
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);
        com.google.android.play.core.tasks.Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE))
            {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo, AppUpdateType.IMMEDIATE, this, APP_UPDATE_CODE);
                } catch (IntentSender.SendIntentException e) {
                    Timber.v("exc%s", e.getLocalizedMessage());
                }
            }else
            {
                pageNavigation();
            }
        }).addOnFailureListener(e -> pageNavigation());
    }

    private void generateToken() {
        String token = new UserSession(this).getToken();
        if (token.equals("")) {
            FirebaseApp.initializeApp(Splash.this);
            Task<InstanceIdResult> data = FirebaseInstanceId.getInstance().getInstanceId();
            data.addOnCompleteListener(task -> {
                try {
                    String token1 = Objects.requireNonNull(task.getResult()).getToken();
                    if (token1.isEmpty()) {
                        token1 = "";
                    }
                    new UserSession(Splash.this).setToken(token1);
                } catch (Exception e) {
                    Timber.v("exception%s", e.getLocalizedMessage());
                }
            });
        }
    }

    private void pageNavigation()
    {
        new Handler().postDelayed(() -> {
            String tag_from = new UserSession(Splash.this).getTagFrom();
            if (!tag_from.equals(""))
            {
                String screen = new UserSession(Splash.this).getScreen();
                String chatDetails = new UserSession(Splash.this).getChatDetails();
                if (!screen.equals("admin_info") && chatDetails.equals(""))
                {
                    Intent intent = new Intent(Splash.this, Dashboard.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else
                {
                    if (!chatDetails.equals(""))
                    {
                        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        assert nMgr != null;
                        nMgr.cancelAll();

                        Intent intent = new Intent(Splash.this, Chat.class);
                        intent.putExtra("parameters", chatDetails);
                        startActivity(intent);
                        overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
                        finish();
                    }else {
                        boolean is_user_loged = new UserSession(Splash.this).isUserLoggedIn();
                        if (is_user_loged)
                        {
                            Intent intent = new Intent(Splash.this, Inbox.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
                            finish();

                        }else
                        {
                            Intent intent = new Intent(Splash.this, Dashboard.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
                            finish();
                        }
                    }
                }
            }else
            {
                String str = new UserSession(Splash.this).getScreen();
                if (!str.equals("")) {
                    Intent intent;
                    switch (str)
                    {
                        case MOVE_SCREEN_DOCUMENTS:
                            intent = new Intent(Splash.this, Documents.class);
                            intent.putExtra("fromSplash", true);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            break;
                        case MOVE_SCREEN_VEHICLE:
                            intent = new Intent(Splash.this, AddVehicleActivity.class);
                            intent.putExtra("fromSplash", true);
                            intent.putExtra("fromDoc", true);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            break;
                        case MOVE_SCREEN_VEHICLE_DOC:
                            intent = new Intent(Splash.this, VehicleDocumentsList.class);
                            intent.putExtra("fromSplash", true);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            break;
                        default:
                            String str_response = new UserSession(Splash.this).getPushParams();
                            String screen = new UserSession(Splash.this).getScreen();
                            new UserSession(Splash.this).setRideInfo(str_response);

                            intent = new Intent(Splash.this, Dashboard.class);
                            intent.putExtra("screen", screen);
                            intent.putExtra("parameters", str_response);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            break;
                    }
                } else {
                    boolean is_user_logged = new UserSession(Splash.this).isUserLoggedIn();
                    if (is_user_logged) {
                        Intent data_intent = getIntent();
                        if (data_intent.hasExtra("tagFrom"))
                        {
                            String tagFrom = data_intent.getStringExtra("tagFrom");
                            String screen = data_intent.getStringExtra("screen");
                            String response = data_intent.getStringExtra("parameters");
                            new UserSession(Splash.this).setTagFrom(tagFrom);
                            new UserSession(Splash.this).setScreen(screen);
                            new UserSession(Splash.this).setPushParams(response);
                        }
                        Intent intent = new Intent(Splash.this, Dashboard.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(Splash.this, Home.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            }
        }, 750);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_UPDATE_CODE) {
            /*if (resultCode != RESULT_OK) {
                checkAppUpdate();
            }else
            {
                pageNavigation();
            }*/
            pageNavigation();
        }
    }

}
