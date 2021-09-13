package com.jambacabs.driver;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PictureInPictureParams;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.jambacabs.driver.adaptor.CancelAdaptor;
import com.jambacabs.driver.adaptor.CompleteReasonsAdaptor;
import com.jambacabs.driver.adaptor.EmergencyAdaptor;
import com.jambacabs.driver.api.ApiClient;
import com.jambacabs.driver.api.ApiInterface;
import com.jambacabs.driver.callbacks.ICancel;
import com.jambacabs.driver.connections.ConnectivityReceiver;
import com.jambacabs.driver.connections.LocationService;
import com.jambacabs.driver.connections.MyLocationReceiver;
import com.jambacabs.driver.font.CustomEditTextView;
import com.jambacabs.driver.font.CustomTextView;
import com.jambacabs.driver.helper.DirectionsJSONParser;
import com.jambacabs.driver.models.ChatModel;
import com.jambacabs.driver.models.CommonResponseBean;
import com.jambacabs.driver.models.CustomerProfileBean;
import com.jambacabs.driver.models.DriverDataBean;
import com.jambacabs.driver.models.DriverVehicleModel;
import com.jambacabs.driver.models.DynamicPagesBean;
import com.jambacabs.driver.models.JourneyDetails;
import com.jambacabs.driver.models.SettingsModel;
import com.jambacabs.driver.models.StatusModel;
import com.jambacabs.driver.models.TripDetails;
import com.jambacabs.driver.singleton.Constants;
import com.jambacabs.driver.singleton.FloatingViewService;
import com.jambacabs.driver.singleton.UserSession;
import com.jambacabs.driver.singleton.Utilities;
import com.jambacabs.driver.singleton.VolleySingleton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.jambacabs.driver.models.HeatMapBaseResponse;
import com.jambacabs.driver.models.HeatMapData;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigationOptions;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.google.android.gms.maps.model.JointType.ROUND;
import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, android.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, Animation.AnimationListener, ConnectivityReceiver.ConnectivityReceiverListener, ICancel, MyLocationReceiver.LocationReceiverListener {
    private DrawerLayout drawer;
    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private Location currentLocation;
    private static final int REQUEST_CODE = 101;
    private static final int GPS_REQUEST_CODE = 24234;
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    public static final int IMAGE_CODE = 2020;
    private RelativeLayout rlBlink;
    private Animation blink;
    private ImageView iv_up;
    private CircleImageView ivUser;
    private BottomSheetBehavior bottomSheetBehavior;
    private CustomTextView tvUserInfo, tvRide, tvUserName, tvDecline;
    private CardView cardViewDetails;
    private CustomTextView tvArea, tvTime, tvDistance, tvCancelArea;
    private RelativeLayout rlDecline, rlSheets;
    private RelativeLayout rlGo;
    private CustomTextView tvStatus;
    private CoordinatorLayout user_bottom_sheet, bottom_sheet, coordinate_cancel;
    private CoordinatorLayout coordinate_complete;
    private CustomTextView tvCompleteArea;
    private CustomTextView tvHeader;
    private RecyclerView rvCompleteReasons;
    private boolean is_hide_state;
    private RelativeLayout rlNavigate;
    private CustomTextView tvNavigate;
    private ListView lvIssues;
    private ArrayList<String> arr_items;
    private Geocoder geocoder;
    private RelativeLayout rlProgress;
    private ConstraintLayout const_main;
    private boolean is_available;
    private ConstraintLayout const_status_card;
    private ImageView ivArrow, tvBullet;
    private String str_mobile_number, driverWallet;
    private float distance;
    private ImageView ivOutStation, ivInCity;
    private CircleImageView ivPerson;
    private JSONObject dict_ride_info;
    private JSONObject dict_user_data;
    private Polyline mPolyline;
    private int estimatedTime;
    private boolean is_map_loaded;
    private DatabaseReference refDatabase;
    private DatabaseReference refRideDatabase;
    private ValueEventListener reflistiner;
    private DatabaseReference chatDatabase;
    private RelativeLayout rlChatCount;
    private CustomTextView tvChatCount;
    private LatLng user_lat_lng;
    private Marker marker;
    private boolean is_dest_show, is_route_populated;
    private LatLng old_position;
    private LatLng driver_old_latlng;
    private LocationManager locationManager;
    private RelativeLayout rlEarningsDash;
    private CustomTextView tvEarningsDash;
    private boolean is_updating;
    private Handler release_handler;
    private Runnable runnable;
    private BottomSheetDialog dialog;
    private View mapView;
    private boolean is_user_logout;
    private BottomSheetBehavior bottomSheetBehavior1;
    private FusedLocationProviderClient fusedLocationClient;
    private RelativeLayout rlNotification, rlEmergency;
    private RelativeLayout rlChat;
    private CustomTextView tvRating;
    private boolean doubleBackToExitPressedOnce = false;
    private ArrayList<String> arr_emergency;
    private ArrayList<Integer> arrEmergencyIcon, listMenuIds;
    private NavigationView navigationView;
    View headerLayout;
    private ArrayList<DynamicPagesBean.DataItem> dynamicPagesList;
    private boolean is_internet_disconected;
    private CustomEditTextView etReason;
    private CustomTextView tvReason;
    private float finalTime;
    private float finalDis;
    private String pickupPic;
    private String deliveryPic;
    private SupportMapFragment fragment;
    private Marker polyLineMarker;
    private Marker polyLineMarkerDest;
    private boolean isAcceptClicked;
    private LatLng locLatLng;
    private String completeReason;
    private int cancelPosition;
    private boolean isPolyLineDrawed;
    private String mapAPI;
    //    private MapboxNavigation navigation;
    private List<LatLng> arrPolyLinePoints;
    private double arrivalDistance;
    private double arrivalDuration;
    private boolean isRouteChanged;
    private boolean updatingStarted;
    private Handler rideHandler;
    private Runnable rideRunnable;
    private ArrayList<HeatMapData> dataArrayList;
    ArrayList<LatLng> latLongList = new ArrayList<>();
    // Alternative radius for convolution
    private static final int ALT_HEATMAP_RADIUS = 10;
    // Alternative opacity of heatmap overlay
    private static final double ALT_HEATMAP_OPACITY = 0.4;
    CustomerProfileBean.Data customerData;
    /**
     * Alternative heatmap gradient (blue -> red)
     * Copied from Javascript version
     */
    private static final int[] ALT_HEATMAP_GRADIENT_COLORS = {
            Color.argb(0, 0, 255, 255),// transparent
            Color.argb(255 / 3 * 2, 0, 255, 255),
            Color.rgb(0, 191, 255),
            Color.rgb(0, 0, 127),
            Color.rgb(255, 0, 0)
    };
    public static final float[] ALT_HEATMAP_GRADIENT_START_POINTS = {
            0.0f, 0.10f, 0.20f, 0.60f, 1.0f
    };
    public static final Gradient ALT_HEATMAP_GRADIENT = new Gradient(ALT_HEATMAP_GRADIENT_COLORS,
            ALT_HEATMAP_GRADIENT_START_POINTS);
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;
    private boolean mDefaultGradient = true;
    private boolean mDefaultRadius = true;
    private boolean mDefaultOpacity = true;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String mapBoxKey = new UserSession(Dashboard.this).getMapboxkey();
        if (mapBoxKey.equals(""))
            Mapbox.getInstance(this, Constants.MAPBOX_ACCESS_TOKEN);
        else
            Mapbox.getInstance(this, mapBoxKey);

        setContentView(R.layout.dashboard_layout);
        checkGPSStatus();
        checkOverLayPermission();
        checkFloatingButton();
        drawer = findViewById(R.id.drawer_layout);
        setDrawerListner();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, R.string.open_string, R.string.closed_string);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.nav_view);
        headerLayout = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        CircleImageView iv_user = headerLayout.findViewById(R.id.iv_user);
        iv_user.setOnClickListener(this);
        final CustomTextView tvName = headerLayout.findViewById(R.id.tvName);
        rlBlink = findViewById(R.id.rlBlink);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        blink = AnimationUtils.loadAnimation(this, R.anim.blink);
        rlBlink.startAnimation(blink);
        String ride_type = "inCity";
        is_internet_disconected = false;
        deliveryPic = "";
        pickupPic = "";
        isPolyLineDrawed = false;
        arrPolyLinePoints = new ArrayList<>();
        try {
            String str_data = new UserSession(Dashboard.this).getUserData();
            JSONObject dict_user_data = new JSONObject(str_data);
            String avatar = dict_user_data.optString("avatar");
            driverWallet = dict_user_data.optString("driverWalet");
            String first_name = dict_user_data.optString("firstName");
            String last_name = dict_user_data.optString("lastName");
            String s1 = first_name.substring(0, 1).toUpperCase();
            first_name = s1 + first_name.substring(1);
            String s2 = last_name.substring(0, 1).toUpperCase();
            last_name = s2 + last_name.substring(1);
            String full_name = first_name + " " + last_name;
            tvName.setText(full_name);
            if (dict_user_data.has("rideType")) {
                String str_ride_type = dict_user_data.optString("rideType");
                if (!str_ride_type.equals("")) {
                    ride_type = dict_user_data.optString("rideType");
                }
            }
            tvRating = headerLayout.findViewById(R.id.tvRating);
            if (dict_user_data.has("averageRating")) {
                String str_rating = String.format(Locale.getDefault(), "%.2f", dict_user_data.optDouble("averageRating"));
                tvRating.setText(str_rating);
            } else {
                tvRating.setText(getResources().getString(R.string.not_applicable));
            }
            Uri uri = Uri.parse(avatar);
            ivUser = findViewById(R.id.ivUser);
            Glide.with(getApplicationContext()).load(uri).asBitmap().error(R.drawable.ic_account_gray).placeholder(R.drawable.ic_account_gray).centerCrop().into(new BitmapImageViewTarget(ivUser) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    ivUser.setImageDrawable(circularBitmapDrawable);
                }
            });

            Glide.with(getApplicationContext()).load(uri).asBitmap().error(R.drawable.ic_account_gray).placeholder(R.drawable.ic_account_gray).centerCrop().into(new BitmapImageViewTarget(iv_user) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    iv_user.setImageDrawable(circularBitmapDrawable);
                }
            });
        } catch (JSONException e) {
            Log.v("parseError", "ParseError" + e.getLocalizedMessage());
        }
        RelativeLayout rlStatus = findViewById(R.id.rlStatus);
        bottomSheetBehavior1 = BottomSheetBehavior.from(rlStatus);
        bottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior1.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (!is_available) {
                    if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                        bottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (!is_available) {
                    if (bottomSheetBehavior1.getState() == BottomSheetBehavior.STATE_DRAGGING) {
                        bottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                }
            }
        });
        iv_up = findViewById(R.id.iv_up);
        tvUserInfo = findViewById(R.id.tvUserInfo);
        tvUserName = findViewById(R.id.tvUserName);
        cardViewDetails = findViewById(R.id.cardViewDetails);
        rlGo = findViewById(R.id.rlGo);
        tvStatus = findViewById(R.id.tvStatus);
        tvDecline = findViewById(R.id.tvDecline);
        user_bottom_sheet = findViewById(R.id.user_bottom_sheet);
        bottom_sheet = findViewById(R.id.bottom_sheet);
        is_hide_state = false;
        rlNavigate = findViewById(R.id.rlNavigate);
        tvNavigate = findViewById(R.id.tvNavigate);
        tvRide = findViewById(R.id.tvRide);
        lvIssues = findViewById(R.id.lvIssues);
        coordinate_cancel = findViewById(R.id.coordinate_cancel);
        coordinate_complete = findViewById(R.id.coordinate_complete);
        tvCompleteArea = findViewById(R.id.tvCompleteArea);
        tvHeader = findViewById(R.id.tvHeader);
        rvCompleteReasons = findViewById(R.id.rvCompleteReasons);
        rlProgress = findViewById(R.id.rlProgress);
        const_main = findViewById(R.id.const_main);
        ivArrow = findViewById(R.id.ivArrow);
        tvBullet = findViewById(R.id.tvBullet);
        const_status_card = findViewById(R.id.const_status_card);
        tvArea = findViewById(R.id.tvArea);
        tvTime = findViewById(R.id.tvTime);
        tvDistance = findViewById(R.id.tvDistance);
        ivPerson = findViewById(R.id.ivPerson);
        ivInCity = findViewById(R.id.ivInCity);
        ivOutStation = findViewById(R.id.ivOutStation);
        rlDecline = findViewById(R.id.rlDecline);
        rlSheets = findViewById(R.id.rlSheets);
        tvCancelArea = findViewById(R.id.tvCancelArea);
        rlEarningsDash = findViewById(R.id.rlEarningsDash);
        tvEarningsDash = findViewById(R.id.tvEarningsDash);
        rlNotification = findViewById(R.id.rlNotification);
        rlEmergency = findViewById(R.id.rlEmergency);
        rlChat = findViewById(R.id.rlChat);
        rlChatCount = findViewById(R.id.rlChatCount);
        tvChatCount = findViewById(R.id.tvChatCount);
        etReason = findViewById(R.id.etReason);
        tvReason = findViewById(R.id.tvReason);
        fragment = (SupportMapFragment) (getSupportFragmentManager().findFragmentById(R.id.map));
        is_available = new UserSession(this).getDriverAvailability();
        release_handler = new Handler();
        rideHandler = new Handler();
        str_mobile_number = "";
        is_map_loaded = false;
        is_dest_show = false;
        is_route_populated = false;
        int flags = getWindow().getDecorView().getSystemUiVisibility();
        flags = flags ^ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        getWindow().getDecorView().setSystemUiVisibility(flags);
        refDatabase = FirebaseDatabase.getInstance().getReference(Constants.JOURNEY);
        chatDatabase = FirebaseDatabase.getInstance().getReference(Constants.CHAT_DETAILS);
        if (!new UserSession(Dashboard.this).getTagFrom().equals("")) {
            refRideDatabase = FirebaseDatabase.getInstance().getReference(Constants.RIDE_DETAILS);
        }

        is_updating = false;
        is_user_logout = false;

        arr_emergency = new ArrayList<>();
        arrEmergencyIcon = new ArrayList<>();
        listMenuIds = new ArrayList<>();
        arr_emergency.add("112");
        arr_emergency.add("101");
        arr_emergency.add("102");

        arrEmergencyIcon.add(R.drawable.ic_police);
        arrEmergencyIcon.add(R.drawable.ic_fire);
        arrEmergencyIcon.add(R.drawable.ic_ambulance);


        if (ride_type.equals("inCity")) {
            new UserSession(Dashboard.this).setIncityStatus(true);
            new UserSession(Dashboard.this).setOutstationStatus(false);
        } else {
            new UserSession(Dashboard.this).setOutstationStatus(true);
            new UserSession(Dashboard.this).setIncityStatus(false);
        }


        boolean is_in_city_status = new UserSession(Dashboard.this).getInCityStatus();
        boolean is_outstation_status = new UserSession(Dashboard.this).getOutStationStatus();

        if (is_in_city_status) {
            ivInCity.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_radio_check));
        } else {
            ivInCity.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_radio_uncheck));
        }

        if (is_outstation_status) {
            ivOutStation.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_radio_check));
        } else {
            ivOutStation.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_radio_uncheck));
        }

        if (!driverWallet.equals("") && !driverWallet.equals("null")) {
            String str = "\u20B9 " + driverWallet;
            tvEarningsDash.setText(str);
        } else {
            String str = "\u20B9 " + 0;
            tvEarningsDash.setText(str);
        }
        initUI();
        /*if (Utilities.isInternetAvailable(Dashboard.this)) {
            showLoading();
            getDriverExpiryDataTask();
        } else {
            Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
        }*/
        if (is_available) {
            rlGo.setBackground(ContextCompat.getDrawable(this, R.drawable.corner_circle_black));
            tvStatus.setText(getResources().getString(R.string.online_status));
            const_status_card.setBackgroundColor(ContextCompat.getColor(this, R.color.goclr_green));
            tvStatus.setTextColor(ContextCompat.getColor(this, R.color.white));
            ivArrow.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_up_white));
            tvBullet.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_bullet_white));
        } else {
            rlGo.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_corner));
            tvStatus.setText(getResources().getString(R.string.offline_status));
            const_status_card.setBackgroundColor(ContextCompat.getColor(this, R.color.goclr_red));
            tvStatus.setTextColor(ContextCompat.getColor(this, R.color.white));
            ivArrow.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_up_white));
            tvBullet.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_bullet_white));
            if (mOverlay != null) {
                mOverlay.remove();
                mProvider = null;
            }
        }
        View llUser = findViewById(R.id.llUserInfo);
        bottomSheetBehavior = BottomSheetBehavior.from(llUser);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                if (is_hide_state) {
                    if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                        boolean is_navigation_started = new UserSession(Dashboard.this).getNavigationStarted();
                        boolean is_ride_started = new UserSession(Dashboard.this).getRideStatus();
                        if (is_ride_started) {
                            if (!is_navigation_started) {
                                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                tvUserInfo.setVisibility(View.GONE);
                            } else if (rlNavigate.getVisibility() == View.VISIBLE) {
                                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                tvUserInfo.setVisibility(View.GONE);
                            }
                        }
                    } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        tvUserInfo.setVisibility(View.VISIBLE);
                    } else {
                        tvUserInfo.setVisibility(View.GONE);
                    }
                } else {
                    if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                        boolean is_navigation_started = new UserSession(Dashboard.this).getNavigationStarted();
                        boolean is_ride_started = new UserSession(Dashboard.this).getRideStatus();
                        if (is_ride_started) {
                            if (!is_navigation_started) {
                                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                tvUserInfo.setVisibility(View.GONE);
                            } else if (rlNavigate.getVisibility() == View.VISIBLE) {
                                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                tvUserInfo.setVisibility(View.GONE);
                            }
                        }
                    } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        tvUserInfo.setVisibility(View.VISIBLE);
                    } else {
                        tvUserInfo.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                animateBottomSheetArrows(slideOffset);
            }
        });
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);


        lvIssues.setOnItemClickListener((parent, view, position, id) -> {
            if (arr_items.size() > 0) {
                cancelPosition = position;
                ArrayAdapter cancel_adaptor = new CancelAdaptor(Dashboard.this, R.layout.cancel_list_item, arr_items, position);
                lvIssues.setAdapter(cancel_adaptor);
                cancel_adaptor.notifyDataSetChanged();
            }
        });
        addChatListiner();
    }

    private void addChatListiner() {
        String strParams = new UserSession(Dashboard.this).getPushParams();
        if (!strParams.equals("")) {
            try {
                JSONObject dictParams = new JSONObject(strParams);
                Log.v("dictParams", "dictParams" + dictParams);
                String strRideDtails;
                if (dictParams.has("ride")) {
                    strRideDtails = dictParams.optString("ride");
                } else {
                    strRideDtails = dictParams.optString("rideData");
                }
                JSONObject dictRideData = new JSONObject(strRideDtails);
                chatDatabase = chatDatabase.child(dictRideData.optString("rideId"));
                chatDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Object snapShot = dataSnapshot.getValue();
                        int count = 0;
                        if (snapShot != null) {
                            Gson gson = new Gson();
                            String json = gson.toJson(snapShot);
                            try {
                                JSONObject dictJson = new JSONObject(json);
                                JSONArray arrNames = dictJson.names();

                                if (arrNames != null) {
                                    for (int i = 0; i < dictJson.length(); i++) {
                                        JSONObject dictElements = dictJson.optJSONObject((String) arrNames.get(i));
                                        if (dictElements != null) {
                                            ChatModel model = gson.fromJson(Objects.requireNonNull(dictElements).toString(), ChatModel.class);
                                            if (model.getRole().equals(Constants.USER) && !model.isRead()) {
                                                count = count + 1;
                                            }
                                        }
                                    }
                                    tvChatCount.setText(String.valueOf(count));
                                    if (count > 0)
                                        rlChatCount.setVisibility(View.VISIBLE);
                                    else
                                        rlChatCount.setVisibility(View.GONE);
                                }
                            } catch (JSONException e) {
                                Log.v("error", "error" + e.getLocalizedMessage());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.v("cancelled", "cancelled" + databaseError.getMessage());
                    }
                });
            } catch (JSONException e) {
                Log.v("parseError", "ParseError" + e.getLocalizedMessage());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void onWindowPermission() {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        } else {
            new UserSession(Dashboard.this).setFloating(true);
            initializeView();
        }
    }

    private void initializeView() {
        startService(new Intent(Dashboard.this, FloatingViewService.class));
        finish();
    }

    private void setDrawerListner() {
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                if (Utilities.isInternetAvailable(Dashboard.this)) {
                    getDynamicPages();
                } else {
                    Utilities.showMessage(tvRide, getResources().getString(R.string.internet_error));
                }
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                if (listMenuIds != null && listMenuIds.size() > 0) {
                    Menu menu = navigationView.getMenu();
                    for (int i = 0; i < listMenuIds.size(); i++) {
                        menu.removeItem(listMenuIds.get(i));
                    }
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        registerInternetConnecetivity();
        registerLocationConnectivity();
        if (new UserSession(Dashboard.this).getRideAccept()
                || new UserSession(Dashboard.this).getRideStatus()
                || new UserSession(Dashboard.this).getUserPickUp()) {
            addDataListeners();
        }

    }

    private void getDistanceAndDuration(double lat1, double lng1, double lat2, double lng2) {
        mapAPI = new UserSession(Dashboard.this).getAPI();
        if (mapAPI.equals("")) {
            mapAPI = getResources().getString(R.string.map_api_key);
        }
        String url = Constants.DISTANCE_API + lat1 + "," + lng1 + "&destinations=" + lat2 + "," + lng2 + "&key=" + mapAPI;

        JsonObjectRequest stringRequest = new JsonObjectRequest(com.android.volley.Request.Method.GET, url, null,
                response -> {
                    if (response != null) {
                        JSONArray arr_rows = response.optJSONArray("rows");

                        assert arr_rows != null;
                        JSONObject dict_rows = arr_rows.optJSONObject(0);
                        JSONArray arr_elements = dict_rows.optJSONArray("elements");
                        assert arr_elements != null;
                        JSONObject dict_elements = arr_elements.optJSONObject(0);
                        JSONObject dict_distance = dict_elements.optJSONObject("distance");
                        JSONObject dict_duration = dict_elements.optJSONObject("duration");

                        assert dict_distance != null;
                        String distance_value = dict_distance.optString("value");
                        assert dict_duration != null;
                        String duration_value = dict_duration.optString("value");
                        distance = (float) (Double.parseDouble(distance_value) / 1000);
                        estimatedTime = (int) ((Double.parseDouble(duration_value)) / 60);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(new Date());
                        cal.add(Calendar.MINUTE, estimatedTime);
                        SimpleDateFormat format1 = new SimpleDateFormat("kk", Locale.getDefault());
                        String str_hour = format1.format(cal.getTime());
                        format1 = new SimpleDateFormat("mm", Locale.getDefault());
                        String str_minutes = format1.format(cal.getTime());
                        int minutes = Integer.parseInt(str_minutes);

                        int hour = Integer.parseInt(str_hour);
                        if (hour == 24) {
                            hour = 0;
                        }
                        if (hour < 10) {
                            str_hour = "0" + hour;
                        } else {
                            str_hour = String.valueOf(hour);
                        }
                        if (minutes < 10) {
                            str_minutes = "0" + minutes;
                        }
                        String time_to_arrive = str_hour + ":" + str_minutes;
                        tvTime.setText(time_to_arrive);
                        String str_distance = distance + " km";
                        tvDistance.setText(str_distance);
                    } else {

                        hideLoading();
                        Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                    }
                },
                error -> {
                    hideLoading();
                    Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                }
        );
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void getVehicles() {
        String user_id = new UserSession(this).getUserId();
        String append_url = Constants.URL + Constants.VEHICLES + "/" + user_id;

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, append_url, new JSONObject(),
                response -> {
                    if (response != null) {
                        String status = response.optString("type");
                        String message = response.optString("message");
                        if (status.equals("success")) {
                            JSONArray arr_data = response.optJSONArray("data");
                            assert arr_data != null;
                            for (int i = 0; i < arr_data.length(); i++) {
                                JSONObject dict_data = arr_data.optJSONObject(i);
                                boolean is_vehicle_available = dict_data.optBoolean("available");
                                if (is_vehicle_available) {
                                    String vehicleType = dict_data.optString("vehicleType");
                                    String make = dict_data.optString("make");
                                    String model = dict_data.optString("model");
                                    String vehicle_name = make + " " + model;
                                    new UserSession(Dashboard.this).setSelectedVehicleInfo(dict_data.toString());
                                    new UserSession(Dashboard.this).setVehicleType(vehicleType);
                                    new UserSession(Dashboard.this).setVehicleName(vehicle_name);
                                    new UserSession(Dashboard.this).setVehicleAvailability(true);
                                    if (dict_data.has("availableForDelivery") && vehicleType.equals("Jamba Bike")) {
                                        new UserSession(Dashboard.this).setAvailableDelivery(dict_data.optBoolean("availableForDelivery"));
                                    } else {
                                        try {
                                            dict_data.put("availableForDelivery", false);
                                        } catch (JSONException e) {
                                            Log.v("parseError", "ParseError" + e.getLocalizedMessage());
                                        }
                                    }
                                } else {
                                    if (!dict_data.has("availableForDelivery")) {
                                        try {
                                            dict_data.put("availableForDelivery", false);
                                        } catch (JSONException e) {
                                            Log.v("parseError", "ParseError" + e.getLocalizedMessage());
                                        }
                                    }
                                }
                            }
                            if (Utilities.isInternetAvailable(Dashboard.this)) {
                                getDriverVehicle(new UserSession(Dashboard.this).getVehicleType());
                            } else {
                                hideLoading();
                                Utilities.showMessage(const_main, getResources().getString(R.string.internet_error));
                            }
                        } else {
                            hideLoading();
                            Utilities.showMessage(const_main, message);
                        }

                    } else {
                        hideLoading();
                        Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                    }
                },
                error -> {
                    hideLoading();
                    NetworkResponse res = error.networkResponse;
                    if (res != null) {
                        if (res.statusCode == 403) {
                            showLoading();
                            Utilities.showMessage(const_main, getResources().getString(R.string.session_expired));
                            JambaCabApplication.getInstance().removeDriver(const_main);
                        } else {

                            Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                        }
                    } else {
                        Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("x-custom-token", new UserSession(Dashboard.this).getAccessToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void getDriverVehicle(String type) {
        ApiInterface apiService = ApiClient.retrofitBaseUrl(Constants.URL).create(ApiInterface.class);
        Call<DriverVehicleModel> call = apiService.getPerticularVehicle(new UserSession(Dashboard.this).getAccessToken(), type);
        call.enqueue(new Callback<DriverVehicleModel>() {

            @Override
            public void onResponse(@NonNull Call<DriverVehicleModel> call, @NonNull retrofit2.Response<DriverVehicleModel> response) {
                if (response.code() == 403) {
                    showLoading();
                    Utilities.showMessage(const_main, getResources().getString(R.string.session_expired));
                    JambaCabApplication.getInstance().removeDriver(const_main);
                } else {
                    if (response.body() != null) {
                        DriverVehicleModel model = response.body();
                        new UserSession(Dashboard.this).setDriverVehicle(model.getTypes());
                        if (Utilities.isInternetAvailable(Dashboard.this)) {
                            getNotifocationCount();
                        } else {
                            hideLoading();
                            Utilities.showMessage(const_main, getResources().getString(R.string.internet_error));
                        }
                    } else {
                        hideLoading();
                        Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<DriverVehicleModel> call, @NonNull Throwable t) {
                hideLoading();
                Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
            }
        });
    }

    private void getNotifocationCount() {
        String user_id = new UserSession(this).getUserId();
        String append_url = Constants.URL + Constants.ALL_NOTOFICATION_COUNT + "/" + user_id + "/driver";

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, append_url, new JSONObject(),
                response -> {
                    if (response != null) {
                        String status = response.optString("type");
                        String message = response.optString("message");
                        if (status.equals("success")) {
                            JSONObject dict_data = response.optJSONObject("data");
                            int count = 0;
                            if (dict_data != null) {
                                count = dict_data.optInt("count");
                            }
                            JambaCabApplication.getInstance().setNotificationCount(count);
                            if (count > 0) {
                                findViewById(R.id.ivNotificationDot).setVisibility(View.VISIBLE);
                                Glide.with(getApplicationContext()).load(R.mipmap.ic_dot_animaton).override(50, 50).into((ImageView) findViewById(R.id.ivNotificationDot));
                            } else {
                                findViewById(R.id.ivNotificationDot).setVisibility(View.GONE);
                            }
                            getSettings();
                        } else {
                            hideLoading();
                            Utilities.showMessage(const_main, message);
                        }
                    } else {
                        hideLoading();
                        Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                    }
                },
                error -> {
                    hideLoading();
                    NetworkResponse res = error.networkResponse;
                    if (res != null) {
                        if (res.statusCode == 403) {
                            showLoading();
                            Utilities.showMessage(const_main, getResources().getString(R.string.session_expired));
                            JambaCabApplication.getInstance().removeDriver(const_main);
                        } else {
                            Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                        }
                    } else {
                        Utilities.showMessage(const_main, getResources().getString(R.string.internet_error));
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("x-custom-token", new UserSession(Dashboard.this).getAccessToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void animateBottomSheetArrows(float slideOffset) {
        iv_up.setRotation(slideOffset * 180);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        /*Intent myService = new Intent(Dashboard.this, LocationService.class);
        stopService(myService);*/

        if (Utilities.isInternetAvailable(Dashboard.this)) {
//            showLoading();
            getDriverExpiryDataTask();
        } else {
            long expiry_date = new UserSession(Dashboard.this).getDriverExpiryDate();
            long current_date = new Date().getTime();
            if (current_date > expiry_date && expiry_date > 0) {
                if (is_map_loaded) {
                    if (is_available) {
                        if (Utilities.isInternetAvailable(Dashboard.this)) {
                            updateDriverStatus();
                        } else {
                            Utilities.showMessage(const_main, getResources().getString(R.string.internet_error));
                        }
                    } else {
                        if (mGoogleMap != null) {
                            if (old_position == null) {
                                old_position = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            }
                        }
                        if (is_map_loaded) {
                            if (!new UserSession(Dashboard.this).getTagFrom().equals("") && !new UserSession(Dashboard.this).getScreen().equals("ride_info")) {
                                NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                assert nMgr != null;
                                nMgr.cancelAll();

                                LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                mGoogleMap.setTrafficEnabled(false);
                                mGoogleMap.setIndoorEnabled(false);
                                mGoogleMap.setBuildingsEnabled(false);
                                mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
                                mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);
                                if (mGoogleMap != null) {
                                    enableDisablePointer(true);
                                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                    mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().
                                            target(latLng)
                                            .zoom(16).build()));
                                }
                                prepareUI(latLng);
                            }
                        }
                    }
                }
            } else {
                if (mGoogleMap != null) {
                    if (old_position == null) {
                        old_position = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    }
                }
                if (is_map_loaded) {
                    if (!new UserSession(Dashboard.this).getTagFrom().equals("") && !new UserSession(Dashboard.this).getScreen().equals("ride_info")) {
                        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        assert nMgr != null;
                        nMgr.cancelAll();

                        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        mGoogleMap.setTrafficEnabled(false);
                        mGoogleMap.setIndoorEnabled(false);
                        mGoogleMap.setBuildingsEnabled(false);
                        mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
                        mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);
                        if (mGoogleMap != null) {
                            enableDisablePointer(true);
                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().
                                    target(latLng)
                                    .zoom(16).build()));
                        }
                        prepareUI(latLng);
                    }
                }
            }
        }
    }

    private void changeFromOutStationToInCity() {
        JSONObject dict_params;
        boolean out_station = new UserSession(Dashboard.this).getOutStationStatus();
        if (!out_station) {
            dict_params = setDriverLocationFields("inCity");
        } else {
            String vehile_type = new UserSession(Dashboard.this).getDriverVehicle();
            if (vehile_type.equals("bike") || vehile_type.equals("auto")) {
                dict_params = setDriverLocationFields("inCity");
            } else {
                dict_params = setDriverLocationFields("outstation");
            }
        }

        if (Utilities.isInternetAvailable(Dashboard.this)) {
            showLoading();
            removeDriverExpired(dict_params);
        } else {
            Utilities.showMessage(const_main, getResources().getString(R.string.internet_error));
        }
    }

    private void getCustomerProfileData(String userId) {
        ApiInterface apiService = ApiClient.retrofitBaseUrl(Constants.PAYMENTS_URL).create(ApiInterface.class);

        Call<CustomerProfileBean> call = apiService.getCustomerProfileNew(new UserSession(Dashboard.this).getAccessToken(), Long.valueOf(userId));
        call.enqueue(new Callback<CustomerProfileBean>() {

            @Override
            public void onResponse(@NonNull Call<CustomerProfileBean> call, @NonNull retrofit2.Response<CustomerProfileBean> response) {
                try {
                    if (response.code() == 403) {
                        showLoading();
                        Utilities.showMessage(const_main, getResources().getString(R.string.session_expired));
                        JambaCabApplication.getInstance().removeDriver(const_main);
                    }else
                    {
                        if (response.body() != null)
                        {
                            if (response.body().getType().equals(Constants.RESPONSE_SUCCESS)) {
                                hideLoading();
                                customerData = response.body().getData();
                                String status = customerData.getStatus();
                                if (!status.equals("accept"))
                                {
                                    Utilities.showMessage(const_main, getResources().getString(R.string.reject_error));
                                    if (is_available) {
                                        JSONObject dict_params;
                                        boolean out_station = new UserSession(Dashboard.this).getOutStationStatus();
                                        if (!out_station) {
                                            dict_params = setDriverLocationFields("inCity");
                                        } else {
                                            String vehile_type = new UserSession(Dashboard.this).getDriverVehicle();
                                            if (vehile_type.equals("bike") || vehile_type.equals("auto")) {
                                                dict_params = setDriverLocationFields("inCity");
                                            } else {
                                                dict_params = setDriverLocationFields("outstation");
                                            }
                                        }

                                        if (Utilities.isInternetAvailable(Dashboard.this)) {
                                            removeDriverExpired(dict_params);
                                        } else {
                                            Utilities.showMessage(const_main, getResources().getString(R.string.internet_error));
                                        }
                                    } else {
                                        if (mGoogleMap != null) {
                                            if (old_position == null) {
                                                old_position = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                            }
                                        }
                                        if (is_map_loaded) {
                                            if (!new UserSession(Dashboard.this).getTagFrom().equals("") && !new UserSession(Dashboard.this).getScreen().equals("ride_info")) {
                                                NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                                assert nMgr != null;
                                                nMgr.cancelAll();

                                                LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                                mGoogleMap.setTrafficEnabled(false);
                                                mGoogleMap.setIndoorEnabled(false);
                                                mGoogleMap.setBuildingsEnabled(false);
                                                mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
                                                mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);
                                                enableDisablePointer(true);
                                                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                                mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().
                                                        target(latLng)
                                                        .zoom(16).build()));
                                                prepareUI(latLng);
                                            }
                                        }
                                    }
                                }else
                                {
                                    getVehicles();
                                }

                            } else {
                                hideLoading();
                            }
                        }else
                        {
                            hideLoading();
                        }
                    }
                } catch (NullPointerException e) {
                    hideLoading();
                }
            }
            @Override
            public void onFailure(@NonNull Call<CustomerProfileBean> call, @NonNull Throwable t) {
                hideLoading();
            }
        });
    }


    private void getCustomerStatus(String userId) {
        ApiInterface apiService = ApiClient.retrofitBaseUrl(Constants.PAYMENTS_URL).create(ApiInterface.class);

        Call<CustomerProfileBean> call = apiService.getCustomerProfileNew(new UserSession(Dashboard.this).getAccessToken(), Long.valueOf(userId));
        call.enqueue(new Callback<CustomerProfileBean>() {

            @Override
            public void onResponse(@NonNull Call<CustomerProfileBean> call, @NonNull retrofit2.Response<CustomerProfileBean> response) {
                try {
                    if (response.code() == 403) {
                        showLoading();
                        Utilities.showMessage(const_main, getResources().getString(R.string.session_expired));
                        JambaCabApplication.getInstance().removeDriver(const_main);
                    }else
                    {
                        if (response.body() != null)
                        {
                            if (response.body().getType().equals(Constants.RESPONSE_SUCCESS)) {
                                hideLoading();
                                customerData = response.body().getData();
                                String status = customerData.getStatus();
                                if (!status.equals("accept"))
                                {
                                    Utilities.showMessage(const_main, getResources().getString(R.string.reject_error));
                                    if (is_available) {
                                        JSONObject dict_params;
                                        boolean out_station = new UserSession(Dashboard.this).getOutStationStatus();
                                        if (!out_station) {
                                            dict_params = setDriverLocationFields("inCity");
                                        } else {
                                            String vehile_type = new UserSession(Dashboard.this).getDriverVehicle();
                                            if (vehile_type.equals("bike") || vehile_type.equals("auto")) {
                                                dict_params = setDriverLocationFields("inCity");
                                            } else {
                                                dict_params = setDriverLocationFields("outstation");
                                            }
                                        }
                                        if (Utilities.isInternetAvailable(Dashboard.this)) {
                                            removeDriverExpired(dict_params);
                                        } else {
                                            Utilities.showMessage(const_main, getResources().getString(R.string.internet_error));
                                        }
                                    }
                                }else
                                {
                                    is_updating = true;
                                    updatingStarted = true;
                                    updateDriverAvailabilityEveryMinute(new UserSession(Dashboard.this).getVehicleType());
                                }

                            } else {
                                hideLoading();
                            }
                        }else
                        {
                            hideLoading();
                        }
                    }
                } catch (NullPointerException e) {
                    hideLoading();
                }
            }
            @Override
            public void onFailure(@NonNull Call<CustomerProfileBean> call, @NonNull Throwable t) {
                hideLoading();
            }
        });
    }

    private void checkCustomerStatus(String userId) {
        ApiInterface apiService = ApiClient.retrofitBaseUrl(Constants.PAYMENTS_URL).create(ApiInterface.class);

        Call<CustomerProfileBean> call = apiService.getCustomerProfileNew(new UserSession(Dashboard.this).getAccessToken(), Long.valueOf(userId));
        call.enqueue(new Callback<CustomerProfileBean>() {

            @Override
            public void onResponse(@NonNull Call<CustomerProfileBean> call, @NonNull retrofit2.Response<CustomerProfileBean> response) {
                try {
                    if (response.code() == 403) {
                        showLoading();
                        Utilities.showMessage(const_main, getResources().getString(R.string.session_expired));
                        JambaCabApplication.getInstance().removeDriver(const_main);
                    }else
                    {
                        if (response.body() != null)
                        {
                            if (response.body().getType().equals(Constants.RESPONSE_SUCCESS)) {
                                hideLoading();
                                customerData = response.body().getData();
                                String status = customerData.getStatus();
                                if (!status.equals("accept"))
                                {
                                    Utilities.showMessage(const_main, getResources().getString(R.string.reject_error));
                                    if (is_available) {
                                        JSONObject dict_params;
                                        boolean out_station = new UserSession(Dashboard.this).getOutStationStatus();
                                        if (!out_station) {
                                            dict_params = setDriverLocationFields("inCity");
                                        } else {
                                            String vehile_type = new UserSession(Dashboard.this).getDriverVehicle();
                                            if (vehile_type.equals("bike") || vehile_type.equals("auto")) {
                                                dict_params = setDriverLocationFields("inCity");
                                            } else {
                                                dict_params = setDriverLocationFields("outstation");
                                            }
                                        }
                                        if (Utilities.isInternetAvailable(Dashboard.this)) {
                                            removeDriverExpired(dict_params);
                                        } else {
                                            Utilities.showMessage(const_main, getResources().getString(R.string.internet_error));
                                        }
                                    }
                                }else
                                {
                                    String str_vehicle_type = new UserSession(Dashboard.this).getVehicleType();
                                    if (str_vehicle_type.equals("")) {
                                        Utilities.showMessage(const_main, getResources().getString(R.string.vehicle_error));
                                    } else {
                                        is_updating = false;
                                        if (release_handler != null) {
                                            release_handler.removeCallbacks(runnable);
                                        }
                                        showLoading();
                                        updateDriverAvailability(str_vehicle_type);
                                        playSound();
                                    }
                                }

                            } else {
                                hideLoading();
                            }
                        }else
                        {
                            hideLoading();
                        }
                    }
                } catch (NullPointerException e) {
                    hideLoading();
                }
            }
            @Override
            public void onFailure(@NonNull Call<CustomerProfileBean> call, @NonNull Throwable t) {
                hideLoading();
            }
        });
    }

    private void updateDriverStatus() {
        ApiInterface apiService = ApiClient.retrofitBaseUrl(Constants.PAYMENTS_URL).create(ApiInterface.class);


        StatusModel model = new StatusModel();
        model.setMobileNumber(Long.valueOf(new UserSession(Dashboard.this).getUserId()));

        StatusModel.Data data = new StatusModel.Data();

        data.setStatus("pending");
        data.setStatusReason("pending");
        model.setData(data);

        Call<CommonResponseBean> call = apiService.updateDriverStatus(new UserSession(Dashboard.this).getAccessToken(), model);
        call.enqueue(new Callback<CommonResponseBean>() {

            @Override
            public void onResponse(@NonNull Call<CommonResponseBean> call, @NonNull retrofit2.Response<CommonResponseBean> response) {
                try {
                    if (response.code() == 403) {
                        showLoading();
                        Utilities.showMessage(const_main, getResources().getString(R.string.session_expired));
                        JambaCabApplication.getInstance().removeDriver(const_main);
                    } else {
                        if (response.body() != null) {
                            if (response.body().getType().equals(Constants.RESPONSE_SUCCESS)) {
                                JSONObject dict_params;
                                boolean out_station = new UserSession(Dashboard.this).getOutStationStatus();
                                if (!out_station) {
                                    dict_params = setDriverLocationFields("inCity");
                                } else {
                                    String vehile_type = new UserSession(Dashboard.this).getDriverVehicle();
                                    if (vehile_type.equals("bike") || vehile_type.equals("auto")) {
                                        dict_params = setDriverLocationFields("inCity");
                                    } else {
                                        dict_params = setDriverLocationFields("outstation");
                                    }
                                }

                                if (Utilities.isInternetAvailable(Dashboard.this)) {
//                                    showLoading();
                                    removeDriverExpired(dict_params);
                                } else {
                                    Utilities.showMessage(const_main, getResources().getString(R.string.internet_error));
                                }

                            } else {
                                hideLoading();
                                Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                            }
                        }
                    }
                } catch (NullPointerException e) {
                    Log.v("parseError", "ParseError" + e.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<CommonResponseBean> call, @NonNull Throwable t) {
                hideLoading();
                Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
            }
        });
    }


    private JSONObject setDriverLocationFields(String str_ride_type) {
        double latitude = currentLocation.getLatitude();
        double longitude = currentLocation.getLongitude();

        String notificationId = new UserSession(this).getToken();
        String str_driverId = new UserSession(this).getUserId();
        double driverId = Double.parseDouble(str_driverId);
        String subLocality = "";
        JSONObject dict_params = new JSONObject();
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                subLocality = addresses.get(0).getSubLocality();
                if (subLocality == null || subLocality.equals("")) {
                    subLocality = addresses.get(0).getLocality();
                }
            }
            dict_params.put("driverId", driverId);
            dict_params.put("latitude", latitude);
            dict_params.put("longitude", longitude);
            dict_params.put("city", subLocality);
            dict_params.put("vehicleType", new UserSession(this).getVehicleType());
            dict_params.put("notificationId", notificationId);
            dict_params.put("availableForDelivery", new UserSession(Dashboard.this).getAvailableDelivery());
            dict_params.put("rideType", str_ride_type);

        } catch (IOException | JSONException e) {
            Log.v("parseError", "ParseError" + e.getLocalizedMessage());
        }
        is_updating = false;
        if (release_handler != null) {
            release_handler.removeCallbacks(runnable);
        }
        return dict_params;
    }


    private void prepareUI(LatLng latLng) {
        updateLocation();
        String tagFrom = new UserSession(this).getTagFrom();
        if (!tagFrom.equals("")) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            rlEarningsDash.setVisibility(View.GONE);
            String screen = new UserSession(Dashboard.this).getScreen();
            String str_params = new UserSession(Dashboard.this).getPushParams();
            if (screen.equals("")) {
                Intent intent = getIntent();
                screen = intent.getStringExtra("screen");
                new UserSession(this).setScreen(screen);
                str_params = intent.getStringExtra("parameters");
                new UserSession(this).setPushParams(str_params);
            }
            if (str_params != null && str_params.equals("")) {
                screen = null;
                new UserSession(Dashboard.this).removeScreen();
                new UserSession(Dashboard.this).removeTagFrom();
            }
            if (release_handler != null) {
                release_handler.removeCallbacks(runnable);
            }
            if (screen != null) {
                if (screen.equals("ride_cancelled")) {
                    isPolyLineDrawed = false;
                    Utilities.showMessage(ivUser, getResources().getString(R.string.cancel_dialog_text));
                    if (dict_ride_info != null) {
                        if (dict_ride_info.length() > 0) {
                            String rideId = dict_ride_info.optString("rideId");
                            refRideDatabase.child(new UserSession(Dashboard.this).getUserId()).child(rideId).setValue(null, null);

                            if (refDatabase != null) {
                                refDatabase.child(new UserSession(Dashboard.this).getUserId()).child(rideId).setValue(null, null);
                            }
                        }
                    }
//                       stopNavigating();
                    arrPolyLinePoints = new ArrayList<>();
                    new UserSession(Dashboard.this).removeTagFrom();
                    new UserSession(Dashboard.this).removeScreen();
                    new UserSession(Dashboard.this).removePushparams();
                    new UserSession(Dashboard.this).removeRideAccept();
                    new UserSession(Dashboard.this).removeRideStatus();
                    new UserSession(Dashboard.this).removeLocationReached();
                    new UserSession(Dashboard.this).removeRideAccept();
                    new UserSession(Dashboard.this).removeNavigationStarted();
                    new UserSession(Dashboard.this).removeUserPickup();
                    new UserSession(Dashboard.this).removeFloating();
                    new UserSession(Dashboard.this).removeChatDetails();
                    new UserSession(Dashboard.this).removePickupParams();
                    new UserSession(Dashboard.this).removeCompleteParams();
                    isAcceptClicked = false;
                    if (polyLineMarker != null) {
                        polyLineMarker.remove();
                    }
                    if (polyLineMarkerDest != null) {
                        polyLineMarkerDest.remove();
                    }
                    pickupPic = "";
                    deliveryPic = "";
                    rlGo.setVisibility(View.VISIBLE);
                    bottom_sheet.setVisibility(View.VISIBLE);
                    cardViewDetails.setVisibility(View.GONE);
                    user_bottom_sheet.setVisibility(View.GONE);
                    rlNotification.setVisibility(View.VISIBLE);
                    rlEarningsDash.setVisibility(View.VISIBLE);
                    rlEmergency.setVisibility(View.GONE);
                    rlChat.setVisibility(View.GONE);

                    mGoogleMap.clear();
                    new Handler().postDelayed(() -> {
                        mGoogleMap.clear();
                        if (mPolyline != null) {
                            mPolyline.remove();
                            mPolyline = null;
                        }
                        if (polyLineMarker != null) {
                            polyLineMarker.remove();
                            polyLineMarker = null;
                        }
                        if (polyLineMarkerDest != null) {
                            polyLineMarkerDest.remove();
                            polyLineMarkerDest = null;
                        }
                        enableDisablePointer(true);
                    }, 3000);
                    is_updating = false;
                    if (release_handler != null) {
                        release_handler.removeCallbacks(runnable);
                    }
                    String str_vehicle_type = new UserSession(Dashboard.this).getVehicleType();
                    if (!str_vehicle_type.equals("")) {
                        is_updating = false;
                        updatingStarted = false;
                        if (!is_available) {
                            updateDriverAvailability(str_vehicle_type);
                        }
                    }
                    updatingUserLocation();
                    if (mGoogleMap != null) {
                        enableDisablePointer(true);
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().
                                target(latLng)
                                .zoom(16).bearing(90).build()));
                    }
                } else {

                    String str_type = new UserSession(Dashboard.this).getDriverVehicle();
                    try {
                        assert str_params != null;
                        JSONObject dict_info = new JSONObject(str_params);
                        String str_ride_info;
                        if (dict_info.has("ride")) {
                            str_ride_info = dict_info.optString("ride");
                        } else {
                            str_ride_info = dict_info.optString("rideData");
                        }

                        dict_ride_info = new JSONObject(str_ride_info);
                        String str_user_data = dict_info.optString("user");
                        dict_user_data = new JSONObject(str_user_data);
                        if (dict_ride_info != null)
                        {
                            rlGo.setVisibility(View.GONE);
                            bottom_sheet.setVisibility(View.GONE);
                            cardViewDetails.setVisibility(View.VISIBLE);
                            user_bottom_sheet.setVisibility(View.VISIBLE);
                            rlNotification.setVisibility(View.GONE);
                        }
                        mGoogleMap.clear();
                        JSONObject dict_pickUpLocation = dict_ride_info.optJSONObject("pickUpLocation");

                        assert dict_pickUpLocation != null;
                        String str_user_lat = dict_pickUpLocation.optString("lat");
                        String str_user_lng = dict_pickUpLocation.optString("lng");
                        double user_lat = Double.parseDouble(str_user_lat);
                        double user_lng = Double.parseDouble(str_user_lng);

                        String user_pickUpPoint = dict_ride_info.optString("pickUpPoint");
                        String first_name = dict_user_data.optString("firstName");

                        String last_name = dict_user_data.optString("lastName");

                        String s1 = first_name.substring(0, 1).toUpperCase();
                        first_name = s1 + first_name.substring(1);

                        String s2 = last_name.substring(0, 1).toUpperCase();
                        last_name = s2 + last_name.substring(1);

                        String full_name = first_name + " " + last_name;
                        str_mobile_number = dict_user_data.optString("id");
                        if (dict_ride_info.optBoolean("bookForSomeone")) {
                            String name = dict_ride_info.optString("otherUserName");
                            String s11 = name.substring(0, 1).toUpperCase();
                            full_name = s11 + name.substring(1);
                            str_mobile_number = dict_ride_info.optString("otherMobileNumber");
                        }
                        String avatar = dict_user_data.optString("avatar");
                        if (new UserSession(Dashboard.this).getUserPickUp() && dict_ride_info.optBoolean("delivar")) {
                            full_name = dict_ride_info.optString("reciverUserName");
                            str_mobile_number = dict_ride_info.optString("reciverMobileNumber");
                            avatar = "";
                        }

                        tvArea.setText(user_pickUpPoint);
                        tvCancelArea.setText(user_pickUpPoint);
                        tvUserInfo.setText(full_name);
                        tvUserName.setText(full_name);
                        Uri uri = Uri.parse(avatar);
                        Glide.with(getApplicationContext()).load(uri).asBitmap().error(R.drawable.ic_person_white).placeholder(R.drawable.ic_person_white).override(50, 50).centerCrop().into(new BitmapImageViewTarget(ivPerson) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable =
                                        RoundedBitmapDrawableFactory.create(getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                ivPerson.setImageDrawable(circularBitmapDrawable);
                            }
                        });
                        double current_lat = Double.parseDouble(new UserSession(this).getLatitude());
                        double current_lng = Double.parseDouble(new UserSession(this).getLongitude());
                        LatLng destination_latlng = new LatLng(user_lat, user_lng);
                        if (Utilities.isInternetAvailable(Dashboard.this)) {
                            getDistanceAndDuration(current_lat, current_lng, user_lat, user_lng);
                        } else {
                            hideLoading();
                            Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                        }

                        boolean is_ride_accept = new UserSession(Dashboard.this).getRideAccept();
                        boolean is_ride_started = new UserSession(Dashboard.this).getRideStatus();
                        if (release_handler != null) {
                            release_handler.removeCallbacks(runnable);
                        }

                        JSONObject dict_drop_location = dict_ride_info.optJSONObject("dropLocation");
                        assert dict_drop_location != null;
                        String str_drop_lat = Objects.requireNonNull(dict_drop_location).optString("lat");
                        String str_drop_lng = dict_drop_location.optString("lng");
                        double user_drop_lat = Double.parseDouble(str_drop_lat);
                        double user_drop_lng = Double.parseDouble(str_drop_lng);

                        if (is_ride_accept) {
                            rlChat.setVisibility(View.VISIBLE);
                            showLoading();
                            tvDecline.setText(getResources().getString(R.string.cancel_ride));
                            tvRide.setText(getResources().getString(R.string.start_ride));
                            if (Utilities.isInternetAvailable(Dashboard.this)) {
                                getDistanceAndDuration(user_lat, user_lng, user_drop_lat, user_drop_lng);
                            } else {
                                hideLoading();
                                Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                            }
                            String driver_lat = new UserSession(Dashboard.this).getLatitude();
                            String driver_lng = new UserSession(Dashboard.this).getLongitude();
                            double lat = Double.parseDouble(driver_lat);
                            double lng = Double.parseDouble(driver_lng);
                            LatLng diver_lat_lng = new LatLng(lat, lng);

                            user_lat_lng = new LatLng(user_lat, user_lng);

                            Drawable dr;
                            Bitmap bitmap = null;
                            switch (str_type) {
                                case "bike":
                                    dr = ContextCompat.getDrawable(this, R.drawable.ic_bike);
                                    if (dr != null) {
                                        bitmap = ((BitmapDrawable) dr).getBitmap();
                                        bitmap = Bitmap.createScaledBitmap(bitmap, 80, 120, true);
                                    }
                                    break;
                                case "auto":
                                    dr = ContextCompat.getDrawable(this, R.drawable.ic_auto);
                                    if (dr != null) {
                                        bitmap = ((BitmapDrawable) dr).getBitmap();
                                        bitmap = Bitmap.createScaledBitmap(bitmap, 60, 90, true);
                                    }
                                    break;
                                default:
                                    dr = ContextCompat.getDrawable(this, R.drawable.car_uber);
                                    if (dr != null) {
                                        bitmap = ((BitmapDrawable) dr).getBitmap();
                                        bitmap = Bitmap.createScaledBitmap(bitmap, 60, 90, true);
                                    }
                                    break;
                            }

                            MarkerOptions driver_marker = new MarkerOptions().position(diver_lat_lng)
                                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                    .flat(true)
                                    .alpha(1)
                                    .anchor(0.5f, 0.5f)
                                    .rotation((float) bearingBetweenLocations(diver_lat_lng, user_lat_lng));
                            marker = mGoogleMap.addMarker(driver_marker);
                            MarkerOptions source_marker = new MarkerOptions().position(user_lat_lng)
                                    .icon(bitmapDescriptorFromVectorNew(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.ic_popup))));
                            mGoogleMap.addMarker(source_marker);


                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(diver_lat_lng);
                            builder.include(user_lat_lng);
                            LatLngBounds bounds = builder.build();
                            int width = getResources().getDisplayMetrics().widthPixels;
                            int padding = (int) (width * 0.25);
                            if (fragment != null) {
                                int height;
                                View v = fragment.getView();
                                if (v != null) {
                                    height = v.getHeight();

                                    CameraUpdate cu;
                                    try {
                                        cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                                    } catch (IllegalStateException e) {
                                        padding = (int) (width * 0.55);
                                        cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                                    }

                                    mGoogleMap.moveCamera(cu);
                                    mGoogleMap.animateCamera(cu);
                                    mGoogleMap.setOnMarkerClickListener(marker -> true);
                                }
                                if (arrPolyLinePoints.isEmpty()) {
                                    String url = getDirectionsUrl(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), user_lat_lng);
                                    DownloadTask downloadTask = new DownloadTask();
                                    downloadTask.execute(url);
                                }
                            }
                        } else if (is_ride_started) {
                            rlChat.setVisibility(View.VISIBLE);
                            tvNavigate.setText(getResources().getString(R.string.navigate_customer));
                            if (Utilities.isInternetAvailable(Dashboard.this)) {
                                getDistanceAndDuration(user_lat, user_lng, user_drop_lat, user_drop_lng);
                            } else {
                                hideLoading();
                                Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                            }
                            boolean is_location_reached = new UserSession(Dashboard.this).getLocationReached();
                            if (is_location_reached) {
                                is_dest_show = true;
                                tvDecline.setText(getResources().getString(R.string.cancel_ride));
                                tvRide.setText(getResources().getString(R.string.pickup));
                                tvUserInfo.setVisibility(View.VISIBLE);
                                rlEmergency.setVisibility(View.VISIBLE);
                                driver_old_latlng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                                user_lat_lng = new LatLng(user_lat, user_lng);
                                driver_old_latlng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                old_position = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                String str_drop_location_lat = dict_drop_location.optString("lat");
                                String str_drop_location_lng = dict_drop_location.optString("lng");
                                double drop_location_lat = Double.parseDouble(str_drop_location_lat);
                                double drop_location_lng = Double.parseDouble(str_drop_location_lng);
                                LatLng dest_lat_lng = new LatLng(drop_location_lat, drop_location_lng);

                                Drawable dr;
                                Bitmap bitmap = null;
                                switch (str_type) {
                                    case "bike":
                                        dr = ContextCompat.getDrawable(this, R.drawable.ic_bike);
                                        if (dr != null) {
                                            bitmap = ((BitmapDrawable) dr).getBitmap();
                                            bitmap = Bitmap.createScaledBitmap(bitmap, 80, 120, true);
                                        }
                                        break;
                                    case "auto":
                                        dr = ContextCompat.getDrawable(this, R.drawable.ic_auto);
                                        if (dr != null) {
                                            bitmap = ((BitmapDrawable) dr).getBitmap();
                                            bitmap = Bitmap.createScaledBitmap(bitmap, 60, 90, true);
                                        }
                                        break;
                                    default:
                                        dr = ContextCompat.getDrawable(this, R.drawable.car_uber);
                                        if (dr != null) {
                                            bitmap = ((BitmapDrawable) dr).getBitmap();
                                            bitmap = Bitmap.createScaledBitmap(bitmap, 60, 90, true);
                                        }
                                        break;
                                }

                                MarkerOptions pickup_marker = new MarkerOptions().position(user_lat_lng)
                                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                        .flat(true)
                                        .alpha(1)
                                        .anchor(0.5f, 0.5f)
                                        .rotation((float) bearingBetweenLocations(user_lat_lng, dest_lat_lng));


                                @SuppressLint("InflateParams") View dest_view = ((LayoutInflater) Objects.requireNonNull(getSystemService(Context.LAYOUT_INFLATER_SERVICE))).inflate(R.layout.info_window_layout, null);
                                String str_destination_address = dict_ride_info.optString("dropPoint");
                                tvArea.setText(str_destination_address);
                                LinearLayout llDTime = dest_view.findViewById(R.id.llTime);
                                llDTime.setVisibility(View.GONE);
                                CustomTextView tvDMarkerLocation = dest_view.findViewById(R.id.tvMarkerLocation);
                                String[] arr_destination = str_destination_address.split(",");
                                String destination = arr_destination[0];
                                tvDMarkerLocation.setText(destination);
                                MarkerOptions dropMarker = new MarkerOptions().position(dest_lat_lng)
                                        .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(dest_view)));
                                mGoogleMap.clear();

                                mGoogleMap.setOnMarkerClickListener(marker -> true);
                                marker = mGoogleMap.addMarker(pickup_marker);
                                mGoogleMap.addMarker(dropMarker);

                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                builder.include(user_lat_lng);
                                builder.include(dest_lat_lng);
                                LatLngBounds bounds = builder.build();

                                int width = getResources().getDisplayMetrics().widthPixels;
                                int padding = (int) (width * 0.4);
                                if (fragment != null) {
                                    int height;
                                    View v = fragment.getView();
                                    if (v != null) {
                                        height = v.getHeight();
                                        CameraUpdate cu;
                                        try {
                                            cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                                        } catch (IllegalStateException e) {
                                            padding = (int) (width * 0.55);
                                            cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                                        }
                                        mGoogleMap.moveCamera(cu);
                                        mGoogleMap.animateCamera(cu);
                                    }


                                    boolean is_navigation_started = new UserSession(Dashboard.this).getNavigationStarted();
                                    if (is_navigation_started) {
                                        tvNavigate.setText(getResources().getString(R.string.navigate_drop));
                                        tvUserInfo.setVisibility(View.GONE);
                                        rlNavigate.setVisibility(View.VISIBLE);
                                        rlDecline.setVisibility(View.GONE);
                                        tvRide.setText(getResources().getString(R.string.completed_ride));
                                           /*Point userP = Point.fromLngLat(user_drop_lng, user_drop_lat);
                                           Point driverP = Point.fromLngLat(currentLocation.getLongitude(), currentLocation.getLatitude());
                                           startNavigation(driverP, userP);*/
                                    } else {
                                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                        tvUserInfo.setVisibility(View.GONE);
                                        rlNavigate.setVisibility(View.VISIBLE);
                                    }
                                    if (arrPolyLinePoints != null) {
                                        updateLocation();
                                        String url1 = getDirectionsUrl(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), dest_lat_lng);
                                        DownloadTask downloadTask1 = new DownloadTask();
                                        downloadTask1.execute(url1);
                                    }
                                }
                            } else {
                                showLoading();
                                tvRide.setText(getResources().getString(R.string.pickup));
                                tvDecline.setText(getResources().getString(R.string.cancel_ride));
                                tvUserInfo.setVisibility(View.VISIBLE);

                                String driver_lat = new UserSession(Dashboard.this).getLatitude();
                                String driver_lng = new UserSession(Dashboard.this).getLongitude();
                                double lat = Double.parseDouble(driver_lat);
                                double lng = Double.parseDouble(driver_lng);
                                LatLng diver_lat_lng = new LatLng(lat, lng);

                                user_lat_lng = new LatLng(user_lat, user_lng);
                                driver_old_latlng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                old_position = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                Drawable dr;
                                Bitmap bitmap = null;
                                switch (str_type) {
                                    case "bike":
                                        dr = ContextCompat.getDrawable(this, R.drawable.ic_bike);
                                        if (dr != null) {
                                            bitmap = ((BitmapDrawable) dr).getBitmap();
                                            bitmap = Bitmap.createScaledBitmap(bitmap, 80, 120, true);
                                        }
                                        break;
                                    case "auto":
                                        dr = ContextCompat.getDrawable(this, R.drawable.ic_auto);
                                        if (dr != null) {
                                            bitmap = ((BitmapDrawable) dr).getBitmap();
                                            bitmap = Bitmap.createScaledBitmap(bitmap, 60, 90, true);
                                        }
                                        break;
                                    default:
                                        dr = ContextCompat.getDrawable(this, R.drawable.car_uber);
                                        if (dr != null) {
                                            bitmap = ((BitmapDrawable) dr).getBitmap();
                                            bitmap = Bitmap.createScaledBitmap(bitmap, 60, 90, true);
                                        }
                                        break;
                                }

                                MarkerOptions driver_marker = new MarkerOptions().position(diver_lat_lng)
                                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                        .flat(true)
                                        .alpha(1)
                                        .anchor(0.5f, 0.5f)
                                        .rotation((float) bearingBetweenLocations(diver_lat_lng, user_lat_lng));
                                marker = mGoogleMap.addMarker(driver_marker);
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                builder.include(diver_lat_lng);
                                builder.include(user_lat_lng);
                                LatLngBounds bounds = builder.build();

                                int width = getResources().getDisplayMetrics().widthPixels;
                                int padding = (int) (width * 0.25);
                                if (fragment != null) {
                                    int height;
                                    View v = fragment.getView();
                                    if (v != null) {
                                        height = v.getHeight();
                                        CameraUpdate cu;
                                        try {
                                            cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                                        } catch (IllegalStateException e) {
                                            padding = (int) (width * 0.55);
                                            cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                                        }

                                        mGoogleMap.moveCamera(cu);
                                        mGoogleMap.animateCamera(cu);
                                        mGoogleMap.setOnMarkerClickListener(marker -> true);
                                    }


                                    boolean is_navigation_started = new UserSession(Dashboard.this).getNavigationStarted();
                                    if (is_navigation_started) {
                                        tvUserInfo.setVisibility(View.GONE);
                                        rlDecline.setVisibility(View.GONE);
                                        tvRide.setText(getResources().getString(R.string.completed_ride));
                                           /*Point userP = Point.fromLngLat(user_drop_lng, user_drop_lat);
                                           Point driverP = Point.fromLngLat(currentLocation.getLongitude(), currentLocation.getLatitude());
                                           startNavigation(driverP, userP);*/
                                    } else {
                                           /*Point userP = Point.fromLngLat(user_lng, user_lat);
                                           Point driverP = Point.fromLngLat(currentLocation.getLongitude(), currentLocation.getLatitude());
                                           startNavigation(driverP, userP);*/
                                        final Handler handler = new Handler();
                                        handler.postDelayed(() -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED), 2000);
                                        tvUserInfo.setVisibility(View.GONE);
                                        rlNavigate.setVisibility(View.VISIBLE);
                                    }
                                    if (arrPolyLinePoints != null) {
                                        updateLocation();
                                        String url1 = getDirectionsUrl(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), user_lat_lng);
                                        DownloadTask downloadTask1 = new DownloadTask();
                                        downloadTask1.execute(url1);
                                    }
                                }
                            }
                        } else {
                            long notification_time = new UserSession(Dashboard.this).getNotificationReceivedTime();
                            long current_time = new Date().getTime();
                            notification_time = notification_time + 18 * 1000L;
                            if (notification_time > current_time) {
                                is_updating = false;

                                if (release_handler != null) {
                                    release_handler.removeCallbacks(runnable);
                                }
                                String str_vehicle_type = new UserSession(Dashboard.this).getVehicleType();
                                if (!str_vehicle_type.equals("")) {
                                    is_available = true;
                                    updateDriverAvailability(str_vehicle_type);
                                }
                                tvDecline.setText(getResources().getString(R.string.decline_ride));
                                tvRide.setText(getResources().getString(R.string.accept_ride));
                                if (Utilities.isInternetAvailable(Dashboard.this)) {
                                    getDistanceAndDuration(user_lat, user_lng, user_drop_lat, user_drop_lng);
                                } else {
                                    hideLoading();
                                    Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                                }
                                long seconds = notification_time - current_time;
                                rideHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        rideRunnable = this;
                                        boolean isDeclinedClicked = new UserSession(Dashboard.this).getDeclined();
                                        if (!new UserSession(Dashboard.this).getRideAccept()
                                                && !new UserSession(Dashboard.this).getRideStatus() && !isAcceptClicked && !isDeclinedClicked) {
                                            new UserSession(Dashboard.this).removeTagFrom();
                                            new UserSession(Dashboard.this).removeScreen();
                                            new UserSession(Dashboard.this).removePushparams();
                                            new UserSession(Dashboard.this).removeRideAccept();
                                            new UserSession(Dashboard.this).removeRideStatus();
                                            new UserSession(Dashboard.this).removeNavigationStarted();
                                            new UserSession(Dashboard.this).removeUserPickup();
                                            new UserSession(Dashboard.this).removeNotificaionReceiedTime();
                                            new UserSession(Dashboard.this).removeFloating();
                                            new UserSession(Dashboard.this).removeChatDetails();
                                            isAcceptClicked = false;
                                            pickupPic = "";
                                            deliveryPic = "";
                                            rlGo.setVisibility(View.VISIBLE);
                                            bottom_sheet.setVisibility(View.VISIBLE);
                                            cardViewDetails.setVisibility(View.GONE);
                                            user_bottom_sheet.setVisibility(View.GONE);
                                            rlNotification.setVisibility(View.VISIBLE);
                                            mGoogleMap.clear();
                                            if (mPolyline != null) {
                                                mPolyline.remove();
                                            }
                                            mGoogleMap.clear();
                                            is_updating = false;

                                            if (release_handler != null) {
                                                release_handler.removeCallbacks(runnable);
                                            }
                                            if (!is_available)
                                            {
                                                updateDriverAvailability(str_vehicle_type);
                                                updatingUserLocation();
                                            }
                                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().
                                                    target(latLng)
                                                    .zoom(16).bearing(90).build()));
                                            enableDisablePointer(true);
                                        }
                                        new UserSession(Dashboard.this).removeDeclined();
                                        if (rideHandler != null) {
                                            if (rideRunnable != null)
                                                rideHandler.removeCallbacks(rideRunnable);
                                            rideHandler = new Handler();
                                        }
                                    }
                                }, seconds);

                                mGoogleMap.clear();

                                mGoogleMap.setOnMarkerClickListener(marker -> true);

                                Drawable dr;
                                Bitmap bitmap = null;
                                switch (str_type) {
                                    case "bike":
                                        dr = ContextCompat.getDrawable(this, R.drawable.ic_bike);
                                        if (dr != null) {
                                            bitmap = ((BitmapDrawable) dr).getBitmap();
                                            bitmap = Bitmap.createScaledBitmap(bitmap, 80, 120, true);
                                        }
                                        break;
                                    case "auto":
                                        dr = ContextCompat.getDrawable(this, R.drawable.ic_auto);
                                        if (dr != null) {
                                            bitmap = ((BitmapDrawable) dr).getBitmap();
                                            bitmap = Bitmap.createScaledBitmap(bitmap, 60, 90, true);
                                        }
                                        break;
                                    default:
                                        dr = ContextCompat.getDrawable(this, R.drawable.car_uber);
                                        if (dr != null) {
                                            bitmap = ((BitmapDrawable) dr).getBitmap();
                                            bitmap = Bitmap.createScaledBitmap(bitmap, 60, 90, true);
                                        }
                                        break;
                                }

                                Drawable dr1 = ContextCompat.getDrawable(this, R.drawable.ic_pin_marker);
                                if (dr1 != null) {
                                    MarkerOptions dropMarker = new MarkerOptions().position(destination_latlng)
                                            .icon(bitmapDescriptorFromVectorNew(dr1));
                                    MarkerOptions source_marker = new MarkerOptions().position(latLng)
                                            .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                            .flat(true)
                                            .alpha(1)
                                            .anchor(0.5f, 0.5f)
                                            .rotation((float) bearingBetweenLocations(latLng, destination_latlng));

                                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                    builder.include(latLng);
                                    builder.include(destination_latlng);
                                    LatLngBounds bounds = builder.build();

                                    int width = getResources().getDisplayMetrics().widthPixels;
                                    int padding = (int) (width * 0.25);

                                    if (fragment != null) {
                                        int height;
                                        View v = fragment.getView();
                                        if (v != null) {
                                            height = v.getHeight();
                                            CameraUpdate cu;
                                            try {
                                                cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                                            } catch (IllegalStateException e) {
                                                padding = (int) (width * 0.55);
                                                cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                                            }

                                            mGoogleMap.moveCamera(cu);
                                            mGoogleMap.animateCamera(cu);
                                            mGoogleMap.setOnMarkerClickListener(marker -> true);

                                            mGoogleMap.addMarker(source_marker);
                                            if (mGoogleMap != null)
                                                enableDisablePointer(false);
                                            marker = mGoogleMap.addMarker(dropMarker);
                                        }
                                        if (arrPolyLinePoints.isEmpty()) {
                                            showLoading();
                                            String url = getDirectionsUrl(latLng, destination_latlng);
                                            DownloadTask downloadTask = new DownloadTask();
                                            downloadTask.execute(url);
                                        }
                                    }
                                }
                            } else {
                                new UserSession(Dashboard.this).removeTagFrom();
                                new UserSession(Dashboard.this).removeScreen();
                                new UserSession(Dashboard.this).removePushparams();
                                new UserSession(Dashboard.this).removeRideAccept();
                                new UserSession(Dashboard.this).removeRideStatus();
                                new UserSession(Dashboard.this).removeNavigationStarted();
                                new UserSession(Dashboard.this).removeUserPickup();
                                new UserSession(Dashboard.this).removeNotificaionReceiedTime();
                                new UserSession(Dashboard.this).removeFloating();
                                new UserSession(Dashboard.this).removeChatDetails();
                                isAcceptClicked = false;
                                pickupPic = "";
                                deliveryPic = "";
                                rlGo.setVisibility(View.VISIBLE);
                                bottom_sheet.setVisibility(View.VISIBLE);
                                cardViewDetails.setVisibility(View.GONE);
                                user_bottom_sheet.setVisibility(View.GONE);
                                rlNotification.setVisibility(View.VISIBLE);
                                mGoogleMap.clear();
                                if (mPolyline != null) {
                                    mPolyline.remove();
                                }
                                is_updating = false;
                                if (release_handler != null) {
                                    release_handler.removeCallbacks(runnable);
                                }
                                is_available = false;
                                String str_vehicle_type = new UserSession(this).getVehicleType();
                                updateDriverAvailability(str_vehicle_type);
                                updatingUserLocation();
                                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().
                                        target(latLng)
                                        .zoom(16).bearing(90).build()));
                                enableDisablePointer(true);

                            }
                        }
                    } catch (JSONException e) {
                        user_bottom_sheet.setVisibility(View.GONE);
                        cardViewDetails.setVisibility(View.GONE);
                    }
                }
            }
        } else {
            rlEarningsDash.setVisibility(View.VISIBLE);
            rlNotification.setVisibility(View.VISIBLE);
            rlEmergency.setVisibility(View.GONE);
            rlChat.setVisibility(View.GONE);
            updatingUserLocation();
        }
    }

    private void initUI() {
        locationManager = (LocationManager) Dashboard.this.getSystemService(Context.LOCATION_SERVICE);
        geocoder = new Geocoder(Dashboard.this, Locale.getDefault());
        checkLocation();
    }

    private void checkLocation() {
        locationManager = (LocationManager) Dashboard.this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(Dashboard.this)) {
                fetchLocation();
            } else {
                enableLoc();
            }
        }
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(Dashboard.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Dashboard.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Dashboard.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Dashboard.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_CODE);
        } else {
            updateLocation();
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            currentLocation = location;
                            String latitude = String.valueOf(currentLocation.getLatitude());
                            String longitude = String.valueOf(currentLocation.getLongitude());
                            new UserSession(Dashboard.this).setLatitude(latitude);
                            new UserSession(Dashboard.this).setLongitude(longitude);
                            fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                            if (fragment != null) {
                                mapView = fragment.getView();
                                fragment.getMapAsync(Dashboard.this);
                            }
                        }
                    });
        }
    }

    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    private void enableLoc() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(Dashboard.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {

                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            mGoogleApiClient.connect();
                        }
                    }).build();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(result1 -> {
                final Status status = result1.getStatus();
                if (status.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                    try {
                        status.startResolutionForResult(Dashboard.this, REQUEST_CODE);
                    } catch (IntentSender.SendIntentException e) {
                        Log.v("IntentSender", "IntentSender" + e.getLocalizedMessage());
                    }
                }
            });

            if (ActivityCompat.checkSelfPermission(Dashboard.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Dashboard.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Dashboard.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Dashboard.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_CODE);
            } else {
                updateLocation();
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, location -> {
                            if (location != null) {
                                currentLocation = location;
                                String latitude = String.valueOf(currentLocation.getLatitude());
                                String longitude = String.valueOf(currentLocation.getLongitude());
                                new UserSession(Dashboard.this).setLatitude(latitude);
                                new UserSession(Dashboard.this).setLongitude(longitude);
                                fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                                if (fragment != null) {
                                    mapView = fragment.getView();
                                    fragment.getMapAsync(Dashboard.this);
                                }
                            }
                        });
            }
        } else {
            if (ActivityCompat.checkSelfPermission(Dashboard.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Dashboard.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Dashboard.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Dashboard.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_CODE);
            } else {
                updateLocation();

                fusedLocationClient.getLastLocation().addOnSuccessListener(this, location ->
                {
                    if (location != null) {
                        currentLocation = location;
                        String latitude = String.valueOf(currentLocation.getLatitude());
                        String longitude = String.valueOf(currentLocation.getLongitude());
                        new UserSession(Dashboard.this).setLatitude(latitude);
                        new UserSession(Dashboard.this).setLongitude(longitude);
                        fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                        if (fragment != null) {
                            mapView = fragment.getView();
                            fragment.getMapAsync(Dashboard.this);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(Dashboard.this, R.raw.map_styling));
            Utilities.hideSoftKeyboard(Dashboard.this);
            if (!success) {
                Log.e("Maps", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("Maps", "Can't find style. Error: ", e);
        }
        mGoogleMap = googleMap;
        enableDisablePointer(true);
        mGoogleMap.getUiSettings().setCompassEnabled(false);

        if (new UserSession(Dashboard.this).getDriverAvailability()) {
            getHeatMapData();
        }

        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            ImageView btnMyLocation = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            btnMyLocation.setImageDrawable(ContextCompat.getDrawable(Dashboard.this, R.drawable.ic_gps_location));
            locationButton.setBackground(ContextCompat.getDrawable(Dashboard.this, R.drawable.circle_white));
            locationButton.setPadding(30, 30, 30, 30);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 40, 200);
        }

        if (currentLocation != null) {
            enableDisablePointer(true);

            if (!is_map_loaded) {
                is_map_loaded = true;
                LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mGoogleMap.setTrafficEnabled(false);
                mGoogleMap.setIndoorEnabled(false);
                mGoogleMap.setBuildingsEnabled(false);
                mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
                mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);
                enableDisablePointer(true);
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().
                        target(latLng)
                        .zoom(13).build()));
                if (is_available) {
                    long expiry_date = new UserSession(Dashboard.this).getDriverExpiryDate();
                    long current_date = new Date().getTime();
                    if (current_date > expiry_date && expiry_date > 0) {
                        if (Utilities.isInternetAvailable(Dashboard.this)) {
                            updateDriverStatus();
                        } else {
                            Utilities.showMessage(const_main, getResources().getString(R.string.internet_error));
                        }

                    } else {
                        String str_type = new UserSession(Dashboard.this).getDriverVehicle();
                        if (str_type.equals("bike") || str_type.equals("auto")) {
                            boolean is_ou_station = new UserSession(Dashboard.this).getOutStationStatus();
                            if (is_ou_station) {
                                is_updating = false;
                                if (release_handler != null) {
                                    release_handler.removeCallbacks(runnable);
                                }
                                changeFromOutStationToInCity();
                            }
                        }
                        prepareUI(latLng);
                    }
                } else {
                    prepareUI(latLng);
                }
            }
        }
    }

    private Bitmap getMarkerBitmapFromView(View view) {

        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = view.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        view.draw(canvas);
        return returnedBitmap;
    }


    private void updatingUserLocation() {
        if (is_available) {

            String str_vehicle_type = new UserSession(this).getVehicleType();
            if (!str_vehicle_type.equals("")) {
                if (new UserSession(Dashboard.this).getScreen().equals("")) {
                    if (Utilities.isInternetAvailable(Dashboard.this)) {
                        release_handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                runnable = this;
                                if (new UserSession(Dashboard.this).getScreen().equals("")) {
                                    is_available = new UserSession(Dashboard.this).getDriverAvailability();
                                    if (new UserSession(Dashboard.this).getDriverAvailability()) {
                                        boolean is_out_station = new UserSession(Dashboard.this).getOutStationStatus();
                                        if (is_out_station) {
                                            String vehicle_type = new UserSession(Dashboard.this).getDriverVehicle();
                                            if (!vehicle_type.equals("bike") && !vehicle_type.equals("auto")) {
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        getCustomerStatus(new UserSession(Dashboard.this).getUserId());
                                                        release_handler.postDelayed(this, 60000);
                                                    }
                                                }, 10000);

                                            }
                                        } else {
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    /*is_updating = true;
                                                    updatingStarted = true;
                                                    updateDriverAvailabilityEveryMinute(new UserSession(Dashboard.this).getVehicleType());*/
                                                    getCustomerStatus(new UserSession(Dashboard.this).getUserId());
                                                    release_handler.postDelayed(this, 60000);
                                                }
                                            }, 10000);
                                        }
                                    } else {
                                        release_handler.removeCallbacks(this);
                                        is_updating = false;
                                    }
                                } else {
                                    release_handler.removeCallbacks(this);
                                }
                            }
                        }, 60000);
                    } else {
                        Utilities.showMessage(const_main, getResources().getString(R.string.internet_fail));
                    }

                }
            }
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        mapAPI = new UserSession(Dashboard.this).getAPI();
        if (mapAPI.equals("")) {
            mapAPI = getResources().getString(R.string.map_api_key);
        }
        // Building the parameters to the web service
        String key = "key=" + mapAPI;

        String parameters = str_origin + "&" + str_dest + "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service

        return "https://maps.googleapis.com/maps/api/directions/" + output + "?"
                + "mode=driving&"
                + "transit_routing_preference=less_driving&" + parameters;
    }


    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d(getClass().getName(), e.toString());
        } finally {
            assert iStream != null;
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            if (is_internet_disconected) {
                is_internet_disconected = false;
                isPolyLineDrawed = false;
                updateLocation();
                if (new UserSession(Dashboard.this).getRideAccept()
                        || new UserSession(Dashboard.this).getRideStatus()
                        || new UserSession(Dashboard.this).getUserPickUp()) {
                    addDataListeners();
                }
            }
        } else {
            is_internet_disconected = true;
        }
    }

    private void addDataListeners() {
        String tagFrom = new UserSession(this).getTagFrom();
        if (!tagFrom.equals("")) {
            if (new UserSession(Dashboard.this).getRideAccept()
                    || new UserSession(Dashboard.this).getRideStatus()
                    || new UserSession(Dashboard.this).getUserPickUp()) {
                if (refRideDatabase != null) {

                    String params = new UserSession(Dashboard.this).getPushParams();
                    try {
                        JSONObject dicParams = new JSONObject(params);
                        String strRide;
                        if (dicParams.has("rideData")) {
                            strRide = dicParams.optString("rideData");
                        } else {
                            strRide = dicParams.optString("ride");
                        }
                        if (!strRide.equals("")) {
                            JSONObject dicRide = new JSONObject(strRide);
                            String rideId = dicRide.optString("rideId");

                            Query lastQuery = refRideDatabase.child(new UserSession(Dashboard.this).getUserId()).child(rideId);
                            reflistiner = lastQuery.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Object snapShot = dataSnapshot.getValue();
                                    if (snapShot != null) {
                                        Gson gson = new Gson();
                                        String json = gson.toJson(snapShot);
                                        try {
                                            JSONObject dictJson = new JSONObject(json);
                                            TripDetails details = gson.fromJson(Objects.requireNonNull(dictJson).toString(), TripDetails.class);
                                            if (details != null) {
                                                String status = details.getStatus();
                                                String params = details.getPushParams();
                                                String screen = new UserSession(Dashboard.this).getScreen();
                                                if (!screen.equals(status) && status.equals("ride_cancelled")) {
                                                    new UserSession(Dashboard.this).setScreen(status);
                                                    new UserSession(Dashboard.this).setPushParams(params);
                                                    new UserSession(Dashboard.this).setTagFrom("push");
                                                    if (is_map_loaded) {
                                                        if (reflistiner != null) {
                                                            refRideDatabase.removeEventListener(reflistiner);
                                                        }
                                                        reflistiner = null;
                                                        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                                        prepareUI(latLng);
                                                    }
                                                }
                                            }
                                        } catch (JSONException e) {
                                            Log.v("JSONException111", "JSONException111" + e.getLocalizedMessage());
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Timber.v("onCancelled" + databaseError.getMessage());
                                }
                            });
                        }
                    } catch (JSONException e) {
                        Log.v("cancelled", "exception" + e.getLocalizedMessage());
                    }
                }
            }
        }
    }

    @Override
    public void onProviderChange(boolean isConected) {
        if (isConected) {
            if (!is_map_loaded) {
                showLoading();
                checkLocation();
            }
        }
    }

    public void registerInternetConnecetivity() {
        try {
            ConnectivityReceiver receiver = new ConnectivityReceiver();
            IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
            registerReceiver(receiver, filter);
            setConnectivityListener(Dashboard.this);
        } catch (Exception e) {
            Log.v("Exception ===", "Exception === " + e);
        }
    }

    public void registerLocationConnectivity() {
        try {
            MyLocationReceiver receiver = new MyLocationReceiver();
            IntentFilter filter = new IntentFilter(LocationService.BROADCAST_ACTION);
            registerReceiver(receiver, filter);
            setLocationReceiverList(Dashboard.this);
        } catch (Exception e) {
            Log.v("Exception97", "Exception97" + e);
        }
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    public void setLocationReceiverList(MyLocationReceiver.LocationReceiverListener locationReceiverListener) {
        MyLocationReceiver.setLocationReceiver(locationReceiverListener);
    }


    public void onClickEarnings(View view) {
        drawer.closeDrawer(GravityCompat.START);
        EarningsDialogFragment dialogFragment = EarningsDialogFragment.newInstance();
        dialogFragment.show(getSupportFragmentManager(), "Earnings  Dialiog");
        dialogFragment.setCancelable(true);
    }

    @Override
    public void onClickReason(String selectedReason) {
        completeReason = selectedReason;
        etReason.setText("");
        if (dict_user_data != null)
        {
            String user_notification_id = dict_user_data.optString("notificationId");
            String rideId = dict_ride_info.optString("rideId");
            JSONObject cost_obj = dict_ride_info.optJSONObject("costObject");
            JSONObject dictParams = new JSONObject();
            try {
                List<Address> currentAddress = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                if (!currentAddress.isEmpty()) {

                    String strCurrentAddress = currentAddress.get(0).getAddressLine(0);

                    dictParams.put("rideId", rideId);
                    dictParams.put("distanceInKm", finalDis);
                    dictParams.put("timeInMin", finalTime);
                    dictParams.put("partialRide", true);
                    dictParams.put("driverPartialRideReason", selectedReason);
                    dictParams.put("costObj", cost_obj);
                    dictParams.put("userNotificationId", user_notification_id);
                    dictParams.put("partialRideCompletedAt", strCurrentAddress);
                }
            } catch (JSONException e) {
                Log.v("JsonExec e", "JsonExec e" + e.getLocalizedMessage());
            } catch (IOException e) {
                Log.v("IOException e", "IOException e" + e.getLocalizedMessage());
            }

            if (dict_ride_info.optBoolean("delivar")) {
                if (deliveryPic.equals("")) {
                    new UserSession(Dashboard.this).setCompleteParams(dictParams.toString());
                }
            }
        }
    }

    @Override
    public void onLocationUpdate(Location location) {
//        setLocationChangedData(location);
        currentLocation = location;
        new UserSession(Dashboard.this).setLatitude(String.valueOf(location.getLatitude()));
        new UserSession(Dashboard.this).setLongitude(String.valueOf(location.getLongitude()));
        setLocationChangedData(currentLocation, arrivalDuration, arrivalDistance, isRouteChanged);
//        setLocationChangedData(location, arrivalDistance, arrivalDistance, false);
    }

   /* @Override
    public void onProgressChange(Location location, RouteProgress routeProgress) {
       *//* arrivalDuration = routeProgress.durationRemaining();
        arrivalDistance = routeProgress.distanceRemaining();
        setLocationChangedData(currentLocation, arrivalDuration, arrivalDistance, false);*//*
    }*/

    /*@Override
    public void userOffRoute(Location location) {
        Toast.makeText(this, "route found", Toast.LENGTH_SHORT).show();
        if (dict_ride_info != null)
        {
            JSONObject dictPickupData;
            if (dict_ride_info.has("pickUpLocation")) {
                dictPickupData = dict_ride_info.optJSONObject("pickUpLocation");
                if (dictPickupData != null)
                {
                    double lat = dictPickupData.optDouble("lat");
                    double lng = dictPickupData.optDouble("lng");
                    Point destP = Point.fromLngLat(lng, lat);
                    Point sourceP = Point.fromLngLat(currentLocation.getLongitude(), currentLocation.getLatitude());
                    if (navigation != null)
                    {
                        navigation.stopNavigation();
                    }
                    startNavigation(sourceP, destP);
                    setLocationChangedData(currentLocation, arrivalDuration, arrivalDistance, true);
                }
            }
        }
    }*/

    /*@Override
    public void onRunning(boolean running)
    {
        if (running)
        {
            navigation.addProgressChangeListener(Dashboard.this);
            navigation.addOffRouteListener(this);
        }
    }*/


    // Fetches data from url passed
    @SuppressLint("StaticFieldLeak")
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("DownloadTask", "DownloadTask : " + data);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                Log.v("parseError", "ParseError" + e.getLocalizedMessage());
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            arrPolyLinePoints = new ArrayList<>();
            PolylineOptions lineOptions = null;

            if (result != null) {
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<>();
                    lineOptions = new PolylineOptions();
                    List<HashMap<String, String>> path = result.get(i);
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);
                        double lat = Double.parseDouble(Objects.requireNonNull(point.get("lat")));
                        double lng = Double.parseDouble(Objects.requireNonNull(point.get("lng")));
                        LatLng position = new LatLng(lat, lng);
                        points.add(position);
                    }
                    lineOptions.addAll(points);
                    lineOptions.width(10);
                    lineOptions.startCap(new SquareCap());
                    lineOptions.endCap(new SquareCap());
                    lineOptions.jointType(ROUND);
                    lineOptions.color(ContextCompat.getColor(Dashboard.this, R.color.polyline_primary_color));
                }
                if (lineOptions != null) {
                    if (mPolyline != null) {
                        mPolyline.remove();
                    }
                    hideLoading();
                    mPolyline = mGoogleMap.addPolyline(lineOptions);
                    if (new UserSession(Dashboard.this).getRideStatus() && !points.isEmpty()) {

                        if (new UserSession(Dashboard.this).getUserPickUp()) {
                            arrPolyLinePoints = points;
                            LatLng latLng = points.get(0);
                            isPolyLineDrawed = false;
                            updateUI(latLng);

                            if (polyLineMarkerDest != null) {
                                polyLineMarkerDest.remove();
                            }
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                                    .icon(bitmapDescriptoryPinFromVector(Dashboard.this, R.drawable.ic_popup))
                                    .flat(true)
                                    .alpha(1)
                                    .anchor(0.5f, 0.5f);
                            if (polyLineMarker == null) {
                                polyLineMarker = mGoogleMap.addMarker(markerOptions);
                            } else {
                                polyLineMarker.setPosition(latLng);
                            }

                        } else {
                            arrPolyLinePoints = points;
                            LatLng latLng = points.get(0);
                            LatLng latLng1 = points.get(points.size() - 1);
                            isPolyLineDrawed = false;
                            updateUI(latLng);
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                                    .icon(bitmapDescriptoryPinFromVector(Dashboard.this, R.drawable.ic_popup))
                                    .flat(true)
                                    .alpha(1)
                                    .anchor(0.5f, 0.5f);
                            if (polyLineMarker == null) {
                                polyLineMarker = mGoogleMap.addMarker(markerOptions);
                            } else {
                                polyLineMarker.setPosition(latLng);
                            }

                            MarkerOptions markerOptions1 = new MarkerOptions().position(latLng1)
                                    .icon(bitmapDescriptoryPinFromVector(Dashboard.this, R.drawable.ic_square))
                                    .flat(true)
                                    .alpha(1)
                                    .anchor(0.5f, 0.5f);
                            if (polyLineMarkerDest == null) {
                                polyLineMarkerDest = mGoogleMap.addMarker(markerOptions1);
                            } else {
                                polyLineMarkerDest.setPosition(latLng1);
                            }
                        }
                    } else if (new UserSession(Dashboard.this).getRideAccept() && !points.isEmpty()) {
                        LatLng latLng = points.get(0);
                        MarkerOptions markerOptions1 = new MarkerOptions().position(latLng)
                                .icon(bitmapDescriptoryPinFromVector(Dashboard.this, R.drawable.ic_square))
                                .flat(true)
                                .alpha(1)
                                .anchor(0.5f, 0.5f);
                        if (polyLineMarker == null) {
                            polyLineMarker = mGoogleMap.addMarker(markerOptions1);
                        } else {
                            polyLineMarker.setPosition(latLng);
                        }
                    }
                } else {
                    hideLoading();
                    Utilities.showMessage(drawer, "No route is found");
                }
            }
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVectorNew(Drawable background) {

        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_navigation);
        assert background != null;
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private BitmapDescriptor bitmapDescriptoryPinFromVector(Context context, int id) {
        Drawable background = ContextCompat.getDrawable(context, id);
        assert background != null;
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(Dashboard.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    if (mGoogleApiClient == null) {
                        fetchLocation();
                        enableLoc();
                    }

                }
                updateLocation();

                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(Dashboard.this, location -> {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                currentLocation = location;
                                String latitude = String.valueOf(currentLocation.getLatitude());
                                String longitude = String.valueOf(currentLocation.getLongitude());
                                new UserSession(Dashboard.this).setLatitude(latitude);
                                new UserSession(Dashboard.this).setLongitude(longitude);
                                fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                                if (fragment != null) {
                                    mapView = fragment.getView();
                                    fragment.getMapAsync(Dashboard.this);
                                }
                            }
                        });
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        new UserSession(Dashboard.this).setLatitude(String.valueOf(location.getLatitude()));
        new UserSession(Dashboard.this).setLongitude(String.valueOf(location.getLongitude()));
        setLocationChangedData(currentLocation, arrivalDuration, arrivalDistance, isRouteChanged);
