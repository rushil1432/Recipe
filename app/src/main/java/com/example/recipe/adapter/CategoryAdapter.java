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
import com.example.recipe.model.Category;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    Context context;
    List<Category> categoryList;
    private ArrayList<Category> arraylist;

    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context=context;
        this.categoryList=categoryList;
        this.arraylist = new ArrayList<Category>();
        this.arraylist.addAll(categoryList);
    }


    @NonNull
    @Override
    public CategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.custom_layout_category, null);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.MyViewHolder holder, int position) {

        holder.textView.setText(categoryList.get(position).getCategory_name());

        RequestOptions requestOptions=new RequestOptions();
        requestOptions
                .diskCacheStrategy(DiskCacheStrategy.ALL);


        Glide.with(context).load(EndPoints.CATEGORY_URL+categoryList.get(position).getCategory_image())
                .thumbnail(0.5f)
                .transition(new DrawableTransitionOptions().crossFade())
                .apply(requestOptions)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);

            textView=(TextView)itemView.findViewById(R.id.txtCategoryName);
            imageView=(ImageView)itemView.findViewById(R.id.imgCategory);
        }
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        categoryList.clear();
        if (charText.length() == 0) {
            categoryList.addAll(arraylist);
        } else {
            for (Category cat : arraylist) {
                if (cat.getCategory_name().toLowerCase(Locale.getDefault()).contains(charText)) {
                    categoryList.add(cat);
                }
            }
        }
        notifyDataSetChanged();
    }


}
