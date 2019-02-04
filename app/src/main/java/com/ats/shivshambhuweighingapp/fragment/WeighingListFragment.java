package com.ats.shivshambhuweighingapp.fragment;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ats.shivshambhuweighingapp.R;
import com.ats.shivshambhuweighingapp.adapter.WeighingListAdapter;
import com.ats.shivshambhuweighingapp.constants.Constants;
import com.ats.shivshambhuweighingapp.model.Contractor;
import com.ats.shivshambhuweighingapp.model.GetWeighingList;
import com.ats.shivshambhuweighingapp.model.Vehicle;
import com.ats.shivshambhuweighingapp.util.CommonDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeighingListFragment extends Fragment {

    private RecyclerView recyclerView;

    long fromDateMillis, toDateMillis;
    int yyyy, mm, dd;

    ArrayList<String> vehicleNameList = new ArrayList<>();
    ArrayList<Integer> vehicleIdList = new ArrayList<>();

    ArrayList<String> contractorNameList = new ArrayList<>();
    ArrayList<Integer> contractorIdList = new ArrayList<>();

    ArrayList<String> filterVehicleNameList = new ArrayList<>();
    ArrayList<Integer> filterVehicleIdList = new ArrayList<>();

    ArrayList<String> filterContractorNameList = new ArrayList<>();
    ArrayList<Integer> filterContractorIdList = new ArrayList<>();


    ArrayList<GetWeighingList> weighingList = new ArrayList<>();

    WeighingListAdapter weighingAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weighing_list, container, false);

        getActivity().setTitle("Weighing List");

        recyclerView = view.findViewById(R.id.recyclerView);

        getVehicleList(1);
        getContractorList();


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        getWeighingList(sdf.format(System.currentTimeMillis()), sdf.format(System.currentTimeMillis()), 0, 0);


        return view;
    }


    public void getVehicleList(int type) {
        if (Constants.isOnline(getContext())) {
            final CommonDialog commonDialog = new CommonDialog(getContext(), "Loading", "Please Wait...");
            commonDialog.show();

            vehicleNameList.clear();
            vehicleIdList.clear();

            vehicleIdList.add(0);
            vehicleNameList.add("Select Vehicle");

            filterVehicleNameList.clear();
            filterVehicleIdList.clear();

            filterVehicleIdList.add(0);
            filterVehicleNameList.add("All");

            Call<ArrayList<Vehicle>> listCall = Constants.myInterface.getVehiclelistByType(type);
            listCall.enqueue(new Callback<ArrayList<Vehicle>>() {
                @Override
                public void onResponse(Call<ArrayList<Vehicle>> call, Response<ArrayList<Vehicle>> response) {
                    try {
                        if (response.body() != null) {

                            Log.e("VEHICLE LIST : ", " - " + response.body());


                            if (response.body().size() > 0) {
                                for (int i = 0; i < response.body().size(); i++) {
                                    vehicleNameList.add(response.body().get(i).getVehicleName());
                                    vehicleIdList.add(response.body().get(i).getVehicleId());
                                }
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

    public void getContractorList() {
        if (Constants.isOnline(getContext())) {
            final CommonDialog commonDialog = new CommonDialog(getContext(), "Loading", "Please Wait...");
            commonDialog.show();

            contractorIdList.clear();
            contractorNameList.clear();

            contractorIdList.add(0);
            contractorNameList.add("Select Contractor");

            Call<ArrayList<Contractor>> listCall = Constants.myInterface.getContractorList();
            listCall.enqueue(new Callback<ArrayList<Contractor>>() {
                @Override
                public void onResponse(Call<ArrayList<Contractor>> call, Response<ArrayList<Contractor>> response) {
                    try {
                        if (response.body() != null) {

                            Log.e("CONTRACTOR LIST : ", " - " + response.body());

                            if (response.body().size() > 0) {
                                for (int i = 0; i < response.body().size(); i++) {
                                    contractorNameList.add(response.body().get(i).getContrName());
                                    contractorIdList.add(response.body().get(i).getContrId());
                                }
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
                public void onFailure(Call<ArrayList<Contractor>> call, Throwable t) {
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
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.action_filter);
        item.setVisible(true);


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                new FilterDialog(getContext()).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public class FilterDialog extends Dialog {

        EditText edFromDate, edToDate;
        TextView tvFromDate, tvToDate;
        ImageView ivClose;
        CardView cardView;
        Spinner spVehicle, spContractor;

        public FilterDialog(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setTitle("Filter");
            setContentView(R.layout.dialog_filter);
            setCancelable(false);

            Window window = getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.TOP | Gravity.RIGHT;
            wlp.x = 10;
            wlp.y = 10;
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(wlp);

            edFromDate = findViewById(R.id.edFromDate);
            edToDate = findViewById(R.id.edToDate);
            tvFromDate = findViewById(R.id.tvFromDate);
            tvToDate = findViewById(R.id.tvToDate);
            Button btnFilter = findViewById(R.id.btnFilter);
            ivClose = findViewById(R.id.ivClose);
            spVehicle = findViewById(R.id.spVehicle);
            spContractor = findViewById(R.id.spContractor);

            edFromDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
                }
            });

            edToDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
                }
            });

            final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, vehicleNameList);
            spVehicle.setAdapter(spinnerAdapter);

            ArrayAdapter<String> contAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, contractorNameList);
            spContractor.setAdapter(contAdapter);


            btnFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (edFromDate.getText().toString().isEmpty()) {
                        edFromDate.setError("Select From Date");
                        edFromDate.requestFocus();
                    } else if (edToDate.getText().toString().isEmpty()) {
                        edToDate.setError("Select To Date");
                        edToDate.requestFocus();
                    } else {
                        dismiss();

                        String fromDate = tvFromDate.getText().toString();
                        String toDate = tvToDate.getText().toString();

                        int vehId = 0;
                        if (vehicleIdList.size() > 0) {
                            if (spVehicle.getSelectedItemPosition() == 0) {
                                vehId = 0;
                            } else {
                                vehId = vehicleIdList.get(spVehicle.getSelectedItemPosition());
                            }
                        }

                        int contId = 0;
                        if (contractorIdList.size() > 0) {
                            if (spContractor.getSelectedItemPosition() == 0) {
                                contId = 0;
                            } else {
                                contId = contractorIdList.get(spContractor.getSelectedItemPosition());
                            }
                        }


                        getWeighingList(fromDate, toDate, vehId, contId);


                    }
                }
            });

            ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });

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

    public void getWeighingList(String fromDate, String toDate, int vehId, int contId) {
        if (Constants.isOnline(getContext())) {
            final CommonDialog commonDialog = new CommonDialog(getContext(), "Loading", "Please Wait...");
            commonDialog.show();

            Call<ArrayList<GetWeighingList>> listCall = Constants.myInterface.getWeighingList(contId, vehId, fromDate, toDate);
            listCall.enqueue(new Callback<ArrayList<GetWeighingList>>() {
                @Override
                public void onResponse(Call<ArrayList<GetWeighingList>> call, Response<ArrayList<GetWeighingList>> response) {
                    try {
                        if (response.body() != null) {

                            Log.e("WEIGHING LIST : ", " - " + response.body());
                            weighingList.clear();

                            weighingList = response.body();

                            weighingAdapter = new WeighingListAdapter(weighingList, getContext());
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(weighingAdapter);

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
                public void onFailure(Call<ArrayList<GetWeighingList>> call, Throwable t) {
                    commonDialog.dismiss();
                    Log.e("onFailure : ", "-----------" + t.getMessage());
                    t.printStackTrace();
                }
            });
        } else {
            Toast.makeText(getContext(), "No Internet Connection !", Toast.LENGTH_SHORT).show();
        }
    }


}
