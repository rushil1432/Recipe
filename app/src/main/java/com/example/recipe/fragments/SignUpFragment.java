package com.example.recipe.fragments;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.recipe.HomeActivity;
import com.example.recipe.MainActivity;
import com.example.recipe.R;
import com.example.recipe.app.AppConstants;
import com.example.recipe.app.EndPoints;
import com.example.recipe.helpers.AppHelper;
import com.example.recipe.helpers.SharedPrefManager;
import com.example.recipe.model.User;
import com.example.recipe.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment implements View.OnClickListener {

    View view;
    EditText edtUPhone, edtUEmail, edtUPassword;
    Button btnSignUp;
    String user_contact, user_email, user_password;
    private String TAG = MainActivity.class.getSimpleName();

    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        init();

        btnSignUp.setOnClickListener(this);

        return view;
    }

    private void init() {

        edtUPhone = (EditText) view.findViewById(R.id.editText_phone);
        edtUEmail = (EditText) view.findViewById(R.id.editText_email);
        edtUPassword = (EditText) view.findViewById(R.id.editText_password);
        btnSignUp = (Button) view.findViewById(R.id.btnNewAccount);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnNewAccount:
                Log.i(TAG, "button signup onclick method called...");
                checkValidations();
                break;


            default:
                break;

        }

    }

    private void checkValidations() {

        String str_email = edtUEmail.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[com]+";
        String MobilePattern = "[0-9]{10}";

        String[] parts = str_email.split("@");



         if (edtUEmail.getText().toString().trim().equalsIgnoreCase("")) {
            edtUEmail.setError("Required Email");
            edtUEmail.requestFocus();
            return;
        } else if (str_email.matches(emailPattern)) {

            if (parts[1].equals("gmail.com") || parts[1].equals("ymail.com") || parts[1].equals("yahoo.com") || parts[1].equals("rediffmail.com")) {

            } else {
                edtUEmail.setError("Invalid Email");
                edtUEmail.requestFocus();
                return;
            }
        } else {
            edtUEmail.setError("Invalid Email");
            edtUEmail.requestFocus();
            return;
        }
        if (edtUPassword.getText().toString().trim().equalsIgnoreCase("")) {
            edtUPassword.setError("Required Password.");
            edtUPassword.requestFocus();
            return;
        }
        else if (edtUPhone.getText().toString().trim().equalsIgnoreCase("")) {
            edtUPhone.setError("Required Mobile Number");
            edtUPhone.requestFocus();
            return;

        } else if (!edtUPhone.getText().toString().matches(MobilePattern)) {

            edtUPhone.setError("Please enter valid 10 digit phone number");
            edtUPhone.requestFocus();
            return;
        }

        else {
            if(AppHelper.isNetworkAvailable(getContext())){
                doRegistration();
                clear();
            }
            else{
                AppHelper.ErrorToast(getContext(),"No Internet Connection");
            }

        }
    }

    private void clear() {

        edtUPhone.setText("");
        edtUEmail.setText("");
        edtUPassword.setText("");

    }

    private void doRegistration() {

        user_contact=edtUPhone.getText().toString().trim();
        user_email=edtUEmail.getText().toString().trim();
        user_password=edtUPassword.getText().toString().trim();

        final AlertDialog dialog = new SpotsDialog(getContext(),R.style.CustomDialog);

        dialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.BASE_URL+"signup.php",
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

                                    //storing the user in shared preferences
                                    SharedPrefManager.getInstance(getContext()).userLogin(user);
                                }

                                startActivity(new Intent(getContext(),HomeActivity.class));
                                getActivity().finish();
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

                params.put(AppConstants.KEY_PHONE,user_contact);
                params.put(AppConstants.KEY_EMAIL,user_email);
                params.put(AppConstants.KEY_PWD,user_password);

                return params;
            }
        };

        VolleySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);



    }
}
