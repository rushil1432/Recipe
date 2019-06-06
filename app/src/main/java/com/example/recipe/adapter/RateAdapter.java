package com.example.recipe.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.recipe.R;
import com.example.recipe.app.EndPoints;
import com.example.recipe.helpers.AppHelper;
import com.example.recipe.model.RatingModel;
import com.example.recipe.model.RecipeModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class RateAdapter extends RecyclerView.Adapter<RateAdapter.MyViewHolder> {

    Context context;
    List<RatingModel> ratingModelList;
    Date sDate,eDate;
    long numberOfDays = 0;


    public RateAdapter(Context context, List<RatingModel> ratingModelList) {
        this.context=context;
        this.ratingModelList=ratingModelList;

    }


    @NonNull
    @Override
    public RateAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.custom_rating_item, null);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RateAdapter.MyViewHolder holder, final int position) {

       String fname,lname;
       float rating;

       fname=ratingModelList.get(position).getFirst_name();
       lname=ratingModelList.get(position).getLast_name();
       rating= Float.parseFloat(ratingModelList.get(position).getRate());

       holder.txtCName.setText(fname+" "+lname);
       holder.ratingBar1.setRating(rating);

       holder.txtComments.setText(ratingModelList.get(position).getComments());


        RequestOptions requestOptions=new RequestOptions();
        requestOptions
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(AppHelper.getDrawable(context,R.drawable.ic_account_circle_white));


        Glide.with(context).load(EndPoints.PROFILE_URL+ratingModelList.get(position).getImage())
                .thumbnail(0.5f)
                .apply(requestOptions)
                .into(holder.imageViewC);

        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");
        try {
            sDate=new Date();
            eDate=dateFormat.parse(ratingModelList.get(position).getDate());
            numberOfDays = getUnitBetweenDates(eDate, sDate, TimeUnit.DAYS);


        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.txtDayAgo.setText(numberOfDays+" days ago");





    }
    private static long getUnitBetweenDates(Date startDate, Date endDate, TimeUnit unit) {
        long timeDiff = endDate.getTime() - startDate.getTime();
        return unit.convert(timeDiff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int getItemCount() {
        return ratingModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtCName,txtComments,txtDayAgo;
        ImageView imageViewC;
        RatingBar ratingBar1;


        public MyViewHolder(View itemView) {
            super(itemView);

            txtCName=(TextView)itemView.findViewById(R.id.txtCName);
            txtComments=(TextView)itemView.findViewById(R.id.txtComments);
            imageViewC=(ImageView)itemView.findViewById(R.id.imageViewC);
            ratingBar1=itemView.findViewById(R.id.ratingBar1);
            txtDayAgo=itemView.findViewById(R.id.txtDayAgo);

            if (AppHelper.isAndroid26()) {
                txtComments.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
                }

        }
    }




}
