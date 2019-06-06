package com.example.recipe.fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.recipe.HomeActivity;
import com.example.recipe.R;
import com.example.recipe.app.AppConstants;
import com.example.recipe.app.EndPoints;
import com.example.recipe.helpers.AppHelper;
import com.example.recipe.helpers.CustomTypeface;
import com.example.recipe.helpers.SharedPrefManager;
import com.example.recipe.model.User;
import com.example.recipe.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    View view;
    EditText ediitext_email,ediitext_firstname,ediitext_middlename,ediitext_lastname,ediitext_address,ediitext_phone;
    ImageView ProfileImageView;
    TextView textView_changePhoto;
    String user_email,first_name,middle_name,last_name,user_address,user_phone;
    User user;
    ProgressBar progressBar;
    private int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 123;
    private Bitmap bitmap;
    private Uri filePath;
    RequestOptions requestOptions;
    CustomTypeface customTypeface;
    SpannableString spannableString;





    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_profile, container, false);

        customTypeface=new CustomTypeface();
        spannableString=customTypeface.setTypeFace(getContext(),"Profile");
        ((HomeActivity) getActivity()).setTitle(spannableString);

        setHasOptionsMenu(true);


        init();
        progressBar.setVisibility(View.INVISIBLE);
        user= SharedPrefManager.getInstance(getContext()).getUser();

        ediitext_email.setText(user.getEmail());
        ediitext_firstname.setText(user.getFname());
        ediitext_middlename.setText(user.getMname());
        ediitext_lastname.setText(user.getLname());
        ediitext_address.setText(user.getAddress());
        ediitext_phone.setText(user.getPhone());

        requestOptions=new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_account_circle_white)
                .error(R.drawable.ic_account_circle_white)
                .diskCacheStrategy(DiskCacheStrategy.ALL);


            Glide.with(getContext()).load(EndPoints.PROFILE_URL+user.getImage())
                    .thumbnail(0.5f)
                    .apply(requestOptions)
                    .into(ProfileImageView);



        textView_changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // progressBar.setVisibility(View.VISIBLE);
                requestStoragePermission();
                showFileChooser();
            }
        });


        return view;
    }

    private void checkValidations() {

        String MobilePattern = "[0-9]{10}";

        if (ediitext_phone.getText().toString().trim().equalsIgnoreCase("")) {
            ediitext_phone.setError("Required Phone Number");
            ediitext_phone.requestFocus();
            return;

        } else if (!ediitext_phone.getText().toString().matches(MobilePattern)) {

            ediitext_phone.setError("Please enter valid 10 digit phone number");
            ediitext_phone.requestFocus();
            return;
        }

        else {
            if(AppHelper.isNetworkAvailable(getContext())){
                saveChanges();

            }
            else{
                AppHelper.ErrorToast(getContext(),"No Internet Connection");
            }

        }
    }

    private void saveChanges() {

        first_name=ediitext_firstname.getText().toString().trim();
        user_phone=ediitext_phone.getText().toString().trim();
        middle_name=ediitext_middlename.getText().toString().trim();
        last_name=ediitext_lastname.getText().toString().trim();
        user_address=ediitext_address.getText().toString().trim();
        user_email=ediitext_email.getText().toString().trim();

        final AlertDialog dialog = new SpotsDialog(getContext(),R.style.CustomDialog);

        dialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.BASE_URL+"update_profile.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();

                        try {

                            JSONObject obj = new JSONObject(response);

                            Log.d("response",response.toString());

                            //if no error in response
                            if (obj.getBoolean("Status")) {

                                AppHelper.SuccessToast(getContext(),obj.getString("Message"));

                                //getting the user from the response
                                JSONArray userJson = obj.getJSONArray("response");

                                //creating a new user object
                                for (int i = 0; i < userJson.length(); i++) {
                                    JSONObject jsonObj = userJson.getJSONObject(i);
                                    User user = new User(

                                            jsonObj.getInt("user_id"),
                                            jsonObj.getString("first_name"),
                                            jsonObj.getString("middle_name"),
                                            jsonObj.getString("last_name"),
                                            jsonObj.getString("address"),
                                            jsonObj.getString("user_contact"),
                                            jsonObj.getString("user_email"),
                                            jsonObj.getString("user_password"),
                                            jsonObj.getString("image")
                                    );

                                    SharedPrefManager.getInstance(getContext()).clear();
                                    //storing the user in shared preferences
                                    SharedPrefManager.getInstance(getContext()).userLogin(user);
                                }


                            } else {
                                AppHelper.InfoToast(getContext(),obj.getString("Message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AppHelper.ErrorToast(getContext(),error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(AppConstants.KEY_FNAME,first_name);
                params.put(AppConstants.KEY_PHONE,user_phone);
                params.put(AppConstants.KEY_MNAME,middle_name);
                params.put(AppConstants.KEY_LNAME,last_name);
                params.put(AppConstants.KEY_ADDRESS,user_address);
                params.put(AppConstants.KEY_EMAIL,user_email);


                return params;
            }
        };

        VolleySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);



    }

    private void requestStoragePermission() {

        if (AppHelper.checkAllPermissions(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
            AppHelper.LogCat("READ_EXTERNAL_STORAGE permission already granted.");
        }
        else
        {
            AppHelper.LogCat("Please request READ_EXTERNAL_STORAGE permission.");
            AppHelper.requestPermission(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                ProfileImageView.setImageBitmap(bitmap);
                uploadPic();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadPic() {

        final int user_id=user.getUser_id();
        final String imagePath=getStringImage(bitmap);

        progressBar.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.BASE_URL+"update_profile_pic.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressBar.setVisibility(View.INVISIBLE);

                        try {

                            JSONObject obj = new JSONObject(response);

                            Log.d("response",response.toString());

                            //if no error in response
                            if (obj.getBoolean("Status")) {
                                AppHelper.SuccessToast(getContext(),obj.getString("Message"));

                                //getting the user from the response
                                JSONArray userJson = obj.getJSONArray("response");

                                for (int i = 0; i < userJson.length(); i++) {
                                    JSONObject jsonObj = userJson.getJSONObject(i);
                                    {
                                        User user = new User(

                                                jsonObj.getInt("user_id"),
                                                jsonObj.getString("first_name"),
                                                jsonObj.getString("middle_name"),
                                                jsonObj.getString("last_name"),
                                                jsonObj.getString("address"),
                                                jsonObj.getString("user_contact"),
                                                jsonObj.getString("user_email"),
                                                jsonObj.getString("user_password"),
                                                jsonObj.getString("image")
                                        );




                                        //storing the user in shared preferences
                                       SharedPrefManager.getInstance(getContext()).clear();
                                       SharedPrefManager.getInstance(getContext()).userLogin(user);
                                       user=SharedPrefManager.getInstance(getContext()).getUser();


                                        Glide.with(getContext()).load(user.getImage())
                                                .thumbnail(0.5f)
                                                .apply(requestOptions)
                                                .into(ProfileImageView);

                                    }





                                }
                            }else {
                                AppHelper.InfoToast(getContext(),obj.getString("Message"));
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AppHelper.ErrorToast(getContext(),error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put(AppConstants.KEY_USER_ID, String.valueOf(user_id));
                params.put(AppConstants.KEY_IMAGE,imagePath);

                return params;
            }
        };

        VolleySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);


    }



    public String getStringImage(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imagebytes=byteArrayOutputStream.toByteArray();
        String encodedImage= Base64.encodeToString(imagebytes,Base64.DEFAULT);
        return  encodedImage;
    }

    private void init() {
        ProfileImageView = (ImageView)view.findViewById(R.id.imageView_Profile);
        textView_changePhoto = (TextView) view.findViewById(R.id.textView_ChangePhoto);
        ediitext_email=(EditText)view.findViewById(R.id.ediitext_email);
        ediitext_firstname=(EditText)view.findViewById(R.id.ediitext_firstname);
        ediitext_middlename=(EditText)view.findViewById(R.id.ediitext_middlename);
        ediitext_lastname=(EditText)view.findViewById(R.id.ediitext_lastname);
        ediitext_address=(EditText)view.findViewById(R.id.ediitext_address);
        ediitext_phone=(EditText)view.findViewById(R.id.ediitext_phone);
        progressBar=(ProgressBar)view.findViewById(R.id.progressBar);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_save, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {

            case R.id.action_save:

               checkValidations();
                return  true;


            default:
                return super.onOptionsItemSelected(item);
        }


    }
}
