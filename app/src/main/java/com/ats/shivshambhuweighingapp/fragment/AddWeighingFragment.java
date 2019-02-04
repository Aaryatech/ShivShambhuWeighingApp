package com.ats.shivshambhuweighingapp.fragment;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ats.shivshambhuweighingapp.BuildConfig;
import com.ats.shivshambhuweighingapp.R;
import com.ats.shivshambhuweighingapp.activity.ViewImageActivity;
import com.ats.shivshambhuweighingapp.constants.Constants;
import com.ats.shivshambhuweighingapp.model.Contractor;
import com.ats.shivshambhuweighingapp.model.SubPlant;
import com.ats.shivshambhuweighingapp.model.User;
import com.ats.shivshambhuweighingapp.model.Vehicle;
import com.ats.shivshambhuweighingapp.model.Weighing;
import com.ats.shivshambhuweighingapp.util.CommonDialog;
import com.ats.shivshambhuweighingapp.util.CustomSharedPreference;
import com.ats.shivshambhuweighingapp.util.PermissionsUtil;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class AddWeighingFragment extends Fragment implements View.OnClickListener {

    private Spinner spVehicle, spPoklen, spContractor, spSubPlant;
    private EditText edContractorRate, edDate, edVehKm, edPoklenKM, edRemark, edQty, edRstNo;
    private TextView tvImage1, tvImage2;
    private Button btnSubmit;
    private ImageView ivCamera1, ivImage1, ivCamera2, ivImage2;
    private TextView tvDate, tvPhoto1Error, tvPhoto2Error;

    File folder = new File(Environment.getExternalStorageDirectory() + File.separator, "ShivShambhu");
    File f;

    Bitmap myBitmap1 = null, myBitmap2 = null;
    public static String path1, imagePath1, path2, imagePath2;

    ArrayList<String> vehicleNameList = new ArrayList<>();
    ArrayList<Integer> vehicleIdList = new ArrayList<>();

    ArrayList<String> subPlantNameList = new ArrayList<>();
    ArrayList<Integer> subPlantIdList = new ArrayList<>();

    ArrayList<String> poklenNameList = new ArrayList<>();
    ArrayList<Integer> poklenIdList = new ArrayList<>();

    ArrayList<String> contractorNameList = new ArrayList<>();
    ArrayList<Integer> contractorIdList = new ArrayList<>();

    long dateMillis;
    int yyyy, mm, dd;

    int weightId = 0;

    Weighing weighingModel = null;
    String photo1 = "", photo2 = "";

    User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_weighing, container, false);

        getActivity().setTitle("Add Weighing");

        if (PermissionsUtil.checkAndRequestPermissions(getActivity())) {
        }

        String userStr = CustomSharedPreference.getString(getActivity(), CustomSharedPreference.KEY_USER);
        Gson gson1 = new Gson();
        user = gson1.fromJson(userStr, User.class);

        spVehicle = view.findViewById(R.id.spVehicle);
        spSubPlant = view.findViewById(R.id.spSubPlant);
        spPoklen = view.findViewById(R.id.spPoklen);
        spContractor = view.findViewById(R.id.spContractor);
        edContractorRate = view.findViewById(R.id.edContractorRate);
        edQty = view.findViewById(R.id.edQty);
        edDate = view.findViewById(R.id.edDate);
        edVehKm = view.findViewById(R.id.edVehKM);
        edPoklenKM = view.findViewById(R.id.edPoklenKM);
        edRemark = view.findViewById(R.id.edRemark);
        tvImage1 = view.findViewById(R.id.tvImage1);
        tvImage2 = view.findViewById(R.id.tvImage2);
        ivCamera1 = view.findViewById(R.id.ivCamera1);
        ivImage1 = view.findViewById(R.id.ivImage1);
        ivCamera2 = view.findViewById(R.id.ivCamera2);
        ivImage2 = view.findViewById(R.id.ivImage2);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        tvDate = view.findViewById(R.id.tvDate);
        tvPhoto1Error = view.findViewById(R.id.tvPhoto1Error);
        tvPhoto2Error = view.findViewById(R.id.tvPhoto2Error);
        edRstNo = view.findViewById(R.id.edRstNo);

        btnSubmit.setOnClickListener(this);
        ivCamera1.setOnClickListener(this);
        ivCamera2.setOnClickListener(this);
        edDate.setOnClickListener(this);

        createFolder();

        getVehicleList(1);
        getPoklenList(3);
        getSubPlantList();
        getContractorList();
        // getContractorById();

        spContractor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    getContractorById(contractorIdList.get(i));
                } else {
                    edContractorRate.setText("0");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        try {
            String strWeight = getArguments().getString("model");
            Gson gson = new Gson();
            weighingModel = gson.fromJson(strWeight, Weighing.class);

            if (weighingModel != null) {

                weightId = weighingModel.getWeighId();

                String stringDate = weighingModel.getDate();
                DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                Date date = format.parse(stringDate);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                tvDate.setText("" + simpleDateFormat.format(date.getTime()));

                edDate.setText("" + weighingModel.getDate());
                edPoklenKM.setText("" + weighingModel.getPoklenKm());
                edVehKm.setText("" + weighingModel.getVehKm());
                edQty.setText("" + weighingModel.getQuantity());
                edRemark.setText("" + weighingModel.getRemark());
                edRstNo.setText("" + weighingModel.getExVar1());

                if (!weighingModel.getPhoto1().isEmpty()) {
                    photo1 = weighingModel.getPhoto1();

                    String image = Constants.IMAGE_PATH + "" + weighingModel.getPhoto1();
                    Picasso.get().load(image).into(ivImage1);

                    ivImage1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), ViewImageActivity.class);
                            intent.putExtra("image", "" + weighingModel.getPhoto1());
                            startActivity(intent);
                        }
                    });

                }

                if (!weighingModel.getPhoto2().isEmpty()) {
                    photo2 = weighingModel.getPhoto2();

                    String image = Constants.IMAGE_PATH + "" + weighingModel.getPhoto2();
                    Picasso.get().load(image).into(ivImage2);

                    ivImage2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), ViewImageActivity.class);
                            intent.putExtra("image", "" + weighingModel.getPhoto1());
                            startActivity(intent);
                        }
                    });
                }

                // ivCamera1.setVisibility(View.INVISIBLE);

                // Picasso.get().load(weighingModel.getPhoto1()).into(ivImage1);

                getVehicleList(1);
                getPoklenList(3);
                getSubPlantList();
                getContractorList();
            } else {

                ivCamera1.setVisibility(View.VISIBLE);

                getVehicleList(1);
                getPoklenList(3);
                getSubPlantList();
                getContractorList();

            }

        } catch (Exception e) {
            Log.e("ADD POKLEN READING", " ------- EXCEPTION " + e.getMessage());
            e.printStackTrace();
            getVehicleList(1);
        }


        return view;
    }


    public void createFolder() {
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ivCamera1) {
            showCameraDialog(1);

        } else if (view.getId() == R.id.ivCamera2) {
            showCameraDialog(2);

        } else if (view.getId() == R.id.edDate) {
            int yr, mn, dy;
            if (dateMillis > 0) {
                Calendar purchaseCal = Calendar.getInstance();
                purchaseCal.setTimeInMillis(dateMillis);
                yr = purchaseCal.get(Calendar.YEAR);
                mn = purchaseCal.get(Calendar.MONTH);
                dy = purchaseCal.get(Calendar.DAY_OF_MONTH);
            } else {
                Calendar purchaseCal = Calendar.getInstance();
                yr = purchaseCal.get(Calendar.YEAR);
                mn = purchaseCal.get(Calendar.MONTH);
                dy = purchaseCal.get(Calendar.DAY_OF_MONTH);
            }
            DatePickerDialog dialog = new DatePickerDialog(getContext(), dateListener, yr, mn, dy);
            dialog.show();


        } else if (view.getId() == R.id.btnSubmit) {

            String strConRate, strVehKM, strPoklenKM, strQty, strRemark, strDate, strRstNo;

            boolean isValidVeh = false, isValidPoklen = false, isValidContractor = false, isValidConRate = false, isValidQty = false, isValidDate = false, isValidVehKM = false, isValidSubPlant = false, isValidPoklenKM = false, isValidPhoto1 = false, isValidPhoto2 = false, isValidRst = false;


            strConRate = edContractorRate.getText().toString().trim();
            strQty = edQty.getText().toString().trim();
            // strVehKM = edVehKm.getText().toString().trim();
            strPoklenKM = edPoklenKM.getText().toString().trim();
            strRemark = edRemark.getText().toString().trim();
            strDate = tvDate.getText().toString().trim();
            strRstNo = edRstNo.getText().toString().trim();

            if (spVehicle.getSelectedItemPosition() == 0) {
                TextView spVehView = (TextView) spVehicle.getSelectedView();
                spVehView.setError("Required");
            } else {
                TextView spVehView = (TextView) spVehicle.getSelectedView();
                spVehView.setError(null);

                isValidVeh = true;
            }

            if (spPoklen.getSelectedItemPosition() == 0) {
                TextView spPokView = (TextView) spPoklen.getSelectedView();
                spPokView.setError("Required");
            } else {
                TextView spPokView = (TextView) spPoklen.getSelectedView();
                spPokView.setError(null);

                isValidPoklen = true;
            }

            if (spContractor.getSelectedItemPosition() == 0) {
                TextView spConView = (TextView) spContractor.getSelectedView();
                spConView.setError("Required");
            } else {
                TextView spConView = (TextView) spContractor.getSelectedView();
                spConView.setError(null);

                isValidContractor = true;
            }

            if (spSubPlant.getSelectedItemPosition() == 0) {
                TextView spSubPlantView = (TextView) spSubPlant.getSelectedView();
                spSubPlantView.setError("Required");
            } else {
                TextView spSubPlantView = (TextView) spSubPlant.getSelectedView();
                spSubPlantView.setError(null);

                isValidSubPlant = true;
            }

            if (strConRate.isEmpty()) {
                edContractorRate.setError("Required");
            } else {
                edContractorRate.setError(null);
                isValidConRate = true;
            }

            if (strQty.isEmpty()) {
                edQty.setError("Required");
            } else {
                edQty.setError(null);
                isValidQty = true;
            }

            if (strDate.isEmpty()) {
                edDate.setError("Required");
            } else {
                edDate.setError(null);
                isValidDate = true;
            }

            if (strRstNo.isEmpty()) {
                edRstNo.setError("Required");
            } else {
                edRstNo.setError(null);
                isValidRst = true;
            }

//            if (strVehKM.isEmpty()) {
//                edVehKm.setError("Required");
//            } else {
//                edVehKm.setError(null);
//                isValidVehKM = true;
//            }

         /*   if (strPoklenKM.isEmpty()) {
                edPoklenKM.setError("Required");
            } else {
                edPoklenKM.setError(null);
                isValidPoklenKM = true;
            }*/

            if (weighingModel != null) {
                isValidPhoto1 = true;
            } else {
                if (imagePath1 == null) {
                    tvPhoto1Error.setVisibility(View.VISIBLE);
                } else {
                    tvPhoto1Error.setVisibility(View.GONE);
                    isValidPhoto1 = true;
                }
            }

           /* if (imagePath2 == null) {
                tvPhoto2Error.setVisibility(View.VISIBLE);
            } else {
                tvPhoto2Error.setVisibility(View.GONE);
                isValidPhoto2 = true;
            }*/


            if (isValidVeh && isValidPoklen && isValidContractor && isValidQty && isValidDate && isValidSubPlant && isValidRst && isValidPhoto1) {

                int vehId = vehicleIdList.get(spVehicle.getSelectedItemPosition());
                int poklenId = poklenIdList.get(spPoklen.getSelectedItemPosition());
                int contId = contractorIdList.get(spContractor.getSelectedItemPosition());
                int subPlantId = subPlantIdList.get(spSubPlant.getSelectedItemPosition());

                float contRate = Float.parseFloat(strConRate);
                float qty = Float.parseFloat(strQty);
                // float vehKM = Float.parseFloat(strVehKM);
                //float pokKM = Float.parseFloat(strPoklenKM);


                if (imagePath1 == null) {
                    imagePath1 = "";
                }

                if (imagePath2 == null) {
                    imagePath2 = "";
                }

                Weighing weighing = new Weighing(weightId, vehId, poklenId, contId, contRate, qty, photo1, photo2, strRemark, user.getUserId(), strDate, 0, 1, subPlantId, strRstNo);

/*                if (imagePath1.isEmpty() && imagePath2.isEmpty()) {
                    if (weighingModel == null) {
                        Toast.makeText(getContext(), "Please upload image", Toast.LENGTH_SHORT).show();
                    }
                    Log.e("BOTH EMPTY", "--------------------------------------");
                } else */
                if (!imagePath1.isEmpty() && imagePath2.isEmpty()) {

                    File imgFile = new File(imagePath1);
                    int pos = imgFile.getName().lastIndexOf(".");
                    String ext = imgFile.getName().substring(pos + 1);
                    photo1 = System.currentTimeMillis() + "." + ext;

                    /*if (weighingModel != null) {
                        photo1 = weighingModel.getPhoto1();
                    }*/

                    weighing.setPhoto1(photo1);

                    sendImageWithData(photo1, "w", 1, weighing);
                    Log.e("2 EMPTY", "--------------------------------------");

                } else if (imagePath1.isEmpty() && !imagePath2.isEmpty()) {

                    File imgFile = new File(imagePath2);
                    int pos = imgFile.getName().lastIndexOf(".");
                    String ext = imgFile.getName().substring(pos + 1);
                    photo2 = System.currentTimeMillis() + "." + ext;

                    /*if (weighingModel != null) {
                        photo2 = weighingModel.getPhoto2();
                    }*/

                    weighing.setPhoto2(photo2);

                    sendImageWithData(photo2, "w", 2, weighing);
                    Log.e("1 EMPTY", "--------------------------------------");

                } else if (!imagePath1.isEmpty() && !imagePath2.isEmpty()) {

                    File imgFile = new File(imagePath1);
                    int pos = imgFile.getName().lastIndexOf(".");
                    String ext = imgFile.getName().substring(pos + 1);
                    photo1 = System.currentTimeMillis() + "." + ext;

                    File imgFile2 = new File(imagePath2);
                    int pos2 = imgFile2.getName().lastIndexOf(".");
                    String ext2 = imgFile2.getName().substring(pos2 + 1);
                    photo2 = System.currentTimeMillis() + "." + ext2;

                    sendImages(photo1, "w", photo2, weighing);
                    Log.e("NOT EMPTY", "--------------------------------------");


                } else {
                    saveWeighing(weighing);
                }

            }


        }

    }

    DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            yyyy = year;
            mm = month + 1;
            dd = dayOfMonth;
            edDate.setText(dd + "-" + mm + "-" + yyyy);
            tvDate.setText(yyyy + "-" + mm + "-" + dd);

            Calendar calendar = Calendar.getInstance();
            calendar.set(yyyy, mm - 1, dd);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR, 0);
            dateMillis = calendar.getTimeInMillis();
        }
    };

    public void showCameraDialog(final int sequence) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                f = new File(folder + File.separator, "Camera.jpg");

                String authorities = BuildConfig.APPLICATION_ID + ".provider";
                Uri imageUri = FileProvider.getUriForFile(getContext(), authorities, f);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                if (sequence == 1) {
                    startActivityForResult(intent, 102);
                } else if (sequence == 2) {
                    startActivityForResult(intent, 202);
                }

            } else {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                f = new File(folder + File.separator, "Camera.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                if (sequence == 1) {
                    startActivityForResult(intent, 102);
                } else if (sequence == 2) {
                    startActivityForResult(intent, 202);
                }

            }
        } catch (Exception e) {
            Log.e("select camera : ", " Exception : " + e.getMessage());
        }


    }


    //--------------------------IMAGE-----------------------------------------

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String realPath;
        Bitmap bitmap = null;

        if (resultCode == RESULT_OK && requestCode == 102) {
            try {
                String path = f.getAbsolutePath();
                File imgFile = new File(path);
                if (imgFile.exists()) {
                    myBitmap1 = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    ivImage1.setImageBitmap(myBitmap1);

                    myBitmap1 = shrinkBitmap(imgFile.getAbsolutePath(), 720, 720);

                    try {
                        FileOutputStream out = new FileOutputStream(path);
                        myBitmap1.compress(Bitmap.CompressFormat.PNG, 100, out);
                        out.flush();
                        out.close();
                        Log.e("Image Saved  ", "---------------");

                    } catch (Exception e) {
                        Log.e("Exception : ", "--------" + e.getMessage());
                        e.printStackTrace();
                    }
                }
                imagePath1 = f.getAbsolutePath();
                tvImage1.setText("" + f.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (resultCode == RESULT_OK && requestCode == 202) {
            try {
                String path = f.getAbsolutePath();
                File imgFile = new File(path);
                if (imgFile.exists()) {
                    myBitmap2 = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    ivImage2.setImageBitmap(myBitmap2);

                    myBitmap2 = shrinkBitmap(imgFile.getAbsolutePath(), 720, 720);

                    try {
                        FileOutputStream out = new FileOutputStream(path);
                        myBitmap2.compress(Bitmap.CompressFormat.PNG, 100, out);
                        out.flush();
                        out.close();
                        Log.e("Image Saved  ", "---------------");

                    } catch (Exception e) {
                        Log.e("Exception : ", "--------" + e.getMessage());
                        e.printStackTrace();
                    }
                }
                imagePath2 = f.getAbsolutePath();
                tvImage2.setText("" + f.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public static Bitmap shrinkBitmap(String file, int width, int height) {
        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);

        int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) height);
        int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) width);

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                bmpFactoryOptions.inSampleSize = heightRatio;
            } else {
                bmpFactoryOptions.inSampleSize = widthRatio;
            }
        }

        bmpFactoryOptions.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);
        return bitmap;
    }

    //-----------------------------------------------------------------------------
    private void getSubPlantList() {
        if (Constants.isOnline(getContext())) {
            final CommonDialog commonDialog = new CommonDialog(getContext(), "Loading", "Please Wait...");
            commonDialog.show();

            Call<ArrayList<SubPlant>> listCall = Constants.myInterface.getAllSubPlantList();
            listCall.enqueue(new Callback<ArrayList<SubPlant>>() {
                @Override
                public void onResponse(Call<ArrayList<SubPlant>> call, Response<ArrayList<SubPlant>> response) {
                    try {
                        if (response.body() != null) {

                            Log.e("VEHICLE LIST : ", " - " + response.body());
                            subPlantNameList.clear();
                            subPlantIdList.clear();

                            subPlantIdList.add(0);
                            subPlantNameList.add("Select Sub Plant");

                            if (response.body().size() > 0) {
                                for (int i = 0; i < response.body().size(); i++) {
                                    subPlantNameList.add(response.body().get(i).getSubplantName());
                                    subPlantIdList.add(response.body().get(i).getSubplantId());
                                }
                            }

                            ArrayAdapter<String> vehAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, subPlantNameList);
                            spSubPlant.setAdapter(vehAdapter);

                            if (weighingModel != null) {
                                int pos = 0;
                                for (int i = 0; i < subPlantIdList.size(); i++) {
                                    if (subPlantIdList.get(i) == weighingModel.getExInt1()) {
                                        pos = i;
                                        break;
                                    }
                                }
                                spSubPlant.setSelection(pos);
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
                public void onFailure(Call<ArrayList<SubPlant>> call, Throwable t) {
                    commonDialog.dismiss();
                    Log.e("onFailure : ", "-----------" + t.getMessage());
                    t.printStackTrace();
                }
            });
        } else {
            Toast.makeText(getContext(), "No Internet Connection !", Toast.LENGTH_SHORT).show();
        }
    }

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
                            vehicleNameList.add("Select Vehicle");

                            if (response.body().size() > 0) {
                                for (int i = 0; i < response.body().size(); i++) {
                                    vehicleNameList.add(response.body().get(i).getVehicleName());
                                    vehicleIdList.add(response.body().get(i).getVehicleId());
                                }
                            }

                            ArrayAdapter<String> vehAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, vehicleNameList);
                            spVehicle.setAdapter(vehAdapter);

                            if (weighingModel != null) {
                                int pos = 0;
                                for (int i = 0; i < vehicleIdList.size(); i++) {
                                    if (vehicleIdList.get(i) == weighingModel.getVehId()) {
                                        pos = i;
                                        break;
                                    }
                                }

                                spVehicle.setSelection(pos);
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

    public void getPoklenList(int type) {
        if (Constants.isOnline(getContext())) {
            final CommonDialog commonDialog = new CommonDialog(getContext(), "Loading", "Please Wait...");
            commonDialog.show();

            Call<ArrayList<Vehicle>> listCall = Constants.myInterface.getVehiclelistByType(type);
            listCall.enqueue(new Callback<ArrayList<Vehicle>>() {
                @Override
                public void onResponse(Call<ArrayList<Vehicle>> call, Response<ArrayList<Vehicle>> response) {
                    try {
                        if (response.body() != null) {

                            Log.e("POKLEN LIST : ", " - " + response.body());
                            poklenNameList.clear();
                            poklenIdList.clear();

                            poklenIdList.add(0);
                            poklenNameList.add("Select Poklen");

                            if (response.body().size() > 0) {
                                for (int i = 0; i < response.body().size(); i++) {
                                    poklenNameList.add(response.body().get(i).getVehicleName());
                                    poklenIdList.add(response.body().get(i).getVehicleId());
                                }
                            }

                            ArrayAdapter<String> poklenAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, poklenNameList);
                            spPoklen.setAdapter(poklenAdapter);

                            if (weighingModel != null) {
                                int pos = 0;
                                for (int i = 0; i < poklenIdList.size(); i++) {
                                    if (poklenIdList.get(i) == weighingModel.getPoklenId()) {
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

    public void getContractorList() {
        if (Constants.isOnline(getContext())) {
            final CommonDialog commonDialog = new CommonDialog(getContext(), "Loading", "Please Wait...");
            commonDialog.show();

            Call<ArrayList<Contractor>> listCall = Constants.myInterface.getContractorList();
            listCall.enqueue(new Callback<ArrayList<Contractor>>() {
                @Override
                public void onResponse(Call<ArrayList<Contractor>> call, Response<ArrayList<Contractor>> response) {
                    try {
                        if (response.body() != null) {

                            Log.e("CONTRACTOR LIST : ", " - " + response.body());
                            contractorIdList.clear();
                            contractorNameList.clear();

                            contractorIdList.add(0);
                            contractorNameList.add("Select Contractor");

                            if (response.body().size() > 0) {
                                for (int i = 0; i < response.body().size(); i++) {
                                    contractorNameList.add(response.body().get(i).getContrName());
                                    contractorIdList.add(response.body().get(i).getContrId());
                                }
                            }

                            ArrayAdapter<String> contractorAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, contractorNameList);
                            spContractor.setAdapter(contractorAdapter);

                            if (weighingModel != null) {
                                int pos = 0;
                                for (int i = 0; i < contractorIdList.size(); i++) {
                                    if (contractorIdList.get(i) == weighingModel.getContraId()) {
                                        pos = i;
                                        break;
                                    }
                                }

                                spContractor.setSelection(pos);
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

    public void getContractorById(int conId) {
        if (Constants.isOnline(getContext())) {
            final CommonDialog commonDialog = new CommonDialog(getContext(), "Loading", "Please Wait...");
            commonDialog.show();

            Call<Contractor> listCall = Constants.myInterface.getContractorById(conId);
            listCall.enqueue(new Callback<Contractor>() {
                @Override
                public void onResponse(Call<Contractor> call, Response<Contractor> response) {
                    try {
                        if (response.body() != null) {

                            Log.e("CONTRACTOR : ", " - " + response.body());

                            edContractorRate.setText("" + response.body().getContrRate());

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
                public void onFailure(Call<Contractor> call, Throwable t) {
                    commonDialog.dismiss();
                    Log.e("onFailure : ", "-----------" + t.getMessage());
                    t.printStackTrace();
                }
            });
        } else {
            Toast.makeText(getContext(), "No Internet Connection !", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveWeighing(Weighing weighing) {

        Log.e("PARAMETER : ", "--------------- " + weighing);

        if (Constants.isOnline(getContext())) {
            final CommonDialog commonDialog = new CommonDialog(getContext(), "Loading", "Please Wait...");
            commonDialog.show();

            Call<Weighing> listCall = Constants.myInterface.saveWeighing(weighing);
            listCall.enqueue(new Callback<Weighing>() {
                @Override
                public void onResponse(Call<Weighing> call, Response<Weighing> response) {
                    try {
                        if (response.body() != null) {

                            Log.e("SAVE WEIGHING : ", " - " + response.body());

                            if (response.body().getWeighId() != 0) {
                                int userType = CustomSharedPreference.getInt(getActivity(), CustomSharedPreference.KEY_USER_TYPE);
                                if (userType == 1) {
                                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                    ft.replace(R.id.content_frame, new WeighingListFragment(), "Exit");
                                    ft.commit();

                                } else if (userType == 2) {
                                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                    ft.replace(R.id.content_frame, new WeighingListFragment(), "Exit");
                                    ft.commit();

                                } else {
                                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                    ft.replace(R.id.content_frame, new WeighingListFragment(), "Exit");
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
                public void onFailure(Call<Weighing> call, Throwable t) {
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

    private void sendImageWithData(String filename, String type, final int sequence, final Weighing weighing) {
        final CommonDialog commonDialog = new CommonDialog(getContext(), "Loading", "Please Wait...");
        commonDialog.show();

        Log.e("PARAMETER : -------- ", "FILENAME : " + filename + "        TYPE : " + type + "            SEQUENCE : " + sequence + "             WEIGHING : " + weighing);

        File imgFile = null;

        if (sequence == 1) {
            imgFile = new File(imagePath1);
        } else if (sequence == 2) {
            imgFile = new File(imagePath2);
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imgFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", imgFile.getName(), requestFile);

        RequestBody imgName = RequestBody.create(MediaType.parse("text/plain"), filename);
        RequestBody imgType = RequestBody.create(MediaType.parse("text/plain"), type);

        Call<JSONObject> call = Constants.myInterface.imageUpload(body, imgName, imgType);
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                commonDialog.dismiss();

                saveWeighing(weighing);

                if (sequence == 1) {
                    imagePath1 = "";
                } else if (sequence == 2) {
                    imagePath2 = "";
                }
                Log.e("Response : ", "--" + response.body());
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Log.e("Error : ", "--" + t.getMessage());
                commonDialog.dismiss();

                t.printStackTrace();
                Toast.makeText(getContext(), "Unable To Process", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendImages(String filename1, final String type, final String filename2, final Weighing weighing) {
        final CommonDialog commonDialog = new CommonDialog(getContext(), "Loading", "Please Wait...");
        commonDialog.show();

        File imgFile = new File(imagePath1);

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imgFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", imgFile.getName(), requestFile);

        RequestBody imgName = RequestBody.create(MediaType.parse("text/plain"), filename1);
        RequestBody imgType = RequestBody.create(MediaType.parse("text/plain"), type);

        Call<JSONObject> call = Constants.myInterface.imageUpload(body, imgName, imgType);
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                commonDialog.dismiss();

                sendImageWithData(filename2, type, 2, weighing);

                imagePath1 = "";
                Log.e("Response : ", "--" + response.body());
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Log.e("Error : ", "--" + t.getMessage());
                commonDialog.dismiss();
                t.printStackTrace();
                Toast.makeText(getContext(), "Unable To Process", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
