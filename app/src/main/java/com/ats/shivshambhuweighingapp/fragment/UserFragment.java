package com.ats.shivshambhuweighingapp.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ats.shivshambhuweighingapp.R;
import com.ats.shivshambhuweighingapp.model.User;
import com.ats.shivshambhuweighingapp.util.CustomSharedPreference;
import com.google.gson.Gson;

public class UserFragment extends Fragment {

    private TextView tvName, tvPwd, tvMobile, tvEmail;
    private RadioButton rbReadingUser, rbWeighingUser;

    User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        getActivity().setTitle("User");

        tvName = view.findViewById(R.id.tvName);
        tvPwd = view.findViewById(R.id.tvPwd);
        tvMobile = view.findViewById(R.id.tvMobile);
        tvEmail = view.findViewById(R.id.tvEmail);

        rbReadingUser = view.findViewById(R.id.rbReadingUser);
        rbWeighingUser = view.findViewById(R.id.rbWeighingUser);

        String userStr = CustomSharedPreference.getString(getActivity(), CustomSharedPreference.KEY_USER);
        Gson gson = new Gson();
        user = gson.fromJson(userStr, User.class);
        Log.e("USER FRAGMENT : ", "--------USER-------" + user);

        if (user != null) {

            tvName.setText("" + user.getUsrName());
            tvPwd.setText("" + user.getUserPass());
            tvMobile.setText("" + user.getUsrMob());
            tvEmail.setText("" + user.getUsrEmail());

        }

        int userType = CustomSharedPreference.getInt(getActivity(), CustomSharedPreference.KEY_USER_TYPE);
        if (userType == 1) {
            rbReadingUser.setChecked(true);
        } else if (userType == 2) {
            rbWeighingUser.setChecked(true);
        }

        rbReadingUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    CustomSharedPreference.putInt(getActivity(), CustomSharedPreference.KEY_USER_TYPE, 1);
                }
            }
        });

        rbWeighingUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    CustomSharedPreference.putInt(getActivity(), CustomSharedPreference.KEY_USER_TYPE, 2);
                }
            }
        });

        return view;
    }

}
