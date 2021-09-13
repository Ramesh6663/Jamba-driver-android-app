package com.jambacabs.driver;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.smartreply.FirebaseSmartReply;
import com.google.firebase.ml.naturallanguage.smartreply.FirebaseTextMessage;
import com.google.firebase.ml.naturallanguage.smartreply.SmartReplySuggestion;
import com.google.gson.Gson;
import com.jambacabs.driver.adaptor.ChatAdaptor;
import com.jambacabs.driver.adaptor.SuggestionsAdaptor;
import com.jambacabs.driver.font.CustomEditTextView;
import com.jambacabs.driver.font.CustomTextView;
import com.jambacabs.driver.models.ChatModel;
import com.jambacabs.driver.callbacks.ISuggestions;
import com.jambacabs.driver.singleton.Constants;
import com.jambacabs.driver.singleton.UserSession;
import com.jambacabs.driver.singleton.Utilities;
import com.jambacabs.driver.singleton.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Chat extends Activity implements View.OnClickListener, ISuggestions
{
    private ArrayList<ChatModel> arrChatDetails;
    private CustomEditTextView etMessage;
    private ChatAdaptor adaptor;
    private SuggestionsAdaptor suggestionsAdaptor;
    private RecyclerView rvChat;
    private DatabaseReference chatDatabase;
    private DatabaseReference chatDetails;
    private ValueEventListener listener;
    private String rideId;
    private boolean isInserted;
    private  String userNotificationId;
    private boolean isBackPressed;
    private ArrayList<String> arrMessageSuggestions;
    private RelativeLayout rlSuggestions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);
        int flags = getWindow().getDecorView().getSystemUiVisibility();
        flags = flags ^ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        getWindow().getDecorView().setSystemUiVisibility(flags);
        showHideLoading(true);
        initUI();
    }

    private void initUI()
    {
        rvChat = findViewById(R.id.rvChat);
        etMessage = findViewById(R.id.etMessage);
        ImageView ivUser = findViewById(R.id.ivUser);
        CustomTextView tvName = findViewById(R.id.tvName);
        CustomTextView tvNumber = findViewById(R.id.tvNumber);
        RecyclerView rvSuggestions = findViewById(R.id.rvSuggestions);

        rlSuggestions = findViewById(R.id.rlSuggestions);
        arrMessageSuggestions = new ArrayList<>();
        Intent intent = getIntent();
        if (intent != null)
        {
            String fullName = "";
            String avatar = "";
            String mobileNumber = "";
            if (intent.hasExtra("fullName"))
            {
                fullName = intent.getStringExtra("fullName");
                avatar = intent.getStringExtra("avatar");
                mobileNumber = intent.getStringExtra("mobileNumber");
                rideId = intent.getStringExtra("rideId");
                userNotificationId = intent.getStringExtra("user_notification_id");
            }else
            {
                String params = intent.getStringExtra("parameters");
                try {
                    if (params != null) {
                        JSONObject dictParams = new JSONObject(params);
                        String strMessageData = dictParams.optString("messageData");
                        JSONObject dictMessageData = new JSONObject(strMessageData);
                        fullName = dictMessageData.optString("fullName");
                        avatar = dictMessageData.optString("avatar");
                        mobileNumber = dictMessageData.optString("mobileNumber");
                        rideId = dictMessageData.optString("rideId");
                        userNotificationId = dictMessageData.optString("user_notification_id");
                    }
                } catch (JSONException e) {
                    Log.v("E", "E"+e.getLocalizedMessage());
                }
                new UserSession(Chat.this).removeChatDetails();
            }
            tvName.setText(fullName);
            tvNumber.setText(mobileNumber);
            Uri uri =  Uri.parse(avatar);
            Glide.with(getApplicationContext()).load(uri).asBitmap().error(R.drawable.ic_account_circle_white).placeholder(R.drawable.ic_account_circle_white).centerCrop().into(new BitmapImageViewTarget(ivUser) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    ivUser.setImageDrawable(circularBitmapDrawable);
                }
            });
        }
        chatDatabase = FirebaseDatabase.getInstance().getReference(Constants.CHAT_DETAILS);
        arrChatDetails = new ArrayList<>();
        adaptor = new ChatAdaptor(this, this);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        adaptor.setListContent(arrChatDetails);
        rvChat.setAdapter(adaptor);
        adaptor.notifyDataSetChanged();

        suggestionsAdaptor = new SuggestionsAdaptor(this, this);
        rvSuggestions.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        suggestionsAdaptor.setListContent(arrMessageSuggestions);
        rvSuggestions.setAdapter(suggestionsAdaptor);
        suggestionsAdaptor.notifyDataSetChanged();

        rlSuggestions.setVisibility(View.GONE);

        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }
            @Override
            public void afterTextChanged(Editable s)
            {
                int count = 0;
                if (!arrChatDetails.isEmpty())
                {
                    count = arrChatDetails.size()-1;
                }
                rvChat.scrollToPosition(count);
            }
        });
        fetchChatData();
    }

    /*
     *
     * fetchChatData
     *
     */

    private void fetchChatData()
    {
        chatDetails = chatDatabase.child(rideId);
        listener =  chatDetails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!isInserted)
                {
                    Object snapShot = dataSnapshot.getValue();
                    if (snapShot != null) {
                        Gson gson = new Gson();
                        String json = gson.toJson(snapShot);
                        try {
                            JSONObject dictJson = new JSONObject(json);
                            JSONArray arrNames = dictJson.names();
                            if (arrNames != null) {
                                if (!arrChatDetails.isEmpty())
                                {
                                    arrChatDetails.clear();
                                }
                                for (int i = 0; i < dictJson.length(); i++)
                                {
                                    JSONObject dictElements = dictJson.optJSONObject((String) arrNames.get(i));
                                    if (dictElements != null)
                                    {
                                        ChatModel model = gson.fromJson(Objects.requireNonNull(dictElements).toString(), ChatModel.class);
                                        long chatId = model.getChatId();
                                        String strChatId = String.valueOf(chatId);
                                        if (model.getRole().equals(Constants.USER) && !model.isRead())
                                        {
                                            model.setRead(true);
                                            model.setOnline(true);
                                            isInserted = true;
                                            arrChatDetails.add(model);
                                            chatDatabase.child(rideId).child(strChatId).setValue(model).addOnSuccessListener(aVoid -> {
                                                int index = arrChatDetails.indexOf(model);
                                                if (index>0 && index<arrChatDetails.size()-1)
                                                {
                                                    arrChatDetails.remove(model);
                                                    adaptor.notifyDataSetChanged();
                                                    arrChatDetails.add(index, model);
                                                    adaptor.notifyDataSetChanged();
                                                    rvChat.scrollToPosition(arrChatDetails.size()-1);
                                                }
                                            });
                                        }else
                                        {
                                            if (!arrChatDetails.contains(model))
                                            {
                                                arrChatDetails.add(model);
                                            }else
                                            {
                                                int index = arrChatDetails.indexOf(model);
                                                arrChatDetails.remove(model);
                                                arrChatDetails.add(index, model);
                                            }
                                        }
                                    }
                                }
                                if (!arrChatDetails.isEmpty())
                                {
                                    Collections.sort(arrChatDetails, (o1, o2) -> (int) (o1.getChatId() - o2.getChatId()));
                                    adaptor.notifyDataSetChanged();
                                    rvChat.scrollToPosition(arrChatDetails.size()-1);

                                    if (!arrMessageSuggestions.isEmpty())
                                    {
                                        arrMessageSuggestions.clear();
                                    }
                                    ChatModel model = arrChatDetails.get(arrChatDetails.size()-1);
                                    boolean isLocal = false;
                                    if (model.getRole().equals(Constants.DRIVER))
                                    {
                                        isLocal = true;
                                    }
                                    getSuggestions(model.getMessage(), String.valueOf(model.getUserId()), isLocal);

                                }
                                showHideLoading(false);
                            }else
                            {
                                showHideLoading(false);
                            }
                        }catch (JSONException e)
                        {
                            showHideLoading(false);
                            Log.v("Exc == ", "Exc = "+e.getLocalizedMessage());
                        }
                    }else
                    {
                        showHideLoading(false);
                    }
                }else
                {
                    showHideLoading(false);
                    isInserted = false;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                showHideLoading(false);
                Log.v("cancelled", "cancelled"+databaseError.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        if (id == R.id.rlChatBack)
        {
            onBackPressed();
        }else if (id == R.id.rlMain || id == R.id.rlChat)
        {
            Utilities.hideSoftKeyboard(Chat.this);
        }else if (id == R.id.rlSend)
        {
            if (!Objects.requireNonNull(etMessage.getText()).toString().equals(""))
            {
                sendMessage(etMessage.getText().toString());
            }
        }
    }

    private void sendMessage(String message)
    {
        ChatModel model = new ChatModel();
        long userId = Long.parseLong(new UserSession(Chat.this).getUserId());
        long chatId = new Date().getTime();
        String strChatId = String.valueOf(chatId);
        model.setChatId(chatId);
        model.setUserId(userId);
        model.setRole(Constants.DRIVER);
        model.setRead(false);
        model.setOnline(false);
        model.setMessage(message);
        rlSuggestions.setVisibility(View.GONE);
        etMessage.setText("");
        isInserted = true;
        chatDatabase.child(rideId).child(strChatId).setValue(model).addOnSuccessListener(aVoid -> {
            arrChatDetails.add(model);
            rvChat.scrollToPosition(arrChatDetails.size()-1);
            adaptor.notifyDataSetChanged();

            Gson gson = new Gson();
            String strObject = gson.toJson(model);
            JSONObject dictData = new JSONObject();
            JSONObject dictParams = new JSONObject();
            JSONObject dictNotification = new JSONObject();
            String strUserData = new UserSession(Chat.this).getUserData();
            try {
                JSONObject dictUserData = new JSONObject(strUserData);
                String firstName = dictUserData.optString("firstName");
                String lastName = dictUserData.optString("lastName");
                String s1 = firstName.substring(0, 1).toUpperCase();
                firstName = s1 + firstName.substring(1);
                String s2 = lastName.substring(0, 1).toUpperCase();
                lastName = s2 + lastName.substring(1);
                String fullName = firstName + " " + lastName;
                String avatar = dictUserData.optString("avatar");
                String mobileNumber = dictUserData.optString("mobileNumber");
                JSONObject object = new JSONObject(strObject);
                object.put("fullName", fullName);
                object.put("avatar", avatar);
                object.put("mobileNumber", mobileNumber);
                object.put("rideId", rideId);
                object.put("user_notification_id", userNotificationId);

                dictData.put("messageData", object);
                dictData.put("body", "You have a new message");
                dictData.put("icon", "fcm_push_icon");
                dictData.put("text", model.getMessage());
                dictData.put("type", "message");
                dictData.put("sound", "default");
                dictData.put("title", fullName);

                dictNotification.put("messageData", object);
                dictNotification.put("body", "You have a new message");
                dictNotification.put("icon", "fcm_push_icon");
                dictNotification.put("text", model.getMessage());
                dictNotification.put("type", "message");
                dictNotification.put("sound", "default");
                dictNotification.put("title", fullName);
                JSONArray list = new JSONArray();
                list.put(userNotificationId);
                dictParams.put("data", dictData);
                dictParams.put("registration_ids", list);
                dictParams.put("priority", "high");
                dictParams.put("time_to_live", 20);
                sendNotification(dictParams, model.getMessage(), String.valueOf(model.getUserId()));
            } catch (Exception e) {
                Log.v("Exception e", "Exception"+e.getLocalizedMessage());
            }
        });
    }

    private void sendNotification(JSONObject dictParams, String message,String userId)
    {
        String append_url = Constants.PUSH_URL + Constants.SEND;
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, append_url, dictParams,
                response -> {
                    if(!arrMessageSuggestions.isEmpty())
                    {
                        arrMessageSuggestions.clear();
                    }
                    getSuggestions(message, userId, true);
                },
                error -> Log.v("success", "success"+error.getLocalizedMessage())
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", Constants.SERVER_URL);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void showHideLoading(boolean isShow)
    {
        RelativeLayout rlProgress = findViewById(R.id.rlProgress);
        if (isShow)
            rlProgress.setVisibility(View.VISIBLE);
        else
            rlProgress.setVisibility(View.GONE);
    }

    private void getSuggestions(String message, String userId, boolean isLocal)
    {
        ArrayList<FirebaseTextMessage> conversation = new ArrayList<>();

        if (isLocal)
        {
            conversation.add(FirebaseTextMessage.createForLocalUser(
                    message, System.currentTimeMillis()));
        }else
        {
            conversation.add(FirebaseTextMessage.createForRemoteUser(
                    message, System.currentTimeMillis(), userId));
        }
        FirebaseSmartReply smartReply = FirebaseNaturalLanguage.getInstance().getSmartReply();
        smartReply.suggestReplies(conversation).continueWith(task -> {
            for (SmartReplySuggestion suggestion : Objects.requireNonNull(task.getResult()).getSuggestions())
            {
                String replyText = suggestion.getText();
                arrMessageSuggestions.add(replyText);
            }

            if (!arrMessageSuggestions.isEmpty()) {
                rlSuggestions.setVisibility(View.VISIBLE);
                suggestionsAdaptor.notifyDataSetChanged();
                rvChat.scrollToPosition(arrChatDetails.size()-1);
            }
            return null;
        });
    }

    @Override
    public void onBackPressed() {

        if (!isBackPressed)
        {
            isBackPressed = true;
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(Chat.this, Dashboard.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.move_right_enter, R.anim.move_right_exit);
                finish();
            }, 200);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listener != null)
        {
            chatDetails.removeEventListener(listener);
            chatDetails = null;
            listener = null;
        }
    }

    @Override
    public void onSuggestionClicked(String message)
    {
        sendMessage(message);
    }
}
