package com.example.recipe.fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.recipe.HomeActivity;
import com.example.recipe.R;
import com.example.recipe.RecipesActivity;
import com.example.recipe.SearchResultsActivity;
import com.example.recipe.adapter.CategoryAdapter;
import com.example.recipe.adapter.ViewPagerAdapter;
import com.example.recipe.app.AppConstants;
import com.example.recipe.app.EndPoints;
import com.example.recipe.helpers.AppHelper;
import com.example.recipe.helpers.CustomProgress;
import com.example.recipe.helpers.CustomTypeface;
import com.example.recipe.helpers.GridItemDecoration;
import com.example.recipe.listener.RecyclerItemClickListener;
import com.example.recipe.model.Category;
import com.example.recipe.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import dmax.dialog.SpotsDialog;
import me.relex.circleindicator.CircleIndicator;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    View view;
    public static final String[] recipeName={"Carrot Halwa","Pizza","Khaman","Panner Tikka","Yoghurt Cake","Broccoli Pasta"};
    public static final  int[] recipeImage={R.drawable.carrot_halwa,R.drawable.pizza,R.drawable.khaman1,R.drawable.paneer_tikka,R.drawable.yoghurt_cake,R.drawable.broccoli_pasta};
    private static ViewPager mPager;
    private static int currentPage = 0;
    private ArrayList<Integer> imageArray = new ArrayList<Integer>();
    private ArrayList<String> nameArray = new ArrayList<String>();
    RecyclerView recyclerView_recipeCategory;
    List<Category> categoryList;
    CategoryAdapter categoryAdapter;
    ProgressBar progressBar;
    LinearLayout linearlayout_Progess;
    CustomProgress customProgress;
    CustomTypeface customTypeface;
    SpannableString spannableString;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        setHasOptionsMenu(true);
        customTypeface=new CustomTypeface();
        spannableString=customTypeface.setTypeFace(getContext(),"Home");
        ((HomeActivity) getActivity()).setTitle(spannableString);

        setupSlider();

        init();
        categoryList=new ArrayList<>();
       // customProgress = CustomProgress.getInstance();

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);

        recyclerView_recipeCategory.setLayoutManager(mLayoutManager);
        GridItemDecoration itemDecoration = new GridItemDecoration(getContext(), R.dimen.grid_item_spacing);
        recyclerView_recipeCategory.addItemDecoration(itemDecoration);

        if (AppHelper.isNetworkAvailable(getActivity())){

            populateCategory();
        }
        else{

            AppHelper.ErrorToast(getContext(),"No Internet Connection");
        }

        recyclerView_recipeCategory.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView_recipeCategory, new RecyclerItemClickListener
                .OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Category categoryItem=categoryList.get(position);

                Intent intent=new Intent(getContext(), RecipesActivity.class);
                intent.putExtra("category_id",categoryItem.getCategory_id());
                intent.putExtra("category_name",categoryItem.getCategory_name());
                startActivity(intent);


                //handle click events here
            }

            @Override
            public void onItemLongClick(View view, int position) {
                //handle longClick if any
            }
        }));




        return view;


    }

    private void init() {

        recyclerView_recipeCategory=(RecyclerView)view.findViewById(R.id.recyclerView_recipeCategory);
       progressBar=(ProgressBar)view.findViewById(R.id.progressBar);
       linearlayout_Progess=(LinearLayout)view.findViewById(R.id.linearlayout_Progess);
    }

    private void populateCategory() {

      linearlayout_Progess.setVisibility(View.VISIBLE);
      //  customProgress.showProgress(getContext(),"Please Wait..", false);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, EndPoints.BASE_URL+"get_recipe_category.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                      //  customProgress.hideProgress();

                        linearlayout_Progess.setVisibility(View.GONE);
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
                                        Category category=new Category(
                                                jsonObj.getInt("category_id"),
                                                jsonObj.getString("category_image"),
                                                jsonObj.getString("category_name"));


                                        categoryList.add(category);

                                        categoryAdapter=new CategoryAdapter(getActivity(),categoryList);
                                        recyclerView_recipeCategory.setAdapter(categoryAdapter);

                                    }

                                }
                            }else {
                                Toast.makeText(getContext(), obj.getString("Message"), Toast.LENGTH_SHORT).show();
                                linearlayout_Progess.setVisibility(View.GONE);
                            }


                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        linearlayout_Progess.setVisibility(View.GONE);
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


        VolleySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);


    }



    private void setupSlider() {

        for (int i=0;i<recipeName.length;i++)
        {

            nameArray.add(recipeName[i]);
            imageArray.add(recipeImage[i]);
        }

        mPager = (ViewPager)view.findViewById(R.id.pager);
        mPager.setAdapter(new ViewPagerAdapter(getActivity(),imageArray,nameArray));
        CircleIndicator indicator = (CircleIndicator)view.findViewById(R.id.indicator);
        indicator.setViewPager(mPager);

        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == imageArray.size()) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_search_menu:
                AppHelper.LaunchActivity(getActivity(),SearchResultsActivity.class);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}




