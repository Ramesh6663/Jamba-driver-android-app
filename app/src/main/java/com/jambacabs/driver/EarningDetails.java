package com.jambacabs.driver;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.jambacabs.driver.api.ApiClient;
import com.jambacabs.driver.api.ApiInterface;
import com.jambacabs.driver.font.CustomTextView;
import com.jambacabs.driver.models.EarningChartBean;
import com.jambacabs.driver.models.EarningPostBean;
import com.jambacabs.driver.models.EarningsResponseBean;
import com.jambacabs.driver.singleton.UserSession;
import com.jambacabs.driver.singleton.Utilities;
import com.jambacabs.driver.singleton.Constants;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Vector;
import retrofit2.Call;
import retrofit2.Callback;
import timber.log.Timber;

public class EarningDetails extends Activity implements View.OnClickListener {
    private RelativeLayout rlProgress;
    private CustomTextView tvFromDate;
    private CustomTextView tvToDate;
    private CustomTextView tvTrips;
    private CustomTextView tvTotalEarnings;
    private CustomTextView tvNoEarnings;
    String end_date;
    List<Date> allDates;
    AnyChartView anyChartView ;
    Cartesian cartesian ;
    Column column;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.earning_details_layout);
        Utilities.setFirebaseAnalytics(EarningDetails.this);
        int flags = getWindow().getDecorView().getSystemUiVisibility();
        flags = flags ^ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        getWindow().getDecorView().setSystemUiVisibility(flags);

        initUI();
        setDateInterval();
    }


    private void initUI() {
        rlProgress = findViewById(R.id.rlProgress);
        tvFromDate = findViewById(R.id.tvFromDate);
        tvToDate = findViewById(R.id.tvToDate);
        tvTrips = findViewById(R.id.tvTrips);
        tvTotalEarnings = findViewById(R.id.tvTotalEarnings);
        tvNoEarnings = findViewById(R.id.tvNoEarnings);
        anyChartView = findViewById(R.id.any_chart_view);
        cartesian=AnyChart.column();
        anyChartView.setChart(cartesian);
        setChartDesign();
        allDates = new ArrayList<>();
        anyChartView.setLicenceKey(getResources().getString(R.string.any_chat_licence_key));
    }
    private void setDateInterval() {

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        String[] days = new String[7];
        String start_date = "";
        end_date = "";
        for (int i = 0; i < 7; i++) {
            days[i] = format.format(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            start_date = days[0];
            end_date = days[6];
        }

        if (!start_date.equals("") && !end_date.equals("")) {
            try {
                tvFromDate.setText(start_date);
                tvToDate.setText(end_date);
                tvTrips.setText("0");
                tvTotalEarnings.setText("0");

                format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date date11 = format.parse(start_date);
                Date date22 = format.parse(end_date);
                if (date22 != null) {
                    Date dtPlusOne = new Date(date22.getTime() + (1000 * 60 * 60 * 24));
                    long start_stamp = 0;
                    if (date11 != null) {
                        start_stamp = date11.getTime();
                    }
                    long end_stamp = dtPlusOne.getTime();
                    callDriverEarnings(start_stamp, end_stamp-1);
                }
            } catch (ParseException e) {
                Timber.v("ParseError%s", e.getLocalizedMessage());
            }
        }
    }

    private void setChartDesign() {
        cartesian.animation(true);
        cartesian.title("Total driver Earnings");

        cartesian.yScale().minimum(0d);
        //cartesian.yScale().maximum(1000d);

        cartesian.yAxis(0).labels().format("₹{%Value}{groupsSeparator: }");
        //cartesian.xAxis(0).labels("test");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        cartesian.xAxis(0).title("Dates");
        cartesian.yAxis(0).title("Earnings");
    }

    private void callDriverEarnings(long start_stamp, long end_stamp) {
        String str_user_id = new UserSession(this).getUserId();
        double user_id = Double.parseDouble(str_user_id);
        allDates = Utilities.getDates(Utilities.getDate(start_stamp), Utilities.getDate(end_stamp));

        EarningPostBean earningPostBean = new EarningPostBean();
        earningPostBean.setDriverId((long) user_id);
        earningPostBean.setFromDate(start_stamp);
        earningPostBean.setEndDate(end_stamp);
        Timber.e(" start_stamp:" + start_stamp + " end_stamp:" + end_stamp);
        if (Utilities.isInternetAvailable(EarningDetails.this))
        {
            showLoading();
            getDriverEarnings(earningPostBean);
        }else
        {
            Utilities.showMessage(tvFromDate, getResources().getString(R.string.internet_error));
        }
    }

    private void getDriverEarnings(EarningPostBean earningPostBean) {
        ApiInterface apiService = ApiClient.retrofitBaseUrl(Constants.PAYMENTS_URL).create(ApiInterface.class);

        Timber.e("Start::" + earningPostBean.getFromDate() + "    End::" + earningPostBean.getEndDate());
        Call<EarningsResponseBean> call = apiService.getDriverEarnings(new UserSession(EarningDetails.this).getAccessToken(), earningPostBean);
        call.enqueue(new Callback<EarningsResponseBean>() {

            @Override
            public void onResponse(@NonNull Call<EarningsResponseBean> call, @NonNull retrofit2.Response<EarningsResponseBean> response) {
                try {
                    if (response.code() == 403) {
                        showLoading();
                        Utilities.showMessage(tvFromDate, getResources().getString(R.string.session_expired));
                        JambaCabApplication.getInstance().removeDriver(tvFromDate);
                    }else
                    {
                        if (response.body() != null) {
                            if (response.body().getType().equals(Constants.RESPONSE_SUCCESS)) {
                                hideLoading();
                                EarningsResponseBean.Data earningData = response.body().getData();
                                String trips = earningData.getTotalRides();
                                tvTrips.setText(trips);
                                String earnings = getResources().getString(R.string.rupee_unicode) + " " + numberCalculation((long) Double.parseDouble(earningData.getTotalErnings()));
                                tvTotalEarnings.setText(earnings);

                                if (earningData.getList().size() > 0) {
                                    Timber.e("Count Records From API:: %s", earningData.getList().size());
                                    visibleEarningData();
                                    setChartData(earningData);
                                }else {
                                    noEarningData();
                                }
                            } else {
                                hideLoading();
                                tvNoEarnings.setVisibility(View.VISIBLE);
                                Utilities.showMessage(tvFromDate, getResources().getString(R.string.fail_error));
                            }
                        }
                    }
                } catch (NullPointerException e) {
                    Timber.tag("parseError").v("ParseError%s", e.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<EarningsResponseBean> call, @NonNull Throwable t) {
                hideLoading();
                Utilities.showMessage(tvFromDate, getResources().getString(R.string.fail_error));
            }
        });
    }

    public void visibleEarningData(){
        anyChartView.setVisibility(View.VISIBLE);
        tvNoEarnings.setVisibility(View.INVISIBLE);
    }

    public void noEarningData(){
        anyChartView.setVisibility(View.INVISIBLE);
        tvNoEarnings.setVisibility(View.VISIBLE);
    }

    private String numberCalculation(long number) {
        if (number < 1000)
            return "" + number;
        int exp = (int) (Math.log(number) / Math.log(1000));
        return String.format(Locale.getDefault(),"%.1f %c", number / Math.pow(1000, exp), "kMGTPE".charAt(exp - 1));
    }

    private void setChartData(EarningsResponseBean.Data earningData) {
        tvNoEarnings.setVisibility(View.GONE);
        cartesian.removeAllSeries();
        anyChartView.invalidate();

        ArrayList<EarningChartBean> listDates = new ArrayList<>();
        for (int i = 0; i < earningData.getList().size(); i++) {
            EarningChartBean chart = new EarningChartBean();
            chart.setDate(Utilities.getEarningDate(earningData.getList().get(i).getDate()));
            chart.setAmount(Double.parseDouble(earningData.getList().get(i).getErnings()));
            listDates.add(chart);
        }

        List<DataEntry> data = new ArrayList<>();
        Enumeration e = new Vector(listDates).elements();
        while (e.hasMoreElements()) {
            EarningChartBean bean= (EarningChartBean) e.nextElement();
            data.add(new ValueDataEntry(bean.getDate(), (int) bean.getAmount()));
        }
        column = cartesian.column(data);
        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("₹{%Value}{groupsSeparator: }");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rlEarningBack) {
            onBackPressed();
        } else if (id == R.id.llFromDate) {
            openDatePicker(tvFromDate);
        } else if (id == R.id.llTodate) {
            openDatePicker(tvToDate);
        } else if (id == R.id.btnAllTransactions) {
            startActivity(new Intent(EarningDetails.this, TransactionDetails.class).putExtra("tagFrom", "details"));
            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
        } else if (id == R.id.btnAllTrips) {
            startActivity(new Intent(EarningDetails.this, Trips.class).putExtra("tagFrom", "details"));
            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
        }
    }

    private void openDatePicker(CustomTextView textView) {
        String date = textView.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        int mYear = 0, mMonth = 0, mDay = 0;
        try {
            Date given_date = sdf.parse(date);
            sdf = new SimpleDateFormat("yyyy", Locale.getDefault());
            assert given_date != null;
            String str_year = sdf.format(given_date);
            sdf = new SimpleDateFormat("MM", Locale.getDefault());
            String str_month = sdf.format(given_date);
            sdf = new SimpleDateFormat("dd", Locale.getDefault());
            String str_d = sdf.format(given_date);
            mYear = Integer.parseInt(str_year);
            mMonth = Integer.parseInt(str_month);
            mDay = Integer.parseInt(str_d);

        } catch (ParseException e) {
            Timber.v("ParseError%s", e.getLocalizedMessage());
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(EarningDetails.this,
                (view, year, monthOfYear, dayOfMonth) -> {

                    String str_selected_date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                    textView.setText(str_selected_date);
                    SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    try {
                        Date start_date1 = sdf1.parse(tvFromDate.getText().toString());
                        Date end_date = sdf1.parse(tvToDate.getText().toString());
                        if (end_date != null) {
                            Date dtPlusOne = new Date(end_date.getTime() + (1000 * 60 * 60 * 24));
                            assert start_date1 != null;
                            long start_stamp = Objects.requireNonNull(start_date1).getTime();
                            long end_stamp = dtPlusOne.getTime();
                            callDriverEarnings(start_stamp, end_stamp-1);
                        }
                    } catch (ParseException e) {
                        Timber.v("ParseError%s", e.getLocalizedMessage());
                    }
                }, mYear, mMonth - 1, mDay);


        Date current_date = new Date();
        if (textView == tvToDate) {
            sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            try {
                Date max_date = sdf.parse(end_date);
                Date min_date = sdf.parse(tvFromDate.getText().toString());
                assert max_date != null;
                datePickerDialog.getDatePicker().setMaxDate(max_date.getTime());
                assert min_date != null;
                datePickerDialog.getDatePicker().setMinDate(min_date.getTime());
            } catch (ParseException e) {
                Timber.v("ParseError%s", e.getLocalizedMessage());
            }
        } else {
            datePickerDialog.getDatePicker().setMaxDate(current_date.getTime());
        }

        datePickerDialog.show();
    }

    private void showLoading() {
        rlProgress.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        rlProgress.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EarningDetails.this, Earnings.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.move_right_enter, R.anim.move_right_exit);
        finish();
    }

    /*public static <T> List getDuplicate(Collection<T> list) {
        final List<T> duplicatedObjects = new ArrayList<T>();
        Set<T> set = new HashSet<T>() {
            @Override
            public boolean add(T e) {
                if (contains(e)) {
                    duplicatedObjects.add(e);
                }
                return super.add(e);
            }
        };
        set.addAll(list);
        return duplicatedObjects;
    }*/
}