//        setLocationChangedData(location, arrivalDuration, arrivalDistance, false);
    }

    private void setLocationChangedData(Location location, double duration, double distance, boolean isOffRoute) {
        Log.v("Location", "Location" + location.toString());
        String str_type = new UserSession(Dashboard.this).getDriverVehicle();
        boolean is_start_ride = new UserSession(this).getRideStatus();
        if (is_start_ride) {
            if (mGoogleMap != null) {

                boolean is_location_reached = new UserSession(Dashboard.this).getLocationReached();
                if (!is_location_reached) {
                    long current_time = new Date().getTime();
                    String str_current_time = String.valueOf(current_time);
                    JourneyDetails journeyDetails = new JourneyDetails();
                    journeyDetails.setLatitude(location.getLatitude());
                    journeyDetails.setLongitude(location.getLongitude());
                    journeyDetails.setDistance(distance);
                    journeyDetails.setDuration(duration);
                    journeyDetails.setDate_time(str_current_time);
                    journeyDetails.setOffRoute(isOffRoute);
                    enableDisablePointer(false);
                    mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                    rlNavigate.setVisibility(View.VISIBLE);
                    if (isOffRoute) {
                        isPolyLineDrawed = false;
                    }
//                    locationManager.removeUpdates(this);
                    if (dict_ride_info != null && !isPolyLineDrawed) {
                        isPolyLineDrawed = true;
                        String rideId = dict_ride_info.optString("rideId");
                        /*new Handler().postDelayed(() ->*/
                        refDatabase.child(new UserSession(Dashboard.this).getUserId()).child(rideId).setValue(journeyDetails).addOnSuccessListener(aVoid -> {
                            if (dict_ride_info != null) {
                                JSONObject dict_pickUpLocation = dict_ride_info.optJSONObject("pickUpLocation");
                                if (dict_pickUpLocation != null) {
                                       /* String str_pickup_lat = dict_pickUpLocation.optString("lat");
                                        String str_pickup_lng = dict_pickUpLocation.optString("lng");
                                        double pickup_lat = Double.parseDouble(str_pickup_lat);
                                        double pickup_lng = Double.parseDouble(str_pickup_lng);
                                        LatLng pickup_lat_lng = new LatLng(pickup_lat, pickup_lng);*/

                                    JSONObject dict_drop_location = dict_ride_info.optJSONObject("dropLocation");
                                    assert dict_drop_location != null;
                                    runOnUiThread(() -> {
                                        try {
                                            locLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                                            isPolyLineDrawed = true;
                                            updateUI(locLatLng);
                                            new Handler().postDelayed(() -> {
                                                if (isRouteChanged) {
                                                    isRouteChanged = false;
                                                }
                                            }, 3000);

                                            if (mPolyline != null) {
                                                mPolyline.remove();
                                                mPolyline = null;
                                            }

                                            if (polyLineMarker != null) {
                                                polyLineMarker.remove();
                                                polyLineMarker = null;
                                            }
                                            if (polyLineMarkerDest != null) {
                                                polyLineMarkerDest.remove();
                                                polyLineMarkerDest = null;
                                            }

                                                /*if (!isPolyLineDrawed)
                                                {
                                                    isPolyLineDrawed = true;
                                                    String url1 = getDirectionsUrl(locLatLng, pickup_lat_lng);
                                                    DownloadTask downloadTask1 = new DownloadTask();
                                                    downloadTask1.execute(url1);
                                                }else
                                                {
                                                    updateLocation();
                                                }*/
                                        } catch (Exception e) {
                                            Log.v("LocationException", "LocationException" + e.getLocalizedMessage());
                                        }
                                    });

                                    boolean is_navigation_started = new UserSession(Dashboard.this).getNavigationStarted();
                                    if (!is_navigation_started) {
                                        tvUserInfo.setVisibility(View.GONE);
                                        rlNavigate.setVisibility(View.VISIBLE);
                                    } else {
                                        tvRide.setText(getResources().getString(R.string.completed_ride));

                                    }
                                }
                            }
                        }).addOnFailureListener(e -> Log.v("Fail", "Fail"));/*, 5000)*/
                    } else {
                        isPolyLineDrawed = false;
                    }
                } else {
                    if (dict_ride_info != null && !is_route_populated) {
                        is_route_populated = true;
                        if (!is_dest_show) {
                            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                            JSONObject dict_pickUpLocation = dict_ride_info.optJSONObject("pickUpLocation");
                            assert dict_pickUpLocation != null;
                            String str_pickup_lat = dict_pickUpLocation.optString("lat");
                            String str_pickup_lng = dict_pickUpLocation.optString("lng");
                            double pickup_lat = Double.parseDouble(str_pickup_lat);
                            double pickup_lng = Double.parseDouble(str_pickup_lng);
                            LatLng pickup_lat_lng = new LatLng(pickup_lat, pickup_lng);

                            JSONObject dict_drop_location = dict_ride_info.optJSONObject("dropLocation");
                            assert dict_drop_location != null;
                            String str_drop_location_lat = dict_drop_location.optString("lat");
                            String str_drop_location_lng = dict_drop_location.optString("lng");
                            double drop_location_lat = Double.parseDouble(str_drop_location_lat);
                            double drop_location_lng = Double.parseDouble(str_drop_location_lng);
                            LatLng dest_lat_lng = new LatLng(drop_location_lat, drop_location_lng);
                            mGoogleMap.clear();
                            enableDisablePointer(false);
                            Drawable dr;
                            Bitmap bitmap = null;
                            switch (str_type) {
                                case "bike":
                                    dr = ContextCompat.getDrawable(this, R.drawable.ic_bike);
                                    if (dr != null) {
                                        bitmap = ((BitmapDrawable) dr).getBitmap();
                                        bitmap = Bitmap.createScaledBitmap(bitmap, 80, 120, true);
                                    }
                                    break;
                                case "auto":
                                    dr = ContextCompat.getDrawable(this, R.drawable.ic_auto);
                                    if (dr != null) {
                                        bitmap = ((BitmapDrawable) dr).getBitmap();
                                        bitmap = Bitmap.createScaledBitmap(bitmap, 60, 90, true);
                                    }
                                    break;
                                default:
                                    dr = ContextCompat.getDrawable(this, R.drawable.car_uber);
                                    if (dr != null) {
                                        bitmap = ((BitmapDrawable) dr).getBitmap();
                                        bitmap = Bitmap.createScaledBitmap(bitmap, 60, 90, true);
                                    }
                                    break;
                            }

                            MarkerOptions pickup_marker = new MarkerOptions().position(pickup_lat_lng)
                                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                    .flat(true)
                                    .alpha(1)
                                    .anchor(0.5f, 0.5f)
                                    .rotation((float) bearingBetweenLocations(pickup_lat_lng, dest_lat_lng));

                            @SuppressLint("InflateParams") View dest_view = ((LayoutInflater) Objects.requireNonNull(getSystemService(Context.LAYOUT_INFLATER_SERVICE))).inflate(R.layout.info_window_layout, null);
                            String str_destination_address = dict_ride_info.optString("dropPoint");
                            LinearLayout llDTime = dest_view.findViewById(R.id.llTime);
                            llDTime.setVisibility(View.GONE);
                            CustomTextView tvDMarkerLocation = dest_view.findViewById(R.id.tvMarkerLocation);
                            String[] arrDestination = str_destination_address.split(",");
                            String destination = "";
                            if (arrDestination.length > 2) {
                                destination = arrDestination[0] + ", " + arrDestination[1];
                            } else if (arrDestination.length > 0) {
                                destination = arrDestination[0];
                            }
                            tvDMarkerLocation.setText(destination);
                            MarkerOptions dropMarker = new MarkerOptions().position(dest_lat_lng)
                                    .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(dest_view)));


                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(pickup_lat_lng);
                            builder.include(dest_lat_lng);
                            LatLngBounds bounds = builder.build();

                            int width = getResources().getDisplayMetrics().widthPixels;
                            int padding = (int) (width * 0.10);

                            if (fragment != null) {
                                int height;
                                View v = fragment.getView();
                                if (v != null) {
                                    height = v.getHeight();
                                    CameraUpdate cu;
                                    try {
                                        cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                                    } catch (IllegalStateException e) {
                                        padding = (int) (width * 0.55);
                                        cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                                    }
                                    mGoogleMap.moveCamera(cu);
                                    mGoogleMap.animateCamera(cu);
                                    mGoogleMap.setOnMarkerClickListener(marker -> true);
                                }


                                marker = mGoogleMap.addMarker(pickup_marker);
                                mGoogleMap.addMarker(dropMarker);

                                is_dest_show = true;
                                boolean is_navigation_started = new UserSession(Dashboard.this).getNavigationStarted();
                                if (!is_navigation_started) {
                                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                    tvUserInfo.setVisibility(View.GONE);
                                    rlNavigate.setVisibility(View.VISIBLE);
                                } else {
                                    tvRide.setText(getResources().getString(R.string.completed_ride));
                                }
                                updateLocation();
                                if (arrPolyLinePoints.isEmpty()) {
                                    String url1 = getDirectionsUrl(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), dest_lat_lng);
                                    DownloadTask downloadTask1 = new DownloadTask();
                                    downloadTask1.execute(url1);
                                }
                            }
                        }
                    }
                    boolean is_navigation_started = new UserSession(Dashboard.this).getNavigationStarted();
                    if (is_navigation_started) {
                        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                        enableDisablePointer(false);
                        /*JSONObject dict_drop_location = dict_ride_info.optJSONObject("dropLocation");

                        String str_drop_location_lat = dict_drop_location.optString("lat");
                        String str_drop_location_lng = dict_drop_location.optString("lng");
                        double drop_location_lat = Double.parseDouble(str_drop_location_lat);
                        double drop_location_lng = Double.parseDouble(str_drop_location_lng);
                        LatLng dest_lat_lng = new LatLng(drop_location_lat, drop_location_lng);*/

                        long current_time = new Date().getTime();
                        String str_current_time = String.valueOf(current_time);
                        JourneyDetails journeyDetails = new JourneyDetails();
                        journeyDetails.setLatitude(location.getLatitude());
                        journeyDetails.setLongitude(location.getLongitude());
                        journeyDetails.setDate_time(str_current_time);
                        journeyDetails.setLatitude(location.getLatitude());
                        journeyDetails.setLongitude(location.getLongitude());
                        journeyDetails.setDistance(distance);
                        journeyDetails.setDuration(duration);
                        journeyDetails.setOffRoute(isOffRoute);
                        if (isOffRoute) {
                            isPolyLineDrawed = false;
                        }
                        if (!isPolyLineDrawed) {
                            isPolyLineDrawed = true;
                            String rideId = dict_ride_info.optString("rideId");
                            /*new Handler().postDelayed(() ->*/
                            refDatabase.child(new UserSession(Dashboard.this).getUserId()).child(rideId).setValue(journeyDetails).addOnSuccessListener(aVoid -> runOnUiThread(() -> {
                                try {
                                    locLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                    updateUI(locLatLng);
                                    new Handler().postDelayed(() -> {
                                        if (isRouteChanged) {
                                            isRouteChanged = false;
                                        }
                                    }, 8000);
                                    if (mPolyline != null) {
                                        mPolyline.remove();
                                        mPolyline = null;
                                    }

                                    if (polyLineMarker != null) {
                                        polyLineMarker.remove();
                                        polyLineMarker = null;
                                    }
                                    if (polyLineMarkerDest != null) {
                                        polyLineMarkerDest.remove();
                                        polyLineMarkerDest = null;
                                    }

                                        /*if (!isPolyLineDrawed)
                                        {
                                            isPolyLineDrawed = true;
                                            String url1 = getDirectionsUrl(locLatLng, dest_lat_lng);
                                            DownloadTask downloadTask1 = new DownloadTask();
                                            downloadTask1.execute(url1);

                                        }else
                                        {
                                            updateLocation();
                                        }*/
                                } catch (Exception e) {
                                    Log.v("LocationException", "LocationException" + e.getLocalizedMessage());
                                }
                            })).addOnFailureListener(e -> Log.v("Fail", "Fail"));/*, 8000)*/
                        }
//                        locationManager.removeUpdates(this);
                    }
                }
            }
        } else if (!new UserSession(Dashboard.this).getTripSuccessData().equals("")) {
            if (dict_ride_info != null) {
                if (dict_ride_info.length() > 0) {
                    String rideId = dict_ride_info.optString("rideId");
                    refDatabase.child(new UserSession(Dashboard.this).getUserId()).child(rideId).setValue(null, null);
                } else {
                    refDatabase.child(new UserSession(Dashboard.this).getUserId()).setValue(null, null);
                }
            } else {
                refDatabase.child(new UserSession(Dashboard.this).getUserId()).setValue(null, null);
            }

        }
