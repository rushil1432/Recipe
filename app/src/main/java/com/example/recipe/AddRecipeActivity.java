package com.example.recipe;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.recipe.adapter.CategoryAdapter;
import com.example.recipe.app.EndPoints;
import com.example.recipe.helpers.AppHelper;
import com.example.recipe.helpers.CustomTypeface;
import com.example.recipe.helpers.FilePath;
import com.example.recipe.helpers.SharedPrefManager;
import com.example.recipe.model.Category;
import com.example.recipe.model.User;
import com.example.recipe.volley.VolleySingleton;
import com.google.gson.Gson;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadService;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddRecipeActivity extends AppCompatActivity implements View.OnClickListener {

    SpannableString spannableString;
    ImageView imgCamera, imgVideo, imgPhotoPreview, imgVideoPreview;
    EditText edtRecipeName, edtRecipeDes;
    LinearLayout linearImageContainer, linearVideoContainer, linearServing, linearPreparation, linearCookTime, linearCategory;
    TextView txtServingsPeople, txtPreparationTime, txtCookingTime, txtIngredients, txtRecipeCategory;
    private int PICK_IMAGE_REQUEST = 1;
    private static final int SELECT_VIDEO = 3;
    private Bitmap bitmap;
    private Uri filePath, viPath;
    String imagePath, VideoPath, preparation_time, cooking_time, recipe_name, recipe_des, FileID,category_id,user_id;
    Bitmap videoBitMap;
    int serving_people;
    boolean isImage = false, isVideo = false;
    ArrayList<String> quantityList, ingredientList, categoryList;
    ArrayAdapter<String> arrayAdapter;
    List<Category> catList;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        spannableString = new CustomTypeface().setTypeFace(AddRecipeActivity.this, "Add Recipe");
        getSupportActionBar().setTitle(spannableString);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViews();
        linearImageContainer.setVisibility(View.GONE);
        linearVideoContainer.setVisibility(View.GONE);

        user=SharedPrefManager.getInstance(getApplicationContext()).getUser();
        user_id= String.valueOf(user.getUser_id());

        quantityList = new ArrayList<>();
        ingredientList = new ArrayList<>();
        categoryList = new ArrayList<>();
        catList=new ArrayList<>();

        imgCamera.setOnClickListener(this);
        imgVideo.setOnClickListener(this);
        linearServing.setOnClickListener(this);
        linearCookTime.setOnClickListener(this);
        linearPreparation.setOnClickListener(this);
        txtIngredients.setOnClickListener(this);
        linearCategory.setOnClickListener(this);

        if (AppHelper.isNetworkAvailable(AddRecipeActivity.this)) {
            retrieveCategory();


        } else {
            AppHelper.ErrorToast(AddRecipeActivity.this, "No Internet Connection");
        }


    }

    private void retrieveCategory() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, EndPoints.BASE_URL + "get_recipe_category.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


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
                                        Category category=new Category();
                                        category.setCategory_id(jsonObj.getInt("category_id"));
                                        category.setCategory_name(jsonObj.getString("category_name"));
                                        categoryList.add(jsonObj.getString("category_name"));

                                        catList.add(category);
                                    }


                                }
                            } else {

                                AppHelper.ErrorToast(getApplicationContext(), obj.getString("Message"));
                            }


                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);


    }

    private void initViews() {
        imgCamera = findViewById(R.id.imgCamera);
        imgVideo = findViewById(R.id.imgVideo);
        edtRecipeName = findViewById(R.id.edtRecipeName);
        edtRecipeDes = findViewById(R.id.edtRecipeDes);
        txtServingsPeople = findViewById(R.id.txtServingsPeople);
        txtPreparationTime = findViewById(R.id.txtPreparationTime);
        txtCookingTime = findViewById(R.id.txtCookingTime);
        txtIngredients = findViewById(R.id.txtIngredients);
        linearImageContainer = findViewById(R.id.linearImageContainer);
        linearVideoContainer = findViewById(R.id.linearVideoContainer);
        imgPhotoPreview = findViewById(R.id.imgPhotoPreview);
        imgVideoPreview = findViewById(R.id.imgVideoPreview);
        linearServing = findViewById(R.id.linearServing);
        linearPreparation = findViewById(R.id.linearPreparation);
        linearCookTime = findViewById(R.id.linearCookTime);
        linearCategory = findViewById(R.id.linearCategory);
        txtRecipeCategory = findViewById(R.id.txtRecipeCategory);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_add:
                checkValidations();

                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void checkValidations() {
        if (!isImage) {
            Toast.makeText(this, "select image of recipe", Toast.LENGTH_SHORT).show();
        } else if (!isVideo) {
            Toast.makeText(this, "select video of recipe", Toast.LENGTH_SHORT).show();
        } else if (edtRecipeName.getText().toString().equalsIgnoreCase("")) {
            edtRecipeName.setError("Required Recipe Name");
            edtRecipeName.requestFocus();

        } else if (serving_people == 0) {
            Toast.makeText(this, "please select Servings", Toast.LENGTH_SHORT).show();
        } else if (txtPreparationTime.getText().toString().equalsIgnoreCase("")) {

            Toast.makeText(this, "Add Preparation Time", Toast.LENGTH_SHORT).show();

        } else if (txtCookingTime.getText().toString().equalsIgnoreCase("")) {

            Toast.makeText(this, "Add Cooking Time", Toast.LENGTH_SHORT).show();

        } else {

            if (AppHelper.isNetworkAvailable(AddRecipeActivity.this)) {
                addRecipe();


            } else {
                AppHelper.ErrorToast(AddRecipeActivity.this, "No Internet Connection");
            }

        }

    }

    private void addRecipe() {

        recipe_name = edtRecipeName.getText().toString();
        recipe_des = edtRecipeDes.getText().toString();

        imagePath = FilePath.getPath(this, filePath);
        VideoPath = FilePath.getPath(this, viPath);

        JSONArray array = new JSONArray();

        for (int i = 0; i < quantityList.size(); i++) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("quantity", quantityList.get(i));
                obj.put("ingredient_name", ingredientList.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(obj);
        }


        try {

            UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
            FileID = UUID.randomUUID().toString();

            new MultipartUploadRequest(this, FileID, EndPoints.BASE_URL + "add_recipe.php")
                    .addFileToUpload(imagePath, "image")
                    .addFileToUpload(VideoPath, "video")
                    .addParameter("recipe_name", recipe_name)
                    .addParameter("category_id", category_id)
                    .addParameter("description", recipe_des)
                    .addParameter("cooking_time", cooking_time)
                    .addParameter("serve_people", String.valueOf(serving_people))
                    .addParameter("preparation_time", preparation_time)
                    .addParameter("user_id",user_id)
                    .addParameter("ingredientList", String.valueOf(array))
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setUtf8Charset()
                    .setMaxRetries(5)
                    .setDelegate(new UploadStatusDelegate() {
                        @Override
                        public void onProgress(Context context, UploadInfo uploadInfo) {


                        }

                        @Override
                        public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {

                            AppHelper.ErrorToast(context, "Failed");
                        }

                        @Override
                        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                            AppHelper.SuccessToast(context, "Recipe upload Successfully ");
                            AppHelper.LaunchActivity(AddRecipeActivity.this,HomeActivity.class);
                            finish();

                        }

                        @Override
                        public void onCancelled(Context context, UploadInfo uploadInfo) {

                        }
                    })
                    .startUpload();

        } catch (Exception exception) {

            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {


            case R.id.imgCamera:
                requestStoragePermission();
                showFileChooser();
                break;

            case R.id.imgVideo:
                chooseVideo();
                break;

            case R.id.linearServing:
                openServingDialog();
                break;

            case R.id.linearPreparation:
                openPreparationDialog();
                break;
            case R.id.linearCookTime:
                openCookingDialog();
                break;

            case R.id.txtIngredients:
                Intent intent = new Intent(AddRecipeActivity.this, AddIngredientsActivity.class);
                startActivityForResult(intent, 2);
                break;

            case R.id.linearCategory:
                showBottomSheet();
                break;


        }
    }

    private void showBottomSheet() {
        final BottomSheetDialog dialog = new BottomSheetDialog(AddRecipeActivity.this);
        View view = getLayoutInflater().inflate(R.layout.custom_bottom_sheet, null);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        ListView listView = (ListView) view.findViewById(R.id.listview_category);
        arrayAdapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,categoryList);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Category category=catList.get(position);
                txtRecipeCategory.setText(category.getCategory_name());
                category_id= String.valueOf(category.getCategory_id());
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void openCookingDialog() {

        Button btnCancel, btnSave;
        final NumberPicker numberPickerHour, numberPickerMinute;
        final Dialog dialog = new Dialog(AddRecipeActivity.this);
        dialog.setContentView(R.layout.custom_preparation_dialog);

        btnCancel = dialog.findViewById(R.id.btnCancel);
        btnSave = dialog.findViewById(R.id.btnSave);
        numberPickerHour = dialog.findViewById(R.id.numberPickerHour);
        numberPickerHour.setMinValue(0);
        numberPickerHour.setMaxValue(23);
        numberPickerHour.setWrapSelectorWheel(false);

        numberPickerMinute = dialog.findViewById(R.id.numberPickerMinute);
        numberPickerMinute.setMinValue(0);
        numberPickerMinute.setMaxValue(59);
        numberPickerMinute.setWrapSelectorWheel(false);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (numberPickerHour.getValue() == 0) {
                    cooking_time = "" + String.valueOf(numberPickerMinute.getValue()) + " mins";

                } else {
                    cooking_time = "" + String.valueOf(numberPickerHour.getValue()) + " Hrs " + String.valueOf(numberPickerMinute.getValue()) + " mins";

                }
                txtCookingTime.setText(cooking_time);
                dialog.dismiss();

            }
        });
        dialog.show();

    }

    private void openPreparationDialog() {

        Button btnCancel, btnSave;
        final NumberPicker numberPickerHour, numberPickerMinute;
        final Dialog dialog = new Dialog(AddRecipeActivity.this);
        dialog.setContentView(R.layout.custom_preparation_dialog);

        btnCancel = dialog.findViewById(R.id.btnCancel);
        btnSave = dialog.findViewById(R.id.btnSave);
        numberPickerHour = dialog.findViewById(R.id.numberPickerHour);
        numberPickerHour.setMinValue(0);
        numberPickerHour.setMaxValue(23);
        numberPickerHour.setWrapSelectorWheel(false);

        numberPickerMinute = dialog.findViewById(R.id.numberPickerMinute);
        numberPickerMinute.setMinValue(0);
        numberPickerMinute.setMaxValue(59);
        numberPickerMinute.setWrapSelectorWheel(false);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (numberPickerHour.getValue() == 0) {

                    preparation_time = "" + String.valueOf(numberPickerMinute.getValue()) + " mins";

                } else {
                    preparation_time = "" + String.valueOf(numberPickerHour.getValue()) + " Hrs " + String.valueOf(numberPickerMinute.getValue()) + " mins";

                }
                txtPreparationTime.setText(preparation_time);
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    private void openServingDialog() {
        Button btnCancel, btnSave;
        final NumberPicker numberPicker;
        final Dialog dialog = new Dialog(AddRecipeActivity.this);
        dialog.setContentView(R.layout.custom_servings_dialog);

        btnCancel = dialog.findViewById(R.id.btnCancel);
        btnSave = dialog.findViewById(R.id.btnSave);
        numberPicker = dialog.findViewById(R.id.numberPicker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(15);
        numberPicker.setWrapSelectorWheel(false);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                serving_people = numberPicker.getValue();
                txtServingsPeople.setText("" + serving_people);
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Photo"), PICK_IMAGE_REQUEST);
    }

    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a Video "), SELECT_VIDEO);
    }

    private void requestStoragePermission() {

        if (AppHelper.checkAllPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            AppHelper.LogCat("READ_EXTERNAL_STORAGE permission already granted.");
        } else {
            AppHelper.LogCat("Please request READ_EXTERNAL_STORAGE permission.");
            AppHelper.requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST) {

            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                filePath = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    linearImageContainer.setVisibility(View.VISIBLE);
                    imgPhotoPreview.setImageBitmap(bitmap);
                    isImage = true;


                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {

                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to select image", Toast.LENGTH_SHORT)
                        .show();
                isImage = false;
            }
        } else if (requestCode == SELECT_VIDEO) {

            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                viPath = data.getData();
                try {
                    videoBitMap = ThumbnailUtils.createVideoThumbnail(getPath(viPath), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);

                    linearVideoContainer.setVisibility(View.VISIBLE);
                    imgVideoPreview.setImageBitmap(videoBitMap);
                    isVideo = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to select video", Toast.LENGTH_SHORT)
                        .show();
                isVideo = false;

            }
        } else if (requestCode == 2) {

            quantityList = data.getStringArrayListExtra("qArray");
            ingredientList = data.getStringArrayListExtra("iArray");


        }
        if (resultCode == Activity.RESULT_CANCELED) {
            quantityList.clear();
            ingredientList.clear();
        }
    }

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        cursor.close();

        return path;
    }


}
