package com.ats.shivshambhuweighingapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.ats.shivshambhuweighingapp.R;
import com.ats.shivshambhuweighingapp.constants.Constants;
import com.jsibbold.zoomage.ZoomageView;
import com.squareup.picasso.Picasso;

public class ViewImageActivity extends AppCompatActivity {

    private ZoomageView zoomageView;

    String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        zoomageView = findViewById(R.id.myZoomageView);

        try {

            image = Constants.IMAGE_PATH + "" + getIntent().getExtras().getString("image");
            Log.e("IMAGE PATH : ", " " + image);

            Picasso.get().load(image)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(zoomageView);

        } catch (Exception e) {
        }

    }
}
