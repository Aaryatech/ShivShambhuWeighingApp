package com.ats.shivshambhuweighingapp.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.ats.shivshambhuweighingapp.R;
import com.ats.shivshambhuweighingapp.activity.HomeActivity;
import com.ats.shivshambhuweighingapp.constants.Constants;
import com.ats.shivshambhuweighingapp.fragment.AddPoklenReadingFragment;
import com.ats.shivshambhuweighingapp.fragment.PoklenReadingListFragment;
import com.ats.shivshambhuweighingapp.fragment.WeighingListFragment;
import com.ats.shivshambhuweighingapp.model.GetPoklenReadingList;
import com.ats.shivshambhuweighingapp.model.Info;
import com.ats.shivshambhuweighingapp.util.CommonDialog;
import com.ats.shivshambhuweighingapp.util.CustomSharedPreference;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PoklenReadingListAdapter extends RecyclerView.Adapter<PoklenReadingListAdapter.MyViewHolder> {

    private ArrayList<GetPoklenReadingList> readingList;
    private Context context;

    public PoklenReadingListAdapter(ArrayList<GetPoklenReadingList> readingList, Context context) {
        this.readingList = readingList;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvPoklen, tvType, tvDate, tvShift, tvKM;
        public ImageView ivMenu;

        public MyViewHolder(View view) {
            super(view);
            tvPoklen = view.findViewById(R.id.tvPoklen);
            tvType = view.findViewById(R.id.tvType);
            tvDate = view.findViewById(R.id.tvDate);
            tvShift = view.findViewById(R.id.tvShift);
            tvKM = view.findViewById(R.id.tvKM);
            ivMenu = view.findViewById(R.id.ivMenu);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_poklen_reading_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final GetPoklenReadingList model = readingList.get(position);

        holder.tvPoklen.setText(model.getVehicleName());

        String type = "";
        if (model.getPokType() == 0) {
            type = "Breaking";
        } else {
            type = "Loading";
        }
        holder.tvType.setText(type);
        holder.tvDate.setText(model.getStartDate() + " to " + model.getEndDate());

        String shift = "";
        if (model.getShiftType() == 0) {
            shift = "Day";
        } else {
            shift = "Night";
        }
        holder.tvShift.setText(shift);
        holder.tvKM.setText(model.getStartReading() + " - " + model.getEndReading());

        holder.ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.getMenuInflater().inflate(R.menu.crud_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.action_edit) {

                            Gson gson = new Gson();
                            String json = gson.toJson(model);

                            HomeActivity activity = (HomeActivity) context;

                            Fragment adf = new AddPoklenReadingFragment();
                            Bundle args = new Bundle();
                            args.putString("model", json);
                            adf.setArguments(args);
                            activity.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, adf, "PoklenReadingListFragment").commit();

                        } else if (menuItem.getItemId() == R.id.action_delete) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
                            builder.setTitle("Confirm Action");
                            builder.setMessage("Do you want to delete reading?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deletePoklenReading(model.getReadingId());
                                    dialog.dismiss();
                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return readingList.size();
    }

    public void deletePoklenReading(int readingId) {
        if (Constants.isOnline(context)) {
            final CommonDialog commonDialog = new CommonDialog(context, "Loading", "Please Wait...");
            commonDialog.show();

            Call<Info> listCall = Constants.myInterface.deletePoklenReading(readingId);
            listCall.enqueue(new Callback<Info>() {
                @Override
                public void onResponse(Call<Info> call, Response<Info> response) {
                    try {
                        if (response.body() != null) {

                            Log.e("DELETE READING: ", " - " + response.body());

                            if (!response.body().isError()) {

                                HomeActivity activity = (HomeActivity) context;

                                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();

                                int userType = CustomSharedPreference.getInt(activity, CustomSharedPreference.KEY_USER_TYPE);
                                if (userType == 1) {
                                    FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                                    ft.replace(R.id.content_frame, new PoklenReadingListFragment(), "Exit");
                                    ft.commit();

                                } else if (userType == 2) {
                                    FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                                    ft.replace(R.id.content_frame, new PoklenReadingListFragment(), "HomeFragment");
                                    ft.commit();

                                } else {
                                    FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                                    ft.replace(R.id.content_frame, new PoklenReadingListFragment(), "HomeFragment");
                                    ft.commit();

                                }


                            } else {
                                Toast.makeText(context, "Unable to process", Toast.LENGTH_SHORT).show();
                            }

                            commonDialog.dismiss();

                        } else {
                            commonDialog.dismiss();
                            Log.e("Data Null : ", "-----------");
                            Toast.makeText(context, "Unable to process", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        commonDialog.dismiss();
                        Log.e("Exception : ", "-----------" + e.getMessage());
                        Toast.makeText(context, "Unable to process", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<Info> call, Throwable t) {
                    commonDialog.dismiss();
                    Log.e("onFailure : ", "-----------" + t.getMessage());
                    Toast.makeText(context, "Unable to process", Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
            });
        } else {
            Toast.makeText(context, "No Internet Connection !", Toast.LENGTH_SHORT).show();
        }
    }

}