//        }

        if (!is_map_loaded) {
            fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            if (fragment != null) {
                mapView = fragment.getView();
                fragment.getMapAsync(Dashboard.this);
            }
        }
    }

    private void updateUI(LatLng newLoc) {
        animateCar(newLoc, marker.getPosition(), marker);
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                newLoc, 18));
    }

    private void animateCar(final LatLng destination, LatLng startPosition, Marker m) {
        final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.Spherical();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(3000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(animation -> {
            try {
                float v = animation.getAnimatedFraction();
                LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, destination);
                m.setPosition(newPosition);
                m.setAnchor(0.5f, 0.5f);
                m.setRotation((float) bearingBetweenLocations(startPosition, destination));
                m.setVisible(true);
            } catch (Exception ex) {
                Log.e("Exce ex", "Exce ex" + ex);
                isPolyLineDrawed = false;
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                updateLocation();
                isPolyLineDrawed = false;
            }
        });
        valueAnimator.start();
    }

    /*private interface LatLngInterpolator {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements Dashboard.LatLngInterpolator {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }
    }*/

    public interface LatLngInterpolator {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class Spherical implements LatLngInterpolator {
            @Override
            public LatLng interpolate(float fraction, LatLng from, LatLng to) {
                double fromLat = toRadians(from.latitude);
                double fromLng = toRadians(from.longitude);
                double toLat = toRadians(to.latitude);
                double toLng = toRadians(to.longitude);
                double cosFromLat = cos(fromLat);
                double cosToLat = cos(toLat);
                // Computes Spherical interpolation coefficients.
                double angle = computeAngleBetween(fromLat, fromLng, toLat, toLng);
                double sinAngle = sin(angle);
                if (sinAngle < 1E-6) {
                    return from;
                }
                double a = sin((1 - fraction) * angle) / sinAngle;
                double b = sin(fraction * angle) / sinAngle;
                // Converts from polar to vector and interpolate.
                double x = a * cosFromLat * cos(fromLng) + b * cosToLat * cos(toLng);
                double y = a * cosFromLat * sin(fromLng) + b * cosToLat * sin(toLng);
                double z = a * sin(fromLat) + b * sin(toLat);
                // Converts interpolated vector back to polar.
                double lat = atan2(z, sqrt(x * x + y * y));
                double lng = atan2(y, x);
                return new LatLng(toDegrees(lat), toDegrees(lng));
            }

            private double computeAngleBetween(double fromLat, double fromLng, double toLat, double toLng) {
                // Haversine's formula
                double dLat = fromLat - toLat;
                double dLng = fromLng - toLng;
                return 2 * asin(sqrt(pow(sin(dLat / 2), 2) +
                        cos(fromLat) * cos(toLat) * pow(sin(dLng / 2), 2)));
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rlGo) {
            fetchLocation();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (new UserSession(Dashboard.this).getScreen().equals("")) {
                        boolean isRideAccept = new UserSession(Dashboard.this).getRideAccept();
                        boolean isRideStarted = new UserSession(Dashboard.this).getRideStatus();
                        boolean isRidePickup = new UserSession(Dashboard.this).getUserPickUp();
                        if (!isRideAccept && !isRideStarted && !isRidePickup) {


                            if (!is_available) {
                                long expiry_date = new UserSession(Dashboard.this).getDriverExpiryDate();
                                long current_date = new Date().getTime();
                                if (current_date > expiry_date && expiry_date > 0) {
                                    Utilities.multilineShowMessage(const_main, getResources().getString(R.string.expire_message));
                                } else {
                                    checkCustomerStatus(new UserSession(Dashboard.this).getUserId());
                                }
                            } else {
                                is_updating = false;
                                if (release_handler != null) {
                                    release_handler.removeCallbacks(runnable);
                                }
                                String str_vehicle_type = new UserSession(Dashboard.this).getVehicleType();
                                bottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                showLoading();
                                updateDriverAvailability(str_vehicle_type);
                                playSound();
                            }
                        }
                    }
                }
            }, 300);
        } else if (id == R.id.ivUser) {
            if (Utilities.isInternetAvailable(Dashboard.this)) {
                showLoading();
                updateDriverRating();
            } else {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
                Utilities.showMessage(const_main, getResources().getString(R.string.internet_error));
            }
        } else if (id == R.id.iv_user) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
            new Handler().postDelayed(() -> {
                headerLayout.findViewById(R.id.iv_user).setOnClickListener(null);
                startActivity(new Intent(Dashboard.this, ShowDriverDocument.class).putExtra("position", 3).putExtra("tagFrom", "dashboard"));
                overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
                finish();
            }, 300);
        } else if (id == R.id.rlCancelBack) {
            coordinate_cancel.setVisibility(View.GONE);
            cardViewDetails.setVisibility(View.VISIBLE);
            updateLocation();
        } else if (id == R.id.llSelect) {
            if (is_available) {
                if (bottomSheetBehavior1.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                    ivArrow.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_down_white));
                } else if (bottomSheetBehavior1.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    ivArrow.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_up_white));
                }
            }
        } else if (id == R.id.rlCitySelect) {
            if (is_available) {
                ivOutStation.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_radio_uncheck));
                ivInCity.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_radio_check));

                boolean is_incity = new UserSession(Dashboard.this).getInCityStatus();
                if (!is_incity) {
                    if (is_updating) {
                        is_updating = false;
                        if (release_handler != null) {
                            release_handler.removeCallbacks(runnable);
                        }
                    }
                    double latitude = currentLocation.getLatitude();
                    double longitude = currentLocation.getLongitude();
                    String str_vehicle_type = new UserSession(this).getVehicleType();
                    String notificationId = new UserSession(this).getToken();
                    String str_driverId = new UserSession(this).getUserId();
                    double driverId = Double.parseDouble(str_driverId);
                    String subLocality = "";
                    JSONObject dict_params = new JSONObject();
                    try {
                        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        if (addresses.size() > 0) {
                            subLocality = addresses.get(0).getSubLocality();
                            if (subLocality == null || subLocality.equals("")) {
                                subLocality = addresses.get(0).getLocality();
                            }
                        }
                        dict_params.put("driverId", driverId);
                        dict_params.put("latitude", latitude);
                        dict_params.put("longitude", longitude);
                        dict_params.put("city", subLocality);
                        dict_params.put("vehicleType", str_vehicle_type);
                        dict_params.put("notificationId", notificationId);
                        dict_params.put("availableForDelivery", new UserSession(Dashboard.this).getAvailableDelivery());
                        boolean is_in_city_status = new UserSession(Dashboard.this).getInCityStatus();
                        boolean is_out_station = new UserSession(Dashboard.this).getOutStationStatus();
                        String str_ride_type = "";
                        if (is_in_city_status) {
                            str_ride_type = "inCity";
                        }
                        if (is_out_station) {
                            str_ride_type = "outstation";
                        }
                        dict_params.put("rideType", str_ride_type);

                    } catch (IOException | JSONException e) {
                        Log.v("parseError", "ParseError" + e.getLocalizedMessage());
                    }
                    if (Utilities.isInternetAvailable(Dashboard.this)) {
                        showLoading();
                        updateDriverLocationWithRideType(dict_params, "inCity");
                    } else {
                        Utilities.showMessage(const_main, getResources().getString(R.string.internet_error));
                    }
                }
            }
        } else if (id == R.id.rlOutStationSelect) {
            if (is_available) {
                boolean in_station = new UserSession(Dashboard.this).getOutStationStatus();
                String str_type = new UserSession(Dashboard.this).getDriverVehicle();
                if (!in_station) {
                    if (!str_type.equals("bike") && !str_type.equals("auto")) {
                        if (is_updating) {
                            is_updating = false;
                            if (release_handler != null) {
                                release_handler.removeCallbacks(runnable);
                            }
                        }
                        ivOutStation.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_radio_check));
                        ivInCity.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_radio_uncheck));

                        double latitude = currentLocation.getLatitude();
                        double longitude = currentLocation.getLongitude();
                        String str_vehicle_type = new UserSession(this).getVehicleType();
                        String notificationId = new UserSession(this).getToken();
                        String str_driverId = new UserSession(this).getUserId();
                        double driverId = Double.parseDouble(str_driverId);
                        String subLocality = "";
                        JSONObject dict_params = new JSONObject();
                        try {
                            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                            if (addresses.size() > 0) {
                                subLocality = addresses.get(0).getSubLocality();
                                if (subLocality == null || subLocality.equals("")) {
                                    subLocality = addresses.get(0).getLocality();
                                }
                            }
                            dict_params.put("driverId", driverId);
                            dict_params.put("latitude", latitude);
                            dict_params.put("longitude", longitude);
                            dict_params.put("city", subLocality);
                            dict_params.put("vehicleType", str_vehicle_type);
                            dict_params.put("notificationId", notificationId);
                            dict_params.put("availableForDelivery", new UserSession(Dashboard.this).getAvailableDelivery());
                            boolean is_in_city_status = new UserSession(Dashboard.this).getInCityStatus();
                            boolean is_out_station = new UserSession(Dashboard.this).getOutStationStatus();
                            String str_ride_type = "";
                            if (is_in_city_status) {
                                str_ride_type = "inCity";
                            }
                            if (is_out_station) {
                                str_ride_type = "outstation";
                            }
                            dict_params.put("rideType", str_ride_type);

                        } catch (IOException | JSONException e) {
                            Log.v("parseError", "ParseError" + e.getLocalizedMessage());
                        }
                        if (Utilities.isInternetAvailable(Dashboard.this)) {
                            showLoading();
                            updateDriverLocationWithRideType(dict_params, "outstation");
                        } else {
                            Utilities.showMessage(const_main, getResources().getString(R.string.internet_error));
                        }
                    } else {
                        String message = getResources().getString(R.string.not_available) + " " + str_type;
                        /*Toast toast = Toast.makeText(Dashboard.this, message, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();*/
                        Utilities.showMessage(const_main, message);
                    }
                }
            }
        } else if (id == R.id.rlNotificationCircle) {
            Intent intent = new Intent(Dashboard.this, Inbox.class);
            startActivity(intent);
            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
        } else if (id == R.id.rlEmergency) {
            ArrayAdapter emergency_adaptor = new EmergencyAdaptor(Dashboard.this, R.layout.single_list_item, arr_emergency, arrEmergencyIcon);
            showCustomDialog(emergency_adaptor);
        } else if (id == R.id.rlChat) {
            String firstName = dict_user_data.optString("firstName");
            String lastName = dict_user_data.optString("lastName");
            String s1 = firstName.substring(0, 1).toUpperCase();
            firstName = s1 + firstName.substring(1);
            String s2 = lastName.substring(0, 1).toUpperCase();
            lastName = s2 + lastName.substring(1);
            String fullName = firstName + " " + lastName;
            String avatar = dict_user_data.optString("avatar");
            String mobileNumber = dict_user_data.optString("id");
            String strRideId = dict_ride_info.optString("rideId");
            String userNotificationId = dict_user_data.optString("notificationId");
            Intent intent = new Intent(Dashboard.this, Chat.class);
            intent.putExtra("fullName", fullName);
            intent.putExtra("avatar", avatar);
            intent.putExtra("mobileNumber", mobileNumber);
            intent.putExtra("rideId", strRideId);
            intent.putExtra("user_notification_id", userNotificationId);
            startActivity(intent);
            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
            finish();
        } else if (id == R.id.llSignOut) {
            if (new UserSession(Dashboard.this).getScreen().equals("")) {
                boolean isRideAccept = new UserSession(Dashboard.this).getRideAccept();
                boolean isRideStarted = new UserSession(Dashboard.this).getRideStatus();
                boolean isRidePickup = new UserSession(Dashboard.this).getUserPickUp();
                if (!isRideAccept && !isRideStarted && !isRidePickup) {
                    userSignout();
                    closeDrawer();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.logout_error), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, getResources().getString(R.string.logout_error), Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.iv_up) {
            if (BottomSheetBehavior.STATE_EXPANDED == bottomSheetBehavior.getState()) {
                is_hide_state = true;
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            } else if (BottomSheetBehavior.STATE_COLLAPSED == bottomSheetBehavior.getState()) {
                is_hide_state = false;
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        } else if (id == R.id.rlCompleteBack) {
            coordinate_complete.setVisibility(View.GONE);
            cardViewDetails.setVisibility(View.VISIBLE);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else if (id == R.id.btnSubmitRide) {
            updateLocation();
            Utilities.hideSoftKeyboard(Dashboard.this);
            if (Objects.requireNonNull(etReason.getText()).toString().equals("")) {
                sendCompleteRideData(completeReason);
            } else {
                sendCompleteRideData(etReason.getText().toString());
            }
        } else if (id == R.id.btnCancelRide) {
            String str_reason = arr_items.get(cancelPosition);
            String rideId = dict_ride_info.optString("rideId");
            String user_notification_id = dict_user_data.optString("notificationId");
            String cancledBy = "driver";
            JSONObject dict_params = new JSONObject();
            if (locationManager != null) {
                locationManager.removeUpdates(Dashboard.this);
                locationManager = null;
            }
            try {
                dict_params.put("rideId", rideId);
                dict_params.put("notificationId", user_notification_id);
                dict_params.put("cancledBy", cancledBy);
                dict_params.put("cancleReason", str_reason);
                if (Utilities.isInternetAvailable(Dashboard.this)) {
                    showLoading();
                    cancelRide(dict_params);
                } else {
                    coordinate_cancel.setVisibility(View.GONE);
                    cardViewDetails.setVisibility(View.VISIBLE);
                    Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                }
            } catch (JSONException e) {
                Log.v("JSONEXCEPTION E", "JSONEXCEPTION E" + e.getLocalizedMessage());
            }
        }
    }

    private void sendCompleteRideData(String reason) {
        coordinate_complete.setVisibility(View.GONE);
        cardViewDetails.setVisibility(View.VISIBLE);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        showLoading();
        tvReason.setVisibility(View.GONE);
        String user_notification_id = dict_user_data.optString("notificationId");
        String rideId = dict_ride_info.optString("rideId");
        JSONObject cost_obj = dict_ride_info.optJSONObject("costObject");

        JSONObject dict_params = new JSONObject();
        try {
            List<Address> currentAddress = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
            if (!currentAddress.isEmpty()) {

                String strCurrentAddress = currentAddress.get(0).getAddressLine(0);

                dict_params.put("rideId", rideId);
                dict_params.put("distanceInKm", finalDis);
                dict_params.put("timeInMin", finalTime);
                dict_params.put("partialRide", true);
                dict_params.put("driverPartialRideReason", reason);
                dict_params.put("costObj", cost_obj);
                dict_params.put("userNotificationId", user_notification_id);
                dict_params.put("partialRideCompletedAt", strCurrentAddress);
            }
        } catch (JSONException e) {
            Log.v("execption json", "exception json" + e.getLocalizedMessage());
        } catch (IOException e) {
            Log.v("execption IOException", "exception IOException" + e.getLocalizedMessage());
        }

        if (dict_ride_info.optBoolean("delivar")) {
            if (deliveryPic.equals("")) {
                new UserSession(Dashboard.this).setCompleteParams(dict_params.toString());
                startActivityForResult(new Intent(Dashboard.this, ShowDriverDocument.class).putExtra("position", 7).putExtra("tagFrom", "ridePic").putExtra("value", "complete"), IMAGE_CODE);
            } else {
                try {
                    dict_params.put("delivaryImage", deliveryPic);
                } catch (JSONException e) {
                    Log.v("parseError", "ParseError" + e.getLocalizedMessage());
                }
                if (Utilities.isInternetAvailable(Dashboard.this)) {
                    if (locationManager != null) {
                        locationManager.removeUpdates(this);
                    }
                    showLoading();
                    completeRide(dict_params);
                } else {
                    hideLoading();
                    Utilities.showMessage(const_main, getResources().getString(R.string.internet_error));
                }
            }
        } else {
            if (Utilities.isInternetAvailable(Dashboard.this)) {
                if (locationManager != null) {
                    locationManager.removeUpdates(this);
                }
                showLoading();
                completeRide(dict_params);
            } else {
                hideLoading();
                Utilities.showMessage(const_main, getResources().getString(R.string.internet_error));
            }
        }
    }

    private void updateDriverAvailability(String str_vehicle_type) {
        if (currentLocation != null) {
            double latitude = currentLocation.getLatitude();
            double longitude = currentLocation.getLongitude();

            String notificationId = new UserSession(this).getToken();
            String str_driverId = new UserSession(this).getUserId();
            if (!str_driverId.equals("")) {
                double driverId = Double.parseDouble(str_driverId);
                String subLocality = "";
                JSONObject dict_params = new JSONObject();
                try {
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addresses.size() > 0) {
                        subLocality = addresses.get(0).getSubLocality();
                        if (subLocality == null || subLocality.equals("")) {
                            subLocality = addresses.get(0).getLocality();
                        }
                    }
                    dict_params.put("driverId", driverId);
                    dict_params.put("latitude", latitude);
                    dict_params.put("longitude", longitude);
                    dict_params.put("city", subLocality);
                    dict_params.put("vehicleType", str_vehicle_type);
                    dict_params.put("notificationId", notificationId);
                    dict_params.put("availableForDelivery", new UserSession(Dashboard.this).getAvailableDelivery());
                    boolean is_in_city_status = new UserSession(Dashboard.this).getInCityStatus();
                    boolean is_out_station = new UserSession(Dashboard.this).getOutStationStatus();
                    String str_ride_type = "";
                    if (is_in_city_status) {
                        str_ride_type = "inCity";
                    }
                    if (is_out_station) {
                        str_ride_type = "outstation";
                    }
                    dict_params.put("rideType", str_ride_type);

                } catch (IOException | JSONException e) {
                    Log.v("updateException", "updateException");
                }
                if (Utilities.isInternetAvailable(Dashboard.this)) {
//            showLoading();
                    updateDriverLocation(dict_params);
                } else {
                    Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                }
            }
        }
    }

    private void updateDriverAvailabilityEveryMinute(String str_vehicle_type) {
        if (currentLocation != null) {
            double latitude = currentLocation.getLatitude();
            double longitude = currentLocation.getLongitude();

            String notificationId = new UserSession(this).getToken();
            String str_driverId = new UserSession(this).getUserId();
            if (!str_driverId.equals("")) {
                double driverId = Double.parseDouble(str_driverId);
                String subLocality = "";
                JSONObject dict_params = new JSONObject();
                try {
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    if (addresses.size() > 0) {
                        subLocality = addresses.get(0).getSubLocality();
                        if (subLocality == null || subLocality.equals("")) {
                            subLocality = addresses.get(0).getLocality();
                        }
                    }
                    dict_params.put("driverId", driverId);
                    dict_params.put("latitude", latitude);
                    dict_params.put("longitude", longitude);
                    dict_params.put("city", subLocality);
                    dict_params.put("vehicleType", str_vehicle_type);
                    dict_params.put("notificationId", notificationId);
                    dict_params.put("availableForDelivery", new UserSession(Dashboard.this).getAvailableDelivery());
                    boolean is_in_city_status = new UserSession(Dashboard.this).getInCityStatus();
                    boolean is_out_station = new UserSession(Dashboard.this).getOutStationStatus();
                    String str_ride_type = "";
                    if (is_in_city_status) {
                        str_ride_type = "inCity";
                    }
                    if (is_out_station) {
                        str_ride_type = "outstation";
                    }
                    dict_params.put("rideType", str_ride_type);

                } catch (IOException | JSONException e) {
                    Log.v("updateException", "updateException");
                }

                if (updatingStarted && new UserSession(Dashboard.this).getDriverAvailability()) {
                    if (Utilities.isInternetAvailable(Dashboard.this)) {
//                showLoading();
                        updateDriverLocation(dict_params);
                    } else {
                        Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                    }
                } else {
                    is_updating = false;
                    updatingStarted = false;
                    release_handler.removeCallbacks(runnable);
                    release_handler = new Handler();
                }
            }
        }
    }

    private void sendPreferedRideLocations(JSONObject dict_params, String tag) {
        String append_url = Constants.PAYMENTS_URL + Constants.UPDATE_DRIVER;
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, append_url, dict_params,
                response -> {
                    if (response != null) {
                        String status = response.optString("type");
                        if (status.equals("success")) {
                            if (tag.equals("inCity")) {
                                new UserSession(Dashboard.this).setIncityStatus(true);
                                new UserSession(Dashboard.this).setOutstationStatus(false);
                            } else {
                                new UserSession(Dashboard.this).setOutstationStatus(true);
                                new UserSession(Dashboard.this).setIncityStatus(false);
                            }
                            String str_user_data = new UserSession(Dashboard.this).getUserData();
                            try {
                                JSONObject dict_user_data = new JSONObject(str_user_data);
                                dict_user_data.put("rideType", tag);
                                new UserSession(Dashboard.this).setUserData(dict_user_data.toString());
                            } catch (JSONException e) {
                                Log.v("parseError", "ParseError" + e.getLocalizedMessage());
                            }

                        } else {
                            String message = response.optString("message");
                            Utilities.showMessage(const_main, message);
                            boolean is_in_city_status = new UserSession(Dashboard.this).getInCityStatus();
                            boolean is_outstation_status = new UserSession(Dashboard.this).getOutStationStatus();

                            if (is_in_city_status) {
                                ivInCity.setImageDrawable(ContextCompat.getDrawable(Dashboard.this, R.drawable.ic_radio_check));
                            } else {
                                ivInCity.setImageDrawable(ContextCompat.getDrawable(Dashboard.this, R.drawable.ic_radio_uncheck));
                            }

                            if (is_outstation_status) {
                                ivOutStation.setImageDrawable(ContextCompat.getDrawable(Dashboard.this, R.drawable.ic_radio_check));
                            } else {
                                ivOutStation.setImageDrawable(ContextCompat.getDrawable(Dashboard.this, R.drawable.ic_radio_uncheck));
                            }
                        }

                        double latitude = currentLocation.getLatitude();
                        double longitude = currentLocation.getLongitude();
                        String str_vehicle_type = new UserSession(Dashboard.this).getVehicleType();
                        String notificationId = new UserSession(Dashboard.this).getToken();
                        String str_driverId = new UserSession(Dashboard.this).getUserId();
                        double driverId = Double.parseDouble(str_driverId);
                        String subLocality = "";
                        JSONObject dict_params1 = new JSONObject();
                        try {
                            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                            if (addresses.size() > 0) {
                                subLocality = addresses.get(0).getSubLocality();
                                if (subLocality == null || subLocality.equals("")) {
                                    subLocality = addresses.get(0).getLocality();
                                }
                            }
                            dict_params1.put("driverId", driverId);
                            dict_params1.put("latitude", latitude);
                            dict_params1.put("longitude", longitude);
                            dict_params1.put("city", subLocality);
                            dict_params1.put("vehicleType", str_vehicle_type);
                            dict_params1.put("notificationId", notificationId);
                            dict_params1.put("availableForDelivery", new UserSession(Dashboard.this).getAvailableDelivery());
                            boolean is_in_city_status = new UserSession(Dashboard.this).getInCityStatus();
                            boolean is_out_station = new UserSession(Dashboard.this).getOutStationStatus();
                            String str_ride_type = "";
                            if (is_in_city_status) {
                                str_ride_type = "inCity";
                            }
                            if (is_out_station) {
                                str_ride_type = "outstation";
                            }
                            dict_params1.put("rideType", str_ride_type);

                        } catch (IOException | JSONException e) {
                            Log.v("parseError", "ParseError" + e.getLocalizedMessage());
                        }
                        updateDriverLocationType(dict_params1);

                    } else {
                        hideLoading();
                        Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                        boolean is_in_city_status = new UserSession(Dashboard.this).getInCityStatus();
                        boolean is_outstation_status = new UserSession(Dashboard.this).getOutStationStatus();

                        if (is_in_city_status) {
                            ivInCity.setImageDrawable(ContextCompat.getDrawable(Dashboard.this, R.drawable.ic_radio_check));
                        } else {
                            ivInCity.setImageDrawable(ContextCompat.getDrawable(Dashboard.this, R.drawable.ic_radio_uncheck));
                        }

                        if (is_outstation_status) {
                            ivOutStation.setImageDrawable(ContextCompat.getDrawable(Dashboard.this, R.drawable.ic_radio_check));
                        } else {
                            ivOutStation.setImageDrawable(ContextCompat.getDrawable(Dashboard.this, R.drawable.ic_radio_uncheck));
                        }
                        updatingUserLocation();
                    }
                },
                error -> {
                    hideLoading();
                    NetworkResponse res = error.networkResponse;
                    if (res != null) {
                        if (res.statusCode == 403) {
                            showLoading();
                            Utilities.showMessage(const_main, getResources().getString(R.string.session_expired));
                            JambaCabApplication.getInstance().removeDriver(const_main);
                        } else {
                            boolean is_in_city_status = new UserSession(Dashboard.this).getInCityStatus();
                            boolean is_outstation_status = new UserSession(Dashboard.this).getOutStationStatus();

                            if (is_in_city_status) {
                                ivInCity.setImageDrawable(ContextCompat.getDrawable(Dashboard.this, R.drawable.ic_radio_check));
                            } else {
                                ivInCity.setImageDrawable(ContextCompat.getDrawable(Dashboard.this, R.drawable.ic_radio_uncheck));
                            }

                            if (is_outstation_status) {
                                ivOutStation.setImageDrawable(ContextCompat.getDrawable(Dashboard.this, R.drawable.ic_radio_check));
                            } else {
                                ivOutStation.setImageDrawable(ContextCompat.getDrawable(Dashboard.this, R.drawable.ic_radio_uncheck));
                            }
                            updatingUserLocation();
                            hideLoading();
                            Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                        }
                    } else {
                        boolean is_in_city_status = new UserSession(Dashboard.this).getInCityStatus();
                        boolean is_outstation_status = new UserSession(Dashboard.this).getOutStationStatus();

                        if (is_in_city_status) {
                            ivInCity.setImageDrawable(ContextCompat.getDrawable(Dashboard.this, R.drawable.ic_radio_check));
                        } else {
                            ivInCity.setImageDrawable(ContextCompat.getDrawable(Dashboard.this, R.drawable.ic_radio_uncheck));
                        }

                        if (is_outstation_status) {
                            ivOutStation.setImageDrawable(ContextCompat.getDrawable(Dashboard.this, R.drawable.ic_radio_check));
                        } else {
                            ivOutStation.setImageDrawable(ContextCompat.getDrawable(Dashboard.this, R.drawable.ic_radio_uncheck));
                        }
                        updatingUserLocation();
                        hideLoading();
                        Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("x-custom-token", new UserSession(Dashboard.this).getAccessToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.your_trips) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(Dashboard.this, Trips.class);
                startActivity(intent);
                overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
                finish();
            }, 300);

        } else if (id == R.id.earnings) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(Dashboard.this, Earnings.class);
                startActivity(intent);
                overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
                finish();
            }, 300);
        } else if (id == R.id.documents) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(Dashboard.this, Documents.class);
                intent.putExtra("fromSplash", false);
                startActivity(intent);
                overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
                finish();
            }, 300);
        } else if (id == R.id.vehicles) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(Dashboard.this, Vehicles.class);
                startActivity(intent);
                overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
                finish();
            }, 300);
        } else if (id == R.id.payment) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(Dashboard.this, Payment.class);
                startActivity(intent);
                overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
                finish();
            }, 300);
        } else {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
            if (dynamicPagesList.size() > 0) {
                for (int i = 0; i < dynamicPagesList.size(); i++) {
                    if (id == i) {
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                        int finalI = i;
                        new Handler().postDelayed(() -> {
                            Intent intentDynamicPage = new Intent(Dashboard.this, DynamicPagesActivity.class);
                            intentDynamicPage.putExtra("pageData", dynamicPagesList.get(finalI).getBody());
                            intentDynamicPage.putExtra("title", dynamicPagesList.get(finalI).getId());
                            startActivity(intentDynamicPage);
                            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
                            finish();
                        }, 300);
                    }
                }
            }
        }
        return true;
    }

    public void userSignout() {
        if (currentLocation != null) {
            double latitude = currentLocation.getLatitude();
            double longitude = currentLocation.getLongitude();

            String notificationId = new UserSession(this).getToken();
            String str_driverId = new UserSession(this).getUserId();
            double driverId = Double.parseDouble(str_driverId);
            String subLocality = "";
            JSONObject dict_params = new JSONObject();
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses.size() > 0) {
                    subLocality = addresses.get(0).getSubLocality();
                    if (subLocality == null || subLocality.equals("")) {
                        subLocality = addresses.get(0).getLocality();
                    }
                }
                dict_params.put("driverId", driverId);
                dict_params.put("latitude", latitude);
                dict_params.put("longitude", longitude);
                dict_params.put("city", subLocality);
                dict_params.put("vehicleType", new UserSession(this).getVehicleType());
                dict_params.put("notificationId", notificationId);
                dict_params.put("availableForDelivery", new UserSession(Dashboard.this).getAvailableDelivery());
                boolean is_in_city_status = new UserSession(Dashboard.this).getInCityStatus();
                boolean is_out_station = new UserSession(Dashboard.this).getOutStationStatus();
                String str_ride_type = "";
                if (is_in_city_status) {
                    str_ride_type = "inCity";
                }
                if (is_out_station) {
                    str_ride_type = "outstation";
                }
                dict_params.put("rideType", str_ride_type);

            } catch (IOException | JSONException e) {
                Log.v("parseError", "ParseError" + e.getLocalizedMessage());
            }
            is_updating = false;
            if (release_handler != null) {
                release_handler.removeCallbacks(runnable);
            }
            is_user_logout = true;
            if (Utilities.isInternetAvailable(Dashboard.this)) {
//                showLoading();
                removeDriver(dict_params);
            } else {
                Utilities.showMessage(const_main, getResources().getString(R.string.internet_error));
            }
        } else {
            Utilities.showMessage(const_main, getResources().getString(R.string.location_not_availabe));
        }
    }

    public void closeDrawer() {
        if (drawer != null)
            drawer.closeDrawer(GravityCompat.START);
    }

    private void removeDriverExpired(JSONObject dict_params) {
        String append_url = Constants.DRIVER_URL + Constants.REMOVE_DRIVER_LOCATION;
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, append_url, dict_params,
                response -> {
                    if (response != null) {
                        String status = response.optString("type");
                        if (status.equals("success")) {

                            JSONObject dict_data = new JSONObject();
                            String mobile = new UserSession(Dashboard.this).getUserId();
                            double user_id = Double.parseDouble(mobile);
                            try {
                                dict_data.put("id", user_id);
                                dict_data.put("rideType", dict_params.optString("rideType"));
                                JSONObject dict_available = new JSONObject();
                                dict_available.put("available", false);
                                dict_data.put("obj", dict_available);
                            } catch (JSONException e) {
                                Log.v("parseError", "ParseError" + e.getLocalizedMessage());
                            }
                            if (Utilities.isInternetAvailable(Dashboard.this)) {
                                updateDriverExpired(dict_data);
                            } else {
                                hideLoading();
                                Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                            }
                        } else {
                            hideLoading();
                            String message = response.optString("message");
                            Utilities.showMessage(const_main, message);
                        }
                    } else {
                        hideLoading();
                        Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                    }
                },
                error -> {
                    hideLoading();
                    NetworkResponse res = error.networkResponse;
                    if (res != null) {
                        if (res.statusCode == 403) {
                            showLoading();
                            Utilities.showMessage(const_main, getResources().getString(R.string.session_expired));
                            JambaCabApplication.getInstance().removeDriver(const_main);
                        } else {
                            Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                        }
                    } else {
                        Utilities.showMessage(const_main, getResources().getString(R.string.internet_error));
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("x-custom-token", new UserSession(Dashboard.this).getAccessToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void removeDriver(JSONObject dict_params) {
        String append_url = Constants.DRIVER_URL + Constants.REMOVE_DRIVER_LOCATION;
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, append_url, dict_params,
                response -> {
                    if (response != null) {
                        String status = response.optString("type");
                        if (status.equals("success")) {
                            boolean is_driver_available = new UserSession(Dashboard.this).getDriverAvailability();
                            if (is_driver_available) {
                                JSONObject dict_data = new JSONObject();
                                String mobile = new UserSession(Dashboard.this).getUserId();
                                double user_id = Double.parseDouble(mobile);
                                try {
                                    dict_data.put("id", user_id);
                                    JSONObject dict_available = new JSONObject();
                                    dict_available.put("available", false);
                                    dict_data.put("obj", dict_available);
                                } catch (JSONException e) {
                                    Log.v("parseError", "ParseError" + e.getLocalizedMessage());
                                }
                                if (Utilities.isInternetAvailable(Dashboard.this)) {
                                    updateDriver(dict_data);
                                } else {
                                    hideLoading();
                                    Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                                }
                            } else {
                                JSONObject dict_data = new JSONObject();
                                try {
                                    dict_data.put("driverId", new UserSession(Dashboard.this).getUserId());
                                } catch (JSONException e) {
                                    Log.v("parseError", "ParseError" + e.getLocalizedMessage());
                                }
                                if (Utilities.isInternetAvailable(Dashboard.this)) {
                                    logout(dict_data);
                                } else {
                                    hideLoading();
                                    Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                                }
                            }
                        } else {
                            hideLoading();
                            String message = response.optString("message");
                            Utilities.showMessage(const_main, message);
                        }
                    } else {
                        hideLoading();
                        Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                    }
                },
                error -> {
                    hideLoading();
                    NetworkResponse res = error.networkResponse;
                    if (res != null) {
                        if (res.statusCode == 403) {
                            showLoading();
                            Utilities.showMessage(const_main, getResources().getString(R.string.session_expired));
                            JambaCabApplication.getInstance().removeDriver(const_main);
                        } else {
                            Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                        }
                    } else {
                        Utilities.showMessage(const_main, getResources().getString(R.string.internet_error));
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("x-custom-token", new UserSession(Dashboard.this).getAccessToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void logout(JSONObject dict_params) {
        String append_url = Constants.PAYMENTS_URL + Constants.DRIVER_LOGOUT;
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, append_url, dict_params,
                response -> {
                    if (response != null) {
                        String status = response.optString("type");
                        if (status.equals("success")) {
                            is_user_logout = false;
                            new UserSession(Dashboard.this).clearUserSession();
                            generateToken();
                            Intent intent = new Intent(Dashboard.this, Splash.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            String message = response.optString("message");
                            Utilities.showMessage(const_main, message);
                        }
                    } else {
                        Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                    }
                    hideLoading();
                },
                error -> {
                    hideLoading();
                    Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("x-custom-token", new UserSession(Dashboard.this).getAccessToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void generateToken() {
        String token = new UserSession(this).getToken();
        if (token.equals("")) {
            FirebaseApp.initializeApp(Dashboard.this);
            Task<InstanceIdResult> data = FirebaseInstanceId.getInstance().getInstanceId();
            data.addOnCompleteListener(task -> {
                try {
                    String token1 = Objects.requireNonNull(task.getResult()).getToken();
                    if (token1.isEmpty()) {
                        token1 = "";
                    }
                    new UserSession(Dashboard.this).setToken(token1);
                } catch (Exception e) {
                    Log.v("exception1111", "exception1111" + e);
                }
            });
        }
    }

    private void updateDriverLocation(JSONObject dict_params) {
        String append_url;
        if (!is_updating && !updatingStarted) {
            if (!is_available) {
                append_url = Constants.DRIVER_URL + Constants.UPDATE_DRIVER_LOCATION;

            } else {
                append_url = Constants.DRIVER_URL + Constants.REMOVE_DRIVER_LOCATION;
            }
        } else {
            append_url = Constants.DRIVER_URL + Constants.UPDATE_DRIVER_LOCATION;
        }

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, append_url, dict_params,
                response -> {
                    if (response != null) {
                        String status = response.optString("type");
                        if (status.equals("success")) {
                            JSONObject dict_data = new JSONObject();
                            String mobile = new UserSession(Dashboard.this).getUserId();
                            long user_id = Long.parseLong(mobile);
                            try {
                                dict_data.put("id", user_id);
                                JSONObject dict_available = new JSONObject();
                                if (!is_updating) {
                                    boolean is_available_driver = false;
                                    if (!is_available) {
                                        is_available_driver = true;
                                    }
                                    dict_available.put("available", is_available_driver);
                                } else {
                                    dict_available.put("available", true);
                                }
                                dict_data.put("obj", dict_available);
                            } catch (JSONException e) {
                                hideLoading();
                                Log.v("parseError", "ParseError" + e.getLocalizedMessage());
                            }
                            if (Utilities.isInternetAvailable(Dashboard.this)) {
                                updateDriver(dict_data);
                            } else {
                                hideLoading();
                                Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                            }
                        } else {
                            hideLoading();
                            String message = response.optString("message");
                            Utilities.showMessage(const_main, message);
                        }
                    } else {
                        hideLoading();
                        Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                    }
                },
                error -> {
                    hideLoading();
                    NetworkResponse res = error.networkResponse;
                    if (res != null) {
                        if (res.statusCode == 403) {
                            showLoading();
                            Utilities.showMessage(const_main, getResources().getString(R.string.session_expired));
                            JambaCabApplication.getInstance().removeDriver(const_main);
                        } else {
                            if (Utilities.isInternetAvailable(Dashboard.this)) {
                                updatingUserLocation();
                            }
                            Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                        }
                    } else {
                        Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("x-custom-token", new UserSession(Dashboard.this).getAccessToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }


    private void updateDriverLocationWithRideType(JSONObject dict_params, String tag) {
        String append_url;
        if (!is_available) {
            append_url = Constants.DRIVER_URL + Constants.UPDATE_DRIVER_LOCATION;

        } else {
            append_url = Constants.DRIVER_URL + Constants.REMOVE_DRIVER_LOCATION;
        }

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, append_url, dict_params,
                response -> {
                    if (response != null) {
                        String status = response.optString("type");
                        if (status.equals("success")) {
                            if (is_available) {
                                is_available = false;
                            }
                            String str_mobile_umber = new UserSession(Dashboard.this).getUserId();
                            long mobile_number = Long.parseLong(str_mobile_umber);
                            JSONObject dict_obj = new JSONObject();
                            JSONObject dict_params1 = new JSONObject();
                            try {
                                dict_obj.put("rideType", tag);
                                dict_params1.put("id", mobile_number);
                                dict_params1.put("obj", dict_obj);
                            } catch (JSONException e) {
                                Log.v("parseError", "ParseError" + e.getLocalizedMessage());
                            }
                            if (Utilities.isInternetAvailable(Dashboard.this)) {
                                sendPreferedRideLocations(dict_params1, tag);
                            } else {
                                boolean is_in_city_status = new UserSession(Dashboard.this).getInCityStatus();
                                boolean is_outstation_status = new UserSession(Dashboard.this).getOutStationStatus();

                                if (is_in_city_status) {
                                    ivInCity.setImageDrawable(ContextCompat.getDrawable(Dashboard.this, R.drawable.ic_radio_check));
                                } else {
                                    ivInCity.setImageDrawable(ContextCompat.getDrawable(Dashboard.this, R.drawable.ic_radio_uncheck));
                                }

                                if (is_outstation_status) {
                                    ivOutStation.setImageDrawable(ContextCompat.getDrawable(Dashboard.this, R.drawable.ic_radio_check));
                                } else {
                                    ivOutStation.setImageDrawable(ContextCompat.getDrawable(Dashboard.this, R.drawable.ic_radio_uncheck));
                                }
                                hideLoading();
                                Utilities.showMessage(const_main, getResources().getString(R.string.internet_error));
                            }
                        } else {
                            updatingUserLocation();
                            hideLoading();
                            String message = response.optString("message");
                            Utilities.showMessage(const_main, message);
                        }
                    } else {
                        updatingUserLocation();
                        hideLoading();
                        Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                    }
                },
                error -> {
                    hideLoading();
                    NetworkResponse res = error.networkResponse;
                    if (res != null) {
                        if (res.statusCode == 403) {
                            showLoading();
                            Utilities.showMessage(const_main, getResources().getString(R.string.session_expired));
                            JambaCabApplication.getInstance().removeDriver(const_main);
                        } else {
                            if (Utilities.isInternetAvailable(Dashboard.this)) {
                                updatingUserLocation();
                            }
                            Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                        }
                    } else {
                        Utilities.showMessage(const_main, getResources().getString(R.string.internet_error));
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("x-custom-token", new UserSession(Dashboard.this).getAccessToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }


    private void updateDriverLocationType(JSONObject dict_params) {
        String append_url;
        if (!is_available) {
            append_url = Constants.DRIVER_URL + Constants.UPDATE_DRIVER_LOCATION;

        } else {
            append_url = Constants.DRIVER_URL + Constants.REMOVE_DRIVER_LOCATION;
        }

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, append_url, dict_params,
                response -> {
                    if (response != null) {
                        String status = response.optString("type");
                        if (status.equals("success")) {
                            if (!is_available) {
                                is_available = true;
                            }
                        } else {
                            String message = response.optString("message");
                            Utilities.showMessage(const_main, message);
                        }
                    } else {
                        Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                    }
                    if (Utilities.isInternetAvailable(Dashboard.this)) {
                        updatingUserLocation();
                    } else {
                        Utilities.showMessage(const_main, getResources().getString(R.string.internet_error));
                    }
                    hideLoading();
                },
                error -> {
                    hideLoading();
                    NetworkResponse res = error.networkResponse;
                    if (res != null) {
                        if (res.statusCode == 403) {
                            showLoading();
                            Utilities.showMessage(const_main, getResources().getString(R.string.session_expired));
                            JambaCabApplication.getInstance().removeDriver(const_main);
                        } else {
                            if (Utilities.isInternetAvailable(Dashboard.this)) {
                                updatingUserLocation();
                            }
                            Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                        }
                    } else {
                        Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("x-custom-token", new UserSession(Dashboard.this).getAccessToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void updateDriverExpired(JSONObject dict_params) {
        String append_url = Constants.PAYMENTS_URL + Constants.UPDATE_DRIVER;
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, append_url, dict_params,
                response -> {
                    if (response != null) {
                        String status = response.optString("type");
                        if (status.equals("success")) {
                            is_available = false;
                            ivOutStation.setImageDrawable(ContextCompat.getDrawable(Dashboard.this, R.drawable.ic_radio_uncheck));
                            ivInCity.setImageDrawable(ContextCompat.getDrawable(Dashboard.this, R.drawable.ic_radio_check));
                            new UserSession(Dashboard.this).setIncityStatus(true);
                            new UserSession(Dashboard.this).setOutstationStatus(false);
                            new UserSession(Dashboard.this).setDriverAvailability(false);
                            String str_user_data = new UserSession(Dashboard.this).getUserData();
                            try {
                                JSONObject dict_user_data = new JSONObject(str_user_data);
                                dict_user_data.put("rideType", dict_params.optString("rideType"));
                                new UserSession(Dashboard.this).setUserData(dict_user_data.toString());
                            } catch (JSONException e) {
                                Log.v("parseError", "ParseError" + e.getLocalizedMessage());
                            }
                            rlGo.setBackground(ContextCompat.getDrawable(Dashboard.this, R.drawable.circle_corner));
                            tvStatus.setText(getResources().getString(R.string.offline_status));
                            const_status_card.setBackgroundColor(ContextCompat.getColor(Dashboard.this, R.color.goclr_red));
                            tvStatus.setTextColor(ContextCompat.getColor(Dashboard.this, R.color.white));
                            checkBottomArrowDirection();
                            tvBullet.setImageDrawable(ContextCompat.getDrawable(Dashboard.this, R.drawable.ic_bullet_white));
                            if (mOverlay != null) {
                                mOverlay.remove();
                                mProvider = null;
                            }
                            long expiry_date = new UserSession(Dashboard.this).getDriverExpiryDate();
                            long current_date = new Date().getTime();
                            String str_vehicle_type = new UserSession(Dashboard.this).getVehicleType();
                            if ((current_date < expiry_date) && (!str_vehicle_type.equals(""))) {
                                if (customerData != null)
                                {
                                    String strStatus = customerData.getStatus();
                                    if (strStatus != null)
                                    {
                                        if (strStatus.equals("accept"))
                                            updateDriverAvailability(str_vehicle_type);
                                    }else
                                    {
                                        updateDriverAvailability(str_vehicle_type);
                                    }
                                }else
                                {
                                    updateDriverAvailability(str_vehicle_type);
                                }
                            }
                        } else {
                            String message = response.optString("message");
                            Utilities.showMessage(const_main, message);
                        }
                    } else {
                        Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                    }
                    hideLoading();
                },
                error -> {
                    hideLoading();
                    NetworkResponse res = error.networkResponse;
                    if (res != null) {
                        if (res.statusCode == 403) {
                            showLoading();
                            Utilities.showMessage(const_main, getResources().getString(R.string.session_expired));
                            JambaCabApplication.getInstance().removeDriver(const_main);
                        } else {
                            Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                        }
                    } else {
                        Utilities.showMessage(const_main, getResources().getString(R.string.internet_error));
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("x-custom-token", new UserSession(Dashboard.this).getAccessToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }


    private void updateDriver(JSONObject dict_params) {
        String append_url = Constants.PAYMENTS_URL + Constants.UPDATE_DRIVER;

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, append_url, dict_params,
                response -> {
                    if (response != null) {
                        String status = response.optString("type");
                        if (status.equals("success")) {
                            if (!is_updating) {

                                if (is_user_logout) {
                                    if (is_available) {
                                        if (mOverlay != null) {
                                            mOverlay.remove();
                                            mProvider = null;
                                        }
                                        is_available = false;
                                        if (mOverlay != null) {
                                            mOverlay.remove();
                                            mProvider = null;
                                        }
                                        new UserSession(Dashboard.this).setDriverAvailability(false);
                                        rlGo.setBackground(ContextCompat.getDrawable(Dashboard.this, R.drawable.circle_corner));
                                        tvStatus.setText(getResources().getString(R.string.offline_status));
                                        const_status_card.setBackgroundColor(ContextCompat.getColor(Dashboard.this, R.color.goclr_red));
                                        tvStatus.setTextColor(ContextCompat.getColor(Dashboard.this, R.color.white));
                                        checkBottomArrowDirection();
                                        tvBullet.setImageDrawable(ContextCompat.getDrawable(Dashboard.this, R.drawable.ic_bullet_white));
                                    }
                                    if (release_handler != null) {
                                        release_handler.removeCallbacks(runnable);
                                    }
                                    JSONObject dict_data = new JSONObject();
                                    try {
                                        dict_data.put("driverId", new UserSession(Dashboard.this).getUserId());
                                    } catch (JSONException e) {
                                        Log.v("parseError", "ParseError" + e.getLocalizedMessage());
                                    }
                                    if (Utilities.isInternetAvailable(Dashboard.this)) {
                                        logout(dict_data);
                                    } else {
                                        hideLoading();
                                        Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                                    }

                                } else {
                                    if (is_available) {
                                        if (mOverlay != null) {
                                            mOverlay.remove();
                                            mProvider = null;
                                        }
                                        is_available = false;
                                        new UserSession(Dashboard.this).setDriverAvailability(false);
                                        rlGo.setBackground(ContextCompat.getDrawable(Dashboard.this, R.drawable.circle_corner));
                                        tvStatus.setText(getResources().getString(R.string.offline_status));
                                        const_status_card.setBackgroundColor(ContextCompat.getColor(Dashboard.this, R.color.goclr_red));
                                        tvStatus.setTextColor(ContextCompat.getColor(Dashboard.this, R.color.white));
                                        checkBottomArrowDirection();
                                        tvBullet.setImageDrawable(ContextCompat.getDrawable(Dashboard.this, R.drawable.ic_bullet_white));
                                        if (release_handler != null) {
                                            release_handler.removeCallbacks(runnable);
                                        }
                                    } else {
                                        is_available = true;
                                        new UserSession(Dashboard.this).setDriverAvailability(true);
                                        rlGo.setBackground(ContextCompat.getDrawable(Dashboard.this, R.drawable.corner_circle_black));
                                        tvStatus.setText(getResources().getString(R.string.online_status));
                                        const_status_card.setBackgroundColor(ContextCompat.getColor(Dashboard.this, R.color.goclr_green));
                                        tvStatus.setTextColor(ContextCompat.getColor(Dashboard.this, R.color.white));
                                        checkBottomArrowDirection();
                                        tvBullet.setImageDrawable(ContextCompat.getDrawable(Dashboard.this, R.drawable.ic_bullet_white));
                                        updatingUserLocation();
                                        if (new UserSession(Dashboard.this).getDriverAvailability()) {
                                            getHeatMapData();
                                        }
                                    }
                                }
                            } else {
                                is_updating = false;
                                updatingStarted = false;
                                is_available = true;
                                new UserSession(Dashboard.this).setDriverAvailability(true);
                                rlGo.setBackground(ContextCompat.getDrawable(Dashboard.this, R.drawable.corner_circle_black));
                                tvStatus.setText(getResources().getString(R.string.online_status));
                                const_status_card.setBackgroundColor(ContextCompat.getColor(Dashboard.this, R.color.goclr_green));
                                tvStatus.setTextColor(ContextCompat.getColor(Dashboard.this, R.color.white));
                                checkBottomArrowDirection();
                                tvBullet.setImageDrawable(ContextCompat.getDrawable(Dashboard.this, R.drawable.ic_bullet_white));
//                                    updatingUserLocation();
                                if (new UserSession(Dashboard.this).getDriverAvailability()) {
                                    getHeatMapData();
                                }
                            }
                        } else {
                            String message = response.optString("message");
                            Utilities.showMessage(const_main, message);
                        }
                    } else {
                        Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                    }
                    hideLoading();
                },
                error -> {
                    hideLoading();
                    NetworkResponse res = error.networkResponse;
                    if (res != null) {
                        if (res.statusCode == 403) {
                            showLoading();
                            Utilities.showMessage(const_main, getResources().getString(R.string.session_expired));
                            JambaCabApplication.getInstance().removeDriver(const_main);
                        } else {
                            Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                        }
                    } else {
                        Utilities.showMessage(const_main, getResources().getString(R.string.internet_error));
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("x-custom-token", new UserSession(Dashboard.this).getAccessToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onAnimationStart(Animation animation) {
        if (animation == blink) {
            rlBlink.startAnimation(blink);
        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation == blink) {
            rlBlink.startAnimation(blink);
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    public void onClickInfo(View view) {
        boolean is_navigation_started = new UserSession(Dashboard.this).getNavigationStarted();
        boolean is_ride_started = new UserSession(Dashboard.this).getRideStatus();
        if (is_ride_started) {
            if (!is_navigation_started) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                tvUserInfo.setVisibility(View.GONE);
            } else {
                if (rlNavigate.getVisibility() == View.GONE) {
                    if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    } else {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        tvUserInfo.setVisibility(View.GONE);
                    }
                } else {
                    if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                        tvUserInfo.setVisibility(View.GONE);
                    }
                }
            }
        } else {
            if (rlNavigate.getVisibility() == View.GONE) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    tvUserInfo.setVisibility(View.GONE);
                }
            } else {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    tvUserInfo.setVisibility(View.GONE);
                }
            }
        }
    }

    public void onClickCall(View view) {
        //make call
        String call_number = "tel:" + str_mobile_number;
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(call_number));
        startActivity(intent);
    }

    public void onClickUser(View view) {

    }

    public void onClickDecline(View view) {

        if (tvDecline.getText().toString().equals(getResources().getString(R.string.decline_ride))) {
            new UserSession(Dashboard.this).removeNotificaionReceiedTime();
            new UserSession(Dashboard.this).removeTagFrom();
            new UserSession(Dashboard.this).removeScreen();
            new UserSession(Dashboard.this).removePushparams();
            new UserSession(Dashboard.this).removeFloating();
            new UserSession(Dashboard.this).removeChatDetails();
            new UserSession(Dashboard.this).setUserDeclinedClicked(true);
            isAcceptClicked = false;
            pickupPic = "";
            deliveryPic = "";
            rlGo.setVisibility(View.VISIBLE);
            bottom_sheet.setVisibility(View.VISIBLE);
            cardViewDetails.setVisibility(View.GONE);
            user_bottom_sheet.setVisibility(View.GONE);
            rlNotification.setVisibility(View.VISIBLE);
            mGoogleMap.clear();
            enableDisablePointer(true);

            is_updating = false;
            if (release_handler != null) {
                release_handler.removeCallbacks(runnable);
            }
            if (rideHandler != null) {
                if (rideRunnable != null)
                    rideHandler.removeCallbacks(rideRunnable);
                rideHandler = new Handler();
            }
            String str_vehicle_type = new UserSession(Dashboard.this).getVehicleType();
            if (!str_vehicle_type.equals("")) {
                is_available = false;
                updateDriverAvailability(str_vehicle_type);
            }
            if (Utilities.isInternetAvailable(Dashboard.this)) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updatingUserLocation();
                    }
                }, 60000);
            } else {
                Utilities.showMessage(const_main, getResources().getString(R.string.internet_fail));
            }
        } else {
            if (Utilities.isInternetAvailable(Dashboard.this)) {
                showLoading();
                getCancelReasons("cancel");
            } else {
                Utilities.showMessage(const_main, getResources().getString(R.string.internet_fail));
            }
        }
    }

    private void getCancelReasons(String tag) {
        String url = Constants.URL + Constants.CANCEL_REASONS;

        JsonObjectRequest stringRequest = new JsonObjectRequest(com.android.volley.Request.Method.GET, url, null,
                response -> {
                    if (response != null) {
                        arr_items = new ArrayList<>();

                        String type = response.optString("type");
                        if (type.equals("success")) {
                            JSONArray arr_data = response.optJSONArray("data");
                            if (arr_data != null) {
                                for (int i = 0; i < arr_data.length(); i++) {

                                    String reason_type = arr_data.optJSONObject(i).optString("_id");
                                    if (tag.equals("cancel")) {
                                        if (reason_type.equals("driverCancelReasons")) {
                                            JSONArray arr_reasons = arr_data.optJSONObject(i).optJSONArray("reasons");
                                            if (arr_reasons != null) {
                                                for (int j = 0; j < arr_reasons.length(); j++) {
                                                    JSONObject dict_reasons = arr_reasons.optJSONObject(j);
                                                    String title = dict_reasons.optString("title");
                                                    boolean active = dict_reasons.optBoolean("active");
                                                    if (active) {
                                                        arr_items.add(title);
                                                    }
                                                }
                                            }
                                        }
                                        mGoogleMap.clear();
                                        coordinate_cancel.setVisibility(View.VISIBLE);
                                        cardViewDetails.setVisibility(View.GONE);
                                        ArrayAdapter cancel_adaptor = new CancelAdaptor(Dashboard.this, R.layout.cancel_list_item, arr_items, 0);
                                        lvIssues.setAdapter(cancel_adaptor);
                                        cancel_adaptor.notifyDataSetChanged();
                                    } else {
                                        if (reason_type.equals("driverPartialRideCompleteReasons")) {
                                            JSONArray arr_reasons = arr_data.optJSONObject(i).optJSONArray("reasons");
                                            if (arr_reasons != null) {
                                                for (int j = 0; j < arr_reasons.length(); j++) {
                                                    JSONObject dict_reasons = arr_reasons.optJSONObject(j);
                                                    String title = dict_reasons.optString("title");
                                                    boolean active = dict_reasons.optBoolean("active");
                                                    if (active) {
                                                        arr_items.add(title);
                                                    }
                                                }
                                            }
                                        }
                                        mGoogleMap.clear();
                                        coordinate_complete.setVisibility(View.VISIBLE);
                                        cardViewDetails.setVisibility(View.GONE);

                                        if (!arr_items.isEmpty()) {
                                            CompleteReasonsAdaptor adaptor = new CompleteReasonsAdaptor(this, this);
                                            rvCompleteReasons.setLayoutManager(new LinearLayoutManager(this));
                                            adaptor.setListContent(arr_items);
                                            rvCompleteReasons.setAdapter(adaptor);
                                            adaptor.notifyDataSetChanged();
                                            rvCompleteReasons.setVisibility(View.VISIBLE);
                                            tvHeader.setVisibility(View.VISIBLE);
                                        } else {
                                            rvCompleteReasons.setVisibility(View.GONE);
                                            tvHeader.setVisibility(View.GONE);
                                        }
                                    }
                                }
                            }
                            hideLoading();
                        } else {
                            hideLoading();
                            Utilities.showMessage(const_main, response.optString("message"));
                        }
                    } else {

                        hideLoading();
                        Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                    }
                },
                error -> {
                    hideLoading();
                    NetworkResponse res = error.networkResponse;
                    if (res != null) {
                        if (res.statusCode == 403) {
                            showLoading();
                            Utilities.showMessage(const_main, getResources().getString(R.string.session_expired));
                            JambaCabApplication.getInstance().removeDriver(const_main);
                        } else {
                            hideLoading();
                            Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                        }
                    } else {
                        hideLoading();
                        Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("x-custom-token", new UserSession(Dashboard.this).getAccessToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void onClickRide(View view) {
        if (dict_ride_info != null)
        {
            String str = tvRide.getText().toString();
            if (str.equals(getResources().getString(R.string.accept_ride))) {
                new UserSession(this).removeLocationReached();
                String mobile = new UserSession(this).getUserId();

                double user_id = Double.parseDouble(mobile);
                JSONObject dict_params = new JSONObject();
                try {
                    String str_driver_info = new UserSession(this).getUserData();
                    JSONObject dict_driver_info = new JSONObject(str_driver_info);
                    dict_driver_info.put("id", user_id);
                    dict_driver_info.put("notificationId", new UserSession(this).getToken());
                    String str_selected_vehicle = new UserSession(this).getSelectedVehicleInfo();
                    JSONObject dict_selected_vehicle = new JSONObject(str_selected_vehicle);
                    dict_driver_info.put("vehicle_info", dict_selected_vehicle);
                    dict_driver_info.put("arrival_time", estimatedTime);
                    JSONObject dict_location = new JSONObject();

                    String driver_lat = new UserSession(this).getLatitude();
                    String driver_lng = new UserSession(this).getLongitude();
                    double lat = Double.parseDouble(driver_lat);
                    Double lng = Double.parseDouble(driver_lng);
                    dict_location.put("lat", lat);
                    dict_location.put("lng", lng);
                    dict_driver_info.put("driver_location", dict_location);
                    dict_params.put("driver", dict_driver_info);
                    dict_params.put("user", dict_user_data);
                    dict_params.put("ride", dict_ride_info);
                    if (Utilities.isInternetAvailable(Dashboard.this)) {
                        showLoading();
                        confirmBooking(dict_params);
                    } else {
                        Utilities.showMessage(const_main, getResources().getString(R.string.internet_error));
                    }
                } catch (JSONException e) {
                    Log.v("parseError", "ParseError" + e.getLocalizedMessage());
                }
            } else if (str.equals(getResources().getString(R.string.start_ride))) {
                if (dict_ride_info != null)
                {
                    rlEmergency.setVisibility(View.VISIBLE);
                    new UserSession(Dashboard.this).removeLocationReached();
                    String rideId = dict_ride_info.optString("rideId");
                    String user_notification_id = dict_user_data.optString("notificationId");
                    JSONObject dict_params = new JSONObject();
                    try {
                        dict_params.put("rideId", rideId);
                        dict_params.put("userNotificationId", user_notification_id);

                        String mobile = new UserSession(this).getUserId();
                        double user_id = Double.parseDouble(mobile);

                        String str_driver_info = new UserSession(this).getUserData();
                        JSONObject dict_driver_info = new JSONObject(str_driver_info);
                        dict_driver_info.put("id", user_id);
                        dict_driver_info.put("notificationId", new UserSession(this).getToken());
                        String str_selected_vehicle = new UserSession(this).getSelectedVehicleInfo();
                        JSONObject dict_selected_vehicle = new JSONObject(str_selected_vehicle);
                        dict_driver_info.put("vehicle_info", dict_selected_vehicle);
                        dict_driver_info.put("arrival_time", estimatedTime);
                        JSONObject dict_location = new JSONObject();

                        String driver_lat = new UserSession(this).getLatitude();
                        String driver_lng = new UserSession(this).getLongitude();
                        double lat = Double.parseDouble(driver_lat);
                        Double lng = Double.parseDouble(driver_lng);
                        dict_location.put("lat", lat);
                        dict_location.put("lng", lng);
                        dict_driver_info.put("driver_location", dict_location);
                        dict_params.put("driver", dict_driver_info);
//                dict_params.put("user", dict_user_data);
                        dict_params.put("ride", dict_ride_info);
                        if (Utilities.isInternetAvailable(Dashboard.this)) {
                            showLoading();
                            startRide(dict_params);
                        } else {
                            hideLoading();
                            coordinate_cancel.setVisibility(View.GONE);
                            cardViewDetails.setVisibility(View.VISIBLE);
                            Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                        }
                    } catch (JSONException e) {
                        Log.v("parseError", "ParseError" + e.getLocalizedMessage());
                    }
                }else
                {
                    hideLoading();
                    cardViewDetails.setVisibility(View.GONE);
                    user_bottom_sheet.setVisibility(View.GONE);
                }
            } else if (str.equals(getResources().getString(R.string.pickup))) {
                if (dict_ride_info != null)
                {
                    rlEmergency.setVisibility(View.VISIBLE);
                    dialog = new BottomSheetDialog(Dashboard.this);
                    dialog.setContentView(R.layout.otp_layout);

                    RelativeLayout rlOtpBack = dialog.findViewById(R.id.rlOtpBack);

                    CustomEditTextView etOtp = dialog.findViewById(R.id.etOtp);
                    CustomTextView tvConfirm = dialog.findViewById(R.id.tvConfirm);
                    Window window = dialog.getWindow();
                    assert window != null;
                    Objects.requireNonNull(window).setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    dialog.show();

                    assert rlOtpBack != null;
                    rlOtpBack.setOnClickListener(v -> dialog.dismiss());

                    if (tvConfirm != null) {
                        tvConfirm.setOnClickListener(v -> {

                            updateLocation();

                            if (etOtp != null) {
                                if (Objects.requireNonNull(etOtp.getText()).toString().equals("")) {
                                    Utilities.showMessage(const_main, getResources().getString(R.string.otp_label));
                                } else {
                                    if (etOtp.getText().toString().length() < 4) {
                                        Utilities.showMessage(const_main, getResources().getString(R.string.otp_place_holder));
                                    } else {
                                        JSONObject dict_params = new JSONObject();
                                        int otp = Integer.parseInt(etOtp.getText().toString());
                                        try {
                                            dict_params.put("rideId", dict_ride_info.optString("rideId"));
                                            dict_params.put("rideOtp", otp);
                                            boolean isDelivar = dict_ride_info.optBoolean("delivar");
                                            if (isDelivar) {
                                                if (pickupPic.equals("")) {
                                                    new UserSession(Dashboard.this).setPickupParams(dict_params.toString());
                                                    startActivityForResult(new Intent(Dashboard.this, ShowDriverDocument.class).putExtra("position", 7).putExtra("tagFrom", "ridePic").putExtra("value", "pickup"), IMAGE_CODE);
                                                } else {
                                                    dict_params.put("pickupImage", pickupPic);
                                                    if (Utilities.isInternetAvailable(Dashboard.this)) {
                                                        showLoading();
                                                        pickupCustomer(dict_params);
                                                    } else {
                                                        hideLoading();
                                                        Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                                                    }
                                                }
                                            } else {
                                                if (Utilities.isInternetAvailable(Dashboard.this)) {
                                                    showLoading();
                                                    pickupCustomer(dict_params);
                                                } else {
                                                    hideLoading();
                                                    Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                                                }
                                            }
                                        } catch (JSONException e) {
                                            Log.v("parseError", "ParseError" + e.getLocalizedMessage());
                                        }

                                    }
                                }
                            }
                        });
                    }
                }else
                {
                    hideLoading();
                    cardViewDetails.setVisibility(View.GONE);
                    user_bottom_sheet.setVisibility(View.GONE);
                }
            } else if (str.equals(getString(R.string.completed_ride))) {
                if (dict_ride_info != null)
                {
                    JSONObject dict_pickUpLocation = dict_ride_info.optJSONObject("pickUpLocation");
                    if (dict_pickUpLocation != null) {
                        String str_pickup_lat = dict_pickUpLocation.optString("lat");
                        String str_pickup_lng = dict_pickUpLocation.optString("lng");
                        double pickup_lat = Double.parseDouble(str_pickup_lat);
                        double pickup_lng = Double.parseDouble(str_pickup_lng);

                        JSONObject dict_drop_location = dict_ride_info.optJSONObject("dropLocation");
                        assert dict_drop_location != null;
                        String str_drop_location_lat = dict_drop_location.optString("lat");
                        String str_drop_location_lng = dict_drop_location.optString("lng");
                        double drop_location_lat = Double.parseDouble(str_drop_location_lat);
                        double drop_location_lng = Double.parseDouble(str_drop_location_lng);
                        if (Utilities.isInternetAvailable(Dashboard.this)) {
//                showLoading();
                            getCurrentLocationDistanceAndDuration(pickup_lat, pickup_lng, currentLocation.getLatitude(), currentLocation.getLongitude(), drop_location_lat, drop_location_lng);
                        } else {
                            hideLoading();
                            Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                        }
                    }
                }else
                {
                    hideLoading();
                    cardViewDetails.setVisibility(View.GONE);
                    user_bottom_sheet.setVisibility(View.GONE);
                }
            }
        }else
        {
            hideLoading();
            cardViewDetails.setVisibility(View.GONE);
            user_bottom_sheet.setVisibility(View.GONE);
        }
    }

    private void getFinalDistanceAndDuration(double lat1, double lng1, double lat2, double lng2, float currentDistance, float currentTime) {
        mapAPI = new UserSession(Dashboard.this).getAPI();
        if (mapAPI.equals("")) {
            mapAPI = getResources().getString(R.string.map_api_key);
        }
        String url = Constants.DISTANCE_API + lat1 + "," + lng1 + "&destinations=" + lat2 + "," + lng2 + "&key=" + mapAPI;
        JsonObjectRequest stringRequest = new JsonObjectRequest(com.android.volley.Request.Method.GET, url, null,
                response -> {
                    if (response != null) {
                        JSONArray arr_rows = response.optJSONArray("rows");
                        if (arr_rows != null) {
                            JSONObject dict_rows = arr_rows.optJSONObject(0);
                            JSONArray arr_elements = dict_rows.optJSONArray("elements");
                            assert arr_elements != null;
                            JSONObject dict_elements = arr_elements.optJSONObject(0);
                            JSONObject dict_distance = dict_elements.optJSONObject("distance");
                            JSONObject dict_duration = dict_elements.optJSONObject("duration");
                            String distance_value = "";
                            String duration_value = "";
                            if (dict_distance != null) {
                                if (dict_distance.has("value")) {
                                    distance_value = dict_distance.optString("value");
                                }
                            }
                            if (dict_duration != null) {
                                if (dict_duration.has("value")) {
                                    duration_value = dict_duration.optString("value");
                                }
                            }
                            if (!distance_value.equals("") && !duration_value.equals("")) {
                                float dis = (float) (Double.parseDouble(distance_value) / 1000);
                                float time = (int) ((Double.parseDouble(duration_value)) / 60);

                                float diff = dis - currentDistance;

                                if (diff > 0.5) {
                                    coordinate_complete.setVisibility(View.VISIBLE);
                                    cardViewDetails.setVisibility(View.GONE);
                                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                    tvCompleteArea.setText(dict_ride_info.optString("dropPoint"));
                                    getCancelReasons("complete");
                                } else {
                                    finalDis = dis;
                                    finalTime = time;
                                    String user_notification_id = dict_user_data.optString("notificationId");
                                    String rideId = dict_ride_info.optString("rideId");
                                    JSONObject cost_obj = dict_ride_info.optJSONObject("costObject");
                                    JSONObject dict_params = new JSONObject();
                                    try {
                                        dict_params.put("rideId", rideId);
                                        dict_params.put("distanceInKm", finalDis);
                                        dict_params.put("timeInMin", finalTime);
                                        dict_params.put("costObj", cost_obj);
                                        dict_params.put("partialRide", false);
                                        dict_params.put("driverPartialRideReason", "");
                                        dict_params.put("userNotificationId", user_notification_id);
                                        dict_params.put("partialRideCompletedAt", "");
                                    } catch (JSONException e) {
                                        Log.v("parseError", "ParseError" + e.getLocalizedMessage());
                                    }

                                    if (dict_ride_info.optBoolean("delivar")) {
                                        if (deliveryPic.equals(""))
                                            startActivityForResult(new Intent(Dashboard.this, ShowDriverDocument.class).putExtra("position", 7).putExtra("tagFrom", "ridePic").putExtra("value", "complete"), IMAGE_CODE);
                                        else {
                                            try {
                                                dict_params.put("delivaryImage", deliveryPic);
                                            } catch (JSONException e) {
                                                Log.v("parseError", "ParseError" + e.getLocalizedMessage());
                                            }
                                            if (Utilities.isInternetAvailable(Dashboard.this)) {
                                                if (locationManager != null) {
                                                    locationManager.removeUpdates(this);
                                                }
                                                completeRide(dict_params);
                                            } else {
                                                hideLoading();
                                                Utilities.showMessage(const_main, getResources().getString(R.string.internet_error));
                                            }
                                        }
                                    } else {
                                        if (Utilities.isInternetAvailable(Dashboard.this)) {
                                            if (locationManager != null) {
                                                locationManager.removeUpdates(this);
                                            }
                                            completeRide(dict_params);
                                        } else {
                                            hideLoading();
                                            Utilities.showMessage(const_main, getResources().getString(R.string.internet_error));
                                        }
                                    }
                                }
                            }
                        }
                    } else {

                        hideLoading();
                        Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                    }
                },
                error -> {
                    hideLoading();
                    Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                }
        );
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void getCurrentLocationDistanceAndDuration(double lat1, double lng1, double lat2, double lng2, double dest_lat, double dest_lng) {
        mapAPI = new UserSession(Dashboard.this).getAPI();
        if (mapAPI.equals("")) {
            mapAPI = getResources().getString(R.string.map_api_key);
        }
        String url = Constants.DISTANCE_API + lat1 + "," + lng1 + "&destinations=" + lat2 + "," + lng2 + "&key=" + mapAPI;

        JsonObjectRequest stringRequest = new JsonObjectRequest(com.android.volley.Request.Method.GET, url, null,
                response -> {
                    if (response != null) {
                        JSONArray arr_rows = response.optJSONArray("rows");

                        assert arr_rows != null;
                        JSONObject dict_rows = arr_rows.optJSONObject(0);
                        JSONArray arr_elements = dict_rows.optJSONArray("elements");
                        assert arr_elements != null;
                        JSONObject dict_elements = arr_elements.optJSONObject(0);
                        JSONObject dict_distance = dict_elements.optJSONObject("distance");
                        JSONObject dict_duration = dict_elements.optJSONObject("duration");

                        assert dict_distance != null;
                        String distance_value = dict_distance.optString("value");
                        assert dict_duration != null;
                        String duration_value = dict_duration.optString("value");
                        finalDis = (float) (Double.parseDouble(distance_value) / 1000);
                        finalTime = (int) ((Double.parseDouble(duration_value)) / 60);
                        getFinalDistanceAndDuration(lat1, lng1, dest_lat, dest_lng, finalDis, finalTime);
                    } else {

                        hideLoading();
                        Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                    }
                },
                error -> {
                    hideLoading();
                    Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                }
        );
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void completeRide(JSONObject dict_params) {
        String append_url = Constants.RIDES_URL + Constants.COMPLETE_RIDE;
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, append_url, dict_params,
                response -> {
                    if (response != null) {
                        String status = response.optString("type");
                        String message = response.optString("message");
                        if (status.equals("success")) {
//                            stopNavigating();
                            arrPolyLinePoints = new ArrayList<>();
                            isPolyLineDrawed = false;
                            if (locationManager != null) {
                                locationManager.removeUpdates(Dashboard.this);
                                locationManager = null;
                            }
                            JSONObject dictData = response.optJSONObject("data");
                            if (dictData != null) {
                                String rideID = dictData.optString("rideId");
                                String params = new UserSession(Dashboard.this).getPushParams();
                                String str_driver_data = new UserSession(Dashboard.this).getUserData();
                                if (!params.equals("")) {
                                    try {
                                        JSONObject dict_p_params = new JSONObject(params);
                                        JSONObject dict_driver_data = new JSONObject(str_driver_data);
                                        dict_driver_data.put("id", new UserSession(Dashboard.this).getUserId());
                                        dict_p_params.put("rideData", response.optString("data"));
                                        dict_p_params.put("driver", dict_driver_data.toString());
                                        isAcceptClicked = false;
                                        if (polyLineMarker != null) {
                                            polyLineMarker.remove();
                                        }
                                        if (polyLineMarkerDest != null) {
                                            polyLineMarkerDest.remove();
                                        }
                                        TripDetails trip_details = new TripDetails();
                                        trip_details.setStatus("ride_completed");
                                        trip_details.setPushParams(dict_p_params.toString());
                                        refRideDatabase.child(new UserSession(Dashboard.this).getUserId()).child(rideID).setValue(trip_details);
                                    } catch (JSONException e) {
                                        Log.v("parseError", "ParseError" + e.getLocalizedMessage());
                                    }
                                } else {
                                    try {
                                        JSONObject dict_p_params = new JSONObject();
                                        JSONObject dict_driver_data = new JSONObject(str_driver_data);
                                        dict_driver_data.put("id", new UserSession(Dashboard.this).getUserId());
                                        dict_p_params.put("rideData", response.optString("data"));
                                        dict_p_params.put("driver", dict_driver_data.toString());
                                        isAcceptClicked = false;
                                        if (polyLineMarker != null) {
                                            polyLineMarker.remove();
                                        }
                                        if (polyLineMarkerDest != null) {
                                            polyLineMarkerDest.remove();
                                        }
                                        TripDetails trip_details = new TripDetails();
                                        trip_details.setStatus("ride_completed");
                                        trip_details.setPushParams(dict_p_params.toString());
                                        refRideDatabase.child(new UserSession(Dashboard.this).getUserId()).child(rideID).setValue(trip_details);
                                    } catch (JSONException e) {
                                        Log.v("parseError", "ParseError" + e.getLocalizedMessage());
                                    }
                                }

                                Utilities.showMessage(const_main, message);
                                JSONObject dict_data = response.optJSONObject("data");
                                new UserSession(Dashboard.this).removeTagFrom();
                                new UserSession(Dashboard.this).removeScreen();
                                new UserSession(Dashboard.this).removeRideAccept();
                                new UserSession(Dashboard.this).removeRideStatus();
                                new UserSession(Dashboard.this).removeLocationReached();
                                new UserSession(Dashboard.this).removeRideAccept();
                                new UserSession(Dashboard.this).removeNavigationStarted();
                                new UserSession(Dashboard.this).removeUserPickup();
                                new UserSession(Dashboard.this).removeFloating();
                                new UserSession(Dashboard.this).removeChatDetails();
                                assert dict_data != null;
                                new UserSession(Dashboard.this).setTripSuccessData(dict_data.toString());
                                refDatabase.child(new UserSession(Dashboard.this).getUserId()).child(rideID).setValue(null, null);
                                enableDisablePointer(true);
                                if (Utilities.isInternetAvailable(Dashboard.this)) {
                                    is_updating = false;
                                    updatingStarted = false;
                                    getDriverDocumentDataTask();
                                } else {
                                    Intent intent = new Intent(Dashboard.this, RideComplete.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
                                    finish();
                                }
                            } else {
                                updateLocation();
                                Utilities.showMessage(const_main, message);
                            }
                        } else {
                            updateLocation();
                            Utilities.showMessage(const_main, message);
                        }
                    } else {
                        updateLocation();
                        Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                    }
                    hideLoading();
                },
                error -> {
                    hideLoading();
                    NetworkResponse res = error.networkResponse;
                    if (res != null) {
                        if (res.statusCode == 403) {
                            showLoading();
                            Utilities.showMessage(const_main, getResources().getString(R.string.session_expired));
                            JambaCabApplication.getInstance().removeDriver(const_main);
                        } else {
                            hideLoading();
                            Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                            updateLocation();
                        }
                    } else {
                        hideLoading();
                        Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                        updateLocation();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("x-custom-token", new UserSession(Dashboard.this).getAccessToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void updateLocation() {
        locationManager = (LocationManager) Dashboard.this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(Dashboard.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Dashboard.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Dashboard.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Dashboard.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_CODE);
            } else {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                criteria.setPowerRequirement(Criteria.POWER_HIGH);
                criteria.setAltitudeRequired(false);
                criteria.setSpeedRequired(true);
                criteria.setCostAllowed(true);
                criteria.setBearingRequired(true);

                criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
                criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
                locationManager.requestLocationUpdates(2000, 2, criteria, this, null);
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, Dashboard.this);
            }
        }
    }

    private void getDriverDocumentDataTask() {
        ApiInterface apiService = ApiClient.retrofitBaseUrl(Constants.PAYMENTS_URL).create(ApiInterface.class);
        Call<DriverDataBean> call = apiService.getParticularDriver(new UserSession(Dashboard.this).getAccessToken(), new UserSession(this).getUserId());
        call.enqueue(new Callback<DriverDataBean>() {

            @Override
            public void onResponse(@NonNull Call<DriverDataBean> call, @NonNull retrofit2.Response<DriverDataBean> response) {
                try {
                    if (response.code() == 403) {
                        showLoading();
                        Utilities.showMessage(const_main, getResources().getString(R.string.session_expired));
                        JambaCabApplication.getInstance().removeDriver(const_main);
                    } else {
                        if (response.body() != null) {
                            if (response.body().getType().equals(Constants.RESPONSE_SUCCESS)) {
                                hideLoading();

                                DriverDataBean.Data data = response.body().getData();
                                double balance = data.getDriverWalet();
                                String wallet = "\u20B9 " + balance;
                                tvEarningsDash.setText(wallet);
                                String str_user_data = new UserSession(Dashboard.this).getUserData();
                                dict_user_data = new JSONObject(str_user_data);

                                String str_rating = String.format(Locale.getDefault(), ".%2f", data.getAverageRating());
                                tvRating.setText(str_rating);

                                dict_user_data.put("driverWalet", balance);
                                dict_user_data.put("averageRating", data.getAverageRating());
                                dict_user_data.put("razorPayAccountStatus", data.getRazorPayAccountStatus());
                                dict_user_data.put("razorpayAccountNumber", data.getRazorpayAccountNumber());
                                new UserSession(Dashboard.this).removeUserData();
                                new UserSession(Dashboard.this).setUserData(dict_user_data.toString());
                                is_updating = false;
                                if (release_handler != null) {
                                    release_handler.removeCallbacks(runnable);
                                }

                                if (!is_available) {
                                    String str_vehicle_type = new UserSession(Dashboard.this).getVehicleType();
                                    if (!str_vehicle_type.equals("")) {
                                        updateDriverAvailability(str_vehicle_type);
                                    }
                                } else {
                                    updatingUserLocation();
                                }
                            } else {
                                hideLoading();
                                Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                            }
                        }
                    }
                } catch (NullPointerException | JSONException e) {
                    hideLoading();
                    Log.v("parseError", "ParseError" + e.getLocalizedMessage());
                }

                new Handler().postDelayed(() -> {
                    Intent intent = new Intent(Dashboard.this, FeedBackActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
                    finish();
                }, 500);
            }

            @Override
            public void onFailure(@NonNull Call<DriverDataBean> call, @NonNull Throwable t) {
                new Handler().postDelayed(() -> {
                    Intent intent = new Intent(Dashboard.this, RideComplete.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
                    finish();
                }, 500);
                hideLoading();
                Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
            }
        });
    }


    private void getDriverExpiryDataTask() {
        ApiInterface apiService = ApiClient.retrofitBaseUrl(Constants.PAYMENTS_URL).create(ApiInterface.class);

        Call<DriverDataBean> call = apiService.getParticularDriver(new UserSession(Dashboard.this).getAccessToken(), new UserSession(this).getUserId());
        call.enqueue(new Callback<DriverDataBean>() {

            @Override
            public void onResponse(@NonNull Call<DriverDataBean> call, @NonNull retrofit2.Response<DriverDataBean> response) {
                try {
                    if (response.code() == 403) {
                        showLoading();
                        Utilities.showMessage(const_main, getResources().getString(R.string.session_expired));
                        JambaCabApplication.getInstance().removeDriver(const_main);
                    } else {
                        if (response.body() != null) {
                            if (response.body().getType().equals(Constants.RESPONSE_SUCCESS)) {
                                DriverDataBean.Data data = response.body().getData();
                                new UserSession(Dashboard.this).setDriverExpiryDate(data.getDrivingLicenseExpairy());
                                long expiry_date = data.getDrivingLicenseExpairy();
                                long current_date = new Date().getTime();
                                if (current_date > expiry_date && expiry_date > 0) {
                                    hideLoading();
                                    if (is_map_loaded) {
                                        if (is_available) {
                                            if (Utilities.isInternetAvailable(Dashboard.this)) {
                                                updateDriverStatus();
                                            } else {
                                                Utilities.showMessage(const_main, getResources().getString(R.string.internet_error));
                                            }
                                        } else {
                                            if (mGoogleMap != null) {
                                                if (old_position == null) {
                                                    old_position = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                                }
                                            }
                                            if (is_map_loaded) {
                                                if (!new UserSession(Dashboard.this).getTagFrom().equals("") && !new UserSession(Dashboard.this).getScreen().equals("ride_info")) {
                                                    NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                                    assert nMgr != null;
                                                    nMgr.cancelAll();

                                                    LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                                    mGoogleMap.setTrafficEnabled(false);
                                                    mGoogleMap.setIndoorEnabled(false);
                                                    mGoogleMap.setBuildingsEnabled(false);
                                                    mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
                                                    mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);
                                                    enableDisablePointer(true);
                                                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                                    mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().
                                                            target(latLng)
                                                            .zoom(16).build()));
                                                    prepareUI(latLng);
                                                }
                                            }
                                        }
                                    } else {
                                        if (Utilities.isInternetAvailable(Dashboard.this)) {
                                            getVehicles();
                                        } else {
                                            hideLoading();
                                            Utilities.showMessage(const_main, getResources().getString(R.string.internet_error));
                                        }
                                    }
                                } else {
                                    if (Utilities.isInternetAvailable(Dashboard.this))
                                    {
                                        getCustomerProfileData(new UserSession(Dashboard.this).getUserId());
                                    } else {
                                        hideLoading();
                                        Utilities.showMessage(const_main, getResources().getString(R.string.internet_error));
                                    }
                                    if (mGoogleMap != null) {
                                        if (old_position == null) {
                                            old_position = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                        }
                                    }
                                    if (is_map_loaded) {
                                        if (!new UserSession(Dashboard.this).getTagFrom().equals("") && !new UserSession(Dashboard.this).getScreen().equals("ride_info")) {
                                            NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                            assert nMgr != null;
                                            nMgr.cancelAll();

                                            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                            mGoogleMap.setTrafficEnabled(false);
                                            mGoogleMap.setIndoorEnabled(false);
                                            mGoogleMap.setBuildingsEnabled(false);
                                            mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
                                            mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);
                                            enableDisablePointer(true);
                                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().
                                                    target(latLng)
                                                    .zoom(16).build()));
                                            prepareUI(latLng);
                                        }
                                    }
                                }
                            } else {
                                hideLoading();
                                Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                            }
                        }
                    }
                } catch (NullPointerException e) {
                    hideLoading();
                    Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                    Log.v("parseError", "ParseError" + e.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<DriverDataBean> call, @NonNull Throwable t) {
                hideLoading();
                Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
            }
        });
    }


    private void updateDriverRating() {
        ApiInterface apiService = ApiClient.retrofitBaseUrl(Constants.PAYMENTS_URL).create(ApiInterface.class);

        Call<DriverDataBean> call = apiService.getParticularDriver(new UserSession(Dashboard.this).getAccessToken(), new UserSession(this).getUserId());
        call.enqueue(new Callback<DriverDataBean>() {

            @Override
            public void onResponse(@NonNull Call<DriverDataBean> call, @NonNull retrofit2.Response<DriverDataBean> response) {
                try {

                    if (response.code() == 403) {
                        showLoading();
                        Utilities.showMessage(const_main, getResources().getString(R.string.session_expired));
                        JambaCabApplication.getInstance().removeDriver(const_main);
                    } else {
                        if (response.body() != null) {
                            if (response.body().getType().equals(Constants.RESPONSE_SUCCESS)) {
                                hideLoading();

                                DriverDataBean.Data data = response.body().getData();
                                double balance = data.getDriverWalet();
                                String wallet = "\u20B9 " + balance;
                                tvEarningsDash.setText(wallet);
                                String str_user_data = new UserSession(Dashboard.this).getUserData();
                                dict_user_data = new JSONObject(str_user_data);

                                String str_rating = String.format(Locale.getDefault(), "%.2f", data.getAverageRating());
                                tvRating.setText(str_rating);

                                dict_user_data.put("driverWalet", balance);
                                dict_user_data.put("averageRating", data.getAverageRating());
                                dict_user_data.put("razorPayAccountStatus", data.getRazorPayAccountStatus());
                                dict_user_data.put("razorpayAccountNumber", data.getRazorpayAccountNumber());
                                new UserSession(Dashboard.this).removeUserData();
                                new UserSession(Dashboard.this).setUserData(dict_user_data.toString());

                                if (drawer.isDrawerOpen(GravityCompat.START)) {
                                    drawer.closeDrawer(GravityCompat.START);
                                } else {
                                    drawer.openDrawer(GravityCompat.START);
                                }
                            } else {
                                hideLoading();
                                Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                            }
                        }
                    }
                } catch (NullPointerException | JSONException e) {
                    hideLoading();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DriverDataBean> call, @NonNull Throwable t) {
                hideLoading();
                Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
            }
        });
    }


    private void startRide(JSONObject dict_params) {
        String append_url = Constants.RIDES_URL + Constants.START_RIDE;
        JsonObjectRequest stringRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, append_url, dict_params,
                response -> {
                    if (response != null) {
                        hideLoading();
                        String status = response.optString("type");
                        String message = response.optString("message");
                        if (status.equals("success")) {
                            isPolyLineDrawed = false;
                            if (mOverlay != null) {
                                mOverlay.remove();
                                mProvider = null;
                            }
                            String rideID = dict_ride_info.optString("rideId");
                            TripDetails trip_details = new TripDetails();
                            trip_details.setStatus("ride_started");
                            String params = new UserSession(Dashboard.this).getPushParams();
                            try {
                                JSONObject dicPushParams = new JSONObject(params);
                                JSONObject dictRideData = response.optJSONObject("rideData");
                                if (dictRideData != null) {
                                    dictRideData.put("selectedVehicleType", new UserSession(this).getDriverVehicle());
                                    dicPushParams.put("rideData", dictRideData.toString());
                                    dicPushParams.put("driver", Objects.requireNonNull(response.optJSONObject("driver")).toString());
                                    trip_details.setPushParams(dicPushParams.toString());
                                    refRideDatabase.child(new UserSession(Dashboard.this).getUserId()).child(rideID).setValue(trip_details);
                                    is_hide_state = true;
                                    enableDisablePointer(false);
                                    new UserSession(Dashboard.this).removeRideAccept();
                                    new UserSession(Dashboard.this).setStartRide(true);
                                    tvRide.setText(getResources().getString(R.string.pickup));
                                    tvDecline.setText(getResources().getString(R.string.cancel_ride));
                                    tvNavigate.setText(getResources().getString(R.string.navigate_customer));
                                    boolean is_navigation_started = new UserSession(Dashboard.this).getNavigationStarted();
                                    if (is_navigation_started) {
                                        tvUserInfo.setVisibility(View.GONE);
                                        rlDecline.setVisibility(View.GONE);
                                        tvRide.setText(getResources().getString(R.string.completed_ride));
                                    } else {
                                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                        tvUserInfo.setVisibility(View.GONE);
                                        rlNavigate.setVisibility(View.VISIBLE);
                                        rlDecline.setVisibility(View.VISIBLE);
                                        tvDecline.setText(getResources().getString(R.string.cancel_ride));
                                        tvRide.setText(getResources().getString(R.string.pickup));
                                    }
                                    arrPolyLinePoints = new ArrayList<>();
                                    mPolyline.setColor(ContextCompat.getColor(Dashboard.this, R.color.polyline_primary_color));
                                    updateLocation();
                                }

                                /*JSONObject dictRideData = response.optJSONObject("rideData");
                                JSONObject dictPickupData;
                                if (dictRideData != null) {
                                    dictPickupData = dictRideData.optJSONObject("pickUpLocation");
                                    if (dictPickupData != null)
                                    {
                                        double lat = dictPickupData.optDouble("lat");
                                        double lng = dictPickupData.optDouble("lng");
                                        Point destP = Point.fromLngLat(lng, lat);
                                        Point sourceP = Point.fromLngLat(currentLocation.getLongitude(), currentLocation.getLatitude());

                                        startNavigation(sourceP, destP);
                                    }
                                }*/

                            } catch (JSONException e) {
                                Log.v("PushParams", "PushParams" + e.getLocalizedMessage());
                            }
                        } else {
                            coordinate_cancel.setVisibility(View.GONE);
                            cardViewDetails.setVisibility(View.VISIBLE);
                            Utilities.showMessage(const_main, message);
                        }
                    } else {
                        hideLoading();
                        coordinate_cancel.setVisibility(View.GONE);
                        cardViewDetails.setVisibility(View.VISIBLE);
                        Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                    }
                },
                error -> {
                    hideLoading();
                    NetworkResponse res = error.networkResponse;
                    if (res != null) {
                        if (res.statusCode == 403) {
                            showLoading();
                            Utilities.showMessage(const_main, getResources().getString(R.string.session_expired));
                            JambaCabApplication.getInstance().removeDriver(const_main);
                        } else {
                            hideLoading();
                            coordinate_cancel.setVisibility(View.GONE);
                            cardViewDetails.setVisibility(View.VISIBLE);
                            Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                        }
                    } else {
                        hideLoading();
                        coordinate_cancel.setVisibility(View.GONE);
                        cardViewDetails.setVisibility(View.VISIBLE);
                        Utilities.showMessage(const_main, getResources().getString(R.string.internet_error));
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("x-custom-token", new UserSession(Dashboard.this).getAccessToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void cancelRide(JSONObject dict_params) {
        String append_url = Constants.RIDES_URL + Constants.CANCEL_RIDE;
        JsonObjectRequest stringRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, append_url, dict_params,
                response -> {
                    if (response != null) {
                        new UserSession(Dashboard.this).removeLocationReached();
                        hideLoading();
                        String status = response.optString("type");
                        String message = response.optString("message");
                        if (status.equals("success")) {
                            arrPolyLinePoints = new ArrayList<>();
                            isPolyLineDrawed = false;
//                            stopNavigating();
                            new UserSession(Dashboard.this).removePickupParams();
                            new UserSession(Dashboard.this).removeCompleteParams();
                            String rideID = dict_ride_info.optString("rideId");
                            String strPushParams = new UserSession(Dashboard.this).getPushParams();
                            try {
                                JSONObject dictPushParams = new JSONObject(strPushParams);
                                String strDriverInfo = new UserSession(Dashboard.this).getUserData();
                                JSONObject dictDriverInfo = new JSONObject(strDriverInfo);
                                dictDriverInfo.put("driverId", new UserSession(Dashboard.this).getUserId());
                                dictPushParams.put("driver", dictDriverInfo.toString());
                                TripDetails trip_details = new TripDetails();
                                trip_details.setStatus("ride_cancelled");
                                isAcceptClicked = false;
                                trip_details.setPushParams(dictPushParams.toString());
                                refRideDatabase.child(new UserSession(Dashboard.this).getUserId()).child(rideID).setValue(trip_details);
                                coordinate_cancel.setVisibility(View.GONE);
                                rlEmergency.setVisibility(View.GONE);
                                new UserSession(Dashboard.this).removeTagFrom();
                                new UserSession(Dashboard.this).removeScreen();
                                new UserSession(Dashboard.this).removePushparams();
                                new UserSession(Dashboard.this).removeRideStatus();
                                new UserSession(Dashboard.this).removeRideAccept();
                                new UserSession(Dashboard.this).removeLocationReached();
                                new UserSession(Dashboard.this).removeRideInfo();
                                new UserSession(Dashboard.this).removeNavigationStarted();
                                new UserSession(Dashboard.this).removeUserPickup();
                                new UserSession(Dashboard.this).removeFloating();
                                new UserSession(Dashboard.this).removeChatDetails();
                                if (polyLineMarker != null) {
                                    polyLineMarker.remove();
                                }
                                if (polyLineMarkerDest != null) {
                                    polyLineMarkerDest.remove();
                                }
                                isAcceptClicked = false;
                                pickupPic = "";
                                deliveryPic = "";
                                refDatabase.child(new UserSession(Dashboard.this).getUserId()).child(rideID).setValue(null, null);
                                rlGo.setVisibility(View.VISIBLE);
                                rlNavigate.setVisibility(View.GONE);
                                bottom_sheet.setVisibility(View.VISIBLE);
                                cardViewDetails.setVisibility(View.GONE);
                                user_bottom_sheet.setVisibility(View.GONE);
                                rlNotification.setVisibility(View.VISIBLE);
                                rlEarningsDash.setVisibility(View.VISIBLE);
                                mGoogleMap.clear();
                                is_updating = false;
                            /*if (new_timer != null) {
                                new_timer.cancel();
                                new_timer = null;
                            }*/
                                if (release_handler != null) {
                                    release_handler.removeCallbacks(runnable);
                                }
                                String str_vehicle_type = new UserSession(Dashboard.this).getVehicleType();
                                if (!str_vehicle_type.equals("")) {
                                    is_updating = false;
                                    updatingStarted = false;
                                    if (!is_available) {
                                        updateDriverAvailability(str_vehicle_type);
                                    }

                                }
                                new Handler().postDelayed(this::updatingUserLocation, 60000);
                                enableDisablePointer(true);
                                LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                mGoogleMap.setTrafficEnabled(false);
                                mGoogleMap.setIndoorEnabled(false);
                                mGoogleMap.setBuildingsEnabled(false);
                                mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
                                mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);
                                enableDisablePointer(true);
                                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().
                                        target(latLng)
                                        .zoom(16).build()));
                                mGoogleMap.addMarker(new MarkerOptions()
                                        .flat(true)
                                        .alpha(1)
                                        .anchor(0.5f, 0.5f)
                                        .position(latLng)
                                        .icon(bitmapDescriptorFromVector(Dashboard.this)));
                                dict_ride_info = new JSONObject();
                                dict_user_data = new JSONObject();
                            } catch (JSONException e) {
                                Log.v("pushParamsError", "pushParamsError" + e.getLocalizedMessage());
                            }
                        } else {
                            coordinate_cancel.setVisibility(View.GONE);
                            cardViewDetails.setVisibility(View.VISIBLE);
                            Utilities.showMessage(const_main, message);
                            updateLocation();
                        }
                    } else {
                        coordinate_cancel.setVisibility(View.GONE);
                        cardViewDetails.setVisibility(View.VISIBLE);
                        Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                        updateLocation();
                    }
                },
                error -> {
                    hideLoading();
                    NetworkResponse res = error.networkResponse;
                    if (res != null) {
                        if (res.statusCode == 403) {
                            showLoading();
                            Utilities.showMessage(const_main, getResources().getString(R.string.session_expired));
                            JambaCabApplication.getInstance().removeDriver(const_main);
                        } else {
                            coordinate_cancel.setVisibility(View.GONE);
                            cardViewDetails.setVisibility(View.VISIBLE);
                            Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                            updateLocation();
                        }
                    } else {
                        coordinate_cancel.setVisibility(View.GONE);
                        cardViewDetails.setVisibility(View.VISIBLE);
                        Utilities.showMessage(const_main, getResources().getString(R.string.internet_error));
                        updateLocation();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("x-custom-token", new UserSession(Dashboard.this).getAccessToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void stopNavigating() {
        /*if (navigation == null)
        {
            MapboxNavigationOptions options = MapboxNavigationOptions.builder()
                    .build();
            navigation = new MapboxNavigation(Dashboard.this, Constants.MAPBOX_ACCESS_TOKEN, options);
//            navigation.addProgressChangeListener(Dashboard.this);
//            navigation.addOffRouteListener(Dashboard.this);
        }else
        {
            navigation.stopNavigation();
        }*/

    }

    private void confirmBooking(JSONObject dict_params) {
        String append_url = Constants.RIDES_URL + Constants.CONFIRM_BOOKING;
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, append_url, dict_params,
                response -> {
                    if (response != null) {
                        if (response.has("type")) {
                            String type = response.optString("type");
                            if (type.equals("success")) {
                                isPolyLineDrawed = false;
                                rlChat.setVisibility(View.VISIBLE);
                                new UserSession(Dashboard.this).removeNotificaionReceiedTime();
                                String rideID = dict_ride_info.optString("rideId");
                                TripDetails trip_details = new TripDetails();
                                trip_details.setStatus("ride_success");
                                JSONObject dictSuccessValues = new JSONObject();
                                JSONObject dictRide = dict_params.optJSONObject("ride");
                                JSONObject dictUser = dict_params.optJSONObject("user");
                                JSONObject dictDriver = dict_params.optJSONObject("driver");

                                if (dictRide != null && dictUser != null && dictDriver != null) {
                                    try {
                                        dictRide.put("selectedVehicleType", new UserSession(this).getDriverVehicle());
                                        dictSuccessValues.put("rideData", dictRide.toString());
                                        dictSuccessValues.put("user", dictUser.toString());
                                        dictSuccessValues.put("driver", dictDriver.toString());
                                        trip_details.setPushParams(dictSuccessValues.toString());
                                        refRideDatabase = FirebaseDatabase.getInstance().getReference("rideDetails");
                                        refRideDatabase.child(new UserSession(Dashboard.this).getUserId()).child(rideID).setValue(trip_details);
                                    } catch (JSONException e) {
                                        Log.v("rideSuccess Error", "rideSuccess Error" + e.getLocalizedMessage());
                                    }
                                }
                                is_hide_state = true;
                                isAcceptClicked = false;
                                new UserSession(Dashboard.this).setAcceptRide(true);
                                tvDecline.setText(getResources().getString(R.string.cancel_ride));
                                tvRide.setText(getResources().getString(R.string.start_ride));
                                mGoogleMap.clear();
                                if (rideHandler != null) {
                                    if (rideRunnable != null)
                                        rideHandler.removeCallbacks(rideRunnable);
                                    rideHandler = new Handler();
                                }
                                String driver_lat = new UserSession(Dashboard.this).getLatitude();
                                String driver_lng = new UserSession(Dashboard.this).getLongitude();
                                double lat = Double.parseDouble(driver_lat);
                                double lng = Double.parseDouble(driver_lng);
                                LatLng diver_lat_lng = new LatLng(lat, lng);

                                JSONObject dict_pickUpLocation = dict_ride_info.optJSONObject("pickUpLocation");
                                assert dict_pickUpLocation != null;
                                String str_user_lat = dict_pickUpLocation.optString("lat");
                                String str_user_lng = dict_pickUpLocation.optString("lng");
                                double user_lat = Double.parseDouble(str_user_lat);
                                double user_lng = Double.parseDouble(str_user_lng);
                                user_lat_lng = new LatLng(user_lat, user_lng);

                                String str_type = new UserSession(Dashboard.this).getDriverVehicle();

                                Drawable dr;
                                Bitmap bitmap = null;
                                switch (str_type) {
                                    case "bike":
                                        dr = ContextCompat.getDrawable(Dashboard.this, R.drawable.ic_bike);
                                        if (dr != null) {
                                            bitmap = ((BitmapDrawable) dr).getBitmap();
                                            bitmap = Bitmap.createScaledBitmap(bitmap, 80, 120, true);
                                        }
                                        break;
                                    case "auto":
                                        dr = ContextCompat.getDrawable(Dashboard.this, R.drawable.ic_auto);
                                        if (dr != null) {
                                            bitmap = ((BitmapDrawable) dr).getBitmap();
                                            bitmap = Bitmap.createScaledBitmap(bitmap, 60, 90, true);
                                        }
                                        break;
                                    default:
                                        dr = ContextCompat.getDrawable(Dashboard.this, R.drawable.car_uber);
                                        if (dr != null) {
                                            bitmap = ((BitmapDrawable) dr).getBitmap();
                                            bitmap = Bitmap.createScaledBitmap(bitmap, 60, 90, true);
                                        }
                                        break;
                                }

                                MarkerOptions driver_marker = new MarkerOptions().position(diver_lat_lng)
                                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                        .flat(true)
                                        .alpha(1)
                                        .anchor(0.5f, 0.5f)
                                        .rotation((float) bearingBetweenLocations(diver_lat_lng, user_lat_lng));


                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                builder.include(diver_lat_lng);
                                builder.include(user_lat_lng);
                                LatLngBounds bounds = builder.build();
                                int width = getResources().getDisplayMetrics().widthPixels;
                                int padding = (int) (width * 0.25);

                                if (fragment != null) {
                                    int height;
                                    View v = fragment.getView();
                                    if (v != null) {
                                        height = v.getHeight();
                                        CameraUpdate cu;
                                        try {
                                            cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                                        } catch (IllegalStateException e) {
                                            padding = (int) (width * 0.55);
                                            cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                                        }
                                        mGoogleMap.moveCamera(cu);
                                        mGoogleMap.animateCamera(cu);

                                        marker = mGoogleMap.addMarker(driver_marker);
                                        if (mGoogleMap != null)
                                            enableDisablePointer(false);
                                        Drawable dr1 = ContextCompat.getDrawable(Dashboard.this, R.drawable.ic_pin_marker);
                                        assert dr1 != null;
                                        MarkerOptions source_marker = new MarkerOptions().position(user_lat_lng)
                                                .icon(bitmapDescriptorFromVectorNew(dr1));
                                        mGoogleMap.addMarker(source_marker);
                                    }
                                }
                                String url = getDirectionsUrl(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), user_lat_lng);
                                DownloadTask downloadTask = new DownloadTask();
                                downloadTask.execute(url);
                            } else {
                                isAcceptClicked = false;
                                Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                            }
                        } else {
                            isAcceptClicked = false;
                            Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                        }
                    } else {
                        isAcceptClicked = false;
                        Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                    }
                    hideLoading();
                },
                error -> {
                    isAcceptClicked = false;
                    hideLoading();
                    NetworkResponse res = error.networkResponse;
                    if (res != null) {
                        if (res.statusCode == 403) {
                            showLoading();
                            Utilities.showMessage(const_main, getResources().getString(R.string.session_expired));
                            JambaCabApplication.getInstance().removeDriver(const_main);
                        } else {
                            Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                        }
                    } else {
                        Utilities.showMessage(const_main, getResources().getString(R.string.internet_error));
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("x-custom-token", new UserSession(Dashboard.this).getAccessToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    /*private float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);
        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }*/

    private double bearingBetweenLocations(LatLng latLng1, LatLng latLng2) {
        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);
        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;
        return brng;
    }

    public void onClickNavigate(View view) {
        showLoading();
        new Handler().postDelayed(() -> {
            if (new UserSession(Dashboard.this).getUserPickUp()) {
                JSONObject dict_pickUpLocation = dict_ride_info.optJSONObject("pickUpLocation");
                assert dict_pickUpLocation != null;
                String str_pickup_lat = dict_pickUpLocation.optString("lat");
                String str_pickup_lng = dict_pickUpLocation.optString("lng");
                double pickup_lat = Double.parseDouble(str_pickup_lat);
                double pickup_lng = Double.parseDouble(str_pickup_lng);
                old_position = new LatLng(pickup_lat, pickup_lng);

                JSONObject dict_drop_location = dict_ride_info.optJSONObject("dropLocation");
                assert dict_drop_location != null;
                String str_drop_location_lat = dict_drop_location.optString("lat");
                String str_drop_location_lng = dict_drop_location.optString("lng");
                double drop_location_lat = Double.parseDouble(str_drop_location_lat);
                double drop_location_lng = Double.parseDouble(str_drop_location_lng);
        /*createFloating("maps");
        String uri = "http://maps.google.com/maps?f=d&hl=en&saddr=" + pickup_lat + "," + pickup_lng + "&daddr=" +
                drop_location_lat + "," + drop_location_lng;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);*/

                if (locLatLng == null) {
                    locLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                }
                Point sourceP = Point.fromLngLat(locLatLng.longitude, locLatLng.latitude);
                Point destP = Point.fromLngLat(drop_location_lng, drop_location_lat);
                launchNavigation(sourceP, destP);


            } else if (new UserSession(Dashboard.this).getRideStatus()) {
                driver_old_latlng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                JSONObject dict_pickUpLocation = dict_ride_info.optJSONObject("pickUpLocation");
                assert dict_pickUpLocation != null;
                String str_pickup_lat = dict_pickUpLocation.optString("lat");
                String str_pickup_lng = dict_pickUpLocation.optString("lng");
                double pickup_lat = Double.parseDouble(str_pickup_lat);
                double pickup_lng = Double.parseDouble(str_pickup_lng);
                LatLng pickup_lat_lng = new LatLng(pickup_lat, pickup_lng);

                if (driver_old_latlng != null) {
            /*createFloating("maps");
            String uri = "http://maps.google.com/maps?f=d&hl=en&saddr=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude() + "&daddr=" +
                    pickup_lat + "," + pickup_lng;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            startActivity(intent);*/
                    if (locLatLng == null) {
                        locLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    }
                    Point sourceP = Point.fromLngLat(locLatLng.longitude, locLatLng.latitude);
                    Point destP = Point.fromLngLat(pickup_lng, pickup_lat);
                    launchNavigation(sourceP, destP);
//                    launchNavigation(sourceP, destP);
                } else {
                    Log.i("pickup_lat_lng", "pickup_lat_lng" + pickup_lat_lng);
                    Log.i("driver_old_latlng", "driver_old_latlng" + driver_old_latlng);
                }
            }
            new Handler().postDelayed(this::hideLoading, 500);
        }, 500);
    }

    private void pickupCustomer(JSONObject dict_params) {
        String append_url = Constants.RIDES_URL + Constants.PICKUP_CUSTOMER;
        JsonObjectRequest stringRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, append_url, dict_params,
                response -> {
                    if (response != null) {
                        String status = response.optString("type");
                        String message = response.optString("message");
                        if (status.equals("success")) {
                            isPolyLineDrawed = false;
                            new UserSession(Dashboard.this).removePickupParams();
                            String rideID = dict_ride_info.optString("rideId");
                            TripDetails trip_details = new TripDetails();
                            trip_details.setStatus("ride_pickup");
                            arrPolyLinePoints = new ArrayList<>();
                            if (dict_ride_info.optBoolean("delivar")) {
                                String fullName = dict_ride_info.optString("reciverUserName");
                                str_mobile_number = dict_ride_info.optString("reciverMobileNumber");
                                tvUserInfo.setText(fullName);
                                tvUserName.setText(fullName);
                            }
                            String strDriverInfo = new UserSession(Dashboard.this).getUserData();
                            JSONObject dictDriverInfo;
                            String strPushParams = new UserSession(Dashboard.this).getPushParams();
                            try {
                                JSONObject dictPushParams = new JSONObject(strPushParams);
                                dictDriverInfo = new JSONObject(strDriverInfo);
                                dictDriverInfo.put("driverId", new UserSession(Dashboard.this).getUserId());
                                String vehicleInfo = new UserSession(Dashboard.this).getSelectedVehicleInfo();
                                JSONObject dictVehicleInfo = new JSONObject(vehicleInfo);
                                JSONObject dictDriverLocation = new JSONObject();
                                dictDriverLocation.put("lat", currentLocation.getLatitude());
                                dictDriverLocation.put("lng", currentLocation.getLongitude());
                                dictDriverInfo.put("vehicle_info", dictVehicleInfo);
                                dictDriverInfo.put("driver_location", dictDriverLocation);
                                dictPushParams.put("driver", dictDriverInfo.toString());
                                trip_details.setPushParams(dictPushParams.toString());
                                refRideDatabase.child(new UserSession(Dashboard.this).getUserId()).child(rideID).setValue(trip_details);
                                dialog.dismiss();
                                new UserSession(Dashboard.this).setLocationReached(true);
                                new UserSession(Dashboard.this).setIsUserPickup(true);
                                JSONObject dict_pickUpLocation = dict_ride_info.optJSONObject("pickUpLocation");
                                assert dict_pickUpLocation != null;
                                String str_pickup_lat = dict_pickUpLocation.optString("lat");
                                String str_pickup_lng = dict_pickUpLocation.optString("lng");
                                double pickup_lat = Double.parseDouble(str_pickup_lat);
                                double pickup_lng = Double.parseDouble(str_pickup_lng);
                                old_position = new LatLng(pickup_lat, pickup_lng);

                                JSONObject dict_drop_location = dict_ride_info.optJSONObject("dropLocation");
                                assert dict_drop_location != null;
                                String str_drop_location_lat = dict_drop_location.optString("lat");
                                String str_drop_location_lng = dict_drop_location.optString("lng");
                                double drop_location_lat = Double.parseDouble(str_drop_location_lat);
                                double drop_location_lng = Double.parseDouble(str_drop_location_lng);

                                String str_destination_address = dict_ride_info.optString("dropPoint");
                                tvArea.setText(str_destination_address);
                                rlNavigate.setVisibility(View.VISIBLE);
                                tvNavigate.setText(getResources().getString(R.string.navigate_drop));
                                rlGo.setVisibility(View.GONE);
                                rlNotification.setVisibility(View.GONE);
                                rlDecline.setVisibility(View.GONE);
                                tvRide.setText(getResources().getString(R.string.completed_ride));
                                refDatabase.child(new UserSession(Dashboard.this).getUserId()).child(rideID).setValue(null, null);

                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                builder.include(old_position);
                                builder.include(new LatLng(drop_location_lat, drop_location_lng));
                                LatLngBounds bounds = builder.build();

                                int width = getResources().getDisplayMetrics().widthPixels;
                                int padding = (int) (width * 0.4);
                            /*SupportMapFragment fragment = (SupportMapFragment) (getSupportFragmentManager()
                                    .findFragmentById(R.id.map));*/
                                if (fragment != null) {
                                    int height;
                                    View v = fragment.getView();
                                    if (v != null) {
                                        height = v.getHeight();
                                        CameraUpdate cu;
                                        try {
                                            cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                                        } catch (IllegalStateException e) {
                                            padding = (int) (width * 0.55);
                                            cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                                        }

                                        mGoogleMap.moveCamera(cu);
                                        mGoogleMap.animateCamera(cu);
                                        mGoogleMap.setOnMarkerClickListener(marker -> true);
                                    }
                                }
                                new UserSession(Dashboard.this).setNavigationStarted(true);
                                updateLocation();
                                /*Point userP = Point.fromLngLat(drop_location_lng, drop_location_lat);
                                Point driverP = Point.fromLngLat(currentLocation.getLongitude(), currentLocation.getLatitude());
                                startNavigation(driverP, userP);*/
                            } catch (JSONException e) {
                                Log.v("paramsException", "paramsException" + e.getLocalizedMessage());
                            }
                        } else {
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                        hideLoading();
                    } else {
                        hideLoading();
                        Toast.makeText(this, getString(R.string.fail_error), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    hideLoading();
                    NetworkResponse res = error.networkResponse;
                    if (res != null) {
                        if (res.statusCode == 403) {
                            showLoading();
                            Toast.makeText(this, getResources().getString(R.string.session_expired), Toast.LENGTH_SHORT).show();
                            JambaCabApplication.getInstance().removeDriver(const_main);
                        } else {
                            hideLoading();
                            Toast.makeText(this, getResources().getString(R.string.fail_error), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        hideLoading();
                        Toast.makeText(this, getResources().getString(R.string.fail_error), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("x-custom-token", new UserSession(Dashboard.this).getAccessToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void showLoading() {
        rlProgress.setVisibility(View.VISIBLE);
        /*ImageView ivLoading = findViewById(R.id.ivLoading);
        ivLoading.setImageResource(R.mipmap.load);
        Glide.with(this).load(R.mipmap.load).override(250, 250).into((ImageView) ivLoading);*/
    }

    private void hideLoading() {
        rlProgress.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(Dashboard.this);
        }
        /*if (navigation != null)
        {
            navigation.stopNavigation();
            navigation.onDestroy();
            navigation = null;
        }*/
    }

    @Override
    public void onBackPressed() {

        if (new UserSession(Dashboard.this).getScreen().equals("")) {
            mGoogleMap.clear();
            if (mPolyline != null) {
                mPolyline.remove();
            }
        }

        if (coordinate_cancel.getVisibility() == View.VISIBLE) {
            coordinate_cancel.setVisibility(View.GONE);
            cardViewDetails.setVisibility(View.VISIBLE);
            updateLocation();
        } else if (coordinate_complete.getVisibility() == View.VISIBLE) {
            coordinate_complete.setVisibility(View.GONE);
            cardViewDetails.setVisibility(View.VISIBLE);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            if (doubleBackToExitPressedOnce) {
                if (new UserSession(Dashboard.this).getFloating()) {
                    new UserSession(Dashboard.this).removeFloating();
                    if (FloatingViewService.getInstance() != null) {
                        FloatingViewService.getInstance().stopSelf();
                    }
                }
                Intent a = new Intent(Intent.ACTION_MAIN);
                a.addCategory(Intent.CATEGORY_HOME);
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                a.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                a.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(a);
                return;
            }
            doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getString(R.string.txt_back_to_exit), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }
    }

    @Override
    public void setPictureInPictureParams(@NonNull PictureInPictureParams params) {
        super.setPictureInPictureParams(params);
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        if (isInPictureInPictureMode) {
            hideControlInPictureMode();
        } else {
            visibleControlInPictureMode();
        }
        super.onPictureInPictureModeChanged(isInPictureInPictureMode);
    }

    public void hideControlInPictureMode() {
        rlEarningsDash.setVisibility(View.GONE);
        rlSheets.setVisibility(View.GONE);
        rlGo.setVisibility(View.GONE);
        rlNotification.setVisibility(View.GONE);
        ivUser.setVisibility(View.GONE);
        rlEarningsDash.setVisibility(View.GONE);
        if (mGoogleMap != null) {
            enableDisablePointer(false);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    public void visibleControlInPictureMode() {
        rlEarningsDash.setVisibility(View.VISIBLE);
        rlSheets.setVisibility(View.VISIBLE);
        rlGo.setVisibility(View.VISIBLE);
        ivUser.setVisibility(View.VISIBLE);
        rlEarningsDash.setVisibility(View.VISIBLE);
        if (mGoogleMap != null) {
            enableDisablePointer(true);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onUserLeaveHint() {
        if (!doubleBackToExitPressedOnce)
            createFloating();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void createFloating() {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        if (am != null) {
            ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
            assert foregroundTaskInfo.topActivity != null;
            String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();
            /*if (tag.equals("maps"))
            {
                if (foregroundTaskPackageName.equals(getPackageName())) {
                    String screen = new UserSession(Dashboard.this).getScreen();
                    if (!screen.equals("") && !screen.equals("ride_completed") && !screen.equals("ride_cancelled") && !doubleBackToExitPressedOnce)
                    {
                        onWindowPermission();
                    }
                }
            }else
            {
                *//*if (!foregroundTaskPackageName.equals(getPackageName())) {
                    String screen = new UserSession(Dashboard.this).getScreen();
                    if (!screen.equals("") && !screen.equals("ride_completed") && !screen.equals("ride_cancelled") && !doubleBackToExitPressedOnce)
                    {
                        onWindowPermission();
                    }
                }*//*
            }*/

            if (!foregroundTaskPackageName.equals(getPackageName())) {
                String screen = new UserSession(Dashboard.this).getScreen();
                if (!screen.equals("") && !screen.equals("ride_completed") && !screen.equals("ride_cancelled") && !doubleBackToExitPressedOnce) {
                    onWindowPermission();
                }
            }
        }
    }

    public void playSound() {
        try {
            Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.when);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), uri);
            r.play();
        } catch (Exception e) {
            Log.v("parseError", "ParseError" + e.getLocalizedMessage());
        }
    }

    public void checkBottomArrowDirection() {
        if (bottomSheetBehavior1.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            ivArrow.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_up_white));
        } else if (bottomSheetBehavior1.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            ivArrow.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_down_white));
        }
    }

    private void showCustomDialog(final ArrayAdapter adaptor) {
        final Dialog custom_dialog = new Dialog(this);
        custom_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        custom_dialog.setContentView(R.layout.singleton_custom_dialog_layout);
        custom_dialog.setCanceledOnTouchOutside(false);
        custom_dialog.setCancelable(false);

        RelativeLayout rlDialogBack = custom_dialog.findViewById(R.id.rlDialogBack);
        rlDialogBack.setOnClickListener(v -> custom_dialog.dismiss());

        Window window = custom_dialog.getWindow();
        assert window != null;
        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        final ListView listView = custom_dialog.findViewById(R.id.listview);
        final TextView tvTitle = custom_dialog.findViewById(R.id.tvTitle);
        tvTitle.setText(getString(R.string.emergency_text));

        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            String str_number = arr_emergency.get(position);
            String call_number = "tel:" + str_number;
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(call_number));
            startActivity(intent);

            custom_dialog.dismiss();
        });
        listView.setAdapter(adaptor);
        adaptor.notifyDataSetChanged();
        custom_dialog.show();
    }

    private void getDynamicPages() {
        /*ApiInterface apiService = ApiClient.retrofitBaseUrl(Constants.URL).create(ApiInterface.class);

        Call<DynamicPagesBean> call = apiService.getDynamicPages(new UserSession(Dashboard.this).getAccessToken());
        call.enqueue(new Callback<DynamicPagesBean>() {

            @Override
            public void onResponse(@NonNull Call<DynamicPagesBean> call, @NonNull retrofit2.Response<DynamicPagesBean> response) {
                try {
                    if (response.code() == 403) {
                        showLoading();
                        Utilities.showMessage(tvRide, getResources().getString(R.string.session_expired));
                        JambaCabApplication.getInstance().removeDriver(tvRide);
                    } else {
                        if (response.body() != null && response.body().getType().equals(Constants.RESPONSE_SUCCESS)) {
                                DynamicPagesBean dynamicPagesBean = response.body();
                                setDynamicPage(dynamicPagesBean);
                        } else {
                            hideLoading();
                            Utilities.showMessage(tvRide, getResources().getString(R.string.fail_error));
                        }
                    }
                } catch (NullPointerException e) {
                    Log.v("parseError", "ParseError"+e.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<DynamicPagesBean> call, @NonNull Throwable t) {
                hideLoading();
            }
        });*/

        String url = Constants.URL + Constants.GET_DYNAMIC_PAGES_NEW;
        JsonObjectRequest stringRequest = new JsonObjectRequest(com.android.volley.Request.Method.GET, url, null,
                response -> {
                    if (response != null) {
                        if (response.optString("type").equals("success")) {
                            Gson gson = new Gson();
                            DynamicPagesBean dynamicPagesBean = gson.fromJson(response.toString(), DynamicPagesBean.class);
                            setDynamicPage(dynamicPagesBean);
                        }
                    } else {
                        hideLoading();
                        Utilities.showMessage(tvRide, getResources().getString(R.string.fail_error));
                    }
                },
                error -> {
                    hideLoading();
                    Utilities.showMessage(tvRide, getResources().getString(R.string.fail_error));
                }
        );
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void setDynamicPage(DynamicPagesBean dynamicPagesBean) {

        dynamicPagesList = new ArrayList<>();
        for (int i = 0; i < dynamicPagesBean.getData().size(); i++) {
            if (dynamicPagesBean.getData().get(i).getTo().equalsIgnoreCase(Constants.DYNAMIC_PAGE_DRIVER) || dynamicPagesBean.getData().get(i).getTo().equalsIgnoreCase(Constants.DYNAMIC_PAGE_ALL)) {
                dynamicPagesList.add(dynamicPagesBean.getData().get(i));
            }
        }

        if (dynamicPagesList.size() > 0) {
            listMenuIds = new ArrayList<>();
            Collections.sort(dynamicPagesList, (p1, p2) -> {
                return p1.getOrder() - p2.getOrder(); // Ascending
            });
            for (int i = 0; i < dynamicPagesList.size(); i++) {
                Menu menu = navigationView.getMenu();

                menu.add(i, i, 0, dynamicPagesList.get(i).getId());
                listMenuIds.add(i);
            }

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.gps_not_connected_msg)
                .setCancelable(false)
                .setPositiveButton(R.string.btn_yes, (dialog, id) -> startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), GPS_REQUEST_CODE))
                .setNegativeButton(R.string.btn_no, (dialog, id) -> dialog.cancel());
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void checkGPSStatus() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (manager != null && !manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkOverLayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        }
    }

    private void checkFloatingButton() {
        if (new UserSession(Dashboard.this).getFloating()) {
            new UserSession(Dashboard.this).removeFloating();
            if (FloatingViewService.getInstance() != null) {
                FloatingViewService.getInstance().stopSelf();
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPS_REQUEST_CODE && resultCode == 0) {
            String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_MODE);
            if (provider != null && !provider.equals("0")) {
                fetchLocation();
            } else {
                enableLoc();
            }
        } else if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
            }
        } else if (requestCode == IMAGE_CODE && data != null) {
            boolean isRideStarted = new UserSession(Dashboard.this).getRideStatus();
            boolean isUserPickup = new UserSession(Dashboard.this).getUserPickUp();
            if (isRideStarted && !isUserPickup && data.hasExtra("image")) {
                pickupPic = data.getStringExtra("image");
                String pickupParams = new UserSession(Dashboard.this).getPickupParams();
                try {
                    JSONObject dictPickupParams = new JSONObject(pickupParams);
                    dictPickupParams.put("pickupImage", pickupPic);
                    if (Utilities.isInternetAvailable(Dashboard.this)) {
                        showLoading();
                        pickupCustomer(dictPickupParams);
                    } else {
                        hideLoading();
                        Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                    }
                } catch (JSONException e) {
                    Log.v("parseError", "ParseError" + e.getLocalizedMessage());
                }
            } else if (isRideStarted && isUserPickup && data.hasExtra("image")) {
                deliveryPic = data.getStringExtra("image");
                String deliveryParams = new UserSession(Dashboard.this).getCompleteParams();
                try {
                    JSONObject dictDeliveryParams = new JSONObject(deliveryParams);
                    dictDeliveryParams.put("delivaryImage", deliveryPic);
                    if (Utilities.isInternetAvailable(Dashboard.this)) {
                        showLoading();
                        completeRide(dictDeliveryParams);
                    } else {
                        hideLoading();
                        Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                    }
                } catch (JSONException e) {
                    Log.v("parseError", "ParseError" + e.getLocalizedMessage());
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /*
    public void stopPIPMode(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                onPictureInPictureModeChanged(false);
            }
        }, 10000);

    }*/

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (new UserSession(Dashboard.this).getRideStatus()) {
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            startService(intent);
        }
    }

    private void getSettings() {
        ApiInterface apiService = ApiClient.retrofitBaseUrl(Constants.URL).create(ApiInterface.class);

        Call<SettingsModel> call = apiService.getSettings(new UserSession(Dashboard.this).getAccessToken());
        call.enqueue(new Callback<SettingsModel>() {

            @Override
            public void onResponse(@NotNull Call<SettingsModel> call, @NotNull retrofit2.Response<SettingsModel> response) {
                try {
                    if (response.code() == 403) {
                        Utilities.showMessage(const_main, getResources().getString(R.string.session_expired));
                        JambaCabApplication.getInstance().removeDriver(const_main);
                    } else {
                        if (response.body() != null && response.body().getType().equals(getResources().getString(R.string.success))) {
                            SettingsModel model = response.body();

                            for (int i = 0; i < model.getData().size(); i++) {
                                SettingsModel.DataItem dictData = model.getData().get(i);
                                String id = dictData.get_id();

                                if (id.equals("jambaGoogleApiKey")) {
                                    new UserSession(Dashboard.this).setAPI(dictData.getValueType());
                                }

                                if (id.equals("mapboxKey")) {
                                    new UserSession(Dashboard.this).setMapBoxKey(dictData.getValueType());
                                }
                            }
                        } else {
                            Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                        }
                        hideLoading();
                    }
                } catch (NullPointerException e) {
                    hideLoading();
                    Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                }
            }

            @Override
            public void onFailure(@NotNull Call<SettingsModel> call, @NotNull Throwable t) {
                hideLoading();
                Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
            }
        });
    }

    private void startNavigation(Point origin, Point destination) {
        /*if (navigation != null)
        {
            navigation.stopNavigation();
            navigation = null;
        }*/
        NavigationRoute.builder(this)
                .accessToken(Constants.MAPBOX_ACCESS_TOKEN)
                .origin(origin)
                .destination(destination)
                .alternatives(true)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {

                    @Override
                    public void onResponse(@NonNull Call<DirectionsResponse> call, @NonNull Response<DirectionsResponse> response) {
                        if (response.body() != null) {
                            DirectionsRoute route = response.body().routes().get(0);

                            MapboxNavigationOptions options = MapboxNavigationOptions.builder()
                                    .build();

//                        navigation = new MapboxNavigation(Dashboard.this, Constants.MAPBOX_ACCESS_TOKEN, options);

//                        navigation.addOffRouteListener(Dashboard.this);
//                        navigation.addNavigationEventListener(Dashboard.this);
                       /* navigation.addOffRouteListener(location -> {
                            isRouteChanged = true;
//                            Toast.makeText(Dashboard.this, "Route Changed", Toast.LENGTH_SHORT).show();
                            if (dict_ride_info != null)
                            {
                                JSONObject dictPickupData;
                                if (dict_ride_info.has("pickUpLocation")) {
                                    dictPickupData = dict_ride_info.optJSONObject("pickUpLocation");
                                    if (dictPickupData != null)
                                    {
                                        double lat = dictPickupData.optDouble("lat");
                                        double lng = dictPickupData.optDouble("lng");
                                        Point destP = Point.fromLngLat(lng, lat);
                                        Point sourceP = Point.fromLngLat(currentLocation.getLongitude(), currentLocation.getLatitude());
                                        if (navigation != null)
                                        {
                                            navigation.stopNavigation();
                                        }
                                        startNavigation(sourceP, destP);
                                    }
                                }else
                                {
                                    isRouteChanged = false;
                                }
                            }else
                            {
                                isRouteChanged = false;
                            }
                        });*/
                        /*navigation.addProgressChangeListener((location, routeProgress) -> {
//                            Toast.makeText(Dashboard.this, String.valueOf(routeProgress.durationRemaining()), Toast.LENGTH_SHORT).show();
                            arrivalDuration = routeProgress.durationRemaining();
                            arrivalDistance = routeProgress.distanceRemaining();
                            *//*if (isRouteChanged)
                                setLocationChangedData(currentLocation, arrivalDuration, arrivalDistance, true);
                            else
                                setLocationChangedData(currentLocation, arrivalDuration, arrivalDistance, false);*//*
                            setLocationChangedData(currentLocation, arrivalDuration, arrivalDistance, isRouteChanged);
                        });*/
//                        navigation.startNavigation(route);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<DirectionsResponse> call, @NonNull Throwable t) {
                        Log.v("failure", "failure");
                    }
                });
    }

    private void launchNavigation(Point origin, Point destination) {
        NavigationRoute.builder(this)
                .accessToken(Constants.MAPBOX_ACCESS_TOKEN)
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {

                    @Override
                    public void onResponse(@NonNull Call<DirectionsResponse> call, @NonNull Response<DirectionsResponse> response) {
                        if (response.body() != null) {
                            DirectionsRoute route = response.body().routes().get(0);

                            com.mapbox.mapboxsdk.camera.CameraPosition initialPosition = new com.mapbox.mapboxsdk.camera.CameraPosition.Builder()
                                    .target(new com.mapbox.mapboxsdk.geometry.LatLng(origin.latitude(), origin.longitude()))
                                    .zoom(18)
                                    .build();

                            NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                                    .directionsRoute(route)
                                    .shouldSimulateRoute(false)
                                    .initialMapCameraPosition(initialPosition)
                                    .build();
                            NavigationLauncher.startNavigation(Dashboard.this, options);

                            /*new Handler().postDelayed(() -> startNavigation(origin, destination), 5000);*/
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<DirectionsResponse> call, @NonNull Throwable t) {
                        Log.v("error", "error" + t.getLocalizedMessage());
                    }
                });
    }

    /*private void restartNavigation()
    {
        if (navigation != null)
        {
            navigation.stopNavigation();
            navigation = null;
        }

        MapboxNavigationOptions options = MapboxNavigationOptions.builder()
                .build();

        navigation = new MapboxNavigation(Dashboard.this, Constants.MAPBOX_ACCESS_TOKEN, options);

        navigation.addOffRouteListener(location -> {
            isRouteChanged = true;
            if (dict_ride_info != null)
            {
                JSONObject dictPickupData;
                if (dict_ride_info.has("pickUpLocation")) {
                    dictPickupData = dict_ride_info.optJSONObject("pickUpLocation");
                    if (dictPickupData != null)
                    {
                        double lat = dictPickupData.optDouble("lat");
                        double lng = dictPickupData.optDouble("lng");
                        Point destP = Point.fromLngLat(lng, lat);
                        Point sourceP = Point.fromLngLat(currentLocation.getLongitude(), currentLocation.getLatitude());
                        if (navigation != null)
                        {
                            navigation.stopNavigation();
                        }
                        startNavigation(sourceP, destP);
                    }
                }else
                {
                    isRouteChanged = false;
                }
            }else
            {
                isRouteChanged = false;
            }
        });
        navigation.addProgressChangeListener((location, routeProgress) -> {
            Toast.makeText(Dashboard.this, String.valueOf(routeProgress.durationRemaining()), Toast.LENGTH_SHORT).show();

            arrivalDuration = routeProgress.durationRemaining();
            arrivalDistance = routeProgress.distanceRemaining();
            setLocationChangedData(location, arrivalDuration, arrivalDistance, isRouteChanged);
        });
        navigation.startNavigation(route);
    }*/
    private void getHeatMapData() {
        ApiInterface apiService = ApiClient.retrofitBaseUrl(Constants.URL).create(ApiInterface.class);
        Call<HeatMapBaseResponse> call = apiService.getHeatMapData(new UserSession(Dashboard.this).getAccessToken(), "user", "true");

        call.enqueue(new Callback<HeatMapBaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<HeatMapBaseResponse> call, @NonNull Response<HeatMapBaseResponse> response) {
                if (response.code() == 403) {
                    Utilities.showMessage(const_main, getResources().getString(R.string.session_expired));
                    JambaCabApplication.getInstance().removeDriver(const_main);
                } else {
                    if (response.body() != null) {
                        HeatMapBaseResponse model = response.body();
                        dataArrayList = new ArrayList<>();
                        dataArrayList.addAll(model.getData());
                        latLongList = new ArrayList<>();
                        for (int i = 0; i < model.getData().size(); i++) {
                            double lat = model.getData().get(i).getLatitude();
                            double lng = model.getData().get(i).getLongitude();
                            latLongList.add(new LatLng(lat, lng));
                        }
                        Timber.tag("HeatMapData").d("LatLong%s", latLongList);
                        setHeatMap();
                    } else {
                        Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<HeatMapBaseResponse> call, @NonNull Throwable t) {
                Utilities.showMessage(const_main, getResources().getString(R.string.fail_error));
            }
        });
    }

    private void setHeatMap() {
        if (latLongList != null && latLongList.size() > 0) {
            if (mProvider == null) {
                mProvider = new HeatmapTileProvider.Builder().
                        data(latLongList).gradient(setGradient()).build();
                if (mGoogleMap != null)
                    mOverlay = mGoogleMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
                //For testing perpose
//                changeGradient();
//                changeRadius();
//                changeOpacity();
            } else {
                mProvider.setData(latLongList);
                mOverlay.clearTileCache();
            }
        }
    }

    public void changeRadius() {
        if (mDefaultRadius) {
            mProvider.setRadius(ALT_HEATMAP_RADIUS);
        } else {
            mProvider.setRadius(HeatmapTileProvider.DEFAULT_RADIUS);
        }
        mOverlay.clearTileCache();
        mDefaultRadius = !mDefaultRadius;
    }

    public void changeGradient() {
        if (mDefaultGradient) {
            mProvider.setGradient(ALT_HEATMAP_GRADIENT);
        } else {
            mProvider.setGradient(HeatmapTileProvider.DEFAULT_GRADIENT);
        }
        mOverlay.clearTileCache();
        mDefaultGradient = !mDefaultGradient;
    }

    public Gradient setGradient() {
        int[] colors = {
                Color.rgb(102, 225, 0), // green
                Color.rgb(255, 0, 0)    // red
        };
        float[] startPoints = {
                0.2f, 1f
        };
        return new Gradient(colors, startPoints);
    }

    public void changeOpacity() {
        if (mDefaultOpacity) {
            mProvider.setOpacity(ALT_HEATMAP_OPACITY);
        } else {
            mProvider.setOpacity(HeatmapTileProvider.DEFAULT_OPACITY);
        }
        mOverlay.clearTileCache();
        mDefaultOpacity = !mDefaultOpacity;
    }

    private void sendRideDetailsToServer(JSONObject dictParams) {
        String appendUrl = Constants.URL + Constants.LOCATIONS;
        JsonObjectRequest stringRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, appendUrl, dictParams,
                response -> hideLoading(),
                error -> {
                    NetworkResponse res = error.networkResponse;
                    if (res != null) {
                        if (res.statusCode == 403) {
                            Utilities.showMessage(const_main, getResources().getString(R.string.session_expired));
                            JambaCabApplication.getInstance().removeDriver(const_main);
                        } else {
                            hideLoading();
                        }
                    } else {
                        hideLoading();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put(getResources().getString(R.string.customType), getResources().getString(R.string.applicationJson));
                params.put(getResources().getString(R.string.xCustomToken), new UserSession(Dashboard.this).getAccessToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Handler detailsHandler = new Handler();
        detailsHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (new UserSession(Dashboard.this).getScreen().equals("") && currentLocation != null) {
                    String strUserID = new UserSession(Dashboard.this).getUserId();
                    if (!strUserID.equals("")) {
                        double userID = Double.parseDouble(strUserID);
                        boolean isDriverAvailable = new UserSession(Dashboard.this).getDriverAvailability();
                        JSONObject dictParams = new JSONObject();
                        try {
                            dictParams.put("mobileNumber", userID);
                            dictParams.put("latitude", currentLocation.getLatitude());
                            dictParams.put("longitude", currentLocation.getLongitude());
                            dictParams.put("usertype", "driver");
                            dictParams.put("online", isDriverAvailable);
                            sendRideDetailsToServer(dictParams);
                        } catch (JSONException e) {
                            Log.v("Exception1000", "Exception 1000" + e.getLocalizedMessage());
                        }
                    }
                }
                detailsHandler.postDelayed(this, 120000);
            }
        }, 120000);
    }

    private void enableDisablePointer(boolean enabled) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (enabled)
                mGoogleMap.setMyLocationEnabled(true);
            else
                mGoogleMap.setMyLocationEnabled(false);
        }
    }
}