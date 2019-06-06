package com.example.recipe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recipe.helpers.CustomTypeface;

import java.util.ArrayList;

public class AddIngredientsActivity extends AppCompatActivity {

    SpannableString spannableString;
    LinearLayout linearContainer;
    TextView txtAdd;
    ArrayList<String> quantityList,ingredientList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredients);

        spannableString=new CustomTypeface().setTypeFace(AddIngredientsActivity.this,"Add Ingredients");
        getSupportActionBar().setTitle(spannableString);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViews();

        for (int i=0;i<=2;i++){
            setLayoutViews();

        }


        txtAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setLayoutViews();
            }
        });


    }

    private void setLayoutViews() {

        LayoutInflater layoutInflater =
                (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View addView = layoutInflater.inflate(R.layout.custom_row_item, null);
        ImageView imgRemove = (ImageView) addView.findViewById(R.id.imgCancel);

        imgRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int count = linearContainer.getChildCount();

                if (count<=1){

                    Toast.makeText(AddIngredientsActivity.this, "Minimum one ingredient required", Toast.LENGTH_SHORT).show();
                }
                else {

                    ((LinearLayout) addView.getParent()).removeView(addView);

                }
            }
        });

        linearContainer.addView(addView);
    }

    private void initViews() {
        linearContainer=findViewById(R.id.linearContainer);
        txtAdd=findViewById(R.id.txtAdd);
        quantityList=new ArrayList<>();
        ingredientList=new ArrayList<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_add:
                addIngredients();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void addIngredients() {

        int count = linearContainer.getChildCount();

        String quntity ="",ingrediants="";

        for (int i = 0; i < count; i++) {
            final View row = linearContainer.getChildAt(i);
            EditText editText = (EditText) row.findViewById(R.id.edtQuantity);
            EditText editText1=(EditText)row.findViewById(R.id.edtIngredient);

            quntity=editText.getText().toString();
            ingrediants=editText1.getText().toString();


            quantityList.add(quntity);
            ingredientList.add(ingrediants);

            }

        Intent intent=new Intent();
        intent.putStringArrayListExtra("qArray",quantityList);
        intent.putStringArrayListExtra("iArray",ingredientList);
        setResult(2,intent);
        finish();


    }

    @Override
    public void onBackPressed() {


        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();

        super.onBackPressed();


    }
}
