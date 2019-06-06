package com.example.recipe;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
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
import com.example.recipe.helpers.CustomTypeface;
import com.example.recipe.helpers.GridItemDecoration;
import com.example.recipe.listener.RecyclerItemClickListener;
import com.example.recipe.model.RecipeModel;
import com.example.recipe.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchResultsActivity extends AppCompatActivity {


    private RecyclerView recyclerView_recipes;
    private List<RecipeModel> recipeModelList;
    private RecipeAdapter recipeAdapter;
    LinearLayout linearLayoutNoFound,linearlayout_Progessbar;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViews();
        recipeModelList=new ArrayList<>();


        recyclerView_recipes.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView_recipes, new RecyclerItemClickListener
                .OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {



                RecipeModel recipeModel=recipeModelList.get(position);

                Intent intent=new Intent(SearchResultsActivity.this, RecipeDetailsActivity.class);
                intent.putExtra("recipe_id",recipeModel.getRecipe_id());
                startActivity(intent);
            };

            @Override
            public void onItemLongClick(View view, int position) {
                //handle longClick if any
            }
        }));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setIconified(false);
        searchView.setQueryHint("Ingredient Name..");
        searchView.setIconifiedByDefault(false);
        searchView.setSearchableInfo( searchManager.getSearchableInfo(getComponentName()) );

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                recipeModelList.clear();
                populateRecipes(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    private void initViews() {
        recyclerView_recipes=(RecyclerView)findViewById(R.id.recyclerView_srecipes);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView_recipes.setLayoutManager(layoutManager);
        linearLayoutNoFound=(LinearLayout)findViewById(R.id.linearLayout_nodata);
        linearLayoutNoFound.setVisibility(View.INVISIBLE);
        linearlayout_Progessbar=(LinearLayout)findViewById(R.id.linearlayout_Progessbar);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        linearlayout_Progessbar.setVisibility(View.INVISIBLE);
        GridItemDecoration itemDecoration = new GridItemDecoration(this, R.dimen.grid_item_spacing);
        recyclerView_recipes.addItemDecoration(itemDecoration);



    }





    private void populateRecipes(final String ingredient_name) {



        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.BASE_URL+"get_search_recipe.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        linearlayout_Progessbar.setVisibility(View.GONE);

                        try {

                            JSONObject obj = new JSONObject(response);

                            recyclerView_recipes.setVisibility(View.VISIBLE);
                            linearLayoutNoFound.setVisibility(View.INVISIBLE);

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

                                        recipeAdapter=new RecipeAdapter(SearchResultsActivity.this,recipeModelList);
                                        recyclerView_recipes.setAdapter(recipeAdapter);


                                    }




                                }
                            }else {

                                linearLayoutNoFound.setVisibility(View.VISIBLE);
                                linearlayout_Progessbar.setVisibility(View.GONE);
                                recyclerView_recipes.setVisibility(View.INVISIBLE);

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

                params.put("ingredient_name",ingredient_name);
                return params;
            }
        };


        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);


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
