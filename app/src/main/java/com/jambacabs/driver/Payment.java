package com.jambacabs.driver;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.jambacabs.driver.R;
import com.jambacabs.driver.font.CustomTextView;
import com.jambacabs.driver.singleton.UserSession;
import com.jambacabs.driver.singleton.Utilities;

public class Payment extends Activity implements View.OnClickListener {
    private boolean is_edited;
    private boolean isBackPressed;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_layout);
        int flags = getWindow().getDecorView().getSystemUiVisibility();
        flags = flags ^ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        getWindow().getDecorView().setSystemUiVisibility(flags);
        Utilities.setFirebaseAnalytics(Payment.this);
        RelativeLayout rlAccount = findViewById(R.id.rlAccount);
        RelativeLayout rlAddAccount = findViewById(R.id.rlAddAccount);
        CustomTextView tvAccountNumber = findViewById(R.id.tvAccountNumber);
        is_edited = false;

        String account = new UserSession(this).getAccount();
        if (account.equals("")) {
            rlAddAccount.setVisibility(View.VISIBLE);
            rlAccount.setVisibility(View.GONE);
        } else {
            String number = new UserSession(Payment.this).getAccount();
            String substring = number.substring(Math.max(number.length() - 2, 0));
            String account_number = getResources().getString(R.string.bank_text);
            account_number = account_number + " " + substring;
            tvAccountNumber.setText(account_number);
            rlAddAccount.setVisibility(View.GONE);
            rlAccount.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.llBank) {
            is_edited = true;
            openAddAccountView();
        } else if (id == R.id.rlAddAccount) {
            is_edited = false;
            openAddAccountView();
        } else if (id == R.id.rlPaymentBack) {
            onBackPressed();
        }
    }

    public void openAddAccountView(){
        Intent intent = new Intent(Payment.this, AddAccount.class);
        if (is_edited)
        {
            intent.putExtra("tag", "edit");
        }
        startActivity(intent);
        overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (!isBackPressed)
        {
            isBackPressed = true;
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(Payment.this, Dashboard.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.move_right_enter, R.anim.move_right_exit);
                finish();
            }, 200);
        }
    }
}
