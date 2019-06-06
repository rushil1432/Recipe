package com.example.recipe.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.recipe.HomeActivity;
import com.example.recipe.R;
import com.example.recipe.RecipeDetailsActivity;
import com.example.recipe.RecipesActivity;
import com.example.recipe.adapter.FavRecipeAdapter;
import com.example.recipe.adapter.RecipeAdapter;
import com.example.recipe.app.EndPoints;
import com.example.recipe.helpers.AppHelper;
import com.example.recipe.helpers.CustomTypeface;
import com.example.recipe.helpers.GridItemDecoration;
import com.example.recipe.helpers.SharedPrefManager;
import com.example.recipe.listener.RecyclerItemClickListener;
import com.example.recipe.model.RecipeModel;
import com.example.recipe.model.User;
import com.example.recipe.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment {


    View view;
    SpannableString spannableString;
    private RecyclerView recyclerView_favorites;
    private List<RecipeModel> recipeModelList;
    private FavRecipeAdapter recipeAdapter;
    LinearLayout linearLayoutNoFound,linearlayout_Progessbar;
    private ProgressBar progressBar;
    User user;
    int user_id;
    public FavoriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =inflater.inflate(R.layout.fragment_favorite, container, false);

        spannableString=new CustomTypeface().setTypeFace(getContext(),"Favorite Recipes");
        ((HomeActivity) getActivity()).setTitle(spannableString);

        initViews();
        user=SharedPrefManager.getInstance(getContext()).getUser();
        user_id=user.getUser_id();
        recipeModelList=new ArrayList<>();

        if (AppHelper.isNetworkAvailable(getContext())){

            populateFavRecipes();

        }
        else{

            AppHelper.ErrorToast(getContext(),"No Internet Connection");
        }



        return view;
    }

    private void populateFavRecipes() {

        linearlayout_Progessbar.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.BASE_URL + "get_favorites.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        linearlayout_Progessbar.setVisibility(View.GONE);

                        try {

                            JSONObject obj = new JSONObject(response);

                            Log.d("response", response.toString());

                            //if no error in response
                            if (obj.getBoolean("Status")) {
                                //    Toast.makeText(getContext(), obj.getString("Message"), Toast.LENGTH_SHORT).show();

                                //getting the user from the response
                                JSONArray userJson = obj.getJSONArray("response");

                                for (int i = 0; i < userJson.length(); i++) {
                                    JSONObject jsonObj = userJson.getJSONObject(i);
                                    {


                                        RecipeModel recipeModel = new RecipeModel();
                                        recipeModel.setRecipe_id(jsonObj.getInt("recipe_id"));
                                        recipeModel.setRecipe_name(jsonObj.getString("recipe_name"));
                                        recipeModel.setRecipe_image(jsonObj.getString("recipe_image"));

                                        recipeModelList.add(recipeModel);

                                        recipeAdapter = new FavRecipeAdapter(getActivity(), recipeModelList);
                                        recyclerView_favorites.setAdapter(recipeAdapter);


                                    }


                                }
                            } else {

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
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("user_id", String.valueOf(user_id));
                return params;
            }
        };


        VolleySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);

    }

        private void initViews() {

        recyclerView_favorites=(RecyclerView)view.findViewById(R.id.recyclerView_favorites);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView_favorites.setLayoutManager(layoutManager);
        linearLayoutNoFound=(LinearLayout)view.findViewById(R.id.linearLayout_nodata);
        linearLayoutNoFound.setVisibility(View.INVISIBLE);
        linearlayout_Progessbar=(LinearLayout)view.findViewById(R.id.linearlayout_Progessbar);
        progressBar=(ProgressBar)view.findViewById(R.id.progressBar);
        GridItemDecoration itemDecoration = new GridItemDecoration(getContext(), R.dimen.grid_item_spacing);
        recyclerView_favorites.addItemDecoration(itemDecoration);
    }

    @Override
    public void onResume() {

        super.onResume();
    }
}
