package com.example.recipe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.recipe.adapter.RateAdapter;
import com.example.recipe.app.EndPoints;
import com.example.recipe.helpers.AppHelper;
import com.example.recipe.helpers.CustomTypeface;
import com.example.recipe.helpers.SharedPrefManager;
import com.example.recipe.model.RatingModel;
import com.example.recipe.model.User;
import com.example.recipe.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RatingActivity extends AppCompatActivity {

    SpannableString spannableString;
    int recipe_id;
    String recipe_name,user_image,comments,rate,current_date;
    User user;
    TextView txtRName;
    ImageView imageView_UserProfile;
    RequestOptions requestOptions;
    int user_id;
    EditText edtComments;
    Button btnRate;
    RatingBar ratingBar;
    RecyclerView recyclerView_comments;
    List<RatingModel> ratingModelList;
    RateAdapter rateAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        spannableString = new CustomTypeface().setTypeFace(RatingActivity.this, "Comments");
        getSupportActionBar().setTitle(spannableString);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recipe_name=getIntent().getStringExtra("recipe_name");
        recipe_id=getIntent().getIntExtra("recipe_id",0);
        user=SharedPrefManager.getInstance(getApplicationContext()).getUser();
        user_image=user.getImage();
        user_id=user.getUser_id();

        initViews();
        txtRName.setText(recipe_name);
        requestOptions=new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_account_circle_white)
                .error(R.drawable.ic_account_circle_white)
                .diskCacheStrategy(DiskCacheStrategy.ALL);


        Glide.with(this).load(EndPoints.PROFILE_URL+user.getImage())
                .thumbnail(0.5f)
                .apply(requestOptions)
                .into(imageView_UserProfile);

        Date date=new Date();
        current_date= DateFormat.format("yyyy-MM-dd",date).toString();

        if (AppHelper.isNetworkAvailable(RatingActivity.this)){

           getRecipeRating();


        }
        else{

            AppHelper.ErrorToast(RatingActivity.this,"No Internet Connection");
        }

        btnRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppHelper.isNetworkAvailable(RatingActivity.this)){

                    addRecipeRating();
                    edtComments.setText("");

                }
                else{

                    AppHelper.ErrorToast(RatingActivity.this,"No Internet Connection");
                }

            }
        });

    }

    private void getRecipeRating() {

        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, EndPoints.BASE_URL+"get_rating.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject obj = new JSONObject(response);

                            Log.d("response",response.toString());

                            //if no error in response
                            if (obj.getBoolean("Status")) {

                                JSONArray userJson = obj.getJSONArray("response");

                                for (int i = 0; i < userJson.length(); i++) {
                                    JSONObject jsonObj = userJson.getJSONObject(i);
                                    {

                                        RatingModel ratingModel=new RatingModel();
                                        ratingModel.setRate_id(jsonObj.getInt("rate_id"));
                                        ratingModel.setRecipe_id(jsonObj.getInt("recipe_id"));
                                        ratingModel.setRate(jsonObj.getString("rate"));
                                        ratingModel.setComments(jsonObj.getString("comment"));
                                        ratingModel.setFirst_name(jsonObj.getString("first_name"));
                                        ratingModel.setLast_name(jsonObj.getString("last_name"));
                                        ratingModel.setImage(jsonObj.getString("image"));
                                        ratingModel.setDate(jsonObj.getString("date"));

                                        ratingModelList.add(ratingModel);

                                    }

                                    rateAdapter=new RateAdapter(RatingActivity.this,ratingModelList);
                                    recyclerView_comments.setAdapter(rateAdapter);


                                }

                            }else {


                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("recipe_id", String.valueOf(recipe_id));
                return params;
            }
        };


        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest2);


    }

    private void addRecipeRating() {

        rate= String.valueOf(ratingBar.getRating());
        comments=edtComments.getText().toString().trim();

        StringRequest stringRequest1 = new StringRequest(Request.Method.POST, EndPoints.BASE_URL+"add_rating.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject obj = new JSONObject(response);

                            Log.d("response",response.toString());

                            //if no error in response
                            if (obj.getBoolean("Status")) {
                                //    Toast.makeText(getContext(), obj.getString("Message"), Toast.LENGTH_SHORT).show();
                                AppHelper.SuccessToast(getApplicationContext(),obj.getString("Message"));

                            }else {

                                Toast.makeText(getApplicationContext(), obj.getString("Message"), Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("recipe_id", String.valueOf(recipe_id));
                params.put("comments",comments);
                params.put("rate",rate);
                params.put("date", current_date);
                params.put("user_id", String.valueOf(user_id));
                return params;
            }
        };


        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest1);



    }

    private void initViews() {

        imageView_UserProfile=(ImageView)findViewById(R.id.imageView_UserProfile);
        txtRName=findViewById(R.id.txtRName);
        edtComments=findViewById(R.id.edtComments);
        btnRate=findViewById(R.id.btnRate);
        ratingBar=findViewById(R.id.ratingBar);
        recyclerView_comments=findViewById(R.id.recyclerView_comments);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView_comments.setLayoutManager(layoutManager);
        ratingModelList=new ArrayList<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;



        }

        return super.onOptionsItemSelected(item);
    }
}
