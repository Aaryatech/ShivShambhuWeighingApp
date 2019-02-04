package com.ats.shivshambhuweighingapp.fragment;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ats.shivshambhuweighingapp.R;
import com.ats.shivshambhuweighingapp.adapter.PoklenReadingListAdapter;
import com.ats.shivshambhuweighingapp.constants.Constants;
import com.ats.shivshambhuweighingapp.model.GetPoklenReadingList;
import com.ats.shivshambhuweighingapp.model.Vehicle;
import com.ats.shivshambhuweighingapp.util.CommonDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PoklenReadingListFragment extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerView;
    private EditText edFromDate, edToDate;
    private TextView tvFromDate, tvToDate;
    private LinearLayout llSearch;

    ArrayList<GetPoklenReadingList> poklenReadingList = new ArrayList<>();
    PoklenReadingListAdapter adapter;

    long fromDateMillis, toDateMillis;
    int yyyy, mm, dd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_poklen_reading_list, container, false);

        getActivity().setTitle("Poklen Reading List");

        recyclerView = view.findViewById(R.id.recyclerView);
        edFromDate = view.findViewById(R.id.edFromDate);
        edToDate = view.findViewById(R.id.edToDate);
        tvFromDate = view.findViewById(R.id.tvFromDate);
        tvToDate = view.findViewById(R.id.tvToDate);
        llSearch = view.findViewById(R.id.llSearch);

        edFromDate.setOnClickListener(this);
        edToDate.setOnClickListener(this);
        llSearch.setOnClickListener(this);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");

        edFromDate.setText("" + sdf1.format(System.currentTimeMillis()));
        edToDate.setText("" + sdf1.format(System.currentTimeMillis()));
        tvFromDate.setText("" + sdf.format(System.currentTimeMillis()));
        tvToDate.setText("" + sdf.format(System.currentTimeMillis()));

        getPoklenReadingList(sdf.format(System.currentTimeMillis()), sdf.format(System.currentTimeMillis()));
        //getPoklenReadingList("2018-10-01", "2018-12-31");


        return view;
    }


    public void getPoklenReadingList(String fromDate, String toDate) {
        if (Constants.isOnline(getContext())) {
            final CommonDialog commonDialog = new CommonDialog(getContext(), "Loading", "Please Wait...");
            commonDialog.show();

            Call<ArrayList<GetPoklenReadingList>> listCall = Constants.myInterface.getPoklenReadingList(fromDate, toDate);
            listCall.enqueue(new Callback<ArrayList<GetPoklenReadingList>>() {
                @Override
                public void onResponse(Call<ArrayList<GetPoklenReadingList>> call, Response<ArrayList<GetPoklenReadingList>> response) {
                    try {
                        if (response.body() != null) {

                            Log.e("POKLEN READING LIST : ", " - " + response.body());

                            poklenReadingList.clear();
                            poklenReadingList = response.body();

                            adapter = new PoklenReadingListAdapter(poklenReadingList, getContext());
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(adapter);

                            commonDialog.dismiss();

                        } else {
                            commonDialog.dismiss();
                            Log.e("Data Null : ", "-----------");
                        }
                    } catch (Exception e) {
                        commonDialog.dismiss();
                        Log.e("Exception : ", "-----------" + e.getMessage());
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<GetPoklenReadingList>> call, Throwable t) {
                    commonDialog.dismiss();
                    Log.e("onFailure : ", "-----------" + t.getMessage());
                    t.printStackTrace();
                }
            });
        } else {
            Toast.makeText(getContext(), "No Internet Connection !", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.edFromDate) {
            int yr, mn, dy;
            if (fromDateMillis > 0) {
                Calendar purchaseCal = Calendar.getInstance();
                purchaseCal.setTimeInMillis(fromDateMillis);
                yr = purchaseCal.get(Calendar.YEAR);
                mn = purchaseCal.get(Calendar.MONTH);
                dy = purchaseCal.get(Calendar.DAY_OF_MONTH);
            } else {
                Calendar purchaseCal = Calendar.getInstance();
                yr = purchaseCal.get(Calendar.YEAR);
                mn = purchaseCal.get(Calendar.MONTH);
                dy = purchaseCal.get(Calendar.DAY_OF_MONTH);
            }
            DatePickerDialog dialog = new DatePickerDialog(getContext(), fromDateListener, yr, mn, dy);
            dialog.show();

        } else if (view.getId() == R.id.edToDate) {
            int yr, mn, dy;
            if (toDateMillis > 0) {
                Calendar purchaseCal = Calendar.getInstance();
                purchaseCal.setTimeInMillis(toDateMillis);
                yr = purchaseCal.get(Calendar.YEAR);
                mn = purchaseCal.get(Calendar.MONTH);
                dy = purchaseCal.get(Calendar.DAY_OF_MONTH);
            } else {
                Calendar purchaseCal = Calendar.getInstance();
                yr = purchaseCal.get(Calendar.YEAR);
                mn = purchaseCal.get(Calendar.MONTH);
                dy = purchaseCal.get(Calendar.DAY_OF_MONTH);
            }
            DatePickerDialog dialog = new DatePickerDialog(getContext(), toDateListener, yr, mn, dy);
            dialog.show();

        } else if (view.getId() == R.id.llSearch) {

            String fromDate = tvFromDate.getText().toString().trim();
            String toDate = tvToDate.getText().toString().trim();

            if (fromDate.isEmpty()) {
                edFromDate.setError("Required");
            } else if (toDate.isEmpty()) {
                edFromDate.setError(null);
                edToDate.setError("Required");
            } else {
                edToDate.setError(null);

                getPoklenReadingList(fromDate, toDate);
            }
        }
    }

    DatePickerDialog.OnDateSetListener fromDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            yyyy = year;
            mm = month + 1;
            dd = dayOfMonth;
            edFromDate.setText(dd + "-" + mm + "-" + yyyy);
            tvFromDate.setText(yyyy + "-" + mm + "-" + dd);

            Calendar calendar = Calendar.getInstance();
            calendar.set(yyyy, mm - 1, dd);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR, 0);
            fromDateMillis = calendar.getTimeInMillis();
        }
    };

    DatePickerDialog.OnDateSetListener toDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            yyyy = year;
            mm = month + 1;
            dd = dayOfMonth;
            edToDate.setText(dd + "-" + mm + "-" + yyyy);
            tvToDate.setText(yyyy + "-" + mm + "-" + dd);

            Calendar calendar = Calendar.getInstance();
            calendar.set(yyyy, mm - 1, dd);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR, 0);
            toDateMillis = calendar.getTimeInMillis();
        }
    };

}
