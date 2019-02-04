package com.ats.shivshambhuweighingapp.fragment;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ats.shivshambhuweighingapp.R;
import com.ats.shivshambhuweighingapp.constants.Constants;
import com.ats.shivshambhuweighingapp.model.GetPoklenReadingList;
import com.ats.shivshambhuweighingapp.model.PoklenReading;
import com.ats.shivshambhuweighingapp.model.User;
import com.ats.shivshambhuweighingapp.model.Vehicle;
import com.ats.shivshambhuweighingapp.util.CommonDialog;
import com.ats.shivshambhuweighingapp.util.CustomSharedPreference;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPoklenReadingFragment extends Fragment implements View.OnClickListener {

    private EditText edFromDate, edToDate, edStartKM, edEndKM, edStartTime, edEndTime;
    private RadioButton rbBreaking, rbLoading, rbDay, rbNight;
    private Button btnSubmit;
    private TextView tvFromDate, tvToDate;
    private Spinner spPoklen;

    long fromDateMillis, toDateMillis;
    int yyyy, mm, dd;

    ArrayList<String> vehicleNameList = new ArrayList<>();
    ArrayList<Integer> vehicleIdList = new ArrayList<>();

    GetPoklenReadingList poklenReading = null;
    int readingId = 0;

    User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_poklen_reading, container, false);

        getActivity().setTitle("Add Poklen Reading");

        String userStr = CustomSharedPreference.getString(getActivity(), CustomSharedPreference.KEY_USER);
        Gson gson1 = new Gson();
        user = gson1.fromJson(userStr, User.class);

        edFromDate = view.findViewById(R.id.edFromDate);
        edToDate = view.findViewById(R.id.edToDate);
        edStartKM = view.findViewById(R.id.edStartKM);
        edEndKM = view.findViewById(R.id.edEndKM);
        edStartTime = view.findViewById(R.id.edStartTime);
        edEndTime = view.findViewById(R.id.edEndTime);

        tvFromDate = view.findViewById(R.id.tvFromDate);
        tvToDate = view.findViewById(R.id.tvToDate);

        spPoklen = view.findViewById(R.id.spPoklen);

        rbBreaking = view.findViewById(R.id.rbBreaking);
        rbLoading = view.findViewById(R.id.rbLoading);
        rbDay = view.findViewById(R.id.rbDay);
        rbNight = view.findViewById(R.id.rbNight);

        btnSubmit = view.findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(this);
        edFromDate.setOnClickListener(this);
        edToDate.setOnClickListener(this);
        edStartTime.setOnClickListener(this);
        edEndTime.setOnClickListener(this);


        try {
            String readingStr = getArguments().getString("model");
            Gson gson = new Gson();
            poklenReading = gson.fromJson(readingStr, GetPoklenReadingList.class);

            if (poklenReading != null) {

                readingId = poklenReading.getReadingId();

                edFromDate.setText("" + poklenReading.getStartDate());
                tvFromDate.setText("" + poklenReading.getStartDate());
                edToDate.setText("" + poklenReading.getEndDate());
                tvToDate.setText("" + poklenReading.getEndDate());
                edStartKM.setText("" + poklenReading.getStartReading());
                edEndKM.setText("" + poklenReading.getEndReading());
                edStartTime.setText("" + poklenReading.getStartTime());
                edEndTime.setText("" + poklenReading.getEndTime());

                if (poklenReading.getPokType() == 0) {
                    rbBreaking.setChecked(true);
                } else if (poklenReading.getPokType() == 1) {
                    rbLoading.setChecked(true);
                }

                if (poklenReading.getShiftType() == 0) {
                    rbDay.setChecked(true);
                } else if (poklenReading.getShiftType() == 1) {
                    rbNight.setChecked(true);
                }

                getVehicleList(3);

            } else {
                getVehicleList(3);

            }

        } catch (Exception e) {
            Log.e("ADD POKLEN READING", " ------- EXCEPTION " + e.getMessage());
            e.printStackTrace();
            getVehicleList(3);
        }


        return view;
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

        } else if (view.getId() == R.id.edStartTime) {

            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    edStartTime.setText(selectedHour + ":" + selectedMinute);
                }
            }, hour, minute, false);//Yes 24 hour time
            mTimePicker.setTitle("Select Start Time");
            mTimePicker.show();

        } else if (view.getId() == R.id.edEndTime) {

            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    edEndTime.setText(selectedHour + ":" + selectedMinute);
                }
            }, hour, minute, false);//Yes 24 hour time
            mTimePicker.setTitle("Select End Time");
            mTimePicker.show();

        } else if (view.getId() == R.id.btnSubmit) {

            String fromDate = tvFromDate.getText().toString();
            String toDate = tvToDate.getText().toString();
            String startKm = edStartKM.getText().toString().trim();
            String endKm = edEndKM.getText().toString().trim();
            String startTime = edStartTime.getText().toString();
            String endTime = edEndTime.getText().toString();
            int vehicleId = vehicleIdList.get(spPoklen.getSelectedItemPosition());

            if (vehicleId == 0) {
                TextView spView = (TextView) spPoklen.getSelectedView();
                spView.setError("Required");
            } else if (fromDate.isEmpty()) {
                TextView spView = (TextView) spPoklen.getSelectedView();
                spView.setError(null);

                edFromDate.setError("Required");

            } else if (toDate.isEmpty()) {
                edFromDate.setError(null);
                edToDate.setError("Required");

            } else if (!rbBreaking.isChecked() && !rbLoading.isChecked()) {
                Toast.makeText(getContext(), "PLease select poklen type", Toast.LENGTH_SHORT).show();

                edToDate.setError(null);
            } else if (!rbDay.isChecked() && !rbNight.isChecked()) {
                Toast.makeText(getContext(), "PLease select shift type", Toast.LENGTH_SHORT).show();

            } else if (startKm.isEmpty()) {
                edStartKM.setError("Required");

            } else if (endKm.isEmpty()) {
                edStartKM.setError(null);
                edEndKM.setError("Required");

            } else if (startTime.isEmpty()) {
                edEndKM.setError(null);
                edStartTime.setError("Required");

            } else if (endTime.isEmpty()) {
                edStartTime.setError(null);
                edEndTime.setError("Required");

            } else {

                Float startKmFloat = Float.parseFloat(startKm);
                Float endKmFloat = Float.parseFloat(endKm);

                int poklenType = 0;
                if (rbBreaking.isChecked()) {
                    poklenType = 0;
                } else if (rbLoading.isChecked()) {
                    poklenType = 1;
                }

                int shiftType = 0;
                if (rbDay.isChecked()) {
                    shiftType = 0;
                } else if (rbNight.isChecked()) {
                    shiftType = 1;
                }

                PoklenReading poklenReading = new PoklenReading(readingId, vehicleId, fromDate, toDate, poklenType, shiftType, startKmFloat, endKmFloat, startTime, endTime, 1);
                savePoklenReading(poklenReading);
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


    public void getVehicleList(int type) {
        if (Constants.isOnline(getContext())) {
            final CommonDialog commonDialog = new CommonDialog(getContext(), "Loading", "Please Wait...");
            commonDialog.show();

            Call<ArrayList<Vehicle>> listCall = Constants.myInterface.getVehiclelistByType(type);
            listCall.enqueue(new Callback<ArrayList<Vehicle>>() {
                @Override
                public void onResponse(Call<ArrayList<Vehicle>> call, Response<ArrayList<Vehicle>> response) {
                    try {
                        if (response.body() != null) {

                            Log.e("VEHICLE LIST : ", " - " + response.body());
                            vehicleNameList.clear();
                            vehicleIdList.clear();

                            vehicleIdList.add(0);
                            vehicleNameList.add("Select Poklen");

                            if (response.body().size() > 0) {
                                for (int i = 0; i < response.body().size(); i++) {
                                    vehicleNameList.add(response.body().get(i).getVehicleName());
                                    vehicleIdList.add(response.body().get(i).getVehicleId());
                                }
                            }

                            ArrayAdapter<String> poklenAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, vehicleNameList);
                            spPoklen.setAdapter(poklenAdapter);

                            if (poklenReading != null) {
                                int pos = 0;
                                for (int i = 0; i < vehicleIdList.size(); i++) {
                                    if (vehicleIdList.get(i) == poklenReading.getPoklenId()) {
                                        pos = i;
                                        break;
                                    }
                                }

                                spPoklen.setSelection(pos);
                            }

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
                public void onFailure(Call<ArrayList<Vehicle>> call, Throwable t) {
                    commonDialog.dismiss();
                    Log.e("onFailure : ", "-----------" + t.getMessage());
                    t.printStackTrace();
                }
            });
        } else {
            Toast.makeText(getContext(), "No Internet Connection !", Toast.LENGTH_SHORT).show();
        }
    }

    public void savePoklenReading(PoklenReading poklenReading) {
        if (Constants.isOnline(getContext())) {
            final CommonDialog commonDialog = new CommonDialog(getContext(), "Loading", "Please Wait...");
            commonDialog.show();

            Call<PoklenReading> listCall = Constants.myInterface.savePoklenReading(poklenReading);
            listCall.enqueue(new Callback<PoklenReading>() {
                @Override
                public void onResponse(Call<PoklenReading> call, Response<PoklenReading> response) {
                    try {
                        if (response.body() != null) {

                            Log.e("SAVE POKLEN READING : ", " - " + response.body());

                            if (response.body().getReadingId() != 0) {
                                // Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();

                                int userType = CustomSharedPreference.getInt(getActivity(), CustomSharedPreference.KEY_USER_TYPE);
                                if (userType == 1) {
                                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                    ft.replace(R.id.content_frame, new PoklenReadingListFragment(), "Exit");
                                    ft.commit();

                                } else if (userType == 2) {
                                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                    ft.replace(R.id.content_frame, new PoklenReadingListFragment(), "Exit");
                                    ft.commit();

                                } else {
                                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                    ft.replace(R.id.content_frame, new PoklenReadingListFragment(), "Exit");
                                    ft.commit();

                                }


                            } else {
                                Toast.makeText(getContext(), "Unable to process", Toast.LENGTH_SHORT).show();
                            }


                            commonDialog.dismiss();

                        } else {
                            commonDialog.dismiss();
                            Toast.makeText(getContext(), "Unable to process", Toast.LENGTH_SHORT).show();
                            Log.e("Data Null : ", "-----------");
                        }
                    } catch (Exception e) {
                        commonDialog.dismiss();
                        Toast.makeText(getContext(), "Unable to process", Toast.LENGTH_SHORT).show();
                        Log.e("Exception : ", "-----------" + e.getMessage());
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<PoklenReading> call, Throwable t) {
                    commonDialog.dismiss();
                    Toast.makeText(getContext(), "Unable to process", Toast.LENGTH_SHORT).show();
                    Log.e("onFailure : ", "-----------" + t.getMessage());
                    t.printStackTrace();
                }
            });
        } else {
            Toast.makeText(getContext(), "No Internet Connection !", Toast.LENGTH_SHORT).show();
        }
    }


}
