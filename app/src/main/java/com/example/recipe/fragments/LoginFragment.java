package com.example.recipe.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
public class LoginFragment extends Fragment implements View.OnClickListener {


    EditText edtEmail,edtPassword;
    Button btnLogin;
    TextView link_forgotpwd;
    private String TAG = MainActivity.class.getSimpleName();
    private String email, password;
    View view;
    Dialog mydialog;
    EditText editREmail;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_login, container, false);

        mydialog=new Dialog(getContext());
        init();

        btnLogin.setOnClickListener(this);
        link_forgotpwd.setOnClickListener(this);

        return view;
    }

    private void init() {

        edtEmail=(EditText)view.findViewById(R.id.edtEmail);
        edtPassword=(EditText)view. findViewById(R.id.edtPassword);
        btnLogin=(Button)view.findViewById(R.id.btnLogin);
        link_forgotpwd=(TextView)view.findViewById(R.id.link_forgotpwd);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btnLogin:
                Log.i(TAG, "button login onclick method called...");
                checkValidations();
                break;

            case R.id.link_forgotpwd:
                Log.i(TAG, "link forgot password onclick method called...");

                showPopup(v);
                break;

            default:
                break;

        }
    }

    public  void  showPopup(View v) {

        Button buttonSubmit, buttonCancel;

        mydialog.setContentView(R.layout.custom_popup);
        buttonSubmit = (Button) mydialog.findViewById(R.id.buttonSubmit);
        buttonCancel = (Button) mydialog.findViewById(R.id.buttonCancel);
        editREmail = (EditText) mydialog.findViewById(R.id.edtResetEmail);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String Uemail = editREmail.getText().toString().trim();

                if (editREmail.getText().toString().equalsIgnoreCase("")) {

                    editREmail.setError("Required Email");
                    editREmail.requestFocus();
                    return;
                } else {

                    final AlertDialog dialog = new SpotsDialog(v.getContext(), R.style.CustomDialog);

                    dialog.show();


                    StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.BASE_URL + "forgotpassword.php",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    dialog.dismiss();


                                    try {

                                        JSONObject obj = new JSONObject(response);

                                        Log.d("response", response.toString());

                                        //if no error in response
                                        if (obj.getBoolean("Status")) {


                                            Toast.makeText(getContext(), obj.getString("Message"), Toast.LENGTH_LONG).show();


                                        } else {

                                            Toast.makeText(getContext(), obj.getString("Message"), Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();

                            params.put("email", Uemail);
                            return params;
                        }
                    };

                    VolleySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);

                    dialog.dismiss();
                    mydialog.dismiss();



                }

            }        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mydialog.dismiss();
            }
        });
        mydialog.show();
    }

    private void checkValidations() {

        Log.i(TAG, "checkValidations method called...");

        email = edtEmail.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[com]+";


        String[] parts = email.split("@");

        if (edtEmail.getText().toString().trim().equalsIgnoreCase("")) {
            edtEmail.setError("Required Email");
            edtEmail.requestFocus();
            return;
        } else if (email.matches(emailPattern)) {

            if (parts[1].equals("gmail.com") || parts[1].equals("ymail.com") || parts[1].equals("yahoo.com") || parts[1].equals("rediffmail.com")) {
            } else {
                edtEmail.setError("Invalid Email");
                edtEmail.requestFocus();
                return;
            }

        } else {
            edtEmail.setError("Invalid Email");
            edtEmail.requestFocus();
            return;
        }

        if (edtPassword.getText().toString().trim().equalsIgnoreCase("")) {
            edtPassword.setError("Required Password.");
            edtPassword.requestFocus();
            return;
        } else {

            if(AppHelper.isNetworkAvailable(getContext())){
            doLogin();
            clear();
            }
            else{
                AppHelper.ErrorToast(getContext(),"No Internet Connection");
            }
        }
    }

    private void doLogin() {

        email=edtEmail.getText().toString().trim();
        password=edtPassword.getText().toString().trim();

        final AlertDialog dialog = new SpotsDialog(getContext(),R.style.CustomDialog);

        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.BASE_URL+"login.php",
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
                                        SharedPrefManager.getInstance(getContext()).userLogin(user);
                                    }

                                   startActivity(new Intent(getContext(), HomeActivity.class));
                                    getActivity().finish();


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

                params.put(AppConstants.KEY_EMAIL,email);
                params.put(AppConstants.KEY_PWD,password);

                return params;
            }
        };

        VolleySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);


    }

    private void clear(){

        edtEmail.setText("");
        edtPassword.setText("");
    }


}
