package com.example.recipe.fragments;


import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.recipe.HomeActivity;
import com.example.recipe.R;
import com.example.recipe.app.EndPoints;
import com.example.recipe.helpers.AppHelper;
import com.example.recipe.helpers.CustomTypeface;
import com.example.recipe.helpers.SharedPrefManager;
import com.example.recipe.model.User;
import com.example.recipe.volley.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedbackFragment extends Fragment {

    View view;
    User user;
    int user_id;
    String strDate,message;
    EditText edtMessage;
    Button btnFeedback;
    CustomTypeface customTypeface;
    SpannableString spannableString;

    public FeedbackFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_feedback, container, false);

        customTypeface=new CustomTypeface();
        spannableString=customTypeface.setTypeFace(getContext(),"Feedback");
        ((HomeActivity) getActivity()).setTitle(spannableString);


        initViews();

        user=SharedPrefManager.getInstance(getContext()).getUser();
        user_id=user.getUser_id();

        Date date=new Date();
        strDate= DateFormat.format("yyyy-MM-dd",date).toString();

        btnFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppHelper.isNetworkAvailable(getContext())) {

                    postFeedback();
                    clear();
                } else {

                    AppHelper.ErrorToast(getContext(), "No Internet Connection");
                }

            }
        });


        return view;
    }

    private void clear() {

        edtMessage.setText("");

    }

    private void postFeedback() {


        message=edtMessage.getText().toString();
        final AlertDialog dialog = new SpotsDialog(getActivity(),R.style.CustomDialog);

        dialog.show();

        StringRequest sRequest = new StringRequest(Request.Method.POST, EndPoints.BASE_URL+"add_feedback.php",
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


                            }else {
                                AppHelper.ErrorToast(getContext(),obj.getString("Message"));
                            }


                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // linearlayout_Progess.setVisibility(View.GONE);
                        dialog.dismiss();
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();


                params.put("message",message);
                params.put("date",strDate);
                params.put("user_id", String.valueOf(user_id));


                return params;
            }
        };


        VolleySingleton.getInstance(getContext()).addToRequestQueue(sRequest);


    }

    private void initViews() {


        edtMessage=view.findViewById(R.id.edtMsg);
        btnFeedback=view.findViewById(R.id.btnFeedback);

    }


}
