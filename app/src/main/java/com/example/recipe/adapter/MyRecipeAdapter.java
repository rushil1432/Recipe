package com.example.recipe.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.recipe.R;
import com.example.recipe.app.EndPoints;
import com.example.recipe.helpers.AppHelper;
import com.example.recipe.model.RecipeModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyRecipeAdapter extends RecyclerView.Adapter<MyRecipeAdapter.MyViewHolder> {

    Context context;
    List<RecipeModel> recipeModelList;
    private ArrayList<RecipeModel> arraylist;

    public MyRecipeAdapter(Context context, List<RecipeModel> recipeModelList) {
        this.context=context;
        this.recipeModelList=recipeModelList;
        this.arraylist = new ArrayList<RecipeModel>();
        this.arraylist.addAll(recipeModelList);
    }


    @NonNull
    @Override
    public MyRecipeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.custom_layout_recipe, null);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyRecipeAdapter.MyViewHolder holder, final int position) {

        holder.textView.setText(recipeModelList.get(position).getRecipe_name());

        RequestOptions requestOptions=new RequestOptions();
        requestOptions
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(AppHelper.getDrawable(context,R.drawable.ic_wedding_dinner));


        Glide.with(context).load(EndPoints.MYIMAGE_URL+recipeModelList.get(position).getRecipe_image())
                .thumbnail(0.5f)
                .transition(new DrawableTransitionOptions().crossFade())
                .apply(requestOptions)
                .into(holder.imageView);





    }


    @Override
    public int getItemCount() {
        return recipeModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);

            textView=(TextView)itemView.findViewById(R.id.txtRecipeName);
            imageView=(ImageView)itemView.findViewById(R.id.imgRecipe);

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


}
