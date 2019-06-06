package com.example.recipe;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.recipe.adapter.RecipeAdapter;
import com.example.recipe.app.EndPoints;
import com.example.recipe.helpers.AppHelper;
import com.example.recipe.helpers.CustomTypeface;
import com.example.recipe.helpers.GridItemDecoration;
import com.example.recipe.listener.RecyclerItemClickListener;
import com.example.recipe.model.Category;
import com.example.recipe.model.RecipeModel;
import com.example.recipe.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class RecipesActivity extends AppCompatActivity {

    private RecyclerView recyclerView_recipes;
    private List<RecipeModel> recipeModelList;
    private RecipeAdapter recipeAdapter;
    LinearLayout linearLayoutNoFound,linearlayout_Progessbar;
    int category_id;
    private ProgressBar progressBar;
    CustomTypeface customTypeface;
    SpannableString spannableString;
    SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        initViews();
        customTypeface=new CustomTypeface();
        spannableString=customTypeface.setTypeFace(RecipesActivity.this,getIntent().getStringExtra("category_name"));
        getSupportActionBar().setTitle(spannableString);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        category_id=getIntent().getIntExtra("category_id",0);
        recipeModelList=new ArrayList<>();

        if (AppHelper.isNetworkAvailable(RecipesActivity.this)){

            populateRecipes();

        }
        else{

            AppHelper.ErrorToast(RecipesActivity.this,"No Internet Connection");
        }

        recyclerView_recipes.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView_recipes, new RecyclerItemClickListener
                .OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {



                       RecipeModel recipeModel=recipeModelList.get(position);

                        Intent intent=new Intent(RecipesActivity.this, RecipeDetailsActivity.class);
                        intent.putExtra("recipe_id",recipeModel.getRecipe_id());
                        startActivity(intent);
            };

            @Override
            public void onItemLongClick(View view, int position) {
                //handle longClick if any
            }
        }));



    }

    private void populateRecipes() {

        linearlayout_Progessbar.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.BASE_URL+"get_recipe.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        linearlayout_Progessbar.setVisibility(View.GONE);

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


                                        RecipeModel recipeModel=new RecipeModel();
                                        recipeModel.setRecipe_id(jsonObj.getInt("recipe_id"));
                                        recipeModel.setRecipe_name(jsonObj.getString("recipe_name"));
                                        recipeModel.setRecipe_image(jsonObj.getString("recipe_image"));

                                        recipeModelList.add(recipeModel);

                                        recipeAdapter=new RecipeAdapter(RecipesActivity.this,recipeModelList);
                                        recyclerView_recipes.setAdapter(recipeAdapter);


                                    }




                                }
                            }else {

                                linearLayoutNoFound.setVisibility(View.VISIBLE);
                                linearlayout_Progessbar.setVisibility(View.GONE);

                                // Toast.makeText(getApplicationContext(), obj.getString("Message"), Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        linearlayout_Progessbar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("category_id", String.valueOf(category_id));
                return params;
            }
        };


        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);


    }

    private void initViews() {
        recyclerView_recipes=(RecyclerView)findViewById(R.id.recyclerView_recipes);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView_recipes.setLayoutManager(layoutManager);
        linearLayoutNoFound=(LinearLayout)findViewById(R.id.linearLayout_nodata);
        linearLayoutNoFound.setVisibility(View.INVISIBLE);
        linearlayout_Progessbar=(LinearLayout)findViewById(R.id.linearlayout_Progessbar);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        GridItemDecoration itemDecoration = new GridItemDecoration(this, R.dimen.grid_item_spacing);
        recyclerView_recipes.addItemDecoration(itemDecoration);



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
