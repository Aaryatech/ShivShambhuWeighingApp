package com.ats.shivshambhuweighingapp.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.ats.shivshambhuweighingapp.activity.ViewImageActivity;
import com.ats.shivshambhuweighingapp.constants.Constants;
import com.ats.shivshambhuweighingapp.fragment.AddPoklenReadingFragment;
import com.ats.shivshambhuweighingapp.fragment.AddWeighingFragment;
import com.ats.shivshambhuweighingapp.fragment.PoklenReadingListFragment;
import com.ats.shivshambhuweighingapp.fragment.WeighingListFragment;
import com.ats.shivshambhuweighingapp.model.GetPoklenReadingList;
import com.ats.shivshambhuweighingapp.model.GetWeighingList;
import com.ats.shivshambhuweighingapp.model.Info;
import com.ats.shivshambhuweighingapp.util.CommonDialog;
import com.ats.shivshambhuweighingapp.util.CustomSharedPreference;
import com.google.gson.Gson;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeighingListAdapter extends RecyclerView.Adapter<WeighingListAdapter.MyViewHolder> {

    private ArrayList<GetWeighingList> weighingList;
    private Context context;

    public WeighingListAdapter(ArrayList<GetWeighingList> weighingList, Context context) {
        this.weighingList = weighingList;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDate, tvQty, tvVehicle, tvContractor;
        public ImageView ivMenu, imageView;

        public MyViewHolder(View view) {
            super(view);
            tvDate = view.findViewById(R.id.tvDate);
            tvQty = view.findViewById(R.id.tvQty);
            tvVehicle = view.findViewById(R.id.tvVehicle);
            tvContractor = view.findViewById(R.id.tvContractor);
            imageView = view.findViewById(R.id.imageView);
            ivMenu = view.findViewById(R.id.ivMenu);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_weighing_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final GetWeighingList model = weighingList.get(position);

        holder.tvVehicle.setText(model.getVehicleName());
        holder.tvContractor.setText(model.getContrName());
        holder.tvDate.setText(model.getDate());
        holder.tvQty.setText("" + model.getQuantity());

        try {
            Picasso.get().load("" + Constants.IMAGE_PATH + "" + model.getPhoto1()).placeholder(R.mipmap.ic_launcher).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.imageView);

        } catch (Exception e) {
        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewImageActivity.class);
                intent.putExtra("image", "" + model.getPhoto1());
                context.startActivity(intent);
            }
        });

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

                            Fragment adf = new AddWeighingFragment();
                            Bundle args = new Bundle();
                            args.putString("model", json);
                            adf.setArguments(args);
                            activity.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, adf, "WeighingListFragment").commit();

                        } else if (menuItem.getItemId() == R.id.action_delete) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
                            builder.setTitle("Confirm Action");
                            builder.setMessage("Do you want to delete weight?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteWeight(model.getWeighId());
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
        return weighingList.size();
    }

    public void deleteWeight(int weightId) {
        if (Constants.isOnline(context)) {
            final CommonDialog commonDialog = new CommonDialog(context, "Loading", "Please Wait...");
            commonDialog.show();

            Call<Info> listCall = Constants.myInterface.deleteWeight(weightId);
            listCall.enqueue(new Callback<Info>() {
                @Override
                public void onResponse(Call<Info> call, Response<Info> response) {
                    try {
                        if (response.body() != null) {

                            Log.e("DELETE WEIGHT: ", " - " + response.body());

                            if (!response.body().isError()) {

                                HomeActivity activity = (HomeActivity) context;

                                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();

                                int userType = CustomSharedPreference.getInt(activity, CustomSharedPreference.KEY_USER_TYPE);
                                if (userType == 1) {
                                    FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                                    ft.replace(R.id.content_frame, new WeighingListFragment(), "Exit");
                                    ft.commit();

                                } else if (userType == 2) {
                                    FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                                    ft.replace(R.id.content_frame, new WeighingListFragment(), "HomeFragment");
                                    ft.commit();

                                } else {
                                    FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                                    ft.replace(R.id.content_frame, new WeighingListFragment(), "HomeFragment");
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
