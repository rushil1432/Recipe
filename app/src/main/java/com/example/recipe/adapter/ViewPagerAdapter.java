package com.example.recipe.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
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

import java.util.ArrayList;

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Integer> images;
    private ArrayList<String> names;

    public ViewPagerAdapter(Context context, ArrayList<Integer> images, ArrayList<String> names) {
        this.context = context;
        this.images=images;
        this.names=names;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View myImageLayout = layoutInflater.inflate(R.layout.custom_layout_image, view, false);
        ImageView myImage = (ImageView) myImageLayout
                .findViewById(R.id.imgRecipe);
        TextView myText=(TextView)myImageLayout.findViewById(R.id.txtRecipeName);

        myText.setText(names.get(position));

        RequestOptions requestOptions=new RequestOptions();
        requestOptions
                .diskCacheStrategy(DiskCacheStrategy.ALL);


        Glide.with(context).load(images.get(position))
                .thumbnail(0.5f)
                .transition(new DrawableTransitionOptions().crossFade())
                .apply(requestOptions)
                .into(myImage);

        view.addView(myImageLayout, 0);
        return myImageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}
