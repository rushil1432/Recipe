package com.example.recipe;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.recipe.app.EndPoints;
import com.example.recipe.helpers.AppHelper;
import com.example.recipe.volley.VolleySingleton;
import com.sackcentury.shinebuttonlib.ShineButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import bg.devlabs.fullscreenvideoview.FullscreenVideoView;
import dmax.dialog.SpotsDialog;

public class MyRecipeDetailsActivity extends AppCompatActivity {

    TextView txtPreTime,txtCookTime,txtrecipe_description,txtServings;
    int recipe_id;
    LinearLayout linearlayout_ingredient;
    Typeface typeface;
    ShineButton favButton;
    String recipe_name,recipe_image,recipe_video;
    ImageView recipeImage;
    FullscreenVideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_my_recipe_details);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(" ");
        typeface = Typeface.createFromAsset(getAssets(), "fonts/Raleway.ttf");


        recipe_id=getIntent().getIntExtra("recipe_id",0);
        initViews();

        if (AppHelper.isNetworkAvailable(MyRecipeDetailsActivity.this)){

            populateRecipesDetails();

        }
        else{

            AppHelper.ErrorToast(MyRecipeDetailsActivity.this,"No Internet Connection");
        }






        favButton.setOnCheckStateChangeListener(new ShineButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View view, boolean checked) {

                if (checked){
                    Toast.makeText(MyRecipeDetailsActivity.this, "Add To FavoriteList", Toast.LENGTH_SHORT).show();
                    addOrRemoveFavorites(true);

                }
                else{
                    Toast.makeText(MyRecipeDetailsActivity.this, "Removed from FavoriteList", Toast.LENGTH_SHORT).show();
                    addOrRemoveFavorites(false);
                }
            }
        });

    }

    private void populateRecipesDetails() {

        final AlertDialog dialog = new SpotsDialog(this,R.style.CustomDialog);

        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.BASE_URL+"get_myrecipe_details.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {

                            JSONObject obj = new JSONObject(response);

                            Log.d("response",response.toString());

                            //if no error in response
                            if (obj.getBoolean("Status")) {
                                //    Toast.makeText(getContext(), obj.getString("Message"), Toast.LENGTH_SHORT).show();

                                //getting the user from the response
                                JSONArray userJson = obj.getJSONArray("response");

                                for (int i = 0; i < userJson.length(); i++) {
                                    JSONObject jsonObj = userJson.getJSONObject(i);
                                    {

                                        recipe_name=jsonObj.getString("recipe_name");
                                        recipe_image=jsonObj.getString("recipe_image");
                                        recipe_video=jsonObj.getString("recipe_video");
                                        videoView.videoUrl(EndPoints.MYVIDEO_URL+recipe_video);
                                        txtPreTime.setText(jsonObj.getString("preparation_time"));
                                        txtCookTime.setText(jsonObj.getString("cooking_time"));
                                        txtrecipe_description.setText(jsonObj.getString("description"));
                                      /*  RequestOptions requestOptions=new RequestOptions();
                                        requestOptions
                                                .diskCacheStrategy(DiskCacheStrategy.ALL);
                                                 Glide.with(RecipeDetailsActivity.this).load(jsonObj.getString("recipe_image"))
                                                .thumbnail(0.5f)
                                                .transition(new DrawableTransitionOptions().crossFade())
                                                .apply(requestOptions)
                                                .into(recipeImage);*/
                                        txtServings.setText("Ingredients for "+jsonObj.getString("serve_people")+" Servings");

                                        JSONArray ingrediantArray=jsonObj.getJSONArray("ingredient");
                                        for (int j=0;j<ingrediantArray.length();j++){

                                            JSONObject object=ingrediantArray.getJSONObject(j);
                                            TextView textViewIngredient = new TextView(MyRecipeDetailsActivity.this);
                                            textViewIngredient.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                            textViewIngredient.setGravity(Gravity.LEFT);
                                            textViewIngredient.setTextSize((float) 16);
                                            textViewIngredient.setTypeface(typeface);
                                            if (AppHelper.isAndroid26()) {
                                                textViewIngredient.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
                                            }
                                            textViewIngredient.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_bullet_dot,0,0,0);
                                            textViewIngredient.setCompoundDrawablePadding(15);
                                            textViewIngredient.setTextColor(AppHelper.getColor(MyRecipeDetailsActivity.this,R.color.colorBlack));
                                            textViewIngredient.setPadding(5,8,5,5);
                                            textViewIngredient.setText(object.getString("quantity")+" "+object.getString("ingredient_name"));
                                            linearlayout_ingredient.addView(textViewIngredient);
                                        }

                                        if (jsonObj.getBoolean("isFav")){
                                            favButton.setChecked(true);
                                        }
                                        else
                                        {
                                            favButton.setChecked(false);
                                        }


                                    }




                                }
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
                        dialog.dismiss();
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


        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void addOrRemoveFavorites(final Boolean isFavorite) {


        StringRequest stringRequest1 = new StringRequest(Request.Method.POST, EndPoints.BASE_URL+"add_remove_favorite.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject obj = new JSONObject(response);

                            Log.d("response",response.toString());

                            //if no error in response
                            if (obj.getBoolean("Status")) {
                                //    Toast.makeText(getContext(), obj.getString("Message"), Toast.LENGTH_SHORT).show();

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
                params.put("isFavorite", String.valueOf(isFavorite));
                params.put("recipe_name",recipe_name);
                params.put("recipe_image", recipe_image);

                return params;
            }
        };


        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest1);


    }

    private void initViews() {

        txtPreTime=(TextView)findViewById(R.id.txtPreTime1);
        txtCookTime=(TextView)findViewById(R.id.txtCookTime1);
        txtrecipe_description=(TextView)findViewById(R.id.txtrecipe_description);
        if (AppHelper.isAndroid26()) {
            txtrecipe_description.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
        }
        linearlayout_ingredient=(LinearLayout)findViewById(R.id.linearlayout_ingredient1);
        txtServings=(TextView)findViewById(R.id.txtServings1);
        favButton=(ShineButton)findViewById(R.id.favButton1);
        favButton.init(MyRecipeDetailsActivity.this);
        //   recipeImage=(ImageView)findViewById(R.id.recipeImage);
        videoView=(FullscreenVideoView)findViewById(R.id.fullscreenVideoView1);
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
