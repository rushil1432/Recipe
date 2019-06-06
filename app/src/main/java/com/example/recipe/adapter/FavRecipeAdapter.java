package com.example.recipe.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.example.recipe.R;
import com.example.recipe.RecipeDetailsActivity;
import com.example.recipe.app.EndPoints;
import com.example.recipe.helpers.AppHelper;
import com.example.recipe.helpers.SharedPrefManager;
import com.example.recipe.model.RecipeModel;
import com.example.recipe.model.User;
import com.example.recipe.volley.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FavRecipeAdapter extends RecyclerView.Adapter<FavRecipeAdapter.MyViewHolder> {

    Context context;
    List<RecipeModel> recipeModelList;
    private ArrayList<RecipeModel> arraylist;
    User user;
    int user_id;

    public FavRecipeAdapter(Context context, List<RecipeModel> recipeModelList) {
        this.context=context;
        this.recipeModelList=recipeModelList;
        this.arraylist = new ArrayList<RecipeModel>();
        this.arraylist.addAll(recipeModelList);
    }


    @NonNull
    @Override
    public FavRecipeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.custom_layout_favrecipe, null);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FavRecipeAdapter.MyViewHolder holder, final int position) {

        holder.textView.setText(recipeModelList.get(position).getRecipe_name());

        RequestOptions requestOptions=new RequestOptions();
        requestOptions
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(AppHelper.getDrawable(context,R.drawable.ic_wedding_dinner));


        Glide.with(context).load(EndPoints.IMAGE_URL+recipeModelList.get(position).getRecipe_image())
                .thumbnail(0.5f)
                .transition(new DrawableTransitionOptions().crossFade())
                .apply(requestOptions)
                .into(holder.imageView);

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(context, RecipeDetailsActivity.class);
                intent.putExtra("recipe_id",recipeModelList.get(position).getRecipe_id());
                context.startActivity(intent);
            }
        });

        user=SharedPrefManager.getInstance(context).getUser();
        user_id=user.getUser_id();
        holder.imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addOrRemoveFavorites(false,recipeModelList.get(position).getRecipe_id(),
                        recipeModelList.get(position).getRecipe_name(),
                        recipeModelList.get(position).getRecipe_image(),
                        user_id);
                recipeModelList.remove(position);
                notifyDataSetChanged();

            }
        });
    }


    @Override
    public int getItemCount() {
        return recipeModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView imageView,imgClose;

        public MyViewHolder(View itemView) {
            super(itemView);

            textView=(TextView)itemView.findViewById(R.id.txtRecipeName);
            imageView=(ImageView)itemView.findViewById(R.id.imgRecipe);
            imgClose=(ImageView)itemView.findViewById(R.id.imgClose);

        }
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        recipeModelList.clear();
        if (charText.length() == 0) {
            recipeModelList.addAll(arraylist);
        } else {
            for (RecipeModel cat : arraylist) {
                if (cat.getRecipe_name().toLowerCase(Locale.getDefault()).contains(charText)) {
                    recipeModelList.add(cat);
                }
            }
        }
        notifyDataSetChanged();
    }


    private void addOrRemoveFavorites(final Boolean isFavorite, final int recipe_id, final String recipe_name, final String recipe_image, final int user_id) {


        StringRequest stringRequest1 = new StringRequest(Request.Method.POST, EndPoints.BASE_URL + "add_remove_favorite.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject obj = new JSONObject(response);

                            Log.d("response", response.toString());

                            //if no error in response
                            if (obj.getBoolean("Status")) {
                                //    Toast.makeText(getContext(), obj.getString("Message"), Toast.LENGTH_SHORT).show();

                            } else {

                                Toast.makeText(context, obj.getString("Message"), Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("recipe_id", String.valueOf(recipe_id));
                params.put("isFavorite", String.valueOf(isFavorite));
                params.put("recipe_name", recipe_name);
                params.put("recipe_image",recipe_image);
                params.put("user_id", String.valueOf(user_id));
                return params;
            }
        };


        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest1);
    }

    }